package Agentes;

import QLearning.AccelEnv;
import QLearning.IEnvironment;
import QLearning.QLearning;
import champ2011client.Action;
import champ2011client.SensorModel;

public class DriverAccelTrain extends DriverBase {

    // === 1. COMPONENTES DE Q-LEARNING ===
    private QLearning agent;
    private IEnvironment env; // Tu AccelEnv

    // === 2. VARIABLES DE ESTADO PARA EL APRENDIZAJE ===
    private int previousState = -1;
    private int previousAction = -1;
    
    // === 3. CONSTANTES PARA DISCRETIZACIÓN DE VOLANTE (getSteerState) ===
    // Ajusta estos valores según cómo entrenaste el volante originalmente
    private final double CENTRO_MIN = -0.3;
    private final double CENTRO_MAX = 0.3;
    private final double STEER_RECTO_MIN = -0.05;
    private final double STEER_RECTO_MAX = 0.05;
    private final double STEER_IZQUIERDA = 0.1; 
    private final double STEER_DERECHA = -0.1;

    public DriverAccelTrain() {
        System.out.println("Iniciando DriverAccelTrain...");

       
        // B. Inicializar el Entorno (pasándole la política y 'this')
        // Nota: AccelEnv usará 'this' para llamar a getSteerState
        this.env = new AccelEnv(0.1, 0.99, 1.0);  // Politica - Alpha - Gamma - Epsilon inicial

        // C. Inicializar el Agente Q-Learning
        this.agent = new QLearning(this.env);
        
        System.out.println("Entrenamiento configurado: " + env.getName());
    }

    @Override
    public void reset() {
        System.out.println("Reiniciando carrera (Reset)");
        // Reseteamos la memoria de paso del agente
        previousState = -1;
        previousAction = -1;
    }

    @Override
    public void shutdown() {
        // Aquí podrías guardar la Q-Table de aceleración
        System.out.println("Guardando tabla Q...");
        // agent.saveQTable("accel_qtable.txt");
        System.out.println("Bye bye!");
    }

    // ===================================================================
    // === MÉTODO CLAVE: CONTROL (BUCLE DE APRENDIZAJE) ===
    // ===================================================================
    @Override
    public Action control(SensorModel sensors) {
        
        // --- 1. LÓGICA DE STUCK (Seguridad heredada) ---
        // Si el coche se atasca, dejamos de aprender y usamos lógica de rescate
        if (Math.abs(sensors.getAngleToTrackAxis()) > stuckAngle) stuck++;
        else stuck = 0;

        if (stuck > stuckTime) {
            // Lógica de recuperación (código original)
            float steer = (float) (-sensors.getAngleToTrackAxis() / steerLock);
            int gear = -1;
            if (sensors.getAngleToTrackAxis() * sensors.getTrackPosition() > 0) {
                gear = 1;
                steer = -steer;
            }
            clutch = clutching(sensors, clutch);
            Action action = new Action();
            action.gear = gear;
            action.steering = steer;
            action.accelerate = 1.0;
            action.brake = 0;
            action.clutch = clutch;
            return action; // Retorno temprano, no aprendemos en este tick
        }

        // --- 2. LÓGICA DE Q-LEARNING (Sustituye a tu antiguo 'else') ---
        
        // A. Obtener estado actual y recompensa del entorno
        int currentState = env.discretizeState(sensors);
        double reward = env.calculateReward(sensors);
        boolean isDone = env.isEpisodeDone(sensors);

        // B. Aprender: Actualizar Q-Table (usando s, a, r, s')
        // Solo actualizamos si tenemos un estado previo (no es el primer tick)
        if (previousState != -1) {
            agent.updateQTable(previousState, previousAction, reward, currentState);
        }

        // C. Decidir siguiente acción
        int nextAction;
        
        if (isDone) {
            // El episodio terminó (choque o salida)
            System.out.println("Episodio Finalizado.");
            agent.decayEpsilon(0.998, 0.05); // Reducimos exploración
            
            previousState = -1; // Reset para siguiente episodio
            
            // Enviamos acción de reinicio a TORCS
            Action resetAction = new Action();
            resetAction.restartRace = true;
            return resetAction;
            
        } else {
            // Episodio continúa: El agente elige qué hacer
            nextAction = agent.chooseAction(currentState);
            
            // Guardamos estado para el siguiente ciclo
            previousState = currentState;
            previousAction = nextAction;
        }

        // D. Ejecutar: Traducir acción abstracta a comando TORCS
        // AccelEnv usará getSteerState (abajo) + política + nextAction
        return mapActionToTorcs(this, sensors, nextAction);
    }

    // ===================================================================
    // === NUEVO MÉTODO: GET STEER STATE ===
    // ===================================================================
    /**
     * Este método es llamado por AccelEnv.mapActionToTorcs.
     * Determina en qué estado de volante estamos para consultar la Politica.
     */
    public int getSteerState(SensorModel sensors) {
        double trackPosition = sensors.getTrackPosition();
        double carAngle = sensors.getAngleToTrackAxis();

        // Helper interno para legibilidad
        if (estaEntre(trackPosition, CENTRO_MIN, CENTRO_MAX)) {
            // ZONA CENTRO
            if (estaEntre(carAngle, STEER_RECTO_MIN, STEER_RECTO_MAX)) return 0; 
            else if (estaEntre(carAngle, STEER_RECTO_MAX, STEER_IZQUIERDA)) return 1;
            else if (estaEntre(carAngle, STEER_DERECHA, STEER_RECTO_MIN)) return 2; 
            else if (carAngle > STEER_IZQUIERDA) return 3; 
            else if (carAngle < STEER_DERECHA) return 4;   

        } else if (trackPosition < CENTRO_MIN) { 
            // ZONA DERECHA (O IZQ, DEPENDE DE TU LÓGICA PREVIA)
            if (estaEntre(carAngle, STEER_RECTO_MIN, STEER_RECTO_MAX)) return 5;
            else if (estaEntre(carAngle, STEER_RECTO_MAX, STEER_IZQUIERDA)) return 6;
            else if (estaEntre(carAngle, STEER_DERECHA, STEER_RECTO_MIN)) return 7;
            else if (carAngle > STEER_IZQUIERDA) return 8;
            else if (carAngle < STEER_DERECHA) return 9;

        } else if (trackPosition > CENTRO_MAX) { 
            // ZONA IZQUIERDA (O DER)
            if (estaEntre(carAngle, STEER_RECTO_MIN, STEER_RECTO_MAX)) return 10;
            else if (estaEntre(carAngle, STEER_RECTO_MAX, STEER_IZQUIERDA)) return 11;
            else if (estaEntre(carAngle, STEER_DERECHA, STEER_RECTO_MIN)) return 12;
            else if (carAngle > STEER_IZQUIERDA) return 13;
            else if (carAngle < STEER_DERECHA) return 14;
        }

        return 0; // Default
    }

    // Helper para simplificar ifs
    private boolean estaEntre(double valor, double minimo, double maximo) {
        return (minimo <= valor && valor <= maximo);
    }

    // ===================================================================
    // === EXPOSICIÓN DE MÉTODOS PADRE ===
    // ===================================================================
    // AccelEnv necesita llamar a estos métodos, asegúrate de que sean publicos
    // o accesibles desde el paquete QLearning.
    
    @Override
    public int getGear(SensorModel sensors) {
        return super.getGear(sensors);
    }
    
    @Override
    public float getSteer(SensorModel sensors){
    	return super.getSteer(sensors); 
	}
    
    @Override
    public float filterABS(SensorModel sensors, float brake) {
        return super.filterABS(sensors, brake);
    }
    
    

	public Action mapActionToTorcs(DriverBase baseDriver, SensorModel sensors, int discreteAction) {
	
		// Necesitamos castear el driver base al tipo correcto
		DriverAccelTrain driver = (DriverAccelTrain) baseDriver;
        
        Action action = new Action();

        // 1. Lógica de marchas (del driver base)
        action.gear = driver.getGear(sensors); // Necesita que getGear sea publico
        
		// 2. Lógica de volante (de la política cargada)
        int steerState = driver.getSteerState(sensors); // Necesita que getSteerState sea publico
        action.steering = driver.getSteer(sensors);//this.politica_volante.getAccion(steerState)[0];

        // 3. Lógica de aceleración (de nuestro agente Q-Learning)
        float[] accel_and_brake = env.getActionMap(discreteAction);
        action.accelerate = accel_and_brake[0];
        action.brake = accel_and_brake[1];

        // 4. Lógica de embrague y ABS (del driver base)
        //  action.clutch = driver.clutching(sensors, 0);
        //  action.brake = driver.filterABS(sensors, (float)action.brake); 
       
        return action;
    }

}
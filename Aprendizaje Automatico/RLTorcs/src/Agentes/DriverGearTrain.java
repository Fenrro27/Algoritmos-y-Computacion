package Agentes;

import QLearning.GearEnv;
import QLearning.IEnvironment;
import QLearning.Politica;
import QLearning.QLearning;
import champ2011client.Action;
import champ2011client.SensorModel;

public class DriverGearTrain extends DriverBase {

	// === 1. COMPONENTES DE Q-LEARNING ===
	private QLearning agent;
	private IEnvironment env; // Tu AccelEnv

	// === 2. VARIABLES DE ESTADO PARA EL APRENDIZAJE ===
	private int previousState = -1;
	private int previousAction = -1;
	
	// Variables para detectar si no avanza ===
	private double lastDistance = 0.0;
    private int noProgressCount = 0;
    
    // Configuración:
    private final double MIN_SPEED_THRESHOLD = 5.0;   // 5 km/h mínimo
    private final double MIN_DELTA_DISTANCE = 0.2;    // Metros avanzandos por tick
    private final int MAX_NO_PROGRESS_TICKS = 1000;   
	public DriverGearTrain() {
		System.out.println("Iniciando DriverGearTrain...");

		// B. Inicializar el Entorno (pasándole la política y 'this')
		// Nota: AccelEnv usará 'this' para llamar a getSteerState
		this.env = new GearEnv(0.1, 0.99, 0.4); // Politica - Alpha - Gamma - Epsilon inicial

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
		
		// Resetear variables de control de avance
        lastDistance = 0.0;
        noProgressCount = 0;
		
		agent.saveQTableCSV();
		agent.savePolicyText();
	}

	@Override
	public void shutdown() {
		// Aquí podrías guardar la Q-Table de aceleración
		System.out.println("Guardando tabla Q...");
		agent.saveQTableCSV();
		agent.savePolicyText();
		System.out.println("Bye bye!");
	}

	// ===================================================================
	// === MÉTODO CLAVE: CONTROL (BUCLE DE APRENDIZAJE) ===
	// ===================================================================
	@Override
	public Action control(SensorModel sensors) {

		// --- 1. LÓGICA DE STUCK (Seguridad heredada) ---
		// Si el coche se atasca, dejamos de aprender y usamos lógica de rescate
		if (Math.abs(sensors.getAngleToTrackAxis()) > stuckAngle)
			stuck++;
		else
			stuck = 0;

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

		// --- 2. NUEVO: DETECCIÓN DE "NO AVANCE" (TIMEOUT) ---
		boolean isStuckStopped = false;
		// Solo chequeamos si la carrera ha comenzado realmente (dist > 0)
        if (sensors.getDistanceFromStartLine() > 0) {
            double currentDistance = sensors.getDistanceRaced();
            double deltaDist = currentDistance - lastDistance; // Cuanto avanzó en este tick

            // Si va lento Y NO avanza distancia significativa
            if (sensors.getSpeed() < MIN_SPEED_THRESHOLD && deltaDist < MIN_DELTA_DISTANCE) {
                noProgressCount++;
            } else {
                // Si se mueve, bajamos el contador (recuperación)
                if (noProgressCount > 0) noProgressCount--;
            }
            
            // Actualizamos lastDistance para el siguiente tick
            lastDistance = currentDistance; 
        }

        // Verificamos si superó el tiempo máximo parado
        if (noProgressCount > MAX_NO_PROGRESS_TICKS) {
            isStuckStopped = true;
            System.out.println("⚠️ REINICIO: Coche atascado (No avance detectado).");
        }
        
		// --- 3. LÓGICA DE Q-LEARNING (Sustituye a tu antiguo 'else') ---

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

		if (isDone || isStuckStopped) {
			// El episodio terminó (choque o salida)
			System.out.println("Episodio Finalizado.");
			agent.decayEpsilon(0.95, 0.05); // Reducimos exploración

			previousState = -1; // Reset para siguiente episodio
			previousAction = -1;

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
	// === MÉTODO DE TRADUCCIÓN DE ACCIÓN (Ahora específico para GEAR) ===
	// ===================================================================
	/**
     * Traduce la acción discreta del agente (0=Downshift, 1=Hold, 2=Upshift) a un objeto Action de TORCS.
     * La aceleración, frenado y volante se controlan con la lógica de DriverBase, ya que el agente solo aprende marchas.
     */
	public Action mapActionToTorcs(DriverBase baseDriver, SensorModel sensors, int discreteAction) {
	
        Action action = new Action();
        int currentGear = sensors.getGear();

        // 1. Lógica de Marchas (Controlada por el Agente Q-Learning)
        switch (discreteAction) {
            case 0: // Downshift
                action.gear = currentGear - 1;
                break;
            case 1: // Hold Gear
                action.gear = currentGear;
                break;
            case 2: // Upshift
                action.gear = currentGear + 1;
                break;
            default:
                action.gear = currentGear; // Por si acaso
                break;
        }

        // Ajuste de límites de marcha (no más bajo que -1)
        if (action.gear < 0) {
            action.gear = 0;
        }else if(action.gear>9){
        	action.gear = 9;
        }
        
        // 2. Lógica de Aceleración y Frenado (Usamos el DriverBase)
        
    	float accel_and_brake = getAccel(sensors); // Lógica de aceleración/frenado original
    	float accel,brake;

         if (accel_and_brake>0){
        	accel = accel_and_brake;
        	brake = 0;
        	}
        	else
        	{
        	accel = 0;
        	brake = filterABS(sensors,-accel_and_brake);
        	}
        
        action.accelerate = accel; 
        action.brake = brake;

        // 3. Lógica de Volante (Usamos el DriverBase)
        action.steering = super.getSteer(sensors);

        // 4. Lógica de Embrague y ABS (Usamos el DriverBase)
        action.clutch = super.clutching(sensors, clutch);
        action.brake = super.filterABS(sensors, (float)action.brake); 
       
        return action;
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
	public float getSteer(SensorModel sensors) {
		return super.getSteer(sensors);
	}

	@Override
	public float filterABS(SensorModel sensors, float brake) {
		return super.filterABS(sensors, brake);
	}

}
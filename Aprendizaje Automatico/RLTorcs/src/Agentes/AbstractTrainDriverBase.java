package Agentes;

import QLearning.EnvGear;
import QLearning.IEnvironment;
import QLearning.QLearning;
import champ2011client.Action;
import champ2011client.SensorModel;

public abstract class AbstractTrainDriverBase extends DriverBase{


	// === 1. COMPONENTES DE Q-LEARNING ===
	private QLearning agent;
	private IEnvironment env; // Tu AccelEnv
	private int nEpisodios=0;
	private int nMaxEpisodios = 20;
	
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
    
	protected int currentLearnedAction = -1; 
    

	@Override
	public void reset() {
		// El episodio terminó (choque o salida)
		System.out.println("Episodio Finalizado. ("+(nEpisodios+1)+"/"+nMaxEpisodios+")");
		nEpisodios++;
		
		// Reseteamos AQUÍ para evitar que entre en el siguiente tick
		noProgressCount = 0;
		lastDistance = 0.0;
		previousState = -1; 
		previousAction = -1;
		if(nEpisodios>nMaxEpisodios) {
			//Lanzamos excepcion
			System.out.println("!!! ALCANZADO MÁXIMO DE EPISODIOS (" + nMaxEpisodios + ") !!!");
			System.out.println("Guardando estado final del aprendizaje...");
			
			// 1. Guardar Tablas
			agent.saveQTableCSV();
			agent.savePolicyText();

			System.out.println("FIN DEL ENTRENAMIENTO: Se completaron " + nMaxEpisodios + " episodios.");
			System.exit(0);
		}
		agent.decayEpsilon(0.95, 0.05); // Reducimos exploración

		previousState = -1; // Reset para siguiente episodio
		previousAction = -1;
		
		
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
		if (Math.abs(sensors.getAngleToTrackAxis()) > stuckAngle)
			stuck++;
		else
			stuck = 0;

		if (stuck > stuckTime) {
			noProgressCount = 0; 
			// Lógica de recuperación original de DriverBase (Hardcoded para salir del atasco)
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
			return action; 
		}

		// --- 2. DETECCIÓN DE "NO AVANCE" ---
		boolean isStuckStopped = false;
        if (sensors.getDistanceFromStartLine() > 0) {
            double currentDistance = sensors.getDistanceRaced();
            double deltaDist = currentDistance - lastDistance; 

            if (sensors.getSpeed() < MIN_SPEED_THRESHOLD && deltaDist < MIN_DELTA_DISTANCE) {
                noProgressCount++;
            } else {
                if (noProgressCount > 0) noProgressCount--;
            }
            lastDistance = currentDistance; 
        }

        if (noProgressCount > MAX_NO_PROGRESS_TICKS) {
            isStuckStopped = true;
            System.out.println("⚠️ REINICIO: Coche atascado (No avance detectado).");
        }
        
		// --- 3. LÓGICA DE Q-LEARNING UNIVERSAL ---

		// A. Obtener estado y recompensa
		int currentState = env.discretizeState(sensors);
		double reward = env.calculateReward(sensors);
		boolean isDone = env.isEpisodeDone(sensors);

		// B. Aprender
		if (previousState != -1) {
			agent.updateQTable(previousState, previousAction, reward, currentState);
		}

		// C. Decidir acción o Reiniciar
		if (isDone || isStuckStopped) {
			Action resetAction = new Action();
			resetAction.restartRace = true;
			return resetAction;
		} else {
			// El agente elige la acción abstracta (int)
			int nextAction = agent.chooseAction(currentState);
			
			// GUARDAMOS LA ACCIÓN EN LA VARIABLE DE CLASE
			// Las clases hijas leerán esto dentro de su Override de getAccel/getSteer
			this.currentLearnedAction = nextAction;

			previousState = currentState;
			previousAction = nextAction;
		}

		// D. EJECUTAR (Construcción estándar basada en métodos polimórficos)
		// Esto replica la lógica final de DriverBase.control, pero llamando a tus métodos
		
		Action action = new Action();
		
		// 1. Obtener valores llamando a los métodos (uno de ellos estará sobrecargado por el hijo)
		float steer = getSteer(sensors);
		int gear = getGear(sensors);
		float accel_and_brake = getAccel(sensors); // Puede venir de heurística o de Q-Learning
		
		// 2. Normalización de dirección (lógica original)
        if (steer < -1) steer = -1;
        if (steer > 1)  steer = 1;
        
        // 3. Separación Acelerador/Freno (lógica original)
        float accel, brake;
        if (accel_and_brake > 0) {
            accel = accel_and_brake;
            brake = 0;
        } else {
            accel = 0;
            // Aplicar ABS al freno (lógica original)
            brake = filterABS(sensors, -accel_and_brake);
        }
        
        // 4. Embrague (lógica original)
        clutch = clutching(sensors, clutch);

        // 5. Asignar al objeto final
        action.gear = gear;
        action.steering = steer;
        action.accelerate = accel;
        action.brake = brake;
        action.clutch = clutch;
        
		return action;
	}
}
package QLearning;

import champ2011client.SensorModel;

public class EnvAccel implements IEnvironment {

	double alpha = 0.2;
	double gamma = 0.8;
	double epsilon = 0.8;
	double minEpsilon = 0.2;
	double decayEpsilonFactor = 0.002;

	private final int NUM_STATES = 20;
	// carretera
	// Constants for Reward Calculation
	final float maxSpeedDist = 70;
	final float maxSpeed = 150;
	final float sin5 = (float) 0.08716;
	final float cos5 = (float) 0.99619;

	private final int NUM_ACTIONS = 5;
	private final float[][] ACTION_MAP = {
			{ 1f }, // 0: Full Accel
			{ 0.55f }, // 1: Medium Accel
			{ 0f },
			{ -0.45f }, // 2: No Accel
			{ -0.9f }, // 8: Full Brake
	};

	private int stuck = 0;
	final int stuckTime = 25;
	final float stuckAngle = (float) 0.523598775; // PI/6
	private boolean isStuckState = false; // "Chivato" interno

	// Variables para detectar si no avanza ===
	protected double lastDistance = 0.0;
	protected int noProgressCount = 0;

	// Configuración:
	protected final double MIN_SPEED_THRESHOLD = 5.0; // 5 km/h mínimo
	protected final double MIN_DELTA_DISTANCE = 0.2; // Metros avanzandos por tick
	protected final int MAX_NO_PROGRESS_TICKS = 1000;

	public EnvAccel() {

	}

	@Override
	public String getName() {
		return "Accel";
	}

	@Override
	public int getNumStates() {
		return NUM_STATES;
	}

	@Override
	public int getNumActions() {
		return NUM_ACTIONS;
	}

	@Override
	public int discretizeState(SensorModel sensors) {
		double speed = sensors.getSpeed();
		double distance = sensors.getTrackEdgeSensors()[9];

		// 1. DISCRETIZACIÓN DE VELOCIDAD (5 Niveles)
		int speedState;
		if (speed < 5.0) {
			speedState = 0; // Atascado/Arranque
		} else if (speed < 45.0) {
			speedState = 1; // Lento
		} else if (speed < 80.0) {
			speedState = 2; // Medio
		} else {
			speedState = 3; // Muy Rápido / Top Speed
		}

		// 2. DISCRETIZACIÓN DE DISTANCIA (5 Niveles)
		int distState;
		if (distance < 20.0) {
			distState = 0; // Crítico (Pánico)
		} else if (distance < 45.0) {
			distState = 1; // Peligro (Frenada)
		} else if (distance < 75.0) {
			distState = 2; // Precaución
		} else if (distance < 120.0) {
			distState = 3; // Vía libre
		} else {
			distState = 4; // Recta infinita
		}
		// 3. Combinar en un solo índice (0 a 8)
		// Fórmula: speedState * 3 + distState
		return speedState * 5 + distState;
	}

	// Variables calculadas automáticamente para LINEAL y SIGMOIDE
	// --- Lógica para Velocidad (LINEAL) ---
	double m_speed = 0.018382;
	double c_speed = -0.869194;
	// Formula: reward = m * speed + c

	// --- Lógica para Distancia (SIGMOIDE) ---
	double k_dist = 0.103472;
	double mu_dist = 42.593600;

	@Override
	public double calculateReward(SensorModel sensors) {
		double speed = sensors.getSpeed();
		double frontDist = sensors.getTrackEdgeSensors()[9]; // Sensor central
		double trackPos = sensors.getTrackPosition();

		// -----------------------------------------------------------
		// 1. CONDICIÓN DE TERMINACIÓN (Salida de pista)
		// -----------------------------------------------------------
		if (Math.abs(trackPos) > 0.98) {
			return -1000.0;
		}

		double reward = 0.0;

		// -----------------------------------------------------------
		// 2. LÓGICA DE CONDUCCIÓN (Estado y Recompensa integrados)
		// -----------------------------------------------------------

		// CASO 0: COCHE CASI PARADO (Velocidad < 5 km/h)
		// Lógica: Si no se mueve, castigo constante (-10).
		// Sumamos 'speed' para que intente llegar al menos a 5 km/h.
		if (speed < 5.0) {
			reward = -10.0 + speed;
		}

		// CASO 1: CRÍTICO (El muro está encima: Distancia < 0.4 * Velocidad)
		// Lógica: La velocidad aquí es suicida. Castigo fuerte (-2 * v).
		// Ejemplo: A 100 km/h, si tienes menos de 40m, recibes -200 puntos.
		else if (frontDist < speed * 0.4) {
			reward = -2.0 * speed;
		}

		// CASO 2: PELIGRO (Distancia corta: Distancia < 0.8 * Velocidad)
		// Lógica: Vas demasiado rápido para frenar cómodo. Castigo leve.
		// Incentiva a soltar el acelerador o frenar un poco.
		else if (frontDist < speed * 0.8) {
			reward = -0.5 * speed;
		}

		// CASO 3: PRECAUCIÓN (Distancia media: Distancia < 1.5 * Velocidad)
		// Lógica: Tienes espacio pero no infinito. Recompensa positiva pequeña.
		// Incentiva mantener velocidad pero sin volverse loco.
		else if (frontDist < speed * 1.5) {
			reward = 0.2 * speed;
		}

		// CASO 4: SEGURO (Vía Libre)
		// Lógica: Tienes mucho espacio por delante.
		// Aquí es donde maximizamos la recompensa. ¡Corre!
		else {
			reward = 1.0 * speed;
		}

		return reward;
	}

	@Override
	public boolean isEpisodeDone(SensorModel sensors) {
		double posPista = sensors.getTrackPosition();
		double timeoutSegundos = sensors.getCurrentLapTime();
		double lastLap = sensors.getLastLapTime();
		boolean noAvance = false;

		boolean isDone = false;
		if (isStuckState) {
			isDone = true;
		}
		// 1. Salirse de la pista
		if (Double.isNaN(posPista) || Math.abs(posPista) > 0.98) {
			isDone = true;
		}
		// pero podemos usar el tiempo de vuelta)
		if (timeoutSegundos > 200.0) { // Timeout de 200 segundos
			isDone = true;
		}
		// 3. Vuelta completada
		if (lastLap > 0.0) {
			isDone = true;
		}

		// DETECCIÓN DE "NO AVANCE"
		if (sensors.getDistanceFromStartLine() > 0) {
			double currentDistance = sensors.getDistanceRaced();
			double deltaDist = currentDistance - lastDistance;

			if (sensors.getSpeed() < MIN_SPEED_THRESHOLD && deltaDist < MIN_DELTA_DISTANCE) {
				noProgressCount++;
			} else {
				if (noProgressCount > 0)
					noProgressCount--;
			}
			lastDistance = currentDistance;
		}

		if (noProgressCount > MAX_NO_PROGRESS_TICKS * 3) {
			noAvance = true;
			isDone = true;
		}

		if (isDone) {
			System.out.println("\t- Estado De Stuck: " + isStuckState);
			System.out.println("\t- Posicion En Pista[-0.98,0.98]: " + posPista);
			System.out.println("\t- Tiempo En Segundos[0,200]: " + timeoutSegundos);
			System.out.println("\t- Ultima Vuelta; " + lastLap);
			System.out.println("\t- No Avance Detectado (MAX_NO_PROGRESS_TICKS): " +
					noAvance);

		}

		return isDone;
	}

	@Override
	public double getGamma() {
		return gamma;
	}

	@Override
	public double getAlpha() {
		return alpha;
	}

	@Override
	public double getEpsilon() {
		return epsilon;
	}

	@Override
	public float[][] getActionMap() {
		return ACTION_MAP;
	}

	@Override
	public float[] getActionFromMap(int discreteAction) {

		return ACTION_MAP[discreteAction];

	}

	@Override
	public void reset() {
		this.stuck = 0;
		this.isStuckState = false;
		// Resetear variables de control de avance
		lastDistance = 0.0;
		noProgressCount = 0;
	}

	@Override
	public double getDecayEpsilonFactor() {
		return decayEpsilonFactor;
	}

	@Override
	public double getMinEpsilon() {
		return minEpsilon;
	}

}

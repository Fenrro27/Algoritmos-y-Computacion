package QLearning;

import champ2011client.SensorModel;

public class EnvAccel implements IEnvironment {

	double alpha = 0.2;
	double gamma = 0.8;
	double epsilon = 0.8;
	double minEpsilon = 0.2;
	double decayEpsilonFactor = 0.002;

	private final int NUM_STATES = 20;
	final float maxSpeedDist = 70;
	final float maxSpeed = 150;
	final float sin5 = (float) 0.08716;
	final float cos5 = (float) 0.99619;

	private final int NUM_ACTIONS = 5;
	private final float[][] ACTION_MAP = {
			{ 1f }, 
			{ 0.55f }, 
			{ 0f },
			{ -0.45f },
			{ -0.9f }, 
	};

	final int stuckTime = 25;
	final float stuckAngle = (float) 0.523598775; // PI/6
	private boolean isStuckState = false; 

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

		int speedState;
		if (speed < 5.0) {
			speedState = 0; // Atascado/Arranque
		} else if (speed < 45.0) {
			speedState = 1; // Lento
		} else if (speed < 80.0) {
			speedState = 2; // Medio
		} else {
			speedState = 3; // Muy Rápido
		}

		int distState;
		if (distance < 20.0) {
			distState = 0; // Crítico
		} else if (distance < 45.0) {
			distState = 1; // Peligro
		} else if (distance < 75.0) {
			distState = 2; // Precaución
		} else if (distance < 120.0) {
			distState = 3; // Vía libre
		} else {
			distState = 4; // Recta infinita
		}
		return speedState * 5 + distState;
	}


	double m_speed = 0.018382;
	double c_speed = -0.869194;

	double k_dist = 0.103472;
	double mu_dist = 42.593600;

	@Override
	public double calculateReward(SensorModel sensors) {
		double speed = sensors.getSpeed();
		double frontDist = sensors.getTrackEdgeSensors()[9]; // Sensor central
		double trackPos = sensors.getTrackPosition();

		
		if (Math.abs(trackPos) > 0.98) {
			return -1000.0;
		}

		double reward = 0.0;

		if (speed < 5.0) {
			reward = -10.0 + speed;
		}
		else if (frontDist < speed * 0.4) { // Peligro Inminente. Vas demasiado rápido para el poco espacio que tienes
			reward = -2.0 * speed;
		}
		else if (frontDist < speed * 0.8) { // Precaución. Te estás acercando a algo y sigues yendo rápido
			reward = -0.5 * speed;
		}
		else if (frontDist < speed * 1.5) { // Zona Segura. Tienes espacio razonable
			reward = 0.2 * speed;
		}
		else { // Recta Libre. Tienes mucho espacio por delante
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
		if (Double.isNaN(posPista) || Math.abs(posPista) > 0.98) {
			isDone = true;
		}
		if (timeoutSegundos > 200.0) { // Timeout de 200 segundos
			isDone = true;
		}
		if (lastLap > 0.0) {
			isDone = true;
		}

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

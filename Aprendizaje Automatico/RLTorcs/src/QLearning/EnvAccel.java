package QLearning;

import champ2011client.SensorModel;

public class EnvAccel implements IEnvironment {

	double alpha = 0.2;
	double gamma = 0.8;
	double epsilon = 0.7;
	double minEpsilon = 0.3;
	double decayEpsilonFactor = 0.001;

	// 11 distance bins (0-200 in 20s) * 3 speed bins = 33 states
	private final int NUM_STATES = 40;
	// carretera
	// Constants for Reward Calculation
	final float maxSpeedDist = 70;
	final float maxSpeed = 150;
	final float sin5 = (float) 0.08716;
	final float cos5 = (float) 0.99619;

	private final int NUM_ACTIONS = 9;
	private final float[][] ACTION_MAP = {
			{ 1f }, // 0: Full Accel
			{ 0.5f }, // 1: Medium Accel
			{ 0.2f }, // 2: Low Accel
			{ 0.1f }, // 3: Coast
			{ 0.0f }, // 4: Brake
			{ -0.1f }, // 5: Coast
			{ -0.2f }, // 6: Low Accel
			{ -0.5f }, // 7: Medium Accel
			{ -1f }, // 8: Full Brake
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

	private double lastSpeed = 0;

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
		double s9 = sensors.getTrackEdgeSensors()[9];

		int distState;
		if (s9 < 20) {
			distState = 0;
		} else if (s9 < 40) {
			distState = 1;
		} else if (s9 < 60) {
			distState = 2;
		} else if (s9 < 80) {
			distState = 3;
		} else if (s9 < 100) {
			distState = 4;
		} else if (s9 < 120) {
			distState = 5;
		} else if (s9 < 140) {
			distState = 6;
		} else if (s9 < 160) {
			distState = 7;
		} else if (s9 < 180) {
			distState = 8;
		} else {
			distState = 9;
		}

		int velState;
		if (speed < 20) {
			velState = 0;
		} else if (speed < 40) {
			velState = 1;
		} else if (speed < 60) {
			velState = 2;
		} else {
			velState = 3;
		}

		return distState * 4 + velState;
	}

	@Override
	public double calculateReward(SensorModel sensors) {
		double speed = sensors.getSpeed();
		double trackPos = sensors.getTrackPosition();

		double difSpeed = speed - lastSpeed;
		lastSpeed = speed;

		// Si te saliste, castigo máximo
		if (Math.abs(trackPos) > 0.96f)
			return -10000.0;

		if (speed > 20)
			return 10 * (Math.pow(1 / (1 + Math.abs(trackPos)), 4) * 0.7 + (speed / 200) * 0.3);
		else if (speed <= 1) {
			double plusReward = 0;
			if (difSpeed > 0) {
				plusReward = difSpeed;
			}

			return -200 + plusReward;
		}

		else {

			return  (speed -20.0)/2.0;
		}
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

		if (noProgressCount > MAX_NO_PROGRESS_TICKS*3) {
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
		lastSpeed=0;
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

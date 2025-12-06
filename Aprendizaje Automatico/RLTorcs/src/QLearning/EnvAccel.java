package QLearning;

import champ2011client.SensorModel;

public class EnvAccel implements IEnvironment {

	double alpha = 0.2;
	double gamma = 0.8;
	double epsilon = 0.4;//0.8;
	double minEpsilon = 0.1;
	double decayEpsilonFactor = 0.005;


	private final int NUM_STATES = ; // 5 posiciones en la carretera x 7 angulos de la
																// carretera
	private final int NUM_ACTIONS = ;
	private final float[][] ACTION_MAP = {};

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
		
	}


	@Override
	public double calculateReward(SensorModel sensors) {
		double trackPosition = sensors.getTrackPosition(); // [-1, 1]
		double angle = sensors.getAngleToTrackAxis(); // [-pi, pi]

		if (Double.isNaN(trackPosition) || Math.abs(trackPosition) > 0.98) {
			return -1000.0;

		}

		double reward = 10 * (1 - Math.abs(trackPosition));
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
		// 2. Timeout (basado en ticks, difícil de replicar aquí,
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

		if (noProgressCount > MAX_NO_PROGRESS_TICKS) {
			noAvance = true;
			isDone = true;
		}

		if (isDone) {
			System.out.println("\t- Estado De Stuck: " + isStuckState);
			System.out.println("\t- Posicion En Pista[-1,1]: " + posPista);
			System.out.println("\t- Tiempo En Segundos[0,200]: " + timeoutSegundos);
			System.out.println("\t- Ultima Vuelta; " + lastLap);
			System.out.println("\t- No Avance Detectado: " + noAvance);

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

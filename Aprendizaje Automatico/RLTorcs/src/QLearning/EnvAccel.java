package QLearning;

import java.util.Random;

import champ2011client.SensorModel;

public class EnvAccel implements IEnvironment {

	double alpha = 0.2;
	double gamma = 0.8;
	double epsilon = 0.75;
	double minEpsilon = 0.001;
	double decayEpsilonFactor = 0.0035;

	private final int numFrontalRayBins = 8;
	private final int numSpeedBins = 5;
	private final int NUM_STATES = numFrontalRayBins;// * numSpeedBins;
	// carretera
	private final int NUM_ACTIONS = 8;
	private final float[][] ACTION_MAP = { { 1f }, { 0.8f },
			{ 0.4f }, { 0.2f }, { 0.1f },
			{ 0f }, { -0.2f }, { -0.6f } };

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
		int d = discretizeRay(sensors.getTrackEdgeSensors()[9]);
		return d;
	}

	public int discretizeRay(double frontDist) {
		// Distancias críticas para TORCS (Simplificadas)
		if (frontDist < 20)
			return 0; // Muy cerca (Frenada de emergencia)
		else if (frontDist < 40)
			return 1; // Cerca
		else if (frontDist < 60)
			return 2; // Media-Cerca
		else if (frontDist < 80)
			return 3; // Media
		else if (frontDist < 100)
			return 4; // Lejos
		else if (frontDist < 120)
			return 5; // Lejos
		else if (frontDist < 140)
			return 6; // Lejos
		else
			return 7; // Recta larga
	}

	@Override
	public double calculateReward(SensorModel sensors) {
		double trackPosition = sensors.getTrackPosition(); // [-1, 1]
		double speed = sensors.getSpeed(); // km/h

		if (Double.isNaN(trackPosition) || Math.abs(trackPosition) > 0.98) {
			return -100000.0;
		}

		double reward = 10*(speed / 200) + 10 * (1.0 - Math.abs(trackPosition));

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

		if (noProgressCount > MAX_NO_PROGRESS_TICKS * 2) {
			noAvance = true;
			isDone = true;
		}

		if (isDone) {
			System.out.println("\t- Estado De Stuck: " + isStuckState);
			System.out.println("\t- Posicion En Pista[-0.98,0.98]: " + posPista);
			System.out.println("\t- Tiempo En Segundos[0,200]: " + timeoutSegundos);
			System.out.println("\t- Ultima Vuelta; " + lastLap);
			System.out.println("\t- No Avance Detectado (MAX_NO_PROGRESS_TICKS x 2): " + noAvance);

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

package QLearning;

import champ2011client.SensorModel;

public class EnvSteer implements IEnvironment {

	double alpha = 0.2;
	double gamma = 0.8;
	double epsilon = 0.3;
	double minEpsilon = 0.3;
	double decayEpsilonFactor = 0.01;

	private final int numAngleBins = 5;
	private final int numPosBins = 5;
	private final int NUM_STATES = numAngleBins * numPosBins;// 5 posiciones en la carretera x 7 angulos de la
																// carretera
	private final int NUM_ACTIONS = 5;
	private final float[][] ACTION_MAP = { { -0.40f },{ -0.15f },{ 0.0f },{ 0.15f } , { 0.40f } };
	
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

	public EnvSteer() {

	}

	@Override
	public String getName() {
		return "Steer";
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
		int p = discretizePosition(sensors.getTrackPosition());
		int a = discretizeAngle(sensors.getAngleToTrackAxis());

		switch (p) {
		case 0:
			switch (a) {
			case 0:
				return 0;
			case 1:
				return 1;
			case 2:
				return 2;
			case 3:
				return 3;
			default:// caso 4
				return 4;
			}
		case 1:
			switch (a) {
			case 0:
				return 5;
			case 1:
				return 6;
			case 2:
				return 7;
			case 3:
				return 8;
			default:// caso 4
				return 9;
			}
		case 2:
			switch (a) {
			case 0:
				return 10;
			case 1:
				return 11;
			case 2:
				return 12;
			case 3:
				return 13;
			default:// caso 4
				return 14;
			}
		case 3:
			switch (a) {
			case 0:
				return 15;
			case 1:
				return 16;
			case 2:
				return 17;
			case 3:
				return 18;
			default:// caso 4
				return 19;
			}
		default:// caso 4
			switch (a) {
			case 0:
				return 20;
			case 1:
				return 21;
			case 2:
				return 22;
			case 3:
				return 23;
			default:// caso 4
				return 24;

			}

		}

	}

	private int discretizePosition(double pos) {
		if (pos < -0.6)
			return 0;
		if (pos < -0.2)
			return 1;
		if (pos < 0.2)
			return 2;
		if (pos < 0.6)
			return 3;
		return 4;
	}

	private int discretizeAngle(double angle) {
		if (angle < -0.40)
			return 0;
		if (angle < -0.15)
			return 1;
		if (angle < 0.15)
			return 2;
		if (angle < 0.40)
			return 3;
		return 4;

	}

	@Override
	public double calculateReward(SensorModel sensors) {
		double trackPosition = sensors.getTrackPosition();
		double angle = sensors.getAngleToTrackAxis();
		double alignmentBonus = 5.0 * Math.cos(angle);

		double multiplicador =alignmentBonus;
		
		
		// Castigo terminal
		if (Double.isNaN(trackPosition)) {
			return -30.0;
		}
		if (Math.abs(trackPosition) > 0.80) {
		    double restante = 1 - Math.abs(trackPosition); // cercano a 0 en el borde
		    if (restante<0.025)
		    	return -1000;
		    double reward = -Math.exp(1 /(restante*8));      // cuanto más pequeño restante, más negativo
		    return reward;
		}


		double distanceReward = 10.0 * (1.0 - Math.abs(trackPosition));
		

		 if(Math.abs(trackPosition)<=0.80) {
			 double reward = 1 /(Math.abs(trackPosition));
			 if ((reward)*multiplicador >300)
				 return 300;
			 return (reward)*multiplicador;
		}
		 return multiplicador;

		// Premia ir recto (cos(0)=1). Ayuda en las rectas.
		
		
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
		if (timeoutSegundos > 400.0) { // Timeout de 200 segundos
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
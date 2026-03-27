package QLearning;

import champ2011client.SensorModel;

public class EnvGear implements IEnvironment {

	double alpha = 0.1; 
	double gamma = 0.99;
	double epsilon = 0.5;
	double minEpsilon = 0.2;
	double decayEpsilonFactor = 0.005;

	private final int NUM_STATES = 108;
	private final int NUM_ACTIONS = 3;
	private final float[][] ACTION_MAP = { { -1 }, { 0 }, { 1 } };

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

	public EnvGear() {

	}

	@Override
	public String getName() {
		return "Gear";
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
		int gear = sensors.getGear();
		double speed = sensors.getSpeed();
		double rpm = sensors.getRPM();

		int gearIndex = gear - 1;
		if (gearIndex < 0)
			gearIndex = 0;
		if (gearIndex > 5)
			gearIndex = 5;

	
		int speedState;

		if (speed < 25) {
			speedState = 0; 
		} else if (speed < 50) {
			speedState = 1; 
		} else if (speed < 75) {
			speedState = 2; 
		} else if (speed < 100) {
			speedState = 3; 
		} else if (speed < 125) {
			speedState = 4; 
		} else {
			speedState = 5; // > 125 km/h
		}
			

		int rpmState;

		if (rpm < 1800) {
			rpmState = 0; 
		} else if (rpm < 7500) {
			rpmState = 1; 
		} else {
			rpmState = 2; 
		}

		return ((gearIndex * 6) + speedState)*3 + rpmState;
	}

	@Override
    public double calculateReward(SensorModel sensors) {
        double rpm = sensors.getRPM();
        double speed = sensors.getSpeed();
        int gear = sensors.getGear();

        // RECOMPENSA BASE
        double reward = Math.abs(speed) / 10.0;

        // Premiamos mantener el motor alegre para facilitar la aceleración
        if (rpm >= 2500 && rpm <= 7000) {
            reward += 5.0;
        }

    
        if (rpm < 1800 && gear > 1) reward -= 3.0; // Ahogo leve
        if (rpm > 8500) reward -= 3.0;             // Aviso de línea roja

        // Filtro de Velocidad Mínima para TODAS las marchas       
        boolean marchaIncorrecta = false;

        // Marcha 1ª: Siempre permitida (es para salir de 0).
        if (gear == 1 && speed > 25) marchaIncorrecta = true;
		else if (gear == 2 && speed < 25) marchaIncorrecta = true;
        else if (gear == 3 && speed < 50) marchaIncorrecta = true;
        else if (gear == 4 && speed < 75) marchaIncorrecta = true;
        else if (gear == 5 && speed < 100) marchaIncorrecta = true;
        else if (gear == 6 && speed < 125) marchaIncorrecta = true;

        if (marchaIncorrecta) {
            reward -= 5.0; 
        }
        if (rpm >= 9400) return -50.0;  // Romper motor
        if (speed < -5.0) return -10.0; // Ir marcha atrás

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
		
		if (timeoutSegundos > 400.0) { // Timeout de 200 segundos
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

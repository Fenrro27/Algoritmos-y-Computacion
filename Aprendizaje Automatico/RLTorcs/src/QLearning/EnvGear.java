package QLearning;

import champ2011client.SensorModel;

public class EnvGear implements IEnvironment {

	double alpha = 0.1; // 0.99, 0.5, 0.1, 0.05;
	double gamma = 0.99;
	double epsilon = 0.5;
	double minEpsilon = 0.02;
	double decayEpsilonFactor = 0.005;

	private final int NUM_STATES = 108;
	private final int NUM_ACTIONS = 3;
	private final float[][] ACTION_MAP = { { -1 }, { 0 }, { 1 } };

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

		// 1. Procesar Marcha (0 a 5)
		int gearIndex = gear - 1;
		if (gearIndex < 0)
			gearIndex = 0;
		if (gearIndex > 5)
			gearIndex = 5;

		// 2. Procesar Velocidad (0 a 150+) en 6 Bins
		// Tamaño del bin = 150 / 6 = 25 km/h
		int speedState;

		if (speed < 25) {
			speedState = 0; // 0 - 25 km/h
		} else if (speed < 50) {
			speedState = 1; // 25 - 50 km/h
		} else if (speed < 75) {
			speedState = 2; // 50 - 75 km/h
		} else if (speed < 100) {
			speedState = 3; // 75 - 100 km/h
		} else if (speed < 125) {
			speedState = 4; // 100 - 125 km/h
		} else {
			speedState = 5; // > 125 km/h (Incluye 150 y más)
		}
			

		int rpmState;

		if (rpm < 1800) {
			rpmState = 0; // 0 - 2000 rpm
		} else if (rpm < 7500) {
			rpmState = 1; // 2000 - 4000 rpm
		} else {
			rpmState = 2; // > 6000 rpm
		}


		// 3. Estado Combinado
		return ((gearIndex * 6) + speedState)*3 + rpmState;
		//return gearIndex *3 + rpmState;

	}

	@Override
    public double calculateReward(SensorModel sensors) {
        double rpm = sensors.getRPM();
        double speed = sensors.getSpeed();
        int gear = sensors.getGear();

        // 1. RECOMPENSA BASE: Velocidad (Queremos que corra)
        double reward = Math.abs(speed) / 10.0;

        // 2. BONUS ZONA DE POTENCIA
        // Premiamos mantener el motor alegre para facilitar la aceleración
        if (rpm >= 2500 && rpm <= 7000) {
            reward += 5.0;
        }

        // 3. CASTIGOS CLÁSICOS (RPM)
        // Castigos suaves para guiarle, pero no determinantes
        if (rpm < 1800 && gear > 1) reward -= 3.0; // Ahogo leve (Lugging)
        if (rpm > 8500) reward -= 3.0;             // Aviso de línea roja

        // ============================================================
        // 4. EL "PORTERO" (Filtro de Velocidad Mínima para TODAS las marchas)
        // ============================================================
        // Si el coche está en una marcha alta sin la velocidad mínima necesaria,
        // significa que no tiene par motor para acelerar. Castigo severo.
        
        boolean marchaIncorrecta = false;

        // Marcha 1ª: Siempre permitida (es para salir de 0).
        // Marcha 2ª: Exige mínimo ~35 km/h. Si vas a 20 km/h, mete 1ª.
        if (gear == 1 && speed > 25) marchaIncorrecta = true;
		else if (gear == 2 && speed < 25) marchaIncorrecta = true;
        // Marcha 3ª: Exige mínimo ~65 km/h.
        else if (gear == 3 && speed < 50) marchaIncorrecta = true;
        // Marcha 4ª: Exige mínimo ~100 km/h.
        else if (gear == 4 && speed < 75) marchaIncorrecta = true;
        // Marcha 5ª: Exige mínimo ~140 km/h.
        else if (gear == 5 && speed < 100) marchaIncorrecta = true;
        // Marcha 6ª: Exige mínimo ~175 km/h.
        else if (gear == 6 && speed < 125) marchaIncorrecta = true;


        // APLICAR CASTIGO DEL PORTERO
        if (marchaIncorrecta) {
            reward -= 5.0; // ¡Baja de marcha ya!
        }
        // ============================================================

        // 5. CASTIGOS CRÍTICOS (Game Over / Desastre)
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

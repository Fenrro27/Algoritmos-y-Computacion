package QLearning;

import champ2011client.SensorModel;

public class GearEnv implements IEnvironment {

	double alpha;
	double gamma;
	double epsilon;

	private final int NUM_STATES = 3;
	private final int NUM_ACTIONS = 3;
	private final float[][] ACTION_MAP = { { 0 }, { 1 }, { 2 } };
	
	//rpmGear
	final int[]  rpmGearUp={5000,6000,6000,6500,7000,0};
	final int[]  rpmGearDown={0,2500,3000,3000,3500,3500};
	
	//rpmVel
	final double[] velGearUp   = { 60.0, 110.0, 160.0, 210.0, 250.0, 400.0 }; 
    final double[] velGearDown = { 0.0,   40.0,  90.0, 140.0, 190.0, 230.0 };

	public GearEnv(double alpha, double gamma, double epsilon) {
		this.alpha = alpha;
		this.gamma = gamma;
		this.epsilon = epsilon;
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
	    double rpm = sensors.getRPM();

	    // 1. Ajuste de índice de marcha (Gear 1 es índice 0)
	    // Si está en Neutral (0) o Reversa (-1), usamos la lógica de 1ra marcha
	    if (gear < 1) gear = 1;

	    int gearIndex = gear - 1;

	    // Protección: si la marcha es mayor que el tamaño de tu array (ej: 7ª), usamos la última disponible
	    if (gearIndex >= rpmGearUp.length) {
	        gearIndex = rpmGearUp.length - 1;
	    }

	    // 2. Obtenemos los límites específicos para ESTA marcha
	    int limitDown = rpmGearDown[gearIndex];
	    int limitUp   = rpmGearUp[gearIndex];

	    // 3. Retornamos el estado (0: Bajo, 1: Óptimo, 2: Alto)
	    if (rpm < limitDown) {
	        return 0; // RPM demasiado bajas para esta marcha
	    } else if (rpm > limitUp) {
	        return 2; // RPM demasiado altas para esta marcha
	    } else {
	        return 1; // Zona óptima
	    }
	}

	@Override
	public double calculateReward(SensorModel sensors) {
	    double rpm = sensors.getRPM();
	    double speed = sensors.getSpeed(); // km/h
	    int gear = sensors.getGear();
	    
	    double reward = 0.0;

	    // 0. MANEJO DE CASOS ESPECIALES (Neutral o Marcha atrás)
	    // Si el coche está en neutral o reversa, penalizamos si hay velocidad positiva 
	    // o premiamos si está parado. Simplificaremos forzando a que busque ir hacia adelante.
	    if (gear < 1) {
	        // Si estamos en neutral pero el coche se mueve, castigo fuerte para que meta marcha
	        if (speed > 5) return -1.0;
	        return 0.0; 
	    }
	    
	    // Índice para los arrays (gear 1 es índice 0)
	    int idx = gear - 1;
	    // Protección por si acaso llega una marcha rara (ej. 7)
	    if (idx >= rpmGearUp.length) idx = rpmGearUp.length - 1;

	    // 1. OBTENER LÍMITES DINÁMICOS PARA LA MARCHA ACTUAL
	    double rMin = rpmGearDown[idx];
	    double rMax = rpmGearUp[idx];
	    
	    

	    // 2. RECOMPENSA BASE: ¿ESTAMOS EN EL RANGO RPM IDEAL?
	    // Si estamos dentro del rango [rMin, rMax], damos recompensa positiva.
	    // Si nos salimos, penalizamos proporcionalmente a qué tan lejos estamos.
	    
	    if (rpm >= rMin && rpm <= rMax) {
	        // --- DENTRO DE LA ZONA DE PODER ---
	        // Damos una recompensa alta (0.5). 
	        // Además, premiamos estar cerca del límite superior (donde hay más potencia) 
	        // pero sin pasarse.
	        double range = rMax - rMin;
	        if(range == 0) range = 1; // Evitar div 0
	        
	        // Normalizamos dónde estamos dentro del rango (0.0 a 1.0)
	        double positionInBand = (rpm - rMin) / range;
	        
	        // Preferimos estar en la mitad superior del rango (más potencia), 
	        // así que sumamos un bonus pequeño.
	        reward += 0.5 + (positionInBand * 0.2); 
	        
	    } else {
	        // --- FUERA DE LA ZONA (CAMBIO NECESARIO) ---
	        // Penalización por estar fuera.
	        // Calculamos la distancia al límite más cercano.
	        double dist = 0;
	        if (rpm < rMin) dist = rMin - rpm;
	        else dist = rpm - rMax;
	        
	        // Normalizamos el castigo (ej: si te pasas por 2000 rpm es castigo máximo)
	        double penalty = Math.min(1.0, dist / 2000.0);
	        reward -= penalty * 0.8; // Castigo fuerte pero no total
	    }
 
	    // 5. PENALIZACIONES CRÍTICAS (LÍMITES DUROS)
	    // Motor a punto de calarse (< 1000 RPM) o corte de inyección (> 9500)
	    if (rpm < 1000) reward = -1.0; // Fallo catastrófico inminente
	    if (rpm > 9500) reward = -1.0; // Daño motor

	    // 6. CLIPPING FINAL
	    // Aseguramos que la recompensa se mantenga entre -1 y 1 para estabilidad matemática del Q-Learning
	    if (reward > 1.0) reward = 1.0;
	    if (reward < -1.0) reward = -1.0;

	    return reward;
	}



	@Override
	public boolean isEpisodeDone(SensorModel sensors) {
		// 1. Salirse de la pista
		if (Math.abs(sensors.getTrackPosition()) >= 1.3) {
			return true;
		}
		// 2. Timeout (basado en ticks, difícil de replicar aquí,
		// pero podemos usar el tiempo de vuelta)
		if (sensors.getCurrentLapTime() > 200.0) { // Timeout de 200 segundos
			return true;
		}
		// 3. Vuelta completada
		if (sensors.getLastLapTime() > 0.0) {
			return true;
		}
		// 4. Choque (Deberías añadir esto)
		if (sensors.getDamage() > 0) {
			return true;
		}
		return false;
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

	public float[] getActionMap(int discreteAction) {

		return ACTION_MAP[discreteAction];

	}

}

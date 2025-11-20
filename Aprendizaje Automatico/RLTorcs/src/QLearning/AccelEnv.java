package QLearning;

import Agentes.DriverAccelTrain;
import Agentes.DriverBase;
import champ2011client.Action;
import champ2011client.SensorModel;

public class AccelEnv implements IEnvironment{

	
	double alpha;
	double gamma;
	double epsilon;
	
	private final int NUM_STATES = 11;
    private final int NUM_ACTIONS = 3;
    private final float[][] ACTION_MAP = {{0,1}, {0.5f,0}, {1,0}};
	
    public AccelEnv(double alpha, double gamma,double epsilon) {
    	this.alpha = alpha;
    	this.gamma = gamma;
    	this.epsilon = epsilon;
    }
    
	@Override
	public String getName() {
		return "Acceleration";
	}

	@Override
	public int getNumStates() {
		return NUM_STATES;
	}

	@Override
	public int getNumActions() {
		return NUM_ACTIONS;
	}

	private boolean estaEntre(double valor, double minimo, double maximo) {
		return (minimo <= valor && valor <= maximo);
	}
	
	@Override
	public int discretizeState(SensorModel sensors) {
        double distVec9 = sensors.getTrackEdgeSensors()[9];

        if (estaEntre(distVec9, 0, 20)) return 0;
        if (estaEntre(distVec9, 20, 40)) return 1;
        if (estaEntre(distVec9, 40, 60)) return 2;
        if (estaEntre(distVec9, 60, 80)) return 3;
        if (estaEntre(distVec9, 80, 100)) return 4;
        if (estaEntre(distVec9, 100, 120)) return 5;
        if (estaEntre(distVec9, 120, 140)) return 6;
        if (estaEntre(distVec9, 140, 160)) return 7;
        if (estaEntre(distVec9, 160, 180)) return 8;
        if (distVec9 >= 180) return 9; // Corregido: 180-200 y > 200
        if (distVec9 < 0) return 10;
        
        return 0; // Fallback
    }

	@Override
	public double calculateReward(SensorModel sensors) {
        // Penalización por salirse (fin de episodio)
        if (Math.abs(sensors.getTrackPosition()) >= 1) {
            return -10000.0;
        }
        // Penalización por choque
        if (sensors.getDamage() > 0) {
            return -10000.0;
        }
        // Penalización por timeout (simplificado)
        if (sensors.getCurrentLapTime() > 200.0) {
            return -10000.0;
        }

        // Recompensa positiva por seguir en pista
        double rewardTrackPosition = Math.pow(1 /((Math.abs(sensors.getTrackPosition())) + 1), 4) * 0.7;
        double rewardSpeed = (sensors.getSpeed() / 200) * 0.3;
        
        return rewardTrackPosition + rewardSpeed;
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

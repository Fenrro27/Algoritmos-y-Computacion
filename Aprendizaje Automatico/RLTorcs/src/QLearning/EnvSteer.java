package QLearning;

import champ2011client.SensorModel;

public class EnvSteer implements IEnvironment {

	double alpha;
	double gamma;
	double epsilon;

	private final int numAngleBins = 9;
	private final int numPosBins = 5;
	private final int NUM_STATES = numAngleBins*numPosBins; // 5 posiciones en la carretera x 9 angulos de la carretera
	private final int NUM_ACTIONS = 9; // {-1, -0.45, -0.2,-0.1, 0,0.1,0.2, 0.45, 1}
	private final float[][] ACTION_MAP = {{-1},{-0.45f},{-0.2f},{-0.1f},{0},{0.1f},{0.2f},{0.45f},{1}   };
	
	
    public EnvSteer() {
    	
    }
    
	public EnvSteer(double alpha, double gamma, double epsilon) {
		this.alpha = alpha;
		this.gamma = gamma;
		this.epsilon = epsilon;
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
	        return p * numAngleBins + a;	
	}
	
	 private int discretizePosition(double pos) {
	        double norm = Math.max(-1.0, Math.min(1.0, pos));
	        double step = 2.0 / (numPosBins - 1);
	        int idx = (int) Math.round((norm + 1.0) / step);
	        return Math.max(0, Math.min(numPosBins - 1, idx));
	    }

	    private int discretizeAngle(double angle) {
	        double norm = Math.max(-Math.PI / 2, Math.min(Math.PI / 2, angle));
	        double step = (Math.PI) / (numAngleBins - 1);
	        int idx = (int) Math.round((norm + Math.PI / 2) / step);
	        return Math.max(0, Math.min(numAngleBins - 1, idx));
	    }
	    
	    
	    @Override
	    public double calculateReward(SensorModel sensors) {
	        double trackPosition = sensors.getTrackPosition(); 
	        double angle = sensors.getAngleToTrackAxis(); 

           if (Math.abs(trackPosition) > 1.0) {
	            return -1000.0; 
	        }

	        // Queremos estar en el centro (0).
	        double distFromCenter = Math.abs(trackPosition);
	        // Recompensa lineal: 1.0 en el centro, 0.0 en el borde.
	        double positionReward = 1.0 - distFromCenter;

	        double cosAngle = Math.cos(angle);
	        double angleReward = Math.max(0, cosAngle);
	        
	        double reward = positionReward * angleReward;

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
	@Override
	public float[][] getActionMap(){
		return ACTION_MAP;
	}
	@Override
	public float[] getActionFromMap(int discreteAction) {

		return ACTION_MAP[discreteAction];

	}

}

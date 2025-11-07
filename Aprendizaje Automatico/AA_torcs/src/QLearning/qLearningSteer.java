package QLearning;

import champ2011client.SensorModel;

public class qLearningSteer extends qLearningBase {

    private final int numPosBins = 5;    // posiciones laterales más finas
    private final int numAngleBins = 5;  // discretización más fina del ángulo
    private final int numActionsLocal = 7; // más giros de volante

    private int lastState = -1;
    private int lastAction = -1;
    private boolean restartRace = false;

    private final float[] steerValues; // valores de giro del volante
    private final double epsilonMin = 0.01;
    private final double epsilonDecayLinear = 0.005;

    public qLearningSteer() {
        alpha = 0.2;
        gamma = 0.9;
        epsilon = 0.8;

        steerValues = new float[] {-1f, -0.2f, -0.1f, 0f, 0.1f, 0.2f, 1f};


        int totalStates = numPosBins * numAngleBins;
        initQTable(totalStates, numActionsLocal);
        loadQTableCSV(false);
    }

    @Override
    protected int getStateIndex(Object s) {
        SensorModel sensors = (SensorModel) s;
        int p = discretizePosition(sensors.getTrackPosition());
        int a = discretizeAngle(sensors.getAngleToTrackAxis());
        return p * numAngleBins + a;
    }

    private int discretizePosition(double pos) {
        double norm = Math.max(-1.0, Math.min(1.0, pos));

        if (norm < -0.7) return 0;   // izquierda
        if (norm <= -0.3) return 1;   // izquierda)
        if (norm <= 0.3) return 2;   
        if (norm <= 0.7) return 3;   // centro (70%)
        return 4;                     // derecha (15%)
    }

    private int discretizeAngle(double angle) { // Los angulos nos lo dan en radianes
        // Límites en radianes
        double neg25 = Math.toRadians(-25);
        double neg5  = Math.toRadians(-5);
        double pos5  = Math.toRadians(5);
        double pos25 = Math.toRadians(25);

        if (angle < neg25) return 0;       // muy a la izquierda
        if (angle < neg5)  return 1;       // izquierda leve
        if (angle <= pos5) return 2;       // centrado
        if (angle <= pos25) return 3;      // derecha leve
        return 4;                          // muy a la derecha
    }


    @Override
    protected double computeReward(Object s, int action) {
        SensorModel sensors = (SensorModel) s;
        double pos = sensors.getTrackPosition();
        double angle = sensors.getAngleToTrackAxis();

        if (Double.isNaN(pos) || Math.abs(pos) > 0.95) {
            restartRace = true;
            return -10000 * Math.abs(angle);
        }

        restartRace = false;

        // Recompensa combinando centrado y orientación
        double reward =
                Math.pow(1 / (1 + Math.abs(pos)), 4) * 0.7 +
                Math.pow(1 / (1 + Math.abs(angle)), 4) * 0.3;

        return reward;
    }

    public float chooseSteer(SensorModel sensors) {
        int state = getStateIndex(sensors);
        int action = chooseAction(state);
        float steer = steerValues[action];

        if (lastState != -1) {
            double reward = computeReward(sensors, lastAction);
            int nextState = getStateIndex(sensors);
            updateQ(lastState, lastAction, reward, nextState);
        }

        lastState = state;
        lastAction = action;
        iterationCount++;

        if (iterationCount % 500 == 0) saveQTableCSV();

        return steer;
    }

    private void decayEpsilonLinear() {
        epsilon = Math.max(epsilon - epsilonDecayLinear, epsilonMin);
    }

    private float decodeAction(int action) {
        return steerValues[action];
    }

    public boolean shouldRestartRace() {
        return restartRace;
    }

    public void resetRaceFlag() {
        restartRace = false;
        decayEpsilonLinear();
    }

    @Override
    protected String getQTableName() {
        return "QTable_Steer";
    }
    
    
}

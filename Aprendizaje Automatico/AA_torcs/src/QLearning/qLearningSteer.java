package QLearning;

import champ2011client.SensorModel;

public class qLearningSteer extends qLearningBase {

    private final int numPosBins = 3;
    private final int numAngleBins = 5;
    private final int numActionsLocal = 7;

    private int lastState = -1;
    private int lastAction = -1;
    
    private boolean restartRace = false;

    public qLearningSteer() {
        int totalStates = numPosBins * numAngleBins;
        initQTable(totalStates, numActionsLocal);
        loadQTableCSV();
    }

    // Codificación estado (posición, ángulo) → índice único
    @Override
    protected int getStateIndex(Object s) {
        SensorModel sensors = (SensorModel) s;
        int p = discretizePosition(sensors.getTrackPosition());
        int a = discretizeAngle(sensors.getAngleToTrackAxis());
        return p * numAngleBins + a;
    }

    private int discretizePosition(double pos) {
        if (pos < -0.5) return 0;
        if (pos > 0.5) return 2;
        return 1;
    }

    private int discretizeAngle(double angle) {
        double deg = Math.toDegrees(angle);
        if (deg < -10) return 0;
        if (deg < -3) return 1;
        if (deg < 3) return 2;
        if (deg < 10) return 3;
        return 4;
    }

    @Override
    protected double computeReward(Object s, int action) {
        SensorModel sensors = (SensorModel) s;
        double pos = sensors.getTrackPosition();
        double angle = sensors.getAngleToTrackAxis();
        double trackSensor = sensors.getTrackEdgeSensors()[9]; // sensor central

        // Condición de salida de pista
        if (Double.isNaN(pos) || Double.isInfinite(pos) ||
                Math.abs(pos) > 0.95 || trackSensor < 0.1) {
            restartRace = true;
            return -10000 * Math.abs(angle);
        }
        
        double rewardPos = Math.pow(1.0 / (1.0 + Math.abs(pos)), 4) * 0.7;
        double rewardAngle = Math.pow(1.0 / (1.0 + Math.abs(angle)), 4) * 0.3;
        restartRace = false;
        
        return rewardPos + rewardAngle;
    }

    public float chooseSteer(SensorModel sensors) {
        int state = getStateIndex(sensors);
        int action = chooseAction(state);
        float steer = decodeAction(action);

        if (lastState != -1) {
            double reward = computeReward(sensors, lastAction);
            int nextState = getStateIndex(sensors);
            updateQ(lastState, lastAction, reward, nextState);
        }

        lastState = state;
        lastAction = action;

        iterationCount++;
        decayEpsilon();
        if (iterationCount % 500 == 0) saveQTableCSV();

        return steer;
    }

    private float decodeAction(int action) {
        float[] vals = {-1f, -0.2f, -0.1f, 0f, 0.1f, 0.2f, 1f};
        return vals[action];
    }
    
    public boolean shouldRestartRace() {
        return restartRace;
    }

    public void resetRaceFlag() {
        restartRace = false;
    }

    @Override
    protected String getQTableName() {
        return "QTable_Steer";
    }
}

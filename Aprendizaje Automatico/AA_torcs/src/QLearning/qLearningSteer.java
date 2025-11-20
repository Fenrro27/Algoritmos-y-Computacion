package QLearning;

import champ2011client.SensorModel;

public class qLearningSteer extends qLearningBase {

    private final int numPosBins = 7;     // zonas laterales
    private final int numAngleBins = 11;  // zonas de ángulo
    private final int numActionsLocal = 13; // acciones de dirección

    private int lastState = -1;
    private int lastAction = -1;
    private boolean restartRace = false;

    private final float[] steerValues; // valores de giro del volante
    private final double epsilonMin = 0.01;
    private final double epsilonDecayLinear = 0.001;

    private int restartCount = 0;
    private boolean evaluationMode = false;
    private double startDistance = 0.0;

    public qLearningSteer() {
        alpha = 0.2;
        gamma = 0.9;
        epsilon = 0.8;

        // Acciones de dirección en rango [-1, 1]
        steerValues = new float[numActionsLocal];
        for (int i = 0; i < numActionsLocal; i++) {
            steerValues[i] = -1f + 2f * i / (numActionsLocal - 1);
        }

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
    protected double computeReward(Object s, int action) {
        SensorModel sensors = (SensorModel) s;
        double pos = sensors.getTrackPosition();
        double angle = sensors.getAngleToTrackAxis();

        if (Double.isNaN(pos) || Math.abs(pos) > 0.95) {
            restartRace = true;
            return -10000 * Math.abs(angle);
        }

        restartRace = false;

        double reward =
                Math.pow(1 / (1 + Math.abs(pos)), 4) * 0.7 +
                Math.pow(1 / (1 + Math.abs(angle)), 4) * 0.3;

        return reward;
    }

    public float chooseSteer(SensorModel sensors) {
        int state = getStateIndex(sensors);
        int action;

        if (evaluationMode) {
            // Solo acción greedy
            action = getBestAction(state);
        } else {
            // Política epsilon-greedy normal
            action = chooseAction(state);
        }

        float steer = steerValues[action];

        if (lastState != -1 && !evaluationMode) {
            double reward = computeReward(sensors, lastAction);
            int nextState = getStateIndex(sensors);
            updateQ(lastState, lastAction, reward, nextState);
        }

        lastState = state;
        lastAction = action;
        iterationCount++;

        if (!evaluationMode && iterationCount % 500 == 0) saveQTableCSV();

        return steer;
    }

    private void decayEpsilonLinear() {
        epsilon = Math.max(epsilon - epsilonDecayLinear, epsilonMin);
    }

    public boolean shouldRestartRace() {
        return restartRace;
    }

    public void resetRaceFlag(SensorModel sensors) {
        restartRace = false;
        decayEpsilonLinear();

        restartCount++;

        if (restartCount % 10 == 0) {
            // Activar evaluación
            evaluationMode = true;
            startDistance = sensors.getDistanceRaced();
            System.out.println("=== INICIO MODO EVALUACIÓN #" + (restartCount / 10) + " ===");
        } else if (evaluationMode) {
            // Fin de evaluación
            double distanceTravelled = sensors.getDistanceRaced() - startDistance;
            System.out.println("=== FIN MODO EVALUACIÓN #" + (restartCount / 10) + " ===");
            System.out.printf("Distancia recorrida: %.2f metros%n", distanceTravelled);
            evaluationMode = false;
        }
    }

    @Override
    protected String getQTableName() {
        return "QTable_Steer";
    }

    @Override
    protected boolean checkSuccess(Object s) {
        SensorModel sensors = (SensorModel) s;
        
        if (!restartRace) return true;
        return false;
        
    }

    
    
}

package QLearning;

import champ2011client.SensorModel;

public class qLearningAccel extends qLearningBase {

    private final int numStates = 11;      // 10 bins + 1 estado fuera pista (-1)
    private final int numActionsLocal = 6; // acciones (acelerador, freno)

    // Acciones: {acelerador, freno}
    private final double[][] actions = {
            {0.0, 0.0},   // 0: nada
            {0.3, 0.0},   // 1: acelerar poco
            {0.6, 0.0},   // 2: acelerar medio
            {1.0, 0.0},   // 3: acelerar fuerte
            {0.0, 0.3},   // 4: frenar poco
            {0.0, 0.6}    // 5: frenar fuerte
    };

    private int lastState = -1;
    private int lastAction = -1;

    private int ticks = 0;
    private boolean restartRace = false;

    private int restartCount = 0;
    private boolean evaluationMode = false;
    private double startDistance = 0.0;

    public qLearningAccel() {
        alpha = 0.2;
        gamma = 0.9;
        epsilon = 0.6;
        epsilonDecay = 0.99;

        initQTable(numStates, numActionsLocal);
        loadQTableCSV(false);
    }

    /** 0 = fuera pista
     *  1..10 = bins de 20 en 20 (0-20,20-40,...,180-200)
     */
    private int computeState(double val) {
        if (val < 0) return 0;
        int bin = (int)(val / 20.0);
        if (bin > 9) bin = 9;
        return bin + 1;
    }

    @Override
    protected int getStateIndex(Object s) {
        SensorModel sensors = (SensorModel) s;
        return computeState(sensors.getTrackEdgeSensors()[9]);
    }

    @Override
    protected double computeReward(Object s, int action) {
        SensorModel sensors = (SensorModel) s;

        // 1: fuera de la pista
        if (sensors.getTrackEdgeSensors()[9] < 0) {
            restartRace = true;
            return -10000;
        }

        // 2: demasiados ticks
        if (ticks > 10000) {
            restartRace = true;
            return -10000;
        }

        restartRace = false;

        // 3: fórmula general
        double trackPos = Math.abs(sensors.getTrackPosition());
        double r1 = Math.pow(1.0 / (1.0 + trackPos), 4.0) * 0.7;

        double speed = Math.max(0, Math.min(200, sensors.getSpeed()));
        double r2 = (speed / 200.0) * 0.3;

        return r1 + r2;
    }

    /** Devuelve {acelerador, freno} */
    public double[] chooseAccel(SensorModel sensors) {

        int state = getStateIndex(sensors);
        int action;

        if (evaluationMode) {
            action = getBestAction(state);
        } else {
            action = chooseAction(state);
        }

        // Actualización Q
        if (lastState != -1 && !evaluationMode) {
            double reward = computeReward(sensors, lastAction);
            int nextState = getStateIndex(sensors);
            updateQ(lastState, lastAction, reward, nextState);
        }

        lastState = state;
        lastAction = action;

        ticks++;
        iterationCount++;            

        if (!evaluationMode && iterationCount % 500 == 0) {
        	decayEpsilon();
        	saveQTableCSV();
        }

        return actions[action];
    }

    public boolean shouldRestartRace() {
        return restartRace;
    }

    /** Igual que Steer: episodios + modo evaluación */
    public void resetRaceFlag(SensorModel sensors) {
        restartRace = false;
        ticks = 0;
        lastState = -1;
        lastAction = -1;

        decayEpsilon();

        restartCount++;

        if (restartCount % 10 == 0) {
            // Activar evaluación
            evaluationMode = true;
            startDistance = sensors.getDistanceRaced();
            System.out.println("=== INICIO MODO EVALUACIÓN #" + (restartCount / 10) + " ===");
        } else if (evaluationMode) {
            // Fin evaluación
            double dist = sensors.getDistanceRaced() - startDistance;
            System.out.println("=== FIN MODO EVALUACIÓN #" + (restartCount / 10) + " ===");
            System.out.printf("Distancia recorrida: %.2f m%n", dist);
            evaluationMode = false;
        }
    }

    @Override
    protected String getQTableName() {
        return "QTable_Accel";
    }

    @Override
    protected boolean checkSuccess(Object s) {
        SensorModel sensors = (SensorModel) s;
        return sensors.getTrackEdgeSensors()[9] >= 0 && ticks <= 10000;
    }
}

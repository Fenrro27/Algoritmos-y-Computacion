package QLearning;

import champ2011client.SensorModel;

public class qLearningSteer extends qLearningBase {

    private final int numPosBins = 3;
    private final int numAngleBins = 5;
    private final int numActionsLocal = 7; // {-1, -0.2, -0.1, 0, 0.1, 0.2, 1}

    private int lastState = -1;
    private int lastAction = -1;

    private boolean restartRace = false;

    public qLearningSteer() {
        // Parámetros de entrenamiento
        alpha = 0.2;
        gamma = 0.9;
        epsilon = 0.8;
        epsilonDecay = 0.9995;

        int totalStates = numPosBins * numAngleBins;
        initQTable(totalStates, numActionsLocal);
        loadQTableCSV();
    }

    @Override
    protected int getStateIndex(Object s) {
        SensorModel sensors = (SensorModel) s;
        int p = discretizePosition(sensors.getTrackPosition());
        int a = discretizeAngle(sensors.getAngleToTrackAxis());
        return p * numAngleBins + a;
    }

    private int discretizePosition(double pos) {
        if (pos < -0.4) return 0; // izquierda
        if (pos < 0.4)  return 1; // centro
        return 2;                  // derecha
    }

    private int discretizeAngle(double angle) {
        double deg = Math.toDegrees(angle);
        if (deg <= -25) return 0; // [-90, -25]
        if (deg <= -5)  return 1; // (-25, -5]
        if (deg <= 5)   return 2; // (-5, 5]
        if (deg <= 25)  return 3; // (5, 25]
        return 4;                  // (25, 90]
    }

    @Override
    protected double computeReward(Object s, int action) {
        SensorModel sensors = (SensorModel) s;

        double pos = sensors.getTrackPosition();         // [-1,1]
        double angle = sensors.getAngleToTrackAxis();    // radianes
        double trackSensor = sensors.getTrackEdgeSensors()[9]; // central
        double speed = sensors.getSpeed();

        // Detectar bin de posición
        int posBin = discretizePosition(pos);
        float steerValue = decodeAction(action);

        // 1️⃣ Penalización por salida de pista
        if (Double.isNaN(pos) || Double.isInfinite(pos) || Math.abs(pos) > 1.0 || trackSensor < 0.1) {
            restartRace = true;
            return -10000;  // castigo brutal por salirse
        }
        restartRace = false;

        double reward = 0.0;

        // 2️⃣ Recompensa por mantener el coche centrado
        if (posBin == 1) {
            reward += 100;  // centro → recompensa alta
        } 
        else {
            double deviation = Math.abs(pos);
            if (deviation < 0.3)
                reward -= 10;   // algo desviado
            else
                reward -= 100;  // muy desviado
        }

        // 3️⃣ Corrección de dirección según posición
        if (posBin == 0 && steerValue > 0) {
            reward += 30;   // está a la izquierda y gira a la derecha → bien
        }
        else if (posBin == 2 && steerValue < 0) {
            reward += 30;   // está a la derecha y gira a la izquierda → bien
        }
        else if (posBin == 0 && steerValue < 0) {
            reward -= 30;   // mal, está a la izquierda y gira más a la izquierda
        }
        else if (posBin == 2 && steerValue > 0) {
            reward -= 30;   // mal, está a la derecha y gira más a la derecha
        }

        // 4️⃣ Penalización por ángulos cerrados (para evitar derrapes)
        double deg = Math.toDegrees(angle);
        if (Math.abs(deg) > 25) reward -= 50;
        else if (Math.abs(deg) > 10) reward -= 10;

        // 5️⃣ Pequeña recompensa adicional por velocidad (opcional)
        double maxSpeed = 200.0;
        reward += 0.1 * (speed / maxSpeed);

        return reward;
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

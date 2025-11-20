package QLearning;

import champ2011client.SensorModel;
import java.io.*;
import java.util.Locale;

public class qLearningSteerReader {

    private double[][] Q; // Q[state][action]
    private final int numPosBins = 7;
    private final int numAngleBins = 11;
    private final int numActions = 13;

    private final String qTablePath;
    private final float[] steerValues;

    public qLearningSteerReader(String csvPath) {
        this.qTablePath = (csvPath == null || csvPath.isEmpty())
                ? "Knowledge/QTable_Steer.csv"
                : csvPath;

        int totalStates = numPosBins * numAngleBins;
        Q = new double[totalStates][numActions];

        // Acciones de volante uniformemente distribuidas en [-1, 1]
        steerValues = new float[numActions];
        for (int i = 0; i < numActions; i++) {
            steerValues[i] = -1f + (2f * i / (numActions - 1)); // desde -1 hasta 1
        }

        loadQTableCSV();
    }

    // -------------------------
    //  Discretización de estados
    // -------------------------

    private int discretizePosition(double pos) {
        // Normaliza a [-1, 1] y mapea a 11 bins
        double norm = Math.max(-1.0, Math.min(1.0, pos));
        int idx = (int) ((norm + 1.0) / 2.0 * (numPosBins - 1));
        return idx;
    }

    private int discretizeAngle(double angle) {
        // Normaliza a [-pi/2, pi/2] y mapea a 21 bins
        double norm = Math.max(-Math.PI / 2, Math.min(Math.PI / 2, angle));
        int idx = (int) ((norm + Math.PI / 2) / Math.PI * (numAngleBins - 1));
        return idx;
    }

    protected int getStateIndex(Object s) {
        SensorModel sensors = (SensorModel) s;
        int p = discretizePosition(sensors.getTrackPosition());
        int a = discretizeAngle(sensors.getAngleToTrackAxis());
        return p * numAngleBins + a;
    }

    private float decodeAction(int action) {
        return steerValues[action];
    }

    // -------------------------
    //  Lógica de decisión
    // -------------------------
    private int argMax(double[] arr) {
        int best = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > arr[best]) best = i;
        }
        return best;
    }

    public float chooseSteer(SensorModel sensors) {
        int state = getStateIndex(sensors);
        int bestAction = argMax(Q[state]);
        float steer = decodeAction(bestAction);

        System.out.printf(Locale.US,
                "State=%d (pos=%.3f, angle=%.3f°) → BestAction=%d → Steer=%.3f%n",
                state,
                sensors.getTrackPosition(),
                Math.toDegrees(sensors.getAngleToTrackAxis()),
                bestAction,
                steer
        );

        return steer;
    }

    // -------------------------
    //  Carga del CSV de Q-table
    // -------------------------
    private void loadQTableCSV() {
        File file = new File(qTablePath);
        if (!file.exists()) {
            System.err.println("No se encontró el archivo: " + qTablePath);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // salta cabecera si la hay
            int state = 0;
            while ((line = reader.readLine()) != null && state < Q.length) {
                String[] t = line.split(",");
                if (t.length < numActions + 1) continue; // columna 0 = estado

                for (int j = 0; j < numActions; j++) {
                    Q[state][j] = Double.parseDouble(t[j + 1]);
                }
                state++;
            }
            System.out.println("Q-table de dirección cargada desde " + qTablePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

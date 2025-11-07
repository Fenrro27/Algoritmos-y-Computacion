package QLearning;

import champ2011client.SensorModel;
import java.io.*;
import java.util.Locale;

public class qLearningSteerReader {

    private double[][] Q; // Q[state][action]
    private final int numPosBins = 5;
    private final int numAngleBins = 5;
    private final int numActions = 7; // {-1, -0.6667, -0.3333, 0, 0.3333, 0.6667, 1}

    private final String qTablePath;

    private final float[] steerValues;

    public qLearningSteerReader(String csvPath) {
        this.qTablePath = (csvPath == null || csvPath.isEmpty())
                ? "Knowledge/QTable_Steer.csv"
                : csvPath;

        int totalStates = numPosBins * numAngleBins;
        Q = new double[totalStates][numActions];

        // Inicialización de los valores de giro del volante [-1,1]
        steerValues = new float[] {-1f, -0.2f, -0.1f, 0f, 0.1f, 0.2f, 1f};
        loadQTableCSV();
    }

    // -------------------------
    //  Discretización de estados
    // -------------------------
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
                "State=%d (pos=%.3f, angle=%.3f) → BestAction=%d → Steer=%.2f%n",
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

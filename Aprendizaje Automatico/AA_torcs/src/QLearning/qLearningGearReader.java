package QLearning;

import champ2011client.SensorModel;
import java.io.*;
import java.util.Locale;

public class qLearningGearReader {

    private double[][] Q; // Q[rpmBin][action]
    private final int[] rpmRanges = {2500, 7000, 9000};
    private final int rpmBins = rpmRanges.length;
    private final int numActions = 3;  // 0=down, 1=keep, 2=up

    private final String qTablePath;
    private int lastGear = 1;

    public qLearningGearReader(String csvPath) {
        Q = new double[rpmBins][numActions];
        this.qTablePath = (csvPath == null || csvPath.isEmpty())
                ? "Knowledge/QTable_Gear.csv"
                : csvPath;
        loadQTableCSV();
    }

    // Discretiza RPM en bins
    private int rpmToBin(double rpm) {
        for (int i = 0; i < rpmRanges.length; i++) {
            if (rpm < rpmRanges[i]) return i;
        }
        return rpmRanges.length - 1;
    }

    private int argMax(double[] arr) {
        int best = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > arr[best]) best = i;
        }
        return best;
    }

    public int chooseGear(SensorModel sensors) {
        double rpm = sensors.getRPM();
        int gear = sensors.getGear();
        if (gear < 1) gear = 1;
        if (gear > 6) gear = 6;

        int rpmBin = rpmToBin(rpm);
        int action = argMax(Q[rpmBin]);
        int newGear = gear;

        if (action == 0 && gear > 1) newGear--;       // bajar marcha
        else if (action == 2 && gear < 6) newGear++; // subir marcha

        System.out.printf(Locale.US,
                "Gear=%d  RPM=%.0f (bin=%d)  Action=%d  →  NewGear=%d%n",
                gear, rpm, rpmBin, action, newGear);

        lastGear = newGear;
        return newGear;
    }

    private void loadQTableCSV() {
        File file = new File(qTablePath);
        if (!file.exists()) {
            System.err.println("No se encontró el archivo: " + qTablePath);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // salta cabecera
            while ((line = reader.readLine()) != null) {
                String[] t = line.split(",");
                if (t.length < 4) continue;

                int rpmBin = Integer.parseInt(t[0]);        // columna 0 = rpm_bin
                double down = Double.parseDouble(t[1]);
                double keep = Double.parseDouble(t[2]);
                double up = Double.parseDouble(t[3]);

                if (rpmBin >= 0 && rpmBin < rpmBins) {
                    Q[rpmBin][0] = down;
                    Q[rpmBin][1] = keep;
                    Q[rpmBin][2] = up;
                }
            }
            System.out.println("Q-table cargada desde " + qTablePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

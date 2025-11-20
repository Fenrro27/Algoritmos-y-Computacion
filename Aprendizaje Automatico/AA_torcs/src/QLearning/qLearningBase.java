package QLearning;

import java.io.*;
import java.util.Random;

public abstract class qLearningBase {

    protected double alpha = 0.2;
    protected double gamma = 0.9;
    protected double epsilon = 0.8;
    protected final double minEpsilon = 0.05;
    protected double epsilonDecay = 0.999;

    // Datos de éxito
    protected double successData = 0;
    protected int episodeCount = 0;
    protected int successfulEpisodes = 0;

    protected int numStates;
    protected int numActions;
    protected double[][] Q; // Q[estado][acción]
    protected Random rnd = new Random();

    protected double cumulativeReward = 0;
    protected int episodeSteps = 0;
    protected int optimalActions = 0;

    // Contador de iteraciones global (todos los pasos)
    protected long iterationCount = 0;

    // Monitor global compartido entre todas las instancias
    protected static final QLearningMonitor monitor = new QLearningMonitor();
    private boolean monitorInitialized = false;

    public qLearningBase() {
        monitor.setTitulo(getQTableName());
    }

    // --- Métodos abstractos ---
    protected abstract int getStateIndex(Object sensors);
    protected abstract double computeReward(Object sensors, int action);
    protected abstract String getQTableName();
    protected abstract boolean checkSuccess(Object sensors); // indica si el episodio fue exitoso

    // --- Inicialización de la tabla ---
    protected void initQTable(int numStates, int numActions) {
        this.numStates = numStates;
        this.numActions = numActions;
        Q = new double[numStates][numActions];

        for (int s = 0; s < numStates; s++) {
            for (int a = 0; a < numActions; a++) {
                Q[s][a] = rnd.nextDouble() * 2 - 1; // [-1, 1]
            }
        }

        if (!monitorInitialized) {
            monitor.initCharts();
            monitorInitialized = true;
        }
    }

    // --- Selección de acción (ε-greedy) ---
    protected int chooseAction(int state) {
        iterationCount++; // incremento cada vez que se elige una acción
        if (rnd.nextDouble() < epsilon) {
            return rnd.nextInt(numActions);
        }
        double maxQ = -Double.MAX_VALUE;
        int best = 0;
        for (int a = 0; a < numActions; a++) {
            if (Q[state][a] > maxQ) {
                maxQ = Q[state][a];
                best = a;
            }
        }
        return best;
    }

    // --- Actualización estándar Q-learning ---
    protected void updateQ(int state, int action, double reward, int nextState) {
        double maxNextQ = -Double.MAX_VALUE;
        for (int a = 0; a < numActions; a++) {
            if (Q[nextState][a] > maxNextQ) maxNextQ = Q[nextState][a];
        }
        Q[state][action] += alpha * (reward + gamma * maxNextQ - Q[state][action]);
        logStep(state, action, reward, nextState);
    }

    // --- Registro de pasos ---
    protected void logStep(int state, int action, double reward, int nextState) {
        cumulativeReward += reward;
        episodeSteps++;
        iterationCount++;

        // acción óptima
        double maxQ = Double.NEGATIVE_INFINITY;
        int bestAction = 0;
        for (int a = 0; a < numActions; a++) {
            if (Q[state][a] > maxQ) {
                maxQ = Q[state][a];
                bestAction = a;
            }
        }
        if (action == bestAction) optimalActions++;

        // Cada 50 pasos actualiza el monitor dinámicamente
        if (episodeSteps % 50 == 0) {
            double avgReward = cumulativeReward / 50.0;
            monitor.update(avgReward, epsilon, Q, successData);
            cumulativeReward = 0;
            optimalActions = 0;
        }
    }


    // --- Finalización del episodio ---
    protected void endEpisode(Object sensors) {
        boolean success = checkSuccess(sensors); // implementado en la subclase
        episodeCount++;
        if (success) successfulEpisodes++;

        successData = 100.0 * successfulEpisodes / episodeCount; // % éxito

        double avgReward = cumulativeReward / Math.max(episodeSteps, 1);
        monitor.update(avgReward, epsilon, Q, successData);

        // reset para el siguiente episodio
        cumulativeReward = 0;
        episodeSteps = 0;
        optimalActions = 0;
    }

    // --- Decaimiento de epsilon ---
    protected void decayEpsilon() {
        if (epsilon > minEpsilon) epsilon *= epsilonDecay;
        if (epsilon < minEpsilon) epsilon = minEpsilon;
    }

    private double computeAvgQ() {
        double sum = 0;
        int count = 0;
        for (int s = 0; s < numStates; s++) {
            for (int a = 0; a < numActions; a++) {
                sum += Q[s][a];
                count++;
            }
        }
        return sum / count;
    }

    // --- Mejor acción (greedy) ---
    public int getBestAction(int state) {
        try {
            double[] qValues = Q[state];
            int best = 0;
            for (int a = 1; a < qValues.length; a++) {
                if (qValues[a] > qValues[best]) best = a;
            }
            return best;
        } catch (Throwable ignored) {
            double oldEpsilon = epsilon;
            try {
                epsilon = 0.0;
                return chooseAction(state);
            } finally {
                epsilon = oldEpsilon;
            }
        }
    }

    // --- Guardar y cargar Q-table ---
    public void saveQTableCSV() {
        try {
            File folder = new File("Knowledge");
            if (!folder.exists()) folder.mkdir();
            File file = new File(folder, getQTableName() + ".csv");

            try (PrintWriter pw = new PrintWriter(file)) {
                pw.println("state,actions...");
                for (int s = 0; s < numStates; s++) {
                    pw.print(s);
                    for (int a = 0; a < numActions; a++)
                        pw.print("," + Q[s][a]);
                    pw.println();
                }
            }
        } catch (IOException e) {
            System.err.println("Error guardando Q-table: " + e.getMessage());
        }
    }

    public void loadQTableCSV(boolean loadIfExists) {
        if (!loadIfExists) return;

        try {
            File file = new File("Knowledge/" + getQTableName() + ".csv");
            if (!file.exists()) return;

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                br.readLine(); // cabecera
                String line;
                while ((line = br.readLine()) != null) {
                    String[] v = line.split(",");
                    if (v.length < numActions + 1) continue;
                    int s = Integer.parseInt(v[0]);
                    for (int a = 0; a < numActions; a++)
                        Q[s][a] = Double.parseDouble(v[a + 1]);
                }
            }
        } catch (IOException e) {
            System.err.println("Error cargando Q-table: " + e.getMessage());
        }
    }

    // --- Finalización del entrenamiento ---
    public void finishTraining() {
        monitor.exportToHTML("Knowledge/monitor_"+getQTableName()+".html");
        System.out.println("Gráficas exportadas en Knowledge/monitor_"+getQTableName()+".html");
    }
}

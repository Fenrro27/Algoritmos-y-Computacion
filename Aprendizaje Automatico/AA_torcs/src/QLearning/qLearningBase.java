package QLearning;

import java.io.*;
import java.util.Random;

public abstract class qLearningBase {

    protected double alpha = 0.2;
    protected double gamma = 0.9;
    protected double epsilon = 0.8;
    protected final double minEpsilon = 0.05;
    protected double epsilonDecay = 0.999;

    protected int numStates;
    protected int numActions;
    protected double[][] Q; // Q[estado][acción]
    protected Random rnd = new Random();

    protected long iterationCount = 0;
    
    protected static QLearningMonitor monitor = new QLearningMonitor();
    protected double cumulativeReward = 0;
    protected int episodeSteps = 0;
    protected int optimalActions = 0;

    // --- Métodos abstractos que cada subclase implementa ---
    protected abstract int getStateIndex(Object sensors);
    protected abstract double computeReward(Object sensors, int action);
    protected abstract String getQTableName();

    // --- Inicialización de la tabla ---
    protected void initQTable(int numStates, int numActions) {
        this.numStates = numStates;
        this.numActions = numActions;
        Q = new double[numStates][numActions];
        
        // Inicialización aleatoria
        for (int s = 0; s < numStates; s++) {
            for (int a = 0; a < numActions; a++) {
                Q[s][a] = rnd.nextDouble() * 2 - 1; // entre -1 y 1
            }
        }
        
    }

    // --- Selección de acción (ε-greedy) ---
    protected int chooseAction(int state) {
        if (rnd.nextDouble() < epsilon) { 
//        	System.out.println("Accion Aleatoria++");
        	return rnd.nextInt(numActions);}
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

    // --- Decaimiento de epsilon ---
    protected void decayEpsilon() {
        if (epsilon > minEpsilon) epsilon *= epsilonDecay;
        if (epsilon < minEpsilon) epsilon = 0;
    }

    // --- Guardar y cargar Q-table ---
    public void saveQTableCSV() {
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
        } catch (IOException e) {
            System.err.println("Error guardando Q-table: " + e.getMessage());
        }
    }

    public void loadQTableCSV(boolean loadIfExists) {
        if (!loadIfExists) return; // no cargar si se indica false

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
        } catch (IOException e) {
            System.err.println("Error cargando Q-table: " + e.getMessage());
        }
    }

    
    protected void logStep(int state, int action, double reward, int nextState) {
        cumulativeReward += reward;
        episodeSteps++;

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

        if (episodeSteps % 50 == 0) { // cada 50 pasos actualiza monitor
            double avgReward = cumulativeReward / 50.0;
            double avgQ = computeAvgQ();
            double optimalPct = 100.0 * optimalActions / 50.0;
            monitor.update(avgReward, epsilon, Q);
            cumulativeReward = 0;
            optimalActions = 0;
        }
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

}

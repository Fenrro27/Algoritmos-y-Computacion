package QLearning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase para cargar y ejecutar una política (Q-Table ya entrenada)
 */
public class Politica {

	private double[][] qTable; // Tabla de valores Q
	private float[][] actionMap; 
	private String name; // Nombre del agente para buscar ficheros por defecto
	private Random random;


	public Politica(IEnvironment env) {
		this.name = env.getName();
		this.actionMap = env.getActionMap();
		this.random = new Random();

		this.qTable = new double[env.getNumStates()][env.getNumActions()];
		resetQTable();
	}

	void resetQTable() {
		for (int i = 0; i < qTable.length; i++) {
			for (int j = 0; j < qTable[i].length; j++) {
				qTable[i][j] = Double.NEGATIVE_INFINITY;
			}
		}
	}

	public void loadPolicyText() {
		resetQTable();
		String filename = "Knowledge/Policy_" + name + ".txt";

		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			System.out.println("Cargando política desde TXT: " + filename);

			int loadedStates = 0;

			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (!line.startsWith("State"))
					continue;

				try {
				
					// Buscamos la flecha "->"
					int arrowIndex = line.indexOf("->");
					if (arrowIndex == -1)
						continue; // Línea inválida

					String statePart = line.substring(5, arrowIndex).trim();
					int state = Integer.parseInt(statePart);

					int qStartIndex = line.indexOf("(Q:");
					int qEndIndex = line.indexOf(")", qStartIndex);

					if (qStartIndex != -1 && qEndIndex != -1) {
						String qPart = line.substring(qStartIndex + 3, qEndIndex).trim();
						
						qPart = qPart.replace(",", ".");

						double qValue = Double.parseDouble(qPart);

						int braceStart = line.indexOf("{");
						int braceEnd = line.indexOf("}");

						if (braceStart != -1 && braceEnd != -1) {
							String actionsPart = line.substring(braceStart + 1, braceEnd);
							String[] actionTokens = actionsPart.split(",");

							if (state < qTable.length) {
								for (String token : actionTokens) {
									token = token.trim();
									if (!token.isEmpty()) {
										int action = Integer.parseInt(token);
										if (action < qTable[state].length) {
											qTable[state][action] = qValue;
										}
									}
								}
								loadedStates++;
							}
						}
					}

				} catch (Exception e) {
					System.err.println("Error parseando línea: '" + line + "' -> " + e.getMessage());
				}
			}
			System.out.println(LocalQLearningUtils.GREEN + "-> Política TXT cargada. Estados procesados: "
					+ loadedStates + LocalQLearningUtils.RESET);

		} catch (IOException e) {
			System.err.println("Error leyendo archivo TXT: " + e.getMessage());
		}
	}


	public float[] getAccionValues(int state) {
		if (state < 0 || state >= qTable.length)
			return getDefaultAction();

		double maxQ = Double.NEGATIVE_INFINITY;
		for (double val : qTable[state]) {
			if (val > maxQ)
				maxQ = val;
		}

		List<Integer> bestActions = new ArrayList<>();
		double tolerance = 1e-5;

		for (int action = 0; action < qTable[state].length; action++) {
			if (Math.abs(qTable[state][action] - maxQ) < tolerance) {
				bestActions.add(action);
			}
		}

		if (bestActions.isEmpty())
			return getDefaultAction();

		int selectedActionIndex = bestActions.get(random.nextInt(bestActions.size()));

		if (selectedActionIndex < actionMap.length) {
			return actionMap[selectedActionIndex];
		}
		return getDefaultAction();
	}

	public void savePolicyText(double[][] qTableToSave) {
		String filename = "Knowledge/Policy_" + name + ".txt";

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
			bw.write("=== POLÍTICA APRENDIDA (Q-LEARNING) ===\n");
			bw.write("-> State -> Best Action(s) [Q-Value]\n");
			bw.write("--------------------------------------\n");

			for (int i = 0; i < qTableToSave.length; i++) {
				List<Integer> bestActions = getBestActionsList(qTableToSave, i);

				double maxQ = qTableToSave[i][bestActions.get(0)];

				StringBuilder actionsStr = new StringBuilder("{");
				for (int k = 0; k < bestActions.size(); k++) {
					actionsStr.append(bestActions.get(k));
					if (k < bestActions.size() - 1)
						actionsStr.append(", ");
				}
				actionsStr.append("}");

				bw.write(String.format("State %3d -> Actions: %-10s (Q: %.4f)\n", i, actionsStr.toString(), maxQ));
			}
			System.out.println(
					LocalQLearningUtils.GREEN + "Política guardada en: " + filename + LocalQLearningUtils.RESET);
		} catch (IOException e) {
			System.err.println("Error al guardar Policy TXT: " + e.getMessage());
		}
	}

	private List<Integer> getBestActionsList(double[][] table, int state) {
		double maxQ = Double.NEGATIVE_INFINITY;
		List<Integer> bestActions = new ArrayList<>();
		double tolerance = 1e-5;

		for (int action = 0; action < table[state].length; action++) {
			double qVal = table[state][action];
			if (qVal > maxQ + tolerance) {
				maxQ = qVal;
				bestActions.clear();
				bestActions.add(action);
			} else if (Math.abs(qVal - maxQ) < tolerance) {
				bestActions.add(action);
			}
		}
		if (bestActions.isEmpty())
			bestActions.add(0);
		return bestActions;
	}

	private float[] getDefaultAction() {
		if (actionMap != null && actionMap.length > 0)
			return actionMap[0];
		return new float[] { 0.0f }; // Neutro
	}

	public int getAccionIndex(int state) {

		
		if (state < 0 || state >= qTable.length)
			return getDefaultActionIndex();

		// Buscar el valor máximo Q
		double maxQ = Double.NEGATIVE_INFINITY;
		for (double val : qTable[state]) {
			if (val > maxQ)
				maxQ = val;
		}

		List<Integer> bestActions = new ArrayList<>();
		double tolerance = 1e-5;

		for (int action = 0; action < qTable[state].length; action++) {
			if (Math.abs(qTable[state][action] - maxQ) < tolerance) {
				bestActions.add(action);
			}
		}

		// Si no hay acciones válidas, usar acción por defecto
		if (bestActions.isEmpty())
			return getDefaultActionIndex();

		// Elegir aleatoriamente entre las mejores
		return bestActions.get(random.nextInt(bestActions.size()));
	}


	private int getDefaultActionIndex() {
		if (actionMap != null && actionMap.length > 0)
			return 0;
		return 0;
	}

	public String verQtableString() {
		StringBuilder sb = new StringBuilder();

		sb.append("=== Q-Learning Agent: ").append((name != null) ? name : "Unknown").append(" ===\n");

		int numStates = qTable.length;
		int numActions = (numStates > 0) ? qTable[0].length : 0;
		sb.append(String.format("Q-Table -> %d States x %d Actions\n", numStates, numActions));
		sb.append("--------------------------------------------------\n");

		
		int previewLimit = Math.min(numStates, 10);

		sb.append("Preview (First ").append(previewLimit).append(" states):\n");

		for (int i = 0; i < previewLimit; i++) {
			sb.append(String.format("State %-3d | ", i));

			double maxVal = Double.NEGATIVE_INFINITY;
			int bestIdx = -1;
			for (int k = 0; k < numActions; k++) {
				if (qTable[i][k] > maxVal) {
					maxVal = qTable[i][k];
					bestIdx = k;
				}
			}

			// Imprimir valores
			sb.append("[");
			for (int j = 0; j < numActions; j++) {
				// Formato: 6 espacios, 3 decimales.
				// Ponemos un asterisco (*) visual en la mejor acción
				String valStr = String.format("%6.3f", qTable[i][j]);
				if (j == bestIdx) {
					sb.append(LocalQLearningUtils.GREEN).append(valStr).append("*").append(LocalQLearningUtils.RESET);
				} else {
					sb.append(valStr).append(" ");
				}

				if (j < numActions - 1)
					sb.append(", ");
			}
			sb.append("]\n");
		}

		if (numStates > previewLimit) {
			sb.append("... (").append(numStates - previewLimit).append(" states more hidden) ...\n");
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("=== POLÍTICA: ").append(name).append(" ===\n");

		int totalStates = qTable.length;
		int actionsPerState = (qTable.length > 0) ? qTable[0].length : 0;

		sb.append("Dimensiones: ").append(totalStates).append(" estados x ").append(actionsPerState)
				.append(" acciones.\n");
		sb.append("--------------------------------------------------\n");

		int printCount = 0;

		for (int i = 0; i < totalStates; i++) {
			boolean isVisited = false;
			for (double val : qTable[i]) {
				if (val > Double.NEGATIVE_INFINITY) {
					isVisited = true;
					break;
				}
			}

			if (isVisited) {
				sb.append(String.format("State %-4d | ", i));

				int bestIdx = getAccionIndex(i);
				float[] realValues = getAccionValues(i);
				double maxQ = qTable[i][bestIdx];

				String actionStr = floatArrayToString(realValues);

				sb.append(String.format("Best: [%d] %-10s | MaxQ: %6.2f | All Qs: ", bestIdx, actionStr, maxQ));

				sb.append("[");
				for (int j = 0; j < qTable[i].length; j++) {
					double val = qTable[i][j];
					if (val == Double.NEGATIVE_INFINITY) {
						sb.append("-inf");
					} else {
						sb.append(String.format("%.2f", val));
					}
					if (j < qTable[i].length - 1)
						sb.append(", ");
				}
				sb.append("]\n");

				printCount++;
				if (printCount > 200) {
					sb.append("... (Se han omitido el resto de estados para no saturar la consola) ...\n");
					break;
				}
			}
		}

		return sb.toString();
	}

	// Método auxiliar para imprimir bonitos los arrays de float
	private String floatArrayToString(float[] arr) {
		if (arr == null)
			return "null";
		StringBuilder s = new StringBuilder("{");
		for (int i = 0; i < arr.length; i++) {
			s.append(arr[i]);
			if (i < arr.length - 1)
				s.append(",");
		}
		s.append("}");
		return s.toString();
	}
}
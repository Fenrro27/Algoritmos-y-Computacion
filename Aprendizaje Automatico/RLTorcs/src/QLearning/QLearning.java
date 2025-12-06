package QLearning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * Real QLearning implementation
 * 
 * Agentes.DriverAccelTrain port:3001
 */

public class QLearning {
	// QLearning parameters
	private double alpha; // learning rate
	private double gamma; // discount factor
	private double epsilon; // exploration rate
	private String name;

	// Q-table
	private double[][] qTable;
	
	// Decaymiento Epsilon
	private double decayEpsilonFactor;
	private double minEpsilon;
	
	private MonitorGrafico monitor;

	public QLearning(int numStates, int numActions, double alpha, double gamma, double epsilon,double decayEpsilonFactor, double minEpsilon) {
		this.alpha = alpha;
		this.gamma = gamma;
		this.epsilon = epsilon;
		this.decayEpsilonFactor=decayEpsilonFactor;
		this.minEpsilon=minEpsilon;
		this.qTable = new double[numStates][numActions];
		initializeRandomQTable();
		initChart();

	}

	public QLearning(IEnvironment env) {
		this.name = env.getName();
		this.alpha = env.getAlpha();
		this.gamma = env.getGamma();
		this.epsilon = env.getEpsilon();
		this.decayEpsilonFactor=env.getDecayEpsilonFactor();
		this.minEpsilon=env.getMinEpsilon();
		this.qTable = new double[env.getNumStates()][env.getNumActions()];
		initializeRandomQTable();
		initChart();
	}
	
	private void initChart() {
		// Creamos el gráfico y lo hacemos visible
		this.monitor = new MonitorGrafico("Entrenamiento: " + ((name!=null)?name:"Agente"));
		this.monitor.setVisible(true);
	}
	
	// Método para llamar desde tu bucle principal para actualizar la gráfica
	// Recomiendo llamarlo al final de cada episodio o cada X ticks
	public void updatePlot(double currentReward) {
		if(monitor != null) {
			monitor.agregarDato(this.epsilon, currentReward);
		}
	}
	
	private void initializeRandomQTable() {
		for (int i = 0; i < qTable.length; i++) {
			for (int j = 0; j < qTable[i].length; j++) {
				// Math.random() devuelve [0.0, 1.0)
				// Multiplicamos por 2 -> [0.0, 2.0)
				// Restamos 1 -> [-1.0, 1.0)
				qTable[i][j] = (Math.random() * 2.0) - 1.0;
			}
		}
	}

	public int chooseAction(int state) {
		if (Math.random() < epsilon) {
			// Explore: choose a random action
			return (int) (Math.random() * qTable[state].length);
		} else {
			// Exploit: choose the best action from Q-table
			double maxQ = Double.NEGATIVE_INFINITY;
			int bestAction = 0;
			for (int action = 0; action < qTable[state].length; action++) {
				if (qTable[state][action] > maxQ) {
					maxQ = qTable[state][action];
					bestAction = action;
				}
			}
			return bestAction;
		}
	}

	// Funcion para mirar al futuro
	public void updateQTable(int state, int action, double reward, int nextState, boolean isTerminal) {
	    
	    double maxQNext = 0.0; // Por defecto 0 si es terminal

	    // Solo calculamos el futuro si NO hemos muerto/terminado
	    if (!isTerminal) {
	        maxQNext = Double.NEGATIVE_INFINITY;
	        for (int nextAction = 0; nextAction < qTable[nextState].length; nextAction++) {
	            if (qTable[nextState][nextAction] > maxQNext) {
	                maxQNext = qTable[nextState][nextAction];
	            }
	        }
	    }

	    // Aplicamos la fórmula (ahora maxQNext será 0 si isTerminal es true)
	    qTable[state][action] += alpha * (reward + gamma * maxQNext - qTable[state][action]);
	}

	public void decayEpsilon() {
		epsilon = Math.max(minEpsilon, epsilon - decayEpsilonFactor);
		System.out.println("\t- Epsilon: " + (float)epsilon);
	}

// Lógica de guardado y carga del archivo QLearning

// ===================================================================
// === GESTIÓN DE ARCHIVOS (CSV y TXT) ===
// ===================================================================

	
	 /**
     * Devuelve una LISTA con todas las acciones que tienen el Q-Value máximo.
     * Útil para manejar empates y para guardar la política correctamente.
     */
    public List<Integer> getBestActionsList(int state) {
        double maxQ = Double.NEGATIVE_INFINITY;
        List<Integer> bestActions = new ArrayList<>();
        
        // Tolerancia para comparaciones de punto flotante
        double tolerance = 1e-5;

        for (int action = 0; action < qTable[state].length; action++) {
            double qVal = qTable[state][action];

            if (qVal > maxQ + tolerance) {
                // Nuevo máximo encontrado
                maxQ = qVal;
                bestActions.clear();
                bestActions.add(action);
            } else if (Math.abs(qVal - maxQ) < tolerance) {
                // Empate encontrado
                bestActions.add(action);
            }
        }
        
        // Seguridad por si la lista está vacía (no debería ocurrir)
        if (bestActions.isEmpty()) {
            bestActions.add(0);
        }
        
        return bestActions;
    }
    
	/**
	 * Guarda la Q-Table completa en formato CSV (Comma Separated Values). Formato:
	 * State, Action0, Action1, Action2...
	 */
	public void saveQTableCSV() {
		String filename = "Knowledge/QTable_"+name+".csv";
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
			// 1. Escribir Cabecera
			bw.write("State");
			for (int i = 0; i < qTable[0].length; i++) {
				bw.write(",Action_" + i);
			}
			bw.newLine();

			// 2. Escribir Datos
			for (int i = 0; i < qTable.length; i++) {
				bw.write(String.valueOf(i)); // Estado ID
				for (int j = 0; j < qTable[i].length; j++) {
					bw.write("," + qTable[i][j]); // Valor Q
				}
				bw.newLine();
			}
			System.out.println(LocalQLearningUtils.GREEN + "Q-Table guardada exitosamente en: " + filename + LocalQLearningUtils.RESET);
		} catch (IOException e) {
			System.err.println("Error al guardar Q-Table CSV: " + e.getMessage());
		}
	}

	/**
	 * Carga una Q-Table desde un archivo CSV para continuar entrenamiento o
	 * ejecutar.
	 */
	public void loadQTableCSV() {
		String filename = "Knowledge/QTable_"+name+".csv";
		String line = "";
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			// Saltamos la cabecera
			br.readLine();

			int row = 0;
			while ((line = br.readLine()) != null && row < qTable.length) {
				String[] values = line.split(",");
				// values[0] es el estado, values[1..N] son las acciones

				// Validación básica de dimensiones
				if (values.length - 1 != qTable[row].length) {
					System.err.println("Advertencia: El CSV tiene diferente número de acciones que el agente.");
				}

				for (int col = 0; col < qTable[row].length; col++) {
					// +1 porque el índice 0 del CSV es el ID del estado
					if (col + 1 < values.length) {
						qTable[row][col] = Double.parseDouble(values[col + 1]);
					}
				}
				row++;
			}
			System.out.println(LocalQLearningUtils.GREEN + "Q-Table cargada exitosamente desde: " + filename + LocalQLearningUtils.RESET);
		} catch (IOException e) {
			System.err.println("No se pudo cargar la Q-Table: " + e.getMessage());
		} catch (NumberFormatException e) {
			System.err.println("Error de formato en el CSV: " + e.getMessage());
		}
	}

	  /**
     * Guarda la POLÍTICA mostrando TODAS las mejores acciones en caso de empate.
     */
    public void savePolicyText() {
		String filename = "Knowledge/Policy_"+name+".txt";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("=== POLÍTICA APRENDIDA (Q-LEARNING) ===\n");
            bw.write("-> State -> Best Action(s) [Q-Value]\n");
            bw.write("--------------------------------------\n");

            for (int i = 0; i < qTable.length; i++) {
                // Obtenemos la lista de todas las mejores acciones
                List<Integer> bestActions = getBestActionsList(i);
                
                // Obtenemos el valor Q (cualquiera de la lista sirve, todos son iguales al max)
                double maxQ = qTable[i][bestActions.get(0)];
                
                // Formateamos la lista de acciones ej: "{0, 2}"
                StringBuilder actionsStr = new StringBuilder("{");
                for (int k = 0; k < bestActions.size(); k++) {
                    actionsStr.append(bestActions.get(k));
                    if (k < bestActions.size() - 1) actionsStr.append(", ");
                }
                actionsStr.append("}");

                // Escribimos en el archivo
                bw.write(String.format("State %3d -> Actions: %-10s (Q: %.4f)\n", i, actionsStr.toString(), maxQ));
            }
            System.out.println(LocalQLearningUtils.GREEN + "Política guardada en: " + filename + LocalQLearningUtils.RESET);
        } catch (IOException e) {
            System.err.println("Error al guardar Policy TXT: " + e.getMessage());
        }
    }
    
    @Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		// 1. Cabecera y Parámetros
		sb.append("=== Q-Learning Agent: ").append((name != null) ? name : "Unknown").append(" ===\n");
		sb.append(String.format("Params  -> Alpha: %.2f | Gamma: %.2f | Epsilon: %.4f\n", alpha, gamma, epsilon));
		sb.append(String.format("Config  -> Decay: %.4f | MinEpsilon: %.4f\n", decayEpsilonFactor, minEpsilon));
		
		// 2. Dimensiones
		int numStates = qTable.length;
		int numActions = (numStates > 0) ? qTable[0].length : 0;
		sb.append(String.format("Q-Table -> %d States x %d Actions\n", numStates, numActions));
		sb.append("--------------------------------------------------\n");

		// 3. Vista Previa de la Tabla (Para no saturar la consola)
		// Solo mostramos los primeros 10 estados o menos si la tabla es pequeña
		int previewLimit = Math.min(numStates, 10);
		
		sb.append("Preview (First ").append(previewLimit).append(" states):\n");
		
		for (int i = 0; i < previewLimit; i++) {
			sb.append(String.format("State %-3d | ", i));
			
			// Buscamos la mejor acción para resaltarla visualmente (opcional pero útil)
			double maxVal = Double.NEGATIVE_INFINITY;
			int bestIdx = -1;
			for(int k=0; k<numActions; k++) {
				if(qTable[i][k] > maxVal) {
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
				
				if (j < numActions - 1) sb.append(", ");
			}
			sb.append("]\n");
		}

		if (numStates > previewLimit) {
			sb.append("... (").append(numStates - previewLimit).append(" states more hidden) ...\n");
		}
		
		return sb.toString();
	}
}
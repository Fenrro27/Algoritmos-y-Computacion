package QLearning;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase para cargar y ejecutar una política (Q-Table ya entrenada).
 * Utiliza IEnvironment para inicializar dimensiones correctamente.
 */
public class Politica {

    private double[][] qTable; // Tabla de valores Q
    private float[][] actionMap; // Traductor de índice a valores reales (ej: {accel, brake})
    private String name; // Nombre del agente/entorno para buscar ficheros por defecto
    private Random random;

    /**
     * Constructor basado en Environment.
     * Inicializa la matriz con el tamaño correcto para evitar NullPointerExceptions.
     * * @param env El entorno (para saber numStates, numActions y nombre).
     * @param actionMap Mapa de acciones reales (float[][]) que corresponde a los índices.
     */
    public Politica(IEnvironment env) {
        this.name = env.getName();
        this.actionMap = env.getActionMap();
        this.random = new Random();
        
        // INICIALIZACIÓN CRÍTICA: Reservar memoria basada en el entorno
        this.qTable = new double[env.getNumStates()][env.getNumActions()];
        
        // Inicializamos a un valor muy bajo por defecto para que al cargar la política,
        // las acciones no mencionadas no sean elegidas.
        for (int i = 0; i < qTable.length; i++) {
            for (int j = 0; j < qTable[i].length; j++) {
                qTable[i][j] = Double.NEGATIVE_INFINITY;
            }
        }
    }

   
    public void loadPolicyText(String customFilename) {
        String filename = (customFilename != null) ? customFilename : "Policy_" + name + ".txt";

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            System.out.println("Cargando política desde TXT: " + filename);
            
            int loadedStates = 0;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                // Verificamos que empiece por State
                if (!line.startsWith("State")) continue;

                try {
                    // === PARSEO ROBUSTO ===
                    
                    // 1. Extraer ID del Estado
                    // Buscamos la flecha "->"
                    int arrowIndex = line.indexOf("->");
                    if (arrowIndex == -1) continue; // Línea inválida
                    
                    // "State" tiene 5 letras. Tomamos desde el índice 5 hasta la flecha
                    String statePart = line.substring(5, arrowIndex).trim();
                    int state = Integer.parseInt(statePart);

                    // 2. Extraer Valor Q
                    int qStartIndex = line.indexOf("(Q:");
                    int qEndIndex = line.indexOf(")", qStartIndex);
                    
                    if (qStartIndex != -1 && qEndIndex != -1) {
                        String qPart = line.substring(qStartIndex + 3, qEndIndex).trim();
                        
                        // --- CORRECCIÓN CLAVE AQUÍ ---
                        // Reemplazamos la coma por punto para que Java entienda el decimal
                        qPart = qPart.replace(",", "."); 
                        
                        double qValue = Double.parseDouble(qPart);

                        // 3. Extraer Lista de Acciones "{x, y}"
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
                    // Imprimimos el error para saber qué línea falla exactamente
                    System.err.println("Error parseando línea: '" + line + "' -> " + e.getMessage());
                }
            }
            System.out.println("-> Política TXT cargada. Estados procesados: " + loadedStates);
            
        } catch (IOException e) {
            System.err.println("Error leyendo archivo TXT: " + e.getMessage());
        }
    }
    /**
     * Obtiene los valores reales de la mejor acción.
     * Si hay EMPATE entre varias acciones, elige una AL AZAR entre las mejores.
     */
    public float[] getAccionValues(int state) {
        // 1. Validaciones
        if (state < 0 || state >= qTable.length) return getDefaultAction();

        // 2. Buscar el valor máximo Q
        double maxQ = Double.NEGATIVE_INFINITY;
        for (double val : qTable[state]) {
            if (val > maxQ) maxQ = val;
        }

        // 3. Recopilar todas las acciones que tienen ese valor máximo (con tolerancia)
        List<Integer> bestActions = new ArrayList<>();
        double tolerance = 1e-5;

        for (int action = 0; action < qTable[state].length; action++) {
            if (Math.abs(qTable[state][action] - maxQ) < tolerance) {
                bestActions.add(action);
            }
        }

        // 4. Elegir aleatoriamente si hay empate (Exploración en empate)
        if (bestActions.isEmpty()) return getDefaultAction();
        
        int selectedActionIndex = bestActions.get(random.nextInt(bestActions.size()));

        // 5. Traducir índice a valores reales
        if (selectedActionIndex < actionMap.length) {
            return actionMap[selectedActionIndex];
        }
        return getDefaultAction();
    }
    
    private float[] getDefaultAction() {
        if (actionMap != null && actionMap.length > 0) return actionMap[0];
        return new float[]{0.0f}; // Freno de emergencia / Neutro
    }
}
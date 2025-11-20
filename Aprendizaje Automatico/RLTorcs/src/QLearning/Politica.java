package QLearning;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Clase genérica para cargar y ejecutar una política (Q-Table ya entrenada).
 * Sirve tanto para Volante (1 valor de salida) como para Aceleración (2 valores: Accel/Brake).
 */
public class Politica {

    // La Q-Table cargada del fichero: [Estado][Indice_Accion] -> Valor Q
    private double[][] qValues;
    
    // El mapa que traduce "Indice de Acción" a "Valores Reales de TORCS"
    // Ejemplo Volante: actionMap[0] = {-1.0f}
    // Ejemplo Accel:   actionMap[0] = {1.0f, 0.0f} (Acelerar a tope, sin freno)
    private float[][] actionMap; 

    /**
     * Constructor Genérico.
     * @param actionMap La matriz que define qué valores reales corresponden a cada índice de acción.
     */
    public Politica(float[][] actionMap) {
        this.actionMap = actionMap;
    }

    /**
     * Carga la política desde el archivo de TEXTO generado por savePolicyText.
     * * NOTA: Como el archivo de texto solo guarda las "mejores acciones", 
     * este método establecerá las acciones NO listadas a Double.NEGATIVE_INFINITY
     * para obligar al agente a seguir estrictamente la política cargada.
     */
    public void loadPolicyText(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            System.out.println("Cargando política desde TXT: " + filename);
            
            // Reiniciamos la tabla con valores muy bajos para asegurar que 
            // solo se seleccionen las acciones cargadas.
            for(int i=0; i<qValues.length; i++) {
                for(int j=0; j<qValues[i].length; j++) {
                	qValues[i][j] = Double.NEGATIVE_INFINITY;
                }
            }

            int loadedStates = 0;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                
                // Saltamos cabeceras y líneas decorativas
                if (!line.startsWith("State")) continue;

                try {
                    // Ejemplo de línea: 
                    // "State   0 -> Actions: {0, 1, 2}  (Q: 0.0000)"

                    // 1. Extraer ID del Estado
                    int arrowIndex = line.indexOf("->");
                    String statePart = line.substring(5, arrowIndex).trim();
                    int state = Integer.parseInt(statePart);

                    // 2. Extraer Valor Q
                    int qStartIndex = line.indexOf("(Q:");
                    int qEndIndex = line.indexOf(")", qStartIndex);
                    String qPart = line.substring(qStartIndex + 3, qEndIndex).trim();
                    double qValue = Double.parseDouble(qPart);

                    // 3. Extraer Lista de Acciones
                    int braceStart = line.indexOf("{");
                    int braceEnd = line.indexOf("}");
                    String actionsPart = line.substring(braceStart + 1, braceEnd);
                    
                    // Separar por comas (ej: "0, 2")
                    String[] actionTokens = actionsPart.split(",");

                    // 4. Actualizar la Q-Table
                    if (state < qValues.length) {
                        for (String token : actionTokens) {
                            token = token.trim();
                            if (!token.isEmpty()) {
                                int action = Integer.parseInt(token);
                                if (action < qValues[state].length) {
                                	qValues[state][action] = qValue;
                                }
                            }
                        }
                        loadedStates++;
                    }

                } catch (Exception e) {
                    System.err.println("Error parseando línea: " + line + " -> " + e.getMessage());
                }
            }
            System.out.println("-> Política cargada. Estados procesados: " + loadedStates);
            
        } catch (IOException e) {
            System.err.println("Error leyendo archivo: " + e.getMessage());
        }
    }

    /**
     * Obtiene los valores reales de la mejor acción para un estado dado.
     * * @param state El índice del estado discreto actual.
     * @return Un array de floats con los valores reales (ej: [steer] o [accel, brake]).
     * Devuelve los valores de la Acción 0 si el estado es inválido o la tabla está vacía.
     */
    public float[] getAccionValues(int state) {
        // 1. Validaciones de seguridad
        if (qValues == null || qValues.length == 0) {
            return getDefaultAction();
        }
        if (state < 0 || state >= qValues.length) {
            // Si el estado está fuera de rango (ej: estado nuevo no visto), devolvemos acción por defecto
            return getDefaultAction(); 
        }

        // 2. Encontrar el mejor índice (ArgMax - Explotación pura)
        double maxQ = -Double.MAX_VALUE; // Usar el mínimo valor posible de double
        int bestActionIndex = 0;
        
        double[] actions = qValues[state];
        for (int i = 0; i < actions.length; i++) {
            if (actions[i] > maxQ) {
                maxQ = actions[i];
                bestActionIndex = i;
            }
        }

        // 3. Traducir índice a valores reales usando el mapa
        if (bestActionIndex < actionMap.length) {
            return actionMap[bestActionIndex];
        } else {
            return getDefaultAction();
        }
    }
    
    /**
     * Devuelve la acción por defecto (índice 0) si algo falla.
     */
    private float[] getDefaultAction() {
        if (actionMap != null && actionMap.length > 0) {
            return actionMap[0];
        }
        return new float[]{0.0f}; // Fallback total
    }
    
    /**
     * (Opcional) Permite cambiar el mapa de acciones dinámicamente si fuera necesario.
     */
    public void setActionMap(float[][] actionMap) {
        this.actionMap = actionMap;
    }
}
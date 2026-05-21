import java.util.*;

class SumConstraint implements iConstraint {
    private final List<Node> nodes;
    private final int targetSum;
    private final String type; // "row" o "col"
    private final int index;

    SumConstraint(int targetSum, String type, int index) {
        this.nodes = new ArrayList<>();
        this.targetSum = targetSum;
        this.type = type;
        this.index = index;
    }

    /**
     * Refina los dominios de los nodos en esta restricción.
     * Devuelve true si algún dominio fue modificado (se eliminó un valor de la lista).
     */
    @Override
    public boolean pruneDomains() {
        boolean changed = false;
        
        while (true) {
            int currentChangedCount = 0;
            
            long low = 0; // suma mínima (solo nodos ya fijados como 1)
            long high = 0; // suma máxima (nodos fijados como 1 + nodos que aún tienen el 1 en su lista)
            
            for (Node n : nodes) {
                if (n.isSelected()) {
                    low += n.originalValue;
                    high += n.originalValue;
                } else if (!n.isFixed()) { // Si tiene [0, 1]
                    high += n.originalValue;
                } else if (n.isDot()) {
                    // Si es punto [0], no suma nada
                }
            }

            // Si el mínimo supera el objetivo o el máximo no llega, es imposible
            if (low > targetSum || high < targetSum) {
                return false; 
            }

            for (Node n : nodes) {
                if (!n.isFixed()) { // Si tiene las dos opciones [0, 1]
                    // Comprobar si seleccionar el número excede el objetivo
                    if (low + n.originalValue > targetSum) {
                        // Eliminar la opción 1 (Número) de la lista, dejar solo el 0 (Punto)
                        n.domain.remove(Integer.valueOf(1));
                        currentChangedCount++;
                        changed = true;
                    }
                    // Comprobar si NO seleccionar el número hace que el máximo caiga por debajo del objetivo
                    else if (high - n.originalValue < targetSum) {
                        // Eliminar la opción 0 (Punto) de la lista, dejar solo el 1 (Número)
                        n.domain.remove(Integer.valueOf(0));
                        currentChangedCount++;
                        changed = true;
                    }
                }
            }
            
            if (currentChangedCount == 0) break;
        }
        
        return changed;
    }
    @Override
    public boolean isSatisfied() {
        long sum = 0;
        for (Node n : nodes) {
            if (n.domain.isEmpty()) return false; // Lista vacía = error
            if (!n.isFixed()) return false; // No todas las celdas están decididas
            if (n.isSelected()) sum += n.originalValue;
        }
        return sum == targetSum;
    }
    
    @Override
    public boolean canBeSatisfied() {
        long low = 0;
        long high = 0;
        for (Node n : nodes) {
            if (n.isSelected()) {
                low += n.originalValue;
                high += n.originalValue;
            } else if (!n.isFixed()) {
                high += n.originalValue;
            }
        }
        return low <= targetSum && high >= targetSum;
    }

    @Override
    public List<Node> getNodes() {
      return nodes;
    }
}
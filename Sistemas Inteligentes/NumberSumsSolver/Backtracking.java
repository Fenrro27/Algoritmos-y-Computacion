import java.util.*;

public class Backtracking {
    private List<Node> allNodes;
    private List<iConstraint> constraints;

    public Backtracking(List<Node> allNodes, List<iConstraint> constraints) {
        this.allNodes = allNodes;
        this.constraints = constraints;
    }

    public boolean solve() {
        return search();
    }

    private boolean search() {
        // HEURÍSTICA MRV: Buscar el nodo con restricciones más críticas (dominio más pequeño > 1)
        // Buscar un nodo cuyo dominio tenga más de 1 valor (no decidido)
        Node bestNode = null;
        for (Node n : allNodes) {
            if (n.domain.size() > 1) {
                bestNode = n;
                break; // En binario [0,1], cualquier tamaño > 1 es de tamaño 2. El primero sirve.
            }
        }

        if (bestNode == null) {
            // Comprobar si todas las restricciones están satisfechas
            for (iConstraint sc : constraints) {
                if (!sc.isSatisfied()) return false;
            }
            return true;
        }

        // Hacer una copia del valor original de la lista del dominio para probar opciones
        List<Integer> originalOptions = new ArrayList<>(bestNode.domain);
        
        for (Integer option : originalOptions) {
            if (tryAssignment(bestNode, option)) return true;
        }

        return false;
    }

    private boolean tryAssignment(Node node, int value) {
        // Guardar el estado de TODOS los dominios para poder restaurarlos (Backtrack)
        List<List<Integer>> savedDomains = new ArrayList<>();
        for (Node n : allNodes) {
            savedDomains.add(new ArrayList<>(n.domain));
        }

        // Aplicar la asignación: la lista del nodo ahora solo tiene 1 valor
        node.domain.clear();
        node.domain.add(value);

        // Ejecutar AC3 para propagar la decisión
        AC3 propagator = new AC3(constraints);
        if (propagator.solve()) {
            if (search()) return true;
        }

        // BACKTRACK: Si falló, restauramos las listas de dominios originales
        for (int i = 0; i < allNodes.size(); i++) {
            allNodes.get(i).domain = savedDomains.get(i);
        }
        return false;
    }
}
package si2026.kevinjesusbandaalu.p04;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import core.game.StateObservation;
import ontology.Types.ACTIONS;

public class Motor49 {

    private Mundo49 mundo;

    public Motor49(Mundo49 mundo) {
        this.mundo = mundo;
    }

    public ACTIONS buscar(Mundo49 mundo) {
        StateObservation stateObs = mundo.stateObsActual;
        if (stateObs == null || stateObs.getGameWinner() == ontology.Types.WINNER.PLAYER_LOSES) return ACTIONS.ACTION_NIL;

        // Acciones disponibles para el avatar
        ArrayList<ACTIONS> actions = stateObs.getAvailableActions();
        
        // Estructuras de datos para A*
        PriorityQueue<Node> openList = new PriorityQueue<>();
        HashMap<String, Double> closedList = new HashMap<>();

        // Nodo raíz
        Node root = new Node(stateObs, null, null, 0, mundo.heuristica(stateObs));
        openList.add(root);

        Node bestNode = root;
        double minHeuristic = root.h;

        // Límite de procesamiento para evitar bloqueos (aproximadamente 35ms-40ms de búsqueda)
        // Como no tenemos el timer en la firma, usamos un límite de nodos conservador.
        int maxNodes = 800; 
        int nodesProcessed = 0;

        while (!openList.isEmpty() && nodesProcessed < maxNodes) {
            Node current = openList.poll();
            nodesProcessed++;

            // Clave de estado única: posición + orientación + tipo (Vampiro/Bala) + recursos
            String stateKey = current.state.getAvatarPosition().toString() + "_" + 
                             current.state.getAvatarOrientation().toString() + "_" +
                             current.state.getAvatarType() + "_" +
                             current.state.getAvatarResources().toString();

            if (closedList.containsKey(stateKey) && closedList.get(stateKey) <= current.g) {
                continue;
            }
            closedList.put(stateKey, current.g);

            // Si llegamos a la victoria, devolvemos el primer paso de ese camino
            if (current.state.getGameWinner() == ontology.Types.WINNER.PLAYER_WINS) {
                return getFirstAction(current);
            }

            // Si el estado es de pérdida, no expandimos
            if (current.state.getGameWinner() == ontology.Types.WINNER.PLAYER_LOSES) continue;

            // Mantener track del mejor nodo encontrado (por si no llegamos a la meta)
            if (current.h < minHeuristic) {
                minHeuristic = current.h;
                bestNode = current;
            }

            // Expandir hijos
            for (ACTIONS action : actions) {
                StateObservation nextState = current.state.copy();
                nextState.advance(action);

                double h = mundo.heuristica(nextState);
                Node child = new Node(nextState, current, action, current.g + 1, h);
                openList.add(child);
            }
        }

        // Si no se encontró la meta, devolvemos la acción que nos acerca más
        return getFirstAction(bestNode);
    }

    /**
     * Reconstruye el camino desde el nodo objetivo hacia atrás para encontrar la primera acción.
     */
    private ACTIONS getFirstAction(Node node) {
        if (node == null || node.parent == null) return ACTIONS.ACTION_NIL;
        Node current = node;
        while (current.parent != null && current.parent.parent != null) {
            current = current.parent;
        }
        return current.action;
    }

    /**
     * Clase interna para representar los nodos del árbol de búsqueda.
     */
    private class Node implements Comparable<Node> {
        StateObservation state;
        Node parent;
        ACTIONS action;
        double g, h;

        Node(StateObservation state, Node parent, ACTIONS action, double g, double h) {
            this.state = state;
            this.parent = parent;
            this.action = action;
            this.g = g;
            this.h = h;
        }

        double f() { return g + h; }

        @Override
        public int compareTo(Node o) {
            // A* prioriza el menor valor de f = g + h
            return Double.compare(this.f(), o.f());
        }
    }
}

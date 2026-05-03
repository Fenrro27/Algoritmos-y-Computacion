package si2026.kevinjesusbandaalu.p04;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import core.game.StateObservation;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;

public class Motor49 {

    private Mundo49 mundo;

    public Motor49(Mundo49 mundo) {
        this.mundo = mundo;
    }

    public ACTIONS buscar(Mundo49 mundo, ElapsedCpuTimer timer) {
        StateObservation stateObs = mundo.stateObsActual;
        if (stateObs == null || stateObs.getGameWinner() == ontology.Types.WINNER.PLAYER_LOSES)
            return ACTIONS.ACTION_NIL;

        // Acciones disponibles para el avatar
        ArrayList<ACTIONS> actions = stateObs.getAvailableActions();
        // System.out.println("Acciones disponibles: " + actions);

        // Estructuras de datos para A*
        PriorityQueue<Node> openList = new PriorityQueue<>();
        HashMap<String, Double> closedList = new HashMap<>();

        // Nodo raíz
        Node root = new Node(stateObs, null, null, 0, mundo.heuristica(stateObs));
        openList.add(root);

        Node bestNode = root;
        double minHeuristic = root.h;

        // Búsqueda guiada por tiempo restante (dejamos 20ms de margen de seguridad y
        // límite de nodos)
        int nodesProcessed = 0;
        int MAX_NODES = 1500; // Aumentado para que el A* tenga visión profunda y encuentre el siguiente
                              // nenúfar

        while (!openList.isEmpty() && timer.remainingTimeMillis() > 20 && nodesProcessed < MAX_NODES) {
            Node current = openList.poll();
            nodesProcessed++;

            // Clave de estado única: posición + orientación + tipo (Vampiro/Bala) + tick
            tools.Vector2d pos = current.state.getAvatarPosition();
            tools.Vector2d ori = current.state.getAvatarOrientation();
            String stateKey = pos.x + "_" + pos.y + "_" +
                    ori.x + "_" + ori.y + "_" +
                    current.state.getAvatarType() + "_" +
                    current.state.getGameTick();

            if (closedList.containsKey(stateKey) && closedList.get(stateKey) <= current.g) {
                continue;
            }
            closedList.put(stateKey, current.g);

            // Si llegamos a la victoria, devolvemos el primer paso de ese camino
            if (current.state.getGameWinner() == ontology.Types.WINNER.PLAYER_WINS) {
                return getFirstAction(current);
            }

            // Si el estado es de pérdida, no expandimos
            if (current.state.getGameWinner() == ontology.Types.WINNER.PLAYER_LOSES ||
                    (current.state.isGameOver()
                            && current.state.getGameWinner() != ontology.Types.WINNER.PLAYER_WINS)) {
                continue;
            }

            // Mantener track del mejor nodo encontrado (por si no llegamos a la meta)
            if (current.h < minHeuristic) {
                minHeuristic = current.h;
                bestNode = current;
            }

            // Comprobar si estamos sobre un nenúfar revisando el estado actual simulado
            int celdaX = (int) (pos.x / mundo.Bloque);
            int celdaY = (int) (pos.y / mundo.Bloque);
            boolean enNenufar = false;

            if (celdaX >= 0 && celdaX < mundo.columnas && celdaY >= 0 && celdaY < mundo.filas) {
                ArrayList<core.game.Observation> obsActual = current.state.getObservationGrid()[celdaX][celdaY];
                if (obsActual != null) {
                    for (core.game.Observation o : obsActual) {
                        if (o.itype == 10 || o.itype == 11) {
                            enNenufar = true;
                            break;
                        }
                    }
                }
            }

            StateObservation stateNIL = null;
            if (enNenufar) {
                stateNIL = current.state.copy();
                stateNIL.advance(ACTIONS.ACTION_NIL);
            }

            // Expandir hijos
            for (ACTIONS action : actions) {
                StateObservation nextState;
                if (enNenufar && action == ACTIONS.ACTION_NIL) {
                    nextState = stateNIL; // Reutilizar estado simulado para NADA
                } else {
                    nextState = current.state.copy();
                    nextState.advance(action);
                }

                if (enNenufar && action != ACTIONS.ACTION_NIL) {
                    // "solo debe realizar acciones si va a salirse de este"
                    if (nextState.getAvatarPosition().equals(stateNIL.getAvatarPosition())) {
                        continue;
                    }
                }

                double h = mundo.heuristica(nextState);
                Node child = new Node(nextState, current, action, current.g + 1, h);
                openList.add(child);
            }
        }
        ACTIONS accionElegida = getFirstAction(bestNode);

        // --- SYSO AÑADIDO PARA DEBUG ---
        int rootX = (int) (stateObs.getAvatarPosition().x / mundo.Bloque);
        int rootY = (int) (stateObs.getAvatarPosition().y / mundo.Bloque);
        boolean estoyEnNenufar = false;
        if (rootX >= 0 && rootX < mundo.columnas && rootY >= 0 && rootY < mundo.filas) {
            ArrayList<core.game.Observation> obsRoot = stateObs.getObservationGrid()[rootX][rootY];
            if (obsRoot != null) {
                for (core.game.Observation o : obsRoot) {
                    if (o.itype == 10 || o.itype == 11) {
                        estoyEnNenufar = true;
                        break;
                    }
                }
            }
        }
        if (estoyEnNenufar) {
            System.out.println("\n=== [DEBUG] ESTOY EN NENUFAR ===");
        }
        // -------------------------------

        System.out.println("Acción elegida hacia el siguiente nodo: " + accionElegida + " | Nodos procesados: "
                + nodesProcessed + " | Heurística: " + minHeuristic);
        return accionElegida;
    }

    /**
     * Reconstruye el camino desde el nodo objetivo hacia atrás para encontrar la
     * primera acción.
     */
    private ACTIONS getFirstAction(Node node) {
        if (node == null || node.parent == null)
            return ACTIONS.ACTION_NIL;
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

        double f() {
            return g + h;
        }

        @Override
        public int compareTo(Node o) {
            // A* prioriza el menor valor de f = g + h
            int cmp = Double.compare(this.f(), o.f());
            if (cmp == 0) {
                // En caso de empate, siempre es preferible no hacer nada
                if (this.action == ACTIONS.ACTION_NIL && o.action != ACTIONS.ACTION_NIL)
                    return -1;
                if (this.action != ACTIONS.ACTION_NIL && o.action == ACTIONS.ACTION_NIL)
                    return 1;
            }
            return cmp;
        }
    }
}

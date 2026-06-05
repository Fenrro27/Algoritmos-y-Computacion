package planning.busqueda;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import planning.Action;
import planning.Problema_Plan;
import planning.State;

/**
 * Planificador STRIPS hacia adelante con búsqueda en anchura (BFS).
 *
 * Garantiza un plan óptimo en número de acciones (si existe).
 * Detecta estados repetidos para no expandirlos dos veces.
 */
public final class Planificador {

    private Planificador() {}

    public static final class BfsStep {
        public final int step;
        public final String state;
        public final String action;
        public final String parentState;

        public BfsStep(int step, String state, String action, String parentState) {
            this.step = step;
            this.state = state;
            this.action = action;
            this.parentState = parentState;
        }
    }

    /**
     * Resultado de la planificación: plan encontrado, trayectoria de estados
     * y estadísticas básicas. Si {@link #exito} es false, {@link #plan}
     * y {@link #trayectoria} están vacíos.
     */
    public static final class Resultado {
        public final boolean            exito;
        public final List<List<Action>> planes;
        public final List<List<State>>  trayectorias;
        public final List<Action>       plan;
        public final List<State>        trayectoria;
        public final int                nodosExplorados;
        public final int                nodosGenerados;
        public final List<BfsStep>      trace;

        public Resultado(boolean exito,
                          List<List<Action>> planes,
                          List<List<State>> trayectorias,
                          int nodosExplorados,
                          int nodosGenerados,
                          List<BfsStep> trace) {
            this.exito           = exito;
            this.planes          = Collections.unmodifiableList(planes);
            this.trayectorias    = Collections.unmodifiableList(trayectorias);
            this.plan            = planes.isEmpty() ? Collections.<Action>emptyList() : Collections.unmodifiableList(planes.get(0));
            this.trayectoria     = trayectorias.isEmpty() ? Collections.<State>emptyList() : Collections.unmodifiableList(trayectorias.get(0));
            this.nodosExplorados = nodosExplorados;
            this.nodosGenerados  = nodosGenerados;
            this.trace           = Collections.unmodifiableList(trace);
        }
    }

    /**
     * Resuelve el problema mediante BFS hacia adelante, encontrando todos los planes óptimos.
     *
     * @param problema problema de planificación
     * @return resultado con todos los planes óptimos
     */
    public static Resultado planificar(Problema_Plan problema) {
        State inicial = problema.getEstadoInicial();
        List<BfsStep> trace = new ArrayList<BfsStep>();

        List<List<Action>> planes = new ArrayList<List<Action>>();
        List<List<State>> trayectorias = new ArrayList<List<State>>();

        // Caso trivial: el estado inicial ya cumple el objetivo
        if (problema.esObjetivo(inicial)) {
            List<State> tray = new ArrayList<State>();
            tray.add(inicial);
            trace.add(new BfsStep(1, inicial.toString(), "None", "None"));
            planes.add(new ArrayList<Action>());
            trayectorias.add(tray);
            return new Resultado(true, planes, trayectorias, 0, 1, trace);
        }

        Deque<Nodo>  frontera  = new ArrayDeque<Nodo>();
        Set<State>   visitados = new HashSet<State>();
        frontera.add(new Nodo(inicial, null, null));
        visitados.add(inicial);

        int explorados = 0;
        int generados  = 1;
        int solutionDepth = -1;

        while (!frontera.isEmpty()) {
            Nodo n = frontera.pollFirst();
            int currentDepth = obtenerProfundidad(n);
            if (solutionDepth != -1 && currentDepth >= solutionDepth) {
                break; // Ya encontramos todas las soluciones óptimas al nivel más corto
            }

            explorados++;
            trace.add(new BfsStep(explorados, n.estado.toString(), n.accion != null ? n.accion.getName() : "None", n.padre != null ? n.padre.estado.toString() : "None"));

            for (Action a : problema.accionesAplicables(n.estado)) {
                State sucesor = n.estado.apply(a);
                if (!visitados.add(sucesor)) continue; // ya visto

                Nodo hijo = new Nodo(sucesor, n, a);
                generados++;

                int depthOfHijo = currentDepth + 1;
                if (problema.esObjetivo(sucesor)) {
                    planes.add(reconstruirPlan(hijo));
                    trayectorias.add(reconstruirTrayectoria(hijo));
                    solutionDepth = depthOfHijo;
                    trace.add(new BfsStep(explorados + 1, sucesor.toString(), a.getName(), n.estado.toString()));
                }

                if (solutionDepth == -1) {
                    frontera.addLast(hijo);
                }
            }
        }

        boolean exito = !planes.isEmpty();
        List<List<Action>> filteredPlanes = new ArrayList<List<Action>>();
        List<List<State>> filteredTrayectorias = new ArrayList<List<State>>();
        if (exito) {
            int minLen = Integer.MAX_VALUE;
            for (List<Action> p : planes) {
                if (p.size() < minLen) {
                    minLen = p.size();
                }
            }
            for (int i = 0; i < planes.size(); i++) {
                if (planes.get(i).size() == minLen) {
                    filteredPlanes.add(planes.get(i));
                    filteredTrayectorias.add(trayectorias.get(i));
                }
            }
        }
        return new Resultado(exito,
                             filteredPlanes,
                             filteredTrayectorias,
                             explorados,
                             generados,
                             trace);
    }

    private static int obtenerProfundidad(Nodo n) {
        int d = 0;
        for (Nodo temp = n; temp.padre != null; temp = temp.padre) {
            d++;
        }
        return d;
    }

    // -------------------------------------------------------------------------
    // Reconstrucción del plan a partir de los punteros padre
    // -------------------------------------------------------------------------

    private static List<Action> reconstruirPlan(Nodo objetivo) {
        List<Action> plan = new ArrayList<Action>();
        for (Nodo n = objetivo; n.accion != null; n = n.padre) {
            plan.add(n.accion);
        }
        Collections.reverse(plan);
        return plan;
    }

    private static List<State> reconstruirTrayectoria(Nodo objetivo) {
        List<State> tray = new ArrayList<State>();
        for (Nodo n = objetivo; n != null; n = n.padre) {
            tray.add(n.estado);
        }
        Collections.reverse(tray);
        return tray;
    }

    // -------------------------------------------------------------------------
    // Nodo del árbol de búsqueda (encadenado por puntero al padre)
    // -------------------------------------------------------------------------

    private static final class Nodo {
        final State  estado;
        final Nodo   padre;
        final Action accion; // acción que llevó del padre a este estado

        Nodo(State estado, Nodo padre, Action accion) {
            this.estado = estado;
            this.padre  = padre;
            this.accion = accion;
        }
    }
}

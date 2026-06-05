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

    /**
     * Resultado de la planificación: plan encontrado, trayectoria de estados
     * y estadísticas básicas. Si {@link #exito} es false, {@link #plan}
     * y {@link #trayectoria} están vacíos.
     */
    public static final class Resultado {
        public final boolean      exito;
        public final List<Action> plan;
        public final List<State>  trayectoria;
        public final int          nodosExplorados;
        public final int          nodosGenerados;

        private Resultado(boolean exito,
                          List<Action> plan,
                          List<State> trayectoria,
                          int nodosExplorados,
                          int nodosGenerados) {
            this.exito           = exito;
            this.plan            = Collections.unmodifiableList(plan);
            this.trayectoria     = Collections.unmodifiableList(trayectoria);
            this.nodosExplorados = nodosExplorados;
            this.nodosGenerados  = nodosGenerados;
        }
    }

    /**
     * Resuelve el problema mediante BFS hacia adelante.
     *
     * @param problema problema de planificación
     * @return resultado con el plan (puede estar vacío si Ei ya cumple Ef)
     */
    public static Resultado planificar(Problema_Plan problema) {
        State inicial = problema.getEstadoInicial();

        // Caso trivial: el estado inicial ya cumple el objetivo
        if (problema.esObjetivo(inicial)) {
            List<State> tray = new ArrayList<State>();
            tray.add(inicial);
            return new Resultado(true, new ArrayList<Action>(), tray, 0, 1);
        }

        Deque<Nodo>  frontera  = new ArrayDeque<Nodo>();
        Set<State>   visitados = new HashSet<State>();
        frontera.add(new Nodo(inicial, null, null));
        visitados.add(inicial);

        int explorados = 0;
        int generados  = 1;

        while (!frontera.isEmpty()) {
            Nodo n = frontera.pollFirst();
            explorados++;

            for (Action a : problema.accionesAplicables(n.estado)) {
                State sucesor = n.estado.apply(a);
                if (!visitados.add(sucesor)) continue; // ya visto

                Nodo hijo = new Nodo(sucesor, n, a);
                generados++;

                if (problema.esObjetivo(sucesor)) {
                    return new Resultado(true,
                                         reconstruirPlan(hijo),
                                         reconstruirTrayectoria(hijo),
                                         explorados,
                                         generados);
                }
                frontera.addLast(hijo);
            }
        }

        return new Resultado(false,
                             new ArrayList<Action>(),
                             new ArrayList<State>(),
                             explorados,
                             generados);
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

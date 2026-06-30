package planning.strips;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import planning.Action;
import planning.Literal;
import planning.Problema_Plan;
import planning.State;

/**
 * Planificador STRIPS regresivo basado en una pila de objetivos con backtracking.
 *
 * Busca hacia atrás desde el estado final, descomponiendo objetivos
 * y apilando las precondiciones de las acciones que los logran.
 * Cuenta con detección de ciclos y límites configurables de pasos y nodos.
 */
public final class PlanificadorStrips {

    private PlanificadorStrips() {}

    public static final class StripsStep {
        public final int step;
        public final String state;
        public final List<String> stack;

        public StripsStep(int step, String state, List<String> stack) {
            this.step = step;
            this.state = state;
            this.stack = stack;
        }
    }

    /**
     * Resultado de la planificación STRIPS.
     */
    public static final class Resultado {
        public final boolean            exito;
        public final List<List<Action>> planes;
        public final List<List<State>>  trayectorias;
        public final List<Action>       plan;
        public final List<State>        trayectoria;
        public final int                nodosExplorados;
        public final int                nodosGenerados;
        public final boolean            limiteAlcanzado;
        public final String             mensaje;
        public final List<StripsStep>   trace;

        public Resultado(boolean exito,
                         List<List<Action>> planes,
                         List<List<State>> trayectorias,
                         int nodosExplorados,
                         int nodosGenerados,
                         boolean limiteAlcanzado,
                         String mensaje,
                         List<StripsStep> trace) {
            this.exito           = exito;
            this.planes          = Collections.unmodifiableList(planes);
            this.trayectorias    = Collections.unmodifiableList(trayectorias);
            this.plan            = planes.isEmpty() ? Collections.<Action>emptyList() : Collections.unmodifiableList(planes.get(0));
            this.trayectoria     = trayectorias.isEmpty() ? Collections.<State>emptyList() : Collections.unmodifiableList(trayectorias.get(0));
            this.nodosExplorados = nodosExplorados;
            this.nodosGenerados  = nodosGenerados;
            this.limiteAlcanzado = limiteAlcanzado;
            this.mensaje         = mensaje;
            this.trace           = Collections.unmodifiableList(trace);
        }

        @Override
        public String toString() {
            if (exito) {
                return "Resultado[EXITO, planes=" + planes.size() + ", nodosExplorados=" + nodosExplorados + "]";
            } else if (limiteAlcanzado) {
                return "Resultado[LIMITE_ALCANZADO: " + mensaje + ", nodosExplorados=" + nodosExplorados + "]";
            } else {
                return "Resultado[FALLO, nodosExplorados=" + nodosExplorados + "]";
            }
        }
    }

    /**
     * Planifica con los límites por defecto (15 acciones, 5000 nodos explorados).
     */
    public static Resultado planificar(Problema_Plan problema) {
        return planificar(problema, 15, 5000);
    }

    /**
     * Planifica especificando los límites máximos de acciones y nodos explorados.
     */
    public static Resultado planificar(Problema_Plan problema, int maxAcciones, int maxNodos) {
        Deque<Object> stack = new ArrayDeque<Object>();
        // Estado objetivo inicial como meta compuesta
        stack.addFirst(problema.getEstadoFinal());

        List<Action> plan = new ArrayList<Action>();
        Set<String> visitados = new HashSet<String>();
        List<StripsStep> trace = new ArrayList<StripsStep>();
        List<List<Action>> planes = new ArrayList<List<Action>>();
        List<List<State>> trayectorias = new ArrayList<List<State>>();

        int[] stats = new int[2]; // stats[0] = explorados, stats[1] = generados
        boolean[] limiteAlcanzado = new boolean[1];

        stats[1] = 1; // Primer nodo (estado inicial del stack)
        buscar(stack, problema.getEstadoInicial(), plan, visitados, stats, maxAcciones, maxNodos, problema, limiteAlcanzado, trace, planes, trayectorias);

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
            return new Resultado(true, filteredPlanes, filteredTrayectorias, stats[0], stats[1], false, "Planes encontrados con éxito.", trace);
        } else {
            String msg = "No se encontró solución.";
            if (limiteAlcanzado[0]) {
                msg = "Límite configurado alcanzado (Máx acciones: " + maxAcciones + ", Máx nodos: " + maxNodos + ").";
                System.err.println("ADVERTENCIA: " + msg);
            }
            return new Resultado(false, filteredPlanes, filteredTrayectorias, stats[0], stats[1], limiteAlcanzado[0], msg, trace);
        }
    }

    private static boolean buscar(Deque<Object> stack,
                                  State estado,
                                  List<Action> plan,
                                  Set<String> visitados,
                                  int[] stats,
                                  int maxAcciones,
                                  int maxNodos,
                                  Problema_Plan problema,
                                  boolean[] limiteAlcanzado,
                                  List<StripsStep> trace,
                                  List<List<Action>> planes,
                                  List<List<State>> trayectorias) {
        if (stats[0] >= maxNodos) {
            limiteAlcanzado[0] = true;
            return false;
        }
        if (plan.size() > maxAcciones) {
            limiteAlcanzado[0] = true;
            return false;
        }
        if (planes.size() >= 50) {
            return false; // Criterio de parada por número suficiente de planes
        }

        List<String> stackList = new ArrayList<String>();
        for (Object obj : stack) {
            if (obj instanceof State) {
                stackList.add("State" + ((State) obj).getLiterals().toString());
            } else if (obj instanceof Literal) {
                stackList.add(obj.toString());
            } else if (obj instanceof Action) {
                stackList.add(((Action) obj).getName());
            }
        }
        trace.add(new StripsStep(stats[0], estado.toString(), stackList));

        stats[0]++; // Nodo explorado

        if (stack.isEmpty()) {
            planes.add(new ArrayList<Action>(plan));

            List<State> trayectoria = new ArrayList<State>();
            State temp = problema.getEstadoInicial();
            trayectoria.add(temp);
            for (Action a : plan) {
                temp = temp.apply(a);
                trayectoria.add(temp);
            }
            trayectorias.add(trayectoria);

            return false; // Forzar backtracking para seguir buscando más soluciones
        }

        // Firma para detección de ciclos
        String firma = getSignature(estado, stack);
        if (visitados.contains(firma)) {
            return false; // Ciclo detectado en esta rama
        }
        visitados.add(firma);

        Object item = stack.peekFirst();

        if (item instanceof State) {
            State metaCompuesta = (State) item;
            if (estado.satisfies(metaCompuesta.getLiterals())) {
                // Meta compuesta satisfecha
                stack.removeFirst();
                if (buscar(stack, estado, plan, visitados, stats, maxAcciones, maxNodos, problema, limiteAlcanzado, trace, planes, trayectorias)) {
                    return true;
                }
                // Backtrack
                stack.addFirst(metaCompuesta);
            } else {
                // No satisfecha, descomponer metas individuales
                List<Literal> pendientes = estado.unsatisfied(metaCompuesta.getLiterals());
                List<List<Literal>> permutaciones = obtenerPermutaciones(pendientes);
                for (List<Literal> perm : permutaciones) {
                    stack.removeFirst(); // sacamos meta compuesta
                    stack.addFirst(metaCompuesta); // la ponemos de vuelta abajo
                    for (int i = perm.size() - 1; i >= 0; i--) {
                        stack.addFirst(perm.get(i));
                    }

                    stats[1]++;
                    if (buscar(stack, estado, plan, visitados, stats, maxAcciones, maxNodos, problema, limiteAlcanzado, trace, planes, trayectorias)) {
                        return true;
                    }

                    // Backtrack pila
                    for (int i = 0; i < perm.size(); i++) {
                        stack.removeFirst();
                    }
                    stack.removeFirst(); // metaCompuesta
                    stack.addFirst(metaCompuesta);
                }
            }
        } else if (item instanceof Literal) {
            Literal literal = (Literal) item;
            if (estado.contains(literal)) {
                // Literal ya satisfecho
                stack.removeFirst();
                if (buscar(stack, estado, plan, visitados, stats, maxAcciones, maxNodos, problema, limiteAlcanzado, trace, planes, trayectorias)) {
                    return true;
                }
                // Backtrack
                stack.addFirst(literal);
            } else {
                // Intentar satisfacer con alguna acción
                List<Action> candidatos = problema.accionesQueLogran(literal);
                if (!candidatos.isEmpty()) {
                    stack.removeFirst(); // sacar el literal
                    for (Action a : candidatos) {
                        stack.addFirst(a);
                        State preCompuestas = new State(a.getPreconditions());
                        stack.addFirst(preCompuestas);
                        List<Literal> precondiciones = a.getPreconditions();
                        for (int i = precondiciones.size() - 1; i >= 0; i--) {
                            stack.addFirst(precondiciones.get(i));
                        }

                        stats[1]++;
                        if (buscar(stack, estado, plan, visitados, stats, maxAcciones, maxNodos, problema, limiteAlcanzado, trace, planes, trayectorias)) {
                            return true;
                        }

                        // Backtrack pila
                        for (int i = 0; i < precondiciones.size(); i++) {
                            stack.removeFirst();
                        }
                        stack.removeFirst(); // preCompuestas
                        stack.removeFirst(); // a
                    }
                    stack.addFirst(literal); // restaurar
                }
            }
        } else if (item instanceof Action) {
            Action accion = (Action) item;
            stack.removeFirst();
            State nuevoEstado = estado.apply(accion);
            plan.add(accion);

            if (buscar(stack, nuevoEstado, plan, visitados, stats, maxAcciones, maxNodos, problema, limiteAlcanzado, trace, planes, trayectorias)) {
                return true;
            }

            // Backtrack
            plan.remove(plan.size() - 1);
            stack.addFirst(accion);
        }

        visitados.remove(firma);
        return false;
    }

    private static String getSignature(State state, Deque<Object> stack) {
        List<String> literals = new ArrayList<String>();
        for (Literal l : state.getLiterals()) {
            literals.add(l.toString());
        }
        Collections.sort(literals);

        StringBuilder sb = new StringBuilder();
        sb.append(literals.toString());
        sb.append(" | ");
        for (Object obj : stack) {
            if (obj instanceof State) {
                sb.append("State").append(((State) obj).getLiterals().toString());
            } else if (obj instanceof Literal) {
                sb.append("Lit(").append(obj.toString()).append(")");
            } else if (obj instanceof Action) {
                sb.append("Act(").append(((Action) obj).getName()).append(")");
            }
            sb.append(";");
        }
        return sb.toString();
    }

    private static List<List<Literal>> obtenerPermutaciones(List<Literal> original) {
        List<List<Literal>> res = new ArrayList<List<Literal>>();
        permutar(new ArrayList<Literal>(original), 0, res);
        return res;
    }

    private static void permutar(List<Literal> arr, int k, List<List<Literal>> res) {
        if (k == arr.size()) {
            res.add(new ArrayList<Literal>(arr));
            return;
        }
        for (int i = k; i < arr.size(); i++) {
            Collections.swap(arr, i, k);
            permutar(arr, k + 1, res);
            Collections.swap(arr, i, k);
        }
    }
}

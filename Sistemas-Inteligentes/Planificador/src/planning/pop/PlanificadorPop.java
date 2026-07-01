package planning.pop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import planning.Action;
import planning.Literal;
import planning.Problema_Plan;
import planning.State;
import planning.ConsoleColorFormatter;

/**
 * Planificador de Orden Parcial (Partial Order Planning - POP).
 *
 * Trabaja en el espacio de planes en lugar del espacio de estados.
 * Resuelve subobjetivos añadiendo pasos, estableciendo enlaces causales
 * y resolviendo conflictos/amenazas mediante promoción o degradación.
 * Cuenta con un límite de pasos configurable y verificación de consistencia
 * usando ordenación topológica.
 */
public final class PlanificadorPop {

    private PlanificadorPop() {}

    // -------------------------------------------------------------------------
    // Estructuras de datos para el plan
    // -------------------------------------------------------------------------

    public static class Step {
        public final int id;
        public final Action action;

        public Step(int id, Action action) {
            this.id = id;
            this.action = action;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Step)) return false;
            Step other = (Step) obj;
            return id == other.id;
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public String toString() {
            return id + ":" + action.getName();
        }
    }

    public static class OrderConstraint {
        public final Step before;
        public final Step after;

        public OrderConstraint(Step before, Step after) {
            this.before = before;
            this.after = after;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof OrderConstraint)) return false;
            OrderConstraint other = (OrderConstraint) obj;
            return before.equals(other.before) && after.equals(other.after);
        }

        @Override
        public int hashCode() {
            return 31 * before.hashCode() + after.hashCode();
        }

        @Override
        public String toString() {
            return before + " < " + after;
        }
    }

    public static class CausalLink {
        public final Step source;
        public final Literal literal;
        public final Step target;

        public CausalLink(Step source, Literal literal, Step target) {
            this.source = source;
            this.literal = literal;
            this.target = target;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof CausalLink)) return false;
            CausalLink other = (CausalLink) obj;
            return source.equals(other.source) && literal.equals(other.literal) && target.equals(other.target);
        }

        @Override
        public int hashCode() {
            int result = source.hashCode();
            result = 31 * result + literal.hashCode();
            result = 31 * result + target.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return source + " --[" + literal + "]--> " + target;
        }
    }

    public static class OpenPrecondition {
        public final Step step;
        public final Literal literal;

        public OpenPrecondition(Step step, Literal literal) {
            this.step = step;
            this.literal = literal;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof OpenPrecondition)) return false;
            OpenPrecondition other = (OpenPrecondition) obj;
            return step.equals(other.step) && literal.equals(other.literal);
        }

        @Override
        public int hashCode() {
            return 31 * step.hashCode() + literal.hashCode();
        }

        @Override
        public String toString() {
            return "(" + step + ", " + literal + ")";
        }
    }

    private static class Threat {
        final CausalLink link;
        final Step step;

        Threat(CausalLink link, Step step) {
            this.link = link;
            this.step = step;
        }
    }

    public static final class PopStep {
        public final int step;
        public final List<String> steps;
        public final List<String> constraints;
        public final List<String> links;
        public final List<String> open;

        public PopStep(int step, List<String> steps, List<String> constraints, List<String> links, List<String> open) {
            this.step = step;
            this.steps = steps;
            this.constraints = constraints;
            this.links = links;
            this.open = open;
        }
    }

    public static class PopSolution {
        public final Set<Step> steps;
        public final Set<OrderConstraint> constraints;
        public final Set<CausalLink> links;

        public PopSolution(Set<Step> steps, Set<OrderConstraint> constraints, Set<CausalLink> links) {
            this.steps = new LinkedHashSet<Step>(steps);
            this.constraints = new LinkedHashSet<OrderConstraint>(constraints);
            this.links = new LinkedHashSet<CausalLink>(links);
        }
    }

    // -------------------------------------------------------------------------
    // Clase de Resultado
    // -------------------------------------------------------------------------

    public static final class Resultado {
        public final boolean              exito;
        public final List<List<Action>>   planes;
        public final List<List<State>>    trayectorias;
        public final List<Action>         plan;
        public final List<State>          trayectoria;
        public final int                  nodosExplorados;
        public final int                  nodosGenerados;
        public final boolean              limiteAlcanzado;
        public final String               mensaje;
        public final Set<OrderConstraint> restricciones;
        public final Set<CausalLink>       enlacesCausales;
        public final Set<Step>             pasos;
        public final List<PopStep>        trace;
        public final List<String>         conflictResolution;

        public Resultado(boolean exito,
                         List<List<Action>> planes,
                         List<List<State>> trayectorias,
                         int nodosExplorados,
                         int nodosGenerados,
                         boolean limiteAlcanzado,
                         String mensaje,
                         Set<Step> pasos,
                         Set<OrderConstraint> restricciones,
                         Set<CausalLink> enlacesCausales,
                         List<PopStep> trace,
                         List<String> conflictResolution) {
            this.exito           = exito;
            this.planes          = Collections.unmodifiableList(planes);
            this.trayectorias    = Collections.unmodifiableList(trayectorias);
            this.plan            = planes.isEmpty() ? Collections.<Action>emptyList() : Collections.unmodifiableList(planes.get(0));
            this.trayectoria     = trayectorias.isEmpty() ? Collections.<State>emptyList() : Collections.unmodifiableList(trayectorias.get(0));
            this.nodosExplorados = nodosExplorados;
            this.nodosGenerados  = nodosGenerados;
            this.limiteAlcanzado = limiteAlcanzado;
            this.mensaje         = mensaje;
            this.pasos           = pasos != null ? Collections.unmodifiableSet(pasos) : Collections.<Step>emptySet();
            this.restricciones   = restricciones != null ? Collections.unmodifiableSet(restricciones) : Collections.<OrderConstraint>emptySet();
            this.enlacesCausales = enlacesCausales != null ? Collections.unmodifiableSet(enlacesCausales) : Collections.<CausalLink>emptySet();
            this.trace           = Collections.unmodifiableList(trace);
            this.conflictResolution = Collections.unmodifiableList(conflictResolution);
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

    // -------------------------------------------------------------------------
    // API pública
    // -------------------------------------------------------------------------

    /**
     * Planifica con los límites por defecto (15 acciones máximo).
     */
    public static Resultado planificar(Problema_Plan problema) {
        return planificar(problema, 15);
    }

    /**
     * Planifica especificando el límite máximo de pasos/acciones permitidos en el plan.
     */
    @SuppressWarnings("unchecked")
    public static Resultado planificar(Problema_Plan problema, int maxPasos) {
        // Pasos Start y Finish
        Step startStep = new Step(0, new Action("Start",
                new ArrayList<Literal>(),
                problema.getEstadoInicial().getLiterals(),
                new ArrayList<Literal>()));

        Step finishStep = new Step(1, new Action("Finish",
                problema.getEstadoFinal().getLiterals(),
                new ArrayList<Literal>(),
                new ArrayList<Literal>()));

        Set<Step> pasosIniciales = new LinkedHashSet<Step>();
        pasosIniciales.add(startStep);
        pasosIniciales.add(finishStep);

        Set<OrderConstraint> constraintsIniciales = new LinkedHashSet<OrderConstraint>();
        constraintsIniciales.add(new OrderConstraint(startStep, finishStep));

        Set<CausalLink> linksIniciales = new LinkedHashSet<CausalLink>();

        List<OpenPrecondition> openInicial = new ArrayList<OpenPrecondition>();
        for (Literal g : problema.getEstadoFinal().getLiterals()) {
            openInicial.add(new OpenPrecondition(finishStep, g));
        }

        int[] stats = new int[2]; // stats[0] = explorados, stats[1] = generados
        boolean[] limiteAlcanzado = new boolean[1];
        List<PopStep> trace = new ArrayList<PopStep>();
        List<String> conflictResolution = new ArrayList<String>();
        List<PopSolution> popSolutions = new ArrayList<PopSolution>();

        stats[1] = 1; // Primer nodo
        popSearch(
                pasosIniciales,
                constraintsIniciales,
                linksIniciales,
                openInicial,
                2, // Siguiente ID de paso libre
                maxPasos,
                stats,
                problema,
                startStep,
                finishStep,
                limiteAlcanzado,
                trace,
                conflictResolution,
                popSolutions
        );

        List<List<Action>> planes = new ArrayList<List<Action>>();
        List<List<State>> trayectorias = new ArrayList<List<State>>();
        Set<List<Action>> planesUnicos = new LinkedHashSet<List<Action>>();

        Set<Step> ultimosPasos = null;
        Set<OrderConstraint> ultimasRestricciones = null;
        Set<CausalLink> ultimosEnlaces = null;

        if (!popSolutions.isEmpty()) {
            // Guardamos el primer plan parcial para reportarlo en el constructor y mantener compatibilidad
            PopSolution primeraSol = popSolutions.get(0);
            ultimosPasos = primeraSol.steps;
            ultimasRestricciones = primeraSol.constraints;
            ultimosEnlaces = primeraSol.links;

            // Generar todas las linealizaciones para cada solución de orden parcial
            for (PopSolution sol : popSolutions) {
                List<List<Step>> lineals = obtenerTodasLasLinealizaciones(sol.steps, sol.constraints);
                for (List<Step> lin : lineals) {
                    List<Action> plan = new ArrayList<Action>();
                    for (Step s : lin) {
                        if (s.id != 0 && s.id != 1) {
                            plan.add(s.action);
                        }
                    }
                    if (planesUnicos.add(plan)) {
                        planes.add(plan);

                        // Trayectoria
                        List<State> trayectoria = new ArrayList<State>();
                        State temp = problema.getEstadoInicial();
                        trayectoria.add(temp);
                        for (Action a : plan) {
                            temp = temp.apply(a);
                            trayectoria.add(temp);
                        }
                        trayectorias.add(trayectoria);

                        if (planes.size() >= 50) break; // Límite de seguridad
                    }
                }
                if (planes.size() >= 50) break;
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
            return new Resultado(true, filteredPlanes, filteredTrayectorias, stats[0], stats[1], false, "Planes POP encontrados con éxito.", ultimosPasos, ultimasRestricciones, ultimosEnlaces, trace, conflictResolution);
        } else {
            String msg = "No se encontró solución POP.";
            if (limiteAlcanzado[0]) {
                msg = "Límite configurado alcanzado (Máx acciones POP: " + maxPasos + ").";
                System.err.println("ADVERTENCIA: " + msg);
            }
            return new Resultado(false, filteredPlanes, filteredTrayectorias, stats[0], stats[1], limiteAlcanzado[0], msg, null, null, null, trace, conflictResolution);
        }
    }

    // -------------------------------------------------------------------------
    // Algoritmo de Búsqueda POP
    // -------------------------------------------------------------------------

    private static boolean popSearch(Set<Step> steps,
                                     Set<OrderConstraint> constraints,
                                     Set<CausalLink> links,
                                     List<OpenPrecondition> open,
                                     int nextStepId,
                                     int maxPasos,
                                     int[] stats,
                                     Problema_Plan problema,
                                     Step startStep,
                                     Step finishStep,
                                     boolean[] limiteAlcanzado,
                                     List<PopStep> trace,
                                     List<String> conflictResolution,
                                     List<PopSolution> popSolutions) {

        // Registrar estado actual en la traza
        List<String> stepsList = new ArrayList<String>();
        for (Step s : steps) {
            stepsList.add(s.id + ":" + s.action.getName());
        }

        List<String> constraintsList = new ArrayList<String>();
        for (OrderConstraint oc : constraints) {
            constraintsList.add(oc.before.id + "<" + oc.after.id);
        }

        List<String> linksList = new ArrayList<String>();
        for (CausalLink cl : links) {
            linksList.add(cl.source.id + "-" + cl.literal.toString() + "->" + cl.target.id);
        }

        List<String> openList = new ArrayList<String>();
        for (OpenPrecondition oPre : open) {
            openList.add(oPre.step.id + ":" + oPre.literal.toString());
        }

        trace.add(new PopStep(stats[0], stepsList, constraintsList, linksList, openList));

        // Validar límite de pasos (excluyendo Start y Finish)
        if (steps.size() - 2 > maxPasos) {
            limiteAlcanzado[0] = true;
            return false;
        }
        if (popSolutions.size() >= 50) {
            return false; // Parar si ya recolectamos suficientes redes de orden parcial
        }

        stats[0]++; // Nodo explorado

        if (open.isEmpty()) {
            popSolutions.add(new PopSolution(steps, constraints, links));
            return false; // Forzar backtracking para seguir explorando otras soluciones
        }

        // Seleccionar precondición abierta (primera de la lista)
        OpenPrecondition op = open.remove(0);
        Step sNeed = op.step;
        Literal p = op.literal;

        // --- Opción 1: Reusar un paso existente en el plan ---
        for (Step sAdd : steps) {
            if (sAdd.action.achieves(p)) {
                // Ordenar sAdd antes de sNeed
                Set<OrderConstraint> nextConstraints = new LinkedHashSet<OrderConstraint>(constraints);
                nextConstraints.add(new OrderConstraint(sAdd, sNeed));

                if (linearize(steps, nextConstraints) != null) {
                    CausalLink newLink = new CausalLink(sAdd, p, sNeed);
                    Set<CausalLink> nextLinks = new LinkedHashSet<CausalLink>(links);
                    nextLinks.add(newLink);

                    List<Threat> threats = findThreats(steps, nextLinks, nextConstraints);
                    stats[1]++;
                    if (resolveAndSearch(steps, nextConstraints, nextLinks, open, threats, nextStepId, maxPasos, stats, problema, startStep, finishStep, limiteAlcanzado, trace, conflictResolution, popSolutions)) {
                        return true;
                    }
                }
            }
        }

        // --- Opción 2: Crear un paso nuevo a partir de las acciones ---
        for (Action a : problema.getAcciones()) {
            if (a.achieves(p)) {
                Step sAdd = new Step(nextStepId, a);
                Set<Step> nextSteps = new LinkedHashSet<Step>(steps);
                nextSteps.add(sAdd);

                Set<OrderConstraint> nextConstraints = new LinkedHashSet<OrderConstraint>(constraints);
                nextConstraints.add(new OrderConstraint(sAdd, sNeed));
                nextConstraints.add(new OrderConstraint(startStep, sAdd));
                nextConstraints.add(new OrderConstraint(sAdd, finishStep));

                if (linearize(nextSteps, nextConstraints) != null) {
                    CausalLink newLink = new CausalLink(sAdd, p, sNeed);
                    Set<CausalLink> nextLinks = new LinkedHashSet<CausalLink>(links);
                    nextLinks.add(newLink);

                    List<OpenPrecondition> nextOpen = new ArrayList<OpenPrecondition>(open);
                    for (Literal pre : a.getPreconditions()) {
                        nextOpen.add(new OpenPrecondition(sAdd, pre));
                    }

                    List<Threat> threats = findThreats(nextSteps, nextLinks, nextConstraints);
                    stats[1]++;
                    if (resolveAndSearch(nextSteps, nextConstraints, nextLinks, nextOpen, threats, nextStepId + 1, maxPasos, stats, problema, startStep, finishStep, limiteAlcanzado, trace, conflictResolution, popSolutions)) {
                        return true;
                    }
                }
            }
        }

        // Backtrack
        open.add(0, op);
        return false;
    }

    private static boolean resolveAndSearch(Set<Step> steps,
                                            Set<OrderConstraint> constraints,
                                            Set<CausalLink> links,
                                            List<OpenPrecondition> open,
                                            List<Threat> threats,
                                            int nextStepId,
                                            int maxPasos,
                                            int[] stats,
                                            Problema_Plan problema,
                                            Step startStep,
                                            Step finishStep,
                                            boolean[] limiteAlcanzado,
                                            List<PopStep> trace,
                                            List<String> conflictResolution,
                                            List<PopSolution> popSolutions) {
        if (threats.isEmpty()) {
            return popSearch(steps, constraints, links, open, nextStepId, maxPasos, stats, problema, startStep, finishStep, limiteAlcanzado, trace, conflictResolution, popSolutions);
        }

        // Elegir la primera amenaza
        Threat t = threats.get(0);
        CausalLink link = t.link;
        Step S_threat = t.step;

        String msgThreat = "[POP] Conflicto (Amenaza) detectado: El paso S" + S_threat.id + " (" + S_threat.action.getName() + ") amenaza el enlace causal: S" + link.source.id + " --[" + link.literal + "]--> S" + link.target.id;
        System.out.println(ConsoleColorFormatter.BOLD_YELLOW + "  " + msgThreat + ConsoleColorFormatter.RESET);
        conflictResolution.add(msgThreat);

        // -- Sub-Opción A: Promoción (S_target < S_threat) --
        String msgPromo = "    -> Intentando PROMOCION: forzar orden S" + link.target.id + " (" + link.target.action.getName() + ") < S" + S_threat.id + " (" + S_threat.action.getName() + ")";
        System.out.println(ConsoleColorFormatter.BOLD_CYAN + msgPromo + ConsoleColorFormatter.RESET);
        conflictResolution.add(msgPromo);
        
        Set<OrderConstraint> promoConstraints = new LinkedHashSet<OrderConstraint>(constraints);
        promoConstraints.add(new OrderConstraint(link.target, S_threat));
        if (linearize(steps, promoConstraints) != null) {
            List<Threat> nextThreats = findThreats(steps, links, promoConstraints);
            stats[1]++;
            if (resolveAndSearch(steps, promoConstraints, links, open, nextThreats, nextStepId, maxPasos, stats, problema, startStep, finishStep, limiteAlcanzado, trace, conflictResolution, popSolutions)) {
                return true;
            }
        } else {
            String msgPromoFail = "    -> PROMOCION no viable: crea un ciclo inconsistente.";
            System.out.println(ConsoleColorFormatter.BOLD_RED + msgPromoFail + ConsoleColorFormatter.RESET);
            conflictResolution.add(msgPromoFail);
        }

        // -- Sub-Opción B: Degradación / Democión (S_threat < S_source) --
        String msgDemo = "    -> Intentando DEGRADACION (Democion): forzar orden S" + S_threat.id + " (" + S_threat.action.getName() + ") < S" + link.source.id + " (" + link.source.action.getName() + ")";
        System.out.println(ConsoleColorFormatter.BOLD_CYAN + msgDemo + ConsoleColorFormatter.RESET);
        conflictResolution.add(msgDemo);
        
        Set<OrderConstraint> demoConstraints = new LinkedHashSet<OrderConstraint>(constraints);
        demoConstraints.add(new OrderConstraint(S_threat, link.source));
        if (linearize(steps, demoConstraints) != null) {
            List<Threat> nextThreats = findThreats(steps, links, demoConstraints);
            stats[1]++;
            if (resolveAndSearch(steps, demoConstraints, links, open, nextThreats, nextStepId, maxPasos, stats, problema, startStep, finishStep, limiteAlcanzado, trace, conflictResolution, popSolutions)) {
                return true;
            }
        } else {
            String msgDemoFail = "    -> DEGRADACION no viable: crea un ciclo inconsistente.";
            System.out.println(ConsoleColorFormatter.BOLD_RED + msgDemoFail + ConsoleColorFormatter.RESET);
            conflictResolution.add(msgDemoFail);
        }

        String msgBacktrack = "    -> Ambos metodos de resolucion fallaron en este punto. Haciendo BACKTRACK...";
        System.out.println(ConsoleColorFormatter.BOLD_RED + msgBacktrack + ConsoleColorFormatter.RESET);
        conflictResolution.add(msgBacktrack);
        return false;
    }

    // -------------------------------------------------------------------------
    // Funciones auxiliares
    // -------------------------------------------------------------------------

    private static List<Threat> findThreats(Collection<Step> steps, Collection<CausalLink> links, Collection<OrderConstraint> constraints) {
        List<Threat> threats = new ArrayList<Threat>();
        for (CausalLink link : links) {
            for (Step step : steps) {
                if (step.equals(link.source) || step.equals(link.target)) {
                    continue;
                }
                if (step.action.threatens(link.literal)) {
                    // ¿Es posible colocar a step entre source y target?
                    // Es decir, ¿es consistente constraints + {source < step, step < target}?
                    Set<OrderConstraint> testConstraints = new HashSet<OrderConstraint>(constraints);
                    testConstraints.add(new OrderConstraint(link.source, step));
                    testConstraints.add(new OrderConstraint(step, link.target));
                    if (linearize(steps, testConstraints) != null) {
                        threats.add(new Threat(link, step));
                    }
                }
            }
        }
        return threats;
    }

    /**
     * Comprueba la consistencia de las restricciones y devuelve un ordenamiento lineal de los pasos
     * mediante el algoritmo de Kahn para ordenación topológica.
     * Devuelve null si se detecta un ciclo (inconsistencia).
     */
    public static List<Step> linearize(Collection<Step> steps, Collection<OrderConstraint> constraints) {
        Map<Step, List<Step>> adj = new HashMap<Step, List<Step>>();
        Map<Step, Integer> inDegree = new HashMap<Step, Integer>();
        for (Step s : steps) {
            adj.put(s, new ArrayList<Step>());
            inDegree.put(s, 0);
        }
        for (OrderConstraint oc : constraints) {
            if (adj.containsKey(oc.before) && adj.containsKey(oc.after)) {
                adj.get(oc.before).add(oc.after);
                inDegree.put(oc.after, inDegree.get(oc.after) + 1);
            }
        }

        Queue<Step> queue = new LinkedList<Step>();
        for (Step s : steps) {
            if (inDegree.get(s) == 0) {
                queue.add(s);
            }
        }

        List<Step> sorted = new ArrayList<Step>();
        while (!queue.isEmpty()) {
            Step u = queue.poll();
            sorted.add(u);
            for (Step v : adj.get(u)) {
                inDegree.put(v, inDegree.get(v) - 1);
                if (inDegree.get(v) == 0) {
                    queue.add(v);
                }
            }
        }

        if (sorted.size() != steps.size()) {
            return null; // Inconsistente (ciclo detectado)
        }
        return sorted;
    }

    /**
     * Obtiene todas las linealizaciones topológicas posibles de una red POP.
     */
    public static List<List<Step>> obtenerTodasLasLinealizaciones(Collection<Step> steps, Collection<OrderConstraint> constraints) {
        List<List<Step>> resultados = new ArrayList<List<Step>>();
        List<Step> actual = new ArrayList<Step>();
        Set<Step> visitados = new HashSet<Step>();

        Map<Step, List<Step>> adj = new HashMap<Step, List<Step>>();
        Map<Step, Integer> inDegree = new HashMap<Step, Integer>();
        for (Step s : steps) {
            adj.put(s, new ArrayList<Step>());
            inDegree.put(s, 0);
        }
        for (OrderConstraint oc : constraints) {
            if (adj.containsKey(oc.before) && adj.containsKey(oc.after)) {
                adj.get(oc.before).add(oc.after);
                inDegree.put(oc.after, inDegree.get(oc.after) + 1);
            }
        }

        generarTopologicalSorts(steps, adj, inDegree, visitados, actual, resultados);
        return resultados;
    }

    private static void generarTopologicalSorts(Collection<Step> steps,
                                                Map<Step, List<Step>> adj,
                                                Map<Step, Integer> inDegree,
                                                Set<Step> visitados,
                                                List<Step> actual,
                                                List<List<Step>> resultados) {
        if (resultados.size() >= 50) return; // Limitar tamaño de soluciones linealizadas
        
        boolean flag = false;
        for (Step s : steps) {
            if (!visitados.contains(s) && inDegree.get(s) == 0) {
                visitados.add(s);
                actual.add(s);

                for (Step next : adj.get(s)) {
                    inDegree.put(next, inDegree.get(next) - 1);
                }

                generarTopologicalSorts(steps, adj, inDegree, visitados, actual, resultados);

                // Backtrack
                visitados.remove(s);
                actual.remove(actual.size() - 1);
                for (Step next : adj.get(s)) {
                    inDegree.put(next, inDegree.get(next) + 1);
                }

                flag = true;
            }
        }

        if (!flag && actual.size() == steps.size()) {
            resultados.add(new ArrayList<Step>(actual));
        }
    }
}

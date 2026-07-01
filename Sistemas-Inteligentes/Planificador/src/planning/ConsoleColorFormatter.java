package planning;

import java.util.List;
import planning.Action;
import planning.State;
import planning.busqueda.Planificador;
import planning.strips.PlanificadorStrips;
import planning.pop.PlanificadorPop;

/**
 * Clase de utilidad para formatear y colorear la salida de texto en la terminal.
 * Version ASCII-compatible para evitar problemas de codificacion en terminales Windows.
 */
public final class ConsoleColorFormatter {

    private ConsoleColorFormatter() {}

    // Códigos ANSI para colores
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public static final String BOLD_BLACK = "\u001B[1;30m";
    public static final String BOLD_RED = "\u001B[1;31m";
    public static final String BOLD_GREEN = "\u001B[1;32m";
    public static final String BOLD_YELLOW = "\u001B[1;33m";
    public static final String BOLD_BLUE = "\u001B[1;34m";
    public static final String BOLD_PURPLE = "\u001B[1;35m";
    public static final String BOLD_CYAN = "\u001B[1;36m";
    public static final String BOLD_WHITE = "\u001B[1;37m";

    /**
     * Da formato y color a un estado (ej. { p1, p2 }).
     */
    public static String colorizeState(String stateStr) {
        if (stateStr == null || stateStr.equals("None") || stateStr.trim().isEmpty()) {
            return BOLD_BLACK + "None" + RESET;
        }
        String colorized = stateStr.trim();
        colorized = colorized.replace("{", BOLD_CYAN + "{" + GREEN);
        colorized = colorized.replace("}", BOLD_CYAN + "}" + RESET);
        colorized = colorized.replace(",", BOLD_WHITE + "," + GREEN);
        return colorized;
    }

    /**
     * Da formato y color a los elementos de la pila de STRIPS.
     */
    public static String colorizeStackItem(String item) {
        if (item == null) return "null";
        if (item.startsWith("State[")) {
            String content = item.substring(6, item.length() - 1);
            return BOLD_CYAN + "State{" + GREEN + content + BOLD_CYAN + "}" + RESET;
        } else if (Character.isUpperCase(item.charAt(0)) && !item.contains("(") && !item.contains("[")) {
            // Nombre de acción
            return BOLD_BLUE + item + RESET;
        } else {
            // Literal
            return GREEN + item + RESET;
        }
    }

    /**
     * Da formato y color a un paso POP (id y acción).
     */
    public static String colorizePopStepItem(String stepItem) {
        int idx = stepItem.indexOf(':');
        if (idx >= 0) {
            String id = stepItem.substring(0, idx);
            String name = stepItem.substring(idx + 1);
            return BOLD_YELLOW + "S" + id + RESET + BOLD_BLUE + ":" + name + RESET;
        }
        return stepItem;
    }

    /**
     * Da formato y color a una restricción de orden POP (S1 < S2).
     */
    public static String colorizePopConstraint(String constraint) {
        int idx = constraint.indexOf('<');
        if (idx >= 0) {
            String before = constraint.substring(0, idx);
            String after = constraint.substring(idx + 1);
            return BOLD_YELLOW + "S" + before + BOLD_WHITE + " < " + BOLD_YELLOW + "S" + after + RESET;
        }
        return constraint;
    }

    /**
     * Da formato y color a un enlace causal POP (S1 --[literal]--> S2).
     */
    public static String colorizePopLink(String link) {
        int dashIdx = link.indexOf('-');
        int arrowIdx = link.indexOf("->");
        if (dashIdx >= 0 && arrowIdx > dashIdx) {
            String source = link.substring(0, dashIdx);
            String literal = link.substring(dashIdx + 1, arrowIdx);
            String target = link.substring(arrowIdx + 2);
            return BOLD_YELLOW + "S" + source + BOLD_PURPLE + " --[" + GREEN + literal + BOLD_PURPLE + "]--> " + BOLD_YELLOW + "S" + target + RESET;
        }
        return link;
    }

    /**
     * Da formato y color a una precondición abierta POP (S, literal).
     */
    public static String colorizePopOpen(String openPre) {
        int idx = openPre.indexOf(':');
        if (idx >= 0) {
            String id = openPre.substring(0, idx);
            String literal = openPre.substring(idx + 1);
            return BOLD_RED + "(S" + id + ", " + literal + ")" + RESET;
        }
        return openPre;
    }

    /**
     * Imprime la traza de pasos de BFS.
     */
    public static void printBfsTrace(List<Planificador.BfsStep> trace) {
        System.out.println(BOLD_CYAN + "\n+-----------------------------------------------------------------------------+" + RESET);
        System.out.println(BOLD_CYAN + "|        EVOLUCION PASO A PASO - BFS (Busqueda en Anchura / Forward)          |" + RESET);
        System.out.println(BOLD_CYAN + "+-----------------------------------------------------------------------------+" + RESET);
        for (Planificador.BfsStep step : trace) {
            System.out.printf(BOLD_YELLOW + "  [Paso %2d] " + RESET, step.step);
            System.out.print(BOLD_WHITE + "Padre: " + RESET + colorizeState(step.parentState));
            System.out.print(BOLD_WHITE + "  --> ( " + BOLD_BLUE + step.action + BOLD_WHITE + " ) --> " + RESET);
            System.out.println(colorizeState(step.state));
        }
        System.out.println();
    }

    /**
     * Imprime la traza de la pila de STRIPS.
     */
    public static void printStripsTrace(List<PlanificadorStrips.StripsStep> trace) {
        System.out.println(BOLD_PURPLE + "\n+-----------------------------------------------------------------------------+" + RESET);
        System.out.println(BOLD_PURPLE + "|             EVOLUCION DE LA PILA DE STRIPS (Goal Stack Planning)            |" + RESET);
        System.out.println(BOLD_PURPLE + "+-----------------------------------------------------------------------------+" + RESET);
        for (PlanificadorStrips.StripsStep step : trace) {
            System.out.printf(BOLD_YELLOW + "  [Paso %2d] " + RESET, step.step);
            System.out.println(BOLD_WHITE + "Estado Actual: " + RESET + colorizeState(step.state));
            System.out.print(BOLD_WHITE + "            Pila (cima -> fondo): " + RESET);
            if (step.stack.isEmpty()) {
                System.out.println(BOLD_BLACK + "[Vacia]" + RESET);
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < step.stack.size(); i++) {
                    sb.append(colorizeStackItem(step.stack.get(i)));
                    if (i < step.stack.size() - 1) {
                        sb.append(BOLD_WHITE + " | " + RESET);
                    }
                }
                System.out.println(sb.toString());
            }
            System.out.println(BOLD_BLACK + "            -------------------------------------------------------" + RESET);
        }
        System.out.println();
    }

    /**
     * Imprime la traza de la red de POP.
     */
    public static void printPopTrace(List<PlanificadorPop.PopStep> trace) {
        System.out.println(BOLD_YELLOW + "\n+-----------------------------------------------------------------------------+" + RESET);
        System.out.println(BOLD_YELLOW + "|             EVOLUCION DE LA RED DE PLANIFICACION POP (Orden Parcial)        |" + RESET);
        System.out.println(BOLD_YELLOW + "+-----------------------------------------------------------------------------+" + RESET);
        for (PlanificadorPop.PopStep step : trace) {
            System.out.printf(BOLD_YELLOW + "  [Paso %2d] " + RESET, step.step);
            
            // Pasos activos
            System.out.print(BOLD_WHITE + "Pasos Activos: " + RESET);
            StringBuilder sbSteps = new StringBuilder();
            for (int i = 0; i < step.steps.size(); i++) {
                sbSteps.append(colorizePopStepItem(step.steps.get(i)));
                if (i < step.steps.size() - 1) sbSteps.append(", ");
            }
            System.out.println(sbSteps.toString());

            // Restricciones
            System.out.print("            " + BOLD_WHITE + "Restricciones: " + RESET);
            if (step.constraints.isEmpty()) {
                System.out.println(BOLD_BLACK + "Ninguna" + RESET);
            } else {
                StringBuilder sbCons = new StringBuilder();
                for (int i = 0; i < step.constraints.size(); i++) {
                    sbCons.append(colorizePopConstraint(step.constraints.get(i)));
                    if (i < step.constraints.size() - 1) sbCons.append(", ");
                }
                System.out.println(sbCons.toString());
            }

            // Enlaces causales
            System.out.print("            " + BOLD_WHITE + "Enlaces Causales:\n" + RESET);
            if (step.links.isEmpty()) {
                System.out.println("              " + BOLD_BLACK + "Ninguno" + RESET);
            } else {
                for (String link : step.links) {
                    System.out.println("              " + colorizePopLink(link));
                }
            }

            // Precondiciones abiertas
            System.out.print("            " + BOLD_WHITE + "Precondiciones Abiertas: " + RESET);
            if (step.open.isEmpty()) {
                System.out.println(BOLD_GREEN + "Ninguna (Plan Completo)" + RESET);
            } else {
                StringBuilder sbOpen = new StringBuilder();
                for (int i = 0; i < step.open.size(); i++) {
                    sbOpen.append(colorizePopOpen(step.open.get(i)));
                    if (i < step.open.size() - 1) sbOpen.append(", ");
                }
                System.out.println(sbOpen.toString());
            }
            System.out.println(BOLD_BLACK + "            -------------------------------------------------------" + RESET);
        }
        System.out.println();
    }

    /**
     * Imprime el resultado final del BFS.
     */
    public static void printBfsResult(Planificador.Resultado r) {
        if (!r.exito) {
            System.out.println(BOLD_RED + ">>> Forward BFS: SIN SOLUCION <<<" + RESET);
            System.out.println(BOLD_RED + "[ALERTA] BFS no pudo encontrar un plan. Posible conflicto de objetivos o estado inalcanzable." + RESET);
            System.out.println("Nodos explorados: " + BOLD_YELLOW + r.nodosExplorados + RESET + ", generados: " + BOLD_YELLOW + r.nodosGenerados + RESET);
            System.out.println();
            return;
        }

        System.out.println(BOLD_GREEN + ">>> Forward BFS: " + r.planes.size() + " PLAN(ES) OPTIMO(S) ENCONTRADO(S) <<<" + RESET);
        for (int p = 0; p < r.planes.size(); p++) {
            List<Action> plan = r.planes.get(p);
            List<State> trayectoria = r.trayectorias.get(p);
            System.out.println(BOLD_WHITE + "  [Plan #" + (p + 1) + " - " + plan.size() + " acciones]:" + RESET);
            State estado = trayectoria.get(0);
            System.out.println("    E0 : " + colorizeState(estado.toString()));
            for (int i = 0; i < plan.size(); i++) {
                Action a = plan.get(i);
                estado   = trayectoria.get(i + 1);
                System.out.printf("    " + BOLD_BLUE + "%d) %s" + RESET + "%n", i + 1, a.getName());
                System.out.println("    E" + (i + 1) + " : " + colorizeState(estado.toString()));
            }
        }
        System.out.println("Nodos explorados: " + BOLD_YELLOW + r.nodosExplorados + RESET + ", generados: " + BOLD_YELLOW + r.nodosGenerados + RESET);
        System.out.println();
    }

    /**
     * Imprime el resultado final de STRIPS.
     */
    public static void printStripsResult(PlanificadorStrips.Resultado r) {
        if (!r.exito) {
            System.out.println(BOLD_RED + ">>> STRIPS: SIN SOLUCION (" + r.mensaje + ") <<<" + RESET);
            System.out.println(BOLD_RED + "[ALERTA] STRIPS no pudo resolver el problema. Posible anomalia de Sussman o ciclo infinito en la pila." + RESET);
            System.out.println("Nodos explorados: " + BOLD_YELLOW + r.nodosExplorados + RESET + ", generados: " + BOLD_YELLOW + r.nodosGenerados + RESET);
            System.out.println();
            return;
        }

        System.out.println(BOLD_GREEN + ">>> STRIPS: " + r.planes.size() + " PLAN(ES) ENCONTRADO(S) <<<" + RESET);
        for (int p = 0; p < r.planes.size(); p++) {
            List<Action> plan = r.planes.get(p);
            List<State> trayectoria = r.trayectorias.get(p);
            System.out.println(BOLD_WHITE + "  [Plan #" + (p + 1) + " - " + plan.size() + " acciones]:" + RESET);
            State estado = trayectoria.get(0);
            System.out.println("    E0 : " + colorizeState(estado.toString()));
            for (int i = 0; i < plan.size(); i++) {
                Action a = plan.get(i);
                estado   = trayectoria.get(i + 1);
                System.out.printf("    " + BOLD_BLUE + "%d) %s" + RESET + "%n", i + 1, a.getName());
                System.out.println("    E" + (i + 1) + " : " + colorizeState(estado.toString()));
            }
        }
        System.out.println("Nodos explorados: " + BOLD_YELLOW + r.nodosExplorados + RESET + ", generados: " + BOLD_YELLOW + r.nodosGenerados + RESET);
        System.out.println();
    }

    /**
     * Imprime el resultado final de POP.
     */
    public static void printPopResult(PlanificadorPop.Resultado r) {
        if (!r.exito) {
            System.out.println(BOLD_RED + ">>> POP: SIN SOLUCION (" + r.mensaje + ") <<<" + RESET);
            System.out.println(BOLD_RED + "[ALERTA] POP no pudo resolver el problema. Posible conflicto de amenazas ciclicas (ordenamientos inconsistentes en promocion/degradacion)." + RESET);
            System.out.println("Nodos explorados: " + BOLD_YELLOW + r.nodosExplorados + RESET + ", generados: " + BOLD_YELLOW + r.nodosGenerados + RESET);
            System.out.println();
            return;
        }

        System.out.println(BOLD_GREEN + ">>> POP: " + r.planes.size() + " LINEALIZACION(ES) DE PLAN ENCONTRADA(S) <<<" + RESET);
        System.out.println(BOLD_WHITE + "Pasos (Acciones del plan):" + RESET);
        for (PlanificadorPop.Step s : r.pasos) {
            if (s.id != 0 && s.id != 1) {
                System.out.println("  " + BOLD_YELLOW + "S" + s.id + RESET + ": " + BOLD_BLUE + s.action.getName() + RESET);
            }
        }
        System.out.println(BOLD_WHITE + "Restricciones de orden (Topologia):" + RESET);
        for (PlanificadorPop.OrderConstraint oc : r.restricciones) {
            System.out.println("  " + BOLD_YELLOW + "S" + oc.before.id + RESET + BOLD_WHITE + " < " + BOLD_YELLOW + "S" + oc.after.id + RESET);
        }
        System.out.println(BOLD_WHITE + "Enlaces causales:" + RESET);
        for (PlanificadorPop.CausalLink cl : r.enlacesCausales) {
            System.out.println("  " + BOLD_YELLOW + "S" + cl.source.id + RESET + BOLD_PURPLE + " --[" + GREEN + cl.literal + BOLD_PURPLE + "]--> " + BOLD_YELLOW + "S" + cl.target.id + RESET);
        }
        
        for (int p = 0; p < r.planes.size(); p++) {
            List<Action> plan = r.planes.get(p);
            System.out.println(BOLD_WHITE + "  [Plan linealizado #" + (p + 1) + " - " + plan.size() + " acciones]:" + RESET);
            for (int i = 0; i < plan.size(); i++) {
                System.out.println("    " + BOLD_BLUE + (i + 1) + ") " + plan.get(i).getName() + RESET);
            }
        }
        System.out.println("Nodos explorados: " + BOLD_YELLOW + r.nodosExplorados + RESET + ", generados: " + BOLD_YELLOW + r.nodosGenerados + RESET);
        System.out.println();
    }

    /**
     * Rellena con espacios un texto por la derecha hasta alcanzar la longitud deseada.
     */
    private static String padRight(String text, int length) {
        if (text == null) text = "";
        if (text.length() >= length) return text;
        StringBuilder sb = new StringBuilder(text);
        while (sb.length() < length) {
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * Imprime una tabla resumen comparativa con colores.
     */
    public static void printComparisonTable(String fichero,
                                            Planificador.Resultado rBFS,
                                            PlanificadorStrips.Resultado rStrips,
                                            PlanificadorPop.Resultado rPop) {
        System.out.println(BOLD_CYAN + "=== " + BOLD_WHITE + "RESUMEN COMPARATIVO PARA: " + BOLD_YELLOW + fichero + BOLD_CYAN + " ===" + RESET);
        System.out.println(BOLD_CYAN + "+-----------------+---------+------------+--------------+-----------------+" + RESET);
        
        String h1 = padRight("Planificador", 15);
        String h2 = padRight("Exito", 7);
        String h3 = padRight("Long. Plan", 10);
        String h4 = padRight("Nodos Expl.", 12);
        String h5 = padRight("Nodos Gen.", 15);
        
        System.out.println(BOLD_CYAN + "| " + BOLD_WHITE + h1 + BOLD_CYAN + " | " + BOLD_WHITE + h2 + BOLD_CYAN + " | " + BOLD_WHITE + h3 + BOLD_CYAN + " | " + BOLD_WHITE + h4 + BOLD_CYAN + " | " + BOLD_WHITE + h5 + BOLD_CYAN + " |" + RESET);
        System.out.println(BOLD_CYAN + "+-----------------+---------+------------+--------------+-----------------+" + RESET);

        // Fila BFS
        String statusBFS = rBFS.exito ? "SI" : "NO";
        String lenBFS = rBFS.exito ? String.valueOf(rBFS.plan.size()) : "-";
        String f1 = padRight("Forward (BFS)", 15);
        String f2 = padRight(statusBFS, 7);
        String f3 = padRight(lenBFS, 10);
        String f4 = padRight(String.valueOf(rBFS.nodosExplorados), 12);
        String f5 = padRight(String.valueOf(rBFS.nodosGenerados), 15);
        System.out.println(BOLD_CYAN + "| " + BOLD_WHITE + f1 + BOLD_CYAN + " | " + (rBFS.exito ? BOLD_GREEN : BOLD_RED) + f2 + BOLD_CYAN + " | " + BOLD_YELLOW + f3 + BOLD_CYAN + " | " + BOLD_YELLOW + f4 + BOLD_CYAN + " | " + BOLD_YELLOW + f5 + BOLD_CYAN + " |" + RESET);

        // Fila STRIPS
        String statusStrips = rStrips.exito ? "SI" : "NO";
        String lenStrips = rStrips.exito ? String.valueOf(rStrips.plan.size()) : "-";
        String s1 = padRight("STRIPS Stack", 15);
        String s2 = padRight(statusStrips, 7);
        String s3 = padRight(lenStrips, 10);
        String s4 = padRight(String.valueOf(rStrips.nodosExplorados), 12);
        String s5 = padRight(String.valueOf(rStrips.nodosGenerados), 15);
        System.out.println(BOLD_CYAN + "| " + BOLD_WHITE + s1 + BOLD_CYAN + " | " + (rStrips.exito ? BOLD_GREEN : BOLD_RED) + s2 + BOLD_CYAN + " | " + BOLD_YELLOW + s3 + BOLD_CYAN + " | " + BOLD_YELLOW + s4 + BOLD_CYAN + " | " + BOLD_YELLOW + s5 + BOLD_CYAN + " |" + RESET);

        // Fila POP
        String statusPop = rPop.exito ? "SI" : "NO";
        String lenPop = rPop.exito ? String.valueOf(rPop.plan.size()) : "-";
        String p1 = padRight("POP (Ord. Par.)", 15);
        String p2 = padRight(statusPop, 7);
        String p3 = padRight(lenPop, 10);
        String p4 = padRight(String.valueOf(rPop.nodosExplorados), 12);
        String p5 = padRight(String.valueOf(rPop.nodosGenerados), 15);
        System.out.println(BOLD_CYAN + "| " + BOLD_WHITE + p1 + BOLD_CYAN + " | " + (rPop.exito ? BOLD_GREEN : BOLD_RED) + p2 + BOLD_CYAN + " | " + BOLD_YELLOW + p3 + BOLD_CYAN + " | " + BOLD_YELLOW + p4 + BOLD_CYAN + " | " + BOLD_YELLOW + p5 + BOLD_CYAN + " |" + RESET);

        System.out.println(BOLD_CYAN + "+-----------------+---------+------------+--------------+-----------------+" + RESET);
        System.out.println();
    }
}

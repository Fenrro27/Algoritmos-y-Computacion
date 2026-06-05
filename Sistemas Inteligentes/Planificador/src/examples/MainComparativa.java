package examples;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import planning.Action;
import planning.Problema_Plan;
import planning.State;
import planning.Util;
import planning.busqueda.Planificador;
import planning.strips.PlanificadorStrips;
import planning.pop.PlanificadorPop;
import planning.ConsoleColorFormatter;

/**
 * Clase ejecutable para comparar los tres planificadores y guardar los
 * resultados en archivos JSON dentro del paquete resultados.
 */
public final class MainComparativa {

    public static void main(String[] args) {
        String[] ficheros = { "ejercicio1_junio22.txt", "ejercicio_2_sep22.txt" };

        // Asegurarse de que el directorio del paquete "resultados" existe
        File dirResultados = new File("src/resultados");
        if (!dirResultados.exists()) {
            dirResultados.mkdirs();
        }

        for (String fichero : ficheros) {
            System.out.println(ConsoleColorFormatter.BOLD_CYAN + "================================================================================" + ConsoleColorFormatter.RESET);
            System.out.println(" " + ConsoleColorFormatter.BOLD_WHITE + "PROCESANDO PROBLEMA: " + ConsoleColorFormatter.BOLD_YELLOW + fichero + ConsoleColorFormatter.RESET);
            System.out.println(ConsoleColorFormatter.BOLD_CYAN + "================================================================================" + ConsoleColorFormatter.RESET);

            Problema_Plan problema;
            try {
                problema = Util.cargarProblema(fichero);
            } catch (IOException e) {
                System.err.println("No se pudo cargar el fichero '" + fichero + "': " + e.getMessage());
                continue;
            } catch (IllegalArgumentException e) {
                System.err.println("Formato incorrecto en '" + fichero + "': " + e.getMessage());
                continue;
            }

            // 1. BFS Hacia adelante
            System.out.println(ConsoleColorFormatter.BOLD_WHITE + "Ejecutando BFS..." + ConsoleColorFormatter.RESET);
            Planificador.Resultado rBFS = Planificador.planificar(problema);
            ConsoleColorFormatter.printBfsTrace(rBFS.trace);
            ConsoleColorFormatter.printBfsResult(rBFS);
            if (!rBFS.exito) {
                System.out.println(ConsoleColorFormatter.BOLD_RED + "[ALERTA CRITICA] BFS no pudo resolver el problema. Se aborta la ejecucion para este problema debido a un posible conflicto insalvable." + ConsoleColorFormatter.RESET + "\n");
                continue;
            }

            // 2. STRIPS regresivo (Goal Stack Planning)
            System.out.println(ConsoleColorFormatter.BOLD_WHITE + "Ejecutando STRIPS..." + ConsoleColorFormatter.RESET);
            PlanificadorStrips.Resultado rStrips = PlanificadorStrips.planificar(problema);
            ConsoleColorFormatter.printStripsTrace(rStrips.trace);
            ConsoleColorFormatter.printStripsResult(rStrips);
            if (!rStrips.exito) {
                System.out.println(ConsoleColorFormatter.BOLD_RED + "[ALERTA CRITICA] STRIPS no pudo resolver el problema. Se aborta la ejecucion para este problema debido a un posible conflicto insalvable." + ConsoleColorFormatter.RESET + "\n");
                continue;
            }

            // 3. POP (Partial Order Planning)
            System.out.println(ConsoleColorFormatter.BOLD_WHITE + "Ejecutando POP..." + ConsoleColorFormatter.RESET);
            PlanificadorPop.Resultado rPop = PlanificadorPop.planificar(problema);
            ConsoleColorFormatter.printPopTrace(rPop.trace);
            ConsoleColorFormatter.printPopResult(rPop);
            if (!rPop.exito) {
                System.out.println(ConsoleColorFormatter.BOLD_RED + "[ALERTA CRITICA] POP no pudo resolver el problema. Se aborta la ejecucion para este problema debido a un posible conflicto insalvable." + ConsoleColorFormatter.RESET + "\n");
                continue;
            }

            // Guardar a JSON
            String jsonOutput = buildJson(fichero, rBFS, rStrips, rPop);
            String nombreResultado = fichero.replace(".txt", ".json");
            File fileResultado = new File(dirResultados, nombreResultado);
            try (FileWriter writer = new FileWriter(fileResultado)) {
                writer.write(jsonOutput);
                System.out.println(ConsoleColorFormatter.BOLD_GREEN + "Resultados guardados en: " + fileResultado.getAbsolutePath() + ConsoleColorFormatter.RESET + "\n");
            } catch (IOException e) {
                System.err.println("Error al guardar el archivo de resultados: " + e.getMessage());
            }

            // Resumen por consola
            ConsoleColorFormatter.printComparisonTable(fichero, rBFS, rStrips, rPop);
        }
    }

    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    private static String buildJson(String fichero,
                                    Planificador.Resultado rBFS,
                                    PlanificadorStrips.Resultado rStrips,
                                    PlanificadorPop.Resultado rPop) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"problem\": \"").append(escapeJson(fichero)).append("\",\n");

        // BFS
        sb.append("  \"bfs\": {\n");
        sb.append("    \"exito\": ").append(rBFS.exito).append(",\n");
        sb.append("    \"nodosExplorados\": ").append(rBFS.nodosExplorados).append(",\n");
        sb.append("    \"nodosGenerados\": ").append(rBFS.nodosGenerados).append(",\n");
        sb.append("    \"plan\": [");
        for (int i = 0; i < rBFS.plan.size(); i++) {
            sb.append("\"").append(escapeJson(rBFS.plan.get(i).getName())).append("\"");
            if (i < rBFS.plan.size() - 1) sb.append(", ");
        }
        sb.append("],\n");
        sb.append("    \"planes\": [");
        for (int p = 0; p < rBFS.planes.size(); p++) {
            sb.append("[");
            List<Action> pl = rBFS.planes.get(p);
            for (int i = 0; i < pl.size(); i++) {
                sb.append("\"").append(escapeJson(pl.get(i).getName())).append("\"");
                if (i < pl.size() - 1) sb.append(", ");
            }
            sb.append("]");
            if (p < rBFS.planes.size() - 1) sb.append(", ");
        }
        sb.append("],\n");
        sb.append("    \"trace\": [\n");
        for (int i = 0; i < rBFS.trace.size(); i++) {
            Planificador.BfsStep step = rBFS.trace.get(i);
            sb.append("      {\n");
            sb.append("        \"step\": ").append(step.step).append(",\n");
            sb.append("        \"state\": \"").append(escapeJson(step.state)).append("\",\n");
            sb.append("        \"action\": \"").append(escapeJson(step.action)).append("\",\n");
            sb.append("        \"parentState\": \"").append(escapeJson(step.parentState)).append("\"\n");
            sb.append("      }");
            if (i < rBFS.trace.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("    ]\n");
        sb.append("  },\n");

        // STRIPS
        sb.append("  \"strips\": {\n");
        sb.append("    \"exito\": ").append(rStrips.exito).append(",\n");
        sb.append("    \"nodosExplorados\": ").append(rStrips.nodosExplorados).append(",\n");
        sb.append("    \"nodosGenerados\": ").append(rStrips.nodosGenerados).append(",\n");
        sb.append("    \"plan\": [");
        for (int i = 0; i < rStrips.plan.size(); i++) {
            sb.append("\"").append(escapeJson(rStrips.plan.get(i).getName())).append("\"");
            if (i < rStrips.plan.size() - 1) sb.append(", ");
        }
        sb.append("],\n");
        sb.append("    \"planes\": [");
        for (int p = 0; p < rStrips.planes.size(); p++) {
            sb.append("[");
            List<Action> pl = rStrips.planes.get(p);
            for (int i = 0; i < pl.size(); i++) {
                sb.append("\"").append(escapeJson(pl.get(i).getName())).append("\"");
                if (i < pl.size() - 1) sb.append(", ");
            }
            sb.append("]");
            if (p < rStrips.planes.size() - 1) sb.append(", ");
        }
        sb.append("],\n");
        sb.append("    \"trace\": [\n");
        for (int i = 0; i < rStrips.trace.size(); i++) {
            PlanificadorStrips.StripsStep step = rStrips.trace.get(i);
            sb.append("      {\n");
            sb.append("        \"step\": ").append(step.step).append(",\n");
            sb.append("        \"state\": \"").append(escapeJson(step.state)).append("\",\n");
            sb.append("        \"stack\": [");
            for (int j = 0; j < step.stack.size(); j++) {
                sb.append("\"").append(escapeJson(step.stack.get(j))).append("\"");
                if (j < step.stack.size() - 1) sb.append(", ");
            }
            sb.append("]\n");
            sb.append("      }");
            if (i < rStrips.trace.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("    ]\n");
        sb.append("  },\n");

        // POP
        sb.append("  \"pop\": {\n");
        sb.append("    \"exito\": ").append(rPop.exito).append(",\n");
        sb.append("    \"nodosExplorados\": ").append(rPop.nodosExplorados).append(",\n");
        sb.append("    \"nodosGenerados\": ").append(rPop.nodosGenerados).append(",\n");
        sb.append("    \"plan\": [");
        for (int i = 0; i < rPop.plan.size(); i++) {
            sb.append("\"").append(escapeJson(rPop.plan.get(i).getName())).append("\"");
            if (i < rPop.plan.size() - 1) sb.append(", ");
        }
        sb.append("],\n");
        sb.append("    \"planes\": [");
        for (int p = 0; p < rPop.planes.size(); p++) {
            sb.append("[");
            List<Action> pl = rPop.planes.get(p);
            for (int i = 0; i < pl.size(); i++) {
                sb.append("\"").append(escapeJson(pl.get(i).getName())).append("\"");
                if (i < pl.size() - 1) sb.append(", ");
            }
            sb.append("]");
            if (p < rPop.planes.size() - 1) sb.append(", ");
        }
        sb.append("],\n");
        sb.append("    \"steps\": [\n");
        int count = 0;
        if (rPop.pasos != null) {
            for (PlanificadorPop.Step step : rPop.pasos) {
                sb.append("      {\"id\": ").append(step.id).append(", \"name\": \"").append(escapeJson(step.action.getName())).append("\"}");
                if (count < rPop.pasos.size() - 1) sb.append(",");
                sb.append("\n");
                count++;
            }
        }
        sb.append("    ],\n");
        sb.append("    \"constraints\": [\n");
        count = 0;
        if (rPop.restricciones != null) {
            for (PlanificadorPop.OrderConstraint oc : rPop.restricciones) {
                sb.append("      {\"before\": ").append(oc.before.id).append(", \"after\": ").append(oc.after.id).append("}");
                if (count < rPop.restricciones.size() - 1) sb.append(",");
                sb.append("\n");
                count++;
            }
        }
        sb.append("    ],\n");
        sb.append("    \"links\": [\n");
        count = 0;
        if (rPop.enlacesCausales != null) {
            for (PlanificadorPop.CausalLink cl : rPop.enlacesCausales) {
                sb.append("      {\"source\": ").append(cl.source.id).append(", \"target\": ").append(cl.target.id).append(", \"literal\": \"").append(escapeJson(cl.literal.toString())).append("\"}");
                if (count < rPop.enlacesCausales.size() - 1) sb.append(",");
                sb.append("\n");
                count++;
            }
        }
        sb.append("    ],\n");
        sb.append("    \"trace\": [\n");
        for (int i = 0; i < rPop.trace.size(); i++) {
            PlanificadorPop.PopStep step = rPop.trace.get(i);
            sb.append("      {\n");
            sb.append("        \"step\": ").append(step.step).append(",\n");
            
            sb.append("        \"steps\": [");
            for (int j = 0; j < step.steps.size(); j++) {
                sb.append("\"").append(escapeJson(step.steps.get(j))).append("\"");
                if (j < step.steps.size() - 1) sb.append(", ");
            }
            sb.append("],\n");

            sb.append("        \"constraints\": [");
            for (int j = 0; j < step.constraints.size(); j++) {
                sb.append("\"").append(escapeJson(step.constraints.get(j))).append("\"");
                if (j < step.constraints.size() - 1) sb.append(", ");
            }
            sb.append("],\n");

            sb.append("        \"links\": [");
            for (int j = 0; j < step.links.size(); j++) {
                sb.append("\"").append(escapeJson(step.links.get(j))).append("\"");
                if (j < step.links.size() - 1) sb.append(", ");
            }
            sb.append("],\n");

            sb.append("        \"open\": [");
            for (int j = 0; j < step.open.size(); j++) {
                sb.append("\"").append(escapeJson(step.open.get(j))).append("\"");
                if (j < step.open.size() - 1) sb.append(", ");
            }
            sb.append("]\n");

            sb.append("      }");
            if (i < rPop.trace.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("    ],\n");
        sb.append("    \"conflictResolution\": [");
        for (int i = 0; i < rPop.conflictResolution.size(); i++) {
            sb.append("\"").append(escapeJson(rPop.conflictResolution.get(i))).append("\"");
            if (i < rPop.conflictResolution.size() - 1) sb.append(", ");
        }
        sb.append("]\n");
        sb.append("  }\n");

        sb.append("}\n");
        return sb.toString();
    }
}

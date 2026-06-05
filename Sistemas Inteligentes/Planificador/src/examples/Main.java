package examples;

import java.io.IOException;

import planning.Action;
import planning.Problema_Plan;
import planning.State;
import planning.Util;
import planning.busqueda.Planificador;
import planning.busqueda.Planificador.Resultado;

/**
 * Punto de entrada: carga un problema desde fichero y lo resuelve con
 * el planificador BFS.
 *
 * Uso:
 *   java examples.Main [&lt;ruta_fichero&gt;]
 *
 * Si no se pasa argumento se usa {@code ejercicio1_junio22.txt} por defecto.
 */
public final class Main {

//    private static final String FICHERO_POR_DEFECTO = "ejercicio1_junio22.txt";
    private static final String FICHERO_POR_DEFECTO = "ejercicio_2_sep22.txt";

    public static void main(String[] args) {
        String ruta = (args.length > 0) ? args[0] : FICHERO_POR_DEFECTO;

        Problema_Plan problema;
        try {
            problema = Util.cargarProblema(ruta);
        } catch (IOException e) {
            System.err.println("No se pudo leer el fichero '" + ruta + "': " + e.getMessage());
            System.exit(1);
            return;
        } catch (IllegalArgumentException e) {
            System.err.println("Formato incorrecto en '" + ruta + "': " + e.getMessage());
            System.exit(1);
            return;
        }

        System.out.println("=== Problema cargado desde " + ruta + " ===");
        System.out.println("Estado inicial : " + problema.getEstadoInicial());
        System.out.println("Estado final   : " + problema.getEstadoFinal());
        System.out.println("Acciones       : " + problema.getAcciones().size());
        System.out.println("Subobjetivos pendientes: " + problema.subobjetivosPendientes());
        System.out.println();

        Resultado r = Planificador.planificar(problema);

        if (!r.exito) {
            System.out.println("=== Sin solución ===");
            System.out.println("Nodos explorados: " + r.nodosExplorados);
            System.out.println("Nodos generados : " + r.nodosGenerados);
            return;
        }

        System.out.println("=== Plan encontrado (" + r.plan.size() + " acciones) ===");
        State estado = r.trayectoria.get(0);
        System.out.println("E0 : " + estado);
        for (int i = 0; i < r.plan.size(); i++) {
            Action a = r.plan.get(i);
            estado   = r.trayectoria.get(i + 1);
            System.out.printf("  %d) %s%n", i + 1, a.getName());
            System.out.println("E" + (i + 1) + " : " + estado);
        }

        System.out.println();
        System.out.println("Nodos explorados: " + r.nodosExplorados);
        System.out.println("Nodos generados : " + r.nodosGenerados);
        System.out.println("¿Objetivo alcanzado? "
            + problema.esObjetivo(r.trayectoria.get(r.trayectoria.size() - 1)));
    }
}

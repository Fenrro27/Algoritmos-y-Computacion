package planning;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilidades para la carga y parsing de problemas de planificación STRIPS/POP.
 *
 * <h3>Formato del fichero de entrada</h3>
 * <pre>
 *   Ei = p1, p2
 *   Ef = p5, p6
 *
 *   Accion = A
 *   P = p1
 *   S =
 *   A = p3, p4
 *
 *   Accion = B
 *   P = p2, p4
 *   S = p3, p5
 *   A = p6
 *
 *   Accion = C
 *   P = p3
 *   S = p3
 *   A = p5
 * </pre>
 *
 * Reglas del formato:
 * <ul>
 *   <li>Las líneas en blanco y los espacios extra se ignoran.</li>
 *   <li><b>Ei</b> — estado inicial: lista de literales separados por coma.</li>
 *   <li><b>Ef</b> — estado final: lista de literales separados por coma.</li>
 *   <li>Cada acción comienza con <code>Accion = &lt;nombre&gt;</code> y va seguida
 *       obligatoriamente de tres líneas en cualquier orden:
 *       <ul>
 *         <li><b>P</b> — precondiciones</li>
 *         <li><b>S</b> — lista de substracción (DEL)</li>
 *         <li><b>A</b> — lista de adición (ADD)</li>
 *       </ul>
 *   </li>
 *   <li>Una lista vacía se representa dejando el valor en blanco tras el '='.</li>
 *   <li>Los literales sin argumentos se escriben sólo con su nombre (ej. {@code p1}).</li>
 * </ul>
 */
public class Util {

    // Constructor privado: clase de utilidades, no se instancia.
    private Util() {}

    // -------------------------------------------------------------------------
    // API pública
    // -------------------------------------------------------------------------

    /**
     * Lee un fichero de problema de planificación y devuelve la instancia
     * correspondiente de {@link Problema_Plan}.
     *
     * @param path ruta absoluta o relativa al fichero de entrada
     * @return problema de planificación construido a partir del fichero
     * @throws IOException              si el fichero no existe o no se puede leer
     * @throws IllegalArgumentException si el formato del fichero es incorrecto
     */
    public static Problema_Plan cargarProblema(String path) throws IOException {

        List<String> lineas = leerLineas(path);

        State        estadoInicial = null;
        State        estadoFinal   = null;
        List<Action> acciones      = new ArrayList<Action>();

        int i = 0;
        while (i < lineas.size()) {
            String linea = lineas.get(i);

            // -- Estado inicial -----------------------------------------------
            if (linea.startsWith("Ei")) {
                estadoInicial = parsearEstado(linea);
                i++;

            // -- Estado final --------------------------------------------------
            } else if (linea.startsWith("Ef")) {
                estadoFinal = parsearEstado(linea);
                i++;

            // -- Acción --------------------------------------------------------
            } else if (linea.startsWith("Accion")) {
                // Nombre de la acción
                String nombre = valorDe(linea);

                // Consumir las tres líneas de la acción (P, S, A)
                // en cualquier orden dentro del bloque
                List<Literal> pre = new ArrayList<Literal>();
                List<Literal> add = new ArrayList<Literal>();
                List<Literal> del = new ArrayList<Literal>();

                i++;
                int camposLeidos = 0;
                while (i < lineas.size() && camposLeidos < 3) {
                    String campo = lineas.get(i);
                    if (campo.startsWith("P")) {
                        pre = parsearLiterales(valorDe(campo));
                        camposLeidos++;
                    } else if (campo.startsWith("S")) {
                        del = parsearLiterales(valorDe(campo));
                        camposLeidos++;
                    } else if (campo.startsWith("A")) {
                        add = parsearLiterales(valorDe(campo));
                        camposLeidos++;
                    }
                    i++;
                }

                if (camposLeidos < 3) {
                    throw new IllegalArgumentException(
                        "La acción '" + nombre + "' no tiene los tres campos P, S y A.");
                }

                acciones.add(new Action(nombre, pre, add, del));

            } else {
                // Línea no reconocida (no debería ocurrir tras limpiar)
                i++;
            }
        }

        // Validación mínima
        if (estadoInicial == null) {
            throw new IllegalArgumentException("El fichero no contiene el estado inicial (Ei).");
        }
        if (estadoFinal == null) {
            throw new IllegalArgumentException("El fichero no contiene el estado final (Ef).");
        }

        return new Problema_Plan(estadoInicial, estadoFinal, acciones);
    }

    // -------------------------------------------------------------------------
    // Métodos privados de parsing
    // -------------------------------------------------------------------------

    /**
     * Lee todas las líneas del fichero, descartando líneas en blanco y
     * normalizando los espacios alrededor del '='.
     *
     * @param path ruta al fichero
     * @return lista de líneas limpias y no vacías
     */
    private static List<String> leerLineas(String path) throws IOException {
        List<String> resultado = new ArrayList<String>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path));
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    resultado.add(linea);
                }
            }
        } finally {
            if (br != null) {
                try { br.close(); } catch (IOException e) { /* ignorar */ }
            }
        }
        return resultado;
    }

    /**
     * Parsea una línea de la forma {@code Clave = v1, v2, ...} y
     * devuelve el estado formado por esos literales.
     *
     * @param linea línea completa (ej. {@code "Ei = p1, p2"})
     * @return estado con los literales de la lista
     */
    private static State parsearEstado(String linea) {
        return new State(parsearLiterales(valorDe(linea)));
    }

    /**
     * Extrae la parte derecha de una línea {@code Clave = valor}.
     *
     * @param linea línea completa
     * @return valor tras el '=', recortado de espacios (puede ser vacío)
     */
    private static String valorDe(String linea) {
        int idx = linea.indexOf('=');
        if (idx < 0) {
            throw new IllegalArgumentException(
                "Línea con formato inesperado (falta '='): " + linea);
        }
        return linea.substring(idx + 1).trim();
    }

    /**
     * Convierte una cadena con literales separados por coma en una
     * lista de objetos {@link Literal}.
     *
     * Cada token se trata como el nombre de un literal proposicional sin
     * argumentos (ej. {@code "p1"} → {@code new Literal("p1")}).
     * Si la cadena está vacía, devuelve una lista vacía.
     *
     * @param valor cadena con los literales (ej. {@code "p1, p2, p3"})
     * @return lista de literales (posiblemente vacía)
     */
    private static List<Literal> parsearLiterales(String valor) {
        List<Literal> lista = new ArrayList<Literal>();
        if (valor == null || valor.trim().isEmpty()) {
            return lista;
        }
        String[] tokens = valor.split(",");
        for (String token : tokens) {
            String nombre = token.trim();
            if (!nombre.isEmpty()) {
                lista.add(new Literal(nombre));
            }
        }
        return lista;
    }

    // -------------------------------------------------------------------------
    // main — prueba de carga
    // -------------------------------------------------------------------------

    /**
     * Prueba de carga desde línea de comandos.
     * Uso:  java planning.Util &lt;path_fichero&gt;
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Uso: java planning.Util <path_fichero>");
            System.exit(1);
        }
        try {
            Problema_Plan p = Util.cargarProblema(args[0]);
            System.out.println(p);
            System.out.println("Subobjetivos pendientes: " + p.subobjetivosPendientes());
        } catch (IOException e) {
            System.err.println("Error de lectura: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error de formato: " + e.getMessage());
        }
    }
}

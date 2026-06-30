import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;

public class Main {
    public static boolean VERBOSE = false;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        File tablerosDir = new File("tableros");
        File solucionesDir = new File("soluciones");
        if (!solucionesDir.exists()) {
            solucionesDir.mkdirs();
        }

        if (tablerosDir.exists() && tablerosDir.isDirectory()) {
            File[] files = tablerosDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".txt")) {
                        processFile(file, new File(solucionesDir, "Sol_" + file.getName()));
                    }
                }
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("\r\ntotal running time: " + (end - start) + "ms");
    }

    private static void processFile(File inFile, File outFile) {
        System.out.println("Procesando: " + inFile.getName());

        final List<String> originalLines;
        final List<String> existingSolLines = new ArrayList<>();
        try {
            originalLines = Files.readAllLines(inFile.toPath());
            if (outFile.exists()) {
                existingSolLines.addAll(Files.readAllLines(outFile.toPath()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        final int totalLinesCount = originalLines.size();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        String[] results = new String[totalLinesCount];
        AtomicInteger resolvedCount = new AtomicInteger(0);
        DoubleAdder totalSolveTimeMs = new DoubleAdder();

        for (int i = 0; i < totalLinesCount; i++) {
            final int index = i;
            final String originalLine = originalLines.get(index);
            final String existingSol = (index < existingSolLines.size()) ? existingSolLines.get(index) : null;
            
            if (originalLine.trim().isEmpty()) {
                results[index] = "";
                continue;
            }

            executor.submit(() -> {
                long startSolve = System.nanoTime();
                
                // 1. Verificar si ya está resuelto y es válido
                if (existingSol != null && isValidSolution(originalLine, existingSol)) {
                    results[index] = existingSol;
                    resolvedCount.incrementAndGet();
                    long endSolve = System.nanoTime();
                    double timeMs = (endSolve - startSolve) / 1_000_000.0;
                    totalSolveTimeMs.add(timeMs);
                    synchronized (System.out) {
                        System.out.println(ANSI_BLUE + "Ya Resuelto [" + (index + 1) + "/" + totalLinesCount + "] (T: " + String.format("%.2f", timeMs) + " ms)" + ANSI_RESET);
                    }
                    return;
                }

                // 2. Si no, resolverlo
                String solution = solveBoard(originalLine);
                long endSolve = System.nanoTime();
                double timeMs = (endSolve - startSolve) / 1_000_000.0;

                if (solution != null) {
                    results[index] = solution;
                    resolvedCount.incrementAndGet();
                    totalSolveTimeMs.add(timeMs);
                    synchronized (System.out) {
                        System.out.println(ANSI_GREEN + "Resuelto [" + (index + 1) + "/" + totalLinesCount + "] (T: " + String.format("%.2f", timeMs) + " ms)" + ANSI_RESET);
                    }
                } else {
                    results[index] = originalLine;
                    synchronized (System.out) {
                        System.out.println(ANSI_RED + "Fallido  [" + (index + 1) + "/" + totalLinesCount + "]" + ANSI_RESET);
                    }
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(outFile))) {
            for (String res : results) {
                if (res != null) writer.println(res);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        double avgTime = resolvedCount.get() > 0 ? totalSolveTimeMs.sum() / resolvedCount.get() : 0;
        System.out.println("\r\n" + ANSI_BLUE + "Resumen para " + inFile.getName() + ":" + ANSI_RESET);
        System.out.println("  - Tableros totales: " + totalLinesCount);
        System.out.println("  - Tableros resueltos: " + resolvedCount.get());
        System.out.println("  - Tiempo medio: " + String.format("%.4f", avgTime) + " ms");
    }


    /**
     * MÉTODO DE CONTROL: Verifica que una solución propuesta sea correcta.
     * Si cambia el problema, se reescribe este método por completo
     * para adaptarlo a las nuevas reglas (ej. comprobar que no haya números repetidos si fuera Sudoku).
     */
    private static boolean isValidSolution(String originalLine, String solutionLine) {
        try {
            // Comparar estructura básica (número de ';' y sumas)
            String[] origParts = originalLine.split(";");
            String[] solParts = solutionLine.split(";");
            if (origParts.length != solParts.length) return false;

            int n = origParts.length - 1;
            int[][] gridValues = new int[n][n];
            int[] rowSums = new int[n];
            int[] colSums = new int[n];

            // Parsear original para obtener valores y sumas
            for (int i = 0; i < n; i++) {
                String[] rowVals = origParts[i].trim().split("\\s+");
                for (int j = 0; j < n; j++) gridValues[i][j] = Integer.parseInt(rowVals[j]);
                rowSums[i] = Integer.parseInt(rowVals[n]);
            }
            String[] colVals = origParts[n].trim().split("\\s+");
            for (int j = 0; j < n; j++) colSums[j] = Integer.parseInt(colVals[j]);

            // Parsear solución y verificar contra original y sumas
            long[] currentColSums = new long[n];
            for (int i = 0; i < n; i++) {
                String[] solVals = solParts[i].trim().split("\\s+");
                long currentRowSum = 0;
                for (int j = 0; j < n; j++) {
                    String s = solVals[j];
                    if (!s.equals(".")) {
                        int val = Integer.parseInt(s);
                        if (val != gridValues[i][j]) return false; // El valor no coincide con el original
                        currentRowSum += val;
                        currentColSums[j] += val;
                    }
                }
                if (currentRowSum != rowSums[i]) return false; // Suma de fila incorrecta
            }

            for (int j = 0; j < n; j++) {
                if (currentColSums[j] != colSums[j]) return false; // Suma de columna incorrecta
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * EL CONSTRUCTOR DEL PROBLEMA (Aquí se concentra el 90% de los cambios si cambia de problema)
     */
    private static String solveBoard(String line) {
        try {
            // Parseo de la línea del fichero
            // Cambia según el formato de entrada que defina el problema.
            // Actualmente asume una matriz N x N con sumas separadas por puntos y comas.
            String[] parts = line.split(";");
            int n = parts.length - 1;
            int[][] grid = new int[n][n];
            int[] rowSums = new int[n];
            int[] colSums = new int[n];

            for (int i = 0; i < n; i++) {
                String[] rowVals = parts[i].trim().split("\\s+");
                for (int j = 0; j < n; j++) {
                    grid[i][j] = Integer.parseInt(rowVals[j]);
                }
                rowSums[i] = Integer.parseInt(rowVals[n]);
            }

            String[] colVals = parts[n].trim().split("\\s+");
            for (int j = 0; j < n; j++) {
                colSums[j] = Integer.parseInt(colVals[j]);
            }

            // Listas que recogen los componentes básicos del motor genérico
            List<Node> allNodes = new ArrayList<>();
            Node[][] nodesGrid = new Node[n][n];
            List<iConstraint> constraints = new ArrayList<>();

            //INSTANCIACIÓN DE LAS NUEVAS RESTRICCIONES
            // Si el problema cambia, se Crearan objetos de 
            // las nuevas clases (ej. AllDifferentConstraint, MenorQueConstraint...)
            SumConstraint[] rowConstraints = new SumConstraint[n];
            for (int i = 0; i < n; i++) {
                rowConstraints[i] = new SumConstraint(rowSums[i], "row", i);
                constraints.add(rowConstraints[i]);
            }

            SumConstraint[] colConstraints = new SumConstraint[n];
            for (int j = 0; j < n; j++) {
                colConstraints[j] = new SumConstraint(colSums[j], "col", j);
                constraints.add(colConstraints[j]);
            }

            // Bucle de creación de Nodos y Vinculación
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    Node node = new Node(i, j, grid[i][j]);
                    nodesGrid[i][j] = node;
                    allNodes.add(node);
                    // VINCULACIÓN BIDIRECCIONAL DINÁMICA
                    // Conecta el nodo con las restricciones que le afectan y viceversa.
                    // Si el juego cambia (ej: Sudoku), aquí se enlazarían también la caja 3x3.
                    // REGLA DE ORO: Si metes la restricción en el nodo, mete el nodo en la restricción.                    
                    node.relatedConstraints.add(rowConstraints[i]);
                    node.relatedConstraints.add(colConstraints[j]);
                    rowConstraints[i].getNodes().add(node);
                    colConstraints[j].getNodes().add(node);
                }
            }

            // A PARTIR DE AQUÍ: Si añades nuevas restricciones en el futuro, solo harías:
            // Constraint nuevaRegla = new MiNuevaRestriccion(nodosSeleccionados);
            // constraints.add(nuevaRegla);
            // por cada nodo: nodo.relatedConstraints.add(nuevaRegla);

            // Propagación AC3 inicial
            AC3 propagator = new AC3(constraints);
            if (!propagator.solve()) return null;

            // Backtracking con AC3
            Backtracking solver = new Backtracking(allNodes, constraints);
            if (solver.solve()) {
                
                // Define cómo se debe formatear el string de texto que se guardará en el fichero.
                // Depende estrictamente de cómo se deba guardar la solución.
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        if (nodesGrid[i][j].isSelected()) {
                            sb.append(nodesGrid[i][j].originalValue).append(" ");
                        } else {
                            sb.append(". ");
                        }
                    }
                    sb.append(rowSums[i]);
                    sb.append(" ; ");
                }
                for (int j = 0; j < n; j++) {
                    sb.append(colSums[j]);
                    if (j < n - 1) sb.append(" ");
                }
                return sb.toString().trim();
            }
        } catch (Exception e) {
            // Error silencioso si el formato del tablero es incorrecto o no tiene solución
        }
        return null;
    }
}

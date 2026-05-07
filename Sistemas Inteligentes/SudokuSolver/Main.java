import java.io.*;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.*;


public class Main {
    static boolean VERBOSE = false;

    // Códigos de color ANSI
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
                        // Prefijo Sol_ para el archivo de salida
                        processFile(file, new File(solucionesDir, "Sol_" + file.getName()));
                    }
                }
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("\r\ntotal running time: " + (end - start) + "ms");
    }

    private static boolean isValidSolution(String orig, String sol) {
        if (sol == null || sol.length() != 81)
            return false;

        // 1. Comprobar que todos son números 1-9 y que los fijos coinciden
        for (int i = 0; i < 81; i++) {
            char o = orig.charAt(i);
            char s = sol.charAt(i);
            if (s < '1' || s > '9')
                return false;
            if (o >= '1' && o <= '9' && o != s)
                return false; // si estaba fijo en el original, debe ser el mismo
        }

        // 2. Comprobar reglas estrictas del Sudoku
        Node[][] nodes = Node.setupSudoku(sol);
        return Node.judgeState(nodes) == 0;
    }

    private static class FailedSudoku {
        String origLine;
        int count;
        long filePointer;

        FailedSudoku(String origLine, int count, long filePointer) {
            this.origLine = origLine;
            this.count = count;
            this.filePointer = filePointer;
        }
    }

    private static void processFile(File inFile, File outFile) {

        System.out.println("Procesando: " + inFile.getName());

        long totalSudokus = 0;
        try {
            totalSudokus = Files.lines(inFile.toPath()).filter(l -> !l.trim().isEmpty()).count();
            if (!outFile.exists()) {
                Files.copy(inFile.toPath(), outFile.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try (BufferedReader reader = new BufferedReader(new FileReader(inFile));
                RandomAccessFile readRaf = new RandomAccessFile(outFile, "r");
                RandomAccessFile writeRaf = new RandomAccessFile(outFile, "rw")) {

            String origLine;
            int count = 0;
            AtomicInteger resolvedCount = new AtomicInteger(0);
            DoubleAdder totalSolveTimeMs = new DoubleAdder();
            ConcurrentLinkedQueue<FailedSudoku> failedSudokus = new ConcurrentLinkedQueue<>();

            long filePointer = readRaf.getFilePointer();
            final long totalSudokusFinal = totalSudokus;

            while ((origLine = reader.readLine()) != null) {
                origLine = origLine.trim();

                String solLine = readRaf.readLine();
                if (solLine != null) {
                    solLine = solLine.trim();
                }

                if (origLine.isEmpty()) {
                    filePointer = readRaf.getFilePointer();
                    continue;
                }

                count++;

                final int currentCount = count;
                final long currentFilePointer = filePointer;
                final String fOrigLine = origLine;
                final String fSolLine = solLine;

                executor.submit(() -> {
                    long startSolve = System.nanoTime();

                    // Comprobar si la línea del archivo de solución ya es un sudoku resuelto y
                    // válido
                    if (isValidSolution(fOrigLine, fSolLine)) {
                        long endSolve = System.nanoTime();
                        double timeMs = (endSolve - startSolve) / 1_000_000.0;

                        synchronized (System.out) {
                            System.out.println(ANSI_YELLOW + "Sin resolver [" + currentCount + "/" + totalSudokusFinal
                                    + "]: " + fOrigLine + ANSI_RESET);
                            System.out.println(ANSI_BLUE + "Resuelto (R) [" + currentCount + "/" + totalSudokusFinal
                                    + "]: " + fSolLine +
                                    " (T: " + String.format("%.2f", timeMs) + " ms)" + ANSI_RESET);
                            System.out.println(
                                    "---------------------------------------------------------------------------------");
                        }
                        resolvedCount.incrementAndGet();
                        totalSolveTimeMs.add(timeMs);

                    } else {
                        // Analizar el sudoku original para recalcularlo
                        Node[][] nodes = Node.setupSudoku(fOrigLine);

                        // --- AC3 ---
                        new AC3(nodes);

                        // Terminar de medir el tiempo
                        long endSolve = System.nanoTime();
                        double timeMs = (endSolve - startSolve) / 1_000_000.0;

                        // Verificar si se pudo resolver completamente
                        if (Node.judgeState(nodes) == 0) {
                            String result = Node.toSudokuString(nodes);
                            totalSolveTimeMs.add(timeMs);

                            synchronized (System.out) {
                                System.out.println(ANSI_YELLOW + "Sin resolver [" + currentCount + "/"
                                        + totalSudokusFinal + "]: " + fOrigLine + ANSI_RESET);
                                System.out.println(ANSI_GREEN + "Resuelto     [" + currentCount + "/"
                                        + totalSudokusFinal + "]: " + result +
                                        " (T: " + String.format("%.2f", timeMs) + " ms)" + ANSI_RESET);
                                System.out.println(
                                        "---------------------------------------------------------------------------------");
                            }

                            synchronized (writeRaf) {
                                try {
                                    writeRaf.seek(currentFilePointer);
                                    writeRaf.writeBytes(result);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            resolvedCount.incrementAndGet();
                        } else {
                            failedSudokus.add(new FailedSudoku(fOrigLine, currentCount, currentFilePointer));
                            synchronized (System.out) {
                                System.out.println(ANSI_YELLOW + "Sin resolver [" + currentCount + "/"
                                        + totalSudokusFinal + "]: " + fOrigLine + ANSI_RESET);
                                System.out.println(ANSI_YELLOW + "Postpuesto para reintento [" + currentCount + "/"
                                        + totalSudokusFinal + "]" +
                                        " (T: " + String.format("%.2f", timeMs) + " ms)" + ANSI_RESET);
                                System.out.println(
                                        "---------------------------------------------------------------------------------");
                            }
                        }
                    }
                });


                // Actualizar el puntero para la siguiente línea
                filePointer = readRaf.getFilePointer();
            }

            // Esperar a que terminen todos los hilos
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);

            // SEGUNDO INTENTO para los fallidos
            if (!failedSudokus.isEmpty()) {
                System.out.println(ANSI_YELLOW + "\r\nIniciando segundo intento para " + failedSudokus.size()
                        + " sudokus..." + ANSI_RESET);
                
                ExecutorService retryExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                List<FailedSudoku> toRetry = new ArrayList<>(failedSudokus);
                failedSudokus.clear();

                for (FailedSudoku failed : toRetry) {
                    retryExecutor.submit(() -> {
                        long startSolve = System.nanoTime();
                        Node[][] nodes = Node.setupSudoku(failed.origLine);
                        new AC3(nodes);
                        long endSolve = System.nanoTime();
                        double timeMs = (endSolve - startSolve) / 1_000_000.0;

                        if (Node.judgeState(nodes) == 0) {
                            String result = Node.toSudokuString(nodes);
                            totalSolveTimeMs.add(timeMs);

                            synchronized (System.out) {
                                System.out.println(ANSI_GREEN + "Resuelto (2º intento) [" + failed.count + "/"
                                        + totalSudokusFinal + "]: " + result +
                                        " (T: " + String.format("%.2f", timeMs) + " ms)" + ANSI_RESET);
                            }

                            synchronized (writeRaf) {
                                try {
                                    writeRaf.seek(failed.filePointer);
                                    writeRaf.writeBytes(result);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            resolvedCount.incrementAndGet();
                        } else {
                            synchronized (System.out) {
                                System.out.println(ANSI_RED + "No se pudo resolver tras 2 intentos [" + failed.count + "/"
                                        + totalSudokusFinal + "]: " + failed.origLine +
                                        " (T: " + String.format("%.2f", timeMs) + " ms)" + ANSI_RESET);
                            }
                        }
                    });
                }
                retryExecutor.shutdown();
                retryExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
            }

            double avgTime = resolvedCount.get() > 0 ? totalSolveTimeMs.sum() / resolvedCount.get() : 0;
            System.out.println("\r\n" + ANSI_BLUE + "Resumen para " + inFile.getName() + ":" + ANSI_RESET);
            System.out.println("  - Sudokus totales: " + totalSudokus);
            System.out.println("  - Sudokus resueltos: " + resolvedCount.get());
            System.out.println("  - Sudokus fallidos: " + (totalSudokus - resolvedCount.get()));
            System.out.println("  - Tiempo medio por resolución: " + String.format("%.4f", avgTime) + " ms");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

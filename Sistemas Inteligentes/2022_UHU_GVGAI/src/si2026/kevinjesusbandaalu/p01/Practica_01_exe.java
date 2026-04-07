package si2026.kevinjesusbandaalu.p01;

import java.util.Random;
import tools.Utils;
import tracks.ArcadeMachine;

public class Practica_01_exe {

    public static void main(String[] args) {

        String p1 = "si2026.kevinjesusbandaalu.p01.Jugador1";
        String spGamesCollection = "examples/all_games_sp.csv";
        String[][] games = Utils.readGames(spGamesCollection);

        boolean visuals = false;
        int gameIdx = 84;
        int partidasPorNivel = 100; 
        
        // Arrays para guardar los resultados de cada uno de los 5 niveles
        int[] victoriasPorNivel = new int[5];
        double[] puntosPorNivel = new double[5];

        System.out.println("Iniciando simulación masiva: 5 niveles x 100 partidas...");

        for (int levelIdx = 0; levelIdx < 5; levelIdx++) {
            System.out.println("\nEjecutando Nivel " + levelIdx + "...");
            
            for (int i = 0; i < partidasPorNivel; i++) {
                int seed = new Random().nextInt();
                String gameName = games[gameIdx][1];
                String gamePath = games[gameIdx][0];
                String levelPath = gamePath.replace(gameName, gameName + "_lvl" + levelIdx);

                // Ejecución
                double[] resultado = ArcadeMachine.runOneGame(gamePath, levelPath, visuals, p1, null, seed, 0);
                
                // resultado[0] es Victoria (1.0 = Win), resultado[1] es Puntuación
                if (resultado[0] >= 1.0) {
                    victoriasPorNivel[levelIdx]++;
                }
                puntosPorNivel[levelIdx] += resultado[1];
                
                // Feedback visual cada 25 partidas para saber que no se ha colgado
                if ((i + 1) % 25 == 0) System.out.println((i + 1) + "% ");
            }
            System.out.println("-> OK");
        }

        // --- GENERACIÓN DEL INFORME FINAL ---
        System.out.println("\n");
System.out.println("==================================================");

        System.out.println("        RESUMEN ESTADÍSTICO POR NIVEL");
System.out.println("==================================================");
        System.out.println(String.format("%-10s | %-12s | %-12s", "NIVEL", "% VICTORIA", "MEDIA PUNTOS"));
        System.out.println("-----------------------------------------------------");

        int victoriasGlobales = 0;
        double puntosGlobales = 0;

        for (int n = 0; n < 5; n++) {
            double winRate = (victoriasPorNivel[n] / (double)partidasPorNivel) * 100;
            double avgScore = puntosPorNivel[n] / partidasPorNivel;
            
            victoriasGlobales += victoriasPorNivel[n];
            puntosGlobales += puntosPorNivel[n];

            System.out.println(String.format("Nivel %d    | %9.2f%%   | %11.2f", n, winRate, avgScore));
        }

        // --- RESUMEN GLOBAL ---
        int totalPartidas = partidasPorNivel * 5;
        double winRateGlobal = (victoriasGlobales / (double)totalPartidas) * 100;
        double scoreGlobal = puntosGlobales / totalPartidas;

        System.out.println("==================================================");
        System.out.println("             RESUMEN GLOBAL");
        System.out.println("==================================================");
        System.out.println("Total Partidas:     " + totalPartidas);
        System.out.println("Victorias Totales:  " + victoriasGlobales);
        System.out.println("Win Rate Global:    " + String.format("%.2f", winRateGlobal) + "%");
        System.out.println("Media Score Global: " + String.format("%.2f", scoreGlobal));
        System.out.println("==================================================");

        System.exit(0);
    }
}
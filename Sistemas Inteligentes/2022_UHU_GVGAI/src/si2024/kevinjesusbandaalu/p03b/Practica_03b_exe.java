package si2024.kevinjesusbandaalu.p03b;

import java.util.Random;

import tools.Utils;
import tracks.ArcadeMachine;

public class Practica_03b_exe {

	public static void main(String[] args) {

		double[] scoreRate = new double[5];
		double[] scoreMean = new double[5];
		double[] scoreMax = new double[5];
		int cont2000 = 0;
		int desclasificaciones = 0;

		for (int i = 0; i < 5; i++) {
			scoreRate[i] = 0;
			scoreMean[i] = 0;
			scoreMax[i] = Double.MIN_VALUE;

		}

		for (int lev = 0; lev < 5; lev++) {
			System.out.println("Nivel: " + lev);

			double[] data = null;

			for (int i = 0; i < 100; i++) {
				String p1 = "si2024.kevinjesusbandaalu.p03b.Jugador3";

				// Load available games
				String spGamesCollection = "examples/all_games_sp.csv";
				String[][] games = Utils.readGames(spGamesCollection);

				// Game settings
				boolean visuals = false;
				int seed = new Random().nextInt();

				
				// Game and level to play
				int gameIdx = 68;
				int levelIdx = lev; // level names from 0 to 4 (game_lvlN.txt).

				String gameName = games[gameIdx][1];
				String game = games[gameIdx][0];
				String level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);

				// 1. This starts a game, in a level, played by a human.
				// data = ArcadeMachine.playOneGame(game, level1, null, seed);

				// 2. This plays a game in a level by the controller.

				data = ArcadeMachine.runOneGame(game, level1, visuals, p1, null, seed, 0);
				if (data[0] == 1)
					scoreRate[lev]++;
				scoreMean[lev] = data[1] + scoreMean[lev];
				if (scoreMax[lev] < data[1]) {
					scoreMax[lev] = data[1];
				}
				if (data[2] == 2000) {
					cont2000++;
				} else {
					if (data[1] == -1000) {
						System.out.println("\nSeed: " + seed+"\n");
						desclasificaciones++;
					}
				}
				data[2] = 0;
			}

		}
		System.out.println("Ejecucion con movimiento aleatorio");
		System.out.println("Nivel----Victorias----Score Medio--Score Max");
		for (int i = 0; i < 5; i++) {
			System.out.println("   " + i + " ---- " + scoreRate[i] + "% ------- "
					+ Math.round(scoreMean[i] / (100) * 100.0) / 100.0 + " ----- " + scoreMax[i]);

		}
		System.out.println("Perdidas mas de 2000 ticks: " + cont2000);
		System.out.println("Perdidas por desclasificacion: " + desclasificaciones);
		System.exit(0);

	}
}

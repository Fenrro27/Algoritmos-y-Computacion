package si2024.kevinjesusbandaalu.p04;

import java.util.ArrayList;
import java.util.List;

import core.game.Observation;
import core.game.StateObservation;
import si2024.kevinjesusbandaalu.common.IMundo;
import si2024.kevinjesusbandaalu.common.aStar.Node;
import tools.Vector2d;

public class Mundo45 implements IMundo {

	public StateObservation stateObs;
	public Vector2d MiPosicion;
	public int Bloque, columnas, filas;
	public char[][] mapa; // Mapa del grid del mundo
	public ArrayList<Observation> dulces;
	public ArrayList<Observation> inmovil;
	public int[][] Muros;

	public Mundo45(StateObservation stateObs) {
		Bloque = stateObs.getBlockSize();
		columnas = stateObs.getWorldDimension().width / Bloque;
		filas = stateObs.getWorldDimension().height / Bloque;

		dulces = new ArrayList<Observation>();
		inmovil = new ArrayList<Observation>();
	}

	@Override
	public void AnalizarEntorno(StateObservation stateObs) {

		dulces = new ArrayList<Observation>();
		inmovil = new ArrayList<Observation>();

		// Obtenemos los datos del mundo
		visualizarMundo(stateObs, false);

		int i = 0;
		int[][] MurosAux = null;
		MurosAux = new int[columnas * filas][2]; // hacemos un array con todas las posiciones del mapa

		for (ArrayList<Observation> celdas : stateObs.getImmovablePositions()) {
			for (Observation o : celdas) {
				switch (o.itype) {
				case 0:// muros
				case 3:
					Vector2d v = o.position.copy();
					MurosAux[i][0] = (int) v.y / Bloque; // Row
					MurosAux[i][1] = (int) v.x / Bloque; // Col
					i++; // si añadimos a los muros
					break;
				default:
					System.out.println("IMo: cat: " + o.category + ", type:" + o.itype);
				}
			}
		}
		// copiamos los verdaderos muros en el array
		Muros = new int[i][2];
		for (int j = 0; j < i; j++) {
			Muros[j][0] = MurosAux[j][0];
			Muros[j][1] = MurosAux[j][1];
		}

	}

	public void visualizarMundo(StateObservation stateObs, boolean ver) {

		mapa = new char[filas][columnas];

		for (ArrayList<Observation>[] filas : stateObs.getObservationGrid()) {
			for (ArrayList<Observation> celdas : filas) {
				for (Observation o : celdas) {
					int posx = (int) o.position.x / Bloque;
					int posy = (int) o.position.y / Bloque;
					switch (o.itype) {
					case 0: // Muros
						mapa[posy][posx] = '#';
						inmovil.add(o);
						break;
					case 4:// Dulce
						mapa[posy][posx] = 'D';
						dulces.add(o);
						break;
					case 3:// Estela
						mapa[posy][posx] = '-';
						inmovil.add(o);
						break;
					case 1:// Personaje
						mapa[posy][posx] = 'Y';
						MiPosicion = new Vector2d(posx, posy);
						break;
					default:
						System.out.println("IMo: cat: " + o.category + ", type:" + o.itype);
						mapa[posy][posx] = '?';

					}
				}
			}
		}

		if (ver) {
			for (int i = 0; i < filas; i++) {
				for (int j = 0; j < columnas; j++) {
					System.out.print(mapa[i][j]);
				}
				System.out.println();
			}
			System.out.println();
			System.out.println();
		}

	}

	public static void visualizarMundo(char[][] map, List<Node> nodos) {
		int f = map.length;
		int c = map[0].length;

	//	System.out.println("Longitud Nodos: " + nodos.size());

		// Creamos un array con las posiciones de los nodos
		for (Node n : nodos) {
			if (map[n.getRow()][n.getCol()] != '#') {
				map[n.getRow()][n.getCol()] = '+';
			}

		}

		for (int i = 0; i < f; i++) {
			for (int j = 0; j < c; j++) {
				System.out.print(map[i][j]);

			}
			System.out.println();
		}
		System.out.println();
		System.out.println();
	}

}

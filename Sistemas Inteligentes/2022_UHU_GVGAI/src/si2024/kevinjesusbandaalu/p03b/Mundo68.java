package si2024.kevinjesusbandaalu.p03b;

import java.util.ArrayList;
import java.util.List;

import core.game.Observation;
import core.game.StateObservation;
import ontology.Types.ACTIONS;
import si2024.kevinjesusbandaalu.common.IMundo;
import tools.Vector2d;

public class Mundo68 implements IMundo {

	public StateObservation stateObs;
	public Vector2d MiPosicion;
	public int Bloque, columnas, filas;
	public int[][] Muros; // Primera dimension para la cantidad y segunda para x o y

	public char[][] mapa; // Mapa del grid del mundo

	public List<Observation> frutas;
	public List<Observation> Fantasmas;
	public List<Observation> FantasmasComibles;
	//public ACTIONS ultimaAccion = null;
	public int pasos = 0;

	public boolean ComerFantasmas = false;

	public Mundo68(StateObservation stateObs) {

		Bloque = stateObs.getBlockSize();
		columnas = stateObs.getWorldDimension().width / Bloque;
		filas = stateObs.getWorldDimension().height / Bloque;

		frutas = new ArrayList<Observation>();
		Fantasmas = new ArrayList<Observation>();
		FantasmasComibles = new ArrayList<Observation>();

		int i = 0;
		int[][] MurosAux = null;

		MurosAux = new int[columnas * filas][2]; // hacemos un array con todas las posiciones del mapa

		for (ArrayList<Observation> celdas : stateObs.getImmovablePositions()) {
			for (Observation o : celdas) {
				switch (o.itype) {
				case 0:// muros
					Vector2d v = o.position.copy();
					MurosAux[i][0] = (int) v.y / Bloque; // Row
					MurosAux[i][1] = (int) v.x / Bloque; // Col
					i++; // si añadimos a los muros
					break;
				case 4: // añadimos las frutas indistintamente de sus efector
				case 5:
				case 6:
					break;
				case 2:
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

		visualizarMundo(stateObs, false);

	}

	@Override
	public void AnalizarEntorno(StateObservation stateObs) {
		frutas = new ArrayList<Observation>();
		Fantasmas = new ArrayList<Observation>();
		FantasmasComibles = new ArrayList<Observation>();
		
		visualizarMundo(stateObs, false);
		//System.out.println("----------------------------------------------------");
		// almacenamos el mundo
		this.stateObs = stateObs.copy();
		// obtenemos mi posicion
		Vector2d Pos = stateObs.getAvatarPosition().copy();
		MiPosicion = new Vector2d(Pos.x / Bloque, Pos.y / Bloque);

		if (stateObs.getAvatarType() == 28) {
			ComerFantasmas = true;
		} else {
			ComerFantasmas = false;
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
					case 0:// muros
						mapa[posy][posx] = '#';
						break;
					case 2: // muros
						// mapa[posy][posx] = '?';
						break;
					case 4:// 4 cerezas
						mapa[posy][posx] = 'c';
						frutas.add(o);
						break;
					case 5: // melones
						mapa[posy][posx] = '.';
						frutas.add(o);
						break;
					case 6: // esferas verdes
						mapa[posy][posx] = 'v';
						frutas.add(o);
						break;
					case 8: // F Rojo
						// mapa[posy][posx] = 'R';
						break;
					case 9: // F Naranja
						// mapa[posy][posx] = 'N';
						break;
					case 10:// F Azul
						// mapa[posy][posx] = 'A';
						break;
					case 11:// F Rosa
						// mapa[posy][posx] = 'S';
						break;
					case 15:// Fantasmas No Comible
					case 21:
					case 18:
					case 24:
						mapa[posy][posx] = 'F';
						Fantasmas.add(o);
						break;
					case 16:// Fantasmas Comibles
					case 19:
					case 22:
					case 25:
						FantasmasComibles.add(o);
						mapa[posy][posx]='f';
						break;
					case 27:// pacman(Yo)
					case 28:
						mapa[posy][posx] = 'y';
						break;
					default:
						System.out.println("IMo: cat: " + o.category + ", type:" + o.itype);

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
		}

	}
}

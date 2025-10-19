package si2024.kevinjesusbandaalu.p03;

import java.util.ArrayList;
import java.util.List;

import core.game.Observation;
import core.game.StateObservation;
import si2024.kevinjesusbandaalu.common.IMundo;
import tools.Vector2d;

public class Mundo53 implements IMundo {

	public StateObservation stateObs;
	public Vector2d MiPosicion;
	public int Bloque, columnas, filas;
	public int[][] Muros; // Primera dimension para la cantidad y segunda para x o y
	public int[][] MapNPCs;

	public List<Observation> monjas;
	public List<Observation> infectados;
	public List<Observation> sanos;
	public List<Observation> virus;
	public boolean estoyInfectado;
	public char ultimaDireccion=' ';

	public Mundo53(StateObservation stateObs) {

		Bloque = stateObs.getBlockSize();
		columnas = stateObs.getWorldDimension().width / Bloque;
		filas = stateObs.getWorldDimension().height / Bloque;

		monjas = new ArrayList<Observation>();
		infectados = new ArrayList<Observation>();
		sanos = new ArrayList<Observation>();
		virus = new ArrayList<Observation>();
	
		estoyInfectado = false;

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
				case 5:// virus
					virus.add(o);
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

	@Override
	public void AnalizarEntorno(StateObservation stateObs) {
		//reseteamos los arrayList
		monjas = new ArrayList<Observation>();
		infectados = new ArrayList<Observation>();
		sanos = new ArrayList<Observation>();
		virus = new ArrayList<Observation>();
		
		// almacenamos el mundo
		this.stateObs = stateObs.copy();
		// obtenemos mi posicion
		Vector2d Pos = stateObs.getAvatarPosition().copy();
		MiPosicion = new Vector2d(Pos.x / Bloque, Pos.y / Bloque);

		//vemos si estoy infectado
		if (stateObs.getAvatarType() == 8) {
			estoyInfectado = true;
		} else {
			estoyInfectado = false;
		}

		// inicializamos el mapa con los NPCs
		MapNPCs = new int[filas][columnas];

		for (ArrayList<Observation> celdas : stateObs.getNPCPositions()) {
			for (Observation npc : celdas) {
				MapNPCs[(int) npc.position.y / Bloque][(int) npc.position.x / Bloque] += 1;// aumentamos esa posicion en

				switch (npc.itype) {
				case 10: // npc sano
					sanos.add(npc);
					break;
				case 11: // npc infectados
					infectados.add(npc);
					break;
				case 12: // monja
					monjas.add(npc);
					break;
				default:
					System.out.println("NPC: cat: " + npc.category + ", type:" + npc.itype);

				}

			}
		}

		for (ArrayList<Observation> celdas : stateObs.getImmovablePositions()) {
			for (Observation o : celdas) {
				switch (o.itype) {
				case 0:// muros, ya estaban almacenados no los guardamos
					break;
				case 5:// virus
					virus.add(o);
					break;
				default:
					System.out.println("IMo: cat: " + o.category + ", type:" + o.itype);

				}
			}
		}

	}

	/*
	 * muro = 0; jugador infectado = 8; sin ifectar 7 virus = 5
	 * 
	 */

}

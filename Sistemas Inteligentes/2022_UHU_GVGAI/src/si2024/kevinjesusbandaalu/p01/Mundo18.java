package si2024.kevinjesusbandaalu.p01;

import java.util.ArrayList;
import java.util.List;

import core.game.Observation;
import core.game.StateObservation;
import si2024.kevinjesusbandaalu.common.IMundo;
import tools.Vector2d;

public class Mundo18 implements IMundo {

	public List<Observation> AguilasBlancas;
	public List<Observation> AguilasNegras;
	public StateObservation stateObs;
	public Vector2d MiPosicion;
	public int Bloque, columnas, filas;
	public int[][] Muros; // Primera dimension para la cantidad y segunda para x o y
	public int[][] MapNPCs;
	public List<Observation> Gusanos;

	public Mundo18(StateObservation stateObs) {
		Bloque = stateObs.getBlockSize();
		columnas = stateObs.getWorldDimension().width / Bloque;
		filas = stateObs.getWorldDimension().height / Bloque;

		int i = 0;
		for (ArrayList<Observation> celdas : stateObs.getImmovablePositions()) {
			Muros = new int[celdas.size()][2];
			for (Observation o : celdas) {
				Vector2d v = o.position.copy();
				Muros[i][0] = (int) v.y / Bloque; // Row
				Muros[i][1] = (int) v.x / Bloque; // Col

				// System.out.println("Murito " + i + ": PosX: " + Muros[i][1] + ", PosY: " +
				// Muros[i][0]);

				i++;
			}
		}

	}

	@Override
	public void AnalizarEntorno(StateObservation stateObs) {

		this.stateObs = stateObs.copy();
		AguilasBlancas = new ArrayList<Observation>();
		AguilasNegras = new ArrayList<Observation>();
		Gusanos = new ArrayList<Observation>();
		Vector2d Pos = stateObs.getAvatarPosition().copy();
		MiPosicion = new Vector2d(Pos.x / Bloque, Pos.y / Bloque);
		MapNPCs = new int[filas][columnas];// inicializamos el mapa con los NPCs

		for (ArrayList<Observation> celdas : stateObs.getNPCPositions()) {
			for (Observation npc : celdas) {
				MapNPCs[(int) npc.position.y / Bloque][(int) npc.position.x / Bloque] += 1;// aumentamos esa posicion en
																							// 1
				if (npc.itype == 6) {
					AguilasBlancas.add(npc);
				} else {
					AguilasNegras.add(npc);
				}
			}
		}
	

		// El itype del gusano es 3

		for (ArrayList<Observation> celdas : stateObs.getImmovablePositions()) {
			for (Observation Objeto : celdas) {
				if (Objeto.itype == 3) {
					Gusanos.add(Objeto);
					MapNPCs[(int) Objeto.position.y / Bloque][(int) Objeto.position.x / Bloque] = -1;
					}
					else //muros
						MapNPCs[(int) Objeto.position.y / Bloque][(int) Objeto.position.x / Bloque] = -13;
				
			}
		}

	}

}

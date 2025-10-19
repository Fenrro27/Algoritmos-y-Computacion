package si2024.kevinjesusbandaalu.p01.reglas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import core.game.Observation;
import ontology.Types.ACTIONS;
import si2024.kevinjesusbandaalu.common.IAccion;
import si2024.kevinjesusbandaalu.common.IMundo;
import si2024.kevinjesusbandaalu.common.aStar.AStar;
import si2024.kevinjesusbandaalu.common.aStar.Node;
import si2024.kevinjesusbandaalu.p01.Mundo18;
import tools.Vector2d;

public class Cazar_Accion implements IAccion {

	private Mundo18 xana;

	@Override
	public ACTIONS doAction(IMundo m) {

		// Buscar el aguila blanca mas cercana e ir a por ella esquivando con un radio
		// de seguridad las aguilas negras

		// Controlar que si me pilla cerca el aguila negra tengo q evitarlax
		xana = ((Mundo18) m);

		double dMin = Double.MAX_VALUE;
		Vector2d Objetivo = null, gusanoEnPeligro = null;

		// Buscamos el mejor aguila para atacar

		for (Observation o : xana.AguilasBlancas) {

			double d = xana.MiPosicion.dist(o.position);

			if (d < dMin) {
				Objetivo = new Vector2d(o.position.x / xana.Bloque, o.position.y / xana.Bloque);
				dMin = d;
			}
		}

		if (xana.Gusanos.size() != 0  && dMin>=3) {
			for (Observation blanca : xana.AguilasBlancas) {
				for (Observation gus : xana.Gusanos) {
					double d = gus.position.dist(blanca.position); // Calculamos la distancia entre los gusanos y las
																	// aguilas
					// vemos el aguila que esta mas cerca de un gusano
					if (d < dMin ) {
						Objetivo = new Vector2d(blanca.position.x / xana.Bloque, blanca.position.y / xana.Bloque);
						// gusanoEnPeligro = new Vector2d(gus.position.x / xana.Bloque, gus.position.y /
						// xana.Bloque);
						dMin = d;
					}

				}
			}

		}

		// Buscamos las aguilass negras y las hacemos muros
		int[][] MurosAux = new int[xana.Muros.length + xana.AguilasNegras.size()][2];

		int i;
		for (i = 0; i < xana.Muros.length; i++) {
			MurosAux[i][0] = xana.Muros[i][0];
			MurosAux[i][1] = xana.Muros[i][1];
		}

		for (Observation o : xana.AguilasNegras) {

			double d = xana.MiPosicion.dist(o.position);

			Vector2d v = o.position.copy();
			MurosAux[i][0] = (int) v.y / xana.Bloque; // Row
			MurosAux[i][1] = (int) v.x / xana.Bloque; // Col

			i++;
		}

		if (dMin == 1) {

		}

		// Buscamos el camino al aguila mas cercana
		if (Objetivo == null) {
			return ACTIONS.ACTION_NIL;
		}

		List<Node> camino;

		camino = BuscarCamino(xana.MiPosicion, Objetivo, MurosAux);

		if (camino.size() != 0)
			return AccionMoverse(xana.MiPosicion, new Vector2d(camino.get(1).getCol(), camino.get(1).getRow()));
		else
			return ACTIONS.ACTION_NIL;
	}

	private List<Node> BuscarCamino(Vector2d inicio, Vector2d Objetivo, int[][] Muros) {
		// Buscamos el camino
		Node ini = new Node(xana.MiPosicion); // ya esta con /Bloque
		Node fin = new Node(Objetivo);
		AStar aStar = new AStar(xana.filas, xana.columnas, ini, fin);
		aStar.setBlocks(Muros);
		return aStar.findPath();
	}

	private ACTIONS AccionMoverse(Vector2d miPosicion, Vector2d nuevaPosicion) {

		int posX = (int) miPosicion.x - (int) nuevaPosicion.x;
		int posY = (int) miPosicion.y - (int) nuevaPosicion.y;
		// System.out.println("Posicion a la que moverse: "+nuevaPosicion);
		// System.out.println("PosX: "+posX+", PosY: "+posY);

		if (xana.MapNPCs[(int) nuevaPosicion.y][(int) nuevaPosicion.x] > 1) {
			return ACTIONS.ACTION_NIL;
		}

		// Movimiento a la derecha -1,0
		if (posX == -1 && posY == 0) {
			return ACTIONS.ACTION_RIGHT;
		}
		// Movimiento a la izquierda 1,0
		if (posX == 1 && posY == 0) {
			return ACTIONS.ACTION_LEFT;
		}
		// movimiento hacia arriba 0,1
		if (posX == 0 && posY == 1) {
			return ACTIONS.ACTION_UP;
		}
		// movimiento hacia abajo 0,-1
		if (posX == 0 && posY == -1) {
			return ACTIONS.ACTION_DOWN;
		}

		System.out.println("Fallo al ejecutar accion");
		return ACTIONS.ACTION_NIL;

	}

}

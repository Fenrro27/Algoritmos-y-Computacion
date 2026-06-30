package si2024.kevinjesusbandaalu.p03.reglas;

import java.util.List;

import core.game.Observation;
import ontology.Types.ACTIONS;
import si2024.kevinjesusbandaalu.common.IAccion;
import si2024.kevinjesusbandaalu.common.IMundo;
import si2024.kevinjesusbandaalu.common.aStar.AStar;
import si2024.kevinjesusbandaalu.common.aStar.Node;
import si2024.kevinjesusbandaalu.p03.Mundo53;
import tools.Vector2d;

public class Infectarse_Accion implements IAccion {

	Mundo53 xana;

	@Override
	public ACTIONS doAction(IMundo m) {
		// System.out.println("Accion infectarse");

		xana = (Mundo53) m;
		double dMin = Double.MAX_VALUE;
		Vector2d Objetivo = null;
		List<Node> camino = null;

		for (Observation o : xana.virus) {

			double d = xana.MiPosicion.dist(o.position);

			if (d < dMin) {
				Objetivo = new Vector2d(o.position.x / xana.Bloque, o.position.y / xana.Bloque);
				dMin = d;
			}
		}
		if (Objetivo == null) {
			return ACTIONS.ACTION_NIL;
		}
		camino = BuscarCamino(xana.MiPosicion, Objetivo, xana.Muros);

		if (camino.size() == 0) {
			for (Observation o : xana.virus) {
				double d;
				Vector2d ObjetivoAux = new Vector2d(o.position.x / xana.Bloque, o.position.y / xana.Bloque);

				List<Node> caminoAux = BuscarCamino(xana.MiPosicion, ObjetivoAux, xana.Muros);
				if (!caminoAux.isEmpty()) {
					d = caminoAux.size();
					if (dMin > d) {
						dMin = d;
						Objetivo = new Vector2d(o.position.x / xana.Bloque, o.position.y / xana.Bloque);
						camino = caminoAux;
					}

				}

			}

		}

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

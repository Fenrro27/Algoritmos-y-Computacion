package si2024.kevinjesusbandaalu.p04.reglas;

import java.util.ArrayList;
import java.util.List;

import core.game.Observation;
import ontology.Types.ACTIONS;
import si2024.kevinjesusbandaalu.common.IAccion;
import si2024.kevinjesusbandaalu.common.IMundo;
import si2024.kevinjesusbandaalu.common.aStar.AStar;
import si2024.kevinjesusbandaalu.common.aStar.Node;
import si2024.kevinjesusbandaalu.p04.Mundo45;
import tools.Vector2d;

public class Comer_Accion implements IAccion {
	Mundo45 xana;
	int nCalculos = 0;

	public Comer_Accion() {

	}

	@Override
	public ACTIONS doAction(IMundo m) {
		xana = (Mundo45) m;

		// System.out.println("Calculamos camino a nodo "+nCalculos++);
		Vector2d Objetivo = null;
		double dMin = Double.MAX_VALUE;

		for (Observation o : xana.dulces) {

			Vector2d vAux = new Vector2d(o.position.x / xana.Bloque, o.position.y / xana.Bloque);
			double d = xana.MiPosicion.dist(vAux);
			if (dMin > d) {
				dMin = d;
				Objetivo = vAux;
			}

		}

		// Busqueda A*

		List<Node> camino;

		camino = BuscarCamino(xana.MiPosicion, Objetivo, xana.Muros);

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

	/*
	 * Operadores
	 */
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

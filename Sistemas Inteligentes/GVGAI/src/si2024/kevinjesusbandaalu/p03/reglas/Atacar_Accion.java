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

public class Atacar_Accion implements IAccion {

	Mundo53 xana;

	@Override
	public ACTIONS doAction(IMundo m) {
		// System.out.println("Accion atacar");
		xana = (Mundo53) m;
		double dMin = Double.MAX_VALUE;
		Vector2d Objetivo = null;

		for (Observation o : xana.monjas) {

			double d = xana.MiPosicion.dist(o.position);

			if (d < dMin) {
				Objetivo = new Vector2d(o.position.x / xana.Bloque, o.position.y / xana.Bloque);
				dMin = d;
			}
		}

		if (Objetivo == null) {
			return ACTIONS.ACTION_NIL;
		}
		if (Objetivo.dist(xana.MiPosicion) <= 1) {

			// Apuntamos a donde miramos por ultima vez

			int posX = (int) (xana.MiPosicion.x - Objetivo.x);
			int posY = (int) (xana.MiPosicion.y - Objetivo.y);

			// Movimiento a la derecha -1,0
			if (posX == -1 && posY == 0 && xana.ultimaDireccion!='R') {
				xana.ultimaDireccion = 'R';
				return ACTIONS.ACTION_RIGHT;
			}
			// Movimiento a la izquierda 1,0
			if (posX == 1 && posY == 0 && xana.ultimaDireccion!='L') {
				xana.ultimaDireccion = 'L';
				return ACTIONS.ACTION_LEFT;
			}
			// movimiento hacia arriba 0,1
			if (posX == 0 && posY == 1 && xana.ultimaDireccion!='U') {
				xana.ultimaDireccion = 'U';
				return ACTIONS.ACTION_UP;
			}
			// movimiento hacia abajo 0,-1
			if (posX == 0 && posY == -1 && xana.ultimaDireccion!='D') {
				xana.ultimaDireccion = 'D';
				return ACTIONS.ACTION_DOWN;
			}

			// si ya estamos mirando al sitio correcto atacamos
			return ACTIONS.ACTION_USE;
		}

		List<Node> camino;

		camino = BuscarCamino(xana.MiPosicion, Objetivo, xana.Muros);

		if (camino.size() != 0) {
			if (camino.size() > 1) {
				return AccionMoverse(xana.MiPosicion, new Vector2d(camino.get(1).getCol(), camino.get(1).getRow()));
			} else {
				return ACTIONS.ACTION_NIL;
			}
		} else
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
			xana.ultimaDireccion = 'R';
			return ACTIONS.ACTION_RIGHT;
		}
		// Movimiento a la izquierda 1,0
		if (posX == 1 && posY == 0) {
			xana.ultimaDireccion = 'L';
			return ACTIONS.ACTION_LEFT;
		}
		// movimiento hacia arriba 0,1
		if (posX == 0 && posY == 1) {
			xana.ultimaDireccion = 'U';
			return ACTIONS.ACTION_UP;
		}
		// movimiento hacia abajo 0,-1
		if (posX == 0 && posY == -1) {
			xana.ultimaDireccion = 'D';
			return ACTIONS.ACTION_DOWN;
		}

		System.out.println("Fallo al ejecutar accion");
		return ACTIONS.ACTION_NIL;

	}

}

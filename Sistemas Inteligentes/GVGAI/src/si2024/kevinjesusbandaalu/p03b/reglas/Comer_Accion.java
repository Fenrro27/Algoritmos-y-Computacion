package si2024.kevinjesusbandaalu.p03b.reglas;

import java.awt.Point;
import java.util.List;

import javax.swing.text.Position;

import core.game.Observation;
import ontology.Types.ACTIONS;
import si2024.kevinjesusbandaalu.common.IAccion;
import si2024.kevinjesusbandaalu.common.IMundo;
import si2024.kevinjesusbandaalu.common.aStar.AStar;
import si2024.kevinjesusbandaalu.common.aStar.Node;
import si2024.kevinjesusbandaalu.p03b.Mundo68;
import tools.Vector2d;

public class Comer_Accion implements IAccion {
	Mundo68 xana;
	List<Node> camino;
	Vector2d Objetivo;
	
	@Override
	public ACTIONS doAction(IMundo m) {
		//System.out.println("Comiendo");
		xana = (Mundo68) m;

		if (xana.pasos == 0) {
			
			xana.pasos++;
			double dMin = Double.MAX_VALUE;
			Objetivo = null;

			for (Observation o : xana.frutas) {
				
				double d = xana.stateObs.getAvatarPosition().dist(o.position);					
				if (d < dMin ) {
					Objetivo = new Vector2d(o.position.x / xana.Bloque, o.position.y / xana.Bloque);
					dMin = d;
				}
			}
			
			camino = BuscarCamino(xana.MiPosicion, Objetivo, xana.Muros);

			if (camino.size() > 1) {
				return AccionMoverse(xana.MiPosicion, new Vector2d(camino.get(1).getCol(), camino.get(1).getRow()));
			} else {
				return ACTIONS.ACTION_NIL;
			}

		} else {
			xana.pasos = 0;
			return xana.stateObs.getAvatarLastAction();
		}

	}

	private List<Node> BuscarCamino(Vector2d inicio, Vector2d Objetivo, int[][] Muros) {
		// Buscamos el camino
		Node ini = new Node(xana.MiPosicion); // ya esta con /Bloque
		Node fin = new Node(Objetivo);
		AStar aStar = new AStar(xana.filas, xana.columnas, ini, fin);
		
		Node[][] areaBusqueda = aStar.getSearchArea();
		for(int i =0; i<xana.filas; i++) {
			for(int j=0; j<xana.columnas; j++) {
				if(xana.mapa[i][j]=='F') {
					areaBusqueda[i][j].setH(areaBusqueda[i][j].getG() + 100000);
				}
			}
		}
		aStar.setSearchArea(areaBusqueda);
		
		
		aStar.setBlocks(Muros);
		return aStar.findPath();
	}

	private ACTIONS AccionMoverse(Vector2d miPosicion, Vector2d nuevaPosicion) {

		int posX = (int) miPosicion.x - (int) nuevaPosicion.x;
		int posY = (int) miPosicion.y - (int) nuevaPosicion.y;
		//System.out.println("Posicion a la que moverse: " + nuevaPosicion);
		//System.out.println("PosX: " + posX + ", PosY: " + posY);

		// Movimiento a la derecha -1,0
		if (posX == -1 && posY == 0) {
			//xana.ultimaAccion = ACTIONS.ACTION_RIGHT;
			return ACTIONS.ACTION_RIGHT;
		}
		// Movimiento a la izquierda 1,0
		if (posX == 1 && posY == 0) {
			//xana.ultimaAccion = ACTIONS.ACTION_LEFT;
			return ACTIONS.ACTION_LEFT;
		}
		// movimiento hacia arriba 0,1
		if (posX == 0 && posY == 1) {
			//xana.ultimaAccion = ACTIONS.ACTION_UP;
			return ACTIONS.ACTION_UP;
		}
		// movimiento hacia abajo 0,-1
		if (posX == 0 && posY == -1) {
			//xana.ultimaAccion = ACTIONS.ACTION_DOWN;
			return ACTIONS.ACTION_DOWN;
		}

		System.out.println("Fallo al ejecutar accion");
		return ACTIONS.ACTION_NIL;

	}

}

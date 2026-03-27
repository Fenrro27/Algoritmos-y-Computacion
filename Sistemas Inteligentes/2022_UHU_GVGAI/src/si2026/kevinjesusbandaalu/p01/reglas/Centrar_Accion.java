package si2026.kevinjesusbandaalu.p01.reglas;

import java.util.List;
import ontology.Types.ACTIONS;
import si2026.kevinjesusbandaalu.common.IAccion;
import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.aStar.AStar;
import si2026.kevinjesusbandaalu.common.aStar.Node;
import si2026.kevinjesusbandaalu.p01.Mundo84;
import core.game.Observation;
import tools.Vector2d;

public class Centrar_Accion implements IAccion {

	private Mundo84 mundo;

	@Override
	public ACTIONS doAction(IMundo m) {
		this.mundo = (Mundo84) m;

		// 1. Definir el objetivo (el centro del mapa)
		Vector2d objetivoCentro = new Vector2d((int)(mundo.columnas / 2), (int)(mundo.filas / 2));

		// 2. Crear mapa de obstáculos dinámicos (Láseres)
		// Convertimos los láseres actuales en posiciones bloqueadas para el A*
		int size = mundo.objetosIntocables.size();
		int[][] obstaculos = new int[size][2];
		for (int i = 0; i < size; i++) {
			Observation l = mundo.objetosIntocables.get(i);
			obstaculos[i][0] = (int) (l.position.y / mundo.Bloque); // Fila (Row)
			obstaculos[i][1] = (int) (l.position.x / mundo.Bloque); // Columna (Col)
		}

		// 3. Buscar camino con A*
		List<Node> camino = buscarCamino(mundo.MiPosicion, objetivoCentro, obstaculos);

		// 4. Ejecutar el primer paso del camino
		if (camino != null && camino.size() > 1) {
			Node siguientePaso = camino.get(1); // El índice 0 es la posición actual
			return determinarAccion(mundo.MiPosicion, new Vector2d(siguientePaso.getCol(), siguientePaso.getRow()));
		}

		return ACTIONS.ACTION_NIL;
	}

	private List<Node> buscarCamino(Vector2d inicio, Vector2d fin, int[][] bloques) {
		Node nodoInicial = new Node(inicio);
		Node nodoFinal = new Node(fin);
		
		AStar aStar = new AStar(mundo.filas, mundo.columnas, nodoInicial, nodoFinal);
		aStar.setBlocks(bloques);
		
		return aStar.findPath();
	}

	private ACTIONS determinarAccion(Vector2d actual, Vector2d siguiente) {
		int diffX = (int) siguiente.x - (int) actual.x;
		int diffY = (int) siguiente.y - (int) actual.y;

		if (diffX == 1)  return ACTIONS.ACTION_RIGHT;
		if (diffX == -1) return ACTIONS.ACTION_LEFT;
		if (diffY == 1)  return ACTIONS.ACTION_DOWN;
		if (diffY == -1) return ACTIONS.ACTION_UP;

		return ACTIONS.ACTION_NIL;
	}
}
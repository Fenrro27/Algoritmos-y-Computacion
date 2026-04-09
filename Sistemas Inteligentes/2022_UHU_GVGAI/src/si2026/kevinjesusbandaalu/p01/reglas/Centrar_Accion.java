package si2026.kevinjesusbandaalu.p01.reglas;

import java.util.List;
import ontology.Types.ACTIONS;
import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.MotorReglas.IAccion;
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
		Observation enemigo = mundo.enemigoMasCercano;
		Vector2d objetivo = null;

		if (enemigo != null) {
			// 1. Obtener coordenadas del enemigo en celdas [cite: 148, 149]
			int ex = (int) (enemigo.position.x / mundo.Bloque);
			int ey = (int) (enemigo.position.y / mundo.Bloque);

			// 2. Buscar la casilla libre más cercana a 2 bloques (Sentido Antihorario)
			objetivo = buscarPuntoAcechoValido(ex, ey);
		}

		// 3. Si no hay objetivo válido o no hay enemigos, ir al centro [cite: 153]
		if (objetivo == null) {
			objetivo = new Vector2d(mundo.columnas / 2, mundo.filas / 2);
		}

		// 4. Preparar el mapa de obstáculos para A* [cite: 155, 156]
		int[][] obstaculos = extraerObstaculos();

		// 5. Calcular camino con A* [cite: 162]
		List<Node> camino = buscarCamino(mundo.MiPosicion, objetivo, obstaculos);

		// 6. Determinar acción basada en el primer paso del camino [cite: 159, 160]
		if (camino != null && camino.size() > 1) {
			Node siguientePaso = camino.get(1);
			return determinarAccion(mundo.MiPosicion, new Vector2d(siguientePaso.getCol(), siguientePaso.getRow()));
		}

		return ACTIONS.ACTION_NIL;
	}

	/**
	 * Busca una casilla transitable alrededor del enemigo a una distancia de 2
	 * bloques. Prioriza el orden antihorario para mejorar el posicionamiento.
	 */
	private Vector2d buscarPuntoAcechoValido(int ex, int ey) {
		// Desplazamientos a 2 bloques de distancia en sentido antihorario
		int[][] patrulla = { { 2, 0 }, { 2, -1 }, { 2, -2 }, { 1, -2 }, { 0, -2 }, { -1, -2 }, { -2, -2 }, { -2, -1 },
				{ -2, 0 }, { -2, 1 }, { -2, 2 }, { -1, 2 }, { 0, 2 }, { 1, 2 }, { 2, 2 }, { 2, 1 } };

		for (int[] p : patrulla) {
			int tx = ex + p[0];
			int ty = ey + p[1];

			// Validar límites y que no sea un objeto intocable
			if (tx >= 0 && tx < mundo.columnas && ty >= 0 && ty < mundo.filas) {
				if (!esPosicionBloqueada(tx, ty)) {
					return new Vector2d(tx, ty);
				}
			}
		}
		return null;
	}

	private int[][] extraerObstaculos() {
		int size = mundo.objetosIntocables.size();
		int[][] obs = new int[size][2];
		for (int i = 0; i < size; i++) {
			Observation o = mundo.objetosIntocables.get(i);
			obs[i][0] = (int) (o.position.y / mundo.Bloque);
			obs[i][1] = (int) (o.position.x / mundo.Bloque);
		}
		return obs;
	}

	private boolean esPosicionBloqueada(int x, int y) {
		for (Observation obs : mundo.objetosIntocables) {
			int ox = (int) (obs.position.x / mundo.Bloque);
			int oy = (int) (obs.position.y / mundo.Bloque);
			if (ox == x && oy == y)
				return true;
		}
		return false;
	}

	private List<Node> buscarCamino(Vector2d inicio, Vector2d fin, int[][] bloques) {
		Node nIni = new Node(inicio);
		Node nFin = new Node(fin);
		AStar aStar = new AStar(mundo.filas, mundo.columnas, nIni, nFin);
		aStar.setBlocks(bloques);
		return aStar.findPath();
	}

	private ACTIONS determinarAccion(Vector2d actual, Vector2d siguiente) {
		int dx = (int) siguiente.x - (int) actual.x;
		int dy = (int) siguiente.y - (int) actual.y;
		if (dx == 1)
			return ACTIONS.ACTION_RIGHT;
		if (dx == -1)
			return ACTIONS.ACTION_LEFT;
		if (dy == 1)
			return ACTIONS.ACTION_DOWN;
		if (dy == -1)
			return ACTIONS.ACTION_UP;
		return ACTIONS.ACTION_NIL;
	}


}
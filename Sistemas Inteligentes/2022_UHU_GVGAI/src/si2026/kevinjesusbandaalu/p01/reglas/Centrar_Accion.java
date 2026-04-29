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

		// 3. Si no hay objetivo válido o no hay enemigos, ir al centro del mapa (búnker)
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
	 * Busca una casilla transitable ALINEADA con el enemigo a una distancia
	 * prudencial
	 * (hasta 5 bloques) para que se active rápidamente la regla Disparar.
	 */
	private Vector2d buscarPuntoAcechoValido(int ex, int ey) {
		int mx = (int) mundo.MiPosicion.x;
		int my = (int) mundo.MiPosicion.y;

		// 1. Intentar moverse solo en un eje para alinearse (puntos de intersección)
		Vector2d p1 = new Vector2d(mx, ey); // Moverse verticalmente para alinearse en Y
		Vector2d p2 = new Vector2d(ex, my); // Moverse horizontalmente para alinearse en X

		boolean p1Valido = mx >= 0 && mx < mundo.columnas && ey >= 0 && ey < mundo.filas && !esPosicionBloqueada(mx, ey);
		boolean p2Valido = ex >= 0 && ex < mundo.columnas && my >= 0 && my < mundo.filas && !esPosicionBloqueada(ex, my);

		if (p1Valido && p2Valido) {
			// Ambos son válidos, elegir el que requiere caminar menos
			return (Math.abs(ey - my) < Math.abs(ex - mx)) ? p1 : p2;
		} else if (p1Valido) {
			return p1;
		} else if (p2Valido) {
			return p2;
		}

		// 2. Si las intersecciones directas están bloqueadas, usar desplazamientos cercanos a las intersecciones
		// ... o si no, usar el respaldo cerca del enemigo
		int[][] patrulla = {
				{ 0, -3 }, { 0, 3 }, { -3, 0 }, { 3, 0 },
				{ 0, -4 }, { 0, 4 }, { -4, 0 }, { 4, 0 },
				{ 0, -5 }, { 0, 5 }, { -5, 0 }, { 5, 0 },
				{ 0, -2 }, { 0, 2 }, { -2, 0 }, { 2, 0 },
				{ 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 }
		};

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
package si2026.kevinjesusbandaalu.p01.reglas;

import java.util.HashMap;
import java.util.Map;
import core.game.Observation;
import ontology.Types.ACTIONS;
import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.MotorReglas.IAccion;
import si2026.kevinjesusbandaalu.p01.Mundo84;
import tools.Vector2d;

public class Esquivar_Accion implements IAccion {

	private Map<ACTIONS, Vector2d> movimientos;
	
	public Esquivar_Accion() {
		movimientos = new HashMap<>();
		movimientos.put(ACTIONS.ACTION_UP, new Vector2d(0, -1));
		movimientos.put(ACTIONS.ACTION_DOWN, new Vector2d(0, 1));
		movimientos.put(ACTIONS.ACTION_LEFT, new Vector2d(-1, 0));
		movimientos.put(ACTIONS.ACTION_RIGHT, new Vector2d(1, 0));
	}
	
	@Override
	public ACTIONS doAction(IMundo m) {
		Mundo84 mundo = (Mundo84) m;
		
		if (mundo.lasers == null || mundo.lasers.isEmpty()) return ACTIONS.ACTION_NIL;

		int miX = (int) mundo.MiPosicion.x;
		int miY = (int) mundo.MiPosicion.y;

		// 1. Encontrar la bala más cercana que nos amenaza actualmente
		Observation balaAmenaza = getBalaMasCercanaEnTrayectoria(mundo, miX, miY);
		if (balaAmenaza == null) return ACTIONS.ACTION_NIL;

		int lx = (int)(balaAmenaza.position.x / mundo.Bloque);
		int ly = (int)(balaAmenaza.position.y / mundo.Bloque);

		ACTIONS mejorAccion = ACTIONS.ACTION_NIL;
		double mejorPuntuacion = -Double.MAX_VALUE;

		// 2. Evaluar cada movimiento posible
		for (Map.Entry<ACTIONS, Vector2d> mov : movimientos.entrySet()) {
			int futX = miX + (int) mov.getValue().x;
			int futY = miY + (int) mov.getValue().y;

			// Filtros básicos: límites y muros
			if (futX < 0 || futX >= mundo.columnas || futY < 0 || futY >= mundo.filas) continue;
			if (esPosicionIntocable(mundo, futX, futY)) continue;

			double puntuacion = 0;

			// Lógica de escape: ¿La nueva fila/columna está libre de CUALQUIER láser?
			boolean filaLibre = esTrayectoriaSegura(mundo, -1, futY);
			boolean colLibre = esTrayectoriaSegura(mundo, futX, -1);

			// --- PRIORIDAD 1: Moverse a un eje totalmente limpio ---
			if (lx == miX) { // Amenaza Vertical -> Busco cambiar a Columna Segura
				if (futX != miX && colLibre) puntuacion += 10000; 
				else if (futX != miX) puntuacion += 1000; // Columna con bala, pero al menos salgo de la actual
			} else { // Amenaza Horizontal -> Busco cambiar a Fila Segura
				if (futY != miY && filaLibre) puntuacion += 10000;
				else if (futY != miY) puntuacion += 1000; // Fila con bala, pero al menos salgo de la actual
			}

			// --- PRIORIDAD 2: Alejarse de la bala más cercana (Desempate) ---
			double distFutura = Math.sqrt(Math.pow(futX - lx, 2) + Math.pow(futY - ly, 2));
			puntuacion += distFutura;

			if (puntuacion > mejorPuntuacion) {
				mejorPuntuacion = puntuacion;
				mejorAccion = mov.getKey();
			}
		}

		return mejorAccion;
	}

	// Comprueba si en una fila o columna específica hay ALGÚN láser
	private boolean esTrayectoriaSegura(Mundo84 mundo, int x, int y) {
		for (Observation laser : mundo.lasers) {
			int lx = (int)(laser.position.x / mundo.Bloque);
			int ly = (int)(laser.position.y / mundo.Bloque);
			if (x != -1 && lx == x) return false; // Columna comprometida
			if (y != -1 && ly == y) return false; // Fila comprometida
		}
		return true;
	}

	private Observation getBalaMasCercanaEnTrayectoria(Mundo84 mundo, int miX, int miY) {
		Observation cercana = null;
		double dMin = Double.MAX_VALUE;
		for (Observation l : mundo.lasers) {
			int lx = (int)(l.position.x / mundo.Bloque);
			int ly = (int)(l.position.y / mundo.Bloque);
			if (lx == miX || ly == miY) {
				double d = mundo.MiPosicion.dist(new Vector2d(lx, ly));
				if (d < dMin) { dMin = d; cercana = l; }
			}
		}
		return cercana;
	}

	private boolean esPosicionIntocable(Mundo84 mundo, int x, int y) {
		for (Observation obj : mundo.objetosIntocables) {
			int ox = (int)(obj.position.x / mundo.Bloque);
			int oy = (int)(obj.position.y / mundo.Bloque);
			if (ox == x && oy == y) return true;
		}
		return false;
	}
}
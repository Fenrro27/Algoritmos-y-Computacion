package si2026.kevinjesusbandaalu.p01.reglas;

import core.game.Observation;
import si2026.kevinjesusbandaalu.common.ICondicion;
import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.p01.Mundo84;
import tools.Vector2d;

public class Orientar_Condicion implements ICondicion {

	@Override
	public boolean seCumple(IMundo m) {
		Mundo84 mundo = (Mundo84) m;
		Observation enemigo = mundo.enemigoMasCercano;

		if (enemigo == null) return false;
		

		// 1. Posiciones relativas (Enemigo respecto al Personaje)
		double relX = (enemigo.position.x / mundo.Bloque) - mundo.MiPosicion.x;
		double relY = (enemigo.position.y / mundo.Bloque) - mundo.MiPosicion.y;

		// 2. Determinar la dirección a la que DEBERÍA apuntar según el cuadrante
		// Sentido antihorario: Inferior-Derecha -> Derecha -> Superior-Derecha -> Arriba...
		Vector2d direccionObjetivo = new Vector2d(0, 0);

		if (relX >= 0 && relY >= 0) {
			// CUADRANTE INFERIOR DERECHA: Apunto a la DERECHA (1, 0)
			direccionObjetivo.set(1, 0);
		} else if (relX > 0 && relY < 0) {
			// CUADRANTE SUPERIOR DERECHA: Apunto ARRIBA (0, -1)
			direccionObjetivo.set(0, -1);
		} else if (relX <= 0 && relY <= 0) {
			// CUADRANTE SUPERIOR IZQUIERDA: Apunto a la IZQUIERDA (-1, 0)
			direccionObjetivo.set(-1, 0);
		} else if (relX < 0 && relY > 0) {
			// CUADRANTE INFERIOR IZQUIERDA: Apunto ABAJO (0, 1)
			direccionObjetivo.set(0, 1);
		}

		// 3. Si mi orientación actual NO es la objetivo, devuelvo true para rotar
		// Comparamos los componentes del vector (usando una pequeña tolerancia por si acaso)
		boolean estaOrientadoX = Math.abs(mundo.miOrientacion.x - direccionObjetivo.x) < 0.1;
		boolean estaOrientadoY = Math.abs(mundo.miOrientacion.y - direccionObjetivo.y) < 0.1;
		
		boolean regreso = !(estaOrientadoX && estaOrientadoY);
		
		return regreso;
	}

}
package si2026.kevinjesusbandaalu.p01.reglas;

import core.game.Observation;
import ontology.Types.ACTIONS;
import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.MotorReglas.IAccion;
import si2026.kevinjesusbandaalu.p01.Mundo84;
import tools.Vector2d;

public class Orientar_Accion implements IAccion {

	@Override
	public ACTIONS doAction(IMundo m) {
		Mundo84 mundo = (Mundo84) m;
		
		// 1. Usamos el enemigo más cercano que ya calculó el mundo
		Observation enemigo = mundo.enemigoMasCercano;
		if (enemigo == null) return ACTIONS.ACTION_NIL;

		// 2. Calcular posición relativa (Enemigo respecto a mí) en bloques
		double relX = (enemigo.position.x / mundo.Bloque) - mundo.MiPosicion.x;
		double relY = (enemigo.position.y / mundo.Bloque) - mundo.MiPosicion.y;

		// 3. Determinar a dónde queremos mirar (Lógica de intercepción antihoraria)
		// Cuadrante Inf-Der (X+, Y+) -> Mirar Derecha
		// Cuadrante Sup-Der (X+, Y-) -> Mirar Arriba
		// Cuadrante Sup-Izq (X-, Y-) -> Mirar Izquierda
		// Cuadrante Inf-Izq (X-, Y+) -> Mirar Abajo
		
		if (relX >= 0 && relY >= 0) {
			return ACTIONS.ACTION_RIGHT;
		} 
		else if (relX > 0 && relY < 0) {
			return ACTIONS.ACTION_UP;
		} 
		else if (relX <= 0 && relY <= 0) {
			return ACTIONS.ACTION_LEFT;
		} 
		else if (relX < 0 && relY > 0) {
			return ACTIONS.ACTION_DOWN;
		}

		return ACTIONS.ACTION_NIL;
	}
}
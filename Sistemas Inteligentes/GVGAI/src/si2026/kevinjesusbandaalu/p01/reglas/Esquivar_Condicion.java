package si2026.kevinjesusbandaalu.p01.reglas;

import core.game.Observation;
import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.MotorReglas.ICondicion;
import si2026.kevinjesusbandaalu.p01.Mundo84;

public class Esquivar_Condicion implements ICondicion {

    // Definimos el radio de detección
    private static final double RADIO_PELIGRO = 3.0;

    @Override
    public boolean seCumple(IMundo m) {
        Mundo84 mundo = (Mundo84) m;
        
        if (mundo.lasers == null || mundo.lasers.isEmpty()) {
            return false;
        }

        // Posición del personaje (en bloques)
        double miX = mundo.MiPosicion.x;
        double miY = mundo.MiPosicion.y;

        for (Observation laser : mundo.lasers) {
            // Posición del láser (en bloques)
            double laserX = laser.position.x / mundo.Bloque;
            double laserY = laser.position.y / mundo.Bloque;

            // Calculamos la distancia real entre el personaje y el láser
            // Usamos Pitágoras: d = sqrt((x2-x1)² + (y2-y1)²)
            double distancia = Math.sqrt(Math.pow(laserX - miX, 2) + Math.pow(laserY - miY, 2));

            // Si entra en el radio, activamos la esquiva
            if (distancia <= RADIO_PELIGRO) {
                return true; 
            }
        }
        
        return false;
    }
    
    @Override
	public String toString() {
		return "Esquivar";
	}
}
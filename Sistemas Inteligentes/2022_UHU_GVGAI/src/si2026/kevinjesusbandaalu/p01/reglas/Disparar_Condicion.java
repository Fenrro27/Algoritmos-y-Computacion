package si2026.kevinjesusbandaalu.p01.reglas;

import core.game.Observation;
import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.MotorReglas.ICondicion;
import si2026.kevinjesusbandaalu.p01.Mundo84;

public class Disparar_Condicion implements ICondicion {

    // Radio de efectividad: Solo disparamos si el enemigo está a menos de 6 bloques
    private static final double RANGO_DISPARO = 6.0;

    @Override
    public boolean seCumple(IMundo m) {
        Mundo84 mundo = (Mundo84) m;
        Observation enemigo = mundo.enemigoMasCercano;

        if (enemigo == null) return false;

        double dist = mundo.MiPosicion.dist(new tools.Vector2d(enemigo.position.x / mundo.Bloque, 
                                                              enemigo.position.y / mundo.Bloque));

        // Condición: Cerca del enemigo Y alineado con mi orientación actual
        return dist <= RANGO_DISPARO && estaAlineado(mundo, enemigo);
    }

    private boolean estaAlineado(Mundo84 mundo, Observation e) {
        double ex = e.position.x / mundo.Bloque;
        double ey = e.position.y / mundo.Bloque;
        
        // Comprobar si está en mi misma fila o columna según hacia dónde miro
        if (mundo.miOrientacion.x > 0) return Math.abs(ey - mundo.MiPosicion.y) < 1.0 && ex > mundo.MiPosicion.x;
        if (mundo.miOrientacion.x < 0) return Math.abs(ey - mundo.MiPosicion.y) < 1.0 && ex < mundo.MiPosicion.x;
        if (mundo.miOrientacion.y > 0) return Math.abs(ex - mundo.MiPosicion.x) < 1.0 && ey > mundo.MiPosicion.y;
        if (mundo.miOrientacion.y < 0) return Math.abs(ex - mundo.MiPosicion.x) < 1.0 && ey < mundo.MiPosicion.y;
        
        return false;
    }
    
    @Override
	public String toString() {
		return "Disparar";
	}
}
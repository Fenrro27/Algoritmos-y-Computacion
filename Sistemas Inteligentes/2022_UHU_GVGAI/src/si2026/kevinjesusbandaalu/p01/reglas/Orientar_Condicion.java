package si2026.kevinjesusbandaalu.p01.reglas;

import core.game.Observation;
import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.MotorReglas.ICondicion;
import si2026.kevinjesusbandaalu.p01.Mundo84;
import tools.Vector2d;

public class Orientar_Condicion implements ICondicion {

    @Override
    public boolean seCumple(IMundo m) {
        Mundo84 mundo = (Mundo84) m;
        Observation enemigo = mundo.enemigoMasCercano;

        if (enemigo == null) return false;

        // 1. Posiciones relativas en bloques
        double relX = (enemigo.position.x / mundo.Bloque) - mundo.MiPosicion.x;
        double relY = (enemigo.position.y / mundo.Bloque) - mundo.MiPosicion.y;

        // 2. PRIORIDAD 1: Si ya estoy apuntando al enemigo en su eje, NO ORIENTAR
        // Esto permite que la siguiente regla (Disparar) tome el control inmediatamente.
        if (Math.abs(relY) < 1.0) { // Está en mi fila
            if (relX > 0 && mundo.miOrientacion.x > 0.9) return false; // Ya miro derecha
            if (relX < 0 && mundo.miOrientacion.x < -0.9) return false; // Ya miro izquierda
        }
        if (Math.abs(relX) < 1.0) { // Está en mi columna
            if (relY > 0 && mundo.miOrientacion.y > 0.9) return false; // Ya miro abajo
            if (relY < 0 && mundo.miOrientacion.y < -0.9) return false; // Ya miro arriba
        }

        // 3. PRIORIDAD 2: Decidir hacia qué eje girar basándonos en la cercanía
        // Si la distancia en Y es menor que en X, significa que estamos casi alineados horizontalmente
        Vector2d dirDeseada = new Vector2d(0, 0);
        
        if (Math.abs(relX) > Math.abs(relY)) {
            // El enemigo está más lejos en X que en Y -> Priorizar apuntar en X (Derecha/Izquierda)
            dirDeseada.set(relX > 0 ? 1 : -1, 0);
        } else {
            // El enemigo está más lejos en Y que en X -> Priorizar apuntar en Y (Arriba/Abajo)
            dirDeseada.set(0, relY > 0 ? 1 : -1);
        }

        // 4. Comprobar si mi orientación actual coincide con esa dirección deseada
        boolean yaOrientado = Math.abs(mundo.miOrientacion.x - dirDeseada.x) < 0.1 && 
                              Math.abs(mundo.miOrientacion.y - dirDeseada.y) < 0.1;

        return !yaOrientado;
    }
    
    @Override
	public String toString() {
		return "Orientar";
	}
}
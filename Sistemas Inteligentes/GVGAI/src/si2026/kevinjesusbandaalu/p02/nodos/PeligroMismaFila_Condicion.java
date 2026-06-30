package si2026.kevinjesusbandaalu.p02.nodos;

import core.game.Observation;
import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.MotorReglas.ICondicion;
import si2026.kevinjesusbandaalu.p02.Mundo83;
import tools.Vector2d;

public class PeligroMismaFila_Condicion implements ICondicion {
    private static final double RADIO_PELIGRO = 3.0;

    @Override
    public boolean seCumple(IMundo m) {
        Mundo83 mundo = (Mundo83) m;
        Observation peligro = obtenerAmenazaMasCercana(mundo);
        if (peligro == null) return false;

        double relX = (peligro.position.x / mundo.Bloque) - mundo.MiPosicion.x;
        double relY = (peligro.position.y / mundo.Bloque) - mundo.MiPosicion.y;
        double dist = mundo.MiPosicion.dist(new Vector2d(peligro.position.x / mundo.Bloque, peligro.position.y / mundo.Bloque));
        
        if (Math.abs(relY) < 1.0 && dist <= RADIO_PELIGRO) {
            return true;
        }
        return false;
    }

    private Observation obtenerAmenazaMasCercana(Mundo83 mundo) {
        Observation masCercana = null;
        double distMin = Double.MAX_VALUE;
        for (Observation o : mundo.evitar) {
            double dist = mundo.MiPosicion.dist(new Vector2d(o.position.x / mundo.Bloque, o.position.y / mundo.Bloque));
            if (dist < distMin) {
                distMin = dist;
                masCercana = o;
            }
        }
        return masCercana;
    }
}

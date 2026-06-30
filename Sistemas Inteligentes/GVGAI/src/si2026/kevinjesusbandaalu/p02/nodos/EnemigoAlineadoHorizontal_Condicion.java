package si2026.kevinjesusbandaalu.p02.nodos;

import core.game.Observation;
import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.MotorReglas.ICondicion;
import si2026.kevinjesusbandaalu.p02.Mundo83;

public class EnemigoAlineadoHorizontal_Condicion implements ICondicion {
    @Override
    public boolean seCumple(IMundo m) {
        Mundo83 mundo = (Mundo83) m;
        for (Observation e : mundo.evitar) {
            double ey = e.position.y / mundo.Bloque;
            if (Math.abs(ey - mundo.MiPosicion.y) < 1.0) {
                return true;
            }
        }
        return false;
    }
}

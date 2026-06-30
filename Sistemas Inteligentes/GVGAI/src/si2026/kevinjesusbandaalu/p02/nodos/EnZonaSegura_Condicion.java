package si2026.kevinjesusbandaalu.p02.nodos;

import core.game.Observation;
import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.MotorReglas.ICondicion;
import si2026.kevinjesusbandaalu.p02.Mundo83;

public class EnZonaSegura_Condicion implements ICondicion {

    @Override
    public boolean seCumple(IMundo m) {
        Mundo83 mundo = (Mundo83) m;
        for (Observation zs : mundo.zonaSegura) {
            int zsX = (int) (zs.position.x / mundo.Bloque);
            int zsY = (int) (zs.position.y / mundo.Bloque);
            if (zsX == (int) mundo.MiPosicion.x && zsY == (int) mundo.MiPosicion.y) {
                return true;
            }
        }
        return false;
    }
}

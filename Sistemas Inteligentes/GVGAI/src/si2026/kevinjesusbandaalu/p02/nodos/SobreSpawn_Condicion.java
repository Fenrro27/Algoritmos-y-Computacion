package si2026.kevinjesusbandaalu.p02.nodos;

import core.game.Observation;
import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.MotorReglas.ICondicion;
import si2026.kevinjesusbandaalu.p02.Mundo83;

public class SobreSpawn_Condicion implements ICondicion {

    @Override
    public boolean seCumple(IMundo m) {
        Mundo83 mundo = (Mundo83) m;
        for (Observation sp : mundo.spawnBuzos) {
            int spX = (int) (sp.position.x / mundo.Bloque);
            int spY = (int) (sp.position.y / mundo.Bloque);
            if (spX == (int) mundo.MiPosicion.x && spY == (int) mundo.MiPosicion.y) {
                return true;
            }
        }
        return false;
    }
}

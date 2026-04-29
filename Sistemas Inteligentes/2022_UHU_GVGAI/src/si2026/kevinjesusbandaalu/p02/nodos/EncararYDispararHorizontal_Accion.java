package si2026.kevinjesusbandaalu.p02.nodos;

import core.game.Observation;
import ontology.Types.ACTIONS;
import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.MotorReglas.IAccion;
import si2026.kevinjesusbandaalu.p02.Mundo83;

public class EncararYDispararHorizontal_Accion implements IAccion {
    @Override
    public ACTIONS doAction(IMundo m) {
        Mundo83 mundo = (Mundo83) m;
        for (Observation e : mundo.evitar) {
            double ex = e.position.x / mundo.Bloque;
            double ey = e.position.y / mundo.Bloque;
            
            if (Math.abs(ey - mundo.MiPosicion.y) < 1.0) { // Alineado horizontal
                if (ex < mundo.MiPosicion.x) return (mundo.miOrientacion.x < -0.9) ? ACTIONS.ACTION_USE : ACTIONS.ACTION_LEFT;
                if (ex > mundo.MiPosicion.x) return (mundo.miOrientacion.x > 0.9) ? ACTIONS.ACTION_USE : ACTIONS.ACTION_RIGHT;
            }
        }
        return ACTIONS.ACTION_USE;
    }
}

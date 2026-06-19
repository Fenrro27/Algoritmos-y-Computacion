package si2026.kevinjesusbandaalu.p02.nodos;

import ontology.Types.ACTIONS;
import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.MotorReglas.IAccion;

public class DisparoPorDefecto_Accion implements IAccion {
    @Override
    public ACTIONS doAction(IMundo m) {
        return ACTIONS.ACTION_USE;
    }
}

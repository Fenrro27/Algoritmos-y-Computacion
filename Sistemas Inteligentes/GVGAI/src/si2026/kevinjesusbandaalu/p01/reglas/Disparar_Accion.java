package si2026.kevinjesusbandaalu.p01.reglas;

import ontology.Types.ACTIONS;
import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.MotorReglas.IAccion;
import ontology.Types.ACTIONS;


public class Disparar_Accion implements IAccion {

	@Override
	public ACTIONS doAction(IMundo m) {
		
		return ACTIONS.ACTION_USE;
	}

}

package si2026.kevinjesusbandaalu.common.MotorReglas;

import ontology.Types.ACTIONS;
import si2026.kevinjesusbandaalu.common.IMundo;


//esta interfaz se implementara en las acciones que desarrollemos en el juevo
public interface IAccion {

	//devolvemos una accion de gvgai
	public ACTIONS doAction(IMundo m);
}

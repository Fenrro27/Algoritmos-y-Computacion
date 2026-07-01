package si2024.kevinjesusbandaalu.common;

import ontology.Types.ACTIONS;


//esta interfaz se implementara en las acciones que desarrollemos en el juevo
public interface IAccion {

	//devolvemos una accion de gvgai
	public ACTIONS doAction(IMundo m);
}

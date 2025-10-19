package si2024.kevinjesusbandaalu.common.arbolDecision;

import ontology.Types.ACTIONS;
import si2024.kevinjesusbandaalu.common.IAccion;
import si2024.kevinjesusbandaalu.common.IMundo;

public class NodoAccion extends NodoArbol {

	public IAccion accion;
	
/**
 * nodo hojas del arbol
*/
	public NodoAccion(IAccion accion) {
		this.accion=accion;
		this.de=null;
		this.iz=null;
	}
	
	
	@Override
	public ACTIONS getAccion(IMundo m) {
		return this.accion.doAction(m);
	}

}

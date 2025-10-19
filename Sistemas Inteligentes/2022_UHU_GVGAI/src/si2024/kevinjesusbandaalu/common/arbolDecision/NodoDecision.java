package si2024.kevinjesusbandaalu.common.arbolDecision;

import ontology.Types.ACTIONS;
import si2024.kevinjesusbandaalu.common.ICondicion;
import si2024.kevinjesusbandaalu.common.IMundo;

public class NodoDecision extends NodoArbol {

	/**
	 * nodos intermedios del arbol
	 */
	
	public NodoDecision(ICondicion c, NodoArbol derecha, NodoArbol izquierda) {
		this.c=c; //condicion para decidir a que hijo accedemos
		this.de=derecha; //en caso de true
		this.iz=izquierda;// en caso de false
		
	}
	
	@Override
	public ACTIONS getAccion(IMundo m) {
		return null;
	}

}

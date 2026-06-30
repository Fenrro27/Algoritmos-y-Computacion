package si2024.kevinjesusbandaalu.common.arbolDecision;

import ontology.Types.ACTIONS;
import si2024.kevinjesusbandaalu.common.IMundo;
import si2024.kevinjesusbandaalu.common.Motor;

public abstract class ArbolDecision extends Motor {

	protected NodoArbol raiz;
	
	public ACTIONS Pensar(IMundo m) {
		
		NodoArbol nodoActual=raiz;
		
		//hasta llegar a una hoja vamos decidiendo el camino
		while(!nodoActual.esHoja()) {
			nodoActual = nodoActual.decidir(m);
		}
		
		//devolvemos la accion de la hoja
		return nodoActual.getAccion(m);
	}
}

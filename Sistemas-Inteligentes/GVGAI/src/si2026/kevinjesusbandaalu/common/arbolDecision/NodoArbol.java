package si2026.kevinjesusbandaalu.common.arbolDecision;

import ontology.Types.ACTIONS;
import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.MotorReglas.ICondicion;

public abstract class NodoArbol {

	public ICondicion c;
	public NodoArbol iz, de;

	public NodoArbol decidir(IMundo m) {
		if(c.seCumple(m)) {
			return this.de;
		}
		else {
			return this.iz;
		}
	}

	public boolean esHoja() {
		return iz == null && de == null;
	}
	
	public abstract ACTIONS getAccion(IMundo m);

}

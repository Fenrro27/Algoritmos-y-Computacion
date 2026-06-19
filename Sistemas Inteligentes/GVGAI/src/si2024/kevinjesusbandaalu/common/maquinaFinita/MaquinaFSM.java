package si2024.kevinjesusbandaalu.common.maquinaFinita;

import ontology.Types.ACTIONS;
import si2024.kevinjesusbandaalu.common.IMundo;
import si2024.kevinjesusbandaalu.common.Motor;

public abstract class MaquinaFSM extends Motor{

	protected Estado estadoInicial;
	protected Estado estadoActual;
	
	public ACTIONS Pensar(IMundo m) {
		for(transicion t: estadoActual.getTransiciones() ) {
			if(t.seDispara(m)) {
				estadoActual= t.siguienteEstado();
				return estadoActual.getAccion().doAction(m);
			}
		}
		
		return estadoActual.getAccion().doAction(m);
	}
}

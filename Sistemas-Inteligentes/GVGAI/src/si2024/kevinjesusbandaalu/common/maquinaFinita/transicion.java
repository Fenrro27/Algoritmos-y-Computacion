package si2024.kevinjesusbandaalu.common.maquinaFinita;

import si2024.kevinjesusbandaalu.common.ICondicion;
import si2024.kevinjesusbandaalu.common.IMundo;

public class transicion {

	private Estado eObjetivo;
	private ICondicion condicion;
	
	
	public transicion(Estado eObEstado, ICondicion condicion) {
		this.condicion=condicion;
		this.eObjetivo=eObEstado;
	}
	
	public boolean seDispara(IMundo m) {
		return condicion.seCumple(m);
	}
	
	public Estado siguienteEstado() {
		return eObjetivo;
	}
	

}

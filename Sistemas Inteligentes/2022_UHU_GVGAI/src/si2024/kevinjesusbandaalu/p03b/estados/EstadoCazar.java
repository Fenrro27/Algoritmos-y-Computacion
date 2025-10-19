package si2024.kevinjesusbandaalu.p03b.estados;

import java.util.ArrayList;
import java.util.HashMap;

import si2024.kevinjesusbandaalu.common.IAccion;
import si2024.kevinjesusbandaalu.common.maquinaFinita.Estado;
import si2024.kevinjesusbandaalu.common.maquinaFinita.transicion;
import si2024.kevinjesusbandaalu.p03b.reglas.Cazar_Accion;
import si2024.kevinjesusbandaalu.p03b.reglas.Cazar_Condicion;
import si2024.kevinjesusbandaalu.p03b.reglas.Comer_Condicion;

public class EstadoCazar extends Estado {

	public IAccion a;

	public EstadoCazar() {
		a = new Cazar_Accion();

		transiciones = new ArrayList<transicion>();

	}
	
	public void addTransiciones(HashMap<String, Estado> estados ) {
		this.transiciones.add(new transicion(estados.get("Cazar"), new Cazar_Condicion()));
		this.transiciones.add(new transicion(estados.get("Comer"), new Comer_Condicion()));

	}

	@Override
	public IAccion getAccion() {
		return a;
	}

}

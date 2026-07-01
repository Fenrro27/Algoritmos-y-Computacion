package si2024.kevinjesusbandaalu.p04;

import java.util.ArrayList;
import java.util.List;

import si2024.kevinjesusbandaalu.common.IAccion;
import si2024.kevinjesusbandaalu.common.ICondicion;
import si2024.kevinjesusbandaalu.common.Motor;
import si2024.kevinjesusbandaalu.common.Regla;
import si2024.kevinjesusbandaalu.p04.reglas.Comer_Accion;
import si2024.kevinjesusbandaalu.p04.reglas.Comer_Condicion;

public class Motor45 extends Motor {

	public Motor45() {
		reglas = new ArrayList<>();
		
		
		Comer_Condicion AleCond= new Comer_Condicion();
		List<ICondicion> List_AleCond = new ArrayList<ICondicion>();
		List_AleCond.add(AleCond);
		reglas.add(new Regla(List_AleCond, (IAccion) new Comer_Accion()));
		
	}

}

package si2024.kevinjesusbandaalu.p01;

import java.util.ArrayList;
import java.util.List;

import ontology.Types.ACTIONS;
import si2024.kevinjesusbandaalu.common.IAccion;
import si2024.kevinjesusbandaalu.common.ICondicion;
import si2024.kevinjesusbandaalu.common.IMundo;
import si2024.kevinjesusbandaalu.common.Motor;
import si2024.kevinjesusbandaalu.common.Regla;
import si2024.kevinjesusbandaalu.p01.reglas.Aleatorio_Accion;
import si2024.kevinjesusbandaalu.p01.reglas.Aleatorio_Condicion;
import si2024.kevinjesusbandaalu.p01.reglas.Cazar_Accion;
import si2024.kevinjesusbandaalu.p01.reglas.Cazar_Condicion;

public class Motor18 extends Motor {

	
	
	public Motor18() {
		reglas = new ArrayList<>();

		//Regla Aleatoria
		Aleatorio_Condicion AleCond= new Aleatorio_Condicion();
		List<ICondicion> List_AleCond = new ArrayList<ICondicion>();
		List_AleCond.add(AleCond);
		reglas.add(new Regla(List_AleCond, (IAccion) new Aleatorio_Accion()));
		
		//Regla cazar
		List<ICondicion> List_CazCond = new ArrayList<ICondicion>();
		List_CazCond.add(new Cazar_Condicion());
		reglas.add(new Regla(List_CazCond, (IAccion) new Cazar_Accion()));		
		
		
		
	}

	/*@Override
	public ACTIONS Pensar(IMundo mundo) {
		for (Regla r : reglas) {
			if (r.seCumple(mundo)) {
				ACTIONS a = r.getAccion().doAction(mundo);
				return a;
			}
		}
		return null;
	}*/

}

package si2024.kevinjesusbandaalu.p04.reglas;

import si2024.kevinjesusbandaalu.common.ICondicion;
import si2024.kevinjesusbandaalu.common.IMundo;

public class Comer_Condicion implements ICondicion {

	public Comer_Condicion(){
	}

	@Override
	public boolean seCumple(IMundo m) {
		return true;
	}

}

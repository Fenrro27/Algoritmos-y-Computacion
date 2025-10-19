package si2024.kevinjesusbandaalu.p03.reglas;

import si2024.kevinjesusbandaalu.common.ICondicion;
import si2024.kevinjesusbandaalu.common.IMundo;
import si2024.kevinjesusbandaalu.p03.Mundo53;

public class EstoyInfectado_Condicion implements ICondicion{

	@Override
	public boolean seCumple(IMundo m) {
		Mundo53 xana = (Mundo53)m;
		return xana.estoyInfectado;
		
	}

}

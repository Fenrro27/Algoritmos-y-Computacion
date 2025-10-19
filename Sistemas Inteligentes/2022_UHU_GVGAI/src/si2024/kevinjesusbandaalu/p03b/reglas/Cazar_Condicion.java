package si2024.kevinjesusbandaalu.p03b.reglas;

import si2024.kevinjesusbandaalu.common.ICondicion;
import si2024.kevinjesusbandaalu.common.IMundo;
import si2024.kevinjesusbandaalu.p03b.Mundo68;

public class Cazar_Condicion implements ICondicion {

	Mundo68 xana;

	@Override
	public boolean seCumple(IMundo m) {
		xana = (Mundo68) m;

		return xana.FantasmasComibles.size()>0;
	}

}

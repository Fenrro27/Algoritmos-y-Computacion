package si2024.kevinjesusbandaalu.p01.reglas;

import si2024.kevinjesusbandaalu.common.ICondicion;
import si2024.kevinjesusbandaalu.common.IMundo;
import si2024.kevinjesusbandaalu.p01.Mundo18;

public class Aleatorio_Condicion implements ICondicion {

	@Override
	public boolean seCumple(IMundo m) {
		Mundo18 xana = (Mundo18)m;
		
		//se cumple cuando no nos podemos atacar porque dos aguilas estan en la misma posicion
		
		for(int i=0; i<3; i++) {
			for(int j=0;j<3; j++) {
				if(xana.MapNPCs[(int)xana.MiPosicion.y+(i-1)][(int)xana.MiPosicion.x+(j-1)]>1) //Si en una casilla contigua hay mas de un npc
					return true;
			}
		}
		
		
		
		return false;
	}

}

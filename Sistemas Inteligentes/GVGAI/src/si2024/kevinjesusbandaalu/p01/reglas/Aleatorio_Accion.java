package si2024.kevinjesusbandaalu.p01.reglas;

import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.Random;

import ontology.Types.ACTIONS;
import si2024.kevinjesusbandaalu.common.IAccion;
import si2024.kevinjesusbandaalu.common.IMundo;
import si2024.kevinjesusbandaalu.p01.Mundo18;

public class Aleatorio_Accion implements IAccion {

	@Override
	public ACTIONS doAction(IMundo m) {
		Mundo18 xana = (Mundo18) m;

		// si en MapNPCs hay un -1 indica gusano, si hay un -13 hay un muro, si >0
		// entoces hay un aguilablanca
		ArrayList<ACTIONS> ListaAcciones = new ArrayList<ACTIONS>();

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int elemento = xana.MapNPCs[(int) xana.MiPosicion.y + (i - 1)][(int) xana.MiPosicion.x + (j - 1)];
				if (elemento <= 1 && elemento > -13) {// si no hay dos pajaros y no es un muro añadimos la accion
					if ((i - 1) == -1 && (j - 1) == 0) { // y=-1, x=0
						ListaAcciones.add(ACTIONS.ACTION_DOWN);
					} else if ((i - 1) == 1 && (j - 1) == 0) {// y=1, x=0
						ListaAcciones.add(ACTIONS.ACTION_UP);
					} else if ((i - 1) == 0 && (j - 1) == -1) {// y=0, x=-1
						ListaAcciones.add(ACTIONS.ACTION_LEFT);
					} else if ((i - 1) == 0 && (j - 1) == 1) {// y=0, x=1
						ListaAcciones.add(ACTIONS.ACTION_RIGHT);
					} else {
						//ListaAcciones.add(ACTIONS.ACTION_NIL);
					}
				}
			}
		}

		if (ListaAcciones.size() != 0) {
			int nAleatorio = new Random().nextInt(ListaAcciones.size());
			return ListaAcciones.get(nAleatorio); // devolvemos una accion aleatoria
		} // del array
		else
			return ACTIONS.ACTION_NIL;

	}

}

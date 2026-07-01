package si2024.kevinjesusbandaalu.common;

import java.util.List;

import ontology.Types.ACTIONS;

public abstract class Motor {
	protected List<Regla> reglas;

	
	public ACTIONS Pensar(IMundo mundo) {
		for (Regla r : reglas) {
			if (r.seCumple(mundo)) {
				ACTIONS a = r.getAccion().doAction(mundo);
				return a;
			}
		}
		return null;
	}
}

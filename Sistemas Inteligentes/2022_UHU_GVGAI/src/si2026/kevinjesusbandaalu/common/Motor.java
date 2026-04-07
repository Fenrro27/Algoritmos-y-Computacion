package si2026.kevinjesusbandaalu.common;

import java.util.List;

import ontology.Types.ACTIONS;
import si2026.kevinjesusbandaalu.common.MotorReglas.Regla;

public abstract class Motor {
	protected List<Regla> reglas;

	
	public ACTIONS Pensar(IMundo mundo) {
		for (Regla r : reglas) {
			if (r.seCumple(mundo)) {
				return r.getAccion().doAction(mundo);
			}
		}
		return null;
	}
}

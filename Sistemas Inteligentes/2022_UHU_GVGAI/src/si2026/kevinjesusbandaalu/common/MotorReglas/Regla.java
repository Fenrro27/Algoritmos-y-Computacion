package si2026.kevinjesusbandaalu.common.MotorReglas;

import java.util.List;
import ontology.Types.ACTIONS;
import si2026.kevinjesusbandaalu.common.IMundo;

public class Regla {

	// condiciones para que se ejecute la regla
	private List<ICondicion> antecedentes;
	// Acciones de la regla
	private IAccion accion;

	public Regla(List<ICondicion> ant, IAccion accion) {
		this.antecedentes=ant;
		this.accion=accion;
		
	}

	/**
	 * comprueba que se cumplan los antecedentes de la regla
	 * @param m Mundo en el que esta el agente
	 * @return devuelve verdadd si se cumplen los antecedentes para la percepcion actual actual
	 */
	public boolean seCumple(IMundo m) {

		for (ICondicion condicion : antecedentes) {

			if (!condicion.seCumple(m))
				return false;
		}
		return true;

	}
		
	public IAccion getAccion() {
		return accion;
	}
	

}

package si2026.kevinjesusbandaalu.p02.nodos;

import core.game.Observation;
import ontology.Types.ACTIONS;
import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.MotorReglas.IAccion;
import si2026.kevinjesusbandaalu.p02.Mundo83;
import tools.Vector2d;

public class DefenderHorizontal_Accion implements IAccion {
	
	@Override
	public ACTIONS doAction(IMundo m) {
	    Mundo83 mundo = (Mundo83) m;
	    Observation amenaza = obtenerAmenazaMasCercana(mundo);
	    if (amenaza == null) return ACTIONS.ACTION_NIL;

	    double relX = (amenaza.position.x / mundo.Bloque) - mundo.MiPosicion.x;
	    double relY = (amenaza.position.y / mundo.Bloque) - mundo.MiPosicion.y;

	    // Si está en la misma fila pero no encarado, girar hacia la amenaza
	    if (Math.abs(relY) < 1.0) { 
	        return relX > 0 ? ACTIONS.ACTION_RIGHT : ACTIONS.ACTION_LEFT;
	    }

	    return ACTIONS.ACTION_NIL;
	}

    private Observation obtenerAmenazaMasCercana(Mundo83 mundo) {
        Observation masCercana = null;
        double distMin = Double.MAX_VALUE;
        for (Observation o : mundo.evitar) {
            double dist = mundo.MiPosicion.dist(new Vector2d(o.position.x / mundo.Bloque, o.position.y / mundo.Bloque));
            if (dist < distMin) {
                distMin = dist;
                masCercana = o;
            }
        }
        return masCercana;
    }
}

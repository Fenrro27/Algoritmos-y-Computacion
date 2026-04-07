package si2026.kevinjesusbandaalu.p02;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import si2026.kevinjesusbandaalu.common.*;

public class Jugador1 extends AbstractPlayer {
	


	public Jugador1(StateObservation stateObs, ElapsedCpuTimer arg1) {
	}

	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer arg1) {
		IMundo mundoJugador = new Mundo84(stateObs);
		Motor84 motorJugador = new Motor84();

		mundoJugador.AnalizarEntorno(stateObs);
		return motorJugador.Pensar(mundoJugador);
		
	}

}

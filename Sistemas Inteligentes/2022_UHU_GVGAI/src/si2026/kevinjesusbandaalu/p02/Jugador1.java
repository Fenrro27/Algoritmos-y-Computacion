package si2026.kevinjesusbandaalu.p02;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;

public class Jugador1 extends AbstractPlayer {

	Mundo83 mundo83;

	public Jugador1(StateObservation stateObs, ElapsedCpuTimer arg1) {
		mundo83 = new Mundo83(stateObs);
		
	}

	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer arg1) {
		mundo83.AnalizarEntorno(stateObs);
		return null;

	}

}

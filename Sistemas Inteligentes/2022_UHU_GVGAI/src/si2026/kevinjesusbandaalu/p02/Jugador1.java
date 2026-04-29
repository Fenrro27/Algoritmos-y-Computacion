package si2026.kevinjesusbandaalu.p02;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;

public class Jugador1 extends AbstractPlayer {

	Mundo83 mundo83;
	Motor83 motor83;

	public Jugador1(StateObservation stateObs, ElapsedCpuTimer arg1) {
		mundo83 = new Mundo83(stateObs);
		motor83 = new Motor83(); 
		
	}

	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer arg1) {
		mundo83.AnalizarEntorno(stateObs);
		return motor83.Pensar(mundo83);

	}

}

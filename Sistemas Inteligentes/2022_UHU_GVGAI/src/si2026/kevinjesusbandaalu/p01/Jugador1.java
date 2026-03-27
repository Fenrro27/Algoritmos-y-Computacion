package si2026.kevinjesusbandaalu.p01;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import si2026.kevinjesusbandaalu.common.*;

public class Jugador1 extends AbstractPlayer {
	
	Motor84 motorJugador;
	IMundo mundoJugador;

	public Jugador1(StateObservation stateObs, ElapsedCpuTimer arg1) {
		mundoJugador = new Mundo84(stateObs);
		motorJugador = new Motor84();
	}

	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer arg1) {
		mundoJugador.AnalizarEntorno(stateObs);
		return this.motorJugador.Pensar(mundoJugador);
		
	}

}

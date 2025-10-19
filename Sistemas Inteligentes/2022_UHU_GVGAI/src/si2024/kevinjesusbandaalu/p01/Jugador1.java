package si2024.kevinjesusbandaalu.p01;

import java.util.List;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import si2024.kevinjesusbandaalu.common.Motor;
import si2024.kevinjesusbandaalu.common.IMundo;
import tools.ElapsedCpuTimer;

/*
 * Jugador para el juego 18
 * 
 * */

public class Jugador1 extends AbstractPlayer {

	Motor18 MotorJugador;
	IMundo MundoJugador;

	public Jugador1(StateObservation stateObs, ElapsedCpuTimer arg1) {
		MundoJugador = new Mundo18(stateObs);
		MotorJugador = new Motor18();
	}

	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer arg1) {
		MundoJugador.AnalizarEntorno(stateObs);
		return this.MotorJugador.Pensar(MundoJugador);
	}

}

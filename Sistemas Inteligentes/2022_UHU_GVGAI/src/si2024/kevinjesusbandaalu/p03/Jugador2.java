package si2024.kevinjesusbandaalu.p03;

import java.util.List;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import si2024.kevinjesusbandaalu.common.Motor;
import si2024.kevinjesusbandaalu.common.IMundo;
import tools.ElapsedCpuTimer;

/*
 * Jugador para el juego 53
 * 
 * */

public class Jugador2 extends AbstractPlayer {

	Motor53 MotorJugador;
	IMundo MundoJugador;

	public Jugador2(StateObservation stateObs, ElapsedCpuTimer arg1) {
		MundoJugador = new Mundo53(stateObs);
		MotorJugador = new Motor53();
	}

	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer arg1) {
		MundoJugador.AnalizarEntorno(stateObs);
		return this.MotorJugador.Pensar(MundoJugador);
	}

}

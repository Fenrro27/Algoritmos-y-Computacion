package si2024.kevinjesusbandaalu.p03b;

import java.util.List;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import si2024.kevinjesusbandaalu.common.IMundo;
import tools.ElapsedCpuTimer;

/*
 * Jugador para el juego 68
 * 
 * */

public class Jugador3 extends AbstractPlayer {

	MaquinaFMS68 MotorJugador;
	IMundo MundoJugador;

	public Jugador3(StateObservation stateObs, ElapsedCpuTimer arg1) {
		MundoJugador = new Mundo68(stateObs);
		MotorJugador = new MaquinaFMS68();
	}

	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer arg1) {
		MundoJugador.AnalizarEntorno(stateObs);
		return this.MotorJugador.Pensar(MundoJugador);
	}

}

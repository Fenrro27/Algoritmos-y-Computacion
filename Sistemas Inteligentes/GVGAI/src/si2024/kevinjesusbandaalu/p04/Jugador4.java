package si2024.kevinjesusbandaalu.p04;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;

/*
 * Jugador para el juego 45
 * 
 * */

public class Jugador4 extends AbstractPlayer {

	// Atributos persistentes del agente
	Mundo45 MundoJugador;
	Motor45 motor;

	public Jugador4(StateObservation stateObs, ElapsedCpuTimer arg1) {
		MundoJugador = new Mundo45(stateObs);
		motor = new Motor45();
	}

	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer arg1) {

		MundoJugador.AnalizarEntorno(stateObs);
		return motor.Pensar(MundoJugador);

	}

}

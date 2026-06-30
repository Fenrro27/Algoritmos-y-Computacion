package si2024.kevinjesusbandaalu.p00;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;

public class Jugador1 extends AbstractPlayer {
	
	List<ACTIONS> camino;
	int indice;

	public Jugador1(StateObservation arg0, ElapsedCpuTimer arg1) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ACTIONS act(StateObservation arg0, ElapsedCpuTimer arg1) {
		
		camino = new LinkedList<ACTIONS>();
		
		
		
		// TODO Auto-generated method stub
		switch(new Random().nextInt()%5){
		case 0:
			return ACTIONS.ACTION_LEFT;
			
		case 1:
			return ACTIONS.ACTION_RIGHT;
			
		case 2:
			return ACTIONS.ACTION_UP;
		case 3:
			return ACTIONS.ACTION_USE;
			
		default:
			return ACTIONS.ACTION_DOWN;
		
		}
	}

}

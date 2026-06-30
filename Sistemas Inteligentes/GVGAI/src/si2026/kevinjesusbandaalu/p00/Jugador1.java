package si2026.kevinjesusbandaalu.p00;

import java.util.ArrayList;
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
	int indice=0;

	public Jugador1(StateObservation arg0, ElapsedCpuTimer arg1) {
		camino = new ArrayList<Types.ACTIONS>();
	
		// 12 a la izquierda
		for(int i = 0; i<12; i++)
			camino.add(ACTIONS.ACTION_LEFT);
		// 5 arriba
		for(int i = 0; i<5; i++)
			camino.add(ACTIONS.ACTION_UP);
		// 2 derecha
		for(int i = 0; i<2; i++)
			camino.add(ACTIONS.ACTION_RIGHT);
		// 2 izquierda
		for(int i = 0; i<2; i++)
			camino.add(ACTIONS.ACTION_LEFT);
		// 2 abajo
		for(int i = 0; i<2; i++)
			camino.add(ACTIONS.ACTION_DOWN);
		// 12 a la derecha
		for(int i = 0; i<12; i++)
			camino.add(ACTIONS.ACTION_RIGHT);
		//2 up
		for(int i = 0; i<2; i++)
			camino.add(ACTIONS.ACTION_UP);
		// 9 izq
		for(int i = 0; i<9; i++)
			camino.add(ACTIONS.ACTION_LEFT);
		//2 up
		for(int i = 0; i<2; i++)
			camino.add(ACTIONS.ACTION_UP);
		// 9 right
		for(int i = 0; i<9; i++)
			camino.add(ACTIONS.ACTION_RIGHT);		
		// 2 up
		for(int i = 0; i<2; i++)
			camino.add(ACTIONS.ACTION_UP);
		// 12 izq
		for(int i = 0; i<12; i++)
			camino.add(ACTIONS.ACTION_LEFT);
		//2 down
		for(int i = 0; i<2; i++)
			camino.add(ACTIONS.ACTION_DOWN);
	
		
		
	}

	@Override
	public ACTIONS act(StateObservation arg0, ElapsedCpuTimer arg1) {
		
		//return null;
		return camino.get(indice++);
	}

}

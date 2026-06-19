package si2026.kevinjesusbandaalu.p07;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;

public class Jugador1 extends AbstractPlayer {
    
    private Mundo78 mundo;
    private Motor78 motor;

    public Jugador1(StateObservation stateObs, ElapsedCpuTimer timer) {
        mundo = new Mundo78(stateObs);
        motor = new Motor78(mundo, timer);
    }

    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer timer) {
      return motor.act(stateObs, timer);
    }
}

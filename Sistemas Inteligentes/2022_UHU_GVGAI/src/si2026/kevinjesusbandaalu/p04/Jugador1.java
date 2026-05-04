package si2026.kevinjesusbandaalu.p04;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;

public class Jugador1 extends AbstractPlayer {
    
    private Mundo49 mundo;
    private Motor49 motor;

    public Jugador1(StateObservation stateObs, ElapsedCpuTimer timer) {
        mundo = new Mundo49(stateObs);
        motor = new Motor49(mundo);
    }

    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer timer) {
        mundo.AnalizarEntorno(stateObs);
         
        return motor.buscar(mundo, timer);
    }
}

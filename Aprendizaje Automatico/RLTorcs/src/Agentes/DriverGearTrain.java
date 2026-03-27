package Agentes;

import QLearning.EnvGear;
import QLearning.LocalQLearningUtils;
import QLearning.MonitorGear;
import QLearning.Politica;
import QLearning.QLearning;
import champ2011client.SensorModel;

public class DriverGearTrain extends AbstractTrainDriverBase {
    
private final int TICKS_COOLDOWN = 100;
private int ticksSinceLastShift = TICKS_COOLDOWN-50; // Nos aseguramos q la primera vez pueda cambiar
private MonitorGear monitorGear;


	public DriverGearTrain() {
		maxSpeedDist=150;
		maxSpeed=300;
		nMaxEpisodios = 200;
		System.out.println(LocalQLearningUtils.GREEN + "Iniciando DriverGearTrain..." + LocalQLearningUtils.RESET);
		this.env = new EnvGear(); 
		this.agent = new QLearning(this.env);
		this.pol = new Politica(env);
		agent.loadQTableCSV();

		monitorGear = new MonitorGear();
		System.out.println("Entrenamiento configurado: " + env.getName());
		startTrain();	
	}

	double lastReward = 0;

	@Override
    public int getGear(SensorModel sensors) {
        int currentGear = sensors.getGear();

        ticksSinceLastShift++;

   
        if (ticksSinceLastShift < TICKS_COOLDOWN ) {
			monitorGear.update(sensors, lastReward, currentGear);
            return currentGear; 
        }


        boolean isDone = env.isEpisodeDone(sensors);
        int currentState = env.discretizeState(sensors);
        double reward = env.calculateReward(sensors);

        int nextAction = -1;

        if (previousState != -1 && !isInTestMode) {
            agent.updatePlot(reward);
            agent.updateQTable(previousState, previousAction, reward, currentState, isDone);
        }

        if (isInTestMode) {
            nextAction = pol.getAccionIndex(currentState);
            histTest.registrarEvento(currentState, nextAction);
        } else {
            nextAction = agent.chooseAction(currentState);
            histTrain.registrarEvento(currentState, nextAction);
        }

        previousState = currentState;
        previousAction = nextAction;
		lastReward = reward;

      

		int accion = (int) env.getActionFromMap(nextAction)[0];


        int targetGear = currentGear + accion;

        // Limites físicos
        if (targetGear < 1) targetGear = 1;
        if (targetGear > 6) targetGear = 6;

		if (targetGear != currentGear) {
        ticksSinceLastShift = 0; 
		monitorGear.update(sensors, reward, targetGear);
		}
        return targetGear;
    }
}
package Agentes;

import QLearning.EnvSteer;
import QLearning.LocalQLearningUtils;
import QLearning.Politica;
import QLearning.QLearning;
import champ2011client.SensorModel;

public class DriverSteerTrain extends AbstractTrainDriverBase {

	public DriverSteerTrain() {
		nMaxEpisodios = 250;
		System.out.println(LocalQLearningUtils.GREEN + "Iniciando DriverSteerTrain..." + LocalQLearningUtils.RESET);
		this.env = new EnvSteer();
		this.agent = new QLearning(env);
		this.pol = new Politica(env);
		agent.loadQTableCSV();// Cargamos para volver a entrenar y ajustar mas
		System.out.println("Entrenamiento configurado: " + env.getName());

		System.out.println(agent);
		startTrain();
	}

	@Override
	public float getSteer(SensorModel sensors) {
		boolean isDone = env.isEpisodeDone(sensors);
		int currentState = env.discretizeState(sensors);
		double reward = env.calculateReward(sensors);

		int nextAction = -1;

		if (isInTestMode) {
			nextAction = pol.getAccionIndex(currentState);
			histTest.registrarEvento(currentState, nextAction);
		} else {
			// El agente elige la acción abstracta (int)
			nextAction = agent.chooseAction(currentState);
			histTrain.registrarEvento(currentState, nextAction);
		}

		
		previousState = currentState;
		previousAction = nextAction;

		// B. Aprender
		if (previousState != -1 && !isInTestMode) {
			agent.updatePlot(reward);
			agent.updateQTable(previousState, previousAction, reward, currentState, isDone);
		}

		return env.getActionFromMap(nextAction)[0];

	}

}

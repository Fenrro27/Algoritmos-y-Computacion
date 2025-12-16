package Agentes;

import java.util.Random;

import QLearning.EnvAccel;
import QLearning.LocalQLearningUtils;
import QLearning.Politica;
import QLearning.QLearning;
import champ2011client.SensorModel;

public class DriverAccelTrain extends AbstractTrainDriverBase {

	private Random rand = new Random();
	private float timeToWarmup = 3.0f + (20.0f - 3.0f) * rand.nextFloat();

	public DriverAccelTrain() {
		nMaxEpisodios = 500;
		maxSpeedDist=30;
		maxSpeed=40;
		
		System.out.println(LocalQLearningUtils.GREEN + "Iniciando DriverAccelTrain..." + LocalQLearningUtils.RESET);
		this.env = new EnvAccel();
		this.agent = new QLearning(env);
		this.pol = new Politica(env);
		agent.loadQTableCSV();// Cargamos para volver a entrenar y ajustar mas
		System.out.println("Entrenamiento configurado: " + env.getName());

		System.out.println(agent);
		startTrain();
	}

	@Override
	public void reset() {
		super.reset();
		if (!isInTestMode) {
			this.timeToWarmup = (20.0f) * rand.nextFloat();
		} else {
			this.timeToWarmup = 0.0f;
		}
	}

	@Override
	public float getAccel(SensorModel sensors) {
		boolean isDone = env.isEpisodeDone(sensors);
		int currentState = env.discretizeState(sensors);
		double reward = env.calculateReward(sensors);

		int nextAction = -1;

		if (isInTestMode) {
			nextAction = pol.getAccionIndex(currentState);
			histTest.registrarEvento(currentState, nextAction);
		} else if (timeToWarmup > sensors.getCurrentLapTime()) {
			System.out.print("\rTiempo de calentamiento: "
					+ String.format("%.2f/%.2f", sensors.getCurrentLapTime(), timeToWarmup));
			return super.getAccel(sensors); // Si es tiempo de calentamiento usamos el metodo del padre
		} else {
			// El agente elige la acción abstracta (int)
			nextAction = agent.chooseAction(currentState);
			histTrain.registrarEvento(currentState, nextAction);

			// B. Aprender
			if (previousState != -1) {
				agent.updatePlot(reward);
				agent.updateQTable(previousState, previousAction, reward, currentState, isDone);
			}

			previousState = currentState;
			previousAction = nextAction;

		}

		return env.getActionFromMap(nextAction)[0];

	}

}

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

	// Frame Skip / Action Skipping
	private final int SKIP_TICKS = 5;
	private int ticksSinceLastUpdate = 0;
	private float lastActionFloat = 0; // Para guardar la ultima accion ejecutada

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
		if (!isInTestMode && timeToWarmup > sensors.getCurrentLapTime()) {
			System.out.print("\rTiempo de calentamiento: "
					+ String.format("%.2f/%.2f", sensors.getCurrentLapTime(), timeToWarmup));
			return super.getAccel(sensors); // Si es tiempo de calentamiento usamos el metodo del padre
		}

		ticksSinceLastUpdate++;
		if (ticksSinceLastUpdate < SKIP_TICKS) {
			return lastActionFloat;
		}

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

			// B. Aprender
			if (previousState != -1) {
				agent.updatePlot(reward);
				agent.updateQTable(previousState, previousAction, reward, currentState, isDone);
			}

			previousState = currentState;
			previousAction = nextAction;

		}

		float accel = env.getActionFromMap(nextAction)[0];

		if (accel != lastActionFloat) {
			ticksSinceLastUpdate = 0;
		}
		lastActionFloat = accel;

		return accel;

	}

}

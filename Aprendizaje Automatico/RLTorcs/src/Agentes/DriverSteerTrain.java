package Agentes;

import java.util.Random;

import QLearning.EnvSteer;
import QLearning.LocalQLearningUtils;
import QLearning.Politica;
import QLearning.QLearning;
import champ2011client.SensorModel;

public class DriverSteerTrain extends AbstractTrainDriverBase {

	private Random rand = new Random();
	private float timeToWarmup = 3.0f + (20.0f - 3.0f) * rand.nextFloat();

	// Frame Skip / Action Skipping
	private final int SKIP_TICKS = 5;
	private int ticksSinceLastUpdate = 0;
	private int lastAction = -1; // Para guardar la ultima accion ejecutada

	public DriverSteerTrain() {
		nMaxEpisodios = 250;
		 maxSpeedDist=7;
		 maxSpeed=50;
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
	public void reset() {
		super.reset();
		if (!isInTestMode) {
			this.timeToWarmup = 3.0f + (20.0f - 3.0f) * rand.nextFloat();
			this.ticksSinceLastUpdate = 0;
		} else {
			this.timeToWarmup = 0.0f;
		}
	}

	@Override
	public float getSteer(SensorModel sensors) {
		boolean isDone = env.isEpisodeDone(sensors);
		int currentState = env.discretizeState(sensors);
		double reward = env.calculateReward(sensors);

		int nextAction = -1;

		if (isInTestMode) {
			System.out.println(agent);
			System.out.println(pol.verQtableString());
			nextAction = pol.getAccionIndex(currentState);
			histTest.registrarEvento(currentState, nextAction);
		} else if (timeToWarmup > sensors.getCurrentLapTime()) {
			System.out.print("\rTiempo de calentamiento: "
					+ String.format("%.2f/%.2f", sensors.getCurrentLapTime(), timeToWarmup));
			return super.getSteer(sensors); // Si es tiempo de calentamiento usamos el metodo del padre
		} else {
			// FRAME SKIP LOGIC
			if (ticksSinceLastUpdate < SKIP_TICKS && lastAction != -1) {
				ticksSinceLastUpdate++;
				// System.out.print("\rFrame Skip: " + ticksSinceLastUpdate + "/" + SKIP_TICKS);
				// Retornamos la ultima accion sin aprender nada nuevo ni registrar evento
				return env.getActionFromMap(lastAction)[0];
			}
			// Si nos toca ejecutar (o es la primera vez), reseteamos contador
			ticksSinceLastUpdate = 0;

			// El agente elige la acción abstracta (int)
			nextAction = agent.chooseAction(currentState);
			lastAction = nextAction; // Guardamos para el skip
			histTrain.registrarEvento(currentState, nextAction);

			// B. Aprender
			if (previousState != -1) {
				agent.updatePlot(reward);
				agent.updateQTable(previousState, previousAction, reward, currentState, isDone);
			}

			previousState = currentState;
			previousAction = nextAction;

		}

		// System.out.print("\rEstado: " + currentState + ", Acción: " + nextAction + "
		// ");

		return env.getActionFromMap(nextAction)[0];

	}

}

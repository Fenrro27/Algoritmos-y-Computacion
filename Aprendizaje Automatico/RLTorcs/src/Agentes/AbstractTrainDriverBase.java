package Agentes;

import QLearning.IEnvironment;
import QLearning.LocalQLearningUtils;
import QLearning.Politica;
import QLearning.QLearning;
import QLearning.MonitorHistograma;
import champ2011client.Action;
import champ2011client.SensorModel;

public abstract class AbstractTrainDriverBase extends DriverBase {

	// === 1. COMPONENTES DE Q-LEARNING ===
	protected QLearning agent;
	protected IEnvironment env;
	public Politica pol;
	public MonitorHistograma histTrain;
	public MonitorHistograma histTest;

	protected int nEpisodios = 0;
	protected int nMaxEpisodios = 20;
	protected int trainingInterval = 5;
	protected boolean isInTestMode = false;

	private boolean isRestarting = false;

	// === 2. VARIABLES DE ESTADO PARA EL APRENDIZAJE ===
	protected int previousState = -1;
	protected int previousAction = -1;

	// protected int currentLearnedAction = -1;

	public void startTrain() {
		histTest = new MonitorHistograma(env.getNumStates(), env.getNumActions(),
				"Test - Trainind de " + env.getName());
		histTrain = new MonitorHistograma(env.getNumStates(), env.getNumActions(),
				"Train - Trainind de " + env.getName());
		histTest.setVisible(true);
		histTrain.setVisible(true);
		
	}

	@Override
	public void reset() {
		isRestarting = false;
		env.reset();
		System.out.println(LocalQLearningUtils.YELLOW + "Episodio Finalizado. (" + (nEpisodios + 1) + "/"
				+ nMaxEpisodios + ")" + LocalQLearningUtils.RESET);
		nEpisodios++;

		// Reseteamos AQUÍ para evitar que entre en el siguiente tick
		previousState = -1;
		previousAction = -1;

		if (!isInTestMode) {
			agent.decayEpsilon();
			agent.saveQTableCSV();
			pol.savePolicyText(agent.getQTable());
		}
		if (nEpisodios >= nMaxEpisodios) {
			// Lanzamos excepcion
			System.out.println(LocalQLearningUtils.YELLOW + "!!! ALCANZADO MÁXIMO DE EPISODIOS (" + nMaxEpisodios
					+ ") !!!" + LocalQLearningUtils.RESET);
			System.out.println(LocalQLearningUtils.BLUE + "FIN DEL ENTRENAMIENTO: Se completaron " + nMaxEpisodios
					+ " episodios." + LocalQLearningUtils.RESET);
			System.exit(0);
		}

		System.out.println("\n------------------------------------------------");
		if (((nEpisodios + 1) % (trainingInterval)) == 0) { // Activamos el modo de entrenamiento
			pol.loadPolicyText();
			isInTestMode = true;
			System.out.println(LocalQLearningUtils.BLUE + "Iniciado Modo Test (Episodio " + (nEpisodios + 1) + ")"
					+ LocalQLearningUtils.RESET);
			System.out.println(pol);
		} else {
			System.out.println(LocalQLearningUtils.GREEN + "Reiniciando (Episodio " + (nEpisodios + 1) + ")"
					+ LocalQLearningUtils.RESET);
			isInTestMode = false;
		}
	}

	@Override
	public void shutdown() {
		System.out.println("Bye bye!");
	}

	// ===================================================================
	// === MÉTODO CLAVE: CONTROL (BUCLE DE APRENDIZAJE) ===
	// ===================================================================
	@Override
	public Action control(SensorModel sensors) {
		if (!isRestarting) {

			boolean isDone = env.isEpisodeDone(sensors);

			if (isDone) { 
				isRestarting = true;
				Action resetAction = new Action();
				resetAction.restartRace = true;
				return resetAction;
			}
			Action action = new Action();
			float steer = getSteer(sensors);
			int gear = getGear(sensors);
			float accel_and_brake = getAccel(sensors);

			if (steer < -1)
				steer = -1;
			if (steer > 1)
				steer = 1;
			float accel, brake;
			if (accel_and_brake > 0) {
				accel = accel_and_brake;
				brake = 0;
			} else {
				accel = 0;
				brake = filterABS(sensors, -accel_and_brake);
			}

			clutch = clutching(sensors, clutch);
			action.gear = gear;
			action.steering = steer;
			action.accelerate = accel;
			action.brake = brake;
			action.clutch = clutch;
			return action;
		}
		return new Action();
	}
}
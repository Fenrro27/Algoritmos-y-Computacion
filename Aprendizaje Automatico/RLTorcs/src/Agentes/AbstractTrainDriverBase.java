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
	protected IEnvironment env; // Tu AccelEnv
	public Politica pol;
	public MonitorHistograma histTrain;
	public MonitorHistograma histTest;

	protected int nEpisodios = 0;
	protected int nMaxEpisodios = 20;
	protected int trainingInterval = 10;
	protected boolean isInTestMode = false;

	private boolean isRestarting = false;

	// === 2. VARIABLES DE ESTADO PARA EL APRENDIZAJE ===
	protected int previousState = -1;
	protected int previousAction = -1;

	protected int currentLearnedAction = -1;

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

		// El episodio terminó (choque o salida)
		System.out.println(LocalQLearningUtils.YELLOW + "Episodio Finalizado. (" + (nEpisodios + 1) + "/"
				+ nMaxEpisodios + ")" + LocalQLearningUtils.RESET);
		nEpisodios++;

		// Reseteamos AQUÍ para evitar que entre en el siguiente tick
		previousState = -1;
		previousAction = -1;

		if (nEpisodios >= nMaxEpisodios) {
			// Lanzamos excepcion
			System.out.println(LocalQLearningUtils.YELLOW + "!!! ALCANZADO MÁXIMO DE EPISODIOS (" + nMaxEpisodios
					+ ") !!!" + LocalQLearningUtils.RESET);
			System.out.println("Guardando estado final del aprendizaje...");

			// 1. Guardar Tablas
			agent.saveQTableCSV();
			agent.savePolicyText();

			System.out.println(LocalQLearningUtils.BLUE + "FIN DEL ENTRENAMIENTO: Se completaron " + nMaxEpisodios
					+ " episodios." + LocalQLearningUtils.RESET);
			System.exit(0);
		}

		if (!isInTestMode) {
			agent.decayEpsilon(); // agent.decayEpsilon(0.95, 0.05); // Reducimos exploración
			agent.saveQTableCSV();
			agent.savePolicyText();
		}

		System.out.println("\n------------------------------------------------");
		if (((nEpisodios + 1) % (trainingInterval)) == 0) { // Activamos el modo de entrenamiento
			pol.loadPolicyText(null);
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
		// Aquí podrías guardar la Q-Table de aceleración
		// System.out.println("Guardando tabla Q...");
		// agent.saveQTableCSV();
		// agent.savePolicyText();
		System.out.println("Bye bye!");
	}

	// ===================================================================
	// === MÉTODO CLAVE: CONTROL (BUCLE DE APRENDIZAJE) ===
	// ===================================================================
	@Override
	public Action control(SensorModel sensors) {
		if (!isRestarting) {

			// --- 3. LÓGICA DE Q-LEARNING UNIVERSAL ---

			// A. Obtener estado y recompensa
			int currentState = env.discretizeState(sensors);
			double reward = env.calculateReward(sensors);
			boolean isDone = env.isEpisodeDone(sensors);
			// B. Aprender
			if (previousState != -1 && !isInTestMode) {
				agent.updatePlot(reward);
				agent.updateQTable(previousState, previousAction, reward, currentState, isDone);
			}

			// C. Decidir acción o Reiniciar
			if (isDone) { //
				isRestarting = true;
				Action resetAction = new Action();
				resetAction.restartRace = true;
				return resetAction;
			} else {

				int nextAction = -1;

				if (isInTestMode) {
					nextAction = pol.getAccionIndex(currentState);
					histTest.registrarEvento(currentState, nextAction);
				} else {
					// El agente elige la acción abstracta (int)
					nextAction = agent.chooseAction(currentState);
					histTrain.registrarEvento(currentState, nextAction);
				}

				// GUARDAMOS LA ACCIÓN EN LA VARIABLE DE CLASE
				// Las clases hijas leerán esto dentro de su Override de getAccel/getSteer
				this.currentLearnedAction = nextAction;

				previousState = currentState;
				previousAction = nextAction;
			}

			// D. EJECUTAR (Construcción estándar basada en métodos polimórficos)
			// Esto replica la lógica final de DriverBase.control, pero llamando a tus
			// métodos

			Action action = new Action();

			// 1. Obtener valores llamando a los métodos (uno de ellos estará sobrecargado
			// por el hijo)
			float steer = getSteer(sensors);
			int gear = getGear(sensors);
			float accel_and_brake = getAccel(sensors); // Puede venir de heurística o de Q-Learning

			// 2. Normalización de dirección (lógica original)
			if (steer < -1)
				steer = -1;
			if (steer > 1)
				steer = 1;

			// 3. Separación Acelerador/Freno (lógica original)
			float accel, brake;
			if (accel_and_brake > 0) {
				accel = accel_and_brake;
				brake = 0;
			} else {
				accel = 0;
				// Aplicar ABS al freno (lógica original)
				brake = filterABS(sensors, -accel_and_brake);
			}

			// 4. Embrague (lógica original)
			clutch = clutching(sensors, clutch);

			// 5. Asignar al objeto final
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
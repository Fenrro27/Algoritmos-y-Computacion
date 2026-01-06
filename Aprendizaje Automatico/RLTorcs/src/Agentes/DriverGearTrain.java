package Agentes;

import QLearning.EnvGear;
import QLearning.LocalQLearningUtils;
import QLearning.MonitorGear;
import QLearning.Politica;
import QLearning.QLearning;
import champ2011client.SensorModel;

public class DriverGearTrain extends AbstractTrainDriverBase {
    
private int ticksSinceLastShift = 0;
private final int TICKS_COOLDOWN = 400;
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

        // 1. ACTUALIZAR CONTADOR (Siempre sumamos 1 tick en cada llamada)
        ticksSinceLastShift++;

        // 2. VERIFICAR COOLDOWN
        // Si no han pasado suficientes ticks de simulación...
        if (ticksSinceLastShift < TICKS_COOLDOWN) {
			monitorGear.update(sensors, lastReward, currentGear);
            return currentGear; // Salimos sin pensar
        }

        // --- A PARTIR DE AQUI, EL AGENTE "DESPIERTA" ---

        boolean isDone = env.isEpisodeDone(sensors);
        int currentState = env.discretizeState(sensors);
        double reward = env.calculateReward(sensors);

        int nextAction = -1;

        // B. Aprender
        if (previousState != -1 && !isInTestMode) {
            agent.updatePlot(reward);
            agent.updateQTable(previousState, previousAction, reward, currentState, isDone);
        }

        // C. Decidir
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

        // 3. EJECUTAR LÓGICA Y RESETEAR CONTADOR SI ES NECESARIO
      

		int accion = (int) env.getActionFromMap(nextAction)[0];


        int targetGear = currentGear + accion;

        // Limites físicos
        if (targetGear < 1) targetGear = 1;
        if (targetGear > 6) targetGear = 6;

		if (targetGear != currentGear) {
        ticksSinceLastShift = 0; // ¡RESET!
		monitorGear.update(sensors, reward, targetGear);
		}
        return targetGear;
    }
}
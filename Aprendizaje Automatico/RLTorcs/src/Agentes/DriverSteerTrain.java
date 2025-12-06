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
	return env.getActionFromMap(this.currentLearnedAction)[0];
	
	}

}

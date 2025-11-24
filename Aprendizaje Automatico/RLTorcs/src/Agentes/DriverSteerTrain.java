package Agentes;

import QLearning.EnvSteer;
import QLearning.IEnvironment;
import QLearning.QLearning;
import champ2011client.SensorModel;

public class DriverSteerTrain extends AbstractTrainDriverBase{

	QLearning agent;
	IEnvironment env;
	
	
	public DriverSteerTrain() {
		System.out.println("Iniciando DriverSteerTrain...");
		env = new EnvSteer(0.1, 0.99, 0.4);
		agent = new QLearning(env);
		System.out.println("Entrenamiento configurado: " + env.getName());
	}
	
	
	@Override
	public float getSteer(SensorModel sensors) {

		return agent.chooseAction(env.discretizeState(sensors));
	}
	
}

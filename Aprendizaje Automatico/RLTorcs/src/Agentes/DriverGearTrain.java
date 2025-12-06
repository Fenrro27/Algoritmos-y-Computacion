package Agentes;

import QLearning.EnvGear;
import QLearning.LocalQLearningUtils;
import QLearning.Politica;
import QLearning.QLearning;
import champ2011client.SensorModel;

public class DriverGearTrain extends AbstractTrainDriverBase {
    
	public DriverGearTrain() {
		nMaxEpisodios = 200;
		System.out.println(LocalQLearningUtils.GREEN + "Iniciando DriverGearTrain..." + LocalQLearningUtils.RESET);
		this.env = new EnvGear(); // Politica - Alpha - Gamma - Epsilon inicial
		this.agent = new QLearning(this.env);
		this.pol = new Politica(env);
		agent.loadQTableCSV();//Cargamos para volver a entrenar y ajustar mas

		System.out.println("Entrenamiento configurado: " + env.getName());
	}

	@Override
	public int getGear(SensorModel sensors) {
		int newGear = sensors.getGear();
		
		 // 1. Lógica de Marchas (Controlada por el Agente Q-Learning)
        switch (this.currentLearnedAction) {
            case 0: // Downshift
            	newGear = newGear - 1;
                break;
            case 2: // Upshift
            	newGear = newGear + 1;
                break;
        }

        // Ajuste de límites de marcha (no más bajo que -1)
        if (newGear < 1) {
        	newGear = 1;
        }else if(newGear > 6){
        	newGear = 6;
        }
		return newGear;
	}

}
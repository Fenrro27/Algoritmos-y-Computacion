package Agentes;
import QLearning.EnvSteer;
import QLearning.MonitorHistograma;
import QLearning.IEnvironment;
import QLearning.Politica;
import champ2011client.SensorModel;

public class DriverSteerTest extends DriverBase{
	
	Politica politica;
	IEnvironment env;
	MonitorHistograma monitor;
	
	public DriverSteerTest() {
		System.out.println("Iniciando DriverSteerTest...");
		this.env = new EnvSteer();
	 maxSpeedDist=7;
	 maxSpeed=300;
		politica = new Politica(env);
		politica.loadPolicyText();
		System.out.println(politica);
		
		monitor = new MonitorHistograma(env.getNumStates(), env.getNumActions(), "Monitor Interactivo (Click para detalle)");
		monitor.setVisible(true);
	}

	@Override
	public float getSteer(SensorModel sensors) {
		float steerAngle;
		int state= env.discretizeState(sensors);
		int index = politica.getAccionIndex(state);
		
		monitor.registrarEvento(state, index);
		
		steerAngle=env.getActionFromMap(index)[0];
		
	System.out.println("Estado: "+state+", SteerAngle: "+steerAngle+" (Action "+index+")");
	return steerAngle;
	}

}

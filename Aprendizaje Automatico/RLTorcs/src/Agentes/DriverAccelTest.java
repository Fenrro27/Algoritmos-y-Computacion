package Agentes;
import QLearning.EnvAccel;
import QLearning.MonitorHistograma;
import QLearning.IEnvironment;
import QLearning.Politica;
import champ2011client.SensorModel;

public class DriverAccelTest extends DriverBase{
	
	Politica politica;
	IEnvironment env;
	MonitorHistograma monitor;
	
	public DriverAccelTest() {
		System.out.println("Iniciando DriverAccelTest...");
		this.env = new EnvAccel();
;
		politica = new Politica(env);
		politica.loadPolicyText(null);
		System.out.println(politica);
		
		monitor = new MonitorHistograma(env.getNumStates(), env.getNumActions(), "Monitor Interactivo (Click para detalle)");
		monitor.setVisible(true);
	}

	@Override
	public float getAccel(SensorModel sensors) {
		int state= env.discretizeState(sensors);
		int index = politica.getAccionIndex(state);
				monitor.registrarEvento(state, index);
		float accel = env.getActionFromMap(index)[0];
		
	System.out.println("Estado: "+state+", Accel: "+ accel +" (Action "+index+")");
	return accel;
	}

}

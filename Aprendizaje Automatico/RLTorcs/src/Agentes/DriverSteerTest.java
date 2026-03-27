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

		private final int SKIP_TICKS = 5;
	private int ticksSinceLastUpdate = SKIP_TICKS;
	private int lastAction = -1; // Para guardar la ultima accion ejecutada
	
	public DriverSteerTest() {
		System.out.println("Iniciando DriverSteerTest...");
		this.env = new EnvSteer();
	 maxSpeedDist=30;
	 maxSpeed=40;
		politica = new Politica(env);
		politica.loadPolicyText();
		System.out.println(politica);
		
		monitor = new MonitorHistograma(env.getNumStates(), env.getNumActions(), "Monitor Interactivo (Click para detalle)");
		monitor.setVisible(true);
	}

	@Override
	public float getSteer(SensorModel sensors) {
		ticksSinceLastUpdate++;
		if (ticksSinceLastUpdate < SKIP_TICKS) {
			return env.getActionFromMap(lastAction)[0];
		}

		int state= env.discretizeState(sensors);
		int index = politica.getAccionIndex(state);
		monitor.registrarEvento(state, index);
		float steerAngle = env.getActionFromMap(index)[0];

		if (index != lastAction) {
			ticksSinceLastUpdate = 0;
		}
		lastAction = index;

	System.out.println("Estado: "+state+", SteerAngle: "+steerAngle+" (Action "+index+")");
	return steerAngle;
	}

}

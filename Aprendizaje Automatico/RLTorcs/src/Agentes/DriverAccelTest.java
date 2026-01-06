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

	// Frame Skip / Action Skipping
	private final int SKIP_TICKS = 5;
	private int ticksSinceLastUpdate = 0;
	private float lastActionFloat = 0; // Para guardar la ultima accion ejecutada

	public DriverAccelTest() {
		System.out.println("Iniciando DriverAccelTest...");
		this.env = new EnvAccel();
		;
		politica = new Politica(env);
		politica.loadPolicyText();
		System.out.println(politica);

		monitor = new MonitorHistograma(env.getNumStates(), env.getNumActions(), "Monitor Interactivo (Click para detalle)");
		monitor.setVisible(true);
	}

	@Override
	public float getAccel(SensorModel sensors) {
		ticksSinceLastUpdate++;
		if (ticksSinceLastUpdate < SKIP_TICKS) {
			return lastActionFloat;
		}

		int state = env.discretizeState(sensors);
		int index = politica.getAccionIndex(state);
		monitor.registrarEvento(state, index);
		float accel = env.getActionFromMap(index)[0];

		if (accel != lastActionFloat) {
			ticksSinceLastUpdate = 0;
		}

		lastActionFloat = accel;

		System.out.println("Estado: " + state + ", Accel: " + accel + " (Action " + index + ")");
		return accel;
	}

}

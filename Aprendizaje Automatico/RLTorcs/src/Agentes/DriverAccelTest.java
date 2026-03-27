package Agentes;
import QLearning.EnvAccel;
import QLearning.MonitorHistograma;
import QLearning.IEnvironment;
import QLearning.Politica;
import champ2011client.SensorModel;

public class DriverAccelTest extends DriverBase{

	Politica politica;
	IEnvironment envAccel;
	MonitorHistograma monitor;

	private final int SKIP_TICKS = 5;
	private int ticksSinceLastUpdate = SKIP_TICKS;
	private float lastActionFloat = 0; // Para guardar la ultima accion ejecutada

	private int lastActionSteer = -1; // Para guardar la ultima accion ejecutada
		private final int TICKS_COOLDOWN = 100;

		private int ticksSinceLastShift = TICKS_COOLDOWN-50; // Nos aseguramos q la primera vez pueda cambiar


	public DriverAccelTest() {
		System.out.println("Iniciando DriverAccelTest...");
		this.envAccel = new EnvAccel();
		;
		politica = new Politica(envAccel);
		politica.loadPolicyText();
		System.out.println(politica);

		monitor = new MonitorHistograma(envAccel.getNumStates(), envAccel.getNumActions(), "Monitor Interactivo (Click para detalle)");
		monitor.setVisible(true);
	}

	@Override
	public float getAccel(SensorModel sensors) {
		ticksSinceLastUpdate++;
		if (ticksSinceLastUpdate < SKIP_TICKS) {
			return lastActionFloat;
		}

		int state = envAccel.discretizeState(sensors);
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

@Override
	public float getSteer(SensorModel sensors) {
		ticksSinceLastUpdate++;
		if (ticksSinceLastUpdate < SKIP_TICKS) {
			return env.getActionFromMap(lastActionSteer)[0];
		}

		int state= env.discretizeState(sensors);
		int index = politica.getAccionIndex(state);
		monitor.registrarEvento(state, index);
		float steerAngle = env.getActionFromMap(index)[0];

		if (index != lastActionSteer) {
			ticksSinceLastUpdate = 0;
		}
		lastActionSteer = index;

	System.out.println("Estado: "+state+", SteerAngle: "+steerAngle+" (Action "+index+")");
	return steerAngle;
	}



	@Override
	public int getGear(SensorModel sensors) {
		int state = env.discretizeState(sensors);
		int index = politica.getAccionIndex(state);

        int currentGear = sensors.getGear();
		ticksSinceLastShift++;

       
        if (ticksSinceLastShift < TICKS_COOLDOWN) {
            return currentGear; 
        }

		monitor.registrarEvento(state, index);
		int accion = (int) env.getActionFromMap(index)[0];

		

		
        int targetGear = currentGear + accion;

        // Limites físicos
        if (targetGear < 1) targetGear = 1;
        if (targetGear > 6) targetGear = 6;

        // Solo reseteamos cooldown si hubo cambio real
        if (targetGear != currentGear) {
       
        ticksSinceLastShift = 0; 
		}
        return targetGear;

	}

}

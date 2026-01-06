package Agentes;

import QLearning.EnvGear;
import QLearning.IEnvironment;
import QLearning.MonitorHistograma;
import QLearning.Politica;
import champ2011client.SensorModel;

public class DriverGearTest extends DriverBase {

	Politica politica;
	IEnvironment env;
	MonitorHistograma monitor;
	private int ticksSinceLastShift = 0;
	private final int TICKS_COOLDOWN = 400;

	public DriverGearTest() {
	 maxSpeedDist=150;
		maxSpeed=300;
		System.out.println("Iniciando DriverGearTest...");
		this.env = new EnvGear();
		;
		politica = new Politica(env);
		politica.loadPolicyText();

		monitor = new MonitorHistograma(env.getNumStates(), env.getNumActions(),
				"Monitor Interactivo (Click para detalle)");
		monitor.setVisible(true);
	}

	@Override
	public int getGear(SensorModel sensors) {
		int state = env.discretizeState(sensors);
		int index = politica.getAccionIndex(state);

        int currentGear = sensors.getGear();
		ticksSinceLastShift++;

        // 2. VERIFICAR COOLDOWN
        // Si no han pasado suficientes ticks de simulación...
        if (ticksSinceLastShift < TICKS_COOLDOWN) {
            return currentGear; // Salimos sin pensar
        }

		monitor.registrarEvento(state, index);
		int accion = (int) env.getActionFromMap(index)[0];

		

		
        int targetGear = currentGear + accion;

        // Limites físicos
        if (targetGear < 1) targetGear = 1;
        if (targetGear > 6) targetGear = 6;

        // Solo reseteamos cooldown si hubo cambio real
        if (targetGear != currentGear) {
       
        ticksSinceLastShift = 0; // ¡RESET!
		}
        return targetGear;

	}

}

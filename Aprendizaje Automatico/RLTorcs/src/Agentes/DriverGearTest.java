package Agentes;
import QLearning.EnvGear;
import QLearning.IEnvironment;
import QLearning.Politica;
import champ2011client.SensorModel;

public class DriverGearTest extends DriverBase{
	
	Politica politica;
	IEnvironment env;
	
	public DriverGearTest() {
		System.out.println("Iniciando DriverGearTest...");
		this.env = new EnvGear();
;
		politica = new Politica(env);
		politica.loadPolicyText();
	}

	@Override
	public int getGear(SensorModel sensors){
		int gear = sensors.getGear();
		int discreteAction = (int)politica.getAccionValues(env.discretizeState(sensors))[0];
	    switch (discreteAction) {
        case 0: // Downshift
            gear = gear - 1;
            break;
        case 2: // Upshift
            gear = gear + 1;
            break;
    }

		if(gear<1) gear=1;
		else if (gear>6) gear = 6;
		
		return gear;
	
	}

}

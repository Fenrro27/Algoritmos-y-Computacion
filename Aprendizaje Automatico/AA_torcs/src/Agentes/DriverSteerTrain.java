package Agentes;

import champ2011client.Controller;
import champ2011client.Controller.Stage;
import QLearning.qLearningSteer;
import champ2011client.Action;
import champ2011client.SensorModel;

public class DriverSteerTrain extends Controller {
	
    private qLearningSteer steerAgent = new qLearningSteer();

    final int[] gearUp = {5000,6000,6000,6500,7000,0};
    final int[] gearDown = {0,2500,3000,3000,3500,3500};

    final int stuckTime = 25;
    final float stuckAngle = (float) 0.523598775; // PI/6

    final float maxSpeedDist = 70;
    final float maxSpeed = 150;
    final float sin5 = (float) 0.08716;
    final float cos5 = (float) 0.99619;

    final float steerLock = (float) 0.785398;
    final float wheelRadius[] = {(float) 0.3179,(float) 0.3179,(float) 0.3276,(float) 0.3276};
    final float absSlip = 2.0f;
    final float absRange = 3.0f;
    final float absMinSpeed = 3.0f;

    final float clutchMax = 0.5f;
    final float clutchDelta = 0.05f;
    final float clutchRange = 0.82f;
    final float clutchDeltaTime = 0.02f;
    final float clutchDeltaRaced = 10;
    final float clutchDec = 0.01f;
    final float clutchMaxModifier = 1.3f;
    final float clutchMaxTime = 1.5f;
	
    private int stuck = 0;
    private int stuckEpisodes = 0; // Nuevo contador
    private float clutch = 0;

    @Override
    public void reset() {
        System.out.println("Restarting the race!");
        steerAgent.saveQTableCSV();
        stuckEpisodes = 0;
    }

    @Override
    public void shutdown() {
        System.out.println("Bye bye!");
    }

    private int getGear(SensorModel sensors) {
        int gear = sensors.getGear();
        double rpm = sensors.getRPM();
        if (gear < 1) return 1;
        if (gear < 6 && rpm >= gearUp[gear - 1]) return gear + 1;
        if (gear > 1 && rpm <= gearDown[gear - 1]) return gear - 1;
        return gear;
    }

    private float getSteer(SensorModel sensors) {
        return steerAgent.chooseSteer(sensors);
    }

    private float getAccel(SensorModel sensors) {
        if (sensors.getTrackPosition() < 1 && sensors.getTrackPosition() > -1) {
            float rx = (float) sensors.getTrackEdgeSensors()[10];
            float c = (float) sensors.getTrackEdgeSensors()[9];
            float lx = (float) sensors.getTrackEdgeSensors()[8];
            float targetSpeed;

            if (c > maxSpeedDist || (c >= rx && c >= lx)) targetSpeed = maxSpeed;
            else {
                float h = c * sin5;
                float b = (rx > lx) ? rx - c * cos5 : lx - c * cos5;
                float sinAngle = b * b / (h * h + b * b);
                targetSpeed = maxSpeed * (c * sinAngle / maxSpeedDist);
            }
            return (float) (2 / (1 + Math.exp(sensors.getSpeed() - targetSpeed)) - 1);
        }
        return 0.3f;
    }

    @Override
    public Action control(SensorModel sensors) {
        if (Math.abs(sensors.getAngleToTrackAxis()) > stuckAngle) stuck++;
        else stuck = 0;

        // --- Política de stuck ---
        if (stuck > stuckTime) {
            stuckEpisodes++;
            System.out.println("Stuck episode " + stuckEpisodes);

            if (stuckEpisodes >= 300) {
                Action restart = new Action();
                restart.restartRace = true;
                stuckEpisodes = 0;
                steerAgent.resetRaceFlag();
                return restart;
            }

            float steer = (float) (-sensors.getAngleToTrackAxis() / steerLock);
            int gear = -1;
            if (sensors.getAngleToTrackAxis() * sensors.getTrackPosition() > 0) {
                gear = 1;
                steer = -steer;
            }
            clutch = clutching(sensors, clutch);
            Action action = new Action();
            action.gear = gear;
            action.steering = steer;
            action.accelerate = 1.0f;
            action.brake = 0;
            action.clutch = clutch;
            return action;
        }

        // Reset del contador si el coche no está atascado
        if (stuck == 0) stuckEpisodes = 0;

        // --- Normal control ---
        float accel_and_brake = getAccel(sensors);
        int gear = getGear(sensors);
        float steer = getSteer(sensors);

        if (steerAgent.shouldRestartRace()) {
            Action restart = new Action();
            restart.restartRace = true;
            steerAgent.resetRaceFlag();
            return restart;
        }

        steer = Math.max(-1, Math.min(1, steer));

        float accel = accel_and_brake > 0 ? accel_and_brake : 0;
        float brake = accel_and_brake < 0 ? filterABS(sensors, -accel_and_brake) : 0;

        clutch = clutching(sensors, clutch);

        Action action = new Action();
        action.gear = gear;
        action.steering = steer;
        action.accelerate = accel;
        action.brake = brake;
        action.clutch = clutch;
        return action;
    }

    private float filterABS(SensorModel sensors, float brake) {
        float speed = (float) (sensors.getSpeed() / 3.6);
        if (speed < absMinSpeed) return brake;
        float slip = 0;
        for (int i = 0; i < 4; i++)
            slip += sensors.getWheelSpinVelocity()[i] * wheelRadius[i];
        slip = speed - slip / 4f;
        if (slip > absSlip) brake -= (slip - absSlip) / absRange;
        return Math.max(0, brake);
    }

    private float clutching(SensorModel sensors, float clutch) {
        float maxClutch = clutchMax;
        if (sensors.getCurrentLapTime() < clutchDeltaTime && getStage() == Stage.RACE && sensors.getDistanceRaced() < clutchDeltaRaced)
            clutch = maxClutch;

        if (clutch > 0) {
            double delta = clutchDelta;
            if (sensors.getGear() < 2) {
                delta /= 2;
                maxClutch *= clutchMaxModifier;
                if (sensors.getCurrentLapTime() < clutchMaxTime)
                    clutch = maxClutch;
            }
            clutch = Math.min(maxClutch, clutch);
            if (clutch != maxClutch)
                clutch = Math.max(0, clutch - (float) delta);
            else
                clutch -= clutchDec;
        }
        return clutch;
    }

    @Override
    public float[] initAngles() {
        float[] angles = new float[19];
        for (int i = 0; i < 5; i++) {
            angles[i] = -90 + i * 15;
            angles[18 - i] = 90 - i * 15;
        }
        for (int i = 5; i < 9; i++) {
            angles[i] = -20 + (i - 5) * 5;
            angles[18 - i] = 20 - (i - 5) * 5;
        }
        angles[9] = 0;
        return angles;
    }
}

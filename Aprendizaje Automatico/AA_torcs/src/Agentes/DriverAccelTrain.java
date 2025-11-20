package Agentes;

import champ2011client.Controller;
import champ2011client.Controller.Stage;
import champ2011client.Action;
import champ2011client.SensorModel;
import QLearning.qLearningAccel;

public class DriverAccelTrain extends Controller {

    private qLearningAccel accelAgent = new qLearningAccel();

    /* Gear Changing Constants */
    final int[] gearUp = {5000,6000,6000,6500,7000,0};
    final int[] gearDown = {0,2500,3000,3000,3500,3500};

    /* Stuck constants */
    final int stuckTime = 25;
    final float stuckAngle = 0.523598775f; // PI/6

    /* ABS Filter */
    final float wheelRadius[] = {0.3179f,0.3179f,0.3276f,0.3276f};
    final float absSlip = 2.0f;
    final float absRange = 3.0f;
    final float absMinSpeed = 3.0f;

    /* Steering */
    final float steerLock = 0.785398f;
    final float steerSensitivityOffset = 80.0f;
    final float wheelSensitivityCoeff = 1;

    /* Clutch */
    final float clutchMax = 0.5f;
    final float clutchDelta = 0.05f;
    final float clutchDeltaTime = 0.02f;
    final float clutchDeltaRaced = 10;
    final float clutchDec = 0.01f;
    final float clutchMaxModifier = 1.3f;
    final float clutchMaxTime = 1.5f;

    private int stuck = 0;
    private float clutch = 0;

    /* Episodios */
    private int stuckEpisodes = 0;

    /* No avance */
    private double lastDistance = 0.0;
    private int noProgressCount = 0;
    private int noProgressEpisodes = 0;


    public DriverAccelTrain() {
        System.out.println("Iniciando Entrenamiento qAccel");
        accelAgent.loadQTableCSV(false);
    }

    @Override
    public void reset() {
        System.out.println("Restarting the race!");
        accelAgent.saveQTableCSV();
        stuckEpisodes = 0;
        noProgressEpisodes = 0;
        noProgressCount = 0;
        lastDistance = 0;
    }

    @Override
    public void shutdown() {
        System.out.println("Bye bye!");
        accelAgent.finishTraining();
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
        float targetAngle = (float)(sensors.getAngleToTrackAxis() - sensors.getTrackPosition()*0.5);
        if (sensors.getSpeed() > steerSensitivityOffset)
            return (float)( targetAngle /
                (steerLock * (sensors.getSpeed() - steerSensitivityOffset) * wheelSensitivityCoeff) );
        return targetAngle / steerLock;
    }


    @Override
    public Action control(SensorModel sensors) {

        /* === DETECCIÓN STUCK === */
        if (Math.abs(sensors.getAngleToTrackAxis()) > stuckAngle) stuck++;
        else stuck = 0;

        if (stuck > stuckTime) {

            stuckEpisodes++;
            System.out.println("Stuck episode " + stuckEpisodes);

            if (stuckEpisodes >= 800) {
                System.out.println("Reiniciando por atasco excesivo.");
                Action restart = new Action();
                restart.restartRace = true;
                stuckEpisodes = 0;
                accelAgent.resetRaceFlag(sensors);
                return restart;
            }

            float steer = (float)(-sensors.getAngleToTrackAxis() / steerLock);
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

        if (stuck == 0) stuckEpisodes = 0;


        /* === DETECCIÓN NO AVANCE === */
        final double minSpeed = 10.0;
        final double minDeltaDist = 0.5;
        final int maxNoProgressCount = 80;
        final int maxNoProgressEpisodes = 10;

        double currentDist = sensors.getDistanceRaced();
        double delta = currentDist - lastDistance;

        if (sensors.getSpeed() < minSpeed && delta < minDeltaDist)
            noProgressCount++;
        else if (noProgressCount > 0)
            noProgressCount--;

        if (noProgressCount > maxNoProgressCount) {
            noProgressEpisodes++;
            noProgressCount = 0;
            System.out.println("No advance episode " + noProgressEpisodes);
        }

        if (noProgressEpisodes >= maxNoProgressEpisodes) {
            System.out.println("Reinicio por no avance.");
            Action restart = new Action();
            restart.restartRace = true;
            noProgressEpisodes = 0;
            noProgressCount = 0;
            accelAgent.resetRaceFlag(sensors);
            lastDistance = currentDist;
            return restart;
        }

        lastDistance = currentDist;


        /* === AGENTE: ACCIÓN === */
        double[] accel_brake = accelAgent.chooseAccel(sensors);
        float accel = (float) accel_brake[0];
        float brake = filterABS(sensors, (float) accel_brake[1]);


        if (accelAgent.shouldRestartRace()) {
            Action restart = new Action();
            restart.restartRace = true;
            accelAgent.resetRaceFlag(sensors);
            return restart;
        }


        /* === CONTROL DEL COCHE === */

        int gear = getGear(sensors);
        float steer = getSteer(sensors);
        steer = Math.max(-1, Math.min(1, steer));

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
        float speed = (float)(sensors.getSpeed() / 3.6);
        if (speed < absMinSpeed) return brake;

        float slip = 0;
        for (int i = 0; i < 4; i++)
            slip += sensors.getWheelSpinVelocity()[i] * wheelRadius[i];

        slip = speed - slip / 4f;

        if (slip > absSlip)
            brake = brake - (slip - absSlip) / absRange;

        return Math.max(brake, 0);
    }

    float clutching(SensorModel sensors, float clutch) {
        float maxClutch = clutchMax;

        if (sensors.getCurrentLapTime() < clutchDeltaTime &&
            getStage() == Stage.RACE &&
            sensors.getDistanceRaced() < clutchDeltaRaced)
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

            if (clutch != maxClutch) {
                clutch = (float)Math.max(0, clutch - delta);
            } else {
                clutch -= clutchDec;
            }
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

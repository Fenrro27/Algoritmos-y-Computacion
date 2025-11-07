package QLearning;

import champ2011client.SensorModel;

public class qLearningGear extends qLearningBase {

    private final int[] rpmRanges = {2500, 7000, 9000};
    private final int rpmBins = rpmRanges.length;
    private final int numActionsLocal = 3;

    private int lastState = -1;
    private int lastAction = -1;
    
    private final GearMonitor monitor = new GearMonitor();
    private int iteration = 0;

    public qLearningGear() {
    	epsilonDecay = 0.999;
        initQTable(rpmBins, numActionsLocal);
        loadQTableCSV(false);
    }

    private int rpmToBin(double rpm) {
        for (int i = 0; i < rpmRanges.length; i++) {
            if (rpm < rpmRanges[i]) return i;
        }
        return rpmRanges.length - 1;
    }

    @Override
    protected int getStateIndex(Object s) {
        SensorModel sensors = (SensorModel) s;
        return rpmToBin(sensors.getRPM());
    }

    @Override
    protected double computeReward(Object s, int action) {
        SensorModel sensors = (SensorModel) s;
        double rpm = sensors.getRPM();
        double low = 2500, high = 7000, mid = (low + high) / 2.0;
        double r=0;
        switch (action) {
            case 0: r = (rpm < low) ? 1.0 : -Math.min(1.0, (rpm - low) / 1000); break;
            case 1: r = 1.0 - Math.abs(rpm - mid) / (high - low); break;
            case 2: r = (rpm > high) ? 1.0 : -Math.min(1.0, (high - rpm) / 1000); break;
            default: r = 0; break;
        }
        return r;
    }

    public int chooseGear(SensorModel sensors) {
        int state = getStateIndex(sensors);
        int action = chooseAction(state);
        int currentGear = sensors.getGear();
        int nextGear = currentGear;

        if (action == 0 && currentGear > 1) nextGear--;
        else if (action == 2 && currentGear < 6) nextGear++;

        if (lastState != -1) {
            double reward = computeReward(sensors, lastAction);
            int nextState = getStateIndex(sensors);
            updateQ(lastState, lastAction, reward, nextState);
        }

        lastState = state;
        lastAction = action;

        iterationCount++;
        decayEpsilon();
        if (iterationCount % 500 == 0) saveQTableCSV();

        monitor.updateData(iteration++, sensors.getRPM(), sensors.getGear());
        return nextGear;
    }

    @Override
    protected String getQTableName() {
        return "QTable_Gear";
    }
}

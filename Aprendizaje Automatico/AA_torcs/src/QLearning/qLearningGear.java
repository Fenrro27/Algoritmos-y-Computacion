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

    double lastRpm;
    @Override
    protected double computeReward(Object s, int action) {
        SensorModel sensors = (SensorModel) s;

	    double rpm = sensors.getRPM();
	    double prevRpm = lastRpm;               // debes mantener este valor del paso anterior
	    lastRpm = rpm;

	    double low = 2500;
	    double high = 7000;
	    double rpmOpt = (low + high) / 2.0;     // 4750
	    double halfRange = (high - low) / 2.0;  // 2250

	    // --- shaping principal (cuanto más cerca del óptimo, mejor)
	    double dist = Math.abs(rpm - rpmOpt);
	    double normDist = dist / halfRange;     // normalizado 0..1
	    double shaping = Math.max(0.0, 1.0 - normDist);

	    // --- penalización por fuera del rango útil (futuro malo si el motor no está usable)
	    double outPenalty = 0.0;
	    if (rpm < low) {
	        outPenalty = -0.3 * ((low - rpm) / low);
	    }
	    if (rpm > high) {
	        outPenalty = -0.3 * ((rpm - high) / high);
	    }

	    // --- penalización por zona crítica (estados futuros muy malos)
	    double criticalPenalty = 0.0;
	    if (rpm < 1200) criticalPenalty -= 0.6;   // casi stall
	    if (rpm > 8500) criticalPenalty -= 0.6;   // casi over-rev real

	    // --- penalización por oscilación (importe depende del salto respecto al frame previo)
	    double swing = Math.abs(rpm - prevRpm) / halfRange;   // normalizado
	    double swingPenalty = -0.4 * Math.max(0.0, swing - 0.10);
	    // swing > 10% del rango útil indica inestabilidad → peor para futuros Q-values

	    // --- combinación final
	    double reward = shaping + outPenalty + criticalPenalty + swingPenalty;

	    // clipping para estabilidad numérica
	    if (reward > 1.0) reward = 1.0;
	    if (reward < -1.0) reward = -1.0;

	    return reward;
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

    @Override
    protected boolean checkSuccess(Object s) {
        SensorModel sensors = (SensorModel) s;
        double rpm = sensors.getRPM();

        double low = 2500;
        double high = 7000;

        // Éxito si la RPM está dentro del rango ideal
        return rpm >= low && rpm <= high;
    }

}

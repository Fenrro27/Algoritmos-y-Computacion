package QLearning;

public class StepResult {
    public final int nextState;
    public final double reward;
    public final boolean isDone;

    public StepResult(int nextState, double reward, boolean isDone) {
        this.nextState = nextState;
        this.reward = reward;
        this.isDone = isDone;
    }
}
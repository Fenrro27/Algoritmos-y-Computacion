package si2024.kevinjesusbandaalu.p06;

import java.util.ArrayList;
import java.util.List;

public class Plan {
    public List<String> initialStates;
    public List<String> finalStates;
    public List<Acciones> actions;

    public Plan() {
        initialStates = new ArrayList<>();
        finalStates = new ArrayList<>();
        actions = new ArrayList<>();
    }
    
   
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Initial States: ").append(initialStates).append("\n");
        sb.append("Final States: ").append(finalStates).append("\n");
        sb.append("Actions:\n");
        for (Acciones action : actions) {
            sb.append(action).append("\n");
        }
        return sb.toString();
    }
}

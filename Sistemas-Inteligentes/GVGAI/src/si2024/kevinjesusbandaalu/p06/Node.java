package si2024.kevinjesusbandaalu.p06;

import java.util.List;

public class Node {

	public static enum NodeType {
	    OPERATOR,
	    GOAL,
	    GOAL_CONJUNCTION
	}

	 NodeType type;
	    String goal;
	    Acciones action;
	    List<String> goals;

	    public Node(String goal, NodeType type) {
	        this.goal = goal;
	        this.type = type;
	    }

	    public Node(Acciones action, NodeType type) {
	        this.action = action;
	        this.type = type;
	    }

	    public Node(List<String> goals, NodeType type) {
	        this.goals = goals;
	        this.type = type;
	    }

	    @Override
	    public String toString() {
	        return "Node{type=" + type + ", goal='" + goal + '\'' +
	               ", action=" + action + ", goals=" + goals + '}';
	    }
	
}

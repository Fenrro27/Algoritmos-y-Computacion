package si2024.kevinjesusbandaalu.p06;

import java.util.List;

public class Acciones {
	String name;
    List<String> prerequisites;
    List<String> additions;
    List<String> deletions;

    public Acciones(String name, List<String> prerequisites, List<String> additions, List<String> deletions) {
        this.name = name;
        this.prerequisites = prerequisites;
        this.additions = additions;
        this.deletions = deletions;
    }

    @Override
    public String toString() {
        return "Action{name='" + name + '\'' +
               ", prerequisites=" + prerequisites +
               ", additions=" + additions +
               ", deletions=" + deletions + '}';
    }
    
    public boolean esAccion(String s) {
    	return s == this.name;
    }
    
    @Override
    public boolean equals(Object obj) {
    	return this.name==((Acciones)obj).name;
    }

}

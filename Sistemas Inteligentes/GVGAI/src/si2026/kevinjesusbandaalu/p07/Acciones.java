package si2026.kevinjesusbandaalu.p07;

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
    	return this.name.equals(s);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Acciones other = (Acciones) obj;
    	return this.name.equals(other.name);
    }

}

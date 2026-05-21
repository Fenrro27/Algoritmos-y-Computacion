import java.util.*;

public class AC3 {
    private List<iConstraint> constraints;

    public AC3(List<iConstraint> constraints) {
        this.constraints = constraints;
    }

    public boolean solve() {
        Queue<iConstraint> queue = new LinkedList<>(constraints);
        // Usamos un Set para que las búsquedas queue.contains() pasen de O(N) a O(1)
        Set<iConstraint> inQueue = new HashSet<>(constraints);
        
        while (!queue.isEmpty()) {
            iConstraint sc = queue.poll();
            inQueue.remove(sc);//--
            
            // pruneDomains devuelve true si algún dominio de nodo en esta restricción fue modificado
            if (sc.pruneDomains()) {
               // Si el dominio cambió, alertamos a TODAS las restricciones que comparten este nodo
                for (Node n : sc.getNodes()) {
                    for (iConstraint related : n.relatedConstraints) {
                        if (related != sc && !inQueue.contains(related)) {
                            queue.add(related);
                            inQueue.add(related);
                        }
                    }
                }
            }

            // Comprobar si alguna restricción es ahora imposible de satisfacer
            if (!sc.canBeSatisfied()) {
                return false;
            }
        }
        return true;
    }
}
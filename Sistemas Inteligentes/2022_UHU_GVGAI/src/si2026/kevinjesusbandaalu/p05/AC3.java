import java.util.*;

public class AC3 {
    private List<SumConstraint> constraints;

    public AC3(List<SumConstraint> constraints) {
        this.constraints = constraints;
    }

    public boolean solve() {
        Queue<SumConstraint> queue = new LinkedList<>(constraints);
        
        while (!queue.isEmpty()) {
            SumConstraint sc = queue.poll();
            
            // pruneDomains devuelve true si algún dominio de nodo en esta restricción fue modificado
            if (sc.pruneDomains()) {
                // Si se modificó un dominio, necesitamos volver a verificar todas las restricciones
                // que contienen los nodos modificados (excepto la actual).
                // Para simplificar, añadimos todas las restricciones que comparten nodos con sc.
                for (Node n : sc.nodes) {
                    if (n.rowConstraint != null && n.rowConstraint != sc) {
                        if (!queue.contains(n.rowConstraint)) queue.add(n.rowConstraint);
                    }
                    if (n.colConstraint != null && n.colConstraint != sc) {
                        if (!queue.contains(n.colConstraint)) queue.add(n.colConstraint);
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
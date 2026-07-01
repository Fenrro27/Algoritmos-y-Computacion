import java.util.ArrayList;
import java.util.List;

class Node {
    // Coordenadas o identificadores de la variable dentro del problema (Grid)
    int row;
    int col;
    int originalValue; // Dato específico del problema actual
    
    /**
     * El dominio es una lista de valores posibles:
     * 0: No seleccionado (se representará como un punto '.')
     * 1: Seleccionado (se representará con el número originalValue)
     */
    List<Integer> domain; 
    
    /**
     * ENLACE DINÁMICO (Grafo de Restricciones):
     * Lista de todas las reglas del juego que vigilan a esta celda concreta.
     * Un nodo puede estar vigilado por infinitas restricciones a la vez.
     */
    List<iConstraint> relatedConstraints;

    Node(int row, int col, int value) {
        this.row = row;
        this.col = col;
        this.originalValue = value;
        this.relatedConstraints = new ArrayList<>();

        // MODIFICAR SEGÚN EL DOMINIO DEL PROBLEMA
        this.domain = new ArrayList<>();
        this.domain.add(0); // Opción Punto
        this.domain.add(1); // Opción Número

        // Ejemplo Sudoku: for(int i=1; i<=9; i++) this.domain.add(i);
        // Ejemplo Mapas:  this.domain.addAll(Arrays.asList(ROJO, VERDE, AZUL));
    }

    /**
     * Atajo para saber si la celda ya tiene un valor definitivo asignado.
     */
    boolean isFixed() {
        return domain.size() == 1;
    }

    // --- Métodos semánticos (Eliminar o cambiar si cambia el tipo de problema) ---
    boolean isSelected() {
        return domain.size() == 1 && domain.get(0) == 1;
    }

    boolean isDot() {
        return domain.size() == 1 && domain.get(0) == 0;
    }

    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }
}

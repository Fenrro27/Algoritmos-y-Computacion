package si2026.kevinjesusbandaalu.p04;

import tools.Vector2d;
import java.util.Objects;

public class Node {
    public Vector2d pos;
    public Node parent;
    public double g; // Coste desde el origen
    public double h; // Heurística hasta el destino

    public Node(Vector2d pos, Node parent, double g, double h) {
        this.pos = pos;
        this.parent = parent;
        this.g = g;
        this.h = h;
    }

    public double getF() { return g + h; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return (int)pos.x == (int)node.pos.x && (int)pos.y == (int)node.pos.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash((int)pos.x, (int)pos.y);
    }
}
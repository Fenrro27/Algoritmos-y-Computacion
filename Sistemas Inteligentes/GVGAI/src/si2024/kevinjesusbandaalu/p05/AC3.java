package si2024.kevinjesusbandaalu.p05;

import java.util.ArrayList;
import java.util.Arrays;

class Arc {
	Node node1;
	Node node2;

	Arc(Node n1, Node n2) {
		node1 = n1;
		node2 = n2;
	}
}

public class AC3 {
	private Node[][] nodes;

	private String solution;
	private ArrayList<Arc> arcs = new ArrayList<>();

	public AC3(Node[][] nodes) {

//long ini = System.currentTimeMillis();
		this.nodes = nodes;

		for (int i = 0; i < 9; i++) {
			addRowArcs(i);
			addColArcs(i);
			addBoxArcs(i);
		}

		while (!arcs.isEmpty()) {
			Arc arch = arcs.remove(0);
			if (!establishAC(arch)) {
				System.out.println("no solution.");
				break;
			}
		}

		int result = Node.judgeState(nodes);
		if (result == -1) {
			System.out.println("Error, no solucion alcanzable");
			// Node.printNodes(nodes);
		}
		if (result == 1) {
			// long ini2 = System.currentTimeMillis();
			Backtracking bt = new Backtracking(nodes);
			solution = bt.resolver();
			// System.out.println("Tiempo Backtracking: "+(System.currentTimeMillis()-ini2));

		}else {
			solution = Node.toString(nodes);
		}

		// System.out.println("Tiempo AC3: "+(System.currentTimeMillis()-ini));
	}

	public String resolver() {

		return solution;
	}

	void addRowArcs(int row) {
		for (int i = 0; i < 8; i++) {
			for (int j = i + 1; j < 9; j++) {
				arcs.add(new Arc(nodes[row][i], nodes[row][j]));
				arcs.add(new Arc(nodes[row][j], nodes[row][i]));
			}
		}
	}

	void addColArcs(int col) {
		for (int i = 0; i < 8; i++) {
			for (int j = i + 1; j < 9; j++) {
				arcs.add(new Arc(nodes[i][col], nodes[j][col]));
				arcs.add(new Arc(nodes[j][col], nodes[i][col]));
			}
		}
	}

	void addBoxArcs(int box) {
		// add nodes which are in the same box together
		ArrayList<Node> nodesBox = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (nodes[i][j].box == box) {
					nodesBox.add(nodes[i][j]);
				}
			}
		}
		for (int i = 0; i < 8; i++) {
			for (int j = i + 1; j < 9; j++) {
				arcs.add(new Arc(nodesBox.get(i), nodesBox.get(j)));
				arcs.add(new Arc(nodesBox.get(j), nodesBox.get(i)));
			}
		}
	}

	public boolean establishAC(Arc arc) {
	    Node node1 = arc.node1;
	    Node node2 = arc.node2;
	    int newSize = node1.domain.length;

	    for (int k = 0; k < newSize; k++) {
	        Integer i = node1.domain[k];
	        boolean isDiff = false;
	        for (Integer j : node2.domain) {
	            if (!i.equals(j)) {
	                isDiff = true;
	                break;
	            }
	        }

	        if (!isDiff) {
	            // Eliminar el valor i del dominio de node1
	            for (int j = k; j < newSize - 1; j++) {
	                node1.domain[j] = node1.domain[j + 1];
	            }
	            newSize--; // Reducir el tamaño del dominio
	            k--; // Revisar el nuevo elemento en la posición k

	            if (newSize == 0) {
	                return false;
	            }
	            addNeighborArcsExceptB(node1, node2);
	        }
	    }

	    node1.domain = Arrays.copyOf(node1.domain, newSize);
	    return true;
	}

	public void addNeighborArcsExceptB(Node A, Node B) {

		for (int i = 0; i < 9; i++) {

			// add arcs with nodes in the same row
			if (i != A.col && nodes[A.row][i] != B) {
				arcs.add(new Arc(nodes[A.row][i], A));
			}

			// add arcs with nodes in the same col
			if (i != A.row && nodes[i][A.col] != B) {
				arcs.add(new Arc(nodes[i][A.col], A));
			}

			// add arcs with nodes in the same box
			for (int j = 0; j < 9; j++) {
				if (nodes[i][j].box == A.box && i != A.row && j != A.col && nodes[i][j] != B) {
					arcs.add(new Arc(nodes[i][j], A));
				}
			}
		}
	}
}

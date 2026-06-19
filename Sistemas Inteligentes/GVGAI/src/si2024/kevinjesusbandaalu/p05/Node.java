package si2024.kevinjesusbandaalu.p05;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentSkipListSet;

class Node {
	int row;
	int col;
	int box;
	Integer[] domain;

	public Node(int row, int col) {
		this.row = row;
		this.col = col;
		this.box = calcBox(row, col);
		this.domain = new Integer[9];
		for (int i = 0; i < 9; i++) {
			domain[i] = i+1;
		}
	}

	public Node(int row, int col, int value) {
		this.row = row;
		this.col = col;
		box = calcBox(row, col);
		domain = new Integer[1];
		domain[0] = value;
	}

	public static Node[][] getNodes(String sudoku) {

		Node[][] n = new Node[9][9];

		// analizamos el sudoku pasado por parametro

		if (sudoku.length() > 0 && sudoku.length() < 81)
			return null; // Devolvemos nulo si no podemos analizarlo

		
		int indice = 0;

		for (int i = 0; i < 9; i++) { // filas

			for (int j = 0; j < 9; j++) { // columnas

				char number = sudoku.charAt(indice++);

				if (number != '.') {

					n[i][j] = new Node(i, j, Character.getNumericValue(number));
				} else {

					n[i][j] = new Node(i, j);
				}
			}
		}

		
		// Node.printNodes(n);
		return n;
	}

	public static void printNodes(Node[][] nodes) {
		System.out.print("\r\n");
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				System.out.print(nodes[i][j].printDomain());
			}
			System.out.print("\r\n");
		}
	}

	public static String toString(Node[][] nodes) {

		String s = "";

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				s += nodes[i][j].domain[0];
			}
		}

		return s;

	}

	
	public static int judgeState(Node[][] nodes) {
	    boolean notEnd = false;

	    // Arrays to track seen numbers in rows, columns, and boxes
	    boolean[][] rows = new boolean[9][10];
	    boolean[][] cols = new boolean[9][10];
	    boolean[][] boxes = new boolean[9][10];

	    for (int i = 0; i < 9; i++) {
	        for (int j = 0; j < 9; j++) {
	            int domainLength = nodes[i][j].domain.length;

	            // Check for empty domain
	            if (domainLength == 0) {
	                return -1;
	            } else if (domainLength > 1) {
	                notEnd = true;
	            } else {
	                // Get the value from the domain
	                int value = nodes[i][j].domain[0];

	                // Calculate box index
	                int boxIndex = (i / 3) * 3 + (j / 3);

	                // Check for duplicates in rows, columns, and boxes
	                if (rows[i][value] || cols[j][value] || boxes[boxIndex][value]) {
	                    return -1;
	                }

	                // Mark the value as seen in the current row, column, and box
	                rows[i][value] = true;
	                cols[j][value] = true;
	                boxes[boxIndex][value] = true;
	            }
	        }
	    }

	    return notEnd ? 1 : 0;
	}


	private int calcBox(int row, int col) {
		
		return (row/3)*3+(col/3);
	}

	@Override
	public boolean equals(Object other) {

		if (other == null) {
			return false;
		}
		Node otherNode = (Node) other;
		if (otherNode.row == this.row && otherNode.col == this.col && otherNode.box == this.box) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "(" + this.row + "," + this.col + ")";
	}

	public String printDomain() {
		String str = "[";
		for (Integer i : domain) {
			str += i + "";
		}
		str += "]";
		return str;
	}
}
import java.io.*;
import java.util.ArrayList;

class Node {
    int row;
    int col;
    int box;
    int domain;
    Node[] neighbors;

    // Piscina global de nodos para evitar instanciar objetos nuevos, ahora adaptada por Hilo (ThreadLocal)
    static final ThreadLocal<Node[][]> globalNodesTL = ThreadLocal.withInitial(() -> {
        Node[][] nodes = new Node[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                nodes[i][j] = new Node(i, j);
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                ArrayList<Node> nbs = new ArrayList<>();
                for (int k = 0; k < 9; k++) {
                    if (k != j) nbs.add(nodes[i][k]);
                    if (k != i) nbs.add(nodes[k][j]);
                }
                int boxRow = (i / 3) * 3;
                int boxCol = (j / 3) * 3;
                for (int r = 0; r < 3; r++) {
                    for (int c = 0; c < 3; c++) {
                        if (boxRow + r != i && boxCol + c != j) {
                            nbs.add(nodes[boxRow + r][boxCol + c]);
                        }
                    }
                }
                nodes[i][j].neighbors = nbs.toArray(new Node[0]);
            }
        }
        return nodes;
    });

    static final ThreadLocal<Node[]> flatNodesTL = ThreadLocal.withInitial(() -> {
        Node[][] gn = globalNodesTL.get();
        Node[] flat = new Node[81];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                flat[i * 9 + j] = gn[i][j];
            }
        }
        return flat;
    });

    private Node(int row, int col) {
        this.row = row;
        this.col = col;
        this.box = (row / 3) * 3 + (col / 3);
        this.domain = 0x3FE;
    }

    // Reutiliza la misma matriz en memoria de este hilo, solo reinicia los dominios. Ultra rápido.
    static Node[][] setupSudoku(String line) {
        Node[][] gn = globalNodesTL.get();
        for (int i = 0; i < 81; i++) {
            int row = i / 9;
            int col = i % 9;
            char c = line.charAt(i);
            if (c >= '1' && c <= '9') {
                gn[row][col].domain = (1 << (c - '0'));
            } else { // '.' or '0'
                gn[row][col].domain = 0x3FE;
            }
        }
        return gn;
    }

    static String toSudokuString(Node[][] nodes) {
        char[] chars = new char[81];
        Node[] flatNodes = flatNodesTL.get();
        for (int i = 0; i < 81; i++) {
            int domain = flatNodes[i].domain;
            if ((domain & (domain - 1)) == 0 && domain != 0) {
                chars[i] = (char) ('0' + Integer.numberOfTrailingZeros(domain));
            } else {
                chars[i] = '.';
            }
        }
        return new String(chars);
    }

    static void printNodes(Node[][] nodes) {
        System.out.print("\r\n");
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(nodes[i][j].printDomain());
            }
            System.out.print("\r\n");
        }
    }

    // error, return -1
    // not end, return 1
    // find solution, return 0
    static int judgeState(Node[][] nodes) {
        boolean notEnd = false;
        Node[] flatNodes = flatNodesTL.get();
        for (int i = 0; i < 81; i++) {
            int d = flatNodes[i].domain;
            if (d == 0) {
                return -1;
            } else if ((d & (d - 1)) != 0) {
                notEnd = true;
            }
        }
        if (notEnd) {
            return 1;
        }

        // check all rows & columns usando bitmasks (muy rápido)
        for (int i = 0; i < 9; i++) {
            int rowMask = 0;
            int colMask = 0;
            for (int j = 0; j < 9; j++) {
                rowMask |= nodes[i][j].domain;
                colMask |= nodes[j][i].domain;
            }
            if (rowMask != 0x3FE || colMask != 0x3FE) {
                return -1;
            }
        }

        // check all boxes
        for (int box = 0; box < 9; box++) {
            int boxMask = 0;
            int startRow = (box / 3) * 3;
            int startCol = (box % 3) * 3;
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    boxMask |= nodes[startRow + r][startCol + c].domain;
                }
            }
            if (boxMask != 0x3FE) {
                return -1;
            }
        }

        return 0;
    }

    @Override
    public String toString() {
        return "(" + this.row + "," + this.col + ")";
    }

    String printDomain() {
        String str = "[";
        for (int i = 1; i <= 9; i++) {
            if ((domain & (1 << i)) != 0) {
                str += i;
            }
        }
        str += "]";
        return str;
    }
}
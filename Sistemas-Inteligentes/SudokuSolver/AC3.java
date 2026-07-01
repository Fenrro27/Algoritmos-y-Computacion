public class AC3 {
    private int stepCount = 0;
    private Node[][] nodes;
    private static final ThreadLocal<Node[]> queueTL = ThreadLocal.withInitial(() -> new Node[100]);
    private Node[] queue;

    AC3(Node[][] nodes) {
        if (Main.VERBOSE) System.out.println("\r\nAC3...");

        this.nodes = nodes;
        this.queue = queueTL.get();
        int head = 0;
        int tail = 0;

        Node[] flatNodes = Node.flatNodesTL.get();
        for (int i = 0; i < 81; i++) {
            if (Integer.bitCount(flatNodes[i].domain) == 1) {
                queue[tail++] = flatNodes[i];
            }
        }

        boolean success = true;
        while (head < tail) {
            Node curr = queue[head++];
            int currDomain = curr.domain;
            if (currDomain == 0) {
                success = false;
                break;
            }
            
            int value = Integer.numberOfTrailingZeros(currDomain);
            int mask = ~(1 << value);

            for (Node n : curr.neighbors) {
                if ((n.domain & (1 << value)) != 0) {
                    int prevSize = Integer.bitCount(n.domain);
                    n.domain &= mask;
                    
                    if (Main.VERBOSE) {
                        stepCount++;
                        System.out.print("step " + stepCount +
                                "  set.size():" + (tail - head) +
                                "  AC:" + n.toString() + curr.toString() +
                                "  " + n.toString() + ".remove(" + value + ")" +
                                "  domain:" + n.printDomain() + "\r\n");
                    }

                    if (n.domain == 0) {
                        success = false;
                        break;
                    }

                    if (prevSize > 1 && Integer.bitCount(n.domain) == 1) {
                        queue[tail++] = n;
                    }
                }
            }
            if (!success) break;
        }

        if (!success && Main.VERBOSE) {
            System.out.println("no solution.");
        }

        int result = Node.judgeState(nodes);
        if (result != -1) {
            if (Main.VERBOSE) Node.printNodes(nodes);
        }
        if (result == 1) {
            new Backtracking(nodes, false);
        }
    }
}
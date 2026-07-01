public class Backtracking {
    private int stepCount = 0;
    private Node[][] nodes;
    
    private static final ThreadLocal<Node[]> stackNodesTL = ThreadLocal.withInitial(() -> new Node[729]);
    private static final ThreadLocal<int[]> stackValuesTL = ThreadLocal.withInitial(() -> new int[729]);
    private static final ThreadLocal<Node[]> queueTL = ThreadLocal.withInitial(() -> new Node[100]);
    
    private Node[] stackNodes;
    private int[] stackValues;
    private Node[] queue;
    private int stackSize = 0;

    Backtracking(Node[][] nodes) {
        if (Main.VERBOSE) System.out.println("\r\nfirst time of forward checking...");

        this.nodes = nodes;
        this.stackNodes = stackNodesTL.get();
        this.stackValues = stackValuesTL.get();
        this.queue = queueTL.get();
        if (!initialPropagate()) {
            if (Main.VERBOSE) System.out.println("no solution");
            return;
        }
        if (Main.VERBOSE) Node.printNodes(nodes);

        if (Main.VERBOSE) System.out.println("\r\nbacktracking...");
        if (search(getHeuristicNode()) == -1) {
            if (Main.VERBOSE) System.out.println("no solution");
        } else {
            if (Main.VERBOSE) Node.printNodes(nodes);
        }
    }

    Backtracking(Node[][] nodes, boolean notUsingForwardCheck) {
        if (Main.VERBOSE) System.out.println("\r\nbacktracking...");

        this.nodes = nodes;
        this.stackNodes = stackNodesTL.get();
        this.stackValues = stackValuesTL.get();
        this.queue = queueTL.get();
        if (search(getHeuristicNode()) == -1) {
            if (Main.VERBOSE) System.out.println("no solution");
        } else {
            if (Main.VERBOSE) Node.printNodes(nodes);
        }
    }

    int search(Node node) {
        int state = stackSize;

        int result = Node.judgeState(nodes);
        if (result != 1) {
            return result;
        }
        if (node == null) {
            return -1;
        }

        int tempDomain = node.domain;
        while (tempDomain > 0) {
            int value = Integer.numberOfTrailingZeros(tempDomain);
            if (Main.VERBOSE) {
                stepCount++;
                System.out.println("step " + stepCount + "  try " + value + " for " + node.toString());
            }
            setNodeValue(node, value);
            if (!propagate(node)) {
                backtrackState(state);
                if (!removeNodeValue(node, value)) {
                    return -1;
                }
                state = stackSize;
            } else {
                result = search(getHeuristicNode());
                if (result == 0) {
                    return 0;
                } else if (result == -1) {
                    backtrackState(state);
                    if (!removeNodeValue(node, value)) {
                        return -1;
                    }
                    state = stackSize;
                }
            }
            tempDomain &= ~(1 << value);
        }
        return -1;
    }

    boolean initialPropagate() {
        int head = 0;
        int tail = 0;
        Node[] flatNodes = Node.flatNodesTL.get();
        for (int i = 0; i < 81; i++) {
            if (Integer.bitCount(flatNodes[i].domain) == 1) {
                queue[tail++] = flatNodes[i];
            }
        }
        return processQueue(head, tail);
    }

    boolean propagate(Node startNode) {
        int head = 0;
        int tail = 0;
        queue[tail++] = startNode;
        return processQueue(head, tail);
    }

    boolean processQueue(int head, int tail) {
        while (head < tail) {
            Node curr = queue[head++];
            int currDomain = curr.domain;
            if (currDomain == 0) return false;
            
            int value = Integer.numberOfTrailingZeros(currDomain);
            int mask = ~(1 << value); // eliminar rápidamente un número específico del dominio a nibel de bit

            for (Node n : curr.neighbors) {
                if ((n.domain & (1 << value)) != 0) {
                    int prevSize = Integer.bitCount(n.domain);
                    
                    n.domain &= mask;
                    stackNodes[stackSize] = n;
                    stackValues[stackSize] = value;
                    stackSize++;
                    
                    if (n.domain == 0) {
                        return false;
                    }
                    
                    if (prevSize > 1 && Integer.bitCount(n.domain) == 1) {
                        queue[tail++] = n;
                    }
                }
            }
        }
        return true;
    }

    Node getHeuristicNode() {
        Node bestNode = null;
        int minSize = 10;
        int maxDegree = -1;
        
        Node[] flatNodes = Node.flatNodesTL.get();
        for (int i = 0; i < 81; i++) {
            int size = Integer.bitCount(flatNodes[i].domain);
            if (size > 1) {
                if (size < minSize) {
                    minSize = size;
                    bestNode = flatNodes[i];
                    maxDegree = getForwardDegree(bestNode);
                } else if (size == minSize) {
                    int degree = getForwardDegree(flatNodes[i]);
                    if (degree > maxDegree) {
                        bestNode = flatNodes[i];
                        maxDegree = degree;
                    }
                }
            }
        }
        return bestNode;
    }

    int getForwardDegree(Node node) {
        int count = 0;
        for (Node n : node.neighbors) {
            if (Integer.bitCount(n.domain) > 1) {
                count++;
            }
        }
        return count;
    }

    boolean removeNodeValue(Node node, int value) {
        if ((node.domain & (1 << value)) != 0) {
            node.domain &= ~(1 << value);
            stackNodes[stackSize] = node;
            stackValues[stackSize] = value;
            stackSize++;
            if (node.domain == 0) {
                return false;
            }
        }
        return true;
    }

    void setNodeValue(Node node, int value) {
        int temp = node.domain;
        while (temp > 0) {
            int i = Integer.numberOfTrailingZeros(temp);
            if (i != value) {
                node.domain &= ~(1 << i);
                stackNodes[stackSize] = node;
                stackValues[stackSize] = i;
                stackSize++;
            }
            temp &= ~(1 << i);
        }
    }

    void backtrackState(int state) {
        while (stackSize > state) {
            stackSize--;
            Node node = stackNodes[stackSize];
            int removedValue = stackValues[stackSize];
            node.domain |= (1 << removedValue);
        }
    }
}
package si2024.kevinjesusbandaalu.p05;

import java.util.*;

public class Backtracking {
	private Node[][] nodes;
	private Node[][] nodesSol = null;
	private Stack<Record> stack = new Stack<>();

	class Record {
		Node node;
		int removedValue;

		Record(Node node, int value) {
			this.node = node;
			removedValue = value;
		}
	}

	public Backtracking(Node[][] nodes) { //, boolean notUsingForwardCheck
		this.nodes = nodes;
		if (search(getHeuristicNode()) == -1) {
			System.out.println("no solution");
		} else {
			nodesSol = nodes;
		}
	}

	public String resolver() {
		if (nodesSol != null)
			return Node.toString(nodesSol);
		else
			return null;
	}

	// use recursion to search value for all the nodes
	//
	// error, return -1
	// not end, return 1
	// find solution, return 0
	public int search(Node node) {
	    int result;
	    int state = stack.size();
	    int value;
	    Node heuristicNode;

	    while (true) {
	        result = Node.judgeState(nodes);
	        if (result != 1) {
	            return result;
	        }
	        if (node == null) {
	            return -1;
	        }

	        value = getHeuristicValue(node);
	        while (value != 0) {
	            setNodeValue(node, value);
	            if (!forwardCheck()) {
	                backtrackState(state);
	                if (!removeNodeValue(node, value)) {
	                    return -1;
	                }
	                state = stack.size();
	            } else {
	                heuristicNode = getHeuristicNode();
	                result = search(heuristicNode);
	                if (result == 0) {
	                    return 0;
	                } else if (result == -1) {
	                    backtrackState(state);
	                    if (!removeNodeValue(node, value)) {
	                        return -1;
	                    }
	                    state = stack.size();
	                }
	            }
	            value = getHeuristicValue(node);
	        }
	        return -1;
	    }
	}


	// establish one node's neighbors arc consistency with it: arc(Y,X)
	// when this node has one value in its domain
	public boolean forwardCheck() {
	    Set<Node> processedNodes = new HashSet<>(); // Para almacenar los nodos que ya han sido procesados
	    boolean hasNodesWithSingleValue = true; // Variable para rastrear si hay nodos con un solo valor en su dominio

	    while (hasNodesWithSingleValue) {
	        hasNodesWithSingleValue = false; // Reiniciar la bandera a falso antes de comenzar la iteración

	        for (int i = 0; i < 9; i++) {
	            for (int j = 0; j < 9; j++) {
	                Node node = nodes[i][j];

	                // Si el nodo ya ha sido procesado o su dominio tiene más de un valor, continuar con el siguiente nodo
	                if (processedNodes.contains(node) || node.domain.length > 1) {
	                    continue;
	                }

	                // Marcar el nodo como procesado
	                processedNodes.add(node);

	                // Obtener los vecinos del nodo actual
	                Node[] neighbors = getNeighbors(node);

	                // Eliminar el valor del dominio del nodo actual de sus vecinos
	                int value = node.domain[0];
	                for (Node n : neighbors) {
	                    if (!removeNodeValue(n, value)) {
	                        return false;
	                    } else if (n.domain.length == 1) {
	                        // Si el vecino tiene un solo valor en su dominio, marcar la bandera como verdadera
	                        hasNodesWithSingleValue = true;
	                    }
	                }
	            }
	        }
	    }

	    return true;
	}


	// choose one unassigned node with the minimum remaining value
	public Node getHeuristicNode() {
	    Node minNode = null;
	    int minSize = Integer.MAX_VALUE;

	    for (int i = 0; i < 9; i++) {
	        for (int j = 0; j < 9; j++) {
	            Node node = nodes[i][j];
	            int nodeDomainLength = node.domain.length;

	            // Si el dominio tiene solo dos valores, devuelve este nodo de inmediato
	            if (nodeDomainLength == 2) {
	                return node;
	            }

	            // Actualiza el nodo mínimo si es necesario
	            if (nodeDomainLength > 1 && nodeDomainLength < minSize) {
	                minNode = node;
	                minSize = nodeDomainLength;
	            }
	        }
	    }
	    
	    return minNode;
	}


	public int getHeuristicValue(Node node) {
	   		
		return node.domain[0];
		/* Node[] neighbors = getNeighbors(node);
	    int[] countArray = new int[10]; // Un array para almacenar los recuentos de dominios compartidos para cada valor

	    // Inicializar el array de recuentos
	    for (Node neighbor : neighbors) {
	        for (int value : neighbor.domain) {
	            countArray[value]++;
	        }
	    }

	    int minCount = Integer.MAX_VALUE;
	    int minCountValue = 0;

	    // Encontrar el valor con el menor recuento de dominios compartidos
	    for (int value : node.domain) {
	        int count = countArray[value];
	        if (count < minCount) {
	            minCount = count;
	            minCountValue = value;
	        }
	    }

	    return minCountValue;*/
	}

	// remove one value from one node's domain and return true
	// return false when the domain is empty
	public boolean removeNodeValue(Node node, int value) {
	    int newSize = node.domain.length;
	    boolean valueFound = false;

	    // Buscar y eliminar el valor del dominio
	    for (int i = 0; i < newSize; i++) {
	        if (node.domain[i] == value) {
	            valueFound = true;
	            // Mover elementos hacia la izquierda para sobrescribir el valor eliminado
	            for (int j = i; j < newSize - 1; j++) {
	                node.domain[j] = node.domain[j + 1];
	            }
	            newSize--;
	            break;
	        }
	    }

	    // Si el valor fue encontrado y eliminado, ajustar el tamaño del dominio
	    if (valueFound) {
	        // Guardar el estado anterior en la pila
	        stack.push(new Record(node, value));
	        // Ajustar el array del dominio al nuevo tamaño
	        node.domain = Arrays.copyOf(node.domain, newSize);
	        return newSize > 0;
	    }

	    return true;
	}


	public void setNodeValue(Node node, int value) {
	    int newSize = 0;
	    Integer[] newDomain = new Integer[node.domain.length];

	    for (int i : node.domain) {
	        if (i != value) {
	            // Guardar los elementos que no son igual al valor en la pila
	            stack.push(new Record(node, i));
	        } else {
	            // Guardar el valor en el nuevo dominio
	            newDomain[newSize++] = i;
	        }
	    }

	    // Ajustar el array del dominio al nuevo tamaño
	    node.domain = Arrays.copyOf(newDomain, newSize);
	}


	public void backtrackState(int state) {
	    while (stack.size() > state) {
	        Record record = stack.pop();
	        Integer[] newDomain = new Integer[record.node.domain.length + 1];
	        
	        // Copia los elementos actuales del dominio
	        System.arraycopy(record.node.domain, 0, newDomain, 0, record.node.domain.length);
	        
	        // Agrega el valor eliminado al final del dominio
	        newDomain[newDomain.length - 1] = record.removedValue;
	        
	        // Actualiza el dominio del nodo con el nuevo arreglo
	        record.node.domain = newDomain;
	    }
	}


	// get neighbors whose domains have more than 1 node
	public Node[] getNeighbors(Node node) {
		//ArrayList<Node> neighbors = new ArrayList<>();
		 Node[] neighbors = new Node[24];
		 int indice =0;

		for (int i = 0; i < 9; i++) {

			// same row
			if (i != node.col && nodes[node.row][i].domain.length > 1) {
			//	neighbors.add(nodes[node.row][i]);
				 neighbors[indice++] = nodes[node.row][i];
			}

			// same col
			if (i != node.row && nodes[i][node.col].domain.length > 1) {
				//neighbors.add(nodes[i][node.col]);
				 neighbors[indice++] = nodes[i][node.col];
			}

			// same box
			for (int j = 0; j < 9; j++) {
				if (i != node.row && j != node.col && nodes[i][j].box == node.box && nodes[i][j].domain.length > 1) {
					//neighbors.add(nodes[i][j]);					
					 neighbors[indice++] = nodes[i][j];
				}
			}
		}
		return Arrays.copyOf(neighbors, indice);
	}
}

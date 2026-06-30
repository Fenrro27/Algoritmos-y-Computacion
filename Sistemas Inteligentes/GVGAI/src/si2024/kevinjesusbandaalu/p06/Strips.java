package si2024.kevinjesusbandaalu.p06;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import si2024.kevinjesusbandaalu.p06.Node.NodeType;

public class Strips {

	public static void main(String[] args) {
		// Verificar si se proporcionó la ruta del archivo del problema como argumento
		// de línea de comandos
		if (args.length != 1) {
			System.out.println("Usage: java Strips <problem_file>");
			return;
		}
		long ini = System.currentTimeMillis();
		// Obtener la ruta del archivo del problema del primer argumento
		String filename = args[0];
		Plan plan = PlanParser.readPlanFromFile(filename);

		System.out.println(plan);
		// Ejecutar el algoritmo STRIPS

		List<String> generatedPlan = generatePlan(plan);
		System.out.println("Solution:\nGenerated Plan: " + generatedPlan);
		System.out.println("Tiempo de ejecucion: " + (System.currentTimeMillis() - ini)+"ms");
		
		// Generar la ruta del archivo de solución
		String solutionFilename = filename.replaceFirst("[.][^.]+$", "_sol.txt");

		// Escribir el plan generado en el archivo de solución
		writePlanToFile(generatedPlan, solutionFilename);
	}

	public static List<String> generatePlan(Plan plan) {
		Stack<Node> stack = new Stack<>();
		List<String> finalPlan = new ArrayList<>();
		Set<String> currentState = new HashSet<>(plan.initialStates);

		stack.push(new Node(plan.finalStates, NodeType.GOAL_CONJUNCTION));
		// Initialize the stack with final goals
		for (String goal : plan.finalStates) {
			stack.push(new Node(goal, NodeType.GOAL));
		}
		
		while (!stack.isEmpty()) {
			

			if (verificarPlan(plan, finalPlan)) {
				break;
			}

			Node node = stack.pop();

			if (node.type == NodeType.OPERATOR) {
				Acciones action = node.action;
				if (currentState.containsAll(action.prerequisites)) {
					// Execute the operator
					currentState.addAll(action.additions);
					currentState.removeAll(action.deletions);
					finalPlan.add(action.name); // añadirlo al plan
				} else {
					// Reinsert the operator back to the stack
					stack.push(node);
					// Add missing prerequisites to stack
					for (String prereq : action.prerequisites) {
						if (!currentState.contains(prereq)) {
							stack.push(new Node(prereq, NodeType.GOAL));
						}
					}
				}
			} else if (node.type == NodeType.GOAL) {
				String goal = node.goal;
				if (!currentState.contains(goal)) {
					// Handle cyclic goals and generate successors
					if (hasCycle(stack, goal)) {
						continue;
					}

					boolean successorsGenerated = false;

					for (Acciones action : plan.actions) {
						String a = action + "";

						if (action.additions.contains(goal)) {
							stack.push(new Node(action, NodeType.OPERATOR));
							successorsGenerated = true;
						}
					}
					if (!successorsGenerated) {

						while (!stack.isEmpty() && stack.pop().type != NodeType.OPERATOR) {
							stack.pop();
						}

						continue;
					}
				}
			} else if (node.type == NodeType.GOAL_CONJUNCTION) {
				if (!currentState.containsAll(node.goals)) {
					for (String subGoal : node.goals) {
						if (!currentState.contains(subGoal)) {
							stack.push(new Node(subGoal, NodeType.GOAL));
						}
					}
				}

			}

		}
		return finalPlan;
	}

	public static boolean verificarPlan(Plan plan, List<String> finalPlan) {

		List<String> estados = plan.initialStates;
		List<Acciones> listAcciones = new ArrayList<Acciones>();

		// Creamos la lista de acciones
		for (String s : finalPlan) {
			for (Acciones a : plan.actions) {
				if (a.esAccion(s)) {
					listAcciones.add(a); // acciones
					break;
				}
			}
		}

		Boolean SeCumple = true;
		// operamos los estados a ver si vamos cumpliendo el plan, si llegamos al plan
		// final esta todo bien
		for (Acciones a : listAcciones) {
			for (String pre : a.prerequisites) {
				if (estados.contains(pre)) {
					// Verificar si el estado ya contiene cada adición antes de agregarla
					for (String addition : a.additions) {
						if (!estados.contains(addition)) {
							estados.add(addition);
						}
					}
					estados.removeAll(a.deletions);
				}
			}
		}

		if (estados.containsAll(plan.finalStates)) {
			return true;
		}
		return false;
	}

	private static boolean hasCycle(Stack<Node> stack, String goal) {
		for (Node node : stack) {
			if (node.type == NodeType.GOAL && node.goal.equals(goal)) {
				return true;
			}
		}
		return false;
	}

	private static void writePlanToFile(List<String> plan, String filename) {
		try (FileWriter writer = new FileWriter(filename)) {
			writer.write("# Lista de acciones. 1 por linea\n");
			for (String action : plan) {
				writer.write(action + "\n");
			}
			System.out.println("Plan successfully written to " + filename);
		} catch (IOException e) {
			System.out.println("An error occurred while writing the plan to file.");
			e.printStackTrace();
		}
	}
}

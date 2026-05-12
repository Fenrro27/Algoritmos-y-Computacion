package si2026.kevinjesusbandaalu.p07;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class Strips {

	public static class ForwardNode {
		public Set<String> state;
		public List<String> planSoFar;
		public int goalsSatisfied;
		public int heuristic;

		public ForwardNode(Set<String> state, List<String> planSoFar, int goalsSatisfied, int heuristic) {
			this.state = state;
			this.planSoFar = planSoFar;
			this.goalsSatisfied = goalsSatisfied;
			this.heuristic = heuristic;
		}
	}

	public static class SearchState {
		public Queue<ForwardNode> queue;
		public Set<Set<String>> visited;
		public List<String> finalSolution;
		public Plan plan;
		public int[][] distMap;
		public java.util.Map<String, List<String>> adj;
		public long totalExpansions = 0;

		public SearchState(Plan plan, int[][] distMap) {
			this.plan = plan;
			this.distMap = distMap;
			this.visited = new HashSet<>();
			this.queue = new PriorityQueue<>((n1, n2) -> {
				return Integer.compare(n1.planSoFar.size() + n1.heuristic, n2.planSoFar.size() + n2.heuristic);
			});
			
			this.adj = new java.util.HashMap<>();
			for (Acciones a : plan.actions) {
				if (a.name.startsWith("MOVE_")) {
					String fromA = null, toA = null;
					for (String p : a.prerequisites) if (p.startsWith("A_")) fromA = p;
					for (String ad : a.additions) if (ad.startsWith("A_")) toA = ad;
					if (fromA != null && toA != null) {
						adj.putIfAbsent(fromA, new ArrayList<>());
						adj.get(fromA).add(toA);
					}
				}
			}
			
			Set<String> cleanInitial = new HashSet<>();
			for (String s : plan.initialStates) {
				if (!s.startsWith("E_")) cleanInitial.add(s);
			}
			
			int initialGoals = countSatisfiedGoals(cleanInitial, plan.finalStates);
			int initialH = calculateHeuristic(cleanInitial, plan.finalStates, distMap);
			ForwardNode root = new ForwardNode(cleanInitial, new ArrayList<>(), initialGoals, initialH);
			
			this.queue.add(root);
			this.visited.add(root.state);
		}
	}

	public static List<String> generateForwardPlan(SearchState search, tools.ElapsedCpuTimer timer) {
		if (search.finalSolution != null) return search.finalSolution;
		
		int expansionsInTick = 0;
		while (!search.queue.isEmpty()) {
			if (timer != null && timer.remainingTimeMillis() < 5 && expansionsInTick > 0) {
				break;
			}
			expansionsInTick++;
			search.totalExpansions++;

			ForwardNode current = search.queue.poll();
			
			// if (search.totalExpansions % 1000 == 0) {
			// 	System.err.println("STRIPS: Buscando... Total Exp: " + search.totalExpansions + " Queue: " + search.queue.size() + " H: " + current.heuristic);
			// }
			
			if (current.state.containsAll(search.plan.finalStates)) {
				// System.err.println("STRIPS: ¡Victoria! Pasos: " + current.planSoFar.size() + " Expansiones totales: " + search.totalExpansions);
				search.finalSolution = current.planSoFar;
				return current.planSoFar;
			}
			
			for (Acciones action : search.plan.actions) {
				if (checkPrerequisites(current.state, action)) {
					Set<String> nextState = new HashSet<>(current.state);
					nextState.removeAll(action.deletions);
					for (String add : action.additions) {
						if (!add.startsWith("E_")) nextState.add(add);
					}
					
					if (isDeadEnd(nextState, search.distMap)) continue;
					if (isGoalDeadlock(nextState, search.plan.finalStates, search.adj)) continue;
					
					if (!search.visited.contains(nextState)) {
						search.visited.add(nextState);
						List<String> nextPlan = new ArrayList<>(current.planSoFar);
						nextPlan.add(action.name);
						int satisfied = countSatisfiedGoals(nextState, search.plan.finalStates);
						int heur = calculateHeuristic(nextState, search.plan.finalStates, search.distMap);
						ForwardNode nextNode = new ForwardNode(nextState, nextPlan, satisfied, heur);
						search.queue.add(nextNode);
					}
				}
			}
		}
		
		return new ArrayList<>(); 
	}

	private static boolean checkPrerequisites(Set<String> state, Acciones action) {
		for (String pre : action.prerequisites) {
			if (pre.startsWith("E_")) {
				String pos = pre.substring(2);
				if (state.contains("C_" + pos) || state.contains("A_" + pos)) return false;
			} else {
				if (!state.contains(pre)) return false;
			}
		}
		return true;
	}

	private static int calculateHeuristic(Set<String> state, List<String> finalStates, int[][] distMap) {
		List<int[]> boxes = new ArrayList<>();
		int ax = -1, ay = -1;
		for (String s : state) {
			if (s.startsWith("C_")) {
				String[] p = s.split("_");
				boxes.add(new int[]{Integer.parseInt(p[1]), Integer.parseInt(p[2])});
			} else if (s.startsWith("A_")) {
				String[] p = s.split("_");
				ax = Integer.parseInt(p[1]);
				ay = Integer.parseInt(p[2]);
			}
		}
		
		if (boxes.isEmpty()) return 0;
		
		int totalBoxDist = 0;
		for (int[] b : boxes) {
			totalBoxDist += distMap[b[0]][b[1]];
		}
		
		int distAvatarBox = 0;
		if (ax != -1) {
			int minD = 1000;
			for (int[] b : boxes) {
				if (!finalStates.contains("C_" + b[0] + "_" + b[1])) {
					int d = Math.abs(ax - b[0]) + Math.abs(ay - b[1]);
					if (d < minD) minD = d;
				}
			}
			if (minD < 1000) distAvatarBox = minD;
		}

		int penalty = 0;
		for (int i = 0; i < finalStates.size(); i++) {
			if (!state.contains(finalStates.get(i))) {
				penalty += (finalStates.size() - i) * 1000;
			}
		}
		
		return totalBoxDist + distAvatarBox + penalty;
	}

	private static int countSatisfiedGoals(Set<String> state, List<String> goals) {
		int score = 0;
		for (int i = 0; i < goals.size(); i++) {
			if (state.contains(goals.get(i))) {
				score += (goals.size() - i) * 1000;
			}
		}
		return score;
	}

	private static boolean isDeadEnd(Set<String> state, int[][] distMap) {
		for (String s : state) {
			if (s.startsWith("C_")) {
				String[] p = s.split("_");
				int x = Integer.parseInt(p[1]);
				int y = Integer.parseInt(p[2]);
				if (distMap[x][y] >= 1000) return true;
			}
		}
		return false;
	}

	private static boolean isGoalDeadlock(Set<String> state, List<String> finalStates, java.util.Map<String, List<String>> adj) {
		String avatarStr = null;
		for (String s : state) {
			if (s.startsWith("A_")) {
				avatarStr = s; break;
			}
		}
		if (avatarStr == null) return false;

		List<String> emptyGoals = new ArrayList<>();
		List<String> unsolvedBoxes = new ArrayList<>();
		for (String g : finalStates) {
			if (!state.contains(g)) {
				emptyGoals.add("A_" + g.substring(2));
			}
		}
		for (String s : state) {
			if (s.startsWith("C_") && !finalStates.contains(s)) {
				unsolvedBoxes.add("A_" + s.substring(2));
			}
		}
		
		if (emptyGoals.isEmpty() && unsolvedBoxes.isEmpty()) return false;

		Set<String> visited = new HashSet<>();
		java.util.Queue<String> q = new java.util.LinkedList<>();
		q.add(avatarStr);
		visited.add(avatarStr);

		while (!q.isEmpty()) {
			String curr = q.poll();
			List<String> neighbors = adj.get(curr);
			if (neighbors != null) {
				for (String next : neighbors) {
					if (!visited.contains(next)) {
						visited.add(next);
						q.add(next);
					}
				}
			}
		}

		int reachableGoals = 0;
		for (String eg : emptyGoals) if (visited.contains(eg)) reachableGoals++;
		
		int reachableBoxes = 0;
		for (String ub : unsolvedBoxes) if (visited.contains(ub)) reachableBoxes++;

		if (reachableGoals < emptyGoals.size() || reachableGoals < reachableBoxes) return true;
		
		return false;
	}
}

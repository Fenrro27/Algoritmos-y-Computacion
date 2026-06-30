package si2024.kevinjesusbandaalu.p06;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlanParser {

	public static Plan readPlanFromFile(String filename) {
		Plan plan = new Plan();
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("#") || line.isEmpty()) {
					continue; // Skip comments and empty lines
				}
				if (line.startsWith("Ei:")) {
					plan.initialStates = parseStates(line);
				} else if (line.startsWith("Ef:")) {
					plan.finalStates = parseStates(line);
				} else if (line.startsWith("Accion:")) {
					plan.actions.add(parseAction(line));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return plan;
	}

	private static List<String> parseStates(String line) {
		String[] parts = line.split(":");
		if (parts.length > 1) {
			String[] states = parts[1].split(",");
			List<String> stateList = new ArrayList<>();
			for (String state : states) {
				stateList.add(state.trim());
			}
			return stateList;
		}
		return new ArrayList<>();
	}

	private static Acciones parseAction(String line) {
		String[] parts = line.split(";");
		String name = parts[0].split(":")[1].trim();
		String[] prerequisites = parts[1].trim().split(",");
		String[] additions = parts[2].trim().split(",");

		List<String> prereqList = new ArrayList<>();
		for (String prereq : prerequisites) {
			if (!prereq.trim().isEmpty()) {
				prereqList.add(prereq.trim());
			}
		}

		List<String> addList = new ArrayList<>();
		for (String add : additions) {
			if (!add.trim().isEmpty()) {
				addList.add(add.trim());
			}
		}

		List<String> delList = new ArrayList<>();

		if (parts.length >= 4) {

			String[] deletions = parts[3].trim().split(",");
			for (String del : deletions) {
				if (!del.trim().isEmpty()) {
					delList.add(del.trim());
				}
			}
		}

		return new Acciones(name, prereqList, addList, delList);

	}
}

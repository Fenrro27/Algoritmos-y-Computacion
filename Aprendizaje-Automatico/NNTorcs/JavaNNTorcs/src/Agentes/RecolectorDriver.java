package Agentes;

import champ2011client.Action;
import champ2011client.SensorModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class RecolectorDriver extends DriverBase {

	// --- Variables para el Logger ---
	private PrintWriter logWriter = null;
	private boolean headerWritten = false;
	private static final String LOG_PATH = "Knowledge/datos_recolector.csv";

	@Override
	public Action control(SensorModel sensors) {

		// Inicializamos la acción por defecto
		Action action = new Action();

		// --- Lógica de Control (Tu lógica original) ---

		// check if car is currently stuck
		if (Math.abs(sensors.getAngleToTrackAxis()) > stuckAngle) {
			stuck++;
		} else {
			stuck = 0;
		}

		boolean isRecovering = false;

		// after car is stuck for a while apply recovering policy
		if (stuck > stuckTime) {
			isRecovering = true;
			float steer = (float) (-sensors.getAngleToTrackAxis() / steerLock);
			int gear = -1; // gear R

			if (sensors.getAngleToTrackAxis() * sensors.getTrackPosition() > 0) {
				gear = 1;
				steer = -steer;
			}
			clutch = clutching(sensors, clutch);

			action.gear = gear;
			action.steering = steer;
			action.accelerate = 1.0;
			action.brake = 0;
			action.clutch = clutch;
		}

		if (!isRecovering) {
			float accel_and_brake = getAccel(sensors);
			int gear = getGear(sensors);
			float steer = getSteer(sensors);

			if (steer < -1)
				steer = -1;
			if (steer > 1)
				steer = 1;

			float accel, brake;
			if (accel_and_brake > 0) {
				accel = accel_and_brake;
				brake = 0;
			} else {
				accel = 0;
				brake = filterABS(sensors, -accel_and_brake);
			}

			clutch = clutching(sensors, clutch);

			action.gear = gear;
			action.steering = steer;
			action.accelerate = accel;
			action.brake = brake;
			action.clutch = clutch;
		}

		// --- GUARDADO DE DATOS (NUEVO) ---
		guardarDatos(sensors, action);

		return action;
	}

	/**
	 * Escribe en el CSV los sensores y acciones solicitados.
	 */
	private void guardarDatos(SensorModel sensors, Action action) {
		try {
			// 1. Crear carpeta y abrir flujo de escritura si no existe
			if (logWriter == null) {
				File folder = new File("Knowledge");
				if (!folder.exists()) {
					folder.mkdir();
				}
				// 'true' para modo append (no borrar lo anterior)
				logWriter = new PrintWriter(new FileWriter(LOG_PATH, true));
			}

			// 2. Escribir Cabecera (Solo si el archivo está vacío)
			File f = new File(LOG_PATH);
			if (!headerWritten && f.length() == 0) {
				StringBuilder sb = new StringBuilder();

				// --- CABECERAS SENSORES ---
				sb.append("S_angle,");

				writeHeaderArray(sb, "S_focus", sensors.getFocusSensors().length);

				sb.append("S_gear,");


				sb.append("S_rpm,S_speed,");

				writeHeaderArray(sb, "S_track", sensors.getTrackEdgeSensors().length);

				sb.append("S_trackPos,");

				writeHeaderArray(sb, "S_wheelSpinVel", sensors.getWheelSpinVelocity().length);

				sb.append("S_z,");

				// --- CABECERAS ACCIONES ---
				sb.append("A_Accel,A_Brake,A_Gear,A_Steer");

				logWriter.println(sb.toString());
				headerWritten = true;
			} else {
				headerWritten = true;
			}

			// 3. Escribir Valores (Mismo orden que la cabecera)
			StringBuilder sb = new StringBuilder();

			// Sensores escalares
			sb.append(sensors.getAngleToTrackAxis()).append(",");
			// Array Focus
			writeDataArray(sb, sensors.getFocusSensors());

			sb.append(sensors.getGear()).append(","); // Gear del sensor (coche actual)

			sb.append(sensors.getRPM()).append(",");
			sb.append(sensors.getSpeed()).append(",");

			// Array Track
			writeDataArray(sb, sensors.getTrackEdgeSensors());

			sb.append(sensors.getTrackPosition()).append(",");

			// Array WheelSpin
			writeDataArray(sb, sensors.getWheelSpinVelocity());

			sb.append(sensors.getZ()).append(",");

			// --- ACCIONES ---
			sb.append(action.accelerate).append(",");
			sb.append(action.brake).append(",");
			sb.append(action.gear).append(","); // Gear de la acción (comando)
			sb.append(action.steering); // Último valor sin coma

			logWriter.println(sb.toString());
			logWriter.flush(); // Forzar escritura inmediata

		} catch (IOException e) {
			System.err.println("Error guardando datos CSV: " + e.getMessage());
		}
	}

	// --- Helpers para escribir arrays en el CSV ---

	private void writeHeaderArray(StringBuilder sb, String prefix, int length) {
		for (int i = 0; i < length; i++) {
			sb.append(prefix).append("_").append(i).append(",");
		}
	}

	private void writeDataArray(StringBuilder sb, double[] data) {
		for (double val : data) {
			sb.append(val).append(",");
		}
	}

	// Método opcional para cerrar recurso
	public void shutdown() {
		if (logWriter != null) {
			logWriter.close();
		}
	}
}
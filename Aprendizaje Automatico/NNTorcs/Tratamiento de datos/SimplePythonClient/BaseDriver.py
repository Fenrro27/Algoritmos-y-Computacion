'''
This class is based on the C++ code of Daniele Loiacono
Modified to use ONNX Neural Network
'''
import SimplePythonClient.CarControl as CarControl
import SimplePythonClient.CarState as CarState
import numpy as np
import onnxruntime as ort  # Necesario para leer el modelo

class tstage:
    WARMUP = 0
    QUALIFYING = 1
    RACE = 2
    UNKNOWN = 3

STUCKANGLE = np.pi/6
STUCKTIME = 1

class BaseDriver(object):

    stage = tstage()  
    trackName=""
    gearUp = [5000,6000,6000,6500,7000,0]
    gearDown = [0,2500,3000,3000,3500,3500]

    def __init__(self):
        print("Inicializando BaseDriver con IA (ONNX)...")
        self.stuckCounter = 0
        self.bringingCartBack = 0
        
       # Cargar los 3 cerebros independientes
        try:
            self.sess_accel = ort.InferenceSession('cerebro_pedales.onnx')
            self.sess_gear  = ort.InferenceSession('cerebro_gear.onnx')
            self.sess_steer = ort.InferenceSession('cerebro_steer.onnx')
            print('✅ Modelos ONNX cargados correctamente.')
        except Exception as e:
            print(f'❌ Error cargando ONNX: {e}')

            # 2. Parámetros del Scaler (Generados automáticamente del entrenamiento)
        self.scaler_mean = np.array([
            -0.00190332, 2.64689548, 4415.54587640, 77.99133326, 7.61264134, 11.86988950, 19.37918404, 34.81733083, 42.00534021, 49.01516420, 54.86910626, 58.47841215, 48.82313349, 40.80773549, 33.84768239, 25.74177277, 17.33828542, 10.53945940, 7.62616021, 6.56810951, -0.04577084, 62.74958214, 0.34168767
        ], dtype=np.float32)

        self.scaler_scale = np.array([
            0.06401216, 1.08814601, 1059.84393089, 36.60614492, 5.30129321, 10.62789249, 19.33451569, 43.79186508, 45.40435988, 44.59295743, 44.68933850, 49.50425672, 40.23375292, 35.57198773, 31.09990037, 23.36153376, 17.99208135, 10.42618601, 5.30225932, 2.97124732, 0.32592006, 32.16390344, 0.00355665
        ], dtype=np.float32)
                
    def init(self, angles):
        # Initialization of the desired angles for the rangefinders
        i = 0
        for i in range(0,len(angles)):
            angles[i]=-90+i*10    
    
    # --- AQUÍ OCURRE LA MAGIA ---
    def Update(self, buffer):
        # 1. Parsear el buffer a objeto CarState (sensors)
        sensors = CarState.CarState(buffer)
        
        # A) Construir el vector de estado (Debe ser IDÉNTICO al del entrenamiento)
        state = []
        state.append(sensors.getAngle()) # S_angle
        state.append(sensors.getGear()) # S_gear
        state.append(sensors.getRpm()) # S_rpm
        state.append(sensors.getSpeedX()) # S_speed
        state.append(sensors.getTracks()[0]) # S_track_0
        state.append(sensors.getTracks()[3]) # S_track_3
        state.append(sensors.getTracks()[4]) # S_track_4
        state.append(sensors.getTracks()[5]) # S_track_5
        state.append(sensors.getTracks()[6]) # S_track_6
        state.append(sensors.getTracks()[7]) # S_track_7
        state.append(sensors.getTracks()[8]) # S_track_8
        state.append(sensors.getTracks()[9]) # S_track_9
        state.append(sensors.getTracks()[10]) # S_track_10
        state.append(sensors.getTracks()[11]) # S_track_11
        state.append(sensors.getTracks()[12]) # S_track_12
        state.append(sensors.getTracks()[13]) # S_track_13
        state.append(sensors.getTracks()[14]) # S_track_14
        state.append(sensors.getTracks()[15]) # S_track_15
        state.append(sensors.getTracks()[16]) # S_track_16
        state.append(sensors.getTracks()[17]) # S_track_17
        state.append(sensors.getTrackPos()) # S_trackPos
        state.append(sensors.getWheelSpinVel(0)) # S_wheelSpinVel_0
        state.append(sensors.getZ()[0]) # S_z


        # 2. Preprocesamiento (Escalado)
        input_raw = np.array(state, dtype=np.float32)
        input_norm = (input_raw - self.scaler_mean) / self.scaler_scale
        input_onnx = input_norm.reshape(1, -1)

        # 3. Inferencia Independiente (3 Cerebros)
        # Inicializamos variables por defecto
        raw_pedals = [[0.0, 0.0]] # Accel, Brake
        pred_gear = 1
        pred_steer = 0.0
        try:
            # Nombre del input en ONNX
            in_name_a = self.sess_accel.get_inputs()[0].name
            in_name_g = self.sess_gear.get_inputs()[0].name
            in_name_s = self.sess_steer.get_inputs()[0].name

            # Ejecutamos cada red
            # OJO: sess_accel devuelve [[Accel, Brake]]
            raw_pedals = self.sess_accel.run(None, {in_name_a: input_onnx})[0]
            pred_gear  = self.sess_gear.run(None,  {in_name_g: input_onnx})[0][0][0]
            pred_steer = self.sess_steer.run(None, {in_name_s: input_onnx})[0][0][0]

        except Exception as e:
            print(f'❌ Error en inferencia: {e}')


      
        # 4. Procesado de Salida (Extracción y Limpieza)
        # Desempaquetamos Pedales (Columna 0: Accel, Columna 1: Brake)
        pred_accel = float(raw_pedals[0][0])
        pred_brake = float(raw_pedals[0][1])

        # Aplicamos Clips (Límites físicos)
        accel_out = np.clip(pred_accel, 0.0, 1.0)
        brake_out = np.clip(pred_brake, 0.0, 1.0)
        gear_out  = int(np.clip(np.round(pred_gear), 1, 6))
        steer_out = np.clip(float(pred_steer), -1.0, 1.0)

        # 5. Crear Acción Final
        print("Accel: ", accel_out,"Brake: ", brake_out,"Gear: ", gear_out,"Steer: ", steer_out)
        action = CarControl.CarControl(accel=accel_out, brake=brake_out, gear=gear_out, steer=steer_out, clutch=0, meta=0, focus=0)
        return str(action)
    
    def getInitAngles(self):
        return [-90,-80,-70,-60,-50,-40,-30,-20,-10, 0, 10, 20, 30, 40, 50, 60, 70, 80, 90]

    def onShutdown(self):
        print ("Bye bye!")
    
    def onRestart(self):
        print ("Restarting the race!")

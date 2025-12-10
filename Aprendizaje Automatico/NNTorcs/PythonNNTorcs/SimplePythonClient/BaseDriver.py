'''
This class is based on the C++ code of Daniele Loiacono
Modified to use ONNX Neural Network
'''
import SimplePythonClient.CarControl as CarControl
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
        
        # --- 1. CARGA DEL MODELO ONNX ---
        try:
            self.session = ort.InferenceSession("nntorcs_model.onnx")
            self.input_name = self.session.get_inputs()[0].name
            print("Modelo ONNX cargado correctamente.")
        except Exception as e:
            print(f"ERROR CRÍTICO: No se pudo cargar nntorcs_model.onnx. {e}")

        # --- 2. PARÁMETROS DEL SCALER (Tus arrays entrenados) ---
        self.scaler_mean = np.array([
            0.00044152, 8086.62900462, 2.73075022, 4314.36187740, 77.57477840, 
            7.12170799, 11.29315920, 18.75440354, 32.37319796, 38.96576663, 
            45.61052999, 52.59994598, 57.66694685, 50.72528763, 42.85226657, 
            34.98738452, 26.53467435, 17.08299741, 9.99609342, 7.42149788, -0.02511637
        ])

        self.scaler_scale = np.array([
            0.06234030, 6577.00911105, 1.07660265, 1031.79949051, 35.54888019, 
            4.66526208, 10.76674877, 19.63332139, 40.27038246, 40.93868822, 
            40.78565537, 43.14679524, 48.65082474, 42.10052799, 37.70015365, 
            31.38534264, 24.07408875, 17.40259727, 8.05898174, 3.48297626, 0.32419549
        ])
        
    def init(self, angles):
        # Initialization of the desired angles for the rangefinders
        i = 0
        for i in range(0,len(angles)):
            angles[i]=-90+i*10    
    
    # --- AQUÍ OCURRE LA MAGIA ---
    def Update(self, sensors):
        # 1. SEGURIDAD: Si el coche se atasca, usamos la lógica "clásica" para recuperarlo
        if self.stuck(sensors) or self.bringingCartBack > 0:
            return self.bringCarBackOnTrack(sensors)
        
        # 2. IA: Si estamos bien, conduce la Red Neuronal
        
        # A) Construir el vector de estado (Debe ser IDÉNTICO al del entrenamiento)
        state = []
        state.append(sensors.getAngleToTrackAxis())   # S_angle
        state.append(sensors.getDistanceFromStart())  # S_distRaced
        state.append(sensors.getGear())               # S_gear
        state.append(sensors.getRpm())                # S_rpm
        state.append(sensors.getSpeed())              # S_speed
        
        # Sensores de pista (Ojo con los índices, deben coincidir con tu entrenamiento)
        # Según tu código anterior: 0, 3, 4, 5... hasta 16
        track_indices = [0, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16]
        track_sensors = sensors.getTrackEdgeSensors()
        for idx in track_indices:
            state.append(track_sensors[idx])
            
        state.append(sensors.getTrackPosition())      # S_trackPos

        # B) Procesar para ONNX
        input_vector = np.array(state, dtype=np.float32)
        
        # Normalizar: (Valor - Media) / Escala
        input_norm = (input_vector - self.scaler_mean) / self.scaler_scale
        input_norm = input_norm.reshape(1, -1) # Formato [1, 21]

        # C) Predicción
        outputs = self.session.run(None, {self.input_name: input_norm})
        prediction = outputs[0][0]

        # D) Decodificar salida (Asumiendo orden: [Accel, Gear, Steer])
        pred_accel = float(prediction[0])
        pred_gear  = int(prediction[1])
        pred_steer = float(prediction[2])

        # E) Limitar valores físicos
        accel = np.clip(pred_accel, 0.0, 1.0)
        steer = np.clip(pred_steer, -1.0, 1.0)
        gear  = max(1, min(6, int(round(pred_gear)))) # Marchas 1-6

        # F) Retornar control
        # brake=0 porque asumimos que la red controla la velocidad soltando el acelerador
        # o puedes entrenar una salida extra para freno.
        return CarControl(accel=accel, brake=0, gear=gear, steer=steer, clutch=0, meta=0, focus=0)
    
    def getInitAngles(self):
        return [-90,-80,-70,-60,-50,-40,-30,-20,-10, 0, 10, 20, 30, 40, 50, 60, 70, 80, 90]

    def onShutdown(self):
        print ("Bye bye!")
    
    def onRestart(self):
        print ("Restarting the race!")

    # Este método getGear se mantiene por si lo usa la lógica de recuperación
    def getGear(self, sensors): 
        gear = sensors.getGear()
        rpm  = sensors.getRpm()
        if gear < 1: return 1
        if gear < 6 and rpm >= self.gearUp[gear-1]: return gear + 1
        else:
            if gear > 1 and rpm <= self.gearDown[gear-1]: return gear - 1
            else: return gear
        
    def stuck(self, cs):
        #check if car is currently stuckCounter
        # print ("abs(cs.getAngle()): ", abs(cs.getAngle()) , "   STUCKANGLE: ", STUCKANGLE)
        
        if( cs.getTrackPos() > 1.0 or cs.getTrackPos() < -1.0 ):
            self.stuckCounter = self.stuckCounter + 1
            self.bringingCartBack = 150
        else:
            if(self.bringingCartBack != 0):
                self.bringingCartBack = self.bringingCartBack - 1
            else:
                self.stuckCounter = 0
        return (self.stuckCounter > STUCKTIME)
    
    def bringCarBackOnTrack(self, cs):
        # Lógica de emergencia original
        if ( cs.getAngle()*cs.getTrackPos() > 0.0 ):
            gear = 1
            steer = cs.getAngle()/4
        else:
            steer = - cs.getAngle()/4 
            gear = -1 # gear R
                    
        if self.bringingCartBack < 5:
            return CarControl(0,1.0,0,0,0,0,0) # Frenar fuerte
                
        return CarControl(0.3,0.0,gear,steer,0,0,0)
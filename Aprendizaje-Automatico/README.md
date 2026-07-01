# Aprendizaje Automático (AA)

Este directorio alberga los proyectos, prácticas y cuadernos de teoría de la asignatura **Aprendizaje Automático** del Grado en Ingeniería Informática de la **Universidad de Huelva (UHU)**. El contenido abarca desde modelos clásicos de regresión y clasificación en Python (Jupyter Notebooks) hasta implementaciones de conducción autónoma en simuladores y aprendizaje por refuerzo en Java.

---

## 📁 Estructura del Directorio

### 📓 Jupyter Notebooks de Prácticas y Teoría
* **[AA_P0.ipynb](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Aprendizaje-Automatico/AA_P0.ipynb) (Práctica 0 - Introducción)**: Introducción al uso de la biblioteca `scikit-learn` en Python. Se entrena un clasificador de árbol de decisión (`DecisionTreeClassifier`) sobre el conjunto de datos *Iris*, evaluando el impacto del ratio de división *train-test* en la precisión (*accuracy*).
* **[AA_P1.ipynb](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Aprendizaje-Automatico/AA_P1.ipynb) (Práctica 1 - Regresión Lineal)**: Implementación manual del cálculo de la función de coste $J(\theta)$ y del algoritmo de descenso de gradiente para regresión lineal tanto univariable como multivariable, utilizando los datasets de soporte [regresion_1.csv](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Aprendizaje-Automatico/regresion_1.csv) y [regresion_2.csv](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Aprendizaje-Automatico/regresion_2.csv).
* **[AA_T2_EjerciciosRegresionLineal.ipynb](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Aprendizaje-Automatico/AA_T2_EjerciciosRegresionLineal.ipynb) (Tema 2 - Regresión)**: Resolución analítica y cálculo simbólico con la biblioteca `sympy` de derivadas parciales, ecuaciones de optimización y el funcionamiento del descenso de gradiente multivariable.
* **[AA_T3_EjerciciosMaquinaSoporteVectorial.ipynb](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Aprendizaje-Automatico/AA_T3_EjerciciosMaquinaSoporteVectorial.ipynb) (Tema 3 - SVM)**: Ejercicios teóricos de Máquinas de Soporte Vectorial (SVM) en 2D, calculando la ecuación del hiperplano de separación óptimo que maximiza el margen y la identificación matemática de los vectores soporte.

---

### 🚗 1. Proyecto `NNTorcs` (Conducción Autónoma mediante Redes Neuronales)
* **Código Fuente y Recursos**: [NNTorcs/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Aprendizaje-Automatico/NNTorcs/)

Pipeline completo para entrenar un agente controlador de coche en el simulador de carreras **TORCS** (The Open Racing Car Simulator) usando redes neuronales artificiales.
* **Recolección de Datos ([JavaNNTorcs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Aprendizaje-Automatico/NNTorcs/JavaNNTorcs/))**: Proyecto de Eclipse/Java con agentes recolectores de telemetría del coche (`RecolectorDriver.java`) durante las carreras.
* **Preprocesamiento ([AnalisisDatos.ipynb](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Aprendizaje-Automatico/NNTorcs/Tratamiento%20de%20datos/AnalisisDatos.ipynb))**: Carga y limpieza del conjunto masivo de telemetría recolectada en [datos_limpios.csv](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Aprendizaje-Automatico/NNTorcs/Tratamiento%20de%20datos/datos_limpios.csv).
* **Entrenamiento ([EntrenarModelo.ipynb](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Aprendizaje-Automatico/NNTorcs/Tratamiento%20de%20datos/EntrenarModelo.ipynb))**: Construcción y optimización de modelos neuronales en Python para controlar la dirección (`steer`), los pedales (`pedales` - acelerador/freno) y la marcha (`gear`).
* **Modelos Exportados**: Redes entrenadas exportadas a formato abierto **ONNX** (`cerebro_steer.onnx`, `cerebro_pedales.onnx`, `cerebro_gear.onnx`).
* **Controlador en Tiempo Real ([PID.py](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Aprendizaje-Automatico/NNTorcs/Tratamiento%20de%20datos/PID.py) / [client.py](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Aprendizaje-Automatico/NNTorcs/Tratamiento%20de%20datos/client.py))**: Scripts en Python que implementan controladores PID auxiliares y ejecutan los cerebros ONNX para pilotar el vehículo de forma autónoma interactuando con el simulador TORCS.

---

### 🏎️ 2. Proyecto `RLTorcs` (Conducción Autónoma mediante Aprendizaje por Refuerzo)
* **Código Fuente y Recursos**: [RLTorcs/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Aprendizaje-Automatico/RLTorcs/)

Implementación de algoritmos de **Aprendizaje por Refuerzo (Q-Learning)** en Java para entrenar diferentes aspectos de la conducción en el simulador TORCS sin supervisión previa.
* **Modelado del Entorno ([QLearning/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Aprendizaje-Automatico/RLTorcs/src/QLearning/))**:
  * `EnvAccel.java`, `EnvGear.java`, y `EnvSteer.java`: Definen las abstracciones de estado, el espacio de acciones posibles, y las funciones de recompensa (reward) para la aceleración, el cambio de marcha y el giro de volante.
  * `QLearning.java` y `Politica.java`: Implementación de la tabla Q, las tasas de aprendizaje, descuento ($\gamma$), y la política de exploración $\epsilon$-greedy.
* **Entrenamiento y Test ([Agentes/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Aprendizaje-Automatico/RLTorcs/src/Agentes/))**: Scripts específicos de entrenamiento autónomo como `DriverAccelTrain.java` y de testeo de la política aprendida como `DriverAccelTest.java`.
* **Visualización**: Monitores de aprendizaje que pintan de forma gráfica el progreso del entrenamiento en tiempo real (`MonitorGrafico.java`, `MonitorHistograma.java`).
* **Conocimiento**: El aprendizaje consolidado se persiste serializado dentro del subdirectorio [Knowledge/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Aprendizaje-Automatico/RLTorcs/Knowledge/).

---

### 🗺️ 3. Proyecto `RLMaze` (Q-Learning en Laberinto)
* **Código Fuente y Recursos**: [RLMaze/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Aprendizaje-Automatico/RLMaze/)

Una implementación simplificada y didáctica en Java de **Q-Learning** para resolver un laberinto bidimensional:
* El agente aprende a navegar desde un punto de inicio hasta la meta a través de recompensas positivas y negativas.
* Carga la estructura del mapa desde el fichero plano [maze.txt](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Aprendizaje-Automatico/RLMaze/src/resources/maze.txt).
* Implementado en la clase [QLearning.java](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Aprendizaje-Automatico/RLMaze/src/QLearning/QLearning.java).

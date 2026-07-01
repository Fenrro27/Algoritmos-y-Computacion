# Modelos Bioinspirados y Heurísticas de Búsqueda (MBHB)

Este directorio contiene los desarrollos y prácticas correspondientes a la asignatura **Modelos Bioinspirados y Heurísticas de Búsqueda** del Grado en Ingeniería Informática de la **Universidad de Huelva (UHU)**. 

El foco principal de las prácticas es el diseño, implementación e instrumentación de algoritmos heurísticos y metaheurísticos para resolver el **Problema de Asignación Cuadrática (QAP - Quadratic Assignment Problem)**, evaluando su rendimiento frente a soluciones óptimas conocidas sobre datasets del estado del arte.

---

## 📘 El Problema QAP (Quadratic Assignment Problem)
El **QAP** es un problema clásico de optimización combinatoria clasificado como **NP-difícil**. Consiste en asignar un conjunto de $n$ instalaciones a $n$ localizaciones con el fin de minimizar el coste total de flujo-distancia:
$$\text{Minimizar } \sum_{i=1}^{n} \sum_{j=1}^{n} f_{ij} d_{\pi(i)\pi(j)}$$
donde $f_{ij}$ representa la matriz de flujos entre instalaciones y $d_{xy}$ representa la matriz de distancias entre localizaciones.

### 📊 Conjuntos de Datos Evaluados (Ubicados en [DataSets/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos-Bioinspirados-y-Heuristicas-de-Busqueda/DataSets/)):
* `tai25b`: Problema de dimensión mediana ($n=25$) con flujo de datos estructurado.
* `sko90`: Problema de tamaño grande ($n=90$).
* `tai150b`: Problema de gran escala ($n=150$).

Los algoritmos implementados comparan sus resultados obtenidos con las soluciones óptimas globales conocidas de cada dataset.

---

## 📂 Contenido de las Prácticas y Algoritmos

El código fuente de todos los algoritmos se encuentra estructurado en el directorio [Algoritmos/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos-Bioinspirados-y-Heuristicas-de-Busqueda/Algoritmos/):

### 🔹 Práctica 1: Heurísticas Constructivas y Búsquedas Locales Iniciales
* **Cuaderno de Ejecución**: [QAP_P1.ipynb](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos-Bioinspirados-y-Heuristicas-de-Busqueda/QAP_P1.ipynb)
* **Algoritmos Implementados**:
  * **Greedy Constructivo** ([AlgoritmoComparacionGreedy.py](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos-Bioinspirados-y-Heuristicas-de-Busqueda/Algoritmos/AlgoritmoComparacionGreedy.py)): Genera una solución inicial acoplando de manera prioritaria flujos máximos en distancias mínimas.
  * **Búsqueda Aleatoria** ([BusquedaAleatoria.py](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos-Bioinspirados-y-Heuristicas-de-Busqueda/Algoritmos/BusquedaAleatoria.py)): Generación aleatoria de soluciones como cota inferior de rendimiento.
  * **Búsqueda Local - El Mejor Vecino** ([BusquedaLocal.py](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos-Bioinspirados-y-Heuristicas-de-Busqueda/Algoritmos/BusquedaLocal.py)): Explora el vecindario completo mediante operador de intercambio 2-opt y selecciona la mejor mejora.
  * **Búsqueda Local - El Primer Mejor** ([BusquedaLocal.py](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos-Bioinspirados-y-Heuristicas-de-Busqueda/Algoritmos/BusquedaLocal.py)): Recorre el vecindario aceptando el primer movimiento que mejore la solución actual.
  * **Enfriamiento Simulado / Simulated Annealing** ([EnfriamientoSimulado.py](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos-Bioinspirados-y-Heuristicas-de-Busqueda/Algoritmos/EnfriamientoSimulado.py)): Metaheurística basada en la termodinámica que permite escapar de óptimos locales aceptando soluciones peores de forma probabilística dependiente de la temperatura.
  * **Búsqueda Tabú** ([BusquedaTabu.py](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos-Bioinspirados-y-Heuristicas-de-Busqueda/Algoritmos/BusquedaTabu.py)): Metaheurística con memoria de corto plazo que prohíbe volver a estados anteriores registrados en la "Lista Tabú".
* **Resultados**: Guardados en [resultados_P1.csv](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos-Bioinspirados-y-Heuristicas-de-Busqueda/resultados_P1.csv).

### 🔹 Práctica 2: Metaheurísticas de Construcción y Exploración Avanzadas
* **Cuaderno de Ejecución**: [QAP_P2.ipynb](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos-Bioinspirados-y-Heuristicas-de-Busqueda/QAP_P2.ipynb)
* **Algoritmos Implementados**:
  * **GRASP (Greedy Randomized Adaptive Search Procedure)** ([GRASP.py](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos-Bioinspirados-y-Heuristicas-de-Busqueda/Algoritmos/GRASP.py)): Genera soluciones combinando selección probabilística adaptativa sobre una lista restringida de candidatos (RCL).
  * **GRASP + Búsqueda Local**: Aplica intensificación mediante búsqueda local 2-opt tras cada fase de construcción de GRASP.
  * **ILS (Iterated Local Search)** ([ILS.py](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos-Bioinspirados-y-Heuristicas-de-Busqueda/Algoritmos/ILS.py)): Aplica de forma iterativa búsqueda local tras efectuar una perturbación controlada en la solución actual para cambiar de cuenca de atracción.
  * **VNS (Variable Neighborhood Search)** ([VNS.py](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos-Bioinspirados-y-Heuristicas-de-Busqueda/Algoritmos/VNS.py)): Explora el espacio alternando sistemáticamente entre diferentes estructuras de vecindario (K-vecindarios).
* **Resultados**: Guardados en [resultados_P2.csv](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos-Bioinspirados-y-Heuristicas-de-Busqueda/resultados_P2.csv).

### 🔹 Práctica 3: Algoritmos Poblacionales y Bioinspirados
* **Cuaderno de Ejecución**: [QAP_P3.ipynb](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos-Bioinspirados-y-Heuristicas-de-Busqueda/QAP_P3.ipynb)
* **Algoritmos Implementados**:
  * **Algoritmo Genético Básico (AGB)** ([AlgoritmoGeneticoBasico.py](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos-Bioinspirados-y-Heuristicas-de-Busqueda/Algoritmos/AlgoritmoGeneticoBasico.py)): Modelo poblacional estándar basado en selección, cruce (OX o PMX) y mutación clásica.
  * **Algoritmo Genético CHC** ([AlgoritmoGeneticoCHC.py](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos-Bioinspirados-y-Heuristicas-de-Busqueda/Algoritmos/AlgoritmoGeneticoCHC.py)): Algoritmo evolutivo con cruce altamente disruptivo (HUX), selección elitista estricta libre de mutación continua, y reinicialización catastrófica de la población cuando se estanca el progreso.
  * **Algoritmo Genético Multimodal (AGM)** ([AlgoritmoGeneticoMultimodal.py](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos-Bioinspirados-y-Heuristicas-de-Busqueda/Algoritmos/AlgoritmoGeneticoMultimodal.py)): Incorpora técnicas de nichos (*crowding* o *sharing*) para mantener y desarrollar múltiples soluciones óptimas locales competitivas de forma paralela en la población.
* **Resultados**: Guardados en [resultados_P3.csv](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos-Bioinspirados-y-Heuristicas-de-Busqueda/resultados_P3.csv).

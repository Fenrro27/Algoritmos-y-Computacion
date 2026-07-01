# Fundamentos de Análisis de Algoritmos (FAA)

Este directorio contiene las prácticas de la asignatura **Fundamentos de Análisis de Algoritmos** del Grado en Ingeniería Informática de la **Universidad de Huelva (UHU)**. El objetivo principal es la implementación y el análisis de la complejidad temporal y de operaciones de diferentes algoritmos de búsqueda, cálculo matemático y ordenación.

Las prácticas están organizadas en dos soluciones independientes de Visual Studio:

---

## 📁 1. Solución `FAA_P1,2,3`
* **Solución de Visual Studio**: [FAA_P1,2,3.sln](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Fundamentos-de-Análisis-de-Algoritmos/FAA_P1,2,3/FAA_P1,2,3.sln)
* **Código Fuente**: [FAA_P1,2,3/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Fundamentos-de-Análisis-de-Algoritmos/FAA_P1,2,3/FAA_P1,2,3/)

Contiene las prácticas 1, 2 y 3 implementadas en un proyecto de consola de C++. Su menú principal en [main.cpp](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Fundamentos-de-Análisis-de-Algoritmos/FAA_P1,2,3/FAA_P1,2,3/main.cpp) permite ejecutar los experimentos para cada una:

### 🔍 Práctica 1: Búsqueda Secuencial (Empírica e Instrumentación de OE)
Analiza la búsqueda de un elemento en un vector aleatorio sin duplicados de tamaño $N$.
* **P1_1_Busqueda**: Medición directa del tiempo de ejecución.
* **P1_2A_Busqueda** y **P1_2B_Busqueda**: Mediciones refinadas que promedian tiempos tras múltiples repeticiones de la búsqueda para mitigar el ruido y la imprecisión del reloj del sistema operativo.
* **P1_3_BusquedaOE**: Cuenta explícitamente el número de Operaciones Elementales (OE) efectuadas en el mejor, peor y caso medio de la búsqueda.
* **Resultados**: Genera archivos CSV con los tiempos y operaciones recopiladas.

### ⚡ Práctica 2: Cálculo de Potencia ($a^n$)
Comparativa empírica de tres enfoques algorítmicos para la exponenciación matemática:
* **Iterativo**: Multiplicaciones sucesivas con coste temporal lineal $O(n)$ y $O(n)$ OE ([P2_1_Potencia](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Fundamentos-de-Análisis-de-Algoritmos/FAA_P1,2,3/FAA_P1,2,3/P2_1_Potencia.cpp)).
* **Recursivo**: Llamadas recursivas con coste temporal lineal $O(n)$ y $O(n)$ OE ([P2_2_PotenciaRec](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Fundamentos-de-Análisis-de-Algoritmos/FAA_P1,2,3/FAA_P1,2,3/P2_2_PotenciaRec.cpp)).
* **Divide y Vencerás**: Exponenciación binaria recursiva que reduce la complejidad temporal a coste logarítmico $O(\log n)$ ([P2_3_PotenciaDV](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Fundamentos-de-Análisis-de-Algoritmos/FAA_P1,2,3/FAA_P1,2,3/P2_3_PotenciaDV.cpp)).

### 📈 Práctica 3: Análisis Teórico y Recurrencias
Simulación analítica para comparar el ritmo de crecimiento de dos algoritmos $A_1$ y $A_2$ modelados mediante ecuaciones de recurrencia en [P3_FAA.cpp](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Fundamentos-de-Análisis-de-Algoritmos/FAA_P1,2,3/FAA_P1,2,3/P3_FAA.cpp):
* **Algoritmo $A_1$**:
  $$T(n) = \begin{cases} 1 & \text{si } n < 5 \\ 3n^2 + 2T(n-1) + 3T(n-2) + 2n & \text{si } n \ge 5 \end{cases}$$
* **Algoritmo $A_2$**:
  $$T(n) = \begin{cases} n! & \text{si } n < 5 \\ n^2 \log(n) + 2T(n/2) + T(n/3) & \text{si } n \ge 5 \end{cases}$$
* **Resultados**: Exporta las evaluaciones numéricas a `resultadosP3.csv` para trazar y contrastar sus curvas de complejidad.

---

## 📁 2. Solución `FAA_P4`
* **Solución de Visual Studio**: [FAA_P4.sln](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Fundamentos-de-Análisis-de-Algoritmos/FAA_P4/FAA_P4.sln)
* **Código Fuente del Proyecto**: [Algoritmica/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Fundamentos-de-Análisis-de-Algoritmos/FAA_P4/Algoritmica/)

Solución dedicada al análisis empírico avanzado de algoritmos de ordenación, algoritmos de búsqueda y problemas de frecuencias en vectores. Incluye instrumentación automatizada y scripts gráficos.

### 🛠️ Estructura del Código:
* **Generación de Datos**: [GenerarVectores](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Fundamentos-de-Análisis-de-Algoritmos/FAA_P4/Algoritmica/GenerarVectores.cpp) genera vectores con distribuciones uniformes o personalizadas.
* **Algoritmos Empíricos**: Implementados en [AlgoritmosEmpiricos.cpp](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Fundamentos-de-Análisis-de-Algoritmos/FAA_P4/Algoritmica/AlgoritmosEmpiricos.cpp):
  * **Ordenación**:
    * `MergeSort`: Algoritmo de ordenación recursivo basado en mezclas ($O(n \log n)$).
    * `MergeSort Hibrido`: Combina *MergeSort* con *Insertion Sort*, delegando la ordenación a este último cuando el subvector es menor o igual a un umbral $K$.
    * `InsertionSort`: Método de ordenación por inserción ($O(n^2)$).
    * `MergeSort Modificado`: Optimización de MergeSort que minimiza la creación/copia de vectores en memoria mediante el uso de un vector auxiliar preasignado.
  * **Búsqueda**:
    * `BusquedaBinaria1` / `BusquedaBinaria2`: Variantes recursivas de búsqueda binaria.
    * `BusquedaBinaria1Iterativo` / `BusquedaBinaria2Iterativo`: Variantes iterativas de búsqueda binaria.
    * `BusquedaBinariaInterpolacion` e `InterpolacionIterativo`: Estiman la posición óptima del elemento mediante una interpolación basada en los valores extremos del subvector ordenado.
  * **Frecuencias**:
    * `encontrarUnicos`: Encuentra todos los valores del vector que aparecen **exactamente una vez**.
    * `encontrarElementosConFrecuencia`: Filtra y recupera aquellos elementos cuya frecuencia exacta de aparición coincide con un parámetro $k$.

* **Controlador de Tests**: [TestAlgoritmos.cpp](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Fundamentos-de-Análisis-de-Algoritmos/FAA_P4/Algoritmica/TestAlgoritmos.cpp) permite ejecutar pruebas de corrección, obtener costes del caso medio, realizar comparaciones directas de dos métodos o evaluar el rendimiento según la distribución de los vectores (uniformes vs sesgados). También incluye el experimento de calibración del umbral óptimo $K$ para el MergeSort Híbrido.

### 📊 Análisis de Resultados y Gráficas
* **Carpeta de Resultados**: [resultados/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Fundamentos-de-Análisis-de-Algoritmos/FAA_P4/Algoritmica/resultados/)
* **Script de Visualización**: [Graficas.py](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Fundamentos-de-Análisis-de-Algoritmos/FAA_P4/Algoritmica/resultados/Graficas.py)
  
El script en Python procesa los ficheros CSV generados en tiempo de ejecución por el programa en C++ y utiliza la librería `matplotlib` para mostrar de forma interactiva las gráficas de tiempos de ejecución (en microsegundos) vs tamaño del vector, facilitando el análisis visual y la redacción de memorias de prácticas.

# Sudoku Solver - High Performance CSP Implementation

Este proyecto implementa un resolutor de **Sudoku** de alto rendimiento utilizando técnicas de **Inteligencia Artificial** basadas en **Problemas de Satisfacción de Restricciones (CSP)**. El programa está optimizado para procesar grandes volúmenes de tableros de forma extremadamente rápida.

## 🧩 El Problema: Sudoku

El Sudoku es un rompecabezas lógico donde se debe completar una rejilla de 9x9 con dígitos del 1 al 9, de forma que cada fila, columna y subcuadrícula de 3x3 contenga todos los números sin repeticiones.

### Ejemplo de Representación (String de 81 caracteres)
`53..7....6..195....98....6.8...6...34..8.3..17...2...6.6....28....419..5....8..79`

---

## 🚀 Arquitectura y Algoritmos

El problema se ha modelado como un **CSP** donde cada celda es una variable con un dominio inicial de `{1, 2, 3, 4, 5, 6, 7, 8, 9}` (a menos que esté predefinida).

### 1. Modelado con Bitmasks
Para maximizar el rendimiento, el dominio de cada celda se representa mediante un **bitmask de 16 bits**. Esto permite realizar operaciones de consistencia y validación mediante aritmética de bits, eliminando la necesidad de estructuras de datos pesadas.

### 2. Algoritmo AC3 (Arc Consistency 3)
Se utiliza **AC3** para reducir los dominios de las variables mediante la propagación de restricciones:
*   Cada vez que una celda fija su valor, se eliminan las posibilidades en sus vecinos (fila, columna y caja).
*   Se mantiene una cola de arcos para asegurar la consistencia local de todo el tablero.

### 3. Backtracking Optimizado
Para sudokus de nivel experto donde AC3 no es suficiente, se aplica una búsqueda con **Backtracking**:
*   **Heurística de Valor Mínimo**: Selecciona la celda con el dominio más pequeño para minimizar el factor de ramificación.
*   **Propagación Inmediata**: Cada paso del backtracking ejecuta una fase de AC3 para podar el árbol de búsqueda.

---

## ⚡ Optimizaciones de Rendimiento

Este solver ha sido diseñado bajo una arquitectura de **"Zero Allocation"** durante la fase crítica de resolución:
*   **ThreadLocal Storage**: Cada hilo de ejecución mantiene su propia instancia de tableros y nodos pre-asignados, evitando la sobrecarga del Garbage Collector (GC).
*   **Paralelismo Masivo**: Utiliza un `ExecutorService` para distribuir la carga de trabajo entre todos los núcleos disponibles del sistema.
*   **Retry Logic**: Implementa un sistema de reintento para asegurar la máxima robustez en la resolución de casos complejos.

## 🛠️ Tecnologías y Resultados

*   **Lenguaje**: Java
*   **Rendimiento**: Capaz de resolver miles de sudokus por segundo.
*   **Precisión**: Sistema de validación estricta post-resolución.

## 📖 Instrucciones de Uso

1.  Coloca los tableros (formato línea de 81 caracteres) en archivos `.txt` dentro de la carpeta `tableros/`.
2.  Compila el proyecto:
    ```bash
    javac Main.java Node.java AC3.java Backtracking.java
    ```
3.  Ejecuta el programa:
    ```bash
    java Main
    ```
4.  Las soluciones se guardarán en la carpeta `soluciones/` con el prefijo `Sol_`.

---
*Desarrollado para la asignatura de Sistemas Inteligentes de la Universidad de Huelva - 2026.*

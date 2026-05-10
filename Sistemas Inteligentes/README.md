# Algoritmos y Computación - Sistemas Inteligentes

Este repositorio contiene las prácticas y proyectos desarrollados para la asignatura de **Sistemas Inteligentes** (Universidad de Huelva, 2026). El enfoque principal es la resolución de problemas complejos mediante técnicas de **Búsqueda** e **Inteligencia Artificial**.

## 🚀 Proyectos Principales

A continuación se detallan las prácticas implementadas, centradas en la resolución de problemas de satisfacción de restricciones (CSP).

### 1. [Sudoku Solver](./SudokuSolver/)
Resolutor de Sudokus de alto rendimiento optimizado con aritmética de bits y procesamiento paralelo.
*   **Técnicas**: AC3, Backtracking, Bitmasks.
*   **Optimización**: Zero-allocation architecture y multithreading.
*   **Estado**: Completado.

### 2. [NumberSums Solver](./NumberSumsSolver/)
Implementación de un resolutor para el puzzle NumberSums, centrado en restricciones N-arias de suma.
*   **Técnicas**: Modelado CSP, AC3 con propagación de rangos (Min/Max sum), Backtracking con MAC.
*   **Rendimiento**: Capaz de resolver cientos de tableros en milisegundos.
*   **Estado**: Completado.

---

## 🛠️ Tecnologías Utilizadas
*   **Lenguaje Principal**: Java 17+
*   **Entornos de Prueba**: Procesamiento por lotes (Batch processing) de datasets extensos.
*   **Herramientas**: Concurrencia avanzada (`java.util.concurrent`).

## 📂 Estructura del Repositorio
*   `SudokuSolver/`: Código fuente, tableros de prueba y soluciones para el problema del Sudoku.
*   `NumberSumsSolver/`: Código fuente y datasets para el enigma NumberSums.
*   `2022_UHU_GVGAI/`: Agentes para entornos de videojuegos.

---
*Repositorio mantenido por [Fenrro27](https://github.com/Fenrro27).*

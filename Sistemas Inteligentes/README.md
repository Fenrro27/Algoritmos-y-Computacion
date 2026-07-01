# Sistemas Inteligentes (SI)

Este directorio contiene las prácticas, proyectos de desarrollo y material de estudio de la asignatura **Sistemas Inteligentes** del Grado en Ingeniería Informática de la **Universidad de Huelva (UHU)**. Las actividades están centradas en la resolución de problemas complejos mediante búsquedas heurísticas, planificación automática y problemas de satisfacción de restricciones (CSP).

---

## 📂 Contenido del Directorio

### 👾 1. Agentes de Videojuegos (GVGAI)
* **Proyecto**: [GVGAI/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Sistemas%20Inteligentes/GVGAI/)
* **Código de los Agentes**: [si2026/kevinjesusbandaalu/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Sistemas%20Inteligentes/GVGAI/src/si2026/kevinjesusbandaalu/)

Desarrollo e implementación de agentes inteligentes capaces de jugar de forma autónoma a diferentes videojuegos haciendo uso del framework **GVGAI** (General Video Game AI):
* **P01 / P02**: Implementación de agentes reactivos básicos y búsqueda simple.
* **P04 (Gymkhana)**: Desarrollo de agentes inteligentes avanzados dotados de búsqueda estructurada en grafos (**A***) para planificar y esquivar obstáculos dinámicos en tiempo real.
* **P05 / P07**: Prácticas adicionales de optimización y algoritmos de control.

---

### 🧩 2. Resolución de Problemas CSP (Constraint Satisfaction Problems)
Implementación en Java de resolutores eficientes de problemas de satisfacción de restricciones sin usar librerías externas:
* **[SudokuSolver/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Sistemas%20Inteligentes/SudokuSolver/)**: Resolutor de sudokus de alto rendimiento basado en:
  * El algoritmo de consistencia de arcos **AC3**.
  * Algoritmo de exploración **Backtracking** óptimo.
  * Optimización mediante máscaras de bits (*Bitmasks*) y concurrencia paralela (`java.util.concurrent`) con una arquitectura libre de asignaciones de memoria adicionales (*zero-allocation*).
* **[NumberSumsSolver/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Sistemas%20Inteligentes/NumberSumsSolver/)**: Resolutor del puzzle matemático *NumberSums* (basado en restricciones N-arias de suma):
  * Emplea modelado CSP estricto.
  * Consistencia de arcos **AC3** combinada con propagación de rangos (suma mínima/máxima).
  * Algoritmo **Backtracking** optimizado con mantenimiento de consistencia de arcos (**MAC**).

---

### 📅 3. Planificador Automático (Planificación en Inteligencia Artificial)
* **Proyecto**: [Planificador/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Sistemas%20Inteligentes/Planificador/)
* **Algoritmos**: [strips/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Sistemas%20Inteligentes/Planificador/src/planning/strips/), [pop/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Sistemas%20Inteligentes/Planificador/src/planning/pop/), y [busqueda/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Sistemas%20Inteligentes/Planificador/src/planning/busqueda/)

Implementación en Java de motores de planificación automática para alcanzar metas a partir de un estado inicial:
* **STRIPS (Stanford Research Institute Planning System)**: Planificador de orden total lineal clásico basado en pilas de objetivos.
* **POP (Partial Order Planner)**: Algoritmo de planificación de orden parcial no lineal, que gestiona enlaces causales y detecta amenazas en paralelo.
* **Planificación por Búsqueda**: Planificador heurístico en espacio de estados utilizando búsquedas informadas.
* **Visualizador**: Incluye un script en Python ([visualize.py](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Sistemas%20Inteligentes/Planificador/visualize.py)) para trazar y analizar gráficamente los resultados y la eficiencia de los planes generados.

---

### 📖 4. Apuntes Teóricos de la Asignatura
* **Recurso**: [Temario/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/)

Compilación estructurada de los temas teóricos de la asignatura organizados en formato de libro digital interactivo. Incluye fuentes en LaTeX ([Sistemas_Inteligentes_Teoria.tex](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Sistemas_Inteligentes_Teoria.tex)) y gráficos explicativos. Cubre desde representación del conocimiento e inferencia lógica hasta búsqueda heurística.

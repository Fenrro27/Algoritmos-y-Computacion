# NumberSums Solver - CSP Implementation

Este proyecto implementa un resolutor de **NumberSums** (Enigmas Numéricos) utilizando técnicas de **Inteligencia Artificial** basadas en **Problemas de Satisfacción de Restricciones (CSP)**. El programa es capaz de resolver tableros de diversos tamaños aplicando los algoritmos AC3 y Backtracking con Propagación.

## 🧩 El Problema: NumberSums

NumberSums consiste en una matriz cuadrada de números. El objetivo es seleccionar qué números de cada fila y columna deben mantenerse para que su suma coincida exactamente con un valor objetivo dado para esa fila y columna. Los números no seleccionados se sustituyen por un punto (`.`).

### Ejemplo de Tablero Original
| Fila / Col | C1 | C2 | C3 | **Suma Fila** |
| :--- | :---: | :---: | :---: | :---: |
| **R1** | 7 | 9 | 6 | **13** |
| **R2** | 3 | 5 | 1 | **5** |
| **R3** | 6 | 9 | 7 | **7** |
| **Suma Col** | **7** | **5** | **13** | |

### Resolución del Ejemplo
| Fila / Col | C1 | C2 | C3 | **Suma Fila** |
| :--- | :---: | :---: | :---: | :---: |
| **R1** | 7 | . | 6 | **13** |
| **R2** | . | 5 | . | **5** |
| **R3** | . | . | 7 | **7** |
| **Suma Col** | **7** | **5** | **13** | |

*(7+6=13, 5=5, 7=7 en filas; 7=7, 5=5, 6+7=13 en columnas)*

---

## 🚀 Algoritmo de Resolución

El problema se ha modelado como un **CSP** donde cada celda es una variable con un dominio binario: `{Seleccionado, No Seleccionado}`.

### 1. Modelado de Restricciones
Se han implementado **restricciones de suma (N-arias)**. Para cada fila y columna, el algoritmo mantiene:
*   **Suma Mínima (Low)**: La suma de los números que ya han sido obligatoriamente seleccionados.
*   **Suma Máxima (High)**: La suma de los números seleccionados más los números que aún podrían ser seleccionados (pendiente de decisión).

### 2. Algoritmo AC3 (Arc Consistency 3)
Se utiliza **AC3** para propagar las restricciones y reducir los dominios antes y durante la búsqueda:
*   Si añadir un número al conjunto "Seleccionado" hace que superemos la suma objetivo, ese número se descarta (se convierte en punto).
*   Si descartar un número hace que sea imposible alcanzar la suma objetivo con los restantes, el número se marca obligatoriamente como "Seleccionado".
*   AC3 gestiona una cola de restricciones; cada vez que una celda se fija, se vuelven a comprobar su fila y su columna.

### 3. Backtracking con MAC (Maintaining Arc Consistency)
Para los casos donde AC3 no es suficiente para resolver el tablero completo (tableros con múltiples soluciones posibles o alta ambigüedad), el programa utiliza un **Backtracking recursivo**:
*   Selecciona una celda no decidida.
*   Prueba una asignación y ejecuta AC3 inmediatamente para propagar las consecuencias.
*   Si detecta una inconsistencia (dominio vacío), deshace el cambio y prueba la otra opción.

---

## 🛠️ Tecnologías y Rendimiento

*   **Lenguaje**: Java
*   **Concurrencia**: Uso de `ExecutorService` para resolver múltiples tableros en paralelo aprovechando todos los núcleos del procesador.
*   **Eficiencia**: Capaz de resolver **510 tableros** (desde 3x3 hasta 10x10) en menos de **400ms**.

## 📖 Instrucciones de Uso

1.  Coloca los tableros en un archivo llamado `tableros.txt` dentro de la carpeta `tableros/`.
2.  Compila el proyecto:
    ```bash
    javac Main.java Node.java AC3.java Backtracking.java
    ```
3.  Ejecuta el programa:
    ```bash
    java Main
    ```
4.  Los resultados se generarán en la carpeta `soluciones/` con el prefijo `Sol_`.

---
*Desarrollado para la asignatura de Sistemas Inteligentes de la Universidad de Huelva - 2026.*

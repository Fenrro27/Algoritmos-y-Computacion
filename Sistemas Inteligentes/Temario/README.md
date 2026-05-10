# Temario de Sistemas Inteligentes

Este directorio contiene el material teórico de la asignatura de **Sistemas Inteligentes** (Universidad de Huelva), organizado en formato de libro/apuntes detallados desarrollados en **LaTeX**.

## 📖 Descripción

El proyecto consiste en una recopilación estructurada de los temas de la asignatura, permitiendo generar un documento PDF unificado con toda la teoría, algoritmos y conceptos clave.

## 🗂️ Contenido del Temario

El material se divide en los siguientes temas (ubicados en la carpeta `Temas/`):

*   **Tema 1**: Introducción a los Sistemas Inteligentes.
*   **Tema 2**: Representación del Conocimiento.
*   **Tema 3**: Algoritmos de Búsqueda No Informada.
*   **Tema 4**: Algoritmos de Búsqueda Informada (Heurísticas).
*   **Tema 5**: Búsqueda Local y Optimización.
*   **Tema 6**: Sistemas Basados en Reglas.
*   **Tema 7**: Otros paradigmas de Inteligencia Artificial.

*(Nota: Los contenidos específicos pueden variar según el progreso del curso).*

## 🛠️ Instrucciones de Compilación

Para generar el PDF a partir del código fuente LaTeX:

1.  Asegúrate de tener instalado una distribución de LaTeX (como TeX Live o MiKTeX).
2.  Compila el archivo principal:
    ```bash
    pdflatex Sistemas_Inteligentes_Teoria.tex
    ```
3.  (Opcional) Si usas `latexmk`:
    ```bash
    latexmk -pdf Sistemas_Inteligentes_Teoria.tex
    ```

El resultado será el archivo `Sistemas_Inteligentes_Teoria.pdf` con el índice y todos los capítulos enlazados.

---
*Apuntes organizados para el curso 2026.*

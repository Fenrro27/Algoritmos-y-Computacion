# Libro de Temarios Teóricos (Grado en Ingeniería Informática - UHU)

Este directorio está concebido como el espacio central de documentación teórica del Grado de Ingeniería Informática en la **Universidad de Huelva (UHU)**. 

---

## 🎯 Visión y Objetivo del Proyecto
El propósito de este directorio es **unificar toda la teoría y conceptos clave** de las asignaturas relacionadas con la computación, algoritmos e inteligencia artificial en un **único formato de libro o compendio teórico unificado**. 

En lugar de mantener apuntes dispersos e independientes para cada asignatura, se utiliza el sistema de tipografía **LaTeX** para compilar un volumen consolidado de apuntes académicos de alta calidad. Este libro compartirá:
1. Una estética visual homogénea, limpia y profesional (siguiendo un estilo adaptado de plantillas de Trabajos de Fin de Grado).
2. Un índice unificado de capítulos y bloques de asignaturas.
3. Una base de datos bibliográfica compartida.

---

## 📂 Estructura General del Proyecto

* **[Portada.tex](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Portada.tex)**: Plantilla LaTeX de la portada oficial del compendio, configurada con marcas de agua, marcos institucionales y logotipos de la ETSI y la Universidad de Huelva.
* **[Media/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Media/)**: Carpeta centralizada para recursos multimedia (diagramas explicativos, esquemas lógicos e imágenes de la portada).
* **[Temas/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/)**: Subdirectorio que aloja los capítulos individuales en archivos `.tex`, los cuales son importados dinámicamente por los documentos principales.

---

## 📚 Volúmenes y Asignaturas Incluidas

### 🧠 Volumen I: Sistemas Inteligentes
* **Documento Principal**: [Sistemas_Inteligentes_Teoria.tex](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Sistemas_Inteligentes_Teoria.tex)
  
Es el primer bloque completado y estructurado del compendio, compuesto por 7 temas distribuidos de la siguiente forma en el subdirectorio `Temas/`:
* **[Tema 1](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/Tema_1.tex)**: Introducción a los Sistemas Inteligentes.
* **[Tema 2](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/Tema_2.tex)**: Representación del Conocimiento.
* **[Tema 3](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/Tema_3.tex)**: Algoritmos de Búsqueda No Informada.
* **[Tema 4](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/Tema_4.tex)**: Algoritmos de Búsqueda Informada (Heurísticas).
* **[Tema 5](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/Tema_5.tex)**: Búsqueda Local y Optimización.
* **[Tema 6](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/Tema_6.tex)**: Sistemas Basados en Reglas.
* **[Tema 7](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/Tema_7.tex)**: Otros paradigmas de Inteligencia Artificial (Agentes Inteligentes y Aprendizaje por Refuerzo).

### 🚀 Futuros Volúmenes (Planificados)
El proyecto está diseñado para expandirse e integrar como nuevos volúmenes los temarios de las siguientes asignaturas del repositorio:
* **Volumen II: Aprendizaje Automático** (Teoría de modelos supervisados, regresiones, SVMs y redes neuronales).
* **Volumen III: Modelos Bioinspirados y Heurísticas de Búsqueda** (Fundamentos de algoritmos evolutivos, genéticos y búsqueda local avanzada).
* **Volumen IV: Modelos Avanzados de Computación** (Teoría del paradigma de programación funcional en Haskell).

---

## 🛠️ Instrucciones de Compilación

Para compilar las fuentes LaTeX a formato PDF de forma local:

1. Asegúrate de contar con una distribución instalada de LaTeX (como TeX Live o MiKTeX) que incluya soporte para fuentes vectoriales clásicas de helvética.
2. Abre una terminal en este directorio y compila el volumen deseado, por ejemplo, para Sistemas Inteligentes:
   ```bash
   pdflatex Sistemas_Inteligentes_Teoria.tex
   ```
3. Alternativamente, para compilar de forma limpia automatizando el procesamiento de referencias bibliográficas, utiliza `latexmk`:
   ```bash
   latexmk -pdf Sistemas_Inteligentes_Teoria.tex
   ```
   
El compilador generará un archivo `.pdf` unificado con portada, índice de contenidos dinámico, referencias cruzadas y bibliografía formateada bajo el estándar APA.

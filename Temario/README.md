# Libro de Temarios Teóricos (Grado en Ingeniería Informática - UHU)

Este directorio alberga las fuentes de documentación teórica del Grado de Ingeniería Informática en la **Universidad de Huelva (UHU)**. 

---

## 🎯 Estructura y Visión del Libro Unificado
El propósito de este proyecto es compilar toda la base teórica de las asignaturas orientadas a algoritmos, computación e inteligencia artificial del grado en un **único libro consolidado en LaTeX**.

El libro se organiza siguiendo rigurosamente el orden de asignaturas establecido en el [README.md](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/README.md) principal del repositorio. Cada asignatura constituye una **Parte (`\part{}`)** independiente, subdividida en capítulos temáticos detallados.

---

## 📂 Componentes del Proyecto

* **[Libro_Teoria.tex](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Libro_Teoria.tex)**: Archivo principal de compilación LaTeX que unifica las partes, capítulos, configuraciones tipográficas y bibliografía.
* **[Portada.tex](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Portada.tex)**: Diseño unificado de la portada del libro académico, con marcos institucionales y logotipos de la ETSI y la Universidad de Huelva.
* **[Bibliografia.bib](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Bibliografia.bib)**: Base de datos bibliográfica en formato BibTeX para la infografía y libros citados.
* **[Media/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Media/)**: Carpeta para imágenes, logotipos y diagramas explicativos incrustados en la teoría.
* **[Temas/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/)**: Carpeta que contiene subdirectorios independientes para cada una de las 8 asignaturas:

### 📚 Partes y Capítulos del Libro:

1. **[Parte I: Fundamentos de Análisis de Algoritmos](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/01_Fundamentos_Analisis_Algoritmos/)**:
   * [complejidad.tex](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/01_Fundamentos_Analisis_Algoritmos/complejidad.tex): Estudio de complejidades asintóticas y cotas temporales/espaciales.
   * [divide_venceras.tex](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/01_Fundamentos_Analisis_Algoritmos/divide_venceras.tex): Paradigma divide y vencerás y ecuaciones de recurrencia.
2. **[Parte II: Inteligencia Artificial](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/02_Inteligencia_Artificial/)**:
   * [sistemas_expertos.tex](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/02_Inteligencia_Artificial/sistemas_expertos.tex): Motores de inferencia y sistemas basados en reglas de producción (CLIPS).
3. **[Parte III: Algoritmia y Modelos de Computación](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/03_Algoritmia_Modelos_Computacion/)**:
   * [automatas.tex](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/03_Algoritmia_Modelos_Computacion/automatas.tex): Autómatas finitos, lenguajes regulares y gramáticas formales.
   * [computabilidad.tex](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/03_Algoritmia_Modelos_Computacion/computabilidad.tex): Máquinas de Turing y límites de la computabilidad (decidibilidad).
4. **[Parte IV: Representación del Conocimiento](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/04_Representacion_Conocimiento/)**:
   * [programacion_logica.tex](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/04_Representacion_Conocimiento/programacion_logica.tex): Fundamentos teóricos del paradigma de programación lógica, unificación y CLP(FD) (Prolog).
5. **[Parte V: Sistemas Inteligentes](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/05_Sistemas_Inteligentes/)**:
   * 7 capítulos teóricos sobre búsquedas heurísticas, consistencias CSP, planificación automática, sistemas multiagente y aprendizaje por refuerzo (Temas del 1 al 7).
6. **[Parte VI: Modelos Bioinspirados y Heurísticas de Búsqueda](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/06_Modelos_Bioinspirados/)**:
   * [metaheuristicas.tex](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/06_Modelos_Bioinspirados/metaheuristicas.tex): Metaheurísticas clásicas y evolutivas aplicadas al problema QAP.
7. **[Parte VII: Aprendizaje Automático](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/07_Aprendizaje_Automatico/)**:
   * [aprendizaje_supervisado.tex](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/07_Aprendizaje_Automatico/aprendizaje_supervisado.tex): Teoría de modelos supervisados (regresiones, SVMs y redes neuronales).
8. **[Parte VIII: Modelos Avanzados de Computación](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/08_Modelos_Avanzados_Computacion/)**:
   * [programacion_funcional.tex](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Temario/Temas/08_Modelos_Avanzados_Computacion/programacion_funcional.tex): Fundamentos teóricos y tipado fuerte del paradigma funcional (Haskell).

---

## 🛠️ Instrucciones de Compilación

Para compilar el compendio unificado a PDF de forma local:

1. Abre una terminal dentro de este directorio.
2. Ejecuta pdflatex sobre el archivo principal:
   ```bash
   pdflatex Libro_Teoria.tex
   ```
3. O bien compila de manera automatizada gestionando las citas bibliográficas con `latexmk`:
   ```bash
   latexmk -pdf Libro_Teoria.tex
   ```
   
El resultado de la compilación será el archivo `Libro_Teoria.pdf`, conteniendo el compendio completo de teoría con portada formal, tabla de contenidos generada dinámicamente y la bibliografía en formato APA.

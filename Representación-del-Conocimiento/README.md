# Representación del Conocimiento (RC) - Programación Lógica con Prolog

Este directorio alberga la colección completa de ejercicios y proyectos en **Prolog** para la asignatura **Representación del Conocimiento** del Grado en Ingeniería Informática de la **Universidad de Huelva (UHU)**. 

El objetivo principal de estas actividades es dominar los conceptos clave de la **programación lógica**, la unificación, la recursión de cola, el backtracking automático y la resolución de problemas mediante la satisfacción de restricciones en dominios finitos utilizando el compilador **SWI-Prolog**.

---

## 🗂️ Organización del Directorio

Los programas de Prolog se encuentran clasificados funcionalmente en 8 subdirectorios temáticos:

### 🔢 1. Aritmética y Recursión Básica
* **Directorio**: [01_Aritmetica_Recursion/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/01_Aritmetica_Recursion/)
* **Ejercicios**:
  * [factorial.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/01_Aritmetica_Recursion/factorial.pl): Cálculo del factorial de un número.
  * [natural.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/01_Aritmetica_Recursion/natural.pl): Definición inductiva de números naturales utilizando Peano.
  * [suma_peano.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/01_Aritmetica_Recursion/suma_peano.pl): Adición de números representados en Peano (ej. `s(s(0))`).
  * [collatz.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/01_Aritmetica_Recursion/collatz.pl): Evaluación de la conjetura de Collatz.
  * [numero_armstrong.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/01_Aritmetica_Recursion/numero_armstrong.pl): Validador de números de Armstrong.
  * [primos_rango.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/01_Aritmetica_Recursion/primos_rango.pl): Generador de números primos en un rango.
  * [raindrops.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/01_Aritmetica_Recursion/raindrops.pl): Juego de divisibilidad y factores primos (fizzbuzz).

---

### 📋 2. Manipulación de Listas
* **Directorio**: [02_Listas/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/)
* **Ejercicios**:
  * [pertenece.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/pertenece.pl): Determina la pertenencia de un elemento en una lista.
  * [my_append.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/my_append.pl): Implementación recursiva de la concatenación de listas.
  * [longitud.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/longitud.pl): Conteo recursivo de elementos en una lista.
  * [invertir.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/invertir.pl): Inversión del orden de los elementos.
  * [aniadir_final.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/aniadir_final.pl): Inserta un elemento al final de una lista.
  * [elemento_n.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/elemento_n.pl): Acceso a la posición N de una lista.
  * [comprimir_adyacentes.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/comprimir_adyacentes.pl) / [comprimir_adyacentes_alt.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/comprimir_adyacentes_alt.pl): Compactación de duplicados adyacentes de una lista.
  * [elemento_mas_frecuente.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/elemento_mas_frecuente.pl): Obtención del elemento que más veces se repite en una lista.
  * [permutar.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/permutar.pl): Obtiene todas las permutaciones lógicas de una lista.
  * [isograma.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/isograma.pl) / [isograma_alt.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/isograma_alt.pl): Validador de isogramas (palabras con letras no repetidas).
  * [binario_a_decimal.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/binario_a_decimal.pl): Convierte secuencias de bits binarios a decimal.
  * [run_length_encoding.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/run_length_encoding.pl): Codificación RLE.
  * [transcripcion_rna.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/transcripcion_rna.pl): Transcripción de ADN a ARN.
  * [recuento_nucleotidos.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/recuento_nucleotidos.pl): Estadísticas de nucleótidos de ADN.
  * [distancia_hamming.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/distancia_hamming.pl): Distancia de Hamming entre cadenas.
  * [aritmetica_compleja.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/aritmetica_compleja.pl): Complejos matemáticos definidos en Prolog.
  * [conjunto_personalizado.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/conjunto_personalizado.pl): Unión, intersección y diferencia.
  * [validador_isbn.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/validador_isbn.pl) / [validador_isbn_alt.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/validador_isbn_alt.pl): Validación de ISBN-10.
  * [validador_luhn.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/validador_luhn.pl): Fórmula de Luhn.
  * [suma_elementos.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/suma_elementos.pl): Adición agregada de los elementos de una lista.
  * [generador_listas.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/generador_listas.pl) / [generador_listas_secuencial.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/02_Listas/generador_listas_secuencial.pl): Generación automática de listas aleatorias o secuenciales ordenadas.

---

### 🔀 3. Algoritmos de Ordenación
* **Directorio**: [03_Ordenacion/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/03_Ordenacion/)
* **Ejercicios**:
  * [ordenacion_burbuja.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/03_Ordenacion/ordenacion_burbuja.pl): Algoritmo de ordenación de burbuja (Bubble Sort).
  * [ordenacion_insercion.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/03_Ordenacion/ordenacion_insercion.pl): Algoritmo de ordenación por inserción (Insertion Sort).
  * [ordenacion_quicksort.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/03_Ordenacion/ordenacion_quicksort.pl): Algoritmo Quicksort de ordenación rápida usando pivotes.

---

### 🌳 4. Árboles Binarios (AB) y de Búsqueda (ABB)
* **Directorio**: [04_Arboles_Binarios/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/04_Arboles_Binarios/)
* **Ejercicios**:
  * [creacion_ab.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/04_Arboles_Binarios/creacion_ab.pl): Creación estructural básica de un árbol binario.
  * [creacion_abb.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/04_Arboles_Binarios/creacion_abb.pl): Creación de un árbol binario de búsqueda (ABB).
  * [pertenece_ab.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/04_Arboles_Binarios/pertenece_ab.pl) / [pertenece_abb.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/04_Arboles_Binarios/pertenece_abb.pl): Comprueba pertenencia de valores en AB y ABB.
  * [cuenta_nodos_ab.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/04_Arboles_Binarios/cuenta_nodos_ab.pl): Recuento recursivo de nodos totales.
  * [balanceado_ab.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/04_Arboles_Binarios/balanceado_ab.pl) / [creacion_ab_balanceado.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/04_Arboles_Binarios/creacion_ab_balanceado.pl): Comprobación y creación de árboles equilibrados en altura.
  * [hojas_ab.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/04_Arboles_Binarios/hojas_ab.pl): Extrae todas las hojas terminales (nodos sin descendencia).
  * [recorridos_ab.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/04_Arboles_Binarios/recorridos_ab.pl): Travesía en preorden, inorden y postorden.
  * [reconstruccion_ab.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/04_Arboles_Binarios/reconstruccion_ab.pl): Reconstrucción estructural a partir de preorden e inorden.
  * [induccion_arboles.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/04_Arboles_Binarios/induccion_arboles.pl): Ejercicios matemáticos de demostración inductiva sobre árboles.

---

### 🌿 5. Árboles Genéricos (N-arios)
* **Directorio**: [05_Arboles_Genericos/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/05_Arboles_Genericos/)
* **Ejercicios**:
  * [creacion_ag.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/05_Arboles_Genericos/creacion_ag.pl) / [creacion_ag_balanceado.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/05_Arboles_Genericos/creacion_ag_balanceado.pl): Definición y equilibrio de árboles n-arios.
  * [creacion_ag_niveles.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/05_Arboles_Genericos/creacion_ag_niveles.pl) / [creacion_ag_niveles_alt.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/05_Arboles_Genericos/creacion_ag_niveles_alt.pl): Creadores ordenando nodos por alturas.
  * [cuenta_nodos_ag.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/05_Arboles_Genericos/cuenta_nodos_ag.pl): Conteo total de nodos contenidos en el árbol.
  * [altura_ag.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/05_Arboles_Genericos/altura_ag.pl): Calcula la profundidad máxima del árbol.
  * [hojas_ag.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/05_Arboles_Genericos/hojas_ag.pl): Listado de nodos terminales.
  * [recorrido_anchura_ag.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/05_Arboles_Genericos/recorrido_anchura_ag.pl): Recorrido por niveles en anchura (BFS).
  * [conversion_bin_a_gen.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/05_Arboles_Genericos/conversion_bin_a_gen.pl): Conversión bidireccional entre árboles binarios y genéricos.

---

### 🕸️ 6. Grafos
* **Directorio**: [06_Grafos/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/06_Grafos/)
* **Ejercicios**:
  * [camino_no_dirigido_aristas.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/06_Grafos/camino_no_dirigido_aristas.pl): Caminos en grafos no dirigidos controlando las aristas ya visitadas y devolviendo la lista de aristas.
  * [camino_no_dirigido_aristas_visitadas.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/06_Grafos/camino_no_dirigido_aristas_visitadas.pl): Caminos previniendo solapamiento de aristas pero listando los vértices visitados.
  * [camino_no_dirigido_vertices.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/06_Grafos/camino_no_dirigido_vertices.pl): Búsqueda básica de caminos con control de vértices visitados.
  * [ciclos_grafo.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/06_Grafos/ciclos_grafo.pl) / [ciclos_grafo_alt.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/06_Grafos/ciclos_grafo_alt.pl): Detección e identificación de ciclos/bucles cerrados.

---

### 🚪 7. Espacio de Estados
* **Directorio**: [07_Espacio_Estados/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/07_Espacio_Estados/)
* **Ejercicios**:
  * [problema_jarras.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/07_Espacio_Estados/problema_jarras.pl): Búsqueda de transiciones de estados óptimos para medir litros de agua usando dos recipientes.
  * [juego_de_la_vida.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/07_Espacio_Estados/juego_de_la_vida.pl) / [juego_de_la_vida_alt1.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/07_Espacio_Estados/juego_de_la_vida_alt1.pl) / [juego_de_la_vida_alt2.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/07_Espacio_Estados/juego_de_la_vida_alt2.pl): Modelados dinámicos del autómata celular del Juego de la Vida de Conway.
  * [domino.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/07_Espacio_Estados/domino.pl): Búsqueda de estados para acoplar y encadenar fichas de dominó.

---

### ⛓️ 8. Programación Lógica con Restricciones (CLP(FD))
* **Directorio**: [08_Restricciones_CLPFD/](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/08_Restricciones_CLPFD/)
* **Ejercicios**:
  * [sudoku.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/08_Restricciones_CLPFD/sudoku.pl): Resolutor estándar de 9x9 mediante restricciones finitas.
  * [sudoku_irregular.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/08_Restricciones_CLPFD/sudoku_irregular.pl): Sudokus de formas irregulares (no cuadradas).
  * [killer_sudoku.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/08_Restricciones_CLPFD/killer_sudoku.pl): Combina Sudoku y sumas restrictivas en regiones.
  * [hyper_sudoku.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/08_Restricciones_CLPFD/hyper_sudoku.pl): Sudoku con solapamiento de subregiones extra de 3x3.
  * [samurai_sudoku.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/08_Restricciones_CLPFD/samurai_sudoku.pl) / [samurai_sudoku_v1.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/08_Restricciones_CLPFD/samurai_sudoku_v1.pl): Resolutores para 5 Sudokus acoplados en aspa.
  * [organizacion_bodas.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/08_Restricciones_CLPFD/organizacion_bodas.pl) / [organizacion_bodas_v2.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/08_Restricciones_CLPFD/organizacion_bodas_v2.pl) / [organizacion_bodas_v3.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/08_Restricciones_CLPFD/organizacion_bodas_v3.pl): Planificación de distribución de mesas de invitados.
  * [acertijo_cebra.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/08_Restricciones_CLPFD/acertijo_cebra.pl): Solución declarativa al acertijo lógico de la Cebra.
  * [problema_zapatos.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/08_Restricciones_CLPFD/problema_zapatos.pl): Rompecabezas lógico de emparejamientos y colores.
  * [planificacion_tiempo.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/08_Restricciones_CLPFD/planificacion_tiempo.pl) / [planificacion_tiempo_alt.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/08_Restricciones_CLPFD/planificacion_tiempo_alt.pl): Asignación de franjas horarias libres de colisiones.
  * [horario_clases.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/08_Restricciones_CLPFD/horario_clases.pl): Asignación óptima de horarios de clase semanales.
  * [ataque_reinas.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/08_Restricciones_CLPFD/ataque_reinas.pl): Comprobación de amenazas en tableros para el problema de las N-Reinas.
  * [planificador_examenes.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/08_Restricciones_CLPFD/planificador_examenes.pl): Calendarización de exámenes sin solapamientos estudiantiles.
  * [simulador_horarios.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/08_Restricciones_CLPFD/simulador_horarios.pl) / [simulador_horarios_tabu.pl](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Representación-del-Conocimiento/08_Restricciones_CLPFD/simulador_horarios_tabu.pl): Simulación y generación avanzada de timetables escolares interactivos.

# Inteligencia Artificial (IA) - Programación con CLIPS

Este directorio contiene los ejercicios prácticos desarrollados para la asignatura **Inteligencia Artificial** del Grado en Ingeniería Informática de la **Universidad de Huelva (UHU)**. 

El objetivo principal es familiarizarse con el paradigma de la **programación declarativa basada en reglas** y el desarrollo de **sistemas expertos** empleando **CLIPS** (C Language Integrated Production System).

---

## 🛠️ Introducción a CLIPS
**CLIPS** es una herramienta y lenguaje diseñado para la creación de sistemas expertos. En lugar del flujo imperativo secuencial tradicional, CLIPS trabaja mediante:
1. **Base de Hechos (*Fact-list*)**: La representación del estado del problema mediante hechos concretos (ej. `(lista 3 2 1)` o `(Tos)`).
2. **Base de Reglas (*Knowledge-base*)**: Declaración de reglas condicionales del tipo `(defrule nombre (condiciones) => (acciones))`.
3. **Motor de Inferencia**: Empareja de forma automática los hechos actuales con los patrones declarados en las reglas (patrón *pattern-matching*), gestionando la agenda y disparando las reglas correspondientes.

---

## 📂 Descripción de los Ejercicios

A continuación se detallan los ejercicios prácticos organizados y renombrados secuencialmente:

### 🔹 Bloque 1: Manipulación de Listas y Vectores
* **[Ejercicio 01: Elementos Repetidos](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Inteligencia%20Artificial/Ejercicio_01_ElementosRepetidos.clp)**: Detecta e identifica qué elementos numéricos se encuentran duplicados dentro de una lista de entrada dada, almacenándolos en una lista resultado.
* **[Ejercicio 01 (V2): Elementos Repetidos Optimizado](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Inteligencia%20Artificial/Ejercicio_01_ElementosRepetidos_V2.clp)**: Versión avanzada del buscador de repetidos que añade control de unicidad al listado resultado, garantizando que un mismo elemento repetido tres o más veces aparezca sólo una vez en la salida.
* **[Ejercicio 02: Suma de una Lista](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Inteligencia%20Artificial/Ejercicio_02_SumaLista.clp)**: Realiza la suma de todos los elementos contenidos en una lista numérica. Consume la lista hecho a hecho sumándolo a un acumulador.
* **[Ejercicio 05: Diferencia Máximo-Mínimo](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Inteligencia%20Artificial/Ejercicio_05_DiferenciaMaxMin.clp)**: Calcula la diferencia absoluta entre el valor máximo y el mínimo de una lista. Para ello, ordena ascendentemente los números y resta el primero (mínimo) al último (máximo).
* **[Ejercicio 06: Ordenación de Burbuja](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Inteligencia%20Artificial/Ejercicio_06_OrdenacionBurbuja.clp)**: Implementa el algoritmo de ordenación por burbuja (*bubble sort*) ascendente directamente mediante el motor de emparejamiento de patrones de CLIPS, intercambiando de posición los valores adyacentes desordenados.
* **[Ejercicio 07: Detector de Palíndromos](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Inteligencia%20Artificial/Ejercicio_07_Palindromo.clp)**: Evalúa si un vector numérico es palíndromo (se lee igual en ambas direcciones) recortando de forma recursiva parejas coincidentes de los extremos e imprimiendo el veredicto final.

---

### 🔹 Bloque 2: Teoría de Conjuntos
* **[Ejercicio 03: Intersección de Conjuntos](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Inteligencia%20Artificial/Ejercicio_03_InterseccionConjuntos.clp)**: Determina la intersección entre dos conjuntos numéricos ($A \cap B$) comparando sus miembros, extrayendo los comunes y volcándolos en un hecho de intersección.
* **[Ejercicio 04: Resta de Conjuntos](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Inteligencia%20Artificial/Ejercicio_04_RestaConjuntos.clp)**: Implementa la resta de conjuntos ($A \setminus B$), filtrando y devolviendo aquellos elementos pertenecientes a $A$ que no están presentes en $B$.

---

### 🔹 Bloque 3: Estructuras y Geometría
* **[Ejercicio 08: Suma de Áreas de Rectángulos](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Inteligencia%20Artificial/Ejercicio_08_AreaRectangulos.clp)**: Declara una plantilla `rectangulo` con sus respectivos slots y calcula recursivamente la suma agregada del área de todas las figuras geométricas registradas en la base de hechos.
* **[Ejercicio 09: Triángulos Rectángulos](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Inteligencia%20Artificial/Ejercicio_09_TriangulosRectangulos.clp)**: Modela triángulos y aplica el teorema de Pitágoras ($z = \sqrt{x^2 + y^2}$) para discriminar cuáles de ellos son triángulos rectángulos, agrupando sus nombres en una lista final.

---

### 🔹 Bloque 4: Sistemas Expertos
* **[Ejercicio 10: Sistema Experto de Diagnóstico Médico](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Inteligencia%20Artificial/Ejercicio_10_SistemaExpertoMedico.clp)**: Sistema experto completo estructurado en dos fases deductivas:
  1. **Fase de Diagnóstico**: Recibe síntomas iniciales (tos, cansancio, fiebre, escalofríos, diarrea, ictericia, etc.) y deduce posibles patologías (Gripe, Rubéola, Malaria, Hepatitis, Tuberculosis, Anemia), prescribiendo medicamentos.
  2. **Fase de Derivación**: Basándose en la prescripción acumulada, el sistema deduce y recomienda a qué especialista médico debe acudir el paciente (otorrino, endocrino, nutricionista o médico general).

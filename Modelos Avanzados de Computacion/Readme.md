# Modelos Avanzados de Computación (MAC)

Este directorio contiene las prácticas de programación funcional desarrolladas para la asignatura **Modelos Avanzados de Computación** del Grado en Ingeniería Informática de la **Universidad de Huelva (UHU)**. 

Todos los ejercicios están implementados utilizando el lenguaje de programación funcional **Haskell**. Para ejecutarlos, es necesario disponer del entorno interactivo **GHCi** (Glasgow Haskell Compiler interactive).

---

## 📂 Contenido del Directorio

### 🔹 Práctica 1: Listas, Recursión y Operaciones Básicas
Ejercicios introductorios enfocados en la manipulación recursiva de listas y propiedades numéricas básicas:
1. [P1_1_cambia_el_primero.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P1_1_cambia_el_primero.hs): Cambia el primer valor de una lista.
2. [P1_2_cambia_el_n.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P1_2_cambia_el_n.hs): Modifica el elemento en la posición $n$ de una lista.
3. [P1_3_get_mayor_abs.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P1_3_get_mayor_abs.hs): Encuentra el número con mayor valor absoluto en una lista.
4. [P1_4_num_veces.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P1_4_num_veces.hs): Cuenta las ocurrencias de un elemento en una lista.
5. [P1_5_palabras_mayores_n.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P1_5_palabras_mayores_n.hs): Filtra aquellas palabras con longitud estrictamente mayor que $n$.
6. [P1_6_es_palindroma.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P1_6_es_palindroma.hs): Determina si un string es palíndromo.
7. [P1_7_esprimo.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P1_7_esprimo.hs): Comprueba si un entero es número primo.
8. [P1_8_sumparesresimp.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P1_8_sumparesresimp.hs): Suma los números pares y resta los impares de una lista.
9. [P1_9_muestra_foldr_suma_n.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P1_9_muestra_foldr_suma_n.hs): Traza paso a paso la reducción con `foldr (+)`.
10. [P1_10_palindromas.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P1_10_palindromas.hs): Verifica si todos los elementos de una lista son strings palíndromos.
11. [P1_11_sumaSiPalindromo.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P1_11_sumaSiPalindromo.hs): Cuenta la cantidad de palíndromos presentes en una lista de palabras.
12. [P1_12_getMenorListas.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P1_12_getMenorListas.hs): Devuelve el número menor de una lista de listas de enteros.
13. [P1_13_SumPrimoRestPali.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P1_13_SumPrimoRestPali.hs): Suma 1 por cada número primo y resta 1 por cada lista palíndroma procesada.
14. [P1_14_sumparmultimp.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P1_14_sumparmultimp.hs): Suma los elementos pares y los multiplica por el mínimo común múltiplo (mcm) de los impares.
15. [P1_15_sumKde0aN.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P1_15_sumKde0aN.hs): Suma una constante $k$ a los primeros $n$ números de una lista.

---

### 🔹 Práctica 2: Combinatoria, Álgebra y Estructuras Condicionales
Implementación de lógica matemática mediante diversas guardas, condicionales (`if-then-else`), construcciones `case` y cláusulas `where`:
1. [P2_1_nsobrek.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P2_1_nsobrek.hs): Cálculo combinatorio del coeficiente binomial $\binom{n}{k}$ bajo diferentes esquemas de control sintácticos.
2. [P2_2_raices.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P2_2_raices.hs): Obtención de las raíces reales de ecuaciones de segundo grado $ax^2 + bx + c = 0$.
3. [P2_3_interseccion.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P2_3_interseccion.hs): Determina y devuelve la intersección geométrica entre dos intervalos cerrados $[a, b]$ y $[c, d]$.
4. [P2_4_pertenece.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P2_4_pertenece.hs): Comprobación de pertenencia de elementos a listas simples y de tuplas utilizando distintas estructuras recursivas.
5. [P2_5_encontrarNPrimo.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P2_5_encontrarNPrimo.hs): Algoritmo de cribado para encontrar el n-ésimo número primo.
6. [P2_6_sumaNPrimerosPrimos.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P2_6_sumaNPrimerosPrimos.hs): Suma total de todos los números primos existentes menores que un entero dado $n$.

---

### 🔹 Práctica 3: Listas Intensionales
* **Archivo de Referencia**: [P3_listasIntensionales.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P3_listasIntensionales.hs)

Ejercicios resueltos empleando la potencia expresiva de las **listas por comprensión** (listas intensionales) en Haskell para la generación, filtrado y transformación compacta de datos numéricos y estructurados.

---

### 🔹 Práctica 4: Registros de Datos y Estructuras de Árbol Binario
Módulo principal y librerías que modelan una base de datos de empleados de una empresa mediante registros condicionales y estructuras arbóreas:
* **Módulo de Lógica**: [P4_MEmpleados.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P4_MEmpleados.hs)
* **Programa Ejecutable**: [P4_Main.hs](file:///c:/Users/kjba2/Documents/Algoritmos-y-Computacion/Modelos%20Avanzados%20de%20Computacion/P4_Main.hs)
  
Funcionalidades implementadas:
1. **Gestión de Empleados**: Listado, filtrado por fechas de nacimiento/alta y búsquedas selectivas de la plantilla de *JobVen SL*.
2. **Árbol Binario de Empleados**: Organización de registros de empleados estructurados en un árbol ordenado por fecha de nacimiento. Soporta búsquedas rápidas por DNI y recorridos sistemáticos en profundidad (inorden, preorden, postorden) y en anchura (niveles).

📌 **Compilación del ejecutable**:
```bash
ghc P4_Main.hs -o P4_Main.exe
```

---

### 🚀 Proyecto Final
Integración de los conceptos de la asignatura en un software robusto. El código base y detalles del proyecto colaborativo final pueden consultarse en el repositorio externo del equipo:

👉 [MAC_PracticaFinal_KJBA_JDCZ](https://github.com/jcalvente083/MAC_PracticaFinal_KJBA_JDCZ)

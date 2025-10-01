# 📘 Ejercicios de Haskell

Repositorio con prácticas de **Modelos Avanzados de Computación**.  
Incluye ejercicios de listas, recursión, funciones de orden superior, combinatoria, álgebra y teoría de números.  

---

## 📂 Contenido

### 🔹 Práctica 1
Ejercicios básicos de listas, palíndromos y propiedades de números.

1. `P1_1_cambia_el_primero.hs` → Cambia el primer valor de una lista.  
2. `P1_2_cambia_el_n.hs` → Cambia el valor en la posición `n` de una lista.  
3. `P1_3_get_mayor_abs.hs` → Devuelve el número con mayor valor absoluto de una lista.  
4. `P1_4_num_veces.hs` → Cuenta cuántas veces aparece un valor en una lista.  
5. `P1_5_palabras_mayores_n.hs` → Filtra palabras cuya longitud es mayor que `n`.  
6. `P1_6_es_palindroma.hs` → Comprueba si una palabra es palíndroma.  
7. `P1_7_esprimo.hs` → Determina si un número es primo.  
8. `P1_8_sumparesresimp.hs` → Suma pares y resta impares en una lista.  
9. `P1_9_muestra_foldr_suma_n.hs` → Muestra los pasos de `foldr (+)`.  
10. `P1_10_palindromas.hs` → Comprueba si todas las palabras de una lista son palíndromas.  
11. `P1_11_sumaSiPalindromo.hs` → Cuenta cuántos palíndromos hay en una lista de strings.  
12. `P1_12_getMenorListas.hs` → Devuelve el menor número de una lista de listas.  
13. `P1_13_SumPrimoRestPali.hs` → Suma 1 por cada primo y resta 1 por cada lista palíndroma.  
14. `P1_14_sumparmultimp.hs` → Suma pares y multiplica por el mcm de los impares.  
15. `P1_15_sumKde0aN.hs` → Suma `k` a los `n` primeros elementos de una lista.  

---

### 🔹 Práctica 2
Ejercicios de combinatoria, ecuaciones cuadráticas, intervalos y primos.  

1. `P2_1_nsobrek.hs` → Calcula el número de combinaciones **C(n, k)** con distintas implementaciones:  
   - Ecuaciones  
   - Guardas  
   - If-then-else  
   - Where  

2. `P2_2_raices.hs` → Calcula las **raíces reales** de una ecuación cuadrática con varias variantes (`raices`, `raicesGuardas`, `raicesIf`, `raicesCase`, `raicesWhere`).  

3. `P2_3_interseccion.hs` → Devuelve la **intersección entre dos intervalos** `[a, b]` y `[c, d]` si existe.  

4. `P2_4_pertenece.hs` → Comprueba la **pertenencia de un elemento en una lista** (y también de una tupla en una lista de tuplas), implementado con:  
   - Recursión simple  
   - Guardas  
   - If-then-else  
   - Case  

5. `P2_5_encontrarNPrimo.hs` → Encuentra el **n-ésimo número primo** (ejemplo: el primo nº 10.001).  

6. `P2_6_sumaNPrimerosPrimos.hs` → Calcula la **suma de todos los primos menores que n**.  

---

### 🔹 Práctica 3
Ejercicios de **listas intensionales** en Haskell.

1. `ej1` → Lista con los números del 1 al 10 sumando 10 a cada uno.  
2. `ej2` → Lista de listas unitarias con los números pares del 1 al 10.  
3. `ej3` → Lista de listas con la expresión `11 - x` para `x` en `[1..10]`.  
4. `ej4` → Lista booleana indicando si cada número de 1 a 10 es impar.  
5. `ej5` → Pares `(x*3, x<10)` para `x` en `[1..6]`.  
6. `ej6` → Pares `(x*5, even x && x < 4)` con condición `x<=3 || x==8`.  
7. `ej7` → Pares `(x+10, x+11)` para los números impares de 1 a 10.  
8. `ej8` → Listas `[5..x+4]` para impares entre 3 y 9.  
9. `ej9` → Secuencia aritmética descendente generada con `(5-x)*5+1`.  
10. `ej10` → Para cada `x` par, genera `[4..x+2]`, la invierte y filtra pares.  

---

### 🔹 Práctica 4
Ejercicios con **registros, fechas y árboles binarios**.

Para comprobar esta práctica se debe compilar `P4_Main.hs`.
Incluye el módulo auxiliar `MEmpleados.hs` con las funciones necesarias. 

1. `Ejercicio 2` → Gestión de empleados de la empresa **JobVen SL**.  
   - Listar empleados con todos sus datos (DNI, nombre, apellidos, fecha de nacimiento, cargo, fecha de alta).  
   - Filtrar por **fecha de nacimiento** y/o **fecha de alta**.  
   - Buscar empleados por **cargo**.  
   - Volver al menú principal.   

2. `Ejercicio 3` → Implementación de un **árbol binario de empleados** ordenado por fecha de nacimiento.  
   - Recorrido en **profundidad**.  
   - Recorrido en **anchura**.  
   - Búsqueda de empleados por **DNI**.  
   - Volver al menú principal.  

📌 **Nota de compilación**:  
Para compilar el programa principal de esta práctica, ejecutar:  

```bash
ghc P4_Main.hs -o P4_Main.exe
```

---

## 📜 Notas
- Cada ejercicio se encuentra en un archivo independiente (`.hs`).  
- Algunos ejercicios incluyen múltiples implementaciones (ecuaciones, guardas, if-then-else, case, where).

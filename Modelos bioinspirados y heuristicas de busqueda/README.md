# 🧠 Modelos Bioinspirados y Heurística de Búsqueda  

Este repositorio contiene las prácticas de la asignatura **Modelos Bioinspirados y Heurística de Búsqueda**, enfocadas en la resolución del **Problema de Asignación Cuadrática (QAP)**.  
El objetivo principal es implementar, analizar y comparar diferentes algoritmos heurísticos y metaheurísticos, evaluando su desempeño frente a soluciones óptimas conocidas en datasets estándar.  

---

## 📘 Introducción al problema QAP  

El **Quadratic Assignment Problem (QAP)** es un problema clásico de optimización combinatoria: dado un conjunto de **n instalaciones** y **n localizaciones**, se debe asignar cada instalación a una localización de manera que se minimice el **costo total**, calculado a partir de:  
- la **distancia** entre localizaciones,  
- y el **flujo** de interacción entre instalaciones.  

Es un problema **NP-difícil**, por lo que resulta ideal para evaluar algoritmos heurísticos y bioinspirados.  

Los datasets empleados en todas las prácticas son:  

- **tai25b**  
- **sko90**  
- **tai150b**  

Cada algoritmo se compara con el **costo óptimo** de referencia publicado para estos problemas.  

---

## 📂 Contenido de las prácticas  

### 🔹 Práctica 1: Heurísticas clásicas y metaheurísticas iniciales  
Se comparan los siguientes algoritmos:  

- **Greedy**: asigna instalaciones en base a una heurística constructiva rápida.  
- **Aleatoria**: genera soluciones iniciales aleatorias para tener una referencia base.  
- **Búsqueda Local (Mejor Vecino)**: explora todo el vecindario y escoge el mejor movimiento.  
- **Búsqueda Local (Primer Mejor)**: acepta la primera mejora encontrada en el vecindario.  
- **Enfriamiento Simulado (SA)**: inspirado en procesos de enfriamiento físico, acepta soluciones peores con cierta probabilidad decreciente.  
- **Búsqueda Tabú (TS)**: evita caer en ciclos manteniendo una memoria de movimientos recientes prohibidos ("tabú").  

---

### 🔹 Práctica 2: Metaheurísticas avanzadas de construcción y exploración  
Se estudian variantes más sofisticadas:  

- **Greedy**.  
- **Búsqueda Local (Primer Mejor)**.  
- **GRASP (Greedy Randomized Adaptive Search Procedure)**: versión pura del algoritmo constructivo aleatorizado basado en greedy.  
- **GRASP + Búsqueda Local**: variante que aplica una búsqueda local al final de cada construcción para intensificar la exploración y mejorar las soluciones obtenidas.  

- **ILS (Iterated Local Search)**: aplica búsqueda local repetidamente desde soluciones perturbadas para escapar de óptimos locales.  
- **VNS (Variable Neighborhood Search)**: explora de forma sistemática distintos vecindarios para diversificar la búsqueda.  

---

### 🔹 Práctica 3: Algoritmos bioinspirados y poblacionales  
En esta última práctica se introducen algoritmos de carácter evolutivo y multimodal:  

- **Greedy** (como referencia base).  
- **Algoritmo Genético Básico**: implementación simple de comparación con heurística inicial.  
- **Algoritmo Genético CHC (Cross-generational elitist selection, Heterogeneous recombination, Cataclysmic mutation)**: un algoritmo genético avanzado con fuerte elitismo y diversidad.  
- **Algoritmo Genético Multimodal**: técnicas que permiten mantener varias soluciones competitivas simultáneamente para explorar mejor el espacio de búsqueda.  

---

## 📊 Evaluación  
En cada práctica:  
- Los algoritmos se ejecutan sobre los datasets **tai25b, sko90 y tai150b**.  
- Se comparan los costos obtenidos con los **valores óptimos conocidos**.  
 

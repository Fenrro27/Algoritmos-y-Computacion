# Kevin Jesús Banda Azogil

import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
import os
from numba import njit, prange
from sklearn.linear_model import LinearRegression



def leer_archivo_y_extraer_matrices(nombre_archivo):
    with open(nombre_archivo, "r") as f:
        # Leer la primera línea para obtener `n`
        n = int(f.readline().strip())  

        # Leer el resto del archivo con np.loadtxt
        datos = np.loadtxt(f, dtype=int)

    # Dividir los datos en dos matrices
    distancia = datos[:n, :n]  # Primera parte de la matriz
    flujo = datos[n:, :n]      # Segunda parte de la matriz

    return distancia, flujo, n


# Mostrar matriz (sin cambios)
def mostrar_matriz(nombre, matriz):
    print(f"Matriz {nombre}:")
    print("\n".join(" ".join(map(str, fila)) for fila in matriz))
    print()

# Gráfica de evolución del costo
def graficar_evolucion(historial_costos, titulo_extra="", linea_referencia=None):
    plt.plot(historial_costos, label="Mejor costo encontrado", color="blue")
    
    if linea_referencia is not None:
        plt.axhline(y=linea_referencia, color='red', linestyle='--', label=f"Costo Mejor Solución ({linea_referencia})")
    
    plt.xlabel("Iteraciones")
    plt.ylabel("Costo")
    plt.title(f"Evolución del costo - {titulo_extra}")
    plt.legend()
    plt.grid()
    plt.show(block=False)  # Mostrar sin detener ejecución

# Cálculo del costo usando NumPy para evitar bucles anidados
@njit
def calcular_costo(solucion, distancia, flujo, n):
    costo = 0
    for i in prange(n):
        si = solucion[i]
        for j in range(n):
            sj = solucion[j]
            costo += distancia[i][j] * flujo[si][sj]
    return costo

@njit
def calcular_costo_swap(solucion, distancia, flujo, n, i, j, costo_actual):
    si = solucion[i]
    sj = solucion[j]
    delta = 0

    for k in range(n):
        if k != i and k != j:
            sk = solucion[k]

            delta += (
                (distancia[i][k] - distancia[j][k]) * (flujo[sj][sk] - flujo[si][sk]) +
                (distancia[k][i] - distancia[k][j]) * (flujo[sk][sj] - flujo[sk][si])
            )
    
    # Diagonales y cruzados
    delta += (distancia[i][i] - distancia[j][j]) * (flujo[sj][sj] - flujo[si][si])
    delta += (distancia[i][j] - distancia[j][i]) * (flujo[sj][si] - flujo[si][sj])

    return costo_actual + delta

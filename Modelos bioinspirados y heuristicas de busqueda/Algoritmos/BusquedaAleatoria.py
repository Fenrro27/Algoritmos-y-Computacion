# Kevin Jesús Banda Azogil

import numpy as np
import FicherosComun as fc

def busqueda_aleatoria(distancia, flujo, n, semilla):
    np.random.seed(semilla)  

    ev=0
    
    iteracionesPorN = 1000
    iteraciones = iteracionesPorN * n

    mejor_solucion = None
    mejor_costo = float("inf")
    historial_costos = []  
    
    for i in range(iteraciones):
        # Generamos una solucion inicial (GSI)
        solucion = np.random.permutation(n)  # Permutacion aleatoria
        costo = fc.calcular_costo(solucion, distancia, flujo, n)  # Funcion objetivo (FO)
        ev=ev+1
        
        if costo < mejor_costo:
            mejor_costo = costo
            mejor_solucion = solucion.copy()  # Copia para evitar referencias
        
        historial_costos.append(mejor_costo)  # Guardamos el mejor costo de la iteracion
    
    return mejor_solucion, mejor_costo, historial_costos, ev

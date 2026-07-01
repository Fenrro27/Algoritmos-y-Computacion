# Kevin Jesus Banda Azogil

import numpy as np
import FicherosComun as fc
import AlgoritmoComparacionGreedy as greedy

def busqueda_tabu_qap(distancia, flujo, n, semilla):
    np.random.seed(semilla)
    ev=0
    iteraciones_max = 40 * n # El numero maximo de iteraciones en total
    # reinicios = 4 # reinicializaciones
    # iteraciones_por_reinicio = iteraciones_max // reinicios
    iteraciones_por_reinicio = 8*n
    lista_tabu_tam = 2
    
    # Generamos solucion inicial
    mejor_solucion = np.random.permutation(n)
    mejor_costo = fc.calcular_costo(mejor_solucion, distancia, flujo, n)
    ev=ev+1
    solucion_actual = mejor_solucion.copy()
    costo_actual = mejor_costo
    # Inicializamos la lista tabu y el historial de costo
    lista_tabu = []
    historial_costos = []
    
    for iteracion in range(iteraciones_max):
        #Generamos los veccinos 
        vecinos = []
        for _ in range(40): # Examinamos 40 vecinos
            i, j = np.random.choice(n, 2, replace=False)
            vecino = solucion_actual.copy()
            vecino[i], vecino[j] = vecino[j], vecino[i]
            costo_vecino = fc.calcular_costo_swap(solucion_actual, distancia, flujo, n, i,j, costo_actual)
            ev=ev+1
            vecinos.append((vecino, costo_vecino, (i, j))) # Lista de vecinos, con el vacino, el costo y el movimiento
        
        # ordena la lista vecinos en funcion del costo de cada vecino.
        vecinos.sort(key=lambda x: x[1])
        
        #Generamos la lista de movimientos tabu
        for vecino, costo_vecino, movimiento in vecinos:
            if movimiento not in lista_tabu or costo_vecino < mejor_costo:
                solucion_actual = vecino
                costo_actual = costo_vecino
                lista_tabu.append(movimiento)
                # No insertamos si la lista esta llena
                if len(lista_tabu) > lista_tabu_tam:
                    lista_tabu.pop(0)
                break
        
        # Actualizamos costo y solucion si mejoramos
        if costo_actual < mejor_costo:
            mejor_solucion = solucion_actual.copy()
            mejor_costo = costo_actual
        historial_costos.append(mejor_costo)
        
        # Estrategia de reinicializacion
        if (iteracion + 1) % iteraciones_por_reinicio == 0:
            prob = np.random.rand()
            if prob < 0.25: # solucion inicial aleatoria
                solucion_actual = np.random.permutation(n)
            elif prob < 0.75: # Nueva solucion Greedy
                solucion_actual, _, evAux = greedy.greedy_qap(distancia, flujo, n)
                ev=ev+evAux
            else: # Mejor solucion obtenida
                solucion_actual = mejor_solucion.copy()
            
            costo_actual = fc.calcular_costo(solucion_actual, distancia, flujo, n)
            ev=ev+1

            # Calculamos un nuevo tamaño posible de la lista tabu
            lista_tabu_tam = int(lista_tabu_tam * (1.5 if np.random.rand() < 0.5 else 0.5)) # variar el tamaño de la lista tabú, incrementándola o reduciéndola en un 50%
            # Si es menos de 2 nos quedamos con una lista de tamaño 2
            lista_tabu_tam = max(2, lista_tabu_tam)
            lista_tabu = []
    
    return mejor_solucion, mejor_costo, historial_costos, ev

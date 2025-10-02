# Kevin Jesús Banda Azogil

import numpy as np
import random
from numba import njit
from FicherosComun import calcular_costo

def algoritmo_CHC(distancia, flujo, n, semilla, generaciones=None):
    np.random.seed(semilla)
    random.seed(semilla)
    
    tam_poblacion = 50  # Tamanio fijo de la poblacion

    if generaciones is None: 
        generaciones = 5000  # Numero de generaciones por defecto
    
    poblacion = [np.random.permutation(n) for _ in range(tam_poblacion)]  # Poblacion inicial de permutaciones aleatorias
    costos = np.array([calcular_costo(ind, distancia, flujo, n) for ind in poblacion])  # Evaluacion inicial de cada individuo
    
    evaluaciones = tam_poblacion
    historial_costos = [costos.min()]  # Guarda el mejor costo por generacion
    mejor_solucion = poblacion[np.argmin(costos)].copy()
    mejor_costo = costos.min()

    # Umbral de Hamming: solo se cruzan padres con diferencia significativa
    umbral = n // 4 

    # Historiales para analisis
    mejores_fit = []
    promedios_fit = []
    peores_fit = []
    diversidad = []
    reinicios = 0  # Contador de reinicios de poblacion

    # Bucle principal de generaciones
    for _ in range(generaciones):
        hijos = []

        # Generar hijos cruzando parejas si cumplen el umbral de Hamming
        for i in range(0, len(poblacion), 2):
            p1, p2 = poblacion[i], poblacion[(i + 1) % len(poblacion)] # evitamos indices fuera de rango usando el modulo

            # Si la distancia de Hamming entre padres > umbral, se cruzan
            h1, h2 = generar_descendencia_CHC(p1, p2, umbral)

            if h1 is not None: # Si no son nulos almacenamos los hijos
                hijos.append(h1)
                hijos.append(h2)


        mejora = False # Bandera para saber si hubo mejora en esta generación


        # Evaluar hijos y reemplazar si son mejores que el peor individuo
        for hijo in hijos:
            # Se calcula el costo (fitness) del hijo usando la función objetivo
            costo_hijo = calcular_costo(hijo, distancia, flujo, n)
            evaluaciones += 1

            # Se identifica el índice del peor individuo actual (el de mayor costo)
            peor_idx = np.argmax(costos)

            # Si el hijo es mejor (tiene menor costo) que el peor individuo
            if costo_hijo < costos[peor_idx]:
                poblacion[peor_idx] = hijo # Se reemplaza al peor individuo con el nuevo hijo
                costos[peor_idx] = costo_hijo # Se actualiza el valor de su costo en el vector de costos
                mejora = True # Se marca que hubo una mejora en esta generación

                # Si este hijo es mejor que el mejor encontrado hasta ahora
                if costo_hijo < mejor_costo: # Se actualiza la mejor solución global y su costo
                    mejor_costo = costo_hijo
                    mejor_solucion = hijo.copy()

        # Guardar mejor costo de la generación
        historial_costos.append(mejor_costo)

        # Guardar estadisticas de fitness y diversidad
        mejores_fit.append(costos.min())
        promedios_fit.append(costos.mean())
        peores_fit.append(costos.max())
        diversidad.append(calcular_diversidad(poblacion))

        # Si no hubo mejora, se reduce el umbral
        if not mejora:
            umbral -= 1
            if umbral < 0: # si el umbral llega a cero reiniciar poblacion
                umbral = n // 4  # Se reinicia el umbral
                reinicios += 1
                # Reiniciar población usando el mejor individuo como plantilla
                poblacion = reiniciar_poblacion(mejor_solucion, n, tam_poblacion)
                # Recalcular costos de la nueva población
                costos = np.array([calcular_costo(ind, distancia, flujo, n) for ind in poblacion])

    # Se devuelve la mejor solucion, su costo y los historiales
    return mejor_solucion, mejor_costo, historial_costos, evaluaciones, mejores_fit, promedios_fit, peores_fit, diversidad, reinicios



@njit
def distancia_hamming(p1, p2):
    # Calcula la distancia de Hamming entre dos permutaciones
    dist = 0
    for i in range(len(p1)):
        if p1[i] != p2[i]:
            dist += 1
    return dist


def generar_descendencia_CHC(p1, p2, umbral):
    # Si los padres son lo suficientemente diferentes, se cruzan
    if distancia_hamming(p1, p2) > umbral:
        return cruce_OX(p1, p2), cruce_OX(p2, p1)
    return None, None  # No se genera descendencia si no cumplen el umbral, es decir, se parecen demasiado


def reiniciar_poblacion(mejor, n, tam_poblacion):
    # Reinicia la poblacion partiendo del mejor individuo
    nueva_poblacion = [mejor.copy()]
    while len(nueva_poblacion) < tam_poblacion:
        perm = np.random.permutation(n) # Genera una permutacion aleatoria de los nmueros del 0 al n-1
        if not np.array_equal(perm, mejor):
            nueva_poblacion.append(perm)
    return nueva_poblacion


def calcular_diversidad(poblacion):
    # Calcula la diversidad promedio normalizada en la poblacion
    n = len(poblacion[0])
    total = 0 # Suma acumulada de distancias de Hamming entre todos los pares de individuos
    count = 0 # Número total de pares comparados

    for i in range(len(poblacion)):
        for j in range(i + 1, len(poblacion)):
            total += distancia_hamming(poblacion[i], poblacion[j])
            count += 1

    # Se divide por el total de comparaciones y por la longitud del cromosoma para normalizar
    return total / (count * n)  # Valor normalizado entre 0 y 1




def cruce_OX(padre1, padre2):
    # CRUCE OX (Order Crossover) - ESTILO DE LOS APUNTES
    n = len(padre1)
    ini, fin = sorted(random.sample(range(n), 2)) # Se elige un segmento aleatorio
    hijo = [-1] * n  # Inicializa el hijo con valores vacios

    segmento = set()
    # Se copia un segmento del primer padre respetando la poblacion
    for i in range(ini, fin):
        hijo[i] = padre1[i]
        segmento.add(padre1[i]) # Se guarda el contenido para evitar duplicados


    # Se completa el hijo con los elementos del segundo padre en orden (Sin repetir)
    idx_padre2 = 0
    for i in range(n):
        if hijo[i] == -1:
            while padre2[idx_padre2] in segmento: # Avanzamos mientras q el elemento del padre2 sea el q ha dado el padre1
                idx_padre2 += 1
            hijo[i] = padre2[idx_padre2]
            idx_padre2 += 1

    return np.array(hijo)
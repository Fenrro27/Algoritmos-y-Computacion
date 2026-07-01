# Kevin Jesús Banda Azogil

import numpy as np
import FicherosComun as fc

def greedy_qap(distancia, flujo, n):
    ev=0
    potencialFlujo = []
    potencialDistancia = []

    #Calculo del potencial de distancia
    for j in range(n):
        potencialDistancia.append([sum(distancia[j]),j])
    potencialDistancia.sort(reverse=False, key=lambda x: x[0]) #De menor a mayor

    #Calculo del potencial de flujo
    for i in range(n):
        potencialFlujo.append([sum(flujo[i]),i])
    potencialFlujo.sort(reverse=True, key=lambda x: x[0]) #De mayor a menor
   
    asignaciones = [] # [unidad, localizacion]

    for i in range(n):
        asignaciones.append([potencialFlujo[i][1],potencialDistancia[i][1]])

    asignaciones.sort(reverse=False, key=lambda x: x[0])
    resultado = [subarray[1] for subarray in asignaciones]

    return resultado, fc.calcular_costo(resultado, distancia, flujo, n), (ev+1)


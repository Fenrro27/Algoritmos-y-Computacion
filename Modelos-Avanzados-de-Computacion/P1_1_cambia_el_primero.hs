-- cambia_el_primero(a,b): cambia el primer valor de la lista a por el valor de a

cambiaElPrimero::(a,[a]) ->[a] -- Cabecera
cambiaElPrimero (x,[]) = [] -- Caso base
cambiaElPrimero(x,xs) = x:tail xs --Caso general


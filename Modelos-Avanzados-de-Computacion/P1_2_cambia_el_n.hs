-- cambia_el_n(a,n,b): cambia el valor de la posicion n de la lista a por el valor de a



cambiaElN::(a,Int,[a]) ->[a] -- Cabecera
cambiaElN (x,_,[]) = [] -- Caso base
cambiaElN(x,0,xs) = x:tail xs --Caso en el que es el primer elemento a cambiar
cambiaElN(x, n, cab:resto ) = cab: cambiaElN(x, n-1, resto) -- caso general

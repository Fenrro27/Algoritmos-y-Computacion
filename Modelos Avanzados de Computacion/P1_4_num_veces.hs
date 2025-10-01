-- num_veces(a,b): devuelve la cantidad de veces que aparece el valor a en la lista b

numVeces::(Eq a)=>a->[a]-> Int -- Cabecera
numVeces a list = length(filter(== a) list)


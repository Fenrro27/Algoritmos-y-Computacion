-- get_mayor_abs(a): devuelve el mayor numero en valor absoluto de la lista a


getMayorAbs::(Num a, Ord a)=>[a]-> a -- Cabecera
getMayorAbs[] = 0 -- Caso lista vacia
getMayorAbs [x] = abs x-- Caso un elemento en la lista 
getMayorAbs (cab:resto) = max (abs cab) (getMayorAbs resto)



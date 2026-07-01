--suma los pares elevados al minimo comun multiplo de los imparers


mcmLista :: [Int] -> Int
mcmLista xs = foldl1 lcm (filter (> 0) xs) 


sumparmultimp :: [Int] -> Int
sumparmultimp lista = sum (filter even lista) * mcmLista (filter odd lista)
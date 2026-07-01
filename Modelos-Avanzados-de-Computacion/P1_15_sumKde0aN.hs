-- suma a k los primero n numeros de una lista y devuelve el resultado con el resto de la lista

sumaKPrimerosN :: Int -> Int -> [Int] -> [Int]
sumaKPrimerosN k 0 lista = k:lista
sumaKPrimerosN k n (cab:resto) = sumaKPrimerosN (cab+k) (n-1) resto

-- esprimo(x): Devuelve si el número introducido es primo o no 

-- Funcion que devuelve los divisores de x
divisores:: Integral a => a->[a]
divisores x = filter (\d -> x `mod` d == 0) [1..x]

esPrimo:: Integral a => a->Bool
esPrimo 2 = True
esPrimo x = length(divisores x) == 2
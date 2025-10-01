-- -------------------------------------------------------------------------
-- Ejercicio 5: By listing the first six prime numbers:2,3,5,7,11 , and 13, we can see that the 6th prime is
-- 13. What is the 10 001 st prime number?
-- -------------------------------------------------------------------------

-- esprimo(x): Devuelve si el número introducido es primo o no 
-- Funcion que devuelve los divisores de x
divisores:: Integral a => a->[a]
divisores x = filter (\d -> x `mod` d == 0) [1..x]

esPrimo:: Integral a => a->Bool
esPrimo 2 = True
esPrimo x = length(divisores x) == 2

-- Ejercicio 5
-- usando guardas
encontrarPrimo :: Int -> Int
encontrarPrimo n = encontrarPrimo2 n 2 0  -- Empezamos desde el primer primo (2)
  where
    encontrarPrimo2 0 _ cuenta = cuenta       -- Cuando encontramos el n-ésimo primo, lo devolvemos
    encontrarPrimo2 n actual cuenta
        | esPrimo actual = encontrarPrimo2 (n - 1) (actual + 1) actual  -- Encontramos un primo, restamos 1
        | otherwise = encontrarPrimo2 n (actual + 1) cuenta              -- No es primo, seguimos


-- usando If-then-Else
encontrarPrimoIf :: Int -> Int
encontrarPrimoIf n = encontrarPrimoIf2 n 2 0  -- Empezamos desde el primer primo (2)
  where
    encontrarPrimoIf2 0 _ cuenta = cuenta       -- Cuando encontramos el n-ésimo primo, lo devolvemos
    encontrarPrimoIf2 n actual cuenta =
        if (esPrimo actual) then encontrarPrimoIf2 (n - 1) (actual + 1) actual  -- Encontramos un primo, restamos 1
        else encontrarPrimoIf2 n (actual + 1) cuenta              -- No es primo, seguimos


-- usando el case
encontrarPrimoCase :: Int -> Int
encontrarPrimoCase n = encontrarPrimoCase2 n 2 0  -- Empezamos desde el primer primo (2)
  where
    encontrarPrimoCase2 0 _ cuenta = cuenta  -- Cuando encontramos el n-ésimo primo, lo devolvemos
    encontrarPrimoCase2 n actual cuenta =
      case esPrimo actual of
        True  -> encontrarPrimoCase2 (n - 1) (actual + 1) actual  -- Encontramos un primo, restamos 1
        False -> encontrarPrimoCase2 n (actual + 1) cuenta         -- No es primo, seguimos buscando
--El where se usa en otros casos


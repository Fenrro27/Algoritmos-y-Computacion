-- -------------------------------------------------------------------------
-- EJERCICIO 1: Sea la función nsobrek tal que nsobrek n k es el número de
-- combinaciones de n elementos tomados de k en k
-- -------------------------------------------------------------------------


-- varias funciones
factorial::Integer->Integer
factorial 0 = 1
factorial n = n*factorial(n-1)

nsobrek :: Integer -> Integer -> Integer
nsobrek n k =factorial n `div` (factorial k * factorial (n - k))


-- guardas
factorialGuarda::(Num a, Ord a)=>a->a
factorialGuarda n 
    | n == 0 = 1
    | n > 0 = n*factorialGuarda(n-1)
    |otherwise = error "valor negativo"

nsobrekGuardas :: Integer -> Integer -> Integer
nsobrekGuardas n k
    | k > n     = 0                   -- No se pueden tomar más elementos de los que hay
    | k == 0    = 1                   -- C(n, 0) es 1
    | k == n    = 1                   -- C(n, n) es 1
    | otherwise = factorialGuarda n `div` (factorialGuarda k * factorial (n - k))


-- if-then-else
factorialIf:: Integer->Integer
factorialIf n = if(n==0) then 1 else n*factorialIf(n-1)

nsobrekIf :: Integer -> Integer -> Integer
nsobrekIf n k = 
    if k > n then 0  -- No se pueden tomar más elementos de los que hay
    else if k == 0 then 1  -- C(n, 0) es 1
    else if k == n then 1  -- C(n, n) es 1
    else factorialIf n `div` (factorialIf k * factorialIf (n - k))

-- case
-- No se me ha ocurrido una forma

-- where
nsobrekWhere :: Integer -> Integer -> Integer
nsobrekWhere n k = 
    if k > n then 0  -- No se pueden tomar más elementos de los que hay
    else if k == 0 then 1  -- C(n, 0) es 1
    else if k == n then 1  -- C(n, n) es 1
    else factorialW n `div` (factorialW k * factorialW (n - k))
    where
        factorialW 0 = 1
        factorialW n = n*factorial(n-1)
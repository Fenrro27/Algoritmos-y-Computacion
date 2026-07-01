
-- -------------------------------------------------------------------------
--Ejercicio 6: 10. The sum of the primes below 10 is 2+3+5+7=17 .


divisores:: Integral a => a->[a]
divisores x = filter (\d -> x `mod` d == 0) [1..x]

esPrimo:: Integral a => a->Bool
esPrimo 2 = True
esPrimo x = length(divisores x) == 2

sumPrimos:: Int->Int
sumPrimos 0 = 0
sumPrimos n = sum(filter esPrimo [0..n])

-- Guardas
sumPrimosGuardas:: Int->Int
sumPrimosGuardas 0 = 0
sumPrimosGuardas n = sum(filter esPrimoW [0..n])
    where
        esPrimoW n 
            | n == 2 = True
            | even n = False
            | otherwise = length( filter (\d -> n `mod` d == 0) [1..n] ) == 2

-- If-then-Else
sumPrimosIf:: Int->Int
sumPrimosIf 0 = 0
sumPrimosIf n = sum(filter esPrimoW [0..n])
    where
        esPrimoW n = if
            ( n == 2) then True
            else if (even n) then False
            else length( filter (\d -> n `mod` d == 0) [1..n] ) == 2

-- Where usado en otros casos


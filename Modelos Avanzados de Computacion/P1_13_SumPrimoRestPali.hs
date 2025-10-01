-- dada una lista de listas suma 1 por cada numero primo y resta 1 por cada palindromo, no importa que coincidan

-- es primo
divisores:: Integral a => a->[a]
divisores x = filter (\d -> x `mod` d == 0) [1..x]

esPrimo:: Integral a => a->Bool
esPrimo 0 = True
esPrimo 1 = True
esPrimo 2 = True
esPrimo x = length(divisores x) == 2

nPrimos:: [Int]->Int
nPrimos [] = 0
nPrimos lista = length(filter esPrimo lista)


-- es palindromo, modificado para numeros
esPalindroma :: [Int] -> Bool
esPalindroma [] = True
esPalindroma [_] = True
esPalindroma (cab:resto) = (cab  == last resto) && esPalindroma(init resto)


sumPrimoResPali :: [[Int]] -> Int
sumPrimoResPali lista = sum( map nPrimos lista) -length(filter esPalindroma lista)
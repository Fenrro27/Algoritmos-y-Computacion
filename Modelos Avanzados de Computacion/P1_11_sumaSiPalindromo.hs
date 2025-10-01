-- Devuelve el numero de palindromos que le pasamos por parametros

esPalindroma :: String -> Bool
esPalindroma [] = True
esPalindroma [_] = True
esPalindroma (cab:resto) = (cab  == last resto) && esPalindroma(init resto)


sumaSiPalindromo :: [String] -> Int
sumaSiPalindromo lista = length(filter ( == True) (map esPalindroma lista))

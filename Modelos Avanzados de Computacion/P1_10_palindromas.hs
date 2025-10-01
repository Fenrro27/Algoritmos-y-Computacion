-- palindromas []: comprueba son palíndromas todas las palabras de una lista


-- es_palindroma(p): comprueba si “p” es palíndroma
esPalindroma :: String -> Bool
esPalindroma [] = True
esPalindroma [_] = True
esPalindroma (cab:resto) = (cab  == last resto) && esPalindroma(init resto)


palindromas :: [String] -> Bool
palindromas lista = and (map esPalindroma lista)

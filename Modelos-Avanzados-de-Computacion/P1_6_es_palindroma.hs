-- es_palindroma(p): comprueba si “p” es palíndroma

esPalindroma:: String -> Bool
esPalindroma [] = True
esPalindroma [_] = True
esPalindroma (cab:resto) = (cab  == last resto) && esPalindroma(init resto)
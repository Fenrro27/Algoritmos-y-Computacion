-- palabras_mayores_n(n,a): devuelve una lista con las palabras mayores que n


palabrasMayoresN:: Int->[[a]]->[[a]] -- Cabecera
palabrasMayoresN _ [[]] = [[]] -- Caso base
palabrasMayoresN n listas = filter (\x -> length x > n) listas -- Caso generico




-- getMenorListas devuelve el menor de una lista de listas


getMenor::[Int]-> Int -- Cabecera
getMenor [] = maxBound -- Caso lista vacia
getMenor [x] = x-- Caso un elemento en la lista 
getMenor (cab:resto) = min cab (getMenor resto)


getMenorListas::[[Int]]->Int
getMenorListas [] = maxBound
getMenorListas ([]:resto) = getMenorListas resto
getMenorListas (cab:resto) = min (getMenor cab) (getMenorListas resto)
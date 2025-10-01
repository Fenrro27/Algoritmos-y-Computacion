-- sumparesimp [x..z]: suma los pares y resta los impares de una lista


sumparesimp :: [Int] -> Int
sumparesimp lista = sum(filter even lista) -sum(filter odd lista)
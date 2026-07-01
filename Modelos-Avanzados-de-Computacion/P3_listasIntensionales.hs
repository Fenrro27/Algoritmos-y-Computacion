-- Kevin Jesus Banda Azogil
-- El objetivo de la practica es replicar las listas con codigo Haskell

-- [11,12,13,14,15,16,17,18,19,20]
ej1 :: [Integer]
ej1 = [x+10 | x <- [1..10] ]

-- [[2],[4],[6],[8],[10]]
ej2 :: [[Integer]]
ej2 = [[x] | x <- [1..10], even x ]

-- [[10],[9],[8],[7],[6],[5],[4],[3],[2],[1]]
ej3 :: [[Integer]]
ej3 = [[11-x] | x <- [1..10] ]

-- [True,False,True,False,True,False,True,False,True,False]
ej4 :: [Bool]
ej4 = [odd x | x <- [1..10] ]

-- [(3,True),(6,True),(9,True),(12,True),(15,True),(18,True)]
ej5 :: [(Integer, Bool)]
ej5 = [(x*3, x<10) | x <- [1..10], x<7 ]

-- [(5,False),(10,True),(15,False),(40,False)]
ej6 :: [(Integer, Bool)]
ej6 = [(x*5, even x && x < 4) | x <- [1..10], x<=3 || x ==8 ]

-- [(11,12),(13,14),(15,16),(17,18),(19,20)]
ej7 :: [(Integer, Integer)]
ej7 = [ (x+10, x+11)| x <- [1..10] , odd x]

-- [[5,6,7],[5,6,7,8,9],[5,6,7,8,9,10,11],[5,6,7,8,9,10,11,12,13]]
ej8 :: [[Integer]]
ej8 = [ [5..x+4] | x <- [1..10], odd x && x>1 && x<=9 ]

-- [21,16,11,6,1]
ej9 :: [Integer]
ej9 = [(5-x)*5+1 | x <- [1..10], x<=5 ]

-- [[4],[6,4],[8,6,4],[10,8,6,4],[12,10,8,6,4]]
ej10 :: [[Integer]]
ej10 = [filter even (reverse [4..x+2]) | x <- [1..10], even x ]
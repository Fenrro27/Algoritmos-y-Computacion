-- muestra_foldr_suma_n (n): muestra por pantalla los pasos del foldr (+) [1,2..n]
-- desc. 4
-- (1+(2+(3+(4+ 0))))  

muestraFoldrSumaN:: Integer->[Char]
muestraFoldrSumaN n =foldr (\x y -> "(" ++ show x ++ "+" ++ y ++ ")")  "0" [1,2..n] 


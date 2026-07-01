-- -------------------------------------------------------------------------
-- EJERCICIO 4: Comprobar la pertenencia a una lista usando una función recursiva.
-- Pertenece a [b]
-- -------------------------------------------------------------------------
-- Pertenece para listas (varias ecuaciones)
pertenece :: (Eq a) => a -> [a] -> Bool
pertenece _ [] = False  
pertenece a (x:xs) = (a == x) || pertenece a xs

-- Pertenece usando guardas
perteneceGuardas :: (Eq a) => a -> [a] -> Bool
perteneceGuardas _ [] = False  
perteneceGuardas a (x:xs)      
    | a == x    = True   
    | otherwise  = perteneceGuardas a xs 

-- Pertenece usando if-else
perteneceIf :: (Eq a) => a -> [a] -> Bool
perteneceIf _ [] = False  
perteneceIf a (x:xs) = if (a == x) then True 
    else perteneceIf a xs 

-- Pertenece usando case
perteneceCase :: (Eq a) => a -> [a] -> Bool
perteneceCase _ [] = False  
perteneceCase a (x:xs) = case (a == x) of
    True  -> True
    False -> perteneceCase a xs

-- ----------------------
-- Redefinida para tuplas
-- ----------------------

-- Pertenece para tuplas usando varias ecuaciones
perteneceTupla :: Eq a => (a, a) -> [(a, a)] -> Bool
perteneceTupla _ [] = False  -- Caso base: si la lista está vacía, retorna False.
perteneceTupla t (x:xs) = (t == x) || perteneceTupla t xs 

-- Pertenece para tuplas usando guardas
perteneceTuplaGuardas :: Eq a => (a, a) -> [(a, a)] -> Bool
perteneceTuplaGuardas _ [] = False
perteneceTuplaGuardas a (x:xs)
    | a == x    = True
    | otherwise  = perteneceTuplaGuardas a xs

-- Pertenece para tuplas usando if-else
perteneceTuplaIf :: Eq a => (a, a) -> [(a, a)] -> Bool
perteneceTuplaIf _ [] = False
perteneceTuplaIf a (x:xs) = 
    if a == x then True 
    else perteneceTuplaIf a xs

-- Pertenece para tuplas usando case
perteneceTuplaCase :: Eq a => (a, a) -> [(a, a)] -> Bool
perteneceTuplaCase _ [] = False
perteneceTuplaCase a (x:xs) = case (a == x) of
    True -> True
    False -> perteneceTuplaCase a xs

-- Definiciones locales
-- No se han necesitado


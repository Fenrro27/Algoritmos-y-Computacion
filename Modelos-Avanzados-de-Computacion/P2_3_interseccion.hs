-- -------------------------------------------------------------------------
-- EJERCICIO 3: Sucesión de Fibonacci
-- Los intervalos cerrados se pueden representar mediante -- una lista de dos números (el primero
-- es el extremo inferior del intervalo y el segundo el superior).
-- Definir la función interseccion :: Ord a => [a] -> [a] -> [a], tal que (interseccion i1 i2) es la
-- intersección de los intervalos i1 e-- i2.
-- -------------------------------------------------------------------------

-- varias funciones
-- Definición de la función interseccion
interseccion :: Ord a => [a] -> [a] -> [a]
interseccion [] _ = []           -- Si el primer intervalo es vacío, devuelve vacío
interseccion _ [] = []           -- Si el segundo intervalo es vacío, devuelve vacío
interseccion [a, b] [c, d]
    | max a c <= min b d = [max a c, min b d]  -- Intersección válida
    | otherwise           = []                   -- No hay intersección

-- usando guardas
-- Definición de la función interseccion
interseccionGuardas :: Ord a => [a] -> [a] -> [a]
interseccionGuardas i1 i2
    | null i1 || null i2 = []                          -- Si alguno de los intervalos es vacío
    | max a c <= min b d = [max a c, min b d]         -- Intersección válida
    | otherwise           = []                          -- No hay intersección
  where
    [a, b] = i1  -- Límite inferior y superior del primer intervalo
    [c, d] = i2  -- Límite inferior y superior del segundo intervalo

-- usando if-else
interseccionIf :: Ord a => [a] -> [a] -> [a]
interseccionIf i1 i2
    = if (null i1 || null i2) then []                          -- Si alguno de los intervalos es vacío
    else if (max a c <= min b d) then [max a c, min b d]         -- Intersección válida
    else  []                          -- No hay intersección
  where
    [a, b] = i1  -- Límite inferior y superior del primer intervalo
    [c, d] = i2  -- Límite inferior y superior del segundo intervalo

-- usando case
-- Definición de la función interseccionCase
interseccionCase :: Ord a => [a] -> [a] -> [a]
interseccionCase i1 i2 =
    case (null i1, null i2) of
        (True, _) -> []                          -- Si el primer intervalo es vacío
        (_, True) -> []                          -- Si el segundo intervalo es vacío
        _ -> case (max a c <= min b d) of
            True  -> [max a c, min b d]        -- Intersección válida
            False -> []                         -- No hay intersección
  where
    [a, b] = i1  -- Límite inferior y superior del primer intervalo
    [c, d] = i2  -- Límite inferior y superior del segundo intervalo


-- usando where
-- Se usa en otros casos

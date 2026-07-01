-- -------------------------------------------------------------------------
-- Ejercicio 2: Definir la función raíces tal que raíces a b c es la lista de las raices de
-- la ecuación ax2 + bc + c = 0.
-- -------------------------------------------------------------------------

-- varias funciones
discriminante:: (Num a) => a->a->a->a
discriminante a b c = (b^2 - 4 * a * c)

raices :: (Floating a, Ord a) => a -> a -> a -> [a]
raices a b c = [(-b + sqrt (discriminante a b c)) / (2 * a), (-b - sqrt (discriminante a b c)) / (2 * a)]
-- Falla en algunos casos

-- guardas
raicesGuardas :: (Floating a, Ord a) => a -> a -> a -> [a]
raicesGuardas a b c
  | (discriminante a b c) > 0 = [(-b + sqrt (discriminante a b c)) / (2 * a), (-b - sqrt (discriminante a b c)) / (2 * a)]
  | (discriminante a b c)== 0 = [-b / (2 * a)]
  | otherwise = []  -- No hay raíces reales

-- if-then-else
raicesIf :: (Floating a, Ord a) => a -> a -> a -> [a]
raicesIf a b c =
  if (discriminante a b c) > 0 then [(-b + sqrt (discriminante a b c)) / (2 * a), (-b - sqrt (discriminante a b c)) / (2 * a)]
  else if (discriminante a b c)== 0 then [-b / (2 * a)]
  else []  -- No hay raíces reales


-- case
raicesCase :: (Floating a, Ord a) => a -> a -> a -> [a]
raicesCase a b c = case ( (discriminante a b c) > 0 ,  (discriminante a b c)== 0 ) of
    (True, _) ->[(-b + sqrt (discriminante a b c)) / (2 * a), (-b - sqrt (discriminante a b c)) / (2 * a)]
    (_, True) -> [-b / (2 * a)]
    _ -> []  -- No hay raíces reales

-- where
raicesWhere :: (Floating a, Ord a) => a -> a -> a -> [a]
raicesWhere a b c
  | discriminante2 > 0 = [(-b + sqrt discriminante2) / (2 * a), (-b - sqrt discriminante2) / (2 * a)]
  | discriminante2 == 0 = [-b / (2 * a)]
  | otherwise = []  -- No hay raíces reales
  where
    discriminante2 = b^2 - 4 * a * c


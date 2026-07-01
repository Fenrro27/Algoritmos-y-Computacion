module P4_MEmpleados(
    menu2,
    menu3,
    clear
) where

import System.Process (system)

-- Creacion de tipos:
data DNI = DNI 
    { numero :: Int, 
      letra :: String 
    } deriving (Show, Eq)

data Fecha = Fecha 
    { dia :: Int,
      mes :: Int,
      anio :: Int
    } deriving (Show, Eq)

-- Tipo de Empleado actualizado
data Empleado = Empleado {
    dni :: DNI,
    nombre :: String,
    apellidos :: String,
    fechaNacimiento :: Fecha,
    cargo :: String,
    fechaAlta :: Fecha
} deriving (Show, Eq)

-- Empleados:
e1 :: Empleado
e1 = Empleado
    { dni = DNI 1 "A"
    , nombre = "Juan"
    , apellidos = "Pérez"
    , fechaNacimiento = Fecha 15 5 1990
    , cargo = "Desarrollador"
    , fechaAlta = Fecha 1 1 2015
    }

e2 :: Empleado
e2 = Empleado
    { dni = DNI 2 "B"
    , nombre = "Ana"
    , apellidos = "García"
    , fechaNacimiento = Fecha 20 8 1992
    , cargo = "Diseñadora"
    , fechaAlta = Fecha 15 3 2016
    }

e3 :: Empleado
e3 = Empleado
    { dni = DNI 3 "C"
    , nombre = "Pedro"
    , apellidos = "Martínez"
    , fechaNacimiento = Fecha 10 12 1985
    , cargo = "Analista"
    , fechaAlta = Fecha 8 9 2018
    }

e4 :: Empleado
e4 = Empleado
    { dni = DNI 4 "D"
    , nombre = "Laura"
    , apellidos = "Fernández"
    , fechaNacimiento = Fecha 5 2 1994
    , cargo = "Project Manager"
    , fechaAlta = Fecha 10 11 2017
    }

e5 :: Empleado
e5 = Empleado
    { dni = DNI 5 "E"
    , nombre = "Carlos"
    , apellidos = "López"
    , fechaNacimiento = Fecha 5 2 1994
    , cargo = "Tester"
    , fechaAlta = Fecha 8 9 2018
    }

empleados = [e1, e2, e3, e4, e5] -- Lista con los empleados    

-- --------------------------------------------------------------------------------------------
-- definimos el arbol para el ejercicio 3
-- --------------------------------------------------------------------------------------------

-- Estructura del arbol
data Abb = Vacio | Nodo Empleado Abb Abb deriving(Show)

insertarNodo::Empleado->Abb ->Abb
insertarNodo empleado Vacio = Nodo empleado Vacio Vacio
insertarNodo empleado (Nodo a izq der)
    = case insertarIzquierda (fechaNacimiento empleado) (fechaNacimiento a) of
        -1 -> Nodo a (insertarNodo empleado izq) der
        1 -> Nodo a izq (insertarNodo empleado der)

arbol:: Abb
arbol = insertarNodo e5 (insertarNodo e4 (insertarNodo e3 (insertarNodo e2 (insertarNodo e1 Vacio))) )

insertarIzquierda:: Fecha->Fecha->Int
insertarIzquierda (Fecha d1 m1 a1)(Fecha d2 m2 a2)
    | f1 <= f2 = -1
    | f1 > f2 = 1
    where
        f1 = 10000 * a1 + 100 * m1 + d1
        f2 = 10000 * a2 + 100 * m2 + d2

-- Recorridos del arbol

inorden::Abb ->[Empleado]
inorden Vacio = []
inorden (Nodo empleado izq der) =
    inorden izq ++ [empleado] ++ inorden der

preorden :: Abb -> [Empleado]
preorden Vacio = [] 
preorden (Nodo empleado izq der) =
    [empleado] ++ preorden izq ++ preorden der  -- Visitamos primero la raíz, luego izquierda y derecha

postorden :: Abb -> [Empleado]
postorden Vacio = []  
postorden (Nodo empleado izq der) =
    postorden izq ++ postorden der ++ [empleado]  -- Recorremos primero izquierda y derecha, luego la raíz

anchura :: Abb -> [Empleado]
anchura Vacio = []
anchura arbol = recorrerAnchura [arbol]

-- cojemos el nodo de arriba, nos quedamos con el empleado y agregamos los hijos a la cola para devolver la lista
recorrerAnchura :: [Abb] -> [Empleado]
recorrerAnchura [] = [] 
recorrerAnchura (Vacio : resto) = recorrerAnchura resto  
recorrerAnchura (Nodo empleado izq der : resto) =
    empleado : recorrerAnchura (resto ++ [izq, der]) 


-- --------------------------------------------------------------------------------------------
-- Ejercicio 2
-- --------------------------------------------------------------------------------------------

mostrarEmpleado :: Empleado -> String
mostrarEmpleado e =
    "DNI: " ++ show (dni e) ++ "\t" ++
    "Nombre: " ++ nombre e ++ " "  ++ apellidos e ++ "\n" ++
    "Fecha de Nacimiento: " ++ show (fechaNacimiento e) ++ "\n" ++
    "Cargo: " ++ cargo e ++ "\n" ++
    "Fecha de Alta: " ++ show (fechaAlta e) ++ "\n"

menu2 :: IO()
menu2 = 
    do  clear
        putStrLn "Menu Ejercicio2:"
        putStrLn "1. Listar las empleados con todos sus datos"
        putStrLn "2. Obtener un listado filtrado por la fecha de nacimiento y/o fecha de Alta"
        putStrLn "3. Buscar por cargo"
        putStrLn "4. Volver al menu principal"
        putStrLn "Elige una opcion: "
        s <- getLine
        putStrLn "\n\n"
        case s of 
            "1" -> listaEmpleados
            "2" -> filtrarEmpleados
            "3" -> buscarCargo
            "4" -> volverMain

listaEmpleados :: IO ()
listaEmpleados = do
    clear
    putStrLn "Listar los empleados con todos sus datos:"
    let empleadosTexto = unlines (map mostrarEmpleado empleados)
    putStrLn empleadosTexto
    putStrLn "1. Volver al menu principal Volver (S/N): "
    s <- getLine
    case s of
        "S" -> menu2
        _ -> do
            putStrLn "No se volverá al menú principal"
            listaEmpleados

filtrarEmpleados :: IO ()
filtrarEmpleados = do
    clear
    putStrLn "Obtener un listado filtrado por la fecha de nacimiento y/o fecha de alta:"
    
    -- Solicitar fecha de nacimiento
    putStrLn "Introduce la fecha de nacimiento para filtrar: "
    fechaNac <- leerFecha

    -- Solicitar fecha de alta
    putStrLn "Introduce la fecha de alta para filtrar: "
    fechaAlta <- leerFecha

    -- Filtramos
    let empleadosFiltrados = unlines (map mostrarEmpleado (filtrarFAFN fechaNac fechaAlta empleados))
    putStrLn empleadosFiltrados
  
    -- Volver al menú principal
    putStrLn "1. Volver al menu principal"
    putStrLn "Culaquier Otro Boton: Volver a filtrar"
    putStrLn "Elige una opcion: "
    s <- getLine
    case s of
        "1" -> menu2
        _   -> filtrarEmpleados

filtrarFAFN :: Fecha -> Fecha -> [Empleado] -> [Empleado]
filtrarFAFN fnac falt empl = 
    case (esFechaInvalida fnac, esFechaInvalida falt) of
        (True, False) -> filter (\e -> fechaAlta e == falt) empl            
        (False, True) -> filter (\e -> fechaNacimiento e == fnac) empl            
        (True, True)  -> empl
        (False, False) -> filter (\e -> fechaNacimiento e == fnac && fechaAlta e == falt) empl            
 where
    esFechaInvalida (Fecha dia mes anio) = dia == -1 && mes == -1 && anio == -1

leerFecha :: IO Fecha
leerFecha = do
    putStrLn "Desea insertar una fecha? (S/N):"
    respuesta <- getLine
    case respuesta of
        "S" -> do
            putStrLn "Introduce el día: "
            diaStr <- getLine
            putStrLn "Introduce el mes: "
            mesStr <- getLine
            putStrLn "Introduce el año: "
            anioStr <- getLine
            
            let dia = read diaStr :: Int
                mes = read mesStr :: Int
                anio = read anioStr :: Int

            let f = (Fecha dia mes anio)

            if esFechaInvalida f then
                return (Fecha (-1) (-1) (-1))  
            else
                return f 
        _ -> return (Fecha (-1) (-1) (-1))
    where
        esFechaInvalida (Fecha dia mes anio) = dia == -1 || mes == -1 || anio == -1

buscarCargo :: IO()
buscarCargo = 
    do  clear
        putStrLn "Buscar por cargo:"
        putStrLn "Introduce el cargo: "
        c <- getLine

        let empleadosFiltrados = unlines (map mostrarEmpleado (filter (\e -> cargo e == c) empleados ))
        putStrLn empleadosFiltrados

        putStrLn "1. Volver al menu principal"
        putStrLn "Elige una opcion: "
        s <- getLine
        case s of 
            "1" -> menu2 
            _ -> do 
                putStrLn "Error en la entrada"
                buscarCargo

-- --------------------------------------------------------------------------------------------
-- Ejercicio 3
-- --------------------------------------------------------------------------------------------
menu3 :: IO()
menu3 = 
    do  clear
        putStrLn "Menu Ejercicio3:"
        putStrLn "1. Listar los empleados utilizando recorrido en profundidad"
        putStrLn "2. Listar los empleados utilizando recorrido en anchura"
        putStrLn "3. Buscar un empleado por dni"
        putStrLn "4. Volver al menu principal"
        putStrLn "Elige una opcion: "
        s <- getLine
        putStrLn "\n\n"
        case s of 
            "1" -> listarProfundidad
            "2" -> listarAnchura
            "3" -> buscarDNI
            "4" -> volverMain

  
buscarDNI :: IO ()
buscarDNI = do
    clear
    putStrLn "Buscar un empleado por DNI:"
    
    putStrLn "Desea ingresar el número del DNI? (S/N): "
    respuestaNumero <- getLine
    numeroDNI <- case respuestaNumero of
        "S" -> getNumDNI
        _   -> return (-1) 

    putStrLn "Desea ingresar la letra del DNI? (S/N): "
    respuestaLetra <- getLine
    letraDNI <- case respuestaLetra of
        "S" -> getLetDNI
        _   -> return "NAN" 

    -- Filtramos segun el caso

    let empFilt = case (letraDNI == "NAN", numeroDNI == (-1)) of
            (False, False) -> (filter (\e -> numero (dni e) == numeroDNI && letra (dni e) == letraDNI) empleados)
            (False, True) -> filter (\e -> letra (dni e) == letraDNI) empleados
            (True, False) -> filter (\e -> numero (dni e) == numeroDNI) empleados
            (_, _) -> empleados 


    let empleadosFiltrados = unlines (map mostrarEmpleado empFilt)
    putStrLn empleadosFiltrados


    putStrLn "1. Volver al menu principal"
    putStrLn "Culaquier Otro Boton: Volver a filtrar"
    putStrLn "Elige una opcion: "
    s <- getLine
    case s of
        "1" -> menu3
        _   -> buscarDNI
    

getNumDNI:: IO Int
getNumDNI = do
            putStrLn "Introduce el número del DNI: "
            nDNI <- getLine
            return (read nDNI :: Int)  

getLetDNI:: IO String
getLetDNI = do
            putStrLn "Introduce la letra del DNI: "
            lDNI <- getLine
            return lDNI 


listarProfundidad :: IO ()
listarProfundidad = do
    clear
    putStrLn "Listar empleados utilizando recorrido en profundidad:"
    putStrLn "1. Recorrido Preorden"
    putStrLn "2. Recorrido Inorden"
    putStrLn "3. Recorrido Postorden"
    putStrLn "4. Volver al menú principal"
    putStrLn "Elige una opción:"
    opcion <- getLine
    case opcion of
        "1" -> do
            putStrLn "Recorrido Preorden:"
            let empleadosTexto = unlines (map mostrarEmpleado (preorden arbol))
            putStrLn empleadosTexto
            volverMenuProfundidad
        "2" -> do
            putStrLn "Recorrido Inorden:"
            let empleadosTexto = unlines (map mostrarEmpleado (inorden arbol))
            putStrLn empleadosTexto
            volverMenuProfundidad
        "3" -> do
            putStrLn "Recorrido Postorden:"
            let empleadosTexto = unlines (map mostrarEmpleado (postorden arbol))
            putStrLn empleadosTexto
            volverMenuProfundidad
        "4" -> menu3
        _   -> do
            listarProfundidad

volverMenuProfundidad :: IO ()
volverMenuProfundidad = do
    putStrLn "Volver atras(S/N):"
    opcion <- getLine
    case opcion of
        "S" -> menu3
        _ -> listarProfundidad
        

      
listarAnchura :: IO ()
listarAnchura = do
    clear
    putStrLn "Listar empleados utilizando recorrido en anchura:"
    let empleadosTexto = unlines (map mostrarEmpleado (anchura arbol))
    putStrLn empleadosTexto
    volverMenuAnchura

volverMenuAnchura :: IO ()
volverMenuAnchura = do
    putStrLn "Volver atras(S/N):"
    opcion <- getLine
    case opcion of
        "S" -> menu3
        _ -> listarAnchura



-- --------------------------------------------------------------------------------------------
-- Funciones de pantalla
-- --------------------------------------------------------------------------------------------
-- Borramos la pantalla
clear :: IO ()
clear = do
    _ <- system "cls"  -- Ejecuta "cls" y descarta el valor de retorno (ExitCode)
    return ()           -- Asegura que la función tenga tipo IO ()

volverMain :: IO ()
volverMain = do
    putStrLn " "

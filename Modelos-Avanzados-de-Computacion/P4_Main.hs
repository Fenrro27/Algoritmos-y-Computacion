import P4_MEmpleados


main::IO()
main = do 
    menuMain 


menuMain:: IO()
menuMain = 
    do  clear
        putStrLn "Menu Principal:"
        putStrLn "1. Ejercicio2"
        putStrLn "2. Ejercicio3"
        putStrLn "3. salir"
        putStr "Elige una opcion: "
        s <- getLine
        case s of 
            "1" -> do
                P4_MEmpleados.menu2
                menuMain
            "2" -> do
                P4_MEmpleados.menu3
                menuMain
            "3"->do putStrLn"Salir"
                    esperaTecla
            _ -> do putStr"Error en la entrada"
                    esperaTecla
                    menuMain
        

esperaTecla :: IO ()
esperaTecla = do
    putStrLn "Presiona Enter para continuar..."
    _ <- getLine  -- Captura la entrada del usuario pero no la utiliza
    return ()
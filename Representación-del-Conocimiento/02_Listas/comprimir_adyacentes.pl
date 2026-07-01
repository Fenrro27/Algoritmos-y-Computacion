% ===================================================================
% Ejercicio: comprimir_adyacentes.pl
% Descripcion: Elimina los elementos duplicados contiguos (adyacentes) de una lista.
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================


/*
 
 comprime(+Lista, -R)
   es cierto si R unifica con una lista de la siguiente forma:
   
   comprime([a,a,a,b,b,c,c,c,a,a], R)
   R = [(a,3), (b,2), (c,3), (a,2)]
   
   otra posible representación de la salida
   
   R =[a-3, b-2, c-3, a-2]
   
*/


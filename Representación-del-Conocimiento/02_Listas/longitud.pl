% ===================================================================
% Ejercicio: longitud.pl
% Descripcion: Calcula el numero de elementos (longitud) de una lista dada.
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================

/*

 num_elem(+Lista, -Resultado)
   es cierto si Resultado unifica con el
   número de elementos de Lista.
   
Principio de inducción
1. num_elem([], ).
2. num_elem(n-1) -> num_elem(n)

*/

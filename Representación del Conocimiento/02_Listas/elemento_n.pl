% ===================================================================
% Ejercicio: elemento_n.pl
% Descripcion: Recupera el elemento que se encuentra en la posicion N de una lista (0-indexed).
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================

/*
 elemento_n(+Lista, +N, -R)
   es cierto cuando R unifica con
   el elemento de Lista que ocupa
   la posición N empezando a contar
   en 1.
   
   Inducción
   1) P(n0)
   2) P(n-1) -> P(n)
 
 */


elemento_n([Cab|_], 1, Cab).

elemento_n([_|Resto], N, R):-
    N > 1,
    N2 is N-1,
    elemento_n(Resto, N2, R).

/*

elemento_n([1,2,3,4,5], 3, R).

elemento_n([2,3,4,5], 2, R).

*/

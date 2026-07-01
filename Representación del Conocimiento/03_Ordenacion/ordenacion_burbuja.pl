% ===================================================================
% Ejercicio: ordenacion_burbuja.pl
% Descripcion: Implementa el algoritmo de ordenacion de burbuja (Bubble Sort) para una lista numerica.
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================

%-----------------------------------------------------
% ordena_burbuja(+Lista, -ListaR).
%   es cierto cuando ListaR unifica con una lista que
%   contiene los mismos elementos que Lista ordenados
%   de menor a mayor.
%-----------------------------------------------------

ordena_burbuja(Lista, Lista):- ordenada(Lista).

ordena_burbuja(Lista, R):- \+ ordenada(Lista), append(L1, [Elem1, Elem2|L2], Lista),
  Elem1 > Elem2,
  append(L1, [Elem2, Elem1|L2], ListaN), 
  ordena_burbuja(ListaN, R).

ordena_burbuja(Lista, R):- \+ ordenada(Lista), append(_, [Elem1, Elem2|_], Lista),
  Elem1 =< Elem2,
  ordena_burbuja(Lista, R).


/*

ordenada(+Lista)
  es cierto si Lista está ordenada de menor a mayor. 

*/

ordenada([]).
ordenada([_]).
ordenada([Cab1, Cab2|Resto]):- Cab1 =< Cab2, ordenada([Cab2|Resto]).



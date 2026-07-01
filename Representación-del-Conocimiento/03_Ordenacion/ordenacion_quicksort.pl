% ===================================================================
% Ejercicio: ordenacion_quicksort.pl
% Descripcion: Implementa el algoritmo de ordenacion rapida (Quicksort).
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================

%-----------------------------------------------------
% divide(+Elem, +Lista, -Menores, -Mayores)
%   es cierto cuando Menores unifica con una lista que
%   contiene los elemenentos de Lista que son menores
%   o iguales que Elem y Mayores unifica con una lista
%   que contiene los elementos de Lista que son
%   mayores que Elem.
%------------------------------------------------------

divide(_, [], [], []).
divide(Elem, [Cab|Resto], [Cab|RMe], RMa):- Cab =< Elem, divide(Elem, Resto, RMe, RMa).
divide(Elem, [Cab|Resto], RMe, [Cab|RMa]):- Cab > Elem, divide(Elem, Resto, RMe, RMa).

%---------------------------------------------------
% ordena_quick(+Lista, -ListaR).
%es cierto cuando ListaR unifica con una lista que
%contiene los mismos elementos que Lista ordenados
%de menor a mayor.
%------------------------------------------------

ordena_quick([], []).
ordena_quick([Pivote|Resto], R):- divide(Pivote, Resto, Me, Ma), 
                                   ordena_quick(Me, RMe), ordena_quick(Ma, RMa),
								   append(RMe, [Pivote|RMa], R).



%---------------------------------------------------
% ordena_quick(+Lista, -ListaR).
%es cierto cuando ListaR unifica con una lista que
%contiene los mismos elementos que Lista ordenados
%de menor a mayor.
%------------------------------------------------

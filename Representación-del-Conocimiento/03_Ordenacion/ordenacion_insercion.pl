% ===================================================================
% Ejercicio: ordenacion_insercion.pl
% Descripcion: Implementa el algoritmo de ordenacion por insercion (Insertion Sort).
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================

% inserta_en_list_ord(+Elem, +Lista, -ListaR).  
%   es cierto cuando ListaR unifica con una lista
%    que contiene los elementos de la lista ordenada
%    Lista, con el elemento Elem insertado de forma
%    ordenada.
%-----------------------------------------------------

inserta_en_list_ord(Elem, [], [Elem]).
inserta_en_list_ord(Elem, [Cab|Resto], [Elem, Cab|Resto]):- Elem =< Cab.
inserta_en_list_ord(Elem, [Cab|Resto], [Cab|R]):- Elem > Cab, 
   inserta_en_list_ord(Elem, Resto, R).

%-----------------------------------------------------
% ordena_insercion(+Lista, -ListaR).
%   es cierto cuando ListaR unifica con una lista que
%   contiene los mismos elementos que Lista ordenados
%   bde menor a mayor.
%-----------------------------------------------------

ordena_insercion([], []).
ordena_insercion([Cab|Resto], R2):-  ordena_insercion(Resto, R), inserta_en_list_ord(Cab, R, R2).

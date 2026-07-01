% ===================================================================
% Ejercicio: permutar.pl
% Descripcion: Genera las distintas permutaciones de los elementos de una lista.
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================

%--------------------------------------------------
% permuta(+Lista, -ListaR).
%   es cierto cuando ListaR unifica con una lista
%  que contiene los elementos de Lista en orden
%  distinto. Este predicado genera todas las
%  permutaciones posibles al solicitar más soluciones
%  con ";".
%--------------------------------------------------

permuta([], []).

permuta([Cab|Resto], R2):- permuta(Resto, R),
  insertar_todas(Cab, R, R2).


%--------------------------------------------------
% insertar_todas(+Elem, +Lista, -ListaR)
%   es cierto si ListaR unifica con una lista que
%   contiene los elementos de Lista con Elem
%   insertado en cualquier posicion.
%--------------------------------------------------


insertar_todas(Elem, Lista, [Elem|Lista]).

insertar_todas(Elem, [Cab|Resto], [Cab|R]):- 
   insertar_todas(Elem, Resto, R).

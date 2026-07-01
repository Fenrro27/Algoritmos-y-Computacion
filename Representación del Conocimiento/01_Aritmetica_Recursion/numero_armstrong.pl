% ===================================================================
% Ejercicio: numero_armstrong.pl
% Descripcion: Comprueba si un numero dado es un numero de Armstrong (igual a la suma de sus digitos elevados a la potencia del numero de digitos).
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================


armstrong_number(N):- number_to_chars(N, R), chars_to_numbers(R, R2),
length(R2, Pow), maplist(pow(Pow), R2, R3), sum_list(R3,N). 


chars_to_numbers([], []).
chars_to_numbers([Head|Tail], [N|R]):-
 chars_to_numbers(Tail, R),
 number_to_chars(N, [Head]).
 
pow(P, N, R):- R is N ^ P.

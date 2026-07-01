% ===================================================================
% Ejercicio: binario_a_decimal.pl
% Descripcion: Convierte una lista de digitos binarios en su representacion decimal entera.
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================

/*

binary(Str, Dec).

*/

binary(Str, Dec):- string_chars(Str, List), 
   maplist(binary, List), chars2dec(List, Dec).

/*
chars2dec(List, Dec)
*/


chars2dec([], 0).

chars2dec([Head|Tail], R):-
  chars2dec(Tail, Dec),
  number_chars(N, [Head]),  length(Tail, L), 
  R is Dec + N * 2 ^ L.

binary('0').
binary('1').

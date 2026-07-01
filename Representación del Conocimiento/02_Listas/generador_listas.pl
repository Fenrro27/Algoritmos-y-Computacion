% ===================================================================
% Ejercicio: generador_listas.pl
% Descripcion: Genera una lista de N elementos aleatorios.
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================

/*

crea_lista_n(+Tam, +N, -ListaR)
  es cierto si ListaR unifica con una lista de tamaño N
  que contiene valores aleatorios entre 0 y N - 1.
 
*/


crea_lista_n(0, _, []).

crea_lista_n(Tam, N, [Cab|R]):- Tam > 0, Tam2 is Tam - 1, crea_lista_n(Tam2, N, R),
                          random(0, N, Cab).

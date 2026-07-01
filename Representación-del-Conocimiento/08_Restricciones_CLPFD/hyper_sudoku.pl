% ===================================================================
% Ejercicio: hyper_sudoku.pl
% Descripcion: Resuelve Sudokus del tipo Hyper Sudoku, que anaden 4 subregiones cuadradas adicionales de 3x3.
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================



:- use_module(library(clpfd)).

/*

   Sudoku 9x9

    .  .  4 | 8  .  . | .  1  7	     9  3  4 | 8  2  5 | 6  1  7	     
            |         |                      |         |
    6  7  . | 9  .  . | .  .  .	     6  7  2 | 9  1  4 | 8  5  3
            |         |                      |         |
    5  .  8 | .  3  . | .  .  4      5  1  8 | 6  3  7 | 9  2  4
    --------+---------+--------      --------+---------+--------
    3  .  . | 7  4  . | 1  .  .      3  2  5 | 7  4  8 | 1  6  9
            |         |                      |         |
    .  6  9 | .  .  . | 7  8  .      4  6  9 | 1  5  3 | 7  8  2
            |         |                      |         |
    .  .  1 | .  6  9 | .  .  5      7  8  1 | 2  6  9 | 4  3  5
    --------+---------+--------      --------+---------+--------
    1  .  . | .  8  . | 3  .  6	     1  9  7 | 5  8  2 | 3  4  6
            |         |                      |         |
    .  .  . | .  .  6 | .  9  1	     8  5  3 | 4  7  6 | 2  9  1
            |         |                      |         |
    2  4  . | .  .  1 | 5  .  .      2  4  6 | 3  9  1 | 5  7  8
   



sudoku(+Matriz).
  es cierto si Matriz unifica con un matriz 9x9 que cumple
  las restricciones de un soduku 9x9.
  
  1. Puede generar un sudoku válido si Matriz es una variable libre.
  2. Puede comprobar si Matriz es un sudoku válido si Matriz está instanciada.
  3. Puede encontrar los huecos que faltan si Matriz está parcialmente instanciada.

*/

sudoku1(
[[1, 2, 3, 4, 5, 6, 7, 8, 9],
[4, 5, 6, 7, 8, 9, 1, 2, 3],
[7, 8, 9, 1, 2, 3, 4, 5, 6],
[2, 1, 4, 3, 6, 5, 8, 9, 7],
[3, 6, 5, 8, 9, 7, 2, 1, 4],
[8, 9, 7, 2, 1, 4, 3, 6, 5],
[5, 3, 1, 6, 4, 2, 9, 7, 8],
[6, 4, 2, 9, 7, 8, 5, 3, 1],
[9, 7, 8, 5, 3, 1, 6, 4, 2]]

).


sudoku2(
[[1, 2, 3, _, 5, _, 7, 8, 9],
[4, 5, _, 7, 8, 9, _, 2, 3],
[7, 8, 9, 1, 2, 3, 4, 5, 6],
[2, 1, _, 3, 6, _, 8, 9, 7],
[3, 6, 5, 8, 9, 7, 2, 1, 4],
[8, 9, _, 2, 1, 4, 3, 6, 5],
[5, 3, 1, 6, 4, _, 9, 7, 8],
[6, 4, _, 9, 7, 8, 5, 3, 1],
[9, 7, 8, 5, 3, _, 6, 4, 2]]

).


sudoku(Rows):-
  length(Rows, 9),
  maplist(same_length(Rows), Rows),
  append(Rows, Vs), Vs ins 1 .. 9,
  maplist(all_distinct, Rows),
  transpose(Rows, Columns),
  maplist(all_distinct, Columns),
  Rows = [As,Bs,Cs,Ds,Es,Fs,Gs,Hs,Is],
  blocks(As,Bs,Cs),
  blocks(Ds,Es,Fs),
  blocks(Gs,Hs,Is),
  blocks2(Bs,Cs,Ds),
  blocks2(Fs,Gs,Hs).
  
  

/*
blocks(+L1,+L2,+L3)
  es cierto si los tres bloques 3x3 que forman las
  tres listas de longitud 9, tienen valores distintos.
*/

blocks([],[],[]).

blocks([A,B,C|Resto1], [D,E,F|Resto2], [G,H,I|Resto3]):-
  all_distinct([A,B,C,D,E,F,G,H,I]), 
  blocks(Resto1, Resto2, Resto3).
  
blocks2([],[],[]). 
blocks2([_],[_],[_]). 
blocks2([_,A,B,C|Resto1], [_,D,E,F|Resto2], [_,G,H,I|Resto3]):-
  all_distinct([A,B,C,D,E,F,G,H,I]), 
  blocks2(Resto1, Resto2, Resto3). 
  
  
  
  
  
/*


sudoku(Rows),
         maplist(labeling([ff]), Rows),
         maplist(portray_clause, Rows).
		 
*/
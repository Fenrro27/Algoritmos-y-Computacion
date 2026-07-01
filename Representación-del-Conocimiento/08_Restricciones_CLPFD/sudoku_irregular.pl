% ===================================================================
% Ejercicio: sudoku_irregular.pl
% Descripcion: Resuelve Sudokus irregulares (con subregiones de formas no cuadradas).
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================

sudoku(Rows):-
  length(Rows, 9),
  maplist(same_length(Rows), Rows),
  append(Rows, Vs), Vs ins 1 .. 9,
  maplist(all_distinct, Rows),
  transpose(Rows, Columns),
  maplist(all_distinct, Columns),
  Rows = [ [P11, P12, P13, P14, P15, P16, P17, P18, P19],
           [P21, P22, P23, P24, P25, P26, P27, P28, P29],
		   [P31, P32, P33, P34, P35, P36, P37, P38, P39],
		   [P41, P42, P43, P44, P45, P46, P47, P48, P49],
		   [P51, P52, P53, P54, P55, P56, P57, P58, P59],
		   [P61, P62, P63, P64, P65, P66, P67, P68, P69],
		   [P71, P72, P73, P74, P75, P76, P77, P78, P79],
		   [P81, P82, P83, P84, P85, P86, P87, P88, P89],
		   [P91, P92, P93, P94, P95, P96, P97, P98, P99],
          ],
		  all_distinct([P11,P12,P13,P14,P21,P23,P31,P41,P51]),

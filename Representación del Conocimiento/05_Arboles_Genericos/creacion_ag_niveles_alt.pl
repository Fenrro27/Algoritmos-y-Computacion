% ===================================================================
% Ejercicio: creacion_ag_niveles_alt.pl
% Descripcion: Variacion del creador de arboles genericos por niveles.
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================

crea_ag(N, [Cab|Resto],  a(Cab, R) ):-  length(Resto, L), L < N,
  crea_ag_lista_arboles(Resto, R).
  
  
crea_ag(N, [Cab|Resto], a(Cab, R) ):- N > 1, length(Resto, L), L >= N,
  Div is L div N,
  N2 is N - 1, 
  length(Lista, N2),
  maplist(my_length(Div), Lista),
  append([Cab2|Lista], Resto),
  maplist(crea_ag(N), [Cab2|Lista], R).
  
my_length(L, Lista):- length(Lista, L).  

/*
crea_ag_lista_arboles(+Lista, -ListaArbolesGenericos)
  es cierto si ArbolGenerico unifica con una lista de árboles que 
  contiene las etiquetas de Lista

*/

crea_ag_lista_arboles([], []).
crea_ag_lista_arboles([Cab|Resto], [a(Cab,[])|R]):-
  crea_ag_lista_arboles(Resto, R).

% ===================================================================
% Ejercicio: creacion_ag_niveles.pl
% Descripcion: Crea un arbol generico organizando sus nodos por niveles.
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================



/*

crea_ag(+N, +Lista, -ArbolGenerico) 
 es cierto si ArbolGenerico unifica con un árbol genérico que tiene
 como máximo N hijos por nodo con las etiquetas de Lista.

*/

crea_ag(N, [Cab|Resto],  a(Cab, R) ):-  length(Resto, L), L < N,
  crea_ag_lista_arboles(Resto, R).
  
  
crea_ag(N, [Cab|Resto], a(Cab, R) ):- length(Resto, L), L >= N,
  0 is L mod N,
  Div is L div N,
  length(Lista, N), 
  maplist(my_length(Div), Lista),
  append(Lista, Resto),
  maplist(crea_ag(N), Lista, R).
  
crea_ag(N, [Cab|Resto], a(Cab, R) ):- length(Resto, L), L >= N,
  \+ 0 is L mod N,
  Div is L div N,
  L2 is N * Div, length(Lista, L2),
  append(Lista, Sobrantes, Resto),
  maplist(my_length(Div), Lista),
  append(Lista, Resto),
  Lista = [Cab2|Resto2],
  append(Sobrantes, Cab2, R2),
  append(R2, Resto2, Lista3),
  maplist(crea_ag(N), Lista3, R).  
  
my_length(L, Lista):- length(Lista, L).  
  
  
  


/*
crea_ag_lista_arboles(+Lista, -ListaArbolesGenericos)
  es cierto si ArbolGenerico unifica con una lista de árboles que 
  contiene las etiquetas de Lista

*/

crea_ag_lista_arboles([], []).
crea_ag_lista_arboles([Cab|Resto], [a(Cab,[])|R]):-
  crea_ag_lista_arboles(Resto, R).

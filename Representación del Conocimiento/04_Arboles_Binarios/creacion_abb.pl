% ===================================================================
% Ejercicio: creacion_abb.pl
% Descripcion: Construye un arbol binario de busqueda (ABB) ordenando adecuadamente las inserciones.
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================



/*

crea_abb(+ListaEtiquetas, -ArbolBinario)
  es cierto cuando ArbolBinario unifica con
  un árbol binario de búsqueda balanceado que contiene solo las
  etiquetas de ListaEtiquetas.

*/

crea_abb([], nil).

crea_abb(Lista, a(Raiz, Hi, Hd) ):-
  length(Lista, L),
  Mitad is L div 2,
  length(ListaIzq, Mitad),
  append(ListaIzq, [Raiz|ListaDch], Lista),
  crea_abb(ListaIzq, Hi),
  crea_abb(ListaDch, Hd).
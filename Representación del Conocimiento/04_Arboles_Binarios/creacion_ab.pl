% ===================================================================
% Ejercicio: creacion_ab.pl
% Descripcion: Construye o define un arbol binario a partir de un listado de elementos.
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================

/*

crea_ab(+ListaEtiquetas, -ArbolBinario)
  es cierto cuando ArbolBinario unifica con
  un árbol binario balanceado que contiene solo las
  etiquetas de ListaEtiquetas.

*/

crea_ab([], nil).

crea_ab([Cab|Resto], a(Cab, Hi, Hd) ):-
  length(Resto, L),
  Mitad is L div 2,
  length(ListaIzq, Mitad),
  append(ListaIzq, ListaDch, Resto),
  crea_ab(ListaIzq, Hi),
  crea_ab(ListaDch, Hd).
  





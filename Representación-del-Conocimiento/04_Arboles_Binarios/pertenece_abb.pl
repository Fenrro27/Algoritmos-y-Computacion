% ===================================================================
% Ejercicio: pertenece_abb.pl
% Descripcion: Verifica de forma eficiente si un elemento pertenece a un arbol binario de busqueda.
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================



/*

pertenece_abb(+Elem, +Abb)
  es cierto si Elem pertenece al árbol binario de búsqueda
  Abb.
  
*/

pertenece_abb(Elem, a(Elem, _, _) ).
pertenece_abb(Elem, a(Raiz, _, Hd) ):- Elem > Raiz, pertenece_abb(Elem, Hd).
pertenece_abb(Elem, a(Raiz, Hi, _) ):- Elem < Raiz, pertenece_abb(Elem, Hi). 
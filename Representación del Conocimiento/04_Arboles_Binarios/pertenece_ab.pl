% ===================================================================
% Ejercicio: pertenece_ab.pl
% Descripcion: Verifica si un elemento dado pertenece a un arbol binario.
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================



arbol1( a(1, a(2,nil,nil), a(3, nil, a(4,nil,nil))) ).

/*
pertenece_ab(+Elem, +ArbolBinario)
  es cierto si la etiqueta Elem pertenece 
  a ArbolBinario.
*/ 

pertenece_ab(Elem, a(Elem, _, _) ).

pertenece_ab(Elem, a(_, Hi, _) ):- 
  pertenece_ab(Elem, Hi).

pertenece_ab(Elem, a(_, _, Hd) ):- 
  pertenece_ab(Elem, Hd).  
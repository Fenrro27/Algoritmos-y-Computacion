% ===================================================================
% Ejercicio: balanceado_ab.pl
% Descripcion: Determina si un arbol binario esta balanceado en altura.
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================


arbol2( a(1, a(2, a(4, a(6,nil,nil), nil), nil), a(3, nil, a(5, nil, a(7,nil,nil))) )  ).

/*

balenceado(+ArbolBinario)
  es cierto si ArbolBinario unifica con un árbol
  binario que PARA TODO NODO la diferencia entre
  la altura del árbol izquierdo y derecho es como
  máximo 1.

*/

balanceado(nil).
balanceado(a(_, Hi, Hd)):-
  altura_ab(Hi, Ai),
  altura_ab(Hd, Ad),
  Dif is Ai - Ad,
  Abs is abs(Dif), Abs =< 1,
  balanceado(Hi), balanceado(Hd).


/* 

altura_ab(+ArbolBinario, -Altura)
  es cierto si Altura unifica con la altura del árbol binario AbolBinario

*/

altura_ab(nil, 0).
altura_ab(a(_, Hi, Hd), Altura):-
  altura_ab(Hi, Ai),
  altura_ab(Hd, Ad),
  A is max(Ai, Ad),
  Altura is A + 1.
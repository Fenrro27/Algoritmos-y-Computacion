% ===================================================================
% Ejercicio: altura_ag.pl
% Descripcion: Calcula la altura (maximo numero de niveles) de un arbol generico.
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================



/*

altura_ag(+ArbolGenerico, -Altura)
  es cierto si Altura unifica con la 
  altura de ArbolGenerico.

*/

altura_ag(a(_, ListaArboles), R2):- 
  altura_lista_ag(ListaArboles, R),
  R2 is R + 1.
    
altura_lista_ag([], 0).
altura_lista_ag([Cab|Resto], AlturaListaAG):-
  altura_lista_ag(Resto, R),
  altura_ag(Cab, RCa),
  AlturaListaAG is max(R, RCa).
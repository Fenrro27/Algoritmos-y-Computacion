% ===================================================================
% Ejercicio: cuenta_nodos_ag.pl
% Descripcion: Calcula el numero total de nodos contenidos en un arbol generico.
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================


arbolg1( a(a, [a(b,[a(e,[])]), a(c,[]), a(d,[])]) ).


/*

 cuenta_nodos(+ArbolGen, -Num)
   es cierto si Num unifica con el número de nodos 
   de ArbolGen.
   
*/   

cuenta_nodos( a(_, ListaArboles), R2):-
  cuenta_nodos_lista_arboles(ListaArboles, R),
  R2 is R + 1.
  
  
/*

 cuenta_nodos_lista_arboles(+ListaArboles, -Num)
   es cierto si Num unifica con el número de nodos 
   de la lista de árboles genéricos ListaArboles.
   
*/   


cuenta_nodos_lista_arboles([], 0).

cuenta_nodos_lista_arboles([Cab|Resto], Total):-
  cuenta_nodos_lista_arboles(Resto, R),
  cuenta_nodos(Cab, RCa),
  Total is R + RCa.


 
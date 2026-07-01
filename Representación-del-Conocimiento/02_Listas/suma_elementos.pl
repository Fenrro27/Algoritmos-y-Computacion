% ===================================================================
% Ejercicio: suma_elementos.pl
% Descripcion: Suma todos los elementos numericos contenidos en una lista.
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================

/*

Suma de los elementos de una lista

suma_lista(+Lista, -Resultado)
  es cierto si Resultado unifica con 
  la suma de todos los elementos de Lista.
  
Lista en Prolog

[1,2,3]
n0 = []
[Cabeza|Resto] = [1,2,3]
Cabeza = 1
Resto = [2,3]  
  
Principio de Inducción

1) suma_lista([], 0).
2) suma_lista([Cabeza|Resto], R2):- suma_lista(Resto, R),
      R2 is R + Cabeza.
*/

suma_lista([], 0).
suma_lista([Cabeza|Resto], R2):- suma_lista(Resto, R),
      R2 is R + Cabeza.	  
  
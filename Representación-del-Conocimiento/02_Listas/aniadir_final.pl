% ===================================================================
% Ejercicio: aniadir_final.pl
% Descripcion: Anade un elemento al final de una lista dada.
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================

/*
  aniadir_final(+Elem, +Lista, -ListaR) 
    es cierto cuando ListaR 
    unifica con una lista que contiene los mismos elementos que
    Lista con el elemento Elem añadido al final.
*/ 

aniadir_final(Elem, [], [Elem]).
aniadir_final(Elem, [Cab|Resto], [Cab|R]):- aniadir_final(Elem, Resto, R).
% ===================================================================
% Ejercicio: elemento_mas_frecuente.pl
% Descripcion: Encuentra el elemento que aparece con mayor frecuencia en una lista.
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================

%------------------------------------------------------
% mas_veces(+Lista, -Elem, -Num)
% es cierto cuando Elem unifica con el elemento
% que se repite más veces en la lista Lista
% y Num unifica con el número de veces que se
% repite dicho elemento. En el caso en el que haya
% dos elementos con el mismo número de repeticiones
% Elem unificará con uno de ellos.
%------------------------------------------------------

mas_veces(Lista, Elem, N):- msort(Lista, ListaOrd), 
   comprimir(ListaOrd, ListaComprimida),
   mayor_n(ListaComprimida, Elem, N).

%------------------------------------------------------
% mayor_n(+ListaComprimida, -Elem, -N).
%  es cierto si Elem unifica con el elemento que se
%    repite más veces en ListaComprimida y N unifica con
%    el número de veces que se repite elem.
%    ListaComprimida tiene el siguiente formato:
%    ListaComprimida = [(a,3), (b,3), (c,1), (d,2)]
%------------------------------------------------------

mayor_n([(Elem, N)], Elem, N).
mayor_n([(Elem, N)|Resto],  Elem, N):- mayor_n(Resto, _, NMayor),
  NMayor < N.  
mayor_n([(_, N)|Resto],  Mayor, NMayor):- mayor_n(Resto, Mayor, NMayor),
  NMayor > N.
mayor_n([(_, N)|Resto],  Mayor, NMayor):- mayor_n(Resto, Mayor, NMayor),
  NMayor = N.  
   
%------------------------------------------------------
% comprimir(+Lista, -ListaR)
%  es cierto si ListaR unifica con una lista con
%  el siguiente formato:
%  comprimir([a,a,b,b,b,c,d,d,a], R)
%  R = [(a,2), (b,3), (c,1), (d,2), (a,1)]
%------------------------------------------------------

comprimir([], []).

comprimir([Elem], [(Elem,1)]).

comprimir([Cab, Cab|Resto], [(Elem,N2)|R]):- 
   comprimir([Cab|Resto], [(Elem,N)|R]), N2 is N + 1. 
   
comprimir([Cab1, Cab2|Resto], [(Cab1,1)|R]):- Cab1 \= Cab2,
   comprimir([Cab2|Resto], R).

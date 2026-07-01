;Kevin Jesus Banda Azogil
;Ejercicio 2.- Sumar los elementos de una lista de números.

(deffacts informacion-inicial
(lista 2 3 4 )
(suma 0))


(defrule sumando
?f<-(lista ?m1 $?a)
?s <- (suma ?total)
=>
(retract ?f ?s)
(assert  (lista $?a))
(assert (suma (+ ?total ?m1))
))


(defrule resultado
(lista )
(suma ?x)
=>
(printout t "La suma de los elementos del vector da como resultado:" ?x crlf))
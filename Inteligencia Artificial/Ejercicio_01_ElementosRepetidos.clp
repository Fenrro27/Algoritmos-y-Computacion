;Kevin Jesus Banda Azogil
;Ejercicio 1.- Encontrar elementos repetidos en una lista de números.

(deffacts datos-iniciales
(lista 5 1 2 2 3 5)) 

(defrule fin
(resultado $?x)
(not(nlist $?b ?m1 $?i ?m2&:(= ?m2 ?m1) $?e))
=>
(printout t "Los elementos repetidos son: " $?x t))

(defrule inicio
(lista $?a)
(not(nlista $?))
=>
(assert (nlist $?a))
(assert (resultado)))

;se podria haber hecho usando lista directamente
(defrule comprueba 
?f <- (nlist $?b ?m1 $?i ?m2&:(= ?m2 ?m1) $?e)
?f2 <- (resultado $?r)
=>
(retract ?f ?f2)
(assert (nlist $?b $?i $?e))
(assert (resultado $?r ?m1 )))

;Kevin Jesus Banda Azogil
;Ejercicio 1.- Encontrar elementos repetidos en una lista de números.

(deffacts datos-iniciales
(lista 3 3 2 1 3 3)) 

(defrule inicio
=>
(assert (resultado)))


(defrule comprueba 
?f <- (lista $?b ?m1 $?i ?m2&:(= ?m2 ?m1) $?e)
?f2 <- (resultado $?r)
(not(resultado $? ?repe&:(= ?repe ?m1) $?))
=>
(retract ?f ?f2)
(assert (lista $?b $?i $?e))
(assert (resultado $?r ?m1 )))

(defrule comprueba2
(resultado $? ?a $?)
?h<-(lista $?i ?b&:(= ?a ?b) $?d)
=>
(retract ?h)
(assert(lista $?i $?d))
)


(defrule fin
(resultado $?x)
(not(lista $?b ?m1 $?i ?m2&:(= ?m2 ?m1) $?e))
=>
(printout t "Los elementos repetidos son: " $?x crlf))

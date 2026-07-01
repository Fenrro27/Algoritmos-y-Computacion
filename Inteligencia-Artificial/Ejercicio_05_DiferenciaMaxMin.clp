;Kevin Jesus Banda Azogil
;Ejercicio 5.- Diferencia entre máximo y el mínimo de una lista de una lista de números.

(deffacts datos-iniciales
(lista 3 2 8 4))

(defrule ordena
?f <- (lista $?b ?m1 ?m2&:(< ?m2 ?m1) $?e)
=>
(retract ?f)
(assert (lista $?b ?m2 ?m1 $?e)))


(defrule Diferencia
(lista ?v1 $? ?v2)
(not (lista $?b ?m1 ?m2&:(< ?m2 ?m1) $?e))
=>
(printout t "La diferencia entre maximo y minimo es:" (- ?v2 ?v1) t))


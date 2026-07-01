(defrule ordena
?f <- (vector $?b ?m1 ?m2&:(< ?m2 ?m1) $?e)
=>
(retract ?f)
(assert (vector $?b ?m2 ?m1 $?e)))
(defrule resultado
(vector $?x)
(not (vector $?b ?m1 ?m2&:(< ?m2 ?m1) $?e))
=>
(printout t "El vector ordenado es " $?x t))

(deffacts datos-iniciales
(vector 3 2 1 4))
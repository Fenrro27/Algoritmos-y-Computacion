;Kevin Jesus Banda Azogil
;Ejercicio 4.- Resta de dos conjuntos numéricos.


(deffacts datos-iniciales
(conjunto A 2 3 5)
(conjunto B 1 2 3 4 5))

(defrule inicio
(conjunto ?a $?s)
(conjunto ?b&:(neq ?a ?b) $?c) 
(not (Resta $?))
=>
(assert (Resta $?s))
(assert (aux $?c)) ;Se usa el auxiliar para garantizar una comparativa con el conjunto contrario al seleccionado, en este caso Aux=A
)


(defrule resta
?h <- (Resta $?i ?a $?j)
(aux $? ?b&:(= ?a ?b) $?)
=>
(retract ?h)
(assert (Resta $?i $?j))
)


(defrule fin
?h<-(aux $?i ?a $?d)
(not(Resta $? ?a $?))
=>
(retract ?h)
(assert (aux $?i $?d))
)


(defrule fin2
(Resta $?resultado)
(aux)
=>
(printout t "La resta es: " $?resultado crlf))
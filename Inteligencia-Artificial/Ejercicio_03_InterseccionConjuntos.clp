;Kevin Jesus Banda Azogil
;Ejercicio 3.- Intersección de dos conjuntos numéricos.

(deffacts datos-iniciales
(conjunto A 1 2 3 4 7 8 10)
(conjunto B 10 3 1 5 4)
(interseccion))


(defrule interseccion
?h <- (interseccion $?i)
?h2 <- (conjunto ?id $?aux1 ?a $?aux2)
(conjunto ?id2&:(neq ?id ?id2) $? ?b&:(= ?a ?b) $?)
=>
(retract ?h ?h2)
(assert (conjunto ?id $?aux1 $?aux2))
(assert (interseccion $?i ?a)))

;;Crear salida por pantalla

(defrule no_interseccion
(interseccion $?)
?h <- (conjunto ?id $?aux1 ?a $?aux2)
(not(conjunto ?id2&:(neq ?id ?id2) $? ?b&:(= ?a ?b) $?))
=>
(retract ?h)
(assert (conjunto ?id $?aux1 $?aux2))
)


(defrule final2
?h<-(interseccion $?i)
(conjunto ?id)
(conjunto ?id2&:(neq ?id ?id2) $? )
=>
(retract ?h)
(printout t "La interseccion es: " $?i crlf))
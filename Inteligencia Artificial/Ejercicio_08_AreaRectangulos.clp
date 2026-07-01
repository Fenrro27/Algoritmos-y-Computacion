;AreaRectangulo_Reparado

(deftemplate rectangulo
(slot nombre)
(slot base)
(slot altura))

(deffacts informacion-inicial
(rectangulo (nombre A) (base 9) (altura 6))
(rectangulo (nombre B) (base 7) (altura 5))
(rectangulo (nombre C) (base 6) (altura 8))
(rectangulo (nombre D) (base 2) (altura 5))
(suma 0))

(defrule fin
(suma ?sum)
(not(rectangulo ))
=>
(printout t "La suma es " ?sum crlf))

(defrule suma-areas-de-rectangulos
?rec <- (rectangulo (base ?base) (altura ?altura))
?suma <- (suma ?total)
=>
(retract ?suma ?rec)
(assert (suma (+ ?total (* ?base ?altura)))))



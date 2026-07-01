;Ejercicio Palindromo
;Ejercicio De Palindromos
	(defrule comprueba
	?h <-(pali ?m1 $?b ?m2&:(= ?m1 ?m2))
	=>
	(retract ?h)
	(assert (pali $?b))
	)



;reglas que definen el resultado final
	
	(defrule resultado0 ;optimizar esto esta parte, usar negacion
	(pali $? ? ?);sobra
	(not (pali ?m1 $?b ?m2&:(= ?m1 ?m2))) ; <> es como se nega
	=>
	(printout t "No es Palindromo"t))	

	(defrule resultado1
	(pali ? )
	=>
	(printout t "Es Palindromo y fue impar el total"t))
	

;Hay que reparar esto, si no es palindromo salta-----------------------
	(defrule resultado2
	;(not (pali $? ?)) ;comprobar
	(pali ); es una alternativa a la linea de arriba.	

	=>
	(printout t "Es Palindromo y fue par el total"t))


;inicializacion de los datos
	(deffacts datos-iniciales
	(pali 5 2 2 5))
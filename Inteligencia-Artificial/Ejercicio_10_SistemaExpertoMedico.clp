;Sistema Experto--Kevin Jesus Banda Azogil

(deffacts hechos-iniciales
(Tos)
(Cansancio)
(Fiebre)
(Dolor)
)

;enfermedades-----------------------------------------
(defrule Gripe
(Tos)
(Cansancio)
(Fiebre)
(Dolor)
=>
(assert (jarabe))
(assert (contrex))
(assert (vacuna))
(printout t "Usted tiene Gripe, debe tomarse jarabe, contrex y vacuna "crlf)
)

(defrule Rubeola
(Fiebre)
(Escalofrios)
(Jaqueca)
(Secredon)
=>
(assert (vacuna))
(assert (pastilla))
(printout t "Usted tiene Rubeola, debe tomarse vacuna y pastilla "crlf)
)

(defrule Malaria
(Diarrea)
(Fiebre)
(Ictericia)
(Escalofrios)
=>
(assert (vacuna))
(printout t "Usted tiene Malaria, debe tomarse vacuna "crlf)
)

(defrule Hepatitis
(Diarrea)
(Nauseas)
(Ictericia)
=>
(assert (vacuna))
(assert (pastilla))
(printout t "Usted tiene Hepatitis, debe tomarse vacuna y pastilla "crlf)
)

(defrule Tuberculosis
(Tos)
(Cansancio)
(Fiebre)
(Escalofrios)
=>
(assert (pastilla))
(printout t "Usted tiene Tuberculosis, debe tomarse pastilla "crlf)
)

(defrule Anemia
(Cansancio)
(Nauseas)
(Apatia)
=>
(assert (vitamina))
(printout t "Usted tiene Anemia, debe tomarse vitamina "crlf)
)


;Medico-----------------------------------------------
(defrule otorrino
(jarabe)
(contrex)
=>
(printout t "Debe ir a ver al otorrino "crlf)
)

(defrule endocrino
(vacuna)
=>
(printout t "Debe ir a ver al endocrino "crlf)
)

(defrule nutricionista
(vitamina)
=>
(printout t "Debe ir a ver al Nutricionista "crlf)
)

(defrule medicogenerico
(vacuna)
(pastilla)
=>
(printout t "Debe ir a ver al medico generico "crlf)
)
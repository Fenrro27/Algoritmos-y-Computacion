% ===================================================================
% Ejercicio: run_length_encoding.pl
% Descripcion: Codifica o decodifica una secuencia de elementos usando RLE (Run-Length Encoding).
% Asignatura: Representacion del Conocimiento
% Universidad de Huelva (UHU)
% ===================================================================

/*
encode(Plaintext, Ciphertext).

decode(Ciphertext, Plaintext).

*/

encode(String, R):- string_chars(String, List), 
compress(List, R).

compress([H1, H1|Tail]):- 
  compress(Tail, [HR|R]), number_chars(N, HR),

take_number(List, )  

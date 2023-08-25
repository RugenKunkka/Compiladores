grammar id;

@header {
package compiladores;
}

//OJOOOO CON ESTOOO!!!!!!!!!!!!!!!!!!!!
//TOKENS en mayusculas...
//reglas ne minusculas....

fragment LETRA : [a-zA-Z] ;

//estas 2 reglas de abajo comentadas me molestan. tendria que preguntar como evitar que molesten o como concatenar las relas para que quede igual que letra
//fragment LETRAMINUSCULA : [a-z] ;
//fragment LETRAMAYUSCULA : [A-Z] ;


fragment DIGITO : [0-9] ;
PA : '(';
PC : ')';
TIPO_INT : 'int';
TIPO_FLOAT : 'float';
TIPO_DOUBLE : 'double';
IGUAL :'=';
FIN_DE_SENTENCIA :';';
ID_NOMBRE_VARIABLE : (LETRA|'_') (LETRA|DIGITO|'_') *;
COMA:',';

NUMERO : DIGITO+;

WS : [ \n\t\r] -> skip ;//descarta todo lo espaciado por ende no le va a interesar si el ; el = etc.. estan a continuacion o con espacio entre los unos y los otros

programa : instrucciones EOF;
//PA s PC s

declaracion_de_variable : tipo_variable ID_NOMBRE_VARIABLE asignacion  ;

tipo_variable : TIPO_INT 
              | TIPO_FLOAT 
              | TIPO_DOUBLE ;//yo genero el token con el tipo de variable

asignacion : IGUAL NUMERO asignacion // la recursividad la vas a repetir para caso en el cual sepas que se puede repetir
           | IGUAL ID_NOMBRE_VARIABLE asignacion
           | COMA ID_NOMBRE_VARIABLE asignacion
           |;


//((LETRA|DIGITO|'_') *) esto quiere decir que puede tener o no una letra o digito o _ a continuacion de la letra o _ primera... 

decalracion_y_asigancion_de_variable : declaracion_de_variable asignacion
                                     | declaracion_de_variable asignacion
                                     | ID_NOMBRE_VARIABLE asignacion
                                     | ID_NOMBRE_VARIABLE asignacion;

instrucciones : instruccion instrucciones
  |
  ;

instruccion: declaracion_de_variable FIN_DE_SENTENCIA
            ;










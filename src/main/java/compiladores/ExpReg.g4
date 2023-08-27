grammar ExpReg;

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
LLAVE_APERTURA:'{';
LLAVE_CIERRE:'}';

TIPO_INT : 'int';
TIPO_FLOAT : 'float';
TIPO_DOUBLE : 'double';
IGUAL :'=';
FIN_DE_SENTENCIA :';';
ID_NOMBRE_VARIABLE : (LETRA|'_') (LETRA|DIGITO|'_') *;
COMA:',';
MENOR:'<';
MAYOR:'>';
SA:'!';

NUMERO : DIGITO+;

//operadores
MAS:'+';
MENOS:'-';
MULTIPLICACION:'*';
DIVISION:'/';
MODULO:'%';

operadores:MAS
          |MENOS
          |MULTIPLICACION
          |DIVISION
          |MODULO
          ;

operacion: expresion operacion_ld //ID_NOMBRE_VARIABLE operacion_ld
         //| NUMERO operacion_ld
         ;

//ld es lado derecho
operacion_ld: //(operadores ID_NOMBRE_VARIABLE) operacion_ld*
            //| (operadores NUMERO) operacion_ld*
            (operadores expresion) operacion_ld*
            ;

//comparadores
CMP_IGUAL:IGUAL IGUAL;
CMP_DISTINTO:SA IGUAL;
CMP_MENOR:MENOR;
CMP_MAYOR:MAYOR;
CMP_MENOR_IGUAL:MENOR IGUAL;
CMP_MAYOR_IGUAL:MAYOR IGUAL;

comparadores:CMP_IGUAL
            |CMP_DISTINTO
            |MENOR
            |MAYOR
            |CMP_MENOR_IGUAL
            |CMP_MAYOR_IGUAL
            ;



comparacion_sin_parentesis: NUMERO comparadores NUMERO
           | ID_NOMBRE_VARIABLE comparadores NUMERO
           | ID_NOMBRE_VARIABLE comparadores ID_NOMBRE_VARIABLE
           | NUMERO comparadores ID_NOMBRE_VARIABLE
           ;
comparacion: PA comparacion_sin_parentesis PC;


/*reglas alfred aho para operaciones y le da prioridad a las multiplicaciones
deja las sumas para el ultimo por lo tanto come mas memoria xq siempre va a resolver
primero la multiplicacion por ej x=xs+2*5-4
va a leer hasta xs pero dsps va a resolver la multiplicacion y recien ahi va a parar a la suma y a la resta
*/
expresion: expresion MAS termino
         | expresion MENOS termino
         | termino
         ;

termino: termino MULTIPLICACION factor
       | termino DIVISION factor
       | termino MODULO factor
       | factor
       ;

factor: PA expresion PC
      | ID_NOMBRE_VARIABLE
      | NUMERO
      ;

//ciclos y condicionales
//la C es de custom
c_while:'while';
c_for:'for';
c_if:'if';

WS : [ \n\t\r] -> skip ;//descarta todo lo espaciado por ende no le va a interesar si el ; el = etc.. estan a continuacion o con espacio entre los unos y los otros

programa : instrucciones <EOF>;
//PA s PC s

tipo_variable : TIPO_INT 
              | TIPO_FLOAT 
              | TIPO_DOUBLE ;//yo genero el token con el tipo de variable

asignacion: ID_NOMBRE_VARIABLE IGUAL expresion
          ;

//lado derecho de la
asignacion_ld: //IGUAL NUMERO asignacion_ld // la recursividad la vas a repetir para caso en el cual sepas que se puede repetir
           //| IGUAL ID_NOMBRE_VARIABLE asignacion_ld
             IGUAL expresion asignacion_ld
           | COMA ID_NOMBRE_VARIABLE asignacion_ld
           | IGUAL operacion
           |;

declaracion_y_asigancion_de_variable : tipo_variable ID_NOMBRE_VARIABLE asignacion_ld
                                     //| ID_NOMBRE_VARIABLE asignacion_ld
                                     ;

declaracion_multiple: tipo_variable ID_NOMBRE_VARIABLE (COMA ID_NOMBRE_VARIABLE)*
                    ;

instrucciones : instruccion instrucciones
  |
  ;

instrucciones_del_for: instruccion instruccion ID_NOMBRE_VARIABLE asignacion_ld
                   ;

instruccion: declaracion_y_asigancion_de_variable FIN_DE_SENTENCIA
           | asignacion FIN_DE_SENTENCIA
           | LLAVE_APERTURA instrucciones LLAVE_CIERRE
           | PA instrucciones PC
           | c_while comparacion
           | c_if comparacion
           | c_for PA instrucciones_del_for PC
           | comparacion_sin_parentesis FIN_DE_SENTENCIA
           | operacion FIN_DE_SENTENCIA
           ;










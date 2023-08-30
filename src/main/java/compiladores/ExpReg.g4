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
ID_NOMBRE_VAR_FUNC : (LETRA|'_') (LETRA|DIGITO|'_') *;
COMA:',';
MENOR:'<';
MAYOR:'>';
SA:'!';

CA:'[';
CC:']';

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

operacion: expresion operacion_ld //ID_NOMBRE_VAR_FUNC operacion_ld
         //| NUMERO operacion_ld
         ;

//ld es lado derecho
operacion_ld: //(operadores ID_NOMBRE_VAR_FUNC) operacion_ld*
            //| (operadores NUMERO) operacion_ld*
            (operadores expresion) operacion_ld*
            ;

//inicio comparadores y comparaciones
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



comparacion_sin_parentesis: //NUMERO comparadores NUMERO
                          //| NUMERO comparadores ID_NOMBRE_VAR_FUNC
                          //| ID_NOMBRE_VAR_FUNC comparadores ID_NOMBRE_VAR_FUNC
                          //| ID_NOMBRE_VAR_FUNC comparadores NUMERO
                          expresion comparadores expresion
                          ;
comparacion: PA comparacion_sin_parentesis PC;

//fin comparadores y comparaciones


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
      | ID_NOMBRE_VAR_FUNC
      | NUMERO
      | declaracion_matriz_ld
      ;

//ciclos y condicionales
//la C es de custom
c_while:'while';
bloque_while: c_while comparacion (bloque_instrucciones | instruccion);

c_for:'for';
bloque_for: c_for PA instrucciones_del_for PC (instruccion | bloque_instrucciones);

c_if:'if';
c_elseif:'else if';
c_else:'else';

c_return:'return';

WS : [ \n\t\r] -> skip ;//descarta todo lo espaciado por ende no le va a interesar si el ; el = etc.. estan a continuacion o con espacio entre los unos y los otros


//PA s PC s

tipo_variable : TIPO_INT 
              | TIPO_FLOAT 
              | TIPO_DOUBLE ;//yo genero el token con el tipo de variable

asignacion: ID_NOMBRE_VAR_FUNC IGUAL expresion
          ;

//lado derecho de la
asignacion_ld: //IGUAL NUMERO asignacion_ld // la recursividad la vas a repetir para caso en el cual sepas que se puede repetir
           //| IGUAL ID_NOMBRE_VAR_FUNC asignacion_ld
             IGUAL expresion asignacion_ld
           | COMA ID_NOMBRE_VAR_FUNC asignacion_ld
           | IGUAL operacion
           |;

declaracion_y_asignacion_de_variable : tipo_variable ID_NOMBRE_VAR_FUNC asignacion_ld
                                     //| ID_NOMBRE_VAR_FUNC asignacion_ld
                                     ;

declaracion_multiple: tipo_variable ID_NOMBRE_VAR_FUNC (COMA ID_NOMBRE_VAR_FUNC)*
                    ;

//inicio matriz
instruccion_matriz: declaracion_matriz
                  | asignacion_matriz
                  | declaracion_y_asignacion_matriz
                  | declaracion_matriz_ld
                  ;
instruccion_matriz_forma_generica:tipo_variable? declaracion_matriz_ld (IGUAL expresion)?
                                 ;

declaracion_y_asignacion_matriz: tipo_variable asignacion_matriz
                                ;

asignacion_matriz: declaracion_matriz_ld IGUAL expresion
                  ;
declaracion_matriz: tipo_variable declaracion_matriz_ld
                  ;
declaracion_matriz_ld: ID_NOMBRE_VAR_FUNC (CA expresion CC) (CA expresion CC)?
                      ;     
//fin matriz

//inicio funciones
parametros_declaracion: variable_o_parametro_aislada COMA parametros_declaracion
                      | variable_o_parametro_aislada
                      |
                      ;

parametros_para_llamada: expresion COMA parametros_para_llamada
                       | expresion
                       |
                       ;

variable_o_parametro_aislada:tipo_variable ID_NOMBRE_VAR_FUNC
                            ;

declaracion_funcion: tipo_variable ID_NOMBRE_VAR_FUNC PA parametros_declaracion PC
                   ;
bloque_funcion: declaracion_funcion bloque_instrucciones
              ;
llamada_funcion: ID_NOMBRE_VAR_FUNC PA parametros_para_llamada PC (CA expresion CC)? (CA expresion CC)?
              ;

retorno_funcion: c_return
               | c_return expresion
               ; 
//fin funciones


instrucciones : instruccion instrucciones
  |
  ;

bloque_instrucciones: LLAVE_APERTURA instrucciones LLAVE_CIERRE
                    ;

instrucciones_del_for: instruccion instruccion ID_NOMBRE_VAR_FUNC asignacion_ld
                   ;
//antes bloque_instrucciones estaba instrucciones. consultar con el profe
bloque_if: c_if comparacion (bloque_instrucciones|instruccion) (c_elseif comparacion (bloque_instrucciones|instruccion) )* (c_else (bloque_instrucciones|instruccion))?;

COMENTARIO : '//' ~[\r\n]* -> skip;

programa : instrucciones
         | bloque_instrucciones;

//incio funciones
instruccion: 
            declaracion_funcion FIN_DE_SENTENCIA
           //realmente no hace falta este bloque funcion  pero ya veremos..
           | bloque_funcion //ver esta REGLA!!
           | retorno_funcion FIN_DE_SENTENCIA
           | llamada_funcion FIN_DE_SENTENCIA
           | COMENTARIO
           //fin de funciones
           | declaracion_y_asignacion_de_variable FIN_DE_SENTENCIA
           | asignacion FIN_DE_SENTENCIA
           //| PA instrucciones PC
           //| c_while comparacion
           | bloque_while
           //ifs else ifs else inicio
           //| c_if comparacion
           //| c_elseif comparacion
           //| c_else
           | bloque_if
           //fin ifs else ifs else inicio
           | bloque_for
           //| c_for PA instrucciones_del_for PC
           | comparacion_sin_parentesis FIN_DE_SENTENCIA
           | operacion FIN_DE_SENTENCIA
           | instruccion_matriz FIN_DE_SENTENCIA
           | expresion FIN_DE_SENTENCIA
           ;










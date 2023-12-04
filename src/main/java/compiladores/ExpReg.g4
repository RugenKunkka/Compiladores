grammar ExpReg;

@header {
package compiladores;
}

//OJOOOO CON ESTOOO!!!!!!!!!!!!!!!!!!!!
//TOKENS en mayusculas...
//reglas ne minusculas....

fragment LETRA : [a-zA-Z] ;

//estas 2 reglas de abajo comentadas me molestan. tendria que preguntar como evitar que molesten o como concatenar las relas para que quede igual que letra

fragment DIGITO : [0-9] ;

C_WHILE:'while';
C_FOR:'for';
C_RETURN:'return';

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
PUNTO:'.';

CA:'[';
CC:']';

NUMERO : DIGITO+;
NUMERO_DECIMAL : NUMERO PUNTO NUMERO;

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
operadores_de_menor_orden:MAS
                         |MENOS
                        ;
operadores_mayor_orden:MULTIPLICACION
                      |DIVISION
                      |MODULO
                      ;

operacion: expresion operacion_ld
         ;

//ld es lado derecho
operacion_ld: (operadores expresion) operacion_ld*
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



comparacion_sin_parentesis: expresion comparadores expresion
                          ;
comparacion: PA comparacion_sin_parentesis PC;

//fin comparadores y comparaciones


/*reglas alfred aho para operaciones y le da prioridad a las multiplicaciones
deja las sumas para el ultimo por lo tanto come mas memoria xq siempre va a resolver
primero la multiplicacion por ej x=xs+2*5-4
va a leer hasta xs pero dsps va a resolver la multiplicacion y recien ahi va a parar a la suma y a la resta
*/
expresion: expresion operadores_de_menor_orden termino
         | termino
         ;

termino: termino operadores_mayor_orden factor
       | factor
       ;

factor: factor_con_parentesis
      | ID_NOMBRE_VAR_FUNC
      | NUMERO
      | NUMERO_DECIMAL
      | declaracion_matriz_ld
      ;
factor_con_parentesis:PA expresion PC;

//ciclos y condicionales
//la C es de custom

bloque_while: C_WHILE comparacion (bloque_instrucciones | instruccion);

bloque_for: C_FOR PA instruccion comparacion_sin_parentesis FIN_DE_SENTENCIA actualizacion_del_for PC (instruccion | bloque_instrucciones);
actualizacion_del_for: ID_NOMBRE_VAR_FUNC MAS MAS
                     ;

c_if:'if';
c_elseif:'else if';
c_else:'else';



WS : [ \n\t\r] -> skip ;//descarta todo lo espaciado por ende no le va a interesar si el ; el = etc.. estan a continuacion o con espacio entre los unos y los otros


//PA s PC s

tipo_variable : TIPO_INT 
              | TIPO_FLOAT 
              | TIPO_DOUBLE ;//yo genero el token con el tipo de variable

asignacion:  ID_NOMBRE_VAR_FUNC IGUAL llamada_funcion
            |ID_NOMBRE_VAR_FUNC IGUAL expresion
          ;

//lado derecho de la
asignacion_ld: IGUAL expresion asignacion_ld
           | COMA ID_NOMBRE_VAR_FUNC asignacion_ld
           | COMA NUMERO asignacion_ld
           | COMA NUMERO_DECIMAL asignacion_ld
           | IGUAL operacion
           |;

declaracion_y_asignacion_de_variable : tipo_variable ID_NOMBRE_VAR_FUNC asignacion_ld
                                     ;

declaracion_multiple: tipo_variable ID_NOMBRE_VAR_FUNC (COMA ID_NOMBRE_VAR_FUNC)*
                    ;

//inicio matriz
instruccion_matriz: declaracion_matriz
                  | asignacion_matriz
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

retorno_funcion: C_RETURN
               | C_RETURN expresion
               ; 
//fin funciones


instrucciones : instruccion instrucciones
              | ;

bloque_instrucciones: LLAVE_APERTURA instrucciones LLAVE_CIERRE ;

instrucciones_del_for: instruccion comparacion ID_NOMBRE_VAR_FUNC asignacion_ld ;
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










grammar id;

@header {
package compiladores;
}

fragment LETRA : [A-Za-z] ;
fragment DIGITO : [0-9] ;

WS : [ \n\t\r] -> skip ;

PA : '(';

//asdsad

PC : ')';

si : s EOF;

s : PA s PC s
  |
  ;

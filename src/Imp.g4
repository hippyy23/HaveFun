grammar Imp;

prog : fun EOF ;

fun : (FUN ID LPAR arguments? RPAR LBRACE (com SEMICOLON)? RET exp RBRACE)* ig* com     # fundecl
    ;

ig  : GLOBAL ID ASSIGN exp SEMICOLON    # initGlobal
    ;

com : IF LPAR exp RPAR THEN LBRACE com RBRACE ELSE LBRACE com RBRACE                # if
    | ID ASSIGN exp                                                                 # assign
    | ID VGLOBAL ASSIGN exp                                                         # assignGlobal
    | SKIPP                                                                         # skip
    | com SEMICOLON com                                                             # seq
    | WHILE LPAR exp RPAR LBRACE com RBRACE                                         # while
    | OUT LPAR exp RPAR                                                             # out
    ;

exp : NAT                                 # nat
    | BOOL                                # bool
    | LPAR exp RPAR                       # parExp
    | <assoc=right> exp POW exp           # pow
    | NOT exp                             # not
    | exp op=(DIV | MUL | MOD) exp        # divMulMod
    | exp op=(PLUS | MINUS) exp           # plusMinus
    | exp op=(LT | LEQ | GEQ | GT) exp    # cmpExp
    | exp op=(EQQ | NEQ) exp              # eqExp
    | exp op=(AND | OR) exp               # logicExp
    | ID                                  # id
    | ID VGLOBAL                          # global
    | ID LPAR arguments? RPAR             # funcall
    ;

arguments : exp (COMMA exp)* ;

NAT : '0' | [1-9][0-9]* ;
BOOL : 'true' | 'false' ;

PLUS  : '+' ;
MINUS : '-';
MUL   : '*' ;
DIV   : '/' ;
MOD   : 'mod' ;
POW   : '^' ;

AND : '&' ;
OR  : '|' ;

EQQ : '==' ;
NEQ : '!=' ;
LEQ : '<=' ;
GEQ : '>=' ;
LT  : '<' ;
GT  : '>' ;
NOT : '!' ;

IF     : 'if' ;
THEN   : 'then' ;
ELSE   : 'else' ;
WHILE  : 'while' ;
SKIPP  : 'skip' ;
ASSIGN : '=' ;
OUT    : 'out' ;
FUN    : 'fun' ;
RET    : 'return ' ;
GLOBAL : 'global' ;
VGLOBAL: '.g' ;


LPAR      : '(' ;
RPAR      : ')';
LBRACE    : '{' ;
RBRACE    : '}' ;
SEMICOLON : ';' ;
COMMA     : ',' ;

ID : [a-zA-Z]+ ;

WS : [ \t\r\n]+ -> skip ;

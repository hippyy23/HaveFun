grammar Imp;

prog : com EOF ;

com : IF LPAR exp RPAR THEN LBRACE com RBRACE ELSE LBRACE com RBRACE                # if
    | ID ASSIGN exp                                                                 # assign
    | SKIPP                                                                         # skip
    | com SEMICOLON com                                                             # seq
    | WHILE LPAR exp RPAR LBRACE com RBRACE                                         # while
    | OUT LPAR exp RPAR                                                             # out
    | FUN ID LPAR ((ID)(','|){0,})* RPAR LBRACE (com SEMICOLON)? RET exp RBRACE     # fun
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
    ;

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
RET    : 'return' ;

LPAR      : '(' ;
RPAR      : ')';
LBRACE    : '{' ;
RBRACE    : '}' ;
SEMICOLON : ';' ;

ID : [a-z]+ ;

WS : [ \t\r\n]+ -> skip ;

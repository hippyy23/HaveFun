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
    | LBRACE com RBRACE ND LBRACE com RBRACE                                        # nd
    | WHILE LPAR exp RPAR LBRACE com RBRACE                                         # while
    | OUT LPAR exp RPAR                                                             # out
    | ARNOLDSTART arnoldProg ARNOLDEND                                              # arnold
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

arguments   : exp (COMMA exp)* ;

arnoldProg  : START arnoldCom* END ;

arnoldCom   : AIF arnoldExp arnoldStmnt AELSE arnoldStmnt ENDIF     # ifelse
            | VARDECL ID VARINIT arnoldExp                          # vardecl
            | VARASSIGN ID VAREXP arnoldExp arnoldStmnt+ VAREND     # varassign
            | AWHILE arnoldExp arnoldCom+ ENDWHILE                  # awhile
            | PRINT arnoldExp                                       # print
            ;

arnoldExp   : ID        # aid
            | FLOAT     # float
            | MACRO_0   # macro1
            | MACRO_1   # macro2
            ;

arnoldStmnt : APLUS arnoldExp       # aplus
            | AMINUS arnoldExp      # aminus
            | AMUL arnoldExp        # amul
            | ADIV arnoldExp        # adiv
            | AEQQ arnoldExp        # aeqq
            | AGT arnoldExp         # agt
            | AOR arnoldExp         # aor
            | AND arnoldExp         # and
            ;

ARNOLDSTART : '${' ;
ARNOLDEND   : '}$' ;
START       : 'IT\'S SHOWTIME' ;
PRINT       : 'TALK TO THE HAND' ;
VARDECL     : 'HEY CHRISTMAS TREE' ;
VARINIT     : 'YOU SET US UP' ;
VARASSIGN   : 'GET TO THE CHOPPER' ;
VAREXP      : 'HERE IS MY INVITATION' ;
VAREND      : 'ENOUGH TALK' ;
APLUS       : 'GET UP' ;
AMINUS      : 'GET DOWN ' ;
AMUL        : 'YOU\'RE FIRED' ;
ADIV        : 'HE HAD TO SPLIT' ;
AEQQ        : 'YOU ARE NOT YOU YOU ARE ME' ;
AGT         : 'LET OFF SOME STEAM BENNET' ;
AOR         : 'CONSIDER THAT A DIVORCE' ;
AAND        : 'KNOCK KNOCK' ;
MACRO_0     : '@I LIED' ;
MACRO_1     : '@NO PROBLEMO' ;
AIF         : 'BECAUSE I\'M GOING TO SAY PLEASE' ;
AELSE       : 'BULLSHIT' ;
ENDIF       : 'YOU HAVE NO RESPECT FOR LOGIC' ;
AWHILE      : 'STICK AROUND' ;
ENDWHILE    : 'CHILL' ;
END         : 'YOU HAVE BEEN TERMINATED' ;

FLOAT   : [0-9]*('.'[0-9]) ;
NAT     : '0' | [1-9][0-9]* ;
BOOL    : 'true' | 'false' ;

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
ND     : ' nd ' ;


LPAR      : '(' ;
RPAR      : ')';
LBRACE    : '{' ;
RBRACE    : '}' ;
SEMICOLON : ';' ;
COMMA     : ',' ;

ID : [a-zA-Z]+[0-9]* ;

WS : [ \t\r\n]+ -> skip ;

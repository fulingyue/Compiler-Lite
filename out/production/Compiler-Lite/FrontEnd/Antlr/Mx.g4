grammar Mx;

//////////////////////////////////parser///////////////////////////////////

program:
// the whole program is the set of class,function and glabal variable definition
(classDefinition |functionDefinition | variableDefinition)* ;

classDefinition:
    /*
    class Example{
        int x;
        Example(){};
        void print(){};
     }
    */
    CLASS Identifier LBRACE
        (memberVariable | constructionFunctionDefinition | functionDefinition)*
    RBRACE SEMI
    ;

    memberVariable:
    //int x;
         variableDefinition
    ;

    constructionFunctionDefinition:
    /* Example(int x,...) {}
    */
        Identifier LPAREN formalParameterList? RPAREN block
        ;


functionDefinition:
/* void ExampleFun(int exp = 0, int ...){
        int tmp = 0;
        ....
    }
*/
    variableType Identifier
        LPAREN formalParameterList? RPAREN block
    ;

        formalParameterList:
            formalParameter (COMMA formalParameter)*
            ;

            formalParameter:
                variableType Identifier (ASSIGN expression)?
                ;



block:
/* {....}
*/
    LBRACE statementList RBRACE
    ;

    statementList:
        statement *
        ;


variableDefinition:
/* int v = 0, b = 1;
*/
    variableType variableDeclarExprs SEMI
    ;

    variableType:
        baseType (LBRACK RBRACK)*
    ;
        baseType:
            primitiveType | classType
        ;

    variableDeclarExprs:
        variableDeclarExpr (COMMA variableDeclarExpr)*
    ;
        variableDeclarExpr:
            Identifier (ASSIGN expression)?
       ;


statement:
    expression SEMI # expressionStat
    | IF LPAREN expression RPAREN statement (ELSE statement)? # ifStat
    | FOR LPAREN init=expression? SEMI condition=expression? SEMI after_block=expression? RPAREN statement # forStat
    | WHILE LPAREN expression RPAREN statement # whileStat
    | RETURN expression? SEMI # returnStat
    | BREAK SEMI # breakStat
    | CONTINUE SEMI # continueStat
    | SEMI # emptyStat
    | variableDefinition # definitionStat
    | block # blockStat
    ;



expression:
    caller=expression LPAREN actualParameterList? RPAREN # functionCallExpr
    | caller=expression op=DOT member=expression # memberAccessExpr
    | caller=expression LBRACK index=expression RBRACK # indexAccessExpr

    | expression postfix=(INC | DEC) # unaryExpr
    | prefix=(INC | DEC) expression # unaryExpr
    | prefix=(ADD | SUB) expression # unaryExpr
    | prefix=(NOT | LNOT) expression # unaryExpr

    | NEW creator # newExpr

    | lhs=expression op=(MUL | DIV | MOD) rhs=expression # binaryExpr
    | lhs=expression op=(ADD | SUB) rhs=expression # binaryExpr
    | lhs=expression op=(LSHIFT | RSHIFT) rhs=expression # binaryExpr
    | lhs=expression op=(LE | GE | LT | GT) rhs=expression # binaryExpr
    | lhs=expression op=(EQUAL | NOTEQUAL) rhs=expression # binaryExpr
    | lhs=expression op=AND rhs=expression # binaryExpr
    | lhs=expression op=XOR rhs=expression # binaryExpr
    | lhs=expression op=OR rhs=expression # binaryExpr
    | lhs=expression op=LAND rhs=expression # binaryExpr
    | lhs=expression op=LOR rhs=expression # binaryExpr
    | lhs=expression op=ASSIGN rhs=expression # binaryExpr

     // primitive
    | Identifier # identifierExpr
    | BoolConstant # primaryExpr
    | IntegerConstant # primaryExpr
    | StringConstant # primaryExpr
    | NullConstant # primaryExpr
    | THIS # thisExpr
    | LPAREN expression RPAREN # parensExpr
    ;


    creator:
        baseType (LBRACK expression RBRACK)+ (LBRACK RBRACK)+ (LBRACK expression RBRACK)+     #errorCreator
            | baseType ((LBRACK expression RBRACK)* (LBRACK RBRACK)* | (LPAREN RPAREN))         #correctCreator
    ;
    //creator don't have a parameter

    actualParameterList:
        expression (COMMA expression)*
    ;




// ------------------ lexer ----------------------

LPAREN : '(';
RPAREN : ')';
LBRACE : '{';
RBRACE : '}';
LBRACK : '[';
RBRACK : ']';
SEMI : ';';
COMMA : ',';
DOT : '.';
ASSIGN : '=';
INC : '++';
DEC : '--';
ADD : '+';
SUB : '-';
MUL : '*';
DIV : '/';
MOD : '%';
LAND : '&&';
LOR : '||';
LNOT : '!';
LSHIFT : '<<';
RSHIFT : '>>';
NOT : '~';
OR : '|';
AND : '&';
XOR : '^';
LT : '<';
GT : '>';
EQUAL : '==';
LE : '<=';
GE : '>=';
NOTEQUAL : '!=';

IF : 'if';
ELSE : 'else';
FOR : 'for';
WHILE : 'while';
BREAK : 'break';
CONTINUE : 'continue';
RETURN : 'return';
CLASS : 'class';
NEW : 'new';
THIS : 'this';



primitiveType:
    BOOL | INT | VOID //| STRING
;

    BOOL : 'bool';
    INT : 'int';
    //STRING : 'string';
    VOID : 'void';

classType:
    token = Identifier
;


BoolConstant:
   'true' | 'false'
   ;

IntegerConstant
   : DecimalConstant
   ;

    fragment
    DecimalConstant
        : [1-9] [0-9]*
        | '0'
        ;

StringConstant
    : '"' (~["\\\r\n] | EscapeSequence)* '"'
    ;
        fragment
        EscapeSequence
            : '\\' [btnfr"'\\]
            ;

NullConstant: 'null';


Identifier: [a-zA-Z] [0-9a-zA-Z_]*;

WhiteSpace: [ \t]+ -> skip;

NewLine: [\n\r]+ -> skip;

LineComment: '//' ~[\r\n]* -> skip;

BlockComment: '/*' .*? '*/' -> skip;


















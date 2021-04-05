lexer grammar MyLangLexer;

// Whitespace
NEWLINE : '\r\n' | '\r' | '\n' ;
WS      : [t ]+ -> skip ;

// Curly brackets
LCURLY : '{' ;
RCURLY : '}' ;

// Keywords
PRINT : 'print' ;
GOTO  : 'goto' ;
FOR   : 'for' ;
WHILE : 'while' ;
IF    : 'if' ;
ELSE  : 'else' ;

// Types
INTTYPE     : 'int' ;
STRINGTYPE  : 'string' ;

// Operators
PLUS        : '+' ;
MINUS       : '-' ;
ASTERISK    : '*' ;
DIVISION    : '/' ;
PERCENT     : '%' ;
ASSIGN      : '=' ;
LPAREN      : '(' ;
RPAREN      : ')' ;
NOT         : '!' ;
GREATER     : '>' ;
LESS        : '<' ;
EQUAL       : '==' ;
NOTEQUAL    : '!=' ;
GREATEROREQ : '>=' ;
LESSOREQ    : '<=' ;
LABEL       : ':' ;
AND : '&&' ;
OR : '||' ;

// Literals
INTLIT             : '0'|[1-9][0-9]* ;
STRINGLIT
  : UNTERMINATEDSTRING '"'
  ;
UNTERMINATEDSTRING
  : '"' (~["\\\r\n] | '\\' (. | EOF))*
  ;

// Identifiers
ID : [a-zA-Z_][a-zA-Z0-9_]* ;
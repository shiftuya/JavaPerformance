parser grammar MyLangParser;


options { tokenVocab=MyLangLexer; }

code : lines=line+ ;

codeBlock : LCURLY NEWLINE code RCURLY ;

ifclause : IF LPAREN logicalExpression RPAREN codeBlock ;

labelDeclaration : INTLIT LABEL ;

line : statement (NEWLINE | EOF)              # nonEmptyLine
| NEWLINE                                     # emptyLine ;

varDeclaration : INTTYPE assignment           # intDeclaration
| STRINGTYPE assignment                       # stringDeclaration ;

assignment : ID ASSIGN arithmeticExpression   # intAssignment
| ID ASSIGN stringExpression                  # stringAssignment ;

print : PRINT stringExpression                # printString
| PRINT arithmeticExpression                  # printArithmetic
| PRINT logicalExpression                     # printLogical ;

gotoExpression : GOTO INTLIT ;

arithmeticExpression :
LPAREN arithmeticExpression binaryArithmeticSign arithmeticExpression RPAREN # binaryArithmetic
| INTLIT                                                                     # intLiteral
| ID                                                                         # varReference
| LPAREN arithmeticExpression RPAREN                                         # parenExpression
| MINUS arithmeticExpression                                                 # minusExpression
;

stringExpression :
STRINGLIT                         # stringLiteral
| STRINGLIT PLUS stringExpression # stringConcat
//| ID                              # stringVarReference
;


logicalExpression :
arithmeticExpression comparisonSign arithmeticExpression            # comparison
| LPAREN logicalExpression binaryLogicSign logicalExpression RPAREN # binaryLogic
| NOT LPAREN logicalExpression RPAREN                               # negation ;

statement : assignment      # assignmentStatement
| varDeclaration            # declarationStatement
| print                     # printStatement
| gotoExpression            # gotoStatement
| ifclause                  # ifStatement
| labelDeclaration          # labelStatement;

binaryArithmeticSign : PLUS | MINUS | ASTERISK | DIVISION ;

comparisonSign : EQUAL
| NOTEQUAL
| GREATER
| GREATEROREQ
| LESS
| LESSOREQ ;

binaryLogicSign : AND | OR ;
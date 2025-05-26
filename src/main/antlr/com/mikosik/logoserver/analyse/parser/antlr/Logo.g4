grammar Logo;

@ header
{
package com.mikosik.logoserver.analyse.parser.antlr;
}
document
   : statement*
   ;

statement
   : command
   | call
   ;

expr
   : primaryExpr (('=' | '<' | '>' | '+' | '-' | '*' | '/') primaryExpr)*
   ;

primaryExpr
   : NUMBER
   | WORD
   | query
   | list
   | listliteral
   ;

call
   : NAME expr*
   ;

command
   // motion commands
   : forward
   | back
   | left
   | right
   | home
   | setx
   | sety
   | setxy
   | setxy2
   | setheading
   | arc
   | ellipse
   // turtle and window control
   | showturtle
   | hideturtle
   | clean
   | clearscreen
   | fill
   | filled
   | label
   | setlabelheight
   | wrap
   | window
   | fence
   // pend and background control
   | penup
   | pendown
   | setcolor
   | setwidth
   | changeshape
   // procedure definition
   | to
   | define
   // variable definition
   | makevar
   | namevar
   | localmake
   // control structures
   | repeat
   | for
   | repcount
   | if
   | ifelse
   | test
   | iftrue
   | iffalse
   | waitx
   | bye
   | dotimes
   | dowhile
   | while
   | dountil
   | until
   ;

query
   // turtle motion queries
   : pos
   | xcor
   | ycor
   | heading
   | towards
   // turtle and window queries
   | shownp
   | labelsize
   // pen queries
   | pendownp
   | pencolor
   | pensize
   // procedure definition query
   | def
   // variable query
   | thing
   | thingshort
   // lists
   | first
   | butfirst
   | last
   | butlast
   | item
   | pick
   // math
   | sum
   | minus
   | random
   | modulo
   | power
   // receivers
   | readword
   | readlist
   // predicates
   | wordp
   | listp
   | arrayp
   | numberp
   | emptyp
   | equalp
   | notequalp
   | beforep
   | substringp
   ;

forward
   : cmd = ('forward' | 'fd') expr
   ;

back
   : cmd = ('back' | 'bk') expr
   ;

left
   : cmd = ('left' | 'lt') expr
   ;

right
   : cmd = ('right' | 'rt') expr
   ;

home
   : cmd = 'home'
   ;

setx
   : cmd = 'setx' expr
   ;

sety
   : cmd = 'sety' expr
   ;

setxy
   : cmd = 'setxy' expr expr
   ;

setxy2
   : cmd = 'set' subcmd = 'pos' '[' expr expr ']'
   ;

setheading
   : cmd = ('setheading' | 'seth') expr
   ;

arc
   : cmd = 'arc' expr expr
   ;

ellipse
   : cmd = 'ellipse' expr expr
   ;

pos
   : cmd = 'pos'
   ;

xcor
   : cmd = 'xcor'
   ;

ycor
   : cmd = 'ycor'
   ;

heading
   : cmd = 'heading'
   ;

towards
   : cmd = 'towards'
   ;

showturtle
   : cmd = ('showturtle' | 'st')
   ;

hideturtle
   : cmd = ('hideturtle' | 'ht')
   ;

clean
   : cmd = 'clean'
   ;

clearscreen
   : cmd = ('clearscreen' | 'cs')
   ;

fill
   : cmd = 'fill'
   ;

filled
   : cmd = 'filled' expr '[' statement* ']'
   ;

label
   : cmd = ('label' | 'print' | 'show') expr
   ;

setlabelheight
   : cmd = 'setlabelheight' expr
   ;

wrap
   : cmd = 'wrap'
   ;

window
   : cmd = 'window'
   ;

fence
   : cmd = 'fence'
   ;

shownp
   : cmd = ('shownp' | 'shown?')
   ;

labelsize
   : cmd = 'labelsize'
   ;

penup
   : cmd = ('penup' | 'pu')
   ;

pendown
   : cmd = ('pendown' | 'pd')
   ;

setcolor
   : cmd = ('setcolor' | 'setpencolor') expr
   ;

setwidth
   : cmd = ('setwidth' | 'setpensize') expr
   ;

changeshape
   : cmd = ('changeshape' | 'csh') expr
   ;

pendownp
   : cmd = ('pendownp' | 'pendown?')
   ;

pencolor
   : cmd = ('pencolor' | 'pc')
   ;

pensize
   : cmd = 'pensize'
   ;

to
   : cmd = 'to' NAME COLON_NAME* statement* end = 'end'
   ;

define
   : cmd = 'define' procname = WORD '[' '[' inputs += NAME* ']' ('[' statement ']')* ']' end = 'end'
   ;

def
   : cmd = 'def' expr
   ;

makevar
   : cmd = 'make' WORD expr
   ;

namevar
   : cmd = 'name' expr WORD
   ;

localmake
   : cmd = 'localmake' WORD expr
   ;

thing
   : cmd = 'thing' expr
   ;

thingshort
   : COLON_NAME
   ;

repeat
   : cmd = 'repeat' expr '[' statement* ']'
   ;

for
   : cmd = 'for' '[' localvar = NAME startvalue = expr limitvalue = expr (stepsize = expr)? ']' '[' statement* ']'
   ;

repcount
   : cmd = 'repcount'
   ;

if
   : cmd = 'if' expr '[' statement* ']'
   ;

ifelse
   : cmd = 'ifelse' expr '[' statement* ']' '[' statement* ']'
   ;

test
   : cmd = 'test' expr
   ;

iftrue
   : cmd = 'iftrue' '[' statement* ']'
   ;

iffalse
   : cmd = 'iffalse' '[' statement* ']'
   ;

waitx
   : cmd = 'wait' expr
   ;

bye
   : cmd = 'bye'
   ;

dotimes
   : cmd = 'dotimes' '[' NAME expr ']' '[' statement* ']'
   ;

dowhile
   : cmd = 'do.while' '[' statement* ']' expr
   ;

while
   : cmd = 'while' expr '[' statement* ']'
   ;

dountil
   : cmd = 'do.until' '[' statement* ']' expr
   ;

until
   : cmd = 'until' expr '[' statement* ']'
   ;

list
   : '(' cmd = 'list' expr* ')'
   ;

listliteral
   : '[' expr* ']'
   ;

first
   : cmd = 'first' expr
   ;

butfirst
   : cmd = 'butfirst' expr
   ;

last
   : cmd = 'last' expr
   ;

butlast
   : cmd = 'butlast' expr
   ;

item
   : cmd = 'item' expr expr
   ;

pick
   : cmd = 'pick' expr
   ;

sum
   : cmd = 'sum' expr expr
   ;

minus
   : cmd = 'minus' expr expr
   ;

random
   : cmd = 'random' expr expr
   ;

modulo
   : cmd = 'modulo' expr expr
   ;

power
   : cmd = 'power' expr expr
   ;

readword
   : '(' cmd = 'readword' '[' NAME* ']' ')'
   ;

readlist
   : '(' cmd = 'readlist' '[' NAME* ']' ')'
   ;

wordp
   : cmd = ('wordp' | 'word?') expr
   ;

listp
   : cmd = ('listp' | 'list?') expr
   ;

arrayp
   : cmd = ('arrayp' | 'array?') expr
   ;

numberp
   : cmd = ('numberp' | 'number?') expr
   ;

emptyp
   : cmd = ('emptyp' | 'empty?') expr
   ;

equalp
   : cmd = ('equalp' | 'equal?') expr expr
   ;

notequalp
   : cmd = ('notequalp' | 'notequal?') expr expr
   ;

beforep
   : cmd = ('beforep' | 'before?') expr expr
   ;

substringp
   : cmd = ('substringp' | 'substring?') expr expr
   ;

COLON_NAME
   : ':' NAME_FRAGMENT
   ;

WORD
   : '"' NAME_FRAGMENT
   ;

NAME
   : NAME_FRAGMENT
   ;

fragment NAME_FRAGMENT
   : LETTER (LETTER | DIGIT | '_')*
   ;

NUMBER
   : '-'? DIGIT+ ('.' DIGIT)?
   | '-'? '.' DIGIT+
   ;

fragment DIGIT
   : '0' .. '9'
   ;

fragment LETTER
   : 'a' .. 'z'
   | 'A' .. 'Z'
   ;

WS
   : [ \t\n\r]+ -> skip
   ;


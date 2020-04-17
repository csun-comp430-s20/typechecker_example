# Typechecker Example #

This is the typechecker we've been working on in class.
The grammar is below:

```
x is a variable
i is an integer
b is a boolean value (true or false)
e is an expression
s is a statement
t is a type
p is a program
f is a first-order function definition
fn is a first-order function name
fp is a formal parameter
bop is a binary operator (takes two arguments)

t ::= int | bool | t1 => t2
e ::= x| i | b | e1 bop e2 | (x: t) => e | e1(e2) | fn(e*)
bop ::= < | + | &&
s ::= empty | continue | break | let x: t = e | x = e |
      for (s1; e; s2) { s* }
fp ::= t x
f ::= t fn(fp*) { s* return e }
p ::= f*
```

package typechecker_example.syntax;

import java.util.List;

// int main() {
//   ...
// }
//
// p ::= firstOrderFunction*

public class Program {
    public final List<FirstOrderFunctionDefinition> functions;
    
    public Program(final List<FirstOrderFunctionDefinition> functions) {
        this.functions = functions;
    }
}


//                   gamma
// initial           []
// int x = 3;        [x -> int]
// {
//   bool x = true;  [x -> bool]
// }                 // ---nested gamma discarded---
// int y = x;        [x -> int, y -> int]
//

import java.util.List;

// p ::= s*

public class Program {
    public final List<Stmt> statements;
    
    public Program(final List<Stmt> statements) {
        this.statements = statements;
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

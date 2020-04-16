package typechecker_example.syntax;

// for(int x = 0; x < 10; x++) { ... }
//
// for(s1; e; s2) { ... }
//
// for (;true;) {
//   ...
// }

import java.util.List;

public class ForStmt implements Stmt {
    public final Stmt initializer;
    public final Exp guard;
    public final Stmt update;
    public final List<Stmt> body;

    public ForStmt(final Stmt initializer,
                   final Exp guard,
                   final Stmt update,
                   final List<Stmt> body) {
        this.initializer = initializer;
        this.guard = guard;
        this.update = update;
        this.body = body;
    }
}

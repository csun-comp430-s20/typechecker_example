package typechecker_example.syntax;

// e \in Exp ::= x | i | true | false | e1 op e2
// op \in BOP ::= && | + | <

// binary operation
public class BinopExp implements Exp {
    public final Exp left;
    public final BOP op;
    public final Exp right;

    public BinopExp(final Exp left,
                    final BOP op,
                    final Exp right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }
}


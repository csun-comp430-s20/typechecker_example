public class LetStmt implements Stmt {
    public final Variable x;
    public final Type tau;
    public final Exp e;

    public LetStmt(final Variable x,
                   final Type tau,
                   final Exp e) {
        this.x = x;
        this.tau = tau;
        this.e = e;
    }
}


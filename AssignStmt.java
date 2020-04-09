public class AssignStmt implements Stmt {
    public final Variable x;
    public final Exp e;

    public AssignStmt(final Variable x,
                      final Exp e) {
        this.x = x;
        this.e = e;
    }
}

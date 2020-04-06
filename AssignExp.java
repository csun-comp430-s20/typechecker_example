public class AssignExp implements Exp {
    public final Variable x;
    public final Exp e1;
    public final Exp e2;

    public AssignExp(final Variable x,
                     final Exp e1,
                     final Exp e2) {
        this.x = x;
        this.e1 = e1;
        this.e2 = e2;
    }
}

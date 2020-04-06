public class LetExp implements Exp {
    public final Variable x;
    public final Type tau;
    public final Exp e1;
    public final Exp e2;

    public LetExp(final Variable x,
                  final Type tau,
                  final Exp e1,
                  final Exp e2) {
        this.x = x;
        this.tau = tau;
        this.e1 = e1;
        this.e2 = e2;
    }
}
    

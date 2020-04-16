package typechecker_example.syntax;

public class VariableExp implements Exp {
    public final Variable x;

    public VariableExp(final Variable x) {
        this.x = x;
    }
}

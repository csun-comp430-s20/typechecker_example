package typechecker_example.syntax;

public class IntegerExp implements Exp {
    public final int value;

    public IntegerExp(final int value) {
        this.value = value;
    }
}

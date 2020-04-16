package typechecker_example.syntax;

// TrueExp; new BooleanExp(true)
// FalseExp; new BooleanExp(false)
public class BooleanExp implements Exp {
    public final boolean value;

    public BooleanExp(final boolean value) {
        this.value = value;
    }
}

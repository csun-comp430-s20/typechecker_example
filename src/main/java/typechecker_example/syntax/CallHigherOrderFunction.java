package typechecker_example.syntax;

// e1(e2)
// f(1)
// ((x : Int) => x + 1)(3 + 2)
// 1(2)
public class CallHigherOrderFunction implements Exp {
    public final Exp theFunction;
    public final Exp theParameter;

    public CallHigherOrderFunction(final Exp theFunction,
                                   final Exp theParameter) {
        this.theFunction = theFunction;
        this.theParameter = theParameter;
    }
} // CallHigherOrderFunction


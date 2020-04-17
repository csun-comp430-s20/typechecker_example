package typechecker_example.syntax;

import java.util.List;

public class CallFirstOrderFunction implements Exp {
    public final FunctionName functionName;
    public final List<Exp> actualParams;

    public CallFirstOrderFunction(final FunctionName functionName,
                                  final List<Exp> actualParams) {
        this.functionName = functionName;
        this.actualParams = actualParams;
    }
} // CallFirstOrderFunction

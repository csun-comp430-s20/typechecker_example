// "foo" + "bar" = "foobar"
// 1 + 2 = 3
// "foo" + 1 = "foo1"
//
// (x: String) => x + 1
// (x: Int) => x + 1
//
public class HigherOrderFunctionDef implements Exp {
    public final Variable paramName;
    public final Type paramType;
    public final Exp body;

    public HigherOrderFunctionDef(final Variable paramName,
                                  final Type paramType,
                                  final Exp body) {
        this.paramName = paramName;
        this.paramType = paramType;
        this.body = body;
    }
} // HigherOrderFunctionDef

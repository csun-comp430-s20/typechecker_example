//          formal parameters
// bool foo(int x, int y) { return x < y; }
//
//     actual parameters
// foo(1, 2);
//
// firstOrderFunction ::= type fname '(' (type variableName)* ') '{' stmt* '}'

import java.util.List;

public class FirstOrderFunctionDefinition {
    public final Type returnType;
    public final FunctionName name;
    public final List<FormalParameter> formalParams;
    public final List<Stmt> body;

    public FirstOrderFunctionDefinition(final Type returnType,
                                        final FunctionName name,
                                        final List<FormalParameter> formalParams,
                                        final List<Stmt> body) {
        this.returnType = returnType;
        this.name = name;
        this.formalParams = formalParams;
        this.body = body;
    }
}

// (int => int) foo(int x) {
//   // bar acts like a toplevel function...
//   // except it's only available in foo
//   int bar(int y) {
//     return x + y;
//   }
//   return bar;
// }

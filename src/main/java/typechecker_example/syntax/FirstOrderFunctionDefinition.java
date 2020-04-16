package typechecker_example.syntax;

//          formal parameters
// bool foo(int x, int y) { s*; return x < y; }
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
    // simplification: return can only be at the end of the function,
    // and we must always return a value.  There is no void.
    public final Exp returnExp;
    
    public FirstOrderFunctionDefinition(final Type returnType,
                                        final FunctionName name,
                                        final List<FormalParameter> formalParams,
                                        final List<Stmt> body,
                                        final Exp returnExp) {
        this.returnType = returnType;
        this.name = name;
        this.formalParams = formalParams;
        this.body = body;
        this.returnExp = returnExp;
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

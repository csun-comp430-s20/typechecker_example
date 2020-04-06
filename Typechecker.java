import java.util.Map;

public class Typechecker {
    // int bar(int x) { ... }
    //
    // int x = bar(1);
    //
    //    vardec
    //    /  |  \
    //  int  x  call
    //          /  \
    //        bar   1
    //
    //                  gamma (returned)          gamma (parameter)
    //                  []                        []
    // int x = 3;       [x -> int]                []
    // int y = x + x;   [x -> int, y -> int]      [x -> int]
    //
    //                    gamma (returned)                   gamma (parameter)
    //                    []                                 []
    // int x = 3;         [x -> int]                         []
    // {                                                     [x -> int]
    //   int y = x + x;   [x -> int, y -> int]               [x -> int]
    //   int z = y + y;   [x -> int, y -> int, z -> int]     [x -> int, y -> int]
    // }                  [x -> int, y -> int, z -> int]
    // int a = x + x;     [x -> int, a -> int]               [x -> int]
    
    public static Map<Variable, Type> typecheckStmt(final Map<Variable, Type> gamma,
                                                    final Stmt s) throws IllTypedException { ... }

    // typeof(Gamma, e2) == BoolType
    public static Type typeof(final Map<Variable, Type> gamma, final Exp e) throws IllTypedException {
        if (e instanceof IntegerExp) {
            return new IntType();
        } else if (e instanceof BooleanExp) {
            return new BoolType();
        } else if (e instanceof BinopExp) { // &&, +, or <
            final BinopExp asBinop = (BinopExp)e;
            if (asBinop.op instanceof AndBOP) {
                final Type leftType = typeof(gamma, asBinop.left);
                final Type rightType = typeof(gamma, asBinop.right);

                if (leftType instanceof BoolType &&
                    rightType instanceof BoolType) {
                    return new BoolType();
                } else {
                    throw new IllTypedException("left or right in && is not a boolean");
                }
            } else if (asBinop.op instanceof PlusBOP) {
                final Type leftType = typeof(gamma, asBinop.left);
                final Type rightType = typeof(gamma, asBinop.right);

                if (leftType instanceof IntType &&
                    rightType instanceof IntType) {
                    return new IntType();
                } else {
                    throw new IllTypedException("left or right in + is not an int");
                }
            } else if (asBinop.op instanceof LessThanBOP) {
                final Type leftType = typeof(gamma, asBinop.left);
                final Type rightType = typeof(gamma, asBinop.right);
                
                if (leftType instanceof IntType &&
                    rightType instanceof IntType) {
                    return new BoolType();
                } else {
                    throw new IllTypedException("left or right in < is not an int");
                }
            } else {
                assert(false);
                throw new IllTypedException("should be unreachable; unknown operator");
            }
        } else if (e instanceof VariableExp) {
            // final Map<Variable, Type> gamma
            final VariableExp asVar = (VariableExp)e;
            if (gamma.containsKey(asVar.x)) {
                final Type tau = gamma.get(asVar.x);
                return tau;
            } else {
                throw new IllTypedException("Not in scope: " + asVar.x);
            }
        } else if (e instanceof LetExp) {
            //              e1   e2
            // let x: int = 3 in x + x
            // let y: int = 4 in true
            // let z: bool = 5 in ...
            final LetExp asLet = (LetExp)e;
            if (typeof(gamma, asLet.e1).equals(asLet.tau)) {
                final Map<Variable, Type> copy = (Map<Variable, Type>)gamma.clone();
                copy.put(asLet.x, asLet.tau);
                final Type tau2 = typeof(copy, asLet.e2);
                return tau2;
            } else {
                throw new IllTypedException("type mismatch with let");
            }
        } else if (e instanceof AssignExp) {
            // let x: int = 3 in
            //   assign x = false in
            //     true

            final AssignExp asAssign = (AssignExp)e;
            if (gamma.containsKey(asAssign.x)) {
                final Type tau1 = gamma.get(asAssign.x);
                if (typeof(gamma, asAssign.e1).equals(tau1)) {
                    final Type tau2 = typeof(gamma, asAssign.e2);
                    return tau2;
                } else {
                    throw new IllTypedException("type mismatch on assign: putting wrong type inside");
                }
            } else {
                throw new IllTypedException("variable not in scope");
            }
        } else {
            assert(false);
            throw new IllTypedException("unrecognized expression");
        }
    } // typeof
} // Typechecker

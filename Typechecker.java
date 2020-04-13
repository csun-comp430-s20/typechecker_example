import java.util.Map;
import java.util.HashMap;

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

    public static Map<Variable, Type> makeCopy(final Map<Variable, Type> gamma) {
        final Map<Variable, Type> copy = new HashMap<Variable, Type>();
        copy.putAll(gamma);
        return copy;
    }

    public static void typecheckProgram(final Program program) throws IllTypedException {
        typecheckStmts(new HashMap<Variable, Type>(),
                       false,
                       program.statements);
    } // typecheckProgram
    
    public static Map<Variable, Type> typecheckStmts(
                          Map<Variable, Type> gamma,
                          final boolean breakAndContinueOk,
                          final List<Stmt> stmts) throws IllTypedException {
        for (final Stmt s : stmts) {
            //                  result gamma
            // initial          []
            // int x = 7;       [x -> int]
            // int y = x + 3;   [x -> int, y -> int]
            // int z = y + x;   [x -> int, y -> int, z -> int]
            gamma = typecheckStmt(gamma, breakAndContinueOk, s);
        }

        return gamma;
    }
        
    public static Map<Variable, Type> typecheckStmt(
                          final Map<Variable, Type> gamma,
                          final boolean breakAndContinueOk,
                          final Stmt s) throws IllTypedException {
        // x
        if (s instanceof LetStmt) {
            //     x  tau   e
            // let x: int = 3
            // let y: int = x + x
            //
            // let z: int = bool
            final LetStmt asLet = (LetStmt)s;
            if (typeof(gamma, asLet.e).equals(asLet.tau)) {
                final Map<Variable, Type> copy = makeCopy(gamma);
                copy.put(asLet.x, asLet.tau);
                return copy;
            } else {
                throw new IllTypedException("type mismatch in let");
            }
        } else if (s instanceof AssignStmt) {
            // int int
            // x = 1 + 2
            final AssignStmt asAssign = (AssignStmt)s;
            if (gamma.containsKey(asAssign.x)) {
                final Type variableType = gamma.get(asAssign.x);
                if (typeof(gamma, asAssign.e).equals(variableType)) {
                    return gamma;
                } else {
                    throw new IllTypedException("Assigned something of wrong type");
                }
            } else {
                throw new IllTypedException("Assigning to variable not in scope");
            }
        } else if (s instanceof BreakStmt) {
            if (!breakAndContinueOk) {
                throw new IllTypedException("break outside of a loop");
            }
        } else if (s instanceof ForStmt) {
            // for(int x = 0; x < 10; x++) { s* }
            // gamma: []
            // newGamma: [x -> int]
            // for(int x = 0; x < 10; int y = 10) {
            //   int y = 0;
            //   int z = x + y;
            //   [x -> int, y -> int, z -> int]
            // }
            final ForStmt asFor = (ForStmt)s;
            final Map<Variable, Type> newGamma = typecheckStmt(gamma, asFor.initializer);
            final Type guardType = typeof(newGamma, asFor.guard);
            if (guardType instanceof BoolType) {
                typecheckStmt(newGamma, asFor.update);
                typecheckStmts(newGamma, true, asFor.body);
            }
            return gamma;
        } else {
            assert(false);
            throw new IllTypedException("Unrecognized statement");
        }
    } // typecheckStmt

    // typeof(Gamma, e2) == BoolType
    public static Type typeof(final Map<Variable, Type> gamma,
                              final Exp e) throws IllTypedException {
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
        } else {
            assert(false);
            throw new IllTypedException("unrecognized expression");
        }
    } // typeof
} // Typechecker

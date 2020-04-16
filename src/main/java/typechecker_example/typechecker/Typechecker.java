package typechecker_example.typechecker;

import typechecker_example.syntax.*;

import java.util.Map;
import java.util.List;
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

    private final Map<FunctionName, FirstOrderFunctionDefinition> functionDefinitions;

    // int bar(int x) {
    //   return x;
    // }
    //
    // String bar(String y) {
    //   return y + "blah";
    // }
    //
    // let x: int = bar(7);
    //
    // Takes function definitions which will be in scope, but does NOT typecheck them.
    public Typechecker(final Program program) throws IllTypedException {
        // with overloading:
        // functionDefinitions = new HashMap<(FunctionName, ParameterTypes), FirstOrderFunctionDefinition>();
        functionDefinitions = new HashMap<FunctionName, FirstOrderFunctionDefinition>();
        for (final FirstOrderFunctionDefinition function : program.functions) {
            if (!functionDefinitions.containsKey(function.name)) {
                functionDefinitions.put(function.name, function);
            } else {
                throw new IllTypedException("Duplicate function name: " + function.name);
            }
        }
    } // Typechecker

    public void typecheckProgram(final Program program)
        throws IllTypedException {
        for (final FirstOrderFunctionDefinition function : program.functions) {
            typecheckFunction(function);
        }
    } // typecheckProgram

    public void typecheckFunction(final FirstOrderFunctionDefinition function)
        throws IllTypedException {
        final Map<Variable, Type> gamma = new HashMap<Variable, Type>();
        for (final FormalParameter formalParam : function.formalParams) {
            if (!gamma.containsKey(formalParam.theVariable)) {
                gamma.put(formalParam.theVariable, formalParam.theType);
            } else {
                throw new IllTypedException("Duplicate formal parameter name");
            }
        }

        final Map<Variable, Type> finalGamma = typecheckStmts(gamma, false, function.body);
        final Type actualReturnType = typeof(finalGamma, function.returnExp);
        if (!actualReturnType.equals(function.returnType)) {
            throw new IllTypedException("return type mismatch");
        }
    } // typecheckFunction

    public static Map<Variable, Type> makeCopy(final Map<Variable, Type> gamma) {
        final Map<Variable, Type> copy = new HashMap<Variable, Type>();
        copy.putAll(gamma);
        return copy;
    } // makeCopy

    // int bar(int x) {
    //   return x + 5;
    // }
    //
    // int foo(int x) {
    //   return bar(x);
    // }
    //
    // bool isEven(int x) {
    //   if (x == 2) {
    //     return true;
    //   } else {
    //     return isOdd(x - 1);
    //   }
    // }
    //
    // bool isOdd(int x) {
    //   if (x == 1) {
    //     return true;
    //   } else {
    //     return isEven(x - 1);
    //   }
    // }
    //
    // int sum(int x, int y) {
    //   return x + y;
    // }
    //
    // void blah(int y) {
    //   x = y;
    // }
    //
    // void someValue = blah(1);
    //
    // -Which functions are declared?
    // -What parameter types do they take?
    // -What return types to they have?
    //
    // [bar -> (int, (int)),
    //  foo -> (int, (int)),
    //  isEven -> (bool, (int)),
    //  isOdd -> (bool, (int)),
    //  sum -> (int, (int, int))]
    
    public Map<Variable, Type> typecheckStmts(Map<Variable, Type> gamma,
                                              final boolean breakAndContinueOk,
                                              final List<Stmt> stmts)
        throws IllTypedException {
        for (final Stmt s : stmts) {
            //                  result gamma
            // initial          []
            // int x = 7;       [x -> int]
            // int y = x + 3;   [x -> int, y -> int]
            // int z = y + x;   [x -> int, y -> int, z -> int]
            gamma = typecheckStmt(gamma, breakAndContinueOk, s);
        }

        return gamma;
    } // typecheckStmts
        
    public Map<Variable, Type> typecheckStmt(final Map<Variable, Type> gamma,
                                             final boolean breakAndContinueOk,
                                             final Stmt s)
        throws IllTypedException {
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
            if (breakAndContinueOk) {
                return gamma;
            } else {
                throw new IllTypedException("break outside of a loop");
            }
        } else if (s instanceof ContinueStmt) {
            if (breakAndContinueOk) {
                return gamma;
            } else {
                throw new IllTypedException("continue outside of a loop");
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
            final Map<Variable, Type> newGamma = typecheckStmt(gamma, breakAndContinueOk, asFor.initializer);
            final Type guardType = typeof(newGamma, asFor.guard);
            if (guardType instanceof BoolType) {
                typecheckStmt(newGamma, breakAndContinueOk, asFor.update);
                typecheckStmts(newGamma, true, asFor.body);
            }
            return gamma;
        } else {
            assert(false);
            throw new IllTypedException("Unrecognized statement");
        }
    } // typecheckStmt

    // typeof(Gamma, e2) == BoolType
    public Type typeof(final Map<Variable, Type> gamma,
                       final Exp e)
        throws IllTypedException {
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
        } else if (e instanceof HigherOrderFunctionDef) {
            // (x: Int) => x + 1
            // Int => Int
            final HigherOrderFunctionDef asFunc = (HigherOrderFunctionDef)e;
            final Map<Variable, Type> copy = makeCopy(gamma);
            copy.put(asFunc.paramName, asFunc.paramType);
            final Type bodyType = typeof(copy, asFunc.body);
            return new FunctionType(asFunc.paramType, bodyType);
        } else if (e instanceof CallHigherOrderFunction) {
            // e1(e2)
            // e1: (x: Int) => x + 1 [Int => Int]
            // e2: 7 [Int]
            // e1(e2): [Int]
            final CallHigherOrderFunction asCall = (CallHigherOrderFunction)e;
            final Type hopefullyFunction = typeof(gamma, asCall.theFunction);
            final Type hopefullyParameter = typeof(gamma, asCall.theParameter);
            if (hopefullyFunction instanceof FunctionType) {
                final FunctionType asFunc = (FunctionType)hopefullyFunction;
                if (asFunc.paramType.equals(hopefullyParameter)) {
                    return asFunc.returnType;
                } else {
                    throw new IllTypedException("Parameter type mismatch");
                }
            } else {
                throw new IllTypedException("call of non-function");
            }
        } else {
            assert(false);
            throw new IllTypedException("unrecognized expression");
        }
    } // typeof
} // Typechecker

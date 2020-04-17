package typechecker_example.typechecker;

import typechecker_example.syntax.*;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class TypecheckerTest {
    public static Map<Variable, Type> makeEmptyGamma() {
        return makeGamma(new String[0], new Type[0]);
    }
    
    public static Map<Variable, Type> makeGamma(final String[] variables,
                                                final Type[] types) {
        assert(variables.length == types.length);
        final Map<Variable, Type> gamma = new HashMap<Variable, Type>();

        for (int index = 0; index < variables.length; index++) {
            gamma.put(new Variable(variables[index]), types[index]);
        }

        return gamma;
    } // makeGamma

    public static List<FormalParameter> makeFormalParams(final Type[] types,
                                                         final String[] variables) {
        assert(types.length == variables.length);
        final List<FormalParameter> list = new ArrayList<FormalParameter>();

        for (int index = 0; index < types.length; index++) {
            list.add(new FormalParameter(types[index], new Variable(variables[index])));
        }

        return list;
    } // makeFormalParams

    public static List<Stmt> makeStatements(final Stmt... statements) {
        final List<Stmt> list = new ArrayList<Stmt>();

        for (final Stmt stmt : statements) {
            list.add(stmt);
        }

        return list;
    } // makeStatements
    
    public static Program makeProgram(final FirstOrderFunctionDefinition... functions) {
        final List<FirstOrderFunctionDefinition> list = new ArrayList<FirstOrderFunctionDefinition>();
        for (final FirstOrderFunctionDefinition function : functions) {
            list.add(function);
        }
        return new Program(list);
    } // makeProgram

    public static List<Exp> makeActualParams(final Exp... exps) {
        final List<Exp> list = new ArrayList<Exp>();
        for (final Exp exp : exps) {
            list.add(exp);
        }
        return list;
    } // makeActualParams
    
    public static Type typeof(final Map<Variable, Type> gamma,
                              final Exp e)
        throws IllTypedException {
        return (new Typechecker(makeProgram())).typeof(gamma, e);
    } // typeof

    @Test
    public void canAccessIntVariableInScope() throws IllTypedException {
        assertEquals(new IntType(),
                     typeof(makeGamma(new String[]{ "x" }, new Type[]{ new IntType() }),
                            new VariableExp(new Variable("x"))));
    }

    @Test
    public void canAccessBoolVariableInScope() throws IllTypedException {
        assertEquals(new BoolType(),
                     typeof(makeGamma(new String[]{ "x" }, new Type[]{ new BoolType() }),
                            new VariableExp(new Variable("x"))));
    }

    @Test(expected = IllTypedException.class)
    public void accessingOutOfScopeVariableIsIllTyped() throws IllTypedException {
        typeof(makeEmptyGamma(),
               new VariableExp(new Variable("x")));
    }

    @Test
    public void integerExpReturnsInt() throws IllTypedException {
        assertEquals(new IntType(),
                     typeof(makeEmptyGamma(),
                            new IntegerExp(0)));
    }

    @Test
    public void booleanExpReturnsBool() throws IllTypedException {
        assertEquals(new BoolType(),
                     typeof(makeEmptyGamma(),
                            new BooleanExp(true)));
    }

    @Test
    public void intLessThanIntGivesBool() throws IllTypedException {
        assertEquals(new BoolType(),
                     typeof(makeEmptyGamma(),
                            new BinopExp(new IntegerExp(0),
                                         new LessThanBOP(),
                                         new IntegerExp(1))));
    }

    @Test(expected = IllTypedException.class)
    public void intLessThanBoolGivesTypeError() throws IllTypedException {
        typeof(makeEmptyGamma(),
               new BinopExp(new IntegerExp(0),
                            new LessThanBOP(),
                            new BooleanExp(true)));
    }

    @Test(expected = IllTypedException.class)
    public void boolLessThanIntGivesTypeError() throws IllTypedException {
        typeof(makeEmptyGamma(),
               new BinopExp(new BooleanExp(true),
                            new LessThanBOP(),
                            new IntegerExp(0)));
    }

    @Test(expected = IllTypedException.class)
    public void boolLessThanBoolGivesTypeError() throws IllTypedException {
        typeof(makeEmptyGamma(),
               new BinopExp(new BooleanExp(true),
                            new LessThanBOP(),
                            new BooleanExp(false)));
    }

    @Test
    public void intPlusIntGivesInt() throws IllTypedException {
        assertEquals(new IntType(),
                     typeof(makeEmptyGamma(),
                            new BinopExp(new IntegerExp(0),
                                         new PlusBOP(),
                                         new IntegerExp(1))));
    }

    @Test(expected = IllTypedException.class)
    public void intPlusBoolGivesTypeError() throws IllTypedException {
        typeof(makeEmptyGamma(),
               new BinopExp(new IntegerExp(0),
                            new PlusBOP(),
                            new BooleanExp(true)));
    }

    @Test(expected = IllTypedException.class)
    public void boolPlusIntGivesTypeError() throws IllTypedException {
        typeof(makeEmptyGamma(),
               new BinopExp(new BooleanExp(true),
                            new PlusBOP(),
                            new IntegerExp(0)));
    }

    @Test(expected = IllTypedException.class)
    public void boolPlusBoolGivesTypeError() throws IllTypedException {
        typeof(makeEmptyGamma(),
               new BinopExp(new BooleanExp(true),
                            new PlusBOP(),
                            new BooleanExp(false)));
    }

    @Test
    public void canCreateHigherOrderFunction() throws IllTypedException {
        // (x: int) => true
        assertEquals(new FunctionType(new IntType(), new BoolType()),
                     typeof(makeEmptyGamma(),
                            new HigherOrderFunctionDef(new Variable("x"),
                                                       new IntType(),
                                                       new BooleanExp(true))));
    }

    @Test
    public void higherOrderFunctionCanUsePassedVariable() throws IllTypedException {
        // (x: int) => x + 1
        final Variable x = new Variable("x");
        assertEquals(new FunctionType(new IntType(), new IntType()),
                     typeof(makeEmptyGamma(),
                            new HigherOrderFunctionDef(x,
                                                       new IntType(),
                                                       new BinopExp(new VariableExp(x),
                                                                    new PlusBOP(),
                                                                    new IntegerExp(1)))));
    }

    @Test
    public void higherOrderFunctionCanCaptureEnvironment() throws IllTypedException {
        // [x -> int] (y: int) => y + x
        final Variable x = new Variable("x");
        final Variable y = new Variable("y");

        assertEquals(new FunctionType(new IntType(), new IntType()),
                     typeof(makeGamma(new String[]{ "x" }, new Type[]{ new IntType() }),
                            new HigherOrderFunctionDef(y,
                                                       new IntType(),
                                                       new BinopExp(new VariableExp(y),
                                                                    new PlusBOP(),
                                                                    new VariableExp(x)))));
    }

    @Test
    public void higherOrderFunctionCanShadowEnvironment() throws IllTypedException {
        // [x -> int] (x: bool) => x
        final Variable x = new Variable("x");
        
        assertEquals(new FunctionType(new BoolType(), new BoolType()),
                     typeof(makeGamma(new String[]{ "x" }, new Type[]{ new IntType() }),
                            new HigherOrderFunctionDef(x,
                                                       new BoolType(),
                                                       new VariableExp(x))));
    }

    @Test
    public void higherOrderFunctionsCanBeCalled() throws IllTypedException {
        // [x -> int => bool] x(3)
        final FunctionType ft = new FunctionType(new IntType(),
                                                 new BoolType());
        final Variable x = new Variable("x");

        assertEquals(new BoolType(),
                     typeof(makeGamma(new String[]{ "x" }, new Type[]{ ft }),
                            new CallHigherOrderFunction(new VariableExp(x),
                                                        new IntegerExp(0))));
    }

    @Test(expected = IllTypedException.class)
    public void higherOrderFunctionsNeedCorrectType() throws IllTypedException {
        // [x -> int => bool] x(true)
        final FunctionType ft = new FunctionType(new IntType(),
                                                 new BoolType());
        final Variable x = new Variable("x");

        typeof(makeGamma(new String[]{ "x" }, new Type[]{ ft }),
               new CallHigherOrderFunction(new VariableExp(x),
                                           new BooleanExp(true)));
    }

    @Test(expected = IllTypedException.class)
    public void firstOrderFunctionsCannotHaveDuplicateFormalParameterNames() throws IllTypedException {
        // int foo(int x, int x) { return 1; }
        final FirstOrderFunctionDefinition fdef =
            new FirstOrderFunctionDefinition(new IntType(),
                                             new FunctionName("foo"),
                                             makeFormalParams(new Type[]{ new IntType(), new IntType() },
                                                              new String[]{ "x", "x" }),
                                             makeStatements(),
                                             new IntegerExp(1));
        final Program p = makeProgram(fdef);
        new Typechecker(p).typecheckProgram(p);
    }

    @Test(expected = IllTypedException.class)
    public void firstOrderFunctionsNeedDistinctNames() throws IllTypedException {
        // int foo() { return 1; }
        // int foo() { return 1; }
        final FirstOrderFunctionDefinition fdef =
            new FirstOrderFunctionDefinition(new IntType(),
                                             new FunctionName("foo"),
                                             makeFormalParams(new Type[0], new String[0]),
                                             makeStatements(),
                                             new IntegerExp(1));
        final Program p = makeProgram(fdef, fdef);
        new Typechecker(p);
    }
    
    @Test
    public void firstOrderFunctionsCanBeCalled() throws IllTypedException {
        // int foo() { return 1; }
        // foo()

        final FunctionName fn = new FunctionName("foo");
        final FirstOrderFunctionDefinition fdef =
            new FirstOrderFunctionDefinition(new IntType(),
                                             fn,
                                             makeFormalParams(new Type[0], new String[0]),
                                             makeStatements(),
                                             new IntegerExp(1));
        final Program p = makeProgram(fdef);
        final Typechecker typechecker = new Typechecker(p);
        typechecker.typecheckProgram(p);
        assertEquals(new IntType(),
                     typechecker.typeof(makeEmptyGamma(),
                                        new CallFirstOrderFunction(fn,
                                                                   makeActualParams())));
    }

    @Test
    public void firstOrderFunctionsCanUseParams() throws IllTypedException {
        // int foo(int x) { return x; }
        // foo(1)

        final FunctionName fn = new FunctionName("foo");
        final List<FormalParameter> formalParams =
            makeFormalParams(new Type[]{ new IntType() },
                             new String[]{ "x" });
        final FirstOrderFunctionDefinition fdef =
            new FirstOrderFunctionDefinition(new IntType(),
                                             fn,
                                             formalParams,
                                             makeStatements(),
                                             new VariableExp(new Variable("x")));
        final Program p = makeProgram(fdef);
        final List<Exp> actualParams =
            makeActualParams(new IntegerExp(1));
        final Typechecker typechecker = new Typechecker(p);
        typechecker.typecheckProgram(p);
        assertEquals(new IntType(),
                     new Typechecker(p).typeof(makeEmptyGamma(),
                                               new CallFirstOrderFunction(fn,
                                                                          actualParams)));
    }

    @Test
    public void firstOrderFunctionsCanTakeParams() throws IllTypedException {
        // int foo(int x, bool y, int => bool z) { return 1; }
        // [a -> int, b -> bool, c -> int => bool] foo(a, b, c)
        final Type[] types = new Type[]{ new IntType(),
                                         new BoolType(),
                                         new FunctionType(new IntType(), new BoolType()) };
        final FunctionName fn = new FunctionName("foo");
        final List<FormalParameter> formalParams =
            makeFormalParams(types,
                             new String[]{ "x", "y", "z" });
        final FirstOrderFunctionDefinition fdef =
            new FirstOrderFunctionDefinition(new IntType(),
                                             fn,
                                             formalParams,
                                             makeStatements(),
                                             new IntegerExp(1));

        final Program p = makeProgram(fdef);
        final Map<Variable, Type> gamma =
            makeGamma(new String[]{ "a", "b", "c" },
                      types);

        final List<Exp> actualParams =
            makeActualParams(new VariableExp(new Variable("a")),
                             new VariableExp(new Variable("b")),
                             new VariableExp(new Variable("c")));
                                             
        assertEquals(new IntType(),
                     new Typechecker(p).typeof(gamma,
                                               new CallFirstOrderFunction(fn,
                                                                          actualParams)));
    }

    @Test(expected = IllTypedException.class)
    public void firstOrderFunctionsRejectBadParams() throws IllTypedException {
        // int foo(int x) { return x; }
        // foo(true);
        final FunctionName fn = new FunctionName("foo");
        final FirstOrderFunctionDefinition fdef =
            new FirstOrderFunctionDefinition(new IntType(),
                                             fn,
                                             makeFormalParams(new Type[0], new String[0]),
                                             makeStatements(),
                                             new IntegerExp(1));
        final Program p = makeProgram(fdef);
        new Typechecker(p).typeof(makeEmptyGamma(),
                                  new CallFirstOrderFunction(fn,
                                                             makeActualParams(new BooleanExp(true))));
    }

    @Test(expected = IllTypedException.class)
    public void typecheckingFailsIfTypeErrorIsInBodyOfFirstOrderFunction() throws IllTypedException {
        // int foo() { break; return 1; }
        final FunctionName fn = new FunctionName("foo");
        final FirstOrderFunctionDefinition fdef =
            new FirstOrderFunctionDefinition(new IntType(),
                                             fn,
                                             makeFormalParams(new Type[0], new String[0]),
                                             makeStatements(new BreakStmt()),
                                             new IntegerExp(1));

        final Program p = makeProgram(fdef);
        new Typechecker(p).typecheckProgram(p);
    }

    @Test(expected = IllTypedException.class)
    public void typecheckingFailsOnReturnTypeMismatch() throws IllTypedException {
        // int foo() { return true; }
        final FunctionName fn = new FunctionName("foo");
        final FirstOrderFunctionDefinition fdef =
            new FirstOrderFunctionDefinition(new IntType(),
                                             fn,
                                             makeFormalParams(new Type[0], new String[0]),
                                             makeStatements(),
                                             new BooleanExp(true));

        final Program p = makeProgram(fdef);
        new Typechecker(p).typecheckProgram(p);
    }
} // TypecheckerTest

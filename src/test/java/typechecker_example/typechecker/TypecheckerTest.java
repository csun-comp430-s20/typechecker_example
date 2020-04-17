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

    public static Program makeProgram(final FirstOrderFunctionDefinition... functions) {
        final List<FirstOrderFunctionDefinition> list = new ArrayList<FirstOrderFunctionDefinition>();
        for (final FirstOrderFunctionDefinition function : functions) {
            list.add(function);
        }
        return new Program(list);
    } // makeProgram
    
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
} // TypecheckerTest

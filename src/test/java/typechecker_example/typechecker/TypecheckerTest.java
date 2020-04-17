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
                                         new IntegerExp(0))));
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
} // TypecheckerTest

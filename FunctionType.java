// t1 => t2
public class FunctionType implements Type {
    public final Type paramType;
    public final Type returnType;

    public FunctionType(final Type paramType,
                        final Type returnType) {
        this.paramType = paramType;
        this.returnType = returnType;
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof FunctionType) {
            final FunctionType asFunc = (FunctionType)other;
            return (paramType.equals(asFunc.paramType) &&
                    returnType.equals(asFunc.returnType));
        } else {
            return false;
        }
    } // equals

    @Override
    public int hashCode() {
        return paramType.hashCode() + returnType.hashCode();
    }
} // FunctionType

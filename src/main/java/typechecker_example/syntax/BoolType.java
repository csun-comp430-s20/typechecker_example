public class BoolType implements Type {
    public BoolType() {}
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof BoolType;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}

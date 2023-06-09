package value;

public class ComValue extends Value {

    public static final ComValue INSTANCE = new ComValue();

    private ComValue() { }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ComValue;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}

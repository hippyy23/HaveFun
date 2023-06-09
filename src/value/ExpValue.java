package value;

public abstract class ExpValue<T> extends Value {

    private final T value;

    ExpValue(T value) {
        this.value = value;
    }

    public T toJavaValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpValue<?> expValue = (ExpValue<?>) o;
        return value.equals(expValue.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

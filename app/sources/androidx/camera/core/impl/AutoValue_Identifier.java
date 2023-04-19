package androidx.camera.core.impl;

final class AutoValue_Identifier extends Identifier {
    private final Object value;

    AutoValue_Identifier(Object value2) {
        if (value2 != null) {
            this.value = value2;
            return;
        }
        throw new NullPointerException("Null value");
    }

    public Object getValue() {
        return this.value;
    }

    public String toString() {
        return "Identifier{value=" + this.value + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Identifier) {
            return this.value.equals(((Identifier) o).getValue());
        }
        return false;
    }

    public int hashCode() {
        return (1 * 1000003) ^ this.value.hashCode();
    }
}

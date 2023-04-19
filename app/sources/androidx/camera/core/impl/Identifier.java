package androidx.camera.core.impl;

public abstract class Identifier {
    public abstract Object getValue();

    public static Identifier create(Object value) {
        return new AutoValue_Identifier(value);
    }
}

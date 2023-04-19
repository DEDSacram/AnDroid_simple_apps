package androidx.camera.core.impl;

public class MutableStateObservable<T> extends StateObservable<T> {
    private MutableStateObservable(Object initialState, boolean isError) {
        super(initialState, isError);
    }

    public static <T> MutableStateObservable<T> withInitialState(T initialState) {
        return new MutableStateObservable<>(initialState, false);
    }

    public static <T> MutableStateObservable<T> withInitialError(Throwable initialError) {
        return new MutableStateObservable<>(initialError, true);
    }

    public void setState(T state) {
        updateState(state);
    }

    public void setError(Throwable error) {
        updateStateAsError(error);
    }
}

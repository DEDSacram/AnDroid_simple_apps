package androidx.camera.core;

public final class FocusMeteringResult {
    private boolean mIsFocusSuccessful;

    public static FocusMeteringResult emptyInstance() {
        return new FocusMeteringResult(false);
    }

    public static FocusMeteringResult create(boolean isFocusSuccess) {
        return new FocusMeteringResult(isFocusSuccess);
    }

    private FocusMeteringResult(boolean isFocusSuccess) {
        this.mIsFocusSuccessful = isFocusSuccess;
    }

    public boolean isFocusSuccessful() {
        return this.mIsFocusSuccessful;
    }
}

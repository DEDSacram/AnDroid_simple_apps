package androidx.camera.core;

public final class CameraInfoUnavailableException extends Exception {
    public CameraInfoUnavailableException(String s, Throwable e) {
        super(s, e);
    }

    public CameraInfoUnavailableException(String s) {
        super(s);
    }
}

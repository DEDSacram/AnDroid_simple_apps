package androidx.camera.core;

final class CameraClosedException extends RuntimeException {
    CameraClosedException(String s, Throwable e) {
        super(s, e);
    }

    CameraClosedException(String s) {
        super(s);
    }
}

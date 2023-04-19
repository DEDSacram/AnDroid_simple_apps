package androidx.camera.core.impl;

public abstract class CameraCaptureCallback {
    public void onCaptureCompleted(CameraCaptureResult cameraCaptureResult) {
    }

    public void onCaptureFailed(CameraCaptureFailure failure) {
    }

    public void onCaptureCancelled() {
    }
}

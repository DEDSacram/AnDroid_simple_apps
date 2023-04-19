package androidx.camera.core.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CameraCaptureCallbacks {
    private CameraCaptureCallbacks() {
    }

    public static CameraCaptureCallback createNoOpCallback() {
        return new NoOpCameraCaptureCallback();
    }

    static CameraCaptureCallback createComboCallback(List<CameraCaptureCallback> callbacks) {
        if (callbacks.isEmpty()) {
            return createNoOpCallback();
        }
        if (callbacks.size() == 1) {
            return callbacks.get(0);
        }
        return new ComboCameraCaptureCallback(callbacks);
    }

    public static CameraCaptureCallback createComboCallback(CameraCaptureCallback... callbacks) {
        return createComboCallback((List<CameraCaptureCallback>) Arrays.asList(callbacks));
    }

    static final class NoOpCameraCaptureCallback extends CameraCaptureCallback {
        NoOpCameraCaptureCallback() {
        }

        public void onCaptureCompleted(CameraCaptureResult cameraCaptureResult) {
        }

        public void onCaptureFailed(CameraCaptureFailure failure) {
        }
    }

    public static final class ComboCameraCaptureCallback extends CameraCaptureCallback {
        private final List<CameraCaptureCallback> mCallbacks = new ArrayList();

        ComboCameraCaptureCallback(List<CameraCaptureCallback> callbacks) {
            for (CameraCaptureCallback callback : callbacks) {
                if (!(callback instanceof NoOpCameraCaptureCallback)) {
                    this.mCallbacks.add(callback);
                }
            }
        }

        public void onCaptureCompleted(CameraCaptureResult cameraCaptureResult) {
            for (CameraCaptureCallback callback : this.mCallbacks) {
                callback.onCaptureCompleted(cameraCaptureResult);
            }
        }

        public void onCaptureFailed(CameraCaptureFailure failure) {
            for (CameraCaptureCallback callback : this.mCallbacks) {
                callback.onCaptureFailed(failure);
            }
        }

        public void onCaptureCancelled() {
            for (CameraCaptureCallback callback : this.mCallbacks) {
                callback.onCaptureCancelled();
            }
        }

        public List<CameraCaptureCallback> getCallbacks() {
            return this.mCallbacks;
        }
    }
}

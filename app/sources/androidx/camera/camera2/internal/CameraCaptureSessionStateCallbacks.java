package androidx.camera.camera2.internal;

import android.hardware.camera2.CameraCaptureSession;
import android.view.Surface;
import androidx.camera.camera2.internal.compat.ApiCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CameraCaptureSessionStateCallbacks {
    private CameraCaptureSessionStateCallbacks() {
    }

    public static CameraCaptureSession.StateCallback createNoOpCallback() {
        return new NoOpSessionStateCallback();
    }

    public static CameraCaptureSession.StateCallback createComboCallback(List<CameraCaptureSession.StateCallback> callbacks) {
        if (callbacks.isEmpty()) {
            return createNoOpCallback();
        }
        if (callbacks.size() == 1) {
            return callbacks.get(0);
        }
        return new ComboSessionStateCallback(callbacks);
    }

    public static CameraCaptureSession.StateCallback createComboCallback(CameraCaptureSession.StateCallback... callbacks) {
        return createComboCallback((List<CameraCaptureSession.StateCallback>) Arrays.asList(callbacks));
    }

    static final class NoOpSessionStateCallback extends CameraCaptureSession.StateCallback {
        NoOpSessionStateCallback() {
        }

        public void onConfigured(CameraCaptureSession session) {
        }

        public void onActive(CameraCaptureSession session) {
        }

        public void onClosed(CameraCaptureSession session) {
        }

        public void onReady(CameraCaptureSession session) {
        }

        public void onCaptureQueueEmpty(CameraCaptureSession session) {
        }

        public void onSurfacePrepared(CameraCaptureSession session, Surface surface) {
        }

        public void onConfigureFailed(CameraCaptureSession session) {
        }
    }

    static final class ComboSessionStateCallback extends CameraCaptureSession.StateCallback {
        private final List<CameraCaptureSession.StateCallback> mCallbacks = new ArrayList();

        ComboSessionStateCallback(List<CameraCaptureSession.StateCallback> callbacks) {
            for (CameraCaptureSession.StateCallback callback : callbacks) {
                if (!(callback instanceof NoOpSessionStateCallback)) {
                    this.mCallbacks.add(callback);
                }
            }
        }

        public void onConfigured(CameraCaptureSession session) {
            for (CameraCaptureSession.StateCallback callback : this.mCallbacks) {
                callback.onConfigured(session);
            }
        }

        public void onActive(CameraCaptureSession session) {
            for (CameraCaptureSession.StateCallback callback : this.mCallbacks) {
                callback.onActive(session);
            }
        }

        public void onClosed(CameraCaptureSession session) {
            for (CameraCaptureSession.StateCallback callback : this.mCallbacks) {
                callback.onClosed(session);
            }
        }

        public void onReady(CameraCaptureSession session) {
            for (CameraCaptureSession.StateCallback callback : this.mCallbacks) {
                callback.onReady(session);
            }
        }

        public void onCaptureQueueEmpty(CameraCaptureSession session) {
            for (CameraCaptureSession.StateCallback callback : this.mCallbacks) {
                ApiCompat.Api26Impl.onCaptureQueueEmpty(callback, session);
            }
        }

        public void onSurfacePrepared(CameraCaptureSession session, Surface surface) {
            for (CameraCaptureSession.StateCallback callback : this.mCallbacks) {
                ApiCompat.Api23Impl.onSurfacePrepared(callback, session, surface);
            }
        }

        public void onConfigureFailed(CameraCaptureSession session) {
            for (CameraCaptureSession.StateCallback callback : this.mCallbacks) {
                callback.onConfigureFailed(session);
            }
        }
    }
}

package androidx.camera.camera2.internal;

import android.hardware.camera2.CameraCaptureSession;
import android.view.Surface;
import androidx.camera.camera2.internal.SynchronizedCaptureSession;
import androidx.camera.camera2.internal.compat.ApiCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class SynchronizedCaptureSessionStateCallbacks extends SynchronizedCaptureSession.StateCallback {
    private final List<SynchronizedCaptureSession.StateCallback> mCallbacks;

    static SynchronizedCaptureSession.StateCallback createComboCallback(SynchronizedCaptureSession.StateCallback... callbacks) {
        return new SynchronizedCaptureSessionStateCallbacks(Arrays.asList(callbacks));
    }

    SynchronizedCaptureSessionStateCallbacks(List<SynchronizedCaptureSession.StateCallback> callbacks) {
        ArrayList arrayList = new ArrayList();
        this.mCallbacks = arrayList;
        arrayList.addAll(callbacks);
    }

    public void onSurfacePrepared(SynchronizedCaptureSession session, Surface surface) {
        for (SynchronizedCaptureSession.StateCallback callback : this.mCallbacks) {
            callback.onSurfacePrepared(session, surface);
        }
    }

    public void onReady(SynchronizedCaptureSession session) {
        for (SynchronizedCaptureSession.StateCallback callback : this.mCallbacks) {
            callback.onReady(session);
        }
    }

    public void onActive(SynchronizedCaptureSession session) {
        for (SynchronizedCaptureSession.StateCallback callback : this.mCallbacks) {
            callback.onActive(session);
        }
    }

    public void onCaptureQueueEmpty(SynchronizedCaptureSession session) {
        for (SynchronizedCaptureSession.StateCallback callback : this.mCallbacks) {
            callback.onCaptureQueueEmpty(session);
        }
    }

    public void onConfigured(SynchronizedCaptureSession session) {
        for (SynchronizedCaptureSession.StateCallback callback : this.mCallbacks) {
            callback.onConfigured(session);
        }
    }

    public void onConfigureFailed(SynchronizedCaptureSession session) {
        for (SynchronizedCaptureSession.StateCallback callback : this.mCallbacks) {
            callback.onConfigureFailed(session);
        }
    }

    public void onClosed(SynchronizedCaptureSession session) {
        for (SynchronizedCaptureSession.StateCallback callback : this.mCallbacks) {
            callback.onClosed(session);
        }
    }

    /* access modifiers changed from: package-private */
    public void onSessionFinished(SynchronizedCaptureSession session) {
        for (SynchronizedCaptureSession.StateCallback callback : this.mCallbacks) {
            callback.onSessionFinished(session);
        }
    }

    static class Adapter extends SynchronizedCaptureSession.StateCallback {
        private final CameraCaptureSession.StateCallback mCameraCaptureSessionStateCallback;

        Adapter(CameraCaptureSession.StateCallback cameraCaptureSessionStateCallback) {
            this.mCameraCaptureSessionStateCallback = cameraCaptureSessionStateCallback;
        }

        Adapter(List<CameraCaptureSession.StateCallback> callbackList) {
            this(CameraCaptureSessionStateCallbacks.createComboCallback(callbackList));
        }

        public void onSurfacePrepared(SynchronizedCaptureSession session, Surface surface) {
            ApiCompat.Api23Impl.onSurfacePrepared(this.mCameraCaptureSessionStateCallback, session.toCameraCaptureSessionCompat().toCameraCaptureSession(), surface);
        }

        public void onReady(SynchronizedCaptureSession session) {
            this.mCameraCaptureSessionStateCallback.onReady(session.toCameraCaptureSessionCompat().toCameraCaptureSession());
        }

        public void onActive(SynchronizedCaptureSession session) {
            this.mCameraCaptureSessionStateCallback.onActive(session.toCameraCaptureSessionCompat().toCameraCaptureSession());
        }

        public void onCaptureQueueEmpty(SynchronizedCaptureSession session) {
            ApiCompat.Api26Impl.onCaptureQueueEmpty(this.mCameraCaptureSessionStateCallback, session.toCameraCaptureSessionCompat().toCameraCaptureSession());
        }

        public void onConfigured(SynchronizedCaptureSession session) {
            this.mCameraCaptureSessionStateCallback.onConfigured(session.toCameraCaptureSessionCompat().toCameraCaptureSession());
        }

        public void onConfigureFailed(SynchronizedCaptureSession session) {
            this.mCameraCaptureSessionStateCallback.onConfigureFailed(session.toCameraCaptureSessionCompat().toCameraCaptureSession());
        }

        public void onClosed(SynchronizedCaptureSession session) {
            this.mCameraCaptureSessionStateCallback.onClosed(session.toCameraCaptureSessionCompat().toCameraCaptureSession());
        }

        /* access modifiers changed from: package-private */
        public void onSessionFinished(SynchronizedCaptureSession session) {
        }
    }
}

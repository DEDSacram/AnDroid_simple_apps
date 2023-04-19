package androidx.camera.camera2.internal;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.view.Surface;
import androidx.camera.camera2.internal.compat.ApiCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Camera2CaptureCallbacks {
    private Camera2CaptureCallbacks() {
    }

    public static CameraCaptureSession.CaptureCallback createNoOpCallback() {
        return new NoOpSessionCaptureCallback();
    }

    static CameraCaptureSession.CaptureCallback createComboCallback(List<CameraCaptureSession.CaptureCallback> callbacks) {
        return new ComboSessionCaptureCallback(callbacks);
    }

    public static CameraCaptureSession.CaptureCallback createComboCallback(CameraCaptureSession.CaptureCallback... callbacks) {
        return createComboCallback((List<CameraCaptureSession.CaptureCallback>) Arrays.asList(callbacks));
    }

    static final class NoOpSessionCaptureCallback extends CameraCaptureSession.CaptureCallback {
        NoOpSessionCaptureCallback() {
        }

        public void onCaptureBufferLost(CameraCaptureSession session, CaptureRequest request, Surface surface, long frame) {
        }

        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
        }

        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
        }

        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
        }

        public void onCaptureSequenceAborted(CameraCaptureSession session, int sequenceId) {
        }

        public void onCaptureSequenceCompleted(CameraCaptureSession session, int sequenceId, long frame) {
        }

        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frame) {
        }
    }

    private static final class ComboSessionCaptureCallback extends CameraCaptureSession.CaptureCallback {
        private final List<CameraCaptureSession.CaptureCallback> mCallbacks = new ArrayList();

        ComboSessionCaptureCallback(List<CameraCaptureSession.CaptureCallback> callbacks) {
            for (CameraCaptureSession.CaptureCallback callback : callbacks) {
                if (!(callback instanceof NoOpSessionCaptureCallback)) {
                    this.mCallbacks.add(callback);
                }
            }
        }

        public void onCaptureBufferLost(CameraCaptureSession session, CaptureRequest request, Surface surface, long frame) {
            for (CameraCaptureSession.CaptureCallback callback : this.mCallbacks) {
                ApiCompat.Api24Impl.onCaptureBufferLost(callback, session, request, surface, frame);
            }
        }

        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            for (CameraCaptureSession.CaptureCallback callback : this.mCallbacks) {
                callback.onCaptureCompleted(session, request, result);
            }
        }

        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
            for (CameraCaptureSession.CaptureCallback callback : this.mCallbacks) {
                callback.onCaptureFailed(session, request, failure);
            }
        }

        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
            for (CameraCaptureSession.CaptureCallback callback : this.mCallbacks) {
                callback.onCaptureProgressed(session, request, partialResult);
            }
        }

        public void onCaptureSequenceAborted(CameraCaptureSession session, int sequenceId) {
            for (CameraCaptureSession.CaptureCallback callback : this.mCallbacks) {
                callback.onCaptureSequenceAborted(session, sequenceId);
            }
        }

        public void onCaptureSequenceCompleted(CameraCaptureSession session, int sequenceId, long frame) {
            for (CameraCaptureSession.CaptureCallback callback : this.mCallbacks) {
                callback.onCaptureSequenceCompleted(session, sequenceId, frame);
            }
        }

        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frame) {
            for (CameraCaptureSession.CaptureCallback callback : this.mCallbacks) {
                callback.onCaptureStarted(session, request, timestamp, frame);
            }
        }
    }
}

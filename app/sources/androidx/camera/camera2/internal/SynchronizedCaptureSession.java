package androidx.camera.camera2.internal;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.view.Surface;
import androidx.camera.camera2.internal.compat.CameraCaptureSessionCompat;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.List;
import java.util.concurrent.Executor;

public interface SynchronizedCaptureSession {
    void abortCaptures() throws CameraAccessException;

    int captureBurstRequests(List<CaptureRequest> list, CameraCaptureSession.CaptureCallback captureCallback) throws CameraAccessException;

    int captureBurstRequests(List<CaptureRequest> list, Executor executor, CameraCaptureSession.CaptureCallback captureCallback) throws CameraAccessException;

    int captureSingleRequest(CaptureRequest captureRequest, CameraCaptureSession.CaptureCallback captureCallback) throws CameraAccessException;

    int captureSingleRequest(CaptureRequest captureRequest, Executor executor, CameraCaptureSession.CaptureCallback captureCallback) throws CameraAccessException;

    void close();

    void finishClose();

    CameraDevice getDevice();

    Surface getInputSurface();

    ListenableFuture<Void> getOpeningBlocker();

    StateCallback getStateCallback();

    int setRepeatingBurstRequests(List<CaptureRequest> list, CameraCaptureSession.CaptureCallback captureCallback) throws CameraAccessException;

    int setRepeatingBurstRequests(List<CaptureRequest> list, Executor executor, CameraCaptureSession.CaptureCallback captureCallback) throws CameraAccessException;

    int setSingleRepeatingRequest(CaptureRequest captureRequest, CameraCaptureSession.CaptureCallback captureCallback) throws CameraAccessException;

    int setSingleRepeatingRequest(CaptureRequest captureRequest, Executor executor, CameraCaptureSession.CaptureCallback captureCallback) throws CameraAccessException;

    void stopRepeating() throws CameraAccessException;

    CameraCaptureSessionCompat toCameraCaptureSessionCompat();

    public static abstract class StateCallback {
        /* access modifiers changed from: package-private */
        public void onReady(SynchronizedCaptureSession session) {
        }

        /* access modifiers changed from: package-private */
        public void onActive(SynchronizedCaptureSession session) {
        }

        /* access modifiers changed from: package-private */
        public void onCaptureQueueEmpty(SynchronizedCaptureSession session) {
        }

        /* access modifiers changed from: package-private */
        public void onSurfacePrepared(SynchronizedCaptureSession session, Surface surface) {
        }

        /* access modifiers changed from: package-private */
        public void onConfigured(SynchronizedCaptureSession session) {
        }

        public void onConfigureFailed(SynchronizedCaptureSession session) {
        }

        public void onClosed(SynchronizedCaptureSession session) {
        }

        /* access modifiers changed from: package-private */
        public void onSessionFinished(SynchronizedCaptureSession session) {
        }
    }
}

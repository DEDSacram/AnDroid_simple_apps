package androidx.camera.camera2.internal.compat;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.OutputConfiguration;
import android.util.Size;
import android.view.Surface;

public final class ApiCompat {
    private ApiCompat() {
    }

    public static class Api21Impl {
        private Api21Impl() {
        }

        public static void close(CameraDevice cameraDevice) {
            cameraDevice.close();
        }
    }

    public static class Api23Impl {
        private Api23Impl() {
        }

        public static void onSurfacePrepared(CameraCaptureSession.StateCallback callback, CameraCaptureSession session, Surface surface) {
            callback.onSurfacePrepared(session, surface);
        }
    }

    public static class Api24Impl {
        private Api24Impl() {
        }

        public static void onCaptureBufferLost(CameraCaptureSession.CaptureCallback callback, CameraCaptureSession session, CaptureRequest request, Surface surface, long frame) {
            callback.onCaptureBufferLost(session, request, surface, frame);
        }
    }

    public static class Api26Impl {
        private Api26Impl() {
        }

        public static void onCaptureQueueEmpty(CameraCaptureSession.StateCallback callback, CameraCaptureSession session) {
            callback.onCaptureQueueEmpty(session);
        }

        public static <T> OutputConfiguration newOutputConfiguration(Size surfaceSize, Class<T> klass) {
            return new OutputConfiguration(surfaceSize, klass);
        }
    }

    public static class Api29Impl {
        private Api29Impl() {
        }

        public static void onCameraAccessPrioritiesChanged(CameraManager.AvailabilityCallback callback) {
            callback.onCameraAccessPrioritiesChanged();
        }
    }
}

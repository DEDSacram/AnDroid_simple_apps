package androidx.camera.camera2.internal;

import android.hardware.camera2.CameraDevice;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CameraDeviceStateCallbacks {
    private CameraDeviceStateCallbacks() {
    }

    public static CameraDevice.StateCallback createNoOpCallback() {
        return new NoOpDeviceStateCallback();
    }

    public static CameraDevice.StateCallback createComboCallback(List<CameraDevice.StateCallback> callbacks) {
        if (callbacks.isEmpty()) {
            return createNoOpCallback();
        }
        if (callbacks.size() == 1) {
            return callbacks.get(0);
        }
        return new ComboDeviceStateCallback(callbacks);
    }

    public static CameraDevice.StateCallback createComboCallback(CameraDevice.StateCallback... callbacks) {
        return createComboCallback((List<CameraDevice.StateCallback>) Arrays.asList(callbacks));
    }

    static final class NoOpDeviceStateCallback extends CameraDevice.StateCallback {
        NoOpDeviceStateCallback() {
        }

        public void onOpened(CameraDevice cameraDevice) {
        }

        public void onClosed(CameraDevice cameraDevice) {
        }

        public void onDisconnected(CameraDevice cameraDevice) {
        }

        public void onError(CameraDevice cameraDevice, int error) {
        }
    }

    private static final class ComboDeviceStateCallback extends CameraDevice.StateCallback {
        private final List<CameraDevice.StateCallback> mCallbacks = new ArrayList();

        ComboDeviceStateCallback(List<CameraDevice.StateCallback> callbacks) {
            for (CameraDevice.StateCallback callback : callbacks) {
                if (!(callback instanceof NoOpDeviceStateCallback)) {
                    this.mCallbacks.add(callback);
                }
            }
        }

        public void onOpened(CameraDevice cameraDevice) {
            for (CameraDevice.StateCallback callback : this.mCallbacks) {
                callback.onOpened(cameraDevice);
            }
        }

        public void onClosed(CameraDevice cameraDevice) {
            for (CameraDevice.StateCallback callback : this.mCallbacks) {
                callback.onClosed(cameraDevice);
            }
        }

        public void onDisconnected(CameraDevice cameraDevice) {
            for (CameraDevice.StateCallback callback : this.mCallbacks) {
                callback.onDisconnected(cameraDevice);
            }
        }

        public void onError(CameraDevice cameraDevice, int error) {
            for (CameraDevice.StateCallback callback : this.mCallbacks) {
                callback.onError(cameraDevice, error);
            }
        }
    }
}

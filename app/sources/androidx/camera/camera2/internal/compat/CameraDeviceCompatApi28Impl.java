package androidx.camera.camera2.internal.compat;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.params.SessionConfiguration;
import androidx.camera.camera2.internal.compat.params.SessionConfigurationCompat;
import androidx.core.util.Preconditions;

class CameraDeviceCompatApi28Impl extends CameraDeviceCompatApi24Impl {
    CameraDeviceCompatApi28Impl(CameraDevice cameraDevice) {
        super((CameraDevice) Preconditions.checkNotNull(cameraDevice), (Object) null);
    }

    public void createCaptureSession(SessionConfigurationCompat config) throws CameraAccessExceptionCompat {
        SessionConfiguration sessionConfig = (SessionConfiguration) config.unwrap();
        Preconditions.checkNotNull(sessionConfig);
        try {
            this.mCameraDevice.createCaptureSession(sessionConfig);
        } catch (CameraAccessException e) {
            throw CameraAccessExceptionCompat.toCameraAccessExceptionCompat(e);
        }
    }
}

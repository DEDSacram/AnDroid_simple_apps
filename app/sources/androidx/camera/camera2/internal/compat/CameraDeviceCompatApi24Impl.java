package androidx.camera.camera2.internal.compat;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.params.InputConfiguration;
import android.os.Handler;
import androidx.camera.camera2.internal.compat.CameraCaptureSessionCompat;
import androidx.camera.camera2.internal.compat.CameraDeviceCompatBaseImpl;
import androidx.camera.camera2.internal.compat.params.InputConfigurationCompat;
import androidx.camera.camera2.internal.compat.params.OutputConfigurationCompat;
import androidx.camera.camera2.internal.compat.params.SessionConfigurationCompat;
import androidx.core.util.Preconditions;
import java.util.List;

class CameraDeviceCompatApi24Impl extends CameraDeviceCompatApi23Impl {
    CameraDeviceCompatApi24Impl(CameraDevice cameraDevice, Object implParams) {
        super(cameraDevice, implParams);
    }

    static CameraDeviceCompatApi24Impl create(CameraDevice cameraDevice, Handler compatHandler) {
        return new CameraDeviceCompatApi24Impl(cameraDevice, new CameraDeviceCompatBaseImpl.CameraDeviceCompatParamsApi21(compatHandler));
    }

    public void createCaptureSession(SessionConfigurationCompat config) throws CameraAccessExceptionCompat {
        checkPreconditions(this.mCameraDevice, config);
        CameraCaptureSession.StateCallback cb = new CameraCaptureSessionCompat.StateCallbackExecutorWrapper(config.getExecutor(), config.getStateCallback());
        List<OutputConfigurationCompat> outputs = config.getOutputConfigurations();
        Handler handler = ((CameraDeviceCompatBaseImpl.CameraDeviceCompatParamsApi21) Preconditions.checkNotNull((CameraDeviceCompatBaseImpl.CameraDeviceCompatParamsApi21) this.mImplParams)).mCompatHandler;
        InputConfigurationCompat inputConfigCompat = config.getInputConfiguration();
        if (inputConfigCompat != null) {
            try {
                InputConfiguration inputConfig = (InputConfiguration) inputConfigCompat.unwrap();
                Preconditions.checkNotNull(inputConfig);
                this.mCameraDevice.createReprocessableCaptureSessionByConfigurations(inputConfig, SessionConfigurationCompat.transformFromCompat(outputs), cb, handler);
            } catch (CameraAccessException e) {
                throw CameraAccessExceptionCompat.toCameraAccessExceptionCompat(e);
            }
        } else if (config.getSessionType() == 1) {
            this.mCameraDevice.createConstrainedHighSpeedCaptureSession(unpackSurfaces(outputs), cb, handler);
        } else {
            this.mCameraDevice.createCaptureSessionByOutputConfigurations(SessionConfigurationCompat.transformFromCompat(outputs), cb, handler);
        }
    }
}

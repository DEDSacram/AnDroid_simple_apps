package androidx.camera.camera2.internal.compat;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import java.util.List;
import java.util.concurrent.Executor;

class CameraCaptureSessionCompatApi28Impl extends CameraCaptureSessionCompatBaseImpl {
    CameraCaptureSessionCompatApi28Impl(CameraCaptureSession captureSession) {
        super(captureSession, (Object) null);
    }

    public int captureBurstRequests(List<CaptureRequest> requests, Executor executor, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        return this.mCameraCaptureSession.captureBurstRequests(requests, executor, listener);
    }

    public int captureSingleRequest(CaptureRequest request, Executor executor, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        return this.mCameraCaptureSession.captureSingleRequest(request, executor, listener);
    }

    public int setRepeatingBurstRequests(List<CaptureRequest> requests, Executor executor, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        return this.mCameraCaptureSession.setRepeatingBurstRequests(requests, executor, listener);
    }

    public int setSingleRepeatingRequest(CaptureRequest request, Executor executor, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        return this.mCameraCaptureSession.setSingleRepeatingRequest(request, executor, listener);
    }
}

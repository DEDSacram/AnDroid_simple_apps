package androidx.camera.camera2.internal.compat;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import androidx.camera.camera2.internal.compat.CameraCaptureSessionCompat;
import androidx.core.util.Preconditions;
import java.util.List;
import java.util.concurrent.Executor;

class CameraCaptureSessionCompatBaseImpl implements CameraCaptureSessionCompat.CameraCaptureSessionCompatImpl {
    final CameraCaptureSession mCameraCaptureSession;
    final Object mObject;

    CameraCaptureSessionCompatBaseImpl(CameraCaptureSession captureSession, Object implParams) {
        this.mCameraCaptureSession = (CameraCaptureSession) Preconditions.checkNotNull(captureSession);
        this.mObject = implParams;
    }

    static CameraCaptureSessionCompat.CameraCaptureSessionCompatImpl create(CameraCaptureSession captureSession, Handler compatHandler) {
        return new CameraCaptureSessionCompatBaseImpl(captureSession, new CameraCaptureSessionCompatParamsApi21(compatHandler));
    }

    public int captureBurstRequests(List<CaptureRequest> requests, Executor executor, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        return this.mCameraCaptureSession.captureBurst(requests, new CameraCaptureSessionCompat.CaptureCallbackExecutorWrapper(executor, listener), ((CameraCaptureSessionCompatParamsApi21) this.mObject).mCompatHandler);
    }

    public int captureSingleRequest(CaptureRequest request, Executor executor, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        return this.mCameraCaptureSession.capture(request, new CameraCaptureSessionCompat.CaptureCallbackExecutorWrapper(executor, listener), ((CameraCaptureSessionCompatParamsApi21) this.mObject).mCompatHandler);
    }

    public int setRepeatingBurstRequests(List<CaptureRequest> requests, Executor executor, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        return this.mCameraCaptureSession.setRepeatingBurst(requests, new CameraCaptureSessionCompat.CaptureCallbackExecutorWrapper(executor, listener), ((CameraCaptureSessionCompatParamsApi21) this.mObject).mCompatHandler);
    }

    public int setSingleRepeatingRequest(CaptureRequest request, Executor executor, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        return this.mCameraCaptureSession.setRepeatingRequest(request, new CameraCaptureSessionCompat.CaptureCallbackExecutorWrapper(executor, listener), ((CameraCaptureSessionCompatParamsApi21) this.mObject).mCompatHandler);
    }

    public CameraCaptureSession unwrap() {
        return this.mCameraCaptureSession;
    }

    private static class CameraCaptureSessionCompatParamsApi21 {
        final Handler mCompatHandler;

        CameraCaptureSessionCompatParamsApi21(Handler compatHandler) {
            this.mCompatHandler = compatHandler;
        }
    }
}

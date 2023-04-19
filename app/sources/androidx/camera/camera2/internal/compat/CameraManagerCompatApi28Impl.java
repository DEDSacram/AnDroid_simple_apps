package androidx.camera.camera2.internal.compat;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import java.util.concurrent.Executor;

class CameraManagerCompatApi28Impl extends CameraManagerCompatBaseImpl {
    CameraManagerCompatApi28Impl(Context context) {
        super(context, (Object) null);
    }

    static CameraManagerCompatApi28Impl create(Context context) {
        return new CameraManagerCompatApi28Impl(context);
    }

    public void registerAvailabilityCallback(Executor executor, CameraManager.AvailabilityCallback callback) {
        this.mCameraManager.registerAvailabilityCallback(executor, callback);
    }

    public void unregisterAvailabilityCallback(CameraManager.AvailabilityCallback callback) {
        this.mCameraManager.unregisterAvailabilityCallback(callback);
    }

    public void openCamera(String cameraId, Executor executor, CameraDevice.StateCallback callback) throws CameraAccessExceptionCompat {
        try {
            this.mCameraManager.openCamera(cameraId, executor, callback);
        } catch (CameraAccessException e) {
            throw CameraAccessExceptionCompat.toCameraAccessExceptionCompat(e);
        } catch (IllegalArgumentException | SecurityException e2) {
            throw e2;
        } catch (RuntimeException e3) {
            if (isDndFailCase(e3)) {
                throwDndException(e3);
            }
            throw e3;
        }
    }

    public CameraCharacteristics getCameraCharacteristics(String cameraId) throws CameraAccessExceptionCompat {
        try {
            return super.getCameraCharacteristics(cameraId);
        } catch (RuntimeException e) {
            if (isDndFailCase(e)) {
                throwDndException(e);
            }
            throw e;
        }
    }

    private void throwDndException(Throwable cause) throws CameraAccessExceptionCompat {
        throw new CameraAccessExceptionCompat((int) CameraAccessExceptionCompat.CAMERA_UNAVAILABLE_DO_NOT_DISTURB, cause);
    }

    private boolean isDndFailCase(Throwable throwable) {
        return Build.VERSION.SDK_INT == 28 && isDndRuntimeException(throwable);
    }

    private static boolean isDndRuntimeException(Throwable throwable) {
        if (!throwable.getClass().equals(RuntimeException.class)) {
            return false;
        }
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        StackTraceElement[] stackTraceElement = stackTrace;
        if (stackTrace == null || stackTraceElement.length < 0) {
            return false;
        }
        return "_enableShutterSound".equals(stackTraceElement[0].getMethodName());
    }
}

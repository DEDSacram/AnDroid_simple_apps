package androidx.camera.camera2.internal.compat;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import androidx.camera.camera2.internal.compat.CameraDeviceCompat;
import androidx.camera.camera2.internal.compat.CameraManagerCompat;
import androidx.core.util.Preconditions;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

class CameraManagerCompatBaseImpl implements CameraManagerCompat.CameraManagerCompatImpl {
    final CameraManager mCameraManager;
    final Object mObject;

    CameraManagerCompatBaseImpl(Context context, Object cameraManagerParams) {
        this.mCameraManager = (CameraManager) context.getSystemService("camera");
        this.mObject = cameraManagerParams;
    }

    static CameraManagerCompatBaseImpl create(Context context, Handler compatHandler) {
        return new CameraManagerCompatBaseImpl(context, new CameraManagerCompatParamsApi21(compatHandler));
    }

    public String[] getCameraIdList() throws CameraAccessExceptionCompat {
        try {
            return this.mCameraManager.getCameraIdList();
        } catch (CameraAccessException e) {
            throw CameraAccessExceptionCompat.toCameraAccessExceptionCompat(e);
        }
    }

    public void registerAvailabilityCallback(Executor executor, CameraManager.AvailabilityCallback callback) {
        if (executor != null) {
            CameraManagerCompat.AvailabilityCallbackExecutorWrapper wrapper = null;
            CameraManagerCompatParamsApi21 params = (CameraManagerCompatParamsApi21) this.mObject;
            if (callback != null) {
                synchronized (params.mWrapperMap) {
                    wrapper = params.mWrapperMap.get(callback);
                    if (wrapper == null) {
                        wrapper = new CameraManagerCompat.AvailabilityCallbackExecutorWrapper(executor, callback);
                        params.mWrapperMap.put(callback, wrapper);
                    }
                }
            }
            this.mCameraManager.registerAvailabilityCallback(wrapper, params.mCompatHandler);
            return;
        }
        throw new IllegalArgumentException("executor was null");
    }

    public void unregisterAvailabilityCallback(CameraManager.AvailabilityCallback callback) {
        CameraManagerCompat.AvailabilityCallbackExecutorWrapper wrapper = null;
        if (callback != null) {
            CameraManagerCompatParamsApi21 params = (CameraManagerCompatParamsApi21) this.mObject;
            synchronized (params.mWrapperMap) {
                wrapper = params.mWrapperMap.remove(callback);
            }
        }
        if (wrapper != null) {
            wrapper.setDisabled();
        }
        this.mCameraManager.unregisterAvailabilityCallback(wrapper);
    }

    public CameraCharacteristics getCameraCharacteristics(String cameraId) throws CameraAccessExceptionCompat {
        try {
            return this.mCameraManager.getCameraCharacteristics(cameraId);
        } catch (CameraAccessException e) {
            throw CameraAccessExceptionCompat.toCameraAccessExceptionCompat(e);
        }
    }

    public void openCamera(String cameraId, Executor executor, CameraDevice.StateCallback callback) throws CameraAccessExceptionCompat {
        Preconditions.checkNotNull(executor);
        Preconditions.checkNotNull(callback);
        try {
            this.mCameraManager.openCamera(cameraId, new CameraDeviceCompat.StateCallbackExecutorWrapper(executor, callback), ((CameraManagerCompatParamsApi21) this.mObject).mCompatHandler);
        } catch (CameraAccessException e) {
            throw CameraAccessExceptionCompat.toCameraAccessExceptionCompat(e);
        }
    }

    public CameraManager getCameraManager() {
        return this.mCameraManager;
    }

    static final class CameraManagerCompatParamsApi21 {
        final Handler mCompatHandler;
        final Map<CameraManager.AvailabilityCallback, CameraManagerCompat.AvailabilityCallbackExecutorWrapper> mWrapperMap = new HashMap();

        CameraManagerCompatParamsApi21(Handler compatHandler) {
            this.mCompatHandler = compatHandler;
        }
    }
}

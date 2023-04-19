package androidx.camera.camera2.internal.compat;

import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Handler;
import android.util.ArrayMap;
import androidx.camera.camera2.internal.compat.ApiCompat;
import androidx.camera.core.impl.utils.MainThreadAsyncHandler;
import java.util.Map;
import java.util.concurrent.Executor;

public final class CameraManagerCompat {
    private final Map<String, CameraCharacteristicsCompat> mCameraCharacteristicsMap = new ArrayMap(4);
    private final CameraManagerCompatImpl mImpl;

    private CameraManagerCompat(CameraManagerCompatImpl impl) {
        this.mImpl = impl;
    }

    public static CameraManagerCompat from(Context context) {
        return from(context, MainThreadAsyncHandler.getInstance());
    }

    public static CameraManagerCompat from(Context context, Handler compatHandler) {
        return new CameraManagerCompat(CameraManagerCompatImpl.from(context, compatHandler));
    }

    public static CameraManagerCompat from(CameraManagerCompatImpl impl) {
        return new CameraManagerCompat(impl);
    }

    public String[] getCameraIdList() throws CameraAccessExceptionCompat {
        return this.mImpl.getCameraIdList();
    }

    public void registerAvailabilityCallback(Executor executor, CameraManager.AvailabilityCallback callback) {
        this.mImpl.registerAvailabilityCallback(executor, callback);
    }

    public void unregisterAvailabilityCallback(CameraManager.AvailabilityCallback callback) {
        this.mImpl.unregisterAvailabilityCallback(callback);
    }

    public CameraCharacteristicsCompat getCameraCharacteristicsCompat(String cameraId) throws CameraAccessExceptionCompat {
        CameraCharacteristicsCompat characteristics;
        synchronized (this.mCameraCharacteristicsMap) {
            characteristics = this.mCameraCharacteristicsMap.get(cameraId);
            if (characteristics == null) {
                try {
                    characteristics = CameraCharacteristicsCompat.toCameraCharacteristicsCompat(this.mImpl.getCameraCharacteristics(cameraId));
                    this.mCameraCharacteristicsMap.put(cameraId, characteristics);
                } catch (AssertionError e) {
                    throw new CameraAccessExceptionCompat(CameraAccessExceptionCompat.CAMERA_CHARACTERISTICS_CREATION_ERROR, e.getMessage(), e);
                }
            }
        }
        return characteristics;
    }

    public void openCamera(String cameraId, Executor executor, CameraDevice.StateCallback callback) throws CameraAccessExceptionCompat {
        this.mImpl.openCamera(cameraId, executor, callback);
    }

    public CameraManager unwrap() {
        return this.mImpl.getCameraManager();
    }

    public interface CameraManagerCompatImpl {
        CameraCharacteristics getCameraCharacteristics(String str) throws CameraAccessExceptionCompat;

        String[] getCameraIdList() throws CameraAccessExceptionCompat;

        CameraManager getCameraManager();

        void openCamera(String str, Executor executor, CameraDevice.StateCallback stateCallback) throws CameraAccessExceptionCompat;

        void registerAvailabilityCallback(Executor executor, CameraManager.AvailabilityCallback availabilityCallback);

        void unregisterAvailabilityCallback(CameraManager.AvailabilityCallback availabilityCallback);

        static CameraManagerCompatImpl from(Context context, Handler compatHandler) {
            if (Build.VERSION.SDK_INT >= 29) {
                return new CameraManagerCompatApi29Impl(context);
            }
            if (Build.VERSION.SDK_INT >= 28) {
                return CameraManagerCompatApi28Impl.create(context);
            }
            return CameraManagerCompatBaseImpl.create(context, compatHandler);
        }
    }

    static final class AvailabilityCallbackExecutorWrapper extends CameraManager.AvailabilityCallback {
        private boolean mDisabled = false;
        private final Executor mExecutor;
        private final Object mLock = new Object();
        final CameraManager.AvailabilityCallback mWrappedCallback;

        AvailabilityCallbackExecutorWrapper(Executor executor, CameraManager.AvailabilityCallback wrappedCallback) {
            this.mExecutor = executor;
            this.mWrappedCallback = wrappedCallback;
        }

        /* access modifiers changed from: package-private */
        public void setDisabled() {
            synchronized (this.mLock) {
                this.mDisabled = true;
            }
        }

        public void onCameraAccessPrioritiesChanged() {
            synchronized (this.mLock) {
                if (!this.mDisabled) {
                    this.mExecutor.execute(new CameraManagerCompat$AvailabilityCallbackExecutorWrapper$$ExternalSyntheticLambda0(this));
                }
            }
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onCameraAccessPrioritiesChanged$0$androidx-camera-camera2-internal-compat-CameraManagerCompat$AvailabilityCallbackExecutorWrapper  reason: not valid java name */
        public /* synthetic */ void m115lambda$onCameraAccessPrioritiesChanged$0$androidxcameracamera2internalcompatCameraManagerCompat$AvailabilityCallbackExecutorWrapper() {
            ApiCompat.Api29Impl.onCameraAccessPrioritiesChanged(this.mWrappedCallback);
        }

        public void onCameraAvailable(String cameraId) {
            synchronized (this.mLock) {
                if (!this.mDisabled) {
                    this.mExecutor.execute(new CameraManagerCompat$AvailabilityCallbackExecutorWrapper$$ExternalSyntheticLambda1(this, cameraId));
                }
            }
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onCameraAvailable$1$androidx-camera-camera2-internal-compat-CameraManagerCompat$AvailabilityCallbackExecutorWrapper  reason: not valid java name */
        public /* synthetic */ void m116lambda$onCameraAvailable$1$androidxcameracamera2internalcompatCameraManagerCompat$AvailabilityCallbackExecutorWrapper(String cameraId) {
            this.mWrappedCallback.onCameraAvailable(cameraId);
        }

        public void onCameraUnavailable(String cameraId) {
            synchronized (this.mLock) {
                if (!this.mDisabled) {
                    this.mExecutor.execute(new CameraManagerCompat$AvailabilityCallbackExecutorWrapper$$ExternalSyntheticLambda2(this, cameraId));
                }
            }
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onCameraUnavailable$2$androidx-camera-camera2-internal-compat-CameraManagerCompat$AvailabilityCallbackExecutorWrapper  reason: not valid java name */
        public /* synthetic */ void m117lambda$onCameraUnavailable$2$androidxcameracamera2internalcompatCameraManagerCompat$AvailabilityCallbackExecutorWrapper(String cameraId) {
            this.mWrappedCallback.onCameraUnavailable(cameraId);
        }
    }
}

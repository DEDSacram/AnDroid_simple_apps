package androidx.camera.camera2.internal.compat;

import android.hardware.camera2.CameraDevice;
import android.os.Build;
import android.os.Handler;
import androidx.camera.camera2.internal.compat.params.SessionConfigurationCompat;
import androidx.camera.core.impl.utils.MainThreadAsyncHandler;
import java.util.concurrent.Executor;

public final class CameraDeviceCompat {
    public static final int SESSION_OPERATION_MODE_CONSTRAINED_HIGH_SPEED = 1;
    public static final int SESSION_OPERATION_MODE_NORMAL = 0;
    private final CameraDeviceCompatImpl mImpl;

    interface CameraDeviceCompatImpl {
        void createCaptureSession(SessionConfigurationCompat sessionConfigurationCompat) throws CameraAccessExceptionCompat;

        CameraDevice unwrap();
    }

    private CameraDeviceCompat(CameraDevice cameraDevice, Handler compatHandler) {
        if (Build.VERSION.SDK_INT >= 28) {
            this.mImpl = new CameraDeviceCompatApi28Impl(cameraDevice);
        } else if (Build.VERSION.SDK_INT >= 24) {
            this.mImpl = CameraDeviceCompatApi24Impl.create(cameraDevice, compatHandler);
        } else if (Build.VERSION.SDK_INT >= 23) {
            this.mImpl = CameraDeviceCompatApi23Impl.create(cameraDevice, compatHandler);
        } else {
            this.mImpl = CameraDeviceCompatBaseImpl.create(cameraDevice, compatHandler);
        }
    }

    public static CameraDeviceCompat toCameraDeviceCompat(CameraDevice captureSession) {
        return toCameraDeviceCompat(captureSession, MainThreadAsyncHandler.getInstance());
    }

    public static CameraDeviceCompat toCameraDeviceCompat(CameraDevice cameraDevice, Handler compatHandler) {
        return new CameraDeviceCompat(cameraDevice, compatHandler);
    }

    public CameraDevice toCameraDevice() {
        return this.mImpl.unwrap();
    }

    public void createCaptureSession(SessionConfigurationCompat config) throws CameraAccessExceptionCompat {
        this.mImpl.createCaptureSession(config);
    }

    static final class StateCallbackExecutorWrapper extends CameraDevice.StateCallback {
        private final Executor mExecutor;
        final CameraDevice.StateCallback mWrappedCallback;

        StateCallbackExecutorWrapper(Executor executor, CameraDevice.StateCallback wrappedCallback) {
            this.mExecutor = executor;
            this.mWrappedCallback = wrappedCallback;
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onOpened$0$androidx-camera-camera2-internal-compat-CameraDeviceCompat$StateCallbackExecutorWrapper  reason: not valid java name */
        public /* synthetic */ void m114lambda$onOpened$0$androidxcameracamera2internalcompatCameraDeviceCompat$StateCallbackExecutorWrapper(CameraDevice camera) {
            this.mWrappedCallback.onOpened(camera);
        }

        public void onOpened(CameraDevice camera) {
            this.mExecutor.execute(new CameraDeviceCompat$StateCallbackExecutorWrapper$$ExternalSyntheticLambda2(this, camera));
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onDisconnected$1$androidx-camera-camera2-internal-compat-CameraDeviceCompat$StateCallbackExecutorWrapper  reason: not valid java name */
        public /* synthetic */ void m112lambda$onDisconnected$1$androidxcameracamera2internalcompatCameraDeviceCompat$StateCallbackExecutorWrapper(CameraDevice camera) {
            this.mWrappedCallback.onDisconnected(camera);
        }

        public void onDisconnected(CameraDevice camera) {
            this.mExecutor.execute(new CameraDeviceCompat$StateCallbackExecutorWrapper$$ExternalSyntheticLambda1(this, camera));
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onError$2$androidx-camera-camera2-internal-compat-CameraDeviceCompat$StateCallbackExecutorWrapper  reason: not valid java name */
        public /* synthetic */ void m113lambda$onError$2$androidxcameracamera2internalcompatCameraDeviceCompat$StateCallbackExecutorWrapper(CameraDevice camera, int error) {
            this.mWrappedCallback.onError(camera, error);
        }

        public void onError(CameraDevice camera, int error) {
            this.mExecutor.execute(new CameraDeviceCompat$StateCallbackExecutorWrapper$$ExternalSyntheticLambda3(this, camera, error));
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onClosed$3$androidx-camera-camera2-internal-compat-CameraDeviceCompat$StateCallbackExecutorWrapper  reason: not valid java name */
        public /* synthetic */ void m111lambda$onClosed$3$androidxcameracamera2internalcompatCameraDeviceCompat$StateCallbackExecutorWrapper(CameraDevice camera) {
            this.mWrappedCallback.onClosed(camera);
        }

        public void onClosed(CameraDevice camera) {
            this.mExecutor.execute(new CameraDeviceCompat$StateCallbackExecutorWrapper$$ExternalSyntheticLambda0(this, camera));
        }
    }
}

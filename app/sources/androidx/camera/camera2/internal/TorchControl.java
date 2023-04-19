package androidx.camera.camera2.internal;

import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import androidx.camera.camera2.internal.compat.CameraCharacteristicsCompat;
import androidx.camera.camera2.internal.compat.workaround.FlashAvailabilityChecker;
import androidx.camera.core.CameraControl;
import androidx.camera.core.Logger;
import androidx.camera.core.impl.utils.Threads;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Executor;

final class TorchControl {
    static final int DEFAULT_TORCH_STATE = 0;
    private static final String TAG = "TorchControl";
    private final Camera2CameraControlImpl mCamera2CameraControlImpl;
    CallbackToFutureAdapter.Completer<Void> mEnableTorchCompleter;
    private final Executor mExecutor;
    private final boolean mHasFlashUnit;
    private boolean mIsActive;
    boolean mTargetTorchEnabled;
    private final MutableLiveData<Integer> mTorchState = new MutableLiveData<>(0);

    TorchControl(Camera2CameraControlImpl camera2CameraControlImpl, CameraCharacteristicsCompat cameraCharacteristics, Executor executor) {
        this.mCamera2CameraControlImpl = camera2CameraControlImpl;
        this.mExecutor = executor;
        this.mHasFlashUnit = FlashAvailabilityChecker.isFlashAvailable(cameraCharacteristics);
        camera2CameraControlImpl.addCaptureResultListener(new TorchControl$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$new$0$androidx-camera-camera2-internal-TorchControl  reason: not valid java name */
    public /* synthetic */ boolean m91lambda$new$0$androidxcameracamera2internalTorchControl(TotalCaptureResult captureResult) {
        if (this.mEnableTorchCompleter != null) {
            Integer flashMode = (Integer) captureResult.getRequest().get(CaptureRequest.FLASH_MODE);
            if ((flashMode != null && flashMode.intValue() == 2) == this.mTargetTorchEnabled) {
                this.mEnableTorchCompleter.set(null);
                this.mEnableTorchCompleter = null;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void setActive(boolean isActive) {
        if (this.mIsActive != isActive) {
            this.mIsActive = isActive;
            if (!isActive) {
                if (this.mTargetTorchEnabled) {
                    this.mTargetTorchEnabled = false;
                    this.mCamera2CameraControlImpl.enableTorchInternal(false);
                    setLiveDataValue(this.mTorchState, 0);
                }
                CallbackToFutureAdapter.Completer<Void> completer = this.mEnableTorchCompleter;
                if (completer != null) {
                    completer.setException(new CameraControl.OperationCanceledException("Camera is not active."));
                    this.mEnableTorchCompleter = null;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public ListenableFuture<Void> enableTorch(boolean enabled) {
        if (!this.mHasFlashUnit) {
            Logger.d(TAG, "Unable to enableTorch due to there is no flash unit.");
            return Futures.immediateFailedFuture(new IllegalStateException("No flash unit"));
        }
        setLiveDataValue(this.mTorchState, Integer.valueOf(enabled));
        return CallbackToFutureAdapter.getFuture(new TorchControl$$ExternalSyntheticLambda1(this, enabled));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$enableTorch$2$androidx-camera-camera2-internal-TorchControl  reason: not valid java name */
    public /* synthetic */ Object m90lambda$enableTorch$2$androidxcameracamera2internalTorchControl(boolean enabled, CallbackToFutureAdapter.Completer completer) throws Exception {
        this.mExecutor.execute(new TorchControl$$ExternalSyntheticLambda2(this, completer, enabled));
        return "enableTorch: " + enabled;
    }

    /* access modifiers changed from: package-private */
    public LiveData<Integer> getTorchState() {
        return this.mTorchState;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: enableTorchInternal */
    public void m89lambda$enableTorch$1$androidxcameracamera2internalTorchControl(CallbackToFutureAdapter.Completer<Void> completer, boolean enabled) {
        if (!this.mHasFlashUnit) {
            if (completer != null) {
                completer.setException(new IllegalStateException("No flash unit"));
            }
        } else if (!this.mIsActive) {
            setLiveDataValue(this.mTorchState, 0);
            if (completer != null) {
                completer.setException(new CameraControl.OperationCanceledException("Camera is not active."));
            }
        } else {
            this.mTargetTorchEnabled = enabled;
            this.mCamera2CameraControlImpl.enableTorchInternal(enabled);
            setLiveDataValue(this.mTorchState, Integer.valueOf(enabled));
            CallbackToFutureAdapter.Completer<Void> completer2 = this.mEnableTorchCompleter;
            if (completer2 != null) {
                completer2.setException(new CameraControl.OperationCanceledException("There is a new enableTorch being set"));
            }
            this.mEnableTorchCompleter = completer;
        }
    }

    private <T> void setLiveDataValue(MutableLiveData<T> liveData, T value) {
        if (Threads.isMainThread()) {
            liveData.setValue(value);
        } else {
            liveData.postValue(value);
        }
    }
}

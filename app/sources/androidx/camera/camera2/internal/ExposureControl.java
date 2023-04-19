package androidx.camera.camera2.internal;

import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.util.Range;
import androidx.camera.camera2.impl.Camera2ImplConfig;
import androidx.camera.camera2.internal.Camera2CameraControlImpl;
import androidx.camera.camera2.internal.compat.CameraCharacteristicsCompat;
import androidx.camera.core.CameraControl;
import androidx.camera.core.ExposureState;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.util.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Executor;

public class ExposureControl {
    private static final int DEFAULT_EXPOSURE_COMPENSATION = 0;
    private final Camera2CameraControlImpl mCameraControl;
    private final Executor mExecutor;
    private final ExposureStateImpl mExposureStateImpl;
    private boolean mIsActive = false;
    private Camera2CameraControlImpl.CaptureResultListener mRunningCaptureResultListener;
    private CallbackToFutureAdapter.Completer<Integer> mRunningCompleter;

    ExposureControl(Camera2CameraControlImpl cameraControl, CameraCharacteristicsCompat cameraCharacteristics, Executor executor) {
        this.mCameraControl = cameraControl;
        this.mExposureStateImpl = new ExposureStateImpl(cameraCharacteristics, 0);
        this.mExecutor = executor;
    }

    static ExposureState getDefaultExposureState(CameraCharacteristicsCompat cameraCharacteristics) {
        return new ExposureStateImpl(cameraCharacteristics, 0);
    }

    /* access modifiers changed from: package-private */
    public void setActive(boolean isActive) {
        if (isActive != this.mIsActive) {
            this.mIsActive = isActive;
            if (!isActive) {
                this.mExposureStateImpl.setExposureCompensationIndex(0);
                clearRunningTask();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setCaptureRequestOption(Camera2ImplConfig.Builder configBuilder) {
        configBuilder.setCaptureRequestOption(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, Integer.valueOf(this.mExposureStateImpl.getExposureCompensationIndex()));
    }

    /* access modifiers changed from: package-private */
    public ExposureState getExposureState() {
        return this.mExposureStateImpl;
    }

    /* access modifiers changed from: package-private */
    public ListenableFuture<Integer> setExposureCompensationIndex(int exposure) {
        if (!this.mExposureStateImpl.isExposureCompensationSupported()) {
            return Futures.immediateFailedFuture(new IllegalArgumentException("ExposureCompensation is not supported"));
        }
        Range<Integer> range = this.mExposureStateImpl.getExposureCompensationRange();
        if (!range.contains(Integer.valueOf(exposure))) {
            return Futures.immediateFailedFuture(new IllegalArgumentException("Requested ExposureCompensation " + exposure + " is not within valid range [" + range.getUpper() + ".." + range.getLower() + "]"));
        }
        this.mExposureStateImpl.setExposureCompensationIndex(exposure);
        return Futures.nonCancellationPropagating(CallbackToFutureAdapter.getFuture(new ExposureControl$$ExternalSyntheticLambda1(this, exposure)));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$setExposureCompensationIndex$2$androidx-camera-camera2-internal-ExposureControl  reason: not valid java name */
    public /* synthetic */ Object m62lambda$setExposureCompensationIndex$2$androidxcameracamera2internalExposureControl(int exposure, CallbackToFutureAdapter.Completer completer) throws Exception {
        this.mExecutor.execute(new ExposureControl$$ExternalSyntheticLambda2(this, completer, exposure));
        return "setExposureCompensationIndex[" + exposure + "]";
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$setExposureCompensationIndex$1$androidx-camera-camera2-internal-ExposureControl  reason: not valid java name */
    public /* synthetic */ void m61lambda$setExposureCompensationIndex$1$androidxcameracamera2internalExposureControl(CallbackToFutureAdapter.Completer completer, int exposure) {
        boolean z = false;
        if (!this.mIsActive) {
            this.mExposureStateImpl.setExposureCompensationIndex(0);
            completer.setException(new CameraControl.OperationCanceledException("Camera is not active."));
            return;
        }
        clearRunningTask();
        Preconditions.checkState(this.mRunningCompleter == null, "mRunningCompleter should be null when starting set a new exposure compensation value");
        if (this.mRunningCaptureResultListener == null) {
            z = true;
        }
        Preconditions.checkState(z, "mRunningCaptureResultListener should be null when starting set a new exposure compensation value");
        ExposureControl$$ExternalSyntheticLambda0 exposureControl$$ExternalSyntheticLambda0 = new ExposureControl$$ExternalSyntheticLambda0(exposure, completer);
        this.mRunningCaptureResultListener = exposureControl$$ExternalSyntheticLambda0;
        this.mRunningCompleter = completer;
        this.mCameraControl.addCaptureResultListener(exposureControl$$ExternalSyntheticLambda0);
        this.mCameraControl.updateSessionConfigSynchronous();
    }

    static /* synthetic */ boolean lambda$setExposureCompensationIndex$0(int exposure, CallbackToFutureAdapter.Completer completer, TotalCaptureResult captureResult) {
        Integer state = (Integer) captureResult.get(CaptureResult.CONTROL_AE_STATE);
        Integer evResult = (Integer) captureResult.get(CaptureResult.CONTROL_AE_EXPOSURE_COMPENSATION);
        if (state != null && evResult != null) {
            switch (state.intValue()) {
                case 2:
                case 3:
                case 4:
                    if (evResult.intValue() != exposure) {
                        return false;
                    }
                    completer.set(Integer.valueOf(exposure));
                    return true;
                default:
                    return false;
            }
        } else if (evResult == null || evResult.intValue() != exposure) {
            return false;
        } else {
            completer.set(Integer.valueOf(exposure));
            return true;
        }
    }

    private void clearRunningTask() {
        CallbackToFutureAdapter.Completer<Integer> completer = this.mRunningCompleter;
        if (completer != null) {
            completer.setException(new CameraControl.OperationCanceledException("Cancelled by another setExposureCompensationIndex()"));
            this.mRunningCompleter = null;
        }
        Camera2CameraControlImpl.CaptureResultListener captureResultListener = this.mRunningCaptureResultListener;
        if (captureResultListener != null) {
            this.mCameraControl.removeCaptureResultListener(captureResultListener);
            this.mRunningCaptureResultListener = null;
        }
    }
}

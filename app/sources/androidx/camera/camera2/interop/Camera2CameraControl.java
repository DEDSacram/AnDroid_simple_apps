package androidx.camera.camera2.interop;

import android.hardware.camera2.TotalCaptureResult;
import androidx.camera.camera2.impl.Camera2ImplConfig;
import androidx.camera.camera2.internal.Camera2CameraControlImpl;
import androidx.camera.camera2.interop.CaptureRequestOptions;
import androidx.camera.core.CameraControl;
import androidx.camera.core.impl.Config;
import androidx.camera.core.impl.TagBundle;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.util.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Executor;

public final class Camera2CameraControl {
    public static final String TAG_KEY = "Camera2CameraControl";
    private Camera2ImplConfig.Builder mBuilder = new Camera2ImplConfig.Builder();
    private final Camera2CameraControlImpl mCamera2CameraControlImpl;
    private final Camera2CameraControlImpl.CaptureResultListener mCaptureResultListener = new Camera2CameraControl$$ExternalSyntheticLambda0(this);
    CallbackToFutureAdapter.Completer<Void> mCompleter;
    final Executor mExecutor;
    private boolean mIsActive = false;
    final Object mLock = new Object();
    private boolean mPendingUpdate = false;

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$new$0$androidx-camera-camera2-interop-Camera2CameraControl  reason: not valid java name */
    public /* synthetic */ boolean m123lambda$new$0$androidxcameracamera2interopCamera2CameraControl(TotalCaptureResult captureResult) {
        Integer tagInteger;
        CallbackToFutureAdapter.Completer<Void> completerToSet = null;
        if (this.mCompleter != null) {
            Object tag = captureResult.getRequest().getTag();
            if ((tag instanceof TagBundle) && (tagInteger = (Integer) ((TagBundle) tag).getTag(TAG_KEY)) != null && tagInteger.equals(Integer.valueOf(this.mCompleter.hashCode()))) {
                completerToSet = this.mCompleter;
                this.mCompleter = null;
            }
        }
        if (completerToSet == null) {
            return false;
        }
        completerToSet.set(null);
        return false;
    }

    public Camera2CameraControl(Camera2CameraControlImpl camera2CameraControlImpl, Executor executor) {
        this.mCamera2CameraControlImpl = camera2CameraControlImpl;
        this.mExecutor = executor;
    }

    public Camera2CameraControlImpl.CaptureResultListener getCaptureRequestListener() {
        return this.mCaptureResultListener;
    }

    public static Camera2CameraControl from(CameraControl cameraControl) {
        Preconditions.checkArgument(cameraControl instanceof Camera2CameraControlImpl, "CameraControl doesn't contain Camera2 implementation.");
        return ((Camera2CameraControlImpl) cameraControl).getCamera2CameraControl();
    }

    public ListenableFuture<Void> setCaptureRequestOptions(CaptureRequestOptions bundle) {
        clearCaptureRequestOptionsInternal();
        addCaptureRequestOptionsInternal(bundle);
        return Futures.nonCancellationPropagating(CallbackToFutureAdapter.getFuture(new Camera2CameraControl$$ExternalSyntheticLambda3(this)));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$setCaptureRequestOptions$2$androidx-camera-camera2-interop-Camera2CameraControl  reason: not valid java name */
    public /* synthetic */ Object m126lambda$setCaptureRequestOptions$2$androidxcameracamera2interopCamera2CameraControl(CallbackToFutureAdapter.Completer completer) throws Exception {
        this.mExecutor.execute(new Camera2CameraControl$$ExternalSyntheticLambda6(this, completer));
        return "setCaptureRequestOptions";
    }

    public ListenableFuture<Void> addCaptureRequestOptions(CaptureRequestOptions bundle) {
        addCaptureRequestOptionsInternal(bundle);
        return Futures.nonCancellationPropagating(CallbackToFutureAdapter.getFuture(new Camera2CameraControl$$ExternalSyntheticLambda1(this)));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$addCaptureRequestOptions$4$androidx-camera-camera2-interop-Camera2CameraControl  reason: not valid java name */
    public /* synthetic */ Object m120lambda$addCaptureRequestOptions$4$androidxcameracamera2interopCamera2CameraControl(CallbackToFutureAdapter.Completer completer) throws Exception {
        this.mExecutor.execute(new Camera2CameraControl$$ExternalSyntheticLambda4(this, completer));
        return "addCaptureRequestOptions";
    }

    public CaptureRequestOptions getCaptureRequestOptions() {
        CaptureRequestOptions build;
        synchronized (this.mLock) {
            build = CaptureRequestOptions.Builder.from(this.mBuilder.build()).build();
        }
        return build;
    }

    public ListenableFuture<Void> clearCaptureRequestOptions() {
        clearCaptureRequestOptionsInternal();
        return Futures.nonCancellationPropagating(CallbackToFutureAdapter.getFuture(new Camera2CameraControl$$ExternalSyntheticLambda2(this)));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$clearCaptureRequestOptions$6$androidx-camera-camera2-interop-Camera2CameraControl  reason: not valid java name */
    public /* synthetic */ Object m122lambda$clearCaptureRequestOptions$6$androidxcameracamera2interopCamera2CameraControl(CallbackToFutureAdapter.Completer completer) throws Exception {
        this.mExecutor.execute(new Camera2CameraControl$$ExternalSyntheticLambda5(this, completer));
        return "clearCaptureRequestOptions";
    }

    public Camera2ImplConfig getCamera2ImplConfig() {
        Camera2ImplConfig build;
        synchronized (this.mLock) {
            if (this.mCompleter != null) {
                this.mBuilder.getMutableConfig().insertOption(Camera2ImplConfig.CAPTURE_REQUEST_TAG_OPTION, Integer.valueOf(this.mCompleter.hashCode()));
            }
            build = this.mBuilder.build();
        }
        return build;
    }

    private void addCaptureRequestOptionsInternal(CaptureRequestOptions bundle) {
        synchronized (this.mLock) {
            for (Config.Option<?> next : bundle.listOptions()) {
                this.mBuilder.getMutableConfig().insertOption(next, bundle.retrieveOption(next));
            }
        }
    }

    private void clearCaptureRequestOptionsInternal() {
        synchronized (this.mLock) {
            this.mBuilder = new Camera2ImplConfig.Builder();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: updateConfig */
    public void m125lambda$setCaptureRequestOptions$1$androidxcameracamera2interopCamera2CameraControl(CallbackToFutureAdapter.Completer<Void> completer) {
        this.mPendingUpdate = true;
        CallbackToFutureAdapter.Completer<Void> completerToCancel = null;
        if (this.mCompleter != null) {
            completerToCancel = this.mCompleter;
        }
        this.mCompleter = completer;
        if (this.mIsActive) {
            updateSession();
        }
        if (completerToCancel != null) {
            completerToCancel.setException(new CameraControl.OperationCanceledException("Camera2CameraControl was updated with new options."));
        }
    }

    private void updateSession() {
        this.mCamera2CameraControlImpl.updateSessionConfig();
        this.mPendingUpdate = false;
    }

    public void setActive(boolean isActive) {
        this.mExecutor.execute(new Camera2CameraControl$$ExternalSyntheticLambda7(this, isActive));
    }

    /* access modifiers changed from: private */
    /* renamed from: setActiveInternal */
    public void m124lambda$setActive$7$androidxcameracamera2interopCamera2CameraControl(boolean isActive) {
        if (this.mIsActive != isActive) {
            this.mIsActive = isActive;
            if (!isActive) {
                CallbackToFutureAdapter.Completer<Void> completer = this.mCompleter;
                if (completer != null) {
                    completer.setException(new CameraControl.OperationCanceledException("The camera control has became inactive."));
                    this.mCompleter = null;
                }
            } else if (this.mPendingUpdate) {
                updateSession();
            }
        }
    }
}

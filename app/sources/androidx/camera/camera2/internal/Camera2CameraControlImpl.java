package androidx.camera.camera2.internal;

import android.graphics.Rect;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Build;
import android.util.ArrayMap;
import android.util.Rational;
import androidx.camera.camera2.impl.Camera2ImplConfig;
import androidx.camera.camera2.internal.compat.CameraCharacteristicsCompat;
import androidx.camera.camera2.internal.compat.workaround.AeFpsRange;
import androidx.camera.camera2.internal.compat.workaround.AutoFlashAEModeDisabler;
import androidx.camera.camera2.interop.Camera2CameraControl;
import androidx.camera.camera2.interop.CaptureRequestOptions;
import androidx.camera.core.CameraControl;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.FocusMeteringResult;
import androidx.camera.core.Logger;
import androidx.camera.core.impl.CameraCaptureCallback;
import androidx.camera.core.impl.CameraCaptureFailure;
import androidx.camera.core.impl.CameraCaptureResult;
import androidx.camera.core.impl.CameraControlInternal;
import androidx.camera.core.impl.CaptureConfig;
import androidx.camera.core.impl.Config;
import androidx.camera.core.impl.Quirks;
import androidx.camera.core.impl.SessionConfig;
import androidx.camera.core.impl.TagBundle;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.impl.utils.futures.FutureChain;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.util.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

public class Camera2CameraControlImpl implements CameraControlInternal {
    private static final int DEFAULT_TEMPLATE = 1;
    private static final String TAG = "Camera2CameraControlImp";
    static final String TAG_SESSION_UPDATE_ID = "CameraControlSessionUpdateId";
    private final AeFpsRange mAeFpsRange;
    private final AutoFlashAEModeDisabler mAutoFlashAEModeDisabler;
    private final Camera2CameraControl mCamera2CameraControl;
    private final Camera2CapturePipeline mCamera2CapturePipeline;
    private final CameraCaptureCallbackSet mCameraCaptureCallbackSet;
    private final CameraCharacteristicsCompat mCameraCharacteristics;
    private final CameraControlInternal.ControlUpdateCallback mControlUpdateCallback;
    private long mCurrentSessionUpdateId;
    final Executor mExecutor;
    private final ExposureControl mExposureControl;
    private volatile int mFlashMode;
    private volatile ListenableFuture<Void> mFlashModeChangeSessionUpdateFuture;
    private final FocusMeteringControl mFocusMeteringControl;
    private volatile boolean mIsTorchOn;
    private final Object mLock;
    private final AtomicLong mNextSessionUpdateId;
    final CameraControlSessionCallback mSessionCallback;
    private final SessionConfig.Builder mSessionConfigBuilder;
    private int mTemplate;
    private final TorchControl mTorchControl;
    private int mUseCount;
    private final ZoomControl mZoomControl;
    ZslControl mZslControl;

    public interface CaptureResultListener {
        boolean onCaptureResult(TotalCaptureResult totalCaptureResult);
    }

    Camera2CameraControlImpl(CameraCharacteristicsCompat cameraCharacteristics, ScheduledExecutorService scheduler, Executor executor, CameraControlInternal.ControlUpdateCallback controlUpdateCallback) {
        this(cameraCharacteristics, scheduler, executor, controlUpdateCallback, new Quirks(new ArrayList()));
    }

    Camera2CameraControlImpl(CameraCharacteristicsCompat cameraCharacteristics, ScheduledExecutorService scheduler, Executor executor, CameraControlInternal.ControlUpdateCallback controlUpdateCallback, Quirks cameraQuirks) {
        this.mLock = new Object();
        SessionConfig.Builder builder = new SessionConfig.Builder();
        this.mSessionConfigBuilder = builder;
        this.mUseCount = 0;
        this.mIsTorchOn = false;
        this.mFlashMode = 2;
        this.mNextSessionUpdateId = new AtomicLong(0);
        this.mFlashModeChangeSessionUpdateFuture = Futures.immediateFuture(null);
        this.mTemplate = 1;
        this.mCurrentSessionUpdateId = 0;
        CameraCaptureCallbackSet cameraCaptureCallbackSet = new CameraCaptureCallbackSet();
        this.mCameraCaptureCallbackSet = cameraCaptureCallbackSet;
        this.mCameraCharacteristics = cameraCharacteristics;
        this.mControlUpdateCallback = controlUpdateCallback;
        this.mExecutor = executor;
        CameraControlSessionCallback cameraControlSessionCallback = new CameraControlSessionCallback(executor);
        this.mSessionCallback = cameraControlSessionCallback;
        builder.setTemplateType(this.mTemplate);
        builder.addRepeatingCameraCaptureCallback(CaptureCallbackContainer.create(cameraControlSessionCallback));
        builder.addRepeatingCameraCaptureCallback(cameraCaptureCallbackSet);
        this.mExposureControl = new ExposureControl(this, cameraCharacteristics, executor);
        this.mFocusMeteringControl = new FocusMeteringControl(this, scheduler, executor, cameraQuirks);
        this.mZoomControl = new ZoomControl(this, cameraCharacteristics, executor);
        this.mTorchControl = new TorchControl(this, cameraCharacteristics, executor);
        if (Build.VERSION.SDK_INT >= 23) {
            this.mZslControl = new ZslControlImpl(cameraCharacteristics);
        } else {
            this.mZslControl = new ZslControlNoOpImpl();
        }
        this.mAeFpsRange = new AeFpsRange(cameraQuirks);
        this.mAutoFlashAEModeDisabler = new AutoFlashAEModeDisabler(cameraQuirks);
        this.mCamera2CameraControl = new Camera2CameraControl(this, executor);
        this.mCamera2CapturePipeline = new Camera2CapturePipeline(this, cameraCharacteristics, cameraQuirks, executor);
        executor.execute(new Camera2CameraControlImpl$$ExternalSyntheticLambda5(this));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$new$0$androidx-camera-camera2-internal-Camera2CameraControlImpl  reason: not valid java name */
    public /* synthetic */ void m27lambda$new$0$androidxcameracamera2internalCamera2CameraControlImpl() {
        addCaptureResultListener(this.mCamera2CameraControl.getCaptureRequestListener());
    }

    /* access modifiers changed from: package-private */
    public void incrementUseCount() {
        synchronized (this.mLock) {
            this.mUseCount++;
        }
    }

    /* access modifiers changed from: package-private */
    public void decrementUseCount() {
        synchronized (this.mLock) {
            int i = this.mUseCount;
            if (i != 0) {
                this.mUseCount = i - 1;
            } else {
                throw new IllegalStateException("Decrementing use count occurs more times than incrementing");
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int getUseCount() {
        int i;
        synchronized (this.mLock) {
            i = this.mUseCount;
        }
        return i;
    }

    public ZoomControl getZoomControl() {
        return this.mZoomControl;
    }

    public FocusMeteringControl getFocusMeteringControl() {
        return this.mFocusMeteringControl;
    }

    public TorchControl getTorchControl() {
        return this.mTorchControl;
    }

    public ExposureControl getExposureControl() {
        return this.mExposureControl;
    }

    public ZslControl getZslControl() {
        return this.mZslControl;
    }

    public Camera2CameraControl getCamera2CameraControl() {
        return this.mCamera2CameraControl;
    }

    public void addInteropConfig(Config config) {
        this.mCamera2CameraControl.addCaptureRequestOptions(CaptureRequestOptions.Builder.from(config).build()).addListener(Camera2CameraControlImpl$$ExternalSyntheticLambda10.INSTANCE, CameraXExecutors.directExecutor());
    }

    static /* synthetic */ void lambda$addInteropConfig$1() {
    }

    public void clearInteropConfig() {
        this.mCamera2CameraControl.clearCaptureRequestOptions().addListener(Camera2CameraControlImpl$$ExternalSyntheticLambda1.INSTANCE, CameraXExecutors.directExecutor());
    }

    static /* synthetic */ void lambda$clearInteropConfig$2() {
    }

    public Config getInteropConfig() {
        return this.mCamera2CameraControl.getCamera2ImplConfig();
    }

    /* access modifiers changed from: package-private */
    public void setActive(boolean isActive) {
        this.mFocusMeteringControl.setActive(isActive);
        this.mZoomControl.setActive(isActive);
        this.mTorchControl.setActive(isActive);
        this.mExposureControl.setActive(isActive);
        this.mCamera2CameraControl.setActive(isActive);
    }

    public void setPreviewAspectRatio(Rational previewAspectRatio) {
        this.mFocusMeteringControl.setPreviewAspectRatio(previewAspectRatio);
    }

    public ListenableFuture<FocusMeteringResult> startFocusAndMetering(FocusMeteringAction action) {
        if (!isControlInUse()) {
            return Futures.immediateFailedFuture(new CameraControl.OperationCanceledException("Camera is not active."));
        }
        return Futures.nonCancellationPropagating(this.mFocusMeteringControl.startFocusAndMetering(action));
    }

    public ListenableFuture<Void> cancelFocusAndMetering() {
        if (!isControlInUse()) {
            return Futures.immediateFailedFuture(new CameraControl.OperationCanceledException("Camera is not active."));
        }
        return Futures.nonCancellationPropagating(this.mFocusMeteringControl.cancelFocusAndMetering());
    }

    public ListenableFuture<Void> setZoomRatio(float ratio) {
        if (!isControlInUse()) {
            return Futures.immediateFailedFuture(new CameraControl.OperationCanceledException("Camera is not active."));
        }
        return Futures.nonCancellationPropagating(this.mZoomControl.setZoomRatio(ratio));
    }

    public ListenableFuture<Void> setLinearZoom(float linearZoom) {
        if (!isControlInUse()) {
            return Futures.immediateFailedFuture(new CameraControl.OperationCanceledException("Camera is not active."));
        }
        return Futures.nonCancellationPropagating(this.mZoomControl.setLinearZoom(linearZoom));
    }

    public int getFlashMode() {
        return this.mFlashMode;
    }

    public void setFlashMode(int flashMode) {
        if (!isControlInUse()) {
            Logger.w(TAG, "Camera is not active.");
            return;
        }
        this.mFlashMode = flashMode;
        ZslControl zslControl = this.mZslControl;
        boolean z = true;
        if (!(this.mFlashMode == 1 || this.mFlashMode == 0)) {
            z = false;
        }
        zslControl.setZslDisabledByFlashMode(z);
        this.mFlashModeChangeSessionUpdateFuture = updateSessionConfigAsync();
    }

    public void addZslConfig(SessionConfig.Builder sessionConfigBuilder) {
        this.mZslControl.addZslConfig(sessionConfigBuilder);
    }

    public void setZslDisabledByUserCaseConfig(boolean disabled) {
        this.mZslControl.setZslDisabledByUserCaseConfig(disabled);
    }

    public boolean isZslDisabledByByUserCaseConfig() {
        return this.mZslControl.isZslDisabledByUserCaseConfig();
    }

    public ListenableFuture<Void> enableTorch(boolean torch) {
        if (!isControlInUse()) {
            return Futures.immediateFailedFuture(new CameraControl.OperationCanceledException("Camera is not active."));
        }
        return Futures.nonCancellationPropagating(this.mTorchControl.enableTorch(torch));
    }

    private ListenableFuture<Void> waitForSessionUpdateId(long sessionUpdateIdToWait) {
        return CallbackToFutureAdapter.getFuture(new Camera2CameraControlImpl$$ExternalSyntheticLambda4(this, sessionUpdateIdToWait));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$waitForSessionUpdateId$4$androidx-camera-camera2-internal-Camera2CameraControlImpl  reason: not valid java name */
    public /* synthetic */ Object m32lambda$waitForSessionUpdateId$4$androidxcameracamera2internalCamera2CameraControlImpl(long sessionUpdateIdToWait, CallbackToFutureAdapter.Completer completer) throws Exception {
        addCaptureResultListener(new Camera2CameraControlImpl$$ExternalSyntheticLambda0(sessionUpdateIdToWait, completer));
        return "waitForSessionUpdateId:" + sessionUpdateIdToWait;
    }

    static /* synthetic */ boolean lambda$waitForSessionUpdateId$3(long sessionUpdateIdToWait, CallbackToFutureAdapter.Completer completer, TotalCaptureResult captureResult) {
        if (!isSessionUpdated(captureResult, sessionUpdateIdToWait)) {
            return false;
        }
        completer.set(null);
        return true;
    }

    static boolean isSessionUpdated(TotalCaptureResult captureResult, long sessionUpdateId) {
        Long tagLong;
        if (captureResult.getRequest() == null) {
            return false;
        }
        Object tag = captureResult.getRequest().getTag();
        if (!(tag instanceof TagBundle) || (tagLong = (Long) ((TagBundle) tag).getTag(TAG_SESSION_UPDATE_ID)) == null || tagLong.longValue() < sessionUpdateId) {
            return false;
        }
        return true;
    }

    public ListenableFuture<Integer> setExposureCompensationIndex(int exposure) {
        if (!isControlInUse()) {
            return Futures.immediateFailedFuture(new CameraControl.OperationCanceledException("Camera is not active."));
        }
        return this.mExposureControl.setExposureCompensationIndex(exposure);
    }

    public ListenableFuture<List<Void>> submitStillCaptureRequests(List<CaptureConfig> captureConfigs, int captureMode, int flashType) {
        if (!isControlInUse()) {
            Logger.w(TAG, "Camera is not active.");
            return Futures.immediateFailedFuture(new CameraControl.OperationCanceledException("Camera is not active."));
        }
        return FutureChain.from(Futures.nonCancellationPropagating(this.mFlashModeChangeSessionUpdateFuture)).transformAsync(new Camera2CameraControlImpl$$ExternalSyntheticLambda2(this, captureConfigs, captureMode, getFlashMode(), flashType), this.mExecutor);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$submitStillCaptureRequests$5$androidx-camera-camera2-internal-Camera2CameraControlImpl  reason: not valid java name */
    public /* synthetic */ ListenableFuture m29lambda$submitStillCaptureRequests$5$androidxcameracamera2internalCamera2CameraControlImpl(List captureConfigs, int captureMode, int flashMode, int flashType, Void v) throws Exception {
        return this.mCamera2CapturePipeline.submitStillCaptures(captureConfigs, captureMode, flashMode, flashType);
    }

    public SessionConfig getSessionConfig() {
        this.mSessionConfigBuilder.setTemplateType(this.mTemplate);
        this.mSessionConfigBuilder.setImplementationOptions(getSessionOptions());
        Object tag = this.mCamera2CameraControl.getCamera2ImplConfig().getCaptureRequestTag((Object) null);
        if (tag != null && (tag instanceof Integer)) {
            this.mSessionConfigBuilder.addTag(Camera2CameraControl.TAG_KEY, tag);
        }
        this.mSessionConfigBuilder.addTag(TAG_SESSION_UPDATE_ID, Long.valueOf(this.mCurrentSessionUpdateId));
        return this.mSessionConfigBuilder.build();
    }

    /* access modifiers changed from: package-private */
    public void setTemplate(int template) {
        this.mTemplate = template;
        this.mFocusMeteringControl.setTemplate(template);
        this.mCamera2CapturePipeline.setTemplate(this.mTemplate);
    }

    /* access modifiers changed from: package-private */
    public void resetTemplate() {
        setTemplate(1);
    }

    private boolean isControlInUse() {
        return getUseCount() > 0;
    }

    public void updateSessionConfig() {
        this.mExecutor.execute(new Camera2CameraControlImpl$$ExternalSyntheticLambda6(this));
    }

    /* access modifiers changed from: package-private */
    public ListenableFuture<Void> updateSessionConfigAsync() {
        return Futures.nonCancellationPropagating(CallbackToFutureAdapter.getFuture(new Camera2CameraControlImpl$$ExternalSyntheticLambda3(this)));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$updateSessionConfigAsync$7$androidx-camera-camera2-internal-Camera2CameraControlImpl  reason: not valid java name */
    public /* synthetic */ Object m31lambda$updateSessionConfigAsync$7$androidxcameracamera2internalCamera2CameraControlImpl(CallbackToFutureAdapter.Completer completer) throws Exception {
        this.mExecutor.execute(new Camera2CameraControlImpl$$ExternalSyntheticLambda8(this, completer));
        return "updateSessionConfigAsync";
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$updateSessionConfigAsync$6$androidx-camera-camera2-internal-Camera2CameraControlImpl  reason: not valid java name */
    public /* synthetic */ void m30lambda$updateSessionConfigAsync$6$androidxcameracamera2internalCamera2CameraControlImpl(CallbackToFutureAdapter.Completer completer) {
        Futures.propagate(waitForSessionUpdateId(updateSessionConfigSynchronous()), completer);
    }

    /* access modifiers changed from: package-private */
    public long updateSessionConfigSynchronous() {
        this.mCurrentSessionUpdateId = this.mNextSessionUpdateId.getAndIncrement();
        this.mControlUpdateCallback.onCameraControlUpdateSessionConfig();
        return this.mCurrentSessionUpdateId;
    }

    /* access modifiers changed from: package-private */
    public Rect getCropSensorRegion() {
        return this.mZoomControl.getCropSensorRegion();
    }

    public Rect getSensorRect() {
        return (Rect) Preconditions.checkNotNull((Rect) this.mCameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE));
    }

    /* access modifiers changed from: package-private */
    public void removeCaptureResultListener(CaptureResultListener listener) {
        this.mSessionCallback.removeListener(listener);
    }

    /* access modifiers changed from: package-private */
    public void addCaptureResultListener(CaptureResultListener listener) {
        this.mSessionCallback.addListener(listener);
    }

    /* access modifiers changed from: package-private */
    public void addSessionCameraCaptureCallback(Executor executor, CameraCaptureCallback cameraCaptureCallback) {
        this.mExecutor.execute(new Camera2CameraControlImpl$$ExternalSyntheticLambda9(this, executor, cameraCaptureCallback));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$addSessionCameraCaptureCallback$8$androidx-camera-camera2-internal-Camera2CameraControlImpl  reason: not valid java name */
    public /* synthetic */ void m26lambda$addSessionCameraCaptureCallback$8$androidxcameracamera2internalCamera2CameraControlImpl(Executor executor, CameraCaptureCallback cameraCaptureCallback) {
        this.mCameraCaptureCallbackSet.addCaptureCallback(executor, cameraCaptureCallback);
    }

    /* access modifiers changed from: package-private */
    public void removeSessionCameraCaptureCallback(CameraCaptureCallback cameraCaptureCallback) {
        this.mExecutor.execute(new Camera2CameraControlImpl$$ExternalSyntheticLambda7(this, cameraCaptureCallback));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$removeSessionCameraCaptureCallback$9$androidx-camera-camera2-internal-Camera2CameraControlImpl  reason: not valid java name */
    public /* synthetic */ void m28lambda$removeSessionCameraCaptureCallback$9$androidxcameracamera2internalCamera2CameraControlImpl(CameraCaptureCallback cameraCaptureCallback) {
        this.mCameraCaptureCallbackSet.removeCaptureCallback(cameraCaptureCallback);
    }

    /* access modifiers changed from: package-private */
    public void enableTorchInternal(boolean torch) {
        this.mIsTorchOn = torch;
        if (!torch) {
            CaptureConfig.Builder singleRequestBuilder = new CaptureConfig.Builder();
            singleRequestBuilder.setTemplateType(this.mTemplate);
            singleRequestBuilder.setUseRepeatingSurface(true);
            Camera2ImplConfig.Builder configBuilder = new Camera2ImplConfig.Builder();
            configBuilder.setCaptureRequestOption(CaptureRequest.CONTROL_AE_MODE, Integer.valueOf(getSupportedAeMode(1)));
            configBuilder.setCaptureRequestOption(CaptureRequest.FLASH_MODE, 0);
            singleRequestBuilder.addImplementationOptions(configBuilder.build());
            submitCaptureRequestsInternal(Collections.singletonList(singleRequestBuilder.build()));
        }
        updateSessionConfigSynchronous();
    }

    /* access modifiers changed from: package-private */
    public boolean isTorchOn() {
        return this.mIsTorchOn;
    }

    /* access modifiers changed from: package-private */
    public void submitCaptureRequestsInternal(List<CaptureConfig> captureConfigs) {
        this.mControlUpdateCallback.onCameraControlCaptureRequests(captureConfigs);
    }

    /* access modifiers changed from: package-private */
    public Config getSessionOptions() {
        Camera2ImplConfig.Builder builder = new Camera2ImplConfig.Builder();
        builder.setCaptureRequestOption(CaptureRequest.CONTROL_MODE, 1);
        this.mFocusMeteringControl.addFocusMeteringOptions(builder);
        this.mAeFpsRange.addAeFpsRangeOptions(builder);
        this.mZoomControl.addZoomOption(builder);
        int aeMode = 1;
        if (!this.mIsTorchOn) {
            switch (this.mFlashMode) {
                case 0:
                    aeMode = this.mAutoFlashAEModeDisabler.getCorrectedAeMode(2);
                    break;
                case 1:
                    aeMode = 3;
                    break;
                case 2:
                    aeMode = 1;
                    break;
            }
        } else {
            builder.setCaptureRequestOption(CaptureRequest.FLASH_MODE, 2);
        }
        builder.setCaptureRequestOption(CaptureRequest.CONTROL_AE_MODE, Integer.valueOf(getSupportedAeMode(aeMode)));
        builder.setCaptureRequestOption(CaptureRequest.CONTROL_AWB_MODE, Integer.valueOf(getSupportedAwbMode(1)));
        this.mExposureControl.setCaptureRequestOption(builder);
        Config currentConfig = this.mCamera2CameraControl.getCamera2ImplConfig();
        for (Config.Option<?> next : currentConfig.listOptions()) {
            builder.getMutableConfig().insertOption(next, Config.OptionPriority.ALWAYS_OVERRIDE, currentConfig.retrieveOption(next));
        }
        return builder.build();
    }

    /* access modifiers changed from: package-private */
    public int getSupportedAfMode(int preferredMode) {
        int[] modes = (int[]) this.mCameraCharacteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
        if (modes == null) {
            return 0;
        }
        if (isModeInList(preferredMode, modes)) {
            return preferredMode;
        }
        if (isModeInList(4, modes)) {
            return 4;
        }
        if (isModeInList(1, modes)) {
            return 1;
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int getSupportedAeMode(int preferredMode) {
        int[] modes = (int[]) this.mCameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
        if (modes == null) {
            return 0;
        }
        if (isModeInList(preferredMode, modes)) {
            return preferredMode;
        }
        if (isModeInList(1, modes)) {
            return 1;
        }
        return 0;
    }

    private int getSupportedAwbMode(int preferredMode) {
        int[] modes = (int[]) this.mCameraCharacteristics.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
        if (modes == null) {
            return 0;
        }
        if (isModeInList(preferredMode, modes)) {
            return preferredMode;
        }
        if (isModeInList(1, modes)) {
            return 1;
        }
        return 0;
    }

    private boolean isModeInList(int mode, int[] modeList) {
        for (int m : modeList) {
            if (mode == m) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public int getMaxAfRegionCount() {
        Integer count = (Integer) this.mCameraCharacteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF);
        if (count == null) {
            return 0;
        }
        return count.intValue();
    }

    /* access modifiers changed from: package-private */
    public int getMaxAeRegionCount() {
        Integer count = (Integer) this.mCameraCharacteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE);
        if (count == null) {
            return 0;
        }
        return count.intValue();
    }

    /* access modifiers changed from: package-private */
    public int getMaxAwbRegionCount() {
        Integer count = (Integer) this.mCameraCharacteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AWB);
        if (count == null) {
            return 0;
        }
        return count.intValue();
    }

    /* access modifiers changed from: package-private */
    public long getCurrentSessionUpdateId() {
        return this.mCurrentSessionUpdateId;
    }

    static final class CameraControlSessionCallback extends CameraCaptureSession.CaptureCallback {
        private final Executor mExecutor;
        final Set<CaptureResultListener> mResultListeners = new HashSet();

        CameraControlSessionCallback(Executor executor) {
            this.mExecutor = executor;
        }

        /* access modifiers changed from: package-private */
        public void addListener(CaptureResultListener listener) {
            this.mResultListeners.add(listener);
        }

        /* access modifiers changed from: package-private */
        public void removeListener(CaptureResultListener listener) {
            this.mResultListeners.remove(listener);
        }

        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            this.mExecutor.execute(new Camera2CameraControlImpl$CameraControlSessionCallback$$ExternalSyntheticLambda0(this, result));
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onCaptureCompleted$0$androidx-camera-camera2-internal-Camera2CameraControlImpl$CameraControlSessionCallback  reason: not valid java name */
        public /* synthetic */ void m33lambda$onCaptureCompleted$0$androidxcameracamera2internalCamera2CameraControlImpl$CameraControlSessionCallback(TotalCaptureResult result) {
            Set<CaptureResultListener> removeSet = new HashSet<>();
            for (CaptureResultListener listener : this.mResultListeners) {
                if (listener.onCaptureResult(result)) {
                    removeSet.add(listener);
                }
            }
            if (!removeSet.isEmpty()) {
                this.mResultListeners.removeAll(removeSet);
            }
        }
    }

    static final class CameraCaptureCallbackSet extends CameraCaptureCallback {
        Map<CameraCaptureCallback, Executor> mCallbackExecutors = new ArrayMap();
        Set<CameraCaptureCallback> mCallbacks = new HashSet();

        CameraCaptureCallbackSet() {
        }

        /* access modifiers changed from: package-private */
        public void addCaptureCallback(Executor executor, CameraCaptureCallback callback) {
            this.mCallbacks.add(callback);
            this.mCallbackExecutors.put(callback, executor);
        }

        /* access modifiers changed from: package-private */
        public void removeCaptureCallback(CameraCaptureCallback callback) {
            this.mCallbacks.remove(callback);
            this.mCallbackExecutors.remove(callback);
        }

        public void onCaptureCompleted(CameraCaptureResult cameraCaptureResult) {
            for (CameraCaptureCallback callback : this.mCallbacks) {
                try {
                    this.mCallbackExecutors.get(callback).execute(new Camera2CameraControlImpl$CameraCaptureCallbackSet$$ExternalSyntheticLambda2(callback, cameraCaptureResult));
                } catch (RejectedExecutionException e) {
                    Logger.e(Camera2CameraControlImpl.TAG, "Executor rejected to invoke onCaptureCompleted.", e);
                }
            }
        }

        public void onCaptureFailed(CameraCaptureFailure failure) {
            for (CameraCaptureCallback callback : this.mCallbacks) {
                try {
                    this.mCallbackExecutors.get(callback).execute(new Camera2CameraControlImpl$CameraCaptureCallbackSet$$ExternalSyntheticLambda1(callback, failure));
                } catch (RejectedExecutionException e) {
                    Logger.e(Camera2CameraControlImpl.TAG, "Executor rejected to invoke onCaptureFailed.", e);
                }
            }
        }

        public void onCaptureCancelled() {
            for (CameraCaptureCallback callback : this.mCallbacks) {
                try {
                    this.mCallbackExecutors.get(callback).execute(new Camera2CameraControlImpl$CameraCaptureCallbackSet$$ExternalSyntheticLambda0(callback));
                } catch (RejectedExecutionException e) {
                    Logger.e(Camera2CameraControlImpl.TAG, "Executor rejected to invoke onCaptureCancelled.", e);
                }
            }
        }
    }
}

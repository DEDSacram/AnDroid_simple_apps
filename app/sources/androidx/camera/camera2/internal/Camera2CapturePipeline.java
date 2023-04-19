package androidx.camera.camera2.internal;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import androidx.camera.camera2.impl.Camera2ImplConfig;
import androidx.camera.camera2.internal.Camera2CameraControlImpl;
import androidx.camera.camera2.internal.compat.CameraCharacteristicsCompat;
import androidx.camera.camera2.internal.compat.workaround.OverrideAeModeForStillCapture;
import androidx.camera.camera2.internal.compat.workaround.UseTorchAsFlash;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Logger;
import androidx.camera.core.impl.CameraCaptureCallback;
import androidx.camera.core.impl.CameraCaptureFailure;
import androidx.camera.core.impl.CameraCaptureMetaData;
import androidx.camera.core.impl.CameraCaptureResult;
import androidx.camera.core.impl.CameraCaptureResults;
import androidx.camera.core.impl.CaptureConfig;
import androidx.camera.core.impl.Quirks;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.impl.utils.futures.FutureChain;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

class Camera2CapturePipeline {
    private static final Set<CameraCaptureMetaData.AeState> AE_CONVERGED_STATE_SET;
    private static final Set<CameraCaptureMetaData.AeState> AE_TORCH_AS_FLASH_CONVERGED_STATE_SET;
    private static final Set<CameraCaptureMetaData.AfState> AF_CONVERGED_STATE_SET = Collections.unmodifiableSet(EnumSet.of(CameraCaptureMetaData.AfState.PASSIVE_FOCUSED, CameraCaptureMetaData.AfState.PASSIVE_NOT_FOCUSED, CameraCaptureMetaData.AfState.LOCKED_FOCUSED, CameraCaptureMetaData.AfState.LOCKED_NOT_FOCUSED));
    private static final Set<CameraCaptureMetaData.AwbState> AWB_CONVERGED_STATE_SET = Collections.unmodifiableSet(EnumSet.of(CameraCaptureMetaData.AwbState.CONVERGED, CameraCaptureMetaData.AwbState.UNKNOWN));
    private static final String TAG = "Camera2CapturePipeline";
    private final Camera2CameraControlImpl mCameraControl;
    private final Quirks mCameraQuirk;
    private final Executor mExecutor;
    private final boolean mIsLegacyDevice;
    private int mTemplate = 1;
    private final UseTorchAsFlash mUseTorchAsFlash;

    interface PipelineTask {
        boolean isCaptureResultNeeded();

        void postCapture();

        ListenableFuture<Boolean> preCapture(TotalCaptureResult totalCaptureResult);
    }

    static {
        Set<CameraCaptureMetaData.AeState> unmodifiableSet = Collections.unmodifiableSet(EnumSet.of(CameraCaptureMetaData.AeState.CONVERGED, CameraCaptureMetaData.AeState.FLASH_REQUIRED, CameraCaptureMetaData.AeState.UNKNOWN));
        AE_CONVERGED_STATE_SET = unmodifiableSet;
        EnumSet<CameraCaptureMetaData.AeState> aeStateSet = EnumSet.copyOf(unmodifiableSet);
        aeStateSet.remove(CameraCaptureMetaData.AeState.FLASH_REQUIRED);
        aeStateSet.remove(CameraCaptureMetaData.AeState.UNKNOWN);
        AE_TORCH_AS_FLASH_CONVERGED_STATE_SET = Collections.unmodifiableSet(aeStateSet);
    }

    Camera2CapturePipeline(Camera2CameraControlImpl cameraControl, CameraCharacteristicsCompat cameraCharacteristics, Quirks cameraQuirks, Executor executor) {
        boolean z = true;
        this.mCameraControl = cameraControl;
        Integer level = (Integer) cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
        this.mIsLegacyDevice = (level == null || level.intValue() != 2) ? false : z;
        this.mExecutor = executor;
        this.mCameraQuirk = cameraQuirks;
        this.mUseTorchAsFlash = new UseTorchAsFlash(cameraQuirks);
    }

    public void setTemplate(int template) {
        this.mTemplate = template;
    }

    public ListenableFuture<List<Void>> submitStillCaptures(List<CaptureConfig> captureConfigs, int captureMode, int flashMode, int flashType) {
        OverrideAeModeForStillCapture aeQuirk = new OverrideAeModeForStillCapture(this.mCameraQuirk);
        Pipeline pipeline = new Pipeline(this.mTemplate, this.mExecutor, this.mCameraControl, this.mIsLegacyDevice, aeQuirk);
        if (captureMode == 0) {
            pipeline.addTask(new AfTask(this.mCameraControl));
        }
        if (isTorchAsFlash(flashType)) {
            pipeline.addTask(new TorchTask(this.mCameraControl, flashMode, this.mExecutor));
        } else {
            pipeline.addTask(new AePreCaptureTask(this.mCameraControl, flashMode, aeQuirk));
        }
        return Futures.nonCancellationPropagating(pipeline.executeCapture(captureConfigs, flashMode));
    }

    static class Pipeline {
        private static final long CHECK_3A_TIMEOUT_IN_NS = TimeUnit.SECONDS.toNanos(1);
        private static final long CHECK_3A_WITH_FLASH_TIMEOUT_IN_NS = TimeUnit.SECONDS.toNanos(5);
        private final Camera2CameraControlImpl mCameraControl;
        private final Executor mExecutor;
        private final boolean mIsLegacyDevice;
        private final OverrideAeModeForStillCapture mOverrideAeModeForStillCapture;
        private final PipelineTask mPipelineSubTask = new PipelineTask() {
            public ListenableFuture<Boolean> preCapture(TotalCaptureResult captureResult) {
                ArrayList<ListenableFuture<Boolean>> futures = new ArrayList<>();
                for (PipelineTask task : Pipeline.this.mTasks) {
                    futures.add(task.preCapture(captureResult));
                }
                return Futures.transform(Futures.allAsList(futures), Camera2CapturePipeline$Pipeline$1$$ExternalSyntheticLambda0.INSTANCE, CameraXExecutors.directExecutor());
            }

            public boolean isCaptureResultNeeded() {
                for (PipelineTask task : Pipeline.this.mTasks) {
                    if (task.isCaptureResultNeeded()) {
                        return true;
                    }
                }
                return false;
            }

            public void postCapture() {
                for (PipelineTask task : Pipeline.this.mTasks) {
                    task.postCapture();
                }
            }
        };
        final List<PipelineTask> mTasks = new ArrayList();
        private final int mTemplate;
        private long mTimeout3A = CHECK_3A_TIMEOUT_IN_NS;

        Pipeline(int template, Executor executor, Camera2CameraControlImpl cameraControl, boolean isLegacyDevice, OverrideAeModeForStillCapture overrideAeModeForStillCapture) {
            this.mTemplate = template;
            this.mExecutor = executor;
            this.mCameraControl = cameraControl;
            this.mIsLegacyDevice = isLegacyDevice;
            this.mOverrideAeModeForStillCapture = overrideAeModeForStillCapture;
        }

        /* access modifiers changed from: package-private */
        public void addTask(PipelineTask task) {
            this.mTasks.add(task);
        }

        private void setTimeout3A(long timeout3A) {
            this.mTimeout3A = timeout3A;
        }

        /* access modifiers changed from: package-private */
        public ListenableFuture<List<Void>> executeCapture(List<CaptureConfig> captureConfigs, int flashMode) {
            ListenableFuture listenableFuture;
            ListenableFuture<TotalCaptureResult> preCapture = Futures.immediateFuture(null);
            if (!this.mTasks.isEmpty()) {
                if (this.mPipelineSubTask.isCaptureResultNeeded()) {
                    listenableFuture = Camera2CapturePipeline.waitForResult(0, this.mCameraControl, (ResultListener.Checker) null);
                } else {
                    listenableFuture = Futures.immediateFuture(null);
                }
                preCapture = FutureChain.from(listenableFuture).transformAsync(new Camera2CapturePipeline$Pipeline$$ExternalSyntheticLambda2(this, flashMode), this.mExecutor).transformAsync(new Camera2CapturePipeline$Pipeline$$ExternalSyntheticLambda1(this), this.mExecutor);
            }
            ListenableFuture<List<Void>> future = FutureChain.from(preCapture).transformAsync(new Camera2CapturePipeline$Pipeline$$ExternalSyntheticLambda3(this, captureConfigs, flashMode), this.mExecutor);
            PipelineTask pipelineTask = this.mPipelineSubTask;
            Objects.requireNonNull(pipelineTask);
            future.addListener(new Camera2CapturePipeline$Pipeline$$ExternalSyntheticLambda5(pipelineTask), this.mExecutor);
            return future;
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$executeCapture$0$androidx-camera-camera2-internal-Camera2CapturePipeline$Pipeline  reason: not valid java name */
        public /* synthetic */ ListenableFuture m51lambda$executeCapture$0$androidxcameracamera2internalCamera2CapturePipeline$Pipeline(int flashMode, TotalCaptureResult captureResult) throws Exception {
            if (Camera2CapturePipeline.isFlashRequired(flashMode, captureResult)) {
                setTimeout3A(CHECK_3A_WITH_FLASH_TIMEOUT_IN_NS);
            }
            return this.mPipelineSubTask.preCapture(captureResult);
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$executeCapture$2$androidx-camera-camera2-internal-Camera2CapturePipeline$Pipeline  reason: not valid java name */
        public /* synthetic */ ListenableFuture m52lambda$executeCapture$2$androidxcameracamera2internalCamera2CapturePipeline$Pipeline(Boolean is3aConvergeRequired) throws Exception {
            if (Boolean.TRUE.equals(is3aConvergeRequired)) {
                return Camera2CapturePipeline.waitForResult(this.mTimeout3A, this.mCameraControl, Camera2CapturePipeline$Pipeline$$ExternalSyntheticLambda0.INSTANCE);
            }
            return Futures.immediateFuture(null);
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$executeCapture$3$androidx-camera-camera2-internal-Camera2CapturePipeline$Pipeline  reason: not valid java name */
        public /* synthetic */ ListenableFuture m53lambda$executeCapture$3$androidxcameracamera2internalCamera2CapturePipeline$Pipeline(List captureConfigs, int flashMode, TotalCaptureResult v) throws Exception {
            return submitConfigsInternal(captureConfigs, flashMode);
        }

        /* access modifiers changed from: package-private */
        public ListenableFuture<List<Void>> submitConfigsInternal(List<CaptureConfig> captureConfigs, int flashMode) {
            List<ListenableFuture<Void>> futureList = new ArrayList<>();
            List<CaptureConfig> configsToSubmit = new ArrayList<>();
            for (CaptureConfig captureConfig : captureConfigs) {
                CaptureConfig.Builder configBuilder = CaptureConfig.Builder.from(captureConfig);
                CameraCaptureResult cameraCaptureResult = null;
                if (captureConfig.getTemplateType() == 5 && !this.mCameraControl.getZslControl().isZslDisabledByFlashMode() && !this.mCameraControl.getZslControl().isZslDisabledByUserCaseConfig()) {
                    ImageProxy imageProxy = this.mCameraControl.getZslControl().dequeueImageFromBuffer();
                    if (imageProxy != null && this.mCameraControl.getZslControl().enqueueImageToImageWriter(imageProxy)) {
                        cameraCaptureResult = CameraCaptureResults.retrieveCameraCaptureResult(imageProxy.getImageInfo());
                    }
                }
                if (cameraCaptureResult != null) {
                    configBuilder.setCameraCaptureResult(cameraCaptureResult);
                } else {
                    applyStillCaptureTemplate(configBuilder, captureConfig);
                }
                if (this.mOverrideAeModeForStillCapture.shouldSetAeModeAlwaysFlash(flashMode)) {
                    applyAeModeQuirk(configBuilder);
                }
                futureList.add(CallbackToFutureAdapter.getFuture(new Camera2CapturePipeline$Pipeline$$ExternalSyntheticLambda4(this, configBuilder)));
                configsToSubmit.add(configBuilder.build());
            }
            this.mCameraControl.submitCaptureRequestsInternal(configsToSubmit);
            return Futures.allAsList(futureList);
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$submitConfigsInternal$4$androidx-camera-camera2-internal-Camera2CapturePipeline$Pipeline  reason: not valid java name */
        public /* synthetic */ Object m54lambda$submitConfigsInternal$4$androidxcameracamera2internalCamera2CapturePipeline$Pipeline(CaptureConfig.Builder configBuilder, final CallbackToFutureAdapter.Completer completer) throws Exception {
            configBuilder.addCameraCaptureCallback(new CameraCaptureCallback() {
                public void onCaptureCompleted(CameraCaptureResult result) {
                    completer.set(null);
                }

                public void onCaptureFailed(CameraCaptureFailure failure) {
                    completer.setException(new ImageCaptureException(2, "Capture request failed with reason " + failure.getReason(), (Throwable) null));
                }

                public void onCaptureCancelled() {
                    completer.setException(new ImageCaptureException(3, "Capture request is cancelled because camera is closed", (Throwable) null));
                }
            });
            return "submitStillCapture";
        }

        private void applyStillCaptureTemplate(CaptureConfig.Builder configBuilder, CaptureConfig captureConfig) {
            int templateToModify = -1;
            if (this.mTemplate == 3 && !this.mIsLegacyDevice) {
                templateToModify = 4;
            } else if (captureConfig.getTemplateType() == -1 || captureConfig.getTemplateType() == 5) {
                templateToModify = 2;
            }
            if (templateToModify != -1) {
                configBuilder.setTemplateType(templateToModify);
            }
        }

        private void applyAeModeQuirk(CaptureConfig.Builder configBuilder) {
            Camera2ImplConfig.Builder impBuilder = new Camera2ImplConfig.Builder();
            impBuilder.setCaptureRequestOption(CaptureRequest.CONTROL_AE_MODE, 3);
            configBuilder.addImplementationOptions(impBuilder.build());
        }
    }

    static ListenableFuture<TotalCaptureResult> waitForResult(long waitTimeout, Camera2CameraControlImpl cameraControl, ResultListener.Checker checker) {
        ResultListener resultListener = new ResultListener(waitTimeout, checker);
        cameraControl.addCaptureResultListener(resultListener);
        return resultListener.getFuture();
    }

    /* access modifiers changed from: package-private */
    public static boolean is3AConverged(TotalCaptureResult totalCaptureResult, boolean isTorchAsFlash) {
        boolean isAeReady;
        if (totalCaptureResult == null) {
            return false;
        }
        Camera2CameraCaptureResult captureResult = new Camera2CameraCaptureResult(totalCaptureResult);
        boolean isAfReady = captureResult.getAfMode() == CameraCaptureMetaData.AfMode.OFF || captureResult.getAfMode() == CameraCaptureMetaData.AfMode.UNKNOWN || AF_CONVERGED_STATE_SET.contains(captureResult.getAfState());
        boolean isAeModeOff = ((Integer) totalCaptureResult.get(CaptureResult.CONTROL_AE_MODE)).intValue() == 0;
        if (isTorchAsFlash) {
            isAeReady = isAeModeOff || AE_TORCH_AS_FLASH_CONVERGED_STATE_SET.contains(captureResult.getAeState());
        } else {
            isAeReady = isAeModeOff || AE_CONVERGED_STATE_SET.contains(captureResult.getAeState());
        }
        boolean isAwbReady = (((Integer) totalCaptureResult.get(CaptureResult.CONTROL_AWB_MODE)).intValue() == 0) || AWB_CONVERGED_STATE_SET.contains(captureResult.getAwbState());
        Logger.d(TAG, "checkCaptureResult, AE=" + captureResult.getAeState() + " AF =" + captureResult.getAfState() + " AWB=" + captureResult.getAwbState());
        if (!isAfReady || !isAeReady || !isAwbReady) {
            return false;
        }
        return true;
    }

    static class AfTask implements PipelineTask {
        private final Camera2CameraControlImpl mCameraControl;
        private boolean mIsExecuted = false;

        AfTask(Camera2CameraControlImpl cameraControl) {
            this.mCameraControl = cameraControl;
        }

        public ListenableFuture<Boolean> preCapture(TotalCaptureResult captureResult) {
            Integer afMode;
            ListenableFuture<Boolean> ret = Futures.immediateFuture(true);
            if (captureResult == null || (afMode = (Integer) captureResult.get(CaptureResult.CONTROL_AF_MODE)) == null) {
                return ret;
            }
            switch (afMode.intValue()) {
                case 1:
                case 2:
                    Logger.d(Camera2CapturePipeline.TAG, "TriggerAf? AF mode auto");
                    Integer afState = (Integer) captureResult.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState != null && afState.intValue() == 0) {
                        Logger.d(Camera2CapturePipeline.TAG, "Trigger AF");
                        this.mIsExecuted = true;
                        this.mCameraControl.getFocusMeteringControl().triggerAf((CallbackToFutureAdapter.Completer<CameraCaptureResult>) null, false);
                        return ret;
                    }
            }
            return ret;
        }

        public boolean isCaptureResultNeeded() {
            return true;
        }

        public void postCapture() {
            if (this.mIsExecuted) {
                Logger.d(Camera2CapturePipeline.TAG, "cancel TriggerAF");
                this.mCameraControl.getFocusMeteringControl().cancelAfAeTrigger(true, false);
            }
        }
    }

    static class TorchTask implements PipelineTask {
        private static final long CHECK_3A_WITH_TORCH_TIMEOUT_IN_NS = TimeUnit.SECONDS.toNanos(2);
        private final Camera2CameraControlImpl mCameraControl;
        private final Executor mExecutor;
        private final int mFlashMode;
        private boolean mIsExecuted = false;

        TorchTask(Camera2CameraControlImpl cameraControl, int flashMode, Executor executor) {
            this.mCameraControl = cameraControl;
            this.mFlashMode = flashMode;
            this.mExecutor = executor;
        }

        public ListenableFuture<Boolean> preCapture(TotalCaptureResult captureResult) {
            if (Camera2CapturePipeline.isFlashRequired(this.mFlashMode, captureResult)) {
                if (this.mCameraControl.isTorchOn()) {
                    Logger.d(Camera2CapturePipeline.TAG, "Torch already on, not turn on");
                } else {
                    Logger.d(Camera2CapturePipeline.TAG, "Turn on torch");
                    this.mIsExecuted = true;
                    return FutureChain.from(CallbackToFutureAdapter.getFuture(new Camera2CapturePipeline$TorchTask$$ExternalSyntheticLambda3(this))).transformAsync(new Camera2CapturePipeline$TorchTask$$ExternalSyntheticLambda2(this), this.mExecutor).transform(Camera2CapturePipeline$TorchTask$$ExternalSyntheticLambda0.INSTANCE, CameraXExecutors.directExecutor());
                }
            }
            return Futures.immediateFuture(false);
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$preCapture$0$androidx-camera-camera2-internal-Camera2CapturePipeline$TorchTask  reason: not valid java name */
        public /* synthetic */ Object m56lambda$preCapture$0$androidxcameracamera2internalCamera2CapturePipeline$TorchTask(CallbackToFutureAdapter.Completer completer) throws Exception {
            this.mCameraControl.getTorchControl().m89lambda$enableTorch$1$androidxcameracamera2internalTorchControl(completer, true);
            return "TorchOn";
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$preCapture$2$androidx-camera-camera2-internal-Camera2CapturePipeline$TorchTask  reason: not valid java name */
        public /* synthetic */ ListenableFuture m57lambda$preCapture$2$androidxcameracamera2internalCamera2CapturePipeline$TorchTask(Void input) throws Exception {
            return Camera2CapturePipeline.waitForResult(CHECK_3A_WITH_TORCH_TIMEOUT_IN_NS, this.mCameraControl, Camera2CapturePipeline$TorchTask$$ExternalSyntheticLambda1.INSTANCE);
        }

        static /* synthetic */ Boolean lambda$preCapture$3(TotalCaptureResult input) {
            return false;
        }

        public boolean isCaptureResultNeeded() {
            return this.mFlashMode == 0;
        }

        public void postCapture() {
            if (this.mIsExecuted) {
                this.mCameraControl.getTorchControl().m89lambda$enableTorch$1$androidxcameracamera2internalTorchControl((CallbackToFutureAdapter.Completer<Void>) null, false);
                Logger.d(Camera2CapturePipeline.TAG, "Turn off torch");
            }
        }
    }

    static class AePreCaptureTask implements PipelineTask {
        private final Camera2CameraControlImpl mCameraControl;
        private final int mFlashMode;
        private boolean mIsExecuted = false;
        private final OverrideAeModeForStillCapture mOverrideAeModeForStillCapture;

        AePreCaptureTask(Camera2CameraControlImpl cameraControl, int flashMode, OverrideAeModeForStillCapture overrideAeModeForStillCapture) {
            this.mCameraControl = cameraControl;
            this.mFlashMode = flashMode;
            this.mOverrideAeModeForStillCapture = overrideAeModeForStillCapture;
        }

        public ListenableFuture<Boolean> preCapture(TotalCaptureResult captureResult) {
            if (!Camera2CapturePipeline.isFlashRequired(this.mFlashMode, captureResult)) {
                return Futures.immediateFuture(false);
            }
            Logger.d(Camera2CapturePipeline.TAG, "Trigger AE");
            this.mIsExecuted = true;
            return FutureChain.from(CallbackToFutureAdapter.getFuture(new Camera2CapturePipeline$AePreCaptureTask$$ExternalSyntheticLambda1(this))).transform(Camera2CapturePipeline$AePreCaptureTask$$ExternalSyntheticLambda0.INSTANCE, CameraXExecutors.directExecutor());
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$preCapture$0$androidx-camera-camera2-internal-Camera2CapturePipeline$AePreCaptureTask  reason: not valid java name */
        public /* synthetic */ Object m50lambda$preCapture$0$androidxcameracamera2internalCamera2CapturePipeline$AePreCaptureTask(CallbackToFutureAdapter.Completer completer) throws Exception {
            this.mCameraControl.getFocusMeteringControl().triggerAePrecapture(completer);
            this.mOverrideAeModeForStillCapture.onAePrecaptureStarted();
            return "AePreCapture";
        }

        static /* synthetic */ Boolean lambda$preCapture$1(Void input) {
            return true;
        }

        public boolean isCaptureResultNeeded() {
            return this.mFlashMode == 0;
        }

        public void postCapture() {
            if (this.mIsExecuted) {
                Logger.d(Camera2CapturePipeline.TAG, "cancel TriggerAePreCapture");
                this.mCameraControl.getFocusMeteringControl().cancelAfAeTrigger(false, true);
                this.mOverrideAeModeForStillCapture.onAePrecaptureFinished();
            }
        }
    }

    static boolean isFlashRequired(int flashMode, TotalCaptureResult result) {
        Integer aeState;
        switch (flashMode) {
            case 0:
                if (result != null) {
                    aeState = (Integer) result.get(CaptureResult.CONTROL_AE_STATE);
                } else {
                    aeState = null;
                }
                if (aeState == null || aeState.intValue() != 4) {
                    return false;
                }
                return true;
            case 1:
                return true;
            case 2:
                return false;
            default:
                throw new AssertionError(flashMode);
        }
    }

    static class ResultListener implements Camera2CameraControlImpl.CaptureResultListener {
        static final long NO_TIMEOUT = 0;
        private final Checker mChecker;
        private CallbackToFutureAdapter.Completer<TotalCaptureResult> mCompleter;
        private final ListenableFuture<TotalCaptureResult> mFuture = CallbackToFutureAdapter.getFuture(new Camera2CapturePipeline$ResultListener$$ExternalSyntheticLambda0(this));
        private final long mTimeLimitNs;
        private volatile Long mTimestampOfFirstUpdateNs = null;

        interface Checker {
            boolean check(TotalCaptureResult totalCaptureResult);
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$new$0$androidx-camera-camera2-internal-Camera2CapturePipeline$ResultListener  reason: not valid java name */
        public /* synthetic */ Object m55lambda$new$0$androidxcameracamera2internalCamera2CapturePipeline$ResultListener(CallbackToFutureAdapter.Completer completer) throws Exception {
            this.mCompleter = completer;
            return "waitFor3AResult";
        }

        ResultListener(long timeLimitNs, Checker checker) {
            this.mTimeLimitNs = timeLimitNs;
            this.mChecker = checker;
        }

        public ListenableFuture<TotalCaptureResult> getFuture() {
            return this.mFuture;
        }

        public boolean onCaptureResult(TotalCaptureResult captureResult) {
            Long currentTimestampNs = (Long) captureResult.get(CaptureResult.SENSOR_TIMESTAMP);
            if (currentTimestampNs != null && this.mTimestampOfFirstUpdateNs == null) {
                this.mTimestampOfFirstUpdateNs = currentTimestampNs;
            }
            Long timestampOfFirstUpdateNs = this.mTimestampOfFirstUpdateNs;
            if (0 == this.mTimeLimitNs || timestampOfFirstUpdateNs == null || currentTimestampNs == null || currentTimestampNs.longValue() - timestampOfFirstUpdateNs.longValue() <= this.mTimeLimitNs) {
                Checker checker = this.mChecker;
                if (checker != null && !checker.check(captureResult)) {
                    return false;
                }
                this.mCompleter.set(captureResult);
                return true;
            }
            this.mCompleter.set(null);
            Logger.d(Camera2CapturePipeline.TAG, "Wait for capture result timeout, current:" + currentTimestampNs + " first: " + timestampOfFirstUpdateNs);
            return true;
        }
    }

    private boolean isTorchAsFlash(int flashType) {
        return this.mUseTorchAsFlash.shouldUseTorchAsFlash() || this.mTemplate == 3 || flashType == 1;
    }
}

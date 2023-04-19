package androidx.camera.camera2.internal;

import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.os.Build;
import android.util.Rational;
import androidx.camera.camera2.impl.Camera2ImplConfig;
import androidx.camera.camera2.internal.Camera2CameraControlImpl;
import androidx.camera.camera2.internal.compat.workaround.MeteringRegionCorrection;
import androidx.camera.core.CameraControl;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.FocusMeteringResult;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.impl.CameraCaptureCallback;
import androidx.camera.core.impl.CameraCaptureFailure;
import androidx.camera.core.impl.CameraCaptureResult;
import androidx.camera.core.impl.CameraControlInternal;
import androidx.camera.core.impl.CaptureConfig;
import androidx.camera.core.impl.Quirks;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

class FocusMeteringControl {
    static final long AUTO_FOCUS_TIMEOUT_DURATION = 5000;
    private static final MeteringRectangle[] EMPTY_RECTANGLES = new MeteringRectangle[0];
    private MeteringRectangle[] mAeRects;
    private MeteringRectangle[] mAfRects;
    private ScheduledFuture<?> mAutoCancelHandle;
    private ScheduledFuture<?> mAutoFocusTimeoutHandle;
    private MeteringRectangle[] mAwbRects;
    private final Camera2CameraControlImpl mCameraControl;
    Integer mCurrentAfState = 0;
    final Executor mExecutor;
    long mFocusTimeoutCounter = 0;
    private volatile boolean mIsActive = false;
    boolean mIsAutoFocusCompleted = false;
    boolean mIsFocusSuccessful = false;
    private boolean mIsInAfAutoMode = false;
    private final MeteringRegionCorrection mMeteringRegionCorrection;
    private volatile Rational mPreviewAspectRatio = null;
    CallbackToFutureAdapter.Completer<FocusMeteringResult> mRunningActionCompleter;
    CallbackToFutureAdapter.Completer<Void> mRunningCancelCompleter;
    private final ScheduledExecutorService mScheduler;
    private Camera2CameraControlImpl.CaptureResultListener mSessionListenerForCancel = null;
    private Camera2CameraControlImpl.CaptureResultListener mSessionListenerForFocus = null;
    private int mTemplate = 1;

    FocusMeteringControl(Camera2CameraControlImpl cameraControl, ScheduledExecutorService scheduler, Executor executor, Quirks cameraQuirks) {
        MeteringRectangle[] meteringRectangleArr = EMPTY_RECTANGLES;
        this.mAfRects = meteringRectangleArr;
        this.mAeRects = meteringRectangleArr;
        this.mAwbRects = meteringRectangleArr;
        this.mRunningActionCompleter = null;
        this.mRunningCancelCompleter = null;
        this.mCameraControl = cameraControl;
        this.mExecutor = executor;
        this.mScheduler = scheduler;
        this.mMeteringRegionCorrection = new MeteringRegionCorrection(cameraQuirks);
    }

    /* access modifiers changed from: package-private */
    public void setActive(boolean isActive) {
        if (isActive != this.mIsActive) {
            this.mIsActive = isActive;
            if (!this.mIsActive) {
                cancelFocusAndMeteringWithoutAsyncResult();
            }
        }
    }

    public void setPreviewAspectRatio(Rational previewAspectRatio) {
        this.mPreviewAspectRatio = previewAspectRatio;
    }

    private Rational getDefaultAspectRatio() {
        if (this.mPreviewAspectRatio != null) {
            return this.mPreviewAspectRatio;
        }
        Rect cropSensorRegion = this.mCameraControl.getCropSensorRegion();
        return new Rational(cropSensorRegion.width(), cropSensorRegion.height());
    }

    /* access modifiers changed from: package-private */
    public void setTemplate(int template) {
        this.mTemplate = template;
    }

    /* access modifiers changed from: package-private */
    public void addFocusMeteringOptions(Camera2ImplConfig.Builder configBuilder) {
        int afMode;
        if (this.mIsInAfAutoMode) {
            afMode = 1;
        } else {
            afMode = getDefaultAfMode();
        }
        configBuilder.setCaptureRequestOption(CaptureRequest.CONTROL_AF_MODE, Integer.valueOf(this.mCameraControl.getSupportedAfMode(afMode)));
        if (this.mAfRects.length != 0) {
            configBuilder.setCaptureRequestOption(CaptureRequest.CONTROL_AF_REGIONS, this.mAfRects);
        }
        if (this.mAeRects.length != 0) {
            configBuilder.setCaptureRequestOption(CaptureRequest.CONTROL_AE_REGIONS, this.mAeRects);
        }
        if (this.mAwbRects.length != 0) {
            configBuilder.setCaptureRequestOption(CaptureRequest.CONTROL_AWB_REGIONS, this.mAwbRects);
        }
    }

    private static boolean isValid(MeteringPoint pt) {
        return pt.getX() >= 0.0f && pt.getX() <= 1.0f && pt.getY() >= 0.0f && pt.getY() <= 1.0f;
    }

    private static PointF getFovAdjustedPoint(MeteringPoint meteringPoint, Rational cropRegionAspectRatio, Rational defaultAspectRatio, int meteringMode, MeteringRegionCorrection correction) {
        Rational rational = cropRegionAspectRatio;
        Rational fovAspectRatio = defaultAspectRatio;
        if (meteringPoint.getSurfaceAspectRatio() != null) {
            fovAspectRatio = meteringPoint.getSurfaceAspectRatio();
        }
        PointF adjustedPoint = correction.getCorrectedPoint(meteringPoint, meteringMode);
        if (!fovAspectRatio.equals(rational)) {
            if (fovAspectRatio.compareTo(rational) > 0) {
                float heightOfCropRegion = (float) (fovAspectRatio.doubleValue() / cropRegionAspectRatio.doubleValue());
                adjustedPoint.y = (adjustedPoint.y + ((float) ((((double) heightOfCropRegion) - 1.0d) / 2.0d))) * (1.0f / heightOfCropRegion);
            } else {
                float widthOfCropRegion = (float) (cropRegionAspectRatio.doubleValue() / fovAspectRatio.doubleValue());
                adjustedPoint.x = (adjustedPoint.x + ((float) ((((double) widthOfCropRegion) - 1.0d) / 2.0d))) * (1.0f / widthOfCropRegion);
            }
        }
        return adjustedPoint;
    }

    private static MeteringRectangle getMeteringRect(MeteringPoint meteringPoint, PointF adjustedPoint, Rect cropRegion) {
        int centerX = (int) (((float) cropRegion.left) + (adjustedPoint.x * ((float) cropRegion.width())));
        int centerY = (int) (((float) cropRegion.top) + (adjustedPoint.y * ((float) cropRegion.height())));
        int width = (int) (meteringPoint.getSize() * ((float) cropRegion.width()));
        int height = (int) (meteringPoint.getSize() * ((float) cropRegion.height()));
        Rect focusRect = new Rect(centerX - (width / 2), centerY - (height / 2), (width / 2) + centerX, (height / 2) + centerY);
        focusRect.left = rangeLimit(focusRect.left, cropRegion.right, cropRegion.left);
        focusRect.right = rangeLimit(focusRect.right, cropRegion.right, cropRegion.left);
        focusRect.top = rangeLimit(focusRect.top, cropRegion.bottom, cropRegion.top);
        focusRect.bottom = rangeLimit(focusRect.bottom, cropRegion.bottom, cropRegion.top);
        return new MeteringRectangle(focusRect, 1000);
    }

    private static int rangeLimit(int val, int max, int min) {
        return Math.min(Math.max(val, min), max);
    }

    /* access modifiers changed from: package-private */
    public ListenableFuture<FocusMeteringResult> startFocusAndMetering(FocusMeteringAction action) {
        return CallbackToFutureAdapter.getFuture(new FocusMeteringControl$$ExternalSyntheticLambda3(this, action));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$startFocusAndMetering$1$androidx-camera-camera2-internal-FocusMeteringControl  reason: not valid java name */
    public /* synthetic */ Object m72lambda$startFocusAndMetering$1$androidxcameracamera2internalFocusMeteringControl(FocusMeteringAction action, CallbackToFutureAdapter.Completer completer) throws Exception {
        this.mExecutor.execute(new FocusMeteringControl$$ExternalSyntheticLambda9(this, completer, action));
        return "startFocusAndMetering";
    }

    private List<MeteringRectangle> getMeteringRectangles(List<MeteringPoint> meteringPoints, int maxRegionCount, Rational defaultAspectRatio, Rect cropSensorRegion, int meteringMode) {
        if (meteringPoints.isEmpty() || maxRegionCount == 0) {
            return Collections.emptyList();
        }
        List<MeteringRectangle> meteringRectanglesList = new ArrayList<>();
        Rational cropRegionAspectRatio = new Rational(cropSensorRegion.width(), cropSensorRegion.height());
        for (MeteringPoint meteringPoint : meteringPoints) {
            if (meteringRectanglesList.size() == maxRegionCount) {
                break;
            } else if (isValid(meteringPoint)) {
                MeteringRectangle meteringRectangle = getMeteringRect(meteringPoint, getFovAdjustedPoint(meteringPoint, cropRegionAspectRatio, defaultAspectRatio, meteringMode, this.mMeteringRegionCorrection), cropSensorRegion);
                if (!(meteringRectangle.getWidth() == 0 || meteringRectangle.getHeight() == 0)) {
                    meteringRectanglesList.add(meteringRectangle);
                }
            }
        }
        return Collections.unmodifiableList(meteringRectanglesList);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: startFocusAndMeteringInternal */
    public void m71lambda$startFocusAndMetering$0$androidxcameracamera2internalFocusMeteringControl(CallbackToFutureAdapter.Completer<FocusMeteringResult> completer, FocusMeteringAction action) {
        if (!this.mIsActive) {
            completer.setException(new CameraControl.OperationCanceledException("Camera is not active."));
            return;
        }
        Rect cropSensorRegion = this.mCameraControl.getCropSensorRegion();
        Rational defaultAspectRatio = getDefaultAspectRatio();
        Rational rational = defaultAspectRatio;
        Rect rect = cropSensorRegion;
        List<MeteringRectangle> rectanglesAf = getMeteringRectangles(action.getMeteringPointsAf(), this.mCameraControl.getMaxAfRegionCount(), rational, rect, 1);
        List<MeteringRectangle> rectanglesAe = getMeteringRectangles(action.getMeteringPointsAe(), this.mCameraControl.getMaxAeRegionCount(), rational, rect, 2);
        List<MeteringRectangle> rectanglesAwb = getMeteringRectangles(action.getMeteringPointsAwb(), this.mCameraControl.getMaxAwbRegionCount(), rational, rect, 4);
        if (!rectanglesAf.isEmpty() || !rectanglesAe.isEmpty() || !rectanglesAwb.isEmpty()) {
            failActionFuture("Cancelled by another startFocusAndMetering()");
            failCancelFuture("Cancelled by another startFocusAndMetering()");
            disableAutoCancel();
            this.mRunningActionCompleter = completer;
            MeteringRectangle[] meteringRectangleArr = EMPTY_RECTANGLES;
            executeMeteringAction((MeteringRectangle[]) rectanglesAf.toArray(meteringRectangleArr), (MeteringRectangle[]) rectanglesAe.toArray(meteringRectangleArr), (MeteringRectangle[]) rectanglesAwb.toArray(meteringRectangleArr), action);
            return;
        }
        completer.setException(new IllegalArgumentException("None of the specified AF/AE/AWB MeteringPoints is supported on this camera."));
    }

    /* access modifiers changed from: package-private */
    public void triggerAf(final CallbackToFutureAdapter.Completer<CameraCaptureResult> completer, boolean overrideAeMode) {
        if (this.mIsActive) {
            CaptureConfig.Builder builder = new CaptureConfig.Builder();
            builder.setTemplateType(this.mTemplate);
            builder.setUseRepeatingSurface(true);
            Camera2ImplConfig.Builder configBuilder = new Camera2ImplConfig.Builder();
            configBuilder.setCaptureRequestOption(CaptureRequest.CONTROL_AF_TRIGGER, 1);
            if (overrideAeMode) {
                configBuilder.setCaptureRequestOption(CaptureRequest.CONTROL_AE_MODE, Integer.valueOf(this.mCameraControl.getSupportedAeMode(1)));
            }
            builder.addImplementationOptions(configBuilder.build());
            builder.addCameraCaptureCallback(new CameraCaptureCallback() {
                public void onCaptureCompleted(CameraCaptureResult cameraCaptureResult) {
                    CallbackToFutureAdapter.Completer completer = completer;
                    if (completer != null) {
                        completer.set(cameraCaptureResult);
                    }
                }

                public void onCaptureFailed(CameraCaptureFailure failure) {
                    CallbackToFutureAdapter.Completer completer = completer;
                    if (completer != null) {
                        completer.setException(new CameraControlInternal.CameraControlException(failure));
                    }
                }

                public void onCaptureCancelled() {
                    CallbackToFutureAdapter.Completer completer = completer;
                    if (completer != null) {
                        completer.setException(new CameraControl.OperationCanceledException("Camera is closed"));
                    }
                }
            });
            this.mCameraControl.submitCaptureRequestsInternal(Collections.singletonList(builder.build()));
        } else if (completer != null) {
            completer.setException(new CameraControl.OperationCanceledException("Camera is not active."));
        }
    }

    /* access modifiers changed from: package-private */
    public void triggerAePrecapture(final CallbackToFutureAdapter.Completer<Void> completer) {
        if (this.mIsActive) {
            CaptureConfig.Builder builder = new CaptureConfig.Builder();
            builder.setTemplateType(this.mTemplate);
            builder.setUseRepeatingSurface(true);
            Camera2ImplConfig.Builder configBuilder = new Camera2ImplConfig.Builder();
            configBuilder.setCaptureRequestOption(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, 1);
            builder.addImplementationOptions(configBuilder.build());
            builder.addCameraCaptureCallback(new CameraCaptureCallback() {
                public void onCaptureCompleted(CameraCaptureResult cameraCaptureResult) {
                    CallbackToFutureAdapter.Completer completer = completer;
                    if (completer != null) {
                        completer.set(null);
                    }
                }

                public void onCaptureFailed(CameraCaptureFailure failure) {
                    CallbackToFutureAdapter.Completer completer = completer;
                    if (completer != null) {
                        completer.setException(new CameraControlInternal.CameraControlException(failure));
                    }
                }

                public void onCaptureCancelled() {
                    CallbackToFutureAdapter.Completer completer = completer;
                    if (completer != null) {
                        completer.setException(new CameraControl.OperationCanceledException("Camera is closed"));
                    }
                }
            });
            this.mCameraControl.submitCaptureRequestsInternal(Collections.singletonList(builder.build()));
        } else if (completer != null) {
            completer.setException(new CameraControl.OperationCanceledException("Camera is not active."));
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelAfAeTrigger(boolean cancelAfTrigger, boolean cancelAePrecaptureTrigger) {
        if (this.mIsActive) {
            CaptureConfig.Builder builder = new CaptureConfig.Builder();
            builder.setUseRepeatingSurface(true);
            builder.setTemplateType(this.mTemplate);
            Camera2ImplConfig.Builder configBuilder = new Camera2ImplConfig.Builder();
            if (cancelAfTrigger) {
                configBuilder.setCaptureRequestOption(CaptureRequest.CONTROL_AF_TRIGGER, 2);
            }
            if (Build.VERSION.SDK_INT >= 23 && cancelAePrecaptureTrigger) {
                configBuilder.setCaptureRequestOption(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, 2);
            }
            builder.addImplementationOptions(configBuilder.build());
            this.mCameraControl.submitCaptureRequestsInternal(Collections.singletonList(builder.build()));
        }
    }

    private void disableAutoCancel() {
        ScheduledFuture<?> scheduledFuture = this.mAutoCancelHandle;
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            this.mAutoCancelHandle = null;
        }
    }

    private void clearAutoFocusTimeoutHandle() {
        ScheduledFuture<?> scheduledFuture = this.mAutoFocusTimeoutHandle;
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            this.mAutoFocusTimeoutHandle = null;
        }
    }

    /* access modifiers changed from: package-private */
    public int getDefaultAfMode() {
        switch (this.mTemplate) {
            case 3:
                return 3;
            default:
                return 4;
        }
    }

    private boolean isAfModeSupported() {
        return this.mCameraControl.getSupportedAfMode(1) == 1;
    }

    /* access modifiers changed from: package-private */
    public void completeActionFuture(boolean isFocusSuccessful) {
        clearAutoFocusTimeoutHandle();
        CallbackToFutureAdapter.Completer<FocusMeteringResult> completer = this.mRunningActionCompleter;
        if (completer != null) {
            completer.set(FocusMeteringResult.create(isFocusSuccessful));
            this.mRunningActionCompleter = null;
        }
    }

    private void failActionFuture(String message) {
        this.mCameraControl.removeCaptureResultListener(this.mSessionListenerForFocus);
        CallbackToFutureAdapter.Completer<FocusMeteringResult> completer = this.mRunningActionCompleter;
        if (completer != null) {
            completer.setException(new CameraControl.OperationCanceledException(message));
            this.mRunningActionCompleter = null;
        }
    }

    private void failCancelFuture(String message) {
        this.mCameraControl.removeCaptureResultListener(this.mSessionListenerForCancel);
        CallbackToFutureAdapter.Completer<Void> completer = this.mRunningCancelCompleter;
        if (completer != null) {
            completer.setException(new CameraControl.OperationCanceledException(message));
            this.mRunningCancelCompleter = null;
        }
    }

    private void completeCancelFuture() {
        CallbackToFutureAdapter.Completer<Void> completer = this.mRunningCancelCompleter;
        if (completer != null) {
            completer.set(null);
            this.mRunningCancelCompleter = null;
        }
    }

    private void executeMeteringAction(MeteringRectangle[] afRects, MeteringRectangle[] aeRects, MeteringRectangle[] awbRects, FocusMeteringAction focusMeteringAction) {
        long sessionUpdateId;
        this.mCameraControl.removeCaptureResultListener(this.mSessionListenerForFocus);
        disableAutoCancel();
        clearAutoFocusTimeoutHandle();
        this.mAfRects = afRects;
        this.mAeRects = aeRects;
        this.mAwbRects = awbRects;
        if (shouldTriggerAF()) {
            this.mIsInAfAutoMode = true;
            this.mIsAutoFocusCompleted = false;
            this.mIsFocusSuccessful = false;
            sessionUpdateId = this.mCameraControl.updateSessionConfigSynchronous();
            triggerAf((CallbackToFutureAdapter.Completer<CameraCaptureResult>) null, true);
        } else {
            this.mIsInAfAutoMode = false;
            this.mIsAutoFocusCompleted = true;
            this.mIsFocusSuccessful = false;
            sessionUpdateId = this.mCameraControl.updateSessionConfigSynchronous();
        }
        this.mCurrentAfState = 0;
        FocusMeteringControl$$ExternalSyntheticLambda1 focusMeteringControl$$ExternalSyntheticLambda1 = new FocusMeteringControl$$ExternalSyntheticLambda1(this, isAfModeSupported(), sessionUpdateId);
        this.mSessionListenerForFocus = focusMeteringControl$$ExternalSyntheticLambda1;
        this.mCameraControl.addCaptureResultListener(focusMeteringControl$$ExternalSyntheticLambda1);
        long timeoutId = this.mFocusTimeoutCounter + 1;
        this.mFocusTimeoutCounter = timeoutId;
        this.mAutoFocusTimeoutHandle = this.mScheduler.schedule(new FocusMeteringControl$$ExternalSyntheticLambda5(this, timeoutId), AUTO_FOCUS_TIMEOUT_DURATION, TimeUnit.MILLISECONDS);
        if (focusMeteringAction.isAutoCancelEnabled()) {
            this.mAutoCancelHandle = this.mScheduler.schedule(new FocusMeteringControl$$ExternalSyntheticLambda7(this, timeoutId), focusMeteringAction.getAutoCancelDurationInMillis(), TimeUnit.MILLISECONDS);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$executeMeteringAction$2$androidx-camera-camera2-internal-FocusMeteringControl  reason: not valid java name */
    public /* synthetic */ boolean m66lambda$executeMeteringAction$2$androidxcameracamera2internalFocusMeteringControl(boolean isAfModeSupported, long sessionUpdateId, TotalCaptureResult result) {
        Integer afState = (Integer) result.get(CaptureResult.CONTROL_AF_STATE);
        if (shouldTriggerAF()) {
            if (!isAfModeSupported || afState == null) {
                this.mIsFocusSuccessful = true;
                this.mIsAutoFocusCompleted = true;
            } else if (this.mCurrentAfState.intValue() == 3) {
                if (afState.intValue() == 4) {
                    this.mIsFocusSuccessful = true;
                    this.mIsAutoFocusCompleted = true;
                } else if (afState.intValue() == 5) {
                    this.mIsFocusSuccessful = false;
                    this.mIsAutoFocusCompleted = true;
                }
            }
        }
        if (!this.mIsAutoFocusCompleted || !Camera2CameraControlImpl.isSessionUpdated(result, sessionUpdateId)) {
            if (!this.mCurrentAfState.equals(afState) && afState != null) {
                this.mCurrentAfState = afState;
            }
            return false;
        }
        completeActionFuture(this.mIsFocusSuccessful);
        return true;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$executeMeteringAction$4$androidx-camera-camera2-internal-FocusMeteringControl  reason: not valid java name */
    public /* synthetic */ void m68lambda$executeMeteringAction$4$androidxcameracamera2internalFocusMeteringControl(long timeoutId) {
        this.mExecutor.execute(new FocusMeteringControl$$ExternalSyntheticLambda4(this, timeoutId));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$executeMeteringAction$3$androidx-camera-camera2-internal-FocusMeteringControl  reason: not valid java name */
    public /* synthetic */ void m67lambda$executeMeteringAction$3$androidxcameracamera2internalFocusMeteringControl(long timeoutId) {
        if (timeoutId == this.mFocusTimeoutCounter) {
            this.mIsFocusSuccessful = false;
            completeActionFuture(false);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$executeMeteringAction$6$androidx-camera-camera2-internal-FocusMeteringControl  reason: not valid java name */
    public /* synthetic */ void m70lambda$executeMeteringAction$6$androidxcameracamera2internalFocusMeteringControl(long timeoutId) {
        this.mExecutor.execute(new FocusMeteringControl$$ExternalSyntheticLambda6(this, timeoutId));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$executeMeteringAction$5$androidx-camera-camera2-internal-FocusMeteringControl  reason: not valid java name */
    public /* synthetic */ void m69lambda$executeMeteringAction$5$androidxcameracamera2internalFocusMeteringControl(long timeoutId) {
        if (timeoutId == this.mFocusTimeoutCounter) {
            cancelFocusAndMeteringWithoutAsyncResult();
        }
    }

    private boolean shouldTriggerAF() {
        return this.mAfRects.length > 0;
    }

    /* access modifiers changed from: package-private */
    public ListenableFuture<Void> cancelFocusAndMetering() {
        return CallbackToFutureAdapter.getFuture(new FocusMeteringControl$$ExternalSyntheticLambda2(this));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$cancelFocusAndMetering$8$androidx-camera-camera2-internal-FocusMeteringControl  reason: not valid java name */
    public /* synthetic */ Object m64lambda$cancelFocusAndMetering$8$androidxcameracamera2internalFocusMeteringControl(CallbackToFutureAdapter.Completer completer) throws Exception {
        this.mExecutor.execute(new FocusMeteringControl$$ExternalSyntheticLambda8(this, completer));
        return "cancelFocusAndMetering";
    }

    /* access modifiers changed from: package-private */
    public void cancelFocusAndMeteringWithoutAsyncResult() {
        m63lambda$cancelFocusAndMetering$7$androidxcameracamera2internalFocusMeteringControl((CallbackToFutureAdapter.Completer<Void>) null);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: cancelFocusAndMeteringInternal */
    public void m63lambda$cancelFocusAndMetering$7$androidxcameracamera2internalFocusMeteringControl(CallbackToFutureAdapter.Completer<Void> completer) {
        failCancelFuture("Cancelled by another cancelFocusAndMetering()");
        failActionFuture("Cancelled by cancelFocusAndMetering()");
        this.mRunningCancelCompleter = completer;
        disableAutoCancel();
        clearAutoFocusTimeoutHandle();
        if (shouldTriggerAF()) {
            cancelAfAeTrigger(true, false);
        }
        MeteringRectangle[] meteringRectangleArr = EMPTY_RECTANGLES;
        this.mAfRects = meteringRectangleArr;
        this.mAeRects = meteringRectangleArr;
        this.mAwbRects = meteringRectangleArr;
        this.mIsInAfAutoMode = false;
        long sessionUpdateId = this.mCameraControl.updateSessionConfigSynchronous();
        if (this.mRunningCancelCompleter != null) {
            FocusMeteringControl$$ExternalSyntheticLambda0 focusMeteringControl$$ExternalSyntheticLambda0 = new FocusMeteringControl$$ExternalSyntheticLambda0(this, this.mCameraControl.getSupportedAfMode(getDefaultAfMode()), sessionUpdateId);
            this.mSessionListenerForCancel = focusMeteringControl$$ExternalSyntheticLambda0;
            this.mCameraControl.addCaptureResultListener(focusMeteringControl$$ExternalSyntheticLambda0);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$cancelFocusAndMeteringInternal$9$androidx-camera-camera2-internal-FocusMeteringControl  reason: not valid java name */
    public /* synthetic */ boolean m65lambda$cancelFocusAndMeteringInternal$9$androidxcameracamera2internalFocusMeteringControl(int targetAfMode, long sessionUpdateId, TotalCaptureResult captureResult) {
        if (((Integer) captureResult.get(CaptureResult.CONTROL_AF_MODE)).intValue() != targetAfMode || !Camera2CameraControlImpl.isSessionUpdated(captureResult, sessionUpdateId)) {
            return false;
        }
        completeCancelFuture();
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean isFocusMeteringSupported(FocusMeteringAction action) {
        Rect cropSensorRegion = this.mCameraControl.getCropSensorRegion();
        Rational defaultAspectRatio = getDefaultAspectRatio();
        Rect rect = cropSensorRegion;
        return !getMeteringRectangles(action.getMeteringPointsAf(), this.mCameraControl.getMaxAfRegionCount(), defaultAspectRatio, rect, 1).isEmpty() || !getMeteringRectangles(action.getMeteringPointsAe(), this.mCameraControl.getMaxAeRegionCount(), defaultAspectRatio, rect, 2).isEmpty() || !getMeteringRectangles(action.getMeteringPointsAwb(), this.mCameraControl.getMaxAwbRegionCount(), defaultAspectRatio, rect, 4).isEmpty();
    }
}

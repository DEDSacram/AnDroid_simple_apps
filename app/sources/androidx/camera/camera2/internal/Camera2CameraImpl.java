package androidx.camera.camera2.internal;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import androidx.camera.camera2.impl.Camera2ImplConfig;
import androidx.camera.camera2.internal.SynchronizedCaptureSessionOpener;
import androidx.camera.camera2.internal.compat.ApiCompat;
import androidx.camera.camera2.internal.compat.CameraAccessExceptionCompat;
import androidx.camera.camera2.internal.compat.CameraManagerCompat;
import androidx.camera.camera2.internal.compat.quirk.DeviceQuirks;
import androidx.camera.core.CameraState;
import androidx.camera.core.CameraUnavailableException;
import androidx.camera.core.Logger;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCase;
import androidx.camera.core.impl.CameraConfig;
import androidx.camera.core.impl.CameraConfigs;
import androidx.camera.core.impl.CameraControlInternal;
import androidx.camera.core.impl.CameraInfoInternal;
import androidx.camera.core.impl.CameraInternal;
import androidx.camera.core.impl.CameraStateRegistry;
import androidx.camera.core.impl.CaptureConfig;
import androidx.camera.core.impl.DeferrableSurface;
import androidx.camera.core.impl.ImmediateSurface;
import androidx.camera.core.impl.LiveDataObservable;
import androidx.camera.core.impl.Observable;
import androidx.camera.core.impl.SessionConfig;
import androidx.camera.core.impl.SessionProcessor;
import androidx.camera.core.impl.UseCaseAttachState;
import androidx.camera.core.impl.UseCaseConfig;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.impl.utils.futures.FutureCallback;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.util.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

final class Camera2CameraImpl implements CameraInternal {
    private static final int ERROR_NONE = 0;
    private static final String TAG = "Camera2CameraImpl";
    private final CameraAvailability mCameraAvailability;
    private CameraConfig mCameraConfig;
    private final Camera2CameraControlImpl mCameraControlInternal;
    CameraDevice mCameraDevice;
    int mCameraDeviceError;
    final Camera2CameraInfoImpl mCameraInfoInternal;
    private final CameraManagerCompat mCameraManager;
    private final CameraStateMachine mCameraStateMachine;
    private final CameraStateRegistry mCameraStateRegistry;
    CaptureSessionInterface mCaptureSession;
    private final SynchronizedCaptureSessionOpener.Builder mCaptureSessionOpenerBuilder;
    private final CaptureSessionRepository mCaptureSessionRepository;
    final Set<CaptureSession> mConfiguringForClose;
    private final DisplayInfoManager mDisplayInfoManager;
    private final Executor mExecutor;
    boolean mIsActiveResumingMode;
    final Object mLock;
    private MeteringRepeatingSession mMeteringRepeatingSession;
    private final Set<String> mNotifyStateAttachedSet;
    private final LiveDataObservable<CameraInternal.State> mObservableState;
    final AtomicInteger mReleaseRequestCount;
    final Map<CaptureSessionInterface, ListenableFuture<Void>> mReleasedCaptureSessions;
    private final ScheduledExecutorService mScheduledExecutorService;
    private SessionProcessor mSessionProcessor;
    volatile InternalState mState = InternalState.INITIALIZED;
    private final StateCallback mStateCallback;
    private final UseCaseAttachState mUseCaseAttachState;
    ListenableFuture<Void> mUserReleaseFuture;
    CallbackToFutureAdapter.Completer<Void> mUserReleaseNotifier;

    enum InternalState {
        INITIALIZED,
        PENDING_OPEN,
        OPENING,
        OPENED,
        CLOSING,
        REOPENING,
        RELEASING,
        RELEASED
    }

    Camera2CameraImpl(CameraManagerCompat cameraManager, String cameraId, Camera2CameraInfoImpl cameraInfoImpl, CameraStateRegistry cameraStateRegistry, Executor executor, Handler schedulerHandler, DisplayInfoManager displayInfoManager) throws CameraUnavailableException {
        CameraManagerCompat cameraManagerCompat = cameraManager;
        String str = cameraId;
        Camera2CameraInfoImpl camera2CameraInfoImpl = cameraInfoImpl;
        CameraStateRegistry cameraStateRegistry2 = cameraStateRegistry;
        LiveDataObservable<CameraInternal.State> liveDataObservable = new LiveDataObservable<>();
        this.mObservableState = liveDataObservable;
        this.mCameraDeviceError = 0;
        this.mReleaseRequestCount = new AtomicInteger(0);
        this.mReleasedCaptureSessions = new LinkedHashMap();
        this.mConfiguringForClose = new HashSet();
        this.mNotifyStateAttachedSet = new HashSet();
        this.mCameraConfig = CameraConfigs.emptyConfig();
        this.mLock = new Object();
        this.mIsActiveResumingMode = false;
        this.mCameraManager = cameraManagerCompat;
        this.mCameraStateRegistry = cameraStateRegistry2;
        ScheduledExecutorService newHandlerExecutor = CameraXExecutors.newHandlerExecutor(schedulerHandler);
        this.mScheduledExecutorService = newHandlerExecutor;
        Executor newSequentialExecutor = CameraXExecutors.newSequentialExecutor(executor);
        this.mExecutor = newSequentialExecutor;
        this.mStateCallback = new StateCallback(newSequentialExecutor, newHandlerExecutor);
        this.mUseCaseAttachState = new UseCaseAttachState(str);
        liveDataObservable.postValue(CameraInternal.State.CLOSED);
        CameraStateMachine cameraStateMachine = new CameraStateMachine(cameraStateRegistry2);
        this.mCameraStateMachine = cameraStateMachine;
        CaptureSessionRepository captureSessionRepository = new CaptureSessionRepository(newSequentialExecutor);
        this.mCaptureSessionRepository = captureSessionRepository;
        this.mDisplayInfoManager = displayInfoManager;
        this.mCaptureSession = newCaptureSession();
        try {
            Camera2CameraControlImpl camera2CameraControlImpl = r8;
            Camera2CameraControlImpl camera2CameraControlImpl2 = new Camera2CameraControlImpl(cameraManager.getCameraCharacteristicsCompat(cameraId), newHandlerExecutor, newSequentialExecutor, new ControlUpdateListenerInternal(), cameraInfoImpl.getCameraQuirks());
            this.mCameraControlInternal = camera2CameraControlImpl;
            this.mCameraInfoInternal = camera2CameraInfoImpl;
            camera2CameraInfoImpl.linkWithCameraControl(camera2CameraControlImpl);
            camera2CameraInfoImpl.setCameraStateSource(cameraStateMachine.getStateLiveData());
            this.mCaptureSessionOpenerBuilder = new SynchronizedCaptureSessionOpener.Builder(newSequentialExecutor, newHandlerExecutor, schedulerHandler, captureSessionRepository, cameraInfoImpl.getCameraQuirks(), DeviceQuirks.getAll());
            CameraAvailability cameraAvailability = new CameraAvailability(str);
            this.mCameraAvailability = cameraAvailability;
            cameraStateRegistry2.registerCamera(this, newSequentialExecutor, cameraAvailability);
            cameraManagerCompat.registerAvailabilityCallback(newSequentialExecutor, cameraAvailability);
        } catch (CameraAccessExceptionCompat e) {
            throw CameraUnavailableExceptionHelper.createFrom(e);
        }
    }

    private CaptureSessionInterface newCaptureSession() {
        synchronized (this.mLock) {
            if (this.mSessionProcessor == null) {
                CaptureSession captureSession = new CaptureSession();
                return captureSession;
            }
            ProcessingCaptureSession processingCaptureSession = new ProcessingCaptureSession(this.mSessionProcessor, this.mCameraInfoInternal, this.mExecutor, this.mScheduledExecutorService);
            return processingCaptureSession;
        }
    }

    public void open() {
        this.mExecutor.execute(new Camera2CameraImpl$$ExternalSyntheticLambda11(this));
    }

    /* access modifiers changed from: private */
    public void openInternal() {
        boolean z = false;
        switch (AnonymousClass3.$SwitchMap$androidx$camera$camera2$internal$Camera2CameraImpl$InternalState[this.mState.ordinal()]) {
            case 1:
            case 2:
                tryForceOpenCameraDevice(false);
                return;
            case 3:
                setState(InternalState.REOPENING);
                if (!isSessionCloseComplete() && this.mCameraDeviceError == 0) {
                    if (this.mCameraDevice != null) {
                        z = true;
                    }
                    Preconditions.checkState(z, "Camera Device should be open if session close is not complete");
                    setState(InternalState.OPENED);
                    openCaptureSession();
                    return;
                }
                return;
            default:
                debugLog("open() ignored due to being in state: " + this.mState);
                return;
        }
    }

    public void close() {
        this.mExecutor.execute(new Camera2CameraImpl$$ExternalSyntheticLambda12(this));
    }

    /* access modifiers changed from: private */
    public void closeInternal() {
        debugLog("Closing camera.");
        boolean z = false;
        switch (this.mState) {
            case PENDING_OPEN:
                if (this.mCameraDevice == null) {
                    z = true;
                }
                Preconditions.checkState(z);
                setState(InternalState.INITIALIZED);
                return;
            case OPENED:
                setState(InternalState.CLOSING);
                closeCamera(false);
                return;
            case OPENING:
            case REOPENING:
                boolean canFinish = this.mStateCallback.cancelScheduledReopen();
                setState(InternalState.CLOSING);
                if (canFinish) {
                    Preconditions.checkState(isSessionCloseComplete());
                    finishClose();
                    return;
                }
                return;
            default:
                debugLog("close() ignored due to being in state: " + this.mState);
                return;
        }
    }

    private void configAndClose(boolean abortInFlightCaptures) {
        CaptureSession noOpSession = new CaptureSession();
        this.mConfiguringForClose.add(noOpSession);
        resetCaptureSession(abortInFlightCaptures);
        SurfaceTexture surfaceTexture = new SurfaceTexture(0);
        surfaceTexture.setDefaultBufferSize(640, 480);
        Surface surface = new Surface(surfaceTexture);
        Runnable closeAndCleanupRunner = new Camera2CameraImpl$$ExternalSyntheticLambda10(surface, surfaceTexture);
        SessionConfig.Builder builder = new SessionConfig.Builder();
        DeferrableSurface deferrableSurface = new ImmediateSurface(surface);
        builder.addNonRepeatingSurface(deferrableSurface);
        builder.setTemplateType(1);
        debugLog("Start configAndClose.");
        noOpSession.open(builder.build(), (CameraDevice) Preconditions.checkNotNull(this.mCameraDevice), this.mCaptureSessionOpenerBuilder.build()).addListener(new Camera2CameraImpl$$ExternalSyntheticLambda13(this, noOpSession, deferrableSurface, closeAndCleanupRunner), this.mExecutor);
    }

    static /* synthetic */ void lambda$configAndClose$0(Surface surface, SurfaceTexture surfaceTexture) {
        surface.release();
        surfaceTexture.release();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: releaseNoOpSession */
    public void m36lambda$configAndClose$1$androidxcameracamera2internalCamera2CameraImpl(CaptureSession noOpSession, DeferrableSurface deferrableSurface, Runnable closeAndCleanupRunner) {
        this.mConfiguringForClose.remove(noOpSession);
        ListenableFuture<Void> releaseFuture = releaseSession(noOpSession, false);
        deferrableSurface.close();
        Futures.successfulAsList(Arrays.asList(new ListenableFuture[]{releaseFuture, deferrableSurface.getTerminationFuture()})).addListener(closeAndCleanupRunner, CameraXExecutors.directExecutor());
    }

    /* access modifiers changed from: package-private */
    public boolean isSessionCloseComplete() {
        return this.mReleasedCaptureSessions.isEmpty() && this.mConfiguringForClose.isEmpty();
    }

    /* access modifiers changed from: package-private */
    public void finishClose() {
        Preconditions.checkState(this.mState == InternalState.RELEASING || this.mState == InternalState.CLOSING);
        Preconditions.checkState(this.mReleasedCaptureSessions.isEmpty());
        this.mCameraDevice = null;
        if (this.mState == InternalState.CLOSING) {
            setState(InternalState.INITIALIZED);
            return;
        }
        this.mCameraManager.unregisterAvailabilityCallback(this.mCameraAvailability);
        setState(InternalState.RELEASED);
        CallbackToFutureAdapter.Completer<Void> completer = this.mUserReleaseNotifier;
        if (completer != null) {
            completer.set(null);
            this.mUserReleaseNotifier = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void closeCamera(boolean abortInFlightCaptures) {
        Preconditions.checkState(this.mState == InternalState.CLOSING || this.mState == InternalState.RELEASING || (this.mState == InternalState.REOPENING && this.mCameraDeviceError != 0), "closeCamera should only be called in a CLOSING, RELEASING or REOPENING (with error) state. Current state: " + this.mState + " (error: " + getErrorMessage(this.mCameraDeviceError) + ")");
        if (Build.VERSION.SDK_INT <= 23 || Build.VERSION.SDK_INT >= 29 || !isLegacyDevice() || this.mCameraDeviceError != 0) {
            resetCaptureSession(abortInFlightCaptures);
        } else {
            configAndClose(abortInFlightCaptures);
        }
        this.mCaptureSession.cancelIssuedCaptureRequests();
    }

    public ListenableFuture<Void> release() {
        return CallbackToFutureAdapter.getFuture(new Camera2CameraImpl$$ExternalSyntheticLambda8(this));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$release$3$androidx-camera-camera2-internal-Camera2CameraImpl  reason: not valid java name */
    public /* synthetic */ Object m46lambda$release$3$androidxcameracamera2internalCamera2CameraImpl(CallbackToFutureAdapter.Completer completer) throws Exception {
        this.mExecutor.execute(new Camera2CameraImpl$$ExternalSyntheticLambda14(this, completer));
        return "Release[request=" + this.mReleaseRequestCount.getAndIncrement() + "]";
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$release$2$androidx-camera-camera2-internal-Camera2CameraImpl  reason: not valid java name */
    public /* synthetic */ void m45lambda$release$2$androidxcameracamera2internalCamera2CameraImpl(CallbackToFutureAdapter.Completer completer) {
        Futures.propagate(releaseInternal(), completer);
    }

    private ListenableFuture<Void> releaseInternal() {
        ListenableFuture<Void> future = getOrCreateUserReleaseFuture();
        boolean z = false;
        switch (AnonymousClass3.$SwitchMap$androidx$camera$camera2$internal$Camera2CameraImpl$InternalState[this.mState.ordinal()]) {
            case 1:
            case 2:
                if (this.mCameraDevice == null) {
                    z = true;
                }
                Preconditions.checkState(z);
                setState(InternalState.RELEASING);
                Preconditions.checkState(isSessionCloseComplete());
                finishClose();
                break;
            case 3:
            case 5:
            case 6:
            case 7:
                boolean canFinish = this.mStateCallback.cancelScheduledReopen();
                setState(InternalState.RELEASING);
                if (canFinish) {
                    Preconditions.checkState(isSessionCloseComplete());
                    finishClose();
                    break;
                }
                break;
            case 4:
                setState(InternalState.RELEASING);
                closeCamera(false);
                break;
            default:
                debugLog("release() ignored due to being in state: " + this.mState);
                break;
        }
        return future;
    }

    private ListenableFuture<Void> getOrCreateUserReleaseFuture() {
        if (this.mUserReleaseFuture == null) {
            if (this.mState != InternalState.RELEASED) {
                this.mUserReleaseFuture = CallbackToFutureAdapter.getFuture(new Camera2CameraImpl$$ExternalSyntheticLambda0(this));
            } else {
                this.mUserReleaseFuture = Futures.immediateFuture(null);
            }
        }
        return this.mUserReleaseFuture;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$getOrCreateUserReleaseFuture$4$androidx-camera-camera2-internal-Camera2CameraImpl  reason: not valid java name */
    public /* synthetic */ Object m38lambda$getOrCreateUserReleaseFuture$4$androidxcameracamera2internalCamera2CameraImpl(CallbackToFutureAdapter.Completer completer) throws Exception {
        Preconditions.checkState(this.mUserReleaseNotifier == null, "Camera can only be released once, so release completer should be null on creation.");
        this.mUserReleaseNotifier = completer;
        return "Release[camera=" + this + "]";
    }

    /* access modifiers changed from: package-private */
    public ListenableFuture<Void> releaseSession(final CaptureSessionInterface captureSession, boolean abortInFlightCaptures) {
        captureSession.close();
        ListenableFuture<Void> releaseFuture = captureSession.release(abortInFlightCaptures);
        debugLog("Releasing session in state " + this.mState.name());
        this.mReleasedCaptureSessions.put(captureSession, releaseFuture);
        Futures.addCallback(releaseFuture, new FutureCallback<Void>() {
            public void onSuccess(Void result) {
                Camera2CameraImpl.this.mReleasedCaptureSessions.remove(captureSession);
                switch (AnonymousClass3.$SwitchMap$androidx$camera$camera2$internal$Camera2CameraImpl$InternalState[Camera2CameraImpl.this.mState.ordinal()]) {
                    case 3:
                    case 7:
                        break;
                    case 6:
                        if (Camera2CameraImpl.this.mCameraDeviceError == 0) {
                            return;
                        }
                        break;
                    default:
                        return;
                }
                if (Camera2CameraImpl.this.isSessionCloseComplete() && Camera2CameraImpl.this.mCameraDevice != null) {
                    ApiCompat.Api21Impl.close(Camera2CameraImpl.this.mCameraDevice);
                    Camera2CameraImpl.this.mCameraDevice = null;
                }
            }

            public void onFailure(Throwable t) {
            }
        }, CameraXExecutors.directExecutor());
        return releaseFuture;
    }

    public Observable<CameraInternal.State> getCameraState() {
        return this.mObservableState;
    }

    public void onUseCaseActive(UseCase useCase) {
        Preconditions.checkNotNull(useCase);
        this.mExecutor.execute(new Camera2CameraImpl$$ExternalSyntheticLambda1(this, getUseCaseId(useCase), useCase.getSessionConfig(), useCase.getCurrentConfig()));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onUseCaseActive$5$androidx-camera-camera2-internal-Camera2CameraImpl  reason: not valid java name */
    public /* synthetic */ void m41lambda$onUseCaseActive$5$androidxcameracamera2internalCamera2CameraImpl(String useCaseId, SessionConfig sessionConfig, UseCaseConfig useCaseConfig) {
        debugLog("Use case " + useCaseId + " ACTIVE");
        this.mUseCaseAttachState.setUseCaseActive(useCaseId, sessionConfig, useCaseConfig);
        this.mUseCaseAttachState.updateUseCase(useCaseId, sessionConfig, useCaseConfig);
        updateCaptureSessionConfig();
    }

    public void onUseCaseInactive(UseCase useCase) {
        Preconditions.checkNotNull(useCase);
        this.mExecutor.execute(new Camera2CameraImpl$$ExternalSyntheticLambda16(this, getUseCaseId(useCase)));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onUseCaseInactive$6$androidx-camera-camera2-internal-Camera2CameraImpl  reason: not valid java name */
    public /* synthetic */ void m42lambda$onUseCaseInactive$6$androidxcameracamera2internalCamera2CameraImpl(String useCaseId) {
        debugLog("Use case " + useCaseId + " INACTIVE");
        this.mUseCaseAttachState.setUseCaseInactive(useCaseId);
        updateCaptureSessionConfig();
    }

    public void onUseCaseUpdated(UseCase useCase) {
        Preconditions.checkNotNull(useCase);
        this.mExecutor.execute(new Camera2CameraImpl$$ExternalSyntheticLambda3(this, getUseCaseId(useCase), useCase.getSessionConfig(), useCase.getCurrentConfig()));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onUseCaseUpdated$7$androidx-camera-camera2-internal-Camera2CameraImpl  reason: not valid java name */
    public /* synthetic */ void m44lambda$onUseCaseUpdated$7$androidxcameracamera2internalCamera2CameraImpl(String useCaseId, SessionConfig sessionConfig, UseCaseConfig useCaseConfig) {
        debugLog("Use case " + useCaseId + " UPDATED");
        this.mUseCaseAttachState.updateUseCase(useCaseId, sessionConfig, useCaseConfig);
        updateCaptureSessionConfig();
    }

    public void onUseCaseReset(UseCase useCase) {
        Preconditions.checkNotNull(useCase);
        this.mExecutor.execute(new Camera2CameraImpl$$ExternalSyntheticLambda2(this, getUseCaseId(useCase), useCase.getSessionConfig(), useCase.getCurrentConfig()));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onUseCaseReset$8$androidx-camera-camera2-internal-Camera2CameraImpl  reason: not valid java name */
    public /* synthetic */ void m43lambda$onUseCaseReset$8$androidxcameracamera2internalCamera2CameraImpl(String useCaseId, SessionConfig sessionConfig, UseCaseConfig useCaseConfig) {
        debugLog("Use case " + useCaseId + " RESET");
        this.mUseCaseAttachState.updateUseCase(useCaseId, sessionConfig, useCaseConfig);
        resetCaptureSession(false);
        updateCaptureSessionConfig();
        if (this.mState == InternalState.OPENED) {
            openCaptureSession();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isUseCaseAttached(UseCase useCase) {
        try {
            return ((Boolean) CallbackToFutureAdapter.getFuture(new Camera2CameraImpl$$ExternalSyntheticLambda9(this, getUseCaseId(useCase))).get()).booleanValue();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Unable to check if use case is attached.", e);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$isUseCaseAttached$10$androidx-camera-camera2-internal-Camera2CameraImpl  reason: not valid java name */
    public /* synthetic */ Object m39lambda$isUseCaseAttached$10$androidxcameracamera2internalCamera2CameraImpl(String useCaseId, CallbackToFutureAdapter.Completer completer) throws Exception {
        try {
            this.mExecutor.execute(new Camera2CameraImpl$$ExternalSyntheticLambda15(this, completer, useCaseId));
            return "isUseCaseAttached";
        } catch (RejectedExecutionException e) {
            completer.setException(new RuntimeException("Unable to check if use case is attached. Camera executor shut down."));
            return "isUseCaseAttached";
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$isUseCaseAttached$9$androidx-camera-camera2-internal-Camera2CameraImpl  reason: not valid java name */
    public /* synthetic */ void m40lambda$isUseCaseAttached$9$androidxcameracamera2internalCamera2CameraImpl(CallbackToFutureAdapter.Completer completer, String useCaseId) {
        completer.set(Boolean.valueOf(this.mUseCaseAttachState.isUseCaseAttached(useCaseId)));
    }

    public void attachUseCases(Collection<UseCase> inputUseCases) {
        Collection<UseCase> useCases = new ArrayList<>(inputUseCases);
        if (!useCases.isEmpty()) {
            this.mCameraControlInternal.incrementUseCount();
            notifyStateAttachedToUseCases(new ArrayList(useCases));
            try {
                this.mExecutor.execute(new Camera2CameraImpl$$ExternalSyntheticLambda4(this, new ArrayList<>(toUseCaseInfos(useCases))));
            } catch (RejectedExecutionException e) {
                debugLog("Unable to attach use cases.", e);
                this.mCameraControlInternal.decrementUseCount();
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$attachUseCases$11$androidx-camera-camera2-internal-Camera2CameraImpl  reason: not valid java name */
    public /* synthetic */ void m35lambda$attachUseCases$11$androidxcameracamera2internalCamera2CameraImpl(List useCaseInfos) {
        try {
            tryAttachUseCases(useCaseInfos);
        } finally {
            this.mCameraControlInternal.decrementUseCount();
        }
    }

    private void tryAttachUseCases(Collection<UseCaseInfo> useCaseInfos) {
        Size resolution;
        boolean attachUseCaseFromEmpty = this.mUseCaseAttachState.getAttachedSessionConfigs().isEmpty();
        List<String> useCaseIdsToAttach = new ArrayList<>();
        Rational previewAspectRatio = null;
        for (UseCaseInfo useCaseInfo : useCaseInfos) {
            if (!this.mUseCaseAttachState.isUseCaseAttached(useCaseInfo.getUseCaseId())) {
                this.mUseCaseAttachState.setUseCaseAttached(useCaseInfo.getUseCaseId(), useCaseInfo.getSessionConfig(), useCaseInfo.getUseCaseConfig());
                useCaseIdsToAttach.add(useCaseInfo.getUseCaseId());
                if (useCaseInfo.getUseCaseType() == Preview.class && (resolution = useCaseInfo.getSurfaceResolution()) != null) {
                    previewAspectRatio = new Rational(resolution.getWidth(), resolution.getHeight());
                }
            }
        }
        if (!useCaseIdsToAttach.isEmpty()) {
            debugLog("Use cases [" + TextUtils.join(", ", useCaseIdsToAttach) + "] now ATTACHED");
            if (attachUseCaseFromEmpty) {
                this.mCameraControlInternal.setActive(true);
                this.mCameraControlInternal.incrementUseCount();
            }
            addOrRemoveMeteringRepeatingUseCase();
            updateZslDisabledByUseCaseConfigStatus();
            updateCaptureSessionConfig();
            resetCaptureSession(false);
            if (this.mState == InternalState.OPENED) {
                openCaptureSession();
            } else {
                openInternal();
            }
            if (previewAspectRatio != null) {
                this.mCameraControlInternal.setPreviewAspectRatio(previewAspectRatio);
            }
        }
    }

    private Collection<UseCaseInfo> toUseCaseInfos(Collection<UseCase> useCases) {
        List<UseCaseInfo> useCaseInfos = new ArrayList<>();
        for (UseCase useCase : useCases) {
            useCaseInfos.add(UseCaseInfo.from(useCase));
        }
        return useCaseInfos;
    }

    public void setExtendedConfig(CameraConfig cameraConfig) {
        if (cameraConfig == null) {
            cameraConfig = CameraConfigs.emptyConfig();
        }
        SessionProcessor sessionProcessor = cameraConfig.getSessionProcessor((SessionProcessor) null);
        this.mCameraConfig = cameraConfig;
        synchronized (this.mLock) {
            this.mSessionProcessor = sessionProcessor;
        }
    }

    public CameraConfig getExtendedConfig() {
        return this.mCameraConfig;
    }

    private void notifyStateAttachedToUseCases(List<UseCase> useCases) {
        for (UseCase useCase : useCases) {
            String useCaseId = getUseCaseId(useCase);
            if (!this.mNotifyStateAttachedSet.contains(useCaseId)) {
                this.mNotifyStateAttachedSet.add(useCaseId);
                useCase.onStateAttached();
            }
        }
    }

    private void notifyStateDetachedToUseCases(List<UseCase> useCases) {
        for (UseCase useCase : useCases) {
            String useCaseId = getUseCaseId(useCase);
            if (this.mNotifyStateAttachedSet.contains(useCaseId)) {
                useCase.onStateDetached();
                this.mNotifyStateAttachedSet.remove(useCaseId);
            }
        }
    }

    public void detachUseCases(Collection<UseCase> inputUseCases) {
        Collection<UseCase> useCases = new ArrayList<>(inputUseCases);
        if (!useCases.isEmpty()) {
            List<UseCaseInfo> useCaseInfos = new ArrayList<>(toUseCaseInfos(useCases));
            notifyStateDetachedToUseCases(new ArrayList(useCases));
            this.mExecutor.execute(new Camera2CameraImpl$$ExternalSyntheticLambda5(this, useCaseInfos));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: tryDetachUseCases */
    public void m37lambda$detachUseCases$12$androidxcameracamera2internalCamera2CameraImpl(Collection<UseCaseInfo> useCaseInfos) {
        List<String> useCaseIdsToDetach = new ArrayList<>();
        boolean clearPreviewAspectRatio = false;
        for (UseCaseInfo useCaseInfo : useCaseInfos) {
            if (this.mUseCaseAttachState.isUseCaseAttached(useCaseInfo.getUseCaseId())) {
                this.mUseCaseAttachState.removeUseCase(useCaseInfo.getUseCaseId());
                useCaseIdsToDetach.add(useCaseInfo.getUseCaseId());
                if (useCaseInfo.getUseCaseType() == Preview.class) {
                    clearPreviewAspectRatio = true;
                }
            }
        }
        if (!useCaseIdsToDetach.isEmpty()) {
            debugLog("Use cases [" + TextUtils.join(", ", useCaseIdsToDetach) + "] now DETACHED for camera");
            if (clearPreviewAspectRatio) {
                this.mCameraControlInternal.setPreviewAspectRatio((Rational) null);
            }
            addOrRemoveMeteringRepeatingUseCase();
            if (this.mUseCaseAttachState.getAttachedUseCaseConfigs().isEmpty()) {
                this.mCameraControlInternal.setZslDisabledByUserCaseConfig(false);
            } else {
                updateZslDisabledByUseCaseConfigStatus();
            }
            if (this.mUseCaseAttachState.getAttachedSessionConfigs().isEmpty()) {
                this.mCameraControlInternal.decrementUseCount();
                resetCaptureSession(false);
                this.mCameraControlInternal.setActive(false);
                this.mCaptureSession = newCaptureSession();
                closeInternal();
                return;
            }
            updateCaptureSessionConfig();
            resetCaptureSession(false);
            if (this.mState == InternalState.OPENED) {
                openCaptureSession();
            }
        }
    }

    private void updateZslDisabledByUseCaseConfigStatus() {
        boolean isZslDisabledByUseCaseConfig = false;
        for (UseCaseConfig<?> useCaseConfig : this.mUseCaseAttachState.getAttachedUseCaseConfigs()) {
            isZslDisabledByUseCaseConfig |= useCaseConfig.isZslDisabled(false);
        }
        this.mCameraControlInternal.setZslDisabledByUserCaseConfig(isZslDisabledByUseCaseConfig);
    }

    private void addOrRemoveMeteringRepeatingUseCase() {
        SessionConfig sessionConfig = this.mUseCaseAttachState.getAttachedBuilder().build();
        CaptureConfig captureConfig = sessionConfig.getRepeatingCaptureConfig();
        int sizeRepeatingSurfaces = captureConfig.getSurfaces().size();
        int sizeSessionSurfaces = sessionConfig.getSurfaces().size();
        if (sessionConfig.getSurfaces().isEmpty()) {
            return;
        }
        if (captureConfig.getSurfaces().isEmpty()) {
            if (this.mMeteringRepeatingSession == null) {
                this.mMeteringRepeatingSession = new MeteringRepeatingSession(this.mCameraInfoInternal.getCameraCharacteristicsCompat(), this.mDisplayInfoManager);
            }
            addMeteringRepeating();
        } else if (sizeSessionSurfaces == 1 && sizeRepeatingSurfaces == 1) {
            removeMeteringRepeating();
        } else if (sizeRepeatingSurfaces >= 2) {
            removeMeteringRepeating();
        } else {
            Logger.d(TAG, "mMeteringRepeating is ATTACHED, SessionConfig Surfaces: " + sizeSessionSurfaces + ", CaptureConfig Surfaces: " + sizeRepeatingSurfaces);
        }
    }

    private void removeMeteringRepeating() {
        if (this.mMeteringRepeatingSession != null) {
            this.mUseCaseAttachState.setUseCaseDetached(this.mMeteringRepeatingSession.getName() + this.mMeteringRepeatingSession.hashCode());
            this.mUseCaseAttachState.setUseCaseInactive(this.mMeteringRepeatingSession.getName() + this.mMeteringRepeatingSession.hashCode());
            this.mMeteringRepeatingSession.clear();
            this.mMeteringRepeatingSession = null;
        }
    }

    private void addMeteringRepeating() {
        if (this.mMeteringRepeatingSession != null) {
            this.mUseCaseAttachState.setUseCaseAttached(this.mMeteringRepeatingSession.getName() + this.mMeteringRepeatingSession.hashCode(), this.mMeteringRepeatingSession.getSessionConfig(), this.mMeteringRepeatingSession.getUseCaseConfig());
            this.mUseCaseAttachState.setUseCaseActive(this.mMeteringRepeatingSession.getName() + this.mMeteringRepeatingSession.hashCode(), this.mMeteringRepeatingSession.getSessionConfig(), this.mMeteringRepeatingSession.getUseCaseConfig());
        }
    }

    public CameraInfoInternal getCameraInfoInternal() {
        return this.mCameraInfoInternal;
    }

    public CameraAvailability getCameraAvailability() {
        return this.mCameraAvailability;
    }

    /* access modifiers changed from: package-private */
    public void tryForceOpenCameraDevice(boolean fromScheduledCameraReopen) {
        debugLog("Attempting to force open the camera.");
        if (!this.mCameraStateRegistry.tryOpenCamera(this)) {
            debugLog("No cameras available. Waiting for available camera before opening camera.");
            setState(InternalState.PENDING_OPEN);
            return;
        }
        openCameraDevice(fromScheduledCameraReopen);
    }

    /* access modifiers changed from: package-private */
    public void tryOpenCameraDevice(boolean fromScheduledCameraReopen) {
        debugLog("Attempting to open the camera.");
        if (!(this.mCameraAvailability.isCameraAvailable() && this.mCameraStateRegistry.tryOpenCamera(this))) {
            debugLog("No cameras available. Waiting for available camera before opening camera.");
            setState(InternalState.PENDING_OPEN);
            return;
        }
        openCameraDevice(fromScheduledCameraReopen);
    }

    public void setActiveResumingMode(boolean enabled) {
        this.mExecutor.execute(new Camera2CameraImpl$$ExternalSyntheticLambda6(this, enabled));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$setActiveResumingMode$13$androidx-camera-camera2-internal-Camera2CameraImpl  reason: not valid java name */
    public /* synthetic */ void m47lambda$setActiveResumingMode$13$androidxcameracamera2internalCamera2CameraImpl(boolean enabled) {
        this.mIsActiveResumingMode = enabled;
        if (enabled && this.mState == InternalState.PENDING_OPEN) {
            tryForceOpenCameraDevice(false);
        }
    }

    private void openCameraDevice(boolean fromScheduledCameraReopen) {
        if (!fromScheduledCameraReopen) {
            this.mStateCallback.resetReopenMonitor();
        }
        this.mStateCallback.cancelScheduledReopen();
        debugLog("Opening camera.");
        setState(InternalState.OPENING);
        try {
            this.mCameraManager.openCamera(this.mCameraInfoInternal.getCameraId(), this.mExecutor, createDeviceStateCallback());
        } catch (CameraAccessExceptionCompat e) {
            debugLog("Unable to open camera due to " + e.getMessage());
            switch (e.getReason()) {
                case CameraAccessExceptionCompat.CAMERA_UNAVAILABLE_DO_NOT_DISTURB /*10001*/:
                    setState(InternalState.INITIALIZED, CameraState.StateError.create(7, e));
                    return;
                default:
                    return;
            }
        } catch (SecurityException e2) {
            debugLog("Unable to open camera due to " + e2.getMessage());
            setState(InternalState.REOPENING);
            this.mStateCallback.scheduleCameraReopen();
        }
    }

    /* access modifiers changed from: package-private */
    public void updateCaptureSessionConfig() {
        SessionConfig.ValidatingBuilder validatingBuilder = this.mUseCaseAttachState.getActiveAndAttachedBuilder();
        if (validatingBuilder.isValid()) {
            this.mCameraControlInternal.setTemplate(validatingBuilder.build().getTemplateType());
            validatingBuilder.add(this.mCameraControlInternal.getSessionConfig());
            this.mCaptureSession.setSessionConfig(validatingBuilder.build());
            return;
        }
        this.mCameraControlInternal.resetTemplate();
        this.mCaptureSession.setSessionConfig(this.mCameraControlInternal.getSessionConfig());
    }

    /* access modifiers changed from: package-private */
    public void openCaptureSession() {
        Preconditions.checkState(this.mState == InternalState.OPENED);
        SessionConfig.ValidatingBuilder validatingBuilder = this.mUseCaseAttachState.getAttachedBuilder();
        if (!validatingBuilder.isValid()) {
            debugLog("Unable to create capture session due to conflicting configurations");
            return;
        }
        if (!validatingBuilder.build().getImplementationOptions().containsOption(Camera2ImplConfig.STREAM_USE_CASE_OPTION)) {
            validatingBuilder.addImplementationOption(Camera2ImplConfig.STREAM_USE_CASE_OPTION, Long.valueOf(StreamUseCaseUtil.getStreamUseCaseFromUseCaseConfigs(this.mUseCaseAttachState.getAttachedUseCaseConfigs(), this.mUseCaseAttachState.getAttachedSessionConfigs())));
        }
        Futures.addCallback(this.mCaptureSession.open(validatingBuilder.build(), (CameraDevice) Preconditions.checkNotNull(this.mCameraDevice), this.mCaptureSessionOpenerBuilder.build()), new FutureCallback<Void>() {
            public void onSuccess(Void result) {
            }

            public void onFailure(Throwable t) {
                if (t instanceof DeferrableSurface.SurfaceClosedException) {
                    SessionConfig sessionConfig = Camera2CameraImpl.this.findSessionConfigForSurface(((DeferrableSurface.SurfaceClosedException) t).getDeferrableSurface());
                    if (sessionConfig != null) {
                        Camera2CameraImpl.this.postSurfaceClosedError(sessionConfig);
                    }
                } else if (t instanceof CancellationException) {
                    Camera2CameraImpl.this.debugLog("Unable to configure camera cancelled");
                } else {
                    if (Camera2CameraImpl.this.mState == InternalState.OPENED) {
                        Camera2CameraImpl.this.setState(InternalState.OPENED, CameraState.StateError.create(4, t));
                    }
                    if (t instanceof CameraAccessException) {
                        Camera2CameraImpl.this.debugLog("Unable to configure camera due to " + t.getMessage());
                    } else if (t instanceof TimeoutException) {
                        Logger.e(Camera2CameraImpl.TAG, "Unable to configure camera " + Camera2CameraImpl.this.mCameraInfoInternal.getCameraId() + ", timeout!");
                    }
                }
            }
        }, this.mExecutor);
    }

    private boolean isLegacyDevice() {
        return ((Camera2CameraInfoImpl) getCameraInfoInternal()).getSupportedHardwareLevel() == 2;
    }

    /* access modifiers changed from: package-private */
    public SessionConfig findSessionConfigForSurface(DeferrableSurface surface) {
        for (SessionConfig sessionConfig : this.mUseCaseAttachState.getAttachedSessionConfigs()) {
            if (sessionConfig.getSurfaces().contains(surface)) {
                return sessionConfig;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void postSurfaceClosedError(SessionConfig sessionConfig) {
        Executor executor = CameraXExecutors.mainThreadExecutor();
        List<SessionConfig.ErrorListener> errorListeners = sessionConfig.getErrorListeners();
        if (!errorListeners.isEmpty()) {
            debugLog("Posting surface closed", new Throwable());
            executor.execute(new Camera2CameraImpl$$ExternalSyntheticLambda7(errorListeners.get(0), sessionConfig));
        }
    }

    /* access modifiers changed from: package-private */
    public void resetCaptureSession(boolean abortInFlightCaptures) {
        Preconditions.checkState(this.mCaptureSession != null);
        debugLog("Resetting Capture Session");
        CaptureSessionInterface oldCaptureSession = this.mCaptureSession;
        SessionConfig previousSessionConfig = oldCaptureSession.getSessionConfig();
        List<CaptureConfig> unissuedCaptureConfigs = oldCaptureSession.getCaptureConfigs();
        CaptureSessionInterface newCaptureSession = newCaptureSession();
        this.mCaptureSession = newCaptureSession;
        newCaptureSession.setSessionConfig(previousSessionConfig);
        this.mCaptureSession.issueCaptureRequests(unissuedCaptureConfigs);
        releaseSession(oldCaptureSession, abortInFlightCaptures);
    }

    private CameraDevice.StateCallback createDeviceStateCallback() {
        List<CameraDevice.StateCallback> allStateCallbacks = new ArrayList<>(this.mUseCaseAttachState.getAttachedBuilder().build().getDeviceStateCallbacks());
        allStateCallbacks.add(this.mCaptureSessionRepository.getCameraStateCallback());
        allStateCallbacks.add(this.mStateCallback);
        return CameraDeviceStateCallbacks.createComboCallback(allStateCallbacks);
    }

    private boolean checkAndAttachRepeatingSurface(CaptureConfig.Builder captureConfigBuilder) {
        if (!captureConfigBuilder.getSurfaces().isEmpty()) {
            Logger.w(TAG, "The capture config builder already has surface inside.");
            return false;
        }
        for (SessionConfig sessionConfig : this.mUseCaseAttachState.getActiveAndAttachedSessionConfigs()) {
            List<DeferrableSurface> surfaces = sessionConfig.getRepeatingCaptureConfig().getSurfaces();
            if (!surfaces.isEmpty()) {
                for (DeferrableSurface surface : surfaces) {
                    captureConfigBuilder.addSurface(surface);
                }
            }
        }
        if (!captureConfigBuilder.getSurfaces().isEmpty()) {
            return true;
        }
        Logger.w(TAG, "Unable to find a repeating surface to attach to CaptureConfig");
        return false;
    }

    public CameraControlInternal getCameraControlInternal() {
        return this.mCameraControlInternal;
    }

    /* access modifiers changed from: package-private */
    public void submitCaptureRequests(List<CaptureConfig> captureConfigs) {
        List<CaptureConfig> captureConfigsWithSurface = new ArrayList<>();
        for (CaptureConfig captureConfig : captureConfigs) {
            CaptureConfig.Builder builder = CaptureConfig.Builder.from(captureConfig);
            if (captureConfig.getTemplateType() == 5 && captureConfig.getCameraCaptureResult() != null) {
                builder.setCameraCaptureResult(captureConfig.getCameraCaptureResult());
            }
            if (!captureConfig.getSurfaces().isEmpty() || !captureConfig.isUseRepeatingSurface() || checkAndAttachRepeatingSurface(builder)) {
                captureConfigsWithSurface.add(builder.build());
            }
        }
        debugLog("Issue capture request");
        this.mCaptureSession.issueCaptureRequests(captureConfigsWithSurface);
    }

    public String toString() {
        return String.format(Locale.US, "Camera@%x[id=%s]", new Object[]{Integer.valueOf(hashCode()), this.mCameraInfoInternal.getCameraId()});
    }

    static String getUseCaseId(UseCase useCase) {
        return useCase.getName() + useCase.hashCode();
    }

    /* access modifiers changed from: package-private */
    public void debugLog(String msg) {
        debugLog(msg, (Throwable) null);
    }

    private void debugLog(String msg, Throwable throwable) {
        Logger.d(TAG, String.format("{%s} %s", new Object[]{toString(), msg}), throwable);
    }

    /* access modifiers changed from: package-private */
    public void setState(InternalState state) {
        setState(state, (CameraState.StateError) null);
    }

    /* access modifiers changed from: package-private */
    public void setState(InternalState state, CameraState.StateError stateError) {
        setState(state, stateError, true);
    }

    /* access modifiers changed from: package-private */
    public void setState(InternalState state, CameraState.StateError stateError, boolean notifyImmediately) {
        CameraInternal.State publicState;
        debugLog("Transitioning camera internal state: " + this.mState + " --> " + state);
        this.mState = state;
        switch (AnonymousClass3.$SwitchMap$androidx$camera$camera2$internal$Camera2CameraImpl$InternalState[state.ordinal()]) {
            case 1:
                publicState = CameraInternal.State.CLOSED;
                break;
            case 2:
                publicState = CameraInternal.State.PENDING_OPEN;
                break;
            case 3:
                publicState = CameraInternal.State.CLOSING;
                break;
            case 4:
                publicState = CameraInternal.State.OPEN;
                break;
            case 5:
            case 6:
                publicState = CameraInternal.State.OPENING;
                break;
            case 7:
                publicState = CameraInternal.State.RELEASING;
                break;
            case 8:
                publicState = CameraInternal.State.RELEASED;
                break;
            default:
                throw new IllegalStateException("Unknown state: " + state);
        }
        this.mCameraStateRegistry.markCameraState(this, publicState, notifyImmediately);
        this.mObservableState.postValue(publicState);
        this.mCameraStateMachine.updateState(publicState, stateError);
    }

    static String getErrorMessage(int errorCode) {
        switch (errorCode) {
            case 0:
                return "ERROR_NONE";
            case 1:
                return "ERROR_CAMERA_IN_USE";
            case 2:
                return "ERROR_MAX_CAMERAS_IN_USE";
            case 3:
                return "ERROR_CAMERA_DISABLED";
            case 4:
                return "ERROR_CAMERA_DEVICE";
            case 5:
                return "ERROR_CAMERA_SERVICE";
            default:
                return "UNKNOWN ERROR";
        }
    }

    static abstract class UseCaseInfo {
        /* access modifiers changed from: package-private */
        public abstract SessionConfig getSessionConfig();

        /* access modifiers changed from: package-private */
        public abstract Size getSurfaceResolution();

        /* access modifiers changed from: package-private */
        public abstract UseCaseConfig<?> getUseCaseConfig();

        /* access modifiers changed from: package-private */
        public abstract String getUseCaseId();

        /* access modifiers changed from: package-private */
        public abstract Class<?> getUseCaseType();

        UseCaseInfo() {
        }

        static UseCaseInfo create(String useCaseId, Class<?> useCaseType, SessionConfig sessionConfig, UseCaseConfig<?> useCaseConfig, Size surfaceResolution) {
            return new AutoValue_Camera2CameraImpl_UseCaseInfo(useCaseId, useCaseType, sessionConfig, useCaseConfig, surfaceResolution);
        }

        static UseCaseInfo from(UseCase useCase) {
            return create(Camera2CameraImpl.getUseCaseId(useCase), useCase.getClass(), useCase.getSessionConfig(), useCase.getCurrentConfig(), useCase.getAttachedSurfaceResolution());
        }
    }

    final class StateCallback extends CameraDevice.StateCallback {
        private final CameraReopenMonitor mCameraReopenMonitor = new CameraReopenMonitor();
        private final Executor mExecutor;
        ScheduledFuture<?> mScheduledReopenHandle;
        private ScheduledReopen mScheduledReopenRunnable;
        private final ScheduledExecutorService mScheduler;

        StateCallback(Executor executor, ScheduledExecutorService scheduler) {
            this.mExecutor = executor;
            this.mScheduler = scheduler;
        }

        public void onOpened(CameraDevice cameraDevice) {
            Camera2CameraImpl.this.debugLog("CameraDevice.onOpened()");
            Camera2CameraImpl.this.mCameraDevice = cameraDevice;
            Camera2CameraImpl.this.mCameraDeviceError = 0;
            resetReopenMonitor();
            switch (Camera2CameraImpl.this.mState) {
                case CLOSING:
                case RELEASING:
                    Preconditions.checkState(Camera2CameraImpl.this.isSessionCloseComplete());
                    Camera2CameraImpl.this.mCameraDevice.close();
                    Camera2CameraImpl.this.mCameraDevice = null;
                    return;
                case OPENING:
                case REOPENING:
                    Camera2CameraImpl.this.setState(InternalState.OPENED);
                    Camera2CameraImpl.this.openCaptureSession();
                    return;
                default:
                    throw new IllegalStateException("onOpened() should not be possible from state: " + Camera2CameraImpl.this.mState);
            }
        }

        public void onClosed(CameraDevice cameraDevice) {
            Camera2CameraImpl.this.debugLog("CameraDevice.onClosed()");
            Preconditions.checkState(Camera2CameraImpl.this.mCameraDevice == null, "Unexpected onClose callback on camera device: " + cameraDevice);
            switch (Camera2CameraImpl.this.mState) {
                case CLOSING:
                case RELEASING:
                    Preconditions.checkState(Camera2CameraImpl.this.isSessionCloseComplete());
                    Camera2CameraImpl.this.finishClose();
                    return;
                case REOPENING:
                    if (Camera2CameraImpl.this.mCameraDeviceError != 0) {
                        Camera2CameraImpl.this.debugLog("Camera closed due to error: " + Camera2CameraImpl.getErrorMessage(Camera2CameraImpl.this.mCameraDeviceError));
                        scheduleCameraReopen();
                        return;
                    }
                    Camera2CameraImpl.this.tryOpenCameraDevice(false);
                    return;
                default:
                    throw new IllegalStateException("Camera closed while in state: " + Camera2CameraImpl.this.mState);
            }
        }

        public void onDisconnected(CameraDevice cameraDevice) {
            Camera2CameraImpl.this.debugLog("CameraDevice.onDisconnected()");
            onError(cameraDevice, 1);
        }

        public void onError(CameraDevice cameraDevice, int error) {
            Camera2CameraImpl.this.mCameraDevice = cameraDevice;
            Camera2CameraImpl.this.mCameraDeviceError = error;
            switch (Camera2CameraImpl.this.mState) {
                case CLOSING:
                case RELEASING:
                    Logger.e(Camera2CameraImpl.TAG, String.format("CameraDevice.onError(): %s failed with %s while in %s state. Will finish closing camera.", new Object[]{cameraDevice.getId(), Camera2CameraImpl.getErrorMessage(error), Camera2CameraImpl.this.mState.name()}));
                    Camera2CameraImpl.this.closeCamera(false);
                    return;
                case OPENED:
                case OPENING:
                case REOPENING:
                    Logger.d(Camera2CameraImpl.TAG, String.format("CameraDevice.onError(): %s failed with %s while in %s state. Will attempt recovering from error.", new Object[]{cameraDevice.getId(), Camera2CameraImpl.getErrorMessage(error), Camera2CameraImpl.this.mState.name()}));
                    handleErrorOnOpen(cameraDevice, error);
                    return;
                default:
                    throw new IllegalStateException("onError() should not be possible from state: " + Camera2CameraImpl.this.mState);
            }
        }

        private void handleErrorOnOpen(CameraDevice cameraDevice, int error) {
            int publicErrorCode;
            Preconditions.checkState(Camera2CameraImpl.this.mState == InternalState.OPENING || Camera2CameraImpl.this.mState == InternalState.OPENED || Camera2CameraImpl.this.mState == InternalState.REOPENING, "Attempt to handle open error from non open state: " + Camera2CameraImpl.this.mState);
            switch (error) {
                case 1:
                case 2:
                case 4:
                    Logger.d(Camera2CameraImpl.TAG, String.format("Attempt to reopen camera[%s] after error[%s]", new Object[]{cameraDevice.getId(), Camera2CameraImpl.getErrorMessage(error)}));
                    reopenCameraAfterError(error);
                    return;
                default:
                    Logger.e(Camera2CameraImpl.TAG, "Error observed on open (or opening) camera device " + cameraDevice.getId() + ": " + Camera2CameraImpl.getErrorMessage(error) + " closing camera.");
                    if (error == 3) {
                        publicErrorCode = 5;
                    } else {
                        publicErrorCode = 6;
                    }
                    Camera2CameraImpl.this.setState(InternalState.CLOSING, CameraState.StateError.create(publicErrorCode));
                    Camera2CameraImpl.this.closeCamera(false);
                    return;
            }
        }

        private void reopenCameraAfterError(int error) {
            int publicErrorCode;
            Preconditions.checkState(Camera2CameraImpl.this.mCameraDeviceError != 0, "Can only reopen camera device after error if the camera device is actually in an error state.");
            switch (error) {
                case 1:
                    publicErrorCode = 2;
                    break;
                case 2:
                    publicErrorCode = 1;
                    break;
                default:
                    publicErrorCode = 3;
                    break;
            }
            Camera2CameraImpl.this.setState(InternalState.REOPENING, CameraState.StateError.create(publicErrorCode));
            Camera2CameraImpl.this.closeCamera(false);
        }

        /* access modifiers changed from: package-private */
        public void scheduleCameraReopen() {
            boolean z = true;
            Preconditions.checkState(this.mScheduledReopenRunnable == null);
            if (this.mScheduledReopenHandle != null) {
                z = false;
            }
            Preconditions.checkState(z);
            if (this.mCameraReopenMonitor.canScheduleCameraReopen()) {
                this.mScheduledReopenRunnable = new ScheduledReopen(this.mExecutor);
                Camera2CameraImpl.this.debugLog("Attempting camera re-open in " + this.mCameraReopenMonitor.getReopenDelayMs() + "ms: " + this.mScheduledReopenRunnable + " activeResuming = " + Camera2CameraImpl.this.mIsActiveResumingMode);
                this.mScheduledReopenHandle = this.mScheduler.schedule(this.mScheduledReopenRunnable, (long) this.mCameraReopenMonitor.getReopenDelayMs(), TimeUnit.MILLISECONDS);
                return;
            }
            Logger.e(Camera2CameraImpl.TAG, "Camera reopening attempted for " + this.mCameraReopenMonitor.getReopenLimitMs() + "ms without success.");
            Camera2CameraImpl.this.setState(InternalState.PENDING_OPEN, (CameraState.StateError) null, false);
        }

        /* access modifiers changed from: package-private */
        public boolean cancelScheduledReopen() {
            if (this.mScheduledReopenHandle == null) {
                return false;
            }
            Camera2CameraImpl.this.debugLog("Cancelling scheduled re-open: " + this.mScheduledReopenRunnable);
            this.mScheduledReopenRunnable.cancel();
            this.mScheduledReopenRunnable = null;
            this.mScheduledReopenHandle.cancel(false);
            this.mScheduledReopenHandle = null;
            return true;
        }

        /* access modifiers changed from: package-private */
        public void resetReopenMonitor() {
            this.mCameraReopenMonitor.reset();
        }

        class ScheduledReopen implements Runnable {
            private boolean mCancelled = false;
            private Executor mExecutor;

            ScheduledReopen(Executor executor) {
                this.mExecutor = executor;
            }

            /* access modifiers changed from: package-private */
            public void cancel() {
                this.mCancelled = true;
            }

            public void run() {
                this.mExecutor.execute(new Camera2CameraImpl$StateCallback$ScheduledReopen$$ExternalSyntheticLambda0(this));
            }

            /* access modifiers changed from: package-private */
            /* renamed from: lambda$run$0$androidx-camera-camera2-internal-Camera2CameraImpl$StateCallback$ScheduledReopen  reason: not valid java name */
            public /* synthetic */ void m48lambda$run$0$androidxcameracamera2internalCamera2CameraImpl$StateCallback$ScheduledReopen() {
                if (!this.mCancelled) {
                    Preconditions.checkState(Camera2CameraImpl.this.mState == InternalState.REOPENING);
                    if (StateCallback.this.shouldActiveResume()) {
                        Camera2CameraImpl.this.tryForceOpenCameraDevice(true);
                    } else {
                        Camera2CameraImpl.this.tryOpenCameraDevice(true);
                    }
                }
            }
        }

        /* access modifiers changed from: package-private */
        public boolean shouldActiveResume() {
            return Camera2CameraImpl.this.mIsActiveResumingMode && (Camera2CameraImpl.this.mCameraDeviceError == 1 || Camera2CameraImpl.this.mCameraDeviceError == 2);
        }

        class CameraReopenMonitor {
            static final int ACTIVE_REOPEN_DELAY_BASE_MS = 1000;
            static final int ACTIVE_REOPEN_LIMIT_MS = 1800000;
            static final int INVALID_TIME = -1;
            static final int REOPEN_DELAY_MS = 700;
            static final int REOPEN_LIMIT_MS = 10000;
            private long mFirstReopenTime = -1;

            CameraReopenMonitor() {
            }

            /* access modifiers changed from: package-private */
            public int getReopenDelayMs() {
                if (!StateCallback.this.shouldActiveResume()) {
                    return 700;
                }
                long elapsedTime = getElapsedTime();
                if (elapsedTime <= 120000) {
                    return 1000;
                }
                if (elapsedTime <= 300000) {
                    return 2000;
                }
                return 4000;
            }

            /* access modifiers changed from: package-private */
            public int getReopenLimitMs() {
                if (!StateCallback.this.shouldActiveResume()) {
                    return REOPEN_LIMIT_MS;
                }
                return ACTIVE_REOPEN_LIMIT_MS;
            }

            /* access modifiers changed from: package-private */
            public long getElapsedTime() {
                long now = SystemClock.uptimeMillis();
                if (this.mFirstReopenTime == -1) {
                    this.mFirstReopenTime = now;
                }
                return now - this.mFirstReopenTime;
            }

            /* access modifiers changed from: package-private */
            public boolean canScheduleCameraReopen() {
                if (!(getElapsedTime() >= ((long) getReopenLimitMs()))) {
                    return true;
                }
                reset();
                return false;
            }

            /* access modifiers changed from: package-private */
            public void reset() {
                this.mFirstReopenTime = -1;
            }
        }
    }

    final class CameraAvailability extends CameraManager.AvailabilityCallback implements CameraStateRegistry.OnOpenAvailableListener {
        private boolean mCameraAvailable = true;
        private final String mCameraId;

        CameraAvailability(String cameraId) {
            this.mCameraId = cameraId;
        }

        public void onCameraAvailable(String cameraId) {
            if (this.mCameraId.equals(cameraId)) {
                this.mCameraAvailable = true;
                if (Camera2CameraImpl.this.mState == InternalState.PENDING_OPEN) {
                    Camera2CameraImpl.this.tryOpenCameraDevice(false);
                }
            }
        }

        public void onCameraUnavailable(String cameraId) {
            if (this.mCameraId.equals(cameraId)) {
                this.mCameraAvailable = false;
            }
        }

        public void onOpenAvailable() {
            if (Camera2CameraImpl.this.mState == InternalState.PENDING_OPEN) {
                Camera2CameraImpl.this.tryOpenCameraDevice(false);
            }
        }

        /* access modifiers changed from: package-private */
        public boolean isCameraAvailable() {
            return this.mCameraAvailable;
        }
    }

    final class ControlUpdateListenerInternal implements CameraControlInternal.ControlUpdateCallback {
        ControlUpdateListenerInternal() {
        }

        public void onCameraControlUpdateSessionConfig() {
            Camera2CameraImpl.this.updateCaptureSessionConfig();
        }

        public void onCameraControlCaptureRequests(List<CaptureConfig> captureConfigs) {
            Camera2CameraImpl.this.submitCaptureRequests((List) Preconditions.checkNotNull(captureConfigs));
        }
    }
}

package androidx.camera.camera2.internal;

import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.util.Size;
import android.view.Surface;
import androidx.camera.camera2.impl.Camera2ImplConfig;
import androidx.camera.camera2.interop.CaptureRequestOptions;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Logger;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.CameraCaptureCallback;
import androidx.camera.core.impl.CameraCaptureFailure;
import androidx.camera.core.impl.CameraCaptureResult;
import androidx.camera.core.impl.CaptureConfig;
import androidx.camera.core.impl.DeferrableSurface;
import androidx.camera.core.impl.DeferrableSurfaces;
import androidx.camera.core.impl.OutputSurface;
import androidx.camera.core.impl.SessionConfig;
import androidx.camera.core.impl.SessionProcessor;
import androidx.camera.core.impl.SessionProcessorSurface;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.impl.utils.futures.FutureCallback;
import androidx.camera.core.impl.utils.futures.FutureChain;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.core.util.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;

final class ProcessingCaptureSession implements CaptureSessionInterface {
    private static final String TAG = "ProcessingCaptureSession";
    private static final long TIMEOUT_GET_SURFACE_IN_MS = 5000;
    private static List<DeferrableSurface> sHeldProcessorSurfaces = new ArrayList();
    private static int sNextInstanceId = 0;
    private final Camera2CameraInfoImpl mCamera2CameraInfoImpl;
    private final CaptureSession mCaptureSession = new CaptureSession();
    final Executor mExecutor;
    private int mInstanceId = 0;
    volatile boolean mIsExecutingStillCaptureRequest = false;
    private List<DeferrableSurface> mOutputSurfaces = new ArrayList();
    private volatile CaptureConfig mPendingCaptureConfig = null;
    private SessionConfig mProcessorSessionConfig;
    private ProcessorState mProcessorState;
    private Camera2RequestProcessor mRequestProcessor;
    private final ScheduledExecutorService mScheduledExecutorService;
    private SessionConfig mSessionConfig;
    private CaptureRequestOptions mSessionOptions = new CaptureRequestOptions.Builder().build();
    private final SessionProcessor mSessionProcessor;
    private final SessionProcessorCaptureCallback mSessionProcessorCaptureCallback;
    private CaptureRequestOptions mStillCaptureOptions = new CaptureRequestOptions.Builder().build();

    private enum ProcessorState {
        UNINITIALIZED,
        SESSION_INITIALIZED,
        ON_CAPTURE_SESSION_STARTED,
        ON_CAPTURE_SESSION_ENDED,
        CLOSED
    }

    ProcessingCaptureSession(SessionProcessor sessionProcessor, Camera2CameraInfoImpl camera2CameraInfoImpl, Executor executor, ScheduledExecutorService scheduledExecutorService) {
        this.mSessionProcessor = sessionProcessor;
        this.mCamera2CameraInfoImpl = camera2CameraInfoImpl;
        this.mExecutor = executor;
        this.mScheduledExecutorService = scheduledExecutorService;
        this.mProcessorState = ProcessorState.UNINITIALIZED;
        this.mSessionProcessorCaptureCallback = new SessionProcessorCaptureCallback(executor);
        int i = sNextInstanceId;
        sNextInstanceId = i + 1;
        this.mInstanceId = i;
        Logger.d(TAG, "New ProcessingCaptureSession (id=" + this.mInstanceId + ")");
    }

    public ListenableFuture<Void> open(SessionConfig sessionConfig, CameraDevice cameraDevice, SynchronizedCaptureSessionOpener opener) {
        Preconditions.checkArgument(this.mProcessorState == ProcessorState.UNINITIALIZED, "Invalid state state:" + this.mProcessorState);
        Preconditions.checkArgument(!sessionConfig.getSurfaces().isEmpty(), "SessionConfig contains no surfaces");
        Logger.d(TAG, "open (id=" + this.mInstanceId + ")");
        List<DeferrableSurface> surfaces = sessionConfig.getSurfaces();
        this.mOutputSurfaces = surfaces;
        return FutureChain.from(DeferrableSurfaces.surfaceListWithTimeout(surfaces, false, TIMEOUT_GET_SURFACE_IN_MS, this.mExecutor, this.mScheduledExecutorService)).transformAsync(new ProcessingCaptureSession$$ExternalSyntheticLambda1(this, sessionConfig, cameraDevice, opener), this.mExecutor).transform(new ProcessingCaptureSession$$ExternalSyntheticLambda0(this), this.mExecutor);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$open$2$androidx-camera-camera2-internal-ProcessingCaptureSession  reason: not valid java name */
    public /* synthetic */ ListenableFuture m74lambda$open$2$androidxcameracamera2internalProcessingCaptureSession(SessionConfig sessionConfig, CameraDevice cameraDevice, SynchronizedCaptureSessionOpener opener, List surfaceList) throws Exception {
        Logger.d(TAG, "-- getSurfaces done, start init (id=" + this.mInstanceId + ")");
        if (this.mProcessorState == ProcessorState.CLOSED) {
            return Futures.immediateFailedFuture(new IllegalStateException("SessionProcessorCaptureSession is closed."));
        }
        if (surfaceList.contains((Object) null)) {
            return Futures.immediateFailedFuture(new DeferrableSurface.SurfaceClosedException("Surface closed", sessionConfig.getSurfaces().get(surfaceList.indexOf((Object) null))));
        }
        try {
            DeferrableSurfaces.incrementAll(this.mOutputSurfaces);
            OutputSurface previewOutputSurface = null;
            OutputSurface captureOutputSurface = null;
            OutputSurface analysisOutputSurface = null;
            for (int i = 0; i < sessionConfig.getSurfaces().size(); i++) {
                DeferrableSurface dSurface = sessionConfig.getSurfaces().get(i);
                if (Objects.equals(dSurface.getContainerClass(), Preview.class)) {
                    previewOutputSurface = OutputSurface.create((Surface) dSurface.getSurface().get(), new Size(dSurface.getPrescribedSize().getWidth(), dSurface.getPrescribedSize().getHeight()), dSurface.getPrescribedStreamFormat());
                } else if (Objects.equals(dSurface.getContainerClass(), ImageCapture.class)) {
                    captureOutputSurface = OutputSurface.create((Surface) dSurface.getSurface().get(), new Size(dSurface.getPrescribedSize().getWidth(), dSurface.getPrescribedSize().getHeight()), dSurface.getPrescribedStreamFormat());
                } else if (Objects.equals(dSurface.getContainerClass(), ImageAnalysis.class)) {
                    analysisOutputSurface = OutputSurface.create((Surface) dSurface.getSurface().get(), new Size(dSurface.getPrescribedSize().getWidth(), dSurface.getPrescribedSize().getHeight()), dSurface.getPrescribedStreamFormat());
                }
            }
            this.mProcessorState = ProcessorState.SESSION_INITIALIZED;
            Logger.w(TAG, "== initSession (id=" + this.mInstanceId + ")");
            SessionConfig initSession = this.mSessionProcessor.initSession(this.mCamera2CameraInfoImpl, previewOutputSurface, captureOutputSurface, analysisOutputSurface);
            this.mProcessorSessionConfig = initSession;
            initSession.getSurfaces().get(0).getTerminationFuture().addListener(new ProcessingCaptureSession$$ExternalSyntheticLambda2(this), CameraXExecutors.directExecutor());
            for (DeferrableSurface surface : this.mProcessorSessionConfig.getSurfaces()) {
                sHeldProcessorSurfaces.add(surface);
                surface.getTerminationFuture().addListener(new ProcessingCaptureSession$$ExternalSyntheticLambda3(surface), this.mExecutor);
            }
            SessionConfig.ValidatingBuilder validatingBuilder = new SessionConfig.ValidatingBuilder();
            validatingBuilder.add(sessionConfig);
            validatingBuilder.clearSurfaces();
            validatingBuilder.add(this.mProcessorSessionConfig);
            Preconditions.checkArgument(validatingBuilder.isValid(), "Cannot transform the SessionConfig");
            ListenableFuture<Void> openSessionFuture = this.mCaptureSession.open(validatingBuilder.build(), (CameraDevice) Preconditions.checkNotNull(cameraDevice), opener);
            Futures.addCallback(openSessionFuture, new FutureCallback<Void>() {
                public void onSuccess(Void result) {
                }

                public void onFailure(Throwable t) {
                    Logger.e(ProcessingCaptureSession.TAG, "open session failed ", t);
                    ProcessingCaptureSession.this.close();
                }
            }, this.mExecutor);
            return openSessionFuture;
        } catch (DeferrableSurface.SurfaceClosedException e) {
            return Futures.immediateFailedFuture(e);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$open$0$androidx-camera-camera2-internal-ProcessingCaptureSession  reason: not valid java name */
    public /* synthetic */ void m73lambda$open$0$androidxcameracamera2internalProcessingCaptureSession() {
        DeferrableSurfaces.decrementAll(this.mOutputSurfaces);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$open$3$androidx-camera-camera2-internal-ProcessingCaptureSession  reason: not valid java name */
    public /* synthetic */ Void m75lambda$open$3$androidxcameracamera2internalProcessingCaptureSession(Void v) {
        onConfigured(this.mCaptureSession);
        return null;
    }

    private static void cancelRequests(List<CaptureConfig> captureConfigs) {
        for (CaptureConfig captureConfig : captureConfigs) {
            for (CameraCaptureCallback cameraCaptureCallback : captureConfig.getCameraCaptureCallbacks()) {
                cameraCaptureCallback.onCaptureCancelled();
            }
        }
    }

    private boolean isStillCapture(List<CaptureConfig> captureConfigs) {
        if (captureConfigs.isEmpty()) {
            return false;
        }
        for (CaptureConfig captureConfig : captureConfigs) {
            if (captureConfig.getTemplateType() != 2) {
                return false;
            }
        }
        return true;
    }

    public void issueCaptureRequests(List<CaptureConfig> captureConfigs) {
        if (!captureConfigs.isEmpty()) {
            if (captureConfigs.size() > 1 || !isStillCapture(captureConfigs)) {
                cancelRequests(captureConfigs);
            } else if (this.mPendingCaptureConfig != null || this.mIsExecutingStillCaptureRequest) {
                cancelRequests(captureConfigs);
            } else {
                final CaptureConfig captureConfig = captureConfigs.get(0);
                Logger.d(TAG, "issueCaptureRequests (id=" + this.mInstanceId + ") + state =" + this.mProcessorState);
                switch (AnonymousClass3.$SwitchMap$androidx$camera$camera2$internal$ProcessingCaptureSession$ProcessorState[this.mProcessorState.ordinal()]) {
                    case 1:
                    case 2:
                        this.mPendingCaptureConfig = captureConfig;
                        return;
                    case 3:
                        this.mIsExecutingStillCaptureRequest = true;
                        CaptureRequestOptions.Builder builder = CaptureRequestOptions.Builder.from(captureConfig.getImplementationOptions());
                        if (captureConfig.getImplementationOptions().containsOption(CaptureConfig.OPTION_ROTATION)) {
                            builder.setCaptureRequestOption(CaptureRequest.JPEG_ORIENTATION, (Integer) captureConfig.getImplementationOptions().retrieveOption(CaptureConfig.OPTION_ROTATION));
                        }
                        if (captureConfig.getImplementationOptions().containsOption(CaptureConfig.OPTION_JPEG_QUALITY)) {
                            builder.setCaptureRequestOption(CaptureRequest.JPEG_QUALITY, Byte.valueOf(((Integer) captureConfig.getImplementationOptions().retrieveOption(CaptureConfig.OPTION_JPEG_QUALITY)).byteValue()));
                        }
                        CaptureRequestOptions build = builder.build();
                        this.mStillCaptureOptions = build;
                        updateParameters(this.mSessionOptions, build);
                        this.mSessionProcessor.startCapture(new SessionProcessor.CaptureCallback() {
                            public void onCaptureStarted(int captureSequenceId, long timestamp) {
                            }

                            public void onCaptureProcessStarted(int captureSequenceId) {
                            }

                            public void onCaptureFailed(int captureSequenceId) {
                                ProcessingCaptureSession.this.mExecutor.execute(new ProcessingCaptureSession$2$$ExternalSyntheticLambda0(this, captureConfig));
                            }

                            /* access modifiers changed from: package-private */
                            /* renamed from: lambda$onCaptureFailed$0$androidx-camera-camera2-internal-ProcessingCaptureSession$2  reason: not valid java name */
                            public /* synthetic */ void m76lambda$onCaptureFailed$0$androidxcameracamera2internalProcessingCaptureSession$2(CaptureConfig captureConfig) {
                                for (CameraCaptureCallback cameraCaptureCallback : captureConfig.getCameraCaptureCallbacks()) {
                                    cameraCaptureCallback.onCaptureFailed(new CameraCaptureFailure(CameraCaptureFailure.Reason.ERROR));
                                }
                                ProcessingCaptureSession.this.mIsExecutingStillCaptureRequest = false;
                            }

                            public void onCaptureSequenceCompleted(int captureSequenceId) {
                                ProcessingCaptureSession.this.mExecutor.execute(new ProcessingCaptureSession$2$$ExternalSyntheticLambda1(this, captureConfig));
                            }

                            /* access modifiers changed from: package-private */
                            /* renamed from: lambda$onCaptureSequenceCompleted$1$androidx-camera-camera2-internal-ProcessingCaptureSession$2  reason: not valid java name */
                            public /* synthetic */ void m77lambda$onCaptureSequenceCompleted$1$androidxcameracamera2internalProcessingCaptureSession$2(CaptureConfig captureConfig) {
                                for (CameraCaptureCallback cameraCaptureCallback : captureConfig.getCameraCaptureCallbacks()) {
                                    cameraCaptureCallback.onCaptureCompleted(new CameraCaptureResult.EmptyCameraCaptureResult());
                                }
                                ProcessingCaptureSession.this.mIsExecutingStillCaptureRequest = false;
                            }

                            public void onCaptureSequenceAborted(int captureSequenceId) {
                            }

                            public void onCaptureCompleted(long timestamp, int captureSequenceId, Map<CaptureResult.Key, Object> map) {
                            }
                        });
                        return;
                    case 4:
                    case 5:
                        Logger.d(TAG, "Run issueCaptureRequests in wrong state, state = " + this.mProcessorState);
                        cancelRequests(captureConfigs);
                        return;
                    default:
                        return;
                }
            }
        }
    }

    /* renamed from: androidx.camera.camera2.internal.ProcessingCaptureSession$3  reason: invalid class name */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$androidx$camera$camera2$internal$ProcessingCaptureSession$ProcessorState;

        static {
            int[] iArr = new int[ProcessorState.values().length];
            $SwitchMap$androidx$camera$camera2$internal$ProcessingCaptureSession$ProcessorState = iArr;
            try {
                iArr[ProcessorState.UNINITIALIZED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$androidx$camera$camera2$internal$ProcessingCaptureSession$ProcessorState[ProcessorState.SESSION_INITIALIZED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$androidx$camera$camera2$internal$ProcessingCaptureSession$ProcessorState[ProcessorState.ON_CAPTURE_SESSION_STARTED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$androidx$camera$camera2$internal$ProcessingCaptureSession$ProcessorState[ProcessorState.ON_CAPTURE_SESSION_ENDED.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$androidx$camera$camera2$internal$ProcessingCaptureSession$ProcessorState[ProcessorState.CLOSED.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public ListenableFuture<Void> release(boolean abortInFlightCaptures) {
        Preconditions.checkState(this.mProcessorState == ProcessorState.CLOSED, "release() can only be called in CLOSED state");
        Logger.d(TAG, "release (id=" + this.mInstanceId + ")");
        return this.mCaptureSession.release(abortInFlightCaptures);
    }

    private static List<SessionProcessorSurface> getSessionProcessorSurfaceList(List<DeferrableSurface> deferrableSurfaceList) {
        ArrayList<SessionProcessorSurface> outputSurfaceList = new ArrayList<>();
        for (DeferrableSurface deferrableSurface : deferrableSurfaceList) {
            Preconditions.checkArgument(deferrableSurface instanceof SessionProcessorSurface, "Surface must be SessionProcessorSurface");
            outputSurfaceList.add((SessionProcessorSurface) deferrableSurface);
        }
        return outputSurfaceList;
    }

    /* access modifiers changed from: package-private */
    public void onConfigured(CaptureSession captureSession) {
        Preconditions.checkArgument(this.mProcessorState == ProcessorState.SESSION_INITIALIZED, "Invalid state state:" + this.mProcessorState);
        Camera2RequestProcessor camera2RequestProcessor = new Camera2RequestProcessor(captureSession, getSessionProcessorSurfaceList(this.mProcessorSessionConfig.getSurfaces()));
        this.mRequestProcessor = camera2RequestProcessor;
        this.mSessionProcessor.onCaptureSessionStart(camera2RequestProcessor);
        this.mProcessorState = ProcessorState.ON_CAPTURE_SESSION_STARTED;
        SessionConfig sessionConfig = this.mSessionConfig;
        if (sessionConfig != null) {
            setSessionConfig(sessionConfig);
        }
        if (this.mPendingCaptureConfig != null) {
            List<CaptureConfig> pendingCaptureConfigList = Arrays.asList(new CaptureConfig[]{this.mPendingCaptureConfig});
            this.mPendingCaptureConfig = null;
            issueCaptureRequests(pendingCaptureConfigList);
        }
    }

    public SessionConfig getSessionConfig() {
        return this.mSessionConfig;
    }

    public List<CaptureConfig> getCaptureConfigs() {
        if (this.mPendingCaptureConfig == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(new CaptureConfig[]{this.mPendingCaptureConfig});
    }

    public void cancelIssuedCaptureRequests() {
        Logger.d(TAG, "cancelIssuedCaptureRequests (id=" + this.mInstanceId + ")");
        if (this.mPendingCaptureConfig != null) {
            for (CameraCaptureCallback cameraCaptureCallback : this.mPendingCaptureConfig.getCameraCaptureCallbacks()) {
                cameraCaptureCallback.onCaptureCancelled();
            }
            this.mPendingCaptureConfig = null;
        }
    }

    public void close() {
        Logger.d(TAG, "close (id=" + this.mInstanceId + ") state=" + this.mProcessorState);
        switch (AnonymousClass3.$SwitchMap$androidx$camera$camera2$internal$ProcessingCaptureSession$ProcessorState[this.mProcessorState.ordinal()]) {
            case 2:
            case 4:
                break;
            case 3:
                this.mSessionProcessor.onCaptureSessionEnd();
                Camera2RequestProcessor camera2RequestProcessor = this.mRequestProcessor;
                if (camera2RequestProcessor != null) {
                    camera2RequestProcessor.close();
                }
                this.mProcessorState = ProcessorState.ON_CAPTURE_SESSION_ENDED;
                break;
            case 5:
                return;
        }
        this.mSessionProcessor.deInitSession();
        this.mProcessorState = ProcessorState.CLOSED;
        this.mCaptureSession.close();
    }

    public void setSessionConfig(SessionConfig sessionConfig) {
        Logger.d(TAG, "setSessionConfig (id=" + this.mInstanceId + ")");
        this.mSessionConfig = sessionConfig;
        if (sessionConfig != null) {
            Camera2RequestProcessor camera2RequestProcessor = this.mRequestProcessor;
            if (camera2RequestProcessor != null) {
                camera2RequestProcessor.updateSessionConfig(sessionConfig);
            }
            if (this.mProcessorState == ProcessorState.ON_CAPTURE_SESSION_STARTED) {
                CaptureRequestOptions build = CaptureRequestOptions.Builder.from(sessionConfig.getImplementationOptions()).build();
                this.mSessionOptions = build;
                updateParameters(build, this.mStillCaptureOptions);
                this.mSessionProcessor.startRepeating(this.mSessionProcessorCaptureCallback);
            }
        }
    }

    private void updateParameters(CaptureRequestOptions sessionOptions, CaptureRequestOptions stillCaptureOptions) {
        Camera2ImplConfig.Builder builder = new Camera2ImplConfig.Builder();
        builder.insertAllOptions(sessionOptions);
        builder.insertAllOptions(stillCaptureOptions);
        this.mSessionProcessor.setParameters(builder.build());
    }

    private static class SessionProcessorCaptureCallback implements SessionProcessor.CaptureCallback {
        private List<CameraCaptureCallback> mCameraCaptureCallbacks = Collections.emptyList();
        private final Executor mExecutor;

        SessionProcessorCaptureCallback(Executor executor) {
            this.mExecutor = executor;
        }

        public void setCameraCaptureCallbacks(List<CameraCaptureCallback> cameraCaptureCallbacks) {
            this.mCameraCaptureCallbacks = cameraCaptureCallbacks;
        }

        public void onCaptureStarted(int captureSequenceId, long timestamp) {
        }

        public void onCaptureProcessStarted(int captureSequenceId) {
        }

        public void onCaptureFailed(int captureSequenceId) {
            this.mExecutor.execute(new ProcessingCaptureSession$SessionProcessorCaptureCallback$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onCaptureFailed$0$androidx-camera-camera2-internal-ProcessingCaptureSession$SessionProcessorCaptureCallback  reason: not valid java name */
        public /* synthetic */ void m78lambda$onCaptureFailed$0$androidxcameracamera2internalProcessingCaptureSession$SessionProcessorCaptureCallback() {
            for (CameraCaptureCallback cameraCaptureCallback : this.mCameraCaptureCallbacks) {
                cameraCaptureCallback.onCaptureFailed(new CameraCaptureFailure(CameraCaptureFailure.Reason.ERROR));
            }
        }

        public void onCaptureSequenceCompleted(int captureSequenceId) {
            this.mExecutor.execute(new ProcessingCaptureSession$SessionProcessorCaptureCallback$$ExternalSyntheticLambda1(this));
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onCaptureSequenceCompleted$1$androidx-camera-camera2-internal-ProcessingCaptureSession$SessionProcessorCaptureCallback  reason: not valid java name */
        public /* synthetic */ void m79lambda$onCaptureSequenceCompleted$1$androidxcameracamera2internalProcessingCaptureSession$SessionProcessorCaptureCallback() {
            for (CameraCaptureCallback cameraCaptureCallback : this.mCameraCaptureCallbacks) {
                cameraCaptureCallback.onCaptureCompleted(CameraCaptureResult.EmptyCameraCaptureResult.create());
            }
        }

        public void onCaptureSequenceAborted(int captureSequenceId) {
        }

        public void onCaptureCompleted(long timestamp, int captureSequenceId, Map<CaptureResult.Key, Object> map) {
        }
    }
}

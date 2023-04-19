package androidx.camera.camera2.internal;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.view.Surface;
import androidx.camera.camera2.impl.Camera2ImplConfig;
import androidx.camera.camera2.impl.CameraEventCallbacks;
import androidx.camera.camera2.internal.SynchronizedCaptureSession;
import androidx.camera.camera2.internal.SynchronizedCaptureSessionStateCallbacks;
import androidx.camera.camera2.internal.compat.params.InputConfigurationCompat;
import androidx.camera.camera2.internal.compat.params.OutputConfigurationCompat;
import androidx.camera.camera2.internal.compat.params.SessionConfigurationCompat;
import androidx.camera.camera2.internal.compat.workaround.StillCaptureFlow;
import androidx.camera.camera2.internal.compat.workaround.TorchStateReset;
import androidx.camera.core.Logger;
import androidx.camera.core.impl.CameraCaptureCallback;
import androidx.camera.core.impl.CaptureConfig;
import androidx.camera.core.impl.Config;
import androidx.camera.core.impl.DeferrableSurface;
import androidx.camera.core.impl.MutableOptionsBundle;
import androidx.camera.core.impl.OptionsBundle;
import androidx.camera.core.impl.SessionConfig;
import androidx.camera.core.impl.utils.futures.FutureCallback;
import androidx.camera.core.impl.utils.futures.FutureChain;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.util.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CancellationException;

final class CaptureSession implements CaptureSessionInterface {
    private static final String TAG = "CaptureSession";
    private static final long TIMEOUT_GET_SURFACE_IN_MS = 5000;
    CameraEventCallbacks mCameraEventCallbacks;
    Config mCameraEventOnRepeatingOptions;
    private final CameraCaptureSession.CaptureCallback mCaptureCallback;
    private final List<CaptureConfig> mCaptureConfigs;
    private final StateCallback mCaptureSessionStateCallback;
    List<DeferrableSurface> mConfiguredDeferrableSurfaces;
    private final Map<DeferrableSurface, Surface> mConfiguredSurfaceMap;
    CallbackToFutureAdapter.Completer<Void> mReleaseCompleter;
    ListenableFuture<Void> mReleaseFuture;
    SessionConfig mSessionConfig;
    final Object mSessionLock;
    State mState;
    final StillCaptureFlow mStillCaptureFlow;
    SynchronizedCaptureSession mSynchronizedCaptureSession;
    SynchronizedCaptureSessionOpener mSynchronizedCaptureSessionOpener;
    final TorchStateReset mTorchStateReset;

    enum State {
        UNINITIALIZED,
        INITIALIZED,
        GET_SURFACE,
        OPENING,
        OPENED,
        CLOSED,
        RELEASING,
        RELEASED
    }

    CaptureSession() {
        this.mSessionLock = new Object();
        this.mCaptureConfigs = new ArrayList();
        this.mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
            public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            }
        };
        this.mCameraEventOnRepeatingOptions = OptionsBundle.emptyBundle();
        this.mCameraEventCallbacks = CameraEventCallbacks.createEmptyCallback();
        this.mConfiguredSurfaceMap = new HashMap();
        this.mConfiguredDeferrableSurfaces = Collections.emptyList();
        this.mState = State.UNINITIALIZED;
        this.mStillCaptureFlow = new StillCaptureFlow();
        this.mTorchStateReset = new TorchStateReset();
        this.mState = State.INITIALIZED;
        this.mCaptureSessionStateCallback = new StateCallback();
    }

    public SessionConfig getSessionConfig() {
        SessionConfig sessionConfig;
        synchronized (this.mSessionLock) {
            sessionConfig = this.mSessionConfig;
        }
        return sessionConfig;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0065, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setSessionConfig(androidx.camera.core.impl.SessionConfig r5) {
        /*
            r4 = this;
            java.lang.Object r0 = r4.mSessionLock
            monitor-enter(r0)
            int[] r1 = androidx.camera.camera2.internal.CaptureSession.AnonymousClass4.$SwitchMap$androidx$camera$camera2$internal$CaptureSession$State     // Catch:{ all -> 0x0066 }
            androidx.camera.camera2.internal.CaptureSession$State r2 = r4.mState     // Catch:{ all -> 0x0066 }
            int r2 = r2.ordinal()     // Catch:{ all -> 0x0066 }
            r1 = r1[r2]     // Catch:{ all -> 0x0066 }
            switch(r1) {
                case 1: goto L_0x0048;
                case 2: goto L_0x0045;
                case 3: goto L_0x0045;
                case 4: goto L_0x0045;
                case 5: goto L_0x0019;
                case 6: goto L_0x0011;
                case 7: goto L_0x0011;
                case 8: goto L_0x0011;
                default: goto L_0x0010;
            }     // Catch:{ all -> 0x0066 }
        L_0x0010:
            goto L_0x0064
        L_0x0011:
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException     // Catch:{ all -> 0x0066 }
            java.lang.String r2 = "Session configuration cannot be set on a closed/released session."
            r1.<init>(r2)     // Catch:{ all -> 0x0066 }
            throw r1     // Catch:{ all -> 0x0066 }
        L_0x0019:
            r4.mSessionConfig = r5     // Catch:{ all -> 0x0066 }
            if (r5 != 0) goto L_0x001f
            monitor-exit(r0)     // Catch:{ all -> 0x0066 }
            return
        L_0x001f:
            java.util.Map<androidx.camera.core.impl.DeferrableSurface, android.view.Surface> r1 = r4.mConfiguredSurfaceMap     // Catch:{ all -> 0x0066 }
            java.util.Set r1 = r1.keySet()     // Catch:{ all -> 0x0066 }
            java.util.List r2 = r5.getSurfaces()     // Catch:{ all -> 0x0066 }
            boolean r1 = r1.containsAll(r2)     // Catch:{ all -> 0x0066 }
            if (r1 != 0) goto L_0x0038
            java.lang.String r1 = "CaptureSession"
            java.lang.String r2 = "Does not have the proper configured lists"
            androidx.camera.core.Logger.e(r1, r2)     // Catch:{ all -> 0x0066 }
            monitor-exit(r0)     // Catch:{ all -> 0x0066 }
            return
        L_0x0038:
            java.lang.String r1 = "CaptureSession"
            java.lang.String r2 = "Attempting to submit CaptureRequest after setting"
            androidx.camera.core.Logger.d(r1, r2)     // Catch:{ all -> 0x0066 }
            androidx.camera.core.impl.SessionConfig r1 = r4.mSessionConfig     // Catch:{ all -> 0x0066 }
            r4.issueRepeatingCaptureRequests(r1)     // Catch:{ all -> 0x0066 }
            goto L_0x0064
        L_0x0045:
            r4.mSessionConfig = r5     // Catch:{ all -> 0x0066 }
            goto L_0x0064
        L_0x0048:
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException     // Catch:{ all -> 0x0066 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0066 }
            r2.<init>()     // Catch:{ all -> 0x0066 }
            java.lang.String r3 = "setSessionConfig() should not be possible in state: "
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ all -> 0x0066 }
            androidx.camera.camera2.internal.CaptureSession$State r3 = r4.mState     // Catch:{ all -> 0x0066 }
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ all -> 0x0066 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0066 }
            r1.<init>(r2)     // Catch:{ all -> 0x0066 }
            throw r1     // Catch:{ all -> 0x0066 }
        L_0x0064:
            monitor-exit(r0)     // Catch:{ all -> 0x0066 }
            return
        L_0x0066:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0066 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.camera.camera2.internal.CaptureSession.setSessionConfig(androidx.camera.core.impl.SessionConfig):void");
    }

    public ListenableFuture<Void> open(SessionConfig sessionConfig, CameraDevice cameraDevice, SynchronizedCaptureSessionOpener opener) {
        synchronized (this.mSessionLock) {
            switch (this.mState) {
                case INITIALIZED:
                    this.mState = State.GET_SURFACE;
                    ArrayList arrayList = new ArrayList(sessionConfig.getSurfaces());
                    this.mConfiguredDeferrableSurfaces = arrayList;
                    this.mSynchronizedCaptureSessionOpener = opener;
                    ListenableFuture<Void> openFuture = FutureChain.from(opener.startWithDeferrableSurface(arrayList, TIMEOUT_GET_SURFACE_IN_MS)).transformAsync(new CaptureSession$$ExternalSyntheticLambda1(this, sessionConfig, cameraDevice), this.mSynchronizedCaptureSessionOpener.getExecutor());
                    Futures.addCallback(openFuture, new FutureCallback<Void>() {
                        public void onSuccess(Void result) {
                        }

                        public void onFailure(Throwable t) {
                            synchronized (CaptureSession.this.mSessionLock) {
                                CaptureSession.this.mSynchronizedCaptureSessionOpener.stop();
                                switch (AnonymousClass4.$SwitchMap$androidx$camera$camera2$internal$CaptureSession$State[CaptureSession.this.mState.ordinal()]) {
                                    case 4:
                                    case 6:
                                    case 7:
                                        if (!(t instanceof CancellationException)) {
                                            Logger.w(CaptureSession.TAG, "Opening session with fail " + CaptureSession.this.mState, t);
                                            CaptureSession.this.finishClose();
                                            break;
                                        }
                                        break;
                                }
                            }
                        }
                    }, this.mSynchronizedCaptureSessionOpener.getExecutor());
                    ListenableFuture<Void> nonCancellationPropagating = Futures.nonCancellationPropagating(openFuture);
                    return nonCancellationPropagating;
                default:
                    Logger.e(TAG, "Open not allowed in state: " + this.mState);
                    ListenableFuture<Void> immediateFailedFuture = Futures.immediateFailedFuture(new IllegalStateException("open() should not allow the state: " + this.mState));
                    return immediateFailedFuture;
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: openCaptureSession */
    public ListenableFuture<Void> m59lambda$open$0$androidxcameracamera2internalCaptureSession(List<Surface> configuredSurfaces, SessionConfig sessionConfig, CameraDevice cameraDevice) {
        CameraDevice cameraDevice2 = cameraDevice;
        synchronized (this.mSessionLock) {
            try {
                switch (AnonymousClass4.$SwitchMap$androidx$camera$camera2$internal$CaptureSession$State[this.mState.ordinal()]) {
                    case 1:
                    case 2:
                    case 5:
                        List<Surface> list = configuredSurfaces;
                        ListenableFuture<Void> immediateFailedFuture = Futures.immediateFailedFuture(new IllegalStateException("openCaptureSession() should not be possible in state: " + this.mState));
                        return immediateFailedFuture;
                    case 3:
                        this.mConfiguredSurfaceMap.clear();
                        for (int i = 0; i < configuredSurfaces.size(); i++) {
                            this.mConfiguredSurfaceMap.put(this.mConfiguredDeferrableSurfaces.get(i), configuredSurfaces.get(i));
                        }
                        List<Surface> list2 = configuredSurfaces;
                        this.mState = State.OPENING;
                        Logger.d(TAG, "Opening capture session.");
                        SynchronizedCaptureSession.StateCallback callbacks = SynchronizedCaptureSessionStateCallbacks.createComboCallback(this.mCaptureSessionStateCallback, new SynchronizedCaptureSessionStateCallbacks.Adapter(sessionConfig.getSessionStateCallbacks()));
                        Camera2ImplConfig camera2Config = new Camera2ImplConfig(sessionConfig.getImplementationOptions());
                        CameraEventCallbacks cameraEventCallback = camera2Config.getCameraEventCallback(CameraEventCallbacks.createEmptyCallback());
                        this.mCameraEventCallbacks = cameraEventCallback;
                        List<CaptureConfig> presetList = cameraEventCallback.createComboCallback().onPresetSession();
                        CaptureConfig.Builder captureConfigBuilder = CaptureConfig.Builder.from(sessionConfig.getRepeatingCaptureConfig());
                        for (CaptureConfig config : presetList) {
                            captureConfigBuilder.addImplementationOptions(config.getImplementationOptions());
                        }
                        List<OutputConfigurationCompat> outputConfigList = new ArrayList<>();
                        String physicalCameraIdForAllStreams = camera2Config.getPhysicalCameraId((String) null);
                        for (SessionConfig.OutputConfig outputConfig : sessionConfig.getOutputConfigs()) {
                            OutputConfigurationCompat outputConfiguration = getOutputConfigurationCompat(outputConfig, this.mConfiguredSurfaceMap, physicalCameraIdForAllStreams);
                            if (sessionConfig.getImplementationOptions().containsOption(Camera2ImplConfig.STREAM_USE_CASE_OPTION)) {
                                outputConfiguration.setStreamUseCase(((Long) sessionConfig.getImplementationOptions().retrieveOption(Camera2ImplConfig.STREAM_USE_CASE_OPTION)).longValue());
                            }
                            outputConfigList.add(outputConfiguration);
                        }
                        SessionConfigurationCompat sessionConfigCompat = this.mSynchronizedCaptureSessionOpener.createSessionConfigurationCompat(0, getUniqueOutputConfigurations(outputConfigList), callbacks);
                        if (sessionConfig.getTemplateType() == 5 && sessionConfig.getInputConfiguration() != null) {
                            sessionConfigCompat.setInputConfiguration(InputConfigurationCompat.wrap(sessionConfig.getInputConfiguration()));
                        }
                        try {
                            CaptureRequest captureRequest = Camera2CaptureRequestBuilder.buildWithoutTarget(captureConfigBuilder.build(), cameraDevice2);
                            if (captureRequest != null) {
                                sessionConfigCompat.setSessionParameters(captureRequest);
                            }
                            ListenableFuture<Void> openCaptureSession = this.mSynchronizedCaptureSessionOpener.openCaptureSession(cameraDevice2, sessionConfigCompat, this.mConfiguredDeferrableSurfaces);
                            return openCaptureSession;
                        } catch (CameraAccessException e) {
                            return Futures.immediateFailedFuture(e);
                        } catch (Throwable th) {
                            th = th;
                            break;
                        }
                    default:
                        List<Surface> list3 = configuredSurfaces;
                        ListenableFuture<Void> immediateFailedFuture2 = Futures.immediateFailedFuture(new CancellationException("openCaptureSession() not execute in state: " + this.mState));
                        return immediateFailedFuture2;
                }
            } catch (Throwable th2) {
                th = th2;
                List<Surface> list4 = configuredSurfaces;
                throw th;
            }
        }
    }

    private List<OutputConfigurationCompat> getUniqueOutputConfigurations(List<OutputConfigurationCompat> outputConfigurations) {
        List<Surface> addedSurfaces = new ArrayList<>();
        List<OutputConfigurationCompat> results = new ArrayList<>();
        for (OutputConfigurationCompat outputConfiguration : outputConfigurations) {
            if (!addedSurfaces.contains(outputConfiguration.getSurface())) {
                addedSurfaces.add(outputConfiguration.getSurface());
                results.add(outputConfiguration);
            }
        }
        return results;
    }

    private OutputConfigurationCompat getOutputConfigurationCompat(SessionConfig.OutputConfig outputConfig, Map<DeferrableSurface, Surface> configuredSurfaceMap, String physicalCameraIdForAllStreams) {
        Surface surface = configuredSurfaceMap.get(outputConfig.getSurface());
        Preconditions.checkNotNull(surface, "Surface in OutputConfig not found in configuredSurfaceMap.");
        OutputConfigurationCompat outputConfiguration = new OutputConfigurationCompat(outputConfig.getSurfaceGroupId(), surface);
        if (physicalCameraIdForAllStreams != null) {
            outputConfiguration.setPhysicalCameraId(physicalCameraIdForAllStreams);
        } else {
            outputConfiguration.setPhysicalCameraId(outputConfig.getPhysicalCameraId());
        }
        if (!outputConfig.getSharedSurfaces().isEmpty()) {
            outputConfiguration.enableSurfaceSharing();
            for (DeferrableSurface sharedDeferSurface : outputConfig.getSharedSurfaces()) {
                Surface sharedSurface = configuredSurfaceMap.get(sharedDeferSurface);
                Preconditions.checkNotNull(sharedSurface, "Surface in OutputConfig not found in configuredSurfaceMap.");
                outputConfiguration.addSurface(sharedSurface);
            }
        }
        return outputConfiguration;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x007c, code lost:
        r5.mState = androidx.camera.camera2.internal.CaptureSession.State.RELEASED;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void close() {
        /*
            r5 = this;
            java.lang.Object r0 = r5.mSessionLock
            monitor-enter(r0)
            int[] r1 = androidx.camera.camera2.internal.CaptureSession.AnonymousClass4.$SwitchMap$androidx$camera$camera2$internal$CaptureSession$State     // Catch:{ all -> 0x009e }
            androidx.camera.camera2.internal.CaptureSession$State r2 = r5.mState     // Catch:{ all -> 0x009e }
            int r2 = r2.ordinal()     // Catch:{ all -> 0x009e }
            r1 = r1[r2]     // Catch:{ all -> 0x009e }
            switch(r1) {
                case 1: goto L_0x0081;
                case 2: goto L_0x007c;
                case 3: goto L_0x005d;
                case 4: goto L_0x0036;
                case 5: goto L_0x0012;
                default: goto L_0x0010;
            }     // Catch:{ all -> 0x009e }
        L_0x0010:
            goto L_0x009c
        L_0x0012:
            androidx.camera.core.impl.SessionConfig r1 = r5.mSessionConfig     // Catch:{ all -> 0x009e }
            if (r1 == 0) goto L_0x0036
            androidx.camera.camera2.impl.CameraEventCallbacks r1 = r5.mCameraEventCallbacks     // Catch:{ all -> 0x009e }
            androidx.camera.camera2.impl.CameraEventCallbacks$ComboCameraEventCallback r1 = r1.createComboCallback()     // Catch:{ all -> 0x009e }
            java.util.List r1 = r1.onDisableSession()     // Catch:{ all -> 0x009e }
            boolean r2 = r1.isEmpty()     // Catch:{ all -> 0x009e }
            if (r2 != 0) goto L_0x0036
            java.util.List r2 = r5.setupConfiguredSurface(r1)     // Catch:{ IllegalStateException -> 0x002e }
            r5.issueCaptureRequests(r2)     // Catch:{ IllegalStateException -> 0x002e }
            goto L_0x0036
        L_0x002e:
            r2 = move-exception
            java.lang.String r3 = "CaptureSession"
            java.lang.String r4 = "Unable to issue the request before close the capture session"
            androidx.camera.core.Logger.e(r3, r4, r2)     // Catch:{ all -> 0x009e }
        L_0x0036:
            androidx.camera.camera2.internal.SynchronizedCaptureSessionOpener r1 = r5.mSynchronizedCaptureSessionOpener     // Catch:{ all -> 0x009e }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x009e }
            r2.<init>()     // Catch:{ all -> 0x009e }
            java.lang.String r3 = "The Opener shouldn't null in state:"
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ all -> 0x009e }
            androidx.camera.camera2.internal.CaptureSession$State r3 = r5.mState     // Catch:{ all -> 0x009e }
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ all -> 0x009e }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x009e }
            androidx.core.util.Preconditions.checkNotNull(r1, r2)     // Catch:{ all -> 0x009e }
            androidx.camera.camera2.internal.SynchronizedCaptureSessionOpener r1 = r5.mSynchronizedCaptureSessionOpener     // Catch:{ all -> 0x009e }
            r1.stop()     // Catch:{ all -> 0x009e }
            androidx.camera.camera2.internal.CaptureSession$State r1 = androidx.camera.camera2.internal.CaptureSession.State.CLOSED     // Catch:{ all -> 0x009e }
            r5.mState = r1     // Catch:{ all -> 0x009e }
            r1 = 0
            r5.mSessionConfig = r1     // Catch:{ all -> 0x009e }
            goto L_0x009c
        L_0x005d:
            androidx.camera.camera2.internal.SynchronizedCaptureSessionOpener r1 = r5.mSynchronizedCaptureSessionOpener     // Catch:{ all -> 0x009e }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x009e }
            r2.<init>()     // Catch:{ all -> 0x009e }
            java.lang.String r3 = "The Opener shouldn't null in state:"
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ all -> 0x009e }
            androidx.camera.camera2.internal.CaptureSession$State r3 = r5.mState     // Catch:{ all -> 0x009e }
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ all -> 0x009e }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x009e }
            androidx.core.util.Preconditions.checkNotNull(r1, r2)     // Catch:{ all -> 0x009e }
            androidx.camera.camera2.internal.SynchronizedCaptureSessionOpener r1 = r5.mSynchronizedCaptureSessionOpener     // Catch:{ all -> 0x009e }
            r1.stop()     // Catch:{ all -> 0x009e }
        L_0x007c:
            androidx.camera.camera2.internal.CaptureSession$State r1 = androidx.camera.camera2.internal.CaptureSession.State.RELEASED     // Catch:{ all -> 0x009e }
            r5.mState = r1     // Catch:{ all -> 0x009e }
            goto L_0x009c
        L_0x0081:
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException     // Catch:{ all -> 0x009e }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x009e }
            r2.<init>()     // Catch:{ all -> 0x009e }
            java.lang.String r3 = "close() should not be possible in state: "
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ all -> 0x009e }
            androidx.camera.camera2.internal.CaptureSession$State r3 = r5.mState     // Catch:{ all -> 0x009e }
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ all -> 0x009e }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x009e }
            r1.<init>(r2)     // Catch:{ all -> 0x009e }
            throw r1     // Catch:{ all -> 0x009e }
        L_0x009c:
            monitor-exit(r0)     // Catch:{ all -> 0x009e }
            return
        L_0x009e:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x009e }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.camera.camera2.internal.CaptureSession.close():void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0029, code lost:
        r4.mState = androidx.camera.camera2.internal.CaptureSession.State.RELEASING;
        androidx.core.util.Preconditions.checkNotNull(r4.mSynchronizedCaptureSessionOpener, "The Opener shouldn't null in state:" + r4.mState);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x004d, code lost:
        if (r4.mSynchronizedCaptureSessionOpener.stop() == false) goto L_0x0053;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x004f, code lost:
        finishClose();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0055, code lost:
        if (r4.mReleaseFuture != null) goto L_0x0062;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0057, code lost:
        r4.mReleaseFuture = androidx.concurrent.futures.CallbackToFutureAdapter.getFuture(new androidx.camera.camera2.internal.CaptureSession$$ExternalSyntheticLambda2(r4));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0062, code lost:
        r1 = r4.mReleaseFuture;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0065, code lost:
        return r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0085, code lost:
        r4.mState = androidx.camera.camera2.internal.CaptureSession.State.RELEASED;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00ac, code lost:
        return androidx.camera.core.impl.utils.futures.Futures.immediateFuture(null);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.google.common.util.concurrent.ListenableFuture<java.lang.Void> release(boolean r5) {
        /*
            r4 = this;
            java.lang.Object r0 = r4.mSessionLock
            monitor-enter(r0)
            int[] r1 = androidx.camera.camera2.internal.CaptureSession.AnonymousClass4.$SwitchMap$androidx$camera$camera2$internal$CaptureSession$State     // Catch:{ all -> 0x00ad }
            androidx.camera.camera2.internal.CaptureSession$State r2 = r4.mState     // Catch:{ all -> 0x00ad }
            int r2 = r2.ordinal()     // Catch:{ all -> 0x00ad }
            r1 = r1[r2]     // Catch:{ all -> 0x00ad }
            switch(r1) {
                case 1: goto L_0x008a;
                case 2: goto L_0x0085;
                case 3: goto L_0x0066;
                case 4: goto L_0x0029;
                case 5: goto L_0x0012;
                case 6: goto L_0x0012;
                case 7: goto L_0x0053;
                default: goto L_0x0010;
            }     // Catch:{ all -> 0x00ad }
        L_0x0010:
            goto L_0x00a6
        L_0x0012:
            androidx.camera.camera2.internal.SynchronizedCaptureSession r1 = r4.mSynchronizedCaptureSession     // Catch:{ all -> 0x00ad }
            if (r1 == 0) goto L_0x0029
            if (r5 == 0) goto L_0x0024
            r1.abortCaptures()     // Catch:{ CameraAccessException -> 0x001c }
            goto L_0x0024
        L_0x001c:
            r1 = move-exception
            java.lang.String r2 = "CaptureSession"
            java.lang.String r3 = "Unable to abort captures."
            androidx.camera.core.Logger.e(r2, r3, r1)     // Catch:{ all -> 0x00ad }
        L_0x0024:
            androidx.camera.camera2.internal.SynchronizedCaptureSession r1 = r4.mSynchronizedCaptureSession     // Catch:{ all -> 0x00ad }
            r1.close()     // Catch:{ all -> 0x00ad }
        L_0x0029:
            androidx.camera.camera2.internal.CaptureSession$State r1 = androidx.camera.camera2.internal.CaptureSession.State.RELEASING     // Catch:{ all -> 0x00ad }
            r4.mState = r1     // Catch:{ all -> 0x00ad }
            androidx.camera.camera2.internal.SynchronizedCaptureSessionOpener r1 = r4.mSynchronizedCaptureSessionOpener     // Catch:{ all -> 0x00ad }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ad }
            r2.<init>()     // Catch:{ all -> 0x00ad }
            java.lang.String r3 = "The Opener shouldn't null in state:"
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ all -> 0x00ad }
            androidx.camera.camera2.internal.CaptureSession$State r3 = r4.mState     // Catch:{ all -> 0x00ad }
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ all -> 0x00ad }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x00ad }
            androidx.core.util.Preconditions.checkNotNull(r1, r2)     // Catch:{ all -> 0x00ad }
            androidx.camera.camera2.internal.SynchronizedCaptureSessionOpener r1 = r4.mSynchronizedCaptureSessionOpener     // Catch:{ all -> 0x00ad }
            boolean r1 = r1.stop()     // Catch:{ all -> 0x00ad }
            if (r1 == 0) goto L_0x0053
            r4.finishClose()     // Catch:{ all -> 0x00ad }
            goto L_0x00a6
        L_0x0053:
            com.google.common.util.concurrent.ListenableFuture<java.lang.Void> r1 = r4.mReleaseFuture     // Catch:{ all -> 0x00ad }
            if (r1 != 0) goto L_0x0062
            androidx.camera.camera2.internal.CaptureSession$$ExternalSyntheticLambda2 r1 = new androidx.camera.camera2.internal.CaptureSession$$ExternalSyntheticLambda2     // Catch:{ all -> 0x00ad }
            r1.<init>(r4)     // Catch:{ all -> 0x00ad }
            com.google.common.util.concurrent.ListenableFuture r1 = androidx.concurrent.futures.CallbackToFutureAdapter.getFuture(r1)     // Catch:{ all -> 0x00ad }
            r4.mReleaseFuture = r1     // Catch:{ all -> 0x00ad }
        L_0x0062:
            com.google.common.util.concurrent.ListenableFuture<java.lang.Void> r1 = r4.mReleaseFuture     // Catch:{ all -> 0x00ad }
            monitor-exit(r0)     // Catch:{ all -> 0x00ad }
            return r1
        L_0x0066:
            androidx.camera.camera2.internal.SynchronizedCaptureSessionOpener r1 = r4.mSynchronizedCaptureSessionOpener     // Catch:{ all -> 0x00ad }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ad }
            r2.<init>()     // Catch:{ all -> 0x00ad }
            java.lang.String r3 = "The Opener shouldn't null in state:"
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ all -> 0x00ad }
            androidx.camera.camera2.internal.CaptureSession$State r3 = r4.mState     // Catch:{ all -> 0x00ad }
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ all -> 0x00ad }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x00ad }
            androidx.core.util.Preconditions.checkNotNull(r1, r2)     // Catch:{ all -> 0x00ad }
            androidx.camera.camera2.internal.SynchronizedCaptureSessionOpener r1 = r4.mSynchronizedCaptureSessionOpener     // Catch:{ all -> 0x00ad }
            r1.stop()     // Catch:{ all -> 0x00ad }
        L_0x0085:
            androidx.camera.camera2.internal.CaptureSession$State r1 = androidx.camera.camera2.internal.CaptureSession.State.RELEASED     // Catch:{ all -> 0x00ad }
            r4.mState = r1     // Catch:{ all -> 0x00ad }
            goto L_0x00a6
        L_0x008a:
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException     // Catch:{ all -> 0x00ad }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ad }
            r2.<init>()     // Catch:{ all -> 0x00ad }
            java.lang.String r3 = "release() should not be possible in state: "
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ all -> 0x00ad }
            androidx.camera.camera2.internal.CaptureSession$State r3 = r4.mState     // Catch:{ all -> 0x00ad }
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ all -> 0x00ad }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x00ad }
            r1.<init>(r2)     // Catch:{ all -> 0x00ad }
            throw r1     // Catch:{ all -> 0x00ad }
        L_0x00a6:
            monitor-exit(r0)     // Catch:{ all -> 0x00ad }
            r0 = 0
            com.google.common.util.concurrent.ListenableFuture r0 = androidx.camera.core.impl.utils.futures.Futures.immediateFuture(r0)
            return r0
        L_0x00ad:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00ad }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.camera.camera2.internal.CaptureSession.release(boolean):com.google.common.util.concurrent.ListenableFuture");
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$release$1$androidx-camera-camera2-internal-CaptureSession  reason: not valid java name */
    public /* synthetic */ Object m60lambda$release$1$androidxcameracamera2internalCaptureSession(CallbackToFutureAdapter.Completer completer) throws Exception {
        String str;
        synchronized (this.mSessionLock) {
            Preconditions.checkState(this.mReleaseCompleter == null, "Release completer expected to be null");
            this.mReleaseCompleter = completer;
            str = "Release[session=" + this + "]";
        }
        return str;
    }

    public void issueCaptureRequests(List<CaptureConfig> captureConfigs) {
        synchronized (this.mSessionLock) {
            switch (AnonymousClass4.$SwitchMap$androidx$camera$camera2$internal$CaptureSession$State[this.mState.ordinal()]) {
                case 1:
                    throw new IllegalStateException("issueCaptureRequests() should not be possible in state: " + this.mState);
                case 2:
                case 3:
                case 4:
                    this.mCaptureConfigs.addAll(captureConfigs);
                    break;
                case 5:
                    this.mCaptureConfigs.addAll(captureConfigs);
                    issuePendingCaptureRequest();
                    break;
                case 6:
                case 7:
                case 8:
                    throw new IllegalStateException("Cannot issue capture request on a closed/released session.");
            }
        }
    }

    public List<CaptureConfig> getCaptureConfigs() {
        List<CaptureConfig> unmodifiableList;
        synchronized (this.mSessionLock) {
            unmodifiableList = Collections.unmodifiableList(this.mCaptureConfigs);
        }
        return unmodifiableList;
    }

    /* access modifiers changed from: package-private */
    public State getState() {
        State state;
        synchronized (this.mSessionLock) {
            state = this.mState;
        }
        return state;
    }

    /* access modifiers changed from: package-private */
    public void finishClose() {
        if (this.mState == State.RELEASED) {
            Logger.d(TAG, "Skipping finishClose due to being state RELEASED.");
            return;
        }
        this.mState = State.RELEASED;
        this.mSynchronizedCaptureSession = null;
        CallbackToFutureAdapter.Completer<Void> completer = this.mReleaseCompleter;
        if (completer != null) {
            completer.set(null);
            this.mReleaseCompleter = null;
        }
    }

    /* access modifiers changed from: package-private */
    public int issueRepeatingCaptureRequests(SessionConfig sessionConfig) {
        synchronized (this.mSessionLock) {
            if (sessionConfig == null) {
                Logger.d(TAG, "Skipping issueRepeatingCaptureRequests for no configuration case.");
                return -1;
            }
            CaptureConfig captureConfig = sessionConfig.getRepeatingCaptureConfig();
            if (captureConfig.getSurfaces().isEmpty()) {
                Logger.d(TAG, "Skipping issueRepeatingCaptureRequests for no surface.");
                try {
                    this.mSynchronizedCaptureSession.stopRepeating();
                } catch (CameraAccessException e) {
                    Logger.e(TAG, "Unable to access camera: " + e.getMessage());
                    Thread.dumpStack();
                }
            } else {
                try {
                    Logger.d(TAG, "Issuing request for session.");
                    CaptureConfig.Builder captureConfigBuilder = CaptureConfig.Builder.from(captureConfig);
                    Config mergeOptions = mergeOptions(this.mCameraEventCallbacks.createComboCallback().onRepeating());
                    this.mCameraEventOnRepeatingOptions = mergeOptions;
                    captureConfigBuilder.addImplementationOptions(mergeOptions);
                    CaptureRequest captureRequest = Camera2CaptureRequestBuilder.build(captureConfigBuilder.build(), this.mSynchronizedCaptureSession.getDevice(), this.mConfiguredSurfaceMap);
                    if (captureRequest == null) {
                        Logger.d(TAG, "Skipping issuing empty request for session.");
                        return -1;
                    }
                    int singleRepeatingRequest = this.mSynchronizedCaptureSession.setSingleRepeatingRequest(captureRequest, createCamera2CaptureCallback(captureConfig.getCameraCaptureCallbacks(), this.mCaptureCallback));
                    return singleRepeatingRequest;
                } catch (CameraAccessException e2) {
                    Logger.e(TAG, "Unable to access camera: " + e2.getMessage());
                    Thread.dumpStack();
                    return -1;
                }
            }
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public void issuePendingCaptureRequest() {
        if (!this.mCaptureConfigs.isEmpty()) {
            try {
                issueBurstCaptureRequest(this.mCaptureConfigs);
            } finally {
                this.mCaptureConfigs.clear();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int issueBurstCaptureRequest(List<CaptureConfig> captureConfigs) {
        synchronized (this.mSessionLock) {
            if (captureConfigs.isEmpty()) {
                return -1;
            }
            try {
                CameraBurstCaptureCallback callbackAggregator = new CameraBurstCaptureCallback();
                List<CaptureRequest> captureRequests = new ArrayList<>();
                boolean isStillCapture = false;
                Logger.d(TAG, "Issuing capture request.");
                for (CaptureConfig captureConfig : captureConfigs) {
                    if (captureConfig.getSurfaces().isEmpty()) {
                        Logger.d(TAG, "Skipping issuing empty capture request.");
                    } else {
                        boolean surfacesValid = true;
                        Iterator<DeferrableSurface> it = captureConfig.getSurfaces().iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            DeferrableSurface surface = it.next();
                            if (!this.mConfiguredSurfaceMap.containsKey(surface)) {
                                Logger.d(TAG, "Skipping capture request with invalid surface: " + surface);
                                surfacesValid = false;
                                break;
                            }
                        }
                        if (surfacesValid) {
                            if (captureConfig.getTemplateType() == 2) {
                                isStillCapture = true;
                            }
                            CaptureConfig.Builder captureConfigBuilder = CaptureConfig.Builder.from(captureConfig);
                            if (captureConfig.getTemplateType() == 5 && captureConfig.getCameraCaptureResult() != null) {
                                captureConfigBuilder.setCameraCaptureResult(captureConfig.getCameraCaptureResult());
                            }
                            SessionConfig sessionConfig = this.mSessionConfig;
                            if (sessionConfig != null) {
                                captureConfigBuilder.addImplementationOptions(sessionConfig.getRepeatingCaptureConfig().getImplementationOptions());
                            }
                            captureConfigBuilder.addImplementationOptions(this.mCameraEventOnRepeatingOptions);
                            captureConfigBuilder.addImplementationOptions(captureConfig.getImplementationOptions());
                            CaptureRequest captureRequest = Camera2CaptureRequestBuilder.build(captureConfigBuilder.build(), this.mSynchronizedCaptureSession.getDevice(), this.mConfiguredSurfaceMap);
                            if (captureRequest == null) {
                                Logger.d(TAG, "Skipping issuing request without surface.");
                                return -1;
                            }
                            List<CameraCaptureSession.CaptureCallback> cameraCallbacks = new ArrayList<>();
                            for (CameraCaptureCallback callback : captureConfig.getCameraCaptureCallbacks()) {
                                CaptureCallbackConverter.toCaptureCallback(callback, cameraCallbacks);
                            }
                            callbackAggregator.addCamera2Callbacks(captureRequest, cameraCallbacks);
                            captureRequests.add(captureRequest);
                        }
                    }
                }
                if (!captureRequests.isEmpty()) {
                    if (this.mStillCaptureFlow.shouldStopRepeatingBeforeCapture(captureRequests, isStillCapture)) {
                        this.mSynchronizedCaptureSession.stopRepeating();
                        callbackAggregator.setCaptureSequenceCallback(new CaptureSession$$ExternalSyntheticLambda0(this));
                    }
                    if (this.mTorchStateReset.isTorchResetRequired(captureRequests, isStillCapture)) {
                        callbackAggregator.addCamera2Callbacks(captureRequests.get(captureRequests.size() - 1), Collections.singletonList(new CameraCaptureSession.CaptureCallback() {
                            public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                                synchronized (CaptureSession.this.mSessionLock) {
                                    if (CaptureSession.this.mSessionConfig != null) {
                                        CaptureConfig repeatingConfig = CaptureSession.this.mSessionConfig.getRepeatingCaptureConfig();
                                        Logger.d(CaptureSession.TAG, "Submit FLASH_MODE_OFF request");
                                        CaptureSession captureSession = CaptureSession.this;
                                        captureSession.issueCaptureRequests(Collections.singletonList(captureSession.mTorchStateReset.createTorchResetRequest(repeatingConfig)));
                                    }
                                }
                            }
                        }));
                    }
                    int captureBurstRequests = this.mSynchronizedCaptureSession.captureBurstRequests(captureRequests, callbackAggregator);
                    return captureBurstRequests;
                }
                Logger.d(TAG, "Skipping issuing burst request due to no valid request elements");
            } catch (CameraAccessException e) {
                Logger.e(TAG, "Unable to access camera: " + e.getMessage());
                Thread.dumpStack();
            }
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$issueBurstCaptureRequest$2$androidx-camera-camera2-internal-CaptureSession  reason: not valid java name */
    public /* synthetic */ void m58lambda$issueBurstCaptureRequest$2$androidxcameracamera2internalCaptureSession(CameraCaptureSession session, int sequenceId, boolean isAborted) {
        synchronized (this.mSessionLock) {
            if (this.mState == State.OPENED) {
                issueRepeatingCaptureRequests(this.mSessionConfig);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void abortCaptures() {
        synchronized (this.mSessionLock) {
            if (this.mState != State.OPENED) {
                Logger.e(TAG, "Unable to abort captures. Incorrect state:" + this.mState);
                return;
            }
            try {
                this.mSynchronizedCaptureSession.abortCaptures();
            } catch (CameraAccessException e) {
                Logger.e(TAG, "Unable to abort captures.", e);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void stopRepeating() {
        synchronized (this.mSessionLock) {
            if (this.mState != State.OPENED) {
                Logger.e(TAG, "Unable to stop repeating. Incorrect state:" + this.mState);
                return;
            }
            try {
                this.mSynchronizedCaptureSession.stopRepeating();
            } catch (CameraAccessException e) {
                Logger.e(TAG, "Unable to stop repeating.", e);
            }
        }
    }

    public void cancelIssuedCaptureRequests() {
        List<CaptureConfig> captureConfigs = null;
        synchronized (this.mSessionLock) {
            if (!this.mCaptureConfigs.isEmpty()) {
                captureConfigs = new ArrayList<>(this.mCaptureConfigs);
                this.mCaptureConfigs.clear();
            }
        }
        if (captureConfigs != null) {
            for (CaptureConfig captureConfig : captureConfigs) {
                for (CameraCaptureCallback cameraCaptureCallback : captureConfig.getCameraCaptureCallbacks()) {
                    cameraCaptureCallback.onCaptureCancelled();
                }
            }
        }
    }

    private CameraCaptureSession.CaptureCallback createCamera2CaptureCallback(List<CameraCaptureCallback> cameraCaptureCallbacks, CameraCaptureSession.CaptureCallback... additionalCallbacks) {
        List<CameraCaptureSession.CaptureCallback> camera2Callbacks = new ArrayList<>(cameraCaptureCallbacks.size() + additionalCallbacks.length);
        for (CameraCaptureCallback callback : cameraCaptureCallbacks) {
            camera2Callbacks.add(CaptureCallbackConverter.toCaptureCallback(callback));
        }
        Collections.addAll(camera2Callbacks, additionalCallbacks);
        return Camera2CaptureCallbacks.createComboCallback(camera2Callbacks);
    }

    private static Config mergeOptions(List<CaptureConfig> captureConfigList) {
        MutableOptionsBundle options = MutableOptionsBundle.create();
        for (CaptureConfig captureConfig : captureConfigList) {
            Config newOptions = captureConfig.getImplementationOptions();
            for (Config.Option<?> option : newOptions.listOptions()) {
                Config.Option<?> option2 = option;
                Object newValue = newOptions.retrieveOption(option2, null);
                if (options.containsOption(option)) {
                    Object oldValue = options.retrieveOption(option2, null);
                    if (!Objects.equals(oldValue, newValue)) {
                        Logger.d(TAG, "Detect conflicting option " + option2.getId() + " : " + newValue + " != " + oldValue);
                    }
                } else {
                    options.insertOption(option2, newValue);
                }
            }
        }
        return options;
    }

    final class StateCallback extends SynchronizedCaptureSession.StateCallback {
        StateCallback() {
        }

        public void onConfigured(SynchronizedCaptureSession session) {
            synchronized (CaptureSession.this.mSessionLock) {
                switch (AnonymousClass4.$SwitchMap$androidx$camera$camera2$internal$CaptureSession$State[CaptureSession.this.mState.ordinal()]) {
                    case 1:
                    case 2:
                    case 3:
                    case 5:
                    case 8:
                        throw new IllegalStateException("onConfigured() should not be possible in state: " + CaptureSession.this.mState);
                    case 4:
                        CaptureSession.this.mState = State.OPENED;
                        CaptureSession.this.mSynchronizedCaptureSession = session;
                        if (CaptureSession.this.mSessionConfig != null) {
                            List<CaptureConfig> list = CaptureSession.this.mCameraEventCallbacks.createComboCallback().onEnableSession();
                            if (!list.isEmpty()) {
                                CaptureSession captureSession = CaptureSession.this;
                                captureSession.issueBurstCaptureRequest(captureSession.setupConfiguredSurface(list));
                            }
                        }
                        Logger.d(CaptureSession.TAG, "Attempting to send capture request onConfigured");
                        CaptureSession captureSession2 = CaptureSession.this;
                        captureSession2.issueRepeatingCaptureRequests(captureSession2.mSessionConfig);
                        CaptureSession.this.issuePendingCaptureRequest();
                        break;
                    case 6:
                        CaptureSession.this.mSynchronizedCaptureSession = session;
                        break;
                    case 7:
                        session.close();
                        break;
                }
                Logger.d(CaptureSession.TAG, "CameraCaptureSession.onConfigured() mState=" + CaptureSession.this.mState);
            }
        }

        public void onReady(SynchronizedCaptureSession session) {
            synchronized (CaptureSession.this.mSessionLock) {
                switch (AnonymousClass4.$SwitchMap$androidx$camera$camera2$internal$CaptureSession$State[CaptureSession.this.mState.ordinal()]) {
                    case 1:
                        throw new IllegalStateException("onReady() should not be possible in state: " + CaptureSession.this.mState);
                    default:
                        Logger.d(CaptureSession.TAG, "CameraCaptureSession.onReady() " + CaptureSession.this.mState);
                        break;
                }
            }
        }

        public void onSessionFinished(SynchronizedCaptureSession session) {
            synchronized (CaptureSession.this.mSessionLock) {
                if (CaptureSession.this.mState != State.UNINITIALIZED) {
                    Logger.d(CaptureSession.TAG, "onSessionFinished()");
                    CaptureSession.this.finishClose();
                } else {
                    throw new IllegalStateException("onSessionFinished() should not be possible in state: " + CaptureSession.this.mState);
                }
            }
        }

        public void onConfigureFailed(SynchronizedCaptureSession session) {
            synchronized (CaptureSession.this.mSessionLock) {
                switch (AnonymousClass4.$SwitchMap$androidx$camera$camera2$internal$CaptureSession$State[CaptureSession.this.mState.ordinal()]) {
                    case 1:
                    case 2:
                    case 3:
                    case 5:
                        throw new IllegalStateException("onConfigureFailed() should not be possible in state: " + CaptureSession.this.mState);
                    case 4:
                    case 6:
                    case 7:
                        CaptureSession.this.finishClose();
                        break;
                    case 8:
                        Logger.d(CaptureSession.TAG, "ConfigureFailed callback after change to RELEASED state");
                        break;
                }
                Logger.e(CaptureSession.TAG, "CameraCaptureSession.onConfigureFailed() " + CaptureSession.this.mState);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public List<CaptureConfig> setupConfiguredSurface(List<CaptureConfig> list) {
        List<CaptureConfig> ret = new ArrayList<>();
        for (CaptureConfig c : list) {
            CaptureConfig.Builder builder = CaptureConfig.Builder.from(c);
            builder.setTemplateType(1);
            for (DeferrableSurface deferrableSurface : this.mSessionConfig.getRepeatingCaptureConfig().getSurfaces()) {
                builder.addSurface(deferrableSurface);
            }
            ret.add(builder.build());
        }
        return ret;
    }
}

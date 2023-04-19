package androidx.camera.camera2.internal;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;
import android.view.Surface;
import androidx.camera.camera2.internal.SynchronizedCaptureSession;
import androidx.camera.camera2.internal.SynchronizedCaptureSessionOpener;
import androidx.camera.camera2.internal.compat.CameraCaptureSessionCompat;
import androidx.camera.camera2.internal.compat.CameraDeviceCompat;
import androidx.camera.camera2.internal.compat.params.OutputConfigurationCompat;
import androidx.camera.camera2.internal.compat.params.SessionConfigurationCompat;
import androidx.camera.core.Logger;
import androidx.camera.core.impl.DeferrableSurface;
import androidx.camera.core.impl.DeferrableSurfaces;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.impl.utils.futures.FutureCallback;
import androidx.camera.core.impl.utils.futures.FutureChain;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.util.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;

class SynchronizedCaptureSessionBaseImpl extends SynchronizedCaptureSession.StateCallback implements SynchronizedCaptureSession, SynchronizedCaptureSessionOpener.OpenerImpl {
    private static final String TAG = "SyncCaptureSessionBase";
    CameraCaptureSessionCompat mCameraCaptureSessionCompat;
    final CaptureSessionRepository mCaptureSessionRepository;
    SynchronizedCaptureSession.StateCallback mCaptureSessionStateCallback;
    private boolean mClosed = false;
    final Handler mCompatHandler;
    final Executor mExecutor;
    private List<DeferrableSurface> mHeldDeferrableSurfaces = null;
    final Object mLock = new Object();
    CallbackToFutureAdapter.Completer<Void> mOpenCaptureSessionCompleter;
    ListenableFuture<Void> mOpenCaptureSessionFuture;
    private boolean mOpenerDisabled = false;
    private final ScheduledExecutorService mScheduledExecutorService;
    private boolean mSessionFinished = false;
    private ListenableFuture<List<Surface>> mStartingSurface;

    SynchronizedCaptureSessionBaseImpl(CaptureSessionRepository repository, Executor executor, ScheduledExecutorService scheduledExecutorService, Handler compatHandler) {
        this.mCaptureSessionRepository = repository;
        this.mCompatHandler = compatHandler;
        this.mExecutor = executor;
        this.mScheduledExecutorService = scheduledExecutorService;
    }

    public SynchronizedCaptureSession.StateCallback getStateCallback() {
        return this;
    }

    public ListenableFuture<Void> getOpeningBlocker() {
        return Futures.immediateFuture(null);
    }

    public ListenableFuture<Void> openCaptureSession(CameraDevice cameraDevice, SessionConfigurationCompat sessionConfigurationCompat, List<DeferrableSurface> deferrableSurfaces) {
        synchronized (this.mLock) {
            if (this.mOpenerDisabled) {
                ListenableFuture<Void> immediateFailedFuture = Futures.immediateFailedFuture(new CancellationException("Opener is disabled"));
                return immediateFailedFuture;
            }
            this.mCaptureSessionRepository.onCreateCaptureSession(this);
            ListenableFuture<Void> future = CallbackToFutureAdapter.getFuture(new SynchronizedCaptureSessionBaseImpl$$ExternalSyntheticLambda1(this, deferrableSurfaces, CameraDeviceCompat.toCameraDeviceCompat(cameraDevice, this.mCompatHandler), sessionConfigurationCompat));
            this.mOpenCaptureSessionFuture = future;
            Futures.addCallback(future, new FutureCallback<Void>() {
                public void onSuccess(Void result) {
                }

                public void onFailure(Throwable t) {
                    SynchronizedCaptureSessionBaseImpl.this.finishClose();
                    SynchronizedCaptureSessionBaseImpl.this.mCaptureSessionRepository.onCaptureSessionConfigureFail(SynchronizedCaptureSessionBaseImpl.this);
                }
            }, CameraXExecutors.directExecutor());
            ListenableFuture<Void> nonCancellationPropagating = Futures.nonCancellationPropagating(this.mOpenCaptureSessionFuture);
            return nonCancellationPropagating;
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$openCaptureSession$0$androidx-camera-camera2-internal-SynchronizedCaptureSessionBaseImpl  reason: not valid java name */
    public /* synthetic */ Object m83lambda$openCaptureSession$0$androidxcameracamera2internalSynchronizedCaptureSessionBaseImpl(List deferrableSurfaces, CameraDeviceCompat cameraDeviceCompat, SessionConfigurationCompat sessionConfigurationCompat, CallbackToFutureAdapter.Completer completer) throws Exception {
        String str;
        synchronized (this.mLock) {
            holdDeferrableSurfaces(deferrableSurfaces);
            Preconditions.checkState(this.mOpenCaptureSessionCompleter == null, "The openCaptureSessionCompleter can only set once!");
            this.mOpenCaptureSessionCompleter = completer;
            cameraDeviceCompat.createCaptureSession(sessionConfigurationCompat);
            str = "openCaptureSession[session=" + this + "]";
        }
        return str;
    }

    /* access modifiers changed from: package-private */
    public boolean isCameraCaptureSessionOpen() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mOpenCaptureSessionFuture != null;
        }
        return z;
    }

    public SessionConfigurationCompat createSessionConfigurationCompat(int sessionType, List<OutputConfigurationCompat> outputsCompat, SynchronizedCaptureSession.StateCallback stateCallback) {
        this.mCaptureSessionStateCallback = stateCallback;
        return new SessionConfigurationCompat(sessionType, outputsCompat, getExecutor(), new CameraCaptureSession.StateCallback() {
            public void onReady(CameraCaptureSession session) {
                SynchronizedCaptureSessionBaseImpl.this.createCaptureSessionCompat(session);
                SynchronizedCaptureSessionBaseImpl synchronizedCaptureSessionBaseImpl = SynchronizedCaptureSessionBaseImpl.this;
                synchronizedCaptureSessionBaseImpl.onReady(synchronizedCaptureSessionBaseImpl);
            }

            public void onActive(CameraCaptureSession session) {
                SynchronizedCaptureSessionBaseImpl.this.createCaptureSessionCompat(session);
                SynchronizedCaptureSessionBaseImpl synchronizedCaptureSessionBaseImpl = SynchronizedCaptureSessionBaseImpl.this;
                synchronizedCaptureSessionBaseImpl.onActive(synchronizedCaptureSessionBaseImpl);
            }

            public void onCaptureQueueEmpty(CameraCaptureSession session) {
                SynchronizedCaptureSessionBaseImpl.this.createCaptureSessionCompat(session);
                SynchronizedCaptureSessionBaseImpl synchronizedCaptureSessionBaseImpl = SynchronizedCaptureSessionBaseImpl.this;
                synchronizedCaptureSessionBaseImpl.onCaptureQueueEmpty(synchronizedCaptureSessionBaseImpl);
            }

            public void onSurfacePrepared(CameraCaptureSession session, Surface surface) {
                SynchronizedCaptureSessionBaseImpl.this.createCaptureSessionCompat(session);
                SynchronizedCaptureSessionBaseImpl synchronizedCaptureSessionBaseImpl = SynchronizedCaptureSessionBaseImpl.this;
                synchronizedCaptureSessionBaseImpl.onSurfacePrepared(synchronizedCaptureSessionBaseImpl, surface);
            }

            public void onConfigured(CameraCaptureSession session) {
                CallbackToFutureAdapter.Completer<Void> completer;
                try {
                    SynchronizedCaptureSessionBaseImpl.this.createCaptureSessionCompat(session);
                    SynchronizedCaptureSessionBaseImpl synchronizedCaptureSessionBaseImpl = SynchronizedCaptureSessionBaseImpl.this;
                    synchronizedCaptureSessionBaseImpl.onConfigured(synchronizedCaptureSessionBaseImpl);
                    synchronized (SynchronizedCaptureSessionBaseImpl.this.mLock) {
                        Preconditions.checkNotNull(SynchronizedCaptureSessionBaseImpl.this.mOpenCaptureSessionCompleter, "OpenCaptureSession completer should not null");
                        completer = SynchronizedCaptureSessionBaseImpl.this.mOpenCaptureSessionCompleter;
                        SynchronizedCaptureSessionBaseImpl.this.mOpenCaptureSessionCompleter = null;
                    }
                    completer.set(null);
                } catch (Throwable th) {
                    synchronized (SynchronizedCaptureSessionBaseImpl.this.mLock) {
                        Preconditions.checkNotNull(SynchronizedCaptureSessionBaseImpl.this.mOpenCaptureSessionCompleter, "OpenCaptureSession completer should not null");
                        CallbackToFutureAdapter.Completer<Void> completer2 = SynchronizedCaptureSessionBaseImpl.this.mOpenCaptureSessionCompleter;
                        SynchronizedCaptureSessionBaseImpl.this.mOpenCaptureSessionCompleter = null;
                        completer2.set(null);
                        throw th;
                    }
                }
            }

            public void onConfigureFailed(CameraCaptureSession session) {
                CallbackToFutureAdapter.Completer<Void> completer;
                try {
                    SynchronizedCaptureSessionBaseImpl.this.createCaptureSessionCompat(session);
                    SynchronizedCaptureSessionBaseImpl synchronizedCaptureSessionBaseImpl = SynchronizedCaptureSessionBaseImpl.this;
                    synchronizedCaptureSessionBaseImpl.onConfigureFailed(synchronizedCaptureSessionBaseImpl);
                    synchronized (SynchronizedCaptureSessionBaseImpl.this.mLock) {
                        Preconditions.checkNotNull(SynchronizedCaptureSessionBaseImpl.this.mOpenCaptureSessionCompleter, "OpenCaptureSession completer should not null");
                        completer = SynchronizedCaptureSessionBaseImpl.this.mOpenCaptureSessionCompleter;
                        SynchronizedCaptureSessionBaseImpl.this.mOpenCaptureSessionCompleter = null;
                    }
                    completer.setException(new IllegalStateException("onConfigureFailed"));
                } catch (Throwable th) {
                    synchronized (SynchronizedCaptureSessionBaseImpl.this.mLock) {
                        Preconditions.checkNotNull(SynchronizedCaptureSessionBaseImpl.this.mOpenCaptureSessionCompleter, "OpenCaptureSession completer should not null");
                        CallbackToFutureAdapter.Completer<Void> completer2 = SynchronizedCaptureSessionBaseImpl.this.mOpenCaptureSessionCompleter;
                        SynchronizedCaptureSessionBaseImpl.this.mOpenCaptureSessionCompleter = null;
                        completer2.setException(new IllegalStateException("onConfigureFailed"));
                        throw th;
                    }
                }
            }

            public void onClosed(CameraCaptureSession session) {
                SynchronizedCaptureSessionBaseImpl.this.createCaptureSessionCompat(session);
                SynchronizedCaptureSessionBaseImpl synchronizedCaptureSessionBaseImpl = SynchronizedCaptureSessionBaseImpl.this;
                synchronizedCaptureSessionBaseImpl.onClosed(synchronizedCaptureSessionBaseImpl);
            }
        });
    }

    public Executor getExecutor() {
        return this.mExecutor;
    }

    /* access modifiers changed from: package-private */
    public void createCaptureSessionCompat(CameraCaptureSession session) {
        if (this.mCameraCaptureSessionCompat == null) {
            this.mCameraCaptureSessionCompat = CameraCaptureSessionCompat.toCameraCaptureSessionCompat(session, this.mCompatHandler);
        }
    }

    public ListenableFuture<List<Surface>> startWithDeferrableSurface(List<DeferrableSurface> deferrableSurfaces, long timeout) {
        synchronized (this.mLock) {
            if (this.mOpenerDisabled) {
                ListenableFuture<List<Surface>> immediateFailedFuture = Futures.immediateFailedFuture(new CancellationException("Opener is disabled"));
                return immediateFailedFuture;
            }
            FutureChain<T> transformAsync = FutureChain.from(DeferrableSurfaces.surfaceListWithTimeout(deferrableSurfaces, false, timeout, getExecutor(), this.mScheduledExecutorService)).transformAsync(new SynchronizedCaptureSessionBaseImpl$$ExternalSyntheticLambda0(this, deferrableSurfaces), getExecutor());
            this.mStartingSurface = transformAsync;
            ListenableFuture<List<Surface>> nonCancellationPropagating = Futures.nonCancellationPropagating(transformAsync);
            return nonCancellationPropagating;
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$startWithDeferrableSurface$1$androidx-camera-camera2-internal-SynchronizedCaptureSessionBaseImpl  reason: not valid java name */
    public /* synthetic */ ListenableFuture m84lambda$startWithDeferrableSurface$1$androidxcameracamera2internalSynchronizedCaptureSessionBaseImpl(List deferrableSurfaces, List surfaces) throws Exception {
        Logger.d(TAG, "[" + this + "] getSurface...done");
        if (surfaces.contains((Object) null)) {
            return Futures.immediateFailedFuture(new DeferrableSurface.SurfaceClosedException("Surface closed", (DeferrableSurface) deferrableSurfaces.get(surfaces.indexOf((Object) null))));
        }
        if (surfaces.isEmpty()) {
            return Futures.immediateFailedFuture(new IllegalArgumentException("Unable to open capture session without surfaces"));
        }
        return Futures.immediateFuture(surfaces);
    }

    public boolean stop() {
        boolean z;
        ListenableFuture<List<Surface>> startingSurface = null;
        try {
            synchronized (this.mLock) {
                if (!this.mOpenerDisabled) {
                    ListenableFuture<List<Surface>> listenableFuture = this.mStartingSurface;
                    if (listenableFuture != null) {
                        startingSurface = listenableFuture;
                    }
                    this.mOpenerDisabled = true;
                }
                z = !isCameraCaptureSessionOpen();
            }
            if (startingSurface != null) {
                startingSurface.cancel(true);
            }
            return z;
        } catch (Throwable th) {
            if (startingSurface != null) {
                startingSurface.cancel(true);
            }
            throw th;
        }
    }

    public CameraCaptureSessionCompat toCameraCaptureSessionCompat() {
        Preconditions.checkNotNull(this.mCameraCaptureSessionCompat);
        return this.mCameraCaptureSessionCompat;
    }

    public CameraDevice getDevice() {
        Preconditions.checkNotNull(this.mCameraCaptureSessionCompat);
        return this.mCameraCaptureSessionCompat.toCameraCaptureSession().getDevice();
    }

    public Surface getInputSurface() {
        Preconditions.checkNotNull(this.mCameraCaptureSessionCompat);
        if (Build.VERSION.SDK_INT >= 23) {
            return Api23Impl.getInputSurface(this.mCameraCaptureSessionCompat.toCameraCaptureSession());
        }
        return null;
    }

    public int captureSingleRequest(CaptureRequest request, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        Preconditions.checkNotNull(this.mCameraCaptureSessionCompat, "Need to call openCaptureSession before using this API.");
        return this.mCameraCaptureSessionCompat.captureSingleRequest(request, getExecutor(), listener);
    }

    public int captureBurstRequests(List<CaptureRequest> requests, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        Preconditions.checkNotNull(this.mCameraCaptureSessionCompat, "Need to call openCaptureSession before using this API.");
        return this.mCameraCaptureSessionCompat.captureBurstRequests(requests, getExecutor(), listener);
    }

    public int setSingleRepeatingRequest(CaptureRequest request, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        Preconditions.checkNotNull(this.mCameraCaptureSessionCompat, "Need to call openCaptureSession before using this API.");
        return this.mCameraCaptureSessionCompat.setSingleRepeatingRequest(request, getExecutor(), listener);
    }

    public int setRepeatingBurstRequests(List<CaptureRequest> requests, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        Preconditions.checkNotNull(this.mCameraCaptureSessionCompat, "Need to call openCaptureSession before using this API.");
        return this.mCameraCaptureSessionCompat.setRepeatingBurstRequests(requests, getExecutor(), listener);
    }

    public int captureSingleRequest(CaptureRequest request, Executor executor, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        Preconditions.checkNotNull(this.mCameraCaptureSessionCompat, "Need to call openCaptureSession before using this API.");
        return this.mCameraCaptureSessionCompat.captureSingleRequest(request, executor, listener);
    }

    public int captureBurstRequests(List<CaptureRequest> requests, Executor executor, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        Preconditions.checkNotNull(this.mCameraCaptureSessionCompat, "Need to call openCaptureSession before using this API.");
        return this.mCameraCaptureSessionCompat.captureBurstRequests(requests, executor, listener);
    }

    public int setSingleRepeatingRequest(CaptureRequest request, Executor executor, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        Preconditions.checkNotNull(this.mCameraCaptureSessionCompat, "Need to call openCaptureSession before using this API.");
        return this.mCameraCaptureSessionCompat.setSingleRepeatingRequest(request, executor, listener);
    }

    public int setRepeatingBurstRequests(List<CaptureRequest> requests, Executor executor, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        Preconditions.checkNotNull(this.mCameraCaptureSessionCompat, "Need to call openCaptureSession before using this API.");
        return this.mCameraCaptureSessionCompat.setRepeatingBurstRequests(requests, executor, listener);
    }

    public void stopRepeating() throws CameraAccessException {
        Preconditions.checkNotNull(this.mCameraCaptureSessionCompat, "Need to call openCaptureSession before using this API.");
        this.mCameraCaptureSessionCompat.toCameraCaptureSession().stopRepeating();
    }

    public void abortCaptures() throws CameraAccessException {
        Preconditions.checkNotNull(this.mCameraCaptureSessionCompat, "Need to call openCaptureSession before using this API.");
        this.mCameraCaptureSessionCompat.toCameraCaptureSession().abortCaptures();
    }

    public void close() {
        Preconditions.checkNotNull(this.mCameraCaptureSessionCompat, "Need to call openCaptureSession before using this API.");
        this.mCaptureSessionRepository.onCaptureSessionClosing(this);
        this.mCameraCaptureSessionCompat.toCameraCaptureSession().close();
        getExecutor().execute(new SynchronizedCaptureSessionBaseImpl$$ExternalSyntheticLambda2(this));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$close$2$androidx-camera-camera2-internal-SynchronizedCaptureSessionBaseImpl  reason: not valid java name */
    public /* synthetic */ void m80lambda$close$2$androidxcameracamera2internalSynchronizedCaptureSessionBaseImpl() {
        onSessionFinished(this);
    }

    public void onReady(SynchronizedCaptureSession session) {
        Objects.requireNonNull(this.mCaptureSessionStateCallback);
        this.mCaptureSessionStateCallback.onReady(session);
    }

    public void onActive(SynchronizedCaptureSession session) {
        Objects.requireNonNull(this.mCaptureSessionStateCallback);
        this.mCaptureSessionStateCallback.onActive(session);
    }

    public void onCaptureQueueEmpty(SynchronizedCaptureSession session) {
        Objects.requireNonNull(this.mCaptureSessionStateCallback);
        this.mCaptureSessionStateCallback.onCaptureQueueEmpty(session);
    }

    public void onSurfacePrepared(SynchronizedCaptureSession session, Surface surface) {
        Objects.requireNonNull(this.mCaptureSessionStateCallback);
        this.mCaptureSessionStateCallback.onSurfacePrepared(session, surface);
    }

    public void onConfigured(SynchronizedCaptureSession session) {
        Objects.requireNonNull(this.mCaptureSessionStateCallback);
        this.mCaptureSessionRepository.onCaptureSessionCreated(this);
        this.mCaptureSessionStateCallback.onConfigured(session);
    }

    public void onConfigureFailed(SynchronizedCaptureSession session) {
        Objects.requireNonNull(this.mCaptureSessionStateCallback);
        finishClose();
        this.mCaptureSessionRepository.onCaptureSessionConfigureFail(this);
        this.mCaptureSessionStateCallback.onConfigureFailed(session);
    }

    public void onClosed(SynchronizedCaptureSession session) {
        ListenableFuture<Void> openFuture = null;
        synchronized (this.mLock) {
            if (!this.mClosed) {
                this.mClosed = true;
                Preconditions.checkNotNull(this.mOpenCaptureSessionFuture, "Need to call openCaptureSession before using this API.");
                openFuture = this.mOpenCaptureSessionFuture;
            }
        }
        finishClose();
        if (openFuture != null) {
            openFuture.addListener(new SynchronizedCaptureSessionBaseImpl$$ExternalSyntheticLambda3(this, session), CameraXExecutors.directExecutor());
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onClosed$3$androidx-camera-camera2-internal-SynchronizedCaptureSessionBaseImpl  reason: not valid java name */
    public /* synthetic */ void m81lambda$onClosed$3$androidxcameracamera2internalSynchronizedCaptureSessionBaseImpl(SynchronizedCaptureSession session) {
        this.mCaptureSessionRepository.onCaptureSessionClosed(this);
        onSessionFinished(session);
        Objects.requireNonNull(this.mCaptureSessionStateCallback);
        this.mCaptureSessionStateCallback.onClosed(session);
    }

    /* access modifiers changed from: package-private */
    public void onSessionFinished(SynchronizedCaptureSession session) {
        ListenableFuture<Void> openFuture = null;
        synchronized (this.mLock) {
            if (!this.mSessionFinished) {
                this.mSessionFinished = true;
                Preconditions.checkNotNull(this.mOpenCaptureSessionFuture, "Need to call openCaptureSession before using this API.");
                openFuture = this.mOpenCaptureSessionFuture;
            }
        }
        if (openFuture != null) {
            openFuture.addListener(new SynchronizedCaptureSessionBaseImpl$$ExternalSyntheticLambda4(this, session), CameraXExecutors.directExecutor());
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onSessionFinished$4$androidx-camera-camera2-internal-SynchronizedCaptureSessionBaseImpl  reason: not valid java name */
    public /* synthetic */ void m82lambda$onSessionFinished$4$androidxcameracamera2internalSynchronizedCaptureSessionBaseImpl(SynchronizedCaptureSession session) {
        Objects.requireNonNull(this.mCaptureSessionStateCallback);
        this.mCaptureSessionStateCallback.onSessionFinished(session);
    }

    /* access modifiers changed from: package-private */
    public void holdDeferrableSurfaces(List<DeferrableSurface> deferrableSurfaces) throws DeferrableSurface.SurfaceClosedException {
        synchronized (this.mLock) {
            releaseDeferrableSurfaces();
            DeferrableSurfaces.incrementAll(deferrableSurfaces);
            this.mHeldDeferrableSurfaces = deferrableSurfaces;
        }
    }

    /* access modifiers changed from: package-private */
    public void releaseDeferrableSurfaces() {
        synchronized (this.mLock) {
            List<DeferrableSurface> list = this.mHeldDeferrableSurfaces;
            if (list != null) {
                DeferrableSurfaces.decrementAll(list);
                this.mHeldDeferrableSurfaces = null;
            }
        }
    }

    public void finishClose() {
        releaseDeferrableSurfaces();
    }

    private static class Api23Impl {
        private Api23Impl() {
        }

        static Surface getInputSurface(CameraCaptureSession cameraCaptureSession) {
            return cameraCaptureSession.getInputSurface();
        }
    }
}

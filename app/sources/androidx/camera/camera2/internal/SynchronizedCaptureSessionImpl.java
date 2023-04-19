package androidx.camera.camera2.internal;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.view.Surface;
import androidx.camera.camera2.internal.compat.params.SessionConfigurationCompat;
import androidx.camera.camera2.internal.compat.workaround.ForceCloseCaptureSession;
import androidx.camera.camera2.internal.compat.workaround.ForceCloseDeferrableSurface;
import androidx.camera.camera2.internal.compat.workaround.WaitForRepeatingRequestStart;
import androidx.camera.core.Logger;
import androidx.camera.core.impl.DeferrableSurface;
import androidx.camera.core.impl.Quirks;
import androidx.camera.core.impl.utils.futures.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;

class SynchronizedCaptureSessionImpl extends SynchronizedCaptureSessionBaseImpl {
    private static final String TAG = "SyncCaptureSessionImpl";
    private final ForceCloseDeferrableSurface mCloseSurfaceQuirk;
    private List<DeferrableSurface> mDeferrableSurfaces;
    private final ForceCloseCaptureSession mForceCloseSessionQuirk;
    private final Object mObjectLock = new Object();
    ListenableFuture<Void> mOpeningCaptureSession;
    private final WaitForRepeatingRequestStart mWaitForOtherSessionCompleteQuirk;

    SynchronizedCaptureSessionImpl(Quirks cameraQuirks, Quirks deviceQuirks, CaptureSessionRepository repository, Executor executor, ScheduledExecutorService scheduledExecutorService, Handler compatHandler) {
        super(repository, executor, scheduledExecutorService, compatHandler);
        this.mCloseSurfaceQuirk = new ForceCloseDeferrableSurface(cameraQuirks, deviceQuirks);
        this.mWaitForOtherSessionCompleteQuirk = new WaitForRepeatingRequestStart(cameraQuirks);
        this.mForceCloseSessionQuirk = new ForceCloseCaptureSession(deviceQuirks);
    }

    public ListenableFuture<Void> openCaptureSession(CameraDevice cameraDevice, SessionConfigurationCompat sessionConfigurationCompat, List<DeferrableSurface> deferrableSurfaces) {
        ListenableFuture<Void> nonCancellationPropagating;
        synchronized (this.mObjectLock) {
            ListenableFuture<Void> openCaptureSession = this.mWaitForOtherSessionCompleteQuirk.openCaptureSession(cameraDevice, sessionConfigurationCompat, deferrableSurfaces, this.mCaptureSessionRepository.getClosingCaptureSession(), new SynchronizedCaptureSessionImpl$$ExternalSyntheticLambda1(this));
            this.mOpeningCaptureSession = openCaptureSession;
            nonCancellationPropagating = Futures.nonCancellationPropagating(openCaptureSession);
        }
        return nonCancellationPropagating;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$openCaptureSession$0$androidx-camera-camera2-internal-SynchronizedCaptureSessionImpl  reason: not valid java name */
    public /* synthetic */ ListenableFuture m87lambda$openCaptureSession$0$androidxcameracamera2internalSynchronizedCaptureSessionImpl(CameraDevice x$0, SessionConfigurationCompat x$1, List x$2) {
        return super.openCaptureSession(x$0, x$1, x$2);
    }

    public ListenableFuture<Void> getOpeningBlocker() {
        return this.mWaitForOtherSessionCompleteQuirk.getStartStreamFuture();
    }

    public ListenableFuture<List<Surface>> startWithDeferrableSurface(List<DeferrableSurface> deferrableSurfaces, long timeout) {
        ListenableFuture<List<Surface>> startWithDeferrableSurface;
        synchronized (this.mObjectLock) {
            this.mDeferrableSurfaces = deferrableSurfaces;
            startWithDeferrableSurface = super.startWithDeferrableSurface(deferrableSurfaces, timeout);
        }
        return startWithDeferrableSurface;
    }

    public boolean stop() {
        boolean stop;
        synchronized (this.mObjectLock) {
            if (isCameraCaptureSessionOpen()) {
                this.mCloseSurfaceQuirk.onSessionEnd(this.mDeferrableSurfaces);
            } else {
                ListenableFuture<Void> listenableFuture = this.mOpeningCaptureSession;
                if (listenableFuture != null) {
                    listenableFuture.cancel(true);
                }
            }
            stop = super.stop();
        }
        return stop;
    }

    public int setSingleRepeatingRequest(CaptureRequest request, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        return this.mWaitForOtherSessionCompleteQuirk.setSingleRepeatingRequest(request, listener, new SynchronizedCaptureSessionImpl$$ExternalSyntheticLambda2(this));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$setSingleRepeatingRequest$1$androidx-camera-camera2-internal-SynchronizedCaptureSessionImpl  reason: not valid java name */
    public /* synthetic */ int m88lambda$setSingleRepeatingRequest$1$androidxcameracamera2internalSynchronizedCaptureSessionImpl(CaptureRequest x$0, CameraCaptureSession.CaptureCallback x$1) throws CameraAccessException {
        return super.setSingleRepeatingRequest(x$0, x$1);
    }

    public void onConfigured(SynchronizedCaptureSession session) {
        debugLog("Session onConfigured()");
        this.mForceCloseSessionQuirk.onSessionConfigured(session, this.mCaptureSessionRepository.getCreatingCaptureSessions(), this.mCaptureSessionRepository.getCaptureSessions(), new SynchronizedCaptureSessionImpl$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onConfigured$2$androidx-camera-camera2-internal-SynchronizedCaptureSessionImpl  reason: not valid java name */
    public /* synthetic */ void m86lambda$onConfigured$2$androidxcameracamera2internalSynchronizedCaptureSessionImpl(SynchronizedCaptureSession x$0) {
        super.onConfigured(x$0);
    }

    public void close() {
        debugLog("Session call close()");
        this.mWaitForOtherSessionCompleteQuirk.onSessionEnd();
        this.mWaitForOtherSessionCompleteQuirk.getStartStreamFuture().addListener(new SynchronizedCaptureSessionImpl$$ExternalSyntheticLambda3(this), getExecutor());
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$close$3$androidx-camera-camera2-internal-SynchronizedCaptureSessionImpl  reason: not valid java name */
    public /* synthetic */ void m85lambda$close$3$androidxcameracamera2internalSynchronizedCaptureSessionImpl() {
        debugLog("Session call super.close()");
        super.close();
    }

    public void onClosed(SynchronizedCaptureSession session) {
        synchronized (this.mObjectLock) {
            this.mCloseSurfaceQuirk.onSessionEnd(this.mDeferrableSurfaces);
        }
        debugLog("onClosed()");
        super.onClosed(session);
    }

    /* access modifiers changed from: package-private */
    public void debugLog(String message) {
        Logger.d(TAG, "[" + this + "] " + message);
    }
}

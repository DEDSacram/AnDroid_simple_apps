package androidx.camera.camera2.internal.compat;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Build;
import android.os.Handler;
import android.view.Surface;
import androidx.camera.camera2.internal.compat.ApiCompat;
import androidx.camera.core.impl.utils.MainThreadAsyncHandler;
import java.util.List;
import java.util.concurrent.Executor;

public final class CameraCaptureSessionCompat {
    private final CameraCaptureSessionCompatImpl mImpl;

    interface CameraCaptureSessionCompatImpl {
        int captureBurstRequests(List<CaptureRequest> list, Executor executor, CameraCaptureSession.CaptureCallback captureCallback) throws CameraAccessException;

        int captureSingleRequest(CaptureRequest captureRequest, Executor executor, CameraCaptureSession.CaptureCallback captureCallback) throws CameraAccessException;

        int setRepeatingBurstRequests(List<CaptureRequest> list, Executor executor, CameraCaptureSession.CaptureCallback captureCallback) throws CameraAccessException;

        int setSingleRepeatingRequest(CaptureRequest captureRequest, Executor executor, CameraCaptureSession.CaptureCallback captureCallback) throws CameraAccessException;

        CameraCaptureSession unwrap();
    }

    private CameraCaptureSessionCompat(CameraCaptureSession captureSession, Handler compatHandler) {
        if (Build.VERSION.SDK_INT >= 28) {
            this.mImpl = new CameraCaptureSessionCompatApi28Impl(captureSession);
        } else {
            this.mImpl = CameraCaptureSessionCompatBaseImpl.create(captureSession, compatHandler);
        }
    }

    public static CameraCaptureSessionCompat toCameraCaptureSessionCompat(CameraCaptureSession captureSession) {
        return toCameraCaptureSessionCompat(captureSession, MainThreadAsyncHandler.getInstance());
    }

    public static CameraCaptureSessionCompat toCameraCaptureSessionCompat(CameraCaptureSession captureSession, Handler compatHandler) {
        return new CameraCaptureSessionCompat(captureSession, compatHandler);
    }

    public CameraCaptureSession toCameraCaptureSession() {
        return this.mImpl.unwrap();
    }

    public int captureBurstRequests(List<CaptureRequest> requests, Executor executor, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        return this.mImpl.captureBurstRequests(requests, executor, listener);
    }

    public int captureSingleRequest(CaptureRequest request, Executor executor, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        return this.mImpl.captureSingleRequest(request, executor, listener);
    }

    public int setRepeatingBurstRequests(List<CaptureRequest> requests, Executor executor, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        return this.mImpl.setRepeatingBurstRequests(requests, executor, listener);
    }

    public int setSingleRepeatingRequest(CaptureRequest request, Executor executor, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        return this.mImpl.setSingleRepeatingRequest(request, executor, listener);
    }

    static final class CaptureCallbackExecutorWrapper extends CameraCaptureSession.CaptureCallback {
        private final Executor mExecutor;
        final CameraCaptureSession.CaptureCallback mWrappedCallback;

        CaptureCallbackExecutorWrapper(Executor executor, CameraCaptureSession.CaptureCallback wrappedCallback) {
            this.mExecutor = executor;
            this.mWrappedCallback = wrappedCallback;
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onCaptureStarted$0$androidx-camera-camera2-internal-compat-CameraCaptureSessionCompat$CaptureCallbackExecutorWrapper  reason: not valid java name */
        public /* synthetic */ void m103lambda$onCaptureStarted$0$androidxcameracamera2internalcompatCameraCaptureSessionCompat$CaptureCallbackExecutorWrapper(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
            this.mWrappedCallback.onCaptureStarted(session, request, timestamp, frameNumber);
        }

        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
            this.mExecutor.execute(new CameraCaptureSessionCompat$CaptureCallbackExecutorWrapper$$ExternalSyntheticLambda2(this, session, request, timestamp, frameNumber));
        }

        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
            this.mExecutor.execute(new CameraCaptureSessionCompat$CaptureCallbackExecutorWrapper$$ExternalSyntheticLambda4(this, session, request, partialResult));
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onCaptureProgressed$1$androidx-camera-camera2-internal-compat-CameraCaptureSessionCompat$CaptureCallbackExecutorWrapper  reason: not valid java name */
        public /* synthetic */ void m100lambda$onCaptureProgressed$1$androidxcameracamera2internalcompatCameraCaptureSessionCompat$CaptureCallbackExecutorWrapper(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
            this.mWrappedCallback.onCaptureProgressed(session, request, partialResult);
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onCaptureCompleted$2$androidx-camera-camera2-internal-compat-CameraCaptureSessionCompat$CaptureCallbackExecutorWrapper  reason: not valid java name */
        public /* synthetic */ void m98lambda$onCaptureCompleted$2$androidxcameracamera2internalcompatCameraCaptureSessionCompat$CaptureCallbackExecutorWrapper(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            this.mWrappedCallback.onCaptureCompleted(session, request, result);
        }

        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            this.mExecutor.execute(new CameraCaptureSessionCompat$CaptureCallbackExecutorWrapper$$ExternalSyntheticLambda5(this, session, request, result));
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onCaptureFailed$3$androidx-camera-camera2-internal-compat-CameraCaptureSessionCompat$CaptureCallbackExecutorWrapper  reason: not valid java name */
        public /* synthetic */ void m99lambda$onCaptureFailed$3$androidxcameracamera2internalcompatCameraCaptureSessionCompat$CaptureCallbackExecutorWrapper(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
            this.mWrappedCallback.onCaptureFailed(session, request, failure);
        }

        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
            this.mExecutor.execute(new CameraCaptureSessionCompat$CaptureCallbackExecutorWrapper$$ExternalSyntheticLambda3(this, session, request, failure));
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onCaptureSequenceCompleted$4$androidx-camera-camera2-internal-compat-CameraCaptureSessionCompat$CaptureCallbackExecutorWrapper  reason: not valid java name */
        public /* synthetic */ void m102lambda$onCaptureSequenceCompleted$4$androidxcameracamera2internalcompatCameraCaptureSessionCompat$CaptureCallbackExecutorWrapper(CameraCaptureSession session, int sequenceId, long frameNumber) {
            this.mWrappedCallback.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
        }

        public void onCaptureSequenceCompleted(CameraCaptureSession session, int sequenceId, long frameNumber) {
            this.mExecutor.execute(new CameraCaptureSessionCompat$CaptureCallbackExecutorWrapper$$ExternalSyntheticLambda1(this, session, sequenceId, frameNumber));
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onCaptureSequenceAborted$5$androidx-camera-camera2-internal-compat-CameraCaptureSessionCompat$CaptureCallbackExecutorWrapper  reason: not valid java name */
        public /* synthetic */ void m101lambda$onCaptureSequenceAborted$5$androidxcameracamera2internalcompatCameraCaptureSessionCompat$CaptureCallbackExecutorWrapper(CameraCaptureSession session, int sequenceId) {
            this.mWrappedCallback.onCaptureSequenceAborted(session, sequenceId);
        }

        public void onCaptureSequenceAborted(CameraCaptureSession session, int sequenceId) {
            this.mExecutor.execute(new CameraCaptureSessionCompat$CaptureCallbackExecutorWrapper$$ExternalSyntheticLambda0(this, session, sequenceId));
        }

        public void onCaptureBufferLost(CameraCaptureSession session, CaptureRequest request, Surface target, long frameNumber) {
            this.mExecutor.execute(new CameraCaptureSessionCompat$CaptureCallbackExecutorWrapper$$ExternalSyntheticLambda6(this, session, request, target, frameNumber));
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onCaptureBufferLost$6$androidx-camera-camera2-internal-compat-CameraCaptureSessionCompat$CaptureCallbackExecutorWrapper  reason: not valid java name */
        public /* synthetic */ void m97lambda$onCaptureBufferLost$6$androidxcameracamera2internalcompatCameraCaptureSessionCompat$CaptureCallbackExecutorWrapper(CameraCaptureSession session, CaptureRequest request, Surface target, long frameNumber) {
            ApiCompat.Api24Impl.onCaptureBufferLost(this.mWrappedCallback, session, request, target, frameNumber);
        }
    }

    static final class StateCallbackExecutorWrapper extends CameraCaptureSession.StateCallback {
        private final Executor mExecutor;
        final CameraCaptureSession.StateCallback mWrappedCallback;

        StateCallbackExecutorWrapper(Executor executor, CameraCaptureSession.StateCallback wrappedCallback) {
            this.mExecutor = executor;
            this.mWrappedCallback = wrappedCallback;
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onConfigured$0$androidx-camera-camera2-internal-compat-CameraCaptureSessionCompat$StateCallbackExecutorWrapper  reason: not valid java name */
        public /* synthetic */ void m108lambda$onConfigured$0$androidxcameracamera2internalcompatCameraCaptureSessionCompat$StateCallbackExecutorWrapper(CameraCaptureSession session) {
            this.mWrappedCallback.onConfigured(session);
        }

        public void onConfigured(CameraCaptureSession session) {
            this.mExecutor.execute(new CameraCaptureSessionCompat$StateCallbackExecutorWrapper$$ExternalSyntheticLambda4(this, session));
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onConfigureFailed$1$androidx-camera-camera2-internal-compat-CameraCaptureSessionCompat$StateCallbackExecutorWrapper  reason: not valid java name */
        public /* synthetic */ void m107lambda$onConfigureFailed$1$androidxcameracamera2internalcompatCameraCaptureSessionCompat$StateCallbackExecutorWrapper(CameraCaptureSession session) {
            this.mWrappedCallback.onConfigureFailed(session);
        }

        public void onConfigureFailed(CameraCaptureSession session) {
            this.mExecutor.execute(new CameraCaptureSessionCompat$StateCallbackExecutorWrapper$$ExternalSyntheticLambda3(this, session));
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onReady$2$androidx-camera-camera2-internal-compat-CameraCaptureSessionCompat$StateCallbackExecutorWrapper  reason: not valid java name */
        public /* synthetic */ void m109lambda$onReady$2$androidxcameracamera2internalcompatCameraCaptureSessionCompat$StateCallbackExecutorWrapper(CameraCaptureSession session) {
            this.mWrappedCallback.onReady(session);
        }

        public void onReady(CameraCaptureSession session) {
            this.mExecutor.execute(new CameraCaptureSessionCompat$StateCallbackExecutorWrapper$$ExternalSyntheticLambda5(this, session));
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onActive$3$androidx-camera-camera2-internal-compat-CameraCaptureSessionCompat$StateCallbackExecutorWrapper  reason: not valid java name */
        public /* synthetic */ void m104lambda$onActive$3$androidxcameracamera2internalcompatCameraCaptureSessionCompat$StateCallbackExecutorWrapper(CameraCaptureSession session) {
            this.mWrappedCallback.onActive(session);
        }

        public void onActive(CameraCaptureSession session) {
            this.mExecutor.execute(new CameraCaptureSessionCompat$StateCallbackExecutorWrapper$$ExternalSyntheticLambda0(this, session));
        }

        public void onCaptureQueueEmpty(CameraCaptureSession session) {
            this.mExecutor.execute(new CameraCaptureSessionCompat$StateCallbackExecutorWrapper$$ExternalSyntheticLambda1(this, session));
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onCaptureQueueEmpty$4$androidx-camera-camera2-internal-compat-CameraCaptureSessionCompat$StateCallbackExecutorWrapper  reason: not valid java name */
        public /* synthetic */ void m105lambda$onCaptureQueueEmpty$4$androidxcameracamera2internalcompatCameraCaptureSessionCompat$StateCallbackExecutorWrapper(CameraCaptureSession session) {
            ApiCompat.Api26Impl.onCaptureQueueEmpty(this.mWrappedCallback, session);
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onClosed$5$androidx-camera-camera2-internal-compat-CameraCaptureSessionCompat$StateCallbackExecutorWrapper  reason: not valid java name */
        public /* synthetic */ void m106lambda$onClosed$5$androidxcameracamera2internalcompatCameraCaptureSessionCompat$StateCallbackExecutorWrapper(CameraCaptureSession session) {
            this.mWrappedCallback.onClosed(session);
        }

        public void onClosed(CameraCaptureSession session) {
            this.mExecutor.execute(new CameraCaptureSessionCompat$StateCallbackExecutorWrapper$$ExternalSyntheticLambda2(this, session));
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onSurfacePrepared$6$androidx-camera-camera2-internal-compat-CameraCaptureSessionCompat$StateCallbackExecutorWrapper  reason: not valid java name */
        public /* synthetic */ void m110lambda$onSurfacePrepared$6$androidxcameracamera2internalcompatCameraCaptureSessionCompat$StateCallbackExecutorWrapper(CameraCaptureSession session, Surface surface) {
            ApiCompat.Api23Impl.onSurfacePrepared(this.mWrappedCallback, session, surface);
        }

        public void onSurfacePrepared(CameraCaptureSession session, Surface surface) {
            this.mExecutor.execute(new CameraCaptureSessionCompat$StateCallbackExecutorWrapper$$ExternalSyntheticLambda6(this, session, surface));
        }
    }
}

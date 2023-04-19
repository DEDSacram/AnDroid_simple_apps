package androidx.camera.view;

import androidx.camera.core.CameraInfo;
import androidx.camera.core.Logger;
import androidx.camera.core.impl.CameraCaptureCallback;
import androidx.camera.core.impl.CameraCaptureResult;
import androidx.camera.core.impl.CameraInfoInternal;
import androidx.camera.core.impl.CameraInternal;
import androidx.camera.core.impl.Observable;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.impl.utils.futures.FutureCallback;
import androidx.camera.core.impl.utils.futures.FutureChain;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.camera.view.PreviewView;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.lifecycle.MutableLiveData;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;

final class PreviewStreamStateObserver implements Observable.Observer<CameraInternal.State> {
    private static final String TAG = "StreamStateObserver";
    private final CameraInfoInternal mCameraInfoInternal;
    ListenableFuture<Void> mFlowFuture;
    private boolean mHasStartedPreviewStreamFlow = false;
    private PreviewView.StreamState mPreviewStreamState;
    private final MutableLiveData<PreviewView.StreamState> mPreviewStreamStateLiveData;
    private final PreviewViewImplementation mPreviewViewImplementation;

    PreviewStreamStateObserver(CameraInfoInternal cameraInfoInternal, MutableLiveData<PreviewView.StreamState> previewStreamLiveData, PreviewViewImplementation implementation) {
        this.mCameraInfoInternal = cameraInfoInternal;
        this.mPreviewStreamStateLiveData = previewStreamLiveData;
        this.mPreviewViewImplementation = implementation;
        synchronized (this) {
            this.mPreviewStreamState = previewStreamLiveData.getValue();
        }
    }

    public void onNewData(CameraInternal.State value) {
        if (value == CameraInternal.State.CLOSING || value == CameraInternal.State.CLOSED || value == CameraInternal.State.RELEASING || value == CameraInternal.State.RELEASED) {
            updatePreviewStreamState(PreviewView.StreamState.IDLE);
            if (this.mHasStartedPreviewStreamFlow) {
                this.mHasStartedPreviewStreamFlow = false;
                cancelFlow();
            }
        } else if ((value == CameraInternal.State.OPENING || value == CameraInternal.State.OPEN || value == CameraInternal.State.PENDING_OPEN) && !this.mHasStartedPreviewStreamFlow) {
            startPreviewStreamStateFlow(this.mCameraInfoInternal);
            this.mHasStartedPreviewStreamFlow = true;
        }
    }

    public void onError(Throwable t) {
        clear();
        updatePreviewStreamState(PreviewView.StreamState.IDLE);
    }

    /* access modifiers changed from: package-private */
    public void clear() {
        cancelFlow();
    }

    private void cancelFlow() {
        ListenableFuture<Void> listenableFuture = this.mFlowFuture;
        if (listenableFuture != null) {
            listenableFuture.cancel(false);
            this.mFlowFuture = null;
        }
    }

    private void startPreviewStreamStateFlow(final CameraInfo cameraInfo) {
        updatePreviewStreamState(PreviewView.StreamState.IDLE);
        final List<CameraCaptureCallback> callbacksToClear = new ArrayList<>();
        FutureChain<T> transform = FutureChain.from(waitForCaptureResult(cameraInfo, callbacksToClear)).transformAsync(new PreviewStreamStateObserver$$ExternalSyntheticLambda1(this), CameraXExecutors.directExecutor()).transform(new PreviewStreamStateObserver$$ExternalSyntheticLambda0(this), CameraXExecutors.directExecutor());
        this.mFlowFuture = transform;
        Futures.addCallback(transform, new FutureCallback<Void>() {
            public void onSuccess(Void result) {
                PreviewStreamStateObserver.this.mFlowFuture = null;
            }

            public void onFailure(Throwable t) {
                PreviewStreamStateObserver.this.mFlowFuture = null;
                if (!callbacksToClear.isEmpty()) {
                    for (CameraCaptureCallback callback : callbacksToClear) {
                        ((CameraInfoInternal) cameraInfo).removeSessionCaptureCallback(callback);
                    }
                    callbacksToClear.clear();
                }
            }
        }, CameraXExecutors.directExecutor());
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$startPreviewStreamStateFlow$0$androidx-camera-view-PreviewStreamStateObserver  reason: not valid java name */
    public /* synthetic */ ListenableFuture m199lambda$startPreviewStreamStateFlow$0$androidxcameraviewPreviewStreamStateObserver(Void v) throws Exception {
        return this.mPreviewViewImplementation.waitForNextFrame();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$startPreviewStreamStateFlow$1$androidx-camera-view-PreviewStreamStateObserver  reason: not valid java name */
    public /* synthetic */ Void m200lambda$startPreviewStreamStateFlow$1$androidxcameraviewPreviewStreamStateObserver(Void v) {
        updatePreviewStreamState(PreviewView.StreamState.STREAMING);
        return null;
    }

    /* access modifiers changed from: package-private */
    public void updatePreviewStreamState(PreviewView.StreamState streamState) {
        synchronized (this) {
            if (!this.mPreviewStreamState.equals(streamState)) {
                this.mPreviewStreamState = streamState;
                Logger.d(TAG, "Update Preview stream state to " + streamState);
                this.mPreviewStreamStateLiveData.postValue(streamState);
            }
        }
    }

    private ListenableFuture<Void> waitForCaptureResult(CameraInfo cameraInfo, List<CameraCaptureCallback> callbacksToClear) {
        return CallbackToFutureAdapter.getFuture(new PreviewStreamStateObserver$$ExternalSyntheticLambda2(this, cameraInfo, callbacksToClear));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$waitForCaptureResult$2$androidx-camera-view-PreviewStreamStateObserver  reason: not valid java name */
    public /* synthetic */ Object m201lambda$waitForCaptureResult$2$androidxcameraviewPreviewStreamStateObserver(final CameraInfo cameraInfo, List callbacksToClear, final CallbackToFutureAdapter.Completer completer) throws Exception {
        CameraCaptureCallback callback = new CameraCaptureCallback() {
            public void onCaptureCompleted(CameraCaptureResult result) {
                completer.set(null);
                ((CameraInfoInternal) cameraInfo).removeSessionCaptureCallback(this);
            }
        };
        callbacksToClear.add(callback);
        ((CameraInfoInternal) cameraInfo).addSessionCaptureCallback(CameraXExecutors.directExecutor(), callback);
        return "waitForCaptureResult";
    }
}

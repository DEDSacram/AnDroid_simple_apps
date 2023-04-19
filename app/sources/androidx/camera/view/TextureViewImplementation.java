package androidx.camera.view;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import androidx.camera.core.Logger;
import androidx.camera.core.SurfaceRequest;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.impl.utils.futures.FutureCallback;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.camera.view.PreviewViewImplementation;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.content.ContextCompat;
import androidx.core.util.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

final class TextureViewImplementation extends PreviewViewImplementation {
    private static final String TAG = "TextureViewImpl";
    SurfaceTexture mDetachedSurfaceTexture;
    boolean mIsSurfaceTextureDetachedFromView = false;
    AtomicReference<CallbackToFutureAdapter.Completer<Void>> mNextFrameCompleter = new AtomicReference<>();
    PreviewViewImplementation.OnSurfaceNotInUseListener mOnSurfaceNotInUseListener;
    ListenableFuture<SurfaceRequest.Result> mSurfaceReleaseFuture;
    SurfaceRequest mSurfaceRequest;
    SurfaceTexture mSurfaceTexture;
    TextureView mTextureView;

    TextureViewImplementation(FrameLayout parent, PreviewTransformation previewTransform) {
        super(parent, previewTransform);
    }

    /* access modifiers changed from: package-private */
    public View getPreview() {
        return this.mTextureView;
    }

    /* access modifiers changed from: package-private */
    public void onAttachedToWindow() {
        reattachSurfaceTexture();
    }

    /* access modifiers changed from: package-private */
    public void onDetachedFromWindow() {
        this.mIsSurfaceTextureDetachedFromView = true;
    }

    /* access modifiers changed from: package-private */
    public void onSurfaceRequested(SurfaceRequest surfaceRequest, PreviewViewImplementation.OnSurfaceNotInUseListener onSurfaceNotInUseListener) {
        this.mResolution = surfaceRequest.getResolution();
        this.mOnSurfaceNotInUseListener = onSurfaceNotInUseListener;
        initializePreview();
        SurfaceRequest surfaceRequest2 = this.mSurfaceRequest;
        if (surfaceRequest2 != null) {
            surfaceRequest2.willNotProvideSurface();
        }
        this.mSurfaceRequest = surfaceRequest;
        surfaceRequest.addRequestCancellationListener(ContextCompat.getMainExecutor(this.mTextureView.getContext()), new TextureViewImplementation$$ExternalSyntheticLambda4(this, surfaceRequest));
        tryToProvidePreviewSurface();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onSurfaceRequested$0$androidx-camera-view-TextureViewImplementation  reason: not valid java name */
    public /* synthetic */ void m209lambda$onSurfaceRequested$0$androidxcameraviewTextureViewImplementation(SurfaceRequest surfaceRequest) {
        SurfaceRequest surfaceRequest2 = this.mSurfaceRequest;
        if (surfaceRequest2 != null && surfaceRequest2 == surfaceRequest) {
            this.mSurfaceRequest = null;
            this.mSurfaceReleaseFuture = null;
        }
        notifySurfaceNotInUse();
    }

    private void notifySurfaceNotInUse() {
        PreviewViewImplementation.OnSurfaceNotInUseListener onSurfaceNotInUseListener = this.mOnSurfaceNotInUseListener;
        if (onSurfaceNotInUseListener != null) {
            onSurfaceNotInUseListener.onSurfaceNotInUse();
            this.mOnSurfaceNotInUseListener = null;
        }
    }

    public void initializePreview() {
        Preconditions.checkNotNull(this.mParent);
        Preconditions.checkNotNull(this.mResolution);
        TextureView textureView = new TextureView(this.mParent.getContext());
        this.mTextureView = textureView;
        textureView.setLayoutParams(new FrameLayout.LayoutParams(this.mResolution.getWidth(), this.mResolution.getHeight()));
        this.mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                Logger.d(TextureViewImplementation.TAG, "SurfaceTexture available. Size: " + width + "x" + height);
                TextureViewImplementation.this.mSurfaceTexture = surfaceTexture;
                if (TextureViewImplementation.this.mSurfaceReleaseFuture != null) {
                    Preconditions.checkNotNull(TextureViewImplementation.this.mSurfaceRequest);
                    Logger.d(TextureViewImplementation.TAG, "Surface invalidated " + TextureViewImplementation.this.mSurfaceRequest);
                    TextureViewImplementation.this.mSurfaceRequest.getDeferrableSurface().close();
                    return;
                }
                TextureViewImplementation.this.tryToProvidePreviewSurface();
            }

            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
                Logger.d(TextureViewImplementation.TAG, "SurfaceTexture size changed: " + width + "x" + height);
            }

            public boolean onSurfaceTextureDestroyed(final SurfaceTexture surfaceTexture) {
                TextureViewImplementation.this.mSurfaceTexture = null;
                if (TextureViewImplementation.this.mSurfaceReleaseFuture != null) {
                    Futures.addCallback(TextureViewImplementation.this.mSurfaceReleaseFuture, new FutureCallback<SurfaceRequest.Result>() {
                        public void onSuccess(SurfaceRequest.Result result) {
                            Preconditions.checkState(result.getResultCode() != 3, "Unexpected result from SurfaceRequest. Surface was provided twice.");
                            Logger.d(TextureViewImplementation.TAG, "SurfaceTexture about to manually be destroyed");
                            surfaceTexture.release();
                            if (TextureViewImplementation.this.mDetachedSurfaceTexture != null) {
                                TextureViewImplementation.this.mDetachedSurfaceTexture = null;
                            }
                        }

                        public void onFailure(Throwable t) {
                            throw new IllegalStateException("SurfaceReleaseFuture did not complete nicely.", t);
                        }
                    }, ContextCompat.getMainExecutor(TextureViewImplementation.this.mTextureView.getContext()));
                    TextureViewImplementation.this.mDetachedSurfaceTexture = surfaceTexture;
                    return false;
                }
                Logger.d(TextureViewImplementation.TAG, "SurfaceTexture about to be destroyed");
                return true;
            }

            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                CallbackToFutureAdapter.Completer<Void> completer = TextureViewImplementation.this.mNextFrameCompleter.getAndSet((Object) null);
                if (completer != null) {
                    completer.set(null);
                }
            }
        });
        this.mParent.removeAllViews();
        this.mParent.addView(this.mTextureView);
    }

    /* access modifiers changed from: package-private */
    public void tryToProvidePreviewSurface() {
        SurfaceTexture surfaceTexture;
        if (this.mResolution != null && (surfaceTexture = this.mSurfaceTexture) != null && this.mSurfaceRequest != null) {
            surfaceTexture.setDefaultBufferSize(this.mResolution.getWidth(), this.mResolution.getHeight());
            Surface surface = new Surface(this.mSurfaceTexture);
            SurfaceRequest surfaceRequest = this.mSurfaceRequest;
            ListenableFuture<SurfaceRequest.Result> surfaceReleaseFuture = CallbackToFutureAdapter.getFuture(new TextureViewImplementation$$ExternalSyntheticLambda1(this, surface));
            this.mSurfaceReleaseFuture = surfaceReleaseFuture;
            surfaceReleaseFuture.addListener(new TextureViewImplementation$$ExternalSyntheticLambda3(this, surface, surfaceReleaseFuture, surfaceRequest), ContextCompat.getMainExecutor(this.mTextureView.getContext()));
            onSurfaceProvided();
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$tryToProvidePreviewSurface$1$androidx-camera-view-TextureViewImplementation  reason: not valid java name */
    public /* synthetic */ Object m210lambda$tryToProvidePreviewSurface$1$androidxcameraviewTextureViewImplementation(Surface surface, CallbackToFutureAdapter.Completer completer) throws Exception {
        Logger.d(TAG, "Surface set on Preview.");
        SurfaceRequest surfaceRequest = this.mSurfaceRequest;
        Executor directExecutor = CameraXExecutors.directExecutor();
        Objects.requireNonNull(completer);
        surfaceRequest.provideSurface(surface, directExecutor, new TextureViewImplementation$$ExternalSyntheticLambda2(completer));
        return "provideSurface[request=" + this.mSurfaceRequest + " surface=" + surface + "]";
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$tryToProvidePreviewSurface$2$androidx-camera-view-TextureViewImplementation  reason: not valid java name */
    public /* synthetic */ void m211lambda$tryToProvidePreviewSurface$2$androidxcameraviewTextureViewImplementation(Surface surface, ListenableFuture surfaceReleaseFuture, SurfaceRequest surfaceRequest) {
        Logger.d(TAG, "Safe to release surface.");
        notifySurfaceNotInUse();
        surface.release();
        if (this.mSurfaceReleaseFuture == surfaceReleaseFuture) {
            this.mSurfaceReleaseFuture = null;
        }
        if (this.mSurfaceRequest == surfaceRequest) {
            this.mSurfaceRequest = null;
        }
    }

    private void reattachSurfaceTexture() {
        SurfaceTexture surfaceTexture;
        if (this.mIsSurfaceTextureDetachedFromView && this.mDetachedSurfaceTexture != null && this.mTextureView.getSurfaceTexture() != (surfaceTexture = this.mDetachedSurfaceTexture)) {
            this.mTextureView.setSurfaceTexture(surfaceTexture);
            this.mDetachedSurfaceTexture = null;
            this.mIsSurfaceTextureDetachedFromView = false;
        }
    }

    /* access modifiers changed from: package-private */
    public ListenableFuture<Void> waitForNextFrame() {
        return CallbackToFutureAdapter.getFuture(new TextureViewImplementation$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$waitForNextFrame$3$androidx-camera-view-TextureViewImplementation  reason: not valid java name */
    public /* synthetic */ Object m212lambda$waitForNextFrame$3$androidxcameraviewTextureViewImplementation(CallbackToFutureAdapter.Completer completer) throws Exception {
        this.mNextFrameCompleter.set(completer);
        return "textureViewImpl_waitForNextFrame";
    }

    /* access modifiers changed from: package-private */
    public Bitmap getPreviewBitmap() {
        TextureView textureView = this.mTextureView;
        if (textureView == null || !textureView.isAvailable()) {
            return null;
        }
        return this.mTextureView.getBitmap();
    }
}

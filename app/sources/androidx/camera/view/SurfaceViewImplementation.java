package androidx.camera.view;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Size;
import android.view.PixelCopy;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import androidx.camera.core.Logger;
import androidx.camera.core.SurfaceRequest;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.camera.view.PreviewViewImplementation;
import androidx.core.content.ContextCompat;
import androidx.core.util.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;

final class SurfaceViewImplementation extends PreviewViewImplementation {
    private static final String TAG = "SurfaceViewImpl";
    private PreviewViewImplementation.OnSurfaceNotInUseListener mOnSurfaceNotInUseListener;
    final SurfaceRequestCallback mSurfaceRequestCallback = new SurfaceRequestCallback();
    SurfaceView mSurfaceView;

    SurfaceViewImplementation(FrameLayout parent, PreviewTransformation previewTransform) {
        super(parent, previewTransform);
    }

    /* access modifiers changed from: package-private */
    public void onSurfaceRequested(SurfaceRequest surfaceRequest, PreviewViewImplementation.OnSurfaceNotInUseListener onSurfaceNotInUseListener) {
        this.mResolution = surfaceRequest.getResolution();
        this.mOnSurfaceNotInUseListener = onSurfaceNotInUseListener;
        initializePreview();
        surfaceRequest.addRequestCancellationListener(ContextCompat.getMainExecutor(this.mSurfaceView.getContext()), new SurfaceViewImplementation$$ExternalSyntheticLambda1(this));
        this.mSurfaceView.post(new SurfaceViewImplementation$$ExternalSyntheticLambda2(this, surfaceRequest));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onSurfaceRequested$0$androidx-camera-view-SurfaceViewImplementation  reason: not valid java name */
    public /* synthetic */ void m207lambda$onSurfaceRequested$0$androidxcameraviewSurfaceViewImplementation(SurfaceRequest surfaceRequest) {
        this.mSurfaceRequestCallback.setSurfaceRequest(surfaceRequest);
    }

    /* access modifiers changed from: package-private */
    public void initializePreview() {
        Preconditions.checkNotNull(this.mParent);
        Preconditions.checkNotNull(this.mResolution);
        SurfaceView surfaceView = new SurfaceView(this.mParent.getContext());
        this.mSurfaceView = surfaceView;
        surfaceView.setLayoutParams(new FrameLayout.LayoutParams(this.mResolution.getWidth(), this.mResolution.getHeight()));
        this.mParent.removeAllViews();
        this.mParent.addView(this.mSurfaceView);
        this.mSurfaceView.getHolder().addCallback(this.mSurfaceRequestCallback);
    }

    /* access modifiers changed from: package-private */
    public View getPreview() {
        return this.mSurfaceView;
    }

    /* access modifiers changed from: package-private */
    public void onAttachedToWindow() {
    }

    /* access modifiers changed from: package-private */
    public void onDetachedFromWindow() {
    }

    /* access modifiers changed from: package-private */
    public void notifySurfaceNotInUse() {
        PreviewViewImplementation.OnSurfaceNotInUseListener onSurfaceNotInUseListener = this.mOnSurfaceNotInUseListener;
        if (onSurfaceNotInUseListener != null) {
            onSurfaceNotInUseListener.onSurfaceNotInUse();
            this.mOnSurfaceNotInUseListener = null;
        }
    }

    /* access modifiers changed from: package-private */
    public Bitmap getPreviewBitmap() {
        SurfaceView surfaceView = this.mSurfaceView;
        if (surfaceView == null || surfaceView.getHolder().getSurface() == null || !this.mSurfaceView.getHolder().getSurface().isValid()) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(this.mSurfaceView.getWidth(), this.mSurfaceView.getHeight(), Bitmap.Config.ARGB_8888);
        Api24Impl.pixelCopyRequest(this.mSurfaceView, bitmap, SurfaceViewImplementation$$ExternalSyntheticLambda0.INSTANCE, this.mSurfaceView.getHandler());
        return bitmap;
    }

    static /* synthetic */ void lambda$getPreviewBitmap$1(int copyResult) {
        if (copyResult == 0) {
            Logger.d(TAG, "PreviewView.SurfaceViewImplementation.getBitmap() succeeded");
        } else {
            Logger.e(TAG, "PreviewView.SurfaceViewImplementation.getBitmap() failed with error " + copyResult);
        }
    }

    class SurfaceRequestCallback implements SurfaceHolder.Callback {
        private Size mCurrentSurfaceSize;
        private SurfaceRequest mSurfaceRequest;
        private Size mTargetSize;
        private boolean mWasSurfaceProvided = false;

        SurfaceRequestCallback() {
        }

        /* access modifiers changed from: package-private */
        public void setSurfaceRequest(SurfaceRequest surfaceRequest) {
            cancelPreviousRequest();
            this.mSurfaceRequest = surfaceRequest;
            Size targetSize = surfaceRequest.getResolution();
            this.mTargetSize = targetSize;
            this.mWasSurfaceProvided = false;
            if (!tryToComplete()) {
                Logger.d(SurfaceViewImplementation.TAG, "Wait for new Surface creation.");
                SurfaceViewImplementation.this.mSurfaceView.getHolder().setFixedSize(targetSize.getWidth(), targetSize.getHeight());
            }
        }

        private boolean tryToComplete() {
            Surface surface = SurfaceViewImplementation.this.mSurfaceView.getHolder().getSurface();
            if (!canProvideSurface()) {
                return false;
            }
            Logger.d(SurfaceViewImplementation.TAG, "Surface set on Preview.");
            this.mSurfaceRequest.provideSurface(surface, ContextCompat.getMainExecutor(SurfaceViewImplementation.this.mSurfaceView.getContext()), new SurfaceViewImplementation$SurfaceRequestCallback$$ExternalSyntheticLambda0(this));
            this.mWasSurfaceProvided = true;
            SurfaceViewImplementation.this.onSurfaceProvided();
            return true;
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$tryToComplete$0$androidx-camera-view-SurfaceViewImplementation$SurfaceRequestCallback  reason: not valid java name */
        public /* synthetic */ void m208lambda$tryToComplete$0$androidxcameraviewSurfaceViewImplementation$SurfaceRequestCallback(SurfaceRequest.Result result) {
            Logger.d(SurfaceViewImplementation.TAG, "Safe to release surface.");
            SurfaceViewImplementation.this.notifySurfaceNotInUse();
        }

        /* JADX WARNING: Code restructure failed: missing block: B:4:0x0008, code lost:
            r0 = r2.mTargetSize;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private boolean canProvideSurface() {
            /*
                r2 = this;
                boolean r0 = r2.mWasSurfaceProvided
                if (r0 != 0) goto L_0x0016
                androidx.camera.core.SurfaceRequest r0 = r2.mSurfaceRequest
                if (r0 == 0) goto L_0x0016
                android.util.Size r0 = r2.mTargetSize
                if (r0 == 0) goto L_0x0016
                android.util.Size r1 = r2.mCurrentSurfaceSize
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0016
                r0 = 1
                goto L_0x0017
            L_0x0016:
                r0 = 0
            L_0x0017:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.camera.view.SurfaceViewImplementation.SurfaceRequestCallback.canProvideSurface():boolean");
        }

        private void cancelPreviousRequest() {
            if (this.mSurfaceRequest != null) {
                Logger.d(SurfaceViewImplementation.TAG, "Request canceled: " + this.mSurfaceRequest);
                this.mSurfaceRequest.willNotProvideSurface();
            }
        }

        private void invalidateSurface() {
            if (this.mSurfaceRequest != null) {
                Logger.d(SurfaceViewImplementation.TAG, "Surface invalidated " + this.mSurfaceRequest);
                this.mSurfaceRequest.getDeferrableSurface().close();
            }
        }

        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            Logger.d(SurfaceViewImplementation.TAG, "Surface created.");
        }

        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
            Logger.d(SurfaceViewImplementation.TAG, "Surface changed. Size: " + width + "x" + height);
            this.mCurrentSurfaceSize = new Size(width, height);
            tryToComplete();
        }

        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            Logger.d(SurfaceViewImplementation.TAG, "Surface destroyed.");
            if (this.mWasSurfaceProvided) {
                invalidateSurface();
            } else {
                cancelPreviousRequest();
            }
            this.mWasSurfaceProvided = false;
            this.mSurfaceRequest = null;
            this.mCurrentSurfaceSize = null;
            this.mTargetSize = null;
        }
    }

    /* access modifiers changed from: package-private */
    public ListenableFuture<Void> waitForNextFrame() {
        return Futures.immediateFuture(null);
    }

    private static class Api24Impl {
        private Api24Impl() {
        }

        static void pixelCopyRequest(SurfaceView source, Bitmap dest, PixelCopy.OnPixelCopyFinishedListener listener, Handler handler) {
            PixelCopy.request(source, dest, listener, handler);
        }
    }
}

package androidx.camera.core.processing;

import android.opengl.Matrix;
import android.util.Size;
import android.view.Surface;
import androidx.camera.core.Logger;
import androidx.camera.core.SurfaceOutput;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

final class SurfaceOutputImpl implements SurfaceOutput {
    private static final String TAG = "SurfaceOutputImpl";
    private final float[] mAdditionalTransform;
    private final ListenableFuture<Void> mCloseFuture;
    private CallbackToFutureAdapter.Completer<Void> mCloseFutureCompleter;
    private Executor mExecutor;
    private final int mFormat;
    private boolean mHasPendingCloseRequest = false;
    private boolean mIsClosed = false;
    private final Object mLock = new Object();
    private SurfaceOutput.OnCloseRequestedListener mOnCloseRequestedListener;
    private final Size mSize;
    private final Surface mSurface;
    private final int mTargets;

    SurfaceOutputImpl(Surface surface, int targets, int format, Size size, float[] additionalTransform) {
        this.mSurface = surface;
        this.mTargets = targets;
        this.mFormat = format;
        this.mSize = size;
        float[] fArr = new float[16];
        this.mAdditionalTransform = fArr;
        System.arraycopy(additionalTransform, 0, fArr, 0, additionalTransform.length);
        this.mCloseFuture = CallbackToFutureAdapter.getFuture(new SurfaceOutputImpl$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$new$0$androidx-camera-core-processing-SurfaceOutputImpl  reason: not valid java name */
    public /* synthetic */ Object m193lambda$new$0$androidxcameracoreprocessingSurfaceOutputImpl(CallbackToFutureAdapter.Completer completer) throws Exception {
        this.mCloseFutureCompleter = completer;
        return "SurfaceOutputImpl close future complete";
    }

    public Surface getSurface(Executor executor, SurfaceOutput.OnCloseRequestedListener listener) {
        boolean hasPendingCloseRequest;
        synchronized (this.mLock) {
            this.mExecutor = executor;
            this.mOnCloseRequestedListener = listener;
            hasPendingCloseRequest = this.mHasPendingCloseRequest;
        }
        if (hasPendingCloseRequest) {
            requestClose();
        }
        return this.mSurface;
    }

    public void requestClose() {
        SurfaceOutput.OnCloseRequestedListener onCloseRequestedListener = null;
        Executor executor = null;
        synchronized (this.mLock) {
            Executor executor2 = this.mExecutor;
            if (executor2 != null) {
                SurfaceOutput.OnCloseRequestedListener onCloseRequestedListener2 = this.mOnCloseRequestedListener;
                if (onCloseRequestedListener2 != null) {
                    if (!this.mIsClosed) {
                        onCloseRequestedListener = onCloseRequestedListener2;
                        executor = executor2;
                        this.mHasPendingCloseRequest = false;
                    }
                }
            }
            this.mHasPendingCloseRequest = true;
        }
        if (executor != null) {
            try {
                Objects.requireNonNull(onCloseRequestedListener);
                executor.execute(new SurfaceOutputImpl$$ExternalSyntheticLambda1(onCloseRequestedListener));
            } catch (RejectedExecutionException e) {
                Logger.d(TAG, "Effect executor closed. Close request not posted.", e);
            }
        }
    }

    public int getTargets() {
        return this.mTargets;
    }

    public Size getSize() {
        return this.mSize;
    }

    public int getFormat() {
        return this.mFormat;
    }

    public void close() {
        synchronized (this.mLock) {
            if (!this.mIsClosed) {
                this.mIsClosed = true;
            }
        }
        this.mCloseFutureCompleter.set(null);
    }

    public boolean isClosed() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mIsClosed;
        }
        return z;
    }

    public ListenableFuture<Void> getCloseFuture() {
        return this.mCloseFuture;
    }

    public void updateTransformMatrix(float[] updated, float[] original) {
        Matrix.multiplyMM(updated, 0, this.mAdditionalTransform, 0, original, 0);
    }
}

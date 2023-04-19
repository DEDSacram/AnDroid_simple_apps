package androidx.camera.core;

import android.graphics.Rect;
import android.util.Size;
import android.view.Surface;
import androidx.camera.core.impl.CameraInternal;
import androidx.camera.core.impl.DeferrableSurface;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.impl.utils.futures.FutureCallback;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.util.Consumer;
import androidx.core.util.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

public final class SurfaceRequest {
    private final CameraInternal mCamera;
    private final DeferrableSurface mInternalDeferrableSurface;
    private final Object mLock = new Object();
    private final boolean mRGBA8888Required;
    private final CallbackToFutureAdapter.Completer<Void> mRequestCancellationCompleter;
    private final Size mResolution;
    private final ListenableFuture<Void> mSessionStatusFuture;
    private final CallbackToFutureAdapter.Completer<Surface> mSurfaceCompleter;
    final ListenableFuture<Surface> mSurfaceFuture;
    private TransformationInfo mTransformationInfo;
    private Executor mTransformationInfoExecutor;
    private TransformationInfoListener mTransformationInfoListener;

    public interface TransformationInfoListener {
        void onTransformationInfoUpdate(TransformationInfo transformationInfo);
    }

    public SurfaceRequest(Size resolution, CameraInternal camera, boolean isRGBA8888Required) {
        this.mResolution = resolution;
        this.mCamera = camera;
        this.mRGBA8888Required = isRGBA8888Required;
        final String surfaceRequestString = "SurfaceRequest[size: " + resolution + ", id: " + hashCode() + "]";
        AtomicReference<CallbackToFutureAdapter.Completer<Void>> cancellationCompleterRef = new AtomicReference<>((Object) null);
        final ListenableFuture<Void> requestCancellationFuture = CallbackToFutureAdapter.getFuture(new SurfaceRequest$$ExternalSyntheticLambda0(cancellationCompleterRef, surfaceRequestString));
        final CallbackToFutureAdapter.Completer<Void> requestCancellationCompleter = (CallbackToFutureAdapter.Completer) Preconditions.checkNotNull(cancellationCompleterRef.get());
        this.mRequestCancellationCompleter = requestCancellationCompleter;
        AtomicReference<CallbackToFutureAdapter.Completer<Void>> sessionStatusCompleterRef = new AtomicReference<>((Object) null);
        ListenableFuture<Void> future = CallbackToFutureAdapter.getFuture(new SurfaceRequest$$ExternalSyntheticLambda1(sessionStatusCompleterRef, surfaceRequestString));
        this.mSessionStatusFuture = future;
        Futures.addCallback(future, new FutureCallback<Void>() {
            public void onSuccess(Void result) {
                Preconditions.checkState(requestCancellationCompleter.set(null));
            }

            public void onFailure(Throwable t) {
                if (t instanceof RequestCancelledException) {
                    Preconditions.checkState(requestCancellationFuture.cancel(false));
                } else {
                    Preconditions.checkState(requestCancellationCompleter.set(null));
                }
            }
        }, CameraXExecutors.directExecutor());
        final CallbackToFutureAdapter.Completer<Void> sessionStatusCompleter = (CallbackToFutureAdapter.Completer) Preconditions.checkNotNull(sessionStatusCompleterRef.get());
        AtomicReference<CallbackToFutureAdapter.Completer<Surface>> surfaceCompleterRef = new AtomicReference<>((Object) null);
        ListenableFuture<Surface> future2 = CallbackToFutureAdapter.getFuture(new SurfaceRequest$$ExternalSyntheticLambda2(surfaceCompleterRef, surfaceRequestString));
        this.mSurfaceFuture = future2;
        this.mSurfaceCompleter = (CallbackToFutureAdapter.Completer) Preconditions.checkNotNull(surfaceCompleterRef.get());
        AnonymousClass2 r8 = new DeferrableSurface(resolution, 34) {
            /* access modifiers changed from: protected */
            public ListenableFuture<Surface> provideSurface() {
                return SurfaceRequest.this.mSurfaceFuture;
            }
        };
        this.mInternalDeferrableSurface = r8;
        final ListenableFuture<Void> terminationFuture = r8.getTerminationFuture();
        Futures.addCallback(future2, new FutureCallback<Surface>() {
            public void onSuccess(Surface result) {
                Futures.propagate(terminationFuture, sessionStatusCompleter);
            }

            public void onFailure(Throwable t) {
                if (t instanceof CancellationException) {
                    Preconditions.checkState(sessionStatusCompleter.setException(new RequestCancelledException(surfaceRequestString + " cancelled.", t)));
                } else {
                    sessionStatusCompleter.set(null);
                }
            }
        }, CameraXExecutors.directExecutor());
        terminationFuture.addListener(new SurfaceRequest$$ExternalSyntheticLambda5(this), CameraXExecutors.directExecutor());
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$new$3$androidx-camera-core-SurfaceRequest  reason: not valid java name */
    public /* synthetic */ void m160lambda$new$3$androidxcameracoreSurfaceRequest() {
        this.mSurfaceFuture.cancel(true);
    }

    public DeferrableSurface getDeferrableSurface() {
        return this.mInternalDeferrableSurface;
    }

    public Size getResolution() {
        return this.mResolution;
    }

    public CameraInternal getCamera() {
        return this.mCamera;
    }

    public boolean isRGBA8888Required() {
        return this.mRGBA8888Required;
    }

    public void provideSurface(final Surface surface, Executor executor, final Consumer<Result> resultListener) {
        if (this.mSurfaceCompleter.set(surface) || this.mSurfaceFuture.isCancelled()) {
            Futures.addCallback(this.mSessionStatusFuture, new FutureCallback<Void>() {
                public void onSuccess(Void result) {
                    resultListener.accept(Result.of(0, surface));
                }

                public void onFailure(Throwable t) {
                    Preconditions.checkState(t instanceof RequestCancelledException, "Camera surface session should only fail with request cancellation. Instead failed due to:\n" + t);
                    resultListener.accept(Result.of(1, surface));
                }
            }, executor);
            return;
        }
        Preconditions.checkState(this.mSurfaceFuture.isDone());
        try {
            this.mSurfaceFuture.get();
            executor.execute(new SurfaceRequest$$ExternalSyntheticLambda6(resultListener, surface));
        } catch (InterruptedException | ExecutionException e) {
            executor.execute(new SurfaceRequest$$ExternalSyntheticLambda7(resultListener, surface));
        }
    }

    public boolean willNotProvideSurface() {
        return this.mSurfaceCompleter.setException(new DeferrableSurface.SurfaceUnavailableException("Surface request will not complete."));
    }

    public void addRequestCancellationListener(Executor executor, Runnable listener) {
        this.mRequestCancellationCompleter.addCancellationListener(listener, executor);
    }

    public void updateTransformationInfo(TransformationInfo transformationInfo) {
        TransformationInfoListener listener;
        Executor executor;
        synchronized (this.mLock) {
            this.mTransformationInfo = transformationInfo;
            listener = this.mTransformationInfoListener;
            executor = this.mTransformationInfoExecutor;
        }
        if (listener != null && executor != null) {
            executor.execute(new SurfaceRequest$$ExternalSyntheticLambda4(listener, transformationInfo));
        }
    }

    public void setTransformationInfoListener(Executor executor, TransformationInfoListener listener) {
        TransformationInfo transformationInfo;
        synchronized (this.mLock) {
            this.mTransformationInfoListener = listener;
            this.mTransformationInfoExecutor = executor;
            transformationInfo = this.mTransformationInfo;
        }
        if (transformationInfo != null) {
            executor.execute(new SurfaceRequest$$ExternalSyntheticLambda3(listener, transformationInfo));
        }
    }

    public void clearTransformationInfoListener() {
        synchronized (this.mLock) {
            this.mTransformationInfoListener = null;
            this.mTransformationInfoExecutor = null;
        }
    }

    private static final class RequestCancelledException extends RuntimeException {
        RequestCancelledException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static abstract class Result {
        public static final int RESULT_INVALID_SURFACE = 2;
        public static final int RESULT_REQUEST_CANCELLED = 1;
        public static final int RESULT_SURFACE_ALREADY_PROVIDED = 3;
        public static final int RESULT_SURFACE_USED_SUCCESSFULLY = 0;
        public static final int RESULT_WILL_NOT_PROVIDE_SURFACE = 4;

        @Retention(RetentionPolicy.SOURCE)
        public @interface ResultCode {
        }

        public abstract int getResultCode();

        public abstract Surface getSurface();

        static Result of(int code, Surface surface) {
            return new AutoValue_SurfaceRequest_Result(code, surface);
        }

        Result() {
        }
    }

    public static abstract class TransformationInfo {
        public abstract Rect getCropRect();

        public abstract int getRotationDegrees();

        public abstract int getTargetRotation();

        public static TransformationInfo of(Rect cropRect, int rotationDegrees, int targetRotation) {
            return new AutoValue_SurfaceRequest_TransformationInfo(cropRect, rotationDegrees, targetRotation);
        }

        TransformationInfo() {
        }
    }
}

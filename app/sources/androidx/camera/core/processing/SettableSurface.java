package androidx.camera.core.processing;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Size;
import android.view.Surface;
import androidx.camera.core.SurfaceOutput;
import androidx.camera.core.SurfaceRequest;
import androidx.camera.core.impl.CameraInternal;
import androidx.camera.core.impl.DeferrableSurface;
import androidx.camera.core.impl.utils.Threads;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.util.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;

public class SettableSurface extends DeferrableSurface {
    CallbackToFutureAdapter.Completer<Surface> mCompleter;
    private SurfaceOutputImpl mConsumerToNotify;
    private final Rect mCropRect;
    private boolean mHasConsumer = false;
    private final boolean mHasEmbeddedTransform;
    private boolean mHasProvider = false;
    private final boolean mMirroring;
    private final int mRotationDegrees;
    private final Matrix mSensorToBufferTransform;
    private final ListenableFuture<Surface> mSurfaceFuture;
    private final int mTargets;

    public SettableSurface(int targets, Size size, int format, Matrix sensorToBufferTransform, boolean hasEmbeddedTransform, Rect cropRect, int rotationDegrees, boolean mirroring) {
        super(size, format);
        this.mTargets = targets;
        this.mSensorToBufferTransform = sensorToBufferTransform;
        this.mHasEmbeddedTransform = hasEmbeddedTransform;
        this.mCropRect = cropRect;
        this.mRotationDegrees = rotationDegrees;
        this.mMirroring = mirroring;
        this.mSurfaceFuture = CallbackToFutureAdapter.getFuture(new SettableSurface$$ExternalSyntheticLambda1(this, size));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$new$0$androidx-camera-core-processing-SettableSurface  reason: not valid java name */
    public /* synthetic */ Object m189lambda$new$0$androidxcameracoreprocessingSettableSurface(Size size, CallbackToFutureAdapter.Completer completer) throws Exception {
        this.mCompleter = completer;
        return "SettableFuture size: " + size + " hashCode: " + hashCode();
    }

    /* access modifiers changed from: protected */
    public ListenableFuture<Surface> provideSurface() {
        return this.mSurfaceFuture;
    }

    public void setProvider(ListenableFuture<Surface> surfaceFuture) {
        Threads.checkMainThread();
        Preconditions.checkState(!this.mHasProvider, "Provider can only be linked once.");
        this.mHasProvider = true;
        Futures.propagate(surfaceFuture, this.mCompleter);
    }

    public void setProvider(DeferrableSurface provider) throws DeferrableSurface.SurfaceClosedException {
        Threads.checkMainThread();
        setProvider(provider.getSurface());
        provider.incrementUseCount();
        getTerminationFuture().addListener(new SettableSurface$$ExternalSyntheticLambda2(provider), CameraXExecutors.directExecutor());
    }

    static /* synthetic */ void lambda$setProvider$1(DeferrableSurface provider) {
        provider.decrementUseCount();
        provider.close();
    }

    public SurfaceRequest createSurfaceRequest(CameraInternal cameraInternal) {
        Threads.checkMainThread();
        SurfaceRequest surfaceRequest = new SurfaceRequest(getSize(), cameraInternal, true);
        try {
            setProvider(surfaceRequest.getDeferrableSurface());
            return surfaceRequest;
        } catch (DeferrableSurface.SurfaceClosedException e) {
            throw new AssertionError("Surface is somehow already closed", e);
        }
    }

    public ListenableFuture<SurfaceOutput> createSurfaceOutputFuture(float[] glTransformation) {
        Threads.checkMainThread();
        Preconditions.checkState(!this.mHasConsumer, "Consumer can only be linked once.");
        this.mHasConsumer = true;
        return Futures.transformAsync(getSurface(), new SettableSurface$$ExternalSyntheticLambda0(this, glTransformation), CameraXExecutors.mainThreadExecutor());
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$createSurfaceOutputFuture$2$androidx-camera-core-processing-SettableSurface  reason: not valid java name */
    public /* synthetic */ ListenableFuture m188lambda$createSurfaceOutputFuture$2$androidxcameracoreprocessingSettableSurface(float[] glTransformation, Surface surface) throws Exception {
        Preconditions.checkNotNull(surface);
        try {
            incrementUseCount();
            SurfaceOutputImpl surfaceOutputImpl = new SurfaceOutputImpl(surface, getTargets(), getFormat(), getSize(), glTransformation);
            surfaceOutputImpl.getCloseFuture().addListener(new SettableSurface$$ExternalSyntheticLambda3(this), CameraXExecutors.directExecutor());
            this.mConsumerToNotify = surfaceOutputImpl;
            return Futures.immediateFuture(surfaceOutputImpl);
        } catch (DeferrableSurface.SurfaceClosedException e) {
            return Futures.immediateFailedFuture(e);
        }
    }

    public final void close() {
        Threads.checkMainThread();
        super.close();
        SurfaceOutputImpl surfaceOutputImpl = this.mConsumerToNotify;
        if (surfaceOutputImpl != null) {
            surfaceOutputImpl.requestClose();
            this.mConsumerToNotify = null;
        }
    }

    public int getTargets() {
        return this.mTargets;
    }

    public Size getSize() {
        return getPrescribedSize();
    }

    public int getFormat() {
        return getPrescribedStreamFormat();
    }

    public Matrix getSensorToBufferTransform() {
        return this.mSensorToBufferTransform;
    }

    public boolean hasEmbeddedTransform() {
        return this.mHasEmbeddedTransform;
    }

    public Rect getCropRect() {
        return this.mCropRect;
    }

    public int getRotationDegrees() {
        return this.mRotationDegrees;
    }

    public boolean getMirroring() {
        return this.mMirroring;
    }
}

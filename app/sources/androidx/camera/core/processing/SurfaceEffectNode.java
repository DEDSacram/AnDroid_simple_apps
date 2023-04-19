package androidx.camera.core.processing;

import android.opengl.Matrix;
import androidx.camera.core.SurfaceOutput;
import androidx.camera.core.SurfaceRequest;
import androidx.camera.core.impl.CameraInternal;
import androidx.camera.core.impl.utils.Threads;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.impl.utils.futures.FutureCallback;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.core.util.Preconditions;
import java.util.Collections;

public class SurfaceEffectNode implements Node<SurfaceEdge, SurfaceEdge> {
    final CameraInternal mCameraInternal;
    private SurfaceEdge mInputEdge;
    private SurfaceEdge mOutputEdge;
    final SurfaceEffectInternal mSurfaceEffect;

    public SurfaceEffectNode(CameraInternal cameraInternal, SurfaceEffectInternal surfaceEffect) {
        this.mCameraInternal = cameraInternal;
        this.mSurfaceEffect = surfaceEffect;
    }

    public SurfaceEdge transform(SurfaceEdge inputEdge) {
        Threads.checkMainThread();
        boolean z = true;
        if (inputEdge.getSurfaces().size() != 1) {
            z = false;
        }
        Preconditions.checkArgument(z, "Multiple input stream not supported yet.");
        this.mInputEdge = inputEdge;
        SettableSurface inputSurface = inputEdge.getSurfaces().get(0);
        SettableSurface outputSurface = new SettableSurface(inputSurface.getTargets(), inputSurface.getSize(), inputSurface.getFormat(), inputSurface.getSensorToBufferTransform(), false, inputSurface.getCropRect(), inputSurface.getRotationDegrees(), inputSurface.getMirroring());
        sendSurfacesToEffectWhenReady(inputSurface, outputSurface);
        SurfaceEdge create = SurfaceEdge.create(Collections.singletonList(outputSurface));
        this.mOutputEdge = create;
        return create;
    }

    private void sendSurfacesToEffectWhenReady(SettableSurface input, SettableSurface output) {
        final SurfaceRequest surfaceRequest = input.createSurfaceRequest(this.mCameraInternal);
        Futures.addCallback(output.createSurfaceOutputFuture(calculateGlTransform()), new FutureCallback<SurfaceOutput>() {
            public void onSuccess(SurfaceOutput surfaceOutput) {
                Preconditions.checkNotNull(surfaceOutput);
                SurfaceEffectNode.this.mSurfaceEffect.onOutputSurface(surfaceOutput);
                SurfaceEffectNode.this.mSurfaceEffect.onInputSurface(surfaceRequest);
            }

            public void onFailure(Throwable t) {
                surfaceRequest.willNotProvideSurface();
            }
        }, CameraXExecutors.mainThreadExecutor());
    }

    /* access modifiers changed from: package-private */
    public float[] calculateGlTransform() {
        float[] glTransform = new float[16];
        Matrix.setIdentityM(glTransform, 0);
        return glTransform;
    }

    public void release() {
        this.mSurfaceEffect.release();
        CameraXExecutors.mainThreadExecutor().execute(new SurfaceEffectNode$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$release$0$androidx-camera-core-processing-SurfaceEffectNode  reason: not valid java name */
    public /* synthetic */ void m190lambda$release$0$androidxcameracoreprocessingSurfaceEffectNode() {
        SurfaceEdge surfaceEdge = this.mOutputEdge;
        if (surfaceEdge != null) {
            for (SettableSurface surface : surfaceEdge.getSurfaces()) {
                surface.close();
            }
        }
    }
}

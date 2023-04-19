package androidx.camera.core.processing;

import androidx.camera.core.SurfaceEffect;
import androidx.camera.core.SurfaceOutput;
import androidx.camera.core.SurfaceRequest;
import androidx.core.util.Preconditions;
import java.util.concurrent.Executor;

public class SurfaceEffectWithExecutor implements SurfaceEffectInternal {
    private final Executor mExecutor;
    private final SurfaceEffect mSurfaceEffect;

    public SurfaceEffectWithExecutor(SurfaceEffect surfaceEffect, Executor executor) {
        Preconditions.checkState(!(surfaceEffect instanceof SurfaceEffectInternal), "SurfaceEffectInternal should always be thread safe. Do not wrap.");
        this.mSurfaceEffect = surfaceEffect;
        this.mExecutor = executor;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onInputSurface$0$androidx-camera-core-processing-SurfaceEffectWithExecutor  reason: not valid java name */
    public /* synthetic */ void m191lambda$onInputSurface$0$androidxcameracoreprocessingSurfaceEffectWithExecutor(SurfaceRequest request) {
        this.mSurfaceEffect.onInputSurface(request);
    }

    public void onInputSurface(SurfaceRequest request) {
        this.mExecutor.execute(new SurfaceEffectWithExecutor$$ExternalSyntheticLambda1(this, request));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onOutputSurface$1$androidx-camera-core-processing-SurfaceEffectWithExecutor  reason: not valid java name */
    public /* synthetic */ void m192lambda$onOutputSurface$1$androidxcameracoreprocessingSurfaceEffectWithExecutor(SurfaceOutput surfaceOutput) {
        this.mSurfaceEffect.onOutputSurface(surfaceOutput);
    }

    public void onOutputSurface(SurfaceOutput surfaceOutput) {
        this.mExecutor.execute(new SurfaceEffectWithExecutor$$ExternalSyntheticLambda0(this, surfaceOutput));
    }

    public void release() {
    }
}

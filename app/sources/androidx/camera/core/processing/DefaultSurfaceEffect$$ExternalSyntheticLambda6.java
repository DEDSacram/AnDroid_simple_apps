package androidx.camera.core.processing;

import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class DefaultSurfaceEffect$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ DefaultSurfaceEffect f$0;
    public final /* synthetic */ ShaderProvider f$1;
    public final /* synthetic */ CallbackToFutureAdapter.Completer f$2;

    public /* synthetic */ DefaultSurfaceEffect$$ExternalSyntheticLambda6(DefaultSurfaceEffect defaultSurfaceEffect, ShaderProvider shaderProvider, CallbackToFutureAdapter.Completer completer) {
        this.f$0 = defaultSurfaceEffect;
        this.f$1 = shaderProvider;
        this.f$2 = completer;
    }

    public final void run() {
        this.f$0.m182lambda$initGlRenderer$4$androidxcameracoreprocessingDefaultSurfaceEffect(this.f$1, this.f$2);
    }
}

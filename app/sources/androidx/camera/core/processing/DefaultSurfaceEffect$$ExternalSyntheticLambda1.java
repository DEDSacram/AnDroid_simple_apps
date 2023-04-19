package androidx.camera.core.processing;

import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class DefaultSurfaceEffect$$ExternalSyntheticLambda1 implements CallbackToFutureAdapter.Resolver {
    public final /* synthetic */ DefaultSurfaceEffect f$0;
    public final /* synthetic */ ShaderProvider f$1;

    public /* synthetic */ DefaultSurfaceEffect$$ExternalSyntheticLambda1(DefaultSurfaceEffect defaultSurfaceEffect, ShaderProvider shaderProvider) {
        this.f$0 = defaultSurfaceEffect;
        this.f$1 = shaderProvider;
    }

    public final Object attachCompleter(CallbackToFutureAdapter.Completer completer) {
        return this.f$0.m183lambda$initGlRenderer$5$androidxcameracoreprocessingDefaultSurfaceEffect(this.f$1, completer);
    }
}

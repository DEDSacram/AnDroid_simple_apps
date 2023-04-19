package androidx.camera.camera2.internal;

import androidx.camera.camera2.internal.Camera2CapturePipeline;
import androidx.camera.core.impl.CaptureConfig;
import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class Camera2CapturePipeline$Pipeline$$ExternalSyntheticLambda4 implements CallbackToFutureAdapter.Resolver {
    public final /* synthetic */ Camera2CapturePipeline.Pipeline f$0;
    public final /* synthetic */ CaptureConfig.Builder f$1;

    public /* synthetic */ Camera2CapturePipeline$Pipeline$$ExternalSyntheticLambda4(Camera2CapturePipeline.Pipeline pipeline, CaptureConfig.Builder builder) {
        this.f$0 = pipeline;
        this.f$1 = builder;
    }

    public final Object attachCompleter(CallbackToFutureAdapter.Completer completer) {
        return this.f$0.m54lambda$submitConfigsInternal$4$androidxcameracamera2internalCamera2CapturePipeline$Pipeline(this.f$1, completer);
    }
}

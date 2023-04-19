package androidx.camera.camera2.internal;

import androidx.camera.camera2.internal.Camera2CapturePipeline;
import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class Camera2CapturePipeline$ResultListener$$ExternalSyntheticLambda0 implements CallbackToFutureAdapter.Resolver {
    public final /* synthetic */ Camera2CapturePipeline.ResultListener f$0;

    public /* synthetic */ Camera2CapturePipeline$ResultListener$$ExternalSyntheticLambda0(Camera2CapturePipeline.ResultListener resultListener) {
        this.f$0 = resultListener;
    }

    public final Object attachCompleter(CallbackToFutureAdapter.Completer completer) {
        return this.f$0.m55lambda$new$0$androidxcameracamera2internalCamera2CapturePipeline$ResultListener(completer);
    }
}

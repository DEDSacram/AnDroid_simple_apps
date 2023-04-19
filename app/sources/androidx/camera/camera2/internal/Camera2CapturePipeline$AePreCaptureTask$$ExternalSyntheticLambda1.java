package androidx.camera.camera2.internal;

import androidx.camera.camera2.internal.Camera2CapturePipeline;
import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class Camera2CapturePipeline$AePreCaptureTask$$ExternalSyntheticLambda1 implements CallbackToFutureAdapter.Resolver {
    public final /* synthetic */ Camera2CapturePipeline.AePreCaptureTask f$0;

    public /* synthetic */ Camera2CapturePipeline$AePreCaptureTask$$ExternalSyntheticLambda1(Camera2CapturePipeline.AePreCaptureTask aePreCaptureTask) {
        this.f$0 = aePreCaptureTask;
    }

    public final Object attachCompleter(CallbackToFutureAdapter.Completer completer) {
        return this.f$0.m50lambda$preCapture$0$androidxcameracamera2internalCamera2CapturePipeline$AePreCaptureTask(completer);
    }
}

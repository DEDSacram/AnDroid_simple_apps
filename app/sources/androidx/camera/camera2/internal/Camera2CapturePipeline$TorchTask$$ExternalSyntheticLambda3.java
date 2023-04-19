package androidx.camera.camera2.internal;

import androidx.camera.camera2.internal.Camera2CapturePipeline;
import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class Camera2CapturePipeline$TorchTask$$ExternalSyntheticLambda3 implements CallbackToFutureAdapter.Resolver {
    public final /* synthetic */ Camera2CapturePipeline.TorchTask f$0;

    public /* synthetic */ Camera2CapturePipeline$TorchTask$$ExternalSyntheticLambda3(Camera2CapturePipeline.TorchTask torchTask) {
        this.f$0 = torchTask;
    }

    public final Object attachCompleter(CallbackToFutureAdapter.Completer completer) {
        return this.f$0.m56lambda$preCapture$0$androidxcameracamera2internalCamera2CapturePipeline$TorchTask(completer);
    }
}

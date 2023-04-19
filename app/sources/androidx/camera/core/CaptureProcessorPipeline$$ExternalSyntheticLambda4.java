package androidx.camera.core;

import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class CaptureProcessorPipeline$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ CallbackToFutureAdapter.Completer f$0;

    public /* synthetic */ CaptureProcessorPipeline$$ExternalSyntheticLambda4(CallbackToFutureAdapter.Completer completer) {
        this.f$0 = completer;
    }

    public final void run() {
        this.f$0.set(null);
    }
}

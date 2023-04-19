package androidx.camera.camera2.internal;

import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class TorchControl$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ TorchControl f$0;
    public final /* synthetic */ CallbackToFutureAdapter.Completer f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ TorchControl$$ExternalSyntheticLambda2(TorchControl torchControl, CallbackToFutureAdapter.Completer completer, boolean z) {
        this.f$0 = torchControl;
        this.f$1 = completer;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.m89lambda$enableTorch$1$androidxcameracamera2internalTorchControl(this.f$1, this.f$2);
    }
}

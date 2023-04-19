package androidx.camera.camera2.internal;

import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class TorchControl$$ExternalSyntheticLambda1 implements CallbackToFutureAdapter.Resolver {
    public final /* synthetic */ TorchControl f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ TorchControl$$ExternalSyntheticLambda1(TorchControl torchControl, boolean z) {
        this.f$0 = torchControl;
        this.f$1 = z;
    }

    public final Object attachCompleter(CallbackToFutureAdapter.Completer completer) {
        return this.f$0.m90lambda$enableTorch$2$androidxcameracamera2internalTorchControl(this.f$1, completer);
    }
}

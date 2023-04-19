package androidx.camera.camera2.internal;

import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class FocusMeteringControl$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ FocusMeteringControl f$0;
    public final /* synthetic */ CallbackToFutureAdapter.Completer f$1;

    public /* synthetic */ FocusMeteringControl$$ExternalSyntheticLambda8(FocusMeteringControl focusMeteringControl, CallbackToFutureAdapter.Completer completer) {
        this.f$0 = focusMeteringControl;
        this.f$1 = completer;
    }

    public final void run() {
        this.f$0.m63lambda$cancelFocusAndMetering$7$androidxcameracamera2internalFocusMeteringControl(this.f$1);
    }
}

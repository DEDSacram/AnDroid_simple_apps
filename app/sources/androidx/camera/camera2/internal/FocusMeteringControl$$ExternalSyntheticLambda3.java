package androidx.camera.camera2.internal;

import androidx.camera.core.FocusMeteringAction;
import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class FocusMeteringControl$$ExternalSyntheticLambda3 implements CallbackToFutureAdapter.Resolver {
    public final /* synthetic */ FocusMeteringControl f$0;
    public final /* synthetic */ FocusMeteringAction f$1;

    public /* synthetic */ FocusMeteringControl$$ExternalSyntheticLambda3(FocusMeteringControl focusMeteringControl, FocusMeteringAction focusMeteringAction) {
        this.f$0 = focusMeteringControl;
        this.f$1 = focusMeteringAction;
    }

    public final Object attachCompleter(CallbackToFutureAdapter.Completer completer) {
        return this.f$0.m72lambda$startFocusAndMetering$1$androidxcameracamera2internalFocusMeteringControl(this.f$1, completer);
    }
}

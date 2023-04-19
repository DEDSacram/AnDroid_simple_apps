package androidx.camera.camera2.internal.compat.workaround;

import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class WaitForRepeatingRequestStart$$ExternalSyntheticLambda1 implements CallbackToFutureAdapter.Resolver {
    public final /* synthetic */ WaitForRepeatingRequestStart f$0;

    public /* synthetic */ WaitForRepeatingRequestStart$$ExternalSyntheticLambda1(WaitForRepeatingRequestStart waitForRepeatingRequestStart) {
        this.f$0 = waitForRepeatingRequestStart;
    }

    public final Object attachCompleter(CallbackToFutureAdapter.Completer completer) {
        return this.f$0.m118lambda$new$0$androidxcameracamera2internalcompatworkaroundWaitForRepeatingRequestStart(completer);
    }
}

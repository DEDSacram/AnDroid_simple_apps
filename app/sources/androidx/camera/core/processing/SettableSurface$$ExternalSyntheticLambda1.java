package androidx.camera.core.processing;

import android.util.Size;
import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class SettableSurface$$ExternalSyntheticLambda1 implements CallbackToFutureAdapter.Resolver {
    public final /* synthetic */ SettableSurface f$0;
    public final /* synthetic */ Size f$1;

    public /* synthetic */ SettableSurface$$ExternalSyntheticLambda1(SettableSurface settableSurface, Size size) {
        this.f$0 = settableSurface;
        this.f$1 = size;
    }

    public final Object attachCompleter(CallbackToFutureAdapter.Completer completer) {
        return this.f$0.m189lambda$new$0$androidxcameracoreprocessingSettableSurface(this.f$1, completer);
    }
}

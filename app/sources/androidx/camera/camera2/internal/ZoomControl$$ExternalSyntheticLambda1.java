package androidx.camera.camera2.internal;

import androidx.camera.core.ZoomState;
import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class ZoomControl$$ExternalSyntheticLambda1 implements CallbackToFutureAdapter.Resolver {
    public final /* synthetic */ ZoomControl f$0;
    public final /* synthetic */ ZoomState f$1;

    public /* synthetic */ ZoomControl$$ExternalSyntheticLambda1(ZoomControl zoomControl, ZoomState zoomState) {
        this.f$0 = zoomControl;
        this.f$1 = zoomState;
    }

    public final Object attachCompleter(CallbackToFutureAdapter.Completer completer) {
        return this.f$0.m95lambda$setZoomRatio$1$androidxcameracamera2internalZoomControl(this.f$1, completer);
    }
}

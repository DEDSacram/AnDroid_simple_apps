package androidx.camera.camera2.internal;

import androidx.camera.core.ZoomState;
import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class ZoomControl$$ExternalSyntheticLambda0 implements CallbackToFutureAdapter.Resolver {
    public final /* synthetic */ ZoomControl f$0;
    public final /* synthetic */ ZoomState f$1;

    public /* synthetic */ ZoomControl$$ExternalSyntheticLambda0(ZoomControl zoomControl, ZoomState zoomState) {
        this.f$0 = zoomControl;
        this.f$1 = zoomState;
    }

    public final Object attachCompleter(CallbackToFutureAdapter.Completer completer) {
        return this.f$0.m93lambda$setLinearZoom$3$androidxcameracamera2internalZoomControl(this.f$1, completer);
    }
}

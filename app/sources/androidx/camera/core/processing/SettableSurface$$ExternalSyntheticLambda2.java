package androidx.camera.core.processing;

import androidx.camera.core.impl.DeferrableSurface;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class SettableSurface$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ DeferrableSurface f$0;

    public /* synthetic */ SettableSurface$$ExternalSyntheticLambda2(DeferrableSurface deferrableSurface) {
        this.f$0 = deferrableSurface;
    }

    public final void run() {
        SettableSurface.lambda$setProvider$1(this.f$0);
    }
}

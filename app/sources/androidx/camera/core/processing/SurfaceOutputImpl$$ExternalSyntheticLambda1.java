package androidx.camera.core.processing;

import androidx.camera.core.SurfaceOutput;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class SurfaceOutputImpl$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ SurfaceOutput.OnCloseRequestedListener f$0;

    public /* synthetic */ SurfaceOutputImpl$$ExternalSyntheticLambda1(SurfaceOutput.OnCloseRequestedListener onCloseRequestedListener) {
        this.f$0 = onCloseRequestedListener;
    }

    public final void run() {
        this.f$0.onCloseRequested();
    }
}

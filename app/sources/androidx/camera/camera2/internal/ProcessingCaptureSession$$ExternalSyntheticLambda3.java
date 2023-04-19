package androidx.camera.camera2.internal;

import androidx.camera.core.impl.DeferrableSurface;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class ProcessingCaptureSession$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ DeferrableSurface f$0;

    public /* synthetic */ ProcessingCaptureSession$$ExternalSyntheticLambda3(DeferrableSurface deferrableSurface) {
        this.f$0 = deferrableSurface;
    }

    public final void run() {
        ProcessingCaptureSession.sHeldProcessorSurfaces.remove(this.f$0);
    }
}

package androidx.camera.view;

import androidx.camera.core.impl.CameraInternal;
import androidx.camera.view.PreviewView;
import androidx.camera.view.PreviewViewImplementation;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class PreviewView$1$$ExternalSyntheticLambda1 implements PreviewViewImplementation.OnSurfaceNotInUseListener {
    public final /* synthetic */ PreviewView.AnonymousClass1 f$0;
    public final /* synthetic */ PreviewStreamStateObserver f$1;
    public final /* synthetic */ CameraInternal f$2;

    public /* synthetic */ PreviewView$1$$ExternalSyntheticLambda1(PreviewView.AnonymousClass1 r1, PreviewStreamStateObserver previewStreamStateObserver, CameraInternal cameraInternal) {
        this.f$0 = r1;
        this.f$1 = previewStreamStateObserver;
        this.f$2 = cameraInternal;
    }

    public final void onSurfaceNotInUse() {
        this.f$0.m205lambda$onSurfaceRequested$2$androidxcameraviewPreviewView$1(this.f$1, this.f$2);
    }
}

package androidx.camera.core.internal;

import android.graphics.SurfaceTexture;
import android.view.Surface;
import androidx.camera.core.SurfaceRequest;
import androidx.core.util.Consumer;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class CameraUseCaseAdapter$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ Surface f$0;
    public final /* synthetic */ SurfaceTexture f$1;

    public /* synthetic */ CameraUseCaseAdapter$$ExternalSyntheticLambda1(Surface surface, SurfaceTexture surfaceTexture) {
        this.f$0 = surface;
        this.f$1 = surfaceTexture;
    }

    public final void accept(Object obj) {
        CameraUseCaseAdapter.lambda$createExtraPreview$0(this.f$0, this.f$1, (SurfaceRequest.Result) obj);
    }
}

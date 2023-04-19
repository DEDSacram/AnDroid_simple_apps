package androidx.camera.core.processing;

import android.graphics.SurfaceTexture;
import android.view.Surface;
import androidx.camera.core.SurfaceRequest;
import androidx.core.util.Consumer;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class DefaultSurfaceEffect$$ExternalSyntheticLambda2 implements Consumer {
    public final /* synthetic */ DefaultSurfaceEffect f$0;
    public final /* synthetic */ SurfaceTexture f$1;
    public final /* synthetic */ Surface f$2;

    public /* synthetic */ DefaultSurfaceEffect$$ExternalSyntheticLambda2(DefaultSurfaceEffect defaultSurfaceEffect, SurfaceTexture surfaceTexture, Surface surface) {
        this.f$0 = defaultSurfaceEffect;
        this.f$1 = surfaceTexture;
        this.f$2 = surface;
    }

    public final void accept(Object obj) {
        this.f$0.m184lambda$onInputSurface$0$androidxcameracoreprocessingDefaultSurfaceEffect(this.f$1, this.f$2, (SurfaceRequest.Result) obj);
    }
}

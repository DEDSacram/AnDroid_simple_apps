package androidx.camera.view;

import android.view.Surface;
import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class TextureViewImplementation$$ExternalSyntheticLambda1 implements CallbackToFutureAdapter.Resolver {
    public final /* synthetic */ TextureViewImplementation f$0;
    public final /* synthetic */ Surface f$1;

    public /* synthetic */ TextureViewImplementation$$ExternalSyntheticLambda1(TextureViewImplementation textureViewImplementation, Surface surface) {
        this.f$0 = textureViewImplementation;
        this.f$1 = surface;
    }

    public final Object attachCompleter(CallbackToFutureAdapter.Completer completer) {
        return this.f$0.m210lambda$tryToProvidePreviewSurface$1$androidxcameraviewTextureViewImplementation(this.f$1, completer);
    }
}

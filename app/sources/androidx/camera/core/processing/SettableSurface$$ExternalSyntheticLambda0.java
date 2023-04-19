package androidx.camera.core.processing;

import android.view.Surface;
import androidx.camera.core.impl.utils.futures.AsyncFunction;
import com.google.common.util.concurrent.ListenableFuture;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class SettableSurface$$ExternalSyntheticLambda0 implements AsyncFunction {
    public final /* synthetic */ SettableSurface f$0;
    public final /* synthetic */ float[] f$1;

    public /* synthetic */ SettableSurface$$ExternalSyntheticLambda0(SettableSurface settableSurface, float[] fArr) {
        this.f$0 = settableSurface;
        this.f$1 = fArr;
    }

    public final ListenableFuture apply(Object obj) {
        return this.f$0.m188lambda$createSurfaceOutputFuture$2$androidxcameracoreprocessingSettableSurface(this.f$1, (Surface) obj);
    }
}

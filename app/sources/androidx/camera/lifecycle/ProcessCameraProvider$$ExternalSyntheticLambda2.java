package androidx.camera.lifecycle;

import androidx.camera.core.CameraX;
import androidx.camera.core.impl.utils.futures.AsyncFunction;
import com.google.common.util.concurrent.ListenableFuture;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class ProcessCameraProvider$$ExternalSyntheticLambda2 implements AsyncFunction {
    public final /* synthetic */ CameraX f$0;

    public /* synthetic */ ProcessCameraProvider$$ExternalSyntheticLambda2(CameraX cameraX) {
        this.f$0 = cameraX;
    }

    public final ListenableFuture apply(Object obj) {
        return this.f$0.getInitializeFuture();
    }
}

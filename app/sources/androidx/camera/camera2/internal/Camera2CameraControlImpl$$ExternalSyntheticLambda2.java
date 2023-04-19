package androidx.camera.camera2.internal;

import androidx.camera.core.impl.utils.futures.AsyncFunction;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.List;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class Camera2CameraControlImpl$$ExternalSyntheticLambda2 implements AsyncFunction {
    public final /* synthetic */ Camera2CameraControlImpl f$0;
    public final /* synthetic */ List f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ Camera2CameraControlImpl$$ExternalSyntheticLambda2(Camera2CameraControlImpl camera2CameraControlImpl, List list, int i, int i2, int i3) {
        this.f$0 = camera2CameraControlImpl;
        this.f$1 = list;
        this.f$2 = i;
        this.f$3 = i2;
        this.f$4 = i3;
    }

    public final ListenableFuture apply(Object obj) {
        return this.f$0.m29lambda$submitStillCaptureRequests$5$androidxcameracamera2internalCamera2CameraControlImpl(this.f$1, this.f$2, this.f$3, this.f$4, (Void) obj);
    }
}

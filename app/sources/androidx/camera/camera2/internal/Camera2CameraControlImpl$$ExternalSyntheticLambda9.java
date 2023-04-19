package androidx.camera.camera2.internal;

import androidx.camera.core.impl.CameraCaptureCallback;
import java.util.concurrent.Executor;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class Camera2CameraControlImpl$$ExternalSyntheticLambda9 implements Runnable {
    public final /* synthetic */ Camera2CameraControlImpl f$0;
    public final /* synthetic */ Executor f$1;
    public final /* synthetic */ CameraCaptureCallback f$2;

    public /* synthetic */ Camera2CameraControlImpl$$ExternalSyntheticLambda9(Camera2CameraControlImpl camera2CameraControlImpl, Executor executor, CameraCaptureCallback cameraCaptureCallback) {
        this.f$0 = camera2CameraControlImpl;
        this.f$1 = executor;
        this.f$2 = cameraCaptureCallback;
    }

    public final void run() {
        this.f$0.m26lambda$addSessionCameraCaptureCallback$8$androidxcameracamera2internalCamera2CameraControlImpl(this.f$1, this.f$2);
    }
}

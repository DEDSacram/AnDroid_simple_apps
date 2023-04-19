package androidx.camera.camera2.internal;

import androidx.camera.core.impl.SessionConfig;
import androidx.camera.core.impl.UseCaseConfig;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class Camera2CameraImpl$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ Camera2CameraImpl f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ SessionConfig f$2;
    public final /* synthetic */ UseCaseConfig f$3;

    public /* synthetic */ Camera2CameraImpl$$ExternalSyntheticLambda3(Camera2CameraImpl camera2CameraImpl, String str, SessionConfig sessionConfig, UseCaseConfig useCaseConfig) {
        this.f$0 = camera2CameraImpl;
        this.f$1 = str;
        this.f$2 = sessionConfig;
        this.f$3 = useCaseConfig;
    }

    public final void run() {
        this.f$0.m44lambda$onUseCaseUpdated$7$androidxcameracamera2internalCamera2CameraImpl(this.f$1, this.f$2, this.f$3);
    }
}

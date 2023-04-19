package androidx.camera.camera2.internal;

import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class Camera2CameraImpl$$ExternalSyntheticLambda14 implements Runnable {
    public final /* synthetic */ Camera2CameraImpl f$0;
    public final /* synthetic */ CallbackToFutureAdapter.Completer f$1;

    public /* synthetic */ Camera2CameraImpl$$ExternalSyntheticLambda14(Camera2CameraImpl camera2CameraImpl, CallbackToFutureAdapter.Completer completer) {
        this.f$0 = camera2CameraImpl;
        this.f$1 = completer;
    }

    public final void run() {
        this.f$0.m45lambda$release$2$androidxcameracamera2internalCamera2CameraImpl(this.f$1);
    }
}

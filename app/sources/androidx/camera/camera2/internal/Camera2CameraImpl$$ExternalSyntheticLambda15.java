package androidx.camera.camera2.internal;

import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class Camera2CameraImpl$$ExternalSyntheticLambda15 implements Runnable {
    public final /* synthetic */ Camera2CameraImpl f$0;
    public final /* synthetic */ CallbackToFutureAdapter.Completer f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ Camera2CameraImpl$$ExternalSyntheticLambda15(Camera2CameraImpl camera2CameraImpl, CallbackToFutureAdapter.Completer completer, String str) {
        this.f$0 = camera2CameraImpl;
        this.f$1 = completer;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.m40lambda$isUseCaseAttached$9$androidxcameracamera2internalCamera2CameraImpl(this.f$1, this.f$2);
    }
}

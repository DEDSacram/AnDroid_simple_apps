package androidx.camera.core;

import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class CameraX$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ CameraX f$0;
    public final /* synthetic */ CallbackToFutureAdapter.Completer f$1;

    public /* synthetic */ CameraX$$ExternalSyntheticLambda3(CameraX cameraX, CallbackToFutureAdapter.Completer completer) {
        this.f$0 = cameraX;
        this.f$1 = completer;
    }

    public final void run() {
        this.f$0.m132lambda$shutdownInternal$3$androidxcameracoreCameraX(this.f$1);
    }
}

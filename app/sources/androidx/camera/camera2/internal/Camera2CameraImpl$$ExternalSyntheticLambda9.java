package androidx.camera.camera2.internal;

import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class Camera2CameraImpl$$ExternalSyntheticLambda9 implements CallbackToFutureAdapter.Resolver {
    public final /* synthetic */ Camera2CameraImpl f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ Camera2CameraImpl$$ExternalSyntheticLambda9(Camera2CameraImpl camera2CameraImpl, String str) {
        this.f$0 = camera2CameraImpl;
        this.f$1 = str;
    }

    public final Object attachCompleter(CallbackToFutureAdapter.Completer completer) {
        return this.f$0.m39lambda$isUseCaseAttached$10$androidxcameracamera2internalCamera2CameraImpl(this.f$1, completer);
    }
}

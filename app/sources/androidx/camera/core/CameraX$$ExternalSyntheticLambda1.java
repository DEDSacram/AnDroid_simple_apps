package androidx.camera.core;

import android.content.Context;
import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class CameraX$$ExternalSyntheticLambda1 implements CallbackToFutureAdapter.Resolver {
    public final /* synthetic */ CameraX f$0;
    public final /* synthetic */ Context f$1;

    public /* synthetic */ CameraX$$ExternalSyntheticLambda1(CameraX cameraX, Context context) {
        this.f$0 = cameraX;
        this.f$1 = context;
    }

    public final Object attachCompleter(CallbackToFutureAdapter.Completer completer) {
        return this.f$0.m131lambda$initInternal$0$androidxcameracoreCameraX(this.f$1, completer);
    }
}

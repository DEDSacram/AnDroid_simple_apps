package androidx.camera.core;

import android.content.Context;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import java.util.concurrent.Executor;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class CameraX$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ CameraX f$0;
    public final /* synthetic */ Context f$1;
    public final /* synthetic */ Executor f$2;
    public final /* synthetic */ CallbackToFutureAdapter.Completer f$3;
    public final /* synthetic */ long f$4;

    public /* synthetic */ CameraX$$ExternalSyntheticLambda2(CameraX cameraX, Context context, Executor executor, CallbackToFutureAdapter.Completer completer, long j) {
        this.f$0 = cameraX;
        this.f$1 = context;
        this.f$2 = executor;
        this.f$3 = completer;
        this.f$4 = j;
    }

    public final void run() {
        this.f$0.m130lambda$initAndRetryRecursively$2$androidxcameracoreCameraX(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

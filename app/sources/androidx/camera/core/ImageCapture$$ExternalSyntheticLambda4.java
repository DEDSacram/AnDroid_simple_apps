package androidx.camera.core;

import com.google.common.util.concurrent.ListenableFuture;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class ImageCapture$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ ListenableFuture f$0;

    public /* synthetic */ ImageCapture$$ExternalSyntheticLambda4(ListenableFuture listenableFuture) {
        this.f$0 = listenableFuture;
    }

    public final void run() {
        this.f$0.cancel(true);
    }
}

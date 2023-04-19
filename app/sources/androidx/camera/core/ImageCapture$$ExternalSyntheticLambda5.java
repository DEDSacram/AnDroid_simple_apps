package androidx.camera.core;

import java.util.concurrent.ExecutorService;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class ImageCapture$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ ExecutorService f$0;

    public /* synthetic */ ImageCapture$$ExternalSyntheticLambda5(ExecutorService executorService) {
        this.f$0 = executorService;
    }

    public final void run() {
        this.f$0.shutdown();
    }
}

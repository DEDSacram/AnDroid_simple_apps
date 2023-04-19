package androidx.camera.core;

import androidx.camera.core.ImageCapture;
import java.util.concurrent.Executor;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class ImageCapture$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ ImageCapture f$0;
    public final /* synthetic */ Executor f$1;
    public final /* synthetic */ ImageCapture.OnImageCapturedCallback f$2;

    public /* synthetic */ ImageCapture$$ExternalSyntheticLambda2(ImageCapture imageCapture, Executor executor, ImageCapture.OnImageCapturedCallback onImageCapturedCallback) {
        this.f$0 = imageCapture;
        this.f$1 = executor;
        this.f$2 = onImageCapturedCallback;
    }

    public final void run() {
        this.f$0.m143lambda$takePicture$3$androidxcameracoreImageCapture(this.f$1, this.f$2);
    }
}

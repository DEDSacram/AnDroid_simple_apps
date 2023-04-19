package androidx.camera.core;

import androidx.camera.core.ImageCapture;
import java.util.concurrent.Executor;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class ImageCapture$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ImageCapture f$0;
    public final /* synthetic */ ImageCapture.OutputFileOptions f$1;
    public final /* synthetic */ Executor f$2;
    public final /* synthetic */ ImageCapture.OnImageSavedCallback f$3;

    public /* synthetic */ ImageCapture$$ExternalSyntheticLambda1(ImageCapture imageCapture, ImageCapture.OutputFileOptions outputFileOptions, Executor executor, ImageCapture.OnImageSavedCallback onImageSavedCallback) {
        this.f$0 = imageCapture;
        this.f$1 = outputFileOptions;
        this.f$2 = executor;
        this.f$3 = onImageSavedCallback;
    }

    public final void run() {
        this.f$0.m144lambda$takePicture$4$androidxcameracoreImageCapture(this.f$1, this.f$2, this.f$3);
    }
}

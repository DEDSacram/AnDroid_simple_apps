package androidx.camera.core;

import androidx.camera.core.ImageCapture;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class ImageCapture$$ExternalSyntheticLambda14 implements Runnable {
    public final /* synthetic */ ImageCapture f$0;
    public final /* synthetic */ ImageCapture.OnImageCapturedCallback f$1;

    public /* synthetic */ ImageCapture$$ExternalSyntheticLambda14(ImageCapture imageCapture, ImageCapture.OnImageCapturedCallback onImageCapturedCallback) {
        this.f$0 = imageCapture;
        this.f$1 = onImageCapturedCallback;
    }

    public final void run() {
        this.f$0.m142lambda$sendImageCaptureRequest$5$androidxcameracoreImageCapture(this.f$1);
    }
}

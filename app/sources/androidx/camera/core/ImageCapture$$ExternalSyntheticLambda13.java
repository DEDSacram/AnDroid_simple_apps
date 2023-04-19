package androidx.camera.core;

import androidx.camera.core.ImageCapture;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class ImageCapture$$ExternalSyntheticLambda13 implements Runnable {
    public final /* synthetic */ ImageCapture.OnImageCapturedCallback f$0;

    public /* synthetic */ ImageCapture$$ExternalSyntheticLambda13(ImageCapture.OnImageCapturedCallback onImageCapturedCallback) {
        this.f$0 = onImageCapturedCallback;
    }

    public final void run() {
        this.f$0.onError(new ImageCaptureException(0, "Request is canceled", (Throwable) null));
    }
}

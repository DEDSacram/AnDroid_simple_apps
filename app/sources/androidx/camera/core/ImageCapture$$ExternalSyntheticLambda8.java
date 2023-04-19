package androidx.camera.core;

import androidx.camera.core.ImageCapture;
import androidx.camera.core.ProcessingImageReader;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class ImageCapture$$ExternalSyntheticLambda8 implements ProcessingImageReader.OnProcessingErrorCallback {
    public final /* synthetic */ ImageCapture.ImageCaptureRequest f$0;

    public /* synthetic */ ImageCapture$$ExternalSyntheticLambda8(ImageCapture.ImageCaptureRequest imageCaptureRequest) {
        this.f$0 = imageCaptureRequest;
    }

    public final void notifyProcessingError(String str, Throwable th) {
        ImageCapture.lambda$issueTakePicture$10(this.f$0, str, th);
    }
}

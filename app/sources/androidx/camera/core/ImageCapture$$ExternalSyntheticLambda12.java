package androidx.camera.core;

import androidx.camera.core.ImageCapture;
import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class ImageCapture$$ExternalSyntheticLambda12 implements CallbackToFutureAdapter.Resolver {
    public final /* synthetic */ ImageCapture f$0;
    public final /* synthetic */ ImageCapture.ImageCaptureRequest f$1;

    public /* synthetic */ ImageCapture$$ExternalSyntheticLambda12(ImageCapture imageCapture, ImageCapture.ImageCaptureRequest imageCaptureRequest) {
        this.f$0 = imageCapture;
        this.f$1 = imageCaptureRequest;
    }

    public final Object attachCompleter(CallbackToFutureAdapter.Completer completer) {
        return this.f$0.m145lambda$takePictureInternal$9$androidxcameracoreImageCapture(this.f$1, completer);
    }
}

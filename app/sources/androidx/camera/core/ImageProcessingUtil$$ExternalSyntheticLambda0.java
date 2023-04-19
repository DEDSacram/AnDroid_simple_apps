package androidx.camera.core;

import androidx.camera.core.ForwardingImageProxy;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class ImageProcessingUtil$$ExternalSyntheticLambda0 implements ForwardingImageProxy.OnImageCloseListener {
    public final /* synthetic */ ImageProxy f$0;
    public final /* synthetic */ ImageProxy f$1;

    public /* synthetic */ ImageProcessingUtil$$ExternalSyntheticLambda0(ImageProxy imageProxy, ImageProxy imageProxy2) {
        this.f$0 = imageProxy;
        this.f$1 = imageProxy2;
    }

    public final void onImageClose(ImageProxy imageProxy) {
        ImageProcessingUtil.lambda$convertYUVToRGB$0(this.f$0, this.f$1, imageProxy);
    }
}

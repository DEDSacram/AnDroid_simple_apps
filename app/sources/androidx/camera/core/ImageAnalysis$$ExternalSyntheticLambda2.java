package androidx.camera.core;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class ImageAnalysis$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ SafeCloseImageReaderProxy f$0;
    public final /* synthetic */ SafeCloseImageReaderProxy f$1;

    public /* synthetic */ ImageAnalysis$$ExternalSyntheticLambda2(SafeCloseImageReaderProxy safeCloseImageReaderProxy, SafeCloseImageReaderProxy safeCloseImageReaderProxy2) {
        this.f$0 = safeCloseImageReaderProxy;
        this.f$1 = safeCloseImageReaderProxy2;
    }

    public final void run() {
        ImageAnalysis.lambda$createPipeline$0(this.f$0, this.f$1);
    }
}

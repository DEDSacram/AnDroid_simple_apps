package androidx.camera.core;

import androidx.camera.core.ProcessingImageReader;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class ProcessingImageReader$3$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ProcessingImageReader.OnProcessingErrorCallback f$0;
    public final /* synthetic */ Exception f$1;

    public /* synthetic */ ProcessingImageReader$3$$ExternalSyntheticLambda0(ProcessingImageReader.OnProcessingErrorCallback onProcessingErrorCallback, Exception exc) {
        this.f$0 = onProcessingErrorCallback;
        this.f$1 = exc;
    }

    public final void run() {
        this.f$0.notifyProcessingError(this.f$1.getMessage(), this.f$1.getCause());
    }
}

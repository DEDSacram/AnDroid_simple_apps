package androidx.camera.camera2.internal;

import androidx.camera.camera2.internal.ProcessingCaptureSession;
import androidx.camera.core.impl.CaptureConfig;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class ProcessingCaptureSession$2$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ProcessingCaptureSession.AnonymousClass2 f$0;
    public final /* synthetic */ CaptureConfig f$1;

    public /* synthetic */ ProcessingCaptureSession$2$$ExternalSyntheticLambda1(ProcessingCaptureSession.AnonymousClass2 r1, CaptureConfig captureConfig) {
        this.f$0 = r1;
        this.f$1 = captureConfig;
    }

    public final void run() {
        this.f$0.m77lambda$onCaptureSequenceCompleted$1$androidxcameracamera2internalProcessingCaptureSession$2(this.f$1);
    }
}

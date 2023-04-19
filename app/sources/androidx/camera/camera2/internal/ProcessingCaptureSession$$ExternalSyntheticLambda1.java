package androidx.camera.camera2.internal;

import android.hardware.camera2.CameraDevice;
import androidx.camera.core.impl.SessionConfig;
import androidx.camera.core.impl.utils.futures.AsyncFunction;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.List;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class ProcessingCaptureSession$$ExternalSyntheticLambda1 implements AsyncFunction {
    public final /* synthetic */ ProcessingCaptureSession f$0;
    public final /* synthetic */ SessionConfig f$1;
    public final /* synthetic */ CameraDevice f$2;
    public final /* synthetic */ SynchronizedCaptureSessionOpener f$3;

    public /* synthetic */ ProcessingCaptureSession$$ExternalSyntheticLambda1(ProcessingCaptureSession processingCaptureSession, SessionConfig sessionConfig, CameraDevice cameraDevice, SynchronizedCaptureSessionOpener synchronizedCaptureSessionOpener) {
        this.f$0 = processingCaptureSession;
        this.f$1 = sessionConfig;
        this.f$2 = cameraDevice;
        this.f$3 = synchronizedCaptureSessionOpener;
    }

    public final ListenableFuture apply(Object obj) {
        return this.f$0.m74lambda$open$2$androidxcameracamera2internalProcessingCaptureSession(this.f$1, this.f$2, this.f$3, (List) obj);
    }
}

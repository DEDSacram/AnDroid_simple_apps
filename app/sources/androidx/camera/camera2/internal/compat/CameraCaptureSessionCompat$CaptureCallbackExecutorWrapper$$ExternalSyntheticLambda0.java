package androidx.camera.camera2.internal.compat;

import android.hardware.camera2.CameraCaptureSession;
import androidx.camera.camera2.internal.compat.CameraCaptureSessionCompat;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class CameraCaptureSessionCompat$CaptureCallbackExecutorWrapper$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ CameraCaptureSessionCompat.CaptureCallbackExecutorWrapper f$0;
    public final /* synthetic */ CameraCaptureSession f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ CameraCaptureSessionCompat$CaptureCallbackExecutorWrapper$$ExternalSyntheticLambda0(CameraCaptureSessionCompat.CaptureCallbackExecutorWrapper captureCallbackExecutorWrapper, CameraCaptureSession cameraCaptureSession, int i) {
        this.f$0 = captureCallbackExecutorWrapper;
        this.f$1 = cameraCaptureSession;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.m101lambda$onCaptureSequenceAborted$5$androidxcameracamera2internalcompatCameraCaptureSessionCompat$CaptureCallbackExecutorWrapper(this.f$1, this.f$2);
    }
}

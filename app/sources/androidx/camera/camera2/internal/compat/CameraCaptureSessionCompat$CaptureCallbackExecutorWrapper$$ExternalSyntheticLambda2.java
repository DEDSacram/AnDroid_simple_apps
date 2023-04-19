package androidx.camera.camera2.internal.compat;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import androidx.camera.camera2.internal.compat.CameraCaptureSessionCompat;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class CameraCaptureSessionCompat$CaptureCallbackExecutorWrapper$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ CameraCaptureSessionCompat.CaptureCallbackExecutorWrapper f$0;
    public final /* synthetic */ CameraCaptureSession f$1;
    public final /* synthetic */ CaptureRequest f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ long f$4;

    public /* synthetic */ CameraCaptureSessionCompat$CaptureCallbackExecutorWrapper$$ExternalSyntheticLambda2(CameraCaptureSessionCompat.CaptureCallbackExecutorWrapper captureCallbackExecutorWrapper, CameraCaptureSession cameraCaptureSession, CaptureRequest captureRequest, long j, long j2) {
        this.f$0 = captureCallbackExecutorWrapper;
        this.f$1 = cameraCaptureSession;
        this.f$2 = captureRequest;
        this.f$3 = j;
        this.f$4 = j2;
    }

    public final void run() {
        this.f$0.m103lambda$onCaptureStarted$0$androidxcameracamera2internalcompatCameraCaptureSessionCompat$CaptureCallbackExecutorWrapper(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

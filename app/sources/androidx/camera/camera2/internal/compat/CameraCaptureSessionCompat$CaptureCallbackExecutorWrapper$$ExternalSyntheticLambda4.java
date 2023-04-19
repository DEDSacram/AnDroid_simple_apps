package androidx.camera.camera2.internal.compat;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import androidx.camera.camera2.internal.compat.CameraCaptureSessionCompat;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class CameraCaptureSessionCompat$CaptureCallbackExecutorWrapper$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ CameraCaptureSessionCompat.CaptureCallbackExecutorWrapper f$0;
    public final /* synthetic */ CameraCaptureSession f$1;
    public final /* synthetic */ CaptureRequest f$2;
    public final /* synthetic */ CaptureResult f$3;

    public /* synthetic */ CameraCaptureSessionCompat$CaptureCallbackExecutorWrapper$$ExternalSyntheticLambda4(CameraCaptureSessionCompat.CaptureCallbackExecutorWrapper captureCallbackExecutorWrapper, CameraCaptureSession cameraCaptureSession, CaptureRequest captureRequest, CaptureResult captureResult) {
        this.f$0 = captureCallbackExecutorWrapper;
        this.f$1 = cameraCaptureSession;
        this.f$2 = captureRequest;
        this.f$3 = captureResult;
    }

    public final void run() {
        this.f$0.m100lambda$onCaptureProgressed$1$androidxcameracamera2internalcompatCameraCaptureSessionCompat$CaptureCallbackExecutorWrapper(this.f$1, this.f$2, this.f$3);
    }
}

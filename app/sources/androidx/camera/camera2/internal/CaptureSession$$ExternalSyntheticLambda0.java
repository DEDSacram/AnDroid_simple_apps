package androidx.camera.camera2.internal;

import android.hardware.camera2.CameraCaptureSession;
import androidx.camera.camera2.internal.CameraBurstCaptureCallback;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class CaptureSession$$ExternalSyntheticLambda0 implements CameraBurstCaptureCallback.CaptureSequenceCallback {
    public final /* synthetic */ CaptureSession f$0;

    public /* synthetic */ CaptureSession$$ExternalSyntheticLambda0(CaptureSession captureSession) {
        this.f$0 = captureSession;
    }

    public final void onCaptureSequenceCompletedOrAborted(CameraCaptureSession cameraCaptureSession, int i, boolean z) {
        this.f$0.m58lambda$issueBurstCaptureRequest$2$androidxcameracamera2internalCaptureSession(cameraCaptureSession, i, z);
    }
}

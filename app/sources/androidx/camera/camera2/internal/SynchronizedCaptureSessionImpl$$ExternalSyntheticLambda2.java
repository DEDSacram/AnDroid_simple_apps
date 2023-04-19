package androidx.camera.camera2.internal;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import androidx.camera.camera2.internal.compat.workaround.WaitForRepeatingRequestStart;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class SynchronizedCaptureSessionImpl$$ExternalSyntheticLambda2 implements WaitForRepeatingRequestStart.SingleRepeatingRequest {
    public final /* synthetic */ SynchronizedCaptureSessionImpl f$0;

    public /* synthetic */ SynchronizedCaptureSessionImpl$$ExternalSyntheticLambda2(SynchronizedCaptureSessionImpl synchronizedCaptureSessionImpl) {
        this.f$0 = synchronizedCaptureSessionImpl;
    }

    public final int run(CaptureRequest captureRequest, CameraCaptureSession.CaptureCallback captureCallback) {
        return this.f$0.m88lambda$setSingleRepeatingRequest$1$androidxcameracamera2internalSynchronizedCaptureSessionImpl(captureRequest, captureCallback);
    }
}

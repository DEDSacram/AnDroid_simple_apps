package androidx.camera.camera2.internal;

import android.hardware.camera2.TotalCaptureResult;
import androidx.camera.camera2.internal.Camera2CameraControlImpl;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class FocusMeteringControl$$ExternalSyntheticLambda0 implements Camera2CameraControlImpl.CaptureResultListener {
    public final /* synthetic */ FocusMeteringControl f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ FocusMeteringControl$$ExternalSyntheticLambda0(FocusMeteringControl focusMeteringControl, int i, long j) {
        this.f$0 = focusMeteringControl;
        this.f$1 = i;
        this.f$2 = j;
    }

    public final boolean onCaptureResult(TotalCaptureResult totalCaptureResult) {
        return this.f$0.m65lambda$cancelFocusAndMeteringInternal$9$androidxcameracamera2internalFocusMeteringControl(this.f$1, this.f$2, totalCaptureResult);
    }
}

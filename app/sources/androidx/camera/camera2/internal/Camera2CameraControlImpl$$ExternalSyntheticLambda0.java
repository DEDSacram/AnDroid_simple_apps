package androidx.camera.camera2.internal;

import android.hardware.camera2.TotalCaptureResult;
import androidx.camera.camera2.internal.Camera2CameraControlImpl;
import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class Camera2CameraControlImpl$$ExternalSyntheticLambda0 implements Camera2CameraControlImpl.CaptureResultListener {
    public final /* synthetic */ long f$0;
    public final /* synthetic */ CallbackToFutureAdapter.Completer f$1;

    public /* synthetic */ Camera2CameraControlImpl$$ExternalSyntheticLambda0(long j, CallbackToFutureAdapter.Completer completer) {
        this.f$0 = j;
        this.f$1 = completer;
    }

    public final boolean onCaptureResult(TotalCaptureResult totalCaptureResult) {
        return Camera2CameraControlImpl.lambda$waitForSessionUpdateId$3(this.f$0, this.f$1, totalCaptureResult);
    }
}

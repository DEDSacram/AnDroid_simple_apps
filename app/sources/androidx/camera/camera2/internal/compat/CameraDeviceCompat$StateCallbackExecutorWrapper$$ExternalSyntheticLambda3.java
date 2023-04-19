package androidx.camera.camera2.internal.compat;

import android.hardware.camera2.CameraDevice;
import androidx.camera.camera2.internal.compat.CameraDeviceCompat;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class CameraDeviceCompat$StateCallbackExecutorWrapper$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ CameraDeviceCompat.StateCallbackExecutorWrapper f$0;
    public final /* synthetic */ CameraDevice f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ CameraDeviceCompat$StateCallbackExecutorWrapper$$ExternalSyntheticLambda3(CameraDeviceCompat.StateCallbackExecutorWrapper stateCallbackExecutorWrapper, CameraDevice cameraDevice, int i) {
        this.f$0 = stateCallbackExecutorWrapper;
        this.f$1 = cameraDevice;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.m113lambda$onError$2$androidxcameracamera2internalcompatCameraDeviceCompat$StateCallbackExecutorWrapper(this.f$1, this.f$2);
    }
}

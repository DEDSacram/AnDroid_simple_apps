package androidx.camera.camera2.internal.compat;

import androidx.camera.camera2.internal.compat.CameraManagerCompat;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class CameraManagerCompat$AvailabilityCallbackExecutorWrapper$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ CameraManagerCompat.AvailabilityCallbackExecutorWrapper f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ CameraManagerCompat$AvailabilityCallbackExecutorWrapper$$ExternalSyntheticLambda2(CameraManagerCompat.AvailabilityCallbackExecutorWrapper availabilityCallbackExecutorWrapper, String str) {
        this.f$0 = availabilityCallbackExecutorWrapper;
        this.f$1 = str;
    }

    public final void run() {
        this.f$0.m117lambda$onCameraUnavailable$2$androidxcameracamera2internalcompatCameraManagerCompat$AvailabilityCallbackExecutorWrapper(this.f$1);
    }
}

package androidx.camera.camera2.internal;

import android.hardware.camera2.CameraDevice;
import androidx.camera.camera2.internal.compat.params.SessionConfigurationCompat;
import androidx.camera.camera2.internal.compat.workaround.WaitForRepeatingRequestStart;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.List;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class SynchronizedCaptureSessionImpl$$ExternalSyntheticLambda1 implements WaitForRepeatingRequestStart.OpenCaptureSession {
    public final /* synthetic */ SynchronizedCaptureSessionImpl f$0;

    public /* synthetic */ SynchronizedCaptureSessionImpl$$ExternalSyntheticLambda1(SynchronizedCaptureSessionImpl synchronizedCaptureSessionImpl) {
        this.f$0 = synchronizedCaptureSessionImpl;
    }

    public final ListenableFuture run(CameraDevice cameraDevice, SessionConfigurationCompat sessionConfigurationCompat, List list) {
        return this.f$0.m87lambda$openCaptureSession$0$androidxcameracamera2internalSynchronizedCaptureSessionImpl(cameraDevice, sessionConfigurationCompat, list);
    }
}

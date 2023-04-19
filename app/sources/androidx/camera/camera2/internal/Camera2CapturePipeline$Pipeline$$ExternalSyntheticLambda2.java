package androidx.camera.camera2.internal;

import android.hardware.camera2.TotalCaptureResult;
import androidx.camera.camera2.internal.Camera2CapturePipeline;
import androidx.camera.core.impl.utils.futures.AsyncFunction;
import com.google.common.util.concurrent.ListenableFuture;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class Camera2CapturePipeline$Pipeline$$ExternalSyntheticLambda2 implements AsyncFunction {
    public final /* synthetic */ Camera2CapturePipeline.Pipeline f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ Camera2CapturePipeline$Pipeline$$ExternalSyntheticLambda2(Camera2CapturePipeline.Pipeline pipeline, int i) {
        this.f$0 = pipeline;
        this.f$1 = i;
    }

    public final ListenableFuture apply(Object obj) {
        return this.f$0.m51lambda$executeCapture$0$androidxcameracamera2internalCamera2CapturePipeline$Pipeline(this.f$1, (TotalCaptureResult) obj);
    }
}

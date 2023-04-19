package androidx.camera.camera2.internal;

import android.hardware.camera2.TotalCaptureResult;
import androidx.camera.camera2.internal.Camera2CapturePipeline;
import androidx.camera.core.impl.utils.futures.AsyncFunction;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.List;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class Camera2CapturePipeline$Pipeline$$ExternalSyntheticLambda3 implements AsyncFunction {
    public final /* synthetic */ Camera2CapturePipeline.Pipeline f$0;
    public final /* synthetic */ List f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ Camera2CapturePipeline$Pipeline$$ExternalSyntheticLambda3(Camera2CapturePipeline.Pipeline pipeline, List list, int i) {
        this.f$0 = pipeline;
        this.f$1 = list;
        this.f$2 = i;
    }

    public final ListenableFuture apply(Object obj) {
        return this.f$0.m53lambda$executeCapture$3$androidxcameracamera2internalCamera2CapturePipeline$Pipeline(this.f$1, this.f$2, (TotalCaptureResult) obj);
    }
}

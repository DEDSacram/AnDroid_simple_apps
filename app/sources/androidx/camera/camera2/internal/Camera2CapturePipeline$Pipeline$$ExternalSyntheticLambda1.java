package androidx.camera.camera2.internal;

import androidx.camera.camera2.internal.Camera2CapturePipeline;
import androidx.camera.core.impl.utils.futures.AsyncFunction;
import com.google.common.util.concurrent.ListenableFuture;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class Camera2CapturePipeline$Pipeline$$ExternalSyntheticLambda1 implements AsyncFunction {
    public final /* synthetic */ Camera2CapturePipeline.Pipeline f$0;

    public /* synthetic */ Camera2CapturePipeline$Pipeline$$ExternalSyntheticLambda1(Camera2CapturePipeline.Pipeline pipeline) {
        this.f$0 = pipeline;
    }

    public final ListenableFuture apply(Object obj) {
        return this.f$0.m52lambda$executeCapture$2$androidxcameracamera2internalCamera2CapturePipeline$Pipeline((Boolean) obj);
    }
}

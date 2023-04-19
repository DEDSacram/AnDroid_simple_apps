package androidx.camera.camera2.internal;

import androidx.camera.camera2.internal.Camera2CapturePipeline;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class Camera2CapturePipeline$Pipeline$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ Camera2CapturePipeline.PipelineTask f$0;

    public /* synthetic */ Camera2CapturePipeline$Pipeline$$ExternalSyntheticLambda5(Camera2CapturePipeline.PipelineTask pipelineTask) {
        this.f$0 = pipelineTask;
    }

    public final void run() {
        this.f$0.postCapture();
    }
}

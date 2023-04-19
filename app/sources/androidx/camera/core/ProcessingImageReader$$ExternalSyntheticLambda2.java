package androidx.camera.core;

import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class ProcessingImageReader$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ ProcessingImageReader f$0;
    public final /* synthetic */ CallbackToFutureAdapter.Completer f$1;

    public /* synthetic */ ProcessingImageReader$$ExternalSyntheticLambda2(ProcessingImageReader processingImageReader, CallbackToFutureAdapter.Completer completer) {
        this.f$0 = processingImageReader;
        this.f$1 = completer;
    }

    public final void run() {
        this.f$0.m154lambda$closeAndCompleteFutureIfNecessary$0$androidxcameracoreProcessingImageReader(this.f$1);
    }
}

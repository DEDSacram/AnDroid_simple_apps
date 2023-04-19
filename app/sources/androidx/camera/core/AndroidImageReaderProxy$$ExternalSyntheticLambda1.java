package androidx.camera.core;

import androidx.camera.core.impl.ImageReaderProxy;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class AndroidImageReaderProxy$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ AndroidImageReaderProxy f$0;
    public final /* synthetic */ ImageReaderProxy.OnImageAvailableListener f$1;

    public /* synthetic */ AndroidImageReaderProxy$$ExternalSyntheticLambda1(AndroidImageReaderProxy androidImageReaderProxy, ImageReaderProxy.OnImageAvailableListener onImageAvailableListener) {
        this.f$0 = androidImageReaderProxy;
        this.f$1 = onImageAvailableListener;
    }

    public final void run() {
        this.f$0.m127lambda$setOnImageAvailableListener$0$androidxcameracoreAndroidImageReaderProxy(this.f$1);
    }
}

package androidx.camera.core;

import androidx.camera.core.ImageSaver;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class ImageSaver$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ImageSaver f$0;
    public final /* synthetic */ ImageSaver.SaveError f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ Throwable f$3;

    public /* synthetic */ ImageSaver$$ExternalSyntheticLambda1(ImageSaver imageSaver, ImageSaver.SaveError saveError, String str, Throwable th) {
        this.f$0 = imageSaver;
        this.f$1 = saveError;
        this.f$2 = str;
        this.f$3 = th;
    }

    public final void run() {
        this.f$0.m148lambda$postError$2$androidxcameracoreImageSaver(this.f$1, this.f$2, this.f$3);
    }
}

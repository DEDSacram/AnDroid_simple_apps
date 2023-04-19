package androidx.camera.core;

import androidx.camera.core.VideoCapture;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class VideoCapture$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ VideoCapture f$0;
    public final /* synthetic */ VideoCapture.OnVideoSavedCallback f$1;

    public /* synthetic */ VideoCapture$$ExternalSyntheticLambda5(VideoCapture videoCapture, VideoCapture.OnVideoSavedCallback onVideoSavedCallback) {
        this.f$0 = videoCapture;
        this.f$1 = onVideoSavedCallback;
    }

    public final void run() {
        this.f$0.m164lambda$startRecording$3$androidxcameracoreVideoCapture(this.f$1);
    }
}

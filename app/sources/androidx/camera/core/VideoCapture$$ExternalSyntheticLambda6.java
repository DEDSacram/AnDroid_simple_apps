package androidx.camera.core;

import android.util.Size;
import androidx.camera.core.VideoCapture;
import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class VideoCapture$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ VideoCapture f$0;
    public final /* synthetic */ VideoCapture.OnVideoSavedCallback f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ Size f$3;
    public final /* synthetic */ VideoCapture.OutputFileOptions f$4;
    public final /* synthetic */ CallbackToFutureAdapter.Completer f$5;

    public /* synthetic */ VideoCapture$$ExternalSyntheticLambda6(VideoCapture videoCapture, VideoCapture.OnVideoSavedCallback onVideoSavedCallback, String str, Size size, VideoCapture.OutputFileOptions outputFileOptions, CallbackToFutureAdapter.Completer completer) {
        this.f$0 = videoCapture;
        this.f$1 = onVideoSavedCallback;
        this.f$2 = str;
        this.f$3 = size;
        this.f$4 = outputFileOptions;
        this.f$5 = completer;
    }

    public final void run() {
        this.f$0.m165lambda$startRecording$4$androidxcameracoreVideoCapture(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}

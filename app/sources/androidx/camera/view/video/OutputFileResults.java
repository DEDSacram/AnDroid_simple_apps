package androidx.camera.view.video;

import android.net.Uri;

public abstract class OutputFileResults {
    public abstract Uri getSavedUri();

    OutputFileResults() {
    }

    public static OutputFileResults create(Uri savedUri) {
        return new AutoValue_OutputFileResults(savedUri);
    }
}

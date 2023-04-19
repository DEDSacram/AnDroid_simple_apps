package androidx.camera.core.impl;

import android.content.Context;
import androidx.camera.core.CameraInfo;

public interface CameraConfigProvider {
    public static final CameraConfigProvider EMPTY = CameraConfigProvider$$ExternalSyntheticLambda0.INSTANCE;

    CameraConfig getConfig(CameraInfo cameraInfo, Context context);

    static /* synthetic */ CameraConfig lambda$static$0(CameraInfo cameraInfo, Context context) {
        return null;
    }
}

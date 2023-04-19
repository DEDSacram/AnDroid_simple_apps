package androidx.camera.camera2.internal;

import android.os.Build;
import androidx.camera.core.impl.ImageAnalysisConfig;
import androidx.camera.core.impl.ImageCaptureConfig;
import androidx.camera.core.impl.PreviewConfig;
import androidx.camera.core.impl.SessionConfig;
import androidx.camera.core.impl.UseCaseConfig;
import androidx.camera.core.impl.VideoCaptureConfig;
import java.util.Collection;

public final class StreamUseCaseUtil {
    private StreamUseCaseUtil() {
    }

    public static long getStreamUseCaseFromUseCaseConfigs(Collection<UseCaseConfig<?>> useCaseConfigs, Collection<SessionConfig> sessionConfigs) {
        if (Build.VERSION.SDK_INT < 33 || useCaseConfigs.isEmpty()) {
            return -1;
        }
        for (SessionConfig sessionConfig : sessionConfigs) {
            if (sessionConfig.getTemplateType() == 5) {
                return -1;
            }
        }
        boolean hasImageCapture = false;
        boolean hasVideoCapture = false;
        for (UseCaseConfig<?> useCaseConfig : useCaseConfigs) {
            if (useCaseConfig instanceof ImageAnalysisConfig) {
                return -1;
            }
            if (!(useCaseConfig instanceof PreviewConfig)) {
                if (useCaseConfig instanceof ImageCaptureConfig) {
                    if (hasVideoCapture) {
                        return -1;
                    }
                    hasImageCapture = true;
                } else if (!(useCaseConfig instanceof VideoCaptureConfig)) {
                    continue;
                } else if (hasImageCapture) {
                    return -1;
                } else {
                    hasVideoCapture = true;
                }
            }
        }
        return -1;
    }
}

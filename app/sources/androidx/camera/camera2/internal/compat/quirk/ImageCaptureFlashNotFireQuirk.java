package androidx.camera.camera2.internal.compat.quirk;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;
import androidx.camera.camera2.internal.compat.CameraCharacteristicsCompat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ImageCaptureFlashNotFireQuirk implements UseTorchAsFlashQuirk {
    private static final List<String> BUILD_MODELS = Arrays.asList(new String[]{"itel w6004"});
    private static final List<String> BUILD_MODELS_FRONT_CAMERA = Arrays.asList(new String[]{"sm-j700f", "sm-j710f"});

    static boolean load(CameraCharacteristicsCompat cameraCharacteristics) {
        boolean isFrontCameraAffected = BUILD_MODELS_FRONT_CAMERA.contains(Build.MODEL.toLowerCase(Locale.US)) && ((Integer) cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)).intValue() == 0;
        boolean isAffected = BUILD_MODELS.contains(Build.MODEL.toLowerCase(Locale.US));
        if (isFrontCameraAffected || isAffected) {
            return true;
        }
        return false;
    }
}

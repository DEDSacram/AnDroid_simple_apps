package androidx.camera.camera2.internal.compat.quirk;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;
import androidx.camera.camera2.internal.compat.CameraCharacteristicsCompat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FlashTooSlowQuirk implements UseTorchAsFlashQuirk {
    private static final List<String> AFFECTED_MODELS = Arrays.asList(new String[]{"PIXEL 3A", "PIXEL 3A XL"});

    static boolean load(CameraCharacteristicsCompat cameraCharacteristics) {
        if (!AFFECTED_MODELS.contains(Build.MODEL.toUpperCase(Locale.US)) || ((Integer) cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)).intValue() != 1) {
            return false;
        }
        return true;
    }
}

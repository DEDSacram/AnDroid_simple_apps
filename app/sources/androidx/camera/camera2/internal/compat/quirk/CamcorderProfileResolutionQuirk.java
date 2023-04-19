package androidx.camera.camera2.internal.compat.quirk;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.util.Size;
import androidx.camera.camera2.internal.compat.CameraCharacteristicsCompat;
import androidx.camera.core.Logger;
import androidx.camera.core.impl.Quirk;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CamcorderProfileResolutionQuirk implements Quirk {
    private static final String TAG = "CamcorderProfileResolutionQuirk";
    private final List<Size> mSupportedResolutions;

    static boolean load(CameraCharacteristicsCompat characteristicsCompat) {
        Integer level = (Integer) characteristicsCompat.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
        return level != null && level.intValue() == 2;
    }

    public CamcorderProfileResolutionQuirk(CameraCharacteristicsCompat characteristicsCompat) {
        Size[] sizes;
        List<Size> list;
        StreamConfigurationMap map = (StreamConfigurationMap) characteristicsCompat.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (map == null) {
            Logger.e(TAG, "StreamConfigurationMap is null");
        }
        Size[] sizeArr = null;
        if (Build.VERSION.SDK_INT < 23) {
            sizes = map != null ? map.getOutputSizes(SurfaceTexture.class) : sizeArr;
        } else {
            sizes = map != null ? map.getOutputSizes(34) : sizeArr;
        }
        if (sizes != null) {
            list = Arrays.asList((Size[]) sizes.clone());
        } else {
            list = Collections.emptyList();
        }
        this.mSupportedResolutions = list;
        Logger.d(TAG, "mSupportedResolutions = " + list);
    }

    public List<Size> getSupportedResolutions() {
        return new ArrayList(this.mSupportedResolutions);
    }
}

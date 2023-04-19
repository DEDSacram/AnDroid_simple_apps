package androidx.camera.camera2.internal.compat.quirk;

import android.os.Build;
import android.util.Size;
import androidx.camera.core.Logger;
import androidx.camera.core.impl.Quirk;
import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExcludedSupportedSizesQuirk implements Quirk {
    private static final String TAG = "ExcludedSupportedSizesQuirk";

    static boolean load() {
        return isOnePlus6() || isOnePlus6T() || isHuaweiP20Lite();
    }

    private static boolean isOnePlus6() {
        return "OnePlus".equalsIgnoreCase(Build.BRAND) && "OnePlus6".equalsIgnoreCase(Build.DEVICE);
    }

    private static boolean isOnePlus6T() {
        return "OnePlus".equalsIgnoreCase(Build.BRAND) && "OnePlus6T".equalsIgnoreCase(Build.DEVICE);
    }

    private static boolean isHuaweiP20Lite() {
        return "HUAWEI".equalsIgnoreCase(Build.BRAND) && "HWANE".equalsIgnoreCase(Build.DEVICE);
    }

    public List<Size> getExcludedSizes(String cameraId, int imageFormat) {
        if (isOnePlus6()) {
            return getOnePlus6ExcludedSizes(cameraId, imageFormat);
        }
        if (isOnePlus6T()) {
            return getOnePlus6TExcludedSizes(cameraId, imageFormat);
        }
        if (isHuaweiP20Lite()) {
            return getHuaweiP20LiteExcludedSizes(cameraId, imageFormat);
        }
        Logger.w(TAG, "Cannot retrieve list of supported sizes to exclude on this device.");
        return Collections.emptyList();
    }

    private List<Size> getOnePlus6ExcludedSizes(String cameraId, int imageFormat) {
        List<Size> sizes = new ArrayList<>();
        if (cameraId.equals("0") && imageFormat == 256) {
            sizes.add(new Size(4160, 3120));
            sizes.add(new Size(4000, PathInterpolatorCompat.MAX_NUM_POINTS));
        }
        return sizes;
    }

    private List<Size> getOnePlus6TExcludedSizes(String cameraId, int imageFormat) {
        List<Size> sizes = new ArrayList<>();
        if (cameraId.equals("0") && imageFormat == 256) {
            sizes.add(new Size(4160, 3120));
            sizes.add(new Size(4000, PathInterpolatorCompat.MAX_NUM_POINTS));
        }
        return sizes;
    }

    private List<Size> getHuaweiP20LiteExcludedSizes(String cameraId, int imageFormat) {
        List<Size> sizes = new ArrayList<>();
        if (cameraId.equals("0") && (imageFormat == 34 || imageFormat == 35)) {
            sizes.add(new Size(720, 720));
            sizes.add(new Size(400, 400));
        }
        return sizes;
    }
}

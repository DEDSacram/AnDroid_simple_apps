package androidx.camera.camera2.internal.compat.workaround;

import android.util.Size;
import androidx.camera.camera2.internal.compat.quirk.DeviceQuirks;
import androidx.camera.camera2.internal.compat.quirk.ExcludedSupportedSizesQuirk;
import java.util.ArrayList;
import java.util.List;

public class ExcludedSupportedSizesContainer {
    private final String mCameraId;

    public ExcludedSupportedSizesContainer(String cameraId) {
        this.mCameraId = cameraId;
    }

    public List<Size> get(int imageFormat) {
        ExcludedSupportedSizesQuirk quirk = (ExcludedSupportedSizesQuirk) DeviceQuirks.get(ExcludedSupportedSizesQuirk.class);
        if (quirk == null) {
            return new ArrayList();
        }
        return quirk.getExcludedSizes(this.mCameraId, imageFormat);
    }
}

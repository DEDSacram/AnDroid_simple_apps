package androidx.camera.core.internal.compat.workaround;

import androidx.camera.core.ImageProxy;
import androidx.camera.core.impl.CaptureConfig;
import androidx.camera.core.internal.compat.quirk.DeviceQuirks;
import androidx.camera.core.internal.compat.quirk.ImageCaptureRotationOptionQuirk;

public class ExifRotationAvailability {
    public boolean isRotationOptionSupported() {
        ImageCaptureRotationOptionQuirk quirk = (ImageCaptureRotationOptionQuirk) DeviceQuirks.get(ImageCaptureRotationOptionQuirk.class);
        if (quirk != null) {
            return quirk.isSupported(CaptureConfig.OPTION_ROTATION);
        }
        return true;
    }

    public boolean shouldUseExifOrientation(ImageProxy image) {
        ImageCaptureRotationOptionQuirk quirk = (ImageCaptureRotationOptionQuirk) DeviceQuirks.get(ImageCaptureRotationOptionQuirk.class);
        return (quirk == null || quirk.isSupported(CaptureConfig.OPTION_ROTATION)) && image.getFormat() == 256;
    }
}

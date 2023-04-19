package androidx.camera.camera2.internal.compat.workaround;

import android.hardware.camera2.CaptureRequest;
import androidx.camera.camera2.impl.Camera2ImplConfig;
import androidx.camera.camera2.internal.compat.quirk.DeviceQuirks;
import androidx.camera.camera2.internal.compat.quirk.ImageCapturePixelHDRPlusQuirk;

public class ImageCapturePixelHDRPlus {
    public void toggleHDRPlus(int captureMode, Camera2ImplConfig.Builder builder) {
        if (((ImageCapturePixelHDRPlusQuirk) DeviceQuirks.get(ImageCapturePixelHDRPlusQuirk.class)) != null) {
            switch (captureMode) {
                case 0:
                    builder.setCaptureRequestOption(CaptureRequest.CONTROL_ENABLE_ZSL, true);
                    return;
                case 1:
                    builder.setCaptureRequestOption(CaptureRequest.CONTROL_ENABLE_ZSL, false);
                    return;
                default:
                    return;
            }
        }
    }
}

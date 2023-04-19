package androidx.camera.camera2.internal.compat.workaround;

import android.hardware.camera2.CaptureRequest;
import androidx.camera.camera2.impl.Camera2ImplConfig;
import androidx.camera.camera2.internal.compat.quirk.DeviceQuirks;
import androidx.camera.camera2.internal.compat.quirk.PreviewPixelHDRnetQuirk;
import androidx.camera.core.impl.SessionConfig;

public class PreviewPixelHDRnet {
    private PreviewPixelHDRnet() {
    }

    public static void setHDRnet(SessionConfig.Builder sessionBuilder) {
        if (((PreviewPixelHDRnetQuirk) DeviceQuirks.get(PreviewPixelHDRnetQuirk.class)) != null) {
            Camera2ImplConfig.Builder camera2ConfigBuilder = new Camera2ImplConfig.Builder();
            camera2ConfigBuilder.setCaptureRequestOption(CaptureRequest.TONEMAP_MODE, 2);
            sessionBuilder.addImplementationOptions(camera2ConfigBuilder.build());
        }
    }
}

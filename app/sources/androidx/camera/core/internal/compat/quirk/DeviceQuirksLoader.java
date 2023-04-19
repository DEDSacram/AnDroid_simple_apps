package androidx.camera.core.internal.compat.quirk;

import androidx.camera.core.impl.Quirk;
import java.util.ArrayList;
import java.util.List;

public class DeviceQuirksLoader {
    private DeviceQuirksLoader() {
    }

    static List<Quirk> loadQuirks() {
        List<Quirk> quirks = new ArrayList<>();
        if (ImageCaptureRotationOptionQuirk.load()) {
            quirks.add(new ImageCaptureRotationOptionQuirk());
        }
        if (SurfaceOrderQuirk.load()) {
            quirks.add(new SurfaceOrderQuirk());
        }
        return quirks;
    }
}

package androidx.camera.view.internal.compat.quirk;

import androidx.camera.core.impl.Quirk;
import java.util.ArrayList;
import java.util.List;

public class DeviceQuirksLoader {
    private DeviceQuirksLoader() {
    }

    static List<Quirk> loadQuirks() {
        List<Quirk> quirks = new ArrayList<>();
        if (SurfaceViewStretchedQuirk.load()) {
            quirks.add(new SurfaceViewStretchedQuirk());
        }
        if (SurfaceViewNotCroppedByParentQuirk.load()) {
            quirks.add(new SurfaceViewNotCroppedByParentQuirk());
        }
        return quirks;
    }
}

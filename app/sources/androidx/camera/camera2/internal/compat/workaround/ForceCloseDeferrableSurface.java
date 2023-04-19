package androidx.camera.camera2.internal.compat.workaround;

import androidx.camera.camera2.internal.compat.quirk.ConfigureSurfaceToSecondarySessionFailQuirk;
import androidx.camera.camera2.internal.compat.quirk.PreviewOrientationIncorrectQuirk;
import androidx.camera.camera2.internal.compat.quirk.TextureViewIsClosedQuirk;
import androidx.camera.core.Logger;
import androidx.camera.core.impl.DeferrableSurface;
import androidx.camera.core.impl.Quirks;
import java.util.List;

public class ForceCloseDeferrableSurface {
    private final boolean mHasConfigureSurfaceToSecondarySessionFailQuirk;
    private final boolean mHasPreviewOrientationIncorrectQuirk;
    private final boolean mHasTextureViewIsClosedQuirk;

    public ForceCloseDeferrableSurface(Quirks cameraQuirks, Quirks deviceQuirks) {
        this.mHasTextureViewIsClosedQuirk = deviceQuirks.contains(TextureViewIsClosedQuirk.class);
        this.mHasPreviewOrientationIncorrectQuirk = cameraQuirks.contains(PreviewOrientationIncorrectQuirk.class);
        this.mHasConfigureSurfaceToSecondarySessionFailQuirk = cameraQuirks.contains(ConfigureSurfaceToSecondarySessionFailQuirk.class);
    }

    public boolean shouldForceClose() {
        return this.mHasTextureViewIsClosedQuirk || this.mHasPreviewOrientationIncorrectQuirk || this.mHasConfigureSurfaceToSecondarySessionFailQuirk;
    }

    public void onSessionEnd(List<DeferrableSurface> deferrableSurfaces) {
        if (shouldForceClose() && deferrableSurfaces != null) {
            for (DeferrableSurface deferrableSurface : deferrableSurfaces) {
                deferrableSurface.close();
            }
            Logger.d("ForceCloseDeferrableSurface", "deferrableSurface closed");
        }
    }
}

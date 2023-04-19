package androidx.camera.camera2.internal.compat.workaround;

import android.graphics.PointF;
import androidx.camera.camera2.internal.compat.quirk.AfRegionFlipHorizontallyQuirk;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.impl.Quirks;

public class MeteringRegionCorrection {
    private final Quirks mCameraQuirks;

    public MeteringRegionCorrection(Quirks cameraQuirks) {
        this.mCameraQuirks = cameraQuirks;
    }

    public PointF getCorrectedPoint(MeteringPoint meteringPoint, int meteringMode) {
        if (meteringMode != 1 || !this.mCameraQuirks.contains(AfRegionFlipHorizontallyQuirk.class)) {
            return new PointF(meteringPoint.getX(), meteringPoint.getY());
        }
        return new PointF(1.0f - meteringPoint.getX(), meteringPoint.getY());
    }
}

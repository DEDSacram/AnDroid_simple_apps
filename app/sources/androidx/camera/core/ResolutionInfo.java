package androidx.camera.core;

import android.graphics.Rect;
import android.util.Size;

public abstract class ResolutionInfo {
    public abstract Rect getCropRect();

    public abstract Size getResolution();

    public abstract int getRotationDegrees();

    static ResolutionInfo create(Size resolution, Rect cropRect, int rotationDegrees) {
        return new AutoValue_ResolutionInfo(resolution, cropRect, rotationDegrees);
    }

    ResolutionInfo() {
    }
}

package androidx.camera.core.impl;

import android.util.Range;
import android.util.Size;

public abstract class AttachedSurfaceInfo {
    public abstract int getImageFormat();

    public abstract Size getSize();

    public abstract SurfaceConfig getSurfaceConfig();

    public abstract Range<Integer> getTargetFrameRate();

    AttachedSurfaceInfo() {
    }

    public static AttachedSurfaceInfo create(SurfaceConfig surfaceConfig, int imageFormat, Size size, Range<Integer> targetFrameRate) {
        return new AutoValue_AttachedSurfaceInfo(surfaceConfig, imageFormat, size, targetFrameRate);
    }
}

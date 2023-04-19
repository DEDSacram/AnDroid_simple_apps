package androidx.camera.core.impl;

import android.util.Range;
import android.util.Size;

final class AutoValue_AttachedSurfaceInfo extends AttachedSurfaceInfo {
    private final int imageFormat;
    private final Size size;
    private final SurfaceConfig surfaceConfig;
    private final Range<Integer> targetFrameRate;

    AutoValue_AttachedSurfaceInfo(SurfaceConfig surfaceConfig2, int imageFormat2, Size size2, Range<Integer> targetFrameRate2) {
        if (surfaceConfig2 != null) {
            this.surfaceConfig = surfaceConfig2;
            this.imageFormat = imageFormat2;
            if (size2 != null) {
                this.size = size2;
                this.targetFrameRate = targetFrameRate2;
                return;
            }
            throw new NullPointerException("Null size");
        }
        throw new NullPointerException("Null surfaceConfig");
    }

    public SurfaceConfig getSurfaceConfig() {
        return this.surfaceConfig;
    }

    public int getImageFormat() {
        return this.imageFormat;
    }

    public Size getSize() {
        return this.size;
    }

    public Range<Integer> getTargetFrameRate() {
        return this.targetFrameRate;
    }

    public String toString() {
        return "AttachedSurfaceInfo{surfaceConfig=" + this.surfaceConfig + ", imageFormat=" + this.imageFormat + ", size=" + this.size + ", targetFrameRate=" + this.targetFrameRate + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AttachedSurfaceInfo)) {
            return false;
        }
        AttachedSurfaceInfo that = (AttachedSurfaceInfo) o;
        if (this.surfaceConfig.equals(that.getSurfaceConfig()) && this.imageFormat == that.getImageFormat() && this.size.equals(that.getSize())) {
            Range<Integer> range = this.targetFrameRate;
            if (range == null) {
                if (that.getTargetFrameRate() == null) {
                    return true;
                }
            } else if (range.equals(that.getTargetFrameRate())) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        int h$ = ((((((1 * 1000003) ^ this.surfaceConfig.hashCode()) * 1000003) ^ this.imageFormat) * 1000003) ^ this.size.hashCode()) * 1000003;
        Range<Integer> range = this.targetFrameRate;
        return h$ ^ (range == null ? 0 : range.hashCode());
    }
}

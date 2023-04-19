package androidx.camera.core;

import android.graphics.Rect;
import android.util.Size;

final class AutoValue_ResolutionInfo extends ResolutionInfo {
    private final Rect cropRect;
    private final Size resolution;
    private final int rotationDegrees;

    AutoValue_ResolutionInfo(Size resolution2, Rect cropRect2, int rotationDegrees2) {
        if (resolution2 != null) {
            this.resolution = resolution2;
            if (cropRect2 != null) {
                this.cropRect = cropRect2;
                this.rotationDegrees = rotationDegrees2;
                return;
            }
            throw new NullPointerException("Null cropRect");
        }
        throw new NullPointerException("Null resolution");
    }

    public Size getResolution() {
        return this.resolution;
    }

    public Rect getCropRect() {
        return this.cropRect;
    }

    public int getRotationDegrees() {
        return this.rotationDegrees;
    }

    public String toString() {
        return "ResolutionInfo{resolution=" + this.resolution + ", cropRect=" + this.cropRect + ", rotationDegrees=" + this.rotationDegrees + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ResolutionInfo)) {
            return false;
        }
        ResolutionInfo that = (ResolutionInfo) o;
        if (!this.resolution.equals(that.getResolution()) || !this.cropRect.equals(that.getCropRect()) || this.rotationDegrees != that.getRotationDegrees()) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (((((1 * 1000003) ^ this.resolution.hashCode()) * 1000003) ^ this.cropRect.hashCode()) * 1000003) ^ this.rotationDegrees;
    }
}

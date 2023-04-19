package androidx.camera.core.impl;

import android.util.Size;
import android.view.Surface;

final class AutoValue_OutputSurface extends OutputSurface {
    private final int imageFormat;
    private final Size size;
    private final Surface surface;

    AutoValue_OutputSurface(Surface surface2, Size size2, int imageFormat2) {
        if (surface2 != null) {
            this.surface = surface2;
            if (size2 != null) {
                this.size = size2;
                this.imageFormat = imageFormat2;
                return;
            }
            throw new NullPointerException("Null size");
        }
        throw new NullPointerException("Null surface");
    }

    public Surface getSurface() {
        return this.surface;
    }

    public Size getSize() {
        return this.size;
    }

    public int getImageFormat() {
        return this.imageFormat;
    }

    public String toString() {
        return "OutputSurface{surface=" + this.surface + ", size=" + this.size + ", imageFormat=" + this.imageFormat + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OutputSurface)) {
            return false;
        }
        OutputSurface that = (OutputSurface) o;
        if (!this.surface.equals(that.getSurface()) || !this.size.equals(that.getSize()) || this.imageFormat != that.getImageFormat()) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (((((1 * 1000003) ^ this.surface.hashCode()) * 1000003) ^ this.size.hashCode()) * 1000003) ^ this.imageFormat;
    }
}

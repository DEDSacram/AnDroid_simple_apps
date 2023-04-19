package androidx.camera.core.processing;

import java.util.List;

final class AutoValue_SurfaceEdge extends SurfaceEdge {
    private final List<SettableSurface> surfaces;

    AutoValue_SurfaceEdge(List<SettableSurface> surfaces2) {
        if (surfaces2 != null) {
            this.surfaces = surfaces2;
            return;
        }
        throw new NullPointerException("Null surfaces");
    }

    public List<SettableSurface> getSurfaces() {
        return this.surfaces;
    }

    public String toString() {
        return "SurfaceEdge{surfaces=" + this.surfaces + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SurfaceEdge) {
            return this.surfaces.equals(((SurfaceEdge) o).getSurfaces());
        }
        return false;
    }

    public int hashCode() {
        return (1 * 1000003) ^ this.surfaces.hashCode();
    }
}

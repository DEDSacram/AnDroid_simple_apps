package androidx.camera.core.impl;

import androidx.camera.core.impl.SessionConfig;
import java.util.List;
import okhttp3.HttpUrl;

final class AutoValue_SessionConfig_OutputConfig extends SessionConfig.OutputConfig {
    private final String physicalCameraId;
    private final List<DeferrableSurface> sharedSurfaces;
    private final DeferrableSurface surface;
    private final int surfaceGroupId;

    private AutoValue_SessionConfig_OutputConfig(DeferrableSurface surface2, List<DeferrableSurface> sharedSurfaces2, String physicalCameraId2, int surfaceGroupId2) {
        this.surface = surface2;
        this.sharedSurfaces = sharedSurfaces2;
        this.physicalCameraId = physicalCameraId2;
        this.surfaceGroupId = surfaceGroupId2;
    }

    public DeferrableSurface getSurface() {
        return this.surface;
    }

    public List<DeferrableSurface> getSharedSurfaces() {
        return this.sharedSurfaces;
    }

    public String getPhysicalCameraId() {
        return this.physicalCameraId;
    }

    public int getSurfaceGroupId() {
        return this.surfaceGroupId;
    }

    public String toString() {
        return "OutputConfig{surface=" + this.surface + ", sharedSurfaces=" + this.sharedSurfaces + ", physicalCameraId=" + this.physicalCameraId + ", surfaceGroupId=" + this.surfaceGroupId + "}";
    }

    public boolean equals(Object o) {
        String str;
        if (o == this) {
            return true;
        }
        if (!(o instanceof SessionConfig.OutputConfig)) {
            return false;
        }
        SessionConfig.OutputConfig that = (SessionConfig.OutputConfig) o;
        if (!this.surface.equals(that.getSurface()) || !this.sharedSurfaces.equals(that.getSharedSurfaces()) || ((str = this.physicalCameraId) != null ? !str.equals(that.getPhysicalCameraId()) : that.getPhysicalCameraId() != null) || this.surfaceGroupId != that.getSurfaceGroupId()) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int h$ = ((((1 * 1000003) ^ this.surface.hashCode()) * 1000003) ^ this.sharedSurfaces.hashCode()) * 1000003;
        String str = this.physicalCameraId;
        return ((h$ ^ (str == null ? 0 : str.hashCode())) * 1000003) ^ this.surfaceGroupId;
    }

    static final class Builder extends SessionConfig.OutputConfig.Builder {
        private String physicalCameraId;
        private List<DeferrableSurface> sharedSurfaces;
        private DeferrableSurface surface;
        private Integer surfaceGroupId;

        Builder() {
        }

        public SessionConfig.OutputConfig.Builder setSurface(DeferrableSurface surface2) {
            if (surface2 != null) {
                this.surface = surface2;
                return this;
            }
            throw new NullPointerException("Null surface");
        }

        public SessionConfig.OutputConfig.Builder setSharedSurfaces(List<DeferrableSurface> sharedSurfaces2) {
            if (sharedSurfaces2 != null) {
                this.sharedSurfaces = sharedSurfaces2;
                return this;
            }
            throw new NullPointerException("Null sharedSurfaces");
        }

        public SessionConfig.OutputConfig.Builder setPhysicalCameraId(String physicalCameraId2) {
            this.physicalCameraId = physicalCameraId2;
            return this;
        }

        public SessionConfig.OutputConfig.Builder setSurfaceGroupId(int surfaceGroupId2) {
            this.surfaceGroupId = Integer.valueOf(surfaceGroupId2);
            return this;
        }

        public SessionConfig.OutputConfig build() {
            String missing = HttpUrl.FRAGMENT_ENCODE_SET;
            if (this.surface == null) {
                missing = missing + " surface";
            }
            if (this.sharedSurfaces == null) {
                missing = missing + " sharedSurfaces";
            }
            if (this.surfaceGroupId == null) {
                missing = missing + " surfaceGroupId";
            }
            if (missing.isEmpty()) {
                return new AutoValue_SessionConfig_OutputConfig(this.surface, this.sharedSurfaces, this.physicalCameraId, this.surfaceGroupId.intValue());
            }
            throw new IllegalStateException("Missing required properties:" + missing);
        }
    }
}

package androidx.camera.view.video;

import android.location.Location;
import androidx.camera.view.video.Metadata;

final class AutoValue_Metadata extends Metadata {
    private final Location location;

    private AutoValue_Metadata(Location location2) {
        this.location = location2;
    }

    public Location getLocation() {
        return this.location;
    }

    public String toString() {
        return "Metadata{location=" + this.location + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Metadata)) {
            return false;
        }
        Metadata that = (Metadata) o;
        Location location2 = this.location;
        if (location2 != null) {
            return location2.equals(that.getLocation());
        }
        if (that.getLocation() == null) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int h$ = 1 * 1000003;
        Location location2 = this.location;
        return h$ ^ (location2 == null ? 0 : location2.hashCode());
    }

    static final class Builder extends Metadata.Builder {
        private Location location;

        Builder() {
        }

        public Metadata.Builder setLocation(Location location2) {
            this.location = location2;
            return this;
        }

        public Metadata build() {
            return new AutoValue_Metadata(this.location);
        }
    }
}

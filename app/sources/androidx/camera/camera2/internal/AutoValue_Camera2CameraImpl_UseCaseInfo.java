package androidx.camera.camera2.internal;

import android.util.Size;
import androidx.camera.camera2.internal.Camera2CameraImpl;
import androidx.camera.core.impl.SessionConfig;
import androidx.camera.core.impl.UseCaseConfig;

final class AutoValue_Camera2CameraImpl_UseCaseInfo extends Camera2CameraImpl.UseCaseInfo {
    private final SessionConfig sessionConfig;
    private final Size surfaceResolution;
    private final UseCaseConfig<?> useCaseConfig;
    private final String useCaseId;
    private final Class<?> useCaseType;

    AutoValue_Camera2CameraImpl_UseCaseInfo(String useCaseId2, Class<?> useCaseType2, SessionConfig sessionConfig2, UseCaseConfig<?> useCaseConfig2, Size surfaceResolution2) {
        if (useCaseId2 != null) {
            this.useCaseId = useCaseId2;
            if (useCaseType2 != null) {
                this.useCaseType = useCaseType2;
                if (sessionConfig2 != null) {
                    this.sessionConfig = sessionConfig2;
                    if (useCaseConfig2 != null) {
                        this.useCaseConfig = useCaseConfig2;
                        this.surfaceResolution = surfaceResolution2;
                        return;
                    }
                    throw new NullPointerException("Null useCaseConfig");
                }
                throw new NullPointerException("Null sessionConfig");
            }
            throw new NullPointerException("Null useCaseType");
        }
        throw new NullPointerException("Null useCaseId");
    }

    /* access modifiers changed from: package-private */
    public String getUseCaseId() {
        return this.useCaseId;
    }

    /* access modifiers changed from: package-private */
    public Class<?> getUseCaseType() {
        return this.useCaseType;
    }

    /* access modifiers changed from: package-private */
    public SessionConfig getSessionConfig() {
        return this.sessionConfig;
    }

    /* access modifiers changed from: package-private */
    public UseCaseConfig<?> getUseCaseConfig() {
        return this.useCaseConfig;
    }

    /* access modifiers changed from: package-private */
    public Size getSurfaceResolution() {
        return this.surfaceResolution;
    }

    public String toString() {
        return "UseCaseInfo{useCaseId=" + this.useCaseId + ", useCaseType=" + this.useCaseType + ", sessionConfig=" + this.sessionConfig + ", useCaseConfig=" + this.useCaseConfig + ", surfaceResolution=" + this.surfaceResolution + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Camera2CameraImpl.UseCaseInfo)) {
            return false;
        }
        Camera2CameraImpl.UseCaseInfo that = (Camera2CameraImpl.UseCaseInfo) o;
        if (this.useCaseId.equals(that.getUseCaseId()) && this.useCaseType.equals(that.getUseCaseType()) && this.sessionConfig.equals(that.getSessionConfig()) && this.useCaseConfig.equals(that.getUseCaseConfig())) {
            Size size = this.surfaceResolution;
            if (size == null) {
                if (that.getSurfaceResolution() == null) {
                    return true;
                }
            } else if (size.equals(that.getSurfaceResolution())) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        int h$ = ((((((((1 * 1000003) ^ this.useCaseId.hashCode()) * 1000003) ^ this.useCaseType.hashCode()) * 1000003) ^ this.sessionConfig.hashCode()) * 1000003) ^ this.useCaseConfig.hashCode()) * 1000003;
        Size size = this.surfaceResolution;
        return h$ ^ (size == null ? 0 : size.hashCode());
    }
}

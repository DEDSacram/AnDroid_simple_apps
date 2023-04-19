package androidx.camera.camera2.internal.compat.params;

import android.hardware.camera2.params.OutputConfiguration;
import android.view.Surface;
import androidx.core.util.Preconditions;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

class OutputConfigurationCompatApi24Impl extends OutputConfigurationCompatBaseImpl {
    OutputConfigurationCompatApi24Impl(Surface surface) {
        this((Object) new OutputConfigurationParamsApi24(new OutputConfiguration(surface)));
    }

    OutputConfigurationCompatApi24Impl(int surfaceGroupId, Surface surface) {
        this((Object) new OutputConfigurationParamsApi24(new OutputConfiguration(surfaceGroupId, surface)));
    }

    OutputConfigurationCompatApi24Impl(Object outputConfiguration) {
        super(outputConfiguration);
    }

    static OutputConfigurationCompatApi24Impl wrap(OutputConfiguration outputConfiguration) {
        return new OutputConfigurationCompatApi24Impl((Object) new OutputConfigurationParamsApi24(outputConfiguration));
    }

    public void enableSurfaceSharing() {
        ((OutputConfigurationParamsApi24) this.mObject).mIsShared = true;
    }

    /* access modifiers changed from: package-private */
    public boolean isSurfaceSharingEnabled() {
        return ((OutputConfigurationParamsApi24) this.mObject).mIsShared;
    }

    public void setPhysicalCameraId(String physicalCameraId) {
        ((OutputConfigurationParamsApi24) this.mObject).mPhysicalCameraId = physicalCameraId;
    }

    public String getPhysicalCameraId() {
        return ((OutputConfigurationParamsApi24) this.mObject).mPhysicalCameraId;
    }

    public Surface getSurface() {
        return ((OutputConfiguration) getOutputConfiguration()).getSurface();
    }

    public List<Surface> getSurfaces() {
        return Collections.singletonList(getSurface());
    }

    public int getSurfaceGroupId() {
        return ((OutputConfiguration) getOutputConfiguration()).getSurfaceGroupId();
    }

    public Object getOutputConfiguration() {
        Preconditions.checkArgument(this.mObject instanceof OutputConfigurationParamsApi24);
        return ((OutputConfigurationParamsApi24) this.mObject).mOutputConfiguration;
    }

    private static final class OutputConfigurationParamsApi24 {
        boolean mIsShared;
        final OutputConfiguration mOutputConfiguration;
        String mPhysicalCameraId;

        OutputConfigurationParamsApi24(OutputConfiguration configuration) {
            this.mOutputConfiguration = configuration;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof OutputConfigurationParamsApi24)) {
                return false;
            }
            OutputConfigurationParamsApi24 otherOutputConfig = (OutputConfigurationParamsApi24) obj;
            if (!Objects.equals(this.mOutputConfiguration, otherOutputConfig.mOutputConfiguration) || this.mIsShared != otherOutputConfig.mIsShared || !Objects.equals(this.mPhysicalCameraId, otherOutputConfig.mPhysicalCameraId)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            int h = ((1 << 5) - 1) ^ this.mOutputConfiguration.hashCode();
            int h2 = ((h << 5) - h) ^ this.mIsShared;
            int i = (((int) h2) << true) - h2;
            String str = this.mPhysicalCameraId;
            return i ^ (str == null ? 0 : str.hashCode());
        }
    }
}

package androidx.camera.camera2.internal.compat.params;

import android.hardware.camera2.params.OutputConfiguration;
import android.view.Surface;
import androidx.core.util.Preconditions;

class OutputConfigurationCompatApi28Impl extends OutputConfigurationCompatApi26Impl {
    OutputConfigurationCompatApi28Impl(Surface surface) {
        super((Object) new OutputConfiguration(surface));
    }

    OutputConfigurationCompatApi28Impl(int surfaceGroupId, Surface surface) {
        this((Object) new OutputConfiguration(surfaceGroupId, surface));
    }

    OutputConfigurationCompatApi28Impl(Object outputConfiguration) {
        super(outputConfiguration);
    }

    static OutputConfigurationCompatApi28Impl wrap(OutputConfiguration outputConfiguration) {
        return new OutputConfigurationCompatApi28Impl((Object) outputConfiguration);
    }

    public void removeSurface(Surface surface) {
        ((OutputConfiguration) getOutputConfiguration()).removeSurface(surface);
    }

    public int getMaxSharedSurfaceCount() {
        return ((OutputConfiguration) getOutputConfiguration()).getMaxSharedSurfaceCount();
    }

    public void setPhysicalCameraId(String physicalCameraId) {
        ((OutputConfiguration) getOutputConfiguration()).setPhysicalCameraId(physicalCameraId);
    }

    public String getPhysicalCameraId() {
        return null;
    }

    public Object getOutputConfiguration() {
        Preconditions.checkArgument(this.mObject instanceof OutputConfiguration);
        return this.mObject;
    }
}

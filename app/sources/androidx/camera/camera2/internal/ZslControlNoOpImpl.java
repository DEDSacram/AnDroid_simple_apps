package androidx.camera.camera2.internal;

import androidx.camera.core.ImageProxy;
import androidx.camera.core.impl.SessionConfig;

public class ZslControlNoOpImpl implements ZslControl {
    public void addZslConfig(SessionConfig.Builder sessionConfigBuilder) {
    }

    public void setZslDisabledByUserCaseConfig(boolean disabled) {
    }

    public boolean isZslDisabledByUserCaseConfig() {
        return false;
    }

    public void setZslDisabledByFlashMode(boolean disabled) {
    }

    public boolean isZslDisabledByFlashMode() {
        return false;
    }

    public ImageProxy dequeueImageFromBuffer() {
        return null;
    }

    public boolean enqueueImageToImageWriter(ImageProxy imageProxy) {
        return false;
    }
}

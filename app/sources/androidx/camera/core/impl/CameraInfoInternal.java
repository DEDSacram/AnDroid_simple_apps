package androidx.camera.core.impl;

import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.core.util.Preconditions;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

public interface CameraInfoInternal extends CameraInfo {
    void addSessionCaptureCallback(Executor executor, CameraCaptureCallback cameraCaptureCallback);

    CamcorderProfileProvider getCamcorderProfileProvider();

    String getCameraId();

    Quirks getCameraQuirks();

    Integer getLensFacing();

    void removeSessionCaptureCallback(CameraCaptureCallback cameraCaptureCallback);

    CameraSelector getCameraSelector() {
        return new CameraSelector.Builder().addCameraFilter(new CameraInfoInternal$$ExternalSyntheticLambda0(this)).build();
    }

    static /* synthetic */ List lambda$getCameraSelector$0(CameraInfoInternal _this, List cameraInfos) {
        String cameraId = _this.getCameraId();
        Iterator it = cameraInfos.iterator();
        while (it.hasNext()) {
            CameraInfo cameraInfo = (CameraInfo) it.next();
            Preconditions.checkArgument(cameraInfo instanceof CameraInfoInternal);
            if (((CameraInfoInternal) cameraInfo).getCameraId().equals(cameraId)) {
                return Collections.singletonList(cameraInfo);
            }
        }
        throw new IllegalStateException("Unable to find camera with id " + cameraId + " from list of available cameras.");
    }
}

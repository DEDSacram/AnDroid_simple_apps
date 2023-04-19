package androidx.camera.camera2.internal.compat.workaround;

import android.hardware.camera2.CaptureRequest;
import androidx.camera.camera2.impl.Camera2ImplConfig;
import androidx.camera.camera2.internal.compat.quirk.DeviceQuirks;
import androidx.camera.camera2.internal.compat.quirk.TorchIsClosedAfterImageCapturingQuirk;
import androidx.camera.core.impl.CaptureConfig;
import androidx.camera.core.impl.DeferrableSurface;
import java.util.List;

public class TorchStateReset {
    private final boolean mIsImageCaptureTorchIsClosedQuirkEnabled;

    public TorchStateReset() {
        this.mIsImageCaptureTorchIsClosedQuirkEnabled = DeviceQuirks.get(TorchIsClosedAfterImageCapturingQuirk.class) != null;
    }

    public boolean isTorchResetRequired(List<CaptureRequest> captureRequestList, boolean isSingleCapture) {
        if (!this.mIsImageCaptureTorchIsClosedQuirkEnabled || !isSingleCapture) {
            return false;
        }
        for (CaptureRequest captureRequest : captureRequestList) {
            Integer flashMode = (Integer) captureRequest.get(CaptureRequest.FLASH_MODE);
            if (flashMode != null && flashMode.intValue() == 2) {
                return true;
            }
        }
        return false;
    }

    public CaptureConfig createTorchResetRequest(CaptureConfig repeatingConfig) {
        CaptureConfig.Builder captureConfigBuilder = new CaptureConfig.Builder();
        captureConfigBuilder.setTemplateType(repeatingConfig.getTemplateType());
        for (DeferrableSurface deferrableSurface : repeatingConfig.getSurfaces()) {
            captureConfigBuilder.addSurface(deferrableSurface);
        }
        captureConfigBuilder.addImplementationOptions(repeatingConfig.getImplementationOptions());
        Camera2ImplConfig.Builder builder = new Camera2ImplConfig.Builder();
        builder.setCaptureRequestOption(CaptureRequest.FLASH_MODE, 0);
        captureConfigBuilder.addImplementationOptions(builder.build());
        return captureConfigBuilder.build();
    }
}

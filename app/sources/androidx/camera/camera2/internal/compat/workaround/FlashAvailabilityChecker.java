package androidx.camera.camera2.internal.compat.workaround;

import android.hardware.camera2.CameraCharacteristics;
import androidx.camera.camera2.internal.compat.CameraCharacteristicsCompat;
import androidx.camera.camera2.internal.compat.quirk.DeviceQuirks;
import androidx.camera.camera2.internal.compat.quirk.FlashAvailabilityBufferUnderflowQuirk;
import androidx.camera.core.Logger;
import java.nio.BufferUnderflowException;

public final class FlashAvailabilityChecker {
    private static final String TAG = "FlashAvailability";

    public static boolean isFlashAvailable(CameraCharacteristicsCompat characteristics) {
        if (DeviceQuirks.get(FlashAvailabilityBufferUnderflowQuirk.class) == null) {
            return checkFlashAvailabilityNormally(characteristics);
        }
        Logger.d(TAG, "Device has quirk " + FlashAvailabilityBufferUnderflowQuirk.class.getSimpleName() + ". Checking for flash availability safely...");
        return checkFlashAvailabilityWithPossibleBufferUnderflow(characteristics);
    }

    private static boolean checkFlashAvailabilityWithPossibleBufferUnderflow(CameraCharacteristicsCompat characteristics) {
        try {
            return checkFlashAvailabilityNormally(characteristics);
        } catch (BufferUnderflowException e) {
            return false;
        }
    }

    private static boolean checkFlashAvailabilityNormally(CameraCharacteristicsCompat characteristics) {
        Boolean flashAvailable = (Boolean) characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
        if (flashAvailable == null) {
            Logger.w(TAG, "Characteristics did not contain key FLASH_INFO_AVAILABLE. Flash is not available.");
        }
        if (flashAvailable != null) {
            return flashAvailable.booleanValue();
        }
        return false;
    }

    private FlashAvailabilityChecker() {
    }
}

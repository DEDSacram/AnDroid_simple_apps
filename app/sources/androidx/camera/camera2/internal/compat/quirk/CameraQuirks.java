package androidx.camera.camera2.internal.compat.quirk;

import androidx.camera.camera2.internal.compat.CameraCharacteristicsCompat;
import androidx.camera.core.impl.Quirk;
import androidx.camera.core.impl.Quirks;
import java.util.ArrayList;
import java.util.List;

public class CameraQuirks {
    private CameraQuirks() {
    }

    public static Quirks get(String cameraId, CameraCharacteristicsCompat cameraCharacteristicsCompat) {
        List<Quirk> quirks = new ArrayList<>();
        if (AeFpsRangeLegacyQuirk.load(cameraCharacteristicsCompat)) {
            quirks.add(new AeFpsRangeLegacyQuirk(cameraCharacteristicsCompat));
        }
        if (AspectRatioLegacyApi21Quirk.load(cameraCharacteristicsCompat)) {
            quirks.add(new AspectRatioLegacyApi21Quirk());
        }
        if (JpegHalCorruptImageQuirk.load(cameraCharacteristicsCompat)) {
            quirks.add(new JpegHalCorruptImageQuirk());
        }
        if (CamcorderProfileResolutionQuirk.load(cameraCharacteristicsCompat)) {
            quirks.add(new CamcorderProfileResolutionQuirk(cameraCharacteristicsCompat));
        }
        if (ImageCaptureWashedOutImageQuirk.load(cameraCharacteristicsCompat)) {
            quirks.add(new ImageCaptureWashedOutImageQuirk());
        }
        if (CameraNoResponseWhenEnablingFlashQuirk.load(cameraCharacteristicsCompat)) {
            quirks.add(new CameraNoResponseWhenEnablingFlashQuirk());
        }
        if (YuvImageOnePixelShiftQuirk.load(cameraCharacteristicsCompat)) {
            quirks.add(new YuvImageOnePixelShiftQuirk());
        }
        if (FlashTooSlowQuirk.load(cameraCharacteristicsCompat)) {
            quirks.add(new FlashTooSlowQuirk());
        }
        if (AfRegionFlipHorizontallyQuirk.load(cameraCharacteristicsCompat)) {
            quirks.add(new AfRegionFlipHorizontallyQuirk());
        }
        if (ConfigureSurfaceToSecondarySessionFailQuirk.load(cameraCharacteristicsCompat)) {
            quirks.add(new ConfigureSurfaceToSecondarySessionFailQuirk());
        }
        if (PreviewOrientationIncorrectQuirk.load(cameraCharacteristicsCompat)) {
            quirks.add(new PreviewOrientationIncorrectQuirk());
        }
        if (CaptureSessionStuckQuirk.load(cameraCharacteristicsCompat)) {
            quirks.add(new CaptureSessionStuckQuirk());
        }
        if (ImageCaptureFlashNotFireQuirk.load(cameraCharacteristicsCompat)) {
            quirks.add(new ImageCaptureFlashNotFireQuirk());
        }
        if (ImageCaptureWithFlashUnderexposureQuirk.load(cameraCharacteristicsCompat)) {
            quirks.add(new ImageCaptureWithFlashUnderexposureQuirk());
        }
        if (ImageCaptureFailWithAutoFlashQuirk.load(cameraCharacteristicsCompat)) {
            quirks.add(new ImageCaptureFailWithAutoFlashQuirk());
        }
        return new Quirks(quirks);
    }
}

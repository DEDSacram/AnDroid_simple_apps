package androidx.camera.camera2.internal.compat.quirk;

import android.hardware.camera2.CameraCharacteristics;
import android.util.Range;
import androidx.camera.camera2.internal.compat.CameraCharacteristicsCompat;
import androidx.camera.core.impl.Quirk;

public class AeFpsRangeLegacyQuirk implements Quirk {
    private final Range<Integer> mAeFpsRange;

    public AeFpsRangeLegacyQuirk(CameraCharacteristicsCompat cameraCharacteristicsCompat) {
        this.mAeFpsRange = pickSuitableFpsRange((Range[]) cameraCharacteristicsCompat.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES));
    }

    static boolean load(CameraCharacteristicsCompat cameraCharacteristicsCompat) {
        Integer level = (Integer) cameraCharacteristicsCompat.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
        return level != null && level.intValue() == 2;
    }

    public Range<Integer> getRange() {
        return this.mAeFpsRange;
    }

    private Range<Integer> pickSuitableFpsRange(Range<Integer>[] availableFpsRanges) {
        if (availableFpsRanges == null || availableFpsRanges.length == 0) {
            return null;
        }
        Range<Integer> pickedRange = null;
        for (Range<Integer> fpsRange : availableFpsRanges) {
            Range<Integer> fpsRange2 = getCorrectedFpsRange(fpsRange);
            if (fpsRange2.getUpper().intValue() == 30) {
                if (pickedRange == null) {
                    pickedRange = fpsRange2;
                } else if (fpsRange2.getLower().intValue() < pickedRange.getLower().intValue()) {
                    pickedRange = fpsRange2;
                }
            }
        }
        return pickedRange;
    }

    private Range<Integer> getCorrectedFpsRange(Range<Integer> fpsRange) {
        int newUpper = fpsRange.getUpper().intValue();
        int newLower = fpsRange.getLower().intValue();
        if (fpsRange.getUpper().intValue() >= 1000) {
            newUpper = fpsRange.getUpper().intValue() / 1000;
        }
        if (fpsRange.getLower().intValue() >= 1000) {
            newLower = fpsRange.getLower().intValue() / 1000;
        }
        return new Range<>(Integer.valueOf(newLower), Integer.valueOf(newUpper));
    }
}

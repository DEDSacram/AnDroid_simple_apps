package androidx.camera.camera2.internal;

import android.graphics.Rect;
import android.hardware.camera2.CaptureResult;
import android.os.Build;
import androidx.camera.core.Logger;
import androidx.camera.core.impl.CameraCaptureMetaData;
import androidx.camera.core.impl.CameraCaptureResult;
import androidx.camera.core.impl.TagBundle;
import androidx.camera.core.impl.utils.ExifData;

public class Camera2CameraCaptureResult implements CameraCaptureResult {
    private static final String TAG = "C2CameraCaptureResult";
    private final CaptureResult mCaptureResult;
    private final TagBundle mTagBundle;

    public Camera2CameraCaptureResult(TagBundle tagBundle, CaptureResult captureResult) {
        this.mTagBundle = tagBundle;
        this.mCaptureResult = captureResult;
    }

    public Camera2CameraCaptureResult(CaptureResult captureResult) {
        this(TagBundle.emptyBundle(), captureResult);
    }

    public CameraCaptureMetaData.AfMode getAfMode() {
        Integer mode = (Integer) this.mCaptureResult.get(CaptureResult.CONTROL_AF_MODE);
        if (mode == null) {
            return CameraCaptureMetaData.AfMode.UNKNOWN;
        }
        switch (mode.intValue()) {
            case 0:
            case 5:
                return CameraCaptureMetaData.AfMode.OFF;
            case 1:
            case 2:
                return CameraCaptureMetaData.AfMode.ON_MANUAL_AUTO;
            case 3:
            case 4:
                return CameraCaptureMetaData.AfMode.ON_CONTINUOUS_AUTO;
            default:
                Logger.e(TAG, "Undefined af mode: " + mode);
                return CameraCaptureMetaData.AfMode.UNKNOWN;
        }
    }

    public CameraCaptureMetaData.AfState getAfState() {
        Integer state = (Integer) this.mCaptureResult.get(CaptureResult.CONTROL_AF_STATE);
        if (state == null) {
            return CameraCaptureMetaData.AfState.UNKNOWN;
        }
        switch (state.intValue()) {
            case 0:
                return CameraCaptureMetaData.AfState.INACTIVE;
            case 1:
            case 3:
                return CameraCaptureMetaData.AfState.SCANNING;
            case 2:
                return CameraCaptureMetaData.AfState.PASSIVE_FOCUSED;
            case 4:
                return CameraCaptureMetaData.AfState.LOCKED_FOCUSED;
            case 5:
                return CameraCaptureMetaData.AfState.LOCKED_NOT_FOCUSED;
            case 6:
                return CameraCaptureMetaData.AfState.PASSIVE_NOT_FOCUSED;
            default:
                Logger.e(TAG, "Undefined af state: " + state);
                return CameraCaptureMetaData.AfState.UNKNOWN;
        }
    }

    public CameraCaptureMetaData.AeState getAeState() {
        Integer state = (Integer) this.mCaptureResult.get(CaptureResult.CONTROL_AE_STATE);
        if (state == null) {
            return CameraCaptureMetaData.AeState.UNKNOWN;
        }
        switch (state.intValue()) {
            case 0:
                return CameraCaptureMetaData.AeState.INACTIVE;
            case 1:
            case 5:
                return CameraCaptureMetaData.AeState.SEARCHING;
            case 2:
                return CameraCaptureMetaData.AeState.CONVERGED;
            case 3:
                return CameraCaptureMetaData.AeState.LOCKED;
            case 4:
                return CameraCaptureMetaData.AeState.FLASH_REQUIRED;
            default:
                Logger.e(TAG, "Undefined ae state: " + state);
                return CameraCaptureMetaData.AeState.UNKNOWN;
        }
    }

    public CameraCaptureMetaData.AwbState getAwbState() {
        Integer state = (Integer) this.mCaptureResult.get(CaptureResult.CONTROL_AWB_STATE);
        if (state == null) {
            return CameraCaptureMetaData.AwbState.UNKNOWN;
        }
        switch (state.intValue()) {
            case 0:
                return CameraCaptureMetaData.AwbState.INACTIVE;
            case 1:
                return CameraCaptureMetaData.AwbState.METERING;
            case 2:
                return CameraCaptureMetaData.AwbState.CONVERGED;
            case 3:
                return CameraCaptureMetaData.AwbState.LOCKED;
            default:
                Logger.e(TAG, "Undefined awb state: " + state);
                return CameraCaptureMetaData.AwbState.UNKNOWN;
        }
    }

    public CameraCaptureMetaData.FlashState getFlashState() {
        Integer state = (Integer) this.mCaptureResult.get(CaptureResult.FLASH_STATE);
        if (state == null) {
            return CameraCaptureMetaData.FlashState.UNKNOWN;
        }
        switch (state.intValue()) {
            case 0:
            case 1:
                return CameraCaptureMetaData.FlashState.NONE;
            case 2:
                return CameraCaptureMetaData.FlashState.READY;
            case 3:
            case 4:
                return CameraCaptureMetaData.FlashState.FIRED;
            default:
                Logger.e(TAG, "Undefined flash state: " + state);
                return CameraCaptureMetaData.FlashState.UNKNOWN;
        }
    }

    public long getTimestamp() {
        Long timestamp = (Long) this.mCaptureResult.get(CaptureResult.SENSOR_TIMESTAMP);
        if (timestamp == null) {
            return -1;
        }
        return timestamp.longValue();
    }

    public TagBundle getTagBundle() {
        return this.mTagBundle;
    }

    public void populateExifData(ExifData.Builder exifData) {
        Integer postRawSensitivityBoost;
        super.populateExifData(exifData);
        Rect cropRegion = (Rect) this.mCaptureResult.get(CaptureResult.SCALER_CROP_REGION);
        if (cropRegion != null) {
            exifData.setImageWidth(cropRegion.width()).setImageHeight(cropRegion.height());
        }
        Integer jpegOrientation = (Integer) this.mCaptureResult.get(CaptureResult.JPEG_ORIENTATION);
        if (jpegOrientation != null) {
            exifData.setOrientationDegrees(jpegOrientation.intValue());
        }
        Long exposureTimeNs = (Long) this.mCaptureResult.get(CaptureResult.SENSOR_EXPOSURE_TIME);
        if (exposureTimeNs != null) {
            exifData.setExposureTimeNanos(exposureTimeNs.longValue());
        }
        Float aperture = (Float) this.mCaptureResult.get(CaptureResult.LENS_APERTURE);
        if (aperture != null) {
            exifData.setLensFNumber(aperture.floatValue());
        }
        Integer iso = (Integer) this.mCaptureResult.get(CaptureResult.SENSOR_SENSITIVITY);
        if (iso != null) {
            if (Build.VERSION.SDK_INT >= 24 && (postRawSensitivityBoost = (Integer) this.mCaptureResult.get(CaptureResult.CONTROL_POST_RAW_SENSITIVITY_BOOST)) != null) {
                iso = Integer.valueOf(iso.intValue() * ((int) (((float) postRawSensitivityBoost.intValue()) / 100.0f)));
            }
            exifData.setIso(iso.intValue());
        }
        Float focalLength = (Float) this.mCaptureResult.get(CaptureResult.LENS_FOCAL_LENGTH);
        if (focalLength != null) {
            exifData.setFocalLength(focalLength.floatValue());
        }
        Integer whiteBalanceMode = (Integer) this.mCaptureResult.get(CaptureResult.CONTROL_AWB_MODE);
        if (whiteBalanceMode != null) {
            ExifData.WhiteBalanceMode wbMode = ExifData.WhiteBalanceMode.AUTO;
            if (whiteBalanceMode.intValue() == 0) {
                wbMode = ExifData.WhiteBalanceMode.MANUAL;
            }
            exifData.setWhiteBalanceMode(wbMode);
        }
    }

    public CaptureResult getCaptureResult() {
        return this.mCaptureResult;
    }
}

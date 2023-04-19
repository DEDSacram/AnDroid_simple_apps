package androidx.camera.camera2.internal;

import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import androidx.camera.camera2.impl.Camera2ImplConfig;
import androidx.camera.camera2.internal.ZoomControl;
import androidx.camera.camera2.internal.compat.CameraCharacteristicsCompat;
import androidx.camera.core.CameraControl;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.util.Preconditions;

final class CropRegionZoomImpl implements ZoomControl.ZoomImpl {
    public static final float MIN_DIGITAL_ZOOM = 1.0f;
    private final CameraCharacteristicsCompat mCameraCharacteristics;
    private Rect mCurrentCropRect = null;
    private Rect mPendingZoomCropRegion = null;
    private CallbackToFutureAdapter.Completer<Void> mPendingZoomRatioCompleter;

    CropRegionZoomImpl(CameraCharacteristicsCompat cameraCharacteristics) {
        this.mCameraCharacteristics = cameraCharacteristics;
    }

    public float getMinZoom() {
        return 1.0f;
    }

    public float getMaxZoom() {
        Float maxZoom = (Float) this.mCameraCharacteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
        if (maxZoom == null) {
            return 1.0f;
        }
        if (maxZoom.floatValue() < getMinZoom()) {
            return getMinZoom();
        }
        return maxZoom.floatValue();
    }

    public void addRequestOption(Camera2ImplConfig.Builder builder) {
        if (this.mCurrentCropRect != null) {
            builder.setCaptureRequestOption(CaptureRequest.SCALER_CROP_REGION, this.mCurrentCropRect);
        }
    }

    public void resetZoom() {
        this.mPendingZoomCropRegion = null;
        this.mCurrentCropRect = null;
        CallbackToFutureAdapter.Completer<Void> completer = this.mPendingZoomRatioCompleter;
        if (completer != null) {
            completer.setException(new CameraControl.OperationCanceledException("Camera is not active."));
            this.mPendingZoomRatioCompleter = null;
        }
    }

    private Rect getSensorRect() {
        return (Rect) Preconditions.checkNotNull((Rect) this.mCameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE));
    }

    private static Rect getCropRectByRatio(Rect sensorRect, float ratio) {
        float cropWidth = ((float) sensorRect.width()) / ratio;
        float cropHeight = ((float) sensorRect.height()) / ratio;
        float left = (((float) sensorRect.width()) - cropWidth) / 2.0f;
        float top = (((float) sensorRect.height()) - cropHeight) / 2.0f;
        return new Rect((int) left, (int) top, (int) (left + cropWidth), (int) (top + cropHeight));
    }

    public void setZoomRatio(float zoomRatio, CallbackToFutureAdapter.Completer<Void> completer) {
        this.mCurrentCropRect = getCropRectByRatio(getSensorRect(), zoomRatio);
        CallbackToFutureAdapter.Completer<Void> completer2 = this.mPendingZoomRatioCompleter;
        if (completer2 != null) {
            completer2.setException(new CameraControl.OperationCanceledException("There is a new zoomRatio being set"));
        }
        this.mPendingZoomCropRegion = this.mCurrentCropRect;
        this.mPendingZoomRatioCompleter = completer;
    }

    public void onCaptureResult(TotalCaptureResult captureResult) {
        Rect cropRect;
        if (this.mPendingZoomRatioCompleter != null) {
            CaptureRequest request = captureResult.getRequest();
            if (request == null) {
                cropRect = null;
            } else {
                cropRect = (Rect) request.get(CaptureRequest.SCALER_CROP_REGION);
            }
            Rect rect = this.mPendingZoomCropRegion;
            if (rect != null && rect.equals(cropRect)) {
                this.mPendingZoomRatioCompleter.set(null);
                this.mPendingZoomRatioCompleter = null;
                this.mPendingZoomCropRegion = null;
            }
        }
    }

    public Rect getCropSensorRegion() {
        Rect rect = this.mCurrentCropRect;
        return rect != null ? rect : getSensorRect();
    }
}

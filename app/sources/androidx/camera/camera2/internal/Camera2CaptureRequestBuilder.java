package androidx.camera.camera2.internal;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Build;
import android.view.Surface;
import androidx.camera.camera2.interop.CaptureRequestOptions;
import androidx.camera.core.Logger;
import androidx.camera.core.impl.CameraCaptureResult;
import androidx.camera.core.impl.CaptureConfig;
import androidx.camera.core.impl.Config;
import androidx.camera.core.impl.DeferrableSurface;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Camera2CaptureRequestBuilder {
    private static final String TAG = "Camera2CaptureRequestBuilder";

    private Camera2CaptureRequestBuilder() {
    }

    private static List<Surface> getConfiguredSurfaces(List<DeferrableSurface> deferrableSurfaces, Map<DeferrableSurface, Surface> configuredSurfaceMap) {
        List<Surface> surfaceList = new ArrayList<>();
        for (DeferrableSurface deferrableSurface : deferrableSurfaces) {
            Surface surface = configuredSurfaceMap.get(deferrableSurface);
            if (surface != null) {
                surfaceList.add(surface);
            } else {
                throw new IllegalArgumentException("DeferrableSurface not in configuredSurfaceMap");
            }
        }
        return surfaceList;
    }

    private static void applyImplementationOptionToCaptureBuilder(CaptureRequest.Builder builder, Config config) {
        CaptureRequestOptions bundle = CaptureRequestOptions.Builder.from(config).build();
        for (Config.Option<?> option : bundle.listOptions()) {
            CaptureRequest.Key<Object> key = (CaptureRequest.Key) option.getToken();
            try {
                builder.set(key, bundle.retrieveOption(option));
            } catch (IllegalArgumentException e) {
                Logger.e(TAG, "CaptureRequest.Key is not supported: " + key);
            }
        }
    }

    public static CaptureRequest build(CaptureConfig captureConfig, CameraDevice device, Map<DeferrableSurface, Surface> configuredSurfaceMap) throws CameraAccessException {
        CaptureRequest.Builder builder;
        if (device == null) {
            return null;
        }
        List<Surface> surfaceList = getConfiguredSurfaces(captureConfig.getSurfaces(), configuredSurfaceMap);
        if (surfaceList.isEmpty()) {
            return null;
        }
        CameraCaptureResult cameraCaptureResult = captureConfig.getCameraCaptureResult();
        if (Build.VERSION.SDK_INT < 23 || captureConfig.getTemplateType() != 5 || cameraCaptureResult == null || !(cameraCaptureResult.getCaptureResult() instanceof TotalCaptureResult)) {
            Logger.d(TAG, "createCaptureRequest");
            builder = device.createCaptureRequest(captureConfig.getTemplateType());
        } else {
            Logger.d(TAG, "createReprocessCaptureRequest");
            builder = Api23Impl.createReprocessCaptureRequest(device, (TotalCaptureResult) cameraCaptureResult.getCaptureResult());
        }
        applyImplementationOptionToCaptureBuilder(builder, captureConfig.getImplementationOptions());
        if (captureConfig.getImplementationOptions().containsOption(CaptureConfig.OPTION_ROTATION)) {
            builder.set(CaptureRequest.JPEG_ORIENTATION, (Integer) captureConfig.getImplementationOptions().retrieveOption(CaptureConfig.OPTION_ROTATION));
        }
        if (captureConfig.getImplementationOptions().containsOption(CaptureConfig.OPTION_JPEG_QUALITY)) {
            builder.set(CaptureRequest.JPEG_QUALITY, Byte.valueOf(((Integer) captureConfig.getImplementationOptions().retrieveOption(CaptureConfig.OPTION_JPEG_QUALITY)).byteValue()));
        }
        for (Surface surface : surfaceList) {
            builder.addTarget(surface);
        }
        builder.setTag(captureConfig.getTagBundle());
        return builder.build();
    }

    public static CaptureRequest buildWithoutTarget(CaptureConfig captureConfig, CameraDevice device) throws CameraAccessException {
        if (device == null) {
            return null;
        }
        CaptureRequest.Builder builder = device.createCaptureRequest(captureConfig.getTemplateType());
        applyImplementationOptionToCaptureBuilder(builder, captureConfig.getImplementationOptions());
        return builder.build();
    }

    static class Api23Impl {
        private Api23Impl() {
        }

        static CaptureRequest.Builder createReprocessCaptureRequest(CameraDevice cameraDevice, TotalCaptureResult totalCaptureResult) throws CameraAccessException {
            return cameraDevice.createReprocessCaptureRequest(totalCaptureResult);
        }
    }
}

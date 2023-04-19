package androidx.camera.camera2.internal;

import android.content.Context;
import android.media.CamcorderProfile;
import android.util.Size;
import androidx.camera.camera2.internal.compat.CameraManagerCompat;
import androidx.camera.core.CameraUnavailableException;
import androidx.camera.core.impl.AttachedSurfaceInfo;
import androidx.camera.core.impl.CameraDeviceSurfaceManager;
import androidx.camera.core.impl.SurfaceConfig;
import androidx.camera.core.impl.UseCaseConfig;
import androidx.core.util.Preconditions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Camera2DeviceSurfaceManager implements CameraDeviceSurfaceManager {
    private static final String TAG = "Camera2DeviceSurfaceManager";
    private final CamcorderProfileHelper mCamcorderProfileHelper;
    private final Map<String, SupportedSurfaceCombination> mCameraSupportedSurfaceCombinationMap;

    public Camera2DeviceSurfaceManager(Context context, Object cameraManager, Set<String> availableCameraIds) throws CameraUnavailableException {
        this(context, new CamcorderProfileHelper() {
            public boolean hasProfile(int cameraId, int quality) {
                return CamcorderProfile.hasProfile(cameraId, quality);
            }

            public CamcorderProfile get(int cameraId, int quality) {
                return CamcorderProfile.get(cameraId, quality);
            }
        }, cameraManager, availableCameraIds);
    }

    Camera2DeviceSurfaceManager(Context context, CamcorderProfileHelper camcorderProfileHelper, Object cameraManager, Set<String> availableCameraIds) throws CameraUnavailableException {
        CameraManagerCompat cameraManagerCompat;
        this.mCameraSupportedSurfaceCombinationMap = new HashMap();
        Preconditions.checkNotNull(camcorderProfileHelper);
        this.mCamcorderProfileHelper = camcorderProfileHelper;
        if (cameraManager instanceof CameraManagerCompat) {
            cameraManagerCompat = (CameraManagerCompat) cameraManager;
        } else {
            cameraManagerCompat = CameraManagerCompat.from(context);
        }
        init(context, cameraManagerCompat, availableCameraIds);
    }

    private void init(Context context, CameraManagerCompat cameraManager, Set<String> availableCameraIds) throws CameraUnavailableException {
        Preconditions.checkNotNull(context);
        for (String cameraId : availableCameraIds) {
            this.mCameraSupportedSurfaceCombinationMap.put(cameraId, new SupportedSurfaceCombination(context, cameraId, cameraManager, this.mCamcorderProfileHelper));
        }
    }

    public boolean checkSupported(String cameraId, List<SurfaceConfig> surfaceConfigList) {
        if (surfaceConfigList == null || surfaceConfigList.isEmpty()) {
            return true;
        }
        SupportedSurfaceCombination supportedSurfaceCombination = this.mCameraSupportedSurfaceCombinationMap.get(cameraId);
        if (supportedSurfaceCombination != null) {
            return supportedSurfaceCombination.checkSupported(surfaceConfigList);
        }
        return false;
    }

    public SurfaceConfig transformSurfaceConfig(String cameraId, int imageFormat, Size size) {
        SupportedSurfaceCombination supportedSurfaceCombination = this.mCameraSupportedSurfaceCombinationMap.get(cameraId);
        if (supportedSurfaceCombination != null) {
            return supportedSurfaceCombination.transformSurfaceConfig(imageFormat, size);
        }
        return null;
    }

    public Map<UseCaseConfig<?>, Size> getSuggestedResolutions(String cameraId, List<AttachedSurfaceInfo> existingSurfaces, List<UseCaseConfig<?>> newUseCaseConfigs) {
        Preconditions.checkArgument(!newUseCaseConfigs.isEmpty(), "No new use cases to be bound.");
        SupportedSurfaceCombination supportedSurfaceCombination = this.mCameraSupportedSurfaceCombinationMap.get(cameraId);
        if (supportedSurfaceCombination != null) {
            return supportedSurfaceCombination.getSuggestedResolutions(existingSurfaces, newUseCaseConfigs);
        }
        throw new IllegalArgumentException("No such camera id in supported combination list: " + cameraId);
    }
}

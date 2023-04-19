package androidx.camera.camera2.internal;

import android.hardware.camera2.CameraCharacteristics;
import androidx.camera.camera2.internal.compat.CameraAccessExceptionCompat;
import androidx.camera.camera2.internal.compat.CameraManagerCompat;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraUnavailableException;
import androidx.camera.core.InitializationException;
import androidx.camera.core.impl.CameraInfoInternal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

class CameraSelectionOptimizer {
    private CameraSelectionOptimizer() {
    }

    static List<String> getSelectedAvailableCameraIds(Camera2CameraFactory cameraFactory, CameraSelector availableCamerasSelector) throws InitializationException {
        String skippedCameraId;
        try {
            List<String> availableCameraIds = new ArrayList<>();
            List<String> cameraIdList = Arrays.asList(cameraFactory.getCameraManager().getCameraIdList());
            if (availableCamerasSelector == null) {
                for (String id : cameraIdList) {
                    availableCameraIds.add(id);
                }
                return availableCameraIds;
            }
            try {
                skippedCameraId = decideSkippedCameraIdByHeuristic(cameraFactory.getCameraManager(), availableCamerasSelector.getLensFacing(), cameraIdList);
            } catch (IllegalStateException e) {
                skippedCameraId = null;
            }
            List<CameraInfo> cameraInfos = new ArrayList<>();
            for (String id2 : cameraIdList) {
                if (!id2.equals(skippedCameraId)) {
                    cameraInfos.add(cameraFactory.getCameraInfo(id2));
                }
            }
            Iterator<CameraInfo> it = availableCamerasSelector.filter(cameraInfos).iterator();
            while (it.hasNext()) {
                availableCameraIds.add(((CameraInfoInternal) it.next()).getCameraId());
            }
            return availableCameraIds;
        } catch (CameraAccessExceptionCompat e2) {
            throw new InitializationException((Throwable) CameraUnavailableExceptionHelper.createFrom(e2));
        } catch (CameraUnavailableException e3) {
            throw new InitializationException((Throwable) e3);
        }
    }

    private static String decideSkippedCameraIdByHeuristic(CameraManagerCompat cameraManager, Integer lensFacingInteger, List<String> cameraIdList) throws CameraAccessExceptionCompat {
        if (lensFacingInteger == null || !cameraIdList.contains("0") || !cameraIdList.contains("1")) {
            return null;
        }
        if (lensFacingInteger.intValue() == 1) {
            if (((Integer) cameraManager.getCameraCharacteristicsCompat("0").get(CameraCharacteristics.LENS_FACING)).intValue() == 1) {
                return "1";
            }
            return null;
        } else if (lensFacingInteger.intValue() == 0 && ((Integer) cameraManager.getCameraCharacteristicsCompat("1").get(CameraCharacteristics.LENS_FACING)).intValue() == 0) {
            return "0";
        } else {
            return null;
        }
    }
}

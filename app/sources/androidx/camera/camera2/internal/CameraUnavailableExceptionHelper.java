package androidx.camera.camera2.internal;

import androidx.camera.camera2.internal.compat.CameraAccessExceptionCompat;
import androidx.camera.core.CameraUnavailableException;

public final class CameraUnavailableExceptionHelper {
    private CameraUnavailableExceptionHelper() {
    }

    public static CameraUnavailableException createFrom(CameraAccessExceptionCompat e) {
        int errorCode;
        switch (e.getReason()) {
            case 1:
                errorCode = 1;
                break;
            case 2:
                errorCode = 2;
                break;
            case 3:
                errorCode = 3;
                break;
            case 4:
                errorCode = 4;
                break;
            case 5:
                errorCode = 5;
                break;
            case CameraAccessExceptionCompat.CAMERA_UNAVAILABLE_DO_NOT_DISTURB /*10001*/:
                errorCode = 6;
                break;
            default:
                errorCode = 0;
                break;
        }
        return new CameraUnavailableException(errorCode, (Throwable) e);
    }
}

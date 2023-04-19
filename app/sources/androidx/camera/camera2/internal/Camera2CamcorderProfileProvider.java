package androidx.camera.camera2.internal;

import android.media.CamcorderProfile;
import androidx.camera.camera2.internal.compat.CameraCharacteristicsCompat;
import androidx.camera.camera2.internal.compat.quirk.CamcorderProfileResolutionQuirk;
import androidx.camera.camera2.internal.compat.quirk.CameraQuirks;
import androidx.camera.camera2.internal.compat.workaround.CamcorderProfileResolutionValidator;
import androidx.camera.core.Logger;
import androidx.camera.core.impl.CamcorderProfileProvider;
import androidx.camera.core.impl.CamcorderProfileProxy;

public class Camera2CamcorderProfileProvider implements CamcorderProfileProvider {
    private static final String TAG = "Camera2CamcorderProfileProvider";
    private final CamcorderProfileResolutionValidator mCamcorderProfileResolutionValidator;
    private final int mCameraId;
    private final boolean mHasValidCameraId;

    public Camera2CamcorderProfileProvider(String cameraId, CameraCharacteristicsCompat cameraCharacteristics) {
        boolean hasValidCameraId = false;
        int intCameraId = -1;
        try {
            intCameraId = Integer.parseInt(cameraId);
            hasValidCameraId = true;
        } catch (NumberFormatException e) {
            Logger.w(TAG, "Camera id is not an integer: " + cameraId + ", unable to create CamcorderProfileProvider");
        }
        this.mHasValidCameraId = hasValidCameraId;
        this.mCameraId = intCameraId;
        this.mCamcorderProfileResolutionValidator = new CamcorderProfileResolutionValidator((CamcorderProfileResolutionQuirk) CameraQuirks.get(cameraId, cameraCharacteristics).get(CamcorderProfileResolutionQuirk.class));
    }

    public boolean hasProfile(int quality) {
        if (!this.mHasValidCameraId || !CamcorderProfile.hasProfile(this.mCameraId, quality)) {
            return false;
        }
        if (!this.mCamcorderProfileResolutionValidator.hasQuirk()) {
            return true;
        }
        return this.mCamcorderProfileResolutionValidator.hasValidVideoResolution(getProfileInternal(quality));
    }

    public CamcorderProfileProxy get(int quality) {
        if (!this.mHasValidCameraId || !CamcorderProfile.hasProfile(this.mCameraId, quality)) {
            return null;
        }
        CamcorderProfileProxy profile = getProfileInternal(quality);
        if (!this.mCamcorderProfileResolutionValidator.hasValidVideoResolution(profile)) {
            return null;
        }
        return profile;
    }

    private CamcorderProfileProxy getProfileInternal(int quality) {
        CamcorderProfile profile = null;
        try {
            profile = CamcorderProfile.get(this.mCameraId, quality);
        } catch (RuntimeException e) {
            Logger.w(TAG, "Unable to get CamcorderProfile by quality: " + quality, e);
        }
        if (profile != null) {
            return CamcorderProfileProxy.fromCamcorderProfile(profile);
        }
        return null;
    }
}

package androidx.camera.camera2.internal.compat.workaround;

import android.util.Size;
import androidx.camera.camera2.internal.compat.quirk.CamcorderProfileResolutionQuirk;
import androidx.camera.core.impl.CamcorderProfileProxy;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CamcorderProfileResolutionValidator {
    private final CamcorderProfileResolutionQuirk mQuirk;
    private final Set<Size> mSupportedResolutions;

    public CamcorderProfileResolutionValidator(CamcorderProfileResolutionQuirk quirk) {
        Set<Size> set;
        this.mQuirk = quirk;
        if (quirk != null) {
            set = new HashSet<>(quirk.getSupportedResolutions());
        } else {
            set = Collections.emptySet();
        }
        this.mSupportedResolutions = set;
    }

    public boolean hasQuirk() {
        return this.mQuirk != null;
    }

    public boolean hasValidVideoResolution(CamcorderProfileProxy profile) {
        if (profile == null) {
            return false;
        }
        if (this.mQuirk == null) {
            return true;
        }
        return this.mSupportedResolutions.contains(new Size(profile.getVideoFrameWidth(), profile.getVideoFrameHeight()));
    }
}

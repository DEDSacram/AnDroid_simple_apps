package androidx.camera.core;

import androidx.camera.core.impl.CameraInternal;
import androidx.camera.core.impl.LensFacingCameraFilter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public final class CameraSelector {
    public static final CameraSelector DEFAULT_BACK_CAMERA = new Builder().requireLensFacing(1).build();
    public static final CameraSelector DEFAULT_FRONT_CAMERA = new Builder().requireLensFacing(0).build();
    public static final int LENS_FACING_BACK = 1;
    public static final int LENS_FACING_FRONT = 0;
    private LinkedHashSet<CameraFilter> mCameraFilterSet;

    @Retention(RetentionPolicy.SOURCE)
    public @interface LensFacing {
    }

    CameraSelector(LinkedHashSet<CameraFilter> cameraFilterSet) {
        this.mCameraFilterSet = cameraFilterSet;
    }

    public CameraInternal select(LinkedHashSet<CameraInternal> cameras) {
        Iterator<CameraInternal> cameraInternalIterator = filter(cameras).iterator();
        if (cameraInternalIterator.hasNext()) {
            return cameraInternalIterator.next();
        }
        throw new IllegalArgumentException("No available camera can be found");
    }

    public List<CameraInfo> filter(List<CameraInfo> cameraInfos) {
        List<CameraInfo> output = new ArrayList<>(cameraInfos);
        Iterator it = this.mCameraFilterSet.iterator();
        while (it.hasNext()) {
            output = ((CameraFilter) it.next()).filter(Collections.unmodifiableList(output));
        }
        output.retainAll(cameraInfos);
        return output;
    }

    public LinkedHashSet<CameraInternal> filter(LinkedHashSet<CameraInternal> cameras) {
        List<CameraInfo> input = new ArrayList<>();
        Iterator it = cameras.iterator();
        while (it.hasNext()) {
            input.add(((CameraInternal) it.next()).getCameraInfo());
        }
        List<CameraInfo> result = filter(input);
        LinkedHashSet<CameraInternal> output = new LinkedHashSet<>();
        Iterator it2 = cameras.iterator();
        while (it2.hasNext()) {
            CameraInternal camera = (CameraInternal) it2.next();
            if (result.contains(camera.getCameraInfo())) {
                output.add(camera);
            }
        }
        return output;
    }

    public LinkedHashSet<CameraFilter> getCameraFilterSet() {
        return this.mCameraFilterSet;
    }

    public Integer getLensFacing() {
        Integer currentLensFacing = null;
        Iterator it = this.mCameraFilterSet.iterator();
        while (it.hasNext()) {
            CameraFilter filter = (CameraFilter) it.next();
            if (filter instanceof LensFacingCameraFilter) {
                Integer newLensFacing = Integer.valueOf(((LensFacingCameraFilter) filter).getLensFacing());
                if (currentLensFacing == null) {
                    currentLensFacing = newLensFacing;
                } else if (!currentLensFacing.equals(newLensFacing)) {
                    throw new IllegalStateException("Multiple conflicting lens facing requirements exist.");
                }
            }
        }
        return currentLensFacing;
    }

    public static final class Builder {
        private final LinkedHashSet<CameraFilter> mCameraFilterSet;

        public Builder() {
            this.mCameraFilterSet = new LinkedHashSet<>();
        }

        private Builder(LinkedHashSet<CameraFilter> cameraFilterSet) {
            this.mCameraFilterSet = new LinkedHashSet<>(cameraFilterSet);
        }

        public Builder requireLensFacing(int lensFacing) {
            this.mCameraFilterSet.add(new LensFacingCameraFilter(lensFacing));
            return this;
        }

        public Builder addCameraFilter(CameraFilter cameraFilter) {
            this.mCameraFilterSet.add(cameraFilter);
            return this;
        }

        public static Builder fromSelector(CameraSelector cameraSelector) {
            return new Builder(cameraSelector.getCameraFilterSet());
        }

        public CameraSelector build() {
            return new CameraSelector(this.mCameraFilterSet);
        }
    }
}

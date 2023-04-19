package androidx.camera.camera2.internal;

import android.hardware.camera2.CameraCharacteristics;
import android.util.Range;
import android.util.Rational;
import androidx.camera.camera2.internal.compat.CameraCharacteristicsCompat;
import androidx.camera.core.ExposureState;

class ExposureStateImpl implements ExposureState {
    private final CameraCharacteristicsCompat mCameraCharacteristics;
    private int mExposureCompensation;
    private final Object mLock = new Object();

    ExposureStateImpl(CameraCharacteristicsCompat characteristics, int exposureCompensation) {
        this.mCameraCharacteristics = characteristics;
        this.mExposureCompensation = exposureCompensation;
    }

    public int getExposureCompensationIndex() {
        int i;
        synchronized (this.mLock) {
            i = this.mExposureCompensation;
        }
        return i;
    }

    /* access modifiers changed from: package-private */
    public void setExposureCompensationIndex(int value) {
        synchronized (this.mLock) {
            this.mExposureCompensation = value;
        }
    }

    public Range<Integer> getExposureCompensationRange() {
        return (Range) this.mCameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);
    }

    public Rational getExposureCompensationStep() {
        if (!isExposureCompensationSupported()) {
            return Rational.ZERO;
        }
        return (Rational) this.mCameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP);
    }

    public boolean isExposureCompensationSupported() {
        Range<Integer> compensationRange = (Range) this.mCameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);
        return (compensationRange == null || compensationRange.getLower().intValue() == 0 || compensationRange.getUpper().intValue() == 0) ? false : true;
    }
}

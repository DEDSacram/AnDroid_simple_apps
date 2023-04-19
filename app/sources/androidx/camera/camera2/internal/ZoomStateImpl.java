package androidx.camera.camera2.internal;

import androidx.camera.core.ZoomState;
import androidx.core.math.MathUtils;

class ZoomStateImpl implements ZoomState {
    private float mLinearZoom;
    private final float mMaxZoomRatio;
    private final float mMinZoomRatio;
    private float mZoomRatio;

    ZoomStateImpl(float maxZoomRatio, float minZoomRatio) {
        this.mMaxZoomRatio = maxZoomRatio;
        this.mMinZoomRatio = minZoomRatio;
    }

    /* access modifiers changed from: package-private */
    public void setZoomRatio(float zoomRatio) throws IllegalArgumentException {
        if (zoomRatio > this.mMaxZoomRatio || zoomRatio < this.mMinZoomRatio) {
            throw new IllegalArgumentException("Requested zoomRatio " + zoomRatio + " is not within valid range [" + this.mMinZoomRatio + " , " + this.mMaxZoomRatio + "]");
        }
        this.mZoomRatio = zoomRatio;
        this.mLinearZoom = getPercentageByRatio(zoomRatio);
    }

    /* access modifiers changed from: package-private */
    public void setLinearZoom(float linearZoom) throws IllegalArgumentException {
        if (linearZoom > 1.0f || linearZoom < 0.0f) {
            throw new IllegalArgumentException("Requested linearZoom " + linearZoom + " is not within valid range [0..1]");
        }
        this.mLinearZoom = linearZoom;
        this.mZoomRatio = getRatioByPercentage(linearZoom);
    }

    public float getZoomRatio() {
        return this.mZoomRatio;
    }

    public float getMaxZoomRatio() {
        return this.mMaxZoomRatio;
    }

    public float getMinZoomRatio() {
        return this.mMinZoomRatio;
    }

    public float getLinearZoom() {
        return this.mLinearZoom;
    }

    private float getRatioByPercentage(float percentage) {
        float f = percentage;
        if (f == 1.0f) {
            return this.mMaxZoomRatio;
        }
        if (f == 0.0f) {
            return this.mMinZoomRatio;
        }
        float f2 = this.mMaxZoomRatio;
        float f3 = this.mMinZoomRatio;
        double cropWidthInMinZoom = (double) (1.0f / f3);
        return (float) MathUtils.clamp(1.0d / (((((double) (1.0f / f2)) - cropWidthInMinZoom) * ((double) f)) + cropWidthInMinZoom), (double) f3, (double) f2);
    }

    private float getPercentageByRatio(float ratio) {
        float f = this.mMaxZoomRatio;
        float f2 = this.mMinZoomRatio;
        if (f == f2) {
            return 0.0f;
        }
        if (ratio == f) {
            return 1.0f;
        }
        if (ratio == f2) {
            return 0.0f;
        }
        float cropWidthInMinZoom = 1.0f / f2;
        return ((1.0f / ratio) - cropWidthInMinZoom) / ((1.0f / f) - cropWidthInMinZoom);
    }
}

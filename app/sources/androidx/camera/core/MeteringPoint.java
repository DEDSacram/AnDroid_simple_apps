package androidx.camera.core;

import android.util.Rational;

public class MeteringPoint {
    private float mNormalizedX;
    private float mNormalizedY;
    private float mSize;
    private Rational mSurfaceAspectRatio;

    MeteringPoint(float normalizedX, float normalizedY, float size, Rational surfaceAspectRatio) {
        this.mNormalizedX = normalizedX;
        this.mNormalizedY = normalizedY;
        this.mSize = size;
        this.mSurfaceAspectRatio = surfaceAspectRatio;
    }

    public float getX() {
        return this.mNormalizedX;
    }

    public float getY() {
        return this.mNormalizedY;
    }

    public float getSize() {
        return this.mSize;
    }

    public Rational getSurfaceAspectRatio() {
        return this.mSurfaceAspectRatio;
    }
}

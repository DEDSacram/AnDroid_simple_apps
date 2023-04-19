package androidx.camera.core;

import android.graphics.PointF;
import android.util.Rational;

public abstract class MeteringPointFactory {
    private Rational mSurfaceAspectRatio;

    /* access modifiers changed from: protected */
    public abstract PointF convertPoint(float f, float f2);

    public MeteringPointFactory() {
        this((Rational) null);
    }

    public MeteringPointFactory(Rational surfaceAspectRatio) {
        this.mSurfaceAspectRatio = surfaceAspectRatio;
    }

    public static float getDefaultPointSize() {
        return 0.15f;
    }

    public final MeteringPoint createPoint(float x, float y) {
        return createPoint(x, y, getDefaultPointSize());
    }

    public final MeteringPoint createPoint(float x, float y, float size) {
        PointF convertedPoint = convertPoint(x, y);
        return new MeteringPoint(convertedPoint.x, convertedPoint.y, size, this.mSurfaceAspectRatio);
    }
}

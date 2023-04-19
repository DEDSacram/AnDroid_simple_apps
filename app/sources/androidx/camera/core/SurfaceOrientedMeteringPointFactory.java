package androidx.camera.core;

import android.graphics.PointF;
import android.util.Rational;
import android.util.Size;

public class SurfaceOrientedMeteringPointFactory extends MeteringPointFactory {
    private final float mHeight;
    private final float mWidth;

    public SurfaceOrientedMeteringPointFactory(float width, float height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public SurfaceOrientedMeteringPointFactory(float width, float height, UseCase useCaseForAspectRatio) {
        super(getUseCaseAspectRatio(useCaseForAspectRatio));
        this.mWidth = width;
        this.mHeight = height;
    }

    private static Rational getUseCaseAspectRatio(UseCase useCase) {
        if (useCase == null) {
            return null;
        }
        Size resolution = useCase.getAttachedSurfaceResolution();
        if (resolution != null) {
            return new Rational(resolution.getWidth(), resolution.getHeight());
        }
        throw new IllegalStateException("UseCase " + useCase + " is not bound.");
    }

    /* access modifiers changed from: protected */
    public PointF convertPoint(float x, float y) {
        return new PointF(x / this.mWidth, y / this.mHeight);
    }
}

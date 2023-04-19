package androidx.camera.view.transform;

import android.graphics.Matrix;
import android.graphics.RectF;
import androidx.camera.core.ImageProxy;
import androidx.camera.view.TransformUtils;

public final class ImageProxyTransformFactory {
    private boolean mUsingCropRect;
    private boolean mUsingRotationDegrees;

    public void setUsingCropRect(boolean usingCropRect) {
        this.mUsingCropRect = usingCropRect;
    }

    public boolean isUsingCropRect() {
        return this.mUsingCropRect;
    }

    public void setUsingRotationDegrees(boolean usingRotationDegrees) {
        this.mUsingRotationDegrees = usingRotationDegrees;
    }

    public boolean isUsingRotationDegrees() {
        return this.mUsingRotationDegrees;
    }

    public OutputTransform getOutputTransform(ImageProxy imageProxy) {
        int rotationDegrees = getRotationDegrees(imageProxy);
        RectF source = getCropRect(imageProxy);
        Matrix matrix = TransformUtils.getRectToRect(source, getRotatedCropRect(source, rotationDegrees), rotationDegrees);
        matrix.preConcat(TransformUtils.getNormalizedToBuffer(imageProxy.getCropRect()));
        return new OutputTransform(matrix, TransformUtils.rectToSize(imageProxy.getCropRect()));
    }

    private RectF getCropRect(ImageProxy imageProxy) {
        if (this.mUsingCropRect) {
            return new RectF(imageProxy.getCropRect());
        }
        return new RectF(0.0f, 0.0f, (float) imageProxy.getWidth(), (float) imageProxy.getHeight());
    }

    private int getRotationDegrees(ImageProxy imageProxy) {
        if (this.mUsingRotationDegrees) {
            return imageProxy.getImageInfo().getRotationDegrees();
        }
        return 0;
    }

    static RectF getRotatedCropRect(RectF rect, int rotationDegrees) {
        if (TransformUtils.is90or270(rotationDegrees)) {
            return new RectF(0.0f, 0.0f, rect.height(), rect.width());
        }
        return new RectF(0.0f, 0.0f, rect.width(), rect.height());
    }
}

package androidx.camera.core;

import android.graphics.Matrix;
import android.media.ImageReader;
import androidx.camera.core.impl.TagBundle;

class ModifiableImageReaderProxy extends AndroidImageReaderProxy {
    private volatile Integer mRotationDegrees = null;
    private volatile Matrix mSensorToBufferTransformMatrix = null;
    private volatile TagBundle mTagBundle = null;
    private volatile Long mTimestamp = null;

    ModifiableImageReaderProxy(ImageReader imageReader) {
        super(imageReader);
    }

    /* access modifiers changed from: package-private */
    public void setImageTagBundle(TagBundle tagBundle) {
        this.mTagBundle = tagBundle;
    }

    /* access modifiers changed from: package-private */
    public void setImageTimeStamp(long timestamp) {
        this.mTimestamp = Long.valueOf(timestamp);
    }

    /* access modifiers changed from: package-private */
    public void setImageRotationDegrees(int rotationDegrees) {
        this.mRotationDegrees = Integer.valueOf(rotationDegrees);
    }

    /* access modifiers changed from: package-private */
    public void setImageSensorToBufferTransformaMatrix(Matrix matrix) {
        this.mSensorToBufferTransformMatrix = matrix;
    }

    public ImageProxy acquireLatestImage() {
        return modifyImage(super.acquireNextImage());
    }

    public ImageProxy acquireNextImage() {
        return modifyImage(super.acquireNextImage());
    }

    private ImageProxy modifyImage(ImageProxy imageProxy) {
        int i;
        Matrix matrix;
        ImageInfo origin = imageProxy.getImageInfo();
        TagBundle tagBundle = this.mTagBundle != null ? this.mTagBundle : origin.getTagBundle();
        long longValue = this.mTimestamp != null ? this.mTimestamp.longValue() : origin.getTimestamp();
        if (this.mRotationDegrees != null) {
            i = this.mRotationDegrees.intValue();
        } else {
            i = origin.getRotationDegrees();
        }
        if (this.mSensorToBufferTransformMatrix != null) {
            matrix = this.mSensorToBufferTransformMatrix;
        } else {
            matrix = origin.getSensorToBufferTransformMatrix();
        }
        return new SettableImageProxy(imageProxy, ImmutableImageInfo.create(tagBundle, longValue, i, matrix));
    }
}

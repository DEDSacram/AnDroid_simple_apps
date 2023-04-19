package androidx.camera.core;

import android.graphics.Matrix;
import androidx.camera.core.impl.TagBundle;
import androidx.camera.core.impl.utils.ExifData;

abstract class ImmutableImageInfo implements ImageInfo {
    public abstract int getRotationDegrees();

    public abstract Matrix getSensorToBufferTransformMatrix();

    public abstract TagBundle getTagBundle();

    public abstract long getTimestamp();

    ImmutableImageInfo() {
    }

    public static ImageInfo create(TagBundle tag, long timestamp, int rotationDegrees, Matrix sensorToBufferTransformMatrix) {
        return new AutoValue_ImmutableImageInfo(tag, timestamp, rotationDegrees, sensorToBufferTransformMatrix);
    }

    public void populateExifData(ExifData.Builder exifBuilder) {
        exifBuilder.setOrientationDegrees(getRotationDegrees());
    }
}

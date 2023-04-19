package androidx.camera.view.transform;

import android.content.ContentResolver;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import androidx.camera.core.impl.utils.Exif;
import androidx.camera.view.TransformUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class FileTransformFactory {
    private boolean mUsingExifOrientation;

    public void setUsingExifOrientation(boolean usingExifOrientation) {
        this.mUsingExifOrientation = usingExifOrientation;
    }

    public boolean isUsingExifOrientation() {
        return this.mUsingExifOrientation;
    }

    public OutputTransform getOutputTransform(ContentResolver contentResolver, Uri uri) throws IOException {
        InputStream inputStream = contentResolver.openInputStream(uri);
        try {
            OutputTransform outputTransform = getOutputTransform(inputStream);
            if (inputStream != null) {
                inputStream.close();
            }
            return outputTransform;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    public OutputTransform getOutputTransform(File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        try {
            OutputTransform outputTransform = getOutputTransform(inputStream);
            inputStream.close();
            return outputTransform;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    public OutputTransform getOutputTransform(InputStream inputStream) throws IOException {
        Exif exif = Exif.createFromInputStream(inputStream);
        Rect cropRect = new Rect(0, 0, exif.getWidth(), exif.getHeight());
        Matrix matrix = TransformUtils.getNormalizedToBuffer(cropRect);
        if (this.mUsingExifOrientation) {
            matrix.postConcat(TransformUtils.getExifTransform(exif.getOrientation(), exif.getWidth(), exif.getHeight()));
        }
        return new OutputTransform(matrix, TransformUtils.rectToSize(cropRect));
    }
}

package androidx.camera.core;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.Image;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.impl.TagBundle;
import java.nio.ByteBuffer;

final class AndroidImageProxy implements ImageProxy {
    private final Image mImage;
    private final ImageInfo mImageInfo;
    private final PlaneProxy[] mPlanes;

    AndroidImageProxy(Image image) {
        this.mImage = image;
        Image.Plane[] originalPlanes = image.getPlanes();
        if (originalPlanes != null) {
            this.mPlanes = new PlaneProxy[originalPlanes.length];
            for (int i = 0; i < originalPlanes.length; i++) {
                this.mPlanes[i] = new PlaneProxy(originalPlanes[i]);
            }
        } else {
            this.mPlanes = new PlaneProxy[0];
        }
        this.mImageInfo = ImmutableImageInfo.create(TagBundle.emptyBundle(), image.getTimestamp(), 0, new Matrix());
    }

    public void close() {
        this.mImage.close();
    }

    public Rect getCropRect() {
        return this.mImage.getCropRect();
    }

    public void setCropRect(Rect rect) {
        this.mImage.setCropRect(rect);
    }

    public int getFormat() {
        return this.mImage.getFormat();
    }

    public int getHeight() {
        return this.mImage.getHeight();
    }

    public int getWidth() {
        return this.mImage.getWidth();
    }

    public ImageProxy.PlaneProxy[] getPlanes() {
        return this.mPlanes;
    }

    private static final class PlaneProxy implements ImageProxy.PlaneProxy {
        private final Image.Plane mPlane;

        PlaneProxy(Image.Plane plane) {
            this.mPlane = plane;
        }

        public int getRowStride() {
            return this.mPlane.getRowStride();
        }

        public int getPixelStride() {
            return this.mPlane.getPixelStride();
        }

        public ByteBuffer getBuffer() {
            return this.mPlane.getBuffer();
        }
    }

    public ImageInfo getImageInfo() {
        return this.mImageInfo;
    }

    public Image getImage() {
        return this.mImage;
    }
}

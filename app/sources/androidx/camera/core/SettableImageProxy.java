package androidx.camera.core;

import android.graphics.Rect;
import android.util.Size;

final class SettableImageProxy extends ForwardingImageProxy {
    private Rect mCropRect;
    private final int mHeight;
    private final ImageInfo mImageInfo;
    private final Object mLock;
    private final int mWidth;

    SettableImageProxy(ImageProxy imageProxy, ImageInfo imageInfo) {
        this(imageProxy, (Size) null, imageInfo);
    }

    SettableImageProxy(ImageProxy imageProxy, Size resolution, ImageInfo imageInfo) {
        super(imageProxy);
        this.mLock = new Object();
        if (resolution == null) {
            this.mWidth = super.getWidth();
            this.mHeight = super.getHeight();
        } else {
            this.mWidth = resolution.getWidth();
            this.mHeight = resolution.getHeight();
        }
        this.mImageInfo = imageInfo;
    }

    public Rect getCropRect() {
        synchronized (this.mLock) {
            if (this.mCropRect == null) {
                Rect rect = new Rect(0, 0, getWidth(), getHeight());
                return rect;
            }
            Rect rect2 = new Rect(this.mCropRect);
            return rect2;
        }
    }

    public void setCropRect(Rect cropRect) {
        if (cropRect != null) {
            cropRect = new Rect(cropRect);
            if (!cropRect.intersect(0, 0, getWidth(), getHeight())) {
                cropRect.setEmpty();
            }
        }
        synchronized (this.mLock) {
            this.mCropRect = cropRect;
        }
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public ImageInfo getImageInfo() {
        return this.mImageInfo;
    }
}

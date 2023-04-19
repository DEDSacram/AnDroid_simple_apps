package androidx.camera.core;

import android.view.Surface;
import androidx.camera.core.ForwardingImageProxy;
import androidx.camera.core.impl.ImageReaderProxy;
import java.util.concurrent.Executor;

public class SafeCloseImageReaderProxy implements ImageReaderProxy {
    private final ForwardingImageProxy.OnImageCloseListener mImageCloseListener = new SafeCloseImageReaderProxy$$ExternalSyntheticLambda0(this);
    private final ImageReaderProxy mImageReaderProxy;
    private boolean mIsClosed = false;
    private final Object mLock = new Object();
    private int mOutstandingImages = 0;
    private final Surface mSurface;

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$new$0$androidx-camera-core-SafeCloseImageReaderProxy  reason: not valid java name */
    public /* synthetic */ void m158lambda$new$0$androidxcameracoreSafeCloseImageReaderProxy(ImageProxy image) {
        synchronized (this.mLock) {
            int i = this.mOutstandingImages - 1;
            this.mOutstandingImages = i;
            if (this.mIsClosed && i == 0) {
                close();
            }
        }
    }

    public SafeCloseImageReaderProxy(ImageReaderProxy imageReaderProxy) {
        this.mImageReaderProxy = imageReaderProxy;
        this.mSurface = imageReaderProxy.getSurface();
    }

    public ImageProxy acquireLatestImage() {
        ImageProxy wrapImageProxy;
        synchronized (this.mLock) {
            wrapImageProxy = wrapImageProxy(this.mImageReaderProxy.acquireLatestImage());
        }
        return wrapImageProxy;
    }

    public ImageProxy acquireNextImage() {
        ImageProxy wrapImageProxy;
        synchronized (this.mLock) {
            wrapImageProxy = wrapImageProxy(this.mImageReaderProxy.acquireNextImage());
        }
        return wrapImageProxy;
    }

    public void close() {
        synchronized (this.mLock) {
            Surface surface = this.mSurface;
            if (surface != null) {
                surface.release();
            }
            this.mImageReaderProxy.close();
        }
    }

    private ImageProxy wrapImageProxy(ImageProxy imageProxy) {
        if (imageProxy == null) {
            return null;
        }
        this.mOutstandingImages++;
        SingleCloseImageProxy singleCloseImageProxy = new SingleCloseImageProxy(imageProxy);
        singleCloseImageProxy.addOnImageCloseListener(this.mImageCloseListener);
        return singleCloseImageProxy;
    }

    public void safeClose() {
        synchronized (this.mLock) {
            this.mIsClosed = true;
            this.mImageReaderProxy.clearOnImageAvailableListener();
            if (this.mOutstandingImages == 0) {
                close();
            }
        }
    }

    public int getHeight() {
        int height;
        synchronized (this.mLock) {
            height = this.mImageReaderProxy.getHeight();
        }
        return height;
    }

    public int getWidth() {
        int width;
        synchronized (this.mLock) {
            width = this.mImageReaderProxy.getWidth();
        }
        return width;
    }

    public int getImageFormat() {
        int imageFormat;
        synchronized (this.mLock) {
            imageFormat = this.mImageReaderProxy.getImageFormat();
        }
        return imageFormat;
    }

    public int getMaxImages() {
        int maxImages;
        synchronized (this.mLock) {
            maxImages = this.mImageReaderProxy.getMaxImages();
        }
        return maxImages;
    }

    public Surface getSurface() {
        Surface surface;
        synchronized (this.mLock) {
            surface = this.mImageReaderProxy.getSurface();
        }
        return surface;
    }

    public void setOnImageAvailableListener(ImageReaderProxy.OnImageAvailableListener listener, Executor executor) {
        synchronized (this.mLock) {
            this.mImageReaderProxy.setOnImageAvailableListener(new SafeCloseImageReaderProxy$$ExternalSyntheticLambda1(this, listener), executor);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$setOnImageAvailableListener$1$androidx-camera-core-SafeCloseImageReaderProxy  reason: not valid java name */
    public /* synthetic */ void m159lambda$setOnImageAvailableListener$1$androidxcameracoreSafeCloseImageReaderProxy(ImageReaderProxy.OnImageAvailableListener listener, ImageReaderProxy imageReader) {
        listener.onImageAvailable(this);
    }

    public void clearOnImageAvailableListener() {
        synchronized (this.mLock) {
            this.mImageReaderProxy.clearOnImageAvailableListener();
        }
    }
}

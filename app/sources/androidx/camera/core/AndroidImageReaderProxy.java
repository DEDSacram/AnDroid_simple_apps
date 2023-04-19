package androidx.camera.core;

import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.view.Surface;
import androidx.camera.core.impl.ImageReaderProxy;
import androidx.camera.core.impl.utils.MainThreadAsyncHandler;
import java.util.concurrent.Executor;

class AndroidImageReaderProxy implements ImageReaderProxy {
    private final ImageReader mImageReader;
    private final Object mLock = new Object();

    AndroidImageReaderProxy(ImageReader imageReader) {
        this.mImageReader = imageReader;
    }

    public ImageProxy acquireLatestImage() {
        Image image;
        synchronized (this.mLock) {
            try {
                image = this.mImageReader.acquireLatestImage();
            } catch (RuntimeException e) {
                if (isImageReaderContextNotInitializedException(e)) {
                    image = null;
                } else {
                    throw e;
                }
            }
            if (image == null) {
                return null;
            }
            AndroidImageProxy androidImageProxy = new AndroidImageProxy(image);
            return androidImageProxy;
        }
    }

    public ImageProxy acquireNextImage() {
        Image image;
        synchronized (this.mLock) {
            try {
                image = this.mImageReader.acquireNextImage();
            } catch (RuntimeException e) {
                if (isImageReaderContextNotInitializedException(e)) {
                    image = null;
                } else {
                    throw e;
                }
            }
            if (image == null) {
                return null;
            }
            AndroidImageProxy androidImageProxy = new AndroidImageProxy(image);
            return androidImageProxy;
        }
    }

    private boolean isImageReaderContextNotInitializedException(RuntimeException e) {
        return "ImageReaderContext is not initialized".equals(e.getMessage());
    }

    public void close() {
        synchronized (this.mLock) {
            this.mImageReader.close();
        }
    }

    public int getHeight() {
        int height;
        synchronized (this.mLock) {
            height = this.mImageReader.getHeight();
        }
        return height;
    }

    public int getWidth() {
        int width;
        synchronized (this.mLock) {
            width = this.mImageReader.getWidth();
        }
        return width;
    }

    public int getImageFormat() {
        int imageFormat;
        synchronized (this.mLock) {
            imageFormat = this.mImageReader.getImageFormat();
        }
        return imageFormat;
    }

    public int getMaxImages() {
        int maxImages;
        synchronized (this.mLock) {
            maxImages = this.mImageReader.getMaxImages();
        }
        return maxImages;
    }

    public Surface getSurface() {
        Surface surface;
        synchronized (this.mLock) {
            surface = this.mImageReader.getSurface();
        }
        return surface;
    }

    public void setOnImageAvailableListener(ImageReaderProxy.OnImageAvailableListener listener, Executor executor) {
        synchronized (this.mLock) {
            this.mImageReader.setOnImageAvailableListener(new AndroidImageReaderProxy$$ExternalSyntheticLambda0(this, executor, listener), MainThreadAsyncHandler.getInstance());
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$setOnImageAvailableListener$0$androidx-camera-core-AndroidImageReaderProxy  reason: not valid java name */
    public /* synthetic */ void m127lambda$setOnImageAvailableListener$0$androidxcameracoreAndroidImageReaderProxy(ImageReaderProxy.OnImageAvailableListener listener) {
        listener.onImageAvailable(this);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$setOnImageAvailableListener$1$androidx-camera-core-AndroidImageReaderProxy  reason: not valid java name */
    public /* synthetic */ void m128lambda$setOnImageAvailableListener$1$androidxcameracoreAndroidImageReaderProxy(Executor executor, ImageReaderProxy.OnImageAvailableListener listener, ImageReader imageReader) {
        executor.execute(new AndroidImageReaderProxy$$ExternalSyntheticLambda1(this, listener));
    }

    public void clearOnImageAvailableListener() {
        synchronized (this.mLock) {
            this.mImageReader.setOnImageAvailableListener((ImageReader.OnImageAvailableListener) null, (Handler) null);
        }
    }
}

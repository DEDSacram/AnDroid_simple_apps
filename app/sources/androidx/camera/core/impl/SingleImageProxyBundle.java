package androidx.camera.core.impl;

import androidx.camera.core.ImageInfo;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.impl.utils.futures.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Collections;
import java.util.List;

public final class SingleImageProxyBundle implements ImageProxyBundle {
    private final int mCaptureId;
    private final ImageProxy mImageProxy;

    public SingleImageProxyBundle(ImageProxy imageProxy, String tagBundleKey) {
        ImageInfo imageInfo = imageProxy.getImageInfo();
        if (imageInfo != null) {
            Integer tagValue = (Integer) imageInfo.getTagBundle().getTag(tagBundleKey);
            if (tagValue != null) {
                this.mCaptureId = tagValue.intValue();
                this.mImageProxy = imageProxy;
                return;
            }
            throw new IllegalArgumentException("ImageProxy has no associated tag");
        }
        throw new IllegalArgumentException("ImageProxy has no associated ImageInfo");
    }

    SingleImageProxyBundle(ImageProxy imageProxy, int captureId) {
        this.mCaptureId = captureId;
        this.mImageProxy = imageProxy;
    }

    public void close() {
        this.mImageProxy.close();
    }

    public ListenableFuture<ImageProxy> getImageProxy(int captureId) {
        if (captureId != this.mCaptureId) {
            return Futures.immediateFailedFuture(new IllegalArgumentException("Capture id does not exist in the bundle"));
        }
        return Futures.immediateFuture(this.mImageProxy);
    }

    public List<Integer> getCaptureIds() {
        return Collections.singletonList(Integer.valueOf(this.mCaptureId));
    }
}

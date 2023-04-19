package androidx.camera.core;

import androidx.camera.core.impl.ImageReaderProxy;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.impl.utils.futures.FutureCallback;
import androidx.camera.core.impl.utils.futures.Futures;

final class ImageAnalysisBlockingAnalyzer extends ImageAnalysisAbstractAnalyzer {
    ImageAnalysisBlockingAnalyzer() {
    }

    /* access modifiers changed from: package-private */
    public ImageProxy acquireImage(ImageReaderProxy imageReaderProxy) {
        return imageReaderProxy.acquireNextImage();
    }

    /* access modifiers changed from: package-private */
    public void onValidImageAvailable(final ImageProxy imageProxy) {
        Futures.addCallback(analyzeImage(imageProxy), new FutureCallback<Void>() {
            public void onSuccess(Void result) {
            }

            public void onFailure(Throwable t) {
                imageProxy.close();
            }
        }, CameraXExecutors.directExecutor());
    }

    /* access modifiers changed from: package-private */
    public void clearCache() {
    }
}

package androidx.camera.core.internal.utils;

import androidx.camera.core.ImageInfo;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.impl.CameraCaptureMetaData;
import androidx.camera.core.impl.CameraCaptureResult;
import androidx.camera.core.impl.CameraCaptureResults;
import androidx.camera.core.internal.utils.RingBuffer;

public final class ZslRingBuffer extends ArrayRingBuffer<ImageProxy> {
    public ZslRingBuffer(int ringBufferCapacity, RingBuffer.OnRemoveCallback<ImageProxy> onRemoveCallback) {
        super(ringBufferCapacity, onRemoveCallback);
    }

    public void enqueue(ImageProxy imageProxy) {
        if (isValidZslFrame(imageProxy.getImageInfo())) {
            super.enqueue(imageProxy);
        } else {
            this.mOnRemoveCallback.onRemove(imageProxy);
        }
    }

    private boolean isValidZslFrame(ImageInfo imageInfo) {
        CameraCaptureResult cameraCaptureResult = CameraCaptureResults.retrieveCameraCaptureResult(imageInfo);
        if ((cameraCaptureResult.getAfState() == CameraCaptureMetaData.AfState.LOCKED_FOCUSED || cameraCaptureResult.getAfState() == CameraCaptureMetaData.AfState.PASSIVE_FOCUSED) && cameraCaptureResult.getAeState() == CameraCaptureMetaData.AeState.CONVERGED && cameraCaptureResult.getAwbState() == CameraCaptureMetaData.AwbState.CONVERGED) {
            return true;
        }
        return false;
    }
}

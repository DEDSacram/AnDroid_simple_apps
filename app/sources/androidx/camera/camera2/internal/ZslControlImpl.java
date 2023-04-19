package androidx.camera.camera2.internal;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.InputConfiguration;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageWriter;
import android.os.Build;
import android.util.Size;
import android.view.Surface;
import androidx.camera.camera2.internal.compat.CameraCharacteristicsCompat;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Logger;
import androidx.camera.core.MetadataImageReader;
import androidx.camera.core.SafeCloseImageReaderProxy;
import androidx.camera.core.impl.CameraCaptureCallback;
import androidx.camera.core.impl.DeferrableSurface;
import androidx.camera.core.impl.ImageReaderProxy;
import androidx.camera.core.impl.ImmediateSurface;
import androidx.camera.core.impl.SessionConfig;
import androidx.camera.core.impl.utils.CompareSizesByArea;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.internal.compat.ImageWriterCompat;
import androidx.camera.core.internal.utils.ZslRingBuffer;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

final class ZslControlImpl implements ZslControl {
    static final int MAX_IMAGES = 9;
    static final int RING_BUFFER_CAPACITY = 3;
    private static final String TAG = "ZslControlImpl";
    private final CameraCharacteristicsCompat mCameraCharacteristicsCompat;
    final ZslRingBuffer mImageRingBuffer;
    private boolean mIsPrivateReprocessingSupported = false;
    private boolean mIsZslDisabledByFlashMode = false;
    private boolean mIsZslDisabledByUseCaseConfig = false;
    private CameraCaptureCallback mMetadataMatchingCaptureCallback;
    private DeferrableSurface mReprocessingImageDeferrableSurface;
    SafeCloseImageReaderProxy mReprocessingImageReader;
    ImageWriter mReprocessingImageWriter;
    private final Map<Integer, Size> mReprocessingInputSizeMap;

    ZslControlImpl(CameraCharacteristicsCompat cameraCharacteristicsCompat) {
        this.mCameraCharacteristicsCompat = cameraCharacteristicsCompat;
        this.mIsPrivateReprocessingSupported = ZslUtil.isCapabilitySupported(cameraCharacteristicsCompat, 4);
        this.mReprocessingInputSizeMap = createReprocessingInputSizeMap(cameraCharacteristicsCompat);
        this.mImageRingBuffer = new ZslRingBuffer(3, ZslControlImpl$$ExternalSyntheticLambda1.INSTANCE);
    }

    public void setZslDisabledByUserCaseConfig(boolean disabled) {
        this.mIsZslDisabledByUseCaseConfig = disabled;
    }

    public boolean isZslDisabledByUserCaseConfig() {
        return this.mIsZslDisabledByUseCaseConfig;
    }

    public void setZslDisabledByFlashMode(boolean disabled) {
        this.mIsZslDisabledByFlashMode = disabled;
    }

    public boolean isZslDisabledByFlashMode() {
        return this.mIsZslDisabledByFlashMode;
    }

    public void addZslConfig(SessionConfig.Builder sessionConfigBuilder) {
        cleanup();
        if (!this.mIsZslDisabledByUseCaseConfig && this.mIsPrivateReprocessingSupported && !this.mReprocessingInputSizeMap.isEmpty() && this.mReprocessingInputSizeMap.containsKey(34) && isJpegValidOutputForInputFormat(this.mCameraCharacteristicsCompat, 34)) {
            Size resolution = this.mReprocessingInputSizeMap.get(34);
            MetadataImageReader metadataImageReader = new MetadataImageReader(resolution.getWidth(), resolution.getHeight(), 34, 9);
            this.mMetadataMatchingCaptureCallback = metadataImageReader.getCameraCaptureCallback();
            this.mReprocessingImageReader = new SafeCloseImageReaderProxy(metadataImageReader);
            metadataImageReader.setOnImageAvailableListener(new ZslControlImpl$$ExternalSyntheticLambda0(this), CameraXExecutors.ioExecutor());
            ImmediateSurface immediateSurface = new ImmediateSurface(this.mReprocessingImageReader.getSurface(), new Size(this.mReprocessingImageReader.getWidth(), this.mReprocessingImageReader.getHeight()), 34);
            this.mReprocessingImageDeferrableSurface = immediateSurface;
            SafeCloseImageReaderProxy reprocessingImageReaderProxy = this.mReprocessingImageReader;
            ListenableFuture<Void> terminationFuture = immediateSurface.getTerminationFuture();
            Objects.requireNonNull(reprocessingImageReaderProxy);
            terminationFuture.addListener(new ZslControlImpl$$ExternalSyntheticLambda2(reprocessingImageReaderProxy), CameraXExecutors.mainThreadExecutor());
            sessionConfigBuilder.addSurface(this.mReprocessingImageDeferrableSurface);
            sessionConfigBuilder.addCameraCaptureCallback(this.mMetadataMatchingCaptureCallback);
            sessionConfigBuilder.addSessionStateCallback(new CameraCaptureSession.StateCallback() {
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    Surface surface = cameraCaptureSession.getInputSurface();
                    if (surface != null) {
                        ZslControlImpl.this.mReprocessingImageWriter = ImageWriterCompat.newInstance(surface, 1);
                    }
                }

                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                }
            });
            sessionConfigBuilder.setInputConfiguration(new InputConfiguration(this.mReprocessingImageReader.getWidth(), this.mReprocessingImageReader.getHeight(), this.mReprocessingImageReader.getImageFormat()));
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$addZslConfig$1$androidx-camera-camera2-internal-ZslControlImpl  reason: not valid java name */
    public /* synthetic */ void m96lambda$addZslConfig$1$androidxcameracamera2internalZslControlImpl(ImageReaderProxy imageReader) {
        try {
            ImageProxy imageProxy = imageReader.acquireLatestImage();
            if (imageProxy != null) {
                this.mImageRingBuffer.enqueue(imageProxy);
            }
        } catch (IllegalStateException e) {
            Logger.e(TAG, "Failed to acquire latest image IllegalStateException = " + e.getMessage());
        }
    }

    public ImageProxy dequeueImageFromBuffer() {
        try {
            return (ImageProxy) this.mImageRingBuffer.dequeue();
        } catch (NoSuchElementException e) {
            Logger.e(TAG, "dequeueImageFromBuffer no such element");
            return null;
        }
    }

    public boolean enqueueImageToImageWriter(ImageProxy imageProxy) {
        ImageWriter imageWriter;
        Image image = imageProxy.getImage();
        if (Build.VERSION.SDK_INT < 23 || (imageWriter = this.mReprocessingImageWriter) == null || image == null) {
            return false;
        }
        try {
            ImageWriterCompat.queueInputImage(imageWriter, image);
            return true;
        } catch (IllegalStateException e) {
            Logger.e(TAG, "enqueueImageToImageWriter throws IllegalStateException = " + e.getMessage());
            return false;
        }
    }

    private void cleanup() {
        ZslRingBuffer imageRingBuffer = this.mImageRingBuffer;
        while (!imageRingBuffer.isEmpty()) {
            ((ImageProxy) imageRingBuffer.dequeue()).close();
        }
        DeferrableSurface reprocessingImageDeferrableSurface = this.mReprocessingImageDeferrableSurface;
        if (reprocessingImageDeferrableSurface != null) {
            SafeCloseImageReaderProxy reprocessingImageReaderProxy = this.mReprocessingImageReader;
            if (reprocessingImageReaderProxy != null) {
                ListenableFuture<Void> terminationFuture = reprocessingImageDeferrableSurface.getTerminationFuture();
                Objects.requireNonNull(reprocessingImageReaderProxy);
                terminationFuture.addListener(new ZslControlImpl$$ExternalSyntheticLambda2(reprocessingImageReaderProxy), CameraXExecutors.mainThreadExecutor());
                this.mReprocessingImageReader = null;
            }
            reprocessingImageDeferrableSurface.close();
            this.mReprocessingImageDeferrableSurface = null;
        }
        ImageWriter reprocessingImageWriter = this.mReprocessingImageWriter;
        if (reprocessingImageWriter != null) {
            reprocessingImageWriter.close();
            this.mReprocessingImageWriter = null;
        }
    }

    private Map<Integer, Size> createReprocessingInputSizeMap(CameraCharacteristicsCompat cameraCharacteristicsCompat) {
        StreamConfigurationMap map = (StreamConfigurationMap) cameraCharacteristicsCompat.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (map == null || map.getInputFormats() == null) {
            return new HashMap();
        }
        Map<Integer, Size> inputSizeMap = new HashMap<>();
        for (int format : map.getInputFormats()) {
            Size[] inputSizes = map.getInputSizes(format);
            if (inputSizes != null) {
                Arrays.sort(inputSizes, new CompareSizesByArea(true));
                inputSizeMap.put(Integer.valueOf(format), inputSizes[0]);
            }
        }
        return inputSizeMap;
    }

    private boolean isJpegValidOutputForInputFormat(CameraCharacteristicsCompat cameraCharacteristicsCompat, int inputFormat) {
        int[] validOutputFormats;
        StreamConfigurationMap map = (StreamConfigurationMap) cameraCharacteristicsCompat.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (map == null || (validOutputFormats = map.getValidOutputFormatsForInput(inputFormat)) == null) {
            return false;
        }
        for (int outputFormat : validOutputFormats) {
            if (outputFormat == 256) {
                return true;
            }
        }
        return false;
    }
}

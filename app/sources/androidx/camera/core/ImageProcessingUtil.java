package androidx.camera.core;

import android.media.Image;
import android.media.ImageWriter;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import androidx.camera.core.impl.ImageReaderProxy;
import androidx.camera.core.internal.compat.ImageWriterCompat;
import androidx.core.util.Preconditions;
import java.nio.ByteBuffer;
import java.util.Locale;

final class ImageProcessingUtil {
    private static final String TAG = "ImageProcessingUtil";
    private static int sImageCount = 0;

    enum Result {
        UNKNOWN,
        SUCCESS,
        ERROR_CONVERSION
    }

    private static native int nativeConvertAndroid420ToABGR(ByteBuffer byteBuffer, int i, ByteBuffer byteBuffer2, int i2, ByteBuffer byteBuffer3, int i3, int i4, int i5, Surface surface, ByteBuffer byteBuffer4, int i6, int i7, int i8, int i9, int i10, int i11);

    private static native int nativeRotateYUV(ByteBuffer byteBuffer, int i, ByteBuffer byteBuffer2, int i2, ByteBuffer byteBuffer3, int i3, int i4, ByteBuffer byteBuffer4, int i5, int i6, ByteBuffer byteBuffer5, int i7, int i8, ByteBuffer byteBuffer6, int i9, int i10, ByteBuffer byteBuffer7, ByteBuffer byteBuffer8, ByteBuffer byteBuffer9, int i11, int i12, int i13);

    private static native int nativeShiftPixel(ByteBuffer byteBuffer, int i, ByteBuffer byteBuffer2, int i2, ByteBuffer byteBuffer3, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10);

    private static native int nativeWriteJpegToSurface(byte[] bArr, Surface surface);

    static {
        System.loadLibrary("image_processing_util_jni");
    }

    private ImageProcessingUtil() {
    }

    public static ImageProxy convertJpegBytesToImage(ImageReaderProxy jpegImageReaderProxy, byte[] jpegBytes) {
        Preconditions.checkArgument(jpegImageReaderProxy.getImageFormat() == 256);
        Preconditions.checkNotNull(jpegBytes);
        Surface surface = jpegImageReaderProxy.getSurface();
        Preconditions.checkNotNull(surface);
        if (nativeWriteJpegToSurface(jpegBytes, surface) != 0) {
            Logger.e(TAG, "Failed to enqueue JPEG image.");
            return null;
        }
        ImageProxy imageProxy = jpegImageReaderProxy.acquireLatestImage();
        if (imageProxy == null) {
            Logger.e(TAG, "Failed to get acquire JPEG image.");
        }
        return imageProxy;
    }

    public static ImageProxy convertYUVToRGB(ImageProxy imageProxy, ImageReaderProxy rgbImageReaderProxy, ByteBuffer rgbConvertedBuffer, int rotationDegrees, boolean onePixelShiftEnabled) {
        if (!isSupportedYUVFormat(imageProxy)) {
            Logger.e(TAG, "Unsupported format for YUV to RGB");
            return null;
        }
        long startTimeMillis = System.currentTimeMillis();
        if (!isSupportedRotationDegrees(rotationDegrees)) {
            Logger.e(TAG, "Unsupported rotation degrees for rotate RGB");
            return null;
        } else if (convertYUVToRGBInternal(imageProxy, rgbImageReaderProxy.getSurface(), rgbConvertedBuffer, rotationDegrees, onePixelShiftEnabled) == Result.ERROR_CONVERSION) {
            Logger.e(TAG, "YUV to RGB conversion failure");
            return null;
        } else {
            if (Log.isLoggable("MH", 3)) {
                Logger.d(TAG, String.format(Locale.US, "Image processing performance profiling, duration: [%d], image count: %d", new Object[]{Long.valueOf(System.currentTimeMillis() - startTimeMillis), Integer.valueOf(sImageCount)}));
                sImageCount++;
            }
            ImageProxy rgbImageProxy = rgbImageReaderProxy.acquireLatestImage();
            if (rgbImageProxy == null) {
                Logger.e(TAG, "YUV to RGB acquireLatestImage failure");
                return null;
            }
            SingleCloseImageProxy wrappedRgbImageProxy = new SingleCloseImageProxy(rgbImageProxy);
            wrappedRgbImageProxy.addOnImageCloseListener(new ImageProcessingUtil$$ExternalSyntheticLambda0(rgbImageProxy, imageProxy));
            return wrappedRgbImageProxy;
        }
    }

    static /* synthetic */ void lambda$convertYUVToRGB$0(ImageProxy rgbImageProxy, ImageProxy imageProxy, ImageProxy image) {
        if (rgbImageProxy != null && imageProxy != null) {
            imageProxy.close();
        }
    }

    public static boolean applyPixelShiftForYUV(ImageProxy imageProxy) {
        if (!isSupportedYUVFormat(imageProxy)) {
            Logger.e(TAG, "Unsupported format for YUV to RGB");
            return false;
        } else if (applyPixelShiftInternal(imageProxy) != Result.ERROR_CONVERSION) {
            return true;
        } else {
            Logger.e(TAG, "One pixel shift for YUV failure");
            return false;
        }
    }

    public static ImageProxy rotateYUV(ImageProxy imageProxy, ImageReaderProxy rotatedImageReaderProxy, ImageWriter rotatedImageWriter, ByteBuffer yRotatedBuffer, ByteBuffer uRotatedBuffer, ByteBuffer vRotatedBuffer, int rotationDegrees) {
        if (!isSupportedYUVFormat(imageProxy)) {
            Logger.e(TAG, "Unsupported format for rotate YUV");
            return null;
        } else if (!isSupportedRotationDegrees(rotationDegrees)) {
            Logger.e(TAG, "Unsupported rotation degrees for rotate YUV");
            return null;
        } else {
            Result result = Result.ERROR_CONVERSION;
            if (Build.VERSION.SDK_INT >= 23 && rotationDegrees > 0) {
                result = rotateYUVInternal(imageProxy, rotatedImageWriter, yRotatedBuffer, uRotatedBuffer, vRotatedBuffer, rotationDegrees);
            }
            if (result == Result.ERROR_CONVERSION) {
                Logger.e(TAG, "rotate YUV failure");
                return null;
            }
            ImageProxy rotatedImageProxy = rotatedImageReaderProxy.acquireLatestImage();
            if (rotatedImageProxy == null) {
                Logger.e(TAG, "YUV rotation acquireLatestImage failure");
                return null;
            }
            SingleCloseImageProxy wrappedRotatedImageProxy = new SingleCloseImageProxy(rotatedImageProxy);
            wrappedRotatedImageProxy.addOnImageCloseListener(new ImageProcessingUtil$$ExternalSyntheticLambda1(rotatedImageProxy, imageProxy));
            return wrappedRotatedImageProxy;
        }
    }

    static /* synthetic */ void lambda$rotateYUV$1(ImageProxy rotatedImageProxy, ImageProxy imageProxy, ImageProxy image) {
        if (rotatedImageProxy != null && imageProxy != null) {
            imageProxy.close();
        }
    }

    private static boolean isSupportedYUVFormat(ImageProxy imageProxy) {
        return imageProxy.getFormat() == 35 && imageProxy.getPlanes().length == 3;
    }

    private static boolean isSupportedRotationDegrees(int rotationDegrees) {
        return rotationDegrees == 0 || rotationDegrees == 90 || rotationDegrees == 180 || rotationDegrees == 270;
    }

    private static Result convertYUVToRGBInternal(ImageProxy imageProxy, Surface surface, ByteBuffer rgbConvertedBuffer, int rotation, boolean onePixelShiftEnabled) {
        int imageWidth = imageProxy.getWidth();
        int imageHeight = imageProxy.getHeight();
        int srcStrideY = imageProxy.getPlanes()[0].getRowStride();
        int srcStrideU = imageProxy.getPlanes()[1].getRowStride();
        int srcStrideV = imageProxy.getPlanes()[2].getRowStride();
        int srcPixelStrideY = imageProxy.getPlanes()[0].getPixelStride();
        int srcPixelStrideUV = imageProxy.getPlanes()[1].getPixelStride();
        if (nativeConvertAndroid420ToABGR(imageProxy.getPlanes()[0].getBuffer(), srcStrideY, imageProxy.getPlanes()[1].getBuffer(), srcStrideU, imageProxy.getPlanes()[2].getBuffer(), srcStrideV, srcPixelStrideY, srcPixelStrideUV, surface, rgbConvertedBuffer, imageWidth, imageHeight, onePixelShiftEnabled ? srcPixelStrideY : 0, onePixelShiftEnabled ? srcPixelStrideUV : 0, onePixelShiftEnabled ? srcPixelStrideUV : 0, rotation) != 0) {
            return Result.ERROR_CONVERSION;
        }
        return Result.SUCCESS;
    }

    private static Result applyPixelShiftInternal(ImageProxy imageProxy) {
        int imageWidth = imageProxy.getWidth();
        int imageHeight = imageProxy.getHeight();
        int srcStrideY = imageProxy.getPlanes()[0].getRowStride();
        int srcStrideU = imageProxy.getPlanes()[1].getRowStride();
        int srcStrideV = imageProxy.getPlanes()[2].getRowStride();
        int srcPixelStrideY = imageProxy.getPlanes()[0].getPixelStride();
        int srcPixelStrideUV = imageProxy.getPlanes()[1].getPixelStride();
        if (nativeShiftPixel(imageProxy.getPlanes()[0].getBuffer(), srcStrideY, imageProxy.getPlanes()[1].getBuffer(), srcStrideU, imageProxy.getPlanes()[2].getBuffer(), srcStrideV, srcPixelStrideY, srcPixelStrideUV, imageWidth, imageHeight, srcPixelStrideY, srcPixelStrideUV, srcPixelStrideUV) != 0) {
            return Result.ERROR_CONVERSION;
        }
        return Result.SUCCESS;
    }

    private static Result rotateYUVInternal(ImageProxy imageProxy, ImageWriter rotatedImageWriter, ByteBuffer yRotatedBuffer, ByteBuffer uRotatedBuffer, ByteBuffer vRotatedBuffer, int rotationDegrees) {
        int imageWidth = imageProxy.getWidth();
        int imageHeight = imageProxy.getHeight();
        int srcStrideY = imageProxy.getPlanes()[0].getRowStride();
        int srcStrideU = imageProxy.getPlanes()[1].getRowStride();
        int srcStrideV = imageProxy.getPlanes()[2].getRowStride();
        int srcPixelStrideUV = imageProxy.getPlanes()[1].getPixelStride();
        Image rotatedImage = ImageWriterCompat.dequeueInputImage(rotatedImageWriter);
        if (rotatedImage == null) {
            return Result.ERROR_CONVERSION;
        }
        Image rotatedImage2 = rotatedImage;
        if (nativeRotateYUV(imageProxy.getPlanes()[0].getBuffer(), srcStrideY, imageProxy.getPlanes()[1].getBuffer(), srcStrideU, imageProxy.getPlanes()[2].getBuffer(), srcStrideV, srcPixelStrideUV, rotatedImage.getPlanes()[0].getBuffer(), rotatedImage.getPlanes()[0].getRowStride(), rotatedImage.getPlanes()[0].getPixelStride(), rotatedImage.getPlanes()[1].getBuffer(), rotatedImage.getPlanes()[1].getRowStride(), rotatedImage.getPlanes()[1].getPixelStride(), rotatedImage.getPlanes()[2].getBuffer(), rotatedImage.getPlanes()[2].getRowStride(), rotatedImage.getPlanes()[2].getPixelStride(), yRotatedBuffer, uRotatedBuffer, vRotatedBuffer, imageWidth, imageHeight, rotationDegrees) != 0) {
            return Result.ERROR_CONVERSION;
        }
        ImageWriterCompat.queueInputImage(rotatedImageWriter, rotatedImage2);
        return Result.SUCCESS;
    }
}

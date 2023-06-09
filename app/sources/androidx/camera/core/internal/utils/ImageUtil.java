package androidx.camera.core.internal.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.util.Rational;
import android.util.Size;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Logger;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public final class ImageUtil {
    private static final String TAG = "ImageUtil";

    private ImageUtil() {
    }

    public static float[] sizeToVertexes(Size size) {
        return new float[]{0.0f, 0.0f, (float) size.getWidth(), 0.0f, (float) size.getWidth(), (float) size.getHeight(), 0.0f, (float) size.getHeight()};
    }

    public static float min(float value1, float value2, float value3, float value4) {
        return Math.min(Math.min(value1, value2), Math.min(value3, value4));
    }

    public static Rational getRotatedAspectRatio(int rotationDegrees, Rational aspectRatio) {
        if (rotationDegrees == 90 || rotationDegrees == 270) {
            return inverseRational(aspectRatio);
        }
        return new Rational(aspectRatio.getNumerator(), aspectRatio.getDenominator());
    }

    public static byte[] jpegImageToJpegByteArray(ImageProxy image) {
        if (image.getFormat() == 256) {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] data = new byte[buffer.capacity()];
            buffer.rewind();
            buffer.get(data);
            return data;
        }
        throw new IllegalArgumentException("Incorrect image format of the input image proxy: " + image.getFormat());
    }

    public static byte[] jpegImageToJpegByteArray(ImageProxy image, Rect cropRect, int jpegQuality) throws CodecFailedException {
        if (image.getFormat() == 256) {
            return cropJpegByteArray(jpegImageToJpegByteArray(image), cropRect, jpegQuality);
        }
        throw new IllegalArgumentException("Incorrect image format of the input image proxy: " + image.getFormat());
    }

    public static byte[] yuvImageToJpegByteArray(ImageProxy image, Rect cropRect, int jpegQuality) throws CodecFailedException {
        if (image.getFormat() == 35) {
            return nv21ToJpeg(yuv_420_888toNv21(image), image.getWidth(), image.getHeight(), cropRect, jpegQuality);
        }
        throw new IllegalArgumentException("Incorrect image format of the input image proxy: " + image.getFormat());
    }

    public static byte[] yuv_420_888toNv21(ImageProxy image) {
        ImageProxy.PlaneProxy yPlane = image.getPlanes()[0];
        ImageProxy.PlaneProxy uPlane = image.getPlanes()[1];
        ImageProxy.PlaneProxy vPlane = image.getPlanes()[2];
        ByteBuffer yBuffer = yPlane.getBuffer();
        ByteBuffer uBuffer = uPlane.getBuffer();
        ByteBuffer vBuffer = vPlane.getBuffer();
        yBuffer.rewind();
        uBuffer.rewind();
        vBuffer.rewind();
        int ySize = yBuffer.remaining();
        int position = 0;
        byte[] nv21 = new byte[(((image.getWidth() * image.getHeight()) / 2) + ySize)];
        for (int row = 0; row < image.getHeight(); row++) {
            yBuffer.get(nv21, position, image.getWidth());
            position += image.getWidth();
            yBuffer.position(Math.min(ySize, (yBuffer.position() - image.getWidth()) + yPlane.getRowStride()));
        }
        int chromaHeight = image.getHeight() / 2;
        int chromaWidth = image.getWidth() / 2;
        int vRowStride = vPlane.getRowStride();
        int uRowStride = uPlane.getRowStride();
        int vPixelStride = vPlane.getPixelStride();
        int uPixelStride = uPlane.getPixelStride();
        byte[] vLineBuffer = new byte[vRowStride];
        ImageProxy.PlaneProxy planeProxy = yPlane;
        byte[] uLineBuffer = new byte[uRowStride];
        ImageProxy.PlaneProxy planeProxy2 = uPlane;
        int row2 = 0;
        while (row2 < chromaHeight) {
            ImageProxy.PlaneProxy vPlane2 = vPlane;
            int vRowStride2 = vRowStride;
            vBuffer.get(vLineBuffer, 0, Math.min(vRowStride, vBuffer.remaining()));
            uBuffer.get(uLineBuffer, 0, Math.min(uRowStride, uBuffer.remaining()));
            int vLineBufferPosition = 0;
            int uLineBufferPosition = 0;
            for (int col = 0; col < chromaWidth; col++) {
                int position2 = position + 1;
                nv21[position] = vLineBuffer[vLineBufferPosition];
                position = position2 + 1;
                nv21[position2] = uLineBuffer[uLineBufferPosition];
                vLineBufferPosition += vPixelStride;
                uLineBufferPosition += uPixelStride;
            }
            row2++;
            vPlane = vPlane2;
            vRowStride = vRowStride2;
        }
        return nv21;
    }

    private static byte[] cropJpegByteArray(byte[] data, Rect cropRect, int jpegQuality) throws CodecFailedException {
        try {
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(data, 0, data.length, false);
            Bitmap bitmap = decoder.decodeRegion(cropRect, new BitmapFactory.Options());
            decoder.recycle();
            if (bitmap != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, jpegQuality, out)) {
                    bitmap.recycle();
                    return out.toByteArray();
                }
                throw new CodecFailedException("Encode bitmap failed.", CodecFailedException.FailureType.ENCODE_FAILED);
            }
            throw new CodecFailedException("Decode byte array failed.", CodecFailedException.FailureType.DECODE_FAILED);
        } catch (IllegalArgumentException e) {
            throw new CodecFailedException("Decode byte array failed with illegal argument." + e, CodecFailedException.FailureType.DECODE_FAILED);
        } catch (IOException e2) {
            throw new CodecFailedException("Decode byte array failed.", CodecFailedException.FailureType.DECODE_FAILED);
        }
    }

    public static boolean isAspectRatioValid(Rational aspectRatio) {
        return aspectRatio != null && aspectRatio.floatValue() > 0.0f && !aspectRatio.isNaN();
    }

    public static boolean isAspectRatioValid(Size sourceSize, Rational aspectRatio) {
        return aspectRatio != null && aspectRatio.floatValue() > 0.0f && isCropAspectRatioHasEffect(sourceSize, aspectRatio) && !aspectRatio.isNaN();
    }

    public static Rect computeCropRectFromAspectRatio(Size sourceSize, Rational aspectRatio) {
        if (!isAspectRatioValid(aspectRatio)) {
            Logger.w(TAG, "Invalid view ratio.");
            return null;
        }
        int sourceWidth = sourceSize.getWidth();
        int sourceHeight = sourceSize.getHeight();
        float srcRatio = ((float) sourceWidth) / ((float) sourceHeight);
        int cropLeft = 0;
        int cropTop = 0;
        int outputWidth = sourceWidth;
        int outputHeight = sourceHeight;
        int numerator = aspectRatio.getNumerator();
        int denominator = aspectRatio.getDenominator();
        if (aspectRatio.floatValue() > srcRatio) {
            outputHeight = Math.round((((float) sourceWidth) / ((float) numerator)) * ((float) denominator));
            cropTop = (sourceHeight - outputHeight) / 2;
        } else {
            outputWidth = Math.round((((float) sourceHeight) / ((float) denominator)) * ((float) numerator));
            cropLeft = (sourceWidth - outputWidth) / 2;
        }
        return new Rect(cropLeft, cropTop, cropLeft + outputWidth, cropTop + outputHeight);
    }

    public static Rect computeCropRectFromDispatchInfo(Rect surfaceCropRect, int surfaceToOutputDegrees, Size dispatchResolution, int dispatchToOutputDegrees) {
        Matrix matrix = new Matrix();
        matrix.setRotate((float) (dispatchToOutputDegrees - surfaceToOutputDegrees));
        float[] vertexes = sizeToVertexes(dispatchResolution);
        matrix.mapPoints(vertexes);
        matrix.postTranslate(-min(vertexes[0], vertexes[2], vertexes[4], vertexes[6]), -min(vertexes[1], vertexes[3], vertexes[5], vertexes[7]));
        matrix.invert(matrix);
        RectF dispatchCropRectF = new RectF();
        matrix.mapRect(dispatchCropRectF, new RectF(surfaceCropRect));
        dispatchCropRectF.sort();
        Rect dispatchCropRect = new Rect();
        dispatchCropRectF.round(dispatchCropRect);
        return dispatchCropRect;
    }

    private static byte[] nv21ToJpeg(byte[] nv21, int width, int height, Rect cropRect, int jpegQuality) throws CodecFailedException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (new YuvImage(nv21, 17, width, height, (int[]) null).compressToJpeg(cropRect == null ? new Rect(0, 0, width, height) : cropRect, jpegQuality, out)) {
            return out.toByteArray();
        }
        throw new CodecFailedException("YuvImage failed to encode jpeg.", CodecFailedException.FailureType.ENCODE_FAILED);
    }

    private static boolean isCropAspectRatioHasEffect(Size sourceSize, Rational aspectRatio) {
        int sourceWidth = sourceSize.getWidth();
        int sourceHeight = sourceSize.getHeight();
        int numerator = aspectRatio.getNumerator();
        int denominator = aspectRatio.getDenominator();
        return (sourceHeight == Math.round((((float) sourceWidth) / ((float) numerator)) * ((float) denominator)) && sourceWidth == Math.round((((float) sourceHeight) / ((float) denominator)) * ((float) numerator))) ? false : true;
    }

    private static Rational inverseRational(Rational rational) {
        if (rational == null) {
            return rational;
        }
        return new Rational(rational.getDenominator(), rational.getNumerator());
    }

    public static boolean shouldCropImage(ImageProxy image) {
        return shouldCropImage(image.getWidth(), image.getHeight(), image.getCropRect().width(), image.getCropRect().height());
    }

    public static boolean shouldCropImage(int sourceWidth, int sourceHeight, int cropRectWidth, int cropRectHeight) {
        return (sourceWidth == cropRectWidth && sourceHeight == cropRectHeight) ? false : true;
    }

    public static final class CodecFailedException extends Exception {
        private FailureType mFailureType;

        public enum FailureType {
            ENCODE_FAILED,
            DECODE_FAILED,
            UNKNOWN
        }

        CodecFailedException(String message) {
            super(message);
            this.mFailureType = FailureType.UNKNOWN;
        }

        CodecFailedException(String message, FailureType failureType) {
            super(message);
            this.mFailureType = failureType;
        }

        public FailureType getFailureType() {
            return this.mFailureType;
        }
    }
}

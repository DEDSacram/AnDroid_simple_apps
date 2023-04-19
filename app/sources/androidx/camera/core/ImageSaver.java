package androidx.camera.core;

import android.content.ContentValues;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.impl.utils.Exif;
import androidx.camera.core.internal.compat.workaround.ExifRotationAvailability;
import androidx.camera.core.internal.utils.ImageUtil;
import androidx.core.util.Preconditions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

final class ImageSaver implements Runnable {
    private static final int COPY_BUFFER_SIZE = 1024;
    private static final int NOT_PENDING = 0;
    private static final int PENDING = 1;
    private static final String TAG = "ImageSaver";
    private static final String TEMP_FILE_PREFIX = "CameraX";
    private static final String TEMP_FILE_SUFFIX = ".tmp";
    private final OnImageSavedCallback mCallback;
    private final ImageProxy mImage;
    private final int mJpegQuality;
    private final int mOrientation;
    private final ImageCapture.OutputFileOptions mOutputFileOptions;
    private final Executor mSequentialIoExecutor;
    private final Executor mUserCallbackExecutor;

    public interface OnImageSavedCallback {
        void onError(SaveError saveError, String str, Throwable th);

        void onImageSaved(ImageCapture.OutputFileResults outputFileResults);
    }

    public enum SaveError {
        FILE_IO_FAILED,
        ENCODE_FAILED,
        CROP_FAILED,
        UNKNOWN
    }

    ImageSaver(ImageProxy image, ImageCapture.OutputFileOptions outputFileOptions, int orientation, int jpegQuality, Executor userCallbackExecutor, Executor sequentialIoExecutor, OnImageSavedCallback callback) {
        this.mImage = image;
        this.mOutputFileOptions = outputFileOptions;
        this.mOrientation = orientation;
        this.mJpegQuality = jpegQuality;
        this.mCallback = callback;
        this.mUserCallbackExecutor = userCallbackExecutor;
        this.mSequentialIoExecutor = sequentialIoExecutor;
    }

    public void run() {
        File tempFile = saveImageToTempFile();
        if (tempFile != null) {
            this.mSequentialIoExecutor.execute(new ImageSaver$$ExternalSyntheticLambda2(this, tempFile));
        }
    }

    private File saveImageToTempFile() {
        File tempFile;
        FileOutputStream output;
        try {
            if (isSaveToFile()) {
                tempFile = new File(this.mOutputFileOptions.getFile().getParent(), TEMP_FILE_PREFIX + UUID.randomUUID().toString() + TEMP_FILE_SUFFIX);
            } else {
                tempFile = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);
            }
            SaveError saveError = null;
            String errorMessage = null;
            Exception exception = null;
            try {
                ImageProxy imageToClose = this.mImage;
                try {
                    output = new FileOutputStream(tempFile);
                    output.write(imageToJpegByteArray(this.mImage, this.mJpegQuality));
                    Exif exif = Exif.createFromFile(tempFile);
                    Exif.createFromImageProxy(this.mImage).copyToCroppedImage(exif);
                    if (!new ExifRotationAvailability().shouldUseExifOrientation(this.mImage)) {
                        exif.rotate(this.mOrientation);
                    }
                    ImageCapture.Metadata metadata = this.mOutputFileOptions.getMetadata();
                    if (metadata.isReversedHorizontal()) {
                        exif.flipHorizontally();
                    }
                    if (metadata.isReversedVertical()) {
                        exif.flipVertically();
                    }
                    if (metadata.getLocation() != null) {
                        exif.attachLocation(this.mOutputFileOptions.getMetadata().getLocation());
                    }
                    exif.save();
                    output.close();
                    if (imageToClose != null) {
                        imageToClose.close();
                    }
                } catch (Throwable th) {
                    if (imageToClose != null) {
                        imageToClose.close();
                    }
                    throw th;
                }
            } catch (IOException | IllegalArgumentException e) {
                saveError = SaveError.FILE_IO_FAILED;
                errorMessage = "Failed to write temp file";
                exception = e;
            } catch (ImageUtil.CodecFailedException e2) {
                switch (AnonymousClass1.$SwitchMap$androidx$camera$core$internal$utils$ImageUtil$CodecFailedException$FailureType[e2.getFailureType().ordinal()]) {
                    case 1:
                        saveError = SaveError.ENCODE_FAILED;
                        errorMessage = "Failed to encode mImage";
                        break;
                    case 2:
                        saveError = SaveError.CROP_FAILED;
                        errorMessage = "Failed to crop mImage";
                        break;
                    default:
                        saveError = SaveError.UNKNOWN;
                        errorMessage = "Failed to transcode mImage";
                        break;
                }
                exception = e2;
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            if (saveError == null) {
                return tempFile;
            }
            postError(saveError, errorMessage, exception);
            tempFile.delete();
            return null;
            throw th;
        } catch (IOException e3) {
            postError(SaveError.FILE_IO_FAILED, "Failed to create temp file", e3);
            return null;
        }
    }

    /* renamed from: androidx.camera.core.ImageSaver$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$androidx$camera$core$internal$utils$ImageUtil$CodecFailedException$FailureType;

        static {
            int[] iArr = new int[ImageUtil.CodecFailedException.FailureType.values().length];
            $SwitchMap$androidx$camera$core$internal$utils$ImageUtil$CodecFailedException$FailureType = iArr;
            try {
                iArr[ImageUtil.CodecFailedException.FailureType.ENCODE_FAILED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$androidx$camera$core$internal$utils$ImageUtil$CodecFailedException$FailureType[ImageUtil.CodecFailedException.FailureType.DECODE_FAILED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$androidx$camera$core$internal$utils$ImageUtil$CodecFailedException$FailureType[ImageUtil.CodecFailedException.FailureType.UNKNOWN.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private byte[] imageToJpegByteArray(ImageProxy image, int jpegQuality) throws ImageUtil.CodecFailedException {
        boolean shouldCropImage = ImageUtil.shouldCropImage(image);
        int imageFormat = image.getFormat();
        if (imageFormat != 256) {
            Rect rect = null;
            if (imageFormat == 35) {
                if (shouldCropImage) {
                    rect = image.getCropRect();
                }
                return ImageUtil.yuvImageToJpegByteArray(image, rect, jpegQuality);
            }
            Logger.w(TAG, "Unrecognized image format: " + imageFormat);
            return null;
        } else if (!shouldCropImage) {
            return ImageUtil.jpegImageToJpegByteArray(image);
        } else {
            return ImageUtil.jpegImageToJpegByteArray(image, image.getCropRect(), jpegQuality);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: copyTempFileToDestination */
    public void m150lambda$run$0$androidxcameracoreImageSaver(File tempFile) {
        ContentValues values;
        Preconditions.checkNotNull(tempFile);
        SaveError saveError = null;
        String errorMessage = null;
        Exception exception = null;
        Uri outputUri = null;
        try {
            if (isSaveToMediaStore()) {
                if (this.mOutputFileOptions.getContentValues() != null) {
                    values = new ContentValues(this.mOutputFileOptions.getContentValues());
                } else {
                    values = new ContentValues();
                }
                setContentValuePending(values, 1);
                outputUri = this.mOutputFileOptions.getContentResolver().insert(this.mOutputFileOptions.getSaveCollection(), values);
                if (outputUri == null) {
                    saveError = SaveError.FILE_IO_FAILED;
                    errorMessage = "Failed to insert URI.";
                } else {
                    if (!copyTempFileToUri(tempFile, outputUri)) {
                        saveError = SaveError.FILE_IO_FAILED;
                        errorMessage = "Failed to save to URI.";
                    }
                    setUriNotPending(outputUri);
                }
            } else if (isSaveToOutputStream()) {
                copyTempFileToOutputStream(tempFile, this.mOutputFileOptions.getOutputStream());
            } else if (isSaveToFile()) {
                File targetFile = this.mOutputFileOptions.getFile();
                if (targetFile.exists()) {
                    targetFile.delete();
                }
                if (!tempFile.renameTo(targetFile)) {
                    saveError = SaveError.FILE_IO_FAILED;
                    errorMessage = "Failed to rename file.";
                }
                outputUri = Uri.fromFile(targetFile);
            }
        } catch (IOException | IllegalArgumentException e) {
            saveError = SaveError.FILE_IO_FAILED;
            errorMessage = "Failed to write destination file.";
            exception = e;
        } catch (Throwable th) {
            tempFile.delete();
            throw th;
        }
        tempFile.delete();
        if (saveError != null) {
            postError(saveError, errorMessage, exception);
        } else {
            postSuccess(outputUri);
        }
    }

    private boolean isSaveToMediaStore() {
        return (this.mOutputFileOptions.getSaveCollection() == null || this.mOutputFileOptions.getContentResolver() == null || this.mOutputFileOptions.getContentValues() == null) ? false : true;
    }

    private boolean isSaveToFile() {
        return this.mOutputFileOptions.getFile() != null;
    }

    private boolean isSaveToOutputStream() {
        return this.mOutputFileOptions.getOutputStream() != null;
    }

    private void setUriNotPending(Uri outputUri) {
        if (Build.VERSION.SDK_INT >= 29) {
            ContentValues values = new ContentValues();
            setContentValuePending(values, 0);
            this.mOutputFileOptions.getContentResolver().update(outputUri, values, (String) null, (String[]) null);
        }
    }

    private void setContentValuePending(ContentValues values, int isPending) {
        if (Build.VERSION.SDK_INT >= 29) {
            values.put("is_pending", Integer.valueOf(isPending));
        }
    }

    private boolean copyTempFileToUri(File tempFile, Uri uri) throws IOException {
        OutputStream outputStream = this.mOutputFileOptions.getContentResolver().openOutputStream(uri);
        if (outputStream == null) {
            if (outputStream != null) {
                outputStream.close();
            }
            return false;
        }
        try {
            copyTempFileToOutputStream(tempFile, outputStream);
            if (outputStream == null) {
                return true;
            }
            outputStream.close();
            return true;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    private void copyTempFileToOutputStream(File tempFile, OutputStream outputStream) throws IOException {
        InputStream in = new FileInputStream(tempFile);
        try {
            byte[] buf = new byte[1024];
            while (true) {
                int read = in.read(buf);
                int len = read;
                if (read > 0) {
                    outputStream.write(buf, 0, len);
                } else {
                    in.close();
                    return;
                }
            }
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    private void postSuccess(Uri outputUri) {
        try {
            this.mUserCallbackExecutor.execute(new ImageSaver$$ExternalSyntheticLambda0(this, outputUri));
        } catch (RejectedExecutionException e) {
            Logger.e(TAG, "Application executor rejected executing OnImageSavedCallback.onImageSaved callback. Skipping.");
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$postSuccess$1$androidx-camera-core-ImageSaver  reason: not valid java name */
    public /* synthetic */ void m149lambda$postSuccess$1$androidxcameracoreImageSaver(Uri outputUri) {
        this.mCallback.onImageSaved(new ImageCapture.OutputFileResults(outputUri));
    }

    private void postError(SaveError saveError, String message, Throwable cause) {
        try {
            this.mUserCallbackExecutor.execute(new ImageSaver$$ExternalSyntheticLambda1(this, saveError, message, cause));
        } catch (RejectedExecutionException e) {
            Logger.e(TAG, "Application executor rejected executing OnImageSavedCallback.onError callback. Skipping.");
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$postError$2$androidx-camera-core-ImageSaver  reason: not valid java name */
    public /* synthetic */ void m148lambda$postError$2$androidxcameracoreImageSaver(SaveError saveError, String message, Throwable cause) {
        this.mCallback.onError(saveError, message, cause);
    }
}

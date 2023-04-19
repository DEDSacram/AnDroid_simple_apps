package androidx.camera.core;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.location.Location;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import androidx.camera.core.ForwardingImageProxy;
import androidx.camera.core.ImageSaver;
import androidx.camera.core.ProcessingImageReader;
import androidx.camera.core.UseCase;
import androidx.camera.core.impl.CameraCaptureCallback;
import androidx.camera.core.impl.CameraInternal;
import androidx.camera.core.impl.CaptureBundle;
import androidx.camera.core.impl.CaptureConfig;
import androidx.camera.core.impl.CaptureProcessor;
import androidx.camera.core.impl.CaptureStage;
import androidx.camera.core.impl.Config;
import androidx.camera.core.impl.ConfigProvider;
import androidx.camera.core.impl.DeferrableSurface;
import androidx.camera.core.impl.ImageCaptureConfig;
import androidx.camera.core.impl.ImageInputConfig;
import androidx.camera.core.impl.ImageOutputConfig;
import androidx.camera.core.impl.ImageReaderProxy;
import androidx.camera.core.impl.ImmediateSurface;
import androidx.camera.core.impl.MutableConfig;
import androidx.camera.core.impl.MutableOptionsBundle;
import androidx.camera.core.impl.MutableTagBundle;
import androidx.camera.core.impl.OptionsBundle;
import androidx.camera.core.impl.SessionConfig;
import androidx.camera.core.impl.SessionProcessor;
import androidx.camera.core.impl.UseCaseConfig;
import androidx.camera.core.impl.UseCaseConfigFactory;
import androidx.camera.core.impl.utils.CameraOrientationUtil;
import androidx.camera.core.impl.utils.Exif;
import androidx.camera.core.impl.utils.Threads;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.impl.utils.futures.FutureCallback;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.camera.core.internal.IoConfig;
import androidx.camera.core.internal.TargetConfig;
import androidx.camera.core.internal.YuvToJpegProcessor;
import androidx.camera.core.internal.compat.workaround.ExifRotationAvailability;
import androidx.camera.core.internal.utils.ImageUtil;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.util.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class ImageCapture extends UseCase {
    public static final int CAPTURE_MODE_MAXIMIZE_QUALITY = 0;
    public static final int CAPTURE_MODE_MINIMIZE_LATENCY = 1;
    public static final int CAPTURE_MODE_ZERO_SHUTTER_LAG = 2;
    private static final int DEFAULT_CAPTURE_MODE = 1;
    public static final Defaults DEFAULT_CONFIG = new Defaults();
    private static final int DEFAULT_FLASH_MODE = 2;
    public static final int ERROR_CAMERA_CLOSED = 3;
    public static final int ERROR_CAPTURE_FAILED = 2;
    public static final int ERROR_FILE_IO = 1;
    public static final int ERROR_INVALID_CAMERA = 4;
    public static final int ERROR_UNKNOWN = 0;
    static final ExifRotationAvailability EXIF_ROTATION_AVAILABILITY = new ExifRotationAvailability();
    public static final int FLASH_MODE_AUTO = 0;
    public static final int FLASH_MODE_OFF = 2;
    public static final int FLASH_MODE_ON = 1;
    private static final int FLASH_MODE_UNKNOWN = -1;
    public static final int FLASH_TYPE_ONE_SHOT_FLASH = 0;
    public static final int FLASH_TYPE_USE_TORCH_AS_FLASH = 1;
    private static final byte JPEG_QUALITY_MAXIMIZE_QUALITY_MODE = 100;
    private static final byte JPEG_QUALITY_MINIMIZE_LATENCY_MODE = 95;
    private static final int MAX_IMAGES = 2;
    private static final String TAG = "ImageCapture";
    private CaptureBundle mCaptureBundle;
    private CaptureConfig mCaptureConfig;
    private final int mCaptureMode;
    private CaptureProcessor mCaptureProcessor;
    private final ImageReaderProxy.OnImageAvailableListener mClosingListener = ImageCapture$$ExternalSyntheticLambda10.INSTANCE;
    private Rational mCropAspectRatio = null;
    private DeferrableSurface mDeferrableSurface;
    private ExecutorService mExecutor;
    private int mFlashMode = -1;
    private final int mFlashType;
    private ImageCaptureRequestProcessor mImageCaptureRequestProcessor;
    SafeCloseImageReaderProxy mImageReader;
    private ListenableFuture<Void> mImageReaderCloseFuture = Futures.immediateFuture(null);
    final Executor mIoExecutor;
    private final AtomicReference<Integer> mLockedFlashMode = new AtomicReference<>((Object) null);
    private int mMaxCaptureStages;
    private CameraCaptureCallback mMetadataMatchingCaptureCallback;
    ProcessingImageReader mProcessingImageReader;
    private Matrix mSensorToBufferTransformMatrix = new Matrix();
    final Executor mSequentialIoExecutor;
    SessionConfig.Builder mSessionConfigBuilder;
    private boolean mUseSoftwareJpeg = false;

    @Retention(RetentionPolicy.SOURCE)
    public @interface CaptureMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface FlashMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface FlashType {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ImageCaptureError {
    }

    public interface OnImageSavedCallback {
        void onError(ImageCaptureException imageCaptureException);

        void onImageSaved(OutputFileResults outputFileResults);
    }

    static /* synthetic */ void lambda$new$0(ImageReaderProxy imageReader) {
        ImageProxy image;
        try {
            image = imageReader.acquireLatestImage();
            Log.d(TAG, "Discarding ImageProxy which was inadvertently acquired: " + image);
            if (image != null) {
                image.close();
                return;
            }
            return;
        } catch (IllegalStateException e) {
            Log.e(TAG, "Failed to acquire latest image.", e);
            return;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    ImageCapture(ImageCaptureConfig userConfig) {
        super(userConfig);
        ImageCaptureConfig useCaseConfig = (ImageCaptureConfig) getCurrentConfig();
        if (useCaseConfig.containsOption(ImageCaptureConfig.OPTION_IMAGE_CAPTURE_MODE)) {
            this.mCaptureMode = useCaseConfig.getCaptureMode();
        } else {
            this.mCaptureMode = 1;
        }
        this.mFlashType = useCaseConfig.getFlashType(0);
        Executor executor = (Executor) Preconditions.checkNotNull(useCaseConfig.getIoExecutor(CameraXExecutors.ioExecutor()));
        this.mIoExecutor = executor;
        this.mSequentialIoExecutor = CameraXExecutors.newSequentialExecutor(executor);
    }

    /* access modifiers changed from: package-private */
    public SessionConfig.Builder createPipeline(String cameraId, ImageCaptureConfig config, Size resolution) {
        ImageCapture$$ExternalSyntheticLambda7 imageCapture$$ExternalSyntheticLambda7;
        ListenableFuture<Void> listenableFuture;
        int outputFormat;
        ImageReaderProxy imageReader;
        Threads.checkMainThread();
        SessionConfig.Builder sessionConfigBuilder = SessionConfig.Builder.createFrom(config);
        YuvToJpegProcessor softwareJpegProcessor = null;
        if (Build.VERSION.SDK_INT >= 23 && getCaptureMode() == 2) {
            getCameraControl().addZslConfig(sessionConfigBuilder);
        }
        if (config.getImageReaderProxyProvider() != null) {
            this.mImageReader = new SafeCloseImageReaderProxy(config.getImageReaderProxyProvider().newInstance(resolution.getWidth(), resolution.getHeight(), getImageFormat(), 2, 0));
            this.mMetadataMatchingCaptureCallback = new CameraCaptureCallback() {
            };
        } else if (isSessionProcessorEnabledInCurrentCamera()) {
            if (getImageFormat() == 256) {
                imageReader = new AndroidImageReaderProxy(ImageReader.newInstance(resolution.getWidth(), resolution.getHeight(), getImageFormat(), 2));
            } else if (getImageFormat() != 35) {
                throw new IllegalArgumentException("Unsupported image format:" + getImageFormat());
            } else if (Build.VERSION.SDK_INT >= 26) {
                softwareJpegProcessor = new YuvToJpegProcessor(getJpegQualityInternal(), 2);
                ModifiableImageReaderProxy inputReader = new ModifiableImageReaderProxy(ImageReader.newInstance(resolution.getWidth(), resolution.getHeight(), 35, 2));
                CaptureBundle captureBundle = CaptureBundles.singleDefaultCaptureBundle();
                ProcessingImageReader processingImageReader = new ProcessingImageReader.Builder(inputReader, captureBundle, softwareJpegProcessor).setPostProcessExecutor(this.mExecutor).setOutputFormat(256).build();
                MutableTagBundle tagBundle = MutableTagBundle.create();
                tagBundle.putTag(processingImageReader.getTagBundleKey(), Integer.valueOf(captureBundle.getCaptureStages().get(0).getId()));
                inputReader.setImageTagBundle(tagBundle);
                imageReader = processingImageReader;
            } else {
                throw new UnsupportedOperationException("Does not support API level < 26");
            }
            this.mMetadataMatchingCaptureCallback = new CameraCaptureCallback() {
            };
            this.mImageReader = new SafeCloseImageReaderProxy(imageReader);
        } else if (this.mCaptureProcessor != null || this.mUseSoftwareJpeg) {
            CaptureProcessor captureProcessor = this.mCaptureProcessor;
            int inputFormat = getImageFormat();
            int outputFormat2 = getImageFormat();
            if (!this.mUseSoftwareJpeg) {
                outputFormat = outputFormat2;
            } else if (Build.VERSION.SDK_INT >= 26) {
                Logger.i(TAG, "Using software JPEG encoder.");
                if (this.mCaptureProcessor != null) {
                    softwareJpegProcessor = new YuvToJpegProcessor(getJpegQualityInternal(), this.mMaxCaptureStages);
                    captureProcessor = new CaptureProcessorPipeline(this.mCaptureProcessor, this.mMaxCaptureStages, softwareJpegProcessor, this.mExecutor);
                } else {
                    YuvToJpegProcessor yuvToJpegProcessor = new YuvToJpegProcessor(getJpegQualityInternal(), this.mMaxCaptureStages);
                    softwareJpegProcessor = yuvToJpegProcessor;
                    captureProcessor = yuvToJpegProcessor;
                }
                outputFormat = 256;
            } else {
                throw new IllegalStateException("Software JPEG only supported on API 26+");
            }
            ProcessingImageReader build = new ProcessingImageReader.Builder(resolution.getWidth(), resolution.getHeight(), inputFormat, this.mMaxCaptureStages, getCaptureBundle(CaptureBundles.singleDefaultCaptureBundle()), captureProcessor).setPostProcessExecutor(this.mExecutor).setOutputFormat(outputFormat).build();
            this.mProcessingImageReader = build;
            this.mMetadataMatchingCaptureCallback = build.getCameraCaptureCallback();
            this.mImageReader = new SafeCloseImageReaderProxy(this.mProcessingImageReader);
        } else {
            MetadataImageReader metadataImageReader = new MetadataImageReader(resolution.getWidth(), resolution.getHeight(), getImageFormat(), 2);
            this.mMetadataMatchingCaptureCallback = metadataImageReader.getCameraCaptureCallback();
            this.mImageReader = new SafeCloseImageReaderProxy(metadataImageReader);
        }
        ImageCaptureRequestProcessor imageCaptureRequestProcessor = this.mImageCaptureRequestProcessor;
        if (imageCaptureRequestProcessor != null) {
            imageCaptureRequestProcessor.cancelRequests(new CancellationException("Request is canceled."));
        }
        YuvToJpegProcessor finalSoftwareJpegProcessor = softwareJpegProcessor;
        ImageCapture$$ExternalSyntheticLambda6 imageCapture$$ExternalSyntheticLambda6 = new ImageCapture$$ExternalSyntheticLambda6(this);
        if (finalSoftwareJpegProcessor == null) {
            imageCapture$$ExternalSyntheticLambda7 = null;
        } else {
            imageCapture$$ExternalSyntheticLambda7 = new ImageCapture$$ExternalSyntheticLambda7(finalSoftwareJpegProcessor);
        }
        this.mImageCaptureRequestProcessor = new ImageCaptureRequestProcessor(2, imageCapture$$ExternalSyntheticLambda6, imageCapture$$ExternalSyntheticLambda7);
        this.mImageReader.setOnImageAvailableListener(this.mClosingListener, CameraXExecutors.mainThreadExecutor());
        DeferrableSurface deferrableSurface = this.mDeferrableSurface;
        if (deferrableSurface != null) {
            deferrableSurface.close();
        }
        this.mDeferrableSurface = new ImmediateSurface((Surface) Objects.requireNonNull(this.mImageReader.getSurface()), new Size(this.mImageReader.getWidth(), this.mImageReader.getHeight()), getImageFormat());
        ProcessingImageReader processingImageReader2 = this.mProcessingImageReader;
        if (processingImageReader2 != null) {
            listenableFuture = processingImageReader2.getCloseFuture();
        } else {
            listenableFuture = Futures.immediateFuture(null);
        }
        this.mImageReaderCloseFuture = listenableFuture;
        ListenableFuture<Void> terminationFuture = this.mDeferrableSurface.getTerminationFuture();
        SafeCloseImageReaderProxy safeCloseImageReaderProxy = this.mImageReader;
        Objects.requireNonNull(safeCloseImageReaderProxy);
        terminationFuture.addListener(new ImageCapture$$ExternalSyntheticLambda3(safeCloseImageReaderProxy), CameraXExecutors.mainThreadExecutor());
        sessionConfigBuilder.addNonRepeatingSurface(this.mDeferrableSurface);
        sessionConfigBuilder.addErrorListener(new ImageCapture$$ExternalSyntheticLambda11(this, cameraId, config, resolution));
        return sessionConfigBuilder;
    }

    static /* synthetic */ void lambda$createPipeline$1(YuvToJpegProcessor finalSoftwareJpegProcessor, ImageCaptureRequest imageCaptureRequest) {
        if (Build.VERSION.SDK_INT >= 26) {
            finalSoftwareJpegProcessor.setJpegQuality(imageCaptureRequest.mJpegQuality);
            finalSoftwareJpegProcessor.setRotationDegrees(imageCaptureRequest.mRotationDegrees);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$createPipeline$2$androidx-camera-core-ImageCapture  reason: not valid java name */
    public /* synthetic */ void m141lambda$createPipeline$2$androidxcameracoreImageCapture(String cameraId, ImageCaptureConfig config, Size resolution, SessionConfig sessionConfig, SessionConfig.SessionError error) {
        List<ImageCaptureRequest> pendingRequests;
        ImageCaptureRequestProcessor imageCaptureRequestProcessor = this.mImageCaptureRequestProcessor;
        if (imageCaptureRequestProcessor != null) {
            pendingRequests = imageCaptureRequestProcessor.pullOutUnfinishedRequests();
        } else {
            pendingRequests = Collections.emptyList();
        }
        clearPipeline();
        if (isCurrentCamera(cameraId)) {
            this.mSessionConfigBuilder = createPipeline(cameraId, config, resolution);
            if (this.mImageCaptureRequestProcessor != null) {
                for (ImageCaptureRequest request : pendingRequests) {
                    this.mImageCaptureRequestProcessor.sendRequest(request);
                }
            }
            updateSessionConfig(this.mSessionConfigBuilder.build());
            notifyReset();
        }
    }

    private boolean isSessionProcessorEnabledInCurrentCamera() {
        if (getCamera() == null || getCamera().getExtendedConfig().getSessionProcessor((SessionProcessor) null) == null) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void clearPipeline() {
        Threads.checkMainThread();
        ImageCaptureRequestProcessor imageCaptureRequestProcessor = this.mImageCaptureRequestProcessor;
        if (imageCaptureRequestProcessor != null) {
            imageCaptureRequestProcessor.cancelRequests(new CancellationException("Request is canceled."));
            this.mImageCaptureRequestProcessor = null;
        }
        DeferrableSurface deferrableSurface = this.mDeferrableSurface;
        this.mDeferrableSurface = null;
        this.mImageReader = null;
        this.mProcessingImageReader = null;
        this.mImageReaderCloseFuture = Futures.immediateFuture(null);
        if (deferrableSurface != null) {
            deferrableSurface.close();
        }
    }

    public UseCaseConfig<?> getDefaultConfig(boolean applyDefaultConfig, UseCaseConfigFactory factory) {
        Config captureConfig = factory.getConfig(UseCaseConfigFactory.CaptureType.IMAGE_CAPTURE, getCaptureMode());
        if (applyDefaultConfig) {
            captureConfig = Config.mergeConfigs(captureConfig, DEFAULT_CONFIG.getConfig());
        }
        if (captureConfig == null) {
            return null;
        }
        return getUseCaseConfigBuilder(captureConfig).getUseCaseConfig();
    }

    public UseCaseConfig.Builder<?, ?, ?> getUseCaseConfigBuilder(Config config) {
        return Builder.fromConfig(config);
    }

    /* JADX WARNING: type inference failed for: r10v0, types: [androidx.camera.core.impl.UseCaseConfig$Builder, androidx.camera.core.impl.UseCaseConfig$Builder<?, ?, ?>] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public androidx.camera.core.impl.UseCaseConfig<?> onMergeConfig(androidx.camera.core.impl.CameraInfoInternal r9, androidx.camera.core.impl.UseCaseConfig.Builder<?, ?, ?> r10) {
        /*
            r8 = this;
            androidx.camera.core.impl.UseCaseConfig r0 = r10.getUseCaseConfig()
            androidx.camera.core.impl.Config$Option<androidx.camera.core.impl.CaptureProcessor> r1 = androidx.camera.core.impl.ImageCaptureConfig.OPTION_CAPTURE_PROCESSOR
            r2 = 0
            java.lang.Object r0 = r0.retrieveOption(r1, r2)
            java.lang.String r1 = "ImageCapture"
            r3 = 1
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r3)
            if (r0 == 0) goto L_0x0029
            int r0 = android.os.Build.VERSION.SDK_INT
            r5 = 29
            if (r0 < r5) goto L_0x0029
            java.lang.String r0 = "Requesting software JPEG due to a CaptureProcessor is set."
            androidx.camera.core.Logger.i(r1, r0)
            androidx.camera.core.impl.MutableConfig r0 = r10.getMutableConfig()
            androidx.camera.core.impl.Config$Option<java.lang.Boolean> r1 = androidx.camera.core.impl.ImageCaptureConfig.OPTION_USE_SOFTWARE_JPEG_ENCODER
            r0.insertOption(r1, r4)
            goto L_0x005c
        L_0x0029:
            androidx.camera.core.impl.Quirks r0 = r9.getCameraQuirks()
            java.lang.Class<androidx.camera.core.internal.compat.quirk.SoftwareJpegEncodingPreferredQuirk> r5 = androidx.camera.core.internal.compat.quirk.SoftwareJpegEncodingPreferredQuirk.class
            boolean r0 = r0.contains(r5)
            if (r0 == 0) goto L_0x005c
            java.lang.Boolean r0 = java.lang.Boolean.FALSE
            androidx.camera.core.impl.MutableConfig r5 = r10.getMutableConfig()
            androidx.camera.core.impl.Config$Option<java.lang.Boolean> r6 = androidx.camera.core.impl.ImageCaptureConfig.OPTION_USE_SOFTWARE_JPEG_ENCODER
            java.lang.Object r5 = r5.retrieveOption(r6, r4)
            boolean r0 = r0.equals(r5)
            if (r0 == 0) goto L_0x004e
            java.lang.String r0 = "Device quirk suggests software JPEG encoder, but it has been explicitly disabled."
            androidx.camera.core.Logger.w(r1, r0)
            goto L_0x005c
        L_0x004e:
            java.lang.String r0 = "Requesting software JPEG due to device quirk."
            androidx.camera.core.Logger.i(r1, r0)
            androidx.camera.core.impl.MutableConfig r0 = r10.getMutableConfig()
            androidx.camera.core.impl.Config$Option<java.lang.Boolean> r1 = androidx.camera.core.impl.ImageCaptureConfig.OPTION_USE_SOFTWARE_JPEG_ENCODER
            r0.insertOption(r1, r4)
        L_0x005c:
            androidx.camera.core.impl.MutableConfig r0 = r10.getMutableConfig()
            boolean r0 = enforceSoftwareJpegConstraints(r0)
            androidx.camera.core.impl.MutableConfig r1 = r10.getMutableConfig()
            androidx.camera.core.impl.Config$Option<java.lang.Integer> r4 = androidx.camera.core.impl.ImageCaptureConfig.OPTION_BUFFER_FORMAT
            java.lang.Object r1 = r1.retrieveOption(r4, r2)
            java.lang.Integer r1 = (java.lang.Integer) r1
            r4 = 0
            r5 = 35
            if (r1 == 0) goto L_0x009f
            androidx.camera.core.impl.MutableConfig r6 = r10.getMutableConfig()
            androidx.camera.core.impl.Config$Option<androidx.camera.core.impl.CaptureProcessor> r7 = androidx.camera.core.impl.ImageCaptureConfig.OPTION_CAPTURE_PROCESSOR
            java.lang.Object r2 = r6.retrieveOption(r7, r2)
            if (r2 != 0) goto L_0x0084
            r2 = r3
            goto L_0x0085
        L_0x0084:
            r2 = r4
        L_0x0085:
            java.lang.String r6 = "Cannot set buffer format with CaptureProcessor defined."
            androidx.core.util.Preconditions.checkArgument(r2, r6)
            androidx.camera.core.impl.MutableConfig r2 = r10.getMutableConfig()
            androidx.camera.core.impl.Config$Option<java.lang.Integer> r6 = androidx.camera.core.impl.ImageInputConfig.OPTION_INPUT_FORMAT
            if (r0 == 0) goto L_0x0093
            goto L_0x0097
        L_0x0093:
            int r5 = r1.intValue()
        L_0x0097:
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r2.insertOption(r6, r5)
            goto L_0x0102
        L_0x009f:
            androidx.camera.core.impl.MutableConfig r6 = r10.getMutableConfig()
            androidx.camera.core.impl.Config$Option<androidx.camera.core.impl.CaptureProcessor> r7 = androidx.camera.core.impl.ImageCaptureConfig.OPTION_CAPTURE_PROCESSOR
            java.lang.Object r6 = r6.retrieveOption(r7, r2)
            if (r6 != 0) goto L_0x00f5
            if (r0 == 0) goto L_0x00ae
            goto L_0x00f5
        L_0x00ae:
            androidx.camera.core.impl.MutableConfig r6 = r10.getMutableConfig()
            androidx.camera.core.impl.Config$Option r7 = androidx.camera.core.impl.ImageCaptureConfig.OPTION_SUPPORTED_RESOLUTIONS
            java.lang.Object r2 = r6.retrieveOption(r7, r2)
            java.util.List r2 = (java.util.List) r2
            r6 = 256(0x100, float:3.59E-43)
            if (r2 != 0) goto L_0x00cd
            androidx.camera.core.impl.MutableConfig r5 = r10.getMutableConfig()
            androidx.camera.core.impl.Config$Option<java.lang.Integer> r7 = androidx.camera.core.impl.ImageInputConfig.OPTION_INPUT_FORMAT
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5.insertOption(r7, r6)
            goto L_0x0102
        L_0x00cd:
            boolean r7 = isImageFormatSupported(r2, r6)
            if (r7 == 0) goto L_0x00e1
            androidx.camera.core.impl.MutableConfig r5 = r10.getMutableConfig()
            androidx.camera.core.impl.Config$Option<java.lang.Integer> r7 = androidx.camera.core.impl.ImageInputConfig.OPTION_INPUT_FORMAT
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5.insertOption(r7, r6)
            goto L_0x0102
        L_0x00e1:
            boolean r6 = isImageFormatSupported(r2, r5)
            if (r6 == 0) goto L_0x0102
            androidx.camera.core.impl.MutableConfig r6 = r10.getMutableConfig()
            androidx.camera.core.impl.Config$Option<java.lang.Integer> r7 = androidx.camera.core.impl.ImageInputConfig.OPTION_INPUT_FORMAT
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r6.insertOption(r7, r5)
            goto L_0x0102
        L_0x00f5:
            androidx.camera.core.impl.MutableConfig r2 = r10.getMutableConfig()
            androidx.camera.core.impl.Config$Option<java.lang.Integer> r6 = androidx.camera.core.impl.ImageInputConfig.OPTION_INPUT_FORMAT
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r2.insertOption(r6, r5)
        L_0x0102:
            androidx.camera.core.impl.MutableConfig r2 = r10.getMutableConfig()
            androidx.camera.core.impl.Config$Option<java.lang.Integer> r5 = androidx.camera.core.impl.ImageCaptureConfig.OPTION_MAX_CAPTURE_STAGES
            r6 = 2
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.Object r2 = r2.retrieveOption(r5, r6)
            java.lang.Integer r2 = (java.lang.Integer) r2
            java.lang.String r5 = "Maximum outstanding image count must be at least 1"
            androidx.core.util.Preconditions.checkNotNull(r2, r5)
            int r6 = r2.intValue()
            if (r6 < r3) goto L_0x0120
            goto L_0x0121
        L_0x0120:
            r3 = r4
        L_0x0121:
            androidx.core.util.Preconditions.checkArgument(r3, r5)
            androidx.camera.core.impl.UseCaseConfig r3 = r10.getUseCaseConfig()
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.camera.core.ImageCapture.onMergeConfig(androidx.camera.core.impl.CameraInfoInternal, androidx.camera.core.impl.UseCaseConfig$Builder):androidx.camera.core.impl.UseCaseConfig");
    }

    private static boolean isImageFormatSupported(List<Pair<Integer, Size[]>> supportedSizes, int imageFormat) {
        if (supportedSizes == null) {
            return false;
        }
        for (Pair<Integer, Size[]> supportedSize : supportedSizes) {
            if (((Integer) supportedSize.first).equals(Integer.valueOf(imageFormat))) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onCameraControlReady() {
        trySetFlashModeToCameraControl();
    }

    public int getFlashMode() {
        int i;
        synchronized (this.mLockedFlashMode) {
            i = this.mFlashMode;
            if (i == -1) {
                i = ((ImageCaptureConfig) getCurrentConfig()).getFlashMode(2);
            }
        }
        return i;
    }

    public void setFlashMode(int flashMode) {
        if (flashMode == 0 || flashMode == 1 || flashMode == 2) {
            synchronized (this.mLockedFlashMode) {
                this.mFlashMode = flashMode;
                trySetFlashModeToCameraControl();
            }
            return;
        }
        throw new IllegalArgumentException("Invalid flash mode: " + flashMode);
    }

    public void setCropAspectRatio(Rational aspectRatio) {
        this.mCropAspectRatio = aspectRatio;
    }

    public int getTargetRotation() {
        return getTargetRotationInternal();
    }

    public void setTargetRotation(int rotation) {
        int oldRotation = getTargetRotation();
        if (setTargetRotationInternal(rotation) && this.mCropAspectRatio != null) {
            this.mCropAspectRatio = ImageUtil.getRotatedAspectRatio(Math.abs(CameraOrientationUtil.surfaceRotationToDegrees(rotation) - CameraOrientationUtil.surfaceRotationToDegrees(oldRotation)), this.mCropAspectRatio);
        }
    }

    public int getCaptureMode() {
        return this.mCaptureMode;
    }

    public int getJpegQuality() {
        return getJpegQualityInternal();
    }

    public ResolutionInfo getResolutionInfo() {
        return super.getResolutionInfo();
    }

    public void setSensorToBufferTransformMatrix(Matrix sensorToBufferTransformMatrix) {
        this.mSensorToBufferTransformMatrix = sensorToBufferTransformMatrix;
    }

    /* access modifiers changed from: protected */
    public ResolutionInfo getResolutionInfoInternal() {
        CameraInternal camera = getCamera();
        Size resolution = getAttachedSurfaceResolution();
        if (camera == null || resolution == null) {
            return null;
        }
        Rect cropRect = getViewPortCropRect();
        Rational cropAspectRatio = this.mCropAspectRatio;
        if (cropRect == null) {
            if (cropAspectRatio != null) {
                cropRect = ImageUtil.computeCropRectFromAspectRatio(resolution, cropAspectRatio);
            } else {
                cropRect = new Rect(0, 0, resolution.getWidth(), resolution.getHeight());
            }
        }
        return ResolutionInfo.create(resolution, (Rect) Objects.requireNonNull(cropRect), getRelativeRotation(camera));
    }

    /* renamed from: takePicture */
    public void m143lambda$takePicture$3$androidxcameracoreImageCapture(Executor executor, OnImageCapturedCallback callback) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            CameraXExecutors.mainThreadExecutor().execute(new ImageCapture$$ExternalSyntheticLambda2(this, executor, callback));
        } else {
            sendImageCaptureRequest(executor, callback, false);
        }
    }

    /* renamed from: takePicture */
    public void m144lambda$takePicture$4$androidxcameracoreImageCapture(OutputFileOptions outputFileOptions, Executor executor, final OnImageSavedCallback imageSavedCallback) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            CameraXExecutors.mainThreadExecutor().execute(new ImageCapture$$ExternalSyntheticLambda1(this, outputFileOptions, executor, imageSavedCallback));
            return;
        }
        final ImageSaver.OnImageSavedCallback imageSavedCallbackWrapper = new ImageSaver.OnImageSavedCallback() {
            public void onImageSaved(OutputFileResults outputFileResults) {
                imageSavedCallback.onImageSaved(outputFileResults);
            }

            public void onError(ImageSaver.SaveError error, String message, Throwable cause) {
                int imageCaptureError = 0;
                if (error == ImageSaver.SaveError.FILE_IO_FAILED) {
                    imageCaptureError = 1;
                }
                imageSavedCallback.onError(new ImageCaptureException(imageCaptureError, message, cause));
            }
        };
        final OutputFileOptions outputFileOptions2 = outputFileOptions;
        final int jpegQualityInternal = getJpegQualityInternal();
        final Executor executor2 = executor;
        final OnImageSavedCallback onImageSavedCallback = imageSavedCallback;
        sendImageCaptureRequest(CameraXExecutors.mainThreadExecutor(), new OnImageCapturedCallback() {
            public void onCaptureSuccess(ImageProxy image) {
                ImageCapture.this.mIoExecutor.execute(new ImageSaver(image, outputFileOptions2, image.getImageInfo().getRotationDegrees(), jpegQualityInternal, executor2, ImageCapture.this.mSequentialIoExecutor, imageSavedCallbackWrapper));
            }

            public void onError(ImageCaptureException exception) {
                onImageSavedCallback.onError(exception);
            }
        }, true);
    }

    static Rect computeDispatchCropRect(Rect viewPortCropRect, Rational cropAspectRatio, int rotationDegrees, Size dispatchResolution, int dispatchRotationDegrees) {
        if (viewPortCropRect != null) {
            return ImageUtil.computeCropRectFromDispatchInfo(viewPortCropRect, rotationDegrees, dispatchResolution, dispatchRotationDegrees);
        }
        if (cropAspectRatio != null) {
            Rational aspectRatio = cropAspectRatio;
            if (dispatchRotationDegrees % 180 != 0) {
                aspectRatio = new Rational(cropAspectRatio.getDenominator(), cropAspectRatio.getNumerator());
            }
            if (ImageUtil.isAspectRatioValid(dispatchResolution, aspectRatio)) {
                return (Rect) Objects.requireNonNull(ImageUtil.computeCropRectFromAspectRatio(dispatchResolution, aspectRatio));
            }
        }
        return new Rect(0, 0, dispatchResolution.getWidth(), dispatchResolution.getHeight());
    }

    public void onStateDetached() {
        abortImageCaptureRequests();
    }

    private void abortImageCaptureRequests() {
        if (this.mImageCaptureRequestProcessor != null) {
            this.mImageCaptureRequestProcessor.cancelRequests(new CameraClosedException("Camera is closed."));
        }
    }

    private void sendImageCaptureRequest(Executor callbackExecutor, OnImageCapturedCallback callback, boolean saveImage) {
        CameraInternal attachedCamera = getCamera();
        if (attachedCamera == null) {
            callbackExecutor.execute(new ImageCapture$$ExternalSyntheticLambda14(this, callback));
            return;
        }
        ImageCaptureRequestProcessor imageCaptureRequestProcessor = this.mImageCaptureRequestProcessor;
        if (imageCaptureRequestProcessor == null) {
            callbackExecutor.execute(new ImageCapture$$ExternalSyntheticLambda13(callback));
        } else {
            imageCaptureRequestProcessor.sendRequest(new ImageCaptureRequest(getRelativeRotation(attachedCamera), getJpegQualityForImageCaptureRequest(attachedCamera, saveImage), this.mCropAspectRatio, getViewPortCropRect(), this.mSensorToBufferTransformMatrix, callbackExecutor, callback));
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$sendImageCaptureRequest$5$androidx-camera-core-ImageCapture  reason: not valid java name */
    public /* synthetic */ void m142lambda$sendImageCaptureRequest$5$androidxcameracoreImageCapture(OnImageCapturedCallback callback) {
        callback.onError(new ImageCaptureException(4, "Not bound to a valid Camera [" + this + "]", (Throwable) null));
    }

    private int getJpegQualityForImageCaptureRequest(CameraInternal cameraInternal, boolean saveImage) {
        if (!saveImage) {
            return getJpegQualityInternal();
        }
        int rotationDegrees = getRelativeRotation(cameraInternal);
        Size dispatchResolution = (Size) Objects.requireNonNull(getAttachedSurfaceResolution());
        Rect cropRect = computeDispatchCropRect(getViewPortCropRect(), this.mCropAspectRatio, rotationDegrees, dispatchResolution, rotationDegrees);
        if (ImageUtil.shouldCropImage(dispatchResolution.getWidth(), dispatchResolution.getHeight(), cropRect.width(), cropRect.height())) {
            return this.mCaptureMode == 0 ? 100 : 95;
        }
        return getJpegQualityInternal();
    }

    private void lockFlashMode() {
        synchronized (this.mLockedFlashMode) {
            if (this.mLockedFlashMode.get() == null) {
                this.mLockedFlashMode.set(Integer.valueOf(getFlashMode()));
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001e, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void unlockFlashMode() {
        /*
            r4 = this;
            java.util.concurrent.atomic.AtomicReference<java.lang.Integer> r0 = r4.mLockedFlashMode
            monitor-enter(r0)
            java.util.concurrent.atomic.AtomicReference<java.lang.Integer> r1 = r4.mLockedFlashMode     // Catch:{ all -> 0x001f }
            r2 = 0
            java.lang.Object r1 = r1.getAndSet(r2)     // Catch:{ all -> 0x001f }
            java.lang.Integer r1 = (java.lang.Integer) r1     // Catch:{ all -> 0x001f }
            if (r1 != 0) goto L_0x0010
            monitor-exit(r0)     // Catch:{ all -> 0x001f }
            return
        L_0x0010:
            int r2 = r1.intValue()     // Catch:{ all -> 0x001f }
            int r3 = r4.getFlashMode()     // Catch:{ all -> 0x001f }
            if (r2 == r3) goto L_0x001d
            r4.trySetFlashModeToCameraControl()     // Catch:{ all -> 0x001f }
        L_0x001d:
            monitor-exit(r0)     // Catch:{ all -> 0x001f }
            return
        L_0x001f:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x001f }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.camera.core.ImageCapture.unlockFlashMode():void");
    }

    private void trySetFlashModeToCameraControl() {
        synchronized (this.mLockedFlashMode) {
            if (this.mLockedFlashMode.get() == null) {
                getCameraControl().setFlashMode(getFlashMode());
            }
        }
    }

    private int getJpegQualityInternal() {
        ImageCaptureConfig imageCaptureConfig = (ImageCaptureConfig) getCurrentConfig();
        if (imageCaptureConfig.containsOption(ImageCaptureConfig.OPTION_JPEG_COMPRESSION_QUALITY)) {
            return imageCaptureConfig.getJpegQuality();
        }
        switch (this.mCaptureMode) {
            case 0:
                return 100;
            case 1:
            case 2:
                return 95;
            default:
                throw new IllegalStateException("CaptureMode " + this.mCaptureMode + " is invalid");
        }
    }

    /* access modifiers changed from: private */
    public ListenableFuture<ImageProxy> takePictureInternal(ImageCaptureRequest imageCaptureRequest) {
        return CallbackToFutureAdapter.getFuture(new ImageCapture$$ExternalSyntheticLambda12(this, imageCaptureRequest));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$takePictureInternal$9$androidx-camera-core-ImageCapture  reason: not valid java name */
    public /* synthetic */ Object m145lambda$takePictureInternal$9$androidxcameracoreImageCapture(ImageCaptureRequest imageCaptureRequest, final CallbackToFutureAdapter.Completer completer) throws Exception {
        this.mImageReader.setOnImageAvailableListener(new ImageCapture$$ExternalSyntheticLambda9(completer), CameraXExecutors.mainThreadExecutor());
        lockFlashMode();
        ListenableFuture<Void> future = issueTakePicture(imageCaptureRequest);
        Futures.addCallback(future, new FutureCallback<Void>() {
            public void onSuccess(Void result) {
                ImageCapture.this.unlockFlashMode();
            }

            public void onFailure(Throwable throwable) {
                ImageCapture.this.unlockFlashMode();
                completer.setException(throwable);
            }
        }, this.mExecutor);
        completer.addCancellationListener(new ImageCapture$$ExternalSyntheticLambda4(future), CameraXExecutors.directExecutor());
        return "takePictureInternal";
    }

    static /* synthetic */ void lambda$takePictureInternal$7(CallbackToFutureAdapter.Completer completer, ImageReaderProxy imageReader) {
        try {
            ImageProxy image = imageReader.acquireLatestImage();
            if (image == null) {
                completer.setException(new IllegalStateException("Unable to acquire image"));
            } else if (!completer.set(image)) {
                image.close();
            }
        } catch (IllegalStateException e) {
            completer.setException(e);
        }
    }

    static class ImageCaptureRequestProcessor implements ForwardingImageProxy.OnImageCloseListener {
        ImageCaptureRequest mCurrentRequest;
        ListenableFuture<ImageProxy> mCurrentRequestFuture;
        private final ImageCaptor mImageCaptor;
        final Object mLock;
        private final int mMaxImages;
        int mOutstandingImages;
        private final Deque<ImageCaptureRequest> mPendingRequests;
        private final RequestProcessCallback mRequestProcessCallback;

        interface ImageCaptor {
            ListenableFuture<ImageProxy> capture(ImageCaptureRequest imageCaptureRequest);
        }

        interface RequestProcessCallback {
            void onPreProcessRequest(ImageCaptureRequest imageCaptureRequest);
        }

        ImageCaptureRequestProcessor(int maxImages, ImageCaptor imageCaptor) {
            this(maxImages, imageCaptor, (RequestProcessCallback) null);
        }

        ImageCaptureRequestProcessor(int maxImages, ImageCaptor imageCaptor, RequestProcessCallback requestProcessCallback) {
            this.mPendingRequests = new ArrayDeque();
            this.mCurrentRequest = null;
            this.mCurrentRequestFuture = null;
            this.mOutstandingImages = 0;
            this.mLock = new Object();
            this.mMaxImages = maxImages;
            this.mImageCaptor = imageCaptor;
            this.mRequestProcessCallback = requestProcessCallback;
        }

        public void sendRequest(ImageCaptureRequest imageCaptureRequest) {
            synchronized (this.mLock) {
                this.mPendingRequests.offer(imageCaptureRequest);
                Locale locale = Locale.US;
                Object[] objArr = new Object[2];
                objArr[0] = Integer.valueOf(this.mCurrentRequest != null ? 1 : 0);
                objArr[1] = Integer.valueOf(this.mPendingRequests.size());
                Logger.d(ImageCapture.TAG, String.format(locale, "Send image capture request [current, pending] = [%d, %d]", objArr));
                processNextRequest();
            }
        }

        public void cancelRequests(Throwable throwable) {
            ImageCaptureRequest currentRequest;
            ListenableFuture<ImageProxy> currentRequestFuture;
            List<ImageCaptureRequest> pendingRequests;
            synchronized (this.mLock) {
                currentRequest = this.mCurrentRequest;
                this.mCurrentRequest = null;
                currentRequestFuture = this.mCurrentRequestFuture;
                this.mCurrentRequestFuture = null;
                pendingRequests = new ArrayList<>(this.mPendingRequests);
                this.mPendingRequests.clear();
            }
            if (!(currentRequest == null || currentRequestFuture == null)) {
                currentRequest.notifyCallbackError(ImageCapture.getError(throwable), throwable.getMessage(), throwable);
                currentRequestFuture.cancel(true);
            }
            for (ImageCaptureRequest request : pendingRequests) {
                request.notifyCallbackError(ImageCapture.getError(throwable), throwable.getMessage(), throwable);
            }
        }

        public List<ImageCaptureRequest> pullOutUnfinishedRequests() {
            List<ImageCaptureRequest> remainingRequests;
            ListenableFuture<ImageProxy> listenableFuture;
            synchronized (this.mLock) {
                remainingRequests = new ArrayList<>(this.mPendingRequests);
                this.mPendingRequests.clear();
                ImageCaptureRequest currentRequest = this.mCurrentRequest;
                this.mCurrentRequest = null;
                if (!(currentRequest == null || (listenableFuture = this.mCurrentRequestFuture) == null || !listenableFuture.cancel(true))) {
                    remainingRequests.add(0, currentRequest);
                }
            }
            return remainingRequests;
        }

        public void onImageClose(ImageProxy image) {
            synchronized (this.mLock) {
                this.mOutstandingImages--;
                processNextRequest();
            }
        }

        /* access modifiers changed from: package-private */
        public void processNextRequest() {
            synchronized (this.mLock) {
                if (this.mCurrentRequest == null) {
                    if (this.mOutstandingImages >= this.mMaxImages) {
                        Logger.w(ImageCapture.TAG, "Too many acquire images. Close image to be able to process next.");
                        return;
                    }
                    final ImageCaptureRequest imageCaptureRequest = this.mPendingRequests.poll();
                    if (imageCaptureRequest != null) {
                        this.mCurrentRequest = imageCaptureRequest;
                        RequestProcessCallback requestProcessCallback = this.mRequestProcessCallback;
                        if (requestProcessCallback != null) {
                            requestProcessCallback.onPreProcessRequest(imageCaptureRequest);
                        }
                        ListenableFuture<ImageProxy> capture = this.mImageCaptor.capture(imageCaptureRequest);
                        this.mCurrentRequestFuture = capture;
                        Futures.addCallback(capture, new FutureCallback<ImageProxy>() {
                            public void onSuccess(ImageProxy image) {
                                synchronized (ImageCaptureRequestProcessor.this.mLock) {
                                    Preconditions.checkNotNull(image);
                                    SingleCloseImageProxy wrappedImage = new SingleCloseImageProxy(image);
                                    wrappedImage.addOnImageCloseListener(ImageCaptureRequestProcessor.this);
                                    ImageCaptureRequestProcessor.this.mOutstandingImages++;
                                    imageCaptureRequest.dispatchImage(wrappedImage);
                                    ImageCaptureRequestProcessor.this.mCurrentRequest = null;
                                    ImageCaptureRequestProcessor.this.mCurrentRequestFuture = null;
                                    ImageCaptureRequestProcessor.this.processNextRequest();
                                }
                            }

                            public void onFailure(Throwable t) {
                                synchronized (ImageCaptureRequestProcessor.this.mLock) {
                                    if (!(t instanceof CancellationException)) {
                                        imageCaptureRequest.notifyCallbackError(ImageCapture.getError(t), t != null ? t.getMessage() : "Unknown error", t);
                                    }
                                    ImageCaptureRequestProcessor.this.mCurrentRequest = null;
                                    ImageCaptureRequestProcessor.this.mCurrentRequestFuture = null;
                                    ImageCaptureRequestProcessor.this.processNextRequest();
                                }
                            }
                        }, CameraXExecutors.directExecutor());
                    }
                }
            }
        }
    }

    public String toString() {
        return "ImageCapture:" + getName();
    }

    static int getError(Throwable throwable) {
        if (throwable instanceof CameraClosedException) {
            return 3;
        }
        if (throwable instanceof ImageCaptureException) {
            return ((ImageCaptureException) throwable).getImageCaptureError();
        }
        return 0;
    }

    static boolean enforceSoftwareJpegConstraints(MutableConfig mutableConfig) {
        if (!Boolean.TRUE.equals(mutableConfig.retrieveOption(ImageCaptureConfig.OPTION_USE_SOFTWARE_JPEG_ENCODER, false))) {
            return false;
        }
        boolean supported = true;
        if (Build.VERSION.SDK_INT < 26) {
            Logger.w(TAG, "Software JPEG only supported on API 26+, but current API level is " + Build.VERSION.SDK_INT);
            supported = false;
        }
        Integer bufferFormat = (Integer) mutableConfig.retrieveOption(ImageCaptureConfig.OPTION_BUFFER_FORMAT, null);
        if (!(bufferFormat == null || bufferFormat.intValue() == 256)) {
            Logger.w(TAG, "Software JPEG cannot be used with non-JPEG output buffer format.");
            supported = false;
        }
        if (!supported) {
            Logger.w(TAG, "Unable to support software JPEG. Disabling.");
            mutableConfig.insertOption(ImageCaptureConfig.OPTION_USE_SOFTWARE_JPEG_ENCODER, false);
        }
        return supported;
    }

    public void onDetached() {
        ListenableFuture<Void> imageReaderCloseFuture = this.mImageReaderCloseFuture;
        abortImageCaptureRequests();
        clearPipeline();
        this.mUseSoftwareJpeg = false;
        ExecutorService executorService = this.mExecutor;
        Objects.requireNonNull(executorService);
        imageReaderCloseFuture.addListener(new ImageCapture$$ExternalSyntheticLambda5(executorService), CameraXExecutors.directExecutor());
    }

    public void onAttached() {
        ImageCaptureConfig useCaseConfig = (ImageCaptureConfig) getCurrentConfig();
        this.mCaptureConfig = CaptureConfig.Builder.createFrom(useCaseConfig).build();
        this.mCaptureProcessor = useCaseConfig.getCaptureProcessor((CaptureProcessor) null);
        this.mMaxCaptureStages = useCaseConfig.getMaxCaptureStages(2);
        this.mCaptureBundle = useCaseConfig.getCaptureBundle(CaptureBundles.singleDefaultCaptureBundle());
        this.mUseSoftwareJpeg = useCaseConfig.isSoftwareJpegEncoderRequested();
        Preconditions.checkNotNull(getCamera(), "Attached camera cannot be null");
        this.mExecutor = Executors.newFixedThreadPool(1, new ThreadFactory() {
            private final AtomicInteger mId = new AtomicInteger(0);

            public Thread newThread(Runnable r) {
                return new Thread(r, "CameraX-image_capture_" + this.mId.getAndIncrement());
            }
        });
    }

    /* access modifiers changed from: protected */
    public Size onSuggestedResolutionUpdated(Size suggestedResolution) {
        SessionConfig.Builder createPipeline = createPipeline(getCameraId(), (ImageCaptureConfig) getCurrentConfig(), suggestedResolution);
        this.mSessionConfigBuilder = createPipeline;
        updateSessionConfig(createPipeline.build());
        notifyActive();
        return suggestedResolution;
    }

    /* access modifiers changed from: package-private */
    public ListenableFuture<Void> issueTakePicture(ImageCaptureRequest imageCaptureRequest) {
        CaptureBundle captureBundle;
        Logger.d(TAG, "issueTakePicture");
        List<CaptureConfig> captureConfigs = new ArrayList<>();
        String tagBundleKey = null;
        if (this.mProcessingImageReader != null) {
            captureBundle = getCaptureBundle(CaptureBundles.singleDefaultCaptureBundle());
            if (captureBundle == null) {
                return Futures.immediateFailedFuture(new IllegalArgumentException("ImageCapture cannot set empty CaptureBundle."));
            }
            List<CaptureStage> captureStages = captureBundle.getCaptureStages();
            if (captureStages == null) {
                return Futures.immediateFailedFuture(new IllegalArgumentException("ImageCapture has CaptureBundle with null capture stages"));
            }
            if (this.mCaptureProcessor == null && captureStages.size() > 1) {
                return Futures.immediateFailedFuture(new IllegalArgumentException("No CaptureProcessor can be found to process the images captured for multiple CaptureStages."));
            }
            if (captureStages.size() > this.mMaxCaptureStages) {
                return Futures.immediateFailedFuture(new IllegalArgumentException("ImageCapture has CaptureStages > Max CaptureStage size"));
            }
            this.mProcessingImageReader.setCaptureBundle(captureBundle);
            this.mProcessingImageReader.setOnProcessingErrorCallback(CameraXExecutors.directExecutor(), new ImageCapture$$ExternalSyntheticLambda8(imageCaptureRequest));
            tagBundleKey = this.mProcessingImageReader.getTagBundleKey();
        } else {
            captureBundle = getCaptureBundle(CaptureBundles.singleDefaultCaptureBundle());
            if (captureBundle == null) {
                return Futures.immediateFailedFuture(new IllegalArgumentException("ImageCapture cannot set empty CaptureBundle."));
            }
            List<CaptureStage> captureStages2 = captureBundle.getCaptureStages();
            if (captureStages2 == null) {
                return Futures.immediateFailedFuture(new IllegalArgumentException("ImageCapture has CaptureBundle with null capture stages"));
            }
            if (captureStages2.size() > 1) {
                return Futures.immediateFailedFuture(new IllegalArgumentException("ImageCapture have no CaptureProcess set with CaptureBundle size > 1."));
            }
        }
        for (CaptureStage captureStage : captureBundle.getCaptureStages()) {
            CaptureConfig.Builder builder = new CaptureConfig.Builder();
            builder.setTemplateType(this.mCaptureConfig.getTemplateType());
            builder.addImplementationOptions(this.mCaptureConfig.getImplementationOptions());
            builder.addAllCameraCaptureCallbacks(this.mSessionConfigBuilder.getSingleCameraCaptureCallbacks());
            builder.addSurface(this.mDeferrableSurface);
            if (getImageFormat() == 256) {
                if (EXIF_ROTATION_AVAILABILITY.isRotationOptionSupported()) {
                    builder.addImplementationOption(CaptureConfig.OPTION_ROTATION, Integer.valueOf(imageCaptureRequest.mRotationDegrees));
                }
                builder.addImplementationOption(CaptureConfig.OPTION_JPEG_QUALITY, Integer.valueOf(imageCaptureRequest.mJpegQuality));
            }
            builder.addImplementationOptions(captureStage.getCaptureConfig().getImplementationOptions());
            if (tagBundleKey != null) {
                builder.addTag(tagBundleKey, Integer.valueOf(captureStage.getId()));
            }
            builder.addCameraCaptureCallback(this.mMetadataMatchingCaptureCallback);
            captureConfigs.add(builder.build());
        }
        return Futures.transform(getCameraControl().submitStillCaptureRequests(captureConfigs, this.mCaptureMode, this.mFlashType), ImageCapture$$ExternalSyntheticLambda0.INSTANCE, CameraXExecutors.directExecutor());
    }

    static /* synthetic */ void lambda$issueTakePicture$10(ImageCaptureRequest imageCaptureRequest, String message, Throwable cause) {
        Logger.e(TAG, "Processing image failed! " + message);
        imageCaptureRequest.notifyCallbackError(2, message, cause);
    }

    static /* synthetic */ Void lambda$issueTakePicture$11(List input) {
        return null;
    }

    private CaptureBundle getCaptureBundle(CaptureBundle defaultCaptureBundle) {
        List<CaptureStage> captureStages = this.mCaptureBundle.getCaptureStages();
        if (captureStages == null || captureStages.isEmpty()) {
            return defaultCaptureBundle;
        }
        return CaptureBundles.createCaptureBundle(captureStages);
    }

    public static abstract class OnImageCapturedCallback {
        public void onCaptureSuccess(ImageProxy image) {
        }

        public void onError(ImageCaptureException exception) {
        }
    }

    public static final class Defaults implements ConfigProvider<ImageCaptureConfig> {
        private static final int DEFAULT_ASPECT_RATIO = 0;
        private static final ImageCaptureConfig DEFAULT_CONFIG = new Builder().setSurfaceOccupancyPriority(4).setTargetAspectRatio(0).getUseCaseConfig();
        private static final int DEFAULT_SURFACE_OCCUPANCY_PRIORITY = 4;

        public ImageCaptureConfig getConfig() {
            return DEFAULT_CONFIG;
        }
    }

    public static final class OutputFileOptions {
        private final ContentResolver mContentResolver;
        private final ContentValues mContentValues;
        private final File mFile;
        private final Metadata mMetadata;
        private final OutputStream mOutputStream;
        private final Uri mSaveCollection;

        OutputFileOptions(File file, ContentResolver contentResolver, Uri saveCollection, ContentValues contentValues, OutputStream outputStream, Metadata metadata) {
            this.mFile = file;
            this.mContentResolver = contentResolver;
            this.mSaveCollection = saveCollection;
            this.mContentValues = contentValues;
            this.mOutputStream = outputStream;
            this.mMetadata = metadata == null ? new Metadata() : metadata;
        }

        /* access modifiers changed from: package-private */
        public File getFile() {
            return this.mFile;
        }

        /* access modifiers changed from: package-private */
        public ContentResolver getContentResolver() {
            return this.mContentResolver;
        }

        /* access modifiers changed from: package-private */
        public Uri getSaveCollection() {
            return this.mSaveCollection;
        }

        /* access modifiers changed from: package-private */
        public ContentValues getContentValues() {
            return this.mContentValues;
        }

        /* access modifiers changed from: package-private */
        public OutputStream getOutputStream() {
            return this.mOutputStream;
        }

        public Metadata getMetadata() {
            return this.mMetadata;
        }

        public static final class Builder {
            private ContentResolver mContentResolver;
            private ContentValues mContentValues;
            private File mFile;
            private Metadata mMetadata;
            private OutputStream mOutputStream;
            private Uri mSaveCollection;

            public Builder(File file) {
                this.mFile = file;
            }

            public Builder(ContentResolver contentResolver, Uri saveCollection, ContentValues contentValues) {
                this.mContentResolver = contentResolver;
                this.mSaveCollection = saveCollection;
                this.mContentValues = contentValues;
            }

            public Builder(OutputStream outputStream) {
                this.mOutputStream = outputStream;
            }

            public Builder setMetadata(Metadata metadata) {
                this.mMetadata = metadata;
                return this;
            }

            public OutputFileOptions build() {
                return new OutputFileOptions(this.mFile, this.mContentResolver, this.mSaveCollection, this.mContentValues, this.mOutputStream, this.mMetadata);
            }
        }
    }

    public static class OutputFileResults {
        private final Uri mSavedUri;

        OutputFileResults(Uri savedUri) {
            this.mSavedUri = savedUri;
        }

        public Uri getSavedUri() {
            return this.mSavedUri;
        }
    }

    public static final class Metadata {
        private boolean mIsReversedHorizontal;
        private boolean mIsReversedHorizontalSet = false;
        private boolean mIsReversedVertical;
        private Location mLocation;

        public boolean isReversedHorizontal() {
            return this.mIsReversedHorizontal;
        }

        public boolean isReversedHorizontalSet() {
            return this.mIsReversedHorizontalSet;
        }

        public void setReversedHorizontal(boolean isReversedHorizontal) {
            this.mIsReversedHorizontal = isReversedHorizontal;
            this.mIsReversedHorizontalSet = true;
        }

        public boolean isReversedVertical() {
            return this.mIsReversedVertical;
        }

        public void setReversedVertical(boolean isReversedVertical) {
            this.mIsReversedVertical = isReversedVertical;
        }

        public Location getLocation() {
            return this.mLocation;
        }

        public void setLocation(Location location) {
            this.mLocation = location;
        }
    }

    static class ImageCaptureRequest {
        private final OnImageCapturedCallback mCallback;
        AtomicBoolean mDispatched = new AtomicBoolean(false);
        final int mJpegQuality;
        private final Executor mListenerExecutor;
        final int mRotationDegrees;
        private final Matrix mSensorToBufferTransformMatrix;
        private final Rational mTargetRatio;
        private final Rect mViewPortCropRect;

        ImageCaptureRequest(int rotationDegrees, int jpegQuality, Rational targetRatio, Rect viewPortCropRect, Matrix sensorToBufferTransformMatrix, Executor executor, OnImageCapturedCallback callback) {
            boolean z = false;
            this.mRotationDegrees = rotationDegrees;
            this.mJpegQuality = jpegQuality;
            if (targetRatio != null) {
                Preconditions.checkArgument(!targetRatio.isZero(), "Target ratio cannot be zero");
                Preconditions.checkArgument(targetRatio.floatValue() > 0.0f ? true : z, "Target ratio must be positive");
            }
            this.mTargetRatio = targetRatio;
            this.mViewPortCropRect = viewPortCropRect;
            this.mSensorToBufferTransformMatrix = sensorToBufferTransformMatrix;
            this.mListenerExecutor = executor;
            this.mCallback = callback;
        }

        /* access modifiers changed from: package-private */
        public void dispatchImage(ImageProxy image) {
            Size dispatchResolution;
            int dispatchRotationDegrees;
            if (!this.mDispatched.compareAndSet(false, true)) {
                image.close();
                return;
            }
            if (ImageCapture.EXIF_ROTATION_AVAILABILITY.shouldUseExifOrientation(image)) {
                try {
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    buffer.rewind();
                    byte[] data = new byte[buffer.capacity()];
                    buffer.get(data);
                    Exif exif = Exif.createFromInputStream(new ByteArrayInputStream(data));
                    buffer.rewind();
                    dispatchResolution = new Size(exif.getWidth(), exif.getHeight());
                    dispatchRotationDegrees = exif.getRotation();
                } catch (IOException e) {
                    notifyCallbackError(1, "Unable to parse JPEG exif", e);
                    image.close();
                    return;
                }
            } else {
                dispatchResolution = new Size(image.getWidth(), image.getHeight());
                dispatchRotationDegrees = this.mRotationDegrees;
            }
            ImageProxy dispatchedImageProxy = new SettableImageProxy(image, dispatchResolution, ImmutableImageInfo.create(image.getImageInfo().getTagBundle(), image.getImageInfo().getTimestamp(), dispatchRotationDegrees, this.mSensorToBufferTransformMatrix));
            dispatchedImageProxy.setCropRect(ImageCapture.computeDispatchCropRect(this.mViewPortCropRect, this.mTargetRatio, this.mRotationDegrees, dispatchResolution, dispatchRotationDegrees));
            try {
                this.mListenerExecutor.execute(new ImageCapture$ImageCaptureRequest$$ExternalSyntheticLambda1(this, dispatchedImageProxy));
            } catch (RejectedExecutionException e2) {
                Logger.e(ImageCapture.TAG, "Unable to post to the supplied executor.");
                image.close();
            }
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$dispatchImage$0$androidx-camera-core-ImageCapture$ImageCaptureRequest  reason: not valid java name */
        public /* synthetic */ void m146lambda$dispatchImage$0$androidxcameracoreImageCapture$ImageCaptureRequest(ImageProxy dispatchedImageProxy) {
            this.mCallback.onCaptureSuccess(dispatchedImageProxy);
        }

        /* access modifiers changed from: package-private */
        public void notifyCallbackError(int imageCaptureError, String message, Throwable cause) {
            if (this.mDispatched.compareAndSet(false, true)) {
                try {
                    this.mListenerExecutor.execute(new ImageCapture$ImageCaptureRequest$$ExternalSyntheticLambda0(this, imageCaptureError, message, cause));
                } catch (RejectedExecutionException e) {
                    Logger.e(ImageCapture.TAG, "Unable to post to the supplied executor.");
                }
            }
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$notifyCallbackError$1$androidx-camera-core-ImageCapture$ImageCaptureRequest  reason: not valid java name */
        public /* synthetic */ void m147lambda$notifyCallbackError$1$androidxcameracoreImageCapture$ImageCaptureRequest(int imageCaptureError, String message, Throwable cause) {
            this.mCallback.onError(new ImageCaptureException(imageCaptureError, message, cause));
        }
    }

    public static final class Builder implements UseCaseConfig.Builder<ImageCapture, ImageCaptureConfig, Builder>, ImageOutputConfig.Builder<Builder>, IoConfig.Builder<Builder> {
        private final MutableOptionsBundle mMutableConfig;

        public Builder() {
            this(MutableOptionsBundle.create());
        }

        private Builder(MutableOptionsBundle mutableConfig) {
            this.mMutableConfig = mutableConfig;
            Class<?> oldConfigClass = (Class) mutableConfig.retrieveOption(TargetConfig.OPTION_TARGET_CLASS, null);
            if (oldConfigClass == null || oldConfigClass.equals(ImageCapture.class)) {
                setTargetClass((Class<ImageCapture>) ImageCapture.class);
                return;
            }
            throw new IllegalArgumentException("Invalid target class configuration for " + this + ": " + oldConfigClass);
        }

        public static Builder fromConfig(Config configuration) {
            return new Builder(MutableOptionsBundle.from(configuration));
        }

        static Builder fromConfig(ImageCaptureConfig configuration) {
            return new Builder(MutableOptionsBundle.from(configuration));
        }

        public MutableConfig getMutableConfig() {
            return this.mMutableConfig;
        }

        public ImageCaptureConfig getUseCaseConfig() {
            return new ImageCaptureConfig(OptionsBundle.from(this.mMutableConfig));
        }

        public ImageCapture build() {
            Integer flashMode;
            if (getMutableConfig().retrieveOption(ImageCaptureConfig.OPTION_TARGET_ASPECT_RATIO, null) == null || getMutableConfig().retrieveOption(ImageCaptureConfig.OPTION_TARGET_RESOLUTION, null) == null) {
                Integer bufferFormat = (Integer) getMutableConfig().retrieveOption(ImageCaptureConfig.OPTION_BUFFER_FORMAT, null);
                boolean z = false;
                if (bufferFormat != null) {
                    Preconditions.checkArgument(getMutableConfig().retrieveOption(ImageCaptureConfig.OPTION_CAPTURE_PROCESSOR, null) == null, "Cannot set buffer format with CaptureProcessor defined.");
                    getMutableConfig().insertOption(ImageInputConfig.OPTION_INPUT_FORMAT, bufferFormat);
                } else if (getMutableConfig().retrieveOption(ImageCaptureConfig.OPTION_CAPTURE_PROCESSOR, null) != null) {
                    getMutableConfig().insertOption(ImageInputConfig.OPTION_INPUT_FORMAT, 35);
                } else {
                    getMutableConfig().insertOption(ImageInputConfig.OPTION_INPUT_FORMAT, 256);
                }
                ImageCapture imageCapture = new ImageCapture(getUseCaseConfig());
                Size targetResolution = (Size) getMutableConfig().retrieveOption(ImageCaptureConfig.OPTION_TARGET_RESOLUTION, null);
                if (targetResolution != null) {
                    imageCapture.setCropAspectRatio(new Rational(targetResolution.getWidth(), targetResolution.getHeight()));
                }
                Integer maxCaptureStages = (Integer) getMutableConfig().retrieveOption(ImageCaptureConfig.OPTION_MAX_CAPTURE_STAGES, 2);
                Preconditions.checkNotNull(maxCaptureStages, "Maximum outstanding image count must be at least 1");
                if (maxCaptureStages.intValue() >= 1) {
                    z = true;
                }
                Preconditions.checkArgument(z, "Maximum outstanding image count must be at least 1");
                Preconditions.checkNotNull((Executor) getMutableConfig().retrieveOption(ImageCaptureConfig.OPTION_IO_EXECUTOR, CameraXExecutors.ioExecutor()), "The IO executor can't be null");
                if (!getMutableConfig().containsOption(ImageCaptureConfig.OPTION_FLASH_MODE) || ((flashMode = (Integer) getMutableConfig().retrieveOption(ImageCaptureConfig.OPTION_FLASH_MODE)) != null && (flashMode.intValue() == 0 || flashMode.intValue() == 1 || flashMode.intValue() == 2))) {
                    return imageCapture;
                }
                throw new IllegalArgumentException("The flash mode is not allowed to set: " + flashMode);
            }
            throw new IllegalArgumentException("Cannot use both setTargetResolution and setTargetAspectRatio on the same config.");
        }

        public Builder setCaptureMode(int captureMode) {
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_IMAGE_CAPTURE_MODE, Integer.valueOf(captureMode));
            return this;
        }

        public Builder setFlashMode(int flashMode) {
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_FLASH_MODE, Integer.valueOf(flashMode));
            return this;
        }

        public Builder setCaptureBundle(CaptureBundle captureBundle) {
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_CAPTURE_BUNDLE, captureBundle);
            return this;
        }

        public Builder setCaptureProcessor(CaptureProcessor captureProcessor) {
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_CAPTURE_PROCESSOR, captureProcessor);
            return this;
        }

        public Builder setBufferFormat(int bufferImageFormat) {
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_BUFFER_FORMAT, Integer.valueOf(bufferImageFormat));
            return this;
        }

        public Builder setMaxCaptureStages(int maxCaptureStages) {
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_MAX_CAPTURE_STAGES, Integer.valueOf(maxCaptureStages));
            return this;
        }

        public Builder setSupportedResolutions(List<Pair<Integer, Size[]>> resolutions) {
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_SUPPORTED_RESOLUTIONS, resolutions);
            return this;
        }

        public Builder setTargetClass(Class<ImageCapture> targetClass) {
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_TARGET_CLASS, targetClass);
            if (getMutableConfig().retrieveOption(ImageCaptureConfig.OPTION_TARGET_NAME, null) == null) {
                setTargetName(targetClass.getCanonicalName() + "-" + UUID.randomUUID());
            }
            return this;
        }

        public Builder setTargetName(String targetName) {
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_TARGET_NAME, targetName);
            return this;
        }

        public Builder setTargetAspectRatio(int aspectRatio) {
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_TARGET_ASPECT_RATIO, Integer.valueOf(aspectRatio));
            return this;
        }

        public Builder setTargetRotation(int rotation) {
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_TARGET_ROTATION, Integer.valueOf(rotation));
            return this;
        }

        public Builder setTargetResolution(Size resolution) {
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_TARGET_RESOLUTION, resolution);
            return this;
        }

        public Builder setDefaultResolution(Size resolution) {
            getMutableConfig().insertOption(ImageOutputConfig.OPTION_DEFAULT_RESOLUTION, resolution);
            return this;
        }

        public Builder setMaxResolution(Size resolution) {
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_MAX_RESOLUTION, resolution);
            return this;
        }

        public Builder setImageReaderProxyProvider(ImageReaderProxyProvider imageReaderProxyProvider) {
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_IMAGE_READER_PROXY_PROVIDER, imageReaderProxyProvider);
            return this;
        }

        public Builder setSoftwareJpegEncoderRequested(boolean requestSoftwareJpeg) {
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_USE_SOFTWARE_JPEG_ENCODER, Boolean.valueOf(requestSoftwareJpeg));
            return this;
        }

        public Builder setFlashType(int flashType) {
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_FLASH_TYPE, Integer.valueOf(flashType));
            return this;
        }

        public Builder setJpegQuality(int jpegQuality) {
            Preconditions.checkArgumentInRange(jpegQuality, 1, 100, "jpegQuality");
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_JPEG_COMPRESSION_QUALITY, Integer.valueOf(jpegQuality));
            return this;
        }

        public Builder setIoExecutor(Executor executor) {
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_IO_EXECUTOR, executor);
            return this;
        }

        public Builder setDefaultSessionConfig(SessionConfig sessionConfig) {
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_DEFAULT_SESSION_CONFIG, sessionConfig);
            return this;
        }

        public Builder setDefaultCaptureConfig(CaptureConfig captureConfig) {
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_DEFAULT_CAPTURE_CONFIG, captureConfig);
            return this;
        }

        public Builder setSessionOptionUnpacker(SessionConfig.OptionUnpacker optionUnpacker) {
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_SESSION_CONFIG_UNPACKER, optionUnpacker);
            return this;
        }

        public Builder setCaptureOptionUnpacker(CaptureConfig.OptionUnpacker optionUnpacker) {
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_CAPTURE_CONFIG_UNPACKER, optionUnpacker);
            return this;
        }

        public Builder setCameraSelector(CameraSelector cameraSelector) {
            getMutableConfig().insertOption(UseCaseConfig.OPTION_CAMERA_SELECTOR, cameraSelector);
            return this;
        }

        public Builder setSurfaceOccupancyPriority(int priority) {
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_SURFACE_OCCUPANCY_PRIORITY, Integer.valueOf(priority));
            return this;
        }

        public Builder setUseCaseEventCallback(UseCase.EventCallback useCaseEventCallback) {
            getMutableConfig().insertOption(ImageCaptureConfig.OPTION_USE_CASE_EVENT_CALLBACK, useCaseEventCallback);
            return this;
        }

        public Builder setZslDisabled(boolean disabled) {
            getMutableConfig().insertOption(UseCaseConfig.OPTION_ZSL_DISABLED, Boolean.valueOf(disabled));
            return this;
        }
    }
}

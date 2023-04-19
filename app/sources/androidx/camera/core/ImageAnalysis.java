package androidx.camera.core;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Pair;
import android.util.Size;
import androidx.camera.core.UseCase;
import androidx.camera.core.impl.CameraInternal;
import androidx.camera.core.impl.CaptureConfig;
import androidx.camera.core.impl.Config;
import androidx.camera.core.impl.ConfigProvider;
import androidx.camera.core.impl.DeferrableSurface;
import androidx.camera.core.impl.ImageAnalysisConfig;
import androidx.camera.core.impl.ImageOutputConfig;
import androidx.camera.core.impl.ImmediateSurface;
import androidx.camera.core.impl.MutableConfig;
import androidx.camera.core.impl.MutableOptionsBundle;
import androidx.camera.core.impl.OptionsBundle;
import androidx.camera.core.impl.SessionConfig;
import androidx.camera.core.impl.UseCaseConfig;
import androidx.camera.core.impl.UseCaseConfigFactory;
import androidx.camera.core.impl.utils.Threads;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.internal.TargetConfig;
import androidx.camera.core.internal.ThreadConfig;
import androidx.core.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

public final class ImageAnalysis extends UseCase {
    public static final int COORDINATE_SYSTEM_ORIGINAL = 0;
    private static final int DEFAULT_BACKPRESSURE_STRATEGY = 0;
    public static final Defaults DEFAULT_CONFIG = new Defaults();
    private static final int DEFAULT_IMAGE_QUEUE_DEPTH = 6;
    private static final Boolean DEFAULT_ONE_PIXEL_SHIFT_ENABLED = null;
    private static final int DEFAULT_OUTPUT_IMAGE_FORMAT = 1;
    private static final boolean DEFAULT_OUTPUT_IMAGE_ROTATION_ENABLED = false;
    private static final int NON_BLOCKING_IMAGE_DEPTH = 4;
    public static final int OUTPUT_IMAGE_FORMAT_RGBA_8888 = 2;
    public static final int OUTPUT_IMAGE_FORMAT_YUV_420_888 = 1;
    public static final int STRATEGY_BLOCK_PRODUCER = 1;
    public static final int STRATEGY_KEEP_ONLY_LATEST = 0;
    private static final String TAG = "ImageAnalysis";
    private final Object mAnalysisLock = new Object();
    private DeferrableSurface mDeferrableSurface;
    final ImageAnalysisAbstractAnalyzer mImageAnalysisAbstractAnalyzer;
    private Analyzer mSubscribedAnalyzer;

    @Retention(RetentionPolicy.SOURCE)
    public @interface BackpressureStrategy {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface OutputImageFormat {
    }

    ImageAnalysis(ImageAnalysisConfig config) {
        super(config);
        if (((ImageAnalysisConfig) getCurrentConfig()).getBackpressureStrategy(0) == 1) {
            this.mImageAnalysisAbstractAnalyzer = new ImageAnalysisBlockingAnalyzer();
        } else {
            this.mImageAnalysisAbstractAnalyzer = new ImageAnalysisNonBlockingAnalyzer(config.getBackgroundExecutor(CameraXExecutors.highPriorityExecutor()));
        }
        this.mImageAnalysisAbstractAnalyzer.setOutputImageFormat(getOutputImageFormat());
        this.mImageAnalysisAbstractAnalyzer.setOutputImageRotationEnabled(isOutputImageRotationEnabled());
    }

    /* JADX WARNING: type inference failed for: r7v0, types: [androidx.camera.core.impl.UseCaseConfig$Builder, androidx.camera.core.impl.UseCaseConfig$Builder<?, ?, ?>] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public androidx.camera.core.impl.UseCaseConfig<?> onMergeConfig(androidx.camera.core.impl.CameraInfoInternal r6, androidx.camera.core.impl.UseCaseConfig.Builder<?, ?, ?> r7) {
        /*
            r5 = this;
            java.lang.Boolean r0 = r5.getOnePixelShiftEnabled()
            androidx.camera.core.impl.Quirks r1 = r6.getCameraQuirks()
            java.lang.Class<androidx.camera.core.internal.compat.quirk.OnePixelShiftQuirk> r2 = androidx.camera.core.internal.compat.quirk.OnePixelShiftQuirk.class
            boolean r1 = r1.contains(r2)
            if (r1 == 0) goto L_0x0012
            r1 = 1
            goto L_0x0013
        L_0x0012:
            r1 = 0
        L_0x0013:
            androidx.camera.core.ImageAnalysisAbstractAnalyzer r2 = r5.mImageAnalysisAbstractAnalyzer
            if (r0 != 0) goto L_0x0019
            r3 = r1
            goto L_0x001d
        L_0x0019:
            boolean r3 = r0.booleanValue()
        L_0x001d:
            r2.setOnePixelShiftEnabled(r3)
            java.lang.Object r2 = r5.mAnalysisLock
            monitor-enter(r2)
            androidx.camera.core.ImageAnalysis$Analyzer r3 = r5.mSubscribedAnalyzer     // Catch:{ all -> 0x004a }
            if (r3 == 0) goto L_0x002c
            android.util.Size r3 = r3.getDefaultTargetResolution()     // Catch:{ all -> 0x004a }
            goto L_0x002d
        L_0x002c:
            r3 = 0
        L_0x002d:
            monitor-exit(r2)     // Catch:{ all -> 0x004a }
            if (r3 == 0) goto L_0x0045
            androidx.camera.core.impl.UseCaseConfig r2 = r7.getUseCaseConfig()
            androidx.camera.core.impl.Config$Option<android.util.Size> r4 = androidx.camera.core.impl.ImageOutputConfig.OPTION_TARGET_RESOLUTION
            boolean r2 = r2.containsOption(r4)
            if (r2 != 0) goto L_0x0045
            androidx.camera.core.impl.MutableConfig r2 = r7.getMutableConfig()
            androidx.camera.core.impl.Config$Option<android.util.Size> r4 = androidx.camera.core.impl.ImageOutputConfig.OPTION_TARGET_RESOLUTION
            r2.insertOption(r4, r3)
        L_0x0045:
            androidx.camera.core.impl.UseCaseConfig r2 = r7.getUseCaseConfig()
            return r2
        L_0x004a:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x004a }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.camera.core.ImageAnalysis.onMergeConfig(androidx.camera.core.impl.CameraInfoInternal, androidx.camera.core.impl.UseCaseConfig$Builder):androidx.camera.core.impl.UseCaseConfig");
    }

    /* access modifiers changed from: package-private */
    public SessionConfig.Builder createPipeline(String cameraId, ImageAnalysisConfig config, Size resolution) {
        int imageQueueDepth;
        SafeCloseImageReaderProxy imageReaderProxy;
        SafeCloseImageReaderProxy processedImageReaderProxy;
        ImageAnalysisConfig imageAnalysisConfig = config;
        Size size = resolution;
        Threads.checkMainThread();
        Executor backgroundExecutor = (Executor) Preconditions.checkNotNull(imageAnalysisConfig.getBackgroundExecutor(CameraXExecutors.highPriorityExecutor()));
        boolean isYuvRotationOrPixelShift = true;
        if (getBackpressureStrategy() == 1) {
            imageQueueDepth = getImageQueueDepth();
        } else {
            imageQueueDepth = 4;
        }
        if (config.getImageReaderProxyProvider() != null) {
            imageReaderProxy = new SafeCloseImageReaderProxy(config.getImageReaderProxyProvider().newInstance(resolution.getWidth(), resolution.getHeight(), getImageFormat(), imageQueueDepth, 0));
        } else {
            imageReaderProxy = new SafeCloseImageReaderProxy(ImageReaderProxys.createIsolatedReader(resolution.getWidth(), resolution.getHeight(), getImageFormat(), imageQueueDepth));
        }
        boolean flipWH = getCamera() != null ? isFlipWH(getCamera()) : false;
        int width = flipWH ? resolution.getHeight() : resolution.getWidth();
        int height = flipWH ? resolution.getWidth() : resolution.getHeight();
        int format = getOutputImageFormat() == 2 ? 1 : 35;
        boolean isYuv2Rgb = getImageFormat() == 35 && getOutputImageFormat() == 2;
        if (getImageFormat() != 35 || ((getCamera() == null || getRelativeRotation(getCamera()) == 0) && !Boolean.TRUE.equals(getOnePixelShiftEnabled()))) {
            isYuvRotationOrPixelShift = false;
        }
        if (isYuv2Rgb || isYuvRotationOrPixelShift) {
            processedImageReaderProxy = new SafeCloseImageReaderProxy(ImageReaderProxys.createIsolatedReader(width, height, format, imageReaderProxy.getMaxImages()));
        } else {
            processedImageReaderProxy = null;
        }
        if (processedImageReaderProxy != null) {
            this.mImageAnalysisAbstractAnalyzer.setProcessedImageReaderProxy(processedImageReaderProxy);
        }
        tryUpdateRelativeRotation();
        imageReaderProxy.setOnImageAvailableListener(this.mImageAnalysisAbstractAnalyzer, backgroundExecutor);
        SessionConfig.Builder sessionConfigBuilder = SessionConfig.Builder.createFrom(config);
        DeferrableSurface deferrableSurface = this.mDeferrableSurface;
        if (deferrableSurface != null) {
            deferrableSurface.close();
        }
        Executor executor = backgroundExecutor;
        ImmediateSurface immediateSurface = new ImmediateSurface(imageReaderProxy.getSurface(), size, getImageFormat());
        this.mDeferrableSurface = immediateSurface;
        immediateSurface.getTerminationFuture().addListener(new ImageAnalysis$$ExternalSyntheticLambda2(imageReaderProxy, processedImageReaderProxy), CameraXExecutors.mainThreadExecutor());
        sessionConfigBuilder.addSurface(this.mDeferrableSurface);
        sessionConfigBuilder.addErrorListener(new ImageAnalysis$$ExternalSyntheticLambda1(this, cameraId, imageAnalysisConfig, size));
        return sessionConfigBuilder;
    }

    static /* synthetic */ void lambda$createPipeline$0(SafeCloseImageReaderProxy imageReaderProxy, SafeCloseImageReaderProxy processedImageReaderProxy) {
        imageReaderProxy.safeClose();
        if (processedImageReaderProxy != null) {
            processedImageReaderProxy.safeClose();
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$createPipeline$1$androidx-camera-core-ImageAnalysis  reason: not valid java name */
    public /* synthetic */ void m137lambda$createPipeline$1$androidxcameracoreImageAnalysis(String cameraId, ImageAnalysisConfig config, Size resolution, SessionConfig sessionConfig, SessionConfig.SessionError error) {
        clearPipeline();
        this.mImageAnalysisAbstractAnalyzer.clearCache();
        if (isCurrentCamera(cameraId)) {
            updateSessionConfig(createPipeline(cameraId, config, resolution).build());
            notifyReset();
        }
    }

    /* access modifiers changed from: package-private */
    public void clearPipeline() {
        Threads.checkMainThread();
        DeferrableSurface deferrableSurface = this.mDeferrableSurface;
        if (deferrableSurface != null) {
            deferrableSurface.close();
            this.mDeferrableSurface = null;
        }
    }

    public void clearAnalyzer() {
        synchronized (this.mAnalysisLock) {
            this.mImageAnalysisAbstractAnalyzer.setAnalyzer((Executor) null, (Analyzer) null);
            if (this.mSubscribedAnalyzer != null) {
                notifyInactive();
            }
            this.mSubscribedAnalyzer = null;
        }
    }

    public int getTargetRotation() {
        return getTargetRotationInternal();
    }

    public void setTargetRotation(int rotation) {
        if (setTargetRotationInternal(rotation)) {
            tryUpdateRelativeRotation();
        }
    }

    public void setAnalyzer(Executor executor, Analyzer analyzer) {
        synchronized (this.mAnalysisLock) {
            this.mImageAnalysisAbstractAnalyzer.setAnalyzer(executor, new ImageAnalysis$$ExternalSyntheticLambda0(analyzer));
            if (this.mSubscribedAnalyzer == null) {
                notifyActive();
            }
            this.mSubscribedAnalyzer = analyzer;
        }
    }

    public void setViewPortCropRect(Rect viewPortCropRect) {
        super.setViewPortCropRect(viewPortCropRect);
        this.mImageAnalysisAbstractAnalyzer.setViewPortCropRect(viewPortCropRect);
    }

    public void setSensorToBufferTransformMatrix(Matrix matrix) {
        this.mImageAnalysisAbstractAnalyzer.setSensorToBufferTransformMatrix(matrix);
    }

    private boolean isFlipWH(CameraInternal cameraInternal) {
        if (!isOutputImageRotationEnabled() || getRelativeRotation(cameraInternal) % 180 == 0) {
            return false;
        }
        return true;
    }

    public int getBackpressureStrategy() {
        return ((ImageAnalysisConfig) getCurrentConfig()).getBackpressureStrategy(0);
    }

    public Executor getBackgroundExecutor() {
        return ((ImageAnalysisConfig) getCurrentConfig()).getBackgroundExecutor((Executor) null);
    }

    public int getImageQueueDepth() {
        return ((ImageAnalysisConfig) getCurrentConfig()).getImageQueueDepth(6);
    }

    public int getOutputImageFormat() {
        return ((ImageAnalysisConfig) getCurrentConfig()).getOutputImageFormat(1);
    }

    public boolean isOutputImageRotationEnabled() {
        return ((ImageAnalysisConfig) getCurrentConfig()).isOutputImageRotationEnabled(false).booleanValue();
    }

    public Boolean getOnePixelShiftEnabled() {
        return ((ImageAnalysisConfig) getCurrentConfig()).getOnePixelShiftEnabled(DEFAULT_ONE_PIXEL_SHIFT_ENABLED);
    }

    public ResolutionInfo getResolutionInfo() {
        return super.getResolutionInfo();
    }

    public String toString() {
        return "ImageAnalysis:" + getName();
    }

    public void onDetached() {
        clearPipeline();
        this.mImageAnalysisAbstractAnalyzer.detach();
    }

    public UseCaseConfig<?> getDefaultConfig(boolean applyDefaultConfig, UseCaseConfigFactory factory) {
        Config captureConfig = factory.getConfig(UseCaseConfigFactory.CaptureType.IMAGE_ANALYSIS, 1);
        if (applyDefaultConfig) {
            captureConfig = Config.mergeConfigs(captureConfig, DEFAULT_CONFIG.getConfig());
        }
        if (captureConfig == null) {
            return null;
        }
        return getUseCaseConfigBuilder(captureConfig).getUseCaseConfig();
    }

    public void onAttached() {
        this.mImageAnalysisAbstractAnalyzer.attach();
    }

    public UseCaseConfig.Builder<?, ?, ?> getUseCaseConfigBuilder(Config config) {
        return Builder.fromConfig(config);
    }

    /* access modifiers changed from: protected */
    public Size onSuggestedResolutionUpdated(Size suggestedResolution) {
        updateSessionConfig(createPipeline(getCameraId(), (ImageAnalysisConfig) getCurrentConfig(), suggestedResolution).build());
        return suggestedResolution;
    }

    private void tryUpdateRelativeRotation() {
        CameraInternal cameraInternal = getCamera();
        if (cameraInternal != null) {
            this.mImageAnalysisAbstractAnalyzer.setRelativeRotation(getRelativeRotation(cameraInternal));
        }
    }

    public interface Analyzer {
        void analyze(ImageProxy imageProxy);

        Size getDefaultTargetResolution() {
            return null;
        }

        int getTargetCoordinateSystem() {
            return 0;
        }

        void updateTransform(Matrix matrix) {
        }
    }

    public static final class Defaults implements ConfigProvider<ImageAnalysisConfig> {
        private static final int DEFAULT_ASPECT_RATIO = 0;
        private static final ImageAnalysisConfig DEFAULT_CONFIG;
        private static final int DEFAULT_SURFACE_OCCUPANCY_PRIORITY = 1;
        private static final Size DEFAULT_TARGET_RESOLUTION;

        static {
            Size size = new Size(640, 480);
            DEFAULT_TARGET_RESOLUTION = size;
            DEFAULT_CONFIG = new Builder().setDefaultResolution(size).setSurfaceOccupancyPriority(1).setTargetAspectRatio(0).getUseCaseConfig();
        }

        public ImageAnalysisConfig getConfig() {
            return DEFAULT_CONFIG;
        }
    }

    public static final class Builder implements ImageOutputConfig.Builder<Builder>, ThreadConfig.Builder<Builder>, UseCaseConfig.Builder<ImageAnalysis, ImageAnalysisConfig, Builder> {
        private final MutableOptionsBundle mMutableConfig;

        public Builder() {
            this(MutableOptionsBundle.create());
        }

        private Builder(MutableOptionsBundle mutableConfig) {
            this.mMutableConfig = mutableConfig;
            Class<?> oldConfigClass = (Class) mutableConfig.retrieveOption(TargetConfig.OPTION_TARGET_CLASS, null);
            if (oldConfigClass == null || oldConfigClass.equals(ImageAnalysis.class)) {
                setTargetClass((Class<ImageAnalysis>) ImageAnalysis.class);
                return;
            }
            throw new IllegalArgumentException("Invalid target class configuration for " + this + ": " + oldConfigClass);
        }

        static Builder fromConfig(Config configuration) {
            return new Builder(MutableOptionsBundle.from(configuration));
        }

        public static Builder fromConfig(ImageAnalysisConfig configuration) {
            return new Builder(MutableOptionsBundle.from(configuration));
        }

        public Builder setBackpressureStrategy(int strategy) {
            getMutableConfig().insertOption(ImageAnalysisConfig.OPTION_BACKPRESSURE_STRATEGY, Integer.valueOf(strategy));
            return this;
        }

        public Builder setImageQueueDepth(int depth) {
            getMutableConfig().insertOption(ImageAnalysisConfig.OPTION_IMAGE_QUEUE_DEPTH, Integer.valueOf(depth));
            return this;
        }

        public Builder setOutputImageFormat(int outputImageFormat) {
            getMutableConfig().insertOption(ImageAnalysisConfig.OPTION_OUTPUT_IMAGE_FORMAT, Integer.valueOf(outputImageFormat));
            return this;
        }

        public Builder setOutputImageRotationEnabled(boolean outputImageRotationEnabled) {
            getMutableConfig().insertOption(ImageAnalysisConfig.OPTION_OUTPUT_IMAGE_ROTATION_ENABLED, Boolean.valueOf(outputImageRotationEnabled));
            return this;
        }

        public Builder setOnePixelShiftEnabled(boolean onePixelShiftEnabled) {
            getMutableConfig().insertOption(ImageAnalysisConfig.OPTION_ONE_PIXEL_SHIFT_ENABLED, Boolean.valueOf(onePixelShiftEnabled));
            return this;
        }

        public MutableConfig getMutableConfig() {
            return this.mMutableConfig;
        }

        public ImageAnalysisConfig getUseCaseConfig() {
            return new ImageAnalysisConfig(OptionsBundle.from(this.mMutableConfig));
        }

        public ImageAnalysis build() {
            if (getMutableConfig().retrieveOption(ImageOutputConfig.OPTION_TARGET_ASPECT_RATIO, null) == null || getMutableConfig().retrieveOption(ImageOutputConfig.OPTION_TARGET_RESOLUTION, null) == null) {
                return new ImageAnalysis(getUseCaseConfig());
            }
            throw new IllegalArgumentException("Cannot use both setTargetResolution and setTargetAspectRatio on the same config.");
        }

        public Builder setTargetClass(Class<ImageAnalysis> targetClass) {
            getMutableConfig().insertOption(UseCaseConfig.OPTION_TARGET_CLASS, targetClass);
            if (getMutableConfig().retrieveOption(UseCaseConfig.OPTION_TARGET_NAME, null) == null) {
                setTargetName(targetClass.getCanonicalName() + "-" + UUID.randomUUID());
            }
            return this;
        }

        public Builder setTargetName(String targetName) {
            getMutableConfig().insertOption(UseCaseConfig.OPTION_TARGET_NAME, targetName);
            return this;
        }

        public Builder setTargetAspectRatio(int aspectRatio) {
            getMutableConfig().insertOption(ImageOutputConfig.OPTION_TARGET_ASPECT_RATIO, Integer.valueOf(aspectRatio));
            return this;
        }

        public Builder setTargetRotation(int rotation) {
            getMutableConfig().insertOption(ImageOutputConfig.OPTION_TARGET_ROTATION, Integer.valueOf(rotation));
            return this;
        }

        public Builder setTargetResolution(Size resolution) {
            getMutableConfig().insertOption(ImageOutputConfig.OPTION_TARGET_RESOLUTION, resolution);
            return this;
        }

        public Builder setDefaultResolution(Size resolution) {
            getMutableConfig().insertOption(ImageOutputConfig.OPTION_DEFAULT_RESOLUTION, resolution);
            return this;
        }

        public Builder setMaxResolution(Size resolution) {
            getMutableConfig().insertOption(ImageOutputConfig.OPTION_MAX_RESOLUTION, resolution);
            return this;
        }

        public Builder setSupportedResolutions(List<Pair<Integer, Size[]>> resolutions) {
            getMutableConfig().insertOption(ImageOutputConfig.OPTION_SUPPORTED_RESOLUTIONS, resolutions);
            return this;
        }

        public Builder setBackgroundExecutor(Executor executor) {
            getMutableConfig().insertOption(ThreadConfig.OPTION_BACKGROUND_EXECUTOR, executor);
            return this;
        }

        public Builder setDefaultSessionConfig(SessionConfig sessionConfig) {
            getMutableConfig().insertOption(UseCaseConfig.OPTION_DEFAULT_SESSION_CONFIG, sessionConfig);
            return this;
        }

        public Builder setDefaultCaptureConfig(CaptureConfig captureConfig) {
            getMutableConfig().insertOption(UseCaseConfig.OPTION_DEFAULT_CAPTURE_CONFIG, captureConfig);
            return this;
        }

        public Builder setSessionOptionUnpacker(SessionConfig.OptionUnpacker optionUnpacker) {
            getMutableConfig().insertOption(UseCaseConfig.OPTION_SESSION_CONFIG_UNPACKER, optionUnpacker);
            return this;
        }

        public Builder setCaptureOptionUnpacker(CaptureConfig.OptionUnpacker optionUnpacker) {
            getMutableConfig().insertOption(UseCaseConfig.OPTION_CAPTURE_CONFIG_UNPACKER, optionUnpacker);
            return this;
        }

        public Builder setSurfaceOccupancyPriority(int priority) {
            getMutableConfig().insertOption(UseCaseConfig.OPTION_SURFACE_OCCUPANCY_PRIORITY, Integer.valueOf(priority));
            return this;
        }

        public Builder setCameraSelector(CameraSelector cameraSelector) {
            getMutableConfig().insertOption(UseCaseConfig.OPTION_CAMERA_SELECTOR, cameraSelector);
            return this;
        }

        public Builder setUseCaseEventCallback(UseCase.EventCallback useCaseEventCallback) {
            getMutableConfig().insertOption(UseCaseConfig.OPTION_USE_CASE_EVENT_CALLBACK, useCaseEventCallback);
            return this;
        }

        public Builder setImageReaderProxyProvider(ImageReaderProxyProvider imageReaderProxyProvider) {
            getMutableConfig().insertOption(ImageAnalysisConfig.OPTION_IMAGE_READER_PROXY_PROVIDER, imageReaderProxyProvider);
            return this;
        }

        public Builder setZslDisabled(boolean disabled) {
            getMutableConfig().insertOption(UseCaseConfig.OPTION_ZSL_DISABLED, Boolean.valueOf(disabled));
            return this;
        }
    }
}

package androidx.camera.core;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Pair;
import android.util.Size;
import androidx.camera.core.SurfaceRequest;
import androidx.camera.core.UseCase;
import androidx.camera.core.impl.CameraCaptureCallback;
import androidx.camera.core.impl.CameraCaptureResult;
import androidx.camera.core.impl.CameraInternal;
import androidx.camera.core.impl.CaptureConfig;
import androidx.camera.core.impl.CaptureProcessor;
import androidx.camera.core.impl.CaptureStage;
import androidx.camera.core.impl.Config;
import androidx.camera.core.impl.ConfigProvider;
import androidx.camera.core.impl.DeferrableSurface;
import androidx.camera.core.impl.ImageInfoProcessor;
import androidx.camera.core.impl.ImageOutputConfig;
import androidx.camera.core.impl.MutableConfig;
import androidx.camera.core.impl.MutableOptionsBundle;
import androidx.camera.core.impl.OptionsBundle;
import androidx.camera.core.impl.PreviewConfig;
import androidx.camera.core.impl.SessionConfig;
import androidx.camera.core.impl.UseCaseConfig;
import androidx.camera.core.impl.UseCaseConfigFactory;
import androidx.camera.core.impl.utils.Threads;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.internal.CameraCaptureResultImageInfo;
import androidx.camera.core.internal.TargetConfig;
import androidx.camera.core.internal.ThreadConfig;
import androidx.camera.core.processing.SettableSurface;
import androidx.camera.core.processing.SurfaceEdge;
import androidx.camera.core.processing.SurfaceEffectInternal;
import androidx.camera.core.processing.SurfaceEffectNode;
import androidx.core.util.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executor;

public final class Preview extends UseCase {
    public static final Defaults DEFAULT_CONFIG = new Defaults();
    private static final Executor DEFAULT_SURFACE_PROVIDER_EXECUTOR = CameraXExecutors.mainThreadExecutor();
    private static final String TAG = "Preview";
    SurfaceRequest mCurrentSurfaceRequest;
    private boolean mHasUnsentSurfaceRequest = false;
    private SurfaceEffectNode mNode;
    private DeferrableSurface mSessionDeferrableSurface;
    private SurfaceEffectInternal mSurfaceEffect;
    private SurfaceProvider mSurfaceProvider;
    private Executor mSurfaceProviderExecutor = DEFAULT_SURFACE_PROVIDER_EXECUTOR;
    private Size mSurfaceSize;

    public interface SurfaceProvider {
        void onSurfaceRequested(SurfaceRequest surfaceRequest);
    }

    Preview(PreviewConfig config) {
        super(config);
    }

    /* access modifiers changed from: package-private */
    public SessionConfig.Builder createPipeline(String cameraId, PreviewConfig config, Size resolution) {
        PreviewConfig previewConfig = config;
        Size size = resolution;
        if (this.mSurfaceEffect != null) {
            return createPipelineWithNode(cameraId, config, resolution);
        }
        Threads.checkMainThread();
        SessionConfig.Builder sessionConfigBuilder = SessionConfig.Builder.createFrom(config);
        CaptureProcessor captureProcessor = previewConfig.getCaptureProcessor((CaptureProcessor) null);
        clearPipeline();
        SurfaceRequest surfaceRequest = new SurfaceRequest(size, getCamera(), previewConfig.isRgba8888SurfaceRequired(false));
        this.mCurrentSurfaceRequest = surfaceRequest;
        if (sendSurfaceRequestIfReady()) {
            sendTransformationInfoIfReady();
        } else {
            this.mHasUnsentSurfaceRequest = true;
        }
        if (captureProcessor != null) {
            CaptureStage captureStage = new CaptureStage.DefaultCaptureStage();
            HandlerThread handlerThread = new HandlerThread("CameraX-preview_processing");
            handlerThread.start();
            String tagBundleKey = Integer.toString(captureStage.hashCode());
            String tagBundleKey2 = tagBundleKey;
            SurfaceRequest surfaceRequest2 = surfaceRequest;
            ProcessingSurface processingSurface = new ProcessingSurface(resolution.getWidth(), resolution.getHeight(), config.getInputFormat(), new Handler(handlerThread.getLooper()), captureStage, captureProcessor, surfaceRequest.getDeferrableSurface(), tagBundleKey2);
            sessionConfigBuilder.addCameraCaptureCallback(processingSurface.getCameraCaptureCallback());
            ListenableFuture<Void> terminationFuture = processingSurface.getTerminationFuture();
            Objects.requireNonNull(handlerThread);
            terminationFuture.addListener(new Preview$$ExternalSyntheticLambda1(handlerThread), CameraXExecutors.directExecutor());
            this.mSessionDeferrableSurface = processingSurface;
            sessionConfigBuilder.addTag(tagBundleKey2, Integer.valueOf(captureStage.getId()));
        } else {
            SurfaceRequest surfaceRequest3 = surfaceRequest;
            final ImageInfoProcessor processor = previewConfig.getImageInfoProcessor((ImageInfoProcessor) null);
            if (processor != null) {
                sessionConfigBuilder.addCameraCaptureCallback(new CameraCaptureCallback() {
                    public void onCaptureCompleted(CameraCaptureResult cameraCaptureResult) {
                        super.onCaptureCompleted(cameraCaptureResult);
                        if (processor.process(new CameraCaptureResultImageInfo(cameraCaptureResult))) {
                            Preview.this.notifyUpdated();
                        }
                    }
                });
            }
            this.mSessionDeferrableSurface = surfaceRequest3.getDeferrableSurface();
        }
        addCameraSurfaceAndErrorListener(sessionConfigBuilder, cameraId, previewConfig, size);
        return sessionConfigBuilder;
    }

    private SessionConfig.Builder createPipelineWithNode(String cameraId, PreviewConfig config, Size resolution) {
        Threads.checkMainThread();
        Preconditions.checkNotNull(this.mSurfaceEffect);
        CameraInternal camera = getCamera();
        Preconditions.checkNotNull(camera);
        clearPipeline();
        this.mNode = new SurfaceEffectNode(camera, this.mSurfaceEffect);
        SettableSurface settableSurface = new SettableSurface(1, resolution, 34, new Matrix(), true, (Rect) Objects.requireNonNull(getCropRect(resolution)), getRelativeRotation(camera), false);
        this.mCurrentSurfaceRequest = this.mNode.transform(SurfaceEdge.create(Collections.singletonList(settableSurface))).getSurfaces().get(0).createSurfaceRequest(camera);
        if (sendSurfaceRequestIfReady()) {
            sendTransformationInfoIfReady();
        } else {
            this.mHasUnsentSurfaceRequest = true;
        }
        this.mSessionDeferrableSurface = settableSurface;
        SessionConfig.Builder sessionConfigBuilder = SessionConfig.Builder.createFrom(config);
        addCameraSurfaceAndErrorListener(sessionConfigBuilder, cameraId, config, resolution);
        return sessionConfigBuilder;
    }

    public void setEffect(SurfaceEffectInternal surfaceEffect) {
        this.mSurfaceEffect = surfaceEffect;
    }

    private void clearPipeline() {
        DeferrableSurface cameraSurface = this.mSessionDeferrableSurface;
        if (cameraSurface != null) {
            cameraSurface.close();
            this.mSessionDeferrableSurface = null;
        }
        SurfaceEffectNode node = this.mNode;
        if (node != null) {
            node.release();
            this.mNode = null;
        }
        this.mCurrentSurfaceRequest = null;
    }

    private void addCameraSurfaceAndErrorListener(SessionConfig.Builder sessionConfigBuilder, String cameraId, PreviewConfig config, Size resolution) {
        sessionConfigBuilder.addSurface(this.mSessionDeferrableSurface);
        sessionConfigBuilder.addErrorListener(new Preview$$ExternalSyntheticLambda0(this, cameraId, config, resolution));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$addCameraSurfaceAndErrorListener$0$androidx-camera-core-Preview  reason: not valid java name */
    public /* synthetic */ void m153lambda$addCameraSurfaceAndErrorListener$0$androidxcameracorePreview(String cameraId, PreviewConfig config, Size resolution, SessionConfig sessionConfig, SessionConfig.SessionError error) {
        if (isCurrentCamera(cameraId)) {
            updateSessionConfig(createPipeline(cameraId, config, resolution).build());
            notifyReset();
        }
    }

    public void setTargetRotation(int targetRotation) {
        if (setTargetRotationInternal(targetRotation)) {
            sendTransformationInfoIfReady();
        }
    }

    private void sendTransformationInfoIfReady() {
        CameraInternal cameraInternal = getCamera();
        SurfaceProvider surfaceProvider = this.mSurfaceProvider;
        Rect cropRect = getCropRect(this.mSurfaceSize);
        SurfaceRequest surfaceRequest = this.mCurrentSurfaceRequest;
        if (cameraInternal != null && surfaceProvider != null && cropRect != null) {
            surfaceRequest.updateTransformationInfo(SurfaceRequest.TransformationInfo.of(cropRect, getRelativeRotation(cameraInternal), getAppTargetRotation()));
        }
    }

    private Rect getCropRect(Size surfaceResolution) {
        if (getViewPortCropRect() != null) {
            return getViewPortCropRect();
        }
        if (surfaceResolution != null) {
            return new Rect(0, 0, surfaceResolution.getWidth(), surfaceResolution.getHeight());
        }
        return null;
    }

    public void setSurfaceProvider(Executor executor, SurfaceProvider surfaceProvider) {
        Threads.checkMainThread();
        if (surfaceProvider == null) {
            this.mSurfaceProvider = null;
            notifyInactive();
            return;
        }
        this.mSurfaceProvider = surfaceProvider;
        this.mSurfaceProviderExecutor = executor;
        notifyActive();
        if (this.mHasUnsentSurfaceRequest) {
            if (sendSurfaceRequestIfReady()) {
                sendTransformationInfoIfReady();
                this.mHasUnsentSurfaceRequest = false;
            }
        } else if (getAttachedSurfaceResolution() != null) {
            updateConfigAndOutput(getCameraId(), (PreviewConfig) getCurrentConfig(), getAttachedSurfaceResolution());
            notifyReset();
        }
    }

    private boolean sendSurfaceRequestIfReady() {
        SurfaceRequest surfaceRequest = this.mCurrentSurfaceRequest;
        SurfaceProvider surfaceProvider = this.mSurfaceProvider;
        if (surfaceProvider == null || surfaceRequest == null) {
            return false;
        }
        this.mSurfaceProviderExecutor.execute(new Preview$$ExternalSyntheticLambda2(surfaceProvider, surfaceRequest));
        return true;
    }

    public void setSurfaceProvider(SurfaceProvider surfaceProvider) {
        setSurfaceProvider(DEFAULT_SURFACE_PROVIDER_EXECUTOR, surfaceProvider);
    }

    private void updateConfigAndOutput(String cameraId, PreviewConfig config, Size resolution) {
        updateSessionConfig(createPipeline(cameraId, config, resolution).build());
    }

    public int getTargetRotation() {
        return getTargetRotationInternal();
    }

    public ResolutionInfo getResolutionInfo() {
        return super.getResolutionInfo();
    }

    public String toString() {
        return "Preview:" + getName();
    }

    public UseCaseConfig<?> getDefaultConfig(boolean applyDefaultConfig, UseCaseConfigFactory factory) {
        Config captureConfig = factory.getConfig(UseCaseConfigFactory.CaptureType.PREVIEW, 1);
        if (applyDefaultConfig) {
            captureConfig = Config.mergeConfigs(captureConfig, DEFAULT_CONFIG.getConfig());
        }
        if (captureConfig == null) {
            return null;
        }
        return getUseCaseConfigBuilder(captureConfig).getUseCaseConfig();
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [androidx.camera.core.impl.UseCaseConfig$Builder, androidx.camera.core.impl.UseCaseConfig$Builder<?, ?, ?>] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public androidx.camera.core.impl.UseCaseConfig<?> onMergeConfig(androidx.camera.core.impl.CameraInfoInternal r4, androidx.camera.core.impl.UseCaseConfig.Builder<?, ?, ?> r5) {
        /*
            r3 = this;
            androidx.camera.core.impl.MutableConfig r0 = r5.getMutableConfig()
            androidx.camera.core.impl.Config$Option<androidx.camera.core.impl.CaptureProcessor> r1 = androidx.camera.core.impl.PreviewConfig.OPTION_PREVIEW_CAPTURE_PROCESSOR
            r2 = 0
            java.lang.Object r0 = r0.retrieveOption(r1, r2)
            if (r0 == 0) goto L_0x001d
            androidx.camera.core.impl.MutableConfig r0 = r5.getMutableConfig()
            androidx.camera.core.impl.Config$Option<java.lang.Integer> r1 = androidx.camera.core.impl.ImageInputConfig.OPTION_INPUT_FORMAT
            r2 = 35
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r0.insertOption(r1, r2)
            goto L_0x002c
        L_0x001d:
            androidx.camera.core.impl.MutableConfig r0 = r5.getMutableConfig()
            androidx.camera.core.impl.Config$Option<java.lang.Integer> r1 = androidx.camera.core.impl.ImageInputConfig.OPTION_INPUT_FORMAT
            r2 = 34
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r0.insertOption(r1, r2)
        L_0x002c:
            androidx.camera.core.impl.UseCaseConfig r0 = r5.getUseCaseConfig()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.camera.core.Preview.onMergeConfig(androidx.camera.core.impl.CameraInfoInternal, androidx.camera.core.impl.UseCaseConfig$Builder):androidx.camera.core.impl.UseCaseConfig");
    }

    public UseCaseConfig.Builder<?, ?, ?> getUseCaseConfigBuilder(Config config) {
        return Builder.fromConfig(config);
    }

    public void onDetached() {
        clearPipeline();
    }

    /* access modifiers changed from: protected */
    public Size onSuggestedResolutionUpdated(Size suggestedResolution) {
        this.mSurfaceSize = suggestedResolution;
        updateConfigAndOutput(getCameraId(), (PreviewConfig) getCurrentConfig(), this.mSurfaceSize);
        return suggestedResolution;
    }

    public void setViewPortCropRect(Rect viewPortCropRect) {
        super.setViewPortCropRect(viewPortCropRect);
        sendTransformationInfoIfReady();
    }

    public static final class Defaults implements ConfigProvider<PreviewConfig> {
        private static final int DEFAULT_ASPECT_RATIO = 0;
        private static final PreviewConfig DEFAULT_CONFIG = new Builder().setSurfaceOccupancyPriority(2).setTargetAspectRatio(0).getUseCaseConfig();
        private static final int DEFAULT_SURFACE_OCCUPANCY_PRIORITY = 2;

        public PreviewConfig getConfig() {
            return DEFAULT_CONFIG;
        }
    }

    public static final class Builder implements UseCaseConfig.Builder<Preview, PreviewConfig, Builder>, ImageOutputConfig.Builder<Builder>, ThreadConfig.Builder<Builder> {
        private final MutableOptionsBundle mMutableConfig;

        public Builder() {
            this(MutableOptionsBundle.create());
        }

        private Builder(MutableOptionsBundle mutableConfig) {
            this.mMutableConfig = mutableConfig;
            Class<?> oldConfigClass = (Class) mutableConfig.retrieveOption(TargetConfig.OPTION_TARGET_CLASS, null);
            if (oldConfigClass == null || oldConfigClass.equals(Preview.class)) {
                setTargetClass((Class<Preview>) Preview.class);
                return;
            }
            throw new IllegalArgumentException("Invalid target class configuration for " + this + ": " + oldConfigClass);
        }

        static Builder fromConfig(Config configuration) {
            return new Builder(MutableOptionsBundle.from(configuration));
        }

        public static Builder fromConfig(PreviewConfig configuration) {
            return new Builder(MutableOptionsBundle.from(configuration));
        }

        public MutableConfig getMutableConfig() {
            return this.mMutableConfig;
        }

        public PreviewConfig getUseCaseConfig() {
            return new PreviewConfig(OptionsBundle.from(this.mMutableConfig));
        }

        public Preview build() {
            if (getMutableConfig().retrieveOption(PreviewConfig.OPTION_TARGET_ASPECT_RATIO, null) == null || getMutableConfig().retrieveOption(PreviewConfig.OPTION_TARGET_RESOLUTION, null) == null) {
                return new Preview(getUseCaseConfig());
            }
            throw new IllegalArgumentException("Cannot use both setTargetResolution and setTargetAspectRatio on the same config.");
        }

        public Builder setTargetClass(Class<Preview> targetClass) {
            getMutableConfig().insertOption(PreviewConfig.OPTION_TARGET_CLASS, targetClass);
            if (getMutableConfig().retrieveOption(PreviewConfig.OPTION_TARGET_NAME, null) == null) {
                setTargetName(targetClass.getCanonicalName() + "-" + UUID.randomUUID());
            }
            return this;
        }

        public Builder setTargetName(String targetName) {
            getMutableConfig().insertOption(PreviewConfig.OPTION_TARGET_NAME, targetName);
            return this;
        }

        public Builder setTargetAspectRatio(int aspectRatio) {
            getMutableConfig().insertOption(PreviewConfig.OPTION_TARGET_ASPECT_RATIO, Integer.valueOf(aspectRatio));
            return this;
        }

        public Builder setTargetRotation(int rotation) {
            getMutableConfig().insertOption(PreviewConfig.OPTION_TARGET_ROTATION, Integer.valueOf(rotation));
            getMutableConfig().insertOption(ImageOutputConfig.OPTION_APP_TARGET_ROTATION, Integer.valueOf(rotation));
            return this;
        }

        public Builder setTargetResolution(Size resolution) {
            getMutableConfig().insertOption(ImageOutputConfig.OPTION_TARGET_RESOLUTION, resolution);
            return this;
        }

        public Builder setDefaultResolution(Size resolution) {
            getMutableConfig().insertOption(PreviewConfig.OPTION_DEFAULT_RESOLUTION, resolution);
            return this;
        }

        public Builder setMaxResolution(Size resolution) {
            getMutableConfig().insertOption(PreviewConfig.OPTION_MAX_RESOLUTION, resolution);
            return this;
        }

        public Builder setSupportedResolutions(List<Pair<Integer, Size[]>> resolutions) {
            getMutableConfig().insertOption(PreviewConfig.OPTION_SUPPORTED_RESOLUTIONS, resolutions);
            return this;
        }

        public Builder setBackgroundExecutor(Executor executor) {
            getMutableConfig().insertOption(PreviewConfig.OPTION_BACKGROUND_EXECUTOR, executor);
            return this;
        }

        public Builder setDefaultSessionConfig(SessionConfig sessionConfig) {
            getMutableConfig().insertOption(PreviewConfig.OPTION_DEFAULT_SESSION_CONFIG, sessionConfig);
            return this;
        }

        public Builder setDefaultCaptureConfig(CaptureConfig captureConfig) {
            getMutableConfig().insertOption(PreviewConfig.OPTION_DEFAULT_CAPTURE_CONFIG, captureConfig);
            return this;
        }

        public Builder setSessionOptionUnpacker(SessionConfig.OptionUnpacker optionUnpacker) {
            getMutableConfig().insertOption(PreviewConfig.OPTION_SESSION_CONFIG_UNPACKER, optionUnpacker);
            return this;
        }

        public Builder setCaptureOptionUnpacker(CaptureConfig.OptionUnpacker optionUnpacker) {
            getMutableConfig().insertOption(PreviewConfig.OPTION_CAPTURE_CONFIG_UNPACKER, optionUnpacker);
            return this;
        }

        public Builder setSurfaceOccupancyPriority(int priority) {
            getMutableConfig().insertOption(PreviewConfig.OPTION_SURFACE_OCCUPANCY_PRIORITY, Integer.valueOf(priority));
            return this;
        }

        public Builder setCameraSelector(CameraSelector cameraSelector) {
            getMutableConfig().insertOption(UseCaseConfig.OPTION_CAMERA_SELECTOR, cameraSelector);
            return this;
        }

        public Builder setUseCaseEventCallback(UseCase.EventCallback useCaseEventCallback) {
            getMutableConfig().insertOption(PreviewConfig.OPTION_USE_CASE_EVENT_CALLBACK, useCaseEventCallback);
            return this;
        }

        public Builder setIsRgba8888SurfaceRequired(boolean isRgba8888SurfaceRequired) {
            getMutableConfig().insertOption(PreviewConfig.OPTION_RGBA8888_SURFACE_REQUIRED, Boolean.valueOf(isRgba8888SurfaceRequired));
            return this;
        }

        public Builder setImageInfoProcessor(ImageInfoProcessor processor) {
            getMutableConfig().insertOption(PreviewConfig.IMAGE_INFO_PROCESSOR, processor);
            return this;
        }

        public Builder setCaptureProcessor(CaptureProcessor captureProcessor) {
            getMutableConfig().insertOption(PreviewConfig.OPTION_PREVIEW_CAPTURE_PROCESSOR, captureProcessor);
            return this;
        }

        public Builder setZslDisabled(boolean disabled) {
            getMutableConfig().insertOption(UseCaseConfig.OPTION_ZSL_DISABLED, Boolean.valueOf(disabled));
            return this;
        }
    }
}

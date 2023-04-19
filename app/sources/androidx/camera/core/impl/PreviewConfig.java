package androidx.camera.core.impl;

import androidx.camera.core.Preview;
import androidx.camera.core.impl.Config;
import androidx.camera.core.internal.ThreadConfig;

public final class PreviewConfig implements UseCaseConfig<Preview>, ImageOutputConfig, ThreadConfig {
    public static final Config.Option<ImageInfoProcessor> IMAGE_INFO_PROCESSOR = Config.Option.create("camerax.core.preview.imageInfoProcessor", ImageInfoProcessor.class);
    public static final Config.Option<CaptureProcessor> OPTION_PREVIEW_CAPTURE_PROCESSOR = Config.Option.create("camerax.core.preview.captureProcessor", CaptureProcessor.class);
    public static final Config.Option<Boolean> OPTION_RGBA8888_SURFACE_REQUIRED = Config.Option.create("camerax.core.preview.isRgba8888SurfaceRequired", Boolean.class);
    private final OptionsBundle mConfig;

    public PreviewConfig(OptionsBundle config) {
        this.mConfig = config;
    }

    public Config getConfig() {
        return this.mConfig;
    }

    public ImageInfoProcessor getImageInfoProcessor(ImageInfoProcessor valueIfMissing) {
        return (ImageInfoProcessor) retrieveOption(IMAGE_INFO_PROCESSOR, valueIfMissing);
    }

    /* access modifiers changed from: package-private */
    public ImageInfoProcessor getImageInfoProcessor() {
        return (ImageInfoProcessor) retrieveOption(IMAGE_INFO_PROCESSOR);
    }

    public CaptureProcessor getCaptureProcessor(CaptureProcessor valueIfMissing) {
        return (CaptureProcessor) retrieveOption(OPTION_PREVIEW_CAPTURE_PROCESSOR, valueIfMissing);
    }

    public CaptureProcessor getCaptureProcessor() {
        return (CaptureProcessor) retrieveOption(OPTION_PREVIEW_CAPTURE_PROCESSOR);
    }

    public boolean isRgba8888SurfaceRequired(boolean valueIfMissing) {
        return ((Boolean) retrieveOption(OPTION_RGBA8888_SURFACE_REQUIRED, Boolean.valueOf(valueIfMissing))).booleanValue();
    }

    public int getInputFormat() {
        return ((Integer) retrieveOption(OPTION_INPUT_FORMAT)).intValue();
    }
}

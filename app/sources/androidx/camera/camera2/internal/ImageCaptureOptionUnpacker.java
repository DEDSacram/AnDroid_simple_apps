package androidx.camera.camera2.internal;

import androidx.camera.camera2.impl.Camera2ImplConfig;
import androidx.camera.camera2.internal.compat.workaround.ImageCapturePixelHDRPlus;
import androidx.camera.core.impl.CaptureConfig;
import androidx.camera.core.impl.ImageCaptureConfig;
import androidx.camera.core.impl.UseCaseConfig;

final class ImageCaptureOptionUnpacker extends Camera2CaptureOptionUnpacker {
    static final ImageCaptureOptionUnpacker INSTANCE = new ImageCaptureOptionUnpacker(new ImageCapturePixelHDRPlus());
    private final ImageCapturePixelHDRPlus mImageCapturePixelHDRPlus;

    private ImageCaptureOptionUnpacker(ImageCapturePixelHDRPlus imageCapturePixelHDRPlus) {
        this.mImageCapturePixelHDRPlus = imageCapturePixelHDRPlus;
    }

    public void unpack(UseCaseConfig<?> config, CaptureConfig.Builder builder) {
        super.unpack(config, builder);
        if (config instanceof ImageCaptureConfig) {
            ImageCaptureConfig imageCaptureConfig = (ImageCaptureConfig) config;
            Camera2ImplConfig.Builder camera2ConfigBuilder = new Camera2ImplConfig.Builder();
            if (imageCaptureConfig.hasCaptureMode()) {
                this.mImageCapturePixelHDRPlus.toggleHDRPlus(imageCaptureConfig.getCaptureMode(), camera2ConfigBuilder);
            }
            builder.addImplementationOptions(camera2ConfigBuilder.build());
            return;
        }
        throw new IllegalArgumentException("config is not ImageCaptureConfig");
    }
}

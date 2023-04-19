package androidx.camera.camera2.internal;

import androidx.camera.camera2.impl.Camera2ImplConfig;
import androidx.camera.core.impl.CaptureConfig;
import androidx.camera.core.impl.Config;
import androidx.camera.core.impl.OptionsBundle;
import androidx.camera.core.impl.UseCaseConfig;

class Camera2CaptureOptionUnpacker implements CaptureConfig.OptionUnpacker {
    static final Camera2CaptureOptionUnpacker INSTANCE = new Camera2CaptureOptionUnpacker();

    Camera2CaptureOptionUnpacker() {
    }

    public void unpack(UseCaseConfig<?> config, CaptureConfig.Builder builder) {
        CaptureConfig defaultCaptureConfig = config.getDefaultCaptureConfig((CaptureConfig) null);
        Config implOptions = OptionsBundle.emptyBundle();
        int templateType = CaptureConfig.defaultEmptyCaptureConfig().getTemplateType();
        if (defaultCaptureConfig != null) {
            templateType = defaultCaptureConfig.getTemplateType();
            builder.addAllCameraCaptureCallbacks(defaultCaptureConfig.getCameraCaptureCallbacks());
            implOptions = defaultCaptureConfig.getImplementationOptions();
        }
        builder.setImplementationOptions(implOptions);
        Camera2ImplConfig camera2Config = new Camera2ImplConfig(config);
        builder.setTemplateType(camera2Config.getCaptureRequestTemplate(templateType));
        builder.addCameraCaptureCallback(CaptureCallbackContainer.create(camera2Config.getSessionCaptureCallback(Camera2CaptureCallbacks.createNoOpCallback())));
        builder.addImplementationOptions(camera2Config.getCaptureRequestOptions());
    }
}

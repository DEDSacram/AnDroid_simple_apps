package androidx.camera.camera2.internal;

import androidx.camera.camera2.impl.Camera2ImplConfig;
import androidx.camera.camera2.impl.CameraEventCallbacks;
import androidx.camera.core.impl.Config;
import androidx.camera.core.impl.MutableOptionsBundle;
import androidx.camera.core.impl.OptionsBundle;
import androidx.camera.core.impl.SessionConfig;
import androidx.camera.core.impl.UseCaseConfig;

final class Camera2SessionOptionUnpacker implements SessionConfig.OptionUnpacker {
    static final Camera2SessionOptionUnpacker INSTANCE = new Camera2SessionOptionUnpacker();

    Camera2SessionOptionUnpacker() {
    }

    public void unpack(UseCaseConfig<?> config, SessionConfig.Builder builder) {
        SessionConfig defaultSessionConfig = config.getDefaultSessionConfig((SessionConfig) null);
        Config implOptions = OptionsBundle.emptyBundle();
        int templateType = SessionConfig.defaultEmptySessionConfig().getTemplateType();
        if (defaultSessionConfig != null) {
            templateType = defaultSessionConfig.getTemplateType();
            builder.addAllDeviceStateCallbacks(defaultSessionConfig.getDeviceStateCallbacks());
            builder.addAllSessionStateCallbacks(defaultSessionConfig.getSessionStateCallbacks());
            builder.addAllRepeatingCameraCaptureCallbacks(defaultSessionConfig.getRepeatingCameraCaptureCallbacks());
            implOptions = defaultSessionConfig.getImplementationOptions();
        }
        builder.setImplementationOptions(implOptions);
        Camera2ImplConfig camera2Config = new Camera2ImplConfig(config);
        builder.setTemplateType(camera2Config.getCaptureRequestTemplate(templateType));
        builder.addDeviceStateCallback(camera2Config.getDeviceStateCallback(CameraDeviceStateCallbacks.createNoOpCallback()));
        builder.addSessionStateCallback(camera2Config.getSessionStateCallback(CameraCaptureSessionStateCallbacks.createNoOpCallback()));
        builder.addCameraCaptureCallback(CaptureCallbackContainer.create(camera2Config.getSessionCaptureCallback(Camera2CaptureCallbacks.createNoOpCallback())));
        MutableOptionsBundle extendedConfig = MutableOptionsBundle.create();
        extendedConfig.insertOption(Camera2ImplConfig.CAMERA_EVENT_CALLBACK_OPTION, camera2Config.getCameraEventCallback(CameraEventCallbacks.createEmptyCallback()));
        extendedConfig.insertOption(Camera2ImplConfig.SESSION_PHYSICAL_CAMERA_ID_OPTION, camera2Config.getPhysicalCameraId((String) null));
        extendedConfig.insertOption(Camera2ImplConfig.STREAM_USE_CASE_OPTION, Long.valueOf(camera2Config.getStreamUseCase(-1)));
        builder.addImplementationOptions(extendedConfig);
        builder.addImplementationOptions(camera2Config.getCaptureRequestOptions());
    }
}

package androidx.camera.camera2;

import android.content.Context;
import androidx.camera.camera2.internal.Camera2DeviceSurfaceManager;
import androidx.camera.camera2.internal.Camera2UseCaseConfigFactory;
import androidx.camera.core.CameraUnavailableException;
import androidx.camera.core.CameraXConfig;
import androidx.camera.core.InitializationException;
import androidx.camera.core.impl.CameraDeviceSurfaceManager;
import androidx.camera.core.impl.CameraFactory;
import androidx.camera.core.impl.UseCaseConfigFactory;
import java.util.Set;

public final class Camera2Config {
    private Camera2Config() {
    }

    public static CameraXConfig defaultConfig() {
        CameraFactory.Provider cameraFactoryProvider = Camera2Config$$ExternalSyntheticLambda1.INSTANCE;
        CameraDeviceSurfaceManager.Provider surfaceManagerProvider = Camera2Config$$ExternalSyntheticLambda0.INSTANCE;
        return new CameraXConfig.Builder().setCameraFactoryProvider(cameraFactoryProvider).setDeviceSurfaceManagerProvider(surfaceManagerProvider).setUseCaseConfigFactoryProvider(Camera2Config$$ExternalSyntheticLambda2.INSTANCE).build();
    }

    static /* synthetic */ CameraDeviceSurfaceManager lambda$defaultConfig$0(Context context, Object cameraManager, Set availableCameraIds) throws InitializationException {
        try {
            return new Camera2DeviceSurfaceManager(context, cameraManager, availableCameraIds);
        } catch (CameraUnavailableException e) {
            throw new InitializationException((Throwable) e);
        }
    }

    static /* synthetic */ UseCaseConfigFactory lambda$defaultConfig$1(Context context) throws InitializationException {
        return new Camera2UseCaseConfigFactory(context);
    }

    public static final class DefaultProvider implements CameraXConfig.Provider {
        public CameraXConfig getCameraXConfig() {
            return Camera2Config.defaultConfig();
        }
    }
}

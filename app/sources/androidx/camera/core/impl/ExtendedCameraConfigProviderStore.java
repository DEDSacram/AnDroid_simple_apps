package androidx.camera.core.impl;

import java.util.HashMap;
import java.util.Map;

public final class ExtendedCameraConfigProviderStore {
    private static final Map<Object, CameraConfigProvider> CAMERA_CONFIG_PROVIDERS = new HashMap();
    private static final Object LOCK = new Object();

    private ExtendedCameraConfigProviderStore() {
    }

    public static void addConfig(Object key, CameraConfigProvider provider) {
        synchronized (LOCK) {
            CAMERA_CONFIG_PROVIDERS.put(key, provider);
        }
    }

    public static CameraConfigProvider getConfigProvider(Object key) {
        CameraConfigProvider provider;
        synchronized (LOCK) {
            provider = CAMERA_CONFIG_PROVIDERS.get(key);
        }
        if (provider == null) {
            return CameraConfigProvider.EMPTY;
        }
        return provider;
    }
}

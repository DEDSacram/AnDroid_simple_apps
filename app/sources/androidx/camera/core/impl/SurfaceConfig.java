package androidx.camera.core.impl;

public abstract class SurfaceConfig {

    public enum ConfigType {
        PRIV,
        YUV,
        JPEG,
        RAW
    }

    public abstract ConfigSize getConfigSize();

    public abstract ConfigType getConfigType();

    SurfaceConfig() {
    }

    public static SurfaceConfig create(ConfigType type, ConfigSize size) {
        return new AutoValue_SurfaceConfig(type, size);
    }

    public final boolean isSupported(SurfaceConfig surfaceConfig) {
        ConfigType configType = surfaceConfig.getConfigType();
        if (surfaceConfig.getConfigSize().getId() > getConfigSize().getId() || configType != getConfigType()) {
            return false;
        }
        return true;
    }

    public enum ConfigSize {
        ANALYSIS(0),
        PREVIEW(1),
        RECORD(2),
        MAXIMUM(3),
        NOT_SUPPORT(4);
        
        final int mId;

        private ConfigSize(int id) {
            this.mId = id;
        }

        /* access modifiers changed from: package-private */
        public int getId() {
            return this.mId;
        }
    }
}

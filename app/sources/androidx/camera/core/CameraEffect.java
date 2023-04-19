package androidx.camera.core;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface CameraEffect {

    @Retention(RetentionPolicy.SOURCE)
    public @interface Targets {
    }
}

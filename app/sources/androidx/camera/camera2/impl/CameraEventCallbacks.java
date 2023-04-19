package androidx.camera.camera2.impl;

import androidx.camera.core.impl.CaptureConfig;
import androidx.camera.core.impl.MultiValueSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CameraEventCallbacks extends MultiValueSet<CameraEventCallback> {
    public CameraEventCallbacks(CameraEventCallback... callbacks) {
        addAll(Arrays.asList(callbacks));
    }

    public ComboCameraEventCallback createComboCallback() {
        return new ComboCameraEventCallback(getAllItems());
    }

    public static CameraEventCallbacks createEmptyCallback() {
        return new CameraEventCallbacks(new CameraEventCallback[0]);
    }

    public MultiValueSet<CameraEventCallback> clone() {
        CameraEventCallbacks ret = createEmptyCallback();
        ret.addAll(getAllItems());
        return ret;
    }

    public static final class ComboCameraEventCallback {
        private final List<CameraEventCallback> mCallbacks = new ArrayList();

        ComboCameraEventCallback(List<CameraEventCallback> callbacks) {
            for (CameraEventCallback callback : callbacks) {
                this.mCallbacks.add(callback);
            }
        }

        public List<CaptureConfig> onPresetSession() {
            List<CaptureConfig> ret = new ArrayList<>();
            for (CameraEventCallback callback : this.mCallbacks) {
                CaptureConfig presetCaptureStage = callback.onPresetSession();
                if (presetCaptureStage != null) {
                    ret.add(presetCaptureStage);
                }
            }
            return ret;
        }

        public List<CaptureConfig> onEnableSession() {
            List<CaptureConfig> ret = new ArrayList<>();
            for (CameraEventCallback callback : this.mCallbacks) {
                CaptureConfig enableCaptureStage = callback.onEnableSession();
                if (enableCaptureStage != null) {
                    ret.add(enableCaptureStage);
                }
            }
            return ret;
        }

        public List<CaptureConfig> onRepeating() {
            List<CaptureConfig> ret = new ArrayList<>();
            for (CameraEventCallback callback : this.mCallbacks) {
                CaptureConfig repeatingCaptureStage = callback.onRepeating();
                if (repeatingCaptureStage != null) {
                    ret.add(repeatingCaptureStage);
                }
            }
            return ret;
        }

        public List<CaptureConfig> onDisableSession() {
            List<CaptureConfig> ret = new ArrayList<>();
            for (CameraEventCallback callback : this.mCallbacks) {
                CaptureConfig disableCaptureStage = callback.onDisableSession();
                if (disableCaptureStage != null) {
                    ret.add(disableCaptureStage);
                }
            }
            return ret;
        }

        public List<CameraEventCallback> getCallbacks() {
            return this.mCallbacks;
        }
    }
}

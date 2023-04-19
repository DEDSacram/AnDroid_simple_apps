package androidx.camera.camera2.interop;

import android.hardware.camera2.CaptureRequest;
import androidx.camera.camera2.impl.Camera2ImplConfig;
import androidx.camera.core.ExtendableBuilder;
import androidx.camera.core.impl.Config;
import androidx.camera.core.impl.MutableConfig;
import androidx.camera.core.impl.MutableOptionsBundle;
import androidx.camera.core.impl.OptionsBundle;
import androidx.camera.core.impl.ReadableConfig;

public class CaptureRequestOptions implements ReadableConfig {
    private final Config mConfig;

    public CaptureRequestOptions(Config config) {
        this.mConfig = config;
    }

    public <ValueT> ValueT getCaptureRequestOption(CaptureRequest.Key<ValueT> key) {
        return this.mConfig.retrieveOption(Camera2ImplConfig.createCaptureRequestOption(key), null);
    }

    public <ValueT> ValueT getCaptureRequestOption(CaptureRequest.Key<ValueT> key, ValueT valueIfMissing) {
        return this.mConfig.retrieveOption(Camera2ImplConfig.createCaptureRequestOption(key), valueIfMissing);
    }

    public Config getConfig() {
        return this.mConfig;
    }

    public static final class Builder implements ExtendableBuilder<CaptureRequestOptions> {
        private final MutableOptionsBundle mMutableOptionsBundle = MutableOptionsBundle.create();

        public static Builder from(Config config) {
            Builder bundleBuilder = new Builder();
            config.findOptions(Camera2ImplConfig.CAPTURE_REQUEST_ID_STEM, new CaptureRequestOptions$Builder$$ExternalSyntheticLambda0(bundleBuilder, config));
            return bundleBuilder;
        }

        static /* synthetic */ boolean lambda$from$0(Builder bundleBuilder, Config config, Config.Option option) {
            Config.Option option2 = option;
            bundleBuilder.getMutableConfig().insertOption(option2, config.getOptionPriority(option2), config.retrieveOption(option2));
            return true;
        }

        public MutableConfig getMutableConfig() {
            return this.mMutableOptionsBundle;
        }

        public <ValueT> Builder setCaptureRequestOption(CaptureRequest.Key<ValueT> key, ValueT value) {
            this.mMutableOptionsBundle.insertOption(Camera2ImplConfig.createCaptureRequestOption(key), value);
            return this;
        }

        public <ValueT> Builder clearCaptureRequestOption(CaptureRequest.Key<ValueT> key) {
            this.mMutableOptionsBundle.removeOption(Camera2ImplConfig.createCaptureRequestOption(key));
            return this;
        }

        public CaptureRequestOptions build() {
            return new CaptureRequestOptions(OptionsBundle.from(this.mMutableOptionsBundle));
        }
    }
}

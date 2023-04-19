package androidx.camera.camera2.interop;

import androidx.camera.camera2.interop.CaptureRequestOptions;
import androidx.camera.core.impl.Config;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class CaptureRequestOptions$Builder$$ExternalSyntheticLambda0 implements Config.OptionMatcher {
    public final /* synthetic */ CaptureRequestOptions.Builder f$0;
    public final /* synthetic */ Config f$1;

    public /* synthetic */ CaptureRequestOptions$Builder$$ExternalSyntheticLambda0(CaptureRequestOptions.Builder builder, Config config) {
        this.f$0 = builder;
        this.f$1 = config;
    }

    public final boolean onOptionMatched(Config.Option option) {
        return CaptureRequestOptions.Builder.lambda$from$0(this.f$0, this.f$1, option);
    }
}

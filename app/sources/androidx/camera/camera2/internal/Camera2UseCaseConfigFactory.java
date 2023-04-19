package androidx.camera.camera2.internal;

import android.content.Context;
import androidx.camera.camera2.internal.compat.workaround.PreviewPixelHDRnet;
import androidx.camera.core.impl.CaptureConfig;
import androidx.camera.core.impl.Config;
import androidx.camera.core.impl.ImageOutputConfig;
import androidx.camera.core.impl.MutableOptionsBundle;
import androidx.camera.core.impl.OptionsBundle;
import androidx.camera.core.impl.SessionConfig;
import androidx.camera.core.impl.UseCaseConfig;
import androidx.camera.core.impl.UseCaseConfigFactory;

public final class Camera2UseCaseConfigFactory implements UseCaseConfigFactory {
    final DisplayInfoManager mDisplayInfoManager;

    public Camera2UseCaseConfigFactory(Context context) {
        this.mDisplayInfoManager = DisplayInfoManager.getInstance(context);
    }

    public Config getConfig(UseCaseConfigFactory.CaptureType captureType, int captureMode) {
        Object obj;
        int i;
        MutableOptionsBundle mutableConfig = MutableOptionsBundle.create();
        SessionConfig.Builder sessionBuilder = new SessionConfig.Builder();
        int i2 = 5;
        switch (AnonymousClass1.$SwitchMap$androidx$camera$core$impl$UseCaseConfigFactory$CaptureType[captureType.ordinal()]) {
            case 1:
                if (captureMode == 2) {
                    i = 5;
                } else {
                    i = 1;
                }
                sessionBuilder.setTemplateType(i);
                break;
            case 2:
            case 3:
                sessionBuilder.setTemplateType(1);
                break;
            case 4:
                sessionBuilder.setTemplateType(3);
                break;
        }
        if (captureType == UseCaseConfigFactory.CaptureType.PREVIEW) {
            PreviewPixelHDRnet.setHDRnet(sessionBuilder);
        }
        mutableConfig.insertOption(UseCaseConfig.OPTION_DEFAULT_SESSION_CONFIG, sessionBuilder.build());
        mutableConfig.insertOption(UseCaseConfig.OPTION_SESSION_CONFIG_UNPACKER, Camera2SessionOptionUnpacker.INSTANCE);
        CaptureConfig.Builder captureBuilder = new CaptureConfig.Builder();
        switch (AnonymousClass1.$SwitchMap$androidx$camera$core$impl$UseCaseConfigFactory$CaptureType[captureType.ordinal()]) {
            case 1:
                if (captureMode != 2) {
                    i2 = 2;
                }
                captureBuilder.setTemplateType(i2);
                break;
            case 2:
            case 3:
                captureBuilder.setTemplateType(1);
                break;
            case 4:
                captureBuilder.setTemplateType(3);
                break;
        }
        mutableConfig.insertOption(UseCaseConfig.OPTION_DEFAULT_CAPTURE_CONFIG, captureBuilder.build());
        Config.Option<CaptureConfig.OptionUnpacker> option = UseCaseConfig.OPTION_CAPTURE_CONFIG_UNPACKER;
        if (captureType == UseCaseConfigFactory.CaptureType.IMAGE_CAPTURE) {
            obj = ImageCaptureOptionUnpacker.INSTANCE;
        } else {
            obj = Camera2CaptureOptionUnpacker.INSTANCE;
        }
        mutableConfig.insertOption(option, obj);
        if (captureType == UseCaseConfigFactory.CaptureType.PREVIEW) {
            mutableConfig.insertOption(ImageOutputConfig.OPTION_MAX_RESOLUTION, this.mDisplayInfoManager.getPreviewSize());
        }
        mutableConfig.insertOption(ImageOutputConfig.OPTION_TARGET_ROTATION, Integer.valueOf(this.mDisplayInfoManager.getMaxSizeDisplay().getRotation()));
        if (captureType == UseCaseConfigFactory.CaptureType.VIDEO_CAPTURE) {
            mutableConfig.insertOption(UseCaseConfig.OPTION_ZSL_DISABLED, true);
        }
        return OptionsBundle.from(mutableConfig);
    }

    /* renamed from: androidx.camera.camera2.internal.Camera2UseCaseConfigFactory$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$androidx$camera$core$impl$UseCaseConfigFactory$CaptureType;

        static {
            int[] iArr = new int[UseCaseConfigFactory.CaptureType.values().length];
            $SwitchMap$androidx$camera$core$impl$UseCaseConfigFactory$CaptureType = iArr;
            try {
                iArr[UseCaseConfigFactory.CaptureType.IMAGE_CAPTURE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$androidx$camera$core$impl$UseCaseConfigFactory$CaptureType[UseCaseConfigFactory.CaptureType.PREVIEW.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$androidx$camera$core$impl$UseCaseConfigFactory$CaptureType[UseCaseConfigFactory.CaptureType.IMAGE_ANALYSIS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$androidx$camera$core$impl$UseCaseConfigFactory$CaptureType[UseCaseConfigFactory.CaptureType.VIDEO_CAPTURE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }
}

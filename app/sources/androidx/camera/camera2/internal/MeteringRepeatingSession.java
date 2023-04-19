package androidx.camera.camera2.internal;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.util.Size;
import android.view.Surface;
import androidx.camera.camera2.internal.compat.CameraCharacteristicsCompat;
import androidx.camera.camera2.internal.compat.workaround.SupportedRepeatingSurfaceSize;
import androidx.camera.core.Logger;
import androidx.camera.core.UseCase;
import androidx.camera.core.impl.Config;
import androidx.camera.core.impl.DeferrableSurface;
import androidx.camera.core.impl.ImmediateSurface;
import androidx.camera.core.impl.MutableOptionsBundle;
import androidx.camera.core.impl.SessionConfig;
import androidx.camera.core.impl.UseCaseConfig;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.impl.utils.futures.FutureCallback;
import androidx.camera.core.impl.utils.futures.Futures;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class MeteringRepeatingSession {
    private static final String TAG = "MeteringRepeating";
    private final MeteringRepeatingConfig mConfigWithDefaults;
    private DeferrableSurface mDeferrableSurface;
    private final SessionConfig mSessionConfig;
    private final SupportedRepeatingSurfaceSize mSupportedRepeatingSurfaceSize = new SupportedRepeatingSurfaceSize();

    MeteringRepeatingSession(CameraCharacteristicsCompat cameraCharacteristicsCompat, DisplayInfoManager displayInfoManager) {
        MeteringRepeatingConfig meteringRepeatingConfig = new MeteringRepeatingConfig();
        this.mConfigWithDefaults = meteringRepeatingConfig;
        final SurfaceTexture surfaceTexture = new SurfaceTexture(0);
        Size meteringSurfaceSize = getProperPreviewSize(cameraCharacteristicsCompat, displayInfoManager);
        Logger.d(TAG, "MeteringSession SurfaceTexture size: " + meteringSurfaceSize);
        surfaceTexture.setDefaultBufferSize(meteringSurfaceSize.getWidth(), meteringSurfaceSize.getHeight());
        final Surface surface = new Surface(surfaceTexture);
        SessionConfig.Builder builder = SessionConfig.Builder.createFrom(meteringRepeatingConfig);
        builder.setTemplateType(1);
        ImmediateSurface immediateSurface = new ImmediateSurface(surface);
        this.mDeferrableSurface = immediateSurface;
        Futures.addCallback(immediateSurface.getTerminationFuture(), new FutureCallback<Void>() {
            public void onSuccess(Void result) {
                surface.release();
                surfaceTexture.release();
            }

            public void onFailure(Throwable t) {
                throw new IllegalStateException("Future should never fail. Did it get completed by GC?", t);
            }
        }, CameraXExecutors.directExecutor());
        builder.addSurface(this.mDeferrableSurface);
        this.mSessionConfig = builder.build();
    }

    /* access modifiers changed from: package-private */
    public UseCaseConfig<?> getUseCaseConfig() {
        return this.mConfigWithDefaults;
    }

    /* access modifiers changed from: package-private */
    public SessionConfig getSessionConfig() {
        return this.mSessionConfig;
    }

    /* access modifiers changed from: package-private */
    public String getName() {
        return TAG;
    }

    /* access modifiers changed from: package-private */
    public void clear() {
        Logger.d(TAG, "MeteringRepeating clear!");
        DeferrableSurface deferrableSurface = this.mDeferrableSurface;
        if (deferrableSurface != null) {
            deferrableSurface.close();
        }
        this.mDeferrableSurface = null;
    }

    private static class MeteringRepeatingConfig implements UseCaseConfig<UseCase> {
        private final Config mConfig;

        MeteringRepeatingConfig() {
            MutableOptionsBundle mutableOptionsBundle = MutableOptionsBundle.create();
            mutableOptionsBundle.insertOption(UseCaseConfig.OPTION_SESSION_CONFIG_UNPACKER, new Camera2SessionOptionUnpacker());
            this.mConfig = mutableOptionsBundle;
        }

        public Config getConfig() {
            return this.mConfig;
        }
    }

    private Size getProperPreviewSize(CameraCharacteristicsCompat cameraCharacteristicsCompat, DisplayInfoManager displayInfoManager) {
        Size[] outputSizes;
        StreamConfigurationMap map = (StreamConfigurationMap) cameraCharacteristicsCompat.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (map == null) {
            Logger.e(TAG, "Can not retrieve SCALER_STREAM_CONFIGURATION_MAP.");
            return new Size(0, 0);
        }
        if (Build.VERSION.SDK_INT < 23) {
            outputSizes = map.getOutputSizes(SurfaceTexture.class);
        } else {
            outputSizes = map.getOutputSizes(34);
        }
        if (outputSizes == null) {
            Logger.e(TAG, "Can not get output size list.");
            return new Size(0, 0);
        }
        Size[] outputSizes2 = this.mSupportedRepeatingSurfaceSize.getSupportedSizes(outputSizes);
        List<Size> outSizesList = Arrays.asList(outputSizes2);
        Collections.sort(outSizesList, MeteringRepeatingSession$$ExternalSyntheticLambda0.INSTANCE);
        Size previewMaxSize = displayInfoManager.getPreviewSize();
        long maxSizeProduct = Math.min(((long) previewMaxSize.getWidth()) * ((long) previewMaxSize.getHeight()), 307200);
        Size previousSize = null;
        int length = outputSizes2.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            Size outputSize = outputSizes2[i];
            Size[] outputSizes3 = outputSizes2;
            long product = ((long) outputSize.getWidth()) * ((long) outputSize.getHeight());
            if (product == maxSizeProduct) {
                return outputSize;
            }
            if (product <= maxSizeProduct) {
                previousSize = outputSize;
                i++;
                outputSizes2 = outputSizes3;
            } else if (previousSize != null) {
                return previousSize;
            }
        }
        return outSizesList.get(0);
    }
}

package androidx.camera.core.impl;

import android.view.Surface;
import androidx.camera.core.impl.utils.futures.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public final class SessionProcessorSurface extends DeferrableSurface {
    private final int mOutputConfigId;
    private final Surface mSurface;

    public SessionProcessorSurface(Surface surface, int outputConfigId) {
        this.mSurface = surface;
        this.mOutputConfigId = outputConfigId;
    }

    public int getOutputConfigId() {
        return this.mOutputConfigId;
    }

    public ListenableFuture<Surface> provideSurface() {
        return Futures.immediateFuture(this.mSurface);
    }
}

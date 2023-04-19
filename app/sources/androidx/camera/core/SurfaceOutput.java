package androidx.camera.core;

import android.util.Size;
import android.view.Surface;
import java.util.concurrent.Executor;

public interface SurfaceOutput {

    public interface OnCloseRequestedListener {
        void onCloseRequested();
    }

    void close();

    int getFormat();

    Size getSize();

    Surface getSurface(Executor executor, OnCloseRequestedListener onCloseRequestedListener);

    int getTargets();

    void updateTransformMatrix(float[] fArr, float[] fArr2);
}

package androidx.camera.core.impl.utils;

import android.util.Size;
import java.util.Comparator;

public final class CompareSizesByArea implements Comparator<Size> {
    private boolean mReverse;

    public CompareSizesByArea() {
        this(false);
    }

    public CompareSizesByArea(boolean reverse) {
        this.mReverse = false;
        this.mReverse = reverse;
    }

    public int compare(Size lhs, Size rhs) {
        int result = Long.signum((((long) lhs.getWidth()) * ((long) lhs.getHeight())) - (((long) rhs.getWidth()) * ((long) rhs.getHeight())));
        if (this.mReverse) {
            return result * -1;
        }
        return result;
    }
}

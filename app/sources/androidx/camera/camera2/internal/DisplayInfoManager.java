package androidx.camera.camera2.internal;

import android.content.Context;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.util.Size;
import android.view.Display;
import androidx.camera.camera2.internal.compat.workaround.MaxPreviewSize;

public class DisplayInfoManager {
    private static final Object INSTANCE_LOCK = new Object();
    private static final Size MAX_PREVIEW_SIZE = new Size(1920, 1080);
    private static volatile DisplayInfoManager sInstance;
    private final DisplayManager mDisplayManager;
    private final MaxPreviewSize mMaxPreviewSize = new MaxPreviewSize();
    private volatile Size mPreviewSize = null;

    private DisplayInfoManager(Context context) {
        this.mDisplayManager = (DisplayManager) context.getSystemService("display");
    }

    public static DisplayInfoManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (INSTANCE_LOCK) {
                if (sInstance == null) {
                    sInstance = new DisplayInfoManager(context);
                }
            }
        }
        return sInstance;
    }

    static void releaseInstance() {
        sInstance = null;
    }

    /* access modifiers changed from: package-private */
    public void refresh() {
        this.mPreviewSize = calculatePreviewSize();
    }

    public Display getMaxSizeDisplay() {
        Display[] displays = this.mDisplayManager.getDisplays();
        if (displays.length == 1) {
            return displays[0];
        }
        Display maxDisplay = null;
        int maxDisplaySize = -1;
        for (Display display : displays) {
            if (display.getState() != 1) {
                Point displaySize = new Point();
                display.getRealSize(displaySize);
                if (displaySize.x * displaySize.y > maxDisplaySize) {
                    maxDisplay = display;
                    maxDisplaySize = displaySize.x * displaySize.y;
                }
            }
        }
        if (maxDisplay != null) {
            return maxDisplay;
        }
        throw new IllegalArgumentException("No display can be found from the input display manager!");
    }

    /* access modifiers changed from: package-private */
    public Size getPreviewSize() {
        if (this.mPreviewSize != null) {
            return this.mPreviewSize;
        }
        this.mPreviewSize = calculatePreviewSize();
        return this.mPreviewSize;
    }

    private Size calculatePreviewSize() {
        Size displayViewSize;
        Point displaySize = new Point();
        getMaxSizeDisplay().getRealSize(displaySize);
        if (displaySize.x > displaySize.y) {
            displayViewSize = new Size(displaySize.x, displaySize.y);
        } else {
            displayViewSize = new Size(displaySize.y, displaySize.x);
        }
        int width = displayViewSize.getWidth() * displayViewSize.getHeight();
        Size size = MAX_PREVIEW_SIZE;
        if (width > size.getWidth() * size.getHeight()) {
            displayViewSize = MAX_PREVIEW_SIZE;
        }
        return this.mMaxPreviewSize.getMaxPreviewResolution(displayViewSize);
    }
}

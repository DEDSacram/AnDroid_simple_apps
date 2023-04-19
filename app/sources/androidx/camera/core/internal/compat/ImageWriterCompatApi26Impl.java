package androidx.camera.core.internal.compat;

import android.media.ImageWriter;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import androidx.core.util.Preconditions;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class ImageWriterCompatApi26Impl {
    private static final String TAG = "ImageWriterCompatApi26";
    private static Method sNewInstanceMethod;

    static {
        Class<ImageWriter> cls = ImageWriter.class;
        try {
            sNewInstanceMethod = cls.getMethod("newInstance", new Class[]{Surface.class, Integer.TYPE, Integer.TYPE});
        } catch (NoSuchMethodException e) {
            Log.i(TAG, "Unable to initialize via reflection.", e);
        }
    }

    static ImageWriter newInstance(Surface surface, int maxImages, int format) {
        Throwable t = null;
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                return (ImageWriter) Preconditions.checkNotNull(sNewInstanceMethod.invoke((Object) null, new Object[]{surface, Integer.valueOf(maxImages), Integer.valueOf(format)}));
            } catch (IllegalAccessException | InvocationTargetException e) {
                t = e;
            }
        }
        throw new RuntimeException("Unable to invoke newInstance(Surface, int, int) via reflection.", t);
    }

    private ImageWriterCompatApi26Impl() {
    }
}

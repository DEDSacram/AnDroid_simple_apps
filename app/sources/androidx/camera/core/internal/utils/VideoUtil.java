package androidx.camera.core.internal.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import androidx.camera.core.Logger;
import androidx.core.util.Preconditions;
import okhttp3.HttpUrl;

public final class VideoUtil {
    private static final String TAG = "VideoUtil";

    private VideoUtil() {
    }

    public static String getAbsolutePathFromUri(ContentResolver resolver, Uri contentUri) {
        Cursor cursor = null;
        try {
            Cursor cursor2 = (Cursor) Preconditions.checkNotNull(resolver.query(contentUri, new String[]{"_data"}, (String) null, (String[]) null, (String) null));
            int columnIndex = cursor2.getColumnIndexOrThrow("_data");
            cursor2.moveToFirst();
            String string = cursor2.getString(columnIndex);
            if (cursor2 != null) {
                cursor2.close();
            }
            return string;
        } catch (RuntimeException e) {
            Logger.e(TAG, String.format("Failed in getting absolute path for Uri %s with Exception %s", new Object[]{contentUri.toString(), e.toString()}));
            if (cursor != null) {
                cursor.close();
            }
            return HttpUrl.FRAGMENT_ENCODE_SET;
        } catch (Throwable e2) {
            if (cursor != null) {
                cursor.close();
            }
            throw e2;
        }
    }
}

package androidx.camera.core.impl.utils;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

public final class ContextUtil {
    public static Context getApplicationContext(Context context) {
        String attributeTag;
        Context applicationContext = context.getApplicationContext();
        if (Build.VERSION.SDK_INT < 30 || (attributeTag = Api30Impl.getAttributionTag(context)) == null) {
            return applicationContext;
        }
        return Api30Impl.createAttributionContext(applicationContext, attributeTag);
    }

    public static Context getBaseContext(ContextWrapper context) {
        String attributeTag;
        Context baseContext = context.getBaseContext();
        if (Build.VERSION.SDK_INT < 30 || (attributeTag = Api30Impl.getAttributionTag(context)) == null) {
            return baseContext;
        }
        return Api30Impl.createAttributionContext(baseContext, attributeTag);
    }

    public static Application getApplicationFromContext(Context context) {
        for (Context appContext = getApplicationContext(context); appContext instanceof ContextWrapper; appContext = getBaseContext((ContextWrapper) appContext)) {
            if (appContext instanceof Application) {
                return (Application) appContext;
            }
        }
        return null;
    }

    private ContextUtil() {
    }

    private static class Api30Impl {
        private Api30Impl() {
        }

        static Context createAttributionContext(Context context, String attributeTag) {
            return context.createAttributionContext(attributeTag);
        }

        static String getAttributionTag(Context context) {
            return context.getAttributionTag();
        }
    }
}

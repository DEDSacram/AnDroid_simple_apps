package okhttp3.internal.platform.android;

import java.lang.reflect.Method;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\b\u0000\u0018\u0000 \r2\u00020\u0001:\u0001\rB#\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0003¢\u0006\u0002\u0010\u0006J\u0010\u0010\u0007\u001a\u0004\u0018\u00010\u00012\u0006\u0010\b\u001a\u00020\tJ\u0010\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u0001R\u0010\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0003X\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0003X\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"Lokhttp3/internal/platform/android/CloseGuard;", "", "getMethod", "Ljava/lang/reflect/Method;", "openMethod", "warnIfOpenMethod", "(Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)V", "createAndOpen", "closer", "", "warnIfOpen", "", "closeGuardInstance", "Companion", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: CloseGuard.kt */
public final class CloseGuard {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    private final Method getMethod;
    private final Method openMethod;
    private final Method warnIfOpenMethod;

    public CloseGuard(Method getMethod2, Method openMethod2, Method warnIfOpenMethod2) {
        this.getMethod = getMethod2;
        this.openMethod = openMethod2;
        this.warnIfOpenMethod = warnIfOpenMethod2;
    }

    public final Object createAndOpen(String closer) {
        Intrinsics.checkNotNullParameter(closer, "closer");
        Method method = this.getMethod;
        if (method != null) {
            try {
                Object closeGuardInstance = method.invoke((Object) null, new Object[0]);
                Method method2 = this.openMethod;
                Intrinsics.checkNotNull(method2);
                method2.invoke(closeGuardInstance, new Object[]{closer});
                return closeGuardInstance;
            } catch (Exception e) {
            }
        }
        return null;
    }

    public final boolean warnIfOpen(Object closeGuardInstance) {
        if (closeGuardInstance == null) {
            return false;
        }
        try {
            Method method = this.warnIfOpenMethod;
            Intrinsics.checkNotNull(method);
            method.invoke(closeGuardInstance, new Object[0]);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004¨\u0006\u0005"}, d2 = {"Lokhttp3/internal/platform/android/CloseGuard$Companion;", "", "()V", "get", "Lokhttp3/internal/platform/android/CloseGuard;", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: CloseGuard.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final CloseGuard get() {
            Method warnIfOpenMethod;
            Method openMethod;
            Method getMethod;
            try {
                Class closeGuardClass = Class.forName("dalvik.system.CloseGuard");
                getMethod = closeGuardClass.getMethod("get", new Class[0]);
                openMethod = closeGuardClass.getMethod("open", new Class[]{String.class});
                warnIfOpenMethod = closeGuardClass.getMethod("warnIfOpen", new Class[0]);
            } catch (Exception e) {
                getMethod = null;
                openMethod = null;
                warnIfOpenMethod = null;
            }
            return new CloseGuard(getMethod, openMethod, warnIfOpenMethod);
        }
    }
}

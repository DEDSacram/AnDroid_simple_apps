package androidx.camera.core.impl.utils;

import android.os.Build;
import android.util.CloseGuard;
import androidx.core.util.Preconditions;

public final class CloseGuardHelper {
    private final CloseGuardImpl mImpl;

    private interface CloseGuardImpl {
        void close();

        void open(String str);

        void warnIfOpen();
    }

    private CloseGuardHelper(CloseGuardImpl impl) {
        this.mImpl = impl;
    }

    public static CloseGuardHelper create() {
        if (Build.VERSION.SDK_INT >= 30) {
            return new CloseGuardHelper(new CloseGuardApi30Impl());
        }
        return new CloseGuardHelper(new CloseGuardNoOpImpl());
    }

    public void open(String closeMethodName) {
        this.mImpl.open(closeMethodName);
    }

    public void close() {
        this.mImpl.close();
    }

    public void warnIfOpen() {
        this.mImpl.warnIfOpen();
    }

    static final class CloseGuardApi30Impl implements CloseGuardImpl {
        private final CloseGuard mPlatformImpl = new CloseGuard();

        CloseGuardApi30Impl() {
        }

        public void open(String closeMethodName) {
            this.mPlatformImpl.open(closeMethodName);
        }

        public void close() {
            this.mPlatformImpl.close();
        }

        public void warnIfOpen() {
            this.mPlatformImpl.warnIfOpen();
        }
    }

    static final class CloseGuardNoOpImpl implements CloseGuardImpl {
        CloseGuardNoOpImpl() {
        }

        public void open(String closeMethodName) {
            Preconditions.checkNotNull(closeMethodName, "CloseMethodName must not be null.");
        }

        public void close() {
        }

        public void warnIfOpen() {
        }
    }
}

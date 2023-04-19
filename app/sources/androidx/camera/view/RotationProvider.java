package androidx.camera.view;

import android.content.Context;
import android.view.OrientationEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public final class RotationProvider {
    boolean mIgnoreCanDetectForTest = false;
    final Map<Listener, ListenerWrapper> mListeners = new HashMap();
    final Object mLock = new Object();
    final OrientationEventListener mOrientationListener;

    public interface Listener {
        void onRotationChanged(int i);
    }

    public RotationProvider(Context context) {
        this.mOrientationListener = new OrientationEventListener(context) {
            private static final int INVALID_SURFACE_ROTATION = -1;
            private int mRotation = -1;

            public void onOrientationChanged(int orientation) {
                int newRotation;
                List<ListenerWrapper> listeners;
                if (orientation != -1 && this.mRotation != (newRotation = RotationProvider.orientationToSurfaceRotation(orientation))) {
                    this.mRotation = newRotation;
                    synchronized (RotationProvider.this.mLock) {
                        listeners = new ArrayList<>(RotationProvider.this.mListeners.values());
                    }
                    if (!listeners.isEmpty()) {
                        for (ListenerWrapper listenerWrapper : listeners) {
                            listenerWrapper.onRotationChanged(newRotation);
                        }
                    }
                }
            }
        };
    }

    public boolean addListener(Executor executor, Listener listener) {
        synchronized (this.mLock) {
            if (!this.mOrientationListener.canDetectOrientation() && !this.mIgnoreCanDetectForTest) {
                return false;
            }
            this.mListeners.put(listener, new ListenerWrapper(listener, executor));
            this.mOrientationListener.enable();
            return true;
        }
    }

    public void removeListener(Listener listener) {
        synchronized (this.mLock) {
            ListenerWrapper listenerWrapper = this.mListeners.get(listener);
            if (listenerWrapper != null) {
                listenerWrapper.disable();
                this.mListeners.remove(listener);
            }
            if (this.mListeners.isEmpty()) {
                this.mOrientationListener.disable();
            }
        }
    }

    static int orientationToSurfaceRotation(int orientation) {
        if (orientation >= 315 || orientation < 45) {
            return 0;
        }
        if (orientation >= 225) {
            return 1;
        }
        if (orientation >= 135) {
            return 2;
        }
        return 3;
    }

    private static class ListenerWrapper {
        private final AtomicBoolean mEnabled = new AtomicBoolean(true);
        private final Executor mExecutor;
        private final Listener mListener;

        ListenerWrapper(Listener listener, Executor executor) {
            this.mListener = listener;
            this.mExecutor = executor;
        }

        /* access modifiers changed from: package-private */
        public void onRotationChanged(int rotation) {
            this.mExecutor.execute(new RotationProvider$ListenerWrapper$$ExternalSyntheticLambda0(this, rotation));
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onRotationChanged$0$androidx-camera-view-RotationProvider$ListenerWrapper  reason: not valid java name */
        public /* synthetic */ void m206lambda$onRotationChanged$0$androidxcameraviewRotationProvider$ListenerWrapper(int rotation) {
            if (this.mEnabled.get()) {
                this.mListener.onRotationChanged(rotation);
            }
        }

        /* access modifiers changed from: package-private */
        public void disable() {
            this.mEnabled.set(false);
        }
    }
}

package androidx.camera.core.impl;

import androidx.camera.core.CameraUnavailableException;
import androidx.camera.core.InitializationException;
import androidx.camera.core.Logger;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.util.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class CameraRepository {
    private static final String TAG = "CameraRepository";
    private final Map<String, CameraInternal> mCameras = new LinkedHashMap();
    private final Object mCamerasLock = new Object();
    private CallbackToFutureAdapter.Completer<Void> mDeinitCompleter;
    private ListenableFuture<Void> mDeinitFuture;
    private final Set<CameraInternal> mReleasingCameras = new HashSet();

    public void init(CameraFactory cameraFactory) throws InitializationException {
        synchronized (this.mCamerasLock) {
            try {
                for (String id : cameraFactory.getAvailableCameraIds()) {
                    Logger.d(TAG, "Added camera: " + id);
                    this.mCameras.put(id, cameraFactory.getCamera(id));
                }
            } catch (CameraUnavailableException e) {
                throw new InitializationException((Throwable) e);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0015, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.google.common.util.concurrent.ListenableFuture<java.lang.Void> deinit() {
        /*
            r7 = this;
            java.lang.Object r0 = r7.mCamerasLock
            monitor-enter(r0)
            java.util.Map<java.lang.String, androidx.camera.core.impl.CameraInternal> r1 = r7.mCameras     // Catch:{ all -> 0x005f }
            boolean r1 = r1.isEmpty()     // Catch:{ all -> 0x005f }
            if (r1 == 0) goto L_0x0016
            com.google.common.util.concurrent.ListenableFuture<java.lang.Void> r1 = r7.mDeinitFuture     // Catch:{ all -> 0x005f }
            if (r1 != 0) goto L_0x0014
            r1 = 0
            com.google.common.util.concurrent.ListenableFuture r1 = androidx.camera.core.impl.utils.futures.Futures.immediateFuture(r1)     // Catch:{ all -> 0x005f }
        L_0x0014:
            monitor-exit(r0)     // Catch:{ all -> 0x005f }
            return r1
        L_0x0016:
            com.google.common.util.concurrent.ListenableFuture<java.lang.Void> r1 = r7.mDeinitFuture     // Catch:{ all -> 0x005f }
            if (r1 != 0) goto L_0x0026
            androidx.camera.core.impl.CameraRepository$$ExternalSyntheticLambda0 r2 = new androidx.camera.core.impl.CameraRepository$$ExternalSyntheticLambda0     // Catch:{ all -> 0x005f }
            r2.<init>(r7)     // Catch:{ all -> 0x005f }
            com.google.common.util.concurrent.ListenableFuture r2 = androidx.concurrent.futures.CallbackToFutureAdapter.getFuture(r2)     // Catch:{ all -> 0x005f }
            r1 = r2
            r7.mDeinitFuture = r1     // Catch:{ all -> 0x005f }
        L_0x0026:
            java.util.Set<androidx.camera.core.impl.CameraInternal> r2 = r7.mReleasingCameras     // Catch:{ all -> 0x005f }
            java.util.Map<java.lang.String, androidx.camera.core.impl.CameraInternal> r3 = r7.mCameras     // Catch:{ all -> 0x005f }
            java.util.Collection r3 = r3.values()     // Catch:{ all -> 0x005f }
            r2.addAll(r3)     // Catch:{ all -> 0x005f }
            java.util.Map<java.lang.String, androidx.camera.core.impl.CameraInternal> r2 = r7.mCameras     // Catch:{ all -> 0x005f }
            java.util.Collection r2 = r2.values()     // Catch:{ all -> 0x005f }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ all -> 0x005f }
        L_0x003b:
            boolean r3 = r2.hasNext()     // Catch:{ all -> 0x005f }
            if (r3 == 0) goto L_0x0058
            java.lang.Object r3 = r2.next()     // Catch:{ all -> 0x005f }
            androidx.camera.core.impl.CameraInternal r3 = (androidx.camera.core.impl.CameraInternal) r3     // Catch:{ all -> 0x005f }
            com.google.common.util.concurrent.ListenableFuture r4 = r3.release()     // Catch:{ all -> 0x005f }
            androidx.camera.core.impl.CameraRepository$$ExternalSyntheticLambda1 r5 = new androidx.camera.core.impl.CameraRepository$$ExternalSyntheticLambda1     // Catch:{ all -> 0x005f }
            r5.<init>(r7, r3)     // Catch:{ all -> 0x005f }
            java.util.concurrent.Executor r6 = androidx.camera.core.impl.utils.executor.CameraXExecutors.directExecutor()     // Catch:{ all -> 0x005f }
            r4.addListener(r5, r6)     // Catch:{ all -> 0x005f }
            goto L_0x003b
        L_0x0058:
            java.util.Map<java.lang.String, androidx.camera.core.impl.CameraInternal> r2 = r7.mCameras     // Catch:{ all -> 0x005f }
            r2.clear()     // Catch:{ all -> 0x005f }
            monitor-exit(r0)     // Catch:{ all -> 0x005f }
            return r1
        L_0x005f:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x005f }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.camera.core.impl.CameraRepository.deinit():com.google.common.util.concurrent.ListenableFuture");
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$deinit$0$androidx-camera-core-impl-CameraRepository  reason: not valid java name */
    public /* synthetic */ Object m169lambda$deinit$0$androidxcameracoreimplCameraRepository(CallbackToFutureAdapter.Completer completer) throws Exception {
        synchronized (this.mCamerasLock) {
            this.mDeinitCompleter = completer;
        }
        return "CameraRepository-deinit";
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$deinit$1$androidx-camera-core-impl-CameraRepository  reason: not valid java name */
    public /* synthetic */ void m170lambda$deinit$1$androidxcameracoreimplCameraRepository(CameraInternal cameraInternal) {
        synchronized (this.mCamerasLock) {
            this.mReleasingCameras.remove(cameraInternal);
            if (this.mReleasingCameras.isEmpty()) {
                Preconditions.checkNotNull(this.mDeinitCompleter);
                this.mDeinitCompleter.set(null);
                this.mDeinitCompleter = null;
                this.mDeinitFuture = null;
            }
        }
    }

    public CameraInternal getCamera(String cameraId) {
        CameraInternal cameraInternal;
        synchronized (this.mCamerasLock) {
            cameraInternal = this.mCameras.get(cameraId);
            if (cameraInternal == null) {
                throw new IllegalArgumentException("Invalid camera: " + cameraId);
            }
        }
        return cameraInternal;
    }

    public LinkedHashSet<CameraInternal> getCameras() {
        LinkedHashSet<CameraInternal> linkedHashSet;
        synchronized (this.mCamerasLock) {
            linkedHashSet = new LinkedHashSet<>(this.mCameras.values());
        }
        return linkedHashSet;
    }

    /* access modifiers changed from: package-private */
    public Set<String> getCameraIds() {
        LinkedHashSet linkedHashSet;
        synchronized (this.mCamerasLock) {
            linkedHashSet = new LinkedHashSet(this.mCameras.keySet());
        }
        return linkedHashSet;
    }
}

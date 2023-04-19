package androidx.camera.camera2.internal;

import androidx.camera.core.ImageProxy;
import androidx.camera.core.internal.utils.RingBuffer;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class ZslControlImpl$$ExternalSyntheticLambda1 implements RingBuffer.OnRemoveCallback {
    public static final /* synthetic */ ZslControlImpl$$ExternalSyntheticLambda1 INSTANCE = new ZslControlImpl$$ExternalSyntheticLambda1();

    private /* synthetic */ ZslControlImpl$$ExternalSyntheticLambda1() {
    }

    public final void onRemove(Object obj) {
        ((ImageProxy) obj).close();
    }
}

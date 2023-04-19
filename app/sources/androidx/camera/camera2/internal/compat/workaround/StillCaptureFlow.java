package androidx.camera.camera2.internal.compat.workaround;

import androidx.camera.camera2.internal.compat.quirk.DeviceQuirks;
import androidx.camera.camera2.internal.compat.quirk.StillCaptureFlashStopRepeatingQuirk;

public class StillCaptureFlow {
    private final boolean mShouldStopRepeatingBeforeStillCapture;

    public StillCaptureFlow() {
        this.mShouldStopRepeatingBeforeStillCapture = ((StillCaptureFlashStopRepeatingQuirk) DeviceQuirks.get(StillCaptureFlashStopRepeatingQuirk.class)) != null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:6:0x0012  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean shouldStopRepeatingBeforeCapture(java.util.List<android.hardware.camera2.CaptureRequest> r6, boolean r7) {
        /*
            r5 = this;
            boolean r0 = r5.mShouldStopRepeatingBeforeStillCapture
            r1 = 0
            if (r0 == 0) goto L_0x002f
            if (r7 != 0) goto L_0x0008
            goto L_0x002f
        L_0x0008:
            java.util.Iterator r0 = r6.iterator()
        L_0x000c:
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L_0x002e
            java.lang.Object r2 = r0.next()
            android.hardware.camera2.CaptureRequest r2 = (android.hardware.camera2.CaptureRequest) r2
            android.hardware.camera2.CaptureRequest$Key r3 = android.hardware.camera2.CaptureRequest.CONTROL_AE_MODE
            java.lang.Object r3 = r2.get(r3)
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            r4 = 2
            if (r3 == r4) goto L_0x002c
            r4 = 3
            if (r3 != r4) goto L_0x002b
            goto L_0x002c
        L_0x002b:
            goto L_0x000c
        L_0x002c:
            r0 = 1
            return r0
        L_0x002e:
            return r1
        L_0x002f:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.camera.camera2.internal.compat.workaround.StillCaptureFlow.shouldStopRepeatingBeforeCapture(java.util.List, boolean):boolean");
    }
}

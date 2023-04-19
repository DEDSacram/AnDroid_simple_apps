package androidx.camera.camera2.internal.compat.workaround;

import androidx.camera.camera2.internal.SynchronizedCaptureSession;
import androidx.camera.camera2.internal.compat.quirk.CaptureSessionOnClosedNotCalledQuirk;
import androidx.camera.core.impl.Quirks;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ForceCloseCaptureSession {
    private final CaptureSessionOnClosedNotCalledQuirk mCaptureSessionOnClosedNotCalledQuirk;

    @FunctionalInterface
    public interface OnConfigured {
        void run(SynchronizedCaptureSession synchronizedCaptureSession);
    }

    public ForceCloseCaptureSession(Quirks deviceQuirks) {
        this.mCaptureSessionOnClosedNotCalledQuirk = (CaptureSessionOnClosedNotCalledQuirk) deviceQuirks.get(CaptureSessionOnClosedNotCalledQuirk.class);
    }

    public boolean shouldForceClose() {
        return this.mCaptureSessionOnClosedNotCalledQuirk != null;
    }

    public void onSessionConfigured(SynchronizedCaptureSession session, List<SynchronizedCaptureSession> creatingSessions, List<SynchronizedCaptureSession> sessions, OnConfigured onConfigured) {
        SynchronizedCaptureSession s;
        SynchronizedCaptureSession s2;
        if (shouldForceClose()) {
            Set<SynchronizedCaptureSession> staleCreatingSessions = new LinkedHashSet<>();
            Iterator<SynchronizedCaptureSession> it = creatingSessions.iterator();
            while (it.hasNext() && (s2 = it.next()) != session) {
                staleCreatingSessions.add(s2);
            }
            forceOnConfigureFailed(staleCreatingSessions);
        }
        onConfigured.run(session);
        if (shouldForceClose()) {
            Set<SynchronizedCaptureSession> openedSessions = new LinkedHashSet<>();
            Iterator<SynchronizedCaptureSession> it2 = sessions.iterator();
            while (it2.hasNext() && (s = it2.next()) != session) {
                openedSessions.add(s);
            }
            forceOnClosed(openedSessions);
        }
    }

    private void forceOnConfigureFailed(Set<SynchronizedCaptureSession> sessions) {
        for (SynchronizedCaptureSession session : sessions) {
            session.getStateCallback().onConfigureFailed(session);
        }
    }

    private void forceOnClosed(Set<SynchronizedCaptureSession> sessions) {
        for (SynchronizedCaptureSession session : sessions) {
            session.getStateCallback().onClosed(session);
        }
    }
}

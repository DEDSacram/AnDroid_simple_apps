package okio;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.Unit;

@Metadata(d1 = {"\u0000%\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\b\u0010\u0004\u001a\u00020\u0005H\u0016J\b\u0010\u0006\u001a\u00020\u0005H\u0016J\b\u0010\u0002\u001a\u00020\u0003H\u0016J\u0018\u0010\u0007\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000¨\u0006\f"}, d2 = {"okio/Pipe$sink$1", "Lokio/Sink;", "timeout", "Lokio/Timeout;", "close", "", "flush", "write", "source", "Lokio/Buffer;", "byteCount", "", "okio"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* compiled from: Pipe.kt */
public final class Pipe$sink$1 implements Sink {
    final /* synthetic */ Pipe this$0;
    private final Timeout timeout = new Timeout();

    Pipe$sink$1(Pipe $receiver) {
        this.this$0 = $receiver;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:31:0x008b, code lost:
        if (r3 != null) goto L_0x0091;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x008d, code lost:
        r16 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0091, code lost:
        r4 = r1.this$0;
        r5 = r3;
        r9 = r5.timeout();
        r10 = r4.sink().timeout();
        r12 = r9.timeoutNanos();
        r16 = r3;
        r17 = r4;
        r9.timeout(okio.Timeout.Companion.minTimeout(r10.timeoutNanos(), r9.timeoutNanos()), java.util.concurrent.TimeUnit.NANOSECONDS);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00c3, code lost:
        if (r9.hasDeadline() == false) goto L_0x0106;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00c5, code lost:
        r3 = r9.deadlineNanoTime();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00cd, code lost:
        if (r10.hasDeadline() == false) goto L_0x00de;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00cf, code lost:
        r9.deadlineNanoTime(java.lang.Math.min(r9.deadlineNanoTime(), r10.deadlineNanoTime()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:?, code lost:
        r5.write(r2, r7);
        r0 = kotlin.Unit.INSTANCE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00f6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00f7, code lost:
        r9.timeout(r12, java.util.concurrent.TimeUnit.NANOSECONDS);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0100, code lost:
        if (r10.hasDeadline() != false) goto L_0x0102;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x0102, code lost:
        r9.deadlineNanoTime(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0105, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x010a, code lost:
        if (r10.hasDeadline() == false) goto L_0x0113;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x010c, code lost:
        r9.deadlineNanoTime(r10.deadlineNanoTime());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:?, code lost:
        r5.write(r2, r7);
        r0 = kotlin.Unit.INSTANCE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x012d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x012e, code lost:
        r9.timeout(r12, java.util.concurrent.TimeUnit.NANOSECONDS);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x0137, code lost:
        if (r10.hasDeadline() != false) goto L_0x0139;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0139, code lost:
        r9.clearDeadline();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x013c, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void write(okio.Buffer r19, long r20) {
        /*
            r18 = this;
            r1 = r18
            r2 = r19
            java.lang.String r0 = "source"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r2, r0)
            r3 = 0
            okio.Pipe r0 = r1.this$0
            okio.Buffer r4 = r0.getBuffer$okio()
            okio.Pipe r0 = r1.this$0
            r5 = 0
            monitor-enter(r4)
            r6 = 0
            boolean r7 = r0.getSinkClosed$okio()     // Catch:{ all -> 0x0158 }
            r7 = r7 ^ 1
            if (r7 == 0) goto L_0x0149
            boolean r7 = r0.getCanceled$okio()     // Catch:{ all -> 0x0158 }
            if (r7 != 0) goto L_0x0141
            r7 = r20
        L_0x0026:
            r9 = 0
            int r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r11 <= 0) goto L_0x0086
            okio.Sink r11 = r0.getFoldedSink$okio()     // Catch:{ all -> 0x0083 }
            if (r11 != 0) goto L_0x007f
            boolean r11 = r0.getSourceClosed$okio()     // Catch:{ all -> 0x0083 }
            if (r11 != 0) goto L_0x0077
            long r11 = r0.getMaxBufferSize$okio()     // Catch:{ all -> 0x0083 }
            okio.Buffer r13 = r0.getBuffer$okio()     // Catch:{ all -> 0x0083 }
            long r13 = r13.size()     // Catch:{ all -> 0x0083 }
            long r11 = r11 - r13
            int r9 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r9 != 0) goto L_0x0061
            okio.Timeout r9 = r1.timeout     // Catch:{ all -> 0x0083 }
            okio.Buffer r10 = r0.getBuffer$okio()     // Catch:{ all -> 0x0083 }
            r9.waitUntilNotified(r10)     // Catch:{ all -> 0x0083 }
            boolean r9 = r0.getCanceled$okio()     // Catch:{ all -> 0x0083 }
            if (r9 != 0) goto L_0x0059
            goto L_0x0026
        L_0x0059:
            java.io.IOException r0 = new java.io.IOException     // Catch:{ all -> 0x0083 }
            java.lang.String r9 = "canceled"
            r0.<init>(r9)     // Catch:{ all -> 0x0083 }
            throw r0     // Catch:{ all -> 0x0083 }
        L_0x0061:
            long r9 = java.lang.Math.min(r11, r7)     // Catch:{ all -> 0x0083 }
            okio.Buffer r13 = r0.getBuffer$okio()     // Catch:{ all -> 0x0083 }
            r13.write((okio.Buffer) r2, (long) r9)     // Catch:{ all -> 0x0083 }
            long r7 = r7 - r9
            okio.Buffer r13 = r0.getBuffer$okio()     // Catch:{ all -> 0x0083 }
            java.lang.Object r13 = (java.lang.Object) r13     // Catch:{ all -> 0x0083 }
            r13.notifyAll()     // Catch:{ all -> 0x0083 }
            goto L_0x0026
        L_0x0077:
            java.io.IOException r0 = new java.io.IOException     // Catch:{ all -> 0x0083 }
            java.lang.String r9 = "source is closed"
            r0.<init>(r9)     // Catch:{ all -> 0x0083 }
            throw r0     // Catch:{ all -> 0x0083 }
        L_0x007f:
            r0 = r11
            r9 = 0
            r3 = r0
            goto L_0x0087
        L_0x0083:
            r0 = move-exception
            goto L_0x015b
        L_0x0086:
        L_0x0087:
            kotlin.Unit r0 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x013d }
            monitor-exit(r4)
            if (r3 != 0) goto L_0x0091
            r16 = r3
            goto L_0x012c
        L_0x0091:
            okio.Pipe r4 = r1.this$0
            r0 = r3
            r5 = r0
            r6 = 0
            okio.Timeout r9 = r5.timeout()
            okio.Sink r0 = r4.sink()
            okio.Timeout r0 = r0.timeout()
            r10 = r0
            r11 = 0
            long r12 = r9.timeoutNanos()
            okio.Timeout$Companion r0 = okio.Timeout.Companion
            long r14 = r10.timeoutNanos()
            r16 = r3
            r17 = r4
            long r3 = r9.timeoutNanos()
            long r3 = r0.minTimeout(r14, r3)
            java.util.concurrent.TimeUnit r0 = java.util.concurrent.TimeUnit.NANOSECONDS
            r9.timeout(r3, r0)
            boolean r0 = r9.hasDeadline()
            if (r0 == 0) goto L_0x0106
            long r3 = r9.deadlineNanoTime()
            boolean r0 = r10.hasDeadline()
            if (r0 == 0) goto L_0x00de
            long r14 = r9.deadlineNanoTime()
            long r0 = r10.deadlineNanoTime()
            long r0 = java.lang.Math.min(r14, r0)
            r9.deadlineNanoTime(r0)
        L_0x00de:
            r0 = 0
            r1 = r5
            r14 = 0
            r1.write(r2, r7)     // Catch:{ all -> 0x00f6 }
            kotlin.Unit r0 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x00f6 }
            java.util.concurrent.TimeUnit r0 = java.util.concurrent.TimeUnit.NANOSECONDS
            r9.timeout(r12, r0)
            boolean r0 = r10.hasDeadline()
            if (r0 == 0) goto L_0x00f5
            r9.deadlineNanoTime(r3)
        L_0x00f5:
            goto L_0x012b
        L_0x00f6:
            r0 = move-exception
            java.util.concurrent.TimeUnit r1 = java.util.concurrent.TimeUnit.NANOSECONDS
            r9.timeout(r12, r1)
            boolean r1 = r10.hasDeadline()
            if (r1 == 0) goto L_0x0105
            r9.deadlineNanoTime(r3)
        L_0x0105:
            throw r0
        L_0x0106:
            boolean r0 = r10.hasDeadline()
            if (r0 == 0) goto L_0x0113
            long r0 = r10.deadlineNanoTime()
            r9.deadlineNanoTime(r0)
        L_0x0113:
            r0 = 0
            r1 = r5
            r3 = 0
            r1.write(r2, r7)     // Catch:{ all -> 0x012d }
            kotlin.Unit r0 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x012d }
            java.util.concurrent.TimeUnit r0 = java.util.concurrent.TimeUnit.NANOSECONDS
            r9.timeout(r12, r0)
            boolean r0 = r10.hasDeadline()
            if (r0 == 0) goto L_0x012a
            r9.clearDeadline()
        L_0x012a:
        L_0x012b:
        L_0x012c:
            return
        L_0x012d:
            r0 = move-exception
            java.util.concurrent.TimeUnit r1 = java.util.concurrent.TimeUnit.NANOSECONDS
            r9.timeout(r12, r1)
            boolean r1 = r10.hasDeadline()
            if (r1 == 0) goto L_0x013c
            r9.clearDeadline()
        L_0x013c:
            throw r0
        L_0x013d:
            r0 = move-exception
            r16 = r3
            goto L_0x015b
        L_0x0141:
            java.io.IOException r0 = new java.io.IOException     // Catch:{ all -> 0x0158 }
            java.lang.String r1 = "canceled"
            r0.<init>(r1)     // Catch:{ all -> 0x0158 }
            throw r0     // Catch:{ all -> 0x0158 }
        L_0x0149:
            r0 = 0
            java.lang.String r1 = "closed"
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException     // Catch:{ all -> 0x0158 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0158 }
            r0.<init>(r1)     // Catch:{ all -> 0x0158 }
            java.lang.Throwable r0 = (java.lang.Throwable) r0     // Catch:{ all -> 0x0158 }
            throw r0     // Catch:{ all -> 0x0158 }
        L_0x0158:
            r0 = move-exception
            r7 = r20
        L_0x015b:
            monitor-exit(r4)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Pipe$sink$1.write(okio.Buffer, long):void");
    }

    public void flush() {
        Sink sink = null;
        Object lock$iv = this.this$0.getBuffer$okio();
        Pipe pipe = this.this$0;
        synchronized (lock$iv) {
            if (!(!pipe.getSinkClosed$okio())) {
                throw new IllegalStateException("closed".toString());
            } else if (!pipe.getCanceled$okio()) {
                Sink it = pipe.getFoldedSink$okio();
                if (it != null) {
                    sink = it;
                } else if (pipe.getSourceClosed$okio()) {
                    if (pipe.getBuffer$okio().size() > 0) {
                        throw new IOException("source is closed");
                    }
                }
                Unit unit = Unit.INSTANCE;
            } else {
                throw new IOException("canceled");
            }
        }
        if (sink != null) {
            Pipe this_$iv = this.this$0;
            Sink $this$forward$iv = sink;
            Timeout this_$iv$iv = $this$forward$iv.timeout();
            Timeout other$iv$iv = this_$iv.sink().timeout();
            long originalTimeout$iv$iv = this_$iv$iv.timeoutNanos();
            this_$iv$iv.timeout(Timeout.Companion.minTimeout(other$iv$iv.timeoutNanos(), this_$iv$iv.timeoutNanos()), TimeUnit.NANOSECONDS);
            if (this_$iv$iv.hasDeadline()) {
                long originalDeadline$iv$iv = this_$iv$iv.deadlineNanoTime();
                if (other$iv$iv.hasDeadline()) {
                    this_$iv$iv.deadlineNanoTime(Math.min(this_$iv$iv.deadlineNanoTime(), other$iv$iv.deadlineNanoTime()));
                }
                try {
                    $this$forward$iv.flush();
                    Unit unit2 = Unit.INSTANCE;
                } finally {
                    this_$iv$iv.timeout(originalTimeout$iv$iv, TimeUnit.NANOSECONDS);
                    if (other$iv$iv.hasDeadline()) {
                        this_$iv$iv.deadlineNanoTime(originalDeadline$iv$iv);
                    }
                }
            } else {
                if (other$iv$iv.hasDeadline()) {
                    this_$iv$iv.deadlineNanoTime(other$iv$iv.deadlineNanoTime());
                }
                try {
                    $this$forward$iv.flush();
                    Unit unit3 = Unit.INSTANCE;
                } finally {
                    this_$iv$iv.timeout(originalTimeout$iv$iv, TimeUnit.NANOSECONDS);
                    if (other$iv$iv.hasDeadline()) {
                        this_$iv$iv.clearDeadline();
                    }
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x004d, code lost:
        if (r0 != null) goto L_0x0051;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0051, code lost:
        r1 = r15.this$0;
        r2 = r0;
        r4 = r2.timeout();
        r5 = r1.sink().timeout();
        r7 = r4.timeoutNanos();
        r4.timeout(okio.Timeout.Companion.minTimeout(r5.timeoutNanos(), r4.timeoutNanos()), java.util.concurrent.TimeUnit.NANOSECONDS);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x007d, code lost:
        if (r4.hasDeadline() == false) goto L_0x00c0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x007f, code lost:
        r9 = r4.deadlineNanoTime();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0087, code lost:
        if (r5.hasDeadline() == false) goto L_0x0098;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0089, code lost:
        r4.deadlineNanoTime(java.lang.Math.min(r4.deadlineNanoTime(), r5.deadlineNanoTime()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
        r2.close();
        r11 = kotlin.Unit.INSTANCE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00b0, code lost:
        r11 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00b1, code lost:
        r4.timeout(r7, java.util.concurrent.TimeUnit.NANOSECONDS);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00ba, code lost:
        if (r5.hasDeadline() != false) goto L_0x00bc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00bc, code lost:
        r4.deadlineNanoTime(r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00bf, code lost:
        throw r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00c4, code lost:
        if (r5.hasDeadline() == false) goto L_0x00cd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00c6, code lost:
        r4.deadlineNanoTime(r5.deadlineNanoTime());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:?, code lost:
        r2.close();
        r9 = kotlin.Unit.INSTANCE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00e7, code lost:
        r9 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00e8, code lost:
        r4.timeout(r7, java.util.concurrent.TimeUnit.NANOSECONDS);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00f1, code lost:
        if (r5.hasDeadline() != false) goto L_0x00f3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00f3, code lost:
        r4.clearDeadline();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00f6, code lost:
        throw r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void close() {
        /*
            r15 = this;
            r0 = 0
            okio.Pipe r1 = r15.this$0
            okio.Buffer r1 = r1.getBuffer$okio()
            okio.Pipe r2 = r15.this$0
            r3 = 0
            monitor-enter(r1)
            r4 = 0
            boolean r5 = r2.getSinkClosed$okio()     // Catch:{ all -> 0x00f7 }
            if (r5 == 0) goto L_0x0014
            monitor-exit(r1)
            return
        L_0x0014:
            okio.Sink r5 = r2.getFoldedSink$okio()     // Catch:{ all -> 0x00f7 }
            if (r5 != 0) goto L_0x0045
            boolean r5 = r2.getSourceClosed$okio()     // Catch:{ all -> 0x00f7 }
            if (r5 == 0) goto L_0x0037
            okio.Buffer r5 = r2.getBuffer$okio()     // Catch:{ all -> 0x00f7 }
            long r5 = r5.size()     // Catch:{ all -> 0x00f7 }
            r7 = 0
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 > 0) goto L_0x002f
            goto L_0x0037
        L_0x002f:
            java.io.IOException r2 = new java.io.IOException     // Catch:{ all -> 0x00f7 }
            java.lang.String r5 = "source is closed"
            r2.<init>(r5)     // Catch:{ all -> 0x00f7 }
            throw r2     // Catch:{ all -> 0x00f7 }
        L_0x0037:
            r5 = 1
            r2.setSinkClosed$okio(r5)     // Catch:{ all -> 0x00f7 }
            okio.Buffer r2 = r2.getBuffer$okio()     // Catch:{ all -> 0x00f7 }
            java.lang.Object r2 = (java.lang.Object) r2     // Catch:{ all -> 0x00f7 }
            r2.notifyAll()     // Catch:{ all -> 0x00f7 }
            goto L_0x0049
        L_0x0045:
            r2 = r5
            r5 = 0
            r0 = r2
        L_0x0049:
            kotlin.Unit r2 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x00f7 }
            monitor-exit(r1)
            if (r0 != 0) goto L_0x0051
            goto L_0x00e6
        L_0x0051:
            okio.Pipe r1 = r15.this$0
            r2 = r0
            r3 = 0
            okio.Timeout r4 = r2.timeout()
            okio.Sink r5 = r1.sink()
            okio.Timeout r5 = r5.timeout()
            r6 = 0
            long r7 = r4.timeoutNanos()
            okio.Timeout$Companion r9 = okio.Timeout.Companion
            long r10 = r5.timeoutNanos()
            long r12 = r4.timeoutNanos()
            long r9 = r9.minTimeout(r10, r12)
            java.util.concurrent.TimeUnit r11 = java.util.concurrent.TimeUnit.NANOSECONDS
            r4.timeout(r9, r11)
            boolean r9 = r4.hasDeadline()
            if (r9 == 0) goto L_0x00c0
            long r9 = r4.deadlineNanoTime()
            boolean r11 = r5.hasDeadline()
            if (r11 == 0) goto L_0x0098
            long r11 = r4.deadlineNanoTime()
            long r13 = r5.deadlineNanoTime()
            long r11 = java.lang.Math.min(r11, r13)
            r4.deadlineNanoTime(r11)
        L_0x0098:
            r11 = 0
            r12 = r2
            r13 = 0
            r12.close()     // Catch:{ all -> 0x00b0 }
            kotlin.Unit r11 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x00b0 }
            java.util.concurrent.TimeUnit r11 = java.util.concurrent.TimeUnit.NANOSECONDS
            r4.timeout(r7, r11)
            boolean r11 = r5.hasDeadline()
            if (r11 == 0) goto L_0x00af
            r4.deadlineNanoTime(r9)
        L_0x00af:
            goto L_0x00e5
        L_0x00b0:
            r11 = move-exception
            java.util.concurrent.TimeUnit r12 = java.util.concurrent.TimeUnit.NANOSECONDS
            r4.timeout(r7, r12)
            boolean r12 = r5.hasDeadline()
            if (r12 == 0) goto L_0x00bf
            r4.deadlineNanoTime(r9)
        L_0x00bf:
            throw r11
        L_0x00c0:
            boolean r9 = r5.hasDeadline()
            if (r9 == 0) goto L_0x00cd
            long r9 = r5.deadlineNanoTime()
            r4.deadlineNanoTime(r9)
        L_0x00cd:
            r9 = 0
            r10 = r2
            r11 = 0
            r10.close()     // Catch:{ all -> 0x00e7 }
            kotlin.Unit r9 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x00e7 }
            java.util.concurrent.TimeUnit r9 = java.util.concurrent.TimeUnit.NANOSECONDS
            r4.timeout(r7, r9)
            boolean r9 = r5.hasDeadline()
            if (r9 == 0) goto L_0x00e4
            r4.clearDeadline()
        L_0x00e4:
        L_0x00e5:
        L_0x00e6:
            return
        L_0x00e7:
            r9 = move-exception
            java.util.concurrent.TimeUnit r10 = java.util.concurrent.TimeUnit.NANOSECONDS
            r4.timeout(r7, r10)
            boolean r10 = r5.hasDeadline()
            if (r10 == 0) goto L_0x00f6
            r4.clearDeadline()
        L_0x00f6:
            throw r9
        L_0x00f7:
            r2 = move-exception
            monitor-exit(r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Pipe$sink$1.close():void");
    }

    public Timeout timeout() {
        return this.timeout;
    }
}

package okio;

import java.io.IOException;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000%\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\b\u0010\u0004\u001a\u00020\u0005H\u0016J\u0018\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0007H\u0016J\b\u0010\u0002\u001a\u00020\u0003H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000¨\u0006\u000b"}, d2 = {"okio/Pipe$source$1", "Lokio/Source;", "timeout", "Lokio/Timeout;", "close", "", "read", "", "sink", "Lokio/Buffer;", "byteCount", "okio"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* compiled from: Pipe.kt */
public final class Pipe$source$1 implements Source {
    final /* synthetic */ Pipe this$0;
    private final Timeout timeout = new Timeout();

    Pipe$source$1(Pipe $receiver) {
        this.this$0 = $receiver;
    }

    public long read(Buffer sink, long byteCount) {
        Intrinsics.checkNotNullParameter(sink, "sink");
        Object lock$iv = this.this$0.getBuffer$okio();
        Pipe pipe = this.this$0;
        synchronized (lock$iv) {
            if (!(!pipe.getSourceClosed$okio())) {
                throw new IllegalStateException("closed".toString());
            } else if (!pipe.getCanceled$okio()) {
                while (pipe.getBuffer$okio().size() == 0) {
                    if (pipe.getSinkClosed$okio()) {
                        return -1;
                    }
                    this.timeout.waitUntilNotified(pipe.getBuffer$okio());
                    if (pipe.getCanceled$okio()) {
                        throw new IOException("canceled");
                    }
                }
                long read = pipe.getBuffer$okio().read(sink, byteCount);
                pipe.getBuffer$okio().notifyAll();
                return read;
            } else {
                throw new IOException("canceled");
            }
        }
    }

    public void close() {
        Object lock$iv = this.this$0.getBuffer$okio();
        Pipe pipe = this.this$0;
        synchronized (lock$iv) {
            pipe.setSourceClosed$okio(true);
            pipe.getBuffer$okio().notifyAll();
            Unit unit = Unit.INSTANCE;
        }
    }

    public Timeout timeout() {
        return this.timeout;
    }
}

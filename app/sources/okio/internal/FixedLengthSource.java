package okio.internal;

import java.io.IOException;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okio.Buffer;
import okio.ForwardingSource;
import okio.Source;

@Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0000\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ\u0018\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u0005H\u0016J\u0014\u0010\u000e\u001a\u00020\u000f*\u00020\f2\u0006\u0010\u0010\u001a\u00020\u0005H\u0002R\u000e\u0010\t\u001a\u00020\u0005X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0004¢\u0006\u0002\n\u0000¨\u0006\u0011"}, d2 = {"Lokio/internal/FixedLengthSource;", "Lokio/ForwardingSource;", "delegate", "Lokio/Source;", "size", "", "truncate", "", "(Lokio/Source;JZ)V", "bytesReceived", "read", "sink", "Lokio/Buffer;", "byteCount", "truncateToSize", "", "newSize", "okio"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* compiled from: FixedLengthSource.kt */
public final class FixedLengthSource extends ForwardingSource {
    private long bytesReceived;
    private final long size;
    private final boolean truncate;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public FixedLengthSource(Source delegate, long size2, boolean truncate2) {
        super(delegate);
        Intrinsics.checkNotNullParameter(delegate, "delegate");
        this.size = size2;
        this.truncate = truncate2;
    }

    public long read(Buffer sink, long byteCount) {
        long remaining;
        Buffer buffer = sink;
        Intrinsics.checkNotNullParameter(buffer, "sink");
        long j = this.bytesReceived;
        long j2 = this.size;
        if (j > j2) {
            long j3 = byteCount;
            remaining = 0;
        } else if (this.truncate) {
            long remaining2 = j2 - j;
            if (remaining2 == 0) {
                return -1;
            }
            remaining = Math.min(byteCount, remaining2);
        } else {
            remaining = byteCount;
        }
        long result = super.read(buffer, remaining);
        if (result != -1) {
            this.bytesReceived += result;
        }
        long j4 = this.bytesReceived;
        long j5 = this.size;
        if ((j4 >= j5 || result != -1) && j4 <= j5) {
            return result;
        }
        if (result > 0 && j4 > j5) {
            truncateToSize(buffer, sink.size() - (this.bytesReceived - this.size));
        }
        throw new IOException("expected " + this.size + " bytes but got " + this.bytesReceived);
    }

    private final void truncateToSize(Buffer $this$truncateToSize, long newSize) {
        Buffer scratch = new Buffer();
        scratch.writeAll($this$truncateToSize);
        $this$truncateToSize.write(scratch, newSize);
        scratch.clear();
    }
}

package okio;

import java.io.OutputStream;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000)\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H\u0016J\b\u0010\u0004\u001a\u00020\u0003H\u0016J\b\u0010\u0005\u001a\u00020\u0006H\u0016J \u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u0016J\u0010\u0010\u0007\u001a\u00020\u00032\u0006\u0010\r\u001a\u00020\u000bH\u0016¨\u0006\u000e"}, d2 = {"okio/Buffer$outputStream$1", "Ljava/io/OutputStream;", "close", "", "flush", "toString", "", "write", "data", "", "offset", "", "byteCount", "b", "okio"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* compiled from: Buffer.kt */
public final class Buffer$outputStream$1 extends OutputStream {
    final /* synthetic */ Buffer this$0;

    Buffer$outputStream$1(Buffer $receiver) {
        this.this$0 = $receiver;
    }

    public void write(int b) {
        this.this$0.writeByte(b);
    }

    public void write(byte[] data, int offset, int byteCount) {
        Intrinsics.checkNotNullParameter(data, "data");
        this.this$0.write(data, offset, byteCount);
    }

    public void flush() {
    }

    public void close() {
    }

    public String toString() {
        return this.this$0 + ".outputStream()";
    }
}

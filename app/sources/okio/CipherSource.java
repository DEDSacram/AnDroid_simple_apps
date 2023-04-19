package okio;

import java.io.IOException;
import javax.crypto.Cipher;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\b\u0010\u0010\u001a\u00020\u0011H\u0016J\b\u0010\u0012\u001a\u00020\u0011H\u0002J\u0018\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\n2\u0006\u0010\u0016\u001a\u00020\u0014H\u0016J\b\u0010\u0017\u001a\u00020\u0011H\u0002J\b\u0010\u0018\u001a\u00020\u0019H\u0016J\b\u0010\u001a\u001a\u00020\u0011H\u0002R\u000e\u0010\u0007\u001a\u00020\bX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u000e\u0010\r\u001a\u00020\u000eX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u000eX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000¨\u0006\u001b"}, d2 = {"Lokio/CipherSource;", "Lokio/Source;", "source", "Lokio/BufferedSource;", "cipher", "Ljavax/crypto/Cipher;", "(Lokio/BufferedSource;Ljavax/crypto/Cipher;)V", "blockSize", "", "buffer", "Lokio/Buffer;", "getCipher", "()Ljavax/crypto/Cipher;", "closed", "", "final", "close", "", "doFinal", "read", "", "sink", "byteCount", "refill", "timeout", "Lokio/Timeout;", "update", "okio"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* compiled from: CipherSource.kt */
public final class CipherSource implements Source {
    private final int blockSize;
    private final Buffer buffer = new Buffer();
    private final Cipher cipher;
    private boolean closed;

    /* renamed from: final  reason: not valid java name */
    private boolean f0final;
    private final BufferedSource source;

    public CipherSource(BufferedSource source2, Cipher cipher2) {
        Intrinsics.checkNotNullParameter(source2, "source");
        Intrinsics.checkNotNullParameter(cipher2, "cipher");
        this.source = source2;
        this.cipher = cipher2;
        int blockSize2 = cipher2.getBlockSize();
        this.blockSize = blockSize2;
        if (!(blockSize2 > 0)) {
            throw new IllegalArgumentException(Intrinsics.stringPlus("Block cipher required ", getCipher()).toString());
        }
    }

    public final Cipher getCipher() {
        return this.cipher;
    }

    public long read(Buffer sink, long byteCount) throws IOException {
        Intrinsics.checkNotNullParameter(sink, "sink");
        if (!(byteCount >= 0)) {
            throw new IllegalArgumentException(Intrinsics.stringPlus("byteCount < 0: ", Long.valueOf(byteCount)).toString());
        } else if (!(!this.closed)) {
            throw new IllegalStateException("closed".toString());
        } else if (byteCount == 0) {
            return 0;
        } else {
            if (this.f0final) {
                return this.buffer.read(sink, byteCount);
            }
            refill();
            return this.buffer.read(sink, byteCount);
        }
    }

    private final void refill() {
        while (this.buffer.size() == 0) {
            if (this.source.exhausted()) {
                this.f0final = true;
                doFinal();
                return;
            }
            update();
        }
    }

    private final void update() {
        Segment head = this.source.getBuffer().head;
        Intrinsics.checkNotNull(head);
        int size = head.limit - head.pos;
        int outputSize = this.cipher.getOutputSize(size);
        int size2 = size;
        while (outputSize > 8192) {
            int i = this.blockSize;
            if (size2 > i) {
                size2 -= i;
                outputSize = this.cipher.getOutputSize(size2);
            } else {
                throw new IllegalStateException(("Unexpected output size " + outputSize + " for input size " + size2).toString());
            }
        }
        Segment s = this.buffer.writableSegment$okio(outputSize);
        int ciphered = this.cipher.update(head.data, head.pos, size2, s.data, s.pos);
        this.source.skip((long) size2);
        s.limit += ciphered;
        Buffer buffer2 = this.buffer;
        buffer2.setSize$okio(buffer2.size() + ((long) ciphered));
        if (s.pos == s.limit) {
            this.buffer.head = s.pop();
            SegmentPool.recycle(s);
        }
    }

    private final void doFinal() {
        int outputSize = this.cipher.getOutputSize(0);
        if (outputSize != 0) {
            Segment s = this.buffer.writableSegment$okio(outputSize);
            int ciphered = this.cipher.doFinal(s.data, s.pos);
            s.limit += ciphered;
            Buffer buffer2 = this.buffer;
            buffer2.setSize$okio(buffer2.size() + ((long) ciphered));
            if (s.pos == s.limit) {
                this.buffer.head = s.pop();
                SegmentPool.recycle(s);
            }
        }
    }

    public Timeout timeout() {
        return this.source.timeout();
    }

    public void close() throws IOException {
        this.closed = true;
        this.source.close();
    }
}

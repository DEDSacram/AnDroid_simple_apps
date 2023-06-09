package okio;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.UByte;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import kotlin.text.Typography;
import okhttp3.HttpUrl;
import okhttp3.internal.connection.RealConnection;
import okio.internal._BufferKt;

@Metadata(d1 = {"\u0000ª\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u001a\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0005\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\n\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0017\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u00032\u00020\u0004:\u0002\u0001B\u0005¢\u0006\u0002\u0010\u0005J\b\u0010\u0006\u001a\u00020\u0000H\u0016J\u0006\u0010\u0011\u001a\u00020\u0012J\b\u0010\u0013\u001a\u00020\u0000H\u0016J\b\u0010\u0014\u001a\u00020\u0012H\u0016J\u0006\u0010\u0015\u001a\u00020\fJ\u0006\u0010\u0016\u001a\u00020\u0000J$\u0010\u0017\u001a\u00020\u00002\u0006\u0010\u0018\u001a\u00020\u00192\b\b\u0002\u0010\u001a\u001a\u00020\f2\b\b\u0002\u0010\u001b\u001a\u00020\fH\u0007J\u0018\u0010\u0017\u001a\u00020\u00002\u0006\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u001a\u001a\u00020\fJ \u0010\u0017\u001a\u00020\u00002\u0006\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u001a\u001a\u00020\f2\u0006\u0010\u001b\u001a\u00020\fJ\u0010\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fH\u0002J\b\u0010 \u001a\u00020\u0000H\u0016J\b\u0010!\u001a\u00020\u0000H\u0016J\u0013\u0010\"\u001a\u00020#2\b\u0010$\u001a\u0004\u0018\u00010%H\u0002J\b\u0010&\u001a\u00020#H\u0016J\b\u0010'\u001a\u00020\u0012H\u0016J\u0016\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020\fH\u0002¢\u0006\u0002\b+J\u0015\u0010+\u001a\u00020)2\u0006\u0010,\u001a\u00020\fH\u0007¢\u0006\u0002\b-J\b\u0010.\u001a\u00020/H\u0016J\u0018\u00100\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u00101\u001a\u00020\u001dH\u0002J\u000e\u00102\u001a\u00020\u001d2\u0006\u00101\u001a\u00020\u001dJ\u000e\u00103\u001a\u00020\u001d2\u0006\u00101\u001a\u00020\u001dJ\u000e\u00104\u001a\u00020\u001d2\u0006\u00101\u001a\u00020\u001dJ\u0010\u00105\u001a\u00020\f2\u0006\u00106\u001a\u00020)H\u0016J\u0018\u00105\u001a\u00020\f2\u0006\u00106\u001a\u00020)2\u0006\u00107\u001a\u00020\fH\u0016J \u00105\u001a\u00020\f2\u0006\u00106\u001a\u00020)2\u0006\u00107\u001a\u00020\f2\u0006\u00108\u001a\u00020\fH\u0016J\u0010\u00105\u001a\u00020\f2\u0006\u00109\u001a\u00020\u001dH\u0016J\u0018\u00105\u001a\u00020\f2\u0006\u00109\u001a\u00020\u001d2\u0006\u00107\u001a\u00020\fH\u0016J\u0010\u0010:\u001a\u00020\f2\u0006\u0010;\u001a\u00020\u001dH\u0016J\u0018\u0010:\u001a\u00020\f2\u0006\u0010;\u001a\u00020\u001d2\u0006\u00107\u001a\u00020\fH\u0016J\b\u0010<\u001a\u00020=H\u0016J\b\u0010>\u001a\u00020#H\u0016J\u0006\u0010?\u001a\u00020\u001dJ\b\u0010@\u001a\u00020\u0019H\u0016J\b\u0010A\u001a\u00020\u0001H\u0016J\u0018\u0010B\u001a\u00020#2\u0006\u0010\u001a\u001a\u00020\f2\u0006\u00109\u001a\u00020\u001dH\u0016J(\u0010B\u001a\u00020#2\u0006\u0010\u001a\u001a\u00020\f2\u0006\u00109\u001a\u00020\u001d2\u0006\u0010C\u001a\u00020/2\u0006\u0010\u001b\u001a\u00020/H\u0016J\u0010\u0010D\u001a\u00020/2\u0006\u0010E\u001a\u00020FH\u0016J\u0010\u0010D\u001a\u00020/2\u0006\u0010E\u001a\u00020GH\u0016J \u0010D\u001a\u00020/2\u0006\u0010E\u001a\u00020G2\u0006\u0010\u001a\u001a\u00020/2\u0006\u0010\u001b\u001a\u00020/H\u0016J\u0018\u0010D\u001a\u00020\f2\u0006\u0010E\u001a\u00020\u00002\u0006\u0010\u001b\u001a\u00020\fH\u0016J\u0010\u0010H\u001a\u00020\f2\u0006\u0010E\u001a\u00020IH\u0016J\u0012\u0010J\u001a\u00020K2\b\b\u0002\u0010L\u001a\u00020KH\u0007J\b\u0010M\u001a\u00020)H\u0016J\b\u0010N\u001a\u00020GH\u0016J\u0010\u0010N\u001a\u00020G2\u0006\u0010\u001b\u001a\u00020\fH\u0016J\b\u0010O\u001a\u00020\u001dH\u0016J\u0010\u0010O\u001a\u00020\u001d2\u0006\u0010\u001b\u001a\u00020\fH\u0016J\b\u0010P\u001a\u00020\fH\u0016J\u000e\u0010Q\u001a\u00020\u00002\u0006\u0010R\u001a\u00020=J\u0016\u0010Q\u001a\u00020\u00002\u0006\u0010R\u001a\u00020=2\u0006\u0010\u001b\u001a\u00020\fJ \u0010Q\u001a\u00020\u00122\u0006\u0010R\u001a\u00020=2\u0006\u0010\u001b\u001a\u00020\f2\u0006\u0010S\u001a\u00020#H\u0002J\u0010\u0010T\u001a\u00020\u00122\u0006\u0010E\u001a\u00020GH\u0016J\u0018\u0010T\u001a\u00020\u00122\u0006\u0010E\u001a\u00020\u00002\u0006\u0010\u001b\u001a\u00020\fH\u0016J\b\u0010U\u001a\u00020\fH\u0016J\b\u0010V\u001a\u00020/H\u0016J\b\u0010W\u001a\u00020/H\u0016J\b\u0010X\u001a\u00020\fH\u0016J\b\u0010Y\u001a\u00020\fH\u0016J\b\u0010Z\u001a\u00020[H\u0016J\b\u0010\\\u001a\u00020[H\u0016J\u0010\u0010]\u001a\u00020\u001f2\u0006\u0010^\u001a\u00020_H\u0016J\u0018\u0010]\u001a\u00020\u001f2\u0006\u0010\u001b\u001a\u00020\f2\u0006\u0010^\u001a\u00020_H\u0016J\u0012\u0010`\u001a\u00020K2\b\b\u0002\u0010L\u001a\u00020KH\u0007J\b\u0010a\u001a\u00020\u001fH\u0016J\u0010\u0010a\u001a\u00020\u001f2\u0006\u0010\u001b\u001a\u00020\fH\u0016J\b\u0010b\u001a\u00020/H\u0016J\n\u0010c\u001a\u0004\u0018\u00010\u001fH\u0016J\b\u0010d\u001a\u00020\u001fH\u0016J\u0010\u0010d\u001a\u00020\u001f2\u0006\u0010e\u001a\u00020\fH\u0016J\u0010\u0010f\u001a\u00020#2\u0006\u0010\u001b\u001a\u00020\fH\u0016J\u0010\u0010g\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\fH\u0016J\u0010\u0010h\u001a\u00020/2\u0006\u0010i\u001a\u00020jH\u0016J\u0006\u0010k\u001a\u00020\u001dJ\u0006\u0010l\u001a\u00020\u001dJ\u0006\u0010m\u001a\u00020\u001dJ\r\u0010\r\u001a\u00020\fH\u0007¢\u0006\u0002\bnJ\u0010\u0010o\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\fH\u0016J\u0006\u0010p\u001a\u00020\u001dJ\u000e\u0010p\u001a\u00020\u001d2\u0006\u0010\u001b\u001a\u00020/J\b\u0010q\u001a\u00020rH\u0016J\b\u0010s\u001a\u00020\u001fH\u0016J\u0015\u0010t\u001a\u00020\n2\u0006\u0010u\u001a\u00020/H\u0000¢\u0006\u0002\bvJ\u0010\u0010w\u001a\u00020/2\u0006\u0010x\u001a\u00020FH\u0016J\u0010\u0010w\u001a\u00020\u00002\u0006\u0010x\u001a\u00020GH\u0016J \u0010w\u001a\u00020\u00002\u0006\u0010x\u001a\u00020G2\u0006\u0010\u001a\u001a\u00020/2\u0006\u0010\u001b\u001a\u00020/H\u0016J\u0018\u0010w\u001a\u00020\u00122\u0006\u0010x\u001a\u00020\u00002\u0006\u0010\u001b\u001a\u00020\fH\u0016J\u0010\u0010w\u001a\u00020\u00002\u0006\u0010y\u001a\u00020\u001dH\u0016J \u0010w\u001a\u00020\u00002\u0006\u0010y\u001a\u00020\u001d2\u0006\u0010\u001a\u001a\u00020/2\u0006\u0010\u001b\u001a\u00020/H\u0016J\u0018\u0010w\u001a\u00020\u00002\u0006\u0010x\u001a\u00020z2\u0006\u0010\u001b\u001a\u00020\fH\u0016J\u0010\u0010{\u001a\u00020\f2\u0006\u0010x\u001a\u00020zH\u0016J\u0010\u0010|\u001a\u00020\u00002\u0006\u00106\u001a\u00020/H\u0016J\u0010\u0010}\u001a\u00020\u00002\u0006\u0010~\u001a\u00020\fH\u0016J\u0010\u0010\u001a\u00020\u00002\u0006\u0010~\u001a\u00020\fH\u0016J\u0012\u0010\u0001\u001a\u00020\u00002\u0007\u0010\u0001\u001a\u00020/H\u0016J\u0012\u0010\u0001\u001a\u00020\u00002\u0007\u0010\u0001\u001a\u00020/H\u0016J\u0011\u0010\u0001\u001a\u00020\u00002\u0006\u0010~\u001a\u00020\fH\u0016J\u0011\u0010\u0001\u001a\u00020\u00002\u0006\u0010~\u001a\u00020\fH\u0016J\u0012\u0010\u0001\u001a\u00020\u00002\u0007\u0010\u0001\u001a\u00020/H\u0016J\u0012\u0010\u0001\u001a\u00020\u00002\u0007\u0010\u0001\u001a\u00020/H\u0016J\u001a\u0010\u0001\u001a\u00020\u00002\u0007\u0010\u0001\u001a\u00020\u001f2\u0006\u0010^\u001a\u00020_H\u0016J,\u0010\u0001\u001a\u00020\u00002\u0007\u0010\u0001\u001a\u00020\u001f2\u0007\u0010\u0001\u001a\u00020/2\u0007\u0010\u0001\u001a\u00020/2\u0006\u0010^\u001a\u00020_H\u0016J\u001b\u0010\u0001\u001a\u00020\u00002\u0006\u0010\u0018\u001a\u00020\u00192\b\b\u0002\u0010\u001b\u001a\u00020\fH\u0007J\u0012\u0010\u0001\u001a\u00020\u00002\u0007\u0010\u0001\u001a\u00020\u001fH\u0016J$\u0010\u0001\u001a\u00020\u00002\u0007\u0010\u0001\u001a\u00020\u001f2\u0007\u0010\u0001\u001a\u00020/2\u0007\u0010\u0001\u001a\u00020/H\u0016J\u0012\u0010\u0001\u001a\u00020\u00002\u0007\u0010\u0001\u001a\u00020/H\u0016R\u0014\u0010\u0006\u001a\u00020\u00008VX\u0004¢\u0006\u0006\u001a\u0004\b\u0007\u0010\bR\u0014\u0010\t\u001a\u0004\u0018\u00010\n8\u0000@\u0000X\u000e¢\u0006\u0002\n\u0000R&\u0010\r\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\f8G@@X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010¨\u0006\u0001"}, d2 = {"Lokio/Buffer;", "Lokio/BufferedSource;", "Lokio/BufferedSink;", "", "Ljava/nio/channels/ByteChannel;", "()V", "buffer", "getBuffer", "()Lokio/Buffer;", "head", "Lokio/Segment;", "<set-?>", "", "size", "()J", "setSize$okio", "(J)V", "clear", "", "clone", "close", "completeSegmentByteCount", "copy", "copyTo", "out", "Ljava/io/OutputStream;", "offset", "byteCount", "digest", "Lokio/ByteString;", "algorithm", "", "emit", "emitCompleteSegments", "equals", "", "other", "", "exhausted", "flush", "get", "", "pos", "getByte", "index", "-deprecated_getByte", "hashCode", "", "hmac", "key", "hmacSha1", "hmacSha256", "hmacSha512", "indexOf", "b", "fromIndex", "toIndex", "bytes", "indexOfElement", "targetBytes", "inputStream", "Ljava/io/InputStream;", "isOpen", "md5", "outputStream", "peek", "rangeEquals", "bytesOffset", "read", "sink", "Ljava/nio/ByteBuffer;", "", "readAll", "Lokio/Sink;", "readAndWriteUnsafe", "Lokio/Buffer$UnsafeCursor;", "unsafeCursor", "readByte", "readByteArray", "readByteString", "readDecimalLong", "readFrom", "input", "forever", "readFully", "readHexadecimalUnsignedLong", "readInt", "readIntLe", "readLong", "readLongLe", "readShort", "", "readShortLe", "readString", "charset", "Ljava/nio/charset/Charset;", "readUnsafe", "readUtf8", "readUtf8CodePoint", "readUtf8Line", "readUtf8LineStrict", "limit", "request", "require", "select", "options", "Lokio/Options;", "sha1", "sha256", "sha512", "-deprecated_size", "skip", "snapshot", "timeout", "Lokio/Timeout;", "toString", "writableSegment", "minimumCapacity", "writableSegment$okio", "write", "source", "byteString", "Lokio/Source;", "writeAll", "writeByte", "writeDecimalLong", "v", "writeHexadecimalUnsignedLong", "writeInt", "i", "writeIntLe", "writeLong", "writeLongLe", "writeShort", "s", "writeShortLe", "writeString", "string", "beginIndex", "endIndex", "writeTo", "writeUtf8", "writeUtf8CodePoint", "codePoint", "UnsafeCursor", "okio"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* compiled from: Buffer.kt */
public final class Buffer implements BufferedSource, BufferedSink, Cloneable, ByteChannel {
    public Segment head;
    private long size;

    public final Buffer copyTo(OutputStream outputStream) throws IOException {
        Intrinsics.checkNotNullParameter(outputStream, "out");
        return copyTo$default(this, outputStream, 0, 0, 6, (Object) null);
    }

    public final Buffer copyTo(OutputStream outputStream, long j) throws IOException {
        Intrinsics.checkNotNullParameter(outputStream, "out");
        return copyTo$default(this, outputStream, j, 0, 4, (Object) null);
    }

    public final UnsafeCursor readAndWriteUnsafe() {
        return readAndWriteUnsafe$default(this, (UnsafeCursor) null, 1, (Object) null);
    }

    public final UnsafeCursor readUnsafe() {
        return readUnsafe$default(this, (UnsafeCursor) null, 1, (Object) null);
    }

    public final Buffer writeTo(OutputStream outputStream) throws IOException {
        Intrinsics.checkNotNullParameter(outputStream, "out");
        return writeTo$default(this, outputStream, 0, 2, (Object) null);
    }

    public final long size() {
        return this.size;
    }

    public final void setSize$okio(long j) {
        this.size = j;
    }

    public Buffer buffer() {
        return this;
    }

    public Buffer getBuffer() {
        return this;
    }

    public OutputStream outputStream() {
        return new Buffer$outputStream$1(this);
    }

    public Buffer emitCompleteSegments() {
        return this;
    }

    public Buffer emit() {
        return this;
    }

    public boolean exhausted() {
        return this.size == 0;
    }

    public void require(long byteCount) throws EOFException {
        if (this.size < byteCount) {
            throw new EOFException();
        }
    }

    public boolean request(long byteCount) {
        return this.size >= byteCount;
    }

    public BufferedSource peek() {
        return Okio.buffer((Source) new PeekSource(this));
    }

    public InputStream inputStream() {
        return new Buffer$inputStream$1(this);
    }

    public static /* synthetic */ Buffer copyTo$default(Buffer buffer, OutputStream outputStream, long j, long j2, int i, Object obj) throws IOException {
        long j3;
        long j4;
        if ((i & 2) != 0) {
            j3 = 0;
        } else {
            j3 = j;
        }
        if ((i & 4) != 0) {
            j4 = buffer.size - j3;
        } else {
            j4 = j2;
        }
        return buffer.copyTo(outputStream, j3, j4);
    }

    public final Buffer copyTo(OutputStream out, long offset, long byteCount) throws IOException {
        OutputStream outputStream = out;
        Intrinsics.checkNotNullParameter(out, "out");
        long offset2 = offset;
        long byteCount2 = byteCount;
        _UtilKt.checkOffsetAndCount(this.size, offset2, byteCount2);
        if (byteCount2 == 0) {
            return this;
        }
        Segment s = this.head;
        while (true) {
            Intrinsics.checkNotNull(s);
            if (offset2 < ((long) (s.limit - s.pos))) {
                break;
            }
            offset2 -= (long) (s.limit - s.pos);
            s = s.next;
        }
        while (byteCount2 > 0) {
            Intrinsics.checkNotNull(s);
            int pos = (int) (((long) s.pos) + offset2);
            int toCopy = (int) Math.min((long) (s.limit - pos), byteCount2);
            out.write(s.data, pos, toCopy);
            byteCount2 -= (long) toCopy;
            offset2 = 0;
            s = s.next;
        }
        return this;
    }

    public static /* synthetic */ Buffer copyTo$default(Buffer buffer, Buffer buffer2, long j, long j2, int i, Object obj) {
        return buffer.copyTo(buffer2, (i & 2) != 0 ? 0 : j, j2);
    }

    public final Buffer copyTo(Buffer out, long offset, long byteCount) {
        Buffer buffer = out;
        Intrinsics.checkNotNullParameter(out, "out");
        long offset$iv = offset;
        long byteCount$iv = byteCount;
        _UtilKt.checkOffsetAndCount(size(), offset$iv, byteCount$iv);
        if (byteCount$iv != 0) {
            out.setSize$okio(out.size() + byteCount$iv);
            Segment s$iv = this.head;
            while (true) {
                Intrinsics.checkNotNull(s$iv);
                if (offset$iv < ((long) (s$iv.limit - s$iv.pos))) {
                    break;
                }
                offset$iv -= (long) (s$iv.limit - s$iv.pos);
                s$iv = s$iv.next;
            }
            while (byteCount$iv > 0) {
                Intrinsics.checkNotNull(s$iv);
                Segment copy$iv = s$iv.sharedCopy();
                copy$iv.pos += (int) offset$iv;
                copy$iv.limit = Math.min(copy$iv.pos + ((int) byteCount$iv), copy$iv.limit);
                Segment segment = buffer.head;
                if (segment == null) {
                    copy$iv.prev = copy$iv;
                    copy$iv.next = copy$iv.prev;
                    buffer.head = copy$iv.next;
                } else {
                    Intrinsics.checkNotNull(segment);
                    Segment segment2 = segment.prev;
                    Intrinsics.checkNotNull(segment2);
                    segment2.push(copy$iv);
                }
                byteCount$iv -= (long) (copy$iv.limit - copy$iv.pos);
                offset$iv = 0;
                s$iv = s$iv.next;
            }
        }
        return this;
    }

    public static /* synthetic */ Buffer copyTo$default(Buffer buffer, Buffer buffer2, long j, int i, Object obj) {
        if ((i & 2) != 0) {
            j = 0;
        }
        return buffer.copyTo(buffer2, j);
    }

    public final Buffer copyTo(Buffer out, long offset) {
        Intrinsics.checkNotNullParameter(out, "out");
        return copyTo(out, offset, this.size - offset);
    }

    public static /* synthetic */ Buffer writeTo$default(Buffer buffer, OutputStream outputStream, long j, int i, Object obj) throws IOException {
        if ((i & 2) != 0) {
            j = buffer.size;
        }
        return buffer.writeTo(outputStream, j);
    }

    public final Buffer writeTo(OutputStream out, long byteCount) throws IOException {
        Intrinsics.checkNotNullParameter(out, "out");
        long byteCount2 = byteCount;
        _UtilKt.checkOffsetAndCount(this.size, 0, byteCount2);
        Segment s = this.head;
        while (byteCount2 > 0) {
            Intrinsics.checkNotNull(s);
            int toCopy = (int) Math.min(byteCount2, (long) (s.limit - s.pos));
            out.write(s.data, s.pos, toCopy);
            s.pos += toCopy;
            this.size -= (long) toCopy;
            byteCount2 -= (long) toCopy;
            if (s.pos == s.limit) {
                Segment toRecycle = s;
                s = toRecycle.pop();
                this.head = s;
                SegmentPool.recycle(toRecycle);
            }
        }
        return this;
    }

    public final Buffer readFrom(InputStream input) throws IOException {
        Intrinsics.checkNotNullParameter(input, "input");
        readFrom(input, Long.MAX_VALUE, true);
        return this;
    }

    public final Buffer readFrom(InputStream input, long byteCount) throws IOException {
        Intrinsics.checkNotNullParameter(input, "input");
        if (byteCount >= 0) {
            readFrom(input, byteCount, false);
            return this;
        }
        throw new IllegalArgumentException(Intrinsics.stringPlus("byteCount < 0: ", Long.valueOf(byteCount)).toString());
    }

    private final void readFrom(InputStream input, long byteCount, boolean forever) throws IOException {
        long byteCount2 = byteCount;
        while (true) {
            if (byteCount2 > 0 || forever) {
                Segment tail = writableSegment$okio(1);
                int bytesRead = input.read(tail.data, tail.limit, (int) Math.min(byteCount2, (long) (8192 - tail.limit)));
                if (bytesRead == -1) {
                    if (tail.pos == tail.limit) {
                        this.head = tail.pop();
                        SegmentPool.recycle(tail);
                    }
                    if (!forever) {
                        throw new EOFException();
                    }
                    return;
                }
                tail.limit += bytesRead;
                this.size += (long) bytesRead;
                byteCount2 -= (long) bytesRead;
            } else {
                return;
            }
        }
    }

    public final long completeSegmentByteCount() {
        long result$iv = size();
        if (result$iv == 0) {
            return 0;
        }
        Segment segment = this.head;
        Intrinsics.checkNotNull(segment);
        Segment tail$iv = segment.prev;
        Intrinsics.checkNotNull(tail$iv);
        if (tail$iv.limit < 8192 && tail$iv.owner) {
            result$iv -= (long) (tail$iv.limit - tail$iv.pos);
        }
        return result$iv;
    }

    public byte readByte() throws EOFException {
        if (size() != 0) {
            Segment segment$iv = this.head;
            Intrinsics.checkNotNull(segment$iv);
            int pos$iv = segment$iv.pos;
            int limit$iv = segment$iv.limit;
            int pos$iv2 = pos$iv + 1;
            byte b$iv = segment$iv.data[pos$iv];
            setSize$okio(size() - 1);
            if (pos$iv2 == limit$iv) {
                this.head = segment$iv.pop();
                SegmentPool.recycle(segment$iv);
            } else {
                segment$iv.pos = pos$iv2;
            }
            return b$iv;
        }
        throw new EOFException();
    }

    public final byte getByte(long pos) {
        byte b;
        _UtilKt.checkOffsetAndCount(size(), pos, 1);
        Segment s$iv$iv = this.head;
        if (s$iv$iv == null) {
            Segment s$iv = null;
            Intrinsics.checkNotNull(s$iv);
            return s$iv.data[(int) ((((long) s$iv.pos) + pos) - -1)];
        }
        if (size() - pos < pos) {
            long offset$iv$iv = size();
            while (offset$iv$iv > pos) {
                Segment segment = s$iv$iv.prev;
                Intrinsics.checkNotNull(segment);
                s$iv$iv = segment;
                offset$iv$iv -= (long) (s$iv$iv.limit - s$iv$iv.pos);
            }
            Segment s$iv2 = s$iv$iv;
            Intrinsics.checkNotNull(s$iv2);
            b = s$iv2.data[(int) ((((long) s$iv2.pos) + pos) - offset$iv$iv)];
        } else {
            long offset$iv$iv2 = 0;
            while (true) {
                long nextOffset$iv$iv = ((long) (s$iv$iv.limit - s$iv$iv.pos)) + offset$iv$iv2;
                if (nextOffset$iv$iv > pos) {
                    break;
                }
                Segment segment2 = s$iv$iv.next;
                Intrinsics.checkNotNull(segment2);
                s$iv$iv = segment2;
                offset$iv$iv2 = nextOffset$iv$iv;
            }
            Segment s$iv3 = s$iv$iv;
            Intrinsics.checkNotNull(s$iv3);
            b = s$iv3.data[(int) ((((long) s$iv3.pos) + pos) - offset$iv$iv2)];
        }
        return b;
    }

    public short readShort() throws EOFException {
        if (size() >= 2) {
            Segment segment$iv = this.head;
            Intrinsics.checkNotNull(segment$iv);
            int pos$iv = segment$iv.pos;
            int limit$iv = segment$iv.limit;
            if (limit$iv - pos$iv < 2) {
                return (short) (((readByte() & UByte.MAX_VALUE) << 8) | (readByte() & 255));
            }
            byte[] data$iv = segment$iv.data;
            int pos$iv2 = pos$iv + 1;
            int pos$iv3 = pos$iv2 + 1;
            int s$iv = ((data$iv[pos$iv] & UByte.MAX_VALUE) << 8) | (data$iv[pos$iv2] & 255);
            setSize$okio(size() - 2);
            if (pos$iv3 == limit$iv) {
                this.head = segment$iv.pop();
                SegmentPool.recycle(segment$iv);
            } else {
                segment$iv.pos = pos$iv3;
            }
            return (short) s$iv;
        }
        throw new EOFException();
    }

    public int readInt() throws EOFException {
        if (size() >= 4) {
            Segment segment$iv = this.head;
            Intrinsics.checkNotNull(segment$iv);
            int pos$iv = segment$iv.pos;
            int limit$iv = segment$iv.limit;
            if (((long) (limit$iv - pos$iv)) < 4) {
                return ((readByte() & UByte.MAX_VALUE) << 24) | ((readByte() & UByte.MAX_VALUE) << 16) | ((readByte() & UByte.MAX_VALUE) << 8) | (readByte() & UByte.MAX_VALUE);
            }
            byte[] data$iv = segment$iv.data;
            int pos$iv2 = pos$iv + 1;
            int pos$iv3 = pos$iv2 + 1;
            int i = ((data$iv[pos$iv] & UByte.MAX_VALUE) << 24) | ((data$iv[pos$iv2] & UByte.MAX_VALUE) << 16);
            int pos$iv4 = pos$iv3 + 1;
            int i2 = i | ((data$iv[pos$iv3] & UByte.MAX_VALUE) << 8);
            int pos$iv5 = pos$iv4 + 1;
            int i$iv = i2 | (data$iv[pos$iv4] & 255);
            setSize$okio(size() - 4);
            if (pos$iv5 == limit$iv) {
                this.head = segment$iv.pop();
                SegmentPool.recycle(segment$iv);
            } else {
                segment$iv.pos = pos$iv5;
            }
            return i$iv;
        }
        throw new EOFException();
    }

    public long readLong() throws EOFException {
        if (size() >= 8) {
            Segment segment$iv = this.head;
            Intrinsics.checkNotNull(segment$iv);
            int pos$iv = segment$iv.pos;
            int limit$iv = segment$iv.limit;
            if (((long) (limit$iv - pos$iv)) < 8) {
                return ((((long) readInt()) & 4294967295L) << 32) | (((long) readInt()) & 4294967295L);
            }
            byte[] data$iv = segment$iv.data;
            int pos$iv2 = pos$iv + 1;
            int pos$iv3 = pos$iv2 + 1;
            long j = (((long) data$iv[pos$iv2]) & 255) << 48;
            int pos$iv4 = pos$iv3 + 1;
            int pos$iv5 = pos$iv4 + 1;
            int pos$iv6 = pos$iv5 + 1;
            int pos$iv7 = pos$iv6 + 1;
            long j2 = j | ((255 & ((long) data$iv[pos$iv])) << 56) | ((255 & ((long) data$iv[pos$iv3])) << 40) | ((((long) data$iv[pos$iv4]) & 255) << 32) | ((255 & ((long) data$iv[pos$iv5])) << 24) | ((((long) data$iv[pos$iv6]) & 255) << 16);
            int pos$iv8 = pos$iv7 + 1;
            int pos$iv9 = pos$iv8 + 1;
            long v$iv = j2 | ((255 & ((long) data$iv[pos$iv7])) << 8) | (((long) data$iv[pos$iv8]) & 255);
            setSize$okio(size() - 8);
            if (pos$iv9 == limit$iv) {
                this.head = segment$iv.pop();
                SegmentPool.recycle(segment$iv);
            } else {
                segment$iv.pos = pos$iv9;
            }
            return v$iv;
        }
        throw new EOFException();
    }

    public short readShortLe() throws EOFException {
        return _UtilKt.reverseBytes(readShort());
    }

    public int readIntLe() throws EOFException {
        return _UtilKt.reverseBytes(readInt());
    }

    public long readLongLe() throws EOFException {
        return _UtilKt.reverseBytes(readLong());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00b9, code lost:
        if (r7 == false) goto L_0x00bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00bb, code lost:
        r0 = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00bd, code lost:
        r0 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00be, code lost:
        if (r6 >= r0) goto L_0x00fe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00c8, code lost:
        if (r5.size() == 0) goto L_0x00f8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00ca, code lost:
        if (r7 == false) goto L_0x00cf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00cc, code lost:
        r1 = "Expected a digit";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00cf, code lost:
        r1 = "Expected a digit or '-'";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00f7, code lost:
        throw new java.lang.NumberFormatException(r1 + " but was 0x" + okio._UtilKt.toHexString(r5.getByte(0)));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00fd, code lost:
        throw new java.io.EOFException();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00fe, code lost:
        if (r7 == false) goto L_0x0101;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:?, code lost:
        return -r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:?, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long readDecimalLong() throws java.io.EOFException {
        /*
            r19 = this;
            r0 = r19
            r1 = 0
            long r2 = r0.size()
            r4 = 0
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 == 0) goto L_0x0104
            r2 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = -7
        L_0x0014:
            okio.Segment r11 = r0.head
            kotlin.jvm.internal.Intrinsics.checkNotNull(r11)
            byte[] r12 = r11.data
            int r13 = r11.pos
            int r14 = r11.limit
        L_0x001f:
            if (r13 >= r14) goto L_0x0091
            byte r15 = r12[r13]
            r4 = 48
            byte r4 = (byte) r4
            if (r15 < r4) goto L_0x0076
            r5 = 57
            byte r5 = (byte) r5
            if (r15 > r5) goto L_0x0076
            int r4 = r4 - r15
            r17 = -922337203685477580(0xf333333333333334, double:-8.390303882365713E246)
            int r5 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1))
            if (r5 < 0) goto L_0x004d
            int r5 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1))
            if (r5 != 0) goto L_0x0044
            r5 = r0
            r17 = r1
            long r0 = (long) r4
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 >= 0) goto L_0x0047
            goto L_0x0050
        L_0x0044:
            r5 = r0
            r17 = r1
        L_0x0047:
            r0 = 10
            long r2 = r2 * r0
            long r0 = (long) r4
            long r2 = r2 + r0
            goto L_0x0084
        L_0x004d:
            r5 = r0
            r17 = r1
        L_0x0050:
            okio.Buffer r0 = new okio.Buffer
            r0.<init>()
            okio.Buffer r0 = r0.writeDecimalLong((long) r2)
            okio.Buffer r0 = r0.writeByte((int) r15)
            if (r7 != 0) goto L_0x0062
            r0.readByte()
        L_0x0062:
            java.lang.NumberFormatException r1 = new java.lang.NumberFormatException
            r18 = r4
            java.lang.String r4 = r0.readUtf8()
            r16 = r0
            java.lang.String r0 = "Number too large: "
            java.lang.String r0 = kotlin.jvm.internal.Intrinsics.stringPlus(r0, r4)
            r1.<init>(r0)
            throw r1
        L_0x0076:
            r5 = r0
            r17 = r1
            r0 = 45
            byte r0 = (byte) r0
            if (r15 != r0) goto L_0x008e
            if (r6 != 0) goto L_0x008e
            r7 = 1
            r0 = 1
            long r9 = r9 - r0
        L_0x0084:
            int r13 = r13 + 1
            int r6 = r6 + 1
            r0 = r5
            r1 = r17
            r4 = 0
            goto L_0x001f
        L_0x008e:
            r0 = 1
            r8 = r0
            goto L_0x0094
        L_0x0091:
            r5 = r0
            r17 = r1
        L_0x0094:
            if (r13 != r14) goto L_0x00a0
            okio.Segment r0 = r11.pop()
            r5.head = r0
            okio.SegmentPool.recycle(r11)
            goto L_0x00a2
        L_0x00a0:
            r11.pos = r13
        L_0x00a2:
            if (r8 != 0) goto L_0x00b0
            okio.Segment r0 = r5.head
            if (r0 != 0) goto L_0x00a9
            goto L_0x00b0
        L_0x00a9:
            r0 = r5
            r1 = r17
            r4 = 0
            goto L_0x0014
        L_0x00b0:
            long r0 = r5.size()
            long r11 = (long) r6
            long r0 = r0 - r11
            r5.setSize$okio(r0)
            if (r7 == 0) goto L_0x00bd
            r0 = 2
            goto L_0x00be
        L_0x00bd:
            r0 = 1
        L_0x00be:
            if (r6 >= r0) goto L_0x00fe
            long r11 = r5.size()
            r13 = 0
            int r1 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r1 == 0) goto L_0x00f8
            if (r7 == 0) goto L_0x00cf
            java.lang.String r1 = "Expected a digit"
            goto L_0x00d1
        L_0x00cf:
            java.lang.String r1 = "Expected a digit or '-'"
        L_0x00d1:
            java.lang.NumberFormatException r4 = new java.lang.NumberFormatException
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.StringBuilder r11 = r11.append(r1)
            java.lang.String r12 = " but was 0x"
            java.lang.StringBuilder r11 = r11.append(r12)
            r12 = 0
            byte r12 = r5.getByte(r12)
            java.lang.String r12 = okio._UtilKt.toHexString((byte) r12)
            java.lang.StringBuilder r11 = r11.append(r12)
            java.lang.String r11 = r11.toString()
            r4.<init>(r11)
            throw r4
        L_0x00f8:
            java.io.EOFException r1 = new java.io.EOFException
            r1.<init>()
            throw r1
        L_0x00fe:
            if (r7 == 0) goto L_0x0101
            goto L_0x0103
        L_0x0101:
            long r11 = -r2
            r2 = r11
        L_0x0103:
            return r2
        L_0x0104:
            r5 = r0
            java.io.EOFException r0 = new java.io.EOFException
            r0.<init>()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.readDecimalLong():long");
    }

    /* JADX WARNING: Removed duplicated region for block: B:30:0x008e  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0098  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x008c A[EDGE_INSN: B:45:0x008c->B:29:0x008c ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:5:0x001e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long readHexadecimalUnsignedLong() throws java.io.EOFException {
        /*
            r16 = this;
            r0 = r16
            r1 = 0
            long r2 = r0.size()
            r4 = 0
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 == 0) goto L_0x00ab
            r2 = 0
            r6 = 0
            r7 = 0
        L_0x0011:
            okio.Segment r8 = r0.head
            kotlin.jvm.internal.Intrinsics.checkNotNull(r8)
            byte[] r9 = r8.data
            int r10 = r8.pos
            int r11 = r8.limit
        L_0x001c:
            if (r10 >= r11) goto L_0x008c
            r12 = 0
            byte r13 = r9[r10]
            r14 = 48
            byte r14 = (byte) r14
            if (r13 < r14) goto L_0x002e
            r15 = 57
            byte r15 = (byte) r15
            if (r13 > r15) goto L_0x002e
            int r12 = r13 - r14
            goto L_0x004b
        L_0x002e:
            r14 = 97
            byte r14 = (byte) r14
            if (r13 < r14) goto L_0x003d
            r15 = 102(0x66, float:1.43E-43)
            byte r15 = (byte) r15
            if (r13 > r15) goto L_0x003d
            int r14 = r13 - r14
            int r12 = r14 + 10
            goto L_0x004b
        L_0x003d:
            r14 = 65
            byte r14 = (byte) r14
            if (r13 < r14) goto L_0x0078
            r15 = 70
            byte r15 = (byte) r15
            if (r13 > r15) goto L_0x0078
            int r14 = r13 - r14
            int r12 = r14 + 10
        L_0x004b:
            r14 = -1152921504606846976(0xf000000000000000, double:-3.105036184601418E231)
            long r14 = r14 & r2
            int r14 = (r14 > r4 ? 1 : (r14 == r4 ? 0 : -1))
            if (r14 != 0) goto L_0x005b
            r14 = 4
            long r2 = r2 << r14
            long r14 = (long) r12
            long r2 = r2 | r14
            int r10 = r10 + 1
            int r6 = r6 + 1
            goto L_0x001c
        L_0x005b:
            okio.Buffer r4 = new okio.Buffer
            r4.<init>()
            okio.Buffer r4 = r4.writeHexadecimalUnsignedLong((long) r2)
            okio.Buffer r4 = r4.writeByte((int) r13)
            java.lang.NumberFormatException r5 = new java.lang.NumberFormatException
            java.lang.String r14 = r4.readUtf8()
            java.lang.String r15 = "Number too large: "
            java.lang.String r14 = kotlin.jvm.internal.Intrinsics.stringPlus(r15, r14)
            r5.<init>(r14)
            throw r5
        L_0x0078:
            if (r6 == 0) goto L_0x007c
            r7 = 1
            goto L_0x008c
        L_0x007c:
            java.lang.NumberFormatException r4 = new java.lang.NumberFormatException
            java.lang.String r5 = okio._UtilKt.toHexString((byte) r13)
            java.lang.String r14 = "Expected leading [0-9a-fA-F] character but was 0x"
            java.lang.String r5 = kotlin.jvm.internal.Intrinsics.stringPlus(r14, r5)
            r4.<init>(r5)
            throw r4
        L_0x008c:
            if (r10 != r11) goto L_0x0098
            okio.Segment r12 = r8.pop()
            r0.head = r12
            okio.SegmentPool.recycle(r8)
            goto L_0x009a
        L_0x0098:
            r8.pos = r10
        L_0x009a:
            if (r7 != 0) goto L_0x00a0
            okio.Segment r12 = r0.head
            if (r12 != 0) goto L_0x0011
        L_0x00a0:
            long r4 = r0.size()
            long r8 = (long) r6
            long r4 = r4 - r8
            r0.setSize$okio(r4)
            return r2
        L_0x00ab:
            java.io.EOFException r2 = new java.io.EOFException
            r2.<init>()
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.readHexadecimalUnsignedLong():long");
    }

    public ByteString readByteString() {
        return readByteString(size());
    }

    public ByteString readByteString(long byteCount) throws EOFException {
        if (!(byteCount >= 0 && byteCount <= 2147483647L)) {
            throw new IllegalArgumentException(Intrinsics.stringPlus("byteCount: ", Long.valueOf(byteCount)).toString());
        } else if (size() < byteCount) {
            throw new EOFException();
        } else if (byteCount < 4096) {
            return new ByteString(readByteArray(byteCount));
        } else {
            ByteString snapshot = snapshot((int) byteCount);
            ByteString byteString = snapshot;
            skip(byteCount);
            return snapshot;
        }
    }

    public int select(Options options) {
        Intrinsics.checkNotNullParameter(options, "options");
        int index$iv = _BufferKt.selectPrefix$default(this, options, false, 2, (Object) null);
        if (index$iv == -1) {
            return -1;
        }
        skip((long) options.getByteStrings$okio()[index$iv].size());
        return index$iv;
    }

    public void readFully(Buffer sink, long byteCount) throws EOFException {
        Intrinsics.checkNotNullParameter(sink, "sink");
        if (size() >= byteCount) {
            sink.write(this, byteCount);
        } else {
            sink.write(this, size());
            throw new EOFException();
        }
    }

    public long readAll(Sink sink) throws IOException {
        Intrinsics.checkNotNullParameter(sink, "sink");
        long byteCount$iv = size();
        if (byteCount$iv > 0) {
            sink.write(this, byteCount$iv);
        }
        return byteCount$iv;
    }

    public String readUtf8() {
        return readString(this.size, Charsets.UTF_8);
    }

    public String readUtf8(long byteCount) throws EOFException {
        return readString(byteCount, Charsets.UTF_8);
    }

    public String readString(Charset charset) {
        Intrinsics.checkNotNullParameter(charset, "charset");
        return readString(this.size, charset);
    }

    public String readString(long byteCount, Charset charset) throws EOFException {
        Intrinsics.checkNotNullParameter(charset, "charset");
        if (!(byteCount >= 0 && byteCount <= 2147483647L)) {
            throw new IllegalArgumentException(Intrinsics.stringPlus("byteCount: ", Long.valueOf(byteCount)).toString());
        } else if (this.size < byteCount) {
            throw new EOFException();
        } else if (byteCount == 0) {
            return HttpUrl.FRAGMENT_ENCODE_SET;
        } else {
            Segment s = this.head;
            Intrinsics.checkNotNull(s);
            if (((long) s.pos) + byteCount > ((long) s.limit)) {
                return new String(readByteArray(byteCount), charset);
            }
            String result = new String(s.data, s.pos, (int) byteCount, charset);
            s.pos += (int) byteCount;
            this.size -= byteCount;
            if (s.pos == s.limit) {
                this.head = s.pop();
                SegmentPool.recycle(s);
            }
            return result;
        }
    }

    public String readUtf8Line() throws EOFException {
        long newline$iv = indexOf((byte) 10);
        if (newline$iv != -1) {
            return _BufferKt.readUtf8Line(this, newline$iv);
        }
        if (size() != 0) {
            return readUtf8(size());
        }
        return null;
    }

    public String readUtf8LineStrict() throws EOFException {
        return readUtf8LineStrict(Long.MAX_VALUE);
    }

    public String readUtf8LineStrict(long limit) throws EOFException {
        long j = limit;
        if (j >= 0) {
            long j2 = Long.MAX_VALUE;
            if (j != Long.MAX_VALUE) {
                j2 = j + 1;
            }
            long scanLength$iv = j2;
            byte b = (byte) 10;
            long newline$iv = indexOf(b, 0, scanLength$iv);
            if (newline$iv != -1) {
                return _BufferKt.readUtf8Line(this, newline$iv);
            }
            if (scanLength$iv < size() && getByte(scanLength$iv - 1) == ((byte) 13) && getByte(scanLength$iv) == b) {
                return _BufferKt.readUtf8Line(this, scanLength$iv);
            }
            Buffer data$iv = new Buffer();
            long j3 = newline$iv;
            copyTo(data$iv, 0, Math.min((long) 32, size()));
            throw new EOFException("\\n not found: limit=" + Math.min(size(), j) + " content=" + data$iv.readByteString().hex() + Typography.ellipsis);
        }
        throw new IllegalArgumentException(Intrinsics.stringPlus("limit < 0: ", Long.valueOf(limit)).toString());
    }

    public int readUtf8CodePoint() throws EOFException {
        int min$iv;
        int byteCount$iv;
        int codePoint$iv;
        if (size() != 0) {
            byte b0$iv = getByte(0);
            if ((128 & b0$iv) == 0) {
                codePoint$iv = 127 & b0$iv;
                byteCount$iv = 1;
                min$iv = 0;
            } else if ((224 & b0$iv) == 192) {
                codePoint$iv = 31 & b0$iv;
                byteCount$iv = 2;
                min$iv = 128;
            } else if ((240 & b0$iv) == 224) {
                codePoint$iv = 15 & b0$iv;
                byteCount$iv = 3;
                min$iv = 2048;
            } else if ((248 & b0$iv) == 240) {
                codePoint$iv = 7 & b0$iv;
                byteCount$iv = 4;
                min$iv = 65536;
            } else {
                skip(1);
                return Utf8.REPLACEMENT_CODE_POINT;
            }
            if (size() >= ((long) byteCount$iv)) {
                boolean z = true;
                if (1 < byteCount$iv) {
                    int i = 1;
                    do {
                        int i$iv = i;
                        i++;
                        int b$iv = getByte((long) i$iv);
                        if ((192 & b$iv) == 128) {
                            codePoint$iv = (codePoint$iv << 6) | (63 & b$iv);
                        } else {
                            skip((long) i$iv);
                            return Utf8.REPLACEMENT_CODE_POINT;
                        }
                    } while (i < byteCount$iv);
                }
                skip((long) byteCount$iv);
                if (codePoint$iv > 1114111) {
                    return Utf8.REPLACEMENT_CODE_POINT;
                }
                if (55296 > codePoint$iv || codePoint$iv > 57343) {
                    z = false;
                }
                if (!z && codePoint$iv >= min$iv) {
                    return codePoint$iv;
                }
                return Utf8.REPLACEMENT_CODE_POINT;
            }
            throw new EOFException("size < " + byteCount$iv + ": " + size() + " (to read code point prefixed 0x" + _UtilKt.toHexString(b0$iv) + ')');
        }
        throw new EOFException();
    }

    public byte[] readByteArray() {
        return readByteArray(size());
    }

    public byte[] readByteArray(long byteCount) throws EOFException {
        if (!(byteCount >= 0 && byteCount <= 2147483647L)) {
            throw new IllegalArgumentException(Intrinsics.stringPlus("byteCount: ", Long.valueOf(byteCount)).toString());
        } else if (size() >= byteCount) {
            byte[] result$iv = new byte[((int) byteCount)];
            readFully(result$iv);
            return result$iv;
        } else {
            throw new EOFException();
        }
    }

    public int read(byte[] sink) {
        Intrinsics.checkNotNullParameter(sink, "sink");
        return read(sink, 0, sink.length);
    }

    public void readFully(byte[] sink) throws EOFException {
        Intrinsics.checkNotNullParameter(sink, "sink");
        int offset$iv = 0;
        while (offset$iv < sink.length) {
            int read$iv = read(sink, offset$iv, sink.length - offset$iv);
            if (read$iv != -1) {
                offset$iv += read$iv;
            } else {
                throw new EOFException();
            }
        }
    }

    public int read(byte[] sink, int offset, int byteCount) {
        Intrinsics.checkNotNullParameter(sink, "sink");
        _UtilKt.checkOffsetAndCount((long) sink.length, (long) offset, (long) byteCount);
        Segment s$iv = this.head;
        if (s$iv == null) {
            return -1;
        }
        int toCopy$iv = Math.min(byteCount, s$iv.limit - s$iv.pos);
        ArraysKt.copyInto(s$iv.data, sink, offset, s$iv.pos, s$iv.pos + toCopy$iv);
        s$iv.pos += toCopy$iv;
        setSize$okio(size() - ((long) toCopy$iv));
        if (s$iv.pos == s$iv.limit) {
            this.head = s$iv.pop();
            SegmentPool.recycle(s$iv);
        }
        return toCopy$iv;
    }

    public int read(ByteBuffer sink) throws IOException {
        Intrinsics.checkNotNullParameter(sink, "sink");
        Segment s = this.head;
        if (s == null) {
            return -1;
        }
        int toCopy = Math.min(sink.remaining(), s.limit - s.pos);
        sink.put(s.data, s.pos, toCopy);
        s.pos += toCopy;
        this.size -= (long) toCopy;
        if (s.pos == s.limit) {
            this.head = s.pop();
            SegmentPool.recycle(s);
        }
        return toCopy;
    }

    public final void clear() {
        skip(size());
    }

    public void skip(long byteCount) throws EOFException {
        long byteCount$iv = byteCount;
        while (byteCount$iv > 0) {
            Segment head$iv = this.head;
            if (head$iv != null) {
                int toSkip$iv = (int) Math.min(byteCount$iv, (long) (head$iv.limit - head$iv.pos));
                setSize$okio(size() - ((long) toSkip$iv));
                byteCount$iv -= (long) toSkip$iv;
                head$iv.pos += toSkip$iv;
                if (head$iv.pos == head$iv.limit) {
                    this.head = head$iv.pop();
                    SegmentPool.recycle(head$iv);
                }
            } else {
                throw new EOFException();
            }
        }
    }

    public Buffer write(ByteString byteString) {
        Intrinsics.checkNotNullParameter(byteString, "byteString");
        byteString.write$okio(this, 0, byteString.size());
        return this;
    }

    public Buffer write(ByteString byteString, int offset, int byteCount) {
        Intrinsics.checkNotNullParameter(byteString, "byteString");
        byteString.write$okio(this, offset, byteCount);
        return this;
    }

    public Buffer writeUtf8(String string) {
        Intrinsics.checkNotNullParameter(string, TypedValues.Custom.S_STRING);
        return writeUtf8(string, 0, string.length());
    }

    public Buffer writeUtf8(String string, int beginIndex, int endIndex) {
        String str = string;
        int i = beginIndex;
        int i2 = endIndex;
        Intrinsics.checkNotNullParameter(str, TypedValues.Custom.S_STRING);
        int i3 = 1;
        if (i >= 0) {
            if (i2 >= i) {
                if (i2 <= string.length()) {
                    int i$iv = beginIndex;
                    while (i$iv < i2) {
                        int c$iv = str.charAt(i$iv);
                        if (c$iv < 128) {
                            Segment tail$iv = writableSegment$okio(i3);
                            byte[] data$iv = tail$iv.data;
                            int segmentOffset$iv = tail$iv.limit - i$iv;
                            int runLimit$iv = Math.min(i2, 8192 - segmentOffset$iv);
                            data$iv[i$iv + segmentOffset$iv] = (byte) c$iv;
                            i$iv++;
                            while (i$iv < runLimit$iv) {
                                int c$iv2 = str.charAt(i$iv);
                                if (c$iv2 >= 128) {
                                    break;
                                }
                                data$iv[i$iv + segmentOffset$iv] = (byte) c$iv2;
                                i$iv++;
                            }
                            int runSize$iv = (i$iv + segmentOffset$iv) - tail$iv.limit;
                            tail$iv.limit += runSize$iv;
                            setSize$okio(size() + ((long) runSize$iv));
                            i3 = 1;
                        } else if (c$iv < 2048) {
                            Segment tail$iv2 = writableSegment$okio(2);
                            tail$iv2.data[tail$iv2.limit] = (byte) ((c$iv >> 6) | 192);
                            tail$iv2.data[tail$iv2.limit + 1] = (byte) (128 | (c$iv & 63));
                            tail$iv2.limit += 2;
                            setSize$okio(size() + 2);
                            i$iv++;
                            i3 = 1;
                        } else if (c$iv < 55296 || c$iv > 57343) {
                            Segment tail$iv3 = writableSegment$okio(3);
                            tail$iv3.data[tail$iv3.limit] = (byte) ((c$iv >> 12) | 224);
                            tail$iv3.data[tail$iv3.limit + 1] = (byte) ((63 & (c$iv >> 6)) | 128);
                            tail$iv3.data[tail$iv3.limit + 2] = (byte) ((c$iv & 63) | 128);
                            tail$iv3.limit += 3;
                            setSize$okio(size() + 3);
                            i$iv++;
                            i3 = 1;
                        } else {
                            int low$iv = i$iv + 1 < i2 ? str.charAt(i$iv + 1) : 0;
                            if (c$iv <= 56319) {
                                if (56320 <= low$iv && low$iv <= 57343) {
                                    int codePoint$iv = (((c$iv & 1023) << 10) | (low$iv & 1023)) + 65536;
                                    Segment tail$iv4 = writableSegment$okio(4);
                                    tail$iv4.data[tail$iv4.limit] = (byte) ((codePoint$iv >> 18) | 240);
                                    tail$iv4.data[tail$iv4.limit + 1] = (byte) (((codePoint$iv >> 12) & 63) | 128);
                                    tail$iv4.data[tail$iv4.limit + 2] = (byte) (((codePoint$iv >> 6) & 63) | 128);
                                    tail$iv4.data[tail$iv4.limit + 3] = (byte) (128 | (codePoint$iv & 63));
                                    tail$iv4.limit += 4;
                                    setSize$okio(size() + 4);
                                    i$iv += 2;
                                    i3 = 1;
                                }
                            }
                            writeByte(63);
                            i$iv++;
                            i3 = 1;
                        }
                    }
                    return this;
                }
                throw new IllegalArgumentException(("endIndex > string.length: " + i2 + " > " + string.length()).toString());
            }
            throw new IllegalArgumentException(("endIndex < beginIndex: " + i2 + " < " + i).toString());
        }
        throw new IllegalArgumentException(Intrinsics.stringPlus("beginIndex < 0: ", Integer.valueOf(beginIndex)).toString());
    }

    public Buffer writeUtf8CodePoint(int codePoint) {
        if (codePoint < 128) {
            writeByte(codePoint);
        } else if (codePoint < 2048) {
            Segment tail$iv = writableSegment$okio(2);
            tail$iv.data[tail$iv.limit] = (byte) ((codePoint >> 6) | 192);
            tail$iv.data[tail$iv.limit + 1] = (byte) (128 | (codePoint & 63));
            tail$iv.limit += 2;
            setSize$okio(size() + 2);
        } else {
            boolean z = false;
            if (55296 <= codePoint && codePoint <= 57343) {
                z = true;
            }
            if (z) {
                writeByte(63);
            } else if (codePoint < 65536) {
                Segment tail$iv2 = writableSegment$okio(3);
                tail$iv2.data[tail$iv2.limit] = (byte) ((codePoint >> 12) | 224);
                tail$iv2.data[tail$iv2.limit + 1] = (byte) ((63 & (codePoint >> 6)) | 128);
                tail$iv2.data[tail$iv2.limit + 2] = (byte) (128 | (codePoint & 63));
                tail$iv2.limit += 3;
                setSize$okio(size() + 3);
            } else if (codePoint <= 1114111) {
                Segment tail$iv3 = writableSegment$okio(4);
                tail$iv3.data[tail$iv3.limit] = (byte) ((codePoint >> 18) | 240);
                tail$iv3.data[tail$iv3.limit + 1] = (byte) (((codePoint >> 12) & 63) | 128);
                tail$iv3.data[tail$iv3.limit + 2] = (byte) ((63 & (codePoint >> 6)) | 128);
                tail$iv3.data[tail$iv3.limit + 3] = (byte) (128 | (codePoint & 63));
                tail$iv3.limit += 4;
                setSize$okio(size() + 4);
            } else {
                throw new IllegalArgumentException(Intrinsics.stringPlus("Unexpected code point: 0x", _UtilKt.toHexString(codePoint)));
            }
        }
        return this;
    }

    public Buffer writeString(String string, Charset charset) {
        Intrinsics.checkNotNullParameter(string, TypedValues.Custom.S_STRING);
        Intrinsics.checkNotNullParameter(charset, "charset");
        return writeString(string, 0, string.length(), charset);
    }

    public Buffer writeString(String string, int beginIndex, int endIndex, Charset charset) {
        Intrinsics.checkNotNullParameter(string, TypedValues.Custom.S_STRING);
        Intrinsics.checkNotNullParameter(charset, "charset");
        boolean z = true;
        if (beginIndex >= 0) {
            if (endIndex >= beginIndex) {
                if (endIndex > string.length()) {
                    z = false;
                }
                if (!z) {
                    throw new IllegalArgumentException(("endIndex > string.length: " + endIndex + " > " + string.length()).toString());
                } else if (Intrinsics.areEqual((Object) charset, (Object) Charsets.UTF_8)) {
                    return writeUtf8(string, beginIndex, endIndex);
                } else {
                    String substring = string.substring(beginIndex, endIndex);
                    Intrinsics.checkNotNullExpressionValue(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
                    if (substring != null) {
                        byte[] data = substring.getBytes(charset);
                        Intrinsics.checkNotNullExpressionValue(data, "(this as java.lang.String).getBytes(charset)");
                        return write(data, 0, data.length);
                    }
                    throw new NullPointerException("null cannot be cast to non-null type java.lang.String");
                }
            } else {
                throw new IllegalArgumentException(("endIndex < beginIndex: " + endIndex + " < " + beginIndex).toString());
            }
        } else {
            throw new IllegalArgumentException(Intrinsics.stringPlus("beginIndex < 0: ", Integer.valueOf(beginIndex)).toString());
        }
    }

    public Buffer write(byte[] source) {
        Intrinsics.checkNotNullParameter(source, "source");
        return write(source, 0, source.length);
    }

    public Buffer write(byte[] source, int offset, int byteCount) {
        Intrinsics.checkNotNullParameter(source, "source");
        int offset$iv = offset;
        _UtilKt.checkOffsetAndCount((long) source.length, (long) offset$iv, (long) byteCount);
        int limit$iv = offset$iv + byteCount;
        while (offset$iv < limit$iv) {
            Segment tail$iv = writableSegment$okio(1);
            int toCopy$iv = Math.min(limit$iv - offset$iv, 8192 - tail$iv.limit);
            ArraysKt.copyInto(source, tail$iv.data, tail$iv.limit, offset$iv, offset$iv + toCopy$iv);
            offset$iv += toCopy$iv;
            tail$iv.limit += toCopy$iv;
        }
        setSize$okio(size() + ((long) byteCount));
        return this;
    }

    public int write(ByteBuffer source) throws IOException {
        Intrinsics.checkNotNullParameter(source, "source");
        int byteCount = source.remaining();
        int remaining = byteCount;
        while (remaining > 0) {
            Segment tail = writableSegment$okio(1);
            int toCopy = Math.min(remaining, 8192 - tail.limit);
            source.get(tail.data, tail.limit, toCopy);
            remaining -= toCopy;
            tail.limit += toCopy;
        }
        this.size += (long) byteCount;
        return byteCount;
    }

    public long writeAll(Source source) throws IOException {
        Intrinsics.checkNotNullParameter(source, "source");
        long totalBytesRead$iv = 0;
        while (true) {
            long readCount$iv = source.read(this, 8192);
            if (readCount$iv == -1) {
                return totalBytesRead$iv;
            }
            totalBytesRead$iv += readCount$iv;
        }
    }

    public Buffer write(Source source, long byteCount) throws IOException {
        Intrinsics.checkNotNullParameter(source, "source");
        long byteCount$iv = byteCount;
        while (byteCount$iv > 0) {
            long read$iv = source.read(this, byteCount$iv);
            if (read$iv != -1) {
                byteCount$iv -= read$iv;
            } else {
                throw new EOFException();
            }
        }
        return this;
    }

    public Buffer writeByte(int b) {
        Segment tail$iv = writableSegment$okio(1);
        byte[] bArr = tail$iv.data;
        int i = tail$iv.limit;
        tail$iv.limit = i + 1;
        bArr[i] = (byte) b;
        setSize$okio(size() + 1);
        return this;
    }

    public Buffer writeShort(int s) {
        Segment tail$iv = writableSegment$okio(2);
        byte[] data$iv = tail$iv.data;
        int limit$iv = tail$iv.limit;
        int limit$iv2 = limit$iv + 1;
        data$iv[limit$iv] = (byte) ((s >>> 8) & 255);
        data$iv[limit$iv2] = (byte) (s & 255);
        tail$iv.limit = limit$iv2 + 1;
        setSize$okio(size() + 2);
        return this;
    }

    public Buffer writeShortLe(int s) {
        return writeShort((int) _UtilKt.reverseBytes((short) s));
    }

    public Buffer writeInt(int i) {
        Segment tail$iv = writableSegment$okio(4);
        byte[] data$iv = tail$iv.data;
        int limit$iv = tail$iv.limit;
        int limit$iv2 = limit$iv + 1;
        data$iv[limit$iv] = (byte) ((i >>> 24) & 255);
        int limit$iv3 = limit$iv2 + 1;
        data$iv[limit$iv2] = (byte) ((i >>> 16) & 255);
        int limit$iv4 = limit$iv3 + 1;
        data$iv[limit$iv3] = (byte) ((i >>> 8) & 255);
        data$iv[limit$iv4] = (byte) (i & 255);
        tail$iv.limit = limit$iv4 + 1;
        setSize$okio(size() + 4);
        return this;
    }

    public Buffer writeIntLe(int i) {
        return writeInt(_UtilKt.reverseBytes(i));
    }

    public Buffer writeLong(long v) {
        Segment tail$iv = writableSegment$okio(8);
        byte[] data$iv = tail$iv.data;
        int limit$iv = tail$iv.limit;
        int limit$iv2 = limit$iv + 1;
        data$iv[limit$iv] = (byte) ((int) ((v >>> 56) & 255));
        int limit$iv3 = limit$iv2 + 1;
        data$iv[limit$iv2] = (byte) ((int) ((v >>> 48) & 255));
        int limit$iv4 = limit$iv3 + 1;
        data$iv[limit$iv3] = (byte) ((int) ((v >>> 40) & 255));
        int limit$iv5 = limit$iv4 + 1;
        data$iv[limit$iv4] = (byte) ((int) ((v >>> 32) & 255));
        int limit$iv6 = limit$iv5 + 1;
        data$iv[limit$iv5] = (byte) ((int) ((v >>> 24) & 255));
        int limit$iv7 = limit$iv6 + 1;
        data$iv[limit$iv6] = (byte) ((int) ((v >>> 16) & 255));
        int limit$iv8 = limit$iv7 + 1;
        data$iv[limit$iv7] = (byte) ((int) ((v >>> 8) & 255));
        data$iv[limit$iv8] = (byte) ((int) (v & 255));
        tail$iv.limit = limit$iv8 + 1;
        setSize$okio(size() + 8);
        return this;
    }

    public Buffer writeLongLe(long v) {
        return writeLong(_UtilKt.reverseBytes(v));
    }

    public Buffer writeDecimalLong(long v) {
        int width$iv;
        long v$iv = v;
        if (v$iv == 0) {
            return writeByte(48);
        }
        boolean negative$iv = false;
        if (v$iv < 0) {
            v$iv = -v$iv;
            if (v$iv < 0) {
                return writeUtf8("-9223372036854775808");
            }
            negative$iv = true;
        }
        if (v$iv < 100000000) {
            if (v$iv < 10000) {
                if (v$iv < 100) {
                    if (v$iv < 10) {
                        width$iv = 1;
                    } else {
                        width$iv = 2;
                    }
                } else if (v$iv < 1000) {
                    width$iv = 3;
                } else {
                    width$iv = 4;
                }
            } else if (v$iv < 1000000) {
                if (v$iv < 100000) {
                    width$iv = 5;
                } else {
                    width$iv = 6;
                }
            } else if (v$iv < 10000000) {
                width$iv = 7;
            } else {
                width$iv = 8;
            }
        } else if (v$iv < 1000000000000L) {
            if (v$iv < RealConnection.IDLE_CONNECTION_HEALTHY_NS) {
                if (v$iv < 1000000000) {
                    width$iv = 9;
                } else {
                    width$iv = 10;
                }
            } else if (v$iv < 100000000000L) {
                width$iv = 11;
            } else {
                width$iv = 12;
            }
        } else if (v$iv < 1000000000000000L) {
            if (v$iv < 10000000000000L) {
                width$iv = 13;
            } else if (v$iv < 100000000000000L) {
                width$iv = 14;
            } else {
                width$iv = 15;
            }
        } else if (v$iv < 100000000000000000L) {
            if (v$iv < 10000000000000000L) {
                width$iv = 16;
            } else {
                width$iv = 17;
            }
        } else if (v$iv < 1000000000000000000L) {
            width$iv = 18;
        } else {
            width$iv = 19;
        }
        if (negative$iv) {
            width$iv++;
        }
        Segment tail$iv = writableSegment$okio(width$iv);
        byte[] data$iv = tail$iv.data;
        int pos$iv = tail$iv.limit + width$iv;
        while (v$iv != 0) {
            long j = (long) 10;
            pos$iv--;
            data$iv[pos$iv] = _BufferKt.getHEX_DIGIT_BYTES()[(int) (v$iv % j)];
            v$iv /= j;
        }
        if (negative$iv) {
            data$iv[pos$iv - 1] = (byte) 45;
        }
        tail$iv.limit += width$iv;
        setSize$okio(size() + ((long) width$iv));
        return this;
    }

    public Buffer writeHexadecimalUnsignedLong(long v) {
        long v$iv = v;
        if (v$iv == 0) {
            return writeByte(48);
        }
        long x$iv = v$iv;
        long x$iv2 = x$iv | (x$iv >>> 1);
        long x$iv3 = x$iv2 | (x$iv2 >>> 2);
        long x$iv4 = x$iv3 | (x$iv3 >>> 4);
        long x$iv5 = x$iv4 | (x$iv4 >>> 8);
        long x$iv6 = x$iv5 | (x$iv5 >>> 16);
        long x$iv7 = x$iv6 | (x$iv6 >>> 32);
        long x$iv8 = x$iv7 - ((x$iv7 >>> 1) & 6148914691236517205L);
        long x$iv9 = ((x$iv8 >>> 2) & 3689348814741910323L) + (3689348814741910323L & x$iv8);
        long x$iv10 = ((x$iv9 >>> 4) + x$iv9) & 1085102592571150095L;
        long x$iv11 = x$iv10 + (x$iv10 >>> 8);
        long x$iv12 = x$iv11 + (x$iv11 >>> 16);
        int width$iv = (int) ((((long) 3) + ((x$iv12 & 63) + (63 & (x$iv12 >>> 32)))) / ((long) 4));
        Segment tail$iv = writableSegment$okio(width$iv);
        byte[] data$iv = tail$iv.data;
        int start$iv = tail$iv.limit;
        for (int pos$iv = (tail$iv.limit + width$iv) - 1; pos$iv >= start$iv; pos$iv--) {
            data$iv[pos$iv] = _BufferKt.getHEX_DIGIT_BYTES()[(int) (15 & v$iv)];
            v$iv >>>= 4;
        }
        tail$iv.limit += width$iv;
        setSize$okio(size() + ((long) width$iv));
        return this;
    }

    public final Segment writableSegment$okio(int minimumCapacity) {
        boolean z = true;
        if (minimumCapacity < 1 || minimumCapacity > 8192) {
            z = false;
        }
        if (z) {
            Segment segment = this.head;
            if (segment == null) {
                Segment tail$iv = SegmentPool.take();
                this.head = tail$iv;
                tail$iv.prev = tail$iv;
                tail$iv.next = tail$iv;
                return tail$iv;
            }
            Intrinsics.checkNotNull(segment);
            Segment tail$iv2 = segment.prev;
            Intrinsics.checkNotNull(tail$iv2);
            if (tail$iv2.limit + minimumCapacity > 8192 || !tail$iv2.owner) {
                return tail$iv2.push(SegmentPool.take());
            }
            return tail$iv2;
        }
        throw new IllegalArgumentException("unexpected capacity".toString());
    }

    public void write(Buffer source, long byteCount) {
        Segment tail$iv;
        Intrinsics.checkNotNullParameter(source, "source");
        long byteCount$iv = byteCount;
        if (source != this) {
            _UtilKt.checkOffsetAndCount(source.size(), 0, byteCount$iv);
            while (byteCount$iv > 0) {
                Segment segment = source.head;
                Intrinsics.checkNotNull(segment);
                int i = segment.limit;
                Segment segment2 = source.head;
                Intrinsics.checkNotNull(segment2);
                if (byteCount$iv < ((long) (i - segment2.pos))) {
                    Segment segment3 = this.head;
                    if (segment3 != null) {
                        Intrinsics.checkNotNull(segment3);
                        tail$iv = segment3.prev;
                    } else {
                        tail$iv = null;
                    }
                    if (tail$iv != null && tail$iv.owner) {
                        if ((((long) tail$iv.limit) + byteCount$iv) - ((long) (tail$iv.shared ? 0 : tail$iv.pos)) <= 8192) {
                            Segment segment4 = source.head;
                            Intrinsics.checkNotNull(segment4);
                            segment4.writeTo(tail$iv, (int) byteCount$iv);
                            source.setSize$okio(source.size() - byteCount$iv);
                            setSize$okio(size() + byteCount$iv);
                            return;
                        }
                    }
                    Segment segment5 = source.head;
                    Intrinsics.checkNotNull(segment5);
                    source.head = segment5.split((int) byteCount$iv);
                }
                Segment tail$iv2 = source.head;
                Intrinsics.checkNotNull(tail$iv2);
                long movedByteCount$iv = (long) (tail$iv2.limit - tail$iv2.pos);
                source.head = tail$iv2.pop();
                Segment segment6 = this.head;
                if (segment6 == null) {
                    this.head = tail$iv2;
                    tail$iv2.prev = tail$iv2;
                    tail$iv2.next = tail$iv2.prev;
                } else {
                    Intrinsics.checkNotNull(segment6);
                    Segment tail$iv3 = segment6.prev;
                    Intrinsics.checkNotNull(tail$iv3);
                    tail$iv3.push(tail$iv2).compact();
                }
                source.setSize$okio(source.size() - movedByteCount$iv);
                setSize$okio(size() + movedByteCount$iv);
                byteCount$iv -= movedByteCount$iv;
            }
            return;
        }
        throw new IllegalArgumentException("source == this".toString());
    }

    public long read(Buffer sink, long byteCount) {
        Intrinsics.checkNotNullParameter(sink, "sink");
        long byteCount$iv = byteCount;
        if (!(byteCount$iv >= 0)) {
            throw new IllegalArgumentException(Intrinsics.stringPlus("byteCount < 0: ", Long.valueOf(byteCount$iv)).toString());
        } else if (size() == 0) {
            return -1;
        } else {
            if (byteCount$iv > size()) {
                byteCount$iv = size();
            }
            sink.write(this, byteCount$iv);
            return byteCount$iv;
        }
    }

    public long indexOf(byte b) {
        return indexOf(b, 0, Long.MAX_VALUE);
    }

    public long indexOf(byte b, long fromIndex) {
        return indexOf(b, fromIndex, Long.MAX_VALUE);
    }

    public long indexOf(byte b, long fromIndex, long toIndex) {
        byte b2 = b;
        long fromIndex$iv = fromIndex;
        long toIndex$iv = toIndex;
        boolean z = false;
        if (0 <= fromIndex$iv && fromIndex$iv <= toIndex$iv) {
            z = true;
        }
        if (z) {
            if (toIndex$iv > size()) {
                toIndex$iv = size();
            }
            long toIndex$iv2 = toIndex$iv;
            if (fromIndex$iv == toIndex$iv2) {
                return -1;
            }
            long fromIndex$iv$iv = fromIndex$iv;
            Buffer $this$seek$iv$iv = this;
            int $i$f$seek = false;
            Segment s$iv$iv = $this$seek$iv$iv.head;
            if (s$iv$iv == null) {
                Segment segment = null;
                return -1;
            } else if ($this$seek$iv$iv.size() - fromIndex$iv$iv < fromIndex$iv$iv) {
                long offset$iv$iv = $this$seek$iv$iv.size();
                while (offset$iv$iv > fromIndex$iv$iv) {
                    Segment segment2 = s$iv$iv.prev;
                    Intrinsics.checkNotNull(segment2);
                    s$iv$iv = segment2;
                    offset$iv$iv -= (long) (s$iv$iv.limit - s$iv$iv.pos);
                }
                Segment s$iv = s$iv$iv;
                long offset$iv = offset$iv$iv;
                if (s$iv == null) {
                    return -1;
                }
                long offset$iv2 = offset$iv;
                Segment s$iv2 = s$iv;
                while (offset$iv2 < toIndex$iv2) {
                    Segment s$iv$iv2 = s$iv$iv;
                    byte[] data$iv = s$iv2.data;
                    Segment s$iv3 = s$iv;
                    long offset$iv3 = offset$iv;
                    Buffer $this$seek$iv$iv2 = $this$seek$iv$iv;
                    int $i$f$seek2 = $i$f$seek;
                    int limit$iv = (int) Math.min((long) s$iv2.limit, (((long) s$iv2.pos) + toIndex$iv2) - offset$iv2);
                    for (int pos$iv = (int) ((((long) s$iv2.pos) + fromIndex$iv) - offset$iv2); pos$iv < limit$iv; pos$iv++) {
                        if (data$iv[pos$iv] == b2) {
                            return ((long) (pos$iv - s$iv2.pos)) + offset$iv2;
                        }
                    }
                    offset$iv2 += (long) (s$iv2.limit - s$iv2.pos);
                    fromIndex$iv = offset$iv2;
                    Segment segment3 = s$iv2.next;
                    Intrinsics.checkNotNull(segment3);
                    s$iv2 = segment3;
                    s$iv$iv = s$iv$iv2;
                    s$iv = s$iv3;
                    offset$iv = offset$iv3;
                    $this$seek$iv$iv = $this$seek$iv$iv2;
                    $i$f$seek = $i$f$seek2;
                }
                Segment segment4 = s$iv;
                long j = offset$iv;
                Buffer buffer = $this$seek$iv$iv;
                int i = $i$f$seek;
                return -1;
            } else {
                Buffer buffer2 = $this$seek$iv$iv;
                long offset$iv$iv2 = 0;
                while (true) {
                    long nextOffset$iv$iv = ((long) (s$iv$iv.limit - s$iv$iv.pos)) + offset$iv$iv2;
                    if (nextOffset$iv$iv > fromIndex$iv$iv) {
                        break;
                    }
                    long j2 = offset$iv$iv2;
                    long j3 = fromIndex$iv$iv;
                    Segment segment5 = s$iv$iv.next;
                    Intrinsics.checkNotNull(segment5);
                    s$iv$iv = segment5;
                    offset$iv$iv2 = nextOffset$iv$iv;
                }
                Segment s$iv4 = s$iv$iv;
                long offset$iv4 = offset$iv$iv2;
                int limit$iv2 = false;
                if (s$iv4 == null) {
                    return -1;
                }
                Segment s$iv5 = s$iv4;
                long offset$iv5 = offset$iv4;
                while (offset$iv5 < toIndex$iv2) {
                    Segment s$iv6 = s$iv4;
                    byte[] data$iv2 = s$iv5.data;
                    int i2 = limit$iv2;
                    long offset$iv$iv3 = offset$iv$iv2;
                    long fromIndex$iv$iv2 = fromIndex$iv$iv;
                    int limit$iv3 = (int) Math.min((long) s$iv5.limit, (((long) s$iv5.pos) + toIndex$iv2) - offset$iv5);
                    for (int pos$iv2 = (int) ((((long) s$iv5.pos) + fromIndex$iv) - offset$iv5); pos$iv2 < limit$iv3; pos$iv2++) {
                        if (data$iv2[pos$iv2] == b2) {
                            return ((long) (pos$iv2 - s$iv5.pos)) + offset$iv5;
                        }
                    }
                    offset$iv5 += (long) (s$iv5.limit - s$iv5.pos);
                    fromIndex$iv = offset$iv5;
                    Segment segment6 = s$iv5.next;
                    Intrinsics.checkNotNull(segment6);
                    s$iv5 = segment6;
                    s$iv4 = s$iv6;
                    limit$iv2 = i2;
                    offset$iv$iv2 = offset$iv$iv3;
                    fromIndex$iv$iv = fromIndex$iv$iv2;
                }
                int i3 = limit$iv2;
                long j4 = offset$iv$iv2;
                long j5 = fromIndex$iv$iv;
                return -1;
            }
        } else {
            throw new IllegalArgumentException(("size=" + size() + " fromIndex=" + fromIndex$iv + " toIndex=" + toIndex$iv).toString());
        }
    }

    public long indexOf(ByteString bytes) throws IOException {
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        return indexOf(bytes, 0);
    }

    public long indexOf(ByteString bytes, long fromIndex) throws IOException {
        byte b0$iv;
        byte[] targetByteArray$iv;
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        Buffer $this$commonIndexOf$iv = this;
        long fromIndex$iv = fromIndex;
        if (bytes.size() > 0) {
            if (fromIndex$iv >= 0) {
                long fromIndex$iv$iv = fromIndex$iv;
                Buffer $this$seek$iv$iv = $this$commonIndexOf$iv;
                int $i$f$seek = false;
                Segment s$iv$iv = $this$seek$iv$iv.head;
                if (s$iv$iv == null) {
                    Segment segment = null;
                    return -1;
                } else if ($this$seek$iv$iv.size() - fromIndex$iv$iv < fromIndex$iv$iv) {
                    long offset$iv$iv = $this$seek$iv$iv.size();
                    while (offset$iv$iv > fromIndex$iv$iv) {
                        Segment segment2 = s$iv$iv.prev;
                        Intrinsics.checkNotNull(segment2);
                        s$iv$iv = segment2;
                        offset$iv$iv -= (long) (s$iv$iv.limit - s$iv$iv.pos);
                    }
                    Segment s$iv = s$iv$iv;
                    long offset$iv = offset$iv$iv;
                    if (s$iv == null) {
                        return -1;
                    }
                    long offset$iv2 = offset$iv;
                    byte[] targetByteArray$iv2 = bytes.internalArray$okio();
                    byte b0$iv2 = targetByteArray$iv2[0];
                    int bytesSize$iv = bytes.size();
                    long resultLimit$iv = ($this$commonIndexOf$iv.size() - ((long) bytesSize$iv)) + 1;
                    Segment s$iv2 = s$iv;
                    while (offset$iv2 < resultLimit$iv) {
                        byte[] data$iv = s$iv2.data;
                        Buffer $this$seek$iv$iv2 = $this$seek$iv$iv;
                        int $i$f$seek2 = $i$f$seek;
                        Segment s$iv$iv2 = s$iv$iv;
                        Segment s$iv3 = s$iv;
                        long offset$iv3 = offset$iv;
                        int segmentLimit$iv = (int) Math.min((long) s$iv2.limit, (((long) s$iv2.pos) + resultLimit$iv) - offset$iv2);
                        int i = (int) ((((long) s$iv2.pos) + fromIndex$iv) - offset$iv2);
                        if (i < segmentLimit$iv) {
                            do {
                                int pos$iv = i;
                                i++;
                                if (data$iv[pos$iv] == b0$iv2 && _BufferKt.rangeEquals(s$iv2, pos$iv + 1, targetByteArray$iv2, 1, bytesSize$iv)) {
                                    return ((long) (pos$iv - s$iv2.pos)) + offset$iv2;
                                }
                            } while (i < segmentLimit$iv);
                        }
                        offset$iv2 += (long) (s$iv2.limit - s$iv2.pos);
                        fromIndex$iv = offset$iv2;
                        Segment segment3 = s$iv2.next;
                        Intrinsics.checkNotNull(segment3);
                        s$iv2 = segment3;
                        $this$seek$iv$iv = $this$seek$iv$iv2;
                        s$iv$iv = s$iv$iv2;
                        $i$f$seek = $i$f$seek2;
                        s$iv = s$iv3;
                        offset$iv = offset$iv3;
                    }
                    int i2 = $i$f$seek;
                    Segment segment4 = s$iv$iv;
                    Segment segment5 = s$iv;
                    long j = offset$iv;
                    return -1;
                } else {
                    Buffer buffer = $this$seek$iv$iv;
                    long offset$iv$iv2 = 0;
                    while (true) {
                        long nextOffset$iv$iv = ((long) (s$iv$iv.limit - s$iv$iv.pos)) + offset$iv$iv2;
                        if (nextOffset$iv$iv > fromIndex$iv$iv) {
                            break;
                        }
                        long j2 = offset$iv$iv2;
                        Segment segment6 = s$iv$iv.next;
                        Intrinsics.checkNotNull(segment6);
                        s$iv$iv = segment6;
                        offset$iv$iv2 = nextOffset$iv$iv;
                        $this$commonIndexOf$iv = $this$commonIndexOf$iv;
                        fromIndex$iv$iv = fromIndex$iv$iv;
                    }
                    Segment s$iv4 = s$iv$iv;
                    long offset$iv4 = offset$iv$iv2;
                    if (s$iv4 == null) {
                        return -1;
                    }
                    Segment s$iv5 = s$iv4;
                    long offset$iv5 = offset$iv4;
                    long j3 = offset$iv$iv2;
                    byte[] targetByteArray$iv3 = bytes.internalArray$okio();
                    byte b0$iv3 = targetByteArray$iv3[0];
                    int bytesSize$iv2 = bytes.size();
                    long j4 = fromIndex$iv$iv;
                    long resultLimit$iv2 = ($this$commonIndexOf$iv.size() - ((long) bytesSize$iv2)) + 1;
                    while (offset$iv5 < resultLimit$iv2) {
                        byte[] data$iv2 = s$iv5.data;
                        Buffer $this$commonIndexOf$iv2 = $this$commonIndexOf$iv;
                        long offset$iv6 = offset$iv4;
                        byte[] targetByteArray$iv4 = targetByteArray$iv3;
                        int segmentLimit$iv2 = (int) Math.min((long) s$iv5.limit, (((long) s$iv5.pos) + resultLimit$iv2) - offset$iv5);
                        int i3 = (int) ((((long) s$iv5.pos) + fromIndex$iv) - offset$iv5);
                        if (i3 < segmentLimit$iv2) {
                            while (true) {
                                int pos$iv2 = i3;
                                i3++;
                                if (data$iv2[pos$iv2] == b0$iv3) {
                                    b0$iv = b0$iv3;
                                    targetByteArray$iv = targetByteArray$iv4;
                                    if (_BufferKt.rangeEquals(s$iv5, pos$iv2 + 1, targetByteArray$iv, 1, bytesSize$iv2)) {
                                        return ((long) (pos$iv2 - s$iv5.pos)) + offset$iv5;
                                    }
                                } else {
                                    b0$iv = b0$iv3;
                                    targetByteArray$iv = targetByteArray$iv4;
                                }
                                if (i3 >= segmentLimit$iv2) {
                                    break;
                                }
                                targetByteArray$iv4 = targetByteArray$iv;
                                b0$iv3 = b0$iv;
                            }
                        } else {
                            b0$iv = b0$iv3;
                            targetByteArray$iv = targetByteArray$iv4;
                        }
                        offset$iv5 += (long) (s$iv5.limit - s$iv5.pos);
                        fromIndex$iv = offset$iv5;
                        Segment segment7 = s$iv5.next;
                        Intrinsics.checkNotNull(segment7);
                        s$iv5 = segment7;
                        targetByteArray$iv3 = targetByteArray$iv;
                        $this$commonIndexOf$iv = $this$commonIndexOf$iv2;
                        b0$iv3 = b0$iv;
                        offset$iv4 = offset$iv6;
                    }
                    byte b = b0$iv3;
                    long j5 = offset$iv4;
                    byte[] bArr = targetByteArray$iv3;
                    return -1;
                }
            } else {
                throw new IllegalArgumentException(Intrinsics.stringPlus("fromIndex < 0: ", Long.valueOf(fromIndex$iv)).toString());
            }
        } else {
            throw new IllegalArgumentException("bytes is empty".toString());
        }
    }

    public long indexOfElement(ByteString targetBytes) {
        Intrinsics.checkNotNullParameter(targetBytes, "targetBytes");
        return indexOfElement(targetBytes, 0);
    }

    public long indexOfElement(ByteString targetBytes, long fromIndex) {
        ByteString byteString = targetBytes;
        Intrinsics.checkNotNullParameter(byteString, "targetBytes");
        Buffer $this$commonIndexOfElement$iv = this;
        int $i$f$commonIndexOfElement = 0;
        long fromIndex$iv = fromIndex;
        if (fromIndex$iv >= 0) {
            long fromIndex$iv$iv = fromIndex$iv;
            Buffer $this$seek$iv$iv = $this$commonIndexOfElement$iv;
            int limit$iv = false;
            Segment s$iv$iv = $this$seek$iv$iv.head;
            if (s$iv$iv == null) {
                Segment segment = null;
                return -1;
            } else if ($this$seek$iv$iv.size() - fromIndex$iv$iv < fromIndex$iv$iv) {
                long offset$iv$iv = $this$seek$iv$iv.size();
                while (offset$iv$iv > fromIndex$iv$iv) {
                    Segment segment2 = s$iv$iv.prev;
                    Intrinsics.checkNotNull(segment2);
                    s$iv$iv = segment2;
                    offset$iv$iv -= (long) (s$iv$iv.limit - s$iv$iv.pos);
                }
                Segment s$iv = s$iv$iv;
                long offset$iv = offset$iv$iv;
                if (s$iv == null) {
                    return -1;
                }
                Segment s$iv2 = s$iv;
                long offset$iv2 = offset$iv;
                if (targetBytes.size() == 2) {
                    byte b0$iv = byteString.getByte(0);
                    byte b1$iv = byteString.getByte(1);
                    Segment s$iv3 = s$iv2;
                    while (offset$iv2 < $this$commonIndexOfElement$iv.size()) {
                        int $i$f$commonIndexOfElement2 = $i$f$commonIndexOfElement;
                        byte[] data$iv = s$iv3.data;
                        Buffer $this$seek$iv$iv2 = $this$seek$iv$iv;
                        int $i$f$seek = limit$iv;
                        Segment s$iv$iv2 = s$iv$iv;
                        int limit$iv2 = s$iv3.limit;
                        for (int pos$iv = (int) ((((long) s$iv3.pos) + fromIndex$iv) - offset$iv2); pos$iv < limit$iv2; pos$iv++) {
                            byte b$iv = data$iv[pos$iv];
                            if (b$iv == b0$iv || b$iv == b1$iv) {
                                byte[] bArr = data$iv;
                                long j = fromIndex$iv;
                                return ((long) (pos$iv - s$iv3.pos)) + offset$iv2;
                            }
                        }
                        long j2 = fromIndex$iv;
                        offset$iv2 += (long) (s$iv3.limit - s$iv3.pos);
                        fromIndex$iv = offset$iv2;
                        Segment segment3 = s$iv3.next;
                        Intrinsics.checkNotNull(segment3);
                        s$iv3 = segment3;
                        s$iv$iv = s$iv$iv2;
                        $i$f$commonIndexOfElement = $i$f$commonIndexOfElement2;
                        $this$seek$iv$iv = $this$seek$iv$iv2;
                        limit$iv = $i$f$seek;
                    }
                    long j3 = fromIndex$iv;
                    Buffer buffer = $this$seek$iv$iv;
                    int i = limit$iv;
                    Segment segment4 = s$iv$iv;
                } else {
                    Buffer buffer2 = $this$seek$iv$iv;
                    Segment segment5 = s$iv$iv;
                    byte[] targetByteArray$iv = targetBytes.internalArray$okio();
                    Segment s$iv4 = s$iv2;
                    while (offset$iv2 < $this$commonIndexOfElement$iv.size()) {
                        byte[] data$iv2 = s$iv4.data;
                        int pos$iv2 = (int) ((((long) s$iv4.pos) + fromIndex$iv) - offset$iv2);
                        int limit$iv3 = s$iv4.limit;
                        while (pos$iv2 < limit$iv3) {
                            byte b$iv2 = data$iv2[pos$iv2];
                            int length = targetByteArray$iv.length;
                            long fromIndex$iv2 = fromIndex$iv;
                            int i2 = 0;
                            while (i2 < length) {
                                byte t$iv = targetByteArray$iv[i2];
                                i2++;
                                if (b$iv2 == t$iv) {
                                    byte[] bArr2 = targetByteArray$iv;
                                    return ((long) (pos$iv2 - s$iv4.pos)) + offset$iv2;
                                }
                            }
                            pos$iv2++;
                            fromIndex$iv = fromIndex$iv2;
                        }
                        byte[] targetByteArray$iv2 = targetByteArray$iv;
                        long j4 = fromIndex$iv;
                        offset$iv2 += (long) (s$iv4.limit - s$iv4.pos);
                        fromIndex$iv = offset$iv2;
                        Segment segment6 = s$iv4.next;
                        Intrinsics.checkNotNull(segment6);
                        s$iv4 = segment6;
                        targetByteArray$iv = targetByteArray$iv2;
                    }
                    long j5 = fromIndex$iv;
                    Segment segment7 = s$iv4;
                }
                return -1;
            } else {
                Buffer buffer3 = $this$seek$iv$iv;
                long offset$iv$iv2 = 0;
                while (true) {
                    long nextOffset$iv$iv = ((long) (s$iv$iv.limit - s$iv$iv.pos)) + offset$iv$iv2;
                    if (nextOffset$iv$iv > fromIndex$iv$iv) {
                        break;
                    }
                    Buffer $this$commonIndexOfElement$iv2 = $this$commonIndexOfElement$iv;
                    long j6 = offset$iv$iv2;
                    Segment segment8 = s$iv$iv.next;
                    Intrinsics.checkNotNull(segment8);
                    s$iv$iv = segment8;
                    offset$iv$iv2 = nextOffset$iv$iv;
                    byteString = targetBytes;
                    $this$commonIndexOfElement$iv = $this$commonIndexOfElement$iv2;
                }
                Segment s$iv5 = s$iv$iv;
                long offset$iv3 = offset$iv$iv2;
                if (s$iv5 == null) {
                    return -1;
                }
                Segment s$iv6 = s$iv5;
                long offset$iv4 = offset$iv3;
                if (targetBytes.size() == 2) {
                    byte b0$iv2 = byteString.getByte(0);
                    byte b1$iv2 = byteString.getByte(1);
                    while (offset$iv4 < $this$commonIndexOfElement$iv.size()) {
                        byte[] data$iv3 = s$iv6.data;
                        Segment s$iv7 = s$iv5;
                        long offset$iv$iv3 = offset$iv$iv2;
                        int pos$iv3 = (int) ((((long) s$iv6.pos) + fromIndex$iv) - offset$iv4);
                        int limit$iv4 = s$iv6.limit;
                        while (pos$iv3 < limit$iv4) {
                            byte b$iv3 = data$iv3[pos$iv3];
                            if (b$iv3 == b0$iv2 || b$iv3 == b1$iv2) {
                                byte[] bArr3 = data$iv3;
                                long j7 = fromIndex$iv;
                                int i3 = pos$iv3;
                                return ((long) (pos$iv3 - s$iv6.pos)) + offset$iv4;
                            }
                            pos$iv3++;
                        }
                        long j8 = fromIndex$iv;
                        int i4 = pos$iv3;
                        offset$iv4 += (long) (s$iv6.limit - s$iv6.pos);
                        Segment segment9 = s$iv6.next;
                        Intrinsics.checkNotNull(segment9);
                        s$iv6 = segment9;
                        ByteString byteString2 = targetBytes;
                        fromIndex$iv = offset$iv4;
                        s$iv5 = s$iv7;
                        offset$iv$iv2 = offset$iv$iv3;
                    }
                    long j9 = fromIndex$iv;
                    long j10 = offset$iv$iv2;
                    Buffer buffer4 = $this$commonIndexOfElement$iv;
                } else {
                    long j11 = offset$iv$iv2;
                    byte[] targetByteArray$iv3 = targetBytes.internalArray$okio();
                    while (offset$iv4 < $this$commonIndexOfElement$iv.size()) {
                        byte[] data$iv4 = s$iv6.data;
                        int pos$iv4 = (int) ((((long) s$iv6.pos) + fromIndex$iv) - offset$iv4);
                        int limit$iv5 = s$iv6.limit;
                        while (pos$iv4 < limit$iv5) {
                            byte b$iv4 = data$iv4[pos$iv4];
                            int length2 = targetByteArray$iv3.length;
                            Buffer $this$commonIndexOfElement$iv3 = $this$commonIndexOfElement$iv;
                            int i5 = 0;
                            while (i5 < length2) {
                                byte[] data$iv5 = data$iv4;
                                byte t$iv2 = targetByteArray$iv3[i5];
                                i5++;
                                if (b$iv4 == t$iv2) {
                                    byte[] bArr4 = targetByteArray$iv3;
                                    return ((long) (pos$iv4 - s$iv6.pos)) + offset$iv4;
                                }
                                data$iv4 = data$iv5;
                            }
                            byte[] bArr5 = data$iv4;
                            pos$iv4++;
                            $this$commonIndexOfElement$iv = $this$commonIndexOfElement$iv3;
                        }
                        byte[] targetByteArray$iv4 = targetByteArray$iv3;
                        byte[] bArr6 = data$iv4;
                        offset$iv4 += (long) (s$iv6.limit - s$iv6.pos);
                        fromIndex$iv = offset$iv4;
                        Segment segment10 = s$iv6.next;
                        Intrinsics.checkNotNull(segment10);
                        s$iv6 = segment10;
                        $this$commonIndexOfElement$iv = $this$commonIndexOfElement$iv;
                        targetByteArray$iv3 = targetByteArray$iv4;
                    }
                    Buffer buffer5 = $this$commonIndexOfElement$iv;
                }
                return -1;
            }
        } else {
            throw new IllegalArgumentException(Intrinsics.stringPlus("fromIndex < 0: ", Long.valueOf(fromIndex$iv)).toString());
        }
    }

    public boolean rangeEquals(long offset, ByteString bytes) {
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        return rangeEquals(offset, bytes, 0, bytes.size());
    }

    public boolean rangeEquals(long offset, ByteString bytes, int bytesOffset, int byteCount) {
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        if (offset < 0 || bytesOffset < 0 || byteCount < 0 || size() - offset < ((long) byteCount) || bytes.size() - bytesOffset < byteCount) {
            return false;
        }
        if (byteCount <= 0) {
            return true;
        }
        int i = 0;
        do {
            int i$iv = i;
            i++;
            if (getByte(((long) i$iv) + offset) != bytes.getByte(bytesOffset + i$iv)) {
                return false;
            }
        } while (i < byteCount);
        return true;
    }

    public void flush() {
    }

    public boolean isOpen() {
        return true;
    }

    public void close() {
    }

    public Timeout timeout() {
        return Timeout.NONE;
    }

    public final ByteString md5() {
        return digest("MD5");
    }

    public final ByteString sha1() {
        return digest("SHA-1");
    }

    public final ByteString sha256() {
        return digest("SHA-256");
    }

    public final ByteString sha512() {
        return digest("SHA-512");
    }

    private final ByteString digest(String algorithm) {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        Segment head2 = this.head;
        if (head2 != null) {
            messageDigest.update(head2.data, head2.pos, head2.limit - head2.pos);
            Segment s = head2.next;
            Intrinsics.checkNotNull(s);
            while (s != head2) {
                messageDigest.update(s.data, s.pos, s.limit - s.pos);
                Segment segment = s.next;
                Intrinsics.checkNotNull(segment);
                s = segment;
            }
        }
        byte[] digest = messageDigest.digest();
        Intrinsics.checkNotNullExpressionValue(digest, "messageDigest.digest()");
        return new ByteString(digest);
    }

    public final ByteString hmacSha1(ByteString key) {
        Intrinsics.checkNotNullParameter(key, "key");
        return hmac("HmacSHA1", key);
    }

    public final ByteString hmacSha256(ByteString key) {
        Intrinsics.checkNotNullParameter(key, "key");
        return hmac("HmacSHA256", key);
    }

    public final ByteString hmacSha512(ByteString key) {
        Intrinsics.checkNotNullParameter(key, "key");
        return hmac("HmacSHA512", key);
    }

    private final ByteString hmac(String algorithm, ByteString key) {
        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key.internalArray$okio(), algorithm));
            Segment head2 = this.head;
            if (head2 != null) {
                mac.update(head2.data, head2.pos, head2.limit - head2.pos);
                Segment s = head2.next;
                Intrinsics.checkNotNull(s);
                while (s != head2) {
                    mac.update(s.data, s.pos, s.limit - s.pos);
                    Segment segment = s.next;
                    Intrinsics.checkNotNull(segment);
                    s = segment;
                }
            }
            byte[] doFinal = mac.doFinal();
            Intrinsics.checkNotNullExpressionValue(doFinal, "mac.doFinal()");
            return new ByteString(doFinal);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /* JADX WARNING: type inference failed for: r24v0, types: [java.lang.Object] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r24) {
        /*
            r23 = this;
            r0 = r24
            r1 = r23
            r2 = 0
            r4 = 1
            if (r1 != r0) goto L_0x000b
            r3 = r4
            goto L_0x0099
        L_0x000b:
            boolean r5 = r0 instanceof okio.Buffer
            if (r5 != 0) goto L_0x0012
            r3 = 0
            goto L_0x0099
        L_0x0012:
            long r5 = r1.size()
            r7 = r0
            okio.Buffer r7 = (okio.Buffer) r7
            long r7 = r7.size()
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 == 0) goto L_0x0024
            r3 = 0
            goto L_0x0099
        L_0x0024:
            long r5 = r1.size()
            r7 = 0
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 != 0) goto L_0x0031
            r3 = r4
            goto L_0x0099
        L_0x0031:
            okio.Segment r5 = r1.head
            kotlin.jvm.internal.Intrinsics.checkNotNull(r5)
            r6 = r0
            okio.Buffer r6 = (okio.Buffer) r6
            okio.Segment r6 = r6.head
            kotlin.jvm.internal.Intrinsics.checkNotNull(r6)
            int r9 = r5.pos
            int r10 = r6.pos
            r11 = 0
            r13 = 0
        L_0x0046:
            long r15 = r1.size()
            int r15 = (r11 > r15 ? 1 : (r11 == r15 ? 0 : -1))
            if (r15 >= 0) goto L_0x0098
            int r15 = r5.limit
            int r15 = r15 - r9
            int r3 = r6.limit
            int r3 = r3 - r10
            int r3 = java.lang.Math.min(r15, r3)
            long r13 = (long) r3
            int r3 = (r7 > r13 ? 1 : (r7 == r13 ? 0 : -1))
            if (r3 >= 0) goto L_0x007c
            r17 = r7
        L_0x005f:
            r19 = r17
            r21 = 1
            long r17 = r17 + r21
            byte[] r3 = r5.data
            int r15 = r9 + 1
            byte r3 = r3[r9]
            byte[] r9 = r6.data
            int r21 = r10 + 1
            byte r9 = r9[r10]
            if (r3 == r9) goto L_0x0075
            r3 = 0
            goto L_0x0099
        L_0x0075:
            int r3 = (r17 > r13 ? 1 : (r17 == r13 ? 0 : -1))
            r9 = r15
            r10 = r21
            if (r3 < 0) goto L_0x005f
        L_0x007c:
            int r3 = r5.limit
            if (r9 != r3) goto L_0x0089
            okio.Segment r3 = r5.next
            kotlin.jvm.internal.Intrinsics.checkNotNull(r3)
            int r5 = r3.pos
            r9 = r5
            r5 = r3
        L_0x0089:
            int r3 = r6.limit
            if (r10 != r3) goto L_0x0096
            okio.Segment r3 = r6.next
            kotlin.jvm.internal.Intrinsics.checkNotNull(r3)
            int r6 = r3.pos
            r10 = r6
            r6 = r3
        L_0x0096:
            long r11 = r11 + r13
            goto L_0x0046
        L_0x0098:
            r3 = r4
        L_0x0099:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        Segment s$iv = this.head;
        if (s$iv == null) {
            return 0;
        }
        int result$iv = 1;
        do {
            int limit$iv = s$iv.limit;
            for (int pos$iv = s$iv.pos; pos$iv < limit$iv; pos$iv++) {
                result$iv = (result$iv * 31) + s$iv.data[pos$iv];
            }
            Segment segment = s$iv.next;
            Intrinsics.checkNotNull(segment);
            s$iv = segment;
        } while (s$iv != this.head);
        return result$iv;
    }

    public String toString() {
        return snapshot().toString();
    }

    public final Buffer copy() {
        Buffer result$iv = new Buffer();
        if (size() != 0) {
            Segment head$iv = this.head;
            Intrinsics.checkNotNull(head$iv);
            Segment headCopy$iv = head$iv.sharedCopy();
            result$iv.head = headCopy$iv;
            headCopy$iv.prev = headCopy$iv;
            headCopy$iv.next = headCopy$iv.prev;
            for (Segment s$iv = head$iv.next; s$iv != head$iv; s$iv = s$iv.next) {
                Segment segment = headCopy$iv.prev;
                Intrinsics.checkNotNull(segment);
                Intrinsics.checkNotNull(s$iv);
                segment.push(s$iv.sharedCopy());
            }
            result$iv.setSize$okio(size());
        }
        return result$iv;
    }

    public Buffer clone() {
        return copy();
    }

    public final ByteString snapshot() {
        if (size() <= 2147483647L) {
            return snapshot((int) size());
        }
        throw new IllegalStateException(Intrinsics.stringPlus("size > Int.MAX_VALUE: ", Long.valueOf(size())).toString());
    }

    public final ByteString snapshot(int byteCount) {
        if (byteCount == 0) {
            return ByteString.EMPTY;
        }
        _UtilKt.checkOffsetAndCount(size(), 0, (long) byteCount);
        int offset$iv = 0;
        int segmentCount$iv = 0;
        Segment s$iv = this.head;
        while (offset$iv < byteCount) {
            Intrinsics.checkNotNull(s$iv);
            if (s$iv.limit != s$iv.pos) {
                offset$iv += s$iv.limit - s$iv.pos;
                segmentCount$iv++;
                s$iv = s$iv.next;
            } else {
                throw new AssertionError("s.limit == s.pos");
            }
        }
        byte[][] segments$iv = new byte[segmentCount$iv][];
        int[] directory$iv = new int[(segmentCount$iv * 2)];
        int offset$iv2 = 0;
        int segmentCount$iv2 = 0;
        Segment s$iv2 = this.head;
        while (offset$iv2 < byteCount) {
            Intrinsics.checkNotNull(s$iv2);
            segments$iv[segmentCount$iv2] = s$iv2.data;
            offset$iv2 += s$iv2.limit - s$iv2.pos;
            directory$iv[segmentCount$iv2] = Math.min(offset$iv2, byteCount);
            directory$iv[((Object[]) segments$iv).length + segmentCount$iv2] = s$iv2.pos;
            s$iv2.shared = true;
            segmentCount$iv2++;
            s$iv2 = s$iv2.next;
        }
        return new SegmentedByteString(segments$iv, directory$iv);
    }

    public static /* synthetic */ UnsafeCursor readUnsafe$default(Buffer buffer, UnsafeCursor unsafeCursor, int i, Object obj) {
        if ((i & 1) != 0) {
            unsafeCursor = _UtilKt.getDEFAULT__new_UnsafeCursor();
        }
        return buffer.readUnsafe(unsafeCursor);
    }

    public final UnsafeCursor readUnsafe(UnsafeCursor unsafeCursor) {
        Intrinsics.checkNotNullParameter(unsafeCursor, "unsafeCursor");
        return _BufferKt.commonReadUnsafe(this, unsafeCursor);
    }

    public static /* synthetic */ UnsafeCursor readAndWriteUnsafe$default(Buffer buffer, UnsafeCursor unsafeCursor, int i, Object obj) {
        if ((i & 1) != 0) {
            unsafeCursor = _UtilKt.getDEFAULT__new_UnsafeCursor();
        }
        return buffer.readAndWriteUnsafe(unsafeCursor);
    }

    public final UnsafeCursor readAndWriteUnsafe(UnsafeCursor unsafeCursor) {
        Intrinsics.checkNotNullParameter(unsafeCursor, "unsafeCursor");
        return _BufferKt.commonReadAndWriteUnsafe(this, unsafeCursor);
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to operator function", replaceWith = @ReplaceWith(expression = "this[index]", imports = {}))
    /* renamed from: -deprecated_getByte  reason: not valid java name */
    public final byte m0deprecated_getByte(long index) {
        return getByte(index);
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "size", imports = {}))
    /* renamed from: -deprecated_size  reason: not valid java name */
    public final long m1deprecated_size() {
        return this.size;
    }

    @Metadata(d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0014\u001a\u00020\u0015H\u0016J\u000e\u0010\u0016\u001a\u00020\n2\u0006\u0010\u0017\u001a\u00020\bJ\u0006\u0010\u0018\u001a\u00020\bJ\u000e\u0010\u0019\u001a\u00020\n2\u0006\u0010\u001a\u001a\u00020\nJ\u000e\u0010\u001b\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nR\u0014\u0010\u0003\u001a\u0004\u0018\u00010\u00048\u0006@\u0006X\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\u0004\u0018\u00010\u00068\u0006@\u0006X\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0007\u001a\u00020\b8\u0006@\u0006X\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\t\u001a\u00020\n8\u0006@\u0006X\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u000b\u001a\u00020\f8\u0006@\u0006X\u000e¢\u0006\u0002\n\u0000R\u001c\u0010\r\u001a\u0004\u0018\u00010\u000eX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u0012\u0010\u0013\u001a\u00020\b8\u0006@\u0006X\u000e¢\u0006\u0002\n\u0000¨\u0006\u001c"}, d2 = {"Lokio/Buffer$UnsafeCursor;", "Ljava/io/Closeable;", "()V", "buffer", "Lokio/Buffer;", "data", "", "end", "", "offset", "", "readWrite", "", "segment", "Lokio/Segment;", "getSegment$okio", "()Lokio/Segment;", "setSegment$okio", "(Lokio/Segment;)V", "start", "close", "", "expandBuffer", "minByteCount", "next", "resizeBuffer", "newSize", "seek", "okio"}, k = 1, mv = {1, 5, 1}, xi = 48)
    /* compiled from: Buffer.kt */
    public static final class UnsafeCursor implements Closeable {
        public Buffer buffer;
        public byte[] data;
        public int end = -1;
        public long offset = -1;
        public boolean readWrite;
        private Segment segment;
        public int start = -1;

        public final Segment getSegment$okio() {
            return this.segment;
        }

        public final void setSegment$okio(Segment segment2) {
            this.segment = segment2;
        }

        public final int next() {
            long j = this.offset;
            Buffer buffer2 = this.buffer;
            Intrinsics.checkNotNull(buffer2);
            if (j != buffer2.size()) {
                long j2 = this.offset;
                return seek(j2 == -1 ? 0 : j2 + ((long) (this.end - this.start)));
            }
            throw new IllegalStateException("no more bytes".toString());
        }

        public final int seek(long offset2) {
            long nextOffset$iv;
            Segment next$iv;
            long j = offset2;
            Buffer buffer$iv = this.buffer;
            if (buffer$iv == null) {
                throw new IllegalStateException("not attached to a buffer".toString());
            } else if (j < -1 || j > buffer$iv.size()) {
                throw new ArrayIndexOutOfBoundsException("offset=" + j + " > size=" + buffer$iv.size());
            } else if (j == -1 || j == buffer$iv.size()) {
                setSegment$okio((Segment) null);
                this.offset = j;
                this.data = null;
                this.start = -1;
                this.end = -1;
                return -1;
            } else {
                long min$iv = 0;
                long max$iv = buffer$iv.size();
                Segment head$iv = buffer$iv.head;
                Segment tail$iv = buffer$iv.head;
                if (getSegment$okio() != null) {
                    long j2 = this.offset;
                    int i = this.start;
                    Segment segment$okio = getSegment$okio();
                    Intrinsics.checkNotNull(segment$okio);
                    long segmentOffset$iv = j2 - ((long) (i - segment$okio.pos));
                    if (segmentOffset$iv > j) {
                        max$iv = segmentOffset$iv;
                        tail$iv = getSegment$okio();
                    } else {
                        min$iv = segmentOffset$iv;
                        head$iv = getSegment$okio();
                    }
                }
                if (max$iv - j > j - min$iv) {
                    next$iv = head$iv;
                    nextOffset$iv = min$iv;
                    while (true) {
                        Intrinsics.checkNotNull(next$iv);
                        if (j < ((long) (next$iv.limit - next$iv.pos)) + nextOffset$iv) {
                            break;
                        }
                        nextOffset$iv += (long) (next$iv.limit - next$iv.pos);
                        next$iv = next$iv.next;
                    }
                } else {
                    Segment next$iv2 = tail$iv;
                    long nextOffset$iv2 = max$iv;
                    while (nextOffset$iv > j) {
                        Intrinsics.checkNotNull(next$iv);
                        next$iv2 = next$iv.prev;
                        Intrinsics.checkNotNull(next$iv2);
                        nextOffset$iv2 = nextOffset$iv - ((long) (next$iv2.limit - next$iv2.pos));
                    }
                }
                if (this.readWrite) {
                    Intrinsics.checkNotNull(next$iv);
                    if (next$iv.shared) {
                        Segment unsharedNext$iv = next$iv.unsharedCopy();
                        if (buffer$iv.head == next$iv) {
                            buffer$iv.head = unsharedNext$iv;
                        }
                        next$iv = next$iv.push(unsharedNext$iv);
                        Segment segment2 = next$iv.prev;
                        Intrinsics.checkNotNull(segment2);
                        segment2.pop();
                    }
                }
                setSegment$okio(next$iv);
                this.offset = j;
                Intrinsics.checkNotNull(next$iv);
                this.data = next$iv.data;
                long j3 = min$iv;
                this.start = next$iv.pos + ((int) (j - nextOffset$iv));
                int i2 = next$iv.limit;
                this.end = i2;
                return i2 - this.start;
            }
        }

        public final long resizeBuffer(long newSize) {
            long j = newSize;
            Buffer buffer$iv = this.buffer;
            if (buffer$iv == null) {
                throw new IllegalStateException("not attached to a buffer".toString());
            } else if (this.readWrite) {
                long oldSize$iv = buffer$iv.size();
                int segmentBytesToAdd$iv = 1;
                long j2 = 0;
                if (j <= oldSize$iv) {
                    if (j < 0) {
                        segmentBytesToAdd$iv = 0;
                    }
                    if (segmentBytesToAdd$iv != 0) {
                        long bytesToSubtract$iv = oldSize$iv - j;
                        while (true) {
                            if (bytesToSubtract$iv <= 0) {
                                break;
                            }
                            Segment segment2 = buffer$iv.head;
                            Intrinsics.checkNotNull(segment2);
                            Segment tail$iv = segment2.prev;
                            Intrinsics.checkNotNull(tail$iv);
                            int tailSize$iv = tail$iv.limit - tail$iv.pos;
                            if (((long) tailSize$iv) > bytesToSubtract$iv) {
                                tail$iv.limit -= (int) bytesToSubtract$iv;
                                break;
                            }
                            buffer$iv.head = tail$iv.pop();
                            SegmentPool.recycle(tail$iv);
                            bytesToSubtract$iv -= (long) tailSize$iv;
                        }
                        setSegment$okio((Segment) null);
                        this.offset = j;
                        this.data = null;
                        this.start = -1;
                        this.end = -1;
                    } else {
                        throw new IllegalArgumentException(Intrinsics.stringPlus("newSize < 0: ", Long.valueOf(newSize)).toString());
                    }
                } else if (j > oldSize$iv) {
                    boolean needsToSeek$iv = true;
                    long bytesToAdd$iv = j - oldSize$iv;
                    while (bytesToAdd$iv > j2) {
                        Segment tail$iv2 = buffer$iv.writableSegment$okio(segmentBytesToAdd$iv);
                        int segmentBytesToAdd$iv2 = (int) Math.min(bytesToAdd$iv, (long) (8192 - tail$iv2.limit));
                        tail$iv2.limit += segmentBytesToAdd$iv2;
                        bytesToAdd$iv -= (long) segmentBytesToAdd$iv2;
                        if (needsToSeek$iv) {
                            setSegment$okio(tail$iv2);
                            this.offset = oldSize$iv;
                            this.data = tail$iv2.data;
                            this.start = tail$iv2.limit - segmentBytesToAdd$iv2;
                            this.end = tail$iv2.limit;
                            needsToSeek$iv = false;
                            segmentBytesToAdd$iv = 1;
                            j2 = 0;
                        } else {
                            segmentBytesToAdd$iv = 1;
                            j2 = 0;
                        }
                    }
                }
                buffer$iv.setSize$okio(j);
                return oldSize$iv;
            } else {
                throw new IllegalStateException("resizeBuffer() only permitted for read/write buffers".toString());
            }
        }

        public final long expandBuffer(int minByteCount) {
            boolean z = true;
            if (minByteCount > 0) {
                if (minByteCount > 8192) {
                    z = false;
                }
                if (z) {
                    Buffer buffer$iv = this.buffer;
                    if (buffer$iv == null) {
                        throw new IllegalStateException("not attached to a buffer".toString());
                    } else if (this.readWrite) {
                        long oldSize$iv = buffer$iv.size();
                        Segment tail$iv = buffer$iv.writableSegment$okio(minByteCount);
                        int result$iv = 8192 - tail$iv.limit;
                        tail$iv.limit = 8192;
                        buffer$iv.setSize$okio(((long) result$iv) + oldSize$iv);
                        setSegment$okio(tail$iv);
                        this.offset = oldSize$iv;
                        this.data = tail$iv.data;
                        this.start = 8192 - result$iv;
                        this.end = 8192;
                        return (long) result$iv;
                    } else {
                        throw new IllegalStateException("expandBuffer() only permitted for read/write buffers".toString());
                    }
                } else {
                    throw new IllegalArgumentException(Intrinsics.stringPlus("minByteCount > Segment.SIZE: ", Integer.valueOf(minByteCount)).toString());
                }
            } else {
                throw new IllegalArgumentException(Intrinsics.stringPlus("minByteCount <= 0: ", Integer.valueOf(minByteCount)).toString());
            }
        }

        public void close() {
            if (this.buffer != null) {
                this.buffer = null;
                setSegment$okio((Segment) null);
                this.offset = -1;
                this.data = null;
                this.start = -1;
                this.end = -1;
                return;
            }
            throw new IllegalStateException("not attached to a buffer".toString());
        }
    }
}

package okio;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import okio.internal._SegmentedByteStringKt;

@Metadata(d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\u0015\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\f\n\u0002\u0010\u0005\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0000\u0018\u00002\u00020\u0001B\u001d\b\u0000\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\b\u0010\r\u001a\u00020\u000eH\u0016J\b\u0010\u000f\u001a\u00020\u0010H\u0016J\b\u0010\u0011\u001a\u00020\u0010H\u0016J(\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0017\u001a\u00020\u00152\u0006\u0010\u0018\u001a\u00020\u0015H\u0016J\u0015\u0010\u0019\u001a\u00020\u00012\u0006\u0010\u001a\u001a\u00020\u0010H\u0010¢\u0006\u0002\b\u001bJ\u0013\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0002J\r\u0010 \u001a\u00020\u0015H\u0010¢\u0006\u0002\b!J\b\u0010\"\u001a\u00020\u0015H\u0016J\b\u0010#\u001a\u00020\u0010H\u0016J\u001d\u0010$\u001a\u00020\u00012\u0006\u0010\u001a\u001a\u00020\u00102\u0006\u0010%\u001a\u00020\u0001H\u0010¢\u0006\u0002\b&J\u0018\u0010'\u001a\u00020\u00152\u0006\u0010\u001e\u001a\u00020\u00042\u0006\u0010(\u001a\u00020\u0015H\u0016J\r\u0010)\u001a\u00020\u0004H\u0010¢\u0006\u0002\b*J\u0015\u0010+\u001a\u00020,2\u0006\u0010-\u001a\u00020\u0015H\u0010¢\u0006\u0002\b.J\u0018\u0010/\u001a\u00020\u00152\u0006\u0010\u001e\u001a\u00020\u00042\u0006\u0010(\u001a\u00020\u0015H\u0016J(\u00100\u001a\u00020\u001d2\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u001e\u001a\u00020\u00042\u0006\u00101\u001a\u00020\u00152\u0006\u0010\u0018\u001a\u00020\u0015H\u0016J(\u00100\u001a\u00020\u001d2\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u001e\u001a\u00020\u00012\u0006\u00101\u001a\u00020\u00152\u0006\u0010\u0018\u001a\u00020\u0015H\u0016J\u0010\u00102\u001a\u00020\u00102\u0006\u00103\u001a\u000204H\u0016J\u0018\u00105\u001a\u00020\u00012\u0006\u00106\u001a\u00020\u00152\u0006\u00107\u001a\u00020\u0015H\u0016J\b\u00108\u001a\u00020\u0001H\u0016J\b\u00109\u001a\u00020\u0001H\u0016J\b\u0010:\u001a\u00020\u0004H\u0016J\b\u0010;\u001a\u00020\u0001H\u0002J\b\u0010<\u001a\u00020\u0010H\u0016J\u0010\u0010=\u001a\u00020\u00132\u0006\u0010>\u001a\u00020?H\u0016J%\u0010=\u001a\u00020\u00132\u0006\u0010@\u001a\u00020A2\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0018\u001a\u00020\u0015H\u0010¢\u0006\u0002\bBJ\b\u0010C\u001a\u00020DH\u0002R\u0014\u0010\u0005\u001a\u00020\u0006X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u001c\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0004¢\u0006\n\n\u0002\u0010\f\u001a\u0004\b\n\u0010\u000b¨\u0006E"}, d2 = {"Lokio/SegmentedByteString;", "Lokio/ByteString;", "segments", "", "", "directory", "", "([[B[I)V", "getDirectory$okio", "()[I", "getSegments$okio", "()[[B", "[[B", "asByteBuffer", "Ljava/nio/ByteBuffer;", "base64", "", "base64Url", "copyInto", "", "offset", "", "target", "targetOffset", "byteCount", "digest", "algorithm", "digest$okio", "equals", "", "other", "", "getSize", "getSize$okio", "hashCode", "hex", "hmac", "key", "hmac$okio", "indexOf", "fromIndex", "internalArray", "internalArray$okio", "internalGet", "", "pos", "internalGet$okio", "lastIndexOf", "rangeEquals", "otherOffset", "string", "charset", "Ljava/nio/charset/Charset;", "substring", "beginIndex", "endIndex", "toAsciiLowercase", "toAsciiUppercase", "toByteArray", "toByteString", "toString", "write", "out", "Ljava/io/OutputStream;", "buffer", "Lokio/Buffer;", "write$okio", "writeReplace", "Ljava/lang/Object;", "okio"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* compiled from: SegmentedByteString.kt */
public final class SegmentedByteString extends ByteString {
    private final transient int[] directory;
    private final transient byte[][] segments;

    public final byte[][] getSegments$okio() {
        return this.segments;
    }

    public final int[] getDirectory$okio() {
        return this.directory;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SegmentedByteString(byte[][] segments2, int[] directory2) {
        super(ByteString.EMPTY.getData$okio());
        Intrinsics.checkNotNullParameter(segments2, "segments");
        Intrinsics.checkNotNullParameter(directory2, "directory");
        this.segments = segments2;
        this.directory = directory2;
    }

    public String string(Charset charset) {
        Intrinsics.checkNotNullParameter(charset, "charset");
        return toByteString().string(charset);
    }

    public String base64() {
        return toByteString().base64();
    }

    public String hex() {
        return toByteString().hex();
    }

    public ByteString toAsciiLowercase() {
        return toByteString().toAsciiLowercase();
    }

    public ByteString toAsciiUppercase() {
        return toByteString().toAsciiUppercase();
    }

    public ByteString digest$okio(String algorithm) {
        Intrinsics.checkNotNullParameter(algorithm, "algorithm");
        MessageDigest $this$digest_u24lambda_u2d1 = MessageDigest.getInstance(algorithm);
        int segmentCount$iv = ((Object[]) getSegments$okio()).length;
        int pos$iv = 0;
        for (int s$iv = 0; s$iv < segmentCount$iv; s$iv++) {
            int segmentPos$iv = getDirectory$okio()[segmentCount$iv + s$iv];
            int nextSegmentOffset$iv = getDirectory$okio()[s$iv];
            $this$digest_u24lambda_u2d1.update(getSegments$okio()[s$iv], segmentPos$iv, nextSegmentOffset$iv - pos$iv);
            pos$iv = nextSegmentOffset$iv;
        }
        byte[] digestBytes = $this$digest_u24lambda_u2d1.digest();
        Intrinsics.checkNotNullExpressionValue(digestBytes, "digestBytes");
        return new ByteString(digestBytes);
    }

    public ByteString hmac$okio(String algorithm, ByteString key) {
        Intrinsics.checkNotNullParameter(algorithm, "algorithm");
        Intrinsics.checkNotNullParameter(key, "key");
        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key.toByteArray(), algorithm));
            int segmentCount$iv = ((Object[]) getSegments$okio()).length;
            int pos$iv = 0;
            for (int s$iv = 0; s$iv < segmentCount$iv; s$iv++) {
                int segmentPos$iv = getDirectory$okio()[segmentCount$iv + s$iv];
                int nextSegmentOffset$iv = getDirectory$okio()[s$iv];
                mac.update(getSegments$okio()[s$iv], segmentPos$iv, nextSegmentOffset$iv - pos$iv);
                pos$iv = nextSegmentOffset$iv;
            }
            byte[] doFinal = mac.doFinal();
            Intrinsics.checkNotNullExpressionValue(doFinal, "mac.doFinal()");
            return new ByteString(doFinal);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String base64Url() {
        return toByteString().base64Url();
    }

    public ByteString substring(int beginIndex, int endIndex) {
        int i = beginIndex;
        int endIndex$iv = _UtilKt.resolveDefaultParameter(this, endIndex);
        int i2 = 1;
        if (i >= 0) {
            if (endIndex$iv <= size()) {
                int subLen$iv = endIndex$iv - i;
                if (!(subLen$iv >= 0)) {
                    throw new IllegalArgumentException(("endIndex=" + endIndex$iv + " < beginIndex=" + i).toString());
                } else if (i == 0 && endIndex$iv == size()) {
                    return this;
                } else {
                    if (i == endIndex$iv) {
                        return ByteString.EMPTY;
                    }
                    int beginSegment$iv = _SegmentedByteStringKt.segment(this, i);
                    int endSegment$iv = _SegmentedByteStringKt.segment(this, endIndex$iv - 1);
                    byte[][] newSegments$iv = (byte[][]) ArraysKt.copyOfRange((T[]) (Object[]) getSegments$okio(), beginSegment$iv, endSegment$iv + 1);
                    int[] newDirectory$iv = new int[(((Object[]) newSegments$iv).length * 2)];
                    int index$iv = 0;
                    if (beginSegment$iv <= endSegment$iv) {
                        int i3 = beginSegment$iv;
                        while (true) {
                            int s$iv = i3;
                            i3 += i2;
                            newDirectory$iv[index$iv] = Math.min(getDirectory$okio()[s$iv] - i, subLen$iv);
                            newDirectory$iv[index$iv + ((Object[]) newSegments$iv).length] = getDirectory$okio()[((Object[]) getSegments$okio()).length + s$iv];
                            index$iv++;
                            if (s$iv == endSegment$iv) {
                                break;
                            }
                            i2 = 1;
                        }
                    }
                    int segmentOffset$iv = beginSegment$iv == 0 ? 0 : getDirectory$okio()[beginSegment$iv - 1];
                    int length = ((Object[]) newSegments$iv).length;
                    newDirectory$iv[length] = newDirectory$iv[length] + (i - segmentOffset$iv);
                    return new SegmentedByteString(newSegments$iv, newDirectory$iv);
                }
            } else {
                throw new IllegalArgumentException(("endIndex=" + endIndex$iv + " > length(" + size() + ')').toString());
            }
        } else {
            throw new IllegalArgumentException(("beginIndex=" + i + " < 0").toString());
        }
    }

    public byte internalGet$okio(int pos) {
        _UtilKt.checkOffsetAndCount((long) getDirectory$okio()[((Object[]) getSegments$okio()).length - 1], (long) pos, 1);
        int segment$iv = _SegmentedByteStringKt.segment(this, pos);
        return getSegments$okio()[segment$iv][(pos - (segment$iv == 0 ? 0 : getDirectory$okio()[segment$iv - 1])) + getDirectory$okio()[((Object[]) getSegments$okio()).length + segment$iv]];
    }

    public int getSize$okio() {
        return getDirectory$okio()[((Object[]) getSegments$okio()).length - 1];
    }

    public byte[] toByteArray() {
        byte[] result$iv = new byte[size()];
        int resultPos$iv = 0;
        int segmentCount$iv$iv = ((Object[]) getSegments$okio()).length;
        int pos$iv$iv = 0;
        for (int s$iv$iv = 0; s$iv$iv < segmentCount$iv$iv; s$iv$iv++) {
            int segmentPos$iv$iv = getDirectory$okio()[segmentCount$iv$iv + s$iv$iv];
            int nextSegmentOffset$iv$iv = getDirectory$okio()[s$iv$iv];
            int byteCount$iv = nextSegmentOffset$iv$iv - pos$iv$iv;
            int offset$iv = segmentPos$iv$iv;
            ArraysKt.copyInto(getSegments$okio()[s$iv$iv], result$iv, resultPos$iv, offset$iv, offset$iv + byteCount$iv);
            resultPos$iv += byteCount$iv;
            pos$iv$iv = nextSegmentOffset$iv$iv;
        }
        return result$iv;
    }

    public ByteBuffer asByteBuffer() {
        ByteBuffer asReadOnlyBuffer = ByteBuffer.wrap(toByteArray()).asReadOnlyBuffer();
        Intrinsics.checkNotNullExpressionValue(asReadOnlyBuffer, "wrap(toByteArray()).asReadOnlyBuffer()");
        return asReadOnlyBuffer;
    }

    public void write(OutputStream out) throws IOException {
        Intrinsics.checkNotNullParameter(out, "out");
        int segmentCount$iv = ((Object[]) getSegments$okio()).length;
        int pos$iv = 0;
        for (int s$iv = 0; s$iv < segmentCount$iv; s$iv++) {
            int segmentPos$iv = getDirectory$okio()[segmentCount$iv + s$iv];
            int nextSegmentOffset$iv = getDirectory$okio()[s$iv];
            out.write(getSegments$okio()[s$iv], segmentPos$iv, nextSegmentOffset$iv - pos$iv);
            pos$iv = nextSegmentOffset$iv;
        }
    }

    public void write$okio(Buffer buffer, int offset, int byteCount) {
        SegmentedByteString $this$commonWrite$iv;
        Buffer buffer2 = buffer;
        int i = offset;
        int i2 = byteCount;
        Intrinsics.checkNotNullParameter(buffer2, "buffer");
        SegmentedByteString $this$commonWrite$iv2 = this;
        int endIndex$iv$iv = i + i2;
        SegmentedByteString $this$forEachSegment$iv$iv = $this$commonWrite$iv2;
        int s$iv$iv = _SegmentedByteStringKt.segment($this$forEachSegment$iv$iv, i);
        int pos$iv$iv = offset;
        while (pos$iv$iv < endIndex$iv$iv) {
            int segmentOffset$iv$iv = s$iv$iv == 0 ? 0 : $this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv - 1];
            int segmentPos$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[((Object[]) $this$forEachSegment$iv$iv.getSegments$okio()).length + s$iv$iv];
            int byteCount$iv$iv = Math.min(endIndex$iv$iv, segmentOffset$iv$iv + ($this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv] - segmentOffset$iv$iv)) - pos$iv$iv;
            int offset$iv = (pos$iv$iv - segmentOffset$iv$iv) + segmentPos$iv$iv;
            Segment segment$iv = new Segment($this$forEachSegment$iv$iv.getSegments$okio()[s$iv$iv], offset$iv, offset$iv + byteCount$iv$iv, true, false);
            if (buffer2.head == null) {
                Segment segment$iv2 = segment$iv;
                segment$iv2.prev = segment$iv2;
                $this$commonWrite$iv = $this$commonWrite$iv2;
                segment$iv2.next = segment$iv2.prev;
                buffer2.head = segment$iv2.next;
            } else {
                Segment segment$iv3 = segment$iv;
                $this$commonWrite$iv = $this$commonWrite$iv2;
                Segment segment = buffer2.head;
                Intrinsics.checkNotNull(segment);
                Segment segment2 = segment.prev;
                Intrinsics.checkNotNull(segment2);
                segment2.push(segment$iv3);
            }
            pos$iv$iv += byteCount$iv$iv;
            s$iv$iv++;
            int i3 = offset;
            $this$commonWrite$iv2 = $this$commonWrite$iv;
        }
        buffer2.setSize$okio(buffer.size() + ((long) i2));
    }

    public boolean rangeEquals(int offset, ByteString other, int otherOffset, int byteCount) {
        int i = offset;
        ByteString byteString = other;
        Intrinsics.checkNotNullParameter(byteString, "other");
        SegmentedByteString $this$commonRangeEquals$iv = this;
        if (i < 0) {
        } else if (i > $this$commonRangeEquals$iv.size() - byteCount) {
            SegmentedByteString segmentedByteString = $this$commonRangeEquals$iv;
        } else {
            int otherOffset$iv = otherOffset;
            int endIndex$iv$iv = i + byteCount;
            SegmentedByteString $this$forEachSegment$iv$iv = $this$commonRangeEquals$iv;
            int s$iv$iv = _SegmentedByteStringKt.segment($this$forEachSegment$iv$iv, i);
            int pos$iv$iv = offset;
            while (pos$iv$iv < endIndex$iv$iv) {
                int segmentOffset$iv$iv = s$iv$iv == 0 ? 0 : $this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv - 1];
                int segmentPos$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[((Object[]) $this$forEachSegment$iv$iv.getSegments$okio()).length + s$iv$iv];
                int byteCount$iv$iv = Math.min(endIndex$iv$iv, segmentOffset$iv$iv + ($this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv] - segmentOffset$iv$iv)) - pos$iv$iv;
                int byteCount$iv = byteCount$iv$iv;
                SegmentedByteString $this$commonRangeEquals$iv2 = $this$commonRangeEquals$iv;
                if (byteString.rangeEquals(otherOffset$iv, $this$forEachSegment$iv$iv.getSegments$okio()[s$iv$iv], (pos$iv$iv - segmentOffset$iv$iv) + segmentPos$iv$iv, byteCount$iv) == 0) {
                    return false;
                }
                otherOffset$iv += byteCount$iv;
                pos$iv$iv += byteCount$iv$iv;
                s$iv$iv++;
                int i2 = offset;
                $this$commonRangeEquals$iv = $this$commonRangeEquals$iv2;
            }
            return true;
        }
        return false;
    }

    public boolean rangeEquals(int offset, byte[] other, int otherOffset, int byteCount) {
        int i = offset;
        byte[] bArr = other;
        int i2 = otherOffset;
        Intrinsics.checkNotNullParameter(bArr, "other");
        if (i < 0 || i > size() - byteCount || i2 < 0 || i2 > bArr.length - byteCount) {
            return false;
        }
        int otherOffset$iv = otherOffset;
        int endIndex$iv$iv = i + byteCount;
        int s$iv$iv = _SegmentedByteStringKt.segment(this, i);
        int pos$iv$iv = offset;
        while (pos$iv$iv < endIndex$iv$iv) {
            int segmentOffset$iv$iv = s$iv$iv == 0 ? 0 : getDirectory$okio()[s$iv$iv - 1];
            int segmentPos$iv$iv = getDirectory$okio()[((Object[]) getSegments$okio()).length + s$iv$iv];
            int byteCount$iv$iv = Math.min(endIndex$iv$iv, segmentOffset$iv$iv + (getDirectory$okio()[s$iv$iv] - segmentOffset$iv$iv)) - pos$iv$iv;
            int byteCount$iv = byteCount$iv$iv;
            if (_UtilKt.arrayRangeEquals(getSegments$okio()[s$iv$iv], segmentPos$iv$iv + (pos$iv$iv - segmentOffset$iv$iv), bArr, otherOffset$iv, byteCount$iv) == 0) {
                return false;
            }
            otherOffset$iv += byteCount$iv;
            pos$iv$iv += byteCount$iv$iv;
            s$iv$iv++;
            int i3 = offset;
            int i4 = otherOffset;
        }
        return true;
    }

    public void copyInto(int offset, byte[] target, int targetOffset, int byteCount) {
        int i = offset;
        byte[] bArr = target;
        int i2 = byteCount;
        Intrinsics.checkNotNullParameter(bArr, TypedValues.AttributesType.S_TARGET);
        SegmentedByteString $this$commonCopyInto$iv = this;
        _UtilKt.checkOffsetAndCount((long) $this$commonCopyInto$iv.size(), (long) i, (long) i2);
        _UtilKt.checkOffsetAndCount((long) bArr.length, (long) targetOffset, (long) i2);
        int targetOffset$iv = targetOffset;
        int endIndex$iv$iv = i + i2;
        SegmentedByteString $this$forEachSegment$iv$iv = $this$commonCopyInto$iv;
        int s$iv$iv = _SegmentedByteStringKt.segment($this$forEachSegment$iv$iv, i);
        int pos$iv$iv = offset;
        while (pos$iv$iv < endIndex$iv$iv) {
            int segmentOffset$iv$iv = s$iv$iv == 0 ? 0 : $this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv - 1];
            int segmentPos$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[((Object[]) $this$forEachSegment$iv$iv.getSegments$okio()).length + s$iv$iv];
            int byteCount$iv$iv = Math.min(endIndex$iv$iv, segmentOffset$iv$iv + ($this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv] - segmentOffset$iv$iv)) - pos$iv$iv;
            int byteCount$iv = byteCount$iv$iv;
            int offset$iv = segmentPos$iv$iv + (pos$iv$iv - segmentOffset$iv$iv);
            ArraysKt.copyInto($this$forEachSegment$iv$iv.getSegments$okio()[s$iv$iv], bArr, targetOffset$iv, offset$iv, offset$iv + byteCount$iv);
            targetOffset$iv += byteCount$iv;
            pos$iv$iv += byteCount$iv$iv;
            s$iv$iv++;
            int i3 = offset;
            int i4 = byteCount;
            $this$commonCopyInto$iv = $this$commonCopyInto$iv;
        }
    }

    public int indexOf(byte[] other, int fromIndex) {
        Intrinsics.checkNotNullParameter(other, "other");
        return toByteString().indexOf(other, fromIndex);
    }

    public int lastIndexOf(byte[] other, int fromIndex) {
        Intrinsics.checkNotNullParameter(other, "other");
        return toByteString().lastIndexOf(other, fromIndex);
    }

    private final ByteString toByteString() {
        return new ByteString(toByteArray());
    }

    public byte[] internalArray$okio() {
        return toByteArray();
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ByteString)) {
            return false;
        }
        if (((ByteString) other).size() != size() || !rangeEquals(0, (ByteString) other, 0, size())) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result$iv = getHashCode$okio();
        if (result$iv == 0) {
            result$iv = 1;
            int segmentCount$iv$iv = ((Object[]) getSegments$okio()).length;
            int pos$iv$iv = 0;
            for (int s$iv$iv = 0; s$iv$iv < segmentCount$iv$iv; s$iv$iv++) {
                int segmentPos$iv$iv = getDirectory$okio()[segmentCount$iv$iv + s$iv$iv];
                int nextSegmentOffset$iv$iv = getDirectory$okio()[s$iv$iv];
                byte[] data$iv = getSegments$okio()[s$iv$iv];
                int offset$iv = segmentPos$iv$iv;
                int limit$iv = offset$iv + (nextSegmentOffset$iv$iv - pos$iv$iv);
                for (int i$iv = offset$iv; i$iv < limit$iv; i$iv++) {
                    result$iv = (result$iv * 31) + data$iv[i$iv];
                }
                pos$iv$iv = nextSegmentOffset$iv$iv;
            }
            setHashCode$okio(result$iv);
        }
        return result$iv;
    }

    public String toString() {
        return toByteString().toString();
    }

    private final Object writeReplace() {
        return toByteString();
    }
}

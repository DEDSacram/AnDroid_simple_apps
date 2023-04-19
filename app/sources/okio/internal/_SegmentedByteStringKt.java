package okio.internal;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import okio.Buffer;
import okio.ByteString;
import okio.Segment;
import okio.SegmentedByteString;
import okio._UtilKt;

@Metadata(d1 = {"\u0000T\n\u0000\n\u0002\u0010\b\n\u0002\u0010\u0015\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0005\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a$\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00012\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0000\u001a-\u0010\u0006\u001a\u00020\u0007*\u00020\b2\u0006\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u0001H\b\u001a\u0017\u0010\u000e\u001a\u00020\u000f*\u00020\b2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\b\u001a\r\u0010\u0012\u001a\u00020\u0001*\u00020\bH\b\u001a\r\u0010\u0013\u001a\u00020\u0001*\u00020\bH\b\u001a\u0015\u0010\u0014\u001a\u00020\u0015*\u00020\b2\u0006\u0010\u0016\u001a\u00020\u0001H\b\u001a-\u0010\u0017\u001a\u00020\u000f*\u00020\b2\u0006\u0010\t\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u000b2\u0006\u0010\u0018\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u0001H\b\u001a-\u0010\u0017\u001a\u00020\u000f*\u00020\b2\u0006\u0010\t\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u00192\u0006\u0010\u0018\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u0001H\b\u001a\u001d\u0010\u001a\u001a\u00020\u0019*\u00020\b2\u0006\u0010\u001b\u001a\u00020\u00012\u0006\u0010\u001c\u001a\u00020\u0001H\b\u001a\r\u0010\u001d\u001a\u00020\u000b*\u00020\bH\b\u001a%\u0010\u001e\u001a\u00020\u0007*\u00020\b2\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010\t\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u0001H\b\u001a]\u0010!\u001a\u00020\u0007*\u00020\b2K\u0010\"\u001aG\u0012\u0013\u0012\u00110\u000b¢\u0006\f\b$\u0012\b\b%\u0012\u0004\b\b(&\u0012\u0013\u0012\u00110\u0001¢\u0006\f\b$\u0012\b\b%\u0012\u0004\b\b(\t\u0012\u0013\u0012\u00110\u0001¢\u0006\f\b$\u0012\b\b%\u0012\u0004\b\b(\r\u0012\u0004\u0012\u00020\u00070#H\bø\u0001\u0000\u001aj\u0010!\u001a\u00020\u0007*\u00020\b2\u0006\u0010\u001b\u001a\u00020\u00012\u0006\u0010\u001c\u001a\u00020\u00012K\u0010\"\u001aG\u0012\u0013\u0012\u00110\u000b¢\u0006\f\b$\u0012\b\b%\u0012\u0004\b\b(&\u0012\u0013\u0012\u00110\u0001¢\u0006\f\b$\u0012\b\b%\u0012\u0004\b\b(\t\u0012\u0013\u0012\u00110\u0001¢\u0006\f\b$\u0012\b\b%\u0012\u0004\b\b(\r\u0012\u0004\u0012\u00020\u00070#H\b\u001a\u0014\u0010'\u001a\u00020\u0001*\u00020\b2\u0006\u0010\u0016\u001a\u00020\u0001H\u0000\u0002\u0007\n\u0005\b20\u0001¨\u0006("}, d2 = {"binarySearch", "", "", "value", "fromIndex", "toIndex", "commonCopyInto", "", "Lokio/SegmentedByteString;", "offset", "target", "", "targetOffset", "byteCount", "commonEquals", "", "other", "", "commonGetSize", "commonHashCode", "commonInternalGet", "", "pos", "commonRangeEquals", "otherOffset", "Lokio/ByteString;", "commonSubstring", "beginIndex", "endIndex", "commonToByteArray", "commonWrite", "buffer", "Lokio/Buffer;", "forEachSegment", "action", "Lkotlin/Function3;", "Lkotlin/ParameterName;", "name", "data", "segment", "okio"}, k = 2, mv = {1, 5, 1}, xi = 48)
/* compiled from: -SegmentedByteString.kt */
public final class _SegmentedByteStringKt {
    public static final int binarySearch(int[] $this$binarySearch, int value, int fromIndex, int toIndex) {
        Intrinsics.checkNotNullParameter($this$binarySearch, "<this>");
        int left = fromIndex;
        int right = toIndex - 1;
        while (left <= right) {
            int mid = (left + right) >>> 1;
            int midVal = $this$binarySearch[mid];
            if (midVal < value) {
                left = mid + 1;
            } else if (midVal <= value) {
                return mid;
            } else {
                right = mid - 1;
            }
        }
        return (-left) - 1;
    }

    public static final int segment(SegmentedByteString $this$segment, int pos) {
        Intrinsics.checkNotNullParameter($this$segment, "<this>");
        int i = binarySearch($this$segment.getDirectory$okio(), pos + 1, 0, ((Object[]) $this$segment.getSegments$okio()).length);
        return i >= 0 ? i : ~i;
    }

    public static final void forEachSegment(SegmentedByteString $this$forEachSegment, Function3<? super byte[], ? super Integer, ? super Integer, Unit> action) {
        Intrinsics.checkNotNullParameter($this$forEachSegment, "<this>");
        Intrinsics.checkNotNullParameter(action, "action");
        int segmentCount = ((Object[]) $this$forEachSegment.getSegments$okio()).length;
        int pos = 0;
        for (int s = 0; s < segmentCount; s++) {
            int segmentPos = $this$forEachSegment.getDirectory$okio()[segmentCount + s];
            int nextSegmentOffset = $this$forEachSegment.getDirectory$okio()[s];
            action.invoke($this$forEachSegment.getSegments$okio()[s], Integer.valueOf(segmentPos), Integer.valueOf(nextSegmentOffset - pos));
            pos = nextSegmentOffset;
        }
    }

    private static final void forEachSegment(SegmentedByteString $this$forEachSegment, int beginIndex, int endIndex, Function3<? super byte[], ? super Integer, ? super Integer, Unit> action) {
        int s = segment($this$forEachSegment, beginIndex);
        int pos = beginIndex;
        while (pos < endIndex) {
            int segmentOffset = s == 0 ? 0 : $this$forEachSegment.getDirectory$okio()[s - 1];
            int segmentPos = $this$forEachSegment.getDirectory$okio()[((Object[]) $this$forEachSegment.getSegments$okio()).length + s];
            int byteCount = Math.min(endIndex, segmentOffset + ($this$forEachSegment.getDirectory$okio()[s] - segmentOffset)) - pos;
            action.invoke($this$forEachSegment.getSegments$okio()[s], Integer.valueOf((pos - segmentOffset) + segmentPos), Integer.valueOf(byteCount));
            pos += byteCount;
            s++;
        }
    }

    public static final ByteString commonSubstring(SegmentedByteString $this$commonSubstring, int beginIndex, int endIndex) {
        SegmentedByteString segmentedByteString = $this$commonSubstring;
        int i = beginIndex;
        Intrinsics.checkNotNullParameter(segmentedByteString, "<this>");
        int endIndex2 = _UtilKt.resolveDefaultParameter(segmentedByteString, endIndex);
        int i2 = 1;
        if (i >= 0) {
            if (endIndex2 <= $this$commonSubstring.size()) {
                int subLen = endIndex2 - i;
                if (!(subLen >= 0)) {
                    throw new IllegalArgumentException(("endIndex=" + endIndex2 + " < beginIndex=" + i).toString());
                } else if (i == 0 && endIndex2 == $this$commonSubstring.size()) {
                    return segmentedByteString;
                } else {
                    if (i == endIndex2) {
                        return ByteString.EMPTY;
                    }
                    int beginSegment = segment($this$commonSubstring, beginIndex);
                    int endSegment = segment(segmentedByteString, endIndex2 - 1);
                    byte[][] newSegments = (byte[][]) ArraysKt.copyOfRange((T[]) (Object[]) $this$commonSubstring.getSegments$okio(), beginSegment, endSegment + 1);
                    int[] newDirectory = new int[(((Object[]) newSegments).length * 2)];
                    int index = 0;
                    if (beginSegment <= endSegment) {
                        int i3 = beginSegment;
                        while (true) {
                            int s = i3;
                            i3 += i2;
                            newDirectory[index] = Math.min($this$commonSubstring.getDirectory$okio()[s] - i, subLen);
                            newDirectory[index + ((Object[]) newSegments).length] = $this$commonSubstring.getDirectory$okio()[((Object[]) $this$commonSubstring.getSegments$okio()).length + s];
                            index++;
                            if (s == endSegment) {
                                break;
                            }
                            i2 = 1;
                        }
                    }
                    int segmentOffset = beginSegment == 0 ? 0 : $this$commonSubstring.getDirectory$okio()[beginSegment - 1];
                    int length = ((Object[]) newSegments).length;
                    newDirectory[length] = newDirectory[length] + (i - segmentOffset);
                    return new SegmentedByteString(newSegments, newDirectory);
                }
            } else {
                throw new IllegalArgumentException(("endIndex=" + endIndex2 + " > length(" + $this$commonSubstring.size() + ')').toString());
            }
        } else {
            throw new IllegalArgumentException(("beginIndex=" + i + " < 0").toString());
        }
    }

    public static final byte commonInternalGet(SegmentedByteString $this$commonInternalGet, int pos) {
        Intrinsics.checkNotNullParameter($this$commonInternalGet, "<this>");
        _UtilKt.checkOffsetAndCount((long) $this$commonInternalGet.getDirectory$okio()[((Object[]) $this$commonInternalGet.getSegments$okio()).length - 1], (long) pos, 1);
        int segment = segment($this$commonInternalGet, pos);
        return $this$commonInternalGet.getSegments$okio()[segment][(pos - (segment == 0 ? 0 : $this$commonInternalGet.getDirectory$okio()[segment - 1])) + $this$commonInternalGet.getDirectory$okio()[((Object[]) $this$commonInternalGet.getSegments$okio()).length + segment]];
    }

    public static final int commonGetSize(SegmentedByteString $this$commonGetSize) {
        Intrinsics.checkNotNullParameter($this$commonGetSize, "<this>");
        return $this$commonGetSize.getDirectory$okio()[((Object[]) $this$commonGetSize.getSegments$okio()).length - 1];
    }

    public static final byte[] commonToByteArray(SegmentedByteString $this$commonToByteArray) {
        Intrinsics.checkNotNullParameter($this$commonToByteArray, "<this>");
        byte[] result = new byte[$this$commonToByteArray.size()];
        int resultPos = 0;
        SegmentedByteString $this$forEachSegment$iv = $this$commonToByteArray;
        int segmentCount$iv = ((Object[]) $this$forEachSegment$iv.getSegments$okio()).length;
        int pos$iv = 0;
        for (int s$iv = 0; s$iv < segmentCount$iv; s$iv++) {
            int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[segmentCount$iv + s$iv];
            int nextSegmentOffset$iv = $this$forEachSegment$iv.getDirectory$okio()[s$iv];
            int byteCount = nextSegmentOffset$iv - pos$iv;
            int offset = segmentPos$iv;
            ArraysKt.copyInto($this$forEachSegment$iv.getSegments$okio()[s$iv], result, resultPos, offset, offset + byteCount);
            resultPos += byteCount;
            pos$iv = nextSegmentOffset$iv;
        }
        return result;
    }

    public static final void commonWrite(SegmentedByteString $this$commonWrite, Buffer buffer, int offset, int byteCount) {
        int $i$f$commonWrite;
        Buffer buffer2 = buffer;
        int i = offset;
        int i2 = byteCount;
        Intrinsics.checkNotNullParameter($this$commonWrite, "<this>");
        Intrinsics.checkNotNullParameter(buffer2, "buffer");
        int $i$f$commonWrite2 = 0;
        int endIndex$iv = i + i2;
        SegmentedByteString $this$forEachSegment$iv = $this$commonWrite;
        int s$iv = segment($this$forEachSegment$iv, i);
        int pos$iv = offset;
        while (pos$iv < endIndex$iv) {
            int segmentOffset$iv = s$iv == 0 ? 0 : $this$forEachSegment$iv.getDirectory$okio()[s$iv - 1];
            int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[((Object[]) $this$forEachSegment$iv.getSegments$okio()).length + s$iv];
            int byteCount$iv = Math.min(endIndex$iv, segmentOffset$iv + ($this$forEachSegment$iv.getDirectory$okio()[s$iv] - segmentOffset$iv)) - pos$iv;
            int offset2 = (pos$iv - segmentOffset$iv) + segmentPos$iv;
            Segment segment = new Segment($this$forEachSegment$iv.getSegments$okio()[s$iv], offset2, offset2 + byteCount$iv, true, false);
            if (buffer2.head == null) {
                Segment segment2 = segment;
                segment2.prev = segment2;
                $i$f$commonWrite = $i$f$commonWrite2;
                segment2.next = segment2.prev;
                buffer2.head = segment2.next;
            } else {
                Segment segment3 = segment;
                $i$f$commonWrite = $i$f$commonWrite2;
                Segment segment4 = buffer2.head;
                Intrinsics.checkNotNull(segment4);
                Segment segment5 = segment4.prev;
                Intrinsics.checkNotNull(segment5);
                segment5.push(segment3);
            }
            pos$iv += byteCount$iv;
            s$iv++;
            int i3 = offset;
            $i$f$commonWrite2 = $i$f$commonWrite;
        }
        buffer2.setSize$okio(buffer.size() + ((long) i2));
    }

    public static final boolean commonRangeEquals(SegmentedByteString $this$commonRangeEquals, int offset, ByteString other, int otherOffset, int byteCount) {
        int i = offset;
        ByteString byteString = other;
        Intrinsics.checkNotNullParameter($this$commonRangeEquals, "<this>");
        Intrinsics.checkNotNullParameter(byteString, "other");
        int $i$f$commonRangeEquals = 0;
        if (i < 0) {
            return false;
        }
        if (i > $this$commonRangeEquals.size() - byteCount) {
            return false;
        }
        int endIndex$iv = i + byteCount;
        SegmentedByteString $this$forEachSegment$iv = $this$commonRangeEquals;
        int s$iv = segment($this$forEachSegment$iv, i);
        int pos$iv = offset;
        int otherOffset2 = otherOffset;
        while (pos$iv < endIndex$iv) {
            int segmentOffset$iv = s$iv == 0 ? 0 : $this$forEachSegment$iv.getDirectory$okio()[s$iv - 1];
            int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[((Object[]) $this$forEachSegment$iv.getSegments$okio()).length + s$iv];
            int byteCount$iv = Math.min(endIndex$iv, segmentOffset$iv + ($this$forEachSegment$iv.getDirectory$okio()[s$iv] - segmentOffset$iv)) - pos$iv;
            int byteCount2 = byteCount$iv;
            int offset2 = $i$f$commonRangeEquals;
            if (byteString.rangeEquals(otherOffset2, $this$forEachSegment$iv.getSegments$okio()[s$iv], (pos$iv - segmentOffset$iv) + segmentPos$iv, byteCount2) == 0) {
                return false;
            }
            otherOffset2 += byteCount2;
            pos$iv += byteCount$iv;
            s$iv++;
            int i2 = offset;
            $i$f$commonRangeEquals = offset2;
        }
        return true;
    }

    public static final boolean commonRangeEquals(SegmentedByteString $this$commonRangeEquals, int offset, byte[] other, int otherOffset, int byteCount) {
        int i = offset;
        byte[] bArr = other;
        int i2 = otherOffset;
        Intrinsics.checkNotNullParameter($this$commonRangeEquals, "<this>");
        Intrinsics.checkNotNullParameter(bArr, "other");
        if (i < 0 || i > $this$commonRangeEquals.size() - byteCount || i2 < 0 || i2 > bArr.length - byteCount) {
            return false;
        }
        int endIndex$iv = i + byteCount;
        SegmentedByteString $this$forEachSegment$iv = $this$commonRangeEquals;
        int s$iv = segment($this$forEachSegment$iv, i);
        int pos$iv = offset;
        int otherOffset2 = i2;
        while (pos$iv < endIndex$iv) {
            int segmentOffset$iv = s$iv == 0 ? 0 : $this$forEachSegment$iv.getDirectory$okio()[s$iv - 1];
            int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[((Object[]) $this$forEachSegment$iv.getSegments$okio()).length + s$iv];
            int byteCount$iv = Math.min(endIndex$iv, segmentOffset$iv + ($this$forEachSegment$iv.getDirectory$okio()[s$iv] - segmentOffset$iv)) - pos$iv;
            int byteCount2 = byteCount$iv;
            if (_UtilKt.arrayRangeEquals($this$forEachSegment$iv.getSegments$okio()[s$iv], segmentPos$iv + (pos$iv - segmentOffset$iv), bArr, otherOffset2, byteCount2) == 0) {
                return false;
            }
            otherOffset2 += byteCount2;
            pos$iv += byteCount$iv;
            s$iv++;
            int i3 = offset;
            int i4 = otherOffset;
        }
        return true;
    }

    public static final void commonCopyInto(SegmentedByteString $this$commonCopyInto, int offset, byte[] target, int targetOffset, int byteCount) {
        int i = offset;
        byte[] bArr = target;
        int i2 = byteCount;
        Intrinsics.checkNotNullParameter($this$commonCopyInto, "<this>");
        Intrinsics.checkNotNullParameter(bArr, TypedValues.AttributesType.S_TARGET);
        int $i$f$commonCopyInto = 0;
        _UtilKt.checkOffsetAndCount((long) $this$commonCopyInto.size(), (long) i, (long) i2);
        int i3 = targetOffset;
        _UtilKt.checkOffsetAndCount((long) bArr.length, (long) i3, (long) i2);
        int endIndex$iv = i + i2;
        SegmentedByteString $this$forEachSegment$iv = $this$commonCopyInto;
        int s$iv = segment($this$forEachSegment$iv, i);
        int pos$iv = offset;
        int targetOffset2 = i3;
        while (pos$iv < endIndex$iv) {
            int segmentOffset$iv = s$iv == 0 ? 0 : $this$forEachSegment$iv.getDirectory$okio()[s$iv - 1];
            int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[((Object[]) $this$forEachSegment$iv.getSegments$okio()).length + s$iv];
            int byteCount$iv = Math.min(endIndex$iv, segmentOffset$iv + ($this$forEachSegment$iv.getDirectory$okio()[s$iv] - segmentOffset$iv)) - pos$iv;
            int byteCount2 = byteCount$iv;
            int offset2 = segmentPos$iv + (pos$iv - segmentOffset$iv);
            ArraysKt.copyInto($this$forEachSegment$iv.getSegments$okio()[s$iv], bArr, targetOffset2, offset2, offset2 + byteCount2);
            targetOffset2 += byteCount2;
            pos$iv += byteCount$iv;
            s$iv++;
            int i4 = offset;
            int i5 = byteCount;
            $i$f$commonCopyInto = $i$f$commonCopyInto;
        }
    }

    public static final boolean commonEquals(SegmentedByteString $this$commonEquals, Object other) {
        Intrinsics.checkNotNullParameter($this$commonEquals, "<this>");
        if (other == $this$commonEquals) {
            return true;
        }
        if (!(other instanceof ByteString)) {
            return false;
        }
        if (((ByteString) other).size() != $this$commonEquals.size() || !$this$commonEquals.rangeEquals(0, (ByteString) other, 0, $this$commonEquals.size())) {
            return false;
        }
        return true;
    }

    public static final int commonHashCode(SegmentedByteString $this$commonHashCode) {
        SegmentedByteString segmentedByteString = $this$commonHashCode;
        Intrinsics.checkNotNullParameter(segmentedByteString, "<this>");
        int result = $this$commonHashCode.getHashCode$okio();
        if (result != 0) {
            return result;
        }
        int result2 = 1;
        SegmentedByteString $this$forEachSegment$iv = $this$commonHashCode;
        int segmentCount$iv = ((Object[]) $this$forEachSegment$iv.getSegments$okio()).length;
        int pos$iv = 0;
        for (int s$iv = 0; s$iv < segmentCount$iv; s$iv++) {
            int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[segmentCount$iv + s$iv];
            int nextSegmentOffset$iv = $this$forEachSegment$iv.getDirectory$okio()[s$iv];
            byte[] data = $this$forEachSegment$iv.getSegments$okio()[s$iv];
            int offset = segmentPos$iv;
            int limit = offset + (nextSegmentOffset$iv - pos$iv);
            for (int i = offset; i < limit; i++) {
                result2 = (result2 * 31) + data[i];
            }
            pos$iv = nextSegmentOffset$iv;
        }
        segmentedByteString.setHashCode$okio(result2);
        return result2;
    }
}

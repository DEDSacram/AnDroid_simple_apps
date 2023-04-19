package okio.internal;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import java.util.Arrays;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okio.Buffer;
import okio.ByteString;
import okio._Base64Kt;
import okio._JvmPlatformKt;
import okio._UtilKt;

@Metadata(d1 = {"\u0000R\n\u0000\n\u0002\u0010\u0019\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\f\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0005\n\u0002\b\u0017\n\u0002\u0018\u0002\n\u0000\u001a\u0018\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0007H\u0002\u001a\u0011\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\tH\b\u001a\u0010\u0010\u000e\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u0010H\u0002\u001a\r\u0010\u0011\u001a\u00020\u0012*\u00020\fH\b\u001a\r\u0010\u0013\u001a\u00020\u0012*\u00020\fH\b\u001a\u0015\u0010\u0014\u001a\u00020\u0007*\u00020\f2\u0006\u0010\u0015\u001a\u00020\fH\b\u001a-\u0010\u0016\u001a\u00020\u0017*\u00020\f2\u0006\u0010\u0018\u001a\u00020\u00072\u0006\u0010\u0019\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u0007H\b\u001a\u000f\u0010\u001c\u001a\u0004\u0018\u00010\f*\u00020\u0012H\b\u001a\r\u0010\u001d\u001a\u00020\f*\u00020\u0012H\b\u001a\r\u0010\u001e\u001a\u00020\f*\u00020\u0012H\b\u001a\u0015\u0010\u001f\u001a\u00020 *\u00020\f2\u0006\u0010!\u001a\u00020\tH\b\u001a\u0015\u0010\u001f\u001a\u00020 *\u00020\f2\u0006\u0010!\u001a\u00020\fH\b\u001a\u0017\u0010\"\u001a\u00020 *\u00020\f2\b\u0010\u0015\u001a\u0004\u0018\u00010#H\b\u001a\u0015\u0010$\u001a\u00020%*\u00020\f2\u0006\u0010&\u001a\u00020\u0007H\b\u001a\r\u0010'\u001a\u00020\u0007*\u00020\fH\b\u001a\r\u0010(\u001a\u00020\u0007*\u00020\fH\b\u001a\r\u0010)\u001a\u00020\u0012*\u00020\fH\b\u001a\u001d\u0010*\u001a\u00020\u0007*\u00020\f2\u0006\u0010\u0015\u001a\u00020\t2\u0006\u0010+\u001a\u00020\u0007H\b\u001a\r\u0010,\u001a\u00020\t*\u00020\fH\b\u001a\u001d\u0010-\u001a\u00020\u0007*\u00020\f2\u0006\u0010\u0015\u001a\u00020\t2\u0006\u0010+\u001a\u00020\u0007H\b\u001a\u001d\u0010-\u001a\u00020\u0007*\u00020\f2\u0006\u0010\u0015\u001a\u00020\f2\u0006\u0010+\u001a\u00020\u0007H\b\u001a-\u0010.\u001a\u00020 *\u00020\f2\u0006\u0010\u0018\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\t2\u0006\u0010/\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u0007H\b\u001a-\u0010.\u001a\u00020 *\u00020\f2\u0006\u0010\u0018\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\f2\u0006\u0010/\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u0007H\b\u001a\u0015\u00100\u001a\u00020 *\u00020\f2\u0006\u00101\u001a\u00020\tH\b\u001a\u0015\u00100\u001a\u00020 *\u00020\f2\u0006\u00101\u001a\u00020\fH\b\u001a\u001d\u00102\u001a\u00020\f*\u00020\f2\u0006\u00103\u001a\u00020\u00072\u0006\u00104\u001a\u00020\u0007H\b\u001a\r\u00105\u001a\u00020\f*\u00020\fH\b\u001a\r\u00106\u001a\u00020\f*\u00020\fH\b\u001a\r\u00107\u001a\u00020\t*\u00020\fH\b\u001a\u001d\u00108\u001a\u00020\f*\u00020\t2\u0006\u0010\u0018\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u0007H\b\u001a\r\u00109\u001a\u00020\u0012*\u00020\fH\b\u001a\r\u0010:\u001a\u00020\u0012*\u00020\fH\b\u001a$\u0010;\u001a\u00020\u0017*\u00020\f2\u0006\u0010<\u001a\u00020=2\u0006\u0010\u0018\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u0007H\u0000\"\u001c\u0010\u0000\u001a\u00020\u00018\u0000X\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0002\u0010\u0003\u001a\u0004\b\u0004\u0010\u0005¨\u0006>"}, d2 = {"HEX_DIGIT_CHARS", "", "getHEX_DIGIT_CHARS$annotations", "()V", "getHEX_DIGIT_CHARS", "()[C", "codePointIndexToCharIndex", "", "s", "", "codePointCount", "commonOf", "Lokio/ByteString;", "data", "decodeHexDigit", "c", "", "commonBase64", "", "commonBase64Url", "commonCompareTo", "other", "commonCopyInto", "", "offset", "target", "targetOffset", "byteCount", "commonDecodeBase64", "commonDecodeHex", "commonEncodeUtf8", "commonEndsWith", "", "suffix", "commonEquals", "", "commonGetByte", "", "pos", "commonGetSize", "commonHashCode", "commonHex", "commonIndexOf", "fromIndex", "commonInternalArray", "commonLastIndexOf", "commonRangeEquals", "otherOffset", "commonStartsWith", "prefix", "commonSubstring", "beginIndex", "endIndex", "commonToAsciiLowercase", "commonToAsciiUppercase", "commonToByteArray", "commonToByteString", "commonToString", "commonUtf8", "commonWrite", "buffer", "Lokio/Buffer;", "okio"}, k = 2, mv = {1, 5, 1}, xi = 48)
/* compiled from: -ByteString.kt */
public final class _ByteStringKt {
    private static final char[] HEX_DIGIT_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static /* synthetic */ void getHEX_DIGIT_CHARS$annotations() {
    }

    public static final String commonUtf8(ByteString $this$commonUtf8) {
        Intrinsics.checkNotNullParameter($this$commonUtf8, "<this>");
        String result = $this$commonUtf8.getUtf8$okio();
        if (result != null) {
            return result;
        }
        String result2 = _JvmPlatformKt.toUtf8String($this$commonUtf8.internalArray$okio());
        $this$commonUtf8.setUtf8$okio(result2);
        return result2;
    }

    public static final String commonBase64(ByteString $this$commonBase64) {
        Intrinsics.checkNotNullParameter($this$commonBase64, "<this>");
        return _Base64Kt.encodeBase64$default($this$commonBase64.getData$okio(), (byte[]) null, 1, (Object) null);
    }

    public static final String commonBase64Url(ByteString $this$commonBase64Url) {
        Intrinsics.checkNotNullParameter($this$commonBase64Url, "<this>");
        return _Base64Kt.encodeBase64($this$commonBase64Url.getData$okio(), _Base64Kt.getBASE64_URL_SAFE());
    }

    public static final char[] getHEX_DIGIT_CHARS() {
        return HEX_DIGIT_CHARS;
    }

    public static final String commonHex(ByteString $this$commonHex) {
        Intrinsics.checkNotNullParameter($this$commonHex, "<this>");
        char[] result = new char[($this$commonHex.getData$okio().length * 2)];
        int c = 0;
        byte[] data$okio = $this$commonHex.getData$okio();
        int length = data$okio.length;
        int i = 0;
        while (i < length) {
            byte b = data$okio[i];
            i++;
            int c2 = c + 1;
            int $this$and$iv = b;
            result[c] = getHEX_DIGIT_CHARS()[($this$and$iv >> 4) & 15];
            c = c2 + 1;
            result[c2] = getHEX_DIGIT_CHARS()[15 & $this$and$iv];
        }
        return StringsKt.concatToString(result);
    }

    public static final ByteString commonToAsciiLowercase(ByteString $this$commonToAsciiLowercase) {
        byte b;
        Intrinsics.checkNotNullParameter($this$commonToAsciiLowercase, "<this>");
        int i = 0;
        while (i < $this$commonToAsciiLowercase.getData$okio().length) {
            byte c = $this$commonToAsciiLowercase.getData$okio()[i];
            byte b2 = (byte) 65;
            if (c < b2 || c > (b = (byte) 90)) {
                i++;
            } else {
                byte[] data$okio = $this$commonToAsciiLowercase.getData$okio();
                byte[] lowercase = Arrays.copyOf(data$okio, data$okio.length);
                Intrinsics.checkNotNullExpressionValue(lowercase, "java.util.Arrays.copyOf(this, size)");
                int i2 = i + 1;
                lowercase[i] = (byte) (c + 32);
                while (i2 < lowercase.length) {
                    byte c2 = lowercase[i2];
                    if (c2 < b2 || c2 > b) {
                        i2++;
                    } else {
                        lowercase[i2] = (byte) (c2 + 32);
                        i2++;
                    }
                }
                return new ByteString(lowercase);
            }
        }
        return $this$commonToAsciiLowercase;
    }

    public static final ByteString commonToAsciiUppercase(ByteString $this$commonToAsciiUppercase) {
        byte b;
        Intrinsics.checkNotNullParameter($this$commonToAsciiUppercase, "<this>");
        int i = 0;
        while (i < $this$commonToAsciiUppercase.getData$okio().length) {
            byte c = $this$commonToAsciiUppercase.getData$okio()[i];
            byte b2 = (byte) 97;
            if (c < b2 || c > (b = (byte) 122)) {
                i++;
            } else {
                byte[] data$okio = $this$commonToAsciiUppercase.getData$okio();
                byte[] lowercase = Arrays.copyOf(data$okio, data$okio.length);
                Intrinsics.checkNotNullExpressionValue(lowercase, "java.util.Arrays.copyOf(this, size)");
                int i2 = i + 1;
                lowercase[i] = (byte) (c - 32);
                while (i2 < lowercase.length) {
                    byte c2 = lowercase[i2];
                    if (c2 < b2 || c2 > b) {
                        i2++;
                    } else {
                        lowercase[i2] = (byte) (c2 - 32);
                        i2++;
                    }
                }
                return new ByteString(lowercase);
            }
        }
        return $this$commonToAsciiUppercase;
    }

    public static final ByteString commonSubstring(ByteString $this$commonSubstring, int beginIndex, int endIndex) {
        Intrinsics.checkNotNullParameter($this$commonSubstring, "<this>");
        int endIndex2 = _UtilKt.resolveDefaultParameter($this$commonSubstring, endIndex);
        boolean z = true;
        if (beginIndex >= 0) {
            if (endIndex2 <= $this$commonSubstring.getData$okio().length) {
                if (endIndex2 - beginIndex < 0) {
                    z = false;
                }
                if (!z) {
                    throw new IllegalArgumentException("endIndex < beginIndex".toString());
                } else if (beginIndex == 0 && endIndex2 == $this$commonSubstring.getData$okio().length) {
                    return $this$commonSubstring;
                } else {
                    return new ByteString(ArraysKt.copyOfRange($this$commonSubstring.getData$okio(), beginIndex, endIndex2));
                }
            } else {
                throw new IllegalArgumentException(("endIndex > length(" + $this$commonSubstring.getData$okio().length + ')').toString());
            }
        } else {
            throw new IllegalArgumentException("beginIndex < 0".toString());
        }
    }

    public static final byte commonGetByte(ByteString $this$commonGetByte, int pos) {
        Intrinsics.checkNotNullParameter($this$commonGetByte, "<this>");
        return $this$commonGetByte.getData$okio()[pos];
    }

    public static final int commonGetSize(ByteString $this$commonGetSize) {
        Intrinsics.checkNotNullParameter($this$commonGetSize, "<this>");
        return $this$commonGetSize.getData$okio().length;
    }

    public static final byte[] commonToByteArray(ByteString $this$commonToByteArray) {
        Intrinsics.checkNotNullParameter($this$commonToByteArray, "<this>");
        byte[] data$okio = $this$commonToByteArray.getData$okio();
        byte[] copyOf = Arrays.copyOf(data$okio, data$okio.length);
        Intrinsics.checkNotNullExpressionValue(copyOf, "java.util.Arrays.copyOf(this, size)");
        return copyOf;
    }

    public static final byte[] commonInternalArray(ByteString $this$commonInternalArray) {
        Intrinsics.checkNotNullParameter($this$commonInternalArray, "<this>");
        return $this$commonInternalArray.getData$okio();
    }

    public static final boolean commonRangeEquals(ByteString $this$commonRangeEquals, int offset, ByteString other, int otherOffset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonRangeEquals, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        return other.rangeEquals(otherOffset, $this$commonRangeEquals.getData$okio(), offset, byteCount);
    }

    public static final boolean commonRangeEquals(ByteString $this$commonRangeEquals, int offset, byte[] other, int otherOffset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonRangeEquals, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        return offset >= 0 && offset <= $this$commonRangeEquals.getData$okio().length - byteCount && otherOffset >= 0 && otherOffset <= other.length - byteCount && _UtilKt.arrayRangeEquals($this$commonRangeEquals.getData$okio(), offset, other, otherOffset, byteCount);
    }

    public static final void commonCopyInto(ByteString $this$commonCopyInto, int offset, byte[] target, int targetOffset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonCopyInto, "<this>");
        Intrinsics.checkNotNullParameter(target, TypedValues.AttributesType.S_TARGET);
        ArraysKt.copyInto($this$commonCopyInto.getData$okio(), target, targetOffset, offset, offset + byteCount);
    }

    public static final boolean commonStartsWith(ByteString $this$commonStartsWith, ByteString prefix) {
        Intrinsics.checkNotNullParameter($this$commonStartsWith, "<this>");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        return $this$commonStartsWith.rangeEquals(0, prefix, 0, prefix.size());
    }

    public static final boolean commonStartsWith(ByteString $this$commonStartsWith, byte[] prefix) {
        Intrinsics.checkNotNullParameter($this$commonStartsWith, "<this>");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        return $this$commonStartsWith.rangeEquals(0, prefix, 0, prefix.length);
    }

    public static final boolean commonEndsWith(ByteString $this$commonEndsWith, ByteString suffix) {
        Intrinsics.checkNotNullParameter($this$commonEndsWith, "<this>");
        Intrinsics.checkNotNullParameter(suffix, "suffix");
        return $this$commonEndsWith.rangeEquals($this$commonEndsWith.size() - suffix.size(), suffix, 0, suffix.size());
    }

    public static final boolean commonEndsWith(ByteString $this$commonEndsWith, byte[] suffix) {
        Intrinsics.checkNotNullParameter($this$commonEndsWith, "<this>");
        Intrinsics.checkNotNullParameter(suffix, "suffix");
        return $this$commonEndsWith.rangeEquals($this$commonEndsWith.size() - suffix.length, suffix, 0, suffix.length);
    }

    public static final int commonIndexOf(ByteString $this$commonIndexOf, byte[] other, int fromIndex) {
        int i;
        Intrinsics.checkNotNullParameter($this$commonIndexOf, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        int limit = $this$commonIndexOf.getData$okio().length - other.length;
        int max = Math.max(fromIndex, 0);
        if (max > limit) {
            return -1;
        }
        do {
            i = max;
            max++;
            if (_UtilKt.arrayRangeEquals($this$commonIndexOf.getData$okio(), i, other, 0, other.length)) {
                return i;
            }
        } while (i != limit);
        return -1;
    }

    public static final int commonLastIndexOf(ByteString $this$commonLastIndexOf, ByteString other, int fromIndex) {
        Intrinsics.checkNotNullParameter($this$commonLastIndexOf, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        return $this$commonLastIndexOf.lastIndexOf(other.internalArray$okio(), fromIndex);
    }

    public static final int commonLastIndexOf(ByteString $this$commonLastIndexOf, byte[] other, int fromIndex) {
        Intrinsics.checkNotNullParameter($this$commonLastIndexOf, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        int min = Math.min(_UtilKt.resolveDefaultParameter($this$commonLastIndexOf, fromIndex), $this$commonLastIndexOf.getData$okio().length - other.length);
        if (min >= 0) {
            do {
                int i = min;
                min--;
                if (_UtilKt.arrayRangeEquals($this$commonLastIndexOf.getData$okio(), i, other, 0, other.length)) {
                    return i;
                }
            } while (min >= 0);
        }
        return -1;
    }

    public static final boolean commonEquals(ByteString $this$commonEquals, Object other) {
        Intrinsics.checkNotNullParameter($this$commonEquals, "<this>");
        if (other == $this$commonEquals) {
            return true;
        }
        if (!(other instanceof ByteString)) {
            return false;
        }
        if (((ByteString) other).size() != $this$commonEquals.getData$okio().length || !((ByteString) other).rangeEquals(0, $this$commonEquals.getData$okio(), 0, $this$commonEquals.getData$okio().length)) {
            return false;
        }
        return true;
    }

    public static final int commonHashCode(ByteString $this$commonHashCode) {
        Intrinsics.checkNotNullParameter($this$commonHashCode, "<this>");
        int result = $this$commonHashCode.getHashCode$okio();
        if (result != 0) {
            return result;
        }
        int it = Arrays.hashCode($this$commonHashCode.getData$okio());
        $this$commonHashCode.setHashCode$okio(it);
        return it;
    }

    public static final int commonCompareTo(ByteString $this$commonCompareTo, ByteString other) {
        Intrinsics.checkNotNullParameter($this$commonCompareTo, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        int sizeA = $this$commonCompareTo.size();
        int sizeB = other.size();
        int i = 0;
        int size = Math.min(sizeA, sizeB);
        while (i < size) {
            int byteA = $this$commonCompareTo.getByte(i) & 255;
            int byteB = other.getByte(i) & 255;
            if (byteA == byteB) {
                i++;
            } else if (byteA < byteB) {
                return -1;
            } else {
                return 1;
            }
        }
        if (sizeA == sizeB) {
            return 0;
        }
        if (sizeA < sizeB) {
            return -1;
        }
        return 1;
    }

    public static final ByteString commonOf(byte[] data) {
        Intrinsics.checkNotNullParameter(data, "data");
        byte[] copyOf = Arrays.copyOf(data, data.length);
        Intrinsics.checkNotNullExpressionValue(copyOf, "java.util.Arrays.copyOf(this, size)");
        return new ByteString(copyOf);
    }

    public static final ByteString commonToByteString(byte[] $this$commonToByteString, int offset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonToByteString, "<this>");
        _UtilKt.checkOffsetAndCount((long) $this$commonToByteString.length, (long) offset, (long) byteCount);
        return new ByteString(ArraysKt.copyOfRange($this$commonToByteString, offset, offset + byteCount));
    }

    public static final ByteString commonEncodeUtf8(String $this$commonEncodeUtf8) {
        Intrinsics.checkNotNullParameter($this$commonEncodeUtf8, "<this>");
        ByteString byteString = new ByteString(_JvmPlatformKt.asUtf8ToByteArray($this$commonEncodeUtf8));
        byteString.setUtf8$okio($this$commonEncodeUtf8);
        return byteString;
    }

    public static final ByteString commonDecodeBase64(String $this$commonDecodeBase64) {
        Intrinsics.checkNotNullParameter($this$commonDecodeBase64, "<this>");
        byte[] decoded = _Base64Kt.decodeBase64ToArray($this$commonDecodeBase64);
        if (decoded != null) {
            return new ByteString(decoded);
        }
        return null;
    }

    public static final ByteString commonDecodeHex(String $this$commonDecodeHex) {
        Intrinsics.checkNotNullParameter($this$commonDecodeHex, "<this>");
        int i = 0;
        if ($this$commonDecodeHex.length() % 2 == 0) {
            byte[] result = new byte[($this$commonDecodeHex.length() / 2)];
            int length = result.length - 1;
            if (length >= 0) {
                do {
                    int i2 = i;
                    i++;
                    result[i2] = (byte) ((decodeHexDigit($this$commonDecodeHex.charAt(i2 * 2)) << 4) + decodeHexDigit($this$commonDecodeHex.charAt((i2 * 2) + 1)));
                } while (i <= length);
            }
            return new ByteString(result);
        }
        throw new IllegalArgumentException(Intrinsics.stringPlus("Unexpected hex string: ", $this$commonDecodeHex).toString());
    }

    public static final void commonWrite(ByteString $this$commonWrite, Buffer buffer, int offset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonWrite, "<this>");
        Intrinsics.checkNotNullParameter(buffer, "buffer");
        buffer.write($this$commonWrite.getData$okio(), offset, byteCount);
    }

    /* access modifiers changed from: private */
    public static final int decodeHexDigit(char c) {
        boolean z = true;
        if ('0' <= c && c <= '9') {
            return c - '0';
        }
        if ('a' <= c && c <= 'f') {
            return (c - 'a') + 10;
        }
        if ('A' > c || c > 'F') {
            z = false;
        }
        if (z) {
            return (c - 'A') + 10;
        }
        throw new IllegalArgumentException(Intrinsics.stringPlus("Unexpected hex digit: ", Character.valueOf(c)));
    }

    public static final String commonToString(ByteString $this$commonToString) {
        Intrinsics.checkNotNullParameter($this$commonToString, "<this>");
        boolean z = true;
        if ($this$commonToString.getData$okio().length == 0) {
            return "[size=0]";
        }
        int i = codePointIndexToCharIndex($this$commonToString.getData$okio(), 64);
        if (i != -1) {
            String text = $this$commonToString.utf8();
            if (text != null) {
                String substring = text.substring(0, i);
                Intrinsics.checkNotNullExpressionValue(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
                String safeText = StringsKt.replace$default(StringsKt.replace$default(StringsKt.replace$default(substring, "\\", "\\\\", false, 4, (Object) null), "\n", "\\n", false, 4, (Object) null), "\r", "\\r", false, 4, (Object) null);
                if (i < text.length()) {
                    return "[size=" + $this$commonToString.getData$okio().length + " text=" + safeText + "…]";
                }
                return "[text=" + safeText + ']';
            }
            throw new NullPointerException("null cannot be cast to non-null type java.lang.String");
        } else if ($this$commonToString.getData$okio().length <= 64) {
            return "[hex=" + $this$commonToString.hex() + ']';
        } else {
            StringBuilder append = new StringBuilder().append("[size=").append($this$commonToString.getData$okio().length).append(" hex=");
            ByteString $this$commonSubstring$iv = $this$commonToString;
            int endIndex$iv = _UtilKt.resolveDefaultParameter($this$commonSubstring$iv, 64);
            if (endIndex$iv <= $this$commonSubstring$iv.getData$okio().length) {
                if (endIndex$iv - 0 < 0) {
                    z = false;
                }
                if (z) {
                    if (endIndex$iv != $this$commonSubstring$iv.getData$okio().length) {
                        $this$commonSubstring$iv = new ByteString(ArraysKt.copyOfRange($this$commonSubstring$iv.getData$okio(), 0, endIndex$iv));
                    }
                    return append.append($this$commonSubstring$iv.hex()).append("…]").toString();
                }
                throw new IllegalArgumentException("endIndex < beginIndex".toString());
            }
            throw new IllegalArgumentException(("endIndex > length(" + $this$commonSubstring$iv.getData$okio().length + ')').toString());
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:116:0x0162, code lost:
        if ((127(0x7f, float:1.78E-43) <= 65533(0xfffd, float:9.1831E-41) && 65533(0xfffd, float:9.1831E-41) <= 159(0x9f, float:2.23E-43)) != false) goto L_0x0164;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:148:0x01c4, code lost:
        if ((127(0x7f, float:1.78E-43) <= 65533(0xfffd, float:9.1831E-41) && 65533(0xfffd, float:9.1831E-41) <= 159(0x9f, float:2.23E-43)) != false) goto L_0x01c6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:178:0x020d, code lost:
        if ((127 <= r14 && r14 <= 159) != false) goto L_0x020f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x02f6, code lost:
        if ((127(0x7f, float:1.78E-43) <= 65533(0xfffd, float:9.1831E-41) && 65533(0xfffd, float:9.1831E-41) <= 159(0x9f, float:2.23E-43)) != false) goto L_0x02f8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x035f, code lost:
        if ((127(0x7f, float:1.78E-43) <= 65533(0xfffd, float:9.1831E-41) && 65533(0xfffd, float:9.1831E-41) <= 159(0x9f, float:2.23E-43)) != false) goto L_0x0361;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x03c5, code lost:
        if ((127(0x7f, float:1.78E-43) <= 65533(0xfffd, float:9.1831E-41) && 65533(0xfffd, float:9.1831E-41) <= 159(0x9f, float:2.23E-43)) != false) goto L_0x03c7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x0425, code lost:
        if ((127(0x7f, float:1.78E-43) <= 65533(0xfffd, float:9.1831E-41) && 65533(0xfffd, float:9.1831E-41) <= 159(0x9f, float:2.23E-43)) != false) goto L_0x0427;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:368:0x0434, code lost:
        if (65533(0xfffd, float:9.1831E-41) < 65536(0x10000, float:9.18355E-41)) goto L_0x0479;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:390:0x0468, code lost:
        if ((127 <= r15 && r15 <= 159) != false) goto L_0x046a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:396:0x0477, code lost:
        if (r15 < 65536) goto L_0x0479;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x056c, code lost:
        if ((127(0x7f, float:1.78E-43) <= 65533(0xfffd, float:9.1831E-41) && 65533(0xfffd, float:9.1831E-41) <= 159(0x9f, float:2.23E-43)) != false) goto L_0x056e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:512:0x05d3, code lost:
        if ((127(0x7f, float:1.78E-43) <= 65533(0xfffd, float:9.1831E-41) && 65533(0xfffd, float:9.1831E-41) <= 159(0x9f, float:2.23E-43)) != false) goto L_0x05d5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x063d, code lost:
        if ((127(0x7f, float:1.78E-43) <= 65533(0xfffd, float:9.1831E-41) && 65533(0xfffd, float:9.1831E-41) <= 159(0x9f, float:2.23E-43)) != false) goto L_0x063f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:579:0x06a8, code lost:
        if ((127(0x7f, float:1.78E-43) <= 65533(0xfffd, float:9.1831E-41) && 65533(0xfffd, float:9.1831E-41) <= 159(0x9f, float:2.23E-43)) != false) goto L_0x06aa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:617:0x0708, code lost:
        if ((127(0x7f, float:1.78E-43) <= 65533(0xfffd, float:9.1831E-41) && 65533(0xfffd, float:9.1831E-41) <= 159(0x9f, float:2.23E-43)) != false) goto L_0x070a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:623:0x0717, code lost:
        if (65533(0xfffd, float:9.1831E-41) < 65536(0x10000, float:9.18355E-41)) goto L_0x0719;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:649:0x0756, code lost:
        if ((127(0x7f, float:1.78E-43) <= 65533(0xfffd, float:9.1831E-41) && 65533(0xfffd, float:9.1831E-41) <= 159(0x9f, float:2.23E-43)) != false) goto L_0x0758;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:655:0x0765, code lost:
        if (65533(0xfffd, float:9.1831E-41) < 65536(0x10000, float:9.18355E-41)) goto L_0x0719;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:677:0x0799, code lost:
        if ((127 <= r15 && r15 <= 159) != false) goto L_0x079b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:683:0x07a8, code lost:
        if (r15 < 65536) goto L_0x0719;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x07e4, code lost:
        if ((127(0x7f, float:1.78E-43) <= 65533(0xfffd, float:9.1831E-41) && 65533(0xfffd, float:9.1831E-41) <= 159(0x9f, float:2.23E-43)) != false) goto L_0x07e6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x00ef, code lost:
        if ((127(0x7f, float:1.78E-43) <= 65533(0xfffd, float:9.1831E-41) && 65533(0xfffd, float:9.1831E-41) <= 159(0x9f, float:2.23E-43)) != false) goto L_0x00f1;
     */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x0155  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x01b7  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0200  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x02e9  */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x0352  */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x03b8  */
    /* JADX WARNING: Removed duplicated region for block: B:356:0x0418  */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x045b  */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x055f  */
    /* JADX WARNING: Removed duplicated region for block: B:506:0x05c6  */
    /* JADX WARNING: Removed duplicated region for block: B:541:0x0630  */
    /* JADX WARNING: Removed duplicated region for block: B:573:0x069b  */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x06fb  */
    /* JADX WARNING: Removed duplicated region for block: B:643:0x0749  */
    /* JADX WARNING: Removed duplicated region for block: B:671:0x078c  */
    /* JADX WARNING: Removed duplicated region for block: B:718:0x0430 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0059 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0473 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:727:0x00a3 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:731:0x04d6 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:737:0x016d A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:739:0x0577 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:743:0x01cf A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:745:0x05de A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:748:0x0218 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:751:0x0648 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:754:0x027c A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x06b3 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:761:0x0301 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:763:0x0713 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:767:0x0761 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:769:0x036a A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:773:0x07a4 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:776:0x03d0 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final int codePointIndexToCharIndex(byte[] r30, int r31) {
        /*
            r0 = r31
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = r30
            int r5 = r4.length
            r6 = r30
            r7 = 0
            r8 = r3
        L_0x000c:
            if (r8 >= r5) goto L_0x0803
            byte r9 = r6[r8]
            r10 = 159(0x9f, float:2.23E-43)
            r11 = 127(0x7f, float:1.78E-43)
            r12 = 31
            r14 = 13
            r13 = 10
            r15 = 65536(0x10000, float:9.18355E-41)
            r16 = 2
            r17 = 0
            r18 = 1
            if (r9 < 0) goto L_0x00b3
            r19 = r9
            r20 = 0
            int r21 = r2 + 1
            if (r2 != r0) goto L_0x002e
            return r1
        L_0x002e:
            r2 = r19
            if (r2 == r13) goto L_0x0054
            if (r2 == r14) goto L_0x0054
            r19 = 0
            if (r2 < 0) goto L_0x003d
            if (r2 > r12) goto L_0x003d
            r22 = r18
            goto L_0x003f
        L_0x003d:
            r22 = r17
        L_0x003f:
            if (r22 != 0) goto L_0x0050
            if (r11 > r2) goto L_0x0048
            if (r2 > r10) goto L_0x0048
            r22 = r18
            goto L_0x004a
        L_0x0048:
            r22 = r17
        L_0x004a:
            if (r22 == 0) goto L_0x004d
            goto L_0x0050
        L_0x004d:
            r19 = r17
            goto L_0x0052
        L_0x0050:
            r19 = r18
        L_0x0052:
            if (r19 != 0) goto L_0x0059
        L_0x0054:
            r10 = 65533(0xfffd, float:9.1831E-41)
            if (r2 != r10) goto L_0x005b
        L_0x0059:
            r10 = -1
            return r10
        L_0x005b:
            if (r2 >= r15) goto L_0x0060
            r10 = r18
            goto L_0x0062
        L_0x0060:
            r10 = r16
        L_0x0062:
            int r1 = r1 + r10
            int r8 = r8 + 1
            r2 = r21
        L_0x0068:
            if (r8 >= r5) goto L_0x000c
            byte r10 = r6[r8]
            if (r10 < 0) goto L_0x000c
            int r10 = r8 + 1
            byte r8 = r6[r8]
            r20 = 0
            int r21 = r2 + 1
            if (r2 != r0) goto L_0x0079
            return r1
        L_0x0079:
            if (r8 == r13) goto L_0x009e
            if (r8 == r14) goto L_0x009e
            r2 = 0
            if (r8 < 0) goto L_0x0085
            if (r8 > r12) goto L_0x0085
            r22 = r18
            goto L_0x0087
        L_0x0085:
            r22 = r17
        L_0x0087:
            if (r22 != 0) goto L_0x009a
            if (r11 > r8) goto L_0x0092
            r11 = 159(0x9f, float:2.23E-43)
            if (r8 > r11) goto L_0x0092
            r11 = r18
            goto L_0x0094
        L_0x0092:
            r11 = r17
        L_0x0094:
            if (r11 == 0) goto L_0x0097
            goto L_0x009a
        L_0x0097:
            r2 = r17
            goto L_0x009c
        L_0x009a:
            r2 = r18
        L_0x009c:
            if (r2 != 0) goto L_0x00a3
        L_0x009e:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r8 != r2) goto L_0x00a5
        L_0x00a3:
            r2 = -1
            return r2
        L_0x00a5:
            if (r8 >= r15) goto L_0x00aa
            r2 = r18
            goto L_0x00ac
        L_0x00aa:
            r2 = r16
        L_0x00ac:
            int r1 = r1 + r2
            r8 = r10
            r2 = r21
            r11 = 127(0x7f, float:1.78E-43)
            goto L_0x0068
        L_0x00b3:
            r10 = 5
            r11 = r9
            r20 = 0
            int r10 = r11 >> r10
            r11 = -2
            if (r10 != r11) goto L_0x0230
            r10 = r6
            r11 = 0
            int r15 = r8 + 1
            if (r5 > r15) goto L_0x010f
            r15 = 65533(0xfffd, float:9.1831E-41)
            r21 = 0
            r23 = r15
            r24 = 0
            int r25 = r2 + 1
            if (r2 != r0) goto L_0x00d0
            return r1
        L_0x00d0:
            r2 = r23
            if (r2 == r13) goto L_0x00f5
            if (r2 == r14) goto L_0x00f5
            r13 = 0
            if (r2 < 0) goto L_0x00de
            if (r2 > r12) goto L_0x00de
            r12 = r18
            goto L_0x00e0
        L_0x00de:
            r12 = r17
        L_0x00e0:
            if (r12 != 0) goto L_0x00f1
            r12 = 127(0x7f, float:1.78E-43)
            if (r12 > r2) goto L_0x00ed
            r12 = 159(0x9f, float:2.23E-43)
            if (r2 > r12) goto L_0x00ed
            r12 = r18
            goto L_0x00ef
        L_0x00ed:
            r12 = r17
        L_0x00ef:
            if (r12 == 0) goto L_0x00f3
        L_0x00f1:
            r17 = r18
        L_0x00f3:
            if (r17 != 0) goto L_0x00fa
        L_0x00f5:
            r12 = 65533(0xfffd, float:9.1831E-41)
            if (r2 != r12) goto L_0x00fc
        L_0x00fa:
            r12 = -1
            return r12
        L_0x00fc:
            r12 = 65536(0x10000, float:9.18355E-41)
            if (r2 >= r12) goto L_0x0102
            r16 = r18
        L_0x0102:
            int r1 = r1 + r16
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            r27 = r3
            r16 = r18
            r2 = r25
            goto L_0x0228
        L_0x010f:
            byte r15 = r10[r8]
            int r23 = r8 + 1
            byte r12 = r10[r23]
            r23 = 0
            r25 = 192(0xc0, float:2.69E-43)
            r26 = r12
            r27 = 0
            r14 = r26 & r25
            r13 = 128(0x80, float:1.794E-43)
            if (r14 != r13) goto L_0x0126
            r13 = r18
            goto L_0x0128
        L_0x0126:
            r13 = r17
        L_0x0128:
            if (r13 != 0) goto L_0x0180
            r13 = 65533(0xfffd, float:9.1831E-41)
            r14 = 0
            r21 = r13
            r23 = 0
            int r26 = r2 + 1
            if (r2 != r0) goto L_0x0137
            return r1
        L_0x0137:
            r27 = r3
            r2 = r21
            r3 = 10
            if (r2 == r3) goto L_0x0168
            r3 = 13
            if (r2 == r3) goto L_0x0168
            r3 = 0
            if (r2 < 0) goto L_0x014f
            r21 = r3
            r3 = 31
            if (r2 > r3) goto L_0x0151
            r3 = r18
            goto L_0x0153
        L_0x014f:
            r21 = r3
        L_0x0151:
            r3 = r17
        L_0x0153:
            if (r3 != 0) goto L_0x0164
            r3 = 127(0x7f, float:1.78E-43)
            if (r3 > r2) goto L_0x0160
            r3 = 159(0x9f, float:2.23E-43)
            if (r2 > r3) goto L_0x0160
            r3 = r18
            goto L_0x0162
        L_0x0160:
            r3 = r17
        L_0x0162:
            if (r3 == 0) goto L_0x0166
        L_0x0164:
            r17 = r18
        L_0x0166:
            if (r17 != 0) goto L_0x016d
        L_0x0168:
            r3 = 65533(0xfffd, float:9.1831E-41)
            if (r2 != r3) goto L_0x016f
        L_0x016d:
            r3 = -1
            return r3
        L_0x016f:
            r3 = 65536(0x10000, float:9.18355E-41)
            if (r2 >= r3) goto L_0x0175
            r16 = r18
        L_0x0175:
            int r1 = r1 + r16
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            r16 = r18
            r2 = r26
            goto L_0x0228
        L_0x0180:
            r27 = r3
            r3 = r12 ^ 3968(0xf80, float:5.56E-42)
            int r13 = r15 << 6
            r3 = r3 ^ r13
            r13 = 128(0x80, float:1.794E-43)
            if (r3 >= r13) goto L_0x01dc
            r13 = 65533(0xfffd, float:9.1831E-41)
            r14 = 0
            r21 = r13
            r23 = 0
            int r26 = r2 + 1
            if (r2 != r0) goto L_0x019b
            return r1
        L_0x019b:
            r2 = r21
            r4 = 10
            if (r2 == r4) goto L_0x01ca
            r4 = 13
            if (r2 == r4) goto L_0x01ca
            r4 = 0
            if (r2 < 0) goto L_0x01b1
            r21 = r4
            r4 = 31
            if (r2 > r4) goto L_0x01b3
            r4 = r18
            goto L_0x01b5
        L_0x01b1:
            r21 = r4
        L_0x01b3:
            r4 = r17
        L_0x01b5:
            if (r4 != 0) goto L_0x01c6
            r4 = 127(0x7f, float:1.78E-43)
            if (r4 > r2) goto L_0x01c2
            r4 = 159(0x9f, float:2.23E-43)
            if (r2 > r4) goto L_0x01c2
            r4 = r18
            goto L_0x01c4
        L_0x01c2:
            r4 = r17
        L_0x01c4:
            if (r4 == 0) goto L_0x01c8
        L_0x01c6:
            r17 = r18
        L_0x01c8:
            if (r17 != 0) goto L_0x01cf
        L_0x01ca:
            r4 = 65533(0xfffd, float:9.1831E-41)
            if (r2 != r4) goto L_0x01d1
        L_0x01cf:
            r4 = -1
            return r4
        L_0x01d1:
            r4 = 65536(0x10000, float:9.18355E-41)
            if (r2 >= r4) goto L_0x01d6
            goto L_0x01d8
        L_0x01d6:
            r18 = r16
        L_0x01d8:
            int r1 = r1 + r18
            goto L_0x0224
        L_0x01dc:
            r4 = r3
            r13 = 0
            r14 = r4
            r21 = 0
            int r26 = r2 + 1
            if (r2 != r0) goto L_0x01e6
            return r1
        L_0x01e6:
            r2 = 10
            if (r14 == r2) goto L_0x0213
            r2 = 13
            if (r14 == r2) goto L_0x0213
            r2 = 0
            if (r14 < 0) goto L_0x01fa
            r23 = r2
            r2 = 31
            if (r14 > r2) goto L_0x01fc
            r2 = r18
            goto L_0x01fe
        L_0x01fa:
            r23 = r2
        L_0x01fc:
            r2 = r17
        L_0x01fe:
            if (r2 != 0) goto L_0x020f
            r2 = 127(0x7f, float:1.78E-43)
            if (r2 > r14) goto L_0x020b
            r2 = 159(0x9f, float:2.23E-43)
            if (r14 > r2) goto L_0x020b
            r2 = r18
            goto L_0x020d
        L_0x020b:
            r2 = r17
        L_0x020d:
            if (r2 == 0) goto L_0x0211
        L_0x020f:
            r17 = r18
        L_0x0211:
            if (r17 != 0) goto L_0x0218
        L_0x0213:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r14 != r2) goto L_0x021a
        L_0x0218:
            r2 = -1
            return r2
        L_0x021a:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r14 >= r2) goto L_0x021f
            goto L_0x0221
        L_0x021f:
            r18 = r16
        L_0x0221:
            int r1 = r1 + r18
        L_0x0224:
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            r2 = r26
        L_0x0228:
            int r8 = r8 + r16
            r4 = r30
            r3 = r27
            goto L_0x000c
        L_0x0230:
            r27 = r3
            r3 = 4
            r4 = r9
            r10 = 0
            int r3 = r4 >> r3
            if (r3 != r11) goto L_0x048c
            r3 = r6
            r11 = 0
            int r13 = r8 + 2
            if (r5 > r13) goto L_0x02aa
            r4 = 65533(0xfffd, float:9.1831E-41)
            r10 = 0
            r12 = r4
            r13 = 0
            int r14 = r2 + 1
            if (r2 != r0) goto L_0x024a
            return r1
        L_0x024a:
            r2 = 10
            if (r12 == r2) goto L_0x0277
            r2 = 13
            if (r12 == r2) goto L_0x0277
            r2 = 0
            if (r12 < 0) goto L_0x025c
            r15 = 31
            if (r12 > r15) goto L_0x025c
            r15 = r18
            goto L_0x025e
        L_0x025c:
            r15 = r17
        L_0x025e:
            if (r15 != 0) goto L_0x0273
            r15 = 127(0x7f, float:1.78E-43)
            if (r15 > r12) goto L_0x026b
            r15 = 159(0x9f, float:2.23E-43)
            if (r12 > r15) goto L_0x026b
            r15 = r18
            goto L_0x026d
        L_0x026b:
            r15 = r17
        L_0x026d:
            if (r15 == 0) goto L_0x0270
            goto L_0x0273
        L_0x0270:
            r2 = r17
            goto L_0x0275
        L_0x0273:
            r2 = r18
        L_0x0275:
            if (r2 != 0) goto L_0x027c
        L_0x0277:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r12 != r2) goto L_0x027e
        L_0x027c:
            r2 = -1
            return r2
        L_0x027e:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r12 >= r2) goto L_0x0285
            r2 = r18
            goto L_0x0287
        L_0x0285:
            r2 = r16
        L_0x0287:
            int r1 = r1 + r2
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            int r2 = r8 + 1
            if (r5 <= r2) goto L_0x02a5
            int r2 = r8 + 1
            byte r2 = r3[r2]
            r4 = 0
            r10 = 192(0xc0, float:2.69E-43)
            r12 = r2
            r13 = 0
            r10 = r10 & r12
            r12 = 128(0x80, float:1.794E-43)
            if (r10 != r12) goto L_0x029f
            r17 = r18
        L_0x029f:
            if (r17 != 0) goto L_0x02a2
            goto L_0x02a5
        L_0x02a2:
            r2 = r14
            goto L_0x0484
        L_0x02a5:
            r2 = r14
            r16 = r18
            goto L_0x0484
        L_0x02aa:
            byte r13 = r3[r8]
            int r14 = r8 + 1
            byte r14 = r3[r14]
            r15 = 0
            r23 = 192(0xc0, float:2.69E-43)
            r26 = r14
            r28 = 0
            r12 = r26 & r23
            r4 = 128(0x80, float:1.794E-43)
            if (r12 != r4) goto L_0x02c0
            r4 = r18
            goto L_0x02c2
        L_0x02c0:
            r4 = r17
        L_0x02c2:
            if (r4 != 0) goto L_0x0314
            r4 = 65533(0xfffd, float:9.1831E-41)
            r10 = 0
            r12 = r4
            r15 = 0
            int r21 = r2 + 1
            if (r2 != r0) goto L_0x02cf
            return r1
        L_0x02cf:
            r2 = 10
            if (r12 == r2) goto L_0x02fc
            r2 = 13
            if (r12 == r2) goto L_0x02fc
            r2 = 0
            if (r12 < 0) goto L_0x02e3
            r23 = r2
            r2 = 31
            if (r12 > r2) goto L_0x02e5
            r2 = r18
            goto L_0x02e7
        L_0x02e3:
            r23 = r2
        L_0x02e5:
            r2 = r17
        L_0x02e7:
            if (r2 != 0) goto L_0x02f8
            r2 = 127(0x7f, float:1.78E-43)
            if (r2 > r12) goto L_0x02f4
            r2 = 159(0x9f, float:2.23E-43)
            if (r12 > r2) goto L_0x02f4
            r2 = r18
            goto L_0x02f6
        L_0x02f4:
            r2 = r17
        L_0x02f6:
            if (r2 == 0) goto L_0x02fa
        L_0x02f8:
            r17 = r18
        L_0x02fa:
            if (r17 != 0) goto L_0x0301
        L_0x02fc:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r12 != r2) goto L_0x0303
        L_0x0301:
            r2 = -1
            return r2
        L_0x0303:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r12 >= r2) goto L_0x0309
            r16 = r18
        L_0x0309:
            int r1 = r1 + r16
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            r16 = r18
            r2 = r21
            goto L_0x0484
        L_0x0314:
            int r4 = r8 + 2
            byte r4 = r3[r4]
            r12 = 0
            r15 = 192(0xc0, float:2.69E-43)
            r26 = r4
            r28 = 0
            r15 = r26 & r15
            r10 = 128(0x80, float:1.794E-43)
            if (r15 != r10) goto L_0x0328
            r10 = r18
            goto L_0x032a
        L_0x0328:
            r10 = r17
        L_0x032a:
            if (r10 != 0) goto L_0x037c
            r10 = 65533(0xfffd, float:9.1831E-41)
            r12 = 0
            r15 = r10
            r21 = 0
            int r23 = r2 + 1
            if (r2 != r0) goto L_0x0338
            return r1
        L_0x0338:
            r2 = 10
            if (r15 == r2) goto L_0x0365
            r2 = 13
            if (r15 == r2) goto L_0x0365
            r2 = 0
            if (r15 < 0) goto L_0x034c
            r25 = r2
            r2 = 31
            if (r15 > r2) goto L_0x034e
            r2 = r18
            goto L_0x0350
        L_0x034c:
            r25 = r2
        L_0x034e:
            r2 = r17
        L_0x0350:
            if (r2 != 0) goto L_0x0361
            r2 = 127(0x7f, float:1.78E-43)
            if (r2 > r15) goto L_0x035d
            r2 = 159(0x9f, float:2.23E-43)
            if (r15 > r2) goto L_0x035d
            r2 = r18
            goto L_0x035f
        L_0x035d:
            r2 = r17
        L_0x035f:
            if (r2 == 0) goto L_0x0363
        L_0x0361:
            r17 = r18
        L_0x0363:
            if (r17 != 0) goto L_0x036a
        L_0x0365:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r15 != r2) goto L_0x036c
        L_0x036a:
            r2 = -1
            return r2
        L_0x036c:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r15 >= r2) goto L_0x0371
            goto L_0x0373
        L_0x0371:
            r18 = r16
        L_0x0373:
            int r1 = r1 + r18
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            r2 = r23
            goto L_0x0484
        L_0x037c:
            r10 = -123008(0xfffffffffffe1f80, float:NaN)
            r10 = r10 ^ r4
            int r12 = r14 << 6
            r10 = r10 ^ r12
            int r12 = r13 << 12
            r10 = r10 ^ r12
            r12 = 2048(0x800, float:2.87E-42)
            if (r10 >= r12) goto L_0x03df
            r12 = 65533(0xfffd, float:9.1831E-41)
            r15 = 0
            r21 = r12
            r23 = 0
            int r26 = r2 + 1
            if (r2 != r0) goto L_0x039a
            return r1
        L_0x039a:
            r2 = r21
            r21 = r3
            r3 = 10
            if (r2 == r3) goto L_0x03cb
            r3 = 13
            if (r2 == r3) goto L_0x03cb
            r3 = 0
            if (r2 < 0) goto L_0x03b2
            r25 = r3
            r3 = 31
            if (r2 > r3) goto L_0x03b4
            r3 = r18
            goto L_0x03b6
        L_0x03b2:
            r25 = r3
        L_0x03b4:
            r3 = r17
        L_0x03b6:
            if (r3 != 0) goto L_0x03c7
            r3 = 127(0x7f, float:1.78E-43)
            if (r3 > r2) goto L_0x03c3
            r3 = 159(0x9f, float:2.23E-43)
            if (r2 > r3) goto L_0x03c3
            r3 = r18
            goto L_0x03c5
        L_0x03c3:
            r3 = r17
        L_0x03c5:
            if (r3 == 0) goto L_0x03c9
        L_0x03c7:
            r17 = r18
        L_0x03c9:
            if (r17 != 0) goto L_0x03d0
        L_0x03cb:
            r3 = 65533(0xfffd, float:9.1831E-41)
            if (r2 != r3) goto L_0x03d2
        L_0x03d0:
            r3 = -1
            return r3
        L_0x03d2:
            r3 = 65536(0x10000, float:9.18355E-41)
            if (r2 >= r3) goto L_0x03d8
            r16 = r18
        L_0x03d8:
            int r1 = r1 + r16
        L_0x03db:
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            goto L_0x0480
        L_0x03df:
            r21 = r3
            r3 = 55296(0xd800, float:7.7486E-41)
            if (r3 > r10) goto L_0x03ee
            r3 = 57343(0xdfff, float:8.0355E-41)
            if (r10 > r3) goto L_0x03ee
            r3 = r18
            goto L_0x03f0
        L_0x03ee:
            r3 = r17
        L_0x03f0:
            if (r3 == 0) goto L_0x0437
            r3 = 65533(0xfffd, float:9.1831E-41)
            r12 = 0
            r15 = r3
            r23 = 0
            int r26 = r2 + 1
            if (r2 != r0) goto L_0x03fe
            return r1
        L_0x03fe:
            r2 = 10
            if (r15 == r2) goto L_0x042b
            r2 = 13
            if (r15 == r2) goto L_0x042b
            r2 = 0
            if (r15 < 0) goto L_0x0412
            r25 = r2
            r2 = 31
            if (r15 > r2) goto L_0x0414
            r2 = r18
            goto L_0x0416
        L_0x0412:
            r25 = r2
        L_0x0414:
            r2 = r17
        L_0x0416:
            if (r2 != 0) goto L_0x0427
            r2 = 127(0x7f, float:1.78E-43)
            if (r2 > r15) goto L_0x0423
            r2 = 159(0x9f, float:2.23E-43)
            if (r15 > r2) goto L_0x0423
            r2 = r18
            goto L_0x0425
        L_0x0423:
            r2 = r17
        L_0x0425:
            if (r2 == 0) goto L_0x0429
        L_0x0427:
            r17 = r18
        L_0x0429:
            if (r17 != 0) goto L_0x0430
        L_0x042b:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r15 != r2) goto L_0x0432
        L_0x0430:
            r2 = -1
            return r2
        L_0x0432:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r15 >= r2) goto L_0x047b
            goto L_0x0479
        L_0x0437:
            r3 = r10
            r12 = 0
            r15 = r3
            r23 = 0
            int r26 = r2 + 1
            if (r2 != r0) goto L_0x0441
            return r1
        L_0x0441:
            r2 = 10
            if (r15 == r2) goto L_0x046e
            r2 = 13
            if (r15 == r2) goto L_0x046e
            r2 = 0
            if (r15 < 0) goto L_0x0455
            r25 = r2
            r2 = 31
            if (r15 > r2) goto L_0x0457
            r2 = r18
            goto L_0x0459
        L_0x0455:
            r25 = r2
        L_0x0457:
            r2 = r17
        L_0x0459:
            if (r2 != 0) goto L_0x046a
            r2 = 127(0x7f, float:1.78E-43)
            if (r2 > r15) goto L_0x0466
            r2 = 159(0x9f, float:2.23E-43)
            if (r15 > r2) goto L_0x0466
            r2 = r18
            goto L_0x0468
        L_0x0466:
            r2 = r17
        L_0x0468:
            if (r2 == 0) goto L_0x046c
        L_0x046a:
            r17 = r18
        L_0x046c:
            if (r17 != 0) goto L_0x0473
        L_0x046e:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r15 != r2) goto L_0x0475
        L_0x0473:
            r2 = -1
            return r2
        L_0x0475:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r15 >= r2) goto L_0x047b
        L_0x0479:
            r16 = r18
        L_0x047b:
            int r1 = r1 + r16
            goto L_0x03db
        L_0x0480:
            r2 = r26
            r16 = 3
        L_0x0484:
            int r8 = r8 + r16
            r4 = r30
            r3 = r27
            goto L_0x000c
        L_0x048c:
            r3 = 3
            r4 = r9
            r10 = 0
            int r3 = r4 >> r3
            if (r3 != r11) goto L_0x07b8
            r3 = r6
            r4 = 0
            int r10 = r8 + 3
            if (r5 > r10) goto L_0x0523
            r10 = 65533(0xfffd, float:9.1831E-41)
            r11 = 0
            r12 = r10
            r13 = 0
            int r14 = r2 + 1
            if (r2 != r0) goto L_0x04a4
            return r1
        L_0x04a4:
            r2 = 10
            if (r12 == r2) goto L_0x04d1
            r2 = 13
            if (r12 == r2) goto L_0x04d1
            r2 = 0
            if (r12 < 0) goto L_0x04b6
            r15 = 31
            if (r12 > r15) goto L_0x04b6
            r15 = r18
            goto L_0x04b8
        L_0x04b6:
            r15 = r17
        L_0x04b8:
            if (r15 != 0) goto L_0x04cd
            r15 = 127(0x7f, float:1.78E-43)
            if (r15 > r12) goto L_0x04c5
            r15 = 159(0x9f, float:2.23E-43)
            if (r12 > r15) goto L_0x04c5
            r15 = r18
            goto L_0x04c7
        L_0x04c5:
            r15 = r17
        L_0x04c7:
            if (r15 == 0) goto L_0x04ca
            goto L_0x04cd
        L_0x04ca:
            r2 = r17
            goto L_0x04cf
        L_0x04cd:
            r2 = r18
        L_0x04cf:
            if (r2 != 0) goto L_0x04d6
        L_0x04d1:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r12 != r2) goto L_0x04d8
        L_0x04d6:
            r2 = -1
            return r2
        L_0x04d8:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r12 >= r2) goto L_0x04df
            r2 = r18
            goto L_0x04e1
        L_0x04df:
            r2 = r16
        L_0x04e1:
            int r1 = r1 + r2
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            int r2 = r8 + 1
            if (r5 <= r2) goto L_0x051e
            int r2 = r8 + 1
            byte r2 = r3[r2]
            r10 = 0
            r11 = 192(0xc0, float:2.69E-43)
            r12 = r2
            r13 = 0
            r11 = r11 & r12
            r12 = 128(0x80, float:1.794E-43)
            if (r11 != r12) goto L_0x04fa
            r2 = r18
            goto L_0x04fc
        L_0x04fa:
            r2 = r17
        L_0x04fc:
            if (r2 != 0) goto L_0x04ff
            goto L_0x051e
        L_0x04ff:
            int r2 = r8 + 2
            if (r5 <= r2) goto L_0x051b
            int r2 = r8 + 2
            byte r2 = r3[r2]
            r10 = 0
            r11 = 192(0xc0, float:2.69E-43)
            r12 = r2
            r13 = 0
            r11 = r11 & r12
            r12 = 128(0x80, float:1.794E-43)
            if (r11 != r12) goto L_0x0513
            r17 = r18
        L_0x0513:
            if (r17 != 0) goto L_0x0516
            goto L_0x051b
        L_0x0516:
            r2 = r14
            r16 = 3
            goto L_0x07b0
        L_0x051b:
            r2 = r14
            goto L_0x07b0
        L_0x051e:
            r2 = r14
            r16 = r18
            goto L_0x07b0
        L_0x0523:
            byte r10 = r3[r8]
            int r11 = r8 + 1
            byte r11 = r3[r11]
            r12 = 0
            r13 = 192(0xc0, float:2.69E-43)
            r14 = r11
            r15 = 0
            r13 = r13 & r14
            r14 = 128(0x80, float:1.794E-43)
            if (r13 != r14) goto L_0x0536
            r12 = r18
            goto L_0x0538
        L_0x0536:
            r12 = r17
        L_0x0538:
            if (r12 != 0) goto L_0x058a
            r12 = 65533(0xfffd, float:9.1831E-41)
            r13 = 0
            r14 = r12
            r15 = 0
            int r21 = r2 + 1
            if (r2 != r0) goto L_0x0545
            return r1
        L_0x0545:
            r2 = 10
            if (r14 == r2) goto L_0x0572
            r2 = 13
            if (r14 == r2) goto L_0x0572
            r2 = 0
            if (r14 < 0) goto L_0x0559
            r23 = r2
            r2 = 31
            if (r14 > r2) goto L_0x055b
            r2 = r18
            goto L_0x055d
        L_0x0559:
            r23 = r2
        L_0x055b:
            r2 = r17
        L_0x055d:
            if (r2 != 0) goto L_0x056e
            r2 = 127(0x7f, float:1.78E-43)
            if (r2 > r14) goto L_0x056a
            r2 = 159(0x9f, float:2.23E-43)
            if (r14 > r2) goto L_0x056a
            r2 = r18
            goto L_0x056c
        L_0x056a:
            r2 = r17
        L_0x056c:
            if (r2 == 0) goto L_0x0570
        L_0x056e:
            r17 = r18
        L_0x0570:
            if (r17 != 0) goto L_0x0577
        L_0x0572:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r14 != r2) goto L_0x0579
        L_0x0577:
            r2 = -1
            return r2
        L_0x0579:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r14 >= r2) goto L_0x057f
            r16 = r18
        L_0x057f:
            int r1 = r1 + r16
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            r16 = r18
            r2 = r21
            goto L_0x07b0
        L_0x058a:
            int r12 = r8 + 2
            byte r12 = r3[r12]
            r13 = 0
            r14 = 192(0xc0, float:2.69E-43)
            r15 = r12
            r28 = 0
            r14 = r14 & r15
            r15 = 128(0x80, float:1.794E-43)
            if (r14 != r15) goto L_0x059c
            r13 = r18
            goto L_0x059e
        L_0x059c:
            r13 = r17
        L_0x059e:
            if (r13 != 0) goto L_0x05f0
            r13 = 65533(0xfffd, float:9.1831E-41)
            r14 = 0
            r15 = r13
            r21 = 0
            int r23 = r2 + 1
            if (r2 != r0) goto L_0x05ac
            return r1
        L_0x05ac:
            r2 = 10
            if (r15 == r2) goto L_0x05d9
            r2 = 13
            if (r15 == r2) goto L_0x05d9
            r2 = 0
            if (r15 < 0) goto L_0x05c0
            r25 = r2
            r2 = 31
            if (r15 > r2) goto L_0x05c2
            r2 = r18
            goto L_0x05c4
        L_0x05c0:
            r25 = r2
        L_0x05c2:
            r2 = r17
        L_0x05c4:
            if (r2 != 0) goto L_0x05d5
            r2 = 127(0x7f, float:1.78E-43)
            if (r2 > r15) goto L_0x05d1
            r2 = 159(0x9f, float:2.23E-43)
            if (r15 > r2) goto L_0x05d1
            r2 = r18
            goto L_0x05d3
        L_0x05d1:
            r2 = r17
        L_0x05d3:
            if (r2 == 0) goto L_0x05d7
        L_0x05d5:
            r17 = r18
        L_0x05d7:
            if (r17 != 0) goto L_0x05de
        L_0x05d9:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r15 != r2) goto L_0x05e0
        L_0x05de:
            r2 = -1
            return r2
        L_0x05e0:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r15 >= r2) goto L_0x05e5
            goto L_0x05e7
        L_0x05e5:
            r18 = r16
        L_0x05e7:
            int r1 = r1 + r18
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            r2 = r23
            goto L_0x07b0
        L_0x05f0:
            int r13 = r8 + 3
            byte r13 = r3[r13]
            r14 = 0
            r15 = 192(0xc0, float:2.69E-43)
            r28 = r13
            r29 = 0
            r15 = r28 & r15
            r28 = r3
            r3 = 128(0x80, float:1.794E-43)
            if (r15 != r3) goto L_0x0606
            r3 = r18
            goto L_0x0608
        L_0x0606:
            r3 = r17
        L_0x0608:
            if (r3 != 0) goto L_0x065b
            r3 = 65533(0xfffd, float:9.1831E-41)
            r14 = 0
            r15 = r3
            r21 = 0
            int r23 = r2 + 1
            if (r2 != r0) goto L_0x0616
            return r1
        L_0x0616:
            r2 = 10
            if (r15 == r2) goto L_0x0643
            r2 = 13
            if (r15 == r2) goto L_0x0643
            r2 = 0
            if (r15 < 0) goto L_0x062a
            r25 = r2
            r2 = 31
            if (r15 > r2) goto L_0x062c
            r2 = r18
            goto L_0x062e
        L_0x062a:
            r25 = r2
        L_0x062c:
            r2 = r17
        L_0x062e:
            if (r2 != 0) goto L_0x063f
            r2 = 127(0x7f, float:1.78E-43)
            if (r2 > r15) goto L_0x063b
            r2 = 159(0x9f, float:2.23E-43)
            if (r15 > r2) goto L_0x063b
            r2 = r18
            goto L_0x063d
        L_0x063b:
            r2 = r17
        L_0x063d:
            if (r2 == 0) goto L_0x0641
        L_0x063f:
            r17 = r18
        L_0x0641:
            if (r17 != 0) goto L_0x0648
        L_0x0643:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r15 != r2) goto L_0x064a
        L_0x0648:
            r2 = -1
            return r2
        L_0x064a:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r15 >= r2) goto L_0x0650
            r16 = r18
        L_0x0650:
            int r1 = r1 + r16
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            r2 = r23
            r16 = 3
            goto L_0x07b0
        L_0x065b:
            r3 = 3678080(0x381f80, float:5.154088E-39)
            r3 = r3 ^ r13
            int r14 = r12 << 6
            r3 = r3 ^ r14
            int r14 = r11 << 12
            r3 = r3 ^ r14
            int r14 = r10 << 18
            r3 = r3 ^ r14
            r14 = 1114111(0x10ffff, float:1.561202E-39)
            if (r3 <= r14) goto L_0x06c2
            r14 = 65533(0xfffd, float:9.1831E-41)
            r15 = 0
            r21 = r14
            r23 = 0
            int r26 = r2 + 1
            if (r2 != r0) goto L_0x067d
            return r1
        L_0x067d:
            r2 = r21
            r21 = r4
            r4 = 10
            if (r2 == r4) goto L_0x06ae
            r4 = 13
            if (r2 == r4) goto L_0x06ae
            r4 = 0
            if (r2 < 0) goto L_0x0695
            r25 = r4
            r4 = 31
            if (r2 > r4) goto L_0x0697
            r4 = r18
            goto L_0x0699
        L_0x0695:
            r25 = r4
        L_0x0697:
            r4 = r17
        L_0x0699:
            if (r4 != 0) goto L_0x06aa
            r4 = 127(0x7f, float:1.78E-43)
            if (r4 > r2) goto L_0x06a6
            r4 = 159(0x9f, float:2.23E-43)
            if (r2 > r4) goto L_0x06a6
            r4 = r18
            goto L_0x06a8
        L_0x06a6:
            r4 = r17
        L_0x06a8:
            if (r4 == 0) goto L_0x06ac
        L_0x06aa:
            r17 = r18
        L_0x06ac:
            if (r17 != 0) goto L_0x06b3
        L_0x06ae:
            r4 = 65533(0xfffd, float:9.1831E-41)
            if (r2 != r4) goto L_0x06b5
        L_0x06b3:
            r4 = -1
            return r4
        L_0x06b5:
            r4 = 65536(0x10000, float:9.18355E-41)
            if (r2 >= r4) goto L_0x06bb
            r16 = r18
        L_0x06bb:
            int r1 = r1 + r16
        L_0x06be:
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            goto L_0x07ac
        L_0x06c2:
            r21 = r4
            r4 = 55296(0xd800, float:7.7486E-41)
            if (r4 > r3) goto L_0x06d1
            r4 = 57343(0xdfff, float:8.0355E-41)
            if (r3 > r4) goto L_0x06d1
            r4 = r18
            goto L_0x06d3
        L_0x06d1:
            r4 = r17
        L_0x06d3:
            if (r4 == 0) goto L_0x071f
            r4 = 65533(0xfffd, float:9.1831E-41)
            r14 = 0
            r15 = r4
            r23 = 0
            int r26 = r2 + 1
            if (r2 != r0) goto L_0x06e1
            return r1
        L_0x06e1:
            r2 = 10
            if (r15 == r2) goto L_0x070e
            r2 = 13
            if (r15 == r2) goto L_0x070e
            r2 = 0
            if (r15 < 0) goto L_0x06f5
            r25 = r2
            r2 = 31
            if (r15 > r2) goto L_0x06f7
            r2 = r18
            goto L_0x06f9
        L_0x06f5:
            r25 = r2
        L_0x06f7:
            r2 = r17
        L_0x06f9:
            if (r2 != 0) goto L_0x070a
            r2 = 127(0x7f, float:1.78E-43)
            if (r2 > r15) goto L_0x0706
            r2 = 159(0x9f, float:2.23E-43)
            if (r15 > r2) goto L_0x0706
            r2 = r18
            goto L_0x0708
        L_0x0706:
            r2 = r17
        L_0x0708:
            if (r2 == 0) goto L_0x070c
        L_0x070a:
            r17 = r18
        L_0x070c:
            if (r17 != 0) goto L_0x0713
        L_0x070e:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r15 != r2) goto L_0x0715
        L_0x0713:
            r2 = -1
            return r2
        L_0x0715:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r15 >= r2) goto L_0x071b
        L_0x0719:
            r16 = r18
        L_0x071b:
            int r1 = r1 + r16
            goto L_0x06be
        L_0x071f:
            r4 = 65536(0x10000, float:9.18355E-41)
            if (r3 >= r4) goto L_0x0768
            r4 = 65533(0xfffd, float:9.1831E-41)
            r14 = 0
            r15 = r4
            r23 = 0
            int r26 = r2 + 1
            if (r2 != r0) goto L_0x072f
            return r1
        L_0x072f:
            r2 = 10
            if (r15 == r2) goto L_0x075c
            r2 = 13
            if (r15 == r2) goto L_0x075c
            r2 = 0
            if (r15 < 0) goto L_0x0743
            r25 = r2
            r2 = 31
            if (r15 > r2) goto L_0x0745
            r2 = r18
            goto L_0x0747
        L_0x0743:
            r25 = r2
        L_0x0745:
            r2 = r17
        L_0x0747:
            if (r2 != 0) goto L_0x0758
            r2 = 127(0x7f, float:1.78E-43)
            if (r2 > r15) goto L_0x0754
            r2 = 159(0x9f, float:2.23E-43)
            if (r15 > r2) goto L_0x0754
            r2 = r18
            goto L_0x0756
        L_0x0754:
            r2 = r17
        L_0x0756:
            if (r2 == 0) goto L_0x075a
        L_0x0758:
            r17 = r18
        L_0x075a:
            if (r17 != 0) goto L_0x0761
        L_0x075c:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r15 != r2) goto L_0x0763
        L_0x0761:
            r2 = -1
            return r2
        L_0x0763:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r15 >= r2) goto L_0x071b
            goto L_0x0719
        L_0x0768:
            r4 = r3
            r14 = 0
            r15 = r4
            r23 = 0
            int r26 = r2 + 1
            if (r2 != r0) goto L_0x0772
            return r1
        L_0x0772:
            r2 = 10
            if (r15 == r2) goto L_0x079f
            r2 = 13
            if (r15 == r2) goto L_0x079f
            r2 = 0
            if (r15 < 0) goto L_0x0786
            r25 = r2
            r2 = 31
            if (r15 > r2) goto L_0x0788
            r2 = r18
            goto L_0x078a
        L_0x0786:
            r25 = r2
        L_0x0788:
            r2 = r17
        L_0x078a:
            if (r2 != 0) goto L_0x079b
            r2 = 127(0x7f, float:1.78E-43)
            if (r2 > r15) goto L_0x0797
            r2 = 159(0x9f, float:2.23E-43)
            if (r15 > r2) goto L_0x0797
            r2 = r18
            goto L_0x0799
        L_0x0797:
            r2 = r17
        L_0x0799:
            if (r2 == 0) goto L_0x079d
        L_0x079b:
            r17 = r18
        L_0x079d:
            if (r17 != 0) goto L_0x07a4
        L_0x079f:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r15 != r2) goto L_0x07a6
        L_0x07a4:
            r2 = -1
            return r2
        L_0x07a6:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r15 >= r2) goto L_0x071b
            goto L_0x0719
        L_0x07ac:
            r16 = 4
            r2 = r26
        L_0x07b0:
            int r8 = r8 + r16
            r4 = r30
            r3 = r27
            goto L_0x000c
        L_0x07b8:
            r3 = 65533(0xfffd, float:9.1831E-41)
            r4 = 0
            int r10 = r2 + 1
            if (r2 != r0) goto L_0x07c1
            return r1
        L_0x07c1:
            r2 = 10
            if (r3 == r2) goto L_0x07ea
            r2 = 13
            if (r3 == r2) goto L_0x07ea
            r2 = 0
            if (r3 < 0) goto L_0x07d3
            r11 = 31
            if (r3 > r11) goto L_0x07d3
            r11 = r18
            goto L_0x07d5
        L_0x07d3:
            r11 = r17
        L_0x07d5:
            if (r11 != 0) goto L_0x07e6
            r11 = 127(0x7f, float:1.78E-43)
            if (r11 > r3) goto L_0x07e2
            r11 = 159(0x9f, float:2.23E-43)
            if (r3 > r11) goto L_0x07e2
            r11 = r18
            goto L_0x07e4
        L_0x07e2:
            r11 = r17
        L_0x07e4:
            if (r11 == 0) goto L_0x07e8
        L_0x07e6:
            r17 = r18
        L_0x07e8:
            if (r17 != 0) goto L_0x07ef
        L_0x07ea:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r3 != r2) goto L_0x07f1
        L_0x07ef:
            r2 = -1
            return r2
        L_0x07f1:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r3 >= r2) goto L_0x07f7
            r16 = r18
        L_0x07f7:
            int r1 = r1 + r16
            int r8 = r8 + 1
            r4 = r30
            r2 = r10
            r3 = r27
            goto L_0x000c
        L_0x0803:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.internal._ByteStringKt.codePointIndexToCharIndex(byte[], int):int");
    }
}

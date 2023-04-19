package okio.internal;

import java.util.Arrays;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.ByteCompanionObject;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okio.Utf8;

@Metadata(d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u0012\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\u001a\n\u0010\u0000\u001a\u00020\u0001*\u00020\u0002\u001a\u001e\u0010\u0003\u001a\u00020\u0002*\u00020\u00012\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0005¨\u0006\u0007"}, d2 = {"commonAsUtf8ToByteArray", "", "", "commonToUtf8String", "beginIndex", "", "endIndex", "okio"}, k = 2, mv = {1, 5, 1}, xi = 48)
/* compiled from: -Utf8.kt */
public final class _Utf8Kt {
    public static /* synthetic */ String commonToUtf8String$default(byte[] bArr, int i, int i2, int i3, Object obj) {
        if ((i3 & 1) != 0) {
            i = 0;
        }
        if ((i3 & 2) != 0) {
            i2 = bArr.length;
        }
        return commonToUtf8String(bArr, i, i2);
    }

    public static final String commonToUtf8String(byte[] $this$commonToUtf8String, int beginIndex, int endIndex) {
        byte b2$iv$iv;
        int length;
        int length2;
        int length3;
        int length4;
        int length5;
        int length6;
        int length7;
        int i;
        int length8;
        int length9;
        int length10;
        byte b1$iv$iv;
        int length11;
        int length12;
        byte[] bArr = $this$commonToUtf8String;
        int i2 = beginIndex;
        int i3 = endIndex;
        Intrinsics.checkNotNullParameter(bArr, "<this>");
        if (i2 < 0 || i3 > bArr.length || i2 > i3) {
            throw new ArrayIndexOutOfBoundsException("size=" + bArr.length + " beginIndex=" + i2 + " endIndex=" + i3);
        }
        char[] chars = new char[(i3 - i2)];
        int length13 = 0;
        byte[] $this$processUtf16Chars$iv = $this$commonToUtf8String;
        int $i$f$processUtf16Chars = 0;
        int index$iv = beginIndex;
        while (index$iv < i3) {
            byte b0$iv = $this$processUtf16Chars$iv[index$iv];
            if (b0$iv >= 0) {
                chars[length13] = (char) b0$iv;
                index$iv++;
                length13++;
                while (index$iv < i3 && $this$processUtf16Chars$iv[index$iv] >= 0) {
                    chars[length13] = (char) $this$processUtf16Chars$iv[index$iv];
                    index$iv++;
                    length13++;
                }
            } else if ((b0$iv >> 5) == -2) {
                byte[] $this$process2Utf8Bytes$iv$iv = $this$processUtf16Chars$iv;
                if (i3 <= index$iv + 1) {
                    int length14 = length13 + 1;
                    chars[length13] = (char) Utf8.REPLACEMENT_CODE_POINT;
                    Unit unit = Unit.INSTANCE;
                    length12 = length14;
                    b1$iv$iv = 1;
                    length10 = $i$f$processUtf16Chars;
                } else {
                    byte b0$iv$iv = $this$process2Utf8Bytes$iv$iv[index$iv];
                    byte b1$iv$iv2 = $this$process2Utf8Bytes$iv$iv[index$iv + 1];
                    if (!((b1$iv$iv2 & 192) == 128)) {
                        int length15 = length13 + 1;
                        chars[length13] = (char) Utf8.REPLACEMENT_CODE_POINT;
                        Unit unit2 = Unit.INSTANCE;
                        length10 = $i$f$processUtf16Chars;
                        length12 = length15;
                        b1$iv$iv = 1;
                    } else {
                        int codePoint$iv$iv = (b1$iv$iv2 ^ Utf8.MASK_2BYTES) ^ (b0$iv$iv << 6);
                        if (codePoint$iv$iv < 128) {
                            length10 = $i$f$processUtf16Chars;
                            length11 = length13 + 1;
                            chars[length13] = (char) Utf8.REPLACEMENT_CODE_POINT;
                        } else {
                            length10 = $i$f$processUtf16Chars;
                            length11 = length13 + 1;
                            chars[length13] = (char) codePoint$iv$iv;
                        }
                        Unit unit3 = Unit.INSTANCE;
                        length12 = length11;
                        b1$iv$iv = 2;
                    }
                }
                index$iv += b1$iv$iv;
                $i$f$processUtf16Chars = length10;
            } else {
                int $i$f$processUtf16Chars2 = $i$f$processUtf16Chars;
                if ((b0$iv >> 4) == -2) {
                    byte[] $this$process3Utf8Bytes$iv$iv = $this$processUtf16Chars$iv;
                    if (i3 <= index$iv + 2) {
                        int length16 = length13 + 1;
                        chars[length13] = (char) Utf8.REPLACEMENT_CODE_POINT;
                        Unit unit4 = Unit.INSTANCE;
                        if (i3 > index$iv + 1) {
                            if ((192 & $this$process3Utf8Bytes$iv$iv[index$iv + 1]) == 128) {
                                length9 = length16;
                                i = 2;
                            }
                        }
                        length9 = length16;
                        i = 1;
                    } else {
                        byte b0$iv$iv2 = $this$process3Utf8Bytes$iv$iv[index$iv];
                        byte b1$iv$iv3 = $this$process3Utf8Bytes$iv$iv[index$iv + 1];
                        if (!((b1$iv$iv3 & 192) == 128)) {
                            int length17 = length13 + 1;
                            chars[length13] = (char) Utf8.REPLACEMENT_CODE_POINT;
                            Unit unit5 = Unit.INSTANCE;
                            length9 = length17;
                            i = 1;
                        } else {
                            byte b2$iv$iv2 = $this$process3Utf8Bytes$iv$iv[index$iv + 2];
                            if (!((b2$iv$iv2 & 192) == 128)) {
                                int length18 = length13 + 1;
                                chars[length13] = (char) Utf8.REPLACEMENT_CODE_POINT;
                                Unit unit6 = Unit.INSTANCE;
                                length9 = length18;
                                i = 2;
                            } else {
                                int codePoint$iv$iv2 = ((-123008 ^ b2$iv$iv2) ^ (b1$iv$iv3 << 6)) ^ (b0$iv$iv2 << 12);
                                if (codePoint$iv$iv2 < 2048) {
                                    length8 = length13 + 1;
                                    chars[length13] = (char) Utf8.REPLACEMENT_CODE_POINT;
                                } else {
                                    if (55296 <= codePoint$iv$iv2 && codePoint$iv$iv2 <= 57343) {
                                        length8 = length13 + 1;
                                        chars[length13] = (char) Utf8.REPLACEMENT_CODE_POINT;
                                    } else {
                                        length8 = length13 + 1;
                                        chars[length13] = (char) codePoint$iv$iv2;
                                    }
                                }
                                Unit unit7 = Unit.INSTANCE;
                                length9 = length8;
                                i = 3;
                            }
                        }
                    }
                    index$iv += i;
                    $i$f$processUtf16Chars = $i$f$processUtf16Chars2;
                } else if ((b0$iv >> 3) == -2) {
                    byte[] $this$process4Utf8Bytes$iv$iv = $this$processUtf16Chars$iv;
                    if (i3 <= index$iv + 3) {
                        if (65533 != 65533) {
                            int length19 = length13 + 1;
                            chars[length13] = (char) ((Utf8.REPLACEMENT_CODE_POINT >>> 10) + Utf8.HIGH_SURROGATE_HEADER);
                            length7 = length19 + 1;
                            chars[length19] = (char) ((65533 & 1023) + Utf8.LOG_SURROGATE_HEADER);
                        } else {
                            chars[length13] = Utf8.REPLACEMENT_CHARACTER;
                            length7 = length13 + 1;
                        }
                        Unit unit8 = Unit.INSTANCE;
                        if (i3 > index$iv + 1) {
                            if (((192 & $this$process4Utf8Bytes$iv$iv[index$iv + 1]) == 128 ? (byte) 1 : 0) != 0) {
                                if (i3 > index$iv + 2) {
                                    if ((192 & $this$process4Utf8Bytes$iv$iv[index$iv + 2]) == 128) {
                                        length3 = length7;
                                        b2$iv$iv = 3;
                                    }
                                }
                                length3 = length7;
                                b2$iv$iv = 2;
                            }
                        }
                        length3 = length7;
                        b2$iv$iv = 1;
                    } else {
                        byte b0$iv$iv3 = $this$process4Utf8Bytes$iv$iv[index$iv];
                        byte b1$iv$iv4 = $this$process4Utf8Bytes$iv$iv[index$iv + 1];
                        if (!((b1$iv$iv4 & 192) == 128)) {
                            if (65533 != 65533) {
                                int length20 = length13 + 1;
                                chars[length13] = (char) ((Utf8.REPLACEMENT_CODE_POINT >>> 10) + Utf8.HIGH_SURROGATE_HEADER);
                                length6 = length20 + 1;
                                chars[length20] = (char) ((65533 & 1023) + Utf8.LOG_SURROGATE_HEADER);
                            } else {
                                chars[length13] = Utf8.REPLACEMENT_CHARACTER;
                                length6 = length13 + 1;
                            }
                            Unit unit9 = Unit.INSTANCE;
                            length3 = length6;
                            b2$iv$iv = 1;
                        } else {
                            byte b2$iv$iv3 = $this$process4Utf8Bytes$iv$iv[index$iv + 2];
                            if (!((b2$iv$iv3 & 192) == 128)) {
                                if (65533 != 65533) {
                                    int length21 = length13 + 1;
                                    chars[length13] = (char) ((Utf8.REPLACEMENT_CODE_POINT >>> 10) + Utf8.HIGH_SURROGATE_HEADER);
                                    length5 = length21 + 1;
                                    chars[length21] = (char) ((65533 & 1023) + Utf8.LOG_SURROGATE_HEADER);
                                } else {
                                    chars[length13] = Utf8.REPLACEMENT_CHARACTER;
                                    length5 = length13 + 1;
                                }
                                Unit unit10 = Unit.INSTANCE;
                                length3 = length5;
                                b2$iv$iv = 2;
                            } else {
                                byte b3$iv$iv = $this$process4Utf8Bytes$iv$iv[index$iv + 3];
                                if (!((b3$iv$iv & 192) == 128)) {
                                    if (65533 != 65533) {
                                        int length22 = length13 + 1;
                                        chars[length13] = (char) ((Utf8.REPLACEMENT_CODE_POINT >>> 10) + Utf8.HIGH_SURROGATE_HEADER);
                                        length4 = length22 + 1;
                                        chars[length22] = (char) ((65533 & 1023) + Utf8.LOG_SURROGATE_HEADER);
                                    } else {
                                        chars[length13] = Utf8.REPLACEMENT_CHARACTER;
                                        length4 = length13 + 1;
                                    }
                                    Unit unit11 = Unit.INSTANCE;
                                    length3 = length4;
                                    b2$iv$iv = 3;
                                } else {
                                    int codePoint$iv$iv3 = (((3678080 ^ b3$iv$iv) ^ (b2$iv$iv3 << 6)) ^ (b1$iv$iv4 << 12)) ^ (b0$iv$iv3 << 18);
                                    if (codePoint$iv$iv3 <= 1114111) {
                                        if (55296 <= codePoint$iv$iv3 && codePoint$iv$iv3 <= 57343) {
                                            if (65533 != 65533) {
                                                int length23 = length13 + 1;
                                                chars[length13] = (char) ((Utf8.REPLACEMENT_CODE_POINT >>> 10) + Utf8.HIGH_SURROGATE_HEADER);
                                                length2 = length23 + 1;
                                                chars[length23] = (char) ((65533 & 1023) + Utf8.LOG_SURROGATE_HEADER);
                                                Unit unit12 = Unit.INSTANCE;
                                                b2$iv$iv = 4;
                                                length3 = length2;
                                            } else {
                                                length = length13 + 1;
                                                chars[length13] = Utf8.REPLACEMENT_CHARACTER;
                                            }
                                        } else if (codePoint$iv$iv3 >= 65536) {
                                            int codePoint$iv = codePoint$iv$iv3;
                                            if (codePoint$iv != 65533) {
                                                int length24 = length13 + 1;
                                                chars[length13] = (char) ((codePoint$iv >>> 10) + Utf8.HIGH_SURROGATE_HEADER);
                                                length2 = length24 + 1;
                                                chars[length24] = (char) ((codePoint$iv & 1023) + Utf8.LOG_SURROGATE_HEADER);
                                                Unit unit122 = Unit.INSTANCE;
                                                b2$iv$iv = 4;
                                                length3 = length2;
                                            } else {
                                                length = length13 + 1;
                                                chars[length13] = Utf8.REPLACEMENT_CHARACTER;
                                            }
                                        } else if (65533 != 65533) {
                                            int length25 = length13 + 1;
                                            chars[length13] = (char) ((Utf8.REPLACEMENT_CODE_POINT >>> 10) + Utf8.HIGH_SURROGATE_HEADER);
                                            length2 = length25 + 1;
                                            chars[length25] = (char) ((65533 & 1023) + Utf8.LOG_SURROGATE_HEADER);
                                            Unit unit1222 = Unit.INSTANCE;
                                            b2$iv$iv = 4;
                                            length3 = length2;
                                        } else {
                                            length = length13 + 1;
                                            chars[length13] = Utf8.REPLACEMENT_CHARACTER;
                                        }
                                    } else if (65533 != 65533) {
                                        int length26 = length13 + 1;
                                        chars[length13] = (char) ((Utf8.REPLACEMENT_CODE_POINT >>> 10) + Utf8.HIGH_SURROGATE_HEADER);
                                        length2 = length26 + 1;
                                        chars[length26] = (char) ((65533 & 1023) + Utf8.LOG_SURROGATE_HEADER);
                                        Unit unit12222 = Unit.INSTANCE;
                                        b2$iv$iv = 4;
                                        length3 = length2;
                                    } else {
                                        length = length13 + 1;
                                        chars[length13] = Utf8.REPLACEMENT_CHARACTER;
                                    }
                                    length2 = length;
                                    Unit unit122222 = Unit.INSTANCE;
                                    b2$iv$iv = 4;
                                    length3 = length2;
                                }
                            }
                        }
                    }
                    index$iv += b2$iv$iv;
                    $i$f$processUtf16Chars = $i$f$processUtf16Chars2;
                } else {
                    chars[length13] = Utf8.REPLACEMENT_CHARACTER;
                    index$iv++;
                    length13++;
                    $i$f$processUtf16Chars = $i$f$processUtf16Chars2;
                }
            }
        }
        return StringsKt.concatToString(chars, 0, length13);
    }

    public static final byte[] commonAsUtf8ToByteArray(String $this$commonAsUtf8ToByteArray) {
        String str = $this$commonAsUtf8ToByteArray;
        Intrinsics.checkNotNullParameter(str, "<this>");
        byte[] bytes = new byte[($this$commonAsUtf8ToByteArray.length() * 4)];
        int length = $this$commonAsUtf8ToByteArray.length();
        if (length > 0) {
            int i = 0;
            do {
                int index = i;
                i++;
                char b0 = str.charAt(index);
                if (Intrinsics.compare((int) b0, 128) >= 0) {
                    int size = index;
                    int endIndex$iv = $this$commonAsUtf8ToByteArray.length();
                    String $this$processUtf8Bytes$iv = $this$commonAsUtf8ToByteArray;
                    int index$iv = index;
                    while (index$iv < endIndex$iv) {
                        byte c$iv = $this$processUtf8Bytes$iv.charAt(index$iv);
                        if (Intrinsics.compare((int) c$iv, 128) < 0) {
                            bytes[size] = (byte) c$iv;
                            index$iv++;
                            size++;
                            while (index$iv < endIndex$iv && Intrinsics.compare((int) $this$processUtf8Bytes$iv.charAt(index$iv), 128) < 0) {
                                bytes[size] = (byte) $this$processUtf8Bytes$iv.charAt(index$iv);
                                index$iv++;
                                size++;
                            }
                        } else if (Intrinsics.compare((int) c$iv, 2048) < 0) {
                            int size2 = size + 1;
                            bytes[size] = (byte) ((c$iv >> 6) | 192);
                            bytes[size2] = (byte) ((c$iv & Utf8.REPLACEMENT_BYTE) | ByteCompanionObject.MIN_VALUE);
                            index$iv++;
                            size = size2 + 1;
                        } else {
                            if (!(55296 <= c$iv && c$iv <= 57343)) {
                                int size3 = size + 1;
                                bytes[size] = (byte) ((c$iv >> 12) | 224);
                                int size4 = size3 + 1;
                                bytes[size3] = (byte) (((c$iv >> 6) & 63) | 128);
                                bytes[size4] = (byte) ((c$iv & Utf8.REPLACEMENT_BYTE) | ByteCompanionObject.MIN_VALUE);
                                index$iv++;
                                size = size4 + 1;
                            } else {
                                if (Intrinsics.compare((int) c$iv, 56319) <= 0 && endIndex$iv > index$iv + 1) {
                                    char charAt = $this$processUtf8Bytes$iv.charAt(index$iv + 1);
                                    if (56320 <= charAt && charAt <= 57343) {
                                        int codePoint$iv = ((c$iv << 10) + $this$processUtf8Bytes$iv.charAt(index$iv + 1)) - 56613888;
                                        int size5 = size + 1;
                                        bytes[size] = (byte) ((codePoint$iv >> 18) | 240);
                                        int size6 = size5 + 1;
                                        bytes[size5] = (byte) (((codePoint$iv >> 12) & 63) | 128);
                                        int size7 = size6 + 1;
                                        bytes[size6] = (byte) (((codePoint$iv >> 6) & 63) | 128);
                                        bytes[size7] = (byte) ((codePoint$iv & Utf8.REPLACEMENT_BYTE) | ByteCompanionObject.MIN_VALUE);
                                        index$iv += 2;
                                        size = size7 + 1;
                                    }
                                }
                                bytes[size] = Utf8.REPLACEMENT_BYTE;
                                index$iv++;
                                size++;
                            }
                        }
                    }
                    byte[] copyOf = Arrays.copyOf(bytes, size);
                    Intrinsics.checkNotNullExpressionValue(copyOf, "java.util.Arrays.copyOf(this, newSize)");
                    return copyOf;
                }
                bytes[index] = (byte) b0;
            } while (i < length);
        }
        byte[] copyOf2 = Arrays.copyOf(bytes, $this$commonAsUtf8ToByteArray.length());
        Intrinsics.checkNotNullExpressionValue(copyOf2, "java.util.Arrays.copyOf(this, newSize)");
        return copyOf2;
    }
}

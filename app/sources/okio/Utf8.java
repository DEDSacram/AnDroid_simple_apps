package okio;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000D\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u0005\n\u0000\n\u0002\u0010\f\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\u001a\u0011\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u0001H\b\u001a\u0011\u0010\u000e\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u0007H\b\u001a4\u0010\u0010\u001a\u00020\u0001*\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00012\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00160\u0015H\bø\u0001\u0000\u001a4\u0010\u0017\u001a\u00020\u0001*\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00012\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00160\u0015H\bø\u0001\u0000\u001a4\u0010\u0018\u001a\u00020\u0001*\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00012\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00160\u0015H\bø\u0001\u0000\u001a4\u0010\u0019\u001a\u00020\u0016*\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00012\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00160\u0015H\bø\u0001\u0000\u001a4\u0010\u001a\u001a\u00020\u0016*\u00020\u001b2\u0006\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00012\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00160\u0015H\bø\u0001\u0000\u001a4\u0010\u001c\u001a\u00020\u0016*\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00012\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00160\u0015H\bø\u0001\u0000\u001a%\u0010\u001d\u001a\u00020\u001e*\u00020\u001b2\b\b\u0002\u0010\u0012\u001a\u00020\u00012\b\b\u0002\u0010\u0013\u001a\u00020\u0001H\u0007¢\u0006\u0002\b\u001f\"\u000e\u0010\u0000\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000\"\u000e\u0010\u0005\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000\"\u000e\u0010\u0006\u001a\u00020\u0007XT¢\u0006\u0002\n\u0000\"\u000e\u0010\b\u001a\u00020\tXT¢\u0006\u0002\n\u0000\"\u000e\u0010\n\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000\u0002\u0007\n\u0005\b20\u0001¨\u0006 "}, d2 = {"HIGH_SURROGATE_HEADER", "", "LOG_SURROGATE_HEADER", "MASK_2BYTES", "MASK_3BYTES", "MASK_4BYTES", "REPLACEMENT_BYTE", "", "REPLACEMENT_CHARACTER", "", "REPLACEMENT_CODE_POINT", "isIsoControl", "", "codePoint", "isUtf8Continuation", "byte", "process2Utf8Bytes", "", "beginIndex", "endIndex", "yield", "Lkotlin/Function1;", "", "process3Utf8Bytes", "process4Utf8Bytes", "processUtf16Chars", "processUtf8Bytes", "", "processUtf8CodePoints", "utf8Size", "", "size", "okio"}, k = 2, mv = {1, 5, 1}, xi = 48)
/* compiled from: Utf8.kt */
public final class Utf8 {
    public static final int HIGH_SURROGATE_HEADER = 55232;
    public static final int LOG_SURROGATE_HEADER = 56320;
    public static final int MASK_2BYTES = 3968;
    public static final int MASK_3BYTES = -123008;
    public static final int MASK_4BYTES = 3678080;
    public static final byte REPLACEMENT_BYTE = 63;
    public static final char REPLACEMENT_CHARACTER = '�';
    public static final int REPLACEMENT_CODE_POINT = 65533;

    public static final long size(String str) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        return size$default(str, 0, 0, 3, (Object) null);
    }

    public static final long size(String str, int i) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        return size$default(str, i, 0, 2, (Object) null);
    }

    public static /* synthetic */ long size$default(String str, int i, int i2, int i3, Object obj) {
        if ((i3 & 1) != 0) {
            i = 0;
        }
        if ((i3 & 2) != 0) {
            i2 = str.length();
        }
        return size(str, i, i2);
    }

    public static final long size(String $this$utf8Size, int beginIndex, int endIndex) {
        Intrinsics.checkNotNullParameter($this$utf8Size, "<this>");
        boolean z = true;
        if (beginIndex >= 0) {
            if (endIndex >= beginIndex) {
                if (endIndex > $this$utf8Size.length()) {
                    z = false;
                }
                if (z) {
                    long result = 0;
                    int i = beginIndex;
                    while (i < endIndex) {
                        int c = $this$utf8Size.charAt(i);
                        if (c < 128) {
                            result++;
                            i++;
                        } else if (c < 2048) {
                            result += (long) 2;
                            i++;
                        } else if (c < 55296 || c > 57343) {
                            result += (long) 3;
                            i++;
                        } else {
                            int low = i + 1 < endIndex ? $this$utf8Size.charAt(i + 1) : 0;
                            if (c > 56319 || low < 56320 || low > 57343) {
                                result++;
                                i++;
                            } else {
                                result += (long) 4;
                                i += 2;
                            }
                        }
                    }
                    return result;
                }
                throw new IllegalArgumentException(("endIndex > string.length: " + endIndex + " > " + $this$utf8Size.length()).toString());
            }
            throw new IllegalArgumentException(("endIndex < beginIndex: " + endIndex + " < " + beginIndex).toString());
        }
        throw new IllegalArgumentException(Intrinsics.stringPlus("beginIndex < 0: ", Integer.valueOf(beginIndex)).toString());
    }

    public static final boolean isIsoControl(int codePoint) {
        if (codePoint >= 0 && codePoint <= 31) {
            return true;
        }
        return 127 <= codePoint && codePoint <= 159;
    }

    public static final boolean isUtf8Continuation(byte $this$and$iv) {
        return (192 & $this$and$iv) == 128;
    }

    public static final void processUtf8Bytes(String $this$processUtf8Bytes, int beginIndex, int endIndex, Function1<? super Byte, Unit> yield) {
        Intrinsics.checkNotNullParameter($this$processUtf8Bytes, "<this>");
        Intrinsics.checkNotNullParameter(yield, "yield");
        int index = beginIndex;
        while (index < endIndex) {
            char c = $this$processUtf8Bytes.charAt(index);
            if (Intrinsics.compare((int) c, 128) < 0) {
                yield.invoke(Byte.valueOf((byte) c));
                index++;
                while (index < endIndex && Intrinsics.compare((int) $this$processUtf8Bytes.charAt(index), 128) < 0) {
                    yield.invoke(Byte.valueOf((byte) $this$processUtf8Bytes.charAt(index)));
                    index++;
                }
            } else if (Intrinsics.compare((int) c, 2048) < 0) {
                yield.invoke(Byte.valueOf((byte) ((c >> 6) | 192)));
                yield.invoke(Byte.valueOf((byte) (128 | (c & '?'))));
                index++;
            } else {
                boolean z = false;
                if (!(55296 <= c && c <= 57343)) {
                    yield.invoke(Byte.valueOf((byte) ((c >> 12) | 224)));
                    yield.invoke(Byte.valueOf((byte) (((c >> 6) & 63) | 128)));
                    yield.invoke(Byte.valueOf((byte) (128 | (c & '?'))));
                    index++;
                } else {
                    if (Intrinsics.compare((int) c, 56319) <= 0 && endIndex > index + 1) {
                        char charAt = $this$processUtf8Bytes.charAt(index + 1);
                        if (56320 <= charAt && charAt <= 57343) {
                            z = true;
                        }
                        if (z) {
                            int codePoint = ((c << 10) + $this$processUtf8Bytes.charAt(index + 1)) - 56613888;
                            yield.invoke(Byte.valueOf((byte) ((codePoint >> 18) | 240)));
                            yield.invoke(Byte.valueOf((byte) (((codePoint >> 12) & 63) | 128)));
                            yield.invoke(Byte.valueOf((byte) (((codePoint >> 6) & 63) | 128)));
                            yield.invoke(Byte.valueOf((byte) (128 | (codePoint & 63))));
                            index += 2;
                        }
                    }
                    yield.invoke(Byte.valueOf(REPLACEMENT_BYTE));
                    index++;
                }
            }
        }
    }

    public static final void processUtf8CodePoints(byte[] $this$processUtf8CodePoints, int beginIndex, int endIndex, Function1<? super Integer, Unit> yield) {
        byte b0$iv;
        int it;
        byte b2$iv;
        int it2;
        int i;
        int it3;
        byte[] bArr = $this$processUtf8CodePoints;
        int i2 = endIndex;
        Function1<? super Integer, Unit> function1 = yield;
        Intrinsics.checkNotNullParameter(bArr, "<this>");
        Intrinsics.checkNotNullParameter(function1, "yield");
        int index = beginIndex;
        while (index < i2) {
            byte b0 = bArr[index];
            if (b0 >= 0) {
                function1.invoke(Integer.valueOf(b0));
                index++;
                while (index < i2 && bArr[index] >= 0) {
                    function1.invoke(Integer.valueOf(bArr[index]));
                    index++;
                }
            } else if ((b0 >> 5) == -2) {
                byte[] $this$process2Utf8Bytes$iv = $this$processUtf8CodePoints;
                if (i2 <= index + 1) {
                    function1.invoke(Integer.valueOf(REPLACEMENT_CODE_POINT));
                    Unit unit = Unit.INSTANCE;
                    i = 1;
                } else {
                    byte b0$iv2 = $this$process2Utf8Bytes$iv[index];
                    byte b1$iv = $this$process2Utf8Bytes$iv[index + 1];
                    if (!((b1$iv & 192) == 128)) {
                        function1.invoke(Integer.valueOf(REPLACEMENT_CODE_POINT));
                        Unit unit2 = Unit.INSTANCE;
                        i = 1;
                    } else {
                        int codePoint$iv = (b1$iv ^ MASK_2BYTES) ^ (b0$iv2 << 6);
                        if (codePoint$iv < 128) {
                            it3 = REPLACEMENT_CODE_POINT;
                        } else {
                            it3 = codePoint$iv;
                        }
                        function1.invoke(Integer.valueOf(it3));
                        Unit unit3 = Unit.INSTANCE;
                        i = 2;
                    }
                }
                index += i;
            } else if ((b0 >> 4) == -2) {
                byte[] $this$process3Utf8Bytes$iv = $this$processUtf8CodePoints;
                if (i2 <= index + 2) {
                    function1.invoke(Integer.valueOf(REPLACEMENT_CODE_POINT));
                    Unit unit4 = Unit.INSTANCE;
                    if (i2 > index + 1) {
                        if ((192 & $this$process3Utf8Bytes$iv[index + 1]) == 128) {
                            b2$iv = 2;
                        }
                    }
                    b2$iv = 1;
                } else {
                    byte b0$iv3 = $this$process3Utf8Bytes$iv[index];
                    byte b1$iv2 = $this$process3Utf8Bytes$iv[index + 1];
                    if (!((b1$iv2 & 192) == 128)) {
                        function1.invoke(Integer.valueOf(REPLACEMENT_CODE_POINT));
                        Unit unit5 = Unit.INSTANCE;
                        b2$iv = 1;
                    } else {
                        byte b2$iv2 = $this$process3Utf8Bytes$iv[index + 2];
                        if (!((b2$iv2 & 192) == 128)) {
                            function1.invoke(Integer.valueOf(REPLACEMENT_CODE_POINT));
                            Unit unit6 = Unit.INSTANCE;
                            b2$iv = 2;
                        } else {
                            int codePoint$iv2 = ((-123008 ^ b2$iv2) ^ (b1$iv2 << 6)) ^ (b0$iv3 << 12);
                            if (codePoint$iv2 < 2048) {
                                it2 = REPLACEMENT_CODE_POINT;
                            } else {
                                if (55296 <= codePoint$iv2 && codePoint$iv2 <= 57343) {
                                    it2 = REPLACEMENT_CODE_POINT;
                                } else {
                                    it2 = codePoint$iv2;
                                }
                            }
                            function1.invoke(Integer.valueOf(it2));
                            Unit unit7 = Unit.INSTANCE;
                            b2$iv = 3;
                        }
                    }
                }
                index += b2$iv;
            } else if ((b0 >> 3) == -2) {
                byte[] $this$process4Utf8Bytes$iv = $this$processUtf8CodePoints;
                if (i2 <= index + 3) {
                    function1.invoke(Integer.valueOf(REPLACEMENT_CODE_POINT));
                    Unit unit8 = Unit.INSTANCE;
                    if (i2 > index + 1) {
                        if (((192 & $this$process4Utf8Bytes$iv[index + 1]) == 128 ? (byte) 1 : 0) != 0) {
                            if (i2 > index + 2) {
                                if (((192 & $this$process4Utf8Bytes$iv[index + 2]) == 128 ? 1 : 0) != 0) {
                                    b0$iv = 3;
                                }
                            }
                            b0$iv = 2;
                        }
                    }
                    b0$iv = 1;
                } else {
                    byte b0$iv4 = $this$process4Utf8Bytes$iv[index];
                    byte b1$iv3 = $this$process4Utf8Bytes$iv[index + 1];
                    if (!((b1$iv3 & 192) == 128)) {
                        function1.invoke(Integer.valueOf(REPLACEMENT_CODE_POINT));
                        Unit unit9 = Unit.INSTANCE;
                        b0$iv = 1;
                    } else {
                        byte b2$iv3 = $this$process4Utf8Bytes$iv[index + 2];
                        if (!((b2$iv3 & 192) == 128)) {
                            function1.invoke(Integer.valueOf(REPLACEMENT_CODE_POINT));
                            Unit unit10 = Unit.INSTANCE;
                            b0$iv = 2;
                        } else {
                            byte b3$iv = $this$process4Utf8Bytes$iv[index + 3];
                            if (!((b3$iv & 192) == 128)) {
                                function1.invoke(Integer.valueOf(REPLACEMENT_CODE_POINT));
                                Unit unit11 = Unit.INSTANCE;
                                b0$iv = 3;
                            } else {
                                int codePoint$iv3 = (((3678080 ^ b3$iv) ^ (b2$iv3 << 6)) ^ (b1$iv3 << 12)) ^ (b0$iv4 << 18);
                                if (codePoint$iv3 > 1114111) {
                                    it = REPLACEMENT_CODE_POINT;
                                } else {
                                    if (55296 <= codePoint$iv3 && codePoint$iv3 <= 57343) {
                                        it = REPLACEMENT_CODE_POINT;
                                    } else if (codePoint$iv3 < 65536) {
                                        it = REPLACEMENT_CODE_POINT;
                                    } else {
                                        it = codePoint$iv3;
                                    }
                                }
                                function1.invoke(Integer.valueOf(it));
                                Unit unit12 = Unit.INSTANCE;
                                b0$iv = 4;
                            }
                        }
                    }
                }
                index += b0$iv;
            } else {
                function1.invoke(Integer.valueOf(REPLACEMENT_CODE_POINT));
                index++;
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:122:0x02ad, code lost:
        if (65533(0xfffd, float:9.1831E-41) != 65533(0xfffd, float:9.1831E-41)) goto L_0x02af;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:124:0x02c7, code lost:
        r2.invoke(java.lang.Character.valueOf(REPLACEMENT_CHARACTER));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x02e4, code lost:
        if (65533(0xfffd, float:9.1831E-41) != 65533(0xfffd, float:9.1831E-41)) goto L_0x02af;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:137:0x02ef, code lost:
        if (65533(0xfffd, float:9.1831E-41) != 65533(0xfffd, float:9.1831E-41)) goto L_0x02af;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:139:0x02f4, code lost:
        if (r12 != 65533) goto L_0x02af;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final void processUtf16Chars(byte[] r24, int r25, int r26, kotlin.jvm.functions.Function1<? super java.lang.Character, kotlin.Unit> r27) {
        /*
            r0 = r24
            r1 = r26
            r2 = r27
            java.lang.String r3 = "<this>"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r0, r3)
            java.lang.String r3 = "yield"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r2, r3)
            r3 = 0
            r4 = r25
        L_0x0013:
            if (r4 >= r1) goto L_0x0306
            byte r5 = r0[r4]
            if (r5 < 0) goto L_0x0038
            char r6 = (char) r5
            java.lang.Character r6 = java.lang.Character.valueOf(r6)
            r2.invoke(r6)
            int r4 = r4 + 1
        L_0x0024:
            if (r4 >= r1) goto L_0x0013
            byte r6 = r0[r4]
            if (r6 < 0) goto L_0x0013
            int r6 = r4 + 1
            byte r4 = r0[r4]
            char r4 = (char) r4
            java.lang.Character r4 = java.lang.Character.valueOf(r4)
            r2.invoke(r4)
            r4 = r6
            goto L_0x0024
        L_0x0038:
            r6 = 5
            r7 = r5
            r8 = 0
            int r6 = r7 >> r6
            r7 = -2
            r8 = 128(0x80, float:1.794E-43)
            if (r6 != r7) goto L_0x009f
            r6 = r24
            r7 = 0
            int r12 = r4 + 1
            if (r1 > r12) goto L_0x0059
            r8 = 65533(0xfffd, float:9.1831E-41)
            r9 = 0
            char r10 = (char) r8
            java.lang.Character r10 = java.lang.Character.valueOf(r10)
            r2.invoke(r10)
            kotlin.Unit r8 = kotlin.Unit.INSTANCE
            r9 = 1
            goto L_0x009c
        L_0x0059:
            byte r12 = r6[r4]
            int r13 = r4 + 1
            byte r13 = r6[r13]
            r14 = 0
            r15 = 192(0xc0, float:2.69E-43)
            r16 = r13
            r17 = 0
            r15 = r16 & r15
            if (r15 != r8) goto L_0x006c
            r10 = 1
            goto L_0x006d
        L_0x006c:
            r10 = 0
        L_0x006d:
            if (r10 != 0) goto L_0x007f
            r8 = 65533(0xfffd, float:9.1831E-41)
            r9 = 0
            char r10 = (char) r8
            java.lang.Character r10 = java.lang.Character.valueOf(r10)
            r2.invoke(r10)
            kotlin.Unit r8 = kotlin.Unit.INSTANCE
            r9 = 1
            goto L_0x009c
        L_0x007f:
            r10 = r13 ^ 3968(0xf80, float:5.56E-42)
            int r11 = r12 << 6
            r10 = r10 ^ r11
            if (r10 >= r8) goto L_0x008f
            r8 = 65533(0xfffd, float:9.1831E-41)
            r11 = 0
            goto L_0x0091
        L_0x008f:
            r8 = r10
            r11 = 0
        L_0x0091:
            char r14 = (char) r8
            java.lang.Character r14 = java.lang.Character.valueOf(r14)
            r2.invoke(r14)
            kotlin.Unit r8 = kotlin.Unit.INSTANCE
            r9 = 2
        L_0x009c:
            int r4 = r4 + r9
            goto L_0x0013
        L_0x009f:
            r6 = 4
            r12 = r5
            r13 = 0
            int r6 = r12 >> r6
            r12 = 57343(0xdfff, float:8.0355E-41)
            r13 = 55296(0xd800, float:7.7486E-41)
            if (r6 != r7) goto L_0x015f
            r6 = r24
            r7 = 0
            int r15 = r4 + 2
            if (r1 > r15) goto L_0x00de
            r12 = 65533(0xfffd, float:9.1831E-41)
            r13 = 0
            char r14 = (char) r12
            java.lang.Character r14 = java.lang.Character.valueOf(r14)
            r2.invoke(r14)
            kotlin.Unit r12 = kotlin.Unit.INSTANCE
            int r12 = r4 + 1
            if (r1 <= r12) goto L_0x00db
            int r12 = r4 + 1
            byte r12 = r6[r12]
            r13 = 0
            r14 = 192(0xc0, float:2.69E-43)
            r15 = r12
            r16 = 0
            r14 = r14 & r15
            if (r14 != r8) goto L_0x00d4
            r10 = 1
            goto L_0x00d5
        L_0x00d4:
            r10 = 0
        L_0x00d5:
            if (r10 != 0) goto L_0x00d8
            goto L_0x00db
        L_0x00d8:
            r9 = 2
            goto L_0x015c
        L_0x00db:
            r9 = 1
            goto L_0x015c
        L_0x00de:
            byte r15 = r6[r4]
            int r16 = r4 + 1
            byte r16 = r6[r16]
            r17 = 0
            r18 = 192(0xc0, float:2.69E-43)
            r19 = r16
            r20 = 0
            r9 = r19 & r18
            if (r9 != r8) goto L_0x00f2
            r9 = 1
            goto L_0x00f3
        L_0x00f2:
            r9 = 0
        L_0x00f3:
            if (r9 != 0) goto L_0x0105
            r8 = 65533(0xfffd, float:9.1831E-41)
            r9 = 0
            char r10 = (char) r8
            java.lang.Character r10 = java.lang.Character.valueOf(r10)
            r2.invoke(r10)
            kotlin.Unit r8 = kotlin.Unit.INSTANCE
            r9 = 1
            goto L_0x015c
        L_0x0105:
            int r9 = r4 + 2
            byte r9 = r6[r9]
            r17 = 0
            r18 = 192(0xc0, float:2.69E-43)
            r19 = r9
            r20 = 0
            r10 = r19 & r18
            if (r10 != r8) goto L_0x0117
            r8 = 1
            goto L_0x0118
        L_0x0117:
            r8 = 0
        L_0x0118:
            if (r8 != 0) goto L_0x012a
            r8 = 65533(0xfffd, float:9.1831E-41)
            r10 = 0
            char r11 = (char) r8
            java.lang.Character r11 = java.lang.Character.valueOf(r11)
            r2.invoke(r11)
            kotlin.Unit r8 = kotlin.Unit.INSTANCE
            r9 = 2
            goto L_0x015c
        L_0x012a:
            r8 = -123008(0xfffffffffffe1f80, float:NaN)
            r8 = r8 ^ r9
            int r10 = r16 << 6
            r8 = r8 ^ r10
            int r10 = r15 << 12
            r8 = r8 ^ r10
            r10 = 2048(0x800, float:2.87E-42)
            if (r8 >= r10) goto L_0x014a
            r10 = 65533(0xfffd, float:9.1831E-41)
            r11 = 0
        L_0x013f:
            char r12 = (char) r10
            java.lang.Character r12 = java.lang.Character.valueOf(r12)
            r2.invoke(r12)
            kotlin.Unit r10 = kotlin.Unit.INSTANCE
            goto L_0x015b
        L_0x014a:
            if (r13 > r8) goto L_0x0150
            if (r8 > r12) goto L_0x0150
            r10 = 1
            goto L_0x0151
        L_0x0150:
            r10 = 0
        L_0x0151:
            if (r10 == 0) goto L_0x0158
            r10 = 65533(0xfffd, float:9.1831E-41)
            r11 = 0
            goto L_0x013f
        L_0x0158:
            r10 = r8
            r11 = 0
            goto L_0x013f
        L_0x015b:
            r9 = 3
        L_0x015c:
            int r4 = r4 + r9
            goto L_0x0013
        L_0x015f:
            r6 = 3
            r9 = r5
            r10 = 0
            int r6 = r9 >> r6
            r9 = 65533(0xfffd, float:9.1831E-41)
            if (r6 != r7) goto L_0x02fb
            r6 = r24
            r7 = 0
            int r10 = r4 + 3
            r15 = 56320(0xdc00, float:7.8921E-41)
            r16 = 55232(0xd7c0, float:7.7397E-41)
            if (r1 > r10) goto L_0x01d3
            r10 = 65533(0xfffd, float:9.1831E-41)
            r12 = 0
            if (r10 == r9) goto L_0x0194
            int r9 = r10 >>> 10
            int r9 = r9 + r16
            char r9 = (char) r9
            java.lang.Character r9 = java.lang.Character.valueOf(r9)
            r2.invoke(r9)
            r9 = r10 & 1023(0x3ff, float:1.434E-42)
            int r9 = r9 + r15
            char r9 = (char) r9
            java.lang.Character r9 = java.lang.Character.valueOf(r9)
            r2.invoke(r9)
            goto L_0x019b
        L_0x0194:
            java.lang.Character r9 = java.lang.Character.valueOf(r9)
            r2.invoke(r9)
        L_0x019b:
            kotlin.Unit r9 = kotlin.Unit.INSTANCE
            int r9 = r4 + 1
            if (r1 <= r9) goto L_0x01d0
            int r9 = r4 + 1
            byte r9 = r6[r9]
            r10 = 0
            r12 = 192(0xc0, float:2.69E-43)
            r13 = r9
            r15 = 0
            r12 = r12 & r13
            if (r12 != r8) goto L_0x01b0
            r9 = 1
            goto L_0x01b1
        L_0x01b0:
            r9 = 0
        L_0x01b1:
            if (r9 != 0) goto L_0x01b4
            goto L_0x01d0
        L_0x01b4:
            int r9 = r4 + 2
            if (r1 <= r9) goto L_0x01cd
            int r9 = r4 + 2
            byte r9 = r6[r9]
            r10 = 0
            r12 = 192(0xc0, float:2.69E-43)
            r13 = r9
            r15 = 0
            r12 = r12 & r13
            if (r12 != r8) goto L_0x01c6
            r10 = 1
            goto L_0x01c7
        L_0x01c6:
            r10 = 0
        L_0x01c7:
            if (r10 != 0) goto L_0x01ca
            goto L_0x01cd
        L_0x01ca:
            r9 = 3
            goto L_0x02f8
        L_0x01cd:
            r9 = 2
            goto L_0x02f8
        L_0x01d0:
            r9 = 1
            goto L_0x02f8
        L_0x01d3:
            byte r10 = r6[r4]
            int r17 = r4 + 1
            byte r17 = r6[r17]
            r18 = 0
            r19 = 192(0xc0, float:2.69E-43)
            r20 = r17
            r23 = 0
            r11 = r20 & r19
            if (r11 != r8) goto L_0x01e7
            r11 = 1
            goto L_0x01e8
        L_0x01e7:
            r11 = 0
        L_0x01e8:
            if (r11 != 0) goto L_0x0215
            r8 = 65533(0xfffd, float:9.1831E-41)
            r11 = 0
            if (r8 == r9) goto L_0x0208
            int r9 = r8 >>> 10
            int r9 = r9 + r16
            char r9 = (char) r9
            java.lang.Character r9 = java.lang.Character.valueOf(r9)
            r2.invoke(r9)
            r9 = r8 & 1023(0x3ff, float:1.434E-42)
            int r9 = r9 + r15
            char r9 = (char) r9
            java.lang.Character r9 = java.lang.Character.valueOf(r9)
            r2.invoke(r9)
            goto L_0x020f
        L_0x0208:
            java.lang.Character r9 = java.lang.Character.valueOf(r9)
            r2.invoke(r9)
        L_0x020f:
            kotlin.Unit r8 = kotlin.Unit.INSTANCE
            r9 = 1
            goto L_0x02f8
        L_0x0215:
            int r11 = r4 + 2
            byte r11 = r6[r11]
            r18 = 0
            r19 = 192(0xc0, float:2.69E-43)
            r20 = r11
            r23 = 0
            r14 = r20 & r19
            if (r14 != r8) goto L_0x0227
            r14 = 1
            goto L_0x0228
        L_0x0227:
            r14 = 0
        L_0x0228:
            if (r14 != 0) goto L_0x0255
            r8 = 65533(0xfffd, float:9.1831E-41)
            r12 = 0
            if (r8 == r9) goto L_0x0248
            int r9 = r8 >>> 10
            int r9 = r9 + r16
            char r9 = (char) r9
            java.lang.Character r9 = java.lang.Character.valueOf(r9)
            r2.invoke(r9)
            r9 = r8 & 1023(0x3ff, float:1.434E-42)
            int r9 = r9 + r15
            char r9 = (char) r9
            java.lang.Character r9 = java.lang.Character.valueOf(r9)
            r2.invoke(r9)
            goto L_0x024f
        L_0x0248:
            java.lang.Character r9 = java.lang.Character.valueOf(r9)
            r2.invoke(r9)
        L_0x024f:
            kotlin.Unit r8 = kotlin.Unit.INSTANCE
            r9 = 2
            goto L_0x02f8
        L_0x0255:
            int r14 = r4 + 3
            byte r14 = r6[r14]
            r18 = 0
            r19 = 192(0xc0, float:2.69E-43)
            r20 = r14
            r21 = 0
            r12 = r20 & r19
            if (r12 != r8) goto L_0x0267
            r8 = 1
            goto L_0x0268
        L_0x0267:
            r8 = 0
        L_0x0268:
            if (r8 != 0) goto L_0x0294
            r8 = 65533(0xfffd, float:9.1831E-41)
            r12 = 0
            if (r8 == r9) goto L_0x0288
            int r9 = r8 >>> 10
            int r9 = r9 + r16
            char r9 = (char) r9
            java.lang.Character r9 = java.lang.Character.valueOf(r9)
            r2.invoke(r9)
            r9 = r8 & 1023(0x3ff, float:1.434E-42)
            int r9 = r9 + r15
            char r9 = (char) r9
            java.lang.Character r9 = java.lang.Character.valueOf(r9)
            r2.invoke(r9)
            goto L_0x028f
        L_0x0288:
            java.lang.Character r9 = java.lang.Character.valueOf(r9)
            r2.invoke(r9)
        L_0x028f:
            kotlin.Unit r8 = kotlin.Unit.INSTANCE
            r9 = 3
            goto L_0x02f8
        L_0x0294:
            r8 = 3678080(0x381f80, float:5.154088E-39)
            r8 = r8 ^ r14
            int r12 = r11 << 6
            r8 = r8 ^ r12
            int r12 = r17 << 12
            r8 = r8 ^ r12
            int r12 = r10 << 18
            r8 = r8 ^ r12
            r12 = 1114111(0x10ffff, float:1.561202E-39)
            if (r8 <= r12) goto L_0x02d2
            r12 = 65533(0xfffd, float:9.1831E-41)
            r13 = 0
            if (r12 == r9) goto L_0x02c7
        L_0x02af:
            int r9 = r12 >>> 10
            int r9 = r9 + r16
            char r9 = (char) r9
            java.lang.Character r9 = java.lang.Character.valueOf(r9)
            r2.invoke(r9)
            r9 = r12 & 1023(0x3ff, float:1.434E-42)
            int r9 = r9 + r15
            char r9 = (char) r9
            java.lang.Character r9 = java.lang.Character.valueOf(r9)
            r2.invoke(r9)
            goto L_0x02ce
        L_0x02c7:
            java.lang.Character r9 = java.lang.Character.valueOf(r9)
            r2.invoke(r9)
        L_0x02ce:
            kotlin.Unit r9 = kotlin.Unit.INSTANCE
            goto L_0x02f7
        L_0x02d2:
            if (r13 > r8) goto L_0x02dc
            r12 = 57343(0xdfff, float:8.0355E-41)
            if (r8 > r12) goto L_0x02dc
            r22 = 1
            goto L_0x02de
        L_0x02dc:
            r22 = 0
        L_0x02de:
            if (r22 == 0) goto L_0x02e7
            r12 = 65533(0xfffd, float:9.1831E-41)
            r13 = 0
            if (r12 == r9) goto L_0x02c7
            goto L_0x02af
        L_0x02e7:
            r12 = 65536(0x10000, float:9.18355E-41)
            if (r8 >= r12) goto L_0x02f2
            r12 = 65533(0xfffd, float:9.1831E-41)
            r13 = 0
            if (r12 == r9) goto L_0x02c7
            goto L_0x02af
        L_0x02f2:
            r12 = r8
            r13 = 0
            if (r12 == r9) goto L_0x02c7
            goto L_0x02af
        L_0x02f7:
            r9 = 4
        L_0x02f8:
            int r4 = r4 + r9
            goto L_0x0013
        L_0x02fb:
            java.lang.Character r6 = java.lang.Character.valueOf(r9)
            r2.invoke(r6)
            int r4 = r4 + 1
            goto L_0x0013
        L_0x0306:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Utf8.processUtf16Chars(byte[], int, int, kotlin.jvm.functions.Function1):void");
    }

    public static final int process2Utf8Bytes(byte[] $this$process2Utf8Bytes, int beginIndex, int endIndex, Function1<? super Integer, Unit> yield) {
        Intrinsics.checkNotNullParameter($this$process2Utf8Bytes, "<this>");
        Intrinsics.checkNotNullParameter(yield, "yield");
        int i = beginIndex + 1;
        Integer valueOf = Integer.valueOf(REPLACEMENT_CODE_POINT);
        if (endIndex <= i) {
            yield.invoke(valueOf);
            return 1;
        }
        byte b0 = $this$process2Utf8Bytes[beginIndex];
        byte b1 = $this$process2Utf8Bytes[beginIndex + 1];
        if (!((192 & b1) == 128)) {
            yield.invoke(valueOf);
            return 1;
        }
        int codePoint = (b1 ^ MASK_2BYTES) ^ (b0 << 6);
        if (codePoint < 128) {
            yield.invoke(valueOf);
            return 2;
        }
        yield.invoke(Integer.valueOf(codePoint));
        return 2;
    }

    public static final int process3Utf8Bytes(byte[] $this$process3Utf8Bytes, int beginIndex, int endIndex, Function1<? super Integer, Unit> yield) {
        byte[] bArr = $this$process3Utf8Bytes;
        int i = endIndex;
        Function1<? super Integer, Unit> function1 = yield;
        Intrinsics.checkNotNullParameter(bArr, "<this>");
        Intrinsics.checkNotNullParameter(function1, "yield");
        int i2 = beginIndex + 2;
        boolean z = false;
        Integer valueOf = Integer.valueOf(REPLACEMENT_CODE_POINT);
        if (i <= i2) {
            function1.invoke(valueOf);
            if (i > beginIndex + 1) {
                if ((192 & bArr[beginIndex + 1]) == 128) {
                    z = true;
                }
                return !z ? 1 : 2;
            }
        }
        byte b0 = bArr[beginIndex];
        byte b1 = bArr[beginIndex + 1];
        if (((192 & b1) == 128 ? 1 : 0) == 0) {
            function1.invoke(valueOf);
            return 1;
        }
        byte b2 = bArr[beginIndex + 2];
        if (!((192 & b2) == 128)) {
            function1.invoke(valueOf);
            return 2;
        }
        int codePoint = ((-123008 ^ b2) ^ (b1 << 6)) ^ (b0 << 12);
        if (codePoint < 2048) {
            function1.invoke(valueOf);
            return 3;
        }
        if (55296 <= codePoint && codePoint <= 57343) {
            z = true;
        }
        if (z) {
            function1.invoke(valueOf);
            return 3;
        }
        function1.invoke(Integer.valueOf(codePoint));
        return 3;
    }

    public static final int process4Utf8Bytes(byte[] $this$process4Utf8Bytes, int beginIndex, int endIndex, Function1<? super Integer, Unit> yield) {
        byte[] bArr = $this$process4Utf8Bytes;
        int i = endIndex;
        Function1<? super Integer, Unit> function1 = yield;
        Intrinsics.checkNotNullParameter(bArr, "<this>");
        Intrinsics.checkNotNullParameter(function1, "yield");
        int i2 = beginIndex + 3;
        boolean z = false;
        Integer valueOf = Integer.valueOf(REPLACEMENT_CODE_POINT);
        if (i <= i2) {
            function1.invoke(valueOf);
            if (i > beginIndex + 1) {
                if (((192 & bArr[beginIndex + 1]) == 128 ? (byte) 1 : 0) != 0) {
                    if (i > beginIndex + 2) {
                        if ((192 & bArr[beginIndex + 2]) == 128) {
                            z = true;
                        }
                        return !z ? 2 : 3;
                    }
                }
            }
            return 1;
        }
        byte b0 = bArr[beginIndex];
        byte b1 = bArr[beginIndex + 1];
        if (((192 & b1) == 128 ? 1 : 0) == 0) {
            function1.invoke(valueOf);
            return 1;
        }
        byte b2 = bArr[beginIndex + 2];
        if (((192 & b2) == 128 ? 1 : 0) == 0) {
            function1.invoke(valueOf);
            return 2;
        }
        byte b3 = bArr[beginIndex + 3];
        if (!((192 & b3) == 128)) {
            function1.invoke(valueOf);
            return 3;
        }
        int codePoint = (((3678080 ^ b3) ^ (b2 << 6)) ^ (b1 << 12)) ^ (b0 << 18);
        if (codePoint > 1114111) {
            function1.invoke(valueOf);
            return 4;
        }
        if (55296 <= codePoint && codePoint <= 57343) {
            z = true;
        }
        if (z) {
            function1.invoke(valueOf);
            return 4;
        } else if (codePoint < 65536) {
            function1.invoke(valueOf);
            return 4;
        } else {
            function1.invoke(Integer.valueOf(codePoint));
            return 4;
        }
    }
}

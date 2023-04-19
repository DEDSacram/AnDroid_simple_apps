package okio;

import kotlin.Metadata;
import kotlin.UByte;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0012\n\u0002\b\b\n\u0002\u0010\u000e\n\u0002\b\u0003\u001a\u000e\u0010\t\u001a\u0004\u0018\u00010\u0001*\u00020\nH\u0000\u001a\u0016\u0010\u000b\u001a\u00020\n*\u00020\u00012\b\b\u0002\u0010\f\u001a\u00020\u0001H\u0000\"\u001c\u0010\u0000\u001a\u00020\u00018\u0000X\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0002\u0010\u0003\u001a\u0004\b\u0004\u0010\u0005\"\u001c\u0010\u0006\u001a\u00020\u00018\u0000X\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0007\u0010\u0003\u001a\u0004\b\b\u0010\u0005¨\u0006\r"}, d2 = {"BASE64", "", "getBASE64$annotations", "()V", "getBASE64", "()[B", "BASE64_URL_SAFE", "getBASE64_URL_SAFE$annotations", "getBASE64_URL_SAFE", "decodeBase64ToArray", "", "encodeBase64", "map", "okio"}, k = 2, mv = {1, 5, 1}, xi = 48)
/* compiled from: -Base64.kt */
public final class _Base64Kt {
    private static final byte[] BASE64 = ByteString.Companion.encodeUtf8("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/").getData$okio();
    private static final byte[] BASE64_URL_SAFE = ByteString.Companion.encodeUtf8("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_").getData$okio();

    public static /* synthetic */ void getBASE64$annotations() {
    }

    public static /* synthetic */ void getBASE64_URL_SAFE$annotations() {
    }

    public static final byte[] getBASE64() {
        return BASE64;
    }

    public static final byte[] getBASE64_URL_SAFE() {
        return BASE64_URL_SAFE;
    }

    /* JADX WARNING: Removed duplicated region for block: B:55:0x00b6 A[LOOP:1: B:13:0x003c->B:55:0x00b6, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00b9 A[EDGE_INSN: B:74:0x00b9->B:56:0x00b9 ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final byte[] decodeBase64ToArray(java.lang.String r17) {
        /*
            r0 = r17
            java.lang.String r1 = "<this>"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r0, r1)
            int r1 = r17.length()
        L_0x000b:
            r2 = 9
            r3 = 32
            r4 = 13
            r5 = 10
            if (r1 <= 0) goto L_0x002b
            int r6 = r1 + -1
            char r6 = r0.charAt(r6)
            r7 = 61
            if (r6 == r7) goto L_0x0028
            if (r6 == r5) goto L_0x0028
            if (r6 == r4) goto L_0x0028
            if (r6 == r3) goto L_0x0028
            if (r6 == r2) goto L_0x0028
            goto L_0x002b
        L_0x0028:
            int r1 = r1 + -1
            goto L_0x000b
        L_0x002b:
            long r6 = (long) r1
            r8 = 6
            long r6 = r6 * r8
            r8 = 8
            long r6 = r6 / r8
            int r6 = (int) r6
            byte[] r6 = new byte[r6]
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            if (r1 <= 0) goto L_0x00b9
            r12 = 0
        L_0x003c:
            r13 = r12
            r14 = 1
            int r12 = r12 + r14
            char r15 = r0.charAt(r13)
            r16 = 0
            r11 = 65
            if (r11 > r15) goto L_0x004f
            r11 = 90
            if (r15 > r11) goto L_0x004f
            r11 = r14
            goto L_0x0050
        L_0x004f:
            r11 = 0
        L_0x0050:
            if (r11 == 0) goto L_0x0055
            int r11 = r15 + -65
            goto L_0x0096
        L_0x0055:
            r11 = 97
            if (r11 > r15) goto L_0x005f
            r11 = 122(0x7a, float:1.71E-43)
            if (r15 > r11) goto L_0x005f
            r11 = r14
            goto L_0x0060
        L_0x005f:
            r11 = 0
        L_0x0060:
            if (r11 == 0) goto L_0x0065
            int r11 = r15 + -71
            goto L_0x0096
        L_0x0065:
            r11 = 48
            if (r11 > r15) goto L_0x006e
            r11 = 57
            if (r15 > r11) goto L_0x006e
            goto L_0x006f
        L_0x006e:
            r14 = 0
        L_0x006f:
            if (r14 == 0) goto L_0x0074
            int r11 = r15 + 4
            goto L_0x0096
        L_0x0074:
            r11 = 43
            if (r15 == r11) goto L_0x0094
            r11 = 45
            if (r15 != r11) goto L_0x007d
            goto L_0x0094
        L_0x007d:
            r11 = 47
            if (r15 == r11) goto L_0x0091
            r11 = 95
            if (r15 != r11) goto L_0x0086
            goto L_0x0091
        L_0x0086:
            if (r15 == r5) goto L_0x0090
            if (r15 == r4) goto L_0x0090
            if (r15 == r3) goto L_0x0090
            if (r15 != r2) goto L_0x008f
            goto L_0x0090
        L_0x008f:
            return r10
        L_0x0090:
            goto L_0x00b3
        L_0x0091:
            r11 = 63
            goto L_0x0096
        L_0x0094:
            r11 = 62
        L_0x0096:
            int r14 = r9 << 6
            r9 = r14 | r11
            int r8 = r8 + 1
            int r14 = r8 % 4
            if (r14 != 0) goto L_0x00b3
            int r14 = r7 + 1
            int r2 = r9 >> 16
            byte r2 = (byte) r2
            r6[r7] = r2
            int r2 = r14 + 1
            int r7 = r9 >> 8
            byte r7 = (byte) r7
            r6[r14] = r7
            int r7 = r2 + 1
            byte r14 = (byte) r9
            r6[r2] = r14
        L_0x00b3:
            if (r12 < r1) goto L_0x00b6
            goto L_0x00b9
        L_0x00b6:
            r2 = 9
            goto L_0x003c
        L_0x00b9:
            int r2 = r8 % 4
            switch(r2) {
                case 1: goto L_0x00db;
                case 2: goto L_0x00d0;
                case 3: goto L_0x00bf;
                default: goto L_0x00be;
            }
        L_0x00be:
            goto L_0x00dc
        L_0x00bf:
            int r9 = r9 << 6
            int r3 = r7 + 1
            int r4 = r9 >> 16
            byte r4 = (byte) r4
            r6[r7] = r4
            int r7 = r3 + 1
            int r4 = r9 >> 8
            byte r4 = (byte) r4
            r6[r3] = r4
            goto L_0x00dc
        L_0x00d0:
            int r9 = r9 << 12
            int r3 = r7 + 1
            int r4 = r9 >> 16
            byte r4 = (byte) r4
            r6[r7] = r4
            r7 = r3
            goto L_0x00dc
        L_0x00db:
            return r10
        L_0x00dc:
            int r3 = r6.length
            if (r7 != r3) goto L_0x00e0
            return r6
        L_0x00e0:
            byte[] r3 = java.util.Arrays.copyOf(r6, r7)
            java.lang.String r4 = "java.util.Arrays.copyOf(this, newSize)"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r3, r4)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: okio._Base64Kt.decodeBase64ToArray(java.lang.String):byte[]");
    }

    public static /* synthetic */ String encodeBase64$default(byte[] bArr, byte[] bArr2, int i, Object obj) {
        if ((i & 1) != 0) {
            bArr2 = BASE64;
        }
        return encodeBase64(bArr, bArr2);
    }

    public static final String encodeBase64(byte[] $this$encodeBase64, byte[] map) {
        Intrinsics.checkNotNullParameter($this$encodeBase64, "<this>");
        Intrinsics.checkNotNullParameter(map, "map");
        byte[] out = new byte[((($this$encodeBase64.length + 2) / 3) * 4)];
        int index = 0;
        int end = $this$encodeBase64.length - ($this$encodeBase64.length % 3);
        int b0 = 0;
        while (b0 < end) {
            int i = b0 + 1;
            byte b02 = $this$encodeBase64[b0];
            int i2 = i + 1;
            byte i3 = $this$encodeBase64[i];
            int i4 = i2 + 1;
            byte b2 = $this$encodeBase64[i2];
            int index2 = index + 1;
            out[index] = map[(b02 & UByte.MAX_VALUE) >> 2];
            int index3 = index2 + 1;
            out[index2] = map[((b02 & 3) << 4) | ((i3 & UByte.MAX_VALUE) >> 4)];
            int index4 = index3 + 1;
            out[index3] = map[((i3 & 15) << 2) | ((b2 & UByte.MAX_VALUE) >> 6)];
            index = index4 + 1;
            out[index4] = map[b2 & Utf8.REPLACEMENT_BYTE];
            b0 = i4;
        }
        switch ($this$encodeBase64.length - end) {
            case 1:
                byte b03 = $this$encodeBase64[b0];
                int index5 = index + 1;
                out[index] = map[(b03 & UByte.MAX_VALUE) >> 2];
                int index6 = index5 + 1;
                out[index5] = map[(b03 & 3) << 4];
                int index7 = index6 + 1;
                byte b = (byte) 61;
                out[index6] = b;
                out[index7] = b;
                int i5 = index7;
                break;
            case 2:
                int i6 = b0 + 1;
                byte b04 = $this$encodeBase64[b0];
                byte b1 = $this$encodeBase64[i6];
                int index8 = index + 1;
                out[index] = map[(b04 & UByte.MAX_VALUE) >> 2];
                int index9 = index8 + 1;
                out[index8] = map[((b04 & 3) << 4) | ((b1 & UByte.MAX_VALUE) >> 4)];
                int index10 = index9 + 1;
                out[index9] = map[(b1 & 15) << 2];
                out[index10] = (byte) 61;
                int b05 = i6;
                int i7 = index10;
                break;
        }
        return _JvmPlatformKt.toUtf8String(out);
    }
}

package okhttp3.internal;

import java.net.IDN;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okio.Buffer;

@Metadata(d1 = {"\u0000&\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a0\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0005H\u0002\u001a\"\u0010\n\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005H\u0002\u001a\u0010\u0010\f\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\bH\u0002\u001a\f\u0010\r\u001a\u00020\u0001*\u00020\u0003H\u0002\u001a\f\u0010\u000e\u001a\u0004\u0018\u00010\u0003*\u00020\u0003¨\u0006\u000f"}, d2 = {"decodeIpv4Suffix", "", "input", "", "pos", "", "limit", "address", "", "addressOffset", "decodeIpv6", "Ljava/net/InetAddress;", "inet6AddressToAscii", "containsInvalidHostnameAsciiCodes", "toCanonicalHost", "okhttp"}, k = 2, mv = {1, 6, 0}, xi = 48)
/* compiled from: hostnames.kt */
public final class HostnamesKt {
    public static final String toCanonicalHost(String $this$toCanonicalHost) {
        InetAddress inetAddress;
        Intrinsics.checkNotNullParameter($this$toCanonicalHost, "<this>");
        String host = $this$toCanonicalHost;
        boolean z = false;
        if (StringsKt.contains$default((CharSequence) host, (CharSequence) ":", false, 2, (Object) null)) {
            if (!StringsKt.startsWith$default(host, "[", false, 2, (Object) null) || !StringsKt.endsWith$default(host, "]", false, 2, (Object) null)) {
                inetAddress = decodeIpv6(host, 0, host.length());
            } else {
                inetAddress = decodeIpv6(host, 1, host.length() - 1);
            }
            if (inetAddress == null) {
                return null;
            }
            byte[] address = inetAddress.getAddress();
            if (address.length == 16) {
                Intrinsics.checkNotNullExpressionValue(address, "address");
                return inet6AddressToAscii(address);
            } else if (address.length == 4) {
                return inetAddress.getHostAddress();
            } else {
                throw new AssertionError("Invalid IPv6 address: '" + host + '\'');
            }
        } else {
            try {
                String ascii = IDN.toASCII(host);
                Intrinsics.checkNotNullExpressionValue(ascii, "toASCII(host)");
                Locale locale = Locale.US;
                Intrinsics.checkNotNullExpressionValue(locale, "US");
                String result = ascii.toLowerCase(locale);
                Intrinsics.checkNotNullExpressionValue(result, "this as java.lang.String).toLowerCase(locale)");
                if (result.length() == 0) {
                    z = true;
                }
                if (z) {
                    return null;
                }
                if (!containsInvalidHostnameAsciiCodes(result)) {
                    return result;
                }
                String str = null;
                return null;
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:2:0x0008  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static final boolean containsInvalidHostnameAsciiCodes(java.lang.String r12) {
        /*
            int r0 = r12.length()
            r1 = 0
            r2 = r1
        L_0x0006:
            if (r2 >= r0) goto L_0x0033
            r3 = r2
            int r2 = r2 + 1
            char r10 = r12.charAt(r3)
            r4 = 31
            int r4 = kotlin.jvm.internal.Intrinsics.compare((int) r10, (int) r4)
            r11 = 1
            if (r4 <= 0) goto L_0x0032
            r4 = 127(0x7f, float:1.78E-43)
            int r4 = kotlin.jvm.internal.Intrinsics.compare((int) r10, (int) r4)
            if (r4 < 0) goto L_0x0021
            goto L_0x0032
        L_0x0021:
            java.lang.String r4 = " #%/:?@[\\]"
            java.lang.CharSequence r4 = (java.lang.CharSequence) r4
            r6 = 0
            r7 = 0
            r8 = 6
            r9 = 0
            r5 = r10
            int r4 = kotlin.text.StringsKt.indexOf$default((java.lang.CharSequence) r4, (char) r5, (int) r6, (boolean) r7, (int) r8, (java.lang.Object) r9)
            r5 = -1
            if (r4 == r5) goto L_0x0006
            return r11
        L_0x0032:
            return r11
        L_0x0033:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.HostnamesKt.containsInvalidHostnameAsciiCodes(java.lang.String):boolean");
    }

    private static final InetAddress decodeIpv6(String input, int pos, int limit) {
        byte[] address = new byte[16];
        int b = 0;
        int compress = -1;
        int groupOffset = -1;
        int i = pos;
        while (true) {
            if (i >= limit) {
                break;
            } else if (b == address.length) {
                return null;
            } else {
                if (i + 2 <= limit && StringsKt.startsWith$default(input, "::", i, false, 4, (Object) null)) {
                    if (compress == -1) {
                        i += 2;
                        b += 2;
                        compress = b;
                        if (i == limit) {
                            break;
                        }
                    } else {
                        return null;
                    }
                } else if (b != 0) {
                    if (StringsKt.startsWith$default(input, ":", i, false, 4, (Object) null)) {
                        i++;
                    } else if (!StringsKt.startsWith$default(input, ".", i, false, 4, (Object) null) || !decodeIpv4Suffix(input, groupOffset, limit, address, b - 2)) {
                        return null;
                    } else {
                        b += 2;
                    }
                }
                int value = 0;
                groupOffset = i;
                while (i < limit) {
                    int hexDigit = Util.parseHexDigit(input.charAt(i));
                    if (hexDigit == -1) {
                        break;
                    }
                    value = (value << 4) + hexDigit;
                    i++;
                }
                int groupLength = i - groupOffset;
                if (groupLength == 0 || groupLength > 4) {
                    return null;
                }
                int b2 = b + 1;
                address[b] = (byte) ((value >>> 8) & 255);
                b = b2 + 1;
                address[b2] = (byte) (value & 255);
            }
        }
        if (b != address.length) {
            if (compress == -1) {
                return null;
            }
            System.arraycopy(address, compress, address, address.length - (b - compress), b - compress);
            Arrays.fill(address, compress, (address.length - b) + compress, (byte) 0);
        }
        return InetAddress.getByAddress(address);
    }

    private static final boolean decodeIpv4Suffix(String input, int pos, int limit, byte[] address, int addressOffset) {
        int b = addressOffset;
        int i = pos;
        while (i < limit) {
            if (b == address.length) {
                return false;
            }
            if (b != addressOffset) {
                if (input.charAt(i) != '.') {
                    return false;
                }
                i++;
            }
            int value = 0;
            int groupOffset = i;
            while (i < limit) {
                char c = input.charAt(i);
                if (Intrinsics.compare((int) c, 48) < 0 || Intrinsics.compare((int) c, 57) > 0) {
                    break;
                } else if ((value == 0 && groupOffset != i) || ((value * 10) + c) - 48 > 255) {
                    return false;
                } else {
                    i++;
                }
            }
            if (i - groupOffset == 0) {
                return false;
            }
            address[b] = (byte) value;
            b++;
        }
        if (b == addressOffset + 4) {
            return true;
        }
        return false;
    }

    private static final String inet6AddressToAscii(byte[] address) {
        int longestRunOffset = -1;
        int longestRunLength = 0;
        int i = 0;
        while (i < address.length) {
            int currentRunOffset = i;
            while (i < 16 && address[i] == 0 && address[i + 1] == 0) {
                i += 2;
            }
            int currentRunLength = i - currentRunOffset;
            if (currentRunLength > longestRunLength && currentRunLength >= 4) {
                longestRunOffset = currentRunOffset;
                longestRunLength = currentRunLength;
            }
            i += 2;
        }
        Buffer result = new Buffer();
        int i2 = 0;
        while (i2 < address.length) {
            if (i2 == longestRunOffset) {
                result.writeByte(58);
                i2 += longestRunLength;
                if (i2 == 16) {
                    result.writeByte(58);
                }
            } else {
                if (i2 > 0) {
                    result.writeByte(58);
                }
                result.writeHexadecimalUnsignedLong((long) ((Util.and(address[i2], 255) << 8) | Util.and(address[i2 + 1], 255)));
                i2 += 2;
            }
        }
        return result.readUtf8();
    }
}

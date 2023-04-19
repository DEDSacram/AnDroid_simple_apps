package okhttp3.internal.http2;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.HttpUrl;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
import okio.Source;

@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\t\bÆ\u0002\u0018\u00002\u00020\u0001:\u0002\u0018\u0019B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0015\u001a\u00020\u00052\u0006\u0010\u0016\u001a\u00020\u0005J\u0014\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004H\u0002R\u001d\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\t\u001a\u00020\u0006XT¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0006XT¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0006XT¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0006XT¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0006XT¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0006XT¢\u0006\u0002\n\u0000R\u0019\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010¢\u0006\n\n\u0002\u0010\u0014\u001a\u0004\b\u0012\u0010\u0013¨\u0006\u001a"}, d2 = {"Lokhttp3/internal/http2/Hpack;", "", "()V", "NAME_TO_FIRST_INDEX", "", "Lokio/ByteString;", "", "getNAME_TO_FIRST_INDEX", "()Ljava/util/Map;", "PREFIX_4_BITS", "PREFIX_5_BITS", "PREFIX_6_BITS", "PREFIX_7_BITS", "SETTINGS_HEADER_TABLE_SIZE", "SETTINGS_HEADER_TABLE_SIZE_LIMIT", "STATIC_HEADER_TABLE", "", "Lokhttp3/internal/http2/Header;", "getSTATIC_HEADER_TABLE", "()[Lokhttp3/internal/http2/Header;", "[Lokhttp3/internal/http2/Header;", "checkLowercase", "name", "nameToFirstIndex", "Reader", "Writer", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: Hpack.kt */
public final class Hpack {
    public static final Hpack INSTANCE;
    private static final Map<ByteString, Integer> NAME_TO_FIRST_INDEX;
    private static final int PREFIX_4_BITS = 15;
    private static final int PREFIX_5_BITS = 31;
    private static final int PREFIX_6_BITS = 63;
    private static final int PREFIX_7_BITS = 127;
    private static final int SETTINGS_HEADER_TABLE_SIZE = 4096;
    private static final int SETTINGS_HEADER_TABLE_SIZE_LIMIT = 16384;
    private static final Header[] STATIC_HEADER_TABLE = {new Header(Header.TARGET_AUTHORITY, (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header(Header.TARGET_METHOD, "GET"), new Header(Header.TARGET_METHOD, "POST"), new Header(Header.TARGET_PATH, "/"), new Header(Header.TARGET_PATH, "/index.html"), new Header(Header.TARGET_SCHEME, "http"), new Header(Header.TARGET_SCHEME, "https"), new Header(Header.RESPONSE_STATUS, "200"), new Header(Header.RESPONSE_STATUS, "204"), new Header(Header.RESPONSE_STATUS, "206"), new Header(Header.RESPONSE_STATUS, "304"), new Header(Header.RESPONSE_STATUS, "400"), new Header(Header.RESPONSE_STATUS, "404"), new Header(Header.RESPONSE_STATUS, "500"), new Header("accept-charset", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("accept-encoding", "gzip, deflate"), new Header("accept-language", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("accept-ranges", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("accept", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("access-control-allow-origin", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("age", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("allow", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("authorization", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("cache-control", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("content-disposition", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("content-encoding", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("content-language", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("content-length", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("content-location", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("content-range", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("content-type", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("cookie", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("date", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("etag", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("expect", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("expires", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header((String) TypedValues.TransitionType.S_FROM, (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("host", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("if-match", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("if-modified-since", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("if-none-match", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("if-range", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("if-unmodified-since", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("last-modified", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("link", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("location", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("max-forwards", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("proxy-authenticate", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("proxy-authorization", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("range", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("referer", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("refresh", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("retry-after", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("server", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("set-cookie", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("strict-transport-security", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("transfer-encoding", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("user-agent", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("vary", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("via", (String) HttpUrl.FRAGMENT_ENCODE_SET), new Header("www-authenticate", (String) HttpUrl.FRAGMENT_ENCODE_SET)};

    private Hpack() {
    }

    static {
        Hpack hpack = new Hpack();
        INSTANCE = hpack;
        NAME_TO_FIRST_INDEX = hpack.nameToFirstIndex();
    }

    public final Header[] getSTATIC_HEADER_TABLE() {
        return STATIC_HEADER_TABLE;
    }

    public final Map<ByteString, Integer> getNAME_TO_FIRST_INDEX() {
        return NAME_TO_FIRST_INDEX;
    }

    @Metadata(d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010!\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\r\u0018\u00002\u00020\u0001B!\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005¢\u0006\u0002\u0010\u0007J\b\u0010\u0012\u001a\u00020\u0013H\u0002J\b\u0010\u0014\u001a\u00020\u0013H\u0002J\u0010\u0010\u0015\u001a\u00020\u00052\u0006\u0010\u0016\u001a\u00020\u0005H\u0002J\u0010\u0010\u0017\u001a\u00020\u00052\u0006\u0010\u0018\u001a\u00020\u0005H\u0002J\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\n0\u001aJ\u0010\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u0016\u001a\u00020\u0005H\u0002J\u0018\u0010\u001d\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u00052\u0006\u0010\u001e\u001a\u00020\nH\u0002J\u0010\u0010\u001f\u001a\u00020 2\u0006\u0010\u0016\u001a\u00020\u0005H\u0002J\u0006\u0010\u0006\u001a\u00020\u0005J\b\u0010!\u001a\u00020\u0005H\u0002J\u0006\u0010\"\u001a\u00020\u001cJ\u0006\u0010#\u001a\u00020\u0013J\u0010\u0010$\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u0005H\u0002J\u0016\u0010%\u001a\u00020\u00052\u0006\u0010&\u001a\u00020\u00052\u0006\u0010'\u001a\u00020\u0005J\u0010\u0010(\u001a\u00020\u00132\u0006\u0010)\u001a\u00020\u0005H\u0002J\b\u0010*\u001a\u00020\u0013H\u0002J\u0010\u0010+\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u0005H\u0002J\b\u0010,\u001a\u00020\u0013H\u0002R\u001c\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\t8\u0006@\u0006X\u000e¢\u0006\u0004\n\u0002\u0010\u000bR\u0012\u0010\f\u001a\u00020\u00058\u0006@\u0006X\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\r\u001a\u00020\u00058\u0006@\u0006X\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\n0\u000fX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0005X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0011X\u0004¢\u0006\u0002\n\u0000¨\u0006-"}, d2 = {"Lokhttp3/internal/http2/Hpack$Reader;", "", "source", "Lokio/Source;", "headerTableSizeSetting", "", "maxDynamicTableByteCount", "(Lokio/Source;II)V", "dynamicTable", "", "Lokhttp3/internal/http2/Header;", "[Lokhttp3/internal/http2/Header;", "dynamicTableByteCount", "headerCount", "headerList", "", "nextHeaderIndex", "Lokio/BufferedSource;", "adjustDynamicTableByteCount", "", "clearDynamicTable", "dynamicTableIndex", "index", "evictToRecoverBytes", "bytesToRecover", "getAndResetHeaderList", "", "getName", "Lokio/ByteString;", "insertIntoDynamicTable", "entry", "isStaticHeader", "", "readByte", "readByteString", "readHeaders", "readIndexedHeader", "readInt", "firstByte", "prefixMask", "readLiteralHeaderWithIncrementalIndexingIndexedName", "nameIndex", "readLiteralHeaderWithIncrementalIndexingNewName", "readLiteralHeaderWithoutIndexingIndexedName", "readLiteralHeaderWithoutIndexingNewName", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: Hpack.kt */
    public static final class Reader {
        public Header[] dynamicTable;
        public int dynamicTableByteCount;
        public int headerCount;
        private final List<Header> headerList;
        private final int headerTableSizeSetting;
        private int maxDynamicTableByteCount;
        private int nextHeaderIndex;
        private final BufferedSource source;

        /* JADX INFO: this call moved to the top of the method (can break code semantics) */
        public Reader(Source source2, int i) {
            this(source2, i, 0, 4, (DefaultConstructorMarker) null);
            Intrinsics.checkNotNullParameter(source2, "source");
        }

        public Reader(Source source2, int headerTableSizeSetting2, int maxDynamicTableByteCount2) {
            Intrinsics.checkNotNullParameter(source2, "source");
            this.headerTableSizeSetting = headerTableSizeSetting2;
            this.maxDynamicTableByteCount = maxDynamicTableByteCount2;
            this.headerList = new ArrayList();
            this.source = Okio.buffer(source2);
            Header[] headerArr = new Header[8];
            this.dynamicTable = headerArr;
            this.nextHeaderIndex = headerArr.length - 1;
        }

        /* JADX INFO: this call moved to the top of the method (can break code semantics) */
        public /* synthetic */ Reader(Source source2, int i, int i2, int i3, DefaultConstructorMarker defaultConstructorMarker) {
            this(source2, i, (i3 & 4) != 0 ? i : i2);
        }

        public final List<Header> getAndResetHeaderList() {
            List result = CollectionsKt.toList(this.headerList);
            this.headerList.clear();
            return result;
        }

        public final int maxDynamicTableByteCount() {
            return this.maxDynamicTableByteCount;
        }

        private final void adjustDynamicTableByteCount() {
            int i = this.maxDynamicTableByteCount;
            int i2 = this.dynamicTableByteCount;
            if (i >= i2) {
                return;
            }
            if (i == 0) {
                clearDynamicTable();
            } else {
                evictToRecoverBytes(i2 - i);
            }
        }

        private final void clearDynamicTable() {
            ArraysKt.fill$default((Object[]) this.dynamicTable, (Object) null, 0, 0, 6, (Object) null);
            this.nextHeaderIndex = this.dynamicTable.length - 1;
            this.headerCount = 0;
            this.dynamicTableByteCount = 0;
        }

        private final int evictToRecoverBytes(int bytesToRecover) {
            int i;
            int bytesToRecover2 = bytesToRecover;
            int entriesToEvict = 0;
            if (bytesToRecover2 > 0) {
                int j = this.dynamicTable.length;
                while (true) {
                    j--;
                    i = this.nextHeaderIndex;
                    if (j < i || bytesToRecover2 <= 0) {
                        Header[] headerArr = this.dynamicTable;
                        System.arraycopy(headerArr, i + 1, headerArr, i + 1 + entriesToEvict, this.headerCount);
                        this.nextHeaderIndex += entriesToEvict;
                    } else {
                        Header toEvict = this.dynamicTable[j];
                        Intrinsics.checkNotNull(toEvict);
                        bytesToRecover2 -= toEvict.hpackSize;
                        this.dynamicTableByteCount -= toEvict.hpackSize;
                        this.headerCount--;
                        entriesToEvict++;
                    }
                }
                Header[] headerArr2 = this.dynamicTable;
                System.arraycopy(headerArr2, i + 1, headerArr2, i + 1 + entriesToEvict, this.headerCount);
                this.nextHeaderIndex += entriesToEvict;
            }
            return entriesToEvict;
        }

        public final void readHeaders() throws IOException {
            while (!this.source.exhausted()) {
                int b = Util.and(this.source.readByte(), 255);
                if (b == 128) {
                    throw new IOException("index == 0");
                } else if ((b & 128) == 128) {
                    readIndexedHeader(readInt(b, 127) - 1);
                } else if (b == 64) {
                    readLiteralHeaderWithIncrementalIndexingNewName();
                } else if ((b & 64) == 64) {
                    readLiteralHeaderWithIncrementalIndexingIndexedName(readInt(b, 63) - 1);
                } else if ((b & 32) == 32) {
                    int readInt = readInt(b, 31);
                    this.maxDynamicTableByteCount = readInt;
                    if (readInt < 0 || readInt > this.headerTableSizeSetting) {
                        throw new IOException(Intrinsics.stringPlus("Invalid dynamic table size update ", Integer.valueOf(this.maxDynamicTableByteCount)));
                    }
                    adjustDynamicTableByteCount();
                } else if (b == 16 || b == 0) {
                    readLiteralHeaderWithoutIndexingNewName();
                } else {
                    readLiteralHeaderWithoutIndexingIndexedName(readInt(b, 15) - 1);
                }
            }
        }

        private final void readIndexedHeader(int index) throws IOException {
            if (isStaticHeader(index)) {
                this.headerList.add(Hpack.INSTANCE.getSTATIC_HEADER_TABLE()[index]);
                return;
            }
            int dynamicTableIndex = dynamicTableIndex(index - Hpack.INSTANCE.getSTATIC_HEADER_TABLE().length);
            if (dynamicTableIndex >= 0) {
                Header[] headerArr = this.dynamicTable;
                if (dynamicTableIndex < headerArr.length) {
                    Header header = headerArr[dynamicTableIndex];
                    Intrinsics.checkNotNull(header);
                    this.headerList.add(header);
                    return;
                }
            }
            throw new IOException(Intrinsics.stringPlus("Header index too large ", Integer.valueOf(index + 1)));
        }

        private final int dynamicTableIndex(int index) {
            return this.nextHeaderIndex + 1 + index;
        }

        private final void readLiteralHeaderWithoutIndexingIndexedName(int index) throws IOException {
            this.headerList.add(new Header(getName(index), readByteString()));
        }

        private final void readLiteralHeaderWithoutIndexingNewName() throws IOException {
            this.headerList.add(new Header(Hpack.INSTANCE.checkLowercase(readByteString()), readByteString()));
        }

        private final void readLiteralHeaderWithIncrementalIndexingIndexedName(int nameIndex) throws IOException {
            insertIntoDynamicTable(-1, new Header(getName(nameIndex), readByteString()));
        }

        private final void readLiteralHeaderWithIncrementalIndexingNewName() throws IOException {
            insertIntoDynamicTable(-1, new Header(Hpack.INSTANCE.checkLowercase(readByteString()), readByteString()));
        }

        private final ByteString getName(int index) throws IOException {
            if (isStaticHeader(index)) {
                return Hpack.INSTANCE.getSTATIC_HEADER_TABLE()[index].name;
            }
            int dynamicTableIndex = dynamicTableIndex(index - Hpack.INSTANCE.getSTATIC_HEADER_TABLE().length);
            if (dynamicTableIndex >= 0) {
                Header[] headerArr = this.dynamicTable;
                if (dynamicTableIndex < headerArr.length) {
                    Header header = headerArr[dynamicTableIndex];
                    Intrinsics.checkNotNull(header);
                    return header.name;
                }
            }
            throw new IOException(Intrinsics.stringPlus("Header index too large ", Integer.valueOf(index + 1)));
        }

        private final boolean isStaticHeader(int index) {
            return index >= 0 && index <= Hpack.INSTANCE.getSTATIC_HEADER_TABLE().length - 1;
        }

        private final void insertIntoDynamicTable(int index, Header entry) {
            int index2 = index;
            this.headerList.add(entry);
            int delta = entry.hpackSize;
            if (index2 != -1) {
                Header header = this.dynamicTable[dynamicTableIndex(index2)];
                Intrinsics.checkNotNull(header);
                delta -= header.hpackSize;
            }
            int i = this.maxDynamicTableByteCount;
            if (delta > i) {
                clearDynamicTable();
                return;
            }
            int entriesEvicted = evictToRecoverBytes((this.dynamicTableByteCount + delta) - i);
            if (index2 == -1) {
                int i2 = this.headerCount + 1;
                Header[] headerArr = this.dynamicTable;
                if (i2 > headerArr.length) {
                    Header[] doubled = new Header[(headerArr.length * 2)];
                    System.arraycopy(headerArr, 0, doubled, headerArr.length, headerArr.length);
                    this.nextHeaderIndex = this.dynamicTable.length - 1;
                    this.dynamicTable = doubled;
                }
                int index3 = this.nextHeaderIndex;
                this.nextHeaderIndex = index3 - 1;
                this.dynamicTable[index3] = entry;
                this.headerCount++;
            } else {
                this.dynamicTable[index2 + dynamicTableIndex(index2) + entriesEvicted] = entry;
            }
            this.dynamicTableByteCount += delta;
        }

        private final int readByte() throws IOException {
            return Util.and(this.source.readByte(), 255);
        }

        public final int readInt(int firstByte, int prefixMask) throws IOException {
            int prefix = firstByte & prefixMask;
            if (prefix < prefixMask) {
                return prefix;
            }
            int result = prefixMask;
            int shift = 0;
            while (true) {
                int b = readByte();
                if ((b & 128) == 0) {
                    return result + (b << shift);
                }
                result += (b & 127) << shift;
                shift += 7;
            }
        }

        public final ByteString readByteString() throws IOException {
            int firstByte = readByte();
            boolean huffmanDecode = (firstByte & 128) == 128;
            long length = (long) readInt(firstByte, 127);
            if (!huffmanDecode) {
                return this.source.readByteString(length);
            }
            Buffer decodeBuffer = new Buffer();
            Huffman.INSTANCE.decode(this.source, length, decodeBuffer);
            return decodeBuffer.readByteString();
        }
    }

    private final Map<ByteString, Integer> nameToFirstIndex() {
        Header[] headerArr = STATIC_HEADER_TABLE;
        LinkedHashMap result = new LinkedHashMap(headerArr.length);
        int length = headerArr.length;
        int i = 0;
        while (i < length) {
            int i2 = i;
            i++;
            Header[] headerArr2 = STATIC_HEADER_TABLE;
            if (!result.containsKey(headerArr2[i2].name)) {
                result.put(headerArr2[i2].name, Integer.valueOf(i2));
            }
        }
        Map<ByteString, Integer> unmodifiableMap = Collections.unmodifiableMap(result);
        Intrinsics.checkNotNullExpressionValue(unmodifiableMap, "unmodifiableMap(result)");
        return unmodifiableMap;
    }

    @Metadata(d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0005\u0018\u00002\u00020\u0001B#\b\u0007\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ\b\u0010\u0013\u001a\u00020\u0014H\u0002J\b\u0010\u0015\u001a\u00020\u0014H\u0002J\u0010\u0010\u0016\u001a\u00020\u00032\u0006\u0010\u0017\u001a\u00020\u0003H\u0002J\u0010\u0010\u0018\u001a\u00020\u00142\u0006\u0010\u0019\u001a\u00020\u000bH\u0002J\u000e\u0010\u001a\u001a\u00020\u00142\u0006\u0010\u0002\u001a\u00020\u0003J\u000e\u0010\u001b\u001a\u00020\u00142\u0006\u0010\u001c\u001a\u00020\u001dJ\u0014\u0010\u001e\u001a\u00020\u00142\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u000b0 J\u001e\u0010!\u001a\u00020\u00142\u0006\u0010\"\u001a\u00020\u00032\u0006\u0010#\u001a\u00020\u00032\u0006\u0010$\u001a\u00020\u0003R\u001c\u0010\t\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\n8\u0006@\u0006X\u000e¢\u0006\u0004\n\u0002\u0010\fR\u0012\u0010\r\u001a\u00020\u00038\u0006@\u0006X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0005X\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u000f\u001a\u00020\u00038\u0006@\u0006X\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0002\u001a\u00020\u00038\u0006@\u0006X\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0010\u001a\u00020\u00038\u0006@\u0006X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0003X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0003X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0004¢\u0006\u0002\n\u0000¨\u0006%"}, d2 = {"Lokhttp3/internal/http2/Hpack$Writer;", "", "headerTableSizeSetting", "", "useCompression", "", "out", "Lokio/Buffer;", "(IZLokio/Buffer;)V", "dynamicTable", "", "Lokhttp3/internal/http2/Header;", "[Lokhttp3/internal/http2/Header;", "dynamicTableByteCount", "emitDynamicTableSizeUpdate", "headerCount", "maxDynamicTableByteCount", "nextHeaderIndex", "smallestHeaderTableSizeSetting", "adjustDynamicTableByteCount", "", "clearDynamicTable", "evictToRecoverBytes", "bytesToRecover", "insertIntoDynamicTable", "entry", "resizeHeaderTable", "writeByteString", "data", "Lokio/ByteString;", "writeHeaders", "headerBlock", "", "writeInt", "value", "prefixMask", "bits", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: Hpack.kt */
    public static final class Writer {
        public Header[] dynamicTable;
        public int dynamicTableByteCount;
        private boolean emitDynamicTableSizeUpdate;
        public int headerCount;
        public int headerTableSizeSetting;
        public int maxDynamicTableByteCount;
        private int nextHeaderIndex;
        private final Buffer out;
        private int smallestHeaderTableSizeSetting;
        private final boolean useCompression;

        /* JADX INFO: this call moved to the top of the method (can break code semantics) */
        public Writer(int i, Buffer buffer) {
            this(i, false, buffer, 2, (DefaultConstructorMarker) null);
            Intrinsics.checkNotNullParameter(buffer, "out");
        }

        /* JADX INFO: this call moved to the top of the method (can break code semantics) */
        public Writer(Buffer buffer) {
            this(0, false, buffer, 3, (DefaultConstructorMarker) null);
            Intrinsics.checkNotNullParameter(buffer, "out");
        }

        public Writer(int headerTableSizeSetting2, boolean useCompression2, Buffer out2) {
            Intrinsics.checkNotNullParameter(out2, "out");
            this.headerTableSizeSetting = headerTableSizeSetting2;
            this.useCompression = useCompression2;
            this.out = out2;
            this.smallestHeaderTableSizeSetting = Integer.MAX_VALUE;
            this.maxDynamicTableByteCount = headerTableSizeSetting2;
            Header[] headerArr = new Header[8];
            this.dynamicTable = headerArr;
            this.nextHeaderIndex = headerArr.length - 1;
        }

        /* JADX INFO: this call moved to the top of the method (can break code semantics) */
        public /* synthetic */ Writer(int i, boolean z, Buffer buffer, int i2, DefaultConstructorMarker defaultConstructorMarker) {
            this((i2 & 1) != 0 ? 4096 : i, (i2 & 2) != 0 ? true : z, buffer);
        }

        private final void clearDynamicTable() {
            ArraysKt.fill$default((Object[]) this.dynamicTable, (Object) null, 0, 0, 6, (Object) null);
            this.nextHeaderIndex = this.dynamicTable.length - 1;
            this.headerCount = 0;
            this.dynamicTableByteCount = 0;
        }

        private final int evictToRecoverBytes(int bytesToRecover) {
            int i;
            int bytesToRecover2 = bytesToRecover;
            int entriesToEvict = 0;
            if (bytesToRecover2 > 0) {
                int j = this.dynamicTable.length;
                while (true) {
                    j--;
                    i = this.nextHeaderIndex;
                    if (j < i || bytesToRecover2 <= 0) {
                        Header[] headerArr = this.dynamicTable;
                        System.arraycopy(headerArr, i + 1, headerArr, i + 1 + entriesToEvict, this.headerCount);
                        Header[] headerArr2 = this.dynamicTable;
                        int i2 = this.nextHeaderIndex;
                        Arrays.fill(headerArr2, i2 + 1, i2 + 1 + entriesToEvict, (Object) null);
                        this.nextHeaderIndex += entriesToEvict;
                    } else {
                        Header header = this.dynamicTable[j];
                        Intrinsics.checkNotNull(header);
                        bytesToRecover2 -= header.hpackSize;
                        int i3 = this.dynamicTableByteCount;
                        Header header2 = this.dynamicTable[j];
                        Intrinsics.checkNotNull(header2);
                        this.dynamicTableByteCount = i3 - header2.hpackSize;
                        this.headerCount--;
                        entriesToEvict++;
                    }
                }
                Header[] headerArr3 = this.dynamicTable;
                System.arraycopy(headerArr3, i + 1, headerArr3, i + 1 + entriesToEvict, this.headerCount);
                Header[] headerArr22 = this.dynamicTable;
                int i22 = this.nextHeaderIndex;
                Arrays.fill(headerArr22, i22 + 1, i22 + 1 + entriesToEvict, (Object) null);
                this.nextHeaderIndex += entriesToEvict;
            }
            return entriesToEvict;
        }

        private final void insertIntoDynamicTable(Header entry) {
            int delta = entry.hpackSize;
            int i = this.maxDynamicTableByteCount;
            if (delta > i) {
                clearDynamicTable();
                return;
            }
            evictToRecoverBytes((this.dynamicTableByteCount + delta) - i);
            int i2 = this.headerCount + 1;
            Header[] headerArr = this.dynamicTable;
            if (i2 > headerArr.length) {
                Header[] doubled = new Header[(headerArr.length * 2)];
                System.arraycopy(headerArr, 0, doubled, headerArr.length, headerArr.length);
                this.nextHeaderIndex = this.dynamicTable.length - 1;
                this.dynamicTable = doubled;
            }
            int index = this.nextHeaderIndex;
            this.nextHeaderIndex = index - 1;
            this.dynamicTable[index] = entry;
            this.headerCount++;
            this.dynamicTableByteCount += delta;
        }

        public final void writeHeaders(List<Header> headerBlock) throws IOException {
            boolean z;
            List<Header> list = headerBlock;
            Intrinsics.checkNotNullParameter(list, "headerBlock");
            boolean z2 = false;
            if (this.emitDynamicTableSizeUpdate) {
                int i = this.smallestHeaderTableSizeSetting;
                if (i < this.maxDynamicTableByteCount) {
                    writeInt(i, 31, 32);
                }
                this.emitDynamicTableSizeUpdate = false;
                this.smallestHeaderTableSizeSetting = Integer.MAX_VALUE;
                writeInt(this.maxDynamicTableByteCount, 31, 32);
            }
            int size = headerBlock.size();
            int i2 = 0;
            while (i2 < size) {
                int i3 = i2;
                i2++;
                Header header = list.get(i3);
                ByteString name = header.name.toAsciiLowercase();
                ByteString value = header.value;
                int headerIndex = -1;
                int headerNameIndex = -1;
                Integer staticIndex = Hpack.INSTANCE.getNAME_TO_FIRST_INDEX().get(name);
                if (staticIndex != null) {
                    headerNameIndex = staticIndex.intValue() + 1;
                    if ((2 > headerNameIndex || headerNameIndex >= 8) ? z2 : true) {
                        if (Intrinsics.areEqual((Object) Hpack.INSTANCE.getSTATIC_HEADER_TABLE()[headerNameIndex - 1].value, (Object) value)) {
                            headerIndex = headerNameIndex;
                        } else if (Intrinsics.areEqual((Object) Hpack.INSTANCE.getSTATIC_HEADER_TABLE()[headerNameIndex].value, (Object) value)) {
                            headerIndex = headerNameIndex + 1;
                        }
                    }
                }
                int i4 = -1;
                if (headerIndex == -1) {
                    int i5 = this.nextHeaderIndex + 1;
                    int length = this.dynamicTable.length;
                    while (true) {
                        if (i5 >= length) {
                            break;
                        }
                        int j = i5;
                        i5++;
                        Header header2 = this.dynamicTable[j];
                        Intrinsics.checkNotNull(header2);
                        if (Intrinsics.areEqual((Object) header2.name, (Object) name)) {
                            Header header3 = this.dynamicTable[j];
                            Intrinsics.checkNotNull(header3);
                            if (Intrinsics.areEqual((Object) header3.value, (Object) value)) {
                                headerIndex = (j - this.nextHeaderIndex) + Hpack.INSTANCE.getSTATIC_HEADER_TABLE().length;
                                break;
                            } else if (headerNameIndex == i4) {
                                headerNameIndex = (j - this.nextHeaderIndex) + Hpack.INSTANCE.getSTATIC_HEADER_TABLE().length;
                                i4 = -1;
                            }
                        }
                        i4 = -1;
                    }
                }
                if (headerIndex != -1) {
                    writeInt(headerIndex, 127, 128);
                    z = false;
                } else if (headerNameIndex == -1) {
                    this.out.writeByte(64);
                    writeByteString(name);
                    writeByteString(value);
                    insertIntoDynamicTable(header);
                    z = false;
                } else if (!name.startsWith(Header.PSEUDO_PREFIX) || Intrinsics.areEqual((Object) Header.TARGET_AUTHORITY, (Object) name)) {
                    z = false;
                    writeInt(headerNameIndex, 63, 64);
                    writeByteString(value);
                    insertIntoDynamicTable(header);
                } else {
                    z = false;
                    writeInt(headerNameIndex, 15, 0);
                    writeByteString(value);
                }
                z2 = z;
            }
        }

        public final void writeInt(int value, int prefixMask, int bits) {
            int value2 = value;
            if (value2 < prefixMask) {
                this.out.writeByte(bits | value2);
                return;
            }
            this.out.writeByte(bits | prefixMask);
            int value3 = value2 - prefixMask;
            while (value3 >= 128) {
                this.out.writeByte((value3 & 127) | 128);
                value3 >>>= 7;
            }
            this.out.writeByte(value3);
        }

        public final void writeByteString(ByteString data) throws IOException {
            Intrinsics.checkNotNullParameter(data, "data");
            if (!this.useCompression || Huffman.INSTANCE.encodedLength(data) >= data.size()) {
                writeInt(data.size(), 127, 0);
                this.out.write(data);
                return;
            }
            Buffer huffmanBuffer = new Buffer();
            Huffman.INSTANCE.encode(data, huffmanBuffer);
            ByteString huffmanBytes = huffmanBuffer.readByteString();
            writeInt(huffmanBytes.size(), 127, 128);
            this.out.write(huffmanBytes);
        }

        public final void resizeHeaderTable(int headerTableSizeSetting2) {
            this.headerTableSizeSetting = headerTableSizeSetting2;
            int effectiveHeaderTableSize = Math.min(headerTableSizeSetting2, 16384);
            int i = this.maxDynamicTableByteCount;
            if (i != effectiveHeaderTableSize) {
                if (effectiveHeaderTableSize < i) {
                    this.smallestHeaderTableSizeSetting = Math.min(this.smallestHeaderTableSizeSetting, effectiveHeaderTableSize);
                }
                this.emitDynamicTableSizeUpdate = true;
                this.maxDynamicTableByteCount = effectiveHeaderTableSize;
                adjustDynamicTableByteCount();
            }
        }

        private final void adjustDynamicTableByteCount() {
            int i = this.maxDynamicTableByteCount;
            int i2 = this.dynamicTableByteCount;
            if (i >= i2) {
                return;
            }
            if (i == 0) {
                clearDynamicTable();
            } else {
                evictToRecoverBytes(i2 - i);
            }
        }
    }

    public final ByteString checkLowercase(ByteString name) throws IOException {
        boolean z;
        Intrinsics.checkNotNullParameter(name, "name");
        int size = name.size();
        int i = 0;
        while (i < size) {
            int i2 = i;
            i++;
            byte b = name.getByte(i2);
            if (65 > b || b > 90) {
                z = false;
                continue;
            } else {
                z = true;
                continue;
            }
            if (z) {
                throw new IOException(Intrinsics.stringPlus("PROTOCOL_ERROR response malformed: mixed case name: ", name.utf8()));
            }
        }
        return name;
    }
}

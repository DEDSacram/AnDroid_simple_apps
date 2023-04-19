package okio.internal;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.UShort;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.text.CharsKt;
import kotlin.text.StringsKt;
import okhttp3.internal.ws.WebSocketProtocol;
import okio.BufferedSource;
import okio.FileMetadata;
import okio.FileSystem;
import okio.Path;
import okio.ZipFileSystem;

@Metadata(d1 = {"\u0000j\n\u0000\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\u001a\"\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00150\u00132\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00150\u0017H\u0002\u001a\u001f\u0010\u0018\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u0019\u001a\u00020\u00012\u0006\u0010\u001a\u001a\u00020\u0001H\u0002¢\u0006\u0002\u0010\u001b\u001a.\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u00142\u0006\u0010\u001f\u001a\u00020 2\u0014\b\u0002\u0010!\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020#0\"H\u0000\u001a\f\u0010$\u001a\u00020\u0015*\u00020%H\u0000\u001a\f\u0010&\u001a\u00020'*\u00020%H\u0002\u001a.\u0010(\u001a\u00020)*\u00020%2\u0006\u0010*\u001a\u00020\u00012\u0018\u0010+\u001a\u0014\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020)0,H\u0002\u001a\u0014\u0010-\u001a\u00020.*\u00020%2\u0006\u0010/\u001a\u00020.H\u0000\u001a\u0018\u00100\u001a\u0004\u0018\u00010.*\u00020%2\b\u0010/\u001a\u0004\u0018\u00010.H\u0002\u001a\u0014\u00101\u001a\u00020'*\u00020%2\u0006\u00102\u001a\u00020'H\u0002\u001a\f\u00103\u001a\u00020)*\u00020%H\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000\"\u000e\u0010\u0005\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000\"\u000e\u0010\u0006\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000\"\u000e\u0010\u0007\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000\"\u000e\u0010\b\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000\"\u000e\u0010\t\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000\"\u000e\u0010\n\u001a\u00020\u000bXT¢\u0006\u0002\n\u0000\"\u000e\u0010\f\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000\"\u000e\u0010\r\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000\"\u0018\u0010\u000e\u001a\u00020\u000f*\u00020\u00018BX\u0004¢\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011¨\u00064"}, d2 = {"BIT_FLAG_ENCRYPTED", "", "BIT_FLAG_UNSUPPORTED_MASK", "CENTRAL_FILE_HEADER_SIGNATURE", "COMPRESSION_METHOD_DEFLATED", "COMPRESSION_METHOD_STORED", "END_OF_CENTRAL_DIRECTORY_SIGNATURE", "HEADER_ID_EXTENDED_TIMESTAMP", "HEADER_ID_ZIP64_EXTENDED_INFO", "LOCAL_FILE_HEADER_SIGNATURE", "MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE", "", "ZIP64_EOCD_RECORD_SIGNATURE", "ZIP64_LOCATOR_SIGNATURE", "hex", "", "getHex", "(I)Ljava/lang/String;", "buildIndex", "", "Lokio/Path;", "Lokio/internal/ZipEntry;", "entries", "", "dosDateTimeToEpochMillis", "date", "time", "(II)Ljava/lang/Long;", "openZip", "Lokio/ZipFileSystem;", "zipPath", "fileSystem", "Lokio/FileSystem;", "predicate", "Lkotlin/Function1;", "", "readEntry", "Lokio/BufferedSource;", "readEocdRecord", "Lokio/internal/EocdRecord;", "readExtra", "", "extraSize", "block", "Lkotlin/Function2;", "readLocalHeader", "Lokio/FileMetadata;", "basicMetadata", "readOrSkipLocalHeader", "readZip64EocdRecord", "regularRecord", "skipLocalHeader", "okio"}, k = 2, mv = {1, 5, 1}, xi = 48)
/* compiled from: zip.kt */
public final class ZipKt {
    private static final int BIT_FLAG_ENCRYPTED = 1;
    private static final int BIT_FLAG_UNSUPPORTED_MASK = 1;
    private static final int CENTRAL_FILE_HEADER_SIGNATURE = 33639248;
    public static final int COMPRESSION_METHOD_DEFLATED = 8;
    public static final int COMPRESSION_METHOD_STORED = 0;
    private static final int END_OF_CENTRAL_DIRECTORY_SIGNATURE = 101010256;
    private static final int HEADER_ID_EXTENDED_TIMESTAMP = 21589;
    private static final int HEADER_ID_ZIP64_EXTENDED_INFO = 1;
    private static final int LOCAL_FILE_HEADER_SIGNATURE = 67324752;
    private static final long MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE = 4294967295L;
    private static final int ZIP64_EOCD_RECORD_SIGNATURE = 101075792;
    private static final int ZIP64_LOCATOR_SIGNATURE = 117853008;

    public static /* synthetic */ ZipFileSystem openZip$default(Path path, FileSystem fileSystem, Function1 function1, int i, Object obj) throws IOException {
        if ((i & 4) != 0) {
            function1 = ZipKt$openZip$1.INSTANCE;
        }
        return openZip(path, fileSystem, function1);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:100:?, code lost:
        kotlin.io.CloseableKt.closeFinally(r9, (java.lang.Throwable) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:101:0x01be, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:102:0x01bf, code lost:
        r6 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:103:0x01c1, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:104:0x01c2, code lost:
        r21 = r6;
        r29 = r7;
        r30 = r12;
        r6 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:106:?, code lost:
        throw r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:112:0x01d0, code lost:
        r21 = r6;
        r29 = r7;
        r30 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:113:0x01d6, code lost:
        r6 = new java.util.ArrayList();
        r9 = okio.Okio.buffer(r5.source(r7.getCentralDirectoryOffset()));
        r12 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:116:?, code lost:
        r0 = (okio.BufferedSource) r9;
        r13 = r7.getEntryCount();
        r22 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:117:0x01fd, code lost:
        if (0 >= r13) goto L_0x0243;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:118:0x01ff, code lost:
        r15 = r22;
        r22 = r22 + 1;
        r26 = readEntry(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:119:0x0215, code lost:
        if (r26.getOffset() >= r7.getCentralDirectoryOffset()) goto L_0x0237;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x0217, code lost:
        r19 = r0;
        r0 = r26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:121:0x0225, code lost:
        if (r3.invoke(r0).booleanValue() == false) goto L_0x022d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x0227, code lost:
        r6.add(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:124:0x022f, code lost:
        if (r22 < r13) goto L_0x0232;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:126:0x0232, code lost:
        r3 = r36;
        r0 = r19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:127:0x0237, code lost:
        r19 = r0;
        r0 = r26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:128:0x0242, code lost:
        throw new java.io.IOException("bad zip: local file header offset >= central directory offset");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:129:0x0243, code lost:
        r19 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:0x0245, code lost:
        r0 = kotlin.Unit.INSTANCE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:?, code lost:
        kotlin.io.CloseableKt.closeFinally(r9, (java.lang.Throwable) null);
        r3 = new okio.ZipFileSystem(r1, r2, buildIndex(r6), r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:134:0x0255, code lost:
        kotlin.io.CloseableKt.closeFinally(r4, (java.lang.Throwable) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:0x0259, code lost:
        return r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:136:0x025a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:137:0x025b, code lost:
        r3 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:139:?, code lost:
        throw r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:140:0x025d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:141:0x025e, code lost:
        r12 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:143:?, code lost:
        kotlin.io.CloseableKt.closeFinally(r9, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x0262, code lost:
        throw r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:145:0x0263, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:146:0x0264, code lost:
        r21 = r6;
        r29 = r7;
        r24 = r12;
        r3 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:147:0x026c, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:148:0x026d, code lost:
        r29 = r7;
        r24 = r12;
        r15 = r21;
        r21 = r6;
        r3 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:149:0x0277, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:150:0x0278, code lost:
        r24 = r12;
        r15 = r21;
        r21 = r6;
        r3 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00b1, code lost:
        r17 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00b7, code lost:
        r7 = readEocdRecord(r21);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00bc, code lost:
        r15 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00c3, code lost:
        r8 = r15.readUtf8((long) r7.getCommentByteCount());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:?, code lost:
        r15.close();
        r24 = r12;
        r12 = r17 - ((long) 20);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00d3, code lost:
        if (r12 <= 0) goto L_0x01d0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00d5, code lost:
        r9 = okio.Okio.buffer(r5.source(r12));
        r15 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:?, code lost:
        r15 = (okio.BufferedSource) r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00ee, code lost:
        r21 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00f3, code lost:
        if (r15.readIntLe() != ZIP64_LOCATOR_SIGNATURE) goto L_0x01b2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:?, code lost:
        r6 = r15.readIntLe();
        r28 = r15.readLongLe();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0104, code lost:
        r30 = r12;
        r12 = r15.readIntLe();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x010b, code lost:
        if (r12 != 1) goto L_0x0196;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x010d, code lost:
        if (r6 != 0) goto L_0x0196;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x010f, code lost:
        r16 = r12;
        r12 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:?, code lost:
        r26 = r6;
        r6 = okio.Okio.buffer(r5.source(r12));
        r20 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:?, code lost:
        r0 = (okio.BufferedSource) r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x012e, code lost:
        r32 = r12;
        r13 = r0.readIntLe();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x0137, code lost:
        if (r13 != ZIP64_EOCD_RECORD_SIGNATURE) goto L_0x014a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:?, code lost:
        r7 = readZip64EocdRecord(r0, r7);
        r0 = kotlin.Unit.INSTANCE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:?, code lost:
        kotlin.io.CloseableKt.closeFinally(r6, (java.lang.Throwable) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x0147, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0148, code lost:
        r12 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:?, code lost:
        r28 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0153, code lost:
        r29 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0179, code lost:
        throw new java.io.IOException("bad zip: expected " + getHex(ZIP64_EOCD_RECORD_SIGNATURE) + " but was " + getHex(r13));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x017a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x017b, code lost:
        r12 = r0;
        r7 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x017f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0180, code lost:
        r29 = r7;
        r12 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x0184, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x0185, code lost:
        r29 = r7;
        r32 = r12;
        r12 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:?, code lost:
        throw r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x018b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x018c, code lost:
        r13 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:?, code lost:
        kotlin.io.CloseableKt.closeFinally(r6, r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x0190, code lost:
        throw r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x0191, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x0192, code lost:
        r29 = r7;
        r6 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x0196, code lost:
        r26 = r6;
        r16 = r12;
        r32 = r28;
        r29 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x01a5, code lost:
        throw new java.io.IOException("unsupported zip: spanned");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x01a6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x01a7, code lost:
        r6 = r0;
        r7 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x01ab, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x01ac, code lost:
        r29 = r7;
        r30 = r12;
        r6 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x01b2, code lost:
        r29 = r7;
        r30 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:?, code lost:
        r0 = kotlin.Unit.INSTANCE;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:62:0x0142, B:78:0x018a] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final okio.ZipFileSystem openZip(okio.Path r34, okio.FileSystem r35, kotlin.jvm.functions.Function1<? super okio.internal.ZipEntry, java.lang.Boolean> r36) throws java.io.IOException {
        /*
            r1 = r34
            r2 = r35
            r3 = r36
            java.lang.String r0 = "zipPath"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r1, r0)
            java.lang.String r0 = "fileSystem"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r2, r0)
            java.lang.String r0 = "predicate"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r3, r0)
            okio.FileHandle r0 = r2.openReadOnly(r1)
            r4 = r0
            java.io.Closeable r4 = (java.io.Closeable) r4
            r0 = 0
            r5 = r0
            java.lang.Throwable r5 = (java.lang.Throwable) r5
            r5 = r4
            okio.FileHandle r5 = (okio.FileHandle) r5     // Catch:{ all -> 0x02d4 }
            r6 = 0
            r7 = 1
            r8 = 0
            okio.Source r10 = okio.FileHandle.source$default(r5, r8, r7, r0)     // Catch:{ all -> 0x02d4 }
            okio.BufferedSource r10 = okio.Okio.buffer((okio.Source) r10)     // Catch:{ all -> 0x02d4 }
            java.io.Closeable r10 = (java.io.Closeable) r10     // Catch:{ all -> 0x02d4 }
            r11 = r0
            java.lang.Throwable r11 = (java.lang.Throwable) r11     // Catch:{ all -> 0x02d4 }
            r11 = r10
            okio.BufferedSource r11 = (okio.BufferedSource) r11     // Catch:{ all -> 0x02c9 }
            r12 = 0
            int r13 = r11.readIntLe()     // Catch:{ all -> 0x02c9 }
            java.lang.String r14 = " but was "
            r15 = 101010256(0x6054b50, float:2.506985E-35)
            r7 = 67324752(0x4034b50, float:1.5433558E-36)
            if (r13 == r7) goto L_0x007f
            if (r13 != r15) goto L_0x0050
            java.io.IOException r0 = new java.io.IOException     // Catch:{ all -> 0x0079 }
            java.lang.String r7 = "unsupported zip: empty"
            r0.<init>(r7)     // Catch:{ all -> 0x0079 }
            throw r0     // Catch:{ all -> 0x0079 }
        L_0x0050:
            java.io.IOException r0 = new java.io.IOException     // Catch:{ all -> 0x0079 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0079 }
            r8.<init>()     // Catch:{ all -> 0x0079 }
            java.lang.String r9 = "not a zip: expected "
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ all -> 0x0079 }
            java.lang.String r7 = getHex(r7)     // Catch:{ all -> 0x0079 }
            java.lang.StringBuilder r7 = r8.append(r7)     // Catch:{ all -> 0x0079 }
            java.lang.StringBuilder r7 = r7.append(r14)     // Catch:{ all -> 0x0079 }
            java.lang.String r8 = getHex(r13)     // Catch:{ all -> 0x0079 }
            java.lang.StringBuilder r7 = r7.append(r8)     // Catch:{ all -> 0x0079 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0079 }
            r0.<init>(r7)     // Catch:{ all -> 0x0079 }
            throw r0     // Catch:{ all -> 0x0079 }
        L_0x0079:
            r0 = move-exception
            r3 = r0
            r21 = r6
            goto L_0x02cd
        L_0x007f:
            kotlin.Unit r7 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x02c9 }
            kotlin.io.CloseableKt.closeFinally(r10, r0)     // Catch:{ all -> 0x02d4 }
            long r10 = r5.size()     // Catch:{ all -> 0x02d4 }
            r7 = 22
            long r12 = (long) r7     // Catch:{ all -> 0x02d4 }
            long r10 = r10 - r12
            int r7 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
            if (r7 < 0) goto L_0x02b3
            r12 = 65536(0x10000, double:3.2379E-319)
            long r12 = r10 - r12
            long r12 = java.lang.Math.max(r12, r8)     // Catch:{ all -> 0x02d4 }
            r17 = 0
            r7 = 0
            r19 = 0
        L_0x009f:
            okio.Source r20 = r5.source(r10)     // Catch:{ all -> 0x02d4 }
            okio.BufferedSource r20 = okio.Okio.buffer((okio.Source) r20)     // Catch:{ all -> 0x02d4 }
            r21 = r20
            int r0 = r21.readIntLe()     // Catch:{ all -> 0x02a8 }
            if (r0 != r15) goto L_0x0280
            r17 = r10
            okio.internal.EocdRecord r0 = readEocdRecord(r21)     // Catch:{ all -> 0x0277 }
            r7 = r0
            int r0 = r7.getCommentByteCount()     // Catch:{ all -> 0x026c }
            long r8 = (long) r0
            r15 = r21
            java.lang.String r0 = r15.readUtf8(r8)     // Catch:{ all -> 0x0263 }
            r8 = r0
            r15.close()     // Catch:{ all -> 0x02d4 }
            r0 = 20
            r24 = r12
            long r12 = (long) r0     // Catch:{ all -> 0x02d4 }
            long r12 = r17 - r12
            r21 = 0
            int r0 = (r12 > r21 ? 1 : (r12 == r21 ? 0 : -1))
            if (r0 <= 0) goto L_0x01d0
            okio.Source r0 = r5.source(r12)     // Catch:{ all -> 0x02d4 }
            okio.BufferedSource r0 = okio.Okio.buffer((okio.Source) r0)     // Catch:{ all -> 0x02d4 }
            r9 = r0
            java.io.Closeable r9 = (java.io.Closeable) r9     // Catch:{ all -> 0x02d4 }
            r0 = 0
            r15 = r0
            java.lang.Throwable r15 = (java.lang.Throwable) r15     // Catch:{ all -> 0x02d4 }
            r0 = r9
            okio.BufferedSource r0 = (okio.BufferedSource) r0     // Catch:{ all -> 0x01c1 }
            r15 = r0
            r19 = 0
            int r0 = r15.readIntLe()     // Catch:{ all -> 0x01c1 }
            r21 = r6
            r6 = 117853008(0x7064b50, float:1.0103172E-34)
            if (r0 != r6) goto L_0x01b2
            int r0 = r15.readIntLe()     // Catch:{ all -> 0x01ab }
            r6 = r0
            long r26 = r15.readLongLe()     // Catch:{ all -> 0x01ab }
            r28 = r26
            int r0 = r15.readIntLe()     // Catch:{ all -> 0x01ab }
            r26 = r0
            r30 = r12
            r12 = r26
            r0 = 1
            if (r12 != r0) goto L_0x0196
            if (r6 != 0) goto L_0x0196
            r16 = r12
            r12 = r28
            okio.Source r0 = r5.source(r12)     // Catch:{ all -> 0x0191 }
            okio.BufferedSource r0 = okio.Okio.buffer((okio.Source) r0)     // Catch:{ all -> 0x0191 }
            r26 = r6
            r6 = r0
            java.io.Closeable r6 = (java.io.Closeable) r6     // Catch:{ all -> 0x0191 }
            r0 = 0
            r20 = r0
            java.lang.Throwable r20 = (java.lang.Throwable) r20     // Catch:{ all -> 0x0191 }
            r0 = r6
            okio.BufferedSource r0 = (okio.BufferedSource) r0     // Catch:{ all -> 0x0184 }
            r27 = 0
            int r28 = r0.readIntLe()     // Catch:{ all -> 0x0184 }
            r29 = r28
            r32 = r12
            r12 = 101075792(0x6064b50, float:2.525793E-35)
            r13 = r29
            if (r13 != r12) goto L_0x014a
            okio.internal.EocdRecord r12 = readZip64EocdRecord(r0, r7)     // Catch:{ all -> 0x0147 }
            r7 = r12
            kotlin.Unit r0 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x0147 }
            r0 = 0
            kotlin.io.CloseableKt.closeFinally(r6, r0)     // Catch:{ all -> 0x01be }
            goto L_0x01b6
        L_0x0147:
            r0 = move-exception
            r12 = r0
            goto L_0x018a
        L_0x014a:
            java.io.IOException r12 = new java.io.IOException     // Catch:{ all -> 0x017f }
            r28 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x017f }
            r0.<init>()     // Catch:{ all -> 0x017f }
            r29 = r7
            java.lang.String r7 = "bad zip: expected "
            java.lang.StringBuilder r0 = r0.append(r7)     // Catch:{ all -> 0x017a }
            r7 = 101075792(0x6064b50, float:2.525793E-35)
            java.lang.String r7 = getHex(r7)     // Catch:{ all -> 0x017a }
            java.lang.StringBuilder r0 = r0.append(r7)     // Catch:{ all -> 0x017a }
            java.lang.StringBuilder r0 = r0.append(r14)     // Catch:{ all -> 0x017a }
            java.lang.String r7 = getHex(r13)     // Catch:{ all -> 0x017a }
            java.lang.StringBuilder r0 = r0.append(r7)     // Catch:{ all -> 0x017a }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x017a }
            r12.<init>(r0)     // Catch:{ all -> 0x017a }
            throw r12     // Catch:{ all -> 0x017a }
        L_0x017a:
            r0 = move-exception
            r12 = r0
            r7 = r29
            goto L_0x018a
        L_0x017f:
            r0 = move-exception
            r29 = r7
            r12 = r0
            goto L_0x018a
        L_0x0184:
            r0 = move-exception
            r29 = r7
            r32 = r12
            r12 = r0
        L_0x018a:
            throw r12     // Catch:{ all -> 0x018b }
        L_0x018b:
            r0 = move-exception
            r13 = r0
            kotlin.io.CloseableKt.closeFinally(r6, r12)     // Catch:{ all -> 0x01be }
            throw r13     // Catch:{ all -> 0x01be }
        L_0x0191:
            r0 = move-exception
            r29 = r7
            r6 = r0
            goto L_0x01c9
        L_0x0196:
            r26 = r6
            r16 = r12
            r32 = r28
            r29 = r7
            java.io.IOException r0 = new java.io.IOException     // Catch:{ all -> 0x01a6 }
            java.lang.String r6 = "unsupported zip: spanned"
            r0.<init>(r6)     // Catch:{ all -> 0x01a6 }
            throw r0     // Catch:{ all -> 0x01a6 }
        L_0x01a6:
            r0 = move-exception
            r6 = r0
            r7 = r29
            goto L_0x01c9
        L_0x01ab:
            r0 = move-exception
            r29 = r7
            r30 = r12
            r6 = r0
            goto L_0x01c9
        L_0x01b2:
            r29 = r7
            r30 = r12
        L_0x01b6:
            kotlin.Unit r0 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x01be }
            r0 = 0
            kotlin.io.CloseableKt.closeFinally(r9, r0)     // Catch:{ all -> 0x02d4 }
            goto L_0x01d6
        L_0x01be:
            r0 = move-exception
            r6 = r0
            goto L_0x01c9
        L_0x01c1:
            r0 = move-exception
            r21 = r6
            r29 = r7
            r30 = r12
            r6 = r0
        L_0x01c9:
            throw r6     // Catch:{ all -> 0x01ca }
        L_0x01ca:
            r0 = move-exception
            r12 = r0
            kotlin.io.CloseableKt.closeFinally(r9, r6)     // Catch:{ all -> 0x02d4 }
            throw r12     // Catch:{ all -> 0x02d4 }
        L_0x01d0:
            r21 = r6
            r29 = r7
            r30 = r12
        L_0x01d6:
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ all -> 0x02d4 }
            r0.<init>()     // Catch:{ all -> 0x02d4 }
            java.util.List r0 = (java.util.List) r0     // Catch:{ all -> 0x02d4 }
            r6 = r0
            long r12 = r7.getCentralDirectoryOffset()     // Catch:{ all -> 0x02d4 }
            okio.Source r0 = r5.source(r12)     // Catch:{ all -> 0x02d4 }
            okio.BufferedSource r0 = okio.Okio.buffer((okio.Source) r0)     // Catch:{ all -> 0x02d4 }
            r9 = r0
            java.io.Closeable r9 = (java.io.Closeable) r9     // Catch:{ all -> 0x02d4 }
            r0 = 0
            r12 = r0
            java.lang.Throwable r12 = (java.lang.Throwable) r12     // Catch:{ all -> 0x02d4 }
            r0 = r9
            okio.BufferedSource r0 = (okio.BufferedSource) r0     // Catch:{ all -> 0x025a }
            r12 = 0
            long r13 = r7.getEntryCount()     // Catch:{ all -> 0x025a }
            r22 = 0
            int r15 = (r22 > r13 ? 1 : (r22 == r13 ? 0 : -1))
            if (r15 >= 0) goto L_0x0243
        L_0x01ff:
            r15 = r22
            r26 = 1
            long r22 = r22 + r26
            okio.internal.ZipEntry r19 = readEntry(r0)     // Catch:{ all -> 0x025a }
            r26 = r19
            long r27 = r26.getOffset()     // Catch:{ all -> 0x025a }
            long r32 = r7.getCentralDirectoryOffset()     // Catch:{ all -> 0x025a }
            int r19 = (r27 > r32 ? 1 : (r27 == r32 ? 0 : -1))
            if (r19 >= 0) goto L_0x0237
            r19 = r0
            r0 = r26
            java.lang.Object r26 = r3.invoke(r0)     // Catch:{ all -> 0x025a }
            java.lang.Boolean r26 = (java.lang.Boolean) r26     // Catch:{ all -> 0x025a }
            boolean r26 = r26.booleanValue()     // Catch:{ all -> 0x025a }
            if (r26 == 0) goto L_0x022d
            r3 = r6
            java.util.Collection r3 = (java.util.Collection) r3     // Catch:{ all -> 0x025a }
            r3.add(r0)     // Catch:{ all -> 0x025a }
        L_0x022d:
            int r0 = (r22 > r13 ? 1 : (r22 == r13 ? 0 : -1))
            if (r0 < 0) goto L_0x0232
            goto L_0x0245
        L_0x0232:
            r3 = r36
            r0 = r19
            goto L_0x01ff
        L_0x0237:
            r19 = r0
            r0 = r26
            java.io.IOException r3 = new java.io.IOException     // Catch:{ all -> 0x025a }
            java.lang.String r13 = "bad zip: local file header offset >= central directory offset"
            r3.<init>(r13)     // Catch:{ all -> 0x025a }
            throw r3     // Catch:{ all -> 0x025a }
        L_0x0243:
            r19 = r0
        L_0x0245:
            kotlin.Unit r0 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x025a }
            r0 = 0
            kotlin.io.CloseableKt.closeFinally(r9, r0)     // Catch:{ all -> 0x02d4 }
            java.util.Map r0 = buildIndex(r6)     // Catch:{ all -> 0x02d4 }
            okio.ZipFileSystem r3 = new okio.ZipFileSystem     // Catch:{ all -> 0x02d4 }
            r3.<init>(r1, r2, r0, r8)     // Catch:{ all -> 0x02d4 }
            r6 = 0
            kotlin.io.CloseableKt.closeFinally(r4, r6)
            return r3
        L_0x025a:
            r0 = move-exception
            r3 = r0
            throw r3     // Catch:{ all -> 0x025d }
        L_0x025d:
            r0 = move-exception
            r12 = r0
            kotlin.io.CloseableKt.closeFinally(r9, r3)     // Catch:{ all -> 0x02d4 }
            throw r12     // Catch:{ all -> 0x02d4 }
        L_0x0263:
            r0 = move-exception
            r21 = r6
            r29 = r7
            r24 = r12
            r3 = r15
            goto L_0x02af
        L_0x026c:
            r0 = move-exception
            r29 = r7
            r24 = r12
            r15 = r21
            r21 = r6
            r3 = r15
            goto L_0x02af
        L_0x0277:
            r0 = move-exception
            r24 = r12
            r15 = r21
            r21 = r6
            r3 = r15
            goto L_0x02af
        L_0x0280:
            r22 = r8
            r24 = r12
            r3 = r21
            r0 = 1
            r21 = r6
            r6 = 0
            r3.close()     // Catch:{ all -> 0x02d4 }
            r8 = -1
            long r10 = r10 + r8
            int r8 = (r10 > r24 ? 1 : (r10 == r24 ? 0 : -1))
            if (r8 < 0) goto L_0x02a0
            r3 = r36
            r0 = r6
            r6 = r21
            r8 = r22
            r12 = r24
            goto L_0x009f
        L_0x02a0:
            java.io.IOException r0 = new java.io.IOException     // Catch:{ all -> 0x02d4 }
            java.lang.String r6 = "not a zip: end of central directory signature not found"
            r0.<init>(r6)     // Catch:{ all -> 0x02d4 }
            throw r0     // Catch:{ all -> 0x02d4 }
        L_0x02a8:
            r0 = move-exception
            r24 = r12
            r3 = r21
            r21 = r6
        L_0x02af:
            r3.close()     // Catch:{ all -> 0x02d4 }
            throw r0     // Catch:{ all -> 0x02d4 }
        L_0x02b3:
            r21 = r6
            java.io.IOException r0 = new java.io.IOException     // Catch:{ all -> 0x02d4 }
            java.lang.String r3 = "not a zip: size="
            long r6 = r5.size()     // Catch:{ all -> 0x02d4 }
            java.lang.Long r6 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x02d4 }
            java.lang.String r3 = kotlin.jvm.internal.Intrinsics.stringPlus(r3, r6)     // Catch:{ all -> 0x02d4 }
            r0.<init>(r3)     // Catch:{ all -> 0x02d4 }
            throw r0     // Catch:{ all -> 0x02d4 }
        L_0x02c9:
            r0 = move-exception
            r21 = r6
            r3 = r0
        L_0x02cd:
            throw r3     // Catch:{ all -> 0x02ce }
        L_0x02ce:
            r0 = move-exception
            r6 = r0
            kotlin.io.CloseableKt.closeFinally(r10, r3)     // Catch:{ all -> 0x02d4 }
            throw r6     // Catch:{ all -> 0x02d4 }
        L_0x02d4:
            r0 = move-exception
            r3 = r0
            throw r3     // Catch:{ all -> 0x02d7 }
        L_0x02d7:
            r0 = move-exception
            r5 = r0
            kotlin.io.CloseableKt.closeFinally(r4, r3)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.internal.ZipKt.openZip(okio.Path, okio.FileSystem, kotlin.jvm.functions.Function1):okio.ZipFileSystem");
    }

    private static final Map<Path, ZipEntry> buildIndex(List<ZipEntry> entries) {
        Map result = new LinkedHashMap();
        for (ZipEntry entry : CollectionsKt.sortedWith(entries, new ZipKt$buildIndex$$inlined$sortedBy$1())) {
            if (result.put(entry.getCanonicalPath(), entry) == null) {
                ZipEntry child = entry;
                while (true) {
                    Path parentPath = child.getCanonicalPath().parent();
                    if (parentPath == null) {
                        break;
                    }
                    ZipEntry parentEntry = result.get(parentPath);
                    if (parentEntry != null) {
                        parentEntry.getChildren().add(child.getCanonicalPath());
                        break;
                    }
                    ZipEntry parentEntry2 = new ZipEntry(parentPath, true, (String) null, 0, 0, 0, 0, (Long) null, 0, TypedValues.PositionType.TYPE_CURVE_FIT, (DefaultConstructorMarker) null);
                    result.put(parentPath, parentEntry2);
                    parentEntry2.getChildren().add(child.getCanonicalPath());
                    child = parentEntry2;
                }
            }
        }
        return result;
    }

    public static final ZipEntry readEntry(BufferedSource $this$readEntry) throws IOException {
        Ref.LongRef offset;
        BufferedSource bufferedSource = $this$readEntry;
        Intrinsics.checkNotNullParameter(bufferedSource, "<this>");
        int signature = $this$readEntry.readIntLe();
        if (signature == CENTRAL_FILE_HEADER_SIGNATURE) {
            bufferedSource.skip(4);
            int bitFlag = $this$readEntry.readShortLe() & 65535;
            if ((bitFlag & 1) == 0) {
                int compressionMethod = $this$readEntry.readShortLe() & 65535;
                short readShortLe = $this$readEntry.readShortLe() & UShort.MAX_VALUE;
                short readShortLe2 = $this$readEntry.readShortLe() & UShort.MAX_VALUE;
                Long lastModifiedAtMillis = dosDateTimeToEpochMillis(readShortLe2, readShortLe);
                long crc = ((long) $this$readEntry.readIntLe()) & MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE;
                Ref.LongRef compressedSize = new Ref.LongRef();
                compressedSize.element = ((long) $this$readEntry.readIntLe()) & MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE;
                Ref.LongRef size = new Ref.LongRef();
                size.element = ((long) $this$readEntry.readIntLe()) & MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE;
                short readShortLe3 = $this$readEntry.readShortLe() & UShort.MAX_VALUE;
                short readShortLe4 = $this$readEntry.readShortLe() & UShort.MAX_VALUE;
                short readShortLe5 = $this$readEntry.readShortLe() & UShort.MAX_VALUE;
                bufferedSource.skip(8);
                Ref.LongRef offset2 = new Ref.LongRef();
                offset2.element = ((long) $this$readEntry.readIntLe()) & MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE;
                String name = bufferedSource.readUtf8((long) readShortLe3);
                if (!StringsKt.contains$default((CharSequence) name, 0, false, 2, (Object) null)) {
                    BufferedSource bufferedSource2 = $this$readEntry;
                    long result = 0;
                    if (size.element == MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE) {
                        offset = offset2;
                        result = 0 + ((long) 8);
                    } else {
                        offset = offset2;
                    }
                    if (compressedSize.element == MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE) {
                        result += (long) 8;
                    }
                    if (offset.element == MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE) {
                        result += (long) 8;
                    }
                    long requiredZip64ExtraSize = result;
                    Ref.BooleanRef hasZip64Extra = new Ref.BooleanRef();
                    int i = signature;
                    short s = readShortLe3;
                    String name2 = name;
                    Ref.LongRef offset3 = offset;
                    Ref.BooleanRef hasZip64Extra2 = hasZip64Extra;
                    short s2 = readShortLe2;
                    short s3 = readShortLe5;
                    int i2 = bitFlag;
                    short s4 = readShortLe4;
                    readExtra(bufferedSource, s4, new ZipKt$readEntry$1(hasZip64Extra, requiredZip64ExtraSize, size, $this$readEntry, compressedSize, offset3));
                    if (requiredZip64ExtraSize <= 0 || hasZip64Extra2.element) {
                        String comment = bufferedSource.readUtf8((long) s3);
                        short s5 = s4;
                        Ref.LongRef offset4 = offset3;
                        String str = name2;
                        Ref.BooleanRef booleanRef = hasZip64Extra2;
                        short s6 = s;
                        Ref.LongRef longRef = size;
                        Ref.LongRef longRef2 = compressedSize;
                        short s7 = s3;
                        short s8 = s2;
                        short s9 = readShortLe;
                        return new ZipEntry(Path.Companion.get$default(Path.Companion, "/", false, 1, (Object) null).resolve(name2), StringsKt.endsWith$default(name2, "/", false, 2, (Object) null), comment, crc, compressedSize.element, size.element, compressionMethod, lastModifiedAtMillis, offset4.element);
                    }
                    throw new IOException("bad zip: zip64 extra required but absent");
                }
                throw new IOException("bad zip: filename contains 0x00");
            }
            throw new IOException(Intrinsics.stringPlus("unsupported zip: general purpose bit flag=", getHex(bitFlag)));
        }
        throw new IOException("bad zip: expected " + getHex(CENTRAL_FILE_HEADER_SIGNATURE) + " but was " + getHex(signature));
    }

    private static final EocdRecord readEocdRecord(BufferedSource $this$readEocdRecord) throws IOException {
        int diskNumber = $this$readEocdRecord.readShortLe() & 65535;
        int diskWithCentralDir = $this$readEocdRecord.readShortLe() & 65535;
        long entryCount = (long) ($this$readEocdRecord.readShortLe() & UShort.MAX_VALUE);
        if (entryCount == ((long) ($this$readEocdRecord.readShortLe() & UShort.MAX_VALUE)) && diskNumber == 0 && diskWithCentralDir == 0) {
            $this$readEocdRecord.skip(4);
            return new EocdRecord(entryCount, ((long) $this$readEocdRecord.readIntLe()) & MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE, 65535 & $this$readEocdRecord.readShortLe());
        }
        BufferedSource bufferedSource = $this$readEocdRecord;
        throw new IOException("unsupported zip: spanned");
    }

    private static final EocdRecord readZip64EocdRecord(BufferedSource $this$readZip64EocdRecord, EocdRecord regularRecord) throws IOException {
        BufferedSource bufferedSource = $this$readZip64EocdRecord;
        bufferedSource.skip(12);
        int diskNumber = $this$readZip64EocdRecord.readIntLe();
        int diskWithCentralDirStart = $this$readZip64EocdRecord.readIntLe();
        long entryCount = $this$readZip64EocdRecord.readLongLe();
        if (entryCount == $this$readZip64EocdRecord.readLongLe() && diskNumber == 0 && diskWithCentralDirStart == 0) {
            bufferedSource.skip(8);
            return new EocdRecord(entryCount, $this$readZip64EocdRecord.readLongLe(), regularRecord.getCommentByteCount());
        }
        throw new IOException("unsupported zip: spanned");
    }

    private static final void readExtra(BufferedSource $this$readExtra, int extraSize, Function2<? super Integer, ? super Long, Unit> block) {
        long remaining = (long) extraSize;
        while (remaining != 0) {
            if (remaining >= 4) {
                int headerId = $this$readExtra.readShortLe() & 65535;
                long dataSize = ((long) $this$readExtra.readShortLe()) & WebSocketProtocol.PAYLOAD_SHORT_MAX;
                long remaining2 = remaining - ((long) 4);
                if (remaining2 >= dataSize) {
                    $this$readExtra.require(dataSize);
                    long sizeBefore = $this$readExtra.getBuffer().size();
                    block.invoke(Integer.valueOf(headerId), Long.valueOf(dataSize));
                    long fieldRemaining = ($this$readExtra.getBuffer().size() + dataSize) - sizeBefore;
                    if (fieldRemaining >= 0) {
                        if (fieldRemaining > 0) {
                            $this$readExtra.getBuffer().skip(fieldRemaining);
                        }
                        remaining = remaining2 - dataSize;
                    } else {
                        throw new IOException(Intrinsics.stringPlus("unsupported zip: too many bytes processed for ", Integer.valueOf(headerId)));
                    }
                } else {
                    throw new IOException("bad zip: truncated value in extra field");
                }
            } else {
                throw new IOException("bad zip: truncated header in extra field");
            }
        }
    }

    public static final void skipLocalHeader(BufferedSource $this$skipLocalHeader) {
        Intrinsics.checkNotNullParameter($this$skipLocalHeader, "<this>");
        readOrSkipLocalHeader($this$skipLocalHeader, (FileMetadata) null);
    }

    public static final FileMetadata readLocalHeader(BufferedSource $this$readLocalHeader, FileMetadata basicMetadata) {
        Intrinsics.checkNotNullParameter($this$readLocalHeader, "<this>");
        Intrinsics.checkNotNullParameter(basicMetadata, "basicMetadata");
        FileMetadata readOrSkipLocalHeader = readOrSkipLocalHeader($this$readLocalHeader, basicMetadata);
        Intrinsics.checkNotNull(readOrSkipLocalHeader);
        return readOrSkipLocalHeader;
    }

    private static final FileMetadata readOrSkipLocalHeader(BufferedSource $this$readOrSkipLocalHeader, FileMetadata basicMetadata) {
        BufferedSource bufferedSource = $this$readOrSkipLocalHeader;
        Ref.ObjectRef lastModifiedAtMillis = new Ref.ObjectRef();
        lastModifiedAtMillis.element = basicMetadata == null ? null : basicMetadata.getLastModifiedAtMillis();
        Ref.ObjectRef lastAccessedAtMillis = new Ref.ObjectRef();
        Ref.ObjectRef createdAtMillis = new Ref.ObjectRef();
        int signature = $this$readOrSkipLocalHeader.readIntLe();
        if (signature == LOCAL_FILE_HEADER_SIGNATURE) {
            bufferedSource.skip(2);
            int bitFlag = $this$readOrSkipLocalHeader.readShortLe() & 65535;
            if ((bitFlag & 1) == 0) {
                bufferedSource.skip(18);
                long fileNameLength = ((long) $this$readOrSkipLocalHeader.readShortLe()) & WebSocketProtocol.PAYLOAD_SHORT_MAX;
                int extraSize = 65535 & $this$readOrSkipLocalHeader.readShortLe();
                bufferedSource.skip(fileNameLength);
                if (basicMetadata == null) {
                    bufferedSource.skip((long) extraSize);
                    return null;
                }
                readExtra(bufferedSource, extraSize, new ZipKt$readOrSkipLocalHeader$1(bufferedSource, lastModifiedAtMillis, lastAccessedAtMillis, createdAtMillis));
                return new FileMetadata(basicMetadata.isRegularFile(), basicMetadata.isDirectory(), (Path) null, basicMetadata.getSize(), (Long) createdAtMillis.element, (Long) lastModifiedAtMillis.element, (Long) lastAccessedAtMillis.element, (Map) null, 128, (DefaultConstructorMarker) null);
            }
            throw new IOException(Intrinsics.stringPlus("unsupported zip: general purpose bit flag=", getHex(bitFlag)));
        }
        throw new IOException("bad zip: expected " + getHex(LOCAL_FILE_HEADER_SIGNATURE) + " but was " + getHex(signature));
    }

    private static final Long dosDateTimeToEpochMillis(int date, int time) {
        if (time == -1) {
            return null;
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(14, 0);
        int hour = (time >> 11) & 31;
        int minute = (time >> 5) & 63;
        int second = (time & 31) << 1;
        GregorianCalendar gregorianCalendar = cal;
        int i = ((date >> 9) & 127) + 1980;
        gregorianCalendar.set(i, ((date >> 5) & 15) - 1, date & 31, hour, minute, second);
        return Long.valueOf(cal.getTime().getTime());
    }

    private static final String getHex(int $this$hex) {
        String num = Integer.toString($this$hex, CharsKt.checkRadix(16));
        Intrinsics.checkNotNullExpressionValue(num, "java.lang.Integer.toStri…(this, checkRadix(radix))");
        return Intrinsics.stringPlus("0x", num);
    }
}

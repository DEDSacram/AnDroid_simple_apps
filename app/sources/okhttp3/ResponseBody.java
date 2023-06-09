package okhttp3;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.Unit;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;

@Metadata(d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b&\u0018\u0000 !2\u00020\u0001:\u0002 !B\u0005¢\u0006\u0002\u0010\u0002J\u0006\u0010\u0005\u001a\u00020\u0006J\u0006\u0010\u0007\u001a\u00020\bJ\u0006\u0010\t\u001a\u00020\nJ\u0006\u0010\u000b\u001a\u00020\u0004J\b\u0010\f\u001a\u00020\rH\u0002J\b\u0010\u000e\u001a\u00020\u000fH\u0016J@\u0010\u0010\u001a\u0002H\u0011\"\b\b\u0000\u0010\u0011*\u00020\u00122\u0012\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u0002H\u00110\u00142\u0012\u0010\u0016\u001a\u000e\u0012\u0004\u0012\u0002H\u0011\u0012\u0004\u0012\u00020\u00170\u0014H\b¢\u0006\u0002\u0010\u0018J\b\u0010\u0019\u001a\u00020\u001aH&J\n\u0010\u001b\u001a\u0004\u0018\u00010\u001cH&J\b\u0010\u001d\u001a\u00020\u0015H&J\u0006\u0010\u001e\u001a\u00020\u001fR\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u000e¢\u0006\u0002\n\u0000¨\u0006\""}, d2 = {"Lokhttp3/ResponseBody;", "Ljava/io/Closeable;", "()V", "reader", "Ljava/io/Reader;", "byteStream", "Ljava/io/InputStream;", "byteString", "Lokio/ByteString;", "bytes", "", "charStream", "charset", "Ljava/nio/charset/Charset;", "close", "", "consumeSource", "T", "", "consumer", "Lkotlin/Function1;", "Lokio/BufferedSource;", "sizeMapper", "", "(Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "contentLength", "", "contentType", "Lokhttp3/MediaType;", "source", "string", "", "BomAwareReader", "Companion", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: ResponseBody.kt */
public abstract class ResponseBody implements Closeable {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    private Reader reader;

    @JvmStatic
    public static final ResponseBody create(String str, MediaType mediaType) {
        return Companion.create(str, mediaType);
    }

    @JvmStatic
    @Deprecated(level = DeprecationLevel.WARNING, message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.asResponseBody(contentType, contentLength)", imports = {"okhttp3.ResponseBody.Companion.asResponseBody"}))
    public static final ResponseBody create(MediaType mediaType, long j, BufferedSource bufferedSource) {
        return Companion.create(mediaType, j, bufferedSource);
    }

    @JvmStatic
    @Deprecated(level = DeprecationLevel.WARNING, message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.toResponseBody(contentType)", imports = {"okhttp3.ResponseBody.Companion.toResponseBody"}))
    public static final ResponseBody create(MediaType mediaType, String str) {
        return Companion.create(mediaType, str);
    }

    @JvmStatic
    @Deprecated(level = DeprecationLevel.WARNING, message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.toResponseBody(contentType)", imports = {"okhttp3.ResponseBody.Companion.toResponseBody"}))
    public static final ResponseBody create(MediaType mediaType, ByteString byteString) {
        return Companion.create(mediaType, byteString);
    }

    @JvmStatic
    @Deprecated(level = DeprecationLevel.WARNING, message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.toResponseBody(contentType)", imports = {"okhttp3.ResponseBody.Companion.toResponseBody"}))
    public static final ResponseBody create(MediaType mediaType, byte[] bArr) {
        return Companion.create(mediaType, bArr);
    }

    @JvmStatic
    public static final ResponseBody create(BufferedSource bufferedSource, MediaType mediaType, long j) {
        return Companion.create(bufferedSource, mediaType, j);
    }

    @JvmStatic
    public static final ResponseBody create(ByteString byteString, MediaType mediaType) {
        return Companion.create(byteString, mediaType);
    }

    @JvmStatic
    public static final ResponseBody create(byte[] bArr, MediaType mediaType) {
        return Companion.create(bArr, mediaType);
    }

    public abstract long contentLength();

    public abstract MediaType contentType();

    public abstract BufferedSource source();

    public final InputStream byteStream() {
        return source().inputStream();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x005c, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x005d, code lost:
        kotlin.io.CloseableKt.closeFinally(r4, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0060, code lost:
        throw r6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final byte[] bytes() throws java.io.IOException {
        /*
            r9 = this;
            r0 = r9
            r1 = 0
            long r2 = r0.contentLength()
            r4 = 2147483647(0x7fffffff, double:1.060997895E-314)
            int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r4 > 0) goto L_0x0061
            okio.BufferedSource r4 = r0.source()
            java.io.Closeable r4 = (java.io.Closeable) r4
            r5 = 0
            r6 = r4
            okio.BufferedSource r6 = (okio.BufferedSource) r6     // Catch:{ all -> 0x005a }
            r7 = 0
            byte[] r8 = r6.readByteArray()     // Catch:{ all -> 0x005a }
            kotlin.io.CloseableKt.closeFinally(r4, r5)
            r4 = r8
            r5 = r4
            r6 = 0
            int r5 = r5.length
            r6 = -1
            int r6 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r6 == 0) goto L_0x0058
            long r6 = (long) r5
            int r6 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r6 != 0) goto L_0x002f
            goto L_0x0058
        L_0x002f:
            java.io.IOException r6 = new java.io.IOException
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "Content-Length ("
            java.lang.StringBuilder r7 = r7.append(r8)
            java.lang.StringBuilder r7 = r7.append(r2)
            java.lang.String r8 = ") and stream length ("
            java.lang.StringBuilder r7 = r7.append(r8)
            java.lang.StringBuilder r7 = r7.append(r5)
            java.lang.String r8 = ") disagree"
            java.lang.StringBuilder r7 = r7.append(r8)
            java.lang.String r7 = r7.toString()
            r6.<init>(r7)
            throw r6
        L_0x0058:
            return r4
        L_0x005a:
            r5 = move-exception
            throw r5     // Catch:{ all -> 0x005c }
        L_0x005c:
            r6 = move-exception
            kotlin.io.CloseableKt.closeFinally(r4, r5)
            throw r6
        L_0x0061:
            java.io.IOException r4 = new java.io.IOException
            java.lang.Long r5 = java.lang.Long.valueOf(r2)
            java.lang.String r6 = "Cannot buffer entire body for content length: "
            java.lang.String r5 = kotlin.jvm.internal.Intrinsics.stringPlus(r6, r5)
            r4.<init>(r5)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.ResponseBody.bytes():byte[]");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x005f, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0060, code lost:
        kotlin.io.CloseableKt.closeFinally(r4, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0063, code lost:
        throw r6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final okio.ByteString byteString() throws java.io.IOException {
        /*
            r9 = this;
            r0 = r9
            r1 = 0
            long r2 = r0.contentLength()
            r4 = 2147483647(0x7fffffff, double:1.060997895E-314)
            int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r4 > 0) goto L_0x0064
            okio.BufferedSource r4 = r0.source()
            java.io.Closeable r4 = (java.io.Closeable) r4
            r5 = 0
            r6 = r4
            okio.BufferedSource r6 = (okio.BufferedSource) r6     // Catch:{ all -> 0x005d }
            r7 = 0
            okio.ByteString r8 = r6.readByteString()     // Catch:{ all -> 0x005d }
            kotlin.io.CloseableKt.closeFinally(r4, r5)
            r4 = r8
            r5 = r4
            r6 = 0
            int r5 = r5.size()
            r6 = -1
            int r6 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r6 == 0) goto L_0x005b
            long r6 = (long) r5
            int r6 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r6 != 0) goto L_0x0032
            goto L_0x005b
        L_0x0032:
            java.io.IOException r6 = new java.io.IOException
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "Content-Length ("
            java.lang.StringBuilder r7 = r7.append(r8)
            java.lang.StringBuilder r7 = r7.append(r2)
            java.lang.String r8 = ") and stream length ("
            java.lang.StringBuilder r7 = r7.append(r8)
            java.lang.StringBuilder r7 = r7.append(r5)
            java.lang.String r8 = ") disagree"
            java.lang.StringBuilder r7 = r7.append(r8)
            java.lang.String r7 = r7.toString()
            r6.<init>(r7)
            throw r6
        L_0x005b:
            return r4
        L_0x005d:
            r5 = move-exception
            throw r5     // Catch:{ all -> 0x005f }
        L_0x005f:
            r6 = move-exception
            kotlin.io.CloseableKt.closeFinally(r4, r5)
            throw r6
        L_0x0064:
            java.io.IOException r4 = new java.io.IOException
            java.lang.Long r5 = java.lang.Long.valueOf(r2)
            java.lang.String r6 = "Cannot buffer entire body for content length: "
            java.lang.String r5 = kotlin.jvm.internal.Intrinsics.stringPlus(r6, r5)
            r4.<init>(r5)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.ResponseBody.byteString():okio.ByteString");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0064, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0065, code lost:
        kotlin.jvm.internal.InlineMarker.finallyStart(1);
        kotlin.io.CloseableKt.closeFinally(r3, r4);
        kotlin.jvm.internal.InlineMarker.finallyEnd(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x006e, code lost:
        throw r6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final <T> T consumeSource(kotlin.jvm.functions.Function1<? super okio.BufferedSource, ? extends T> r9, kotlin.jvm.functions.Function1<? super T, java.lang.Integer> r10) {
        /*
            r8 = this;
            r0 = 0
            long r1 = r8.contentLength()
            r3 = 2147483647(0x7fffffff, double:1.060997895E-314)
            int r3 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r3 > 0) goto L_0x006f
            okio.BufferedSource r3 = r8.source()
            java.io.Closeable r3 = (java.io.Closeable) r3
            r4 = 0
            r5 = 1
            java.lang.Object r6 = r9.invoke(r3)     // Catch:{ all -> 0x0062 }
            kotlin.jvm.internal.InlineMarker.finallyStart(r5)
            kotlin.io.CloseableKt.closeFinally(r3, r4)
            kotlin.jvm.internal.InlineMarker.finallyEnd(r5)
            r3 = r6
            java.lang.Object r4 = r10.invoke(r3)
            java.lang.Number r4 = (java.lang.Number) r4
            int r4 = r4.intValue()
            r5 = -1
            int r5 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r5 == 0) goto L_0x0061
            long r5 = (long) r4
            int r5 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r5 != 0) goto L_0x0038
            goto L_0x0061
        L_0x0038:
            java.io.IOException r5 = new java.io.IOException
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Content-Length ("
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.StringBuilder r6 = r6.append(r1)
            java.lang.String r7 = ") and stream length ("
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.StringBuilder r6 = r6.append(r4)
            java.lang.String r7 = ") disagree"
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r6 = r6.toString()
            r5.<init>(r6)
            throw r5
        L_0x0061:
            return r3
        L_0x0062:
            r4 = move-exception
            throw r4     // Catch:{ all -> 0x0064 }
        L_0x0064:
            r6 = move-exception
            kotlin.jvm.internal.InlineMarker.finallyStart(r5)
            kotlin.io.CloseableKt.closeFinally(r3, r4)
            kotlin.jvm.internal.InlineMarker.finallyEnd(r5)
            throw r6
        L_0x006f:
            java.io.IOException r3 = new java.io.IOException
            java.lang.Long r4 = java.lang.Long.valueOf(r1)
            java.lang.String r5 = "Cannot buffer entire body for content length: "
            java.lang.String r4 = kotlin.jvm.internal.Intrinsics.stringPlus(r5, r4)
            r3.<init>(r4)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.ResponseBody.consumeSource(kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1):java.lang.Object");
    }

    public final Reader charStream() {
        Reader reader2 = this.reader;
        if (reader2 != null) {
            return reader2;
        }
        BomAwareReader it = new BomAwareReader(source(), charset());
        this.reader = it;
        return it;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0021, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x001d, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x001e, code lost:
        kotlin.io.CloseableKt.closeFinally(r0, r1);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final java.lang.String string() throws java.io.IOException {
        /*
            r4 = this;
            okio.BufferedSource r0 = r4.source()
            java.io.Closeable r0 = (java.io.Closeable) r0
            r1 = r0
            okio.BufferedSource r1 = (okio.BufferedSource) r1     // Catch:{ all -> 0x001b }
            r2 = 0
            java.nio.charset.Charset r3 = r4.charset()     // Catch:{ all -> 0x001b }
            java.nio.charset.Charset r3 = okhttp3.internal.Util.readBomAsCharset(r1, r3)     // Catch:{ all -> 0x001b }
            java.lang.String r3 = r1.readString(r3)     // Catch:{ all -> 0x001b }
            r1 = 0
            kotlin.io.CloseableKt.closeFinally(r0, r1)
            return r3
        L_0x001b:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x001d }
        L_0x001d:
            r2 = move-exception
            kotlin.io.CloseableKt.closeFinally(r0, r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.ResponseBody.string():java.lang.String");
    }

    private final Charset charset() {
        MediaType contentType = contentType();
        Charset charset = contentType == null ? null : contentType.charset(Charsets.UTF_8);
        return charset == null ? Charsets.UTF_8 : charset;
    }

    public void close() {
        Util.closeQuietly((Closeable) source());
    }

    @Metadata(d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0019\n\u0002\b\u0003\b\u0000\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\b\u0010\n\u001a\u00020\u000bH\u0016J \u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\r2\u0006\u0010\u0011\u001a\u00020\rH\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\u0001X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000¨\u0006\u0012"}, d2 = {"Lokhttp3/ResponseBody$BomAwareReader;", "Ljava/io/Reader;", "source", "Lokio/BufferedSource;", "charset", "Ljava/nio/charset/Charset;", "(Lokio/BufferedSource;Ljava/nio/charset/Charset;)V", "closed", "", "delegate", "close", "", "read", "", "cbuf", "", "off", "len", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: ResponseBody.kt */
    public static final class BomAwareReader extends Reader {
        private final Charset charset;
        private boolean closed;
        private Reader delegate;
        private final BufferedSource source;

        public BomAwareReader(BufferedSource source2, Charset charset2) {
            Intrinsics.checkNotNullParameter(source2, "source");
            Intrinsics.checkNotNullParameter(charset2, "charset");
            this.source = source2;
            this.charset = charset2;
        }

        public int read(char[] cbuf, int off, int len) throws IOException {
            Intrinsics.checkNotNullParameter(cbuf, "cbuf");
            if (!this.closed) {
                Reader finalDelegate = this.delegate;
                if (finalDelegate == null) {
                    InputStreamReader it = new InputStreamReader(this.source.inputStream(), Util.readBomAsCharset(this.source, this.charset));
                    this.delegate = it;
                    finalDelegate = it;
                }
                return finalDelegate.read(cbuf, off, len);
            }
            throw new IOException("Stream closed");
        }

        public void close() throws IOException {
            Unit unit;
            this.closed = true;
            Reader reader = this.delegate;
            if (reader == null) {
                unit = null;
            } else {
                reader.close();
                unit = Unit.INSTANCE;
            }
            if (unit == null) {
                this.source.close();
            }
        }
    }

    @Metadata(d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u001a\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0007J\"\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0007\u001a\u00020\u000bH\u0007J\u001a\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0007\u001a\u00020\fH\u0007J\u001a\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0007\u001a\u00020\rH\u0007J'\u0010\u000e\u001a\u00020\u0004*\u00020\u000b2\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00062\b\b\u0002\u0010\t\u001a\u00020\nH\u0007¢\u0006\u0002\b\u0003J\u001d\u0010\u000f\u001a\u00020\u0004*\u00020\b2\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u0007¢\u0006\u0002\b\u0003J\u001d\u0010\u000f\u001a\u00020\u0004*\u00020\f2\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u0007¢\u0006\u0002\b\u0003J\u001d\u0010\u000f\u001a\u00020\u0004*\u00020\r2\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u0007¢\u0006\u0002\b\u0003¨\u0006\u0010"}, d2 = {"Lokhttp3/ResponseBody$Companion;", "", "()V", "create", "Lokhttp3/ResponseBody;", "contentType", "Lokhttp3/MediaType;", "content", "", "contentLength", "", "Lokio/BufferedSource;", "", "Lokio/ByteString;", "asResponseBody", "toResponseBody", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: ResponseBody.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public static /* synthetic */ ResponseBody create$default(Companion companion, String str, MediaType mediaType, int i, Object obj) {
            if ((i & 1) != 0) {
                mediaType = null;
            }
            return companion.create(str, mediaType);
        }

        @JvmStatic
        public final ResponseBody create(String $this$toResponseBody, MediaType contentType) {
            Intrinsics.checkNotNullParameter($this$toResponseBody, "<this>");
            Charset charset = Charsets.UTF_8;
            MediaType finalContentType = contentType;
            if (contentType != null) {
                Charset resolvedCharset = MediaType.charset$default(contentType, (Charset) null, 1, (Object) null);
                if (resolvedCharset == null) {
                    charset = Charsets.UTF_8;
                    finalContentType = MediaType.Companion.parse(contentType + "; charset=utf-8");
                } else {
                    charset = resolvedCharset;
                }
            }
            Buffer buffer = new Buffer().writeString($this$toResponseBody, charset);
            return create((BufferedSource) buffer, finalContentType, buffer.size());
        }

        public static /* synthetic */ ResponseBody create$default(Companion companion, byte[] bArr, MediaType mediaType, int i, Object obj) {
            if ((i & 1) != 0) {
                mediaType = null;
            }
            return companion.create(bArr, mediaType);
        }

        @JvmStatic
        public final ResponseBody create(byte[] $this$toResponseBody, MediaType contentType) {
            Intrinsics.checkNotNullParameter($this$toResponseBody, "<this>");
            return create((BufferedSource) new Buffer().write($this$toResponseBody), contentType, (long) $this$toResponseBody.length);
        }

        public static /* synthetic */ ResponseBody create$default(Companion companion, ByteString byteString, MediaType mediaType, int i, Object obj) {
            if ((i & 1) != 0) {
                mediaType = null;
            }
            return companion.create(byteString, mediaType);
        }

        @JvmStatic
        public final ResponseBody create(ByteString $this$toResponseBody, MediaType contentType) {
            Intrinsics.checkNotNullParameter($this$toResponseBody, "<this>");
            return create((BufferedSource) new Buffer().write($this$toResponseBody), contentType, (long) $this$toResponseBody.size());
        }

        public static /* synthetic */ ResponseBody create$default(Companion companion, BufferedSource bufferedSource, MediaType mediaType, long j, int i, Object obj) {
            if ((i & 1) != 0) {
                mediaType = null;
            }
            if ((i & 2) != 0) {
                j = -1;
            }
            return companion.create(bufferedSource, mediaType, j);
        }

        @JvmStatic
        public final ResponseBody create(BufferedSource $this$asResponseBody, MediaType contentType, long contentLength) {
            Intrinsics.checkNotNullParameter($this$asResponseBody, "<this>");
            return new ResponseBody$Companion$asResponseBody$1(contentType, contentLength, $this$asResponseBody);
        }

        @JvmStatic
        @Deprecated(level = DeprecationLevel.WARNING, message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.toResponseBody(contentType)", imports = {"okhttp3.ResponseBody.Companion.toResponseBody"}))
        public final ResponseBody create(MediaType contentType, String content) {
            Intrinsics.checkNotNullParameter(content, "content");
            return create(content, contentType);
        }

        @JvmStatic
        @Deprecated(level = DeprecationLevel.WARNING, message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.toResponseBody(contentType)", imports = {"okhttp3.ResponseBody.Companion.toResponseBody"}))
        public final ResponseBody create(MediaType contentType, byte[] content) {
            Intrinsics.checkNotNullParameter(content, "content");
            return create(content, contentType);
        }

        @JvmStatic
        @Deprecated(level = DeprecationLevel.WARNING, message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.toResponseBody(contentType)", imports = {"okhttp3.ResponseBody.Companion.toResponseBody"}))
        public final ResponseBody create(MediaType contentType, ByteString content) {
            Intrinsics.checkNotNullParameter(content, "content");
            return create(content, contentType);
        }

        @JvmStatic
        @Deprecated(level = DeprecationLevel.WARNING, message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.asResponseBody(contentType, contentLength)", imports = {"okhttp3.ResponseBody.Companion.asResponseBody"}))
        public final ResponseBody create(MediaType contentType, long contentLength, BufferedSource content) {
            Intrinsics.checkNotNullParameter(content, "content");
            return create(content, contentType, contentLength);
        }
    }
}

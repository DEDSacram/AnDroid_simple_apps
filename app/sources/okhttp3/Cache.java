package okhttp3;

import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.text.StringsKt;
import kotlin.text.Typography;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Util;
import okhttp3.internal.cache.CacheRequest;
import okhttp3.internal.cache.CacheStrategy;
import okhttp3.internal.cache.DiskLruCache;
import okhttp3.internal.concurrent.TaskRunner;
import okhttp3.internal.http.HttpMethod;
import okhttp3.internal.http.StatusLine;
import okhttp3.internal.io.FileSystem;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.ForwardingSink;
import okio.ForwardingSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

@Metadata(d1 = {"\u0000r\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\f\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010)\n\u0002\u0010\u000e\n\u0002\b\u0005\u0018\u0000 C2\u00020\u00012\u00020\u0002:\u0004BCDEB\u0017\b\u0016\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007B\u001f\b\u0000\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0002\u0010\nJ\u0016\u0010\u001f\u001a\u00020 2\f\u0010!\u001a\b\u0018\u00010\"R\u00020\fH\u0002J\b\u0010#\u001a\u00020 H\u0016J\u0006\u0010$\u001a\u00020 J\r\u0010\u0003\u001a\u00020\u0004H\u0007¢\u0006\u0002\b%J\u0006\u0010&\u001a\u00020 J\b\u0010'\u001a\u00020 H\u0016J\u0017\u0010(\u001a\u0004\u0018\u00010)2\u0006\u0010*\u001a\u00020+H\u0000¢\u0006\u0002\b,J\u0006\u0010\u0010\u001a\u00020\u0011J\u0006\u0010-\u001a\u00020 J\u0006\u0010\u0005\u001a\u00020\u0006J\u0006\u0010\u0015\u001a\u00020\u0011J\u0017\u0010.\u001a\u0004\u0018\u00010/2\u0006\u00100\u001a\u00020)H\u0000¢\u0006\u0002\b1J\u0015\u00102\u001a\u00020 2\u0006\u0010*\u001a\u00020+H\u0000¢\u0006\u0002\b3J\u0006\u0010\u0016\u001a\u00020\u0011J\u0006\u00104\u001a\u00020\u0006J\r\u00105\u001a\u00020 H\u0000¢\u0006\u0002\b6J\u0015\u00107\u001a\u00020 2\u0006\u00108\u001a\u000209H\u0000¢\u0006\u0002\b:J\u001d\u0010;\u001a\u00020 2\u0006\u0010<\u001a\u00020)2\u0006\u0010=\u001a\u00020)H\u0000¢\u0006\u0002\b>J\f\u0010?\u001a\b\u0012\u0004\u0012\u00020A0@J\u0006\u0010\u0017\u001a\u00020\u0011J\u0006\u0010\u001c\u001a\u00020\u0011R\u0014\u0010\u000b\u001a\u00020\fX\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0003\u001a\u00020\u00048G¢\u0006\u0006\u001a\u0004\b\u0003\u0010\u000fR\u000e\u0010\u0010\u001a\u00020\u0011X\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\u0012\u001a\u00020\u00138F¢\u0006\u0006\u001a\u0004\b\u0012\u0010\u0014R\u000e\u0010\u0015\u001a\u00020\u0011X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0011X\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\u0017\u001a\u00020\u0011X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0019\"\u0004\b\u001a\u0010\u001bR\u001a\u0010\u001c\u001a\u00020\u0011X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001d\u0010\u0019\"\u0004\b\u001e\u0010\u001b¨\u0006F"}, d2 = {"Lokhttp3/Cache;", "Ljava/io/Closeable;", "Ljava/io/Flushable;", "directory", "Ljava/io/File;", "maxSize", "", "(Ljava/io/File;J)V", "fileSystem", "Lokhttp3/internal/io/FileSystem;", "(Ljava/io/File;JLokhttp3/internal/io/FileSystem;)V", "cache", "Lokhttp3/internal/cache/DiskLruCache;", "getCache$okhttp", "()Lokhttp3/internal/cache/DiskLruCache;", "()Ljava/io/File;", "hitCount", "", "isClosed", "", "()Z", "networkCount", "requestCount", "writeAbortCount", "getWriteAbortCount$okhttp", "()I", "setWriteAbortCount$okhttp", "(I)V", "writeSuccessCount", "getWriteSuccessCount$okhttp", "setWriteSuccessCount$okhttp", "abortQuietly", "", "editor", "Lokhttp3/internal/cache/DiskLruCache$Editor;", "close", "delete", "-deprecated_directory", "evictAll", "flush", "get", "Lokhttp3/Response;", "request", "Lokhttp3/Request;", "get$okhttp", "initialize", "put", "Lokhttp3/internal/cache/CacheRequest;", "response", "put$okhttp", "remove", "remove$okhttp", "size", "trackConditionalCacheHit", "trackConditionalCacheHit$okhttp", "trackResponse", "cacheStrategy", "Lokhttp3/internal/cache/CacheStrategy;", "trackResponse$okhttp", "update", "cached", "network", "update$okhttp", "urls", "", "", "CacheResponseBody", "Companion", "Entry", "RealCacheRequest", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: Cache.kt */
public final class Cache implements Closeable, Flushable {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    private static final int ENTRY_BODY = 1;
    private static final int ENTRY_COUNT = 2;
    private static final int ENTRY_METADATA = 0;
    private static final int VERSION = 201105;
    private final DiskLruCache cache;
    private int hitCount;
    private int networkCount;
    private int requestCount;
    private int writeAbortCount;
    private int writeSuccessCount;

    @JvmStatic
    public static final String key(HttpUrl httpUrl) {
        return Companion.key(httpUrl);
    }

    public Cache(File directory, long maxSize, FileSystem fileSystem) {
        Intrinsics.checkNotNullParameter(directory, "directory");
        Intrinsics.checkNotNullParameter(fileSystem, "fileSystem");
        this.cache = new DiskLruCache(fileSystem, directory, VERSION, 2, maxSize, TaskRunner.INSTANCE);
    }

    public final DiskLruCache getCache$okhttp() {
        return this.cache;
    }

    public final int getWriteSuccessCount$okhttp() {
        return this.writeSuccessCount;
    }

    public final void setWriteSuccessCount$okhttp(int i) {
        this.writeSuccessCount = i;
    }

    public final int getWriteAbortCount$okhttp() {
        return this.writeAbortCount;
    }

    public final void setWriteAbortCount$okhttp(int i) {
        this.writeAbortCount = i;
    }

    public final boolean isClosed() {
        return this.cache.isClosed();
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public Cache(File directory, long maxSize) {
        this(directory, maxSize, FileSystem.SYSTEM);
        Intrinsics.checkNotNullParameter(directory, "directory");
    }

    public final Response get$okhttp(Request request) {
        Intrinsics.checkNotNullParameter(request, "request");
        try {
            DiskLruCache.Snapshot snapshot = this.cache.get(Companion.key(request.url()));
            if (snapshot == null) {
                return null;
            }
            try {
                Entry entry = new Entry(snapshot.getSource(0));
                Response response = entry.response(snapshot);
                if (entry.matches(request, response)) {
                    return response;
                }
                ResponseBody body = response.body();
                if (body != null) {
                    Util.closeQuietly((Closeable) body);
                }
                return null;
            } catch (IOException e) {
                Util.closeQuietly((Closeable) snapshot);
                return null;
            }
        } catch (IOException e2) {
            return null;
        }
    }

    public final CacheRequest put$okhttp(Response response) {
        Intrinsics.checkNotNullParameter(response, "response");
        String requestMethod = response.request().method();
        if (HttpMethod.INSTANCE.invalidatesCache(response.request().method())) {
            try {
                remove$okhttp(response.request());
            } catch (IOException e) {
            }
            return null;
        } else if (!Intrinsics.areEqual((Object) requestMethod, (Object) "GET")) {
            return null;
        } else {
            Companion companion = Companion;
            if (companion.hasVaryAll(response)) {
                return null;
            }
            Entry entry = new Entry(response);
            try {
                DiskLruCache.Editor edit$default = DiskLruCache.edit$default(this.cache, companion.key(response.request().url()), 0, 2, (Object) null);
                if (edit$default == null) {
                    return null;
                }
                DiskLruCache.Editor editor = edit$default;
                entry.writeTo(editor);
                return new RealCacheRequest(this, editor);
            } catch (IOException e2) {
                abortQuietly((DiskLruCache.Editor) null);
                return null;
            }
        }
    }

    public final void remove$okhttp(Request request) throws IOException {
        Intrinsics.checkNotNullParameter(request, "request");
        this.cache.remove(Companion.key(request.url()));
    }

    public final void update$okhttp(Response cached, Response network) {
        Intrinsics.checkNotNullParameter(cached, "cached");
        Intrinsics.checkNotNullParameter(network, "network");
        Entry entry = new Entry(network);
        ResponseBody body = cached.body();
        if (body != null) {
            try {
                DiskLruCache.Editor edit = ((CacheResponseBody) body).getSnapshot().edit();
                if (edit != null) {
                    DiskLruCache.Editor editor = edit;
                    entry.writeTo(editor);
                    editor.commit();
                }
            } catch (IOException e) {
                abortQuietly((DiskLruCache.Editor) null);
            }
        } else {
            throw new NullPointerException("null cannot be cast to non-null type okhttp3.Cache.CacheResponseBody");
        }
    }

    private final void abortQuietly(DiskLruCache.Editor editor) {
        if (editor != null) {
            try {
                editor.abort();
            } catch (IOException e) {
            }
        }
    }

    public final void initialize() throws IOException {
        this.cache.initialize();
    }

    public final void delete() throws IOException {
        this.cache.delete();
    }

    public final void evictAll() throws IOException {
        this.cache.evictAll();
    }

    public final Iterator<String> urls() throws IOException {
        return new Cache$urls$1(this);
    }

    public final synchronized int writeAbortCount() {
        return this.writeAbortCount;
    }

    public final synchronized int writeSuccessCount() {
        return this.writeSuccessCount;
    }

    public final long size() throws IOException {
        return this.cache.size();
    }

    public final long maxSize() {
        return this.cache.getMaxSize();
    }

    public void flush() throws IOException {
        this.cache.flush();
    }

    public void close() throws IOException {
        this.cache.close();
    }

    public final File directory() {
        return this.cache.getDirectory();
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "directory", imports = {}))
    /* renamed from: -deprecated_directory  reason: not valid java name */
    public final File m1810deprecated_directory() {
        return this.cache.getDirectory();
    }

    public final synchronized void trackResponse$okhttp(CacheStrategy cacheStrategy) {
        Intrinsics.checkNotNullParameter(cacheStrategy, "cacheStrategy");
        this.requestCount++;
        if (cacheStrategy.getNetworkRequest() != null) {
            this.networkCount++;
        } else if (cacheStrategy.getCacheResponse() != null) {
            this.hitCount++;
        }
    }

    public final synchronized void trackConditionalCacheHit$okhttp() {
        this.hitCount++;
    }

    public final synchronized int networkCount() {
        return this.networkCount;
    }

    public final synchronized int hitCount() {
        return this.hitCount;
    }

    public final synchronized int requestCount() {
        return this.requestCount;
    }

    @Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\b\u0004\u0018\u00002\u00020\u0001B\u0011\u0012\n\u0010\u0002\u001a\u00060\u0003R\u00020\u0004¢\u0006\u0002\u0010\u0005J\b\u0010\u000f\u001a\u00020\u0010H\u0016J\b\u0010\u0006\u001a\u00020\u0007H\u0016R\u000e\u0010\u0006\u001a\u00020\u0007X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\t\u001a\u00020\nX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u0012\u0010\u0002\u001a\u00060\u0003R\u00020\u0004X\u0004¢\u0006\u0002\n\u0000¨\u0006\u0011"}, d2 = {"Lokhttp3/Cache$RealCacheRequest;", "Lokhttp3/internal/cache/CacheRequest;", "editor", "Lokhttp3/internal/cache/DiskLruCache$Editor;", "Lokhttp3/internal/cache/DiskLruCache;", "(Lokhttp3/Cache;Lokhttp3/internal/cache/DiskLruCache$Editor;)V", "body", "Lokio/Sink;", "cacheOut", "done", "", "getDone", "()Z", "setDone", "(Z)V", "abort", "", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: Cache.kt */
    private final class RealCacheRequest implements CacheRequest {
        private final Sink body;
        private final Sink cacheOut;
        private boolean done;
        /* access modifiers changed from: private */
        public final DiskLruCache.Editor editor;
        final /* synthetic */ Cache this$0;

        public RealCacheRequest(final Cache this$02, DiskLruCache.Editor editor2) {
            Intrinsics.checkNotNullParameter(this$02, "this$0");
            Intrinsics.checkNotNullParameter(editor2, "editor");
            this.this$0 = this$02;
            this.editor = editor2;
            Sink newSink = editor2.newSink(1);
            this.cacheOut = newSink;
            this.body = new ForwardingSink(newSink) {
                public void close() throws IOException {
                    Cache cache = this$02;
                    RealCacheRequest realCacheRequest = this;
                    synchronized (cache) {
                        if (!realCacheRequest.getDone()) {
                            realCacheRequest.setDone(true);
                            cache.setWriteSuccessCount$okhttp(cache.getWriteSuccessCount$okhttp() + 1);
                            super.close();
                            this.editor.commit();
                        }
                    }
                }
            };
        }

        public final boolean getDone() {
            return this.done;
        }

        public final void setDone(boolean z) {
            this.done = z;
        }

        public void abort() {
            Cache cache = this.this$0;
            synchronized (cache) {
                if (!getDone()) {
                    setDone(true);
                    cache.setWriteAbortCount$okhttp(cache.getWriteAbortCount$okhttp() + 1);
                    Util.closeQuietly((Closeable) this.cacheOut);
                    try {
                        this.editor.abort();
                    } catch (IOException e) {
                    }
                }
            }
        }

        public Sink body() {
            return this.body;
        }
    }

    @Metadata(d1 = {"\u0000\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0002\u0018\u0000 /2\u00020\u0001:\u0001/B\u000f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004B\u000f\b\u0016\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\u0016\u0010\u001c\u001a\u00020\r2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u0005\u001a\u00020\u0006J\u0016\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020!0 2\u0006\u0010\"\u001a\u00020#H\u0002J\u0012\u0010\u0005\u001a\u00020\u00062\n\u0010$\u001a\u00060%R\u00020&J\u001e\u0010'\u001a\u00020(2\u0006\u0010)\u001a\u00020*2\f\u0010+\u001a\b\u0012\u0004\u0012\u00020!0 H\u0002J\u0012\u0010,\u001a\u00020(2\n\u0010-\u001a\u00060.R\u00020&R\u000e\u0010\b\u001a\u00020\tX\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\u00020\r8BX\u0004¢\u0006\u0006\u001a\u0004\b\f\u0010\u000eR\u000e\u0010\u000f\u001a\u00020\u0010X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0010X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0014X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u001aX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u0017X\u0004¢\u0006\u0002\n\u0000¨\u00060"}, d2 = {"Lokhttp3/Cache$Entry;", "", "rawSource", "Lokio/Source;", "(Lokio/Source;)V", "response", "Lokhttp3/Response;", "(Lokhttp3/Response;)V", "code", "", "handshake", "Lokhttp3/Handshake;", "isHttps", "", "()Z", "message", "", "protocol", "Lokhttp3/Protocol;", "receivedResponseMillis", "", "requestMethod", "responseHeaders", "Lokhttp3/Headers;", "sentRequestMillis", "url", "Lokhttp3/HttpUrl;", "varyHeaders", "matches", "request", "Lokhttp3/Request;", "readCertificateList", "", "Ljava/security/cert/Certificate;", "source", "Lokio/BufferedSource;", "snapshot", "Lokhttp3/internal/cache/DiskLruCache$Snapshot;", "Lokhttp3/internal/cache/DiskLruCache;", "writeCertList", "", "sink", "Lokio/BufferedSink;", "certificates", "writeTo", "editor", "Lokhttp3/internal/cache/DiskLruCache$Editor;", "Companion", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: Cache.kt */
    private static final class Entry {
        public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
        private static final String RECEIVED_MILLIS = Intrinsics.stringPlus(Platform.Companion.get().getPrefix(), "-Received-Millis");
        private static final String SENT_MILLIS = Intrinsics.stringPlus(Platform.Companion.get().getPrefix(), "-Sent-Millis");
        private final int code;
        private final Handshake handshake;
        private final String message;
        private final Protocol protocol;
        private final long receivedResponseMillis;
        private final String requestMethod;
        private final Headers responseHeaders;
        private final long sentRequestMillis;
        private final HttpUrl url;
        private final Headers varyHeaders;

        private final boolean isHttps() {
            return Intrinsics.areEqual((Object) this.url.scheme(), (Object) "https");
        }

        /* JADX INFO: finally extract failed */
        public Entry(Source rawSource) throws IOException {
            Throwable th;
            long j;
            Throwable th2;
            TlsVersion tlsVersion;
            Source source = rawSource;
            Intrinsics.checkNotNullParameter(source, "rawSource");
            Closeable closeable = source;
            try {
                Source it = (Source) closeable;
                BufferedSource source2 = Okio.buffer(rawSource);
                String urlLine = source2.readUtf8LineStrict();
                HttpUrl parse = HttpUrl.Companion.parse(urlLine);
                if (parse != null) {
                    this.url = parse;
                    this.requestMethod = source2.readUtf8LineStrict();
                    Headers.Builder varyHeadersBuilder = new Headers.Builder();
                    int varyRequestHeaderLineCount = Cache.Companion.readInt$okhttp(source2);
                    int i = 0;
                    while (i < varyRequestHeaderLineCount) {
                        int i2 = i;
                        i++;
                        varyHeadersBuilder.addLenient$okhttp(source2.readUtf8LineStrict());
                    }
                    this.varyHeaders = varyHeadersBuilder.build();
                    StatusLine statusLine = StatusLine.Companion.parse(source2.readUtf8LineStrict());
                    this.protocol = statusLine.protocol;
                    this.code = statusLine.code;
                    this.message = statusLine.message;
                    Headers.Builder responseHeadersBuilder = new Headers.Builder();
                    int responseHeaderLineCount = Cache.Companion.readInt$okhttp(source2);
                    int i3 = 0;
                    while (i3 < responseHeaderLineCount) {
                        int i4 = i3;
                        i3++;
                        responseHeadersBuilder.addLenient$okhttp(source2.readUtf8LineStrict());
                    }
                    String str = SENT_MILLIS;
                    String sendRequestMillisString = responseHeadersBuilder.get(str);
                    String str2 = RECEIVED_MILLIS;
                    String receivedResponseMillisString = responseHeadersBuilder.get(str2);
                    responseHeadersBuilder.removeAll(str);
                    responseHeadersBuilder.removeAll(str2);
                    long j2 = 0;
                    if (sendRequestMillisString == null) {
                        StatusLine statusLine2 = statusLine;
                        j = 0;
                    } else {
                        StatusLine statusLine3 = statusLine;
                        j = Long.parseLong(sendRequestMillisString);
                    }
                    this.sentRequestMillis = j;
                    this.receivedResponseMillis = receivedResponseMillisString != null ? Long.parseLong(receivedResponseMillisString) : j2;
                    this.responseHeaders = responseHeadersBuilder.build();
                    if (isHttps()) {
                        String blank = source2.readUtf8LineStrict();
                        if (!(blank.length() > 0)) {
                            CipherSuite cipherSuite = CipherSuite.Companion.forJavaName(source2.readUtf8LineStrict());
                            List peerCertificates = readCertificateList(source2);
                            List localCertificates = readCertificateList(source2);
                            if (!source2.exhausted()) {
                                Source source3 = it;
                                tlsVersion = TlsVersion.Companion.forJavaName(source2.readUtf8LineStrict());
                            } else {
                                tlsVersion = TlsVersion.SSL_3_0;
                            }
                            List peerCertificates2 = peerCertificates;
                            BufferedSource bufferedSource = source2;
                            this.handshake = Handshake.Companion.get(tlsVersion, cipherSuite, peerCertificates2, localCertificates);
                            th2 = null;
                        } else {
                            BufferedSource bufferedSource2 = source2;
                            throw new IOException("expected \"\" but was \"" + blank + Typography.quote);
                        }
                    } else {
                        BufferedSource bufferedSource3 = source2;
                        th2 = null;
                        this.handshake = null;
                    }
                    Unit unit = Unit.INSTANCE;
                    CloseableKt.closeFinally(closeable, th2);
                    return;
                }
                Source source4 = it;
                BufferedSource bufferedSource4 = source2;
                IOException it2 = new IOException(Intrinsics.stringPlus("Cache corruption for ", urlLine));
                Platform.Companion.get().log("cache corruption", 5, it2);
                throw it2;
            } catch (Throwable th3) {
                Throwable th4 = th3;
                CloseableKt.closeFinally(closeable, th);
                throw th4;
            }
        }

        public Entry(Response response) {
            Intrinsics.checkNotNullParameter(response, "response");
            this.url = response.request().url();
            this.varyHeaders = Cache.Companion.varyHeaders(response);
            this.requestMethod = response.request().method();
            this.protocol = response.protocol();
            this.code = response.code();
            this.message = response.message();
            this.responseHeaders = response.headers();
            this.handshake = response.handshake();
            this.sentRequestMillis = response.sentRequestAtMillis();
            this.receivedResponseMillis = response.receivedResponseAtMillis();
        }

        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0121, code lost:
            r2 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x0122, code lost:
            kotlin.io.CloseableKt.closeFinally(r1, r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x0125, code lost:
            throw r2;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final void writeTo(okhttp3.internal.cache.DiskLruCache.Editor r11) throws java.io.IOException {
            /*
                r10 = this;
                java.lang.String r0 = "editor"
                kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r11, r0)
                r0 = 0
                okio.Sink r1 = r11.newSink(r0)
                okio.BufferedSink r1 = okio.Okio.buffer((okio.Sink) r1)
                java.io.Closeable r1 = (java.io.Closeable) r1
                r2 = r1
                okio.BufferedSink r2 = (okio.BufferedSink) r2     // Catch:{ all -> 0x011f }
                r3 = 0
                okhttp3.HttpUrl r4 = r10.url     // Catch:{ all -> 0x011f }
                java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x011f }
                okio.BufferedSink r4 = r2.writeUtf8(r4)     // Catch:{ all -> 0x011f }
                r5 = 10
                r4.writeByte(r5)     // Catch:{ all -> 0x011f }
                java.lang.String r4 = r10.requestMethod     // Catch:{ all -> 0x011f }
                okio.BufferedSink r4 = r2.writeUtf8(r4)     // Catch:{ all -> 0x011f }
                r4.writeByte(r5)     // Catch:{ all -> 0x011f }
                okhttp3.Headers r4 = r10.varyHeaders     // Catch:{ all -> 0x011f }
                int r4 = r4.size()     // Catch:{ all -> 0x011f }
                long r6 = (long) r4     // Catch:{ all -> 0x011f }
                okio.BufferedSink r4 = r2.writeDecimalLong(r6)     // Catch:{ all -> 0x011f }
                r4.writeByte(r5)     // Catch:{ all -> 0x011f }
                okhttp3.Headers r4 = r10.varyHeaders     // Catch:{ all -> 0x011f }
                int r4 = r4.size()     // Catch:{ all -> 0x011f }
                r6 = r0
            L_0x0041:
                java.lang.String r7 = ": "
                if (r6 >= r4) goto L_0x0064
                r8 = r6
                int r6 = r6 + 1
                okhttp3.Headers r9 = r10.varyHeaders     // Catch:{ all -> 0x011f }
                java.lang.String r9 = r9.name(r8)     // Catch:{ all -> 0x011f }
                okio.BufferedSink r9 = r2.writeUtf8(r9)     // Catch:{ all -> 0x011f }
                okio.BufferedSink r7 = r9.writeUtf8(r7)     // Catch:{ all -> 0x011f }
                okhttp3.Headers r9 = r10.varyHeaders     // Catch:{ all -> 0x011f }
                java.lang.String r9 = r9.value(r8)     // Catch:{ all -> 0x011f }
                okio.BufferedSink r7 = r7.writeUtf8(r9)     // Catch:{ all -> 0x011f }
                r7.writeByte(r5)     // Catch:{ all -> 0x011f }
                goto L_0x0041
            L_0x0064:
                okhttp3.internal.http.StatusLine r4 = new okhttp3.internal.http.StatusLine     // Catch:{ all -> 0x011f }
                okhttp3.Protocol r6 = r10.protocol     // Catch:{ all -> 0x011f }
                int r8 = r10.code     // Catch:{ all -> 0x011f }
                java.lang.String r9 = r10.message     // Catch:{ all -> 0x011f }
                r4.<init>(r6, r8, r9)     // Catch:{ all -> 0x011f }
                java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x011f }
                okio.BufferedSink r4 = r2.writeUtf8(r4)     // Catch:{ all -> 0x011f }
                r4.writeByte(r5)     // Catch:{ all -> 0x011f }
                okhttp3.Headers r4 = r10.responseHeaders     // Catch:{ all -> 0x011f }
                int r4 = r4.size()     // Catch:{ all -> 0x011f }
                int r4 = r4 + 2
                long r8 = (long) r4     // Catch:{ all -> 0x011f }
                okio.BufferedSink r4 = r2.writeDecimalLong(r8)     // Catch:{ all -> 0x011f }
                r4.writeByte(r5)     // Catch:{ all -> 0x011f }
                okhttp3.Headers r4 = r10.responseHeaders     // Catch:{ all -> 0x011f }
                int r4 = r4.size()     // Catch:{ all -> 0x011f }
            L_0x0090:
                if (r0 >= r4) goto L_0x00b1
                r6 = r0
                int r0 = r0 + 1
                okhttp3.Headers r8 = r10.responseHeaders     // Catch:{ all -> 0x011f }
                java.lang.String r8 = r8.name(r6)     // Catch:{ all -> 0x011f }
                okio.BufferedSink r8 = r2.writeUtf8(r8)     // Catch:{ all -> 0x011f }
                okio.BufferedSink r8 = r8.writeUtf8(r7)     // Catch:{ all -> 0x011f }
                okhttp3.Headers r9 = r10.responseHeaders     // Catch:{ all -> 0x011f }
                java.lang.String r9 = r9.value(r6)     // Catch:{ all -> 0x011f }
                okio.BufferedSink r8 = r8.writeUtf8(r9)     // Catch:{ all -> 0x011f }
                r8.writeByte(r5)     // Catch:{ all -> 0x011f }
                goto L_0x0090
            L_0x00b1:
                java.lang.String r0 = SENT_MILLIS     // Catch:{ all -> 0x011f }
                okio.BufferedSink r0 = r2.writeUtf8(r0)     // Catch:{ all -> 0x011f }
                okio.BufferedSink r0 = r0.writeUtf8(r7)     // Catch:{ all -> 0x011f }
                long r8 = r10.sentRequestMillis     // Catch:{ all -> 0x011f }
                okio.BufferedSink r0 = r0.writeDecimalLong(r8)     // Catch:{ all -> 0x011f }
                r0.writeByte(r5)     // Catch:{ all -> 0x011f }
                java.lang.String r0 = RECEIVED_MILLIS     // Catch:{ all -> 0x011f }
                okio.BufferedSink r0 = r2.writeUtf8(r0)     // Catch:{ all -> 0x011f }
                okio.BufferedSink r0 = r0.writeUtf8(r7)     // Catch:{ all -> 0x011f }
                long r6 = r10.receivedResponseMillis     // Catch:{ all -> 0x011f }
                okio.BufferedSink r0 = r0.writeDecimalLong(r6)     // Catch:{ all -> 0x011f }
                r0.writeByte(r5)     // Catch:{ all -> 0x011f }
                boolean r0 = r10.isHttps()     // Catch:{ all -> 0x011f }
                if (r0 == 0) goto L_0x0117
                r2.writeByte(r5)     // Catch:{ all -> 0x011f }
                okhttp3.Handshake r0 = r10.handshake     // Catch:{ all -> 0x011f }
                kotlin.jvm.internal.Intrinsics.checkNotNull(r0)     // Catch:{ all -> 0x011f }
                okhttp3.CipherSuite r0 = r0.cipherSuite()     // Catch:{ all -> 0x011f }
                java.lang.String r0 = r0.javaName()     // Catch:{ all -> 0x011f }
                okio.BufferedSink r0 = r2.writeUtf8(r0)     // Catch:{ all -> 0x011f }
                r0.writeByte(r5)     // Catch:{ all -> 0x011f }
                okhttp3.Handshake r0 = r10.handshake     // Catch:{ all -> 0x011f }
                java.util.List r0 = r0.peerCertificates()     // Catch:{ all -> 0x011f }
                r10.writeCertList(r2, r0)     // Catch:{ all -> 0x011f }
                okhttp3.Handshake r0 = r10.handshake     // Catch:{ all -> 0x011f }
                java.util.List r0 = r0.localCertificates()     // Catch:{ all -> 0x011f }
                r10.writeCertList(r2, r0)     // Catch:{ all -> 0x011f }
                okhttp3.Handshake r0 = r10.handshake     // Catch:{ all -> 0x011f }
                okhttp3.TlsVersion r0 = r0.tlsVersion()     // Catch:{ all -> 0x011f }
                java.lang.String r0 = r0.javaName()     // Catch:{ all -> 0x011f }
                okio.BufferedSink r0 = r2.writeUtf8(r0)     // Catch:{ all -> 0x011f }
                r0.writeByte(r5)     // Catch:{ all -> 0x011f }
            L_0x0117:
                kotlin.Unit r0 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x011f }
                r0 = 0
                kotlin.io.CloseableKt.closeFinally(r1, r0)
                return
            L_0x011f:
                r0 = move-exception
                throw r0     // Catch:{ all -> 0x0121 }
            L_0x0121:
                r2 = move-exception
                kotlin.io.CloseableKt.closeFinally(r1, r0)
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.Cache.Entry.writeTo(okhttp3.internal.cache.DiskLruCache$Editor):void");
        }

        private final List<Certificate> readCertificateList(BufferedSource source) throws IOException {
            int length = Cache.Companion.readInt$okhttp(source);
            if (length == -1) {
                return CollectionsKt.emptyList();
            }
            try {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                ArrayList result = new ArrayList(length);
                int i = 0;
                while (i < length) {
                    int i2 = i;
                    i++;
                    String line = source.readUtf8LineStrict();
                    Buffer bytes = new Buffer();
                    ByteString decodeBase64 = ByteString.Companion.decodeBase64(line);
                    Intrinsics.checkNotNull(decodeBase64);
                    bytes.write(decodeBase64);
                    result.add(certificateFactory.generateCertificate(bytes.inputStream()));
                }
                return result;
            } catch (CertificateException e) {
                throw new IOException(e.getMessage());
            }
        }

        private final void writeCertList(BufferedSink sink, List<? extends Certificate> certificates) throws IOException {
            try {
                sink.writeDecimalLong((long) certificates.size()).writeByte(10);
                for (Certificate element : certificates) {
                    byte[] bytes = element.getEncoded();
                    ByteString.Companion companion = ByteString.Companion;
                    Intrinsics.checkNotNullExpressionValue(bytes, "bytes");
                    sink.writeUtf8(ByteString.Companion.of$default(companion, bytes, 0, 0, 3, (Object) null).base64()).writeByte(10);
                }
            } catch (CertificateEncodingException e) {
                throw new IOException(e.getMessage());
            }
        }

        public final boolean matches(Request request, Response response) {
            Intrinsics.checkNotNullParameter(request, "request");
            Intrinsics.checkNotNullParameter(response, "response");
            return Intrinsics.areEqual((Object) this.url, (Object) request.url()) && Intrinsics.areEqual((Object) this.requestMethod, (Object) request.method()) && Cache.Companion.varyMatches(response, this.varyHeaders, request);
        }

        public final Response response(DiskLruCache.Snapshot snapshot) {
            Intrinsics.checkNotNullParameter(snapshot, "snapshot");
            String contentType = this.responseHeaders.get("Content-Type");
            String contentLength = this.responseHeaders.get("Content-Length");
            return new Response.Builder().request(new Request.Builder().url(this.url).method(this.requestMethod, (RequestBody) null).headers(this.varyHeaders).build()).protocol(this.protocol).code(this.code).message(this.message).headers(this.responseHeaders).body(new CacheResponseBody(snapshot, contentType, contentLength)).handshake(this.handshake).sentRequestAtMillis(this.sentRequestMillis).receivedResponseAtMillis(this.receivedResponseMillis).build();
        }

        @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0004¢\u0006\u0002\n\u0000¨\u0006\u0006"}, d2 = {"Lokhttp3/Cache$Entry$Companion;", "", "()V", "RECEIVED_MILLIS", "", "SENT_MILLIS", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
        /* compiled from: Cache.kt */
        public static final class Companion {
            public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
                this();
            }

            private Companion() {
            }
        }
    }

    @Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0002\u0018\u00002\u00020\u0001B%\u0012\n\u0010\u0002\u001a\u00060\u0003R\u00020\u0004\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0006¢\u0006\u0002\u0010\bJ\b\u0010\u0007\u001a\u00020\rH\u0016J\n\u0010\u0005\u001a\u0004\u0018\u00010\u000eH\u0016J\b\u0010\u000f\u001a\u00020\nH\u0016R\u000e\u0010\t\u001a\u00020\nX\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\u0006X\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0004¢\u0006\u0002\n\u0000R\u0015\u0010\u0002\u001a\u00060\u0003R\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006\u0010"}, d2 = {"Lokhttp3/Cache$CacheResponseBody;", "Lokhttp3/ResponseBody;", "snapshot", "Lokhttp3/internal/cache/DiskLruCache$Snapshot;", "Lokhttp3/internal/cache/DiskLruCache;", "contentType", "", "contentLength", "(Lokhttp3/internal/cache/DiskLruCache$Snapshot;Ljava/lang/String;Ljava/lang/String;)V", "bodySource", "Lokio/BufferedSource;", "getSnapshot", "()Lokhttp3/internal/cache/DiskLruCache$Snapshot;", "", "Lokhttp3/MediaType;", "source", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: Cache.kt */
    private static final class CacheResponseBody extends ResponseBody {
        private final BufferedSource bodySource;
        private final String contentLength;
        private final String contentType;
        private final DiskLruCache.Snapshot snapshot;

        public final DiskLruCache.Snapshot getSnapshot() {
            return this.snapshot;
        }

        public CacheResponseBody(DiskLruCache.Snapshot snapshot2, String contentType2, String contentLength2) {
            Intrinsics.checkNotNullParameter(snapshot2, "snapshot");
            this.snapshot = snapshot2;
            this.contentType = contentType2;
            this.contentLength = contentLength2;
            this.bodySource = Okio.buffer((Source) new ForwardingSource(snapshot2.getSource(1), this) {
                final /* synthetic */ Source $source;
                final /* synthetic */ CacheResponseBody this$0;

                {
                    this.$source = $source;
                    this.this$0 = $receiver;
                }

                public void close() throws IOException {
                    this.this$0.getSnapshot().close();
                    super.close();
                }
            });
        }

        public MediaType contentType() {
            String str = this.contentType;
            if (str == null) {
                return null;
            }
            return MediaType.Companion.parse(str);
        }

        public long contentLength() {
            String str = this.contentLength;
            if (str == null) {
                return -1;
            }
            return Util.toLongOrDefault(str, -1);
        }

        public BufferedSource source() {
            return this.bodySource;
        }
    }

    @Metadata(d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0000\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0007J\u0015\u0010\f\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\u000eH\u0000¢\u0006\u0002\b\u000fJ\u0018\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u0011H\u0002J\u001e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00112\u0006\u0010\u0019\u001a\u00020\u001aJ\n\u0010\u001b\u001a\u00020\u0015*\u00020\u0017J\u0012\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\t0\u001d*\u00020\u0011H\u0002J\n\u0010\u0010\u001a\u00020\u0011*\u00020\u0017R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000¨\u0006\u001e"}, d2 = {"Lokhttp3/Cache$Companion;", "", "()V", "ENTRY_BODY", "", "ENTRY_COUNT", "ENTRY_METADATA", "VERSION", "key", "", "url", "Lokhttp3/HttpUrl;", "readInt", "source", "Lokio/BufferedSource;", "readInt$okhttp", "varyHeaders", "Lokhttp3/Headers;", "requestHeaders", "responseHeaders", "varyMatches", "", "cachedResponse", "Lokhttp3/Response;", "cachedRequest", "newRequest", "Lokhttp3/Request;", "hasVaryAll", "varyFields", "", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: Cache.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @JvmStatic
        public final String key(HttpUrl url) {
            Intrinsics.checkNotNullParameter(url, "url");
            return ByteString.Companion.encodeUtf8(url.toString()).md5().hex();
        }

        public final int readInt$okhttp(BufferedSource source) throws IOException {
            Intrinsics.checkNotNullParameter(source, "source");
            try {
                long result = source.readDecimalLong();
                String line = source.readUtf8LineStrict();
                if (result >= 0 && result <= 2147483647L) {
                    if (!(line.length() > 0)) {
                        return (int) result;
                    }
                }
                throw new IOException("expected an int but was \"" + result + line + Typography.quote);
            } catch (NumberFormatException e) {
                throw new IOException(e.getMessage());
            }
        }

        public final boolean varyMatches(Response cachedResponse, Headers cachedRequest, Request newRequest) {
            Intrinsics.checkNotNullParameter(cachedResponse, "cachedResponse");
            Intrinsics.checkNotNullParameter(cachedRequest, "cachedRequest");
            Intrinsics.checkNotNullParameter(newRequest, "newRequest");
            Iterable<String> $this$none$iv = varyFields(cachedResponse.headers());
            if (($this$none$iv instanceof Collection) && ((Collection) $this$none$iv).isEmpty()) {
                return true;
            }
            for (String it : $this$none$iv) {
                if (!Intrinsics.areEqual((Object) cachedRequest.values(it), (Object) newRequest.headers(it))) {
                    return false;
                }
            }
            return true;
        }

        public final boolean hasVaryAll(Response $this$hasVaryAll) {
            Intrinsics.checkNotNullParameter($this$hasVaryAll, "<this>");
            return varyFields($this$hasVaryAll.headers()).contains("*");
        }

        private final Set<String> varyFields(Headers $this$varyFields) {
            Set result = null;
            int size = $this$varyFields.size();
            int i = 0;
            while (i < size) {
                int i2 = i;
                i++;
                if (StringsKt.equals("Vary", $this$varyFields.name(i2), true)) {
                    String value = $this$varyFields.value(i2);
                    if (result == null) {
                        result = new TreeSet(StringsKt.getCASE_INSENSITIVE_ORDER(StringCompanionObject.INSTANCE));
                    }
                    for (String varyField : StringsKt.split$default((CharSequence) value, new char[]{','}, false, 0, 6, (Object) null)) {
                        result.add(StringsKt.trim((CharSequence) varyField).toString());
                    }
                }
            }
            return result == null ? SetsKt.emptySet() : result;
        }

        public final Headers varyHeaders(Response $this$varyHeaders) {
            Intrinsics.checkNotNullParameter($this$varyHeaders, "<this>");
            Response networkResponse = $this$varyHeaders.networkResponse();
            Intrinsics.checkNotNull(networkResponse);
            return varyHeaders(networkResponse.request().headers(), $this$varyHeaders.headers());
        }

        private final Headers varyHeaders(Headers requestHeaders, Headers responseHeaders) {
            Set varyFields = varyFields(responseHeaders);
            if (varyFields.isEmpty()) {
                return Util.EMPTY_HEADERS;
            }
            Headers.Builder result = new Headers.Builder();
            int i = 0;
            int size = requestHeaders.size();
            while (i < size) {
                int i2 = i;
                i++;
                String fieldName = requestHeaders.name(i2);
                if (varyFields.contains(fieldName)) {
                    result.add(fieldName, requestHeaders.value(i2));
                }
            }
            return result.build();
        }
    }
}

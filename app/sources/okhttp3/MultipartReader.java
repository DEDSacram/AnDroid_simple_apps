package okhttp3;

import java.io.Closeable;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.internal.http1.HeadersReader;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
import okio.Options;
import okio.Source;
import okio.Timeout;

@Metadata(d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 \u001c2\u00020\u0001:\u0003\u001c\u001d\u001eB\u000f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004B\u0015\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0002\u0010\tJ\b\u0010\u0015\u001a\u00020\u0016H\u0016J\u0010\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u0018H\u0002J\b\u0010\u001a\u001a\u0004\u0018\u00010\u001bR\u0013\u0010\u0007\u001a\u00020\b8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\nR\u000e\u0010\u000b\u001a\u00020\fX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\b\u0018\u00010\u0010R\u00020\u0000X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u000eX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\fX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0004¢\u0006\u0002\n\u0000¨\u0006\u001f"}, d2 = {"Lokhttp3/MultipartReader;", "Ljava/io/Closeable;", "response", "Lokhttp3/ResponseBody;", "(Lokhttp3/ResponseBody;)V", "source", "Lokio/BufferedSource;", "boundary", "", "(Lokio/BufferedSource;Ljava/lang/String;)V", "()Ljava/lang/String;", "closed", "", "crlfDashDashBoundary", "Lokio/ByteString;", "currentPart", "Lokhttp3/MultipartReader$PartSource;", "dashDashBoundary", "noMoreParts", "partCount", "", "close", "", "currentPartBytesRemaining", "", "maxResult", "nextPart", "Lokhttp3/MultipartReader$Part;", "Companion", "Part", "PartSource", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: MultipartReader.kt */
public final class MultipartReader implements Closeable {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    /* access modifiers changed from: private */
    public static final Options afterBoundaryOptions = Options.Companion.of(ByteString.Companion.encodeUtf8("\r\n"), ByteString.Companion.encodeUtf8("--"), ByteString.Companion.encodeUtf8(" "), ByteString.Companion.encodeUtf8("\t"));
    private final String boundary;
    private boolean closed;
    private final ByteString crlfDashDashBoundary;
    /* access modifiers changed from: private */
    public PartSource currentPart;
    private final ByteString dashDashBoundary;
    private boolean noMoreParts;
    private int partCount;
    /* access modifiers changed from: private */
    public final BufferedSource source;

    public MultipartReader(BufferedSource source2, String boundary2) throws IOException {
        Intrinsics.checkNotNullParameter(source2, "source");
        Intrinsics.checkNotNullParameter(boundary2, "boundary");
        this.source = source2;
        this.boundary = boundary2;
        this.dashDashBoundary = new Buffer().writeUtf8("--").writeUtf8(boundary2).readByteString();
        this.crlfDashDashBoundary = new Buffer().writeUtf8("\r\n--").writeUtf8(boundary2).readByteString();
    }

    public final String boundary() {
        return this.boundary;
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public MultipartReader(okhttp3.ResponseBody r4) throws java.io.IOException {
        /*
            r3 = this;
            java.lang.String r0 = "response"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r4, r0)
            okio.BufferedSource r0 = r4.source()
            okhttp3.MediaType r1 = r4.contentType()
            if (r1 != 0) goto L_0x0013
            r1 = 0
            goto L_0x0019
        L_0x0013:
            java.lang.String r2 = "boundary"
            java.lang.String r1 = r1.parameter(r2)
        L_0x0019:
            if (r1 == 0) goto L_0x001f
            r3.<init>(r0, r1)
            return
        L_0x001f:
            java.net.ProtocolException r0 = new java.net.ProtocolException
            java.lang.String r1 = "expected the Content-Type to have a boundary parameter"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.MultipartReader.<init>(okhttp3.ResponseBody):void");
    }

    public final Part nextPart() throws IOException {
        if (!this.closed) {
            if (!this.noMoreParts) {
                if (this.partCount != 0 || !this.source.rangeEquals(0, this.dashDashBoundary)) {
                    while (true) {
                        long toSkip = currentPartBytesRemaining(8192);
                        if (toSkip == 0) {
                            break;
                        }
                        this.source.skip(toSkip);
                    }
                    this.source.skip((long) this.crlfDashDashBoundary.size());
                } else {
                    this.source.skip((long) this.dashDashBoundary.size());
                }
                boolean whitespace = false;
                while (true) {
                    switch (this.source.select(afterBoundaryOptions)) {
                        case -1:
                            throw new ProtocolException("unexpected characters after boundary");
                        case 0:
                            this.partCount++;
                            Headers headers = new HeadersReader(this.source).readHeaders();
                            PartSource partSource = new PartSource(this);
                            this.currentPart = partSource;
                            return new Part(headers, Okio.buffer((Source) partSource));
                        case 1:
                            if (whitespace) {
                                throw new ProtocolException("unexpected characters after boundary");
                            } else if (this.partCount != 0) {
                                this.noMoreParts = true;
                                return null;
                            } else {
                                throw new ProtocolException("expected at least 1 part");
                            }
                        case 2:
                        case 3:
                            whitespace = true;
                            break;
                    }
                }
            } else {
                return null;
            }
        } else {
            throw new IllegalStateException("closed".toString());
        }
    }

    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0005\u001a\u00020\u0006H\u0016J\u0018\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\bH\u0016J\b\u0010\u0003\u001a\u00020\u0004H\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0004¢\u0006\u0002\n\u0000¨\u0006\f"}, d2 = {"Lokhttp3/MultipartReader$PartSource;", "Lokio/Source;", "(Lokhttp3/MultipartReader;)V", "timeout", "Lokio/Timeout;", "close", "", "read", "", "sink", "Lokio/Buffer;", "byteCount", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: MultipartReader.kt */
    private final class PartSource implements Source {
        final /* synthetic */ MultipartReader this$0;
        private final Timeout timeout = new Timeout();

        public PartSource(MultipartReader this$02) {
            Intrinsics.checkNotNullParameter(this$02, "this$0");
            this.this$0 = this$02;
        }

        public void close() {
            if (Intrinsics.areEqual((Object) this.this$0.currentPart, (Object) this)) {
                this.this$0.currentPart = null;
            }
        }

        public long read(Buffer sink, long byteCount) {
            long limit;
            long j;
            Buffer buffer = sink;
            long j2 = byteCount;
            Intrinsics.checkNotNullParameter(buffer, "sink");
            if (!(j2 >= 0)) {
                throw new IllegalArgumentException(Intrinsics.stringPlus("byteCount < 0: ", Long.valueOf(byteCount)).toString());
            } else if (Intrinsics.areEqual((Object) this.this$0.currentPart, (Object) this)) {
                Timeout this_$iv = this.this$0.source.timeout();
                Timeout other$iv = this.timeout;
                MultipartReader multipartReader = this.this$0;
                long originalTimeout$iv = this_$iv.timeoutNanos();
                this_$iv.timeout(Timeout.Companion.minTimeout(other$iv.timeoutNanos(), this_$iv.timeoutNanos()), TimeUnit.NANOSECONDS);
                if (this_$iv.hasDeadline()) {
                    long originalDeadline$iv = this_$iv.deadlineNanoTime();
                    if (other$iv.hasDeadline()) {
                        this_$iv.deadlineNanoTime(Math.min(this_$iv.deadlineNanoTime(), other$iv.deadlineNanoTime()));
                    }
                    try {
                        long limit2 = multipartReader.currentPartBytesRemaining(j2);
                        if (limit2 == 0) {
                            j = -1;
                        } else {
                            j = multipartReader.source.read(buffer, limit2);
                        }
                        return j;
                    } finally {
                        this_$iv.timeout(originalTimeout$iv, TimeUnit.NANOSECONDS);
                        if (other$iv.hasDeadline()) {
                            this_$iv.deadlineNanoTime(originalDeadline$iv);
                        }
                    }
                } else {
                    if (other$iv.hasDeadline()) {
                        this_$iv.deadlineNanoTime(other$iv.deadlineNanoTime());
                    }
                    try {
                        long limit3 = multipartReader.currentPartBytesRemaining(j2);
                        if (limit3 == 0) {
                            limit = -1;
                        } else {
                            limit = multipartReader.source.read(buffer, limit3);
                        }
                        return limit;
                    } finally {
                        this_$iv.timeout(originalTimeout$iv, TimeUnit.NANOSECONDS);
                        if (other$iv.hasDeadline()) {
                            this_$iv.clearDeadline();
                        }
                    }
                }
            } else {
                throw new IllegalStateException("closed".toString());
            }
        }

        public Timeout timeout() {
            return this.timeout;
        }
    }

    /* access modifiers changed from: private */
    public final long currentPartBytesRemaining(long maxResult) {
        this.source.require((long) this.crlfDashDashBoundary.size());
        long delimiterIndex = this.source.getBuffer().indexOf(this.crlfDashDashBoundary);
        if (delimiterIndex == -1) {
            return Math.min(maxResult, (this.source.getBuffer().size() - ((long) this.crlfDashDashBoundary.size())) + 1);
        }
        return Math.min(maxResult, delimiterIndex);
    }

    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            this.currentPart = null;
            this.source.close();
        }
    }

    @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\t\u0010\t\u001a\u00020\nH\u0001R\u0013\u0010\u0004\u001a\u00020\u00058\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0007R\u0013\u0010\u0002\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\b¨\u0006\u000b"}, d2 = {"Lokhttp3/MultipartReader$Part;", "Ljava/io/Closeable;", "headers", "Lokhttp3/Headers;", "body", "Lokio/BufferedSource;", "(Lokhttp3/Headers;Lokio/BufferedSource;)V", "()Lokio/BufferedSource;", "()Lokhttp3/Headers;", "close", "", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: MultipartReader.kt */
    public static final class Part implements Closeable {
        private final BufferedSource body;
        private final Headers headers;

        public void close() {
            this.body.close();
        }

        public Part(Headers headers2, BufferedSource body2) {
            Intrinsics.checkNotNullParameter(headers2, "headers");
            Intrinsics.checkNotNullParameter(body2, "body");
            this.headers = headers2;
            this.body = body2;
        }

        public final Headers headers() {
            return this.headers;
        }

        public final BufferedSource body() {
            return this.body;
        }
    }

    @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0007"}, d2 = {"Lokhttp3/MultipartReader$Companion;", "", "()V", "afterBoundaryOptions", "Lokio/Options;", "getAfterBoundaryOptions", "()Lokio/Options;", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: MultipartReader.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final Options getAfterBoundaryOptions() {
            return MultipartReader.afterBoundaryOptions;
        }
    }
}

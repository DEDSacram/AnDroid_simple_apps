package okhttp3.internal.ws;

import java.io.Closeable;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.text.StringsKt;
import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.internal.Util;
import okhttp3.internal.concurrent.Task;
import okhttp3.internal.concurrent.TaskQueue;
import okhttp3.internal.concurrent.TaskRunner;
import okhttp3.internal.connection.Exchange;
import okhttp3.internal.connection.RealCall;
import okhttp3.internal.ws.WebSocketReader;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;

@Metadata(d1 = {"\u0000¶\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u001c\u0018\u0000 `2\u00020\u00012\u00020\u0002:\u0005_`abcB?\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\b\u0010\r\u001a\u0004\u0018\u00010\u000e\u0012\u0006\u0010\u000f\u001a\u00020\f¢\u0006\u0002\u0010\u0010J\u0016\u00102\u001a\u0002032\u0006\u00104\u001a\u00020\f2\u0006\u00105\u001a\u000206J\b\u00107\u001a\u000203H\u0016J\u001f\u00108\u001a\u0002032\u0006\u00109\u001a\u00020:2\b\u0010;\u001a\u0004\u0018\u00010<H\u0000¢\u0006\u0002\b=J\u001a\u0010>\u001a\u00020\u00122\u0006\u0010?\u001a\u00020%2\b\u0010@\u001a\u0004\u0018\u00010\u0018H\u0016J \u0010>\u001a\u00020\u00122\u0006\u0010?\u001a\u00020%2\b\u0010@\u001a\u0004\u0018\u00010\u00182\u0006\u0010A\u001a\u00020\fJ\u000e\u0010B\u001a\u0002032\u0006\u0010C\u001a\u00020DJ\u001c\u0010E\u001a\u0002032\n\u0010F\u001a\u00060Gj\u0002`H2\b\u00109\u001a\u0004\u0018\u00010:J\u0016\u0010I\u001a\u0002032\u0006\u0010\u001e\u001a\u00020\u00182\u0006\u0010*\u001a\u00020+J\u0006\u0010J\u001a\u000203J\u0018\u0010K\u001a\u0002032\u0006\u0010?\u001a\u00020%2\u0006\u0010@\u001a\u00020\u0018H\u0016J\u0010\u0010L\u001a\u0002032\u0006\u0010M\u001a\u00020\u0018H\u0016J\u0010\u0010L\u001a\u0002032\u0006\u0010N\u001a\u00020 H\u0016J\u0010\u0010O\u001a\u0002032\u0006\u0010P\u001a\u00020 H\u0016J\u0010\u0010Q\u001a\u0002032\u0006\u0010P\u001a\u00020 H\u0016J\u000e\u0010R\u001a\u00020\u00122\u0006\u0010P\u001a\u00020 J\u0006\u0010S\u001a\u00020\u0012J\b\u0010!\u001a\u00020\fH\u0016J\u0006\u0010'\u001a\u00020%J\u0006\u0010(\u001a\u00020%J\b\u0010T\u001a\u00020\u0006H\u0016J\b\u0010U\u001a\u000203H\u0002J\u0010\u0010V\u001a\u00020\u00122\u0006\u0010M\u001a\u00020\u0018H\u0016J\u0010\u0010V\u001a\u00020\u00122\u0006\u0010N\u001a\u00020 H\u0016J\u0018\u0010V\u001a\u00020\u00122\u0006\u0010W\u001a\u00020 2\u0006\u0010X\u001a\u00020%H\u0002J\u0006\u0010)\u001a\u00020%J\u0006\u0010Y\u001a\u000203J\r\u0010Z\u001a\u00020\u0012H\u0000¢\u0006\u0002\b[J\r\u0010\\\u001a\u000203H\u0000¢\u0006\u0002\b]J\f\u0010^\u001a\u00020\u0012*\u00020\u000eH\u0002R\u000e\u0010\u0011\u001a\u00020\u0012X\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0014X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0012X\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0012X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0018X\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\u00020\bX\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0014\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001cX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\fX\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u001e\u001a\u0004\u0018\u00010\u0018X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020 0\u001cX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\fX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\"\u001a\u0004\u0018\u00010#X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020%X\u000e¢\u0006\u0002\n\u0000R\u0010\u0010&\u001a\u0004\u0018\u00010\u0018X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010'\u001a\u00020%X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020%X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010)\u001a\u00020%X\u000e¢\u0006\u0002\n\u0000R\u0010\u0010*\u001a\u0004\u0018\u00010+X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010,\u001a\u00020-X\u000e¢\u0006\u0002\n\u0000R\u0010\u0010.\u001a\u0004\u0018\u00010/X\u000e¢\u0006\u0002\n\u0000R\u0010\u00100\u001a\u0004\u0018\u000101X\u000e¢\u0006\u0002\n\u0000¨\u0006d"}, d2 = {"Lokhttp3/internal/ws/RealWebSocket;", "Lokhttp3/WebSocket;", "Lokhttp3/internal/ws/WebSocketReader$FrameCallback;", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "originalRequest", "Lokhttp3/Request;", "listener", "Lokhttp3/WebSocketListener;", "random", "Ljava/util/Random;", "pingIntervalMillis", "", "extensions", "Lokhttp3/internal/ws/WebSocketExtensions;", "minimumDeflateSize", "(Lokhttp3/internal/concurrent/TaskRunner;Lokhttp3/Request;Lokhttp3/WebSocketListener;Ljava/util/Random;JLokhttp3/internal/ws/WebSocketExtensions;J)V", "awaitingPong", "", "call", "Lokhttp3/Call;", "enqueuedClose", "failed", "key", "", "getListener$okhttp", "()Lokhttp3/WebSocketListener;", "messageAndCloseQueue", "Ljava/util/ArrayDeque;", "", "name", "pongQueue", "Lokio/ByteString;", "queueSize", "reader", "Lokhttp3/internal/ws/WebSocketReader;", "receivedCloseCode", "", "receivedCloseReason", "receivedPingCount", "receivedPongCount", "sentPingCount", "streams", "Lokhttp3/internal/ws/RealWebSocket$Streams;", "taskQueue", "Lokhttp3/internal/concurrent/TaskQueue;", "writer", "Lokhttp3/internal/ws/WebSocketWriter;", "writerTask", "Lokhttp3/internal/concurrent/Task;", "awaitTermination", "", "timeout", "timeUnit", "Ljava/util/concurrent/TimeUnit;", "cancel", "checkUpgradeSuccess", "response", "Lokhttp3/Response;", "exchange", "Lokhttp3/internal/connection/Exchange;", "checkUpgradeSuccess$okhttp", "close", "code", "reason", "cancelAfterCloseMillis", "connect", "client", "Lokhttp3/OkHttpClient;", "failWebSocket", "e", "Ljava/lang/Exception;", "Lkotlin/Exception;", "initReaderAndWriter", "loopReader", "onReadClose", "onReadMessage", "text", "bytes", "onReadPing", "payload", "onReadPong", "pong", "processNextFrame", "request", "runWriter", "send", "data", "formatOpcode", "tearDown", "writeOneFrame", "writeOneFrame$okhttp", "writePingFrame", "writePingFrame$okhttp", "isValid", "Close", "Companion", "Message", "Streams", "WriterTask", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: RealWebSocket.kt */
public final class RealWebSocket implements WebSocket, WebSocketReader.FrameCallback {
    private static final long CANCEL_AFTER_CLOSE_MILLIS = 60000;
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    public static final long DEFAULT_MINIMUM_DEFLATE_SIZE = 1024;
    private static final long MAX_QUEUE_SIZE = 16777216;
    private static final List<Protocol> ONLY_HTTP1 = CollectionsKt.listOf(Protocol.HTTP_1_1);
    private boolean awaitingPong;
    private Call call;
    private boolean enqueuedClose;
    /* access modifiers changed from: private */
    public WebSocketExtensions extensions;
    private boolean failed;
    private final String key;
    private final WebSocketListener listener;
    /* access modifiers changed from: private */
    public final ArrayDeque<Object> messageAndCloseQueue = new ArrayDeque<>();
    private long minimumDeflateSize;
    /* access modifiers changed from: private */
    public String name;
    private final Request originalRequest;
    private final long pingIntervalMillis;
    private final ArrayDeque<ByteString> pongQueue = new ArrayDeque<>();
    private long queueSize;
    private final Random random;
    private WebSocketReader reader;
    private int receivedCloseCode = -1;
    private String receivedCloseReason;
    private int receivedPingCount;
    private int receivedPongCount;
    private int sentPingCount;
    private Streams streams;
    private TaskQueue taskQueue;
    private WebSocketWriter writer;
    private Task writerTask;

    public RealWebSocket(TaskRunner taskRunner, Request originalRequest2, WebSocketListener listener2, Random random2, long pingIntervalMillis2, WebSocketExtensions extensions2, long minimumDeflateSize2) {
        Request request = originalRequest2;
        WebSocketListener webSocketListener = listener2;
        Random random3 = random2;
        Intrinsics.checkNotNullParameter(taskRunner, "taskRunner");
        Intrinsics.checkNotNullParameter(request, "originalRequest");
        Intrinsics.checkNotNullParameter(webSocketListener, "listener");
        Intrinsics.checkNotNullParameter(random3, "random");
        this.originalRequest = request;
        this.listener = webSocketListener;
        this.random = random3;
        this.pingIntervalMillis = pingIntervalMillis2;
        this.extensions = extensions2;
        this.minimumDeflateSize = minimumDeflateSize2;
        this.taskQueue = taskRunner.newQueue();
        if (Intrinsics.areEqual((Object) "GET", (Object) originalRequest2.method())) {
            ByteString.Companion companion = ByteString.Companion;
            byte[] $this$_init__u24lambda_u2d1 = new byte[16];
            random3.nextBytes($this$_init__u24lambda_u2d1);
            Unit unit = Unit.INSTANCE;
            this.key = ByteString.Companion.of$default(companion, $this$_init__u24lambda_u2d1, 0, 0, 3, (Object) null).base64();
            return;
        }
        throw new IllegalArgumentException(Intrinsics.stringPlus("Request must be GET: ", originalRequest2.method()).toString());
    }

    public final WebSocketListener getListener$okhttp() {
        return this.listener;
    }

    public Request request() {
        return this.originalRequest;
    }

    public synchronized long queueSize() {
        return this.queueSize;
    }

    public void cancel() {
        Call call2 = this.call;
        Intrinsics.checkNotNull(call2);
        call2.cancel();
    }

    public final void connect(OkHttpClient client) {
        Intrinsics.checkNotNullParameter(client, "client");
        if (this.originalRequest.header("Sec-WebSocket-Extensions") != null) {
            failWebSocket(new ProtocolException("Request header not permitted: 'Sec-WebSocket-Extensions'"), (Response) null);
            return;
        }
        OkHttpClient webSocketClient = client.newBuilder().eventListener(EventListener.NONE).protocols(ONLY_HTTP1).build();
        Request request = this.originalRequest.newBuilder().header("Upgrade", "websocket").header("Connection", "Upgrade").header("Sec-WebSocket-Key", this.key).header("Sec-WebSocket-Version", "13").header("Sec-WebSocket-Extensions", "permessage-deflate").build();
        Call realCall = new RealCall(webSocketClient, request, true);
        this.call = realCall;
        Intrinsics.checkNotNull(realCall);
        realCall.enqueue(new RealWebSocket$connect$1(this, request));
    }

    /* access modifiers changed from: private */
    public final boolean isValid(WebSocketExtensions $this$isValid) {
        if ($this$isValid.unknownValues || $this$isValid.clientMaxWindowBits != null) {
            return false;
        }
        if ($this$isValid.serverMaxWindowBits == null || new IntRange(8, 15).contains($this$isValid.serverMaxWindowBits.intValue())) {
            return true;
        }
        return false;
    }

    public final void checkUpgradeSuccess$okhttp(Response response, Exchange exchange) throws IOException {
        Intrinsics.checkNotNullParameter(response, "response");
        if (response.code() == 101) {
            String headerConnection = Response.header$default(response, "Connection", (String) null, 2, (Object) null);
            if (StringsKt.equals("Upgrade", headerConnection, true)) {
                String headerUpgrade = Response.header$default(response, "Upgrade", (String) null, 2, (Object) null);
                if (StringsKt.equals("websocket", headerUpgrade, true)) {
                    String headerAccept = Response.header$default(response, "Sec-WebSocket-Accept", (String) null, 2, (Object) null);
                    String acceptExpected = ByteString.Companion.encodeUtf8(Intrinsics.stringPlus(this.key, WebSocketProtocol.ACCEPT_MAGIC)).sha1().base64();
                    if (!Intrinsics.areEqual((Object) acceptExpected, (Object) headerAccept)) {
                        throw new ProtocolException("Expected 'Sec-WebSocket-Accept' header value '" + acceptExpected + "' but was '" + headerAccept + '\'');
                    } else if (exchange == null) {
                        throw new ProtocolException("Web Socket exchange missing: bad interceptor?");
                    }
                } else {
                    throw new ProtocolException("Expected 'Upgrade' header value 'websocket' but was '" + headerUpgrade + '\'');
                }
            } else {
                throw new ProtocolException("Expected 'Connection' header value 'Upgrade' but was '" + headerConnection + '\'');
            }
        } else {
            throw new ProtocolException("Expected HTTP 101 response but was '" + response.code() + ' ' + response.message() + '\'');
        }
    }

    public final void initReaderAndWriter(String name2, Streams streams2) throws IOException {
        Intrinsics.checkNotNullParameter(name2, "name");
        Intrinsics.checkNotNullParameter(streams2, "streams");
        WebSocketExtensions extensions2 = this.extensions;
        Intrinsics.checkNotNull(extensions2);
        synchronized (this) {
            this.name = name2;
            this.streams = streams2;
            this.writer = new WebSocketWriter(streams2.getClient(), streams2.getSink(), this.random, extensions2.perMessageDeflate, extensions2.noContextTakeover(streams2.getClient()), this.minimumDeflateSize);
            this.writerTask = new WriterTask(this);
            if (this.pingIntervalMillis != 0) {
                long pingIntervalNanos = TimeUnit.MILLISECONDS.toNanos(this.pingIntervalMillis);
                this.taskQueue.schedule(new RealWebSocket$initReaderAndWriter$lambda3$$inlined$schedule$1(Intrinsics.stringPlus(name2, " ping"), this, pingIntervalNanos), pingIntervalNanos);
            }
            if (!this.messageAndCloseQueue.isEmpty()) {
                runWriter();
            }
            Unit unit = Unit.INSTANCE;
        }
        this.reader = new WebSocketReader(streams2.getClient(), streams2.getSource(), this, extensions2.perMessageDeflate, extensions2.noContextTakeover(!streams2.getClient()));
    }

    public final void loopReader() throws IOException {
        while (this.receivedCloseCode == -1) {
            WebSocketReader webSocketReader = this.reader;
            Intrinsics.checkNotNull(webSocketReader);
            webSocketReader.processNextFrame();
        }
    }

    public final boolean processNextFrame() throws IOException {
        try {
            WebSocketReader webSocketReader = this.reader;
            Intrinsics.checkNotNull(webSocketReader);
            webSocketReader.processNextFrame();
            if (this.receivedCloseCode == -1) {
                return true;
            }
            return false;
        } catch (Exception e) {
            failWebSocket(e, (Response) null);
            return false;
        }
    }

    public final void awaitTermination(long timeout, TimeUnit timeUnit) throws InterruptedException {
        Intrinsics.checkNotNullParameter(timeUnit, "timeUnit");
        this.taskQueue.idleLatch().await(timeout, timeUnit);
    }

    public final void tearDown() throws InterruptedException {
        this.taskQueue.shutdown();
        this.taskQueue.idleLatch().await(10, TimeUnit.SECONDS);
    }

    public final synchronized int sentPingCount() {
        return this.sentPingCount;
    }

    public final synchronized int receivedPingCount() {
        return this.receivedPingCount;
    }

    public final synchronized int receivedPongCount() {
        return this.receivedPongCount;
    }

    public void onReadMessage(String text) throws IOException {
        Intrinsics.checkNotNullParameter(text, "text");
        this.listener.onMessage((WebSocket) this, text);
    }

    public void onReadMessage(ByteString bytes) throws IOException {
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        this.listener.onMessage((WebSocket) this, bytes);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0029, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void onReadPing(okio.ByteString r2) {
        /*
            r1 = this;
            monitor-enter(r1)
            java.lang.String r0 = "payload"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r2, r0)     // Catch:{ all -> 0x002a }
            boolean r0 = r1.failed     // Catch:{ all -> 0x002a }
            if (r0 != 0) goto L_0x0028
            boolean r0 = r1.enqueuedClose     // Catch:{ all -> 0x002a }
            if (r0 == 0) goto L_0x0018
            java.util.ArrayDeque<java.lang.Object> r0 = r1.messageAndCloseQueue     // Catch:{ all -> 0x002a }
            boolean r0 = r0.isEmpty()     // Catch:{ all -> 0x002a }
            if (r0 == 0) goto L_0x0018
            goto L_0x0028
        L_0x0018:
            java.util.ArrayDeque<okio.ByteString> r0 = r1.pongQueue     // Catch:{ all -> 0x002a }
            r0.add(r2)     // Catch:{ all -> 0x002a }
            r1.runWriter()     // Catch:{ all -> 0x002a }
            int r0 = r1.receivedPingCount     // Catch:{ all -> 0x002a }
            int r0 = r0 + 1
            r1.receivedPingCount = r0     // Catch:{ all -> 0x002a }
            monitor-exit(r1)
            return
        L_0x0028:
            monitor-exit(r1)
            return
        L_0x002a:
            r2 = move-exception
            monitor-exit(r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.RealWebSocket.onReadPing(okio.ByteString):void");
    }

    public synchronized void onReadPong(ByteString payload) {
        Intrinsics.checkNotNullParameter(payload, "payload");
        this.receivedPongCount++;
        this.awaitingPong = false;
    }

    public void onReadClose(int code, String reason) {
        Intrinsics.checkNotNullParameter(reason, "reason");
        boolean z = true;
        if (code != -1) {
            Object toClose = null;
            Object readerToClose = null;
            Object writerToClose = null;
            synchronized (this) {
                if (this.receivedCloseCode != -1) {
                    z = false;
                }
                if (z) {
                    this.receivedCloseCode = code;
                    this.receivedCloseReason = reason;
                    if (this.enqueuedClose && this.messageAndCloseQueue.isEmpty()) {
                        toClose = this.streams;
                        this.streams = null;
                        readerToClose = this.reader;
                        this.reader = null;
                        writerToClose = this.writer;
                        this.writer = null;
                        this.taskQueue.shutdown();
                    }
                    Unit unit = Unit.INSTANCE;
                } else {
                    throw new IllegalStateException("already closed".toString());
                }
            }
            try {
                this.listener.onClosing(this, code, reason);
                if (toClose != null) {
                    this.listener.onClosed(this, code, reason);
                }
            } finally {
                if (toClose != null) {
                    Util.closeQuietly((Closeable) toClose);
                }
                if (readerToClose != null) {
                    Util.closeQuietly((Closeable) readerToClose);
                }
                if (writerToClose != null) {
                    Util.closeQuietly((Closeable) writerToClose);
                }
            }
        } else {
            throw new IllegalArgumentException("Failed requirement.".toString());
        }
    }

    public boolean send(String text) {
        Intrinsics.checkNotNullParameter(text, "text");
        return send(ByteString.Companion.encodeUtf8(text), 1);
    }

    public boolean send(ByteString bytes) {
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        return send(bytes, 2);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003d, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final synchronized boolean send(okio.ByteString r7, int r8) {
        /*
            r6 = this;
            monitor-enter(r6)
            boolean r0 = r6.failed     // Catch:{ all -> 0x003e }
            r1 = 0
            if (r0 != 0) goto L_0x003c
            boolean r0 = r6.enqueuedClose     // Catch:{ all -> 0x003e }
            if (r0 == 0) goto L_0x000b
            goto L_0x003c
        L_0x000b:
            long r2 = r6.queueSize     // Catch:{ all -> 0x003e }
            int r0 = r7.size()     // Catch:{ all -> 0x003e }
            long r4 = (long) r0     // Catch:{ all -> 0x003e }
            long r2 = r2 + r4
            r4 = 16777216(0x1000000, double:8.289046E-317)
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 <= 0) goto L_0x0022
            r0 = 1001(0x3e9, float:1.403E-42)
            r2 = 0
            r6.close(r0, r2)     // Catch:{ all -> 0x003e }
            monitor-exit(r6)
            return r1
        L_0x0022:
            long r0 = r6.queueSize     // Catch:{ all -> 0x003e }
            int r2 = r7.size()     // Catch:{ all -> 0x003e }
            long r2 = (long) r2     // Catch:{ all -> 0x003e }
            long r0 = r0 + r2
            r6.queueSize = r0     // Catch:{ all -> 0x003e }
            java.util.ArrayDeque<java.lang.Object> r0 = r6.messageAndCloseQueue     // Catch:{ all -> 0x003e }
            okhttp3.internal.ws.RealWebSocket$Message r1 = new okhttp3.internal.ws.RealWebSocket$Message     // Catch:{ all -> 0x003e }
            r1.<init>(r8, r7)     // Catch:{ all -> 0x003e }
            r0.add(r1)     // Catch:{ all -> 0x003e }
            r6.runWriter()     // Catch:{ all -> 0x003e }
            r0 = 1
            monitor-exit(r6)
            return r0
        L_0x003c:
            monitor-exit(r6)
            return r1
        L_0x003e:
            r7 = move-exception
            monitor-exit(r6)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.RealWebSocket.send(okio.ByteString, int):boolean");
    }

    public final synchronized boolean pong(ByteString payload) {
        Intrinsics.checkNotNullParameter(payload, "payload");
        if (!this.failed) {
            if (!this.enqueuedClose || !this.messageAndCloseQueue.isEmpty()) {
                this.pongQueue.add(payload);
                runWriter();
                return true;
            }
        }
        return false;
    }

    public boolean close(int code, String reason) {
        return close(code, reason, CANCEL_AFTER_CLOSE_MILLIS);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0050, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final synchronized boolean close(int r8, java.lang.String r9, long r10) {
        /*
            r7 = this;
            monitor-enter(r7)
            okhttp3.internal.ws.WebSocketProtocol r0 = okhttp3.internal.ws.WebSocketProtocol.INSTANCE     // Catch:{ all -> 0x0051 }
            r0.validateCloseCode(r8)     // Catch:{ all -> 0x0051 }
            r0 = 0
            r1 = 0
            r2 = 1
            if (r9 == 0) goto L_0x0035
            okio.ByteString$Companion r3 = okio.ByteString.Companion     // Catch:{ all -> 0x0051 }
            okio.ByteString r3 = r3.encodeUtf8(r9)     // Catch:{ all -> 0x0051 }
            r0 = r3
            int r3 = r0.size()     // Catch:{ all -> 0x0051 }
            long r3 = (long) r3     // Catch:{ all -> 0x0051 }
            r5 = 123(0x7b, double:6.1E-322)
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 > 0) goto L_0x001f
            r3 = r2
            goto L_0x0020
        L_0x001f:
            r3 = r1
        L_0x0020:
            if (r3 == 0) goto L_0x0023
            goto L_0x0035
        L_0x0023:
            r1 = 0
            java.lang.String r2 = "reason.size() > 123: "
            java.lang.String r2 = kotlin.jvm.internal.Intrinsics.stringPlus(r2, r9)     // Catch:{ all -> 0x0051 }
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x0051 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0051 }
            r1.<init>(r2)     // Catch:{ all -> 0x0051 }
            throw r1     // Catch:{ all -> 0x0051 }
        L_0x0035:
            boolean r3 = r7.failed     // Catch:{ all -> 0x0051 }
            if (r3 != 0) goto L_0x004f
            boolean r3 = r7.enqueuedClose     // Catch:{ all -> 0x0051 }
            if (r3 == 0) goto L_0x003e
            goto L_0x004f
        L_0x003e:
            r7.enqueuedClose = r2     // Catch:{ all -> 0x0051 }
            java.util.ArrayDeque<java.lang.Object> r1 = r7.messageAndCloseQueue     // Catch:{ all -> 0x0051 }
            okhttp3.internal.ws.RealWebSocket$Close r3 = new okhttp3.internal.ws.RealWebSocket$Close     // Catch:{ all -> 0x0051 }
            r3.<init>(r8, r0, r10)     // Catch:{ all -> 0x0051 }
            r1.add(r3)     // Catch:{ all -> 0x0051 }
            r7.runWriter()     // Catch:{ all -> 0x0051 }
            monitor-exit(r7)
            return r2
        L_0x004f:
            monitor-exit(r7)
            return r1
        L_0x0051:
            r8 = move-exception
            monitor-exit(r7)
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.RealWebSocket.close(int, java.lang.String, long):boolean");
    }

    private final void runWriter() {
        if (!Util.assertionsEnabled || Thread.holdsLock(this)) {
            Task writerTask2 = this.writerTask;
            if (writerTask2 != null) {
                TaskQueue.schedule$default(this.taskQueue, writerTask2, 0, 2, (Object) null);
                return;
            }
            return;
        }
        throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + this);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v0, resolved type: okhttp3.internal.ws.RealWebSocket$Close} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: okhttp3.internal.ws.RealWebSocket$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: okhttp3.internal.ws.RealWebSocket$Close} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: okhttp3.internal.ws.RealWebSocket$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v20, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v2, resolved type: okhttp3.internal.ws.RealWebSocket$Close} */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0093, code lost:
        if (r3 == null) goto L_0x009f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:?, code lost:
        kotlin.jvm.internal.Intrinsics.checkNotNull(r2);
        r2.writePong((okio.ByteString) r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00a1, code lost:
        if ((r4 instanceof okhttp3.internal.ws.RealWebSocket.Message) == false) goto L_0x00cd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00a3, code lost:
        r10 = r4;
        kotlin.jvm.internal.Intrinsics.checkNotNull(r2);
        r2.writeMessageFrame(r10.getFormatOpcode(), r10.getData());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00b5, code lost:
        monitor-enter(r19);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:?, code lost:
        r1.queueSize -= (long) r10.getData().size();
        r0 = kotlin.Unit.INSTANCE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:?, code lost:
        monitor-exit(r19);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00cf, code lost:
        if ((r4 instanceof okhttp3.internal.ws.RealWebSocket.Close) == false) goto L_0x010c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00d1, code lost:
        r0 = (okhttp3.internal.ws.RealWebSocket.Close) r4;
        kotlin.jvm.internal.Intrinsics.checkNotNull(r2);
        r2.writeClose(r0.getCode(), r0.getReason());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x00e2, code lost:
        if (r7 == null) goto L_0x00ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x00e4, code lost:
        kotlin.jvm.internal.Intrinsics.checkNotNull(r6);
        r1.listener.onClosed(r1, r5, r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x00f0, code lost:
        if (r7 != null) goto L_0x00f3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x00f3, code lost:
        okhttp3.internal.Util.closeQuietly((java.io.Closeable) r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x00f9, code lost:
        if (r8 != null) goto L_0x00fc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x00fc, code lost:
        okhttp3.internal.Util.closeQuietly((java.io.Closeable) r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0102, code lost:
        if (r9 != null) goto L_0x0105;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0105, code lost:
        okhttp3.internal.Util.closeQuietly((java.io.Closeable) r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x010b, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x0111, code lost:
        throw new java.lang.AssertionError();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x0112, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0113, code lost:
        if (r7 != null) goto L_0x0116;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x0116, code lost:
        okhttp3.internal.Util.closeQuietly((java.io.Closeable) r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x011c, code lost:
        if (r8 != null) goto L_0x011f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x011f, code lost:
        okhttp3.internal.Util.closeQuietly((java.io.Closeable) r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x0125, code lost:
        if (r9 != null) goto L_0x0128;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x0128, code lost:
        okhttp3.internal.Util.closeQuietly((java.io.Closeable) r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x012e, code lost:
        throw r0;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean writeOneFrame$okhttp() throws java.io.IOException {
        /*
            r19 = this;
            r1 = r19
            r2 = 0
            r3 = 0
            r4 = 0
            r0 = 0
            r5 = -1
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            monitor-enter(r19)
            r0 = 0
            boolean r10 = r1.failed     // Catch:{ all -> 0x012f }
            r11 = 0
            if (r10 == 0) goto L_0x0015
            monitor-exit(r19)
            return r11
        L_0x0015:
            okhttp3.internal.ws.WebSocketWriter r10 = r1.writer     // Catch:{ all -> 0x012f }
            r2 = r10
            java.util.ArrayDeque<okio.ByteString> r10 = r1.pongQueue     // Catch:{ all -> 0x012f }
            java.lang.Object r10 = r10.poll()     // Catch:{ all -> 0x012f }
            r3 = r10
            if (r3 != 0) goto L_0x008c
            java.util.ArrayDeque<java.lang.Object> r10 = r1.messageAndCloseQueue     // Catch:{ all -> 0x012f }
            java.lang.Object r10 = r10.poll()     // Catch:{ all -> 0x012f }
            r4 = r10
            boolean r10 = r4 instanceof okhttp3.internal.ws.RealWebSocket.Close     // Catch:{ all -> 0x012f }
            if (r10 == 0) goto L_0x0085
            int r10 = r1.receivedCloseCode     // Catch:{ all -> 0x012f }
            r5 = r10
            java.lang.String r10 = r1.receivedCloseReason     // Catch:{ all -> 0x0080 }
            r6 = r10
            r10 = -1
            if (r5 == r10) goto L_0x004e
            okhttp3.internal.ws.RealWebSocket$Streams r10 = r1.streams     // Catch:{ all -> 0x012f }
            r7 = r10
            r10 = 0
            r1.streams = r10     // Catch:{ all -> 0x012f }
            okhttp3.internal.ws.WebSocketReader r11 = r1.reader     // Catch:{ all -> 0x012f }
            r8 = r11
            r1.reader = r10     // Catch:{ all -> 0x012f }
            okhttp3.internal.ws.WebSocketWriter r11 = r1.writer     // Catch:{ all -> 0x012f }
            r9 = r11
            r1.writer = r10     // Catch:{ all -> 0x012f }
            okhttp3.internal.concurrent.TaskQueue r10 = r1.taskQueue     // Catch:{ all -> 0x012f }
            r10.shutdown()     // Catch:{ all -> 0x012f }
            r16 = r0
            goto L_0x008e
        L_0x004e:
            r10 = r4
            okhttp3.internal.ws.RealWebSocket$Close r10 = (okhttp3.internal.ws.RealWebSocket.Close) r10     // Catch:{ all -> 0x0080 }
            long r10 = r10.getCancelAfterCloseMillis()     // Catch:{ all -> 0x0080 }
            okhttp3.internal.concurrent.TaskQueue r12 = r1.taskQueue     // Catch:{ all -> 0x0080 }
            java.lang.String r13 = r1.name     // Catch:{ all -> 0x0080 }
            java.lang.String r14 = " cancel"
            java.lang.String r13 = kotlin.jvm.internal.Intrinsics.stringPlus(r13, r14)     // Catch:{ all -> 0x0080 }
            java.util.concurrent.TimeUnit r14 = java.util.concurrent.TimeUnit.MILLISECONDS     // Catch:{ all -> 0x0080 }
            long r14 = r14.toNanos(r10)     // Catch:{ all -> 0x0080 }
            r16 = r0
            r0 = 1
            r17 = 0
            r18 = r5
            okhttp3.internal.ws.RealWebSocket$writeOneFrame$lambda-8$$inlined$execute$default$1 r5 = new okhttp3.internal.ws.RealWebSocket$writeOneFrame$lambda-8$$inlined$execute$default$1     // Catch:{ all -> 0x007b }
            r5.<init>(r13, r0, r1)     // Catch:{ all -> 0x007b }
            okhttp3.internal.concurrent.Task r5 = (okhttp3.internal.concurrent.Task) r5     // Catch:{ all -> 0x007b }
            r12.schedule(r5, r14)     // Catch:{ all -> 0x007b }
            r5 = r18
            goto L_0x008e
        L_0x007b:
            r0 = move-exception
            r5 = r18
            goto L_0x0130
        L_0x0080:
            r0 = move-exception
            r18 = r5
            goto L_0x0130
        L_0x0085:
            r16 = r0
            if (r4 != 0) goto L_0x008e
            monitor-exit(r19)
            return r11
        L_0x008c:
            r16 = r0
        L_0x008e:
            kotlin.Unit r0 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x012f }
            monitor-exit(r19)
            if (r3 == 0) goto L_0x009f
            kotlin.jvm.internal.Intrinsics.checkNotNull(r2)     // Catch:{ all -> 0x0112 }
            r0 = r3
            okio.ByteString r0 = (okio.ByteString) r0     // Catch:{ all -> 0x0112 }
            r2.writePong(r0)     // Catch:{ all -> 0x0112 }
            goto L_0x00ef
        L_0x009f:
            boolean r0 = r4 instanceof okhttp3.internal.ws.RealWebSocket.Message     // Catch:{ all -> 0x0112 }
            if (r0 == 0) goto L_0x00cd
            r0 = r4
            okhttp3.internal.ws.RealWebSocket$Message r0 = (okhttp3.internal.ws.RealWebSocket.Message) r0     // Catch:{ all -> 0x0112 }
            r10 = r0
            kotlin.jvm.internal.Intrinsics.checkNotNull(r2)     // Catch:{ all -> 0x0112 }
            int r0 = r10.getFormatOpcode()     // Catch:{ all -> 0x0112 }
            okio.ByteString r11 = r10.getData()     // Catch:{ all -> 0x0112 }
            r2.writeMessageFrame(r0, r11)     // Catch:{ all -> 0x0112 }
            monitor-enter(r19)     // Catch:{ all -> 0x0112 }
            r0 = 0
            long r11 = r1.queueSize     // Catch:{ all -> 0x00ca }
            okio.ByteString r13 = r10.getData()     // Catch:{ all -> 0x00ca }
            int r13 = r13.size()     // Catch:{ all -> 0x00ca }
            long r13 = (long) r13     // Catch:{ all -> 0x00ca }
            long r11 = r11 - r13
            r1.queueSize = r11     // Catch:{ all -> 0x00ca }
            kotlin.Unit r0 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x00ca }
            monitor-exit(r19)     // Catch:{ all -> 0x0112 }
            goto L_0x00ef
        L_0x00ca:
            r0 = move-exception
            monitor-exit(r19)     // Catch:{ all -> 0x0112 }
            throw r0     // Catch:{ all -> 0x0112 }
        L_0x00cd:
            boolean r0 = r4 instanceof okhttp3.internal.ws.RealWebSocket.Close     // Catch:{ all -> 0x0112 }
            if (r0 == 0) goto L_0x010c
            r0 = r4
            okhttp3.internal.ws.RealWebSocket$Close r0 = (okhttp3.internal.ws.RealWebSocket.Close) r0     // Catch:{ all -> 0x0112 }
            kotlin.jvm.internal.Intrinsics.checkNotNull(r2)     // Catch:{ all -> 0x0112 }
            int r10 = r0.getCode()     // Catch:{ all -> 0x0112 }
            okio.ByteString r11 = r0.getReason()     // Catch:{ all -> 0x0112 }
            r2.writeClose(r10, r11)     // Catch:{ all -> 0x0112 }
            if (r7 == 0) goto L_0x00ef
            okhttp3.WebSocketListener r10 = r1.listener     // Catch:{ all -> 0x0112 }
            r11 = r1
            okhttp3.WebSocket r11 = (okhttp3.WebSocket) r11     // Catch:{ all -> 0x0112 }
            kotlin.jvm.internal.Intrinsics.checkNotNull(r6)     // Catch:{ all -> 0x0112 }
            r10.onClosed(r11, r5, r6)     // Catch:{ all -> 0x0112 }
        L_0x00ef:
            r0 = 1
            if (r7 != 0) goto L_0x00f3
            goto L_0x00f9
        L_0x00f3:
            r10 = r7
            java.io.Closeable r10 = (java.io.Closeable) r10
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r10)
        L_0x00f9:
            if (r8 != 0) goto L_0x00fc
            goto L_0x0102
        L_0x00fc:
            r10 = r8
            java.io.Closeable r10 = (java.io.Closeable) r10
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r10)
        L_0x0102:
            if (r9 != 0) goto L_0x0105
            goto L_0x010b
        L_0x0105:
            r10 = r9
            java.io.Closeable r10 = (java.io.Closeable) r10
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r10)
        L_0x010b:
            return r0
        L_0x010c:
            java.lang.AssertionError r0 = new java.lang.AssertionError     // Catch:{ all -> 0x0112 }
            r0.<init>()     // Catch:{ all -> 0x0112 }
            throw r0     // Catch:{ all -> 0x0112 }
        L_0x0112:
            r0 = move-exception
            if (r7 != 0) goto L_0x0116
            goto L_0x011c
        L_0x0116:
            r10 = r7
            java.io.Closeable r10 = (java.io.Closeable) r10
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r10)
        L_0x011c:
            if (r8 != 0) goto L_0x011f
            goto L_0x0125
        L_0x011f:
            r10 = r8
            java.io.Closeable r10 = (java.io.Closeable) r10
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r10)
        L_0x0125:
            if (r9 != 0) goto L_0x0128
            goto L_0x012e
        L_0x0128:
            r10 = r9
            java.io.Closeable r10 = (java.io.Closeable) r10
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r10)
        L_0x012e:
            throw r0
        L_0x012f:
            r0 = move-exception
        L_0x0130:
            monitor-exit(r19)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.RealWebSocket.writeOneFrame$okhttp():boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0028, code lost:
        if (r1 == -1) goto L_0x005f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x002a, code lost:
        failWebSocket(new java.net.SocketTimeoutException("sent ping but didn't receive pong within " + r7.pingIntervalMillis + "ms (after " + (r1 - 1) + " successful ping/pongs)"), (okhttp3.Response) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x005d, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r0.writePing(okio.ByteString.EMPTY);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0065, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0066, code lost:
        failWebSocket(r3, (okhttp3.Response) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void writePingFrame$okhttp() {
        /*
            r7 = this;
            r0 = 0
            r1 = 0
            monitor-enter(r7)
            r2 = 0
            boolean r3 = r7.failed     // Catch:{ all -> 0x006d }
            if (r3 == 0) goto L_0x000a
            monitor-exit(r7)
            return
        L_0x000a:
            okhttp3.internal.ws.WebSocketWriter r3 = r7.writer     // Catch:{ all -> 0x006d }
            if (r3 != 0) goto L_0x0010
            monitor-exit(r7)
            return
        L_0x0010:
            r0 = r3
            boolean r3 = r7.awaitingPong     // Catch:{ all -> 0x006d }
            r4 = -1
            if (r3 == 0) goto L_0x0019
            int r3 = r7.sentPingCount     // Catch:{ all -> 0x006d }
            goto L_0x001a
        L_0x0019:
            r3 = r4
        L_0x001a:
            r1 = r3
            int r3 = r7.sentPingCount     // Catch:{ all -> 0x006d }
            r5 = 1
            int r3 = r3 + r5
            r7.sentPingCount = r3     // Catch:{ all -> 0x006d }
            r7.awaitingPong = r5     // Catch:{ all -> 0x006d }
            kotlin.Unit r2 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x006d }
            monitor-exit(r7)
            r2 = 0
            if (r1 == r4) goto L_0x005e
            java.net.SocketTimeoutException r3 = new java.net.SocketTimeoutException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "sent ping but didn't receive pong within "
            java.lang.StringBuilder r4 = r4.append(r5)
            long r5 = r7.pingIntervalMillis
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.String r5 = "ms (after "
            java.lang.StringBuilder r4 = r4.append(r5)
            int r5 = r1 + -1
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.String r5 = " successful ping/pongs)"
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4)
            java.lang.Exception r3 = (java.lang.Exception) r3
            r7.failWebSocket(r3, r2)
            return
        L_0x005e:
            okio.ByteString r3 = okio.ByteString.EMPTY     // Catch:{ IOException -> 0x0065 }
            r0.writePing(r3)     // Catch:{ IOException -> 0x0065 }
            goto L_0x006c
        L_0x0065:
            r3 = move-exception
            r4 = r3
            java.lang.Exception r4 = (java.lang.Exception) r4
            r7.failWebSocket(r4, r2)
        L_0x006c:
            return
        L_0x006d:
            r2 = move-exception
            monitor-exit(r7)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.RealWebSocket.writePingFrame$okhttp():void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        r6.listener.onFailure(r6, r7, r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0055, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0056, code lost:
        if (r0 != null) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0059, code lost:
        okhttp3.internal.Util.closeQuietly((java.io.Closeable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x005f, code lost:
        if (r1 != null) goto L_0x0062;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0062, code lost:
        okhttp3.internal.Util.closeQuietly((java.io.Closeable) r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0068, code lost:
        if (r2 != null) goto L_0x006b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x006b, code lost:
        okhttp3.internal.Util.closeQuietly((java.io.Closeable) r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0071, code lost:
        throw r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void failWebSocket(java.lang.Exception r7, okhttp3.Response r8) {
        /*
            r6 = this;
            java.lang.String r0 = "e"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r7, r0)
            r0 = 0
            r1 = 0
            r2 = 0
            monitor-enter(r6)
            r3 = 0
            boolean r4 = r6.failed     // Catch:{ all -> 0x0072 }
            if (r4 == 0) goto L_0x0010
            monitor-exit(r6)
            return
        L_0x0010:
            r4 = 1
            r6.failed = r4     // Catch:{ all -> 0x0072 }
            okhttp3.internal.ws.RealWebSocket$Streams r4 = r6.streams     // Catch:{ all -> 0x0072 }
            r0 = r4
            r4 = 0
            r6.streams = r4     // Catch:{ all -> 0x0072 }
            okhttp3.internal.ws.WebSocketReader r5 = r6.reader     // Catch:{ all -> 0x0072 }
            r1 = r5
            r6.reader = r4     // Catch:{ all -> 0x0072 }
            okhttp3.internal.ws.WebSocketWriter r5 = r6.writer     // Catch:{ all -> 0x0072 }
            r2 = r5
            r6.writer = r4     // Catch:{ all -> 0x0072 }
            okhttp3.internal.concurrent.TaskQueue r4 = r6.taskQueue     // Catch:{ all -> 0x0072 }
            r4.shutdown()     // Catch:{ all -> 0x0072 }
            kotlin.Unit r3 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x0072 }
            monitor-exit(r6)
            okhttp3.WebSocketListener r3 = r6.listener     // Catch:{ all -> 0x0055 }
            r4 = r6
            okhttp3.WebSocket r4 = (okhttp3.WebSocket) r4     // Catch:{ all -> 0x0055 }
            r5 = r7
            java.lang.Throwable r5 = (java.lang.Throwable) r5     // Catch:{ all -> 0x0055 }
            r3.onFailure(r4, r5, r8)     // Catch:{ all -> 0x0055 }
            if (r0 != 0) goto L_0x003b
            goto L_0x0041
        L_0x003b:
            r3 = r0
            java.io.Closeable r3 = (java.io.Closeable) r3
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r3)
        L_0x0041:
            if (r1 != 0) goto L_0x0044
            goto L_0x004a
        L_0x0044:
            r3 = r1
            java.io.Closeable r3 = (java.io.Closeable) r3
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r3)
        L_0x004a:
            if (r2 != 0) goto L_0x004d
            goto L_0x0053
        L_0x004d:
            r3 = r2
            java.io.Closeable r3 = (java.io.Closeable) r3
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r3)
        L_0x0053:
            return
        L_0x0055:
            r3 = move-exception
            if (r0 != 0) goto L_0x0059
            goto L_0x005f
        L_0x0059:
            r4 = r0
            java.io.Closeable r4 = (java.io.Closeable) r4
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r4)
        L_0x005f:
            if (r1 != 0) goto L_0x0062
            goto L_0x0068
        L_0x0062:
            r4 = r1
            java.io.Closeable r4 = (java.io.Closeable) r4
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r4)
        L_0x0068:
            if (r2 != 0) goto L_0x006b
            goto L_0x0071
        L_0x006b:
            r4 = r2
            java.io.Closeable r4 = (java.io.Closeable) r4
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r4)
        L_0x0071:
            throw r3
        L_0x0072:
            r3 = move-exception
            monitor-exit(r6)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.RealWebSocket.failWebSocket(java.lang.Exception, okhttp3.Response):void");
    }

    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0000\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n¨\u0006\u000b"}, d2 = {"Lokhttp3/internal/ws/RealWebSocket$Message;", "", "formatOpcode", "", "data", "Lokio/ByteString;", "(ILokio/ByteString;)V", "getData", "()Lokio/ByteString;", "getFormatOpcode", "()I", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: RealWebSocket.kt */
    public static final class Message {
        private final ByteString data;
        private final int formatOpcode;

        public Message(int formatOpcode2, ByteString data2) {
            Intrinsics.checkNotNullParameter(data2, "data");
            this.formatOpcode = formatOpcode2;
            this.data = data2;
        }

        public final int getFormatOpcode() {
            return this.formatOpcode;
        }

        public final ByteString getData() {
            return this.data;
        }
    }

    @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\b\b\u0000\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e¨\u0006\u000f"}, d2 = {"Lokhttp3/internal/ws/RealWebSocket$Close;", "", "code", "", "reason", "Lokio/ByteString;", "cancelAfterCloseMillis", "", "(ILokio/ByteString;J)V", "getCancelAfterCloseMillis", "()J", "getCode", "()I", "getReason", "()Lokio/ByteString;", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: RealWebSocket.kt */
    public static final class Close {
        private final long cancelAfterCloseMillis;
        private final int code;
        private final ByteString reason;

        public Close(int code2, ByteString reason2, long cancelAfterCloseMillis2) {
            this.code = code2;
            this.reason = reason2;
            this.cancelAfterCloseMillis = cancelAfterCloseMillis2;
        }

        public final int getCode() {
            return this.code;
        }

        public final ByteString getReason() {
            return this.reason;
        }

        public final long getCancelAfterCloseMillis() {
            return this.cancelAfterCloseMillis;
        }
    }

    @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\b&\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e¨\u0006\u000f"}, d2 = {"Lokhttp3/internal/ws/RealWebSocket$Streams;", "Ljava/io/Closeable;", "client", "", "source", "Lokio/BufferedSource;", "sink", "Lokio/BufferedSink;", "(ZLokio/BufferedSource;Lokio/BufferedSink;)V", "getClient", "()Z", "getSink", "()Lokio/BufferedSink;", "getSource", "()Lokio/BufferedSource;", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: RealWebSocket.kt */
    public static abstract class Streams implements Closeable {
        private final boolean client;
        private final BufferedSink sink;
        private final BufferedSource source;

        public Streams(boolean client2, BufferedSource source2, BufferedSink sink2) {
            Intrinsics.checkNotNullParameter(source2, "source");
            Intrinsics.checkNotNullParameter(sink2, "sink");
            this.client = client2;
            this.source = source2;
            this.sink = sink2;
        }

        public final boolean getClient() {
            return this.client;
        }

        public final BufferedSource getSource() {
            return this.source;
        }

        public final BufferedSink getSink() {
            return this.sink;
        }
    }

    @Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\b\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016¨\u0006\u0005"}, d2 = {"Lokhttp3/internal/ws/RealWebSocket$WriterTask;", "Lokhttp3/internal/concurrent/Task;", "(Lokhttp3/internal/ws/RealWebSocket;)V", "runOnce", "", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: RealWebSocket.kt */
    private final class WriterTask extends Task {
        final /* synthetic */ RealWebSocket this$0;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public WriterTask(RealWebSocket this$02) {
            super(Intrinsics.stringPlus(this$02.name, " writer"), false, 2, (DefaultConstructorMarker) null);
            Intrinsics.checkNotNullParameter(this$02, "this$0");
            this.this$0 = this$02;
        }

        public long runOnce() {
            try {
                if (this.this$0.writeOneFrame$okhttp()) {
                    return 0;
                }
                return -1;
            } catch (IOException e) {
                this.this$0.failWebSocket(e, (Response) null);
                return -1;
            }
        }
    }

    @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0004¢\u0006\u0002\n\u0000¨\u0006\n"}, d2 = {"Lokhttp3/internal/ws/RealWebSocket$Companion;", "", "()V", "CANCEL_AFTER_CLOSE_MILLIS", "", "DEFAULT_MINIMUM_DEFLATE_SIZE", "MAX_QUEUE_SIZE", "ONLY_HTTP1", "", "Lokhttp3/Protocol;", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: RealWebSocket.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }
}

package okhttp3.internal.connection;

import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.io.IOException;
import java.lang.ref.Reference;
import java.net.ConnectException;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLPeerUnverifiedException;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okhttp3.Address;
import okhttp3.Call;
import okhttp3.CertificatePinner;
import okhttp3.Connection;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.Util;
import okhttp3.internal.concurrent.TaskRunner;
import okhttp3.internal.http.ExchangeCodec;
import okhttp3.internal.http.RealInterceptorChain;
import okhttp3.internal.http1.Http1ExchangeCodec;
import okhttp3.internal.http2.ConnectionShutdownException;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.Http2Connection;
import okhttp3.internal.http2.Http2ExchangeCodec;
import okhttp3.internal.http2.Http2Stream;
import okhttp3.internal.http2.Settings;
import okhttp3.internal.http2.StreamResetException;
import okhttp3.internal.platform.Platform;
import okhttp3.internal.tls.OkHostnameVerifier;
import okhttp3.internal.ws.RealWebSocket;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

@Metadata(d1 = {"\u0000ì\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0005\u0018\u0000 {2\u00020\u00012\u00020\u0002:\u0001{B\u0015\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\u0006\u00105\u001a\u000206J\u0018\u00107\u001a\u00020\u001d2\u0006\u00108\u001a\u0002092\u0006\u0010\u0012\u001a\u00020\u0013H\u0002J>\u0010:\u001a\u0002062\u0006\u0010;\u001a\u00020\t2\u0006\u0010<\u001a\u00020\t2\u0006\u0010=\u001a\u00020\t2\u0006\u0010>\u001a\u00020\t2\u0006\u0010?\u001a\u00020\u001d2\u0006\u0010@\u001a\u00020A2\u0006\u0010B\u001a\u00020CJ%\u0010D\u001a\u0002062\u0006\u0010E\u001a\u00020F2\u0006\u0010G\u001a\u00020\u00062\u0006\u0010H\u001a\u00020IH\u0000¢\u0006\u0002\bJJ(\u0010K\u001a\u0002062\u0006\u0010;\u001a\u00020\t2\u0006\u0010<\u001a\u00020\t2\u0006\u0010@\u001a\u00020A2\u0006\u0010B\u001a\u00020CH\u0002J\u0010\u0010L\u001a\u0002062\u0006\u0010M\u001a\u00020NH\u0002J0\u0010O\u001a\u0002062\u0006\u0010;\u001a\u00020\t2\u0006\u0010<\u001a\u00020\t2\u0006\u0010=\u001a\u00020\t2\u0006\u0010@\u001a\u00020A2\u0006\u0010B\u001a\u00020CH\u0002J*\u0010P\u001a\u0004\u0018\u00010Q2\u0006\u0010<\u001a\u00020\t2\u0006\u0010=\u001a\u00020\t2\u0006\u0010R\u001a\u00020Q2\u0006\u00108\u001a\u000209H\u0002J\b\u0010S\u001a\u00020QH\u0002J(\u0010T\u001a\u0002062\u0006\u0010M\u001a\u00020N2\u0006\u0010>\u001a\u00020\t2\u0006\u0010@\u001a\u00020A2\u0006\u0010B\u001a\u00020CH\u0002J\n\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0016J\r\u0010U\u001a\u000206H\u0000¢\u0006\u0002\bVJ%\u0010W\u001a\u00020\u001d2\u0006\u0010X\u001a\u00020Y2\u000e\u0010Z\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010[H\u0000¢\u0006\u0002\b\\J\u000e\u0010]\u001a\u00020\u001d2\u0006\u0010^\u001a\u00020\u001dJ\u001d\u0010_\u001a\u00020`2\u0006\u0010E\u001a\u00020F2\u0006\u0010a\u001a\u00020bH\u0000¢\u0006\u0002\bcJ\u0015\u0010d\u001a\u00020e2\u0006\u0010f\u001a\u00020gH\u0000¢\u0006\u0002\bhJ\r\u0010 \u001a\u000206H\u0000¢\u0006\u0002\biJ\r\u0010!\u001a\u000206H\u0000¢\u0006\u0002\bjJ\u0018\u0010k\u001a\u0002062\u0006\u0010l\u001a\u00020\u00152\u0006\u0010m\u001a\u00020nH\u0016J\u0010\u0010o\u001a\u0002062\u0006\u0010p\u001a\u00020qH\u0016J\b\u0010%\u001a\u00020&H\u0016J\b\u0010\u0005\u001a\u00020\u0006H\u0016J\u0016\u0010r\u001a\u00020\u001d2\f\u0010s\u001a\b\u0012\u0004\u0012\u00020\u00060[H\u0002J\b\u00101\u001a\u00020(H\u0016J\u0010\u0010t\u001a\u0002062\u0006\u0010>\u001a\u00020\tH\u0002J\u0010\u0010u\u001a\u00020\u001d2\u0006\u00108\u001a\u000209H\u0002J\b\u0010v\u001a\u00020wH\u0016J\u001f\u0010x\u001a\u0002062\u0006\u0010@\u001a\u00020\r2\b\u0010y\u001a\u0004\u0018\u00010IH\u0000¢\u0006\u0002\bzR\u000e\u0010\b\u001a\u00020\tX\u000e¢\u0006\u0002\n\u0000R\u001d\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\f0\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0013X\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u0015X\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\u0016\u001a\u00020\u0017X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0019\"\u0004\b\u001a\u0010\u001bR\u0014\u0010\u001c\u001a\u00020\u001d8@X\u0004¢\u0006\u0006\u001a\u0004\b\u001e\u0010\u001fR\u000e\u0010 \u001a\u00020\u001dX\u000e¢\u0006\u0002\n\u0000R\u001a\u0010!\u001a\u00020\u001dX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010\u001f\"\u0004\b#\u0010$R\u0010\u0010%\u001a\u0004\u0018\u00010&X\u000e¢\u0006\u0002\n\u0000R\u0010\u0010'\u001a\u0004\u0018\u00010(X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010)\u001a\u00020\tX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0004¢\u0006\u0002\n\u0000R\u001a\u0010*\u001a\u00020\tX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b+\u0010,\"\u0004\b-\u0010.R\u0010\u0010/\u001a\u0004\u0018\u000100X\u000e¢\u0006\u0002\n\u0000R\u0010\u00101\u001a\u0004\u0018\u00010(X\u000e¢\u0006\u0002\n\u0000R\u0010\u00102\u001a\u0004\u0018\u000103X\u000e¢\u0006\u0002\n\u0000R\u000e\u00104\u001a\u00020\tX\u000e¢\u0006\u0002\n\u0000¨\u0006|"}, d2 = {"Lokhttp3/internal/connection/RealConnection;", "Lokhttp3/internal/http2/Http2Connection$Listener;", "Lokhttp3/Connection;", "connectionPool", "Lokhttp3/internal/connection/RealConnectionPool;", "route", "Lokhttp3/Route;", "(Lokhttp3/internal/connection/RealConnectionPool;Lokhttp3/Route;)V", "allocationLimit", "", "calls", "", "Ljava/lang/ref/Reference;", "Lokhttp3/internal/connection/RealCall;", "getCalls", "()Ljava/util/List;", "getConnectionPool", "()Lokhttp3/internal/connection/RealConnectionPool;", "handshake", "Lokhttp3/Handshake;", "http2Connection", "Lokhttp3/internal/http2/Http2Connection;", "idleAtNs", "", "getIdleAtNs$okhttp", "()J", "setIdleAtNs$okhttp", "(J)V", "isMultiplexed", "", "isMultiplexed$okhttp", "()Z", "noCoalescedConnections", "noNewExchanges", "getNoNewExchanges", "setNoNewExchanges", "(Z)V", "protocol", "Lokhttp3/Protocol;", "rawSocket", "Ljava/net/Socket;", "refusedStreamCount", "routeFailureCount", "getRouteFailureCount$okhttp", "()I", "setRouteFailureCount$okhttp", "(I)V", "sink", "Lokio/BufferedSink;", "socket", "source", "Lokio/BufferedSource;", "successCount", "cancel", "", "certificateSupportHost", "url", "Lokhttp3/HttpUrl;", "connect", "connectTimeout", "readTimeout", "writeTimeout", "pingIntervalMillis", "connectionRetryEnabled", "call", "Lokhttp3/Call;", "eventListener", "Lokhttp3/EventListener;", "connectFailed", "client", "Lokhttp3/OkHttpClient;", "failedRoute", "failure", "Ljava/io/IOException;", "connectFailed$okhttp", "connectSocket", "connectTls", "connectionSpecSelector", "Lokhttp3/internal/connection/ConnectionSpecSelector;", "connectTunnel", "createTunnel", "Lokhttp3/Request;", "tunnelRequest", "createTunnelRequest", "establishProtocol", "incrementSuccessCount", "incrementSuccessCount$okhttp", "isEligible", "address", "Lokhttp3/Address;", "routes", "", "isEligible$okhttp", "isHealthy", "doExtensiveChecks", "newCodec", "Lokhttp3/internal/http/ExchangeCodec;", "chain", "Lokhttp3/internal/http/RealInterceptorChain;", "newCodec$okhttp", "newWebSocketStreams", "Lokhttp3/internal/ws/RealWebSocket$Streams;", "exchange", "Lokhttp3/internal/connection/Exchange;", "newWebSocketStreams$okhttp", "noCoalescedConnections$okhttp", "noNewExchanges$okhttp", "onSettings", "connection", "settings", "Lokhttp3/internal/http2/Settings;", "onStream", "stream", "Lokhttp3/internal/http2/Http2Stream;", "routeMatchesAny", "candidates", "startHttp2", "supportsUrl", "toString", "", "trackFailure", "e", "trackFailure$okhttp", "Companion", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: RealConnection.kt */
public final class RealConnection extends Http2Connection.Listener implements Connection {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    public static final long IDLE_CONNECTION_HEALTHY_NS = 10000000000L;
    private static final int MAX_TUNNEL_ATTEMPTS = 21;
    private static final String NPE_THROW_WITH_NULL = "throw with null exception";
    private int allocationLimit = 1;
    private final List<Reference<RealCall>> calls = new ArrayList();
    private final RealConnectionPool connectionPool;
    /* access modifiers changed from: private */
    public Handshake handshake;
    private Http2Connection http2Connection;
    private long idleAtNs = Long.MAX_VALUE;
    private boolean noCoalescedConnections;
    private boolean noNewExchanges;
    private Protocol protocol;
    private Socket rawSocket;
    private int refusedStreamCount;
    private final Route route;
    private int routeFailureCount;
    private BufferedSink sink;
    /* access modifiers changed from: private */
    public Socket socket;
    private BufferedSource source;
    private int successCount;

    @Metadata(k = 3, mv = {1, 6, 0}, xi = 48)
    /* compiled from: RealConnection.kt */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[Proxy.Type.values().length];
            iArr[Proxy.Type.DIRECT.ordinal()] = 1;
            iArr[Proxy.Type.HTTP.ordinal()] = 2;
            $EnumSwitchMapping$0 = iArr;
        }
    }

    public final RealConnectionPool getConnectionPool() {
        return this.connectionPool;
    }

    public RealConnection(RealConnectionPool connectionPool2, Route route2) {
        Intrinsics.checkNotNullParameter(connectionPool2, "connectionPool");
        Intrinsics.checkNotNullParameter(route2, "route");
        this.connectionPool = connectionPool2;
        this.route = route2;
    }

    public final boolean getNoNewExchanges() {
        return this.noNewExchanges;
    }

    public final void setNoNewExchanges(boolean z) {
        this.noNewExchanges = z;
    }

    public final int getRouteFailureCount$okhttp() {
        return this.routeFailureCount;
    }

    public final void setRouteFailureCount$okhttp(int i) {
        this.routeFailureCount = i;
    }

    public final List<Reference<RealCall>> getCalls() {
        return this.calls;
    }

    public final long getIdleAtNs$okhttp() {
        return this.idleAtNs;
    }

    public final void setIdleAtNs$okhttp(long j) {
        this.idleAtNs = j;
    }

    public final boolean isMultiplexed$okhttp() {
        return this.http2Connection != null;
    }

    public final synchronized void noNewExchanges$okhttp() {
        this.noNewExchanges = true;
    }

    public final synchronized void noCoalescedConnections$okhttp() {
        this.noCoalescedConnections = true;
    }

    public final synchronized void incrementSuccessCount$okhttp() {
        this.successCount++;
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x00ad A[Catch:{ IOException -> 0x0112 }] */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00cc  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00f7  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x011e  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0126  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0151  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0158  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void connect(int r17, int r18, int r19, int r20, boolean r21, okhttp3.Call r22, okhttp3.EventListener r23) {
        /*
            r16 = this;
            r7 = r16
            r8 = r22
            r9 = r23
            java.lang.String r0 = "call"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r8, r0)
            java.lang.String r0 = "eventListener"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r9, r0)
            okhttp3.Protocol r0 = r7.protocol
            r10 = 1
            if (r0 != 0) goto L_0x0017
            r0 = r10
            goto L_0x0018
        L_0x0017:
            r0 = 0
        L_0x0018:
            if (r0 == 0) goto L_0x017a
            r0 = 0
            okhttp3.Route r1 = r7.route
            okhttp3.Address r1 = r1.address()
            java.util.List r11 = r1.connectionSpecs()
            okhttp3.internal.connection.ConnectionSpecSelector r1 = new okhttp3.internal.connection.ConnectionSpecSelector
            r1.<init>(r11)
            r12 = r1
            okhttp3.Route r1 = r7.route
            okhttp3.Address r1 = r1.address()
            javax.net.ssl.SSLSocketFactory r1 = r1.sslSocketFactory()
            if (r1 != 0) goto L_0x0090
            okhttp3.ConnectionSpec r1 = okhttp3.ConnectionSpec.CLEARTEXT
            boolean r1 = r11.contains(r1)
            if (r1 == 0) goto L_0x0080
            okhttp3.Route r1 = r7.route
            okhttp3.Address r1 = r1.address()
            okhttp3.HttpUrl r1 = r1.url()
            java.lang.String r1 = r1.host()
            okhttp3.internal.platform.Platform$Companion r2 = okhttp3.internal.platform.Platform.Companion
            okhttp3.internal.platform.Platform r2 = r2.get()
            boolean r2 = r2.isCleartextTrafficPermitted(r1)
            if (r2 == 0) goto L_0x005a
            goto L_0x00a2
        L_0x005a:
            okhttp3.internal.connection.RouteException r2 = new okhttp3.internal.connection.RouteException
            java.net.UnknownServiceException r3 = new java.net.UnknownServiceException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "CLEARTEXT communication to "
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.StringBuilder r4 = r4.append(r1)
            java.lang.String r5 = " not permitted by network security policy"
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4)
            java.io.IOException r3 = (java.io.IOException) r3
            r2.<init>(r3)
            throw r2
        L_0x0080:
            okhttp3.internal.connection.RouteException r1 = new okhttp3.internal.connection.RouteException
            java.net.UnknownServiceException r2 = new java.net.UnknownServiceException
            java.lang.String r3 = "CLEARTEXT communication not enabled for client"
            r2.<init>(r3)
            java.io.IOException r2 = (java.io.IOException) r2
            r1.<init>(r2)
            throw r1
        L_0x0090:
            okhttp3.Route r1 = r7.route
            okhttp3.Address r1 = r1.address()
            java.util.List r1 = r1.protocols()
            okhttp3.Protocol r2 = okhttp3.Protocol.H2_PRIOR_KNOWLEDGE
            boolean r1 = r1.contains(r2)
            if (r1 != 0) goto L_0x0166
        L_0x00a2:
            r13 = r0
        L_0x00a3:
            okhttp3.Route r0 = r7.route     // Catch:{ IOException -> 0x0112 }
            boolean r0 = r0.requiresTunnel()     // Catch:{ IOException -> 0x0112 }
            if (r0 == 0) goto L_0x00cc
            r1 = r16
            r2 = r17
            r3 = r18
            r4 = r19
            r5 = r22
            r6 = r23
            r1.connectTunnel(r2, r3, r4, r5, r6)     // Catch:{ IOException -> 0x0112 }
            java.net.Socket r0 = r7.rawSocket     // Catch:{ IOException -> 0x0112 }
            if (r0 != 0) goto L_0x00c7
            r14 = r17
            r15 = r18
            r6 = r20
            goto L_0x00ea
        L_0x00c7:
            r14 = r17
            r15 = r18
            goto L_0x00d3
        L_0x00cc:
            r14 = r17
            r15 = r18
            r7.connectSocket(r14, r15, r8, r9)     // Catch:{ IOException -> 0x0110 }
        L_0x00d3:
            r6 = r20
            r7.establishProtocol(r12, r6, r8, r9)     // Catch:{ IOException -> 0x010e }
            okhttp3.Route r0 = r7.route     // Catch:{ IOException -> 0x010e }
            java.net.InetSocketAddress r0 = r0.socketAddress()     // Catch:{ IOException -> 0x010e }
            okhttp3.Route r1 = r7.route     // Catch:{ IOException -> 0x010e }
            java.net.Proxy r1 = r1.proxy()     // Catch:{ IOException -> 0x010e }
            okhttp3.Protocol r2 = r7.protocol     // Catch:{ IOException -> 0x010e }
            r9.connectEnd(r8, r0, r1, r2)     // Catch:{ IOException -> 0x010e }
        L_0x00ea:
            okhttp3.Route r0 = r7.route
            boolean r0 = r0.requiresTunnel()
            if (r0 == 0) goto L_0x0107
            java.net.Socket r0 = r7.rawSocket
            if (r0 == 0) goto L_0x00f7
            goto L_0x0107
        L_0x00f7:
            okhttp3.internal.connection.RouteException r0 = new okhttp3.internal.connection.RouteException
            java.net.ProtocolException r1 = new java.net.ProtocolException
            java.lang.String r2 = "Too many tunnel connections attempted: 21"
            r1.<init>(r2)
            java.io.IOException r1 = (java.io.IOException) r1
            r0.<init>(r1)
            throw r0
        L_0x0107:
            long r0 = java.lang.System.nanoTime()
            r7.idleAtNs = r0
            return
        L_0x010e:
            r0 = move-exception
            goto L_0x0119
        L_0x0110:
            r0 = move-exception
            goto L_0x0117
        L_0x0112:
            r0 = move-exception
            r14 = r17
            r15 = r18
        L_0x0117:
            r6 = r20
        L_0x0119:
            java.net.Socket r1 = r7.socket
            if (r1 != 0) goto L_0x011e
            goto L_0x0121
        L_0x011e:
            okhttp3.internal.Util.closeQuietly((java.net.Socket) r1)
        L_0x0121:
            java.net.Socket r1 = r7.rawSocket
            if (r1 != 0) goto L_0x0126
            goto L_0x0129
        L_0x0126:
            okhttp3.internal.Util.closeQuietly((java.net.Socket) r1)
        L_0x0129:
            r1 = 0
            r7.socket = r1
            r7.rawSocket = r1
            r7.source = r1
            r7.sink = r1
            r7.handshake = r1
            r7.protocol = r1
            r7.http2Connection = r1
            r7.allocationLimit = r10
            okhttp3.Route r1 = r7.route
            java.net.InetSocketAddress r3 = r1.socketAddress()
            okhttp3.Route r1 = r7.route
            java.net.Proxy r4 = r1.proxy()
            r5 = 0
            r1 = r23
            r2 = r22
            r6 = r0
            r1.connectFailed(r2, r3, r4, r5, r6)
            if (r13 != 0) goto L_0x0158
            okhttp3.internal.connection.RouteException r1 = new okhttp3.internal.connection.RouteException
            r1.<init>(r0)
            r13 = r1
            goto L_0x015b
        L_0x0158:
            r13.addConnectException(r0)
        L_0x015b:
            if (r21 == 0) goto L_0x0165
            boolean r1 = r12.connectionFailed(r0)
            if (r1 == 0) goto L_0x0165
            goto L_0x00a3
        L_0x0165:
            throw r13
        L_0x0166:
            r14 = r17
            r15 = r18
            okhttp3.internal.connection.RouteException r1 = new okhttp3.internal.connection.RouteException
            java.net.UnknownServiceException r2 = new java.net.UnknownServiceException
            java.lang.String r3 = "H2_PRIOR_KNOWLEDGE cannot be used with HTTPS"
            r2.<init>(r3)
            java.io.IOException r2 = (java.io.IOException) r2
            r1.<init>(r2)
            throw r1
        L_0x017a:
            r14 = r17
            r15 = r18
            r0 = 0
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "already connected"
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.RealConnection.connect(int, int, int, int, boolean, okhttp3.Call, okhttp3.EventListener):void");
    }

    private final void connectTunnel(int connectTimeout, int readTimeout, int writeTimeout, Call call, EventListener eventListener) throws IOException {
        Request tunnelRequest = createTunnelRequest();
        HttpUrl url = tunnelRequest.url();
        int i = 0;
        while (i < 21) {
            int i2 = i;
            i++;
            connectSocket(connectTimeout, readTimeout, call, eventListener);
            Request createTunnel = createTunnel(readTimeout, writeTimeout, tunnelRequest, url);
            if (createTunnel != null) {
                tunnelRequest = createTunnel;
                Socket socket2 = this.rawSocket;
                if (socket2 != null) {
                    Util.closeQuietly(socket2);
                }
                this.rawSocket = null;
                this.sink = null;
                this.source = null;
                eventListener.connectEnd(call, this.route.socketAddress(), this.route.proxy(), (Protocol) null);
            } else {
                return;
            }
        }
    }

    private final void connectSocket(int connectTimeout, int readTimeout, Call call, EventListener eventListener) throws IOException {
        Socket rawSocket2;
        Proxy proxy = this.route.proxy();
        Address address = this.route.address();
        Proxy.Type type = proxy.type();
        switch (type == null ? -1 : WhenMappings.$EnumSwitchMapping$0[type.ordinal()]) {
            case 1:
            case 2:
                rawSocket2 = address.socketFactory().createSocket();
                Intrinsics.checkNotNull(rawSocket2);
                break;
            default:
                rawSocket2 = new Socket(proxy);
                break;
        }
        this.rawSocket = rawSocket2;
        eventListener.connectStart(call, this.route.socketAddress(), proxy);
        rawSocket2.setSoTimeout(readTimeout);
        try {
            Platform.Companion.get().connectSocket(rawSocket2, this.route.socketAddress(), connectTimeout);
            try {
                this.source = Okio.buffer(Okio.source(rawSocket2));
                this.sink = Okio.buffer(Okio.sink(rawSocket2));
            } catch (NullPointerException npe) {
                if (Intrinsics.areEqual((Object) npe.getMessage(), (Object) NPE_THROW_WITH_NULL)) {
                    throw new IOException(npe);
                }
            }
        } catch (ConnectException e) {
            ConnectException $this$connectSocket_u24lambda_u2d1 = new ConnectException(Intrinsics.stringPlus("Failed to connect to ", this.route.socketAddress()));
            $this$connectSocket_u24lambda_u2d1.initCause(e);
            throw $this$connectSocket_u24lambda_u2d1;
        }
    }

    private final void establishProtocol(ConnectionSpecSelector connectionSpecSelector, int pingIntervalMillis, Call call, EventListener eventListener) throws IOException {
        if (this.route.address().sslSocketFactory() != null) {
            eventListener.secureConnectStart(call);
            connectTls(connectionSpecSelector);
            eventListener.secureConnectEnd(call, this.handshake);
            if (this.protocol == Protocol.HTTP_2) {
                startHttp2(pingIntervalMillis);
            }
        } else if (this.route.address().protocols().contains(Protocol.H2_PRIOR_KNOWLEDGE)) {
            this.socket = this.rawSocket;
            this.protocol = Protocol.H2_PRIOR_KNOWLEDGE;
            startHttp2(pingIntervalMillis);
        } else {
            this.socket = this.rawSocket;
            this.protocol = Protocol.HTTP_1_1;
        }
    }

    private final void startHttp2(int pingIntervalMillis) throws IOException {
        Socket socket2 = this.socket;
        Intrinsics.checkNotNull(socket2);
        BufferedSource source2 = this.source;
        Intrinsics.checkNotNull(source2);
        BufferedSink sink2 = this.sink;
        Intrinsics.checkNotNull(sink2);
        socket2.setSoTimeout(0);
        Http2Connection http2Connection2 = new Http2Connection.Builder(true, TaskRunner.INSTANCE).socket(socket2, this.route.address().url().host(), source2, sink2).listener(this).pingIntervalMillis(pingIntervalMillis).build();
        this.http2Connection = http2Connection2;
        this.allocationLimit = Http2Connection.Companion.getDEFAULT_SETTINGS().getMaxConcurrentStreams();
        Http2Connection.start$default(http2Connection2, false, (TaskRunner) null, 3, (Object) null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:37:0x01a8  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x01b5  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void connectTls(okhttp3.internal.connection.ConnectionSpecSelector r17) throws java.io.IOException {
        /*
            r16 = this;
            r1 = r16
            okhttp3.Route r0 = r1.route
            okhttp3.Address r2 = r0.address()
            javax.net.ssl.SSLSocketFactory r3 = r2.sslSocketFactory()
            r4 = 0
            r5 = 0
            kotlin.jvm.internal.Intrinsics.checkNotNull(r3)     // Catch:{ all -> 0x01a3 }
            java.net.Socket r0 = r1.rawSocket     // Catch:{ all -> 0x01a3 }
            okhttp3.HttpUrl r6 = r2.url()     // Catch:{ all -> 0x01a3 }
            java.lang.String r6 = r6.host()     // Catch:{ all -> 0x01a3 }
            okhttp3.HttpUrl r7 = r2.url()     // Catch:{ all -> 0x01a3 }
            int r7 = r7.port()     // Catch:{ all -> 0x01a3 }
            r8 = 1
            java.net.Socket r0 = r3.createSocket(r0, r6, r7, r8)     // Catch:{ all -> 0x01a3 }
            if (r0 == 0) goto L_0x0197
            javax.net.ssl.SSLSocket r0 = (javax.net.ssl.SSLSocket) r0     // Catch:{ all -> 0x01a3 }
            r5 = r0
            r6 = r17
            okhttp3.ConnectionSpec r0 = r6.configureSecureSocket(r5)     // Catch:{ all -> 0x01a1 }
            boolean r7 = r0.supportsTlsExtensions()     // Catch:{ all -> 0x01a1 }
            if (r7 == 0) goto L_0x004f
            okhttp3.internal.platform.Platform$Companion r7 = okhttp3.internal.platform.Platform.Companion     // Catch:{ all -> 0x01a1 }
            okhttp3.internal.platform.Platform r7 = r7.get()     // Catch:{ all -> 0x01a1 }
            okhttp3.HttpUrl r9 = r2.url()     // Catch:{ all -> 0x01a1 }
            java.lang.String r9 = r9.host()     // Catch:{ all -> 0x01a1 }
            java.util.List r10 = r2.protocols()     // Catch:{ all -> 0x01a1 }
            r7.configureTlsExtensions(r5, r9, r10)     // Catch:{ all -> 0x01a1 }
        L_0x004f:
            r5.startHandshake()     // Catch:{ all -> 0x01a1 }
            javax.net.ssl.SSLSession r7 = r5.getSession()     // Catch:{ all -> 0x01a1 }
            okhttp3.Handshake$Companion r9 = okhttp3.Handshake.Companion     // Catch:{ all -> 0x01a1 }
            java.lang.String r10 = "sslSocketSession"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r7, r10)     // Catch:{ all -> 0x01a1 }
            okhttp3.Handshake r9 = r9.get(r7)     // Catch:{ all -> 0x01a1 }
            javax.net.ssl.HostnameVerifier r10 = r2.hostnameVerifier()     // Catch:{ all -> 0x01a1 }
            kotlin.jvm.internal.Intrinsics.checkNotNull(r10)     // Catch:{ all -> 0x01a1 }
            okhttp3.HttpUrl r11 = r2.url()     // Catch:{ all -> 0x01a1 }
            java.lang.String r11 = r11.host()     // Catch:{ all -> 0x01a1 }
            boolean r10 = r10.verify(r11, r7)     // Catch:{ all -> 0x01a1 }
            r11 = 0
            if (r10 != 0) goto L_0x0115
            java.util.List r10 = r9.peerCertificates()     // Catch:{ all -> 0x01a1 }
            r12 = r10
            java.util.Collection r12 = (java.util.Collection) r12     // Catch:{ all -> 0x01a1 }
            boolean r12 = r12.isEmpty()     // Catch:{ all -> 0x01a1 }
            r12 = r12 ^ r8
            if (r12 == 0) goto L_0x00ee
            r12 = 0
            java.lang.Object r12 = r10.get(r12)     // Catch:{ all -> 0x01a1 }
            java.security.cert.X509Certificate r12 = (java.security.cert.X509Certificate) r12     // Catch:{ all -> 0x01a1 }
            javax.net.ssl.SSLPeerUnverifiedException r13 = new javax.net.ssl.SSLPeerUnverifiedException     // Catch:{ all -> 0x01a1 }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x01a1 }
            r14.<init>()     // Catch:{ all -> 0x01a1 }
            java.lang.String r15 = "\n              |Hostname "
            java.lang.StringBuilder r14 = r14.append(r15)     // Catch:{ all -> 0x01a1 }
            okhttp3.HttpUrl r15 = r2.url()     // Catch:{ all -> 0x01a1 }
            java.lang.String r15 = r15.host()     // Catch:{ all -> 0x01a1 }
            java.lang.StringBuilder r14 = r14.append(r15)     // Catch:{ all -> 0x01a1 }
            java.lang.String r15 = " not verified:\n              |    certificate: "
            java.lang.StringBuilder r14 = r14.append(r15)     // Catch:{ all -> 0x01a1 }
            okhttp3.CertificatePinner$Companion r15 = okhttp3.CertificatePinner.Companion     // Catch:{ all -> 0x01a1 }
            r8 = r12
            java.security.cert.Certificate r8 = (java.security.cert.Certificate) r8     // Catch:{ all -> 0x01a1 }
            java.lang.String r8 = r15.pin(r8)     // Catch:{ all -> 0x01a1 }
            java.lang.StringBuilder r8 = r14.append(r8)     // Catch:{ all -> 0x01a1 }
            java.lang.String r14 = "\n              |    DN: "
            java.lang.StringBuilder r8 = r8.append(r14)     // Catch:{ all -> 0x01a1 }
            java.security.Principal r14 = r12.getSubjectDN()     // Catch:{ all -> 0x01a1 }
            java.lang.String r14 = r14.getName()     // Catch:{ all -> 0x01a1 }
            java.lang.StringBuilder r8 = r8.append(r14)     // Catch:{ all -> 0x01a1 }
            java.lang.String r14 = "\n              |    subjectAltNames: "
            java.lang.StringBuilder r8 = r8.append(r14)     // Catch:{ all -> 0x01a1 }
            okhttp3.internal.tls.OkHostnameVerifier r14 = okhttp3.internal.tls.OkHostnameVerifier.INSTANCE     // Catch:{ all -> 0x01a1 }
            java.util.List r14 = r14.allSubjectAltNames(r12)     // Catch:{ all -> 0x01a1 }
            java.lang.StringBuilder r8 = r8.append(r14)     // Catch:{ all -> 0x01a1 }
            java.lang.String r14 = "\n              "
            java.lang.StringBuilder r8 = r8.append(r14)     // Catch:{ all -> 0x01a1 }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x01a1 }
            r14 = 1
            java.lang.String r8 = kotlin.text.StringsKt.trimMargin$default(r8, r11, r14, r11)     // Catch:{ all -> 0x01a1 }
            r13.<init>(r8)     // Catch:{ all -> 0x01a1 }
            throw r13     // Catch:{ all -> 0x01a1 }
        L_0x00ee:
            javax.net.ssl.SSLPeerUnverifiedException r8 = new javax.net.ssl.SSLPeerUnverifiedException     // Catch:{ all -> 0x01a1 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x01a1 }
            r11.<init>()     // Catch:{ all -> 0x01a1 }
            java.lang.String r12 = "Hostname "
            java.lang.StringBuilder r11 = r11.append(r12)     // Catch:{ all -> 0x01a1 }
            okhttp3.HttpUrl r12 = r2.url()     // Catch:{ all -> 0x01a1 }
            java.lang.String r12 = r12.host()     // Catch:{ all -> 0x01a1 }
            java.lang.StringBuilder r11 = r11.append(r12)     // Catch:{ all -> 0x01a1 }
            java.lang.String r12 = " not verified (no certificates)"
            java.lang.StringBuilder r11 = r11.append(r12)     // Catch:{ all -> 0x01a1 }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x01a1 }
            r8.<init>(r11)     // Catch:{ all -> 0x01a1 }
            throw r8     // Catch:{ all -> 0x01a1 }
        L_0x0115:
            okhttp3.CertificatePinner r8 = r2.certificatePinner()     // Catch:{ all -> 0x01a1 }
            kotlin.jvm.internal.Intrinsics.checkNotNull(r8)     // Catch:{ all -> 0x01a1 }
            okhttp3.Handshake r10 = new okhttp3.Handshake     // Catch:{ all -> 0x01a1 }
            okhttp3.TlsVersion r12 = r9.tlsVersion()     // Catch:{ all -> 0x01a1 }
            okhttp3.CipherSuite r13 = r9.cipherSuite()     // Catch:{ all -> 0x01a1 }
            java.util.List r14 = r9.localCertificates()     // Catch:{ all -> 0x01a1 }
            okhttp3.internal.connection.RealConnection$connectTls$1 r15 = new okhttp3.internal.connection.RealConnection$connectTls$1     // Catch:{ all -> 0x01a1 }
            r15.<init>(r8, r9, r2)     // Catch:{ all -> 0x01a1 }
            kotlin.jvm.functions.Function0 r15 = (kotlin.jvm.functions.Function0) r15     // Catch:{ all -> 0x01a1 }
            r10.<init>(r12, r13, r14, r15)     // Catch:{ all -> 0x01a1 }
            r1.handshake = r10     // Catch:{ all -> 0x01a1 }
            okhttp3.HttpUrl r10 = r2.url()     // Catch:{ all -> 0x01a1 }
            java.lang.String r10 = r10.host()     // Catch:{ all -> 0x01a1 }
            okhttp3.internal.connection.RealConnection$connectTls$2 r12 = new okhttp3.internal.connection.RealConnection$connectTls$2     // Catch:{ all -> 0x01a1 }
            r12.<init>(r1)     // Catch:{ all -> 0x01a1 }
            kotlin.jvm.functions.Function0 r12 = (kotlin.jvm.functions.Function0) r12     // Catch:{ all -> 0x01a1 }
            r8.check$okhttp(r10, r12)     // Catch:{ all -> 0x01a1 }
            boolean r10 = r0.supportsTlsExtensions()     // Catch:{ all -> 0x01a1 }
            if (r10 == 0) goto L_0x0159
            okhttp3.internal.platform.Platform$Companion r10 = okhttp3.internal.platform.Platform.Companion     // Catch:{ all -> 0x01a1 }
            okhttp3.internal.platform.Platform r10 = r10.get()     // Catch:{ all -> 0x01a1 }
            java.lang.String r11 = r10.getSelectedProtocol(r5)     // Catch:{ all -> 0x01a1 }
            goto L_0x015c
        L_0x0159:
            r10 = r11
            java.lang.String r10 = (java.lang.String) r10     // Catch:{ all -> 0x01a1 }
        L_0x015c:
            r10 = r11
            r11 = r5
            java.net.Socket r11 = (java.net.Socket) r11     // Catch:{ all -> 0x01a1 }
            r1.socket = r11     // Catch:{ all -> 0x01a1 }
            r11 = r5
            java.net.Socket r11 = (java.net.Socket) r11     // Catch:{ all -> 0x01a1 }
            okio.Source r11 = okio.Okio.source((java.net.Socket) r11)     // Catch:{ all -> 0x01a1 }
            okio.BufferedSource r11 = okio.Okio.buffer((okio.Source) r11)     // Catch:{ all -> 0x01a1 }
            r1.source = r11     // Catch:{ all -> 0x01a1 }
            r11 = r5
            java.net.Socket r11 = (java.net.Socket) r11     // Catch:{ all -> 0x01a1 }
            okio.Sink r11 = okio.Okio.sink((java.net.Socket) r11)     // Catch:{ all -> 0x01a1 }
            okio.BufferedSink r11 = okio.Okio.buffer((okio.Sink) r11)     // Catch:{ all -> 0x01a1 }
            r1.sink = r11     // Catch:{ all -> 0x01a1 }
            if (r10 == 0) goto L_0x0185
            okhttp3.Protocol$Companion r11 = okhttp3.Protocol.Companion     // Catch:{ all -> 0x01a1 }
            okhttp3.Protocol r11 = r11.get(r10)     // Catch:{ all -> 0x01a1 }
            goto L_0x0187
        L_0x0185:
            okhttp3.Protocol r11 = okhttp3.Protocol.HTTP_1_1     // Catch:{ all -> 0x01a1 }
        L_0x0187:
            r1.protocol = r11     // Catch:{ all -> 0x01a1 }
            r0 = 1
            okhttp3.internal.platform.Platform$Companion r4 = okhttp3.internal.platform.Platform.Companion
            okhttp3.internal.platform.Platform r4 = r4.get()
            r4.afterHandshake(r5)
            return
        L_0x0197:
            r6 = r17
            java.lang.NullPointerException r0 = new java.lang.NullPointerException     // Catch:{ all -> 0x01a1 }
            java.lang.String r7 = "null cannot be cast to non-null type javax.net.ssl.SSLSocket"
            r0.<init>(r7)     // Catch:{ all -> 0x01a1 }
            throw r0     // Catch:{ all -> 0x01a1 }
        L_0x01a1:
            r0 = move-exception
            goto L_0x01a6
        L_0x01a3:
            r0 = move-exception
            r6 = r17
        L_0x01a6:
            if (r5 == 0) goto L_0x01b1
            okhttp3.internal.platform.Platform$Companion r7 = okhttp3.internal.platform.Platform.Companion
            okhttp3.internal.platform.Platform r7 = r7.get()
            r7.afterHandshake(r5)
        L_0x01b1:
            if (r5 != 0) goto L_0x01b5
            goto L_0x01bb
        L_0x01b5:
            r7 = r5
            java.net.Socket r7 = (java.net.Socket) r7
            okhttp3.internal.Util.closeQuietly((java.net.Socket) r7)
        L_0x01bb:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.RealConnection.connectTls(okhttp3.internal.connection.ConnectionSpecSelector):void");
    }

    private final Request createTunnel(int readTimeout, int writeTimeout, Request tunnelRequest, HttpUrl url) throws IOException {
        Response response;
        Request nextRequest = tunnelRequest;
        String requestLine = "CONNECT " + Util.toHostHeader(url, true) + " HTTP/1.1";
        do {
            BufferedSource source2 = this.source;
            Intrinsics.checkNotNull(source2);
            BufferedSink sink2 = this.sink;
            Intrinsics.checkNotNull(sink2);
            Http1ExchangeCodec tunnelCodec = new Http1ExchangeCodec((OkHttpClient) null, this, source2, sink2);
            source2.timeout().timeout((long) readTimeout, TimeUnit.MILLISECONDS);
            sink2.timeout().timeout((long) writeTimeout, TimeUnit.MILLISECONDS);
            tunnelCodec.writeRequest(nextRequest.headers(), requestLine);
            tunnelCodec.finishRequest();
            Response.Builder readResponseHeaders = tunnelCodec.readResponseHeaders(false);
            Intrinsics.checkNotNull(readResponseHeaders);
            response = readResponseHeaders.request(nextRequest).build();
            tunnelCodec.skipConnectBody(response);
            switch (response.code()) {
                case ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION:
                    if (source2.getBuffer().exhausted() && sink2.getBuffer().exhausted()) {
                        return null;
                    }
                    throw new IOException("TLS tunnel buffered too many bytes!");
                case 407:
                    Request authenticate = this.route.address().proxyAuthenticator().authenticate(this.route, response);
                    if (authenticate != null) {
                        nextRequest = authenticate;
                        break;
                    } else {
                        throw new IOException("Failed to authenticate with proxy");
                    }
                default:
                    throw new IOException(Intrinsics.stringPlus("Unexpected response code for CONNECT: ", Integer.valueOf(response.code())));
            }
        } while (!StringsKt.equals("close", Response.header$default(response, "Connection", (String) null, 2, (Object) null), true));
        return nextRequest;
    }

    private final Request createTunnelRequest() throws IOException {
        Request proxyConnectRequest = new Request.Builder().url(this.route.address().url()).method("CONNECT", (RequestBody) null).header("Host", Util.toHostHeader(this.route.address().url(), true)).header("Proxy-Connection", "Keep-Alive").header("User-Agent", Util.userAgent).build();
        Request authenticatedRequest = this.route.address().proxyAuthenticator().authenticate(this.route, new Response.Builder().request(proxyConnectRequest).protocol(Protocol.HTTP_1_1).code(407).message("Preemptive Authenticate").body(Util.EMPTY_RESPONSE).sentRequestAtMillis(-1).receivedResponseAtMillis(-1).header("Proxy-Authenticate", "OkHttp-Preemptive").build());
        return authenticatedRequest == null ? proxyConnectRequest : authenticatedRequest;
    }

    public final boolean isEligible$okhttp(Address address, List<Route> routes) {
        Intrinsics.checkNotNullParameter(address, "address");
        if (Util.assertionsEnabled && !Thread.holdsLock(this)) {
            throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + this);
        } else if (this.calls.size() >= this.allocationLimit || this.noNewExchanges || !this.route.address().equalsNonHost$okhttp(address)) {
            return false;
        } else {
            if (Intrinsics.areEqual((Object) address.url().host(), (Object) route().address().url().host())) {
                return true;
            }
            if (this.http2Connection == null || routes == null || !routeMatchesAny(routes) || address.hostnameVerifier() != OkHostnameVerifier.INSTANCE || !supportsUrl(address.url())) {
                return false;
            }
            try {
                CertificatePinner certificatePinner = address.certificatePinner();
                Intrinsics.checkNotNull(certificatePinner);
                String host = address.url().host();
                Handshake handshake2 = handshake();
                Intrinsics.checkNotNull(handshake2);
                certificatePinner.check(host, (List<? extends Certificate>) handshake2.peerCertificates());
                return true;
            } catch (SSLPeerUnverifiedException e) {
                return false;
            }
        }
    }

    private final boolean routeMatchesAny(List<Route> candidates) {
        boolean z;
        Iterable<Route> $this$any$iv = candidates;
        if (($this$any$iv instanceof Collection) && ((Collection) $this$any$iv).isEmpty()) {
            return false;
        }
        for (Route it : $this$any$iv) {
            if (it.proxy().type() == Proxy.Type.DIRECT && this.route.proxy().type() == Proxy.Type.DIRECT && Intrinsics.areEqual((Object) this.route.socketAddress(), (Object) it.socketAddress())) {
                z = true;
                continue;
            } else {
                z = false;
                continue;
            }
            if (z) {
                return true;
            }
        }
        return false;
    }

    private final boolean supportsUrl(HttpUrl url) {
        Handshake handshake2;
        if (!Util.assertionsEnabled || Thread.holdsLock(this)) {
            HttpUrl routeUrl = this.route.address().url();
            if (url.port() != routeUrl.port()) {
                return false;
            }
            if (Intrinsics.areEqual((Object) url.host(), (Object) routeUrl.host())) {
                return true;
            }
            if (this.noCoalescedConnections || (handshake2 = this.handshake) == null) {
                return false;
            }
            Intrinsics.checkNotNull(handshake2);
            if (certificateSupportHost(url, handshake2)) {
                return true;
            }
            return false;
        }
        throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + this);
    }

    private final boolean certificateSupportHost(HttpUrl url, Handshake handshake2) {
        List<Certificate> peerCertificates = handshake2.peerCertificates();
        return (peerCertificates.isEmpty() ^ true) && OkHostnameVerifier.INSTANCE.verify(url.host(), (X509Certificate) peerCertificates.get(0));
    }

    public final ExchangeCodec newCodec$okhttp(OkHttpClient client, RealInterceptorChain chain) throws SocketException {
        Intrinsics.checkNotNullParameter(client, "client");
        Intrinsics.checkNotNullParameter(chain, "chain");
        Socket socket2 = this.socket;
        Intrinsics.checkNotNull(socket2);
        BufferedSource source2 = this.source;
        Intrinsics.checkNotNull(source2);
        BufferedSink sink2 = this.sink;
        Intrinsics.checkNotNull(sink2);
        Http2Connection http2Connection2 = this.http2Connection;
        if (http2Connection2 != null) {
            return new Http2ExchangeCodec(client, this, chain, http2Connection2);
        }
        socket2.setSoTimeout(chain.readTimeoutMillis());
        source2.timeout().timeout((long) chain.getReadTimeoutMillis$okhttp(), TimeUnit.MILLISECONDS);
        sink2.timeout().timeout((long) chain.getWriteTimeoutMillis$okhttp(), TimeUnit.MILLISECONDS);
        return new Http1ExchangeCodec(client, this, source2, sink2);
    }

    public final RealWebSocket.Streams newWebSocketStreams$okhttp(Exchange exchange) throws SocketException {
        Intrinsics.checkNotNullParameter(exchange, "exchange");
        Socket socket2 = this.socket;
        Intrinsics.checkNotNull(socket2);
        BufferedSource source2 = this.source;
        Intrinsics.checkNotNull(source2);
        BufferedSink sink2 = this.sink;
        Intrinsics.checkNotNull(sink2);
        socket2.setSoTimeout(0);
        noNewExchanges$okhttp();
        return new RealConnection$newWebSocketStreams$1(source2, sink2, exchange);
    }

    public Route route() {
        return this.route;
    }

    public final void cancel() {
        Socket socket2 = this.rawSocket;
        if (socket2 != null) {
            Util.closeQuietly(socket2);
        }
    }

    public Socket socket() {
        Socket socket2 = this.socket;
        Intrinsics.checkNotNull(socket2);
        return socket2;
    }

    public final boolean isHealthy(boolean doExtensiveChecks) {
        long idleDurationNs;
        if (!Util.assertionsEnabled || !Thread.holdsLock(this)) {
            long nowNs = System.nanoTime();
            Socket rawSocket2 = this.rawSocket;
            Intrinsics.checkNotNull(rawSocket2);
            Socket socket2 = this.socket;
            Intrinsics.checkNotNull(socket2);
            BufferedSource source2 = this.source;
            Intrinsics.checkNotNull(source2);
            if (rawSocket2.isClosed() || socket2.isClosed() || socket2.isInputShutdown() || socket2.isOutputShutdown()) {
                return false;
            }
            Http2Connection http2Connection2 = this.http2Connection;
            if (http2Connection2 != null) {
                return http2Connection2.isHealthy(nowNs);
            }
            synchronized (this) {
                idleDurationNs = nowNs - getIdleAtNs$okhttp();
            }
            if (idleDurationNs < IDLE_CONNECTION_HEALTHY_NS || !doExtensiveChecks) {
                return true;
            }
            return Util.isHealthy(socket2, source2);
        }
        throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + this);
    }

    public void onStream(Http2Stream stream) throws IOException {
        Intrinsics.checkNotNullParameter(stream, "stream");
        stream.close(ErrorCode.REFUSED_STREAM, (IOException) null);
    }

    public synchronized void onSettings(Http2Connection connection, Settings settings) {
        Intrinsics.checkNotNullParameter(connection, "connection");
        Intrinsics.checkNotNullParameter(settings, "settings");
        this.allocationLimit = settings.getMaxConcurrentStreams();
    }

    public Handshake handshake() {
        return this.handshake;
    }

    public final void connectFailed$okhttp(OkHttpClient client, Route failedRoute, IOException failure) {
        Intrinsics.checkNotNullParameter(client, "client");
        Intrinsics.checkNotNullParameter(failedRoute, "failedRoute");
        Intrinsics.checkNotNullParameter(failure, "failure");
        if (failedRoute.proxy().type() != Proxy.Type.DIRECT) {
            Address address = failedRoute.address();
            address.proxySelector().connectFailed(address.url().uri(), failedRoute.proxy().address(), failure);
        }
        client.getRouteDatabase().failed(failedRoute);
    }

    public final synchronized void trackFailure$okhttp(RealCall call, IOException e) {
        Intrinsics.checkNotNullParameter(call, NotificationCompat.CATEGORY_CALL);
        if (e instanceof StreamResetException) {
            if (((StreamResetException) e).errorCode == ErrorCode.REFUSED_STREAM) {
                int i = this.refusedStreamCount + 1;
                this.refusedStreamCount = i;
                if (i > 1) {
                    this.noNewExchanges = true;
                    this.routeFailureCount++;
                }
            } else if (((StreamResetException) e).errorCode != ErrorCode.CANCEL || !call.isCanceled()) {
                this.noNewExchanges = true;
                this.routeFailureCount++;
            }
        } else if (!isMultiplexed$okhttp() || (e instanceof ConnectionShutdownException)) {
            this.noNewExchanges = true;
            if (this.successCount == 0) {
                if (e != null) {
                    connectFailed$okhttp(call.getClient(), this.route, e);
                }
                this.routeFailureCount++;
            }
        }
    }

    public Protocol protocol() {
        Protocol protocol2 = this.protocol;
        Intrinsics.checkNotNull(protocol2);
        return protocol2;
    }

    public String toString() {
        Object cipherSuite;
        StringBuilder append = new StringBuilder().append("Connection{").append(this.route.address().url().host()).append(':').append(this.route.address().url().port()).append(", proxy=").append(this.route.proxy()).append(" hostAddress=").append(this.route.socketAddress()).append(" cipherSuite=");
        Handshake handshake2 = this.handshake;
        Object obj = "none";
        if (!(handshake2 == null || (cipherSuite = handshake2.cipherSuite()) == null)) {
            obj = cipherSuite;
        }
        return append.append(obj).append(" protocol=").append(this.protocol).append('}').toString();
    }

    @Metadata(d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J&\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bXT¢\u0006\u0002\n\u0000¨\u0006\u0012"}, d2 = {"Lokhttp3/internal/connection/RealConnection$Companion;", "", "()V", "IDLE_CONNECTION_HEALTHY_NS", "", "MAX_TUNNEL_ATTEMPTS", "", "NPE_THROW_WITH_NULL", "", "newTestConnection", "Lokhttp3/internal/connection/RealConnection;", "connectionPool", "Lokhttp3/internal/connection/RealConnectionPool;", "route", "Lokhttp3/Route;", "socket", "Ljava/net/Socket;", "idleAtNs", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: RealConnection.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final RealConnection newTestConnection(RealConnectionPool connectionPool, Route route, Socket socket, long idleAtNs) {
            Intrinsics.checkNotNullParameter(connectionPool, "connectionPool");
            Intrinsics.checkNotNullParameter(route, "route");
            Intrinsics.checkNotNullParameter(socket, "socket");
            RealConnection result = new RealConnection(connectionPool, route);
            result.socket = socket;
            result.setIdleAtNs$okhttp(idleAtNs);
            return result;
        }
    }
}

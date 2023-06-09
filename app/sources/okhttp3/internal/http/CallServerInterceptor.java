package okhttp3.internal.http;

import java.io.IOException;
import java.net.ProtocolException;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okhttp3.internal.connection.Exchange;
import okio.BufferedSink;
import okio.Okio;

@Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000¨\u0006\t"}, d2 = {"Lokhttp3/internal/http/CallServerInterceptor;", "Lokhttp3/Interceptor;", "forWebSocket", "", "(Z)V", "intercept", "Lokhttp3/Response;", "chain", "Lokhttp3/Interceptor$Chain;", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: CallServerInterceptor.kt */
public final class CallServerInterceptor implements Interceptor {
    private final boolean forWebSocket;

    public CallServerInterceptor(boolean forWebSocket2) {
        this.forWebSocket = forWebSocket2;
    }

    public Response intercept(Interceptor.Chain chain) throws IOException {
        Response response;
        Interceptor.Chain chain2 = chain;
        Intrinsics.checkNotNullParameter(chain2, "chain");
        RealInterceptorChain realChain = (RealInterceptorChain) chain2;
        Exchange exchange = realChain.getExchange$okhttp();
        Intrinsics.checkNotNull(exchange);
        Request request = realChain.getRequest$okhttp();
        RequestBody requestBody = request.body();
        long sentRequestMillis = System.currentTimeMillis();
        exchange.writeRequestHeaders(request);
        boolean invokeStartEvent = true;
        Response.Builder responseBuilder = null;
        if (!HttpMethod.permitsRequestBody(request.method()) || requestBody == null) {
            exchange.noRequestBody();
        } else {
            if (StringsKt.equals("100-continue", request.header("Expect"), true)) {
                exchange.flushRequest();
                responseBuilder = exchange.readResponseHeaders(true);
                exchange.responseHeadersStart();
                invokeStartEvent = false;
            }
            if (responseBuilder != null) {
                exchange.noRequestBody();
                if (!exchange.getConnection$okhttp().isMultiplexed$okhttp()) {
                    exchange.noNewExchangesOnConnection();
                }
            } else if (requestBody.isDuplex()) {
                exchange.flushRequest();
                requestBody.writeTo(Okio.buffer(exchange.createRequestBody(request, true)));
            } else {
                BufferedSink bufferedRequestBody = Okio.buffer(exchange.createRequestBody(request, false));
                requestBody.writeTo(bufferedRequestBody);
                bufferedRequestBody.close();
            }
        }
        if (requestBody == null || !requestBody.isDuplex()) {
            exchange.finishRequest();
        }
        if (responseBuilder == null) {
            Response.Builder readResponseHeaders = exchange.readResponseHeaders(false);
            Intrinsics.checkNotNull(readResponseHeaders);
            responseBuilder = readResponseHeaders;
            if (invokeStartEvent) {
                exchange.responseHeadersStart();
                invokeStartEvent = false;
            }
        }
        Response response2 = responseBuilder.request(request).handshake(exchange.getConnection$okhttp().handshake()).sentRequestAtMillis(sentRequestMillis).receivedResponseAtMillis(System.currentTimeMillis()).build();
        int code = response2.code();
        if (code == 100) {
            Response.Builder readResponseHeaders2 = exchange.readResponseHeaders(false);
            Intrinsics.checkNotNull(readResponseHeaders2);
            Response.Builder responseBuilder2 = readResponseHeaders2;
            if (invokeStartEvent) {
                exchange.responseHeadersStart();
            }
            response2 = responseBuilder2.request(request).handshake(exchange.getConnection$okhttp().handshake()).sentRequestAtMillis(sentRequestMillis).receivedResponseAtMillis(System.currentTimeMillis()).build();
            code = response2.code();
        }
        exchange.responseHeadersEnd(response2);
        if (!this.forWebSocket || code != 101) {
            response = response2.newBuilder().body(exchange.openResponseBody(response2)).build();
        } else {
            response = response2.newBuilder().body(Util.EMPTY_RESPONSE).build();
        }
        Response response3 = response;
        Long l = null;
        if (StringsKt.equals("close", response3.request().header("Connection"), true) || StringsKt.equals("close", Response.header$default(response3, "Connection", (String) null, 2, (Object) null), true)) {
            exchange.noNewExchangesOnConnection();
        }
        if (code == 204 || code == 205) {
            ResponseBody body = response3.body();
            if ((body == null ? -1 : body.contentLength()) > 0) {
                StringBuilder append = new StringBuilder().append("HTTP ").append(code).append(" had non-zero Content-Length: ");
                ResponseBody body2 = response3.body();
                if (body2 != null) {
                    l = Long.valueOf(body2.contentLength());
                }
                throw new ProtocolException(append.append(l).toString());
            }
        }
        return response3;
    }
}

package okhttp3;

import java.util.concurrent.TimeUnit;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\n\n\u0002\u0010\u000e\n\u0002\b\u0011\u0018\u0000 !2\u00020\u0001:\u0002 !Bq\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u0003\u0012\u0006\u0010\u000b\u001a\u00020\u0006\u0012\u0006\u0010\f\u001a\u00020\u0006\u0012\u0006\u0010\r\u001a\u00020\u0003\u0012\u0006\u0010\u000e\u001a\u00020\u0003\u0012\u0006\u0010\u000f\u001a\u00020\u0003\u0012\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011¢\u0006\u0002\u0010\u0012J\r\u0010\u000f\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u0015J\r\u0010\u0005\u001a\u00020\u0006H\u0007¢\u0006\u0002\b\u0016J\r\u0010\u000b\u001a\u00020\u0006H\u0007¢\u0006\u0002\b\u0017J\r\u0010\f\u001a\u00020\u0006H\u0007¢\u0006\u0002\b\u0018J\r\u0010\n\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u0019J\r\u0010\u0002\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u001aJ\r\u0010\u0004\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u001bJ\r\u0010\u000e\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u001cJ\r\u0010\r\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u001dJ\r\u0010\u0007\u001a\u00020\u0006H\u0007¢\u0006\u0002\b\u001eJ\b\u0010\u001f\u001a\u00020\u0011H\u0016R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u000e¢\u0006\u0002\n\u0000R\u0013\u0010\u000f\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0013R\u0011\u0010\b\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0013R\u0011\u0010\t\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0013R\u0013\u0010\u0005\u001a\u00020\u00068\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0014R\u0013\u0010\u000b\u001a\u00020\u00068\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\u0014R\u0013\u0010\f\u001a\u00020\u00068\u0007¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u0014R\u0013\u0010\n\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0013R\u0013\u0010\u0002\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0013R\u0013\u0010\u0004\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0013R\u0013\u0010\u000e\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u0013R\u0013\u0010\r\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u0013R\u0013\u0010\u0007\u001a\u00020\u00068\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\u0014¨\u0006\""}, d2 = {"Lokhttp3/CacheControl;", "", "noCache", "", "noStore", "maxAgeSeconds", "", "sMaxAgeSeconds", "isPrivate", "isPublic", "mustRevalidate", "maxStaleSeconds", "minFreshSeconds", "onlyIfCached", "noTransform", "immutable", "headerValue", "", "(ZZIIZZZIIZZZLjava/lang/String;)V", "()Z", "()I", "-deprecated_immutable", "-deprecated_maxAgeSeconds", "-deprecated_maxStaleSeconds", "-deprecated_minFreshSeconds", "-deprecated_mustRevalidate", "-deprecated_noCache", "-deprecated_noStore", "-deprecated_noTransform", "-deprecated_onlyIfCached", "-deprecated_sMaxAgeSeconds", "toString", "Builder", "Companion", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: CacheControl.kt */
public final class CacheControl {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    public static final CacheControl FORCE_CACHE = new Builder().onlyIfCached().maxStale(Integer.MAX_VALUE, TimeUnit.SECONDS).build();
    public static final CacheControl FORCE_NETWORK = new Builder().noCache().build();
    private String headerValue;
    private final boolean immutable;
    private final boolean isPrivate;
    private final boolean isPublic;
    private final int maxAgeSeconds;
    private final int maxStaleSeconds;
    private final int minFreshSeconds;
    private final boolean mustRevalidate;
    private final boolean noCache;
    private final boolean noStore;
    private final boolean noTransform;
    private final boolean onlyIfCached;
    private final int sMaxAgeSeconds;

    public /* synthetic */ CacheControl(boolean z, boolean z2, int i, int i2, boolean z3, boolean z4, boolean z5, int i3, int i4, boolean z6, boolean z7, boolean z8, String str, DefaultConstructorMarker defaultConstructorMarker) {
        this(z, z2, i, i2, z3, z4, z5, i3, i4, z6, z7, z8, str);
    }

    @JvmStatic
    public static final CacheControl parse(Headers headers) {
        return Companion.parse(headers);
    }

    private CacheControl(boolean noCache2, boolean noStore2, int maxAgeSeconds2, int sMaxAgeSeconds2, boolean isPrivate2, boolean isPublic2, boolean mustRevalidate2, int maxStaleSeconds2, int minFreshSeconds2, boolean onlyIfCached2, boolean noTransform2, boolean immutable2, String headerValue2) {
        this.noCache = noCache2;
        this.noStore = noStore2;
        this.maxAgeSeconds = maxAgeSeconds2;
        this.sMaxAgeSeconds = sMaxAgeSeconds2;
        this.isPrivate = isPrivate2;
        this.isPublic = isPublic2;
        this.mustRevalidate = mustRevalidate2;
        this.maxStaleSeconds = maxStaleSeconds2;
        this.minFreshSeconds = minFreshSeconds2;
        this.onlyIfCached = onlyIfCached2;
        this.noTransform = noTransform2;
        this.immutable = immutable2;
        this.headerValue = headerValue2;
    }

    public final boolean noCache() {
        return this.noCache;
    }

    public final boolean noStore() {
        return this.noStore;
    }

    public final int maxAgeSeconds() {
        return this.maxAgeSeconds;
    }

    public final int sMaxAgeSeconds() {
        return this.sMaxAgeSeconds;
    }

    public final boolean isPrivate() {
        return this.isPrivate;
    }

    public final boolean isPublic() {
        return this.isPublic;
    }

    public final boolean mustRevalidate() {
        return this.mustRevalidate;
    }

    public final int maxStaleSeconds() {
        return this.maxStaleSeconds;
    }

    public final int minFreshSeconds() {
        return this.minFreshSeconds;
    }

    public final boolean onlyIfCached() {
        return this.onlyIfCached;
    }

    public final boolean noTransform() {
        return this.noTransform;
    }

    public final boolean immutable() {
        return this.immutable;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "noCache", imports = {}))
    /* renamed from: -deprecated_noCache  reason: not valid java name */
    public final boolean m1816deprecated_noCache() {
        return this.noCache;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "noStore", imports = {}))
    /* renamed from: -deprecated_noStore  reason: not valid java name */
    public final boolean m1817deprecated_noStore() {
        return this.noStore;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "maxAgeSeconds", imports = {}))
    /* renamed from: -deprecated_maxAgeSeconds  reason: not valid java name */
    public final int m1812deprecated_maxAgeSeconds() {
        return this.maxAgeSeconds;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "sMaxAgeSeconds", imports = {}))
    /* renamed from: -deprecated_sMaxAgeSeconds  reason: not valid java name */
    public final int m1820deprecated_sMaxAgeSeconds() {
        return this.sMaxAgeSeconds;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "mustRevalidate", imports = {}))
    /* renamed from: -deprecated_mustRevalidate  reason: not valid java name */
    public final boolean m1815deprecated_mustRevalidate() {
        return this.mustRevalidate;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "maxStaleSeconds", imports = {}))
    /* renamed from: -deprecated_maxStaleSeconds  reason: not valid java name */
    public final int m1813deprecated_maxStaleSeconds() {
        return this.maxStaleSeconds;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "minFreshSeconds", imports = {}))
    /* renamed from: -deprecated_minFreshSeconds  reason: not valid java name */
    public final int m1814deprecated_minFreshSeconds() {
        return this.minFreshSeconds;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "onlyIfCached", imports = {}))
    /* renamed from: -deprecated_onlyIfCached  reason: not valid java name */
    public final boolean m1819deprecated_onlyIfCached() {
        return this.onlyIfCached;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "noTransform", imports = {}))
    /* renamed from: -deprecated_noTransform  reason: not valid java name */
    public final boolean m1818deprecated_noTransform() {
        return this.noTransform;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "immutable", imports = {}))
    /* renamed from: -deprecated_immutable  reason: not valid java name */
    public final boolean m1811deprecated_immutable() {
        return this.immutable;
    }

    public String toString() {
        String result = this.headerValue;
        if (result != null) {
            return result;
        }
        StringBuilder sb = new StringBuilder();
        StringBuilder $this$toString_u24lambda_u2d0 = sb;
        if (noCache()) {
            $this$toString_u24lambda_u2d0.append("no-cache, ");
        }
        if (noStore()) {
            $this$toString_u24lambda_u2d0.append("no-store, ");
        }
        if (maxAgeSeconds() != -1) {
            $this$toString_u24lambda_u2d0.append("max-age=").append(maxAgeSeconds()).append(", ");
        }
        if (sMaxAgeSeconds() != -1) {
            $this$toString_u24lambda_u2d0.append("s-maxage=").append(sMaxAgeSeconds()).append(", ");
        }
        if (isPrivate()) {
            $this$toString_u24lambda_u2d0.append("private, ");
        }
        if (isPublic()) {
            $this$toString_u24lambda_u2d0.append("public, ");
        }
        if (mustRevalidate()) {
            $this$toString_u24lambda_u2d0.append("must-revalidate, ");
        }
        if (maxStaleSeconds() != -1) {
            $this$toString_u24lambda_u2d0.append("max-stale=").append(maxStaleSeconds()).append(", ");
        }
        if (minFreshSeconds() != -1) {
            $this$toString_u24lambda_u2d0.append("min-fresh=").append(minFreshSeconds()).append(", ");
        }
        if (onlyIfCached()) {
            $this$toString_u24lambda_u2d0.append("only-if-cached, ");
        }
        if (noTransform()) {
            $this$toString_u24lambda_u2d0.append("no-transform, ");
        }
        if (immutable()) {
            $this$toString_u24lambda_u2d0.append("immutable, ");
        }
        if ($this$toString_u24lambda_u2d0.length() == 0) {
            return HttpUrl.FRAGMENT_ENCODE_SET;
        }
        $this$toString_u24lambda_u2d0.delete($this$toString_u24lambda_u2d0.length() - 2, $this$toString_u24lambda_u2d0.length());
        String sb2 = sb.toString();
        Intrinsics.checkNotNullExpressionValue(sb2, "StringBuilder().apply(builderAction).toString()");
        String result2 = sb2;
        this.headerValue = result2;
        return result2;
    }

    @Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0006\u0010\r\u001a\u00020\u000eJ\u0006\u0010\u0003\u001a\u00020\u0000J\u0016\u0010\u000f\u001a\u00020\u00002\u0006\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u0011J\u0016\u0010\u0012\u001a\u00020\u00002\u0006\u0010\u0012\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u0011J\u0016\u0010\u0013\u001a\u00020\u00002\u0006\u0010\u0013\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u0011J\u0006\u0010\t\u001a\u00020\u0000J\u0006\u0010\n\u001a\u00020\u0000J\u0006\u0010\u000b\u001a\u00020\u0000J\u0006\u0010\f\u001a\u00020\u0000J\f\u0010\u0014\u001a\u00020\u0006*\u00020\u0015H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0004X\u000e¢\u0006\u0002\n\u0000¨\u0006\u0016"}, d2 = {"Lokhttp3/CacheControl$Builder;", "", "()V", "immutable", "", "maxAgeSeconds", "", "maxStaleSeconds", "minFreshSeconds", "noCache", "noStore", "noTransform", "onlyIfCached", "build", "Lokhttp3/CacheControl;", "maxAge", "timeUnit", "Ljava/util/concurrent/TimeUnit;", "maxStale", "minFresh", "clampToInt", "", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: CacheControl.kt */
    public static final class Builder {
        private boolean immutable;
        private int maxAgeSeconds = -1;
        private int maxStaleSeconds = -1;
        private int minFreshSeconds = -1;
        private boolean noCache;
        private boolean noStore;
        private boolean noTransform;
        private boolean onlyIfCached;

        public final Builder noCache() {
            this.noCache = true;
            return this;
        }

        public final Builder noStore() {
            this.noStore = true;
            return this;
        }

        public final Builder maxAge(int maxAge, TimeUnit timeUnit) {
            Intrinsics.checkNotNullParameter(timeUnit, "timeUnit");
            Builder $this$maxAge_u24lambda_u2d3 = this;
            if (maxAge >= 0) {
                $this$maxAge_u24lambda_u2d3.maxAgeSeconds = $this$maxAge_u24lambda_u2d3.clampToInt(timeUnit.toSeconds((long) maxAge));
                return this;
            }
            throw new IllegalArgumentException(Intrinsics.stringPlus("maxAge < 0: ", Integer.valueOf(maxAge)).toString());
        }

        public final Builder maxStale(int maxStale, TimeUnit timeUnit) {
            Intrinsics.checkNotNullParameter(timeUnit, "timeUnit");
            Builder $this$maxStale_u24lambda_u2d5 = this;
            if (maxStale >= 0) {
                $this$maxStale_u24lambda_u2d5.maxStaleSeconds = $this$maxStale_u24lambda_u2d5.clampToInt(timeUnit.toSeconds((long) maxStale));
                return this;
            }
            throw new IllegalArgumentException(Intrinsics.stringPlus("maxStale < 0: ", Integer.valueOf(maxStale)).toString());
        }

        public final Builder minFresh(int minFresh, TimeUnit timeUnit) {
            Intrinsics.checkNotNullParameter(timeUnit, "timeUnit");
            Builder $this$minFresh_u24lambda_u2d7 = this;
            if (minFresh >= 0) {
                $this$minFresh_u24lambda_u2d7.minFreshSeconds = $this$minFresh_u24lambda_u2d7.clampToInt(timeUnit.toSeconds((long) minFresh));
                return this;
            }
            throw new IllegalArgumentException(Intrinsics.stringPlus("minFresh < 0: ", Integer.valueOf(minFresh)).toString());
        }

        public final Builder onlyIfCached() {
            this.onlyIfCached = true;
            return this;
        }

        public final Builder noTransform() {
            this.noTransform = true;
            return this;
        }

        public final Builder immutable() {
            this.immutable = true;
            return this;
        }

        private final int clampToInt(long $this$clampToInt) {
            if ($this$clampToInt > 2147483647L) {
                return Integer.MAX_VALUE;
            }
            return (int) $this$clampToInt;
        }

        public final CacheControl build() {
            return new CacheControl(this.noCache, this.noStore, this.maxAgeSeconds, -1, false, false, false, this.maxStaleSeconds, this.minFreshSeconds, this.onlyIfCached, this.noTransform, this.immutable, (String) null, (DefaultConstructorMarker) null);
        }
    }

    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0006\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\bH\u0007J\u001e\u0010\t\u001a\u00020\n*\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\b\b\u0002\u0010\r\u001a\u00020\nH\u0002R\u0010\u0010\u0003\u001a\u00020\u00048\u0006X\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u00020\u00048\u0006X\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"Lokhttp3/CacheControl$Companion;", "", "()V", "FORCE_CACHE", "Lokhttp3/CacheControl;", "FORCE_NETWORK", "parse", "headers", "Lokhttp3/Headers;", "indexOfElement", "", "", "characters", "startIndex", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: CacheControl.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        /* JADX WARNING: Removed duplicated region for block: B:29:0x00f8  */
        /* JADX WARNING: Removed duplicated region for block: B:30:0x0103  */
        @kotlin.jvm.JvmStatic
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final okhttp3.CacheControl parse(okhttp3.Headers r36) {
            /*
                r35 = this;
                r0 = r35
                r1 = r36
                java.lang.String r2 = "headers"
                kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r1, r2)
                r2 = 0
                r3 = 0
                r4 = -1
                r5 = -1
                r6 = 0
                r7 = 0
                r8 = 0
                r9 = -1
                r10 = -1
                r11 = 0
                r12 = 0
                r13 = 0
                r14 = 1
                r15 = 0
                r16 = r2
                int r2 = r36.size()
                r17 = 0
                r34 = r17
                r17 = r13
                r13 = r34
            L_0x0025:
                if (r13 >= r2) goto L_0x0200
                r18 = r13
                int r13 = r13 + 1
                r19 = r2
                r2 = r18
                r18 = r13
                java.lang.String r13 = r1.name(r2)
                r33 = r12
                java.lang.String r12 = r1.value(r2)
                java.lang.String r1 = "Cache-Control"
                r20 = r2
                r2 = 1
                boolean r1 = kotlin.text.StringsKt.equals(r13, r1, r2)
                if (r1 == 0) goto L_0x004f
                if (r15 == 0) goto L_0x004c
                r1 = 0
                r14 = r1
                goto L_0x0059
            L_0x004c:
                r1 = r12
                r15 = r1
                goto L_0x0059
            L_0x004f:
                java.lang.String r1 = "Pragma"
                boolean r1 = kotlin.text.StringsKt.equals(r13, r1, r2)
                if (r1 == 0) goto L_0x01f6
                r1 = 0
                r14 = r1
            L_0x0059:
                r1 = 0
            L_0x005a:
                int r2 = r12.length()
                if (r1 >= r2) goto L_0x01e6
                r2 = r1
                r22 = r3
                java.lang.String r3 = "=,;"
                int r1 = r0.indexOfElement(r12, r3, r1)
                java.lang.String r3 = r12.substring(r2, r1)
                r23 = r2
                java.lang.String r2 = "this as java.lang.String…ing(startIndex, endIndex)"
                kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r3, r2)
                java.lang.CharSequence r3 = (java.lang.CharSequence) r3
                java.lang.CharSequence r3 = kotlin.text.StringsKt.trim((java.lang.CharSequence) r3)
                java.lang.String r3 = r3.toString()
                r24 = 0
                r25 = r4
                int r4 = r12.length()
                if (r1 == r4) goto L_0x00e9
                char r4 = r12.charAt(r1)
                r26 = r5
                r5 = 44
                if (r4 == r5) goto L_0x00eb
                char r4 = r12.charAt(r1)
                r5 = 59
                if (r4 != r5) goto L_0x009c
                goto L_0x00eb
            L_0x009c:
                int r1 = r1 + 1
                int r1 = okhttp3.internal.Util.indexOfNonWhitespace(r12, r1)
                int r4 = r12.length()
                if (r1 >= r4) goto L_0x00d0
                char r4 = r12.charAt(r1)
                r5 = 34
                if (r4 != r5) goto L_0x00d0
                int r1 = r1 + 1
                r4 = r1
                r27 = r12
                java.lang.CharSequence r27 = (java.lang.CharSequence) r27
                r28 = 34
                r30 = 0
                r31 = 4
                r32 = 0
                r29 = r1
                int r1 = kotlin.text.StringsKt.indexOf$default((java.lang.CharSequence) r27, (char) r28, (int) r29, (boolean) r30, (int) r31, (java.lang.Object) r32)
                java.lang.String r5 = r12.substring(r4, r1)
                kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r5, r2)
                r2 = r5
                r5 = 1
                int r1 = r1 + r5
                goto L_0x00ee
            L_0x00d0:
                r4 = r1
                java.lang.String r5 = ",;"
                int r1 = r0.indexOfElement(r12, r5, r1)
                java.lang.String r5 = r12.substring(r4, r1)
                kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r5, r2)
                java.lang.CharSequence r5 = (java.lang.CharSequence) r5
                java.lang.CharSequence r2 = kotlin.text.StringsKt.trim((java.lang.CharSequence) r5)
                java.lang.String r2 = r2.toString()
                goto L_0x00ee
            L_0x00e9:
                r26 = r5
            L_0x00eb:
                int r1 = r1 + 1
                r2 = 0
            L_0x00ee:
                java.lang.String r4 = "no-cache"
                r5 = 1
                boolean r4 = kotlin.text.StringsKt.equals(r4, r3, r5)
                if (r4 == 0) goto L_0x0103
                r16 = 1
                r2 = r5
                r3 = r22
                r4 = r25
                r5 = r26
                goto L_0x005a
            L_0x0103:
                java.lang.String r4 = "no-store"
                boolean r4 = kotlin.text.StringsKt.equals(r4, r3, r5)
                if (r4 == 0) goto L_0x0114
                r4 = 1
                r3 = r4
                r2 = r5
                r4 = r25
                r5 = r26
                goto L_0x005a
            L_0x0114:
                java.lang.String r4 = "max-age"
                boolean r4 = kotlin.text.StringsKt.equals(r4, r3, r5)
                r5 = -1
                if (r4 == 0) goto L_0x0128
                int r4 = okhttp3.internal.Util.toNonNegativeInt(r2, r5)
                r3 = r22
                r5 = r26
                r2 = 1
                goto L_0x005a
            L_0x0128:
                java.lang.String r4 = "s-maxage"
                r5 = 1
                boolean r4 = kotlin.text.StringsKt.equals(r4, r3, r5)
                if (r4 == 0) goto L_0x013f
                r4 = -1
                int r4 = okhttp3.internal.Util.toNonNegativeInt(r2, r4)
                r2 = r5
                r3 = r22
                r5 = r4
                r4 = r25
                goto L_0x005a
            L_0x013f:
                java.lang.String r4 = "private"
                boolean r4 = kotlin.text.StringsKt.equals(r4, r3, r5)
                if (r4 == 0) goto L_0x0152
                r6 = 1
                r2 = r5
                r3 = r22
                r4 = r25
                r5 = r26
                goto L_0x005a
            L_0x0152:
                java.lang.String r4 = "public"
                boolean r4 = kotlin.text.StringsKt.equals(r4, r3, r5)
                if (r4 == 0) goto L_0x0165
                r7 = 1
                r2 = r5
                r3 = r22
                r4 = r25
                r5 = r26
                goto L_0x005a
            L_0x0165:
                java.lang.String r4 = "must-revalidate"
                boolean r4 = kotlin.text.StringsKt.equals(r4, r3, r5)
                if (r4 == 0) goto L_0x0177
                r8 = 1
                r2 = r5
                r3 = r22
                r4 = r25
                r5 = r26
                goto L_0x005a
            L_0x0177:
                java.lang.String r4 = "max-stale"
                boolean r4 = kotlin.text.StringsKt.equals(r4, r3, r5)
                if (r4 == 0) goto L_0x018f
                r4 = 2147483647(0x7fffffff, float:NaN)
                int r9 = okhttp3.internal.Util.toNonNegativeInt(r2, r4)
                r2 = r5
                r3 = r22
                r4 = r25
                r5 = r26
                goto L_0x005a
            L_0x018f:
                java.lang.String r4 = "min-fresh"
                boolean r4 = kotlin.text.StringsKt.equals(r4, r3, r5)
                if (r4 == 0) goto L_0x01a5
                r4 = -1
                int r10 = okhttp3.internal.Util.toNonNegativeInt(r2, r4)
                r2 = r5
                r3 = r22
                r4 = r25
                r5 = r26
                goto L_0x005a
            L_0x01a5:
                java.lang.String r4 = "only-if-cached"
                boolean r4 = kotlin.text.StringsKt.equals(r4, r3, r5)
                if (r4 == 0) goto L_0x01b7
                r11 = 1
                r2 = r5
                r3 = r22
                r4 = r25
                r5 = r26
                goto L_0x005a
            L_0x01b7:
                java.lang.String r4 = "no-transform"
                boolean r4 = kotlin.text.StringsKt.equals(r4, r3, r5)
                if (r4 == 0) goto L_0x01ca
                r33 = 1
                r2 = r5
                r3 = r22
                r4 = r25
                r5 = r26
                goto L_0x005a
            L_0x01ca:
                java.lang.String r4 = "immutable"
                boolean r4 = kotlin.text.StringsKt.equals(r4, r3, r5)
                if (r4 == 0) goto L_0x01dd
                r17 = 1
                r2 = r5
                r3 = r22
                r4 = r25
                r5 = r26
                goto L_0x005a
            L_0x01dd:
                r2 = r5
                r3 = r22
                r4 = r25
                r5 = r26
                goto L_0x005a
            L_0x01e6:
                r22 = r3
                r25 = r4
                r26 = r5
                r1 = r36
                r13 = r18
                r2 = r19
                r12 = r33
                goto L_0x0025
            L_0x01f6:
                r1 = r36
                r13 = r18
                r2 = r19
                r12 = r33
                goto L_0x0025
            L_0x0200:
                r33 = r12
                if (r14 != 0) goto L_0x0205
                r15 = 0
            L_0x0205:
                okhttp3.CacheControl r1 = new okhttp3.CacheControl
                r32 = 0
                r18 = r1
                r19 = r16
                r20 = r3
                r21 = r4
                r22 = r5
                r23 = r6
                r24 = r7
                r25 = r8
                r26 = r9
                r27 = r10
                r28 = r11
                r29 = r33
                r30 = r17
                r31 = r15
                r18.<init>(r19, r20, r21, r22, r23, r24, r25, r26, r27, r28, r29, r30, r31, r32)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.CacheControl.Companion.parse(okhttp3.Headers):okhttp3.CacheControl");
        }

        static /* synthetic */ int indexOfElement$default(Companion companion, String str, String str2, int i, int i2, Object obj) {
            if ((i2 & 2) != 0) {
                i = 0;
            }
            return companion.indexOfElement(str, str2, i);
        }

        private final int indexOfElement(String $this$indexOfElement, String characters, int startIndex) {
            int length = $this$indexOfElement.length();
            int i = startIndex;
            while (i < length) {
                int i2 = i;
                i++;
                if (StringsKt.contains$default((CharSequence) characters, $this$indexOfElement.charAt(i2), false, 2, (Object) null)) {
                    return i2;
                }
            }
            return $this$indexOfElement.length();
        }
    }
}

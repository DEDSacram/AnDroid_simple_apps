package okhttp3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import okhttp3.internal.HostnamesKt;
import okhttp3.internal.Util;
import okhttp3.internal.http.DatesKt;
import okhttp3.internal.publicsuffix.PublicSuffixDatabase;

@Metadata(d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\f\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000b\u0018\u0000 &2\u00020\u0001:\u0002%&BO\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\n\u0012\u0006\u0010\f\u001a\u00020\n\u0012\u0006\u0010\r\u001a\u00020\n¢\u0006\u0002\u0010\u000eJ\r\u0010\u0007\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u0012J\u0013\u0010\u0013\u001a\u00020\n2\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u0002J\r\u0010\u0005\u001a\u00020\u0006H\u0007¢\u0006\u0002\b\u0015J\b\u0010\u0016\u001a\u00020\u0017H\u0017J\r\u0010\r\u001a\u00020\nH\u0007¢\u0006\u0002\b\u0018J\r\u0010\u000b\u001a\u00020\nH\u0007¢\u0006\u0002\b\u0019J\u000e\u0010\u001a\u001a\u00020\n2\u0006\u0010\u001b\u001a\u00020\u001cJ\r\u0010\u0002\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u001dJ\r\u0010\b\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u001eJ\r\u0010\f\u001a\u00020\nH\u0007¢\u0006\u0002\b\u001fJ\r\u0010\t\u001a\u00020\nH\u0007¢\u0006\u0002\b J\b\u0010!\u001a\u00020\u0003H\u0016J\u0015\u0010!\u001a\u00020\u00032\u0006\u0010\"\u001a\u00020\nH\u0000¢\u0006\u0002\b#J\r\u0010\u0004\u001a\u00020\u0003H\u0007¢\u0006\u0002\b$R\u0013\u0010\u0007\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\u000fR\u0013\u0010\u0005\u001a\u00020\u00068\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0010R\u0013\u0010\r\u001a\u00020\n8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u0011R\u0013\u0010\u000b\u001a\u00020\n8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\u0011R\u0013\u0010\u0002\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u000fR\u0013\u0010\b\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u000fR\u0013\u0010\f\u001a\u00020\n8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u0011R\u0013\u0010\t\u001a\u00020\n8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0011R\u0013\u0010\u0004\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u000f¨\u0006'"}, d2 = {"Lokhttp3/Cookie;", "", "name", "", "value", "expiresAt", "", "domain", "path", "secure", "", "httpOnly", "persistent", "hostOnly", "(Ljava/lang/String;Ljava/lang/String;JLjava/lang/String;Ljava/lang/String;ZZZZ)V", "()Ljava/lang/String;", "()J", "()Z", "-deprecated_domain", "equals", "other", "-deprecated_expiresAt", "hashCode", "", "-deprecated_hostOnly", "-deprecated_httpOnly", "matches", "url", "Lokhttp3/HttpUrl;", "-deprecated_name", "-deprecated_path", "-deprecated_persistent", "-deprecated_secure", "toString", "forObsoleteRfc2965", "toString$okhttp", "-deprecated_value", "Builder", "Companion", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: Cookie.kt */
public final class Cookie {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    /* access modifiers changed from: private */
    public static final Pattern DAY_OF_MONTH_PATTERN = Pattern.compile("(\\d{1,2})[^\\d]*");
    /* access modifiers changed from: private */
    public static final Pattern MONTH_PATTERN = Pattern.compile("(?i)(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec).*");
    /* access modifiers changed from: private */
    public static final Pattern TIME_PATTERN = Pattern.compile("(\\d{1,2}):(\\d{1,2}):(\\d{1,2})[^\\d]*");
    /* access modifiers changed from: private */
    public static final Pattern YEAR_PATTERN = Pattern.compile("(\\d{2,4})[^\\d]*");
    private final String domain;
    private final long expiresAt;
    private final boolean hostOnly;
    private final boolean httpOnly;
    private final String name;
    private final String path;
    private final boolean persistent;
    private final boolean secure;
    private final String value;

    public /* synthetic */ Cookie(String str, String str2, long j, String str3, String str4, boolean z, boolean z2, boolean z3, boolean z4, DefaultConstructorMarker defaultConstructorMarker) {
        this(str, str2, j, str3, str4, z, z2, z3, z4);
    }

    @JvmStatic
    public static final Cookie parse(HttpUrl httpUrl, String str) {
        return Companion.parse(httpUrl, str);
    }

    @JvmStatic
    public static final List<Cookie> parseAll(HttpUrl httpUrl, Headers headers) {
        return Companion.parseAll(httpUrl, headers);
    }

    private Cookie(String name2, String value2, long expiresAt2, String domain2, String path2, boolean secure2, boolean httpOnly2, boolean persistent2, boolean hostOnly2) {
        this.name = name2;
        this.value = value2;
        this.expiresAt = expiresAt2;
        this.domain = domain2;
        this.path = path2;
        this.secure = secure2;
        this.httpOnly = httpOnly2;
        this.persistent = persistent2;
        this.hostOnly = hostOnly2;
    }

    public final String name() {
        return this.name;
    }

    public final String value() {
        return this.value;
    }

    public final long expiresAt() {
        return this.expiresAt;
    }

    public final String domain() {
        return this.domain;
    }

    public final String path() {
        return this.path;
    }

    public final boolean secure() {
        return this.secure;
    }

    public final boolean httpOnly() {
        return this.httpOnly;
    }

    public final boolean persistent() {
        return this.persistent;
    }

    public final boolean hostOnly() {
        return this.hostOnly;
    }

    public final boolean matches(HttpUrl url) {
        boolean domainMatch;
        Intrinsics.checkNotNullParameter(url, "url");
        if (this.hostOnly) {
            domainMatch = Intrinsics.areEqual((Object) url.host(), (Object) this.domain);
        } else {
            domainMatch = Companion.domainMatch(url.host(), this.domain);
        }
        if (!domainMatch || !Companion.pathMatch(url, this.path)) {
            return false;
        }
        if (!this.secure || url.isHttps()) {
            return true;
        }
        return false;
    }

    public boolean equals(Object other) {
        return (other instanceof Cookie) && Intrinsics.areEqual((Object) ((Cookie) other).name, (Object) this.name) && Intrinsics.areEqual((Object) ((Cookie) other).value, (Object) this.value) && ((Cookie) other).expiresAt == this.expiresAt && Intrinsics.areEqual((Object) ((Cookie) other).domain, (Object) this.domain) && Intrinsics.areEqual((Object) ((Cookie) other).path, (Object) this.path) && ((Cookie) other).secure == this.secure && ((Cookie) other).httpOnly == this.httpOnly && ((Cookie) other).persistent == this.persistent && ((Cookie) other).hostOnly == this.hostOnly;
    }

    public int hashCode() {
        return (((((((((((((((((17 * 31) + this.name.hashCode()) * 31) + this.value.hashCode()) * 31) + Long.hashCode(this.expiresAt)) * 31) + this.domain.hashCode()) * 31) + this.path.hashCode()) * 31) + Boolean.hashCode(this.secure)) * 31) + Boolean.hashCode(this.httpOnly)) * 31) + Boolean.hashCode(this.persistent)) * 31) + Boolean.hashCode(this.hostOnly);
    }

    public String toString() {
        return toString$okhttp(false);
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "name", imports = {}))
    /* renamed from: -deprecated_name  reason: not valid java name */
    public final String m1833deprecated_name() {
        return this.name;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "value", imports = {}))
    /* renamed from: -deprecated_value  reason: not valid java name */
    public final String m1837deprecated_value() {
        return this.value;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "persistent", imports = {}))
    /* renamed from: -deprecated_persistent  reason: not valid java name */
    public final boolean m1835deprecated_persistent() {
        return this.persistent;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "expiresAt", imports = {}))
    /* renamed from: -deprecated_expiresAt  reason: not valid java name */
    public final long m1830deprecated_expiresAt() {
        return this.expiresAt;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "hostOnly", imports = {}))
    /* renamed from: -deprecated_hostOnly  reason: not valid java name */
    public final boolean m1831deprecated_hostOnly() {
        return this.hostOnly;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "domain", imports = {}))
    /* renamed from: -deprecated_domain  reason: not valid java name */
    public final String m1829deprecated_domain() {
        return this.domain;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "path", imports = {}))
    /* renamed from: -deprecated_path  reason: not valid java name */
    public final String m1834deprecated_path() {
        return this.path;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "httpOnly", imports = {}))
    /* renamed from: -deprecated_httpOnly  reason: not valid java name */
    public final boolean m1832deprecated_httpOnly() {
        return this.httpOnly;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "secure", imports = {}))
    /* renamed from: -deprecated_secure  reason: not valid java name */
    public final boolean m1836deprecated_secure() {
        return this.secure;
    }

    public final String toString$okhttp(boolean forObsoleteRfc2965) {
        StringBuilder $this$toString_u24lambda_u2d0 = new StringBuilder();
        $this$toString_u24lambda_u2d0.append(name());
        $this$toString_u24lambda_u2d0.append('=');
        $this$toString_u24lambda_u2d0.append(value());
        if (persistent()) {
            if (expiresAt() == Long.MIN_VALUE) {
                $this$toString_u24lambda_u2d0.append("; max-age=0");
            } else {
                $this$toString_u24lambda_u2d0.append("; expires=").append(DatesKt.toHttpDateString(new Date(expiresAt())));
            }
        }
        if (!hostOnly()) {
            $this$toString_u24lambda_u2d0.append("; domain=");
            if (forObsoleteRfc2965) {
                $this$toString_u24lambda_u2d0.append(".");
            }
            $this$toString_u24lambda_u2d0.append(domain());
        }
        $this$toString_u24lambda_u2d0.append("; path=").append(path());
        if (secure()) {
            $this$toString_u24lambda_u2d0.append("; secure");
        }
        if (httpOnly()) {
            $this$toString_u24lambda_u2d0.append("; httponly");
        }
        String sb = $this$toString_u24lambda_u2d0.toString();
        Intrinsics.checkNotNullExpressionValue(sb, "toString()");
        return sb;
    }

    @Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0006\u0010\u000f\u001a\u00020\u0010J\u000e\u0010\u0003\u001a\u00020\u00002\u0006\u0010\u0003\u001a\u00020\u0004J\u0018\u0010\u0003\u001a\u00020\u00002\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\bH\u0002J\u000e\u0010\u0005\u001a\u00020\u00002\u0006\u0010\u0005\u001a\u00020\u0006J\u000e\u0010\u0011\u001a\u00020\u00002\u0006\u0010\u0003\u001a\u00020\u0004J\u0006\u0010\t\u001a\u00020\u0000J\u000e\u0010\n\u001a\u00020\u00002\u0006\u0010\n\u001a\u00020\u0004J\u000e\u0010\u000b\u001a\u00020\u00002\u0006\u0010\u000b\u001a\u00020\u0004J\u0006\u0010\r\u001a\u00020\u0000J\u000e\u0010\u000e\u001a\u00020\u00002\u0006\u0010\u000e\u001a\u00020\u0004R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u0004X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\bX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\bX\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u0004X\u000e¢\u0006\u0002\n\u0000¨\u0006\u0012"}, d2 = {"Lokhttp3/Cookie$Builder;", "", "()V", "domain", "", "expiresAt", "", "hostOnly", "", "httpOnly", "name", "path", "persistent", "secure", "value", "build", "Lokhttp3/Cookie;", "hostOnlyDomain", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: Cookie.kt */
    public static final class Builder {
        private String domain;
        private long expiresAt = DatesKt.MAX_DATE;
        private boolean hostOnly;
        private boolean httpOnly;
        private String name;
        private String path = "/";
        private boolean persistent;
        private boolean secure;
        private String value;

        public final Builder name(String name2) {
            Intrinsics.checkNotNullParameter(name2, "name");
            Builder $this$name_u24lambda_u2d1 = this;
            if (Intrinsics.areEqual((Object) StringsKt.trim((CharSequence) name2).toString(), (Object) name2)) {
                $this$name_u24lambda_u2d1.name = name2;
                return this;
            }
            throw new IllegalArgumentException("name is not trimmed".toString());
        }

        public final Builder value(String value2) {
            Intrinsics.checkNotNullParameter(value2, "value");
            Builder $this$value_u24lambda_u2d3 = this;
            if (Intrinsics.areEqual((Object) StringsKt.trim((CharSequence) value2).toString(), (Object) value2)) {
                $this$value_u24lambda_u2d3.value = value2;
                return this;
            }
            throw new IllegalArgumentException("value is not trimmed".toString());
        }

        public final Builder expiresAt(long expiresAt2) {
            Builder $this$expiresAt_u24lambda_u2d4 = this;
            long expiresAt3 = expiresAt2;
            if (expiresAt3 <= 0) {
                expiresAt3 = Long.MIN_VALUE;
            }
            if (expiresAt3 > DatesKt.MAX_DATE) {
                expiresAt3 = DatesKt.MAX_DATE;
            }
            $this$expiresAt_u24lambda_u2d4.expiresAt = expiresAt3;
            $this$expiresAt_u24lambda_u2d4.persistent = true;
            return this;
        }

        public final Builder domain(String domain2) {
            Intrinsics.checkNotNullParameter(domain2, "domain");
            return domain(domain2, false);
        }

        public final Builder hostOnlyDomain(String domain2) {
            Intrinsics.checkNotNullParameter(domain2, "domain");
            return domain(domain2, true);
        }

        private final Builder domain(String domain2, boolean hostOnly2) {
            Builder $this$domain_u24lambda_u2d5 = this;
            String canonicalDomain = HostnamesKt.toCanonicalHost(domain2);
            if (canonicalDomain != null) {
                $this$domain_u24lambda_u2d5.domain = canonicalDomain;
                $this$domain_u24lambda_u2d5.hostOnly = hostOnly2;
                return this;
            }
            throw new IllegalArgumentException(Intrinsics.stringPlus("unexpected domain: ", domain2));
        }

        public final Builder path(String path2) {
            Intrinsics.checkNotNullParameter(path2, "path");
            Builder $this$path_u24lambda_u2d7 = this;
            if (StringsKt.startsWith$default(path2, "/", false, 2, (Object) null)) {
                $this$path_u24lambda_u2d7.path = path2;
                return this;
            }
            throw new IllegalArgumentException("path must start with '/'".toString());
        }

        public final Builder secure() {
            this.secure = true;
            return this;
        }

        public final Builder httpOnly() {
            this.httpOnly = true;
            return this;
        }

        public final Cookie build() {
            String str = this.name;
            if (str != null) {
                String str2 = this.value;
                if (str2 != null) {
                    long j = this.expiresAt;
                    String str3 = this.domain;
                    if (str3 != null) {
                        return new Cookie(str, str2, j, str3, this.path, this.secure, this.httpOnly, this.persistent, this.hostOnly, (DefaultConstructorMarker) null);
                    }
                    throw new NullPointerException("builder.domain == null");
                }
                throw new NullPointerException("builder.value == null");
            }
            throw new NullPointerException("builder.name == null");
        }
    }

    @Metadata(d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J(\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u0010H\u0002J\u0018\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0012\u001a\u00020\f2\u0006\u0010\u0013\u001a\u00020\fH\u0002J'\u0010\u0014\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\fH\u0000¢\u0006\u0002\b\u001bJ\u001a\u0010\u0014\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\fH\u0007J\u001e\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00150\u001d2\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001e\u001a\u00020\u001fH\u0007J\u0010\u0010 \u001a\u00020\f2\u0006\u0010!\u001a\u00020\fH\u0002J \u0010\"\u001a\u00020\u00172\u0006\u0010!\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\nH\u0002J\u0010\u0010#\u001a\u00020\u00172\u0006\u0010!\u001a\u00020\fH\u0002J\u0018\u0010$\u001a\u00020\u00102\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010%\u001a\u00020\fH\u0002R\u0016\u0010\u0003\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0006\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0007\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0004¢\u0006\u0002\n\u0000¨\u0006&"}, d2 = {"Lokhttp3/Cookie$Companion;", "", "()V", "DAY_OF_MONTH_PATTERN", "Ljava/util/regex/Pattern;", "kotlin.jvm.PlatformType", "MONTH_PATTERN", "TIME_PATTERN", "YEAR_PATTERN", "dateCharacterOffset", "", "input", "", "pos", "limit", "invert", "", "domainMatch", "urlHost", "domain", "parse", "Lokhttp3/Cookie;", "currentTimeMillis", "", "url", "Lokhttp3/HttpUrl;", "setCookie", "parse$okhttp", "parseAll", "", "headers", "Lokhttp3/Headers;", "parseDomain", "s", "parseExpires", "parseMaxAge", "pathMatch", "path", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: Cookie.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        /* access modifiers changed from: private */
        public final boolean domainMatch(String urlHost, String domain) {
            if (Intrinsics.areEqual((Object) urlHost, (Object) domain)) {
                return true;
            }
            if (!StringsKt.endsWith$default(urlHost, domain, false, 2, (Object) null) || urlHost.charAt((urlHost.length() - domain.length()) - 1) != '.' || Util.canParseAsIpAddress(urlHost)) {
                return false;
            }
            return true;
        }

        /* access modifiers changed from: private */
        public final boolean pathMatch(HttpUrl url, String path) {
            String urlPath = url.encodedPath();
            if (Intrinsics.areEqual((Object) urlPath, (Object) path)) {
                return true;
            }
            if (!StringsKt.startsWith$default(urlPath, path, false, 2, (Object) null) || (!StringsKt.endsWith$default(path, "/", false, 2, (Object) null) && urlPath.charAt(path.length()) != '/')) {
                return false;
            }
            return true;
        }

        @JvmStatic
        public final Cookie parse(HttpUrl url, String setCookie) {
            Intrinsics.checkNotNullParameter(url, "url");
            Intrinsics.checkNotNullParameter(setCookie, "setCookie");
            return parse$okhttp(System.currentTimeMillis(), url, setCookie);
        }

        public final Cookie parse$okhttp(long currentTimeMillis, HttpUrl url, String setCookie) {
            long expiresAt;
            Object obj;
            String encodedPath;
            long deltaMilliseconds;
            String str;
            int limit;
            boolean z;
            String str2 = setCookie;
            Intrinsics.checkNotNullParameter(url, "url");
            Intrinsics.checkNotNullParameter(str2, "setCookie");
            String str3 = setCookie;
            int cookiePairEnd = Util.delimiterOffset$default(str3, ';', 0, 0, 6, (Object) null);
            int pairEqualsSign = Util.delimiterOffset$default(str3, '=', 0, cookiePairEnd, 2, (Object) null);
            if (pairEqualsSign == cookiePairEnd) {
                return null;
            }
            String cookieName = Util.trimSubstring$default(str2, 0, pairEqualsSign, 1, (Object) null);
            if (cookieName.length() == 0) {
                return null;
            } else if (Util.indexOfControlOrNonAscii(cookieName) != -1) {
                int i = pairEqualsSign;
                return null;
            } else {
                String cookieValue = Util.trimSubstring(str2, pairEqualsSign + 1, cookiePairEnd);
                if (Util.indexOfControlOrNonAscii(cookieValue) != -1) {
                    return null;
                }
                int limit2 = setCookie.length();
                long deltaSeconds = -1;
                boolean secureOnly = false;
                boolean httpOnly = false;
                boolean hostOnly = true;
                boolean persistent = false;
                int pos = cookiePairEnd + 1;
                long expiresAt2 = 253402300799999L;
                String path = null;
                String domain = null;
                while (pos < limit2) {
                    int attributePairEnd = Util.delimiterOffset(str2, ';', pos, limit2);
                    int attributeEqualsSign = Util.delimiterOffset(str2, '=', pos, attributePairEnd);
                    String attributeName = Util.trimSubstring(str2, pos, attributeEqualsSign);
                    if (attributeEqualsSign < attributePairEnd) {
                        str = Util.trimSubstring(str2, attributeEqualsSign + 1, attributePairEnd);
                    } else {
                        str = HttpUrl.FRAGMENT_ENCODE_SET;
                    }
                    String attributeValue = str;
                    int pairEqualsSign2 = pairEqualsSign;
                    if (StringsKt.equals(attributeName, "expires", true)) {
                        try {
                            limit = limit2;
                            try {
                                expiresAt2 = parseExpires(attributeValue, 0, attributeValue.length());
                                persistent = true;
                                z = true;
                            } catch (IllegalArgumentException e) {
                                z = true;
                                pos = attributePairEnd + 1;
                                boolean z2 = z;
                                pairEqualsSign = pairEqualsSign2;
                                limit2 = limit;
                            }
                        } catch (IllegalArgumentException e2) {
                            limit = limit2;
                            String str4 = attributeValue;
                            z = true;
                            pos = attributePairEnd + 1;
                            boolean z22 = z;
                            pairEqualsSign = pairEqualsSign2;
                            limit2 = limit;
                        }
                    } else {
                        limit = limit2;
                        String attributeValue2 = attributeValue;
                        if (StringsKt.equals(attributeName, "max-age", true)) {
                            try {
                                persistent = true;
                                deltaSeconds = parseMaxAge(attributeValue2);
                                z = true;
                            } catch (NumberFormatException e3) {
                                z = true;
                            }
                        } else if (StringsKt.equals(attributeName, "domain", true)) {
                            try {
                                domain = parseDomain(attributeValue2);
                                hostOnly = false;
                                z = true;
                            } catch (IllegalArgumentException e4) {
                                z = true;
                            }
                        } else {
                            z = true;
                            if (StringsKt.equals(attributeName, "path", true)) {
                                path = attributeValue2;
                            } else if (StringsKt.equals(attributeName, "secure", true)) {
                                secureOnly = true;
                            } else if (StringsKt.equals(attributeName, "httponly", true)) {
                                httpOnly = true;
                            }
                        }
                    }
                    pos = attributePairEnd + 1;
                    boolean z222 = z;
                    pairEqualsSign = pairEqualsSign2;
                    limit2 = limit;
                }
                int i2 = limit2;
                if (deltaSeconds == Long.MIN_VALUE) {
                    expiresAt = Long.MIN_VALUE;
                } else if (deltaSeconds != -1) {
                    if (deltaSeconds <= 9223372036854775L) {
                        deltaMilliseconds = ((long) 1000) * deltaSeconds;
                    } else {
                        deltaMilliseconds = Long.MAX_VALUE;
                    }
                    long expiresAt3 = currentTimeMillis + deltaMilliseconds;
                    expiresAt = (expiresAt3 < currentTimeMillis || expiresAt3 > DatesKt.MAX_DATE) ? 253402300799999L : expiresAt3;
                } else {
                    expiresAt = expiresAt2;
                }
                String urlHost = url.host();
                if (domain == null) {
                    domain = urlHost;
                } else if (!domainMatch(urlHost, domain)) {
                    return null;
                }
                if (urlHost.length() == domain.length()) {
                    obj = null;
                } else if (PublicSuffixDatabase.Companion.get().getEffectiveTldPlusOne(domain) == null) {
                    return null;
                } else {
                    obj = null;
                }
                String path2 = "/";
                if (path == null || !StringsKt.startsWith$default(path, path2, false, 2, obj)) {
                    String encodedPath2 = url.encodedPath();
                    int lastSlash = StringsKt.lastIndexOf$default((CharSequence) encodedPath2, '/', 0, false, 6, (Object) null);
                    if (lastSlash != 0) {
                        path2 = encodedPath2.substring(0, lastSlash);
                        Intrinsics.checkNotNullExpressionValue(path2, "this as java.lang.String…ing(startIndex, endIndex)");
                    }
                    encodedPath = path2;
                } else {
                    encodedPath = path;
                }
                int i3 = pos;
                return new Cookie(cookieName, cookieValue, expiresAt, domain, encodedPath, secureOnly, httpOnly, persistent, hostOnly, (DefaultConstructorMarker) null);
            }
        }

        private final long parseExpires(String s, int pos, int limit) {
            String str = s;
            int i = limit;
            int pos2 = dateCharacterOffset(str, pos, i, false);
            int minute = -1;
            int minute2 = -1;
            int second = -1;
            int dayOfMonth = -1;
            int month = -1;
            int year = -1;
            Matcher matcher = Cookie.TIME_PATTERN.matcher(str);
            while (pos2 < i) {
                int end = dateCharacterOffset(str, pos2 + 1, i, true);
                matcher.region(pos2, end);
                if (minute == -1 && matcher.usePattern(Cookie.TIME_PATTERN).matches()) {
                    String group = matcher.group(1);
                    Intrinsics.checkNotNullExpressionValue(group, "matcher.group(1)");
                    int hour = Integer.parseInt(group);
                    String group2 = matcher.group(2);
                    Intrinsics.checkNotNullExpressionValue(group2, "matcher.group(2)");
                    int minute3 = Integer.parseInt(group2);
                    String group3 = matcher.group(3);
                    Intrinsics.checkNotNullExpressionValue(group3, "matcher.group(3)");
                    second = Integer.parseInt(group3);
                    minute2 = minute3;
                    minute = hour;
                } else if (dayOfMonth == -1 && matcher.usePattern(Cookie.DAY_OF_MONTH_PATTERN).matches()) {
                    String group4 = matcher.group(1);
                    Intrinsics.checkNotNullExpressionValue(group4, "matcher.group(1)");
                    dayOfMonth = Integer.parseInt(group4);
                } else if (month == -1 && matcher.usePattern(Cookie.MONTH_PATTERN).matches()) {
                    String group5 = matcher.group(1);
                    Intrinsics.checkNotNullExpressionValue(group5, "matcher.group(1)");
                    Locale locale = Locale.US;
                    Intrinsics.checkNotNullExpressionValue(locale, "US");
                    String lowerCase = group5.toLowerCase(locale);
                    Intrinsics.checkNotNullExpressionValue(lowerCase, "this as java.lang.String).toLowerCase(locale)");
                    String monthString = lowerCase;
                    String pattern = Cookie.MONTH_PATTERN.pattern();
                    Intrinsics.checkNotNullExpressionValue(pattern, "MONTH_PATTERN.pattern()");
                    month = StringsKt.indexOf$default((CharSequence) pattern, monthString, 0, false, 6, (Object) null) / 4;
                } else if (year == -1 && matcher.usePattern(Cookie.YEAR_PATTERN).matches()) {
                    String group6 = matcher.group(1);
                    Intrinsics.checkNotNullExpressionValue(group6, "matcher.group(1)");
                    year = Integer.parseInt(group6);
                }
                pos2 = dateCharacterOffset(str, end + 1, i, false);
            }
            if (70 <= year && year < 100) {
                year += 1900;
            }
            if (year >= 0 && year < 70) {
                year += 2000;
            }
            if (year >= 1601) {
                if (month != -1) {
                    if (1 <= dayOfMonth && dayOfMonth < 32) {
                        if (minute >= 0 && minute < 24) {
                            if (minute2 >= 0 && minute2 < 60) {
                                if (second >= 0 && second < 60) {
                                    GregorianCalendar $this$parseExpires_u24lambda_u2d0 = new GregorianCalendar(Util.UTC);
                                    $this$parseExpires_u24lambda_u2d0.setLenient(false);
                                    $this$parseExpires_u24lambda_u2d0.set(1, year);
                                    $this$parseExpires_u24lambda_u2d0.set(2, month - 1);
                                    $this$parseExpires_u24lambda_u2d0.set(5, dayOfMonth);
                                    $this$parseExpires_u24lambda_u2d0.set(11, minute);
                                    $this$parseExpires_u24lambda_u2d0.set(12, minute2);
                                    $this$parseExpires_u24lambda_u2d0.set(13, second);
                                    $this$parseExpires_u24lambda_u2d0.set(14, 0);
                                    return $this$parseExpires_u24lambda_u2d0.getTimeInMillis();
                                }
                                throw new IllegalArgumentException("Failed requirement.".toString());
                            }
                            throw new IllegalArgumentException("Failed requirement.".toString());
                        }
                        throw new IllegalArgumentException("Failed requirement.".toString());
                    }
                    throw new IllegalArgumentException("Failed requirement.".toString());
                }
                throw new IllegalArgumentException("Failed requirement.".toString());
            }
            throw new IllegalArgumentException("Failed requirement.".toString());
        }

        private final int dateCharacterOffset(String input, int pos, int limit, boolean invert) {
            int i = pos;
            while (i < limit) {
                int i2 = i;
                i++;
                int c = input.charAt(i2);
                boolean dateCharacter = true;
                if ((c >= 32 || c == 9) && c < 127) {
                    if (!(c <= 57 && 48 <= c)) {
                        if (!(c <= 122 && 97 <= c)) {
                            if (!(c <= 90 && 65 <= c) && c != 58) {
                                dateCharacter = false;
                            }
                        }
                    }
                }
                if (dateCharacter == (!invert)) {
                    return i2;
                }
            }
            return limit;
        }

        private final long parseMaxAge(String s) {
            try {
                long parsed = Long.parseLong(s);
                if (parsed <= 0) {
                    return Long.MIN_VALUE;
                }
                return parsed;
            } catch (NumberFormatException e) {
                if (!new Regex("-?\\d+").matches(s)) {
                    throw e;
                } else if (StringsKt.startsWith$default(s, "-", false, 2, (Object) null)) {
                    return Long.MIN_VALUE;
                } else {
                    return Long.MAX_VALUE;
                }
            }
        }

        private final String parseDomain(String s) {
            if (!StringsKt.endsWith$default(s, ".", false, 2, (Object) null)) {
                String canonicalHost = HostnamesKt.toCanonicalHost(StringsKt.removePrefix(s, (CharSequence) "."));
                if (canonicalHost != null) {
                    return canonicalHost;
                }
                throw new IllegalArgumentException();
            }
            throw new IllegalArgumentException("Failed requirement.".toString());
        }

        @JvmStatic
        public final List<Cookie> parseAll(HttpUrl url, Headers headers) {
            Intrinsics.checkNotNullParameter(url, "url");
            Intrinsics.checkNotNullParameter(headers, "headers");
            List cookieStrings = headers.values("Set-Cookie");
            List cookies = null;
            int size = cookieStrings.size();
            int i = 0;
            while (i < size) {
                int i2 = i;
                i++;
                Cookie cookie = parse(url, cookieStrings.get(i2));
                if (cookie != null) {
                    if (cookies == null) {
                        cookies = new ArrayList();
                    }
                    cookies.add(cookie);
                }
            }
            if (cookies == null) {
                return CollectionsKt.emptyList();
            }
            List<Cookie> unmodifiableList = Collections.unmodifiableList(cookies);
            Intrinsics.checkNotNullExpressionValue(unmodifiableList, "{\n        Collections.un…ableList(cookies)\n      }");
            return unmodifiableList;
        }
    }
}

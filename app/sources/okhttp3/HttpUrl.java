package okhttp3;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.internal.ProgressionUtilKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntProgression;
import kotlin.ranges.RangesKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import kotlin.text.Typography;
import okhttp3.internal.HostnamesKt;
import okhttp3.internal.Util;
import okhttp3.internal.publicsuffix.PublicSuffixDatabase;
import okio.Buffer;

@Metadata(d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\"\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u0000 J2\u00020\u0001:\u0002IJBa\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00030\n\u0012\u0010\u0010\u000b\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\u0003\u0018\u00010\n\u0012\b\u0010\f\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\r\u001a\u00020\u0003¢\u0006\u0002\u0010\u000eJ\u000f\u0010\u000f\u001a\u0004\u0018\u00010\u0003H\u0007¢\u0006\u0002\b!J\r\u0010\u0011\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\"J\r\u0010\u0012\u001a\u00020\u0003H\u0007¢\u0006\u0002\b#J\u0013\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00030\nH\u0007¢\u0006\u0002\b$J\u000f\u0010\u0015\u001a\u0004\u0018\u00010\u0003H\u0007¢\u0006\u0002\b%J\r\u0010\u0016\u001a\u00020\u0003H\u0007¢\u0006\u0002\b&J\u0013\u0010'\u001a\u00020\u00182\b\u0010(\u001a\u0004\u0018\u00010\u0001H\u0002J\u000f\u0010\f\u001a\u0004\u0018\u00010\u0003H\u0007¢\u0006\u0002\b)J\b\u0010*\u001a\u00020\bH\u0016J\r\u0010\u0006\u001a\u00020\u0003H\u0007¢\u0006\u0002\b+J\u0006\u0010,\u001a\u00020-J\u0010\u0010,\u001a\u0004\u0018\u00010-2\u0006\u0010.\u001a\u00020\u0003J\r\u0010\u0005\u001a\u00020\u0003H\u0007¢\u0006\u0002\b/J\u0013\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00030\nH\u0007¢\u0006\u0002\b0J\r\u0010\u001a\u001a\u00020\bH\u0007¢\u0006\u0002\b1J\r\u0010\u0007\u001a\u00020\bH\u0007¢\u0006\u0002\b2J\u000f\u0010\u001c\u001a\u0004\u0018\u00010\u0003H\u0007¢\u0006\u0002\b3J\u0010\u00104\u001a\u0004\u0018\u00010\u00032\u0006\u00105\u001a\u00020\u0003J\u000e\u00106\u001a\u00020\u00032\u0006\u00107\u001a\u00020\bJ\u0013\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00030\u001eH\u0007¢\u0006\u0002\b8J\u0010\u00109\u001a\u0004\u0018\u00010\u00032\u0006\u00107\u001a\u00020\bJ\u0016\u0010:\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00030\n2\u0006\u00105\u001a\u00020\u0003J\r\u0010 \u001a\u00020\bH\u0007¢\u0006\u0002\b;J\u0006\u0010<\u001a\u00020\u0003J\u0010\u0010=\u001a\u0004\u0018\u00010\u00002\u0006\u0010.\u001a\u00020\u0003J\r\u0010\u0002\u001a\u00020\u0003H\u0007¢\u0006\u0002\b>J\b\u0010?\u001a\u00020\u0003H\u0016J\r\u0010@\u001a\u00020AH\u0007¢\u0006\u0002\bBJ\r\u0010C\u001a\u00020DH\u0007¢\u0006\u0002\b\rJ\b\u0010E\u001a\u0004\u0018\u00010\u0003J\r\u0010B\u001a\u00020AH\u0007¢\u0006\u0002\bFJ\r\u0010\r\u001a\u00020DH\u0007¢\u0006\u0002\bGJ\r\u0010\u0004\u001a\u00020\u0003H\u0007¢\u0006\u0002\bHR\u0013\u0010\u000f\u001a\u0004\u0018\u00010\u00038G¢\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0011\u001a\u00020\u00038G¢\u0006\u0006\u001a\u0004\b\u0011\u0010\u0010R\u0011\u0010\u0012\u001a\u00020\u00038G¢\u0006\u0006\u001a\u0004\b\u0012\u0010\u0010R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00030\n8G¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0014R\u0013\u0010\u0015\u001a\u0004\u0018\u00010\u00038G¢\u0006\u0006\u001a\u0004\b\u0015\u0010\u0010R\u0011\u0010\u0016\u001a\u00020\u00038G¢\u0006\u0006\u001a\u0004\b\u0016\u0010\u0010R\u0015\u0010\f\u001a\u0004\u0018\u00010\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u0010R\u0013\u0010\u0006\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0010R\u0011\u0010\u0017\u001a\u00020\u0018¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0019R\u0013\u0010\u0005\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0010R\u0019\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00030\n8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0014R\u0011\u0010\u001a\u001a\u00020\b8G¢\u0006\u0006\u001a\u0004\b\u001a\u0010\u001bR\u0013\u0010\u0007\u001a\u00020\b8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\u001bR\u0013\u0010\u001c\u001a\u0004\u0018\u00010\u00038G¢\u0006\u0006\u001a\u0004\b\u001c\u0010\u0010R\u0018\u0010\u000b\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\u0003\u0018\u00010\nX\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00030\u001e8G¢\u0006\u0006\u001a\u0004\b\u001d\u0010\u001fR\u0011\u0010 \u001a\u00020\b8G¢\u0006\u0006\u001a\u0004\b \u0010\u001bR\u0013\u0010\u0002\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0010R\u000e\u0010\r\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u0013\u0010\u0004\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0010¨\u0006K"}, d2 = {"Lokhttp3/HttpUrl;", "", "scheme", "", "username", "password", "host", "port", "", "pathSegments", "", "queryNamesAndValues", "fragment", "url", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/util/List;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;)V", "encodedFragment", "()Ljava/lang/String;", "encodedPassword", "encodedPath", "encodedPathSegments", "()Ljava/util/List;", "encodedQuery", "encodedUsername", "isHttps", "", "()Z", "pathSize", "()I", "query", "queryParameterNames", "", "()Ljava/util/Set;", "querySize", "-deprecated_encodedFragment", "-deprecated_encodedPassword", "-deprecated_encodedPath", "-deprecated_encodedPathSegments", "-deprecated_encodedQuery", "-deprecated_encodedUsername", "equals", "other", "-deprecated_fragment", "hashCode", "-deprecated_host", "newBuilder", "Lokhttp3/HttpUrl$Builder;", "link", "-deprecated_password", "-deprecated_pathSegments", "-deprecated_pathSize", "-deprecated_port", "-deprecated_query", "queryParameter", "name", "queryParameterName", "index", "-deprecated_queryParameterNames", "queryParameterValue", "queryParameterValues", "-deprecated_querySize", "redact", "resolve", "-deprecated_scheme", "toString", "toUri", "Ljava/net/URI;", "uri", "toUrl", "Ljava/net/URL;", "topPrivateDomain", "-deprecated_uri", "-deprecated_url", "-deprecated_username", "Builder", "Companion", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: HttpUrl.kt */
public final class HttpUrl {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    public static final String FORM_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#&!$(),~";
    public static final String FRAGMENT_ENCODE_SET = "";
    public static final String FRAGMENT_ENCODE_SET_URI = " \"#<>\\^`{|}";
    /* access modifiers changed from: private */
    public static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    public static final String PASSWORD_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
    public static final String PATH_SEGMENT_ENCODE_SET = " \"<>^`{}|/\\?#";
    public static final String PATH_SEGMENT_ENCODE_SET_URI = "[]";
    public static final String QUERY_COMPONENT_ENCODE_SET = " !\"#$&'(),/:;<=>?@[]\\^`{|}~";
    public static final String QUERY_COMPONENT_ENCODE_SET_URI = "\\^`{|}";
    public static final String QUERY_COMPONENT_REENCODE_SET = " \"'<>#&=";
    public static final String QUERY_ENCODE_SET = " \"'<>#";
    public static final String USERNAME_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
    private final String fragment;
    private final String host;
    private final boolean isHttps;
    private final String password;
    private final List<String> pathSegments;
    private final int port;
    private final List<String> queryNamesAndValues;
    private final String scheme;
    private final String url;
    private final String username;

    @JvmStatic
    public static final int defaultPort(String str) {
        return Companion.defaultPort(str);
    }

    @JvmStatic
    public static final HttpUrl get(String str) {
        return Companion.get(str);
    }

    @JvmStatic
    public static final HttpUrl get(URI uri) {
        return Companion.get(uri);
    }

    @JvmStatic
    public static final HttpUrl get(URL url2) {
        return Companion.get(url2);
    }

    @JvmStatic
    public static final HttpUrl parse(String str) {
        return Companion.parse(str);
    }

    public HttpUrl(String scheme2, String username2, String password2, String host2, int port2, List<String> pathSegments2, List<String> queryNamesAndValues2, String fragment2, String url2) {
        Intrinsics.checkNotNullParameter(scheme2, "scheme");
        Intrinsics.checkNotNullParameter(username2, "username");
        Intrinsics.checkNotNullParameter(password2, "password");
        Intrinsics.checkNotNullParameter(host2, "host");
        Intrinsics.checkNotNullParameter(pathSegments2, "pathSegments");
        Intrinsics.checkNotNullParameter(url2, "url");
        this.scheme = scheme2;
        this.username = username2;
        this.password = password2;
        this.host = host2;
        this.port = port2;
        this.pathSegments = pathSegments2;
        this.queryNamesAndValues = queryNamesAndValues2;
        this.fragment = fragment2;
        this.url = url2;
        this.isHttps = Intrinsics.areEqual((Object) scheme2, (Object) "https");
    }

    public final String scheme() {
        return this.scheme;
    }

    public final String username() {
        return this.username;
    }

    public final String password() {
        return this.password;
    }

    public final String host() {
        return this.host;
    }

    public final int port() {
        return this.port;
    }

    public final List<String> pathSegments() {
        return this.pathSegments;
    }

    public final String fragment() {
        return this.fragment;
    }

    public final boolean isHttps() {
        return this.isHttps;
    }

    public final URL url() {
        try {
            return new URL(this.url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public final URI uri() {
        String uri = newBuilder().reencodeForUri$okhttp().toString();
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            try {
                URI create = URI.create(new Regex("[\\u0000-\\u001F\\u007F-\\u009F\\p{javaWhitespace}]").replace((CharSequence) uri, FRAGMENT_ENCODE_SET));
                Intrinsics.checkNotNullExpressionValue(create, "{\n      // Unlikely edge…Unexpected!\n      }\n    }");
                return create;
            } catch (Exception e2) {
                throw new RuntimeException(e);
            }
        }
    }

    public final String encodedUsername() {
        if (this.username.length() == 0) {
            return FRAGMENT_ENCODE_SET;
        }
        int usernameStart = this.scheme.length() + 3;
        String str = this.url;
        String substring = this.url.substring(usernameStart, Util.delimiterOffset(str, ":@", usernameStart, str.length()));
        Intrinsics.checkNotNullExpressionValue(substring, "this as java.lang.String…ing(startIndex, endIndex)");
        return substring;
    }

    public final String encodedPassword() {
        if (this.password.length() == 0) {
            return FRAGMENT_ENCODE_SET;
        }
        String substring = this.url.substring(StringsKt.indexOf$default((CharSequence) this.url, ':', this.scheme.length() + 3, false, 4, (Object) null) + 1, StringsKt.indexOf$default((CharSequence) this.url, '@', 0, false, 6, (Object) null));
        Intrinsics.checkNotNullExpressionValue(substring, "this as java.lang.String…ing(startIndex, endIndex)");
        return substring;
    }

    public final int pathSize() {
        return this.pathSegments.size();
    }

    public final String encodedPath() {
        int pathStart = StringsKt.indexOf$default((CharSequence) this.url, '/', this.scheme.length() + 3, false, 4, (Object) null);
        String str = this.url;
        String substring = this.url.substring(pathStart, Util.delimiterOffset(str, "?#", pathStart, str.length()));
        Intrinsics.checkNotNullExpressionValue(substring, "this as java.lang.String…ing(startIndex, endIndex)");
        return substring;
    }

    public final List<String> encodedPathSegments() {
        int pathStart = StringsKt.indexOf$default((CharSequence) this.url, '/', this.scheme.length() + 3, false, 4, (Object) null);
        String str = this.url;
        int pathEnd = Util.delimiterOffset(str, "?#", pathStart, str.length());
        List result = new ArrayList();
        int i = pathStart;
        while (i < pathEnd) {
            int i2 = i + 1;
            int segmentEnd = Util.delimiterOffset(this.url, '/', i2, pathEnd);
            String substring = this.url.substring(i2, segmentEnd);
            Intrinsics.checkNotNullExpressionValue(substring, "this as java.lang.String…ing(startIndex, endIndex)");
            result.add(substring);
            i = segmentEnd;
        }
        return result;
    }

    public final String encodedQuery() {
        if (this.queryNamesAndValues == null) {
            return null;
        }
        int queryStart = StringsKt.indexOf$default((CharSequence) this.url, '?', 0, false, 6, (Object) null) + 1;
        String str = this.url;
        String substring = this.url.substring(queryStart, Util.delimiterOffset(str, '#', queryStart, str.length()));
        Intrinsics.checkNotNullExpressionValue(substring, "this as java.lang.String…ing(startIndex, endIndex)");
        return substring;
    }

    public final String query() {
        if (this.queryNamesAndValues == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        Companion.toQueryString$okhttp(this.queryNamesAndValues, result);
        return result.toString();
    }

    public final int querySize() {
        List<String> list = this.queryNamesAndValues;
        if (list != null) {
            return list.size() / 2;
        }
        return 0;
    }

    public final String queryParameter(String name) {
        int i;
        Intrinsics.checkNotNullParameter(name, "name");
        List<String> list = this.queryNamesAndValues;
        if (list == null) {
            return null;
        }
        IntProgression step = RangesKt.step((IntProgression) RangesKt.until(0, list.size()), 2);
        int first = step.getFirst();
        int last = step.getLast();
        int step2 = step.getStep();
        if ((step2 <= 0 || first > last) && (step2 >= 0 || last > first)) {
            return null;
        }
        do {
            i = first;
            first += step2;
            if (Intrinsics.areEqual((Object) name, (Object) this.queryNamesAndValues.get(i))) {
                return this.queryNamesAndValues.get(i + 1);
            }
        } while (i != last);
        return null;
    }

    public final Set<String> queryParameterNames() {
        int i;
        if (this.queryNamesAndValues == null) {
            return SetsKt.emptySet();
        }
        LinkedHashSet result = new LinkedHashSet();
        IntProgression step = RangesKt.step((IntProgression) RangesKt.until(0, this.queryNamesAndValues.size()), 2);
        int first = step.getFirst();
        int last = step.getLast();
        int step2 = step.getStep();
        if ((step2 <= 0 || first > last) && (step2 >= 0 || last > first)) {
            Set<String> unmodifiableSet = Collections.unmodifiableSet(result);
            Intrinsics.checkNotNullExpressionValue(unmodifiableSet, "unmodifiableSet(result)");
            return unmodifiableSet;
        }
        do {
            i = first;
            first += step2;
            String str = this.queryNamesAndValues.get(i);
            Intrinsics.checkNotNull(str);
            result.add(str);
        } while (i != last);
        Set<String> unmodifiableSet2 = Collections.unmodifiableSet(result);
        Intrinsics.checkNotNullExpressionValue(unmodifiableSet2, "unmodifiableSet(result)");
        return unmodifiableSet2;
    }

    public final List<String> queryParameterValues(String name) {
        int i;
        Intrinsics.checkNotNullParameter(name, "name");
        if (this.queryNamesAndValues == null) {
            return CollectionsKt.emptyList();
        }
        List result = new ArrayList();
        IntProgression step = RangesKt.step((IntProgression) RangesKt.until(0, this.queryNamesAndValues.size()), 2);
        int first = step.getFirst();
        int last = step.getLast();
        int step2 = step.getStep();
        if ((step2 <= 0 || first > last) && (step2 >= 0 || last > first)) {
            List<String> unmodifiableList = Collections.unmodifiableList(result);
            Intrinsics.checkNotNullExpressionValue(unmodifiableList, "unmodifiableList(result)");
            return unmodifiableList;
        }
        do {
            i = first;
            first += step2;
            if (Intrinsics.areEqual((Object) name, (Object) this.queryNamesAndValues.get(i))) {
                result.add(this.queryNamesAndValues.get(i + 1));
                continue;
            }
        } while (i != last);
        List<String> unmodifiableList2 = Collections.unmodifiableList(result);
        Intrinsics.checkNotNullExpressionValue(unmodifiableList2, "unmodifiableList(result)");
        return unmodifiableList2;
    }

    public final String queryParameterName(int index) {
        List<String> list = this.queryNamesAndValues;
        if (list != null) {
            String str = list.get(index * 2);
            Intrinsics.checkNotNull(str);
            return str;
        }
        throw new IndexOutOfBoundsException();
    }

    public final String queryParameterValue(int index) {
        List<String> list = this.queryNamesAndValues;
        if (list != null) {
            return list.get((index * 2) + 1);
        }
        throw new IndexOutOfBoundsException();
    }

    public final String encodedFragment() {
        if (this.fragment == null) {
            return null;
        }
        String substring = this.url.substring(StringsKt.indexOf$default((CharSequence) this.url, '#', 0, false, 6, (Object) null) + 1);
        Intrinsics.checkNotNullExpressionValue(substring, "this as java.lang.String).substring(startIndex)");
        return substring;
    }

    public final String redact() {
        Builder newBuilder = newBuilder("/...");
        Intrinsics.checkNotNull(newBuilder);
        return newBuilder.username(FRAGMENT_ENCODE_SET).password(FRAGMENT_ENCODE_SET).build().toString();
    }

    public final HttpUrl resolve(String link) {
        Intrinsics.checkNotNullParameter(link, "link");
        Builder newBuilder = newBuilder(link);
        if (newBuilder == null) {
            return null;
        }
        return newBuilder.build();
    }

    public final Builder newBuilder() {
        Builder result = new Builder();
        result.setScheme$okhttp(this.scheme);
        result.setEncodedUsername$okhttp(encodedUsername());
        result.setEncodedPassword$okhttp(encodedPassword());
        result.setHost$okhttp(this.host);
        result.setPort$okhttp(this.port != Companion.defaultPort(this.scheme) ? this.port : -1);
        result.getEncodedPathSegments$okhttp().clear();
        result.getEncodedPathSegments$okhttp().addAll(encodedPathSegments());
        result.encodedQuery(encodedQuery());
        result.setEncodedFragment$okhttp(encodedFragment());
        return result;
    }

    public final Builder newBuilder(String link) {
        Intrinsics.checkNotNullParameter(link, "link");
        try {
            return new Builder().parse$okhttp(this, link);
        } catch (IllegalArgumentException e) {
            Builder builder = null;
            return null;
        }
    }

    public boolean equals(Object other) {
        return (other instanceof HttpUrl) && Intrinsics.areEqual((Object) ((HttpUrl) other).url, (Object) this.url);
    }

    public int hashCode() {
        return this.url.hashCode();
    }

    public String toString() {
        return this.url;
    }

    public final String topPrivateDomain() {
        if (!Util.canParseAsIpAddress(this.host)) {
            return PublicSuffixDatabase.Companion.get().getEffectiveTldPlusOne(this.host);
        }
        String str = null;
        return null;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to toUrl()", replaceWith = @ReplaceWith(expression = "toUrl()", imports = {}))
    /* renamed from: -deprecated_url  reason: not valid java name */
    public final URL m1867deprecated_url() {
        return url();
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to toUri()", replaceWith = @ReplaceWith(expression = "toUri()", imports = {}))
    /* renamed from: -deprecated_uri  reason: not valid java name */
    public final URI m1866deprecated_uri() {
        return uri();
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "scheme", imports = {}))
    /* renamed from: -deprecated_scheme  reason: not valid java name */
    public final String m1865deprecated_scheme() {
        return this.scheme;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "encodedUsername", imports = {}))
    /* renamed from: -deprecated_encodedUsername  reason: not valid java name */
    public final String m1855deprecated_encodedUsername() {
        return encodedUsername();
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "username", imports = {}))
    /* renamed from: -deprecated_username  reason: not valid java name */
    public final String m1868deprecated_username() {
        return this.username;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "encodedPassword", imports = {}))
    /* renamed from: -deprecated_encodedPassword  reason: not valid java name */
    public final String m1851deprecated_encodedPassword() {
        return encodedPassword();
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "password", imports = {}))
    /* renamed from: -deprecated_password  reason: not valid java name */
    public final String m1858deprecated_password() {
        return this.password;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "host", imports = {}))
    /* renamed from: -deprecated_host  reason: not valid java name */
    public final String m1857deprecated_host() {
        return this.host;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "port", imports = {}))
    /* renamed from: -deprecated_port  reason: not valid java name */
    public final int m1861deprecated_port() {
        return this.port;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "pathSize", imports = {}))
    /* renamed from: -deprecated_pathSize  reason: not valid java name */
    public final int m1860deprecated_pathSize() {
        return pathSize();
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "encodedPath", imports = {}))
    /* renamed from: -deprecated_encodedPath  reason: not valid java name */
    public final String m1852deprecated_encodedPath() {
        return encodedPath();
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "encodedPathSegments", imports = {}))
    /* renamed from: -deprecated_encodedPathSegments  reason: not valid java name */
    public final List<String> m1853deprecated_encodedPathSegments() {
        return encodedPathSegments();
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "pathSegments", imports = {}))
    /* renamed from: -deprecated_pathSegments  reason: not valid java name */
    public final List<String> m1859deprecated_pathSegments() {
        return this.pathSegments;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "encodedQuery", imports = {}))
    /* renamed from: -deprecated_encodedQuery  reason: not valid java name */
    public final String m1854deprecated_encodedQuery() {
        return encodedQuery();
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "query", imports = {}))
    /* renamed from: -deprecated_query  reason: not valid java name */
    public final String m1862deprecated_query() {
        return query();
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "querySize", imports = {}))
    /* renamed from: -deprecated_querySize  reason: not valid java name */
    public final int m1864deprecated_querySize() {
        return querySize();
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "queryParameterNames", imports = {}))
    /* renamed from: -deprecated_queryParameterNames  reason: not valid java name */
    public final Set<String> m1863deprecated_queryParameterNames() {
        return queryParameterNames();
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "encodedFragment", imports = {}))
    /* renamed from: -deprecated_encodedFragment  reason: not valid java name */
    public final String m1850deprecated_encodedFragment() {
        return encodedFragment();
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "fragment", imports = {}))
    /* renamed from: -deprecated_fragment  reason: not valid java name */
    public final String m1856deprecated_fragment() {
        return this.fragment;
    }

    @Metadata(d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010!\n\u0002\b\r\n\u0002\u0010\b\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u0002\n\u0002\b\u0017\u0018\u0000 V2\u00020\u0001:\u0001VB\u0005¢\u0006\u0002\u0010\u0002J\u000e\u0010#\u001a\u00020\u00002\u0006\u0010$\u001a\u00020\u0004J\u000e\u0010%\u001a\u00020\u00002\u0006\u0010\f\u001a\u00020\u0004J\u0018\u0010&\u001a\u00020\u00002\u0006\u0010'\u001a\u00020\u00042\b\u0010(\u001a\u0004\u0018\u00010\u0004J\u000e\u0010)\u001a\u00020\u00002\u0006\u0010*\u001a\u00020\u0004J\u000e\u0010+\u001a\u00020\u00002\u0006\u0010,\u001a\u00020\u0004J\u0018\u0010+\u001a\u00020\u00002\u0006\u0010,\u001a\u00020\u00042\u0006\u0010-\u001a\u00020.H\u0002J\u0018\u0010/\u001a\u00020\u00002\u0006\u00100\u001a\u00020\u00042\b\u00101\u001a\u0004\u0018\u00010\u0004J\u0006\u00102\u001a\u000203J\b\u00104\u001a\u00020\u001bH\u0002J\u0010\u0010\u0003\u001a\u00020\u00002\b\u0010\u0003\u001a\u0004\u0018\u00010\u0004J\u000e\u0010\t\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0004J\u000e\u00105\u001a\u00020\u00002\u0006\u00105\u001a\u00020\u0004J\u0010\u00106\u001a\u00020\u00002\b\u00106\u001a\u0004\u0018\u00010\u0004J\u000e\u0010\u0014\u001a\u00020\u00002\u0006\u0010\u0014\u001a\u00020\u0004J\u0010\u00107\u001a\u00020\u00002\b\u00107\u001a\u0004\u0018\u00010\u0004J\u000e\u0010\u0017\u001a\u00020\u00002\u0006\u0010\u0017\u001a\u00020\u0004J\u0010\u00108\u001a\u00020.2\u0006\u00109\u001a\u00020\u0004H\u0002J\u0010\u0010:\u001a\u00020.2\u0006\u00109\u001a\u00020\u0004H\u0002J\u001f\u0010;\u001a\u00020\u00002\b\u0010<\u001a\u0004\u0018\u0001032\u0006\u00109\u001a\u00020\u0004H\u0000¢\u0006\u0002\b=J\u000e\u0010>\u001a\u00020\u00002\u0006\u0010>\u001a\u00020\u0004J\b\u0010?\u001a\u00020@H\u0002J\u000e\u0010\u001a\u001a\u00020\u00002\u0006\u0010\u001a\u001a\u00020\u001bJ0\u0010A\u001a\u00020@2\u0006\u00109\u001a\u00020\u00042\u0006\u0010B\u001a\u00020\u001b2\u0006\u0010C\u001a\u00020\u001b2\u0006\u0010D\u001a\u00020.2\u0006\u0010-\u001a\u00020.H\u0002J\u0010\u0010E\u001a\u00020\u00002\b\u0010E\u001a\u0004\u0018\u00010\u0004J\r\u0010F\u001a\u00020\u0000H\u0000¢\u0006\u0002\bGJ\u0010\u0010H\u001a\u00020@2\u0006\u0010I\u001a\u00020\u0004H\u0002J\u000e\u0010J\u001a\u00020\u00002\u0006\u0010'\u001a\u00020\u0004J\u000e\u0010K\u001a\u00020\u00002\u0006\u00100\u001a\u00020\u0004J\u000e\u0010L\u001a\u00020\u00002\u0006\u0010M\u001a\u00020\u001bJ \u0010N\u001a\u00020@2\u0006\u00109\u001a\u00020\u00042\u0006\u0010O\u001a\u00020\u001b2\u0006\u0010C\u001a\u00020\u001bH\u0002J\u000e\u0010 \u001a\u00020\u00002\u0006\u0010 \u001a\u00020\u0004J\u0016\u0010P\u001a\u00020\u00002\u0006\u0010M\u001a\u00020\u001b2\u0006\u0010$\u001a\u00020\u0004J\u0018\u0010Q\u001a\u00020\u00002\u0006\u0010'\u001a\u00020\u00042\b\u0010(\u001a\u0004\u0018\u00010\u0004J\u0016\u0010R\u001a\u00020\u00002\u0006\u0010M\u001a\u00020\u001b2\u0006\u0010*\u001a\u00020\u0004J\u0018\u0010S\u001a\u00020\u00002\u0006\u00100\u001a\u00020\u00042\b\u00101\u001a\u0004\u0018\u00010\u0004J\b\u0010T\u001a\u00020\u0004H\u0016J\u000e\u0010U\u001a\u00020\u00002\u0006\u0010U\u001a\u00020\u0004R\u001c\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u001a\u0010\t\u001a\u00020\u0004X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u0006\"\u0004\b\u000b\u0010\bR\u001a\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00040\rX\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR$\u0010\u0010\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\u0004\u0018\u00010\rX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u000f\"\u0004\b\u0012\u0010\u0013R\u001a\u0010\u0014\u001a\u00020\u0004X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0006\"\u0004\b\u0016\u0010\bR\u001c\u0010\u0017\u001a\u0004\u0018\u00010\u0004X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0006\"\u0004\b\u0019\u0010\bR\u001a\u0010\u001a\u001a\u00020\u001bX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001fR\u001c\u0010 \u001a\u0004\u0018\u00010\u0004X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b!\u0010\u0006\"\u0004\b\"\u0010\b¨\u0006W"}, d2 = {"Lokhttp3/HttpUrl$Builder;", "", "()V", "encodedFragment", "", "getEncodedFragment$okhttp", "()Ljava/lang/String;", "setEncodedFragment$okhttp", "(Ljava/lang/String;)V", "encodedPassword", "getEncodedPassword$okhttp", "setEncodedPassword$okhttp", "encodedPathSegments", "", "getEncodedPathSegments$okhttp", "()Ljava/util/List;", "encodedQueryNamesAndValues", "getEncodedQueryNamesAndValues$okhttp", "setEncodedQueryNamesAndValues$okhttp", "(Ljava/util/List;)V", "encodedUsername", "getEncodedUsername$okhttp", "setEncodedUsername$okhttp", "host", "getHost$okhttp", "setHost$okhttp", "port", "", "getPort$okhttp", "()I", "setPort$okhttp", "(I)V", "scheme", "getScheme$okhttp", "setScheme$okhttp", "addEncodedPathSegment", "encodedPathSegment", "addEncodedPathSegments", "addEncodedQueryParameter", "encodedName", "encodedValue", "addPathSegment", "pathSegment", "addPathSegments", "pathSegments", "alreadyEncoded", "", "addQueryParameter", "name", "value", "build", "Lokhttp3/HttpUrl;", "effectivePort", "encodedPath", "encodedQuery", "fragment", "isDot", "input", "isDotDot", "parse", "base", "parse$okhttp", "password", "pop", "", "push", "pos", "limit", "addTrailingSlash", "query", "reencodeForUri", "reencodeForUri$okhttp", "removeAllCanonicalQueryParameters", "canonicalName", "removeAllEncodedQueryParameters", "removeAllQueryParameters", "removePathSegment", "index", "resolvePath", "startPos", "setEncodedPathSegment", "setEncodedQueryParameter", "setPathSegment", "setQueryParameter", "toString", "username", "Companion", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: HttpUrl.kt */
    public static final class Builder {
        public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
        public static final String INVALID_HOST = "Invalid URL host";
        private String encodedFragment;
        private String encodedPassword = HttpUrl.FRAGMENT_ENCODE_SET;
        private final List<String> encodedPathSegments;
        private List<String> encodedQueryNamesAndValues;
        private String encodedUsername = HttpUrl.FRAGMENT_ENCODE_SET;
        private String host;
        private int port = -1;
        private String scheme;

        public Builder() {
            List<String> arrayList = new ArrayList<>();
            this.encodedPathSegments = arrayList;
            arrayList.add(HttpUrl.FRAGMENT_ENCODE_SET);
        }

        public final String getScheme$okhttp() {
            return this.scheme;
        }

        public final void setScheme$okhttp(String str) {
            this.scheme = str;
        }

        public final String getEncodedUsername$okhttp() {
            return this.encodedUsername;
        }

        public final void setEncodedUsername$okhttp(String str) {
            Intrinsics.checkNotNullParameter(str, "<set-?>");
            this.encodedUsername = str;
        }

        public final String getEncodedPassword$okhttp() {
            return this.encodedPassword;
        }

        public final void setEncodedPassword$okhttp(String str) {
            Intrinsics.checkNotNullParameter(str, "<set-?>");
            this.encodedPassword = str;
        }

        public final String getHost$okhttp() {
            return this.host;
        }

        public final void setHost$okhttp(String str) {
            this.host = str;
        }

        public final int getPort$okhttp() {
            return this.port;
        }

        public final void setPort$okhttp(int i) {
            this.port = i;
        }

        public final List<String> getEncodedPathSegments$okhttp() {
            return this.encodedPathSegments;
        }

        public final List<String> getEncodedQueryNamesAndValues$okhttp() {
            return this.encodedQueryNamesAndValues;
        }

        public final void setEncodedQueryNamesAndValues$okhttp(List<String> list) {
            this.encodedQueryNamesAndValues = list;
        }

        public final String getEncodedFragment$okhttp() {
            return this.encodedFragment;
        }

        public final void setEncodedFragment$okhttp(String str) {
            this.encodedFragment = str;
        }

        public final Builder scheme(String scheme2) {
            Intrinsics.checkNotNullParameter(scheme2, "scheme");
            Builder $this$scheme_u24lambda_u2d0 = this;
            if (StringsKt.equals(scheme2, "http", true)) {
                $this$scheme_u24lambda_u2d0.setScheme$okhttp("http");
            } else if (StringsKt.equals(scheme2, "https", true)) {
                $this$scheme_u24lambda_u2d0.setScheme$okhttp("https");
            } else {
                throw new IllegalArgumentException(Intrinsics.stringPlus("unexpected scheme: ", scheme2));
            }
            return this;
        }

        public final Builder username(String username) {
            Intrinsics.checkNotNullParameter(username, "username");
            setEncodedUsername$okhttp(Companion.canonicalize$okhttp$default(HttpUrl.Companion, username, 0, 0, " \"':;<=>@[]^`{}|/\\?#", false, false, false, false, (Charset) null, 251, (Object) null));
            return this;
        }

        public final Builder encodedUsername(String encodedUsername2) {
            Intrinsics.checkNotNullParameter(encodedUsername2, "encodedUsername");
            setEncodedUsername$okhttp(Companion.canonicalize$okhttp$default(HttpUrl.Companion, encodedUsername2, 0, 0, " \"':;<=>@[]^`{}|/\\?#", true, false, false, false, (Charset) null, 243, (Object) null));
            return this;
        }

        public final Builder password(String password) {
            Intrinsics.checkNotNullParameter(password, "password");
            setEncodedPassword$okhttp(Companion.canonicalize$okhttp$default(HttpUrl.Companion, password, 0, 0, " \"':;<=>@[]^`{}|/\\?#", false, false, false, false, (Charset) null, 251, (Object) null));
            return this;
        }

        public final Builder encodedPassword(String encodedPassword2) {
            Intrinsics.checkNotNullParameter(encodedPassword2, "encodedPassword");
            setEncodedPassword$okhttp(Companion.canonicalize$okhttp$default(HttpUrl.Companion, encodedPassword2, 0, 0, " \"':;<=>@[]^`{}|/\\?#", true, false, false, false, (Charset) null, 243, (Object) null));
            return this;
        }

        public final Builder host(String host2) {
            Intrinsics.checkNotNullParameter(host2, "host");
            Builder $this$host_u24lambda_u2d5 = this;
            String encoded = HostnamesKt.toCanonicalHost(Companion.percentDecode$okhttp$default(HttpUrl.Companion, host2, 0, 0, false, 7, (Object) null));
            if (encoded != null) {
                $this$host_u24lambda_u2d5.setHost$okhttp(encoded);
                return this;
            }
            throw new IllegalArgumentException(Intrinsics.stringPlus("unexpected host: ", host2));
        }

        public final Builder port(int port2) {
            Builder $this$port_u24lambda_u2d7 = this;
            boolean z = false;
            if (1 <= port2 && port2 < 65536) {
                z = true;
            }
            if (z) {
                $this$port_u24lambda_u2d7.setPort$okhttp(port2);
                return this;
            }
            throw new IllegalArgumentException(Intrinsics.stringPlus("unexpected port: ", Integer.valueOf(port2)).toString());
        }

        private final int effectivePort() {
            int i = this.port;
            if (i != -1) {
                return i;
            }
            Companion companion = HttpUrl.Companion;
            String str = this.scheme;
            Intrinsics.checkNotNull(str);
            return companion.defaultPort(str);
        }

        public final Builder addPathSegment(String pathSegment) {
            Intrinsics.checkNotNullParameter(pathSegment, "pathSegment");
            push(pathSegment, 0, pathSegment.length(), false, false);
            return this;
        }

        public final Builder addPathSegments(String pathSegments) {
            Intrinsics.checkNotNullParameter(pathSegments, "pathSegments");
            return addPathSegments(pathSegments, false);
        }

        public final Builder addEncodedPathSegment(String encodedPathSegment) {
            Intrinsics.checkNotNullParameter(encodedPathSegment, "encodedPathSegment");
            push(encodedPathSegment, 0, encodedPathSegment.length(), false, true);
            return this;
        }

        public final Builder addEncodedPathSegments(String encodedPathSegments2) {
            Intrinsics.checkNotNullParameter(encodedPathSegments2, "encodedPathSegments");
            return addPathSegments(encodedPathSegments2, true);
        }

        private final Builder addPathSegments(String pathSegments, boolean alreadyEncoded) {
            Builder $this$addPathSegments_u24lambda_u2d10 = this;
            int offset = 0;
            do {
                int segmentEnd = Util.delimiterOffset(pathSegments, "/\\", offset, pathSegments.length());
                $this$addPathSegments_u24lambda_u2d10.push(pathSegments, offset, segmentEnd, segmentEnd < pathSegments.length(), alreadyEncoded);
                offset = segmentEnd + 1;
            } while (offset <= pathSegments.length());
            return this;
        }

        public final Builder setPathSegment(int index, String pathSegment) {
            String str = pathSegment;
            Intrinsics.checkNotNullParameter(str, "pathSegment");
            Builder $this$setPathSegment_u24lambda_u2d12 = this;
            String canonicalPathSegment = Companion.canonicalize$okhttp$default(HttpUrl.Companion, pathSegment, 0, 0, HttpUrl.PATH_SEGMENT_ENCODE_SET, false, false, false, false, (Charset) null, 251, (Object) null);
            if (!$this$setPathSegment_u24lambda_u2d12.isDot(canonicalPathSegment) && !$this$setPathSegment_u24lambda_u2d12.isDotDot(canonicalPathSegment)) {
                $this$setPathSegment_u24lambda_u2d12.getEncodedPathSegments$okhttp().set(index, canonicalPathSegment);
                return this;
            }
            int i = index;
            throw new IllegalArgumentException(Intrinsics.stringPlus("unexpected path segment: ", str).toString());
        }

        public final Builder setEncodedPathSegment(int index, String encodedPathSegment) {
            String str = encodedPathSegment;
            Intrinsics.checkNotNullParameter(str, "encodedPathSegment");
            Builder $this$setEncodedPathSegment_u24lambda_u2d14 = this;
            String canonicalPathSegment = Companion.canonicalize$okhttp$default(HttpUrl.Companion, encodedPathSegment, 0, 0, HttpUrl.PATH_SEGMENT_ENCODE_SET, true, false, false, false, (Charset) null, 243, (Object) null);
            $this$setEncodedPathSegment_u24lambda_u2d14.getEncodedPathSegments$okhttp().set(index, canonicalPathSegment);
            if (!$this$setEncodedPathSegment_u24lambda_u2d14.isDot(canonicalPathSegment) && !$this$setEncodedPathSegment_u24lambda_u2d14.isDotDot(canonicalPathSegment)) {
                return this;
            }
            throw new IllegalArgumentException(Intrinsics.stringPlus("unexpected path segment: ", str).toString());
        }

        public final Builder removePathSegment(int index) {
            Builder $this$removePathSegment_u24lambda_u2d15 = this;
            $this$removePathSegment_u24lambda_u2d15.getEncodedPathSegments$okhttp().remove(index);
            if ($this$removePathSegment_u24lambda_u2d15.getEncodedPathSegments$okhttp().isEmpty()) {
                $this$removePathSegment_u24lambda_u2d15.getEncodedPathSegments$okhttp().add(HttpUrl.FRAGMENT_ENCODE_SET);
            }
            return this;
        }

        public final Builder encodedPath(String encodedPath) {
            Intrinsics.checkNotNullParameter(encodedPath, "encodedPath");
            Builder $this$encodedPath_u24lambda_u2d17 = this;
            if (StringsKt.startsWith$default(encodedPath, "/", false, 2, (Object) null)) {
                $this$encodedPath_u24lambda_u2d17.resolvePath(encodedPath, 0, encodedPath.length());
                return this;
            }
            throw new IllegalArgumentException(Intrinsics.stringPlus("unexpected encodedPath: ", encodedPath).toString());
        }

        public final Builder query(String query) {
            String canonicalize$okhttp$default;
            Builder $this$query_u24lambda_u2d18 = this;
            List<String> list = null;
            if (!(query == null || (canonicalize$okhttp$default = Companion.canonicalize$okhttp$default(HttpUrl.Companion, query, 0, 0, HttpUrl.QUERY_ENCODE_SET, false, false, true, false, (Charset) null, 219, (Object) null)) == null)) {
                list = HttpUrl.Companion.toQueryNamesAndValues$okhttp(canonicalize$okhttp$default);
            }
            $this$query_u24lambda_u2d18.setEncodedQueryNamesAndValues$okhttp(list);
            return this;
        }

        public final Builder encodedQuery(String encodedQuery) {
            String canonicalize$okhttp$default;
            Builder $this$encodedQuery_u24lambda_u2d19 = this;
            List<String> list = null;
            if (!(encodedQuery == null || (canonicalize$okhttp$default = Companion.canonicalize$okhttp$default(HttpUrl.Companion, encodedQuery, 0, 0, HttpUrl.QUERY_ENCODE_SET, true, false, true, false, (Charset) null, 211, (Object) null)) == null)) {
                list = HttpUrl.Companion.toQueryNamesAndValues$okhttp(canonicalize$okhttp$default);
            }
            $this$encodedQuery_u24lambda_u2d19.setEncodedQueryNamesAndValues$okhttp(list);
            return this;
        }

        public final Builder addQueryParameter(String name, String value) {
            Intrinsics.checkNotNullParameter(name, "name");
            Builder $this$addQueryParameter_u24lambda_u2d20 = this;
            if ($this$addQueryParameter_u24lambda_u2d20.getEncodedQueryNamesAndValues$okhttp() == null) {
                $this$addQueryParameter_u24lambda_u2d20.setEncodedQueryNamesAndValues$okhttp(new ArrayList());
            }
            List<String> encodedQueryNamesAndValues$okhttp = $this$addQueryParameter_u24lambda_u2d20.getEncodedQueryNamesAndValues$okhttp();
            Intrinsics.checkNotNull(encodedQueryNamesAndValues$okhttp);
            encodedQueryNamesAndValues$okhttp.add(Companion.canonicalize$okhttp$default(HttpUrl.Companion, name, 0, 0, HttpUrl.QUERY_COMPONENT_ENCODE_SET, false, false, true, false, (Charset) null, 219, (Object) null));
            List<String> encodedQueryNamesAndValues$okhttp2 = $this$addQueryParameter_u24lambda_u2d20.getEncodedQueryNamesAndValues$okhttp();
            Intrinsics.checkNotNull(encodedQueryNamesAndValues$okhttp2);
            encodedQueryNamesAndValues$okhttp2.add(value == null ? null : Companion.canonicalize$okhttp$default(HttpUrl.Companion, value, 0, 0, HttpUrl.QUERY_COMPONENT_ENCODE_SET, false, false, true, false, (Charset) null, 219, (Object) null));
            return this;
        }

        public final Builder addEncodedQueryParameter(String encodedName, String encodedValue) {
            Intrinsics.checkNotNullParameter(encodedName, "encodedName");
            Builder $this$addEncodedQueryParameter_u24lambda_u2d21 = this;
            if ($this$addEncodedQueryParameter_u24lambda_u2d21.getEncodedQueryNamesAndValues$okhttp() == null) {
                $this$addEncodedQueryParameter_u24lambda_u2d21.setEncodedQueryNamesAndValues$okhttp(new ArrayList());
            }
            List<String> encodedQueryNamesAndValues$okhttp = $this$addEncodedQueryParameter_u24lambda_u2d21.getEncodedQueryNamesAndValues$okhttp();
            Intrinsics.checkNotNull(encodedQueryNamesAndValues$okhttp);
            encodedQueryNamesAndValues$okhttp.add(Companion.canonicalize$okhttp$default(HttpUrl.Companion, encodedName, 0, 0, HttpUrl.QUERY_COMPONENT_REENCODE_SET, true, false, true, false, (Charset) null, 211, (Object) null));
            List<String> encodedQueryNamesAndValues$okhttp2 = $this$addEncodedQueryParameter_u24lambda_u2d21.getEncodedQueryNamesAndValues$okhttp();
            Intrinsics.checkNotNull(encodedQueryNamesAndValues$okhttp2);
            encodedQueryNamesAndValues$okhttp2.add(encodedValue == null ? null : Companion.canonicalize$okhttp$default(HttpUrl.Companion, encodedValue, 0, 0, HttpUrl.QUERY_COMPONENT_REENCODE_SET, true, false, true, false, (Charset) null, 211, (Object) null));
            return this;
        }

        public final Builder setQueryParameter(String name, String value) {
            Intrinsics.checkNotNullParameter(name, "name");
            Builder $this$setQueryParameter_u24lambda_u2d22 = this;
            $this$setQueryParameter_u24lambda_u2d22.removeAllQueryParameters(name);
            $this$setQueryParameter_u24lambda_u2d22.addQueryParameter(name, value);
            return this;
        }

        public final Builder setEncodedQueryParameter(String encodedName, String encodedValue) {
            Intrinsics.checkNotNullParameter(encodedName, "encodedName");
            Builder $this$setEncodedQueryParameter_u24lambda_u2d23 = this;
            $this$setEncodedQueryParameter_u24lambda_u2d23.removeAllEncodedQueryParameters(encodedName);
            $this$setEncodedQueryParameter_u24lambda_u2d23.addEncodedQueryParameter(encodedName, encodedValue);
            return this;
        }

        public final Builder removeAllQueryParameters(String name) {
            Intrinsics.checkNotNullParameter(name, "name");
            Builder $this$removeAllQueryParameters_u24lambda_u2d24 = this;
            if ($this$removeAllQueryParameters_u24lambda_u2d24.getEncodedQueryNamesAndValues$okhttp() == null) {
                return $this$removeAllQueryParameters_u24lambda_u2d24;
            }
            $this$removeAllQueryParameters_u24lambda_u2d24.removeAllCanonicalQueryParameters(Companion.canonicalize$okhttp$default(HttpUrl.Companion, name, 0, 0, HttpUrl.QUERY_COMPONENT_ENCODE_SET, false, false, true, false, (Charset) null, 219, (Object) null));
            return this;
        }

        public final Builder removeAllEncodedQueryParameters(String encodedName) {
            Intrinsics.checkNotNullParameter(encodedName, "encodedName");
            Builder $this$removeAllEncodedQueryParameters_u24lambda_u2d25 = this;
            if ($this$removeAllEncodedQueryParameters_u24lambda_u2d25.getEncodedQueryNamesAndValues$okhttp() == null) {
                return $this$removeAllEncodedQueryParameters_u24lambda_u2d25;
            }
            $this$removeAllEncodedQueryParameters_u24lambda_u2d25.removeAllCanonicalQueryParameters(Companion.canonicalize$okhttp$default(HttpUrl.Companion, encodedName, 0, 0, HttpUrl.QUERY_COMPONENT_REENCODE_SET, true, false, true, false, (Charset) null, 211, (Object) null));
            return this;
        }

        private final void removeAllCanonicalQueryParameters(String canonicalName) {
            int i;
            List<String> list = this.encodedQueryNamesAndValues;
            Intrinsics.checkNotNull(list);
            int size = list.size() - 2;
            int progressionLastElement = ProgressionUtilKt.getProgressionLastElement(size, 0, -2);
            if (progressionLastElement <= size) {
                do {
                    i = size;
                    size -= 2;
                    List<String> list2 = this.encodedQueryNamesAndValues;
                    Intrinsics.checkNotNull(list2);
                    if (Intrinsics.areEqual((Object) canonicalName, (Object) list2.get(i))) {
                        List<String> list3 = this.encodedQueryNamesAndValues;
                        Intrinsics.checkNotNull(list3);
                        list3.remove(i + 1);
                        List<String> list4 = this.encodedQueryNamesAndValues;
                        Intrinsics.checkNotNull(list4);
                        list4.remove(i);
                        List<String> list5 = this.encodedQueryNamesAndValues;
                        Intrinsics.checkNotNull(list5);
                        if (list5.isEmpty()) {
                            this.encodedQueryNamesAndValues = null;
                            return;
                        }
                    }
                } while (i != progressionLastElement);
            }
        }

        public final Builder fragment(String fragment) {
            setEncodedFragment$okhttp(fragment == null ? null : Companion.canonicalize$okhttp$default(HttpUrl.Companion, fragment, 0, 0, HttpUrl.FRAGMENT_ENCODE_SET, false, false, false, true, (Charset) null, 187, (Object) null));
            return this;
        }

        public final Builder encodedFragment(String encodedFragment2) {
            setEncodedFragment$okhttp(encodedFragment2 == null ? null : Companion.canonicalize$okhttp$default(HttpUrl.Companion, encodedFragment2, 0, 0, HttpUrl.FRAGMENT_ENCODE_SET, true, false, false, true, (Charset) null, 179, (Object) null));
            return this;
        }

        public final Builder reencodeForUri$okhttp() {
            Builder $this$reencodeForUri_u24lambda_u2d28 = this;
            String host$okhttp = $this$reencodeForUri_u24lambda_u2d28.getHost$okhttp();
            String str = null;
            $this$reencodeForUri_u24lambda_u2d28.setHost$okhttp(host$okhttp == null ? null : new Regex("[\"<>^`{|}]").replace((CharSequence) host$okhttp, HttpUrl.FRAGMENT_ENCODE_SET));
            int size = $this$reencodeForUri_u24lambda_u2d28.getEncodedPathSegments$okhttp().size();
            int i = 0;
            int i2 = 0;
            while (i2 < size) {
                int i3 = i2;
                i2++;
                $this$reencodeForUri_u24lambda_u2d28.getEncodedPathSegments$okhttp().set(i3, Companion.canonicalize$okhttp$default(HttpUrl.Companion, $this$reencodeForUri_u24lambda_u2d28.getEncodedPathSegments$okhttp().get(i3), 0, 0, HttpUrl.PATH_SEGMENT_ENCODE_SET_URI, true, true, false, false, (Charset) null, 227, (Object) null));
            }
            List encodedQueryNamesAndValues2 = $this$reencodeForUri_u24lambda_u2d28.getEncodedQueryNamesAndValues$okhttp();
            if (encodedQueryNamesAndValues2 != null) {
                int size2 = encodedQueryNamesAndValues2.size();
                while (i < size2) {
                    int i4 = i;
                    i++;
                    String str2 = encodedQueryNamesAndValues2.get(i4);
                    encodedQueryNamesAndValues2.set(i4, str2 == null ? null : Companion.canonicalize$okhttp$default(HttpUrl.Companion, str2, 0, 0, HttpUrl.QUERY_COMPONENT_ENCODE_SET_URI, true, true, true, false, (Charset) null, 195, (Object) null));
                }
            }
            String encodedFragment$okhttp = $this$reencodeForUri_u24lambda_u2d28.getEncodedFragment$okhttp();
            if (encodedFragment$okhttp != null) {
                str = Companion.canonicalize$okhttp$default(HttpUrl.Companion, encodedFragment$okhttp, 0, 0, HttpUrl.FRAGMENT_ENCODE_SET_URI, true, true, false, true, (Charset) null, 163, (Object) null);
            }
            $this$reencodeForUri_u24lambda_u2d28.setEncodedFragment$okhttp(str);
            return this;
        }

        public final HttpUrl build() {
            List list;
            String str = this.scheme;
            if (str != null) {
                String percentDecode$okhttp$default = Companion.percentDecode$okhttp$default(HttpUrl.Companion, this.encodedUsername, 0, 0, false, 7, (Object) null);
                String percentDecode$okhttp$default2 = Companion.percentDecode$okhttp$default(HttpUrl.Companion, this.encodedPassword, 0, 0, false, 7, (Object) null);
                String str2 = this.host;
                if (str2 != null) {
                    int effectivePort = effectivePort();
                    Iterable<String> $this$map$iv = this.encodedPathSegments;
                    Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
                    for (String it : $this$map$iv) {
                        destination$iv$iv.add(Companion.percentDecode$okhttp$default(HttpUrl.Companion, it, 0, 0, false, 7, (Object) null));
                    }
                    List list2 = (List) destination$iv$iv;
                    Iterable $this$map$iv2 = this.encodedQueryNamesAndValues;
                    if ($this$map$iv2 == null) {
                        list = null;
                    } else {
                        Iterable<String> $this$map$iv3 = $this$map$iv2;
                        Collection destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv3, 10));
                        for (String it2 : $this$map$iv3) {
                            destination$iv$iv2.add(it2 == null ? null : Companion.percentDecode$okhttp$default(HttpUrl.Companion, it2, 0, 0, true, 3, (Object) null));
                        }
                        list = (List) destination$iv$iv2;
                    }
                    String str3 = this.encodedFragment;
                    return new HttpUrl(str, percentDecode$okhttp$default, percentDecode$okhttp$default2, str2, effectivePort, list2, list, str3 == null ? null : Companion.percentDecode$okhttp$default(HttpUrl.Companion, str3, 0, 0, false, 7, (Object) null), toString());
                }
                throw new IllegalStateException("host == null");
            }
            throw new IllegalStateException("scheme == null");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0043, code lost:
            if ((getEncodedPassword$okhttp().length() > 0) != false) goto L_0x0045;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:34:0x00bf, code lost:
            if (r3 != r4.defaultPort(r5)) goto L_0x00c1;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.String toString() {
            /*
                r8 = this;
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                r1 = r0
                r2 = 0
                java.lang.String r3 = r8.getScheme$okhttp()
                if (r3 == 0) goto L_0x001a
                java.lang.String r3 = r8.getScheme$okhttp()
                r1.append(r3)
                java.lang.String r3 = "://"
                r1.append(r3)
                goto L_0x001f
            L_0x001a:
                java.lang.String r3 = "//"
                r1.append(r3)
            L_0x001f:
                java.lang.String r3 = r8.getEncodedUsername$okhttp()
                java.lang.CharSequence r3 = (java.lang.CharSequence) r3
                int r3 = r3.length()
                r4 = 1
                r5 = 0
                if (r3 <= 0) goto L_0x002f
                r3 = r4
                goto L_0x0030
            L_0x002f:
                r3 = r5
            L_0x0030:
                r6 = 58
                if (r3 != 0) goto L_0x0045
                java.lang.String r3 = r8.getEncodedPassword$okhttp()
                java.lang.CharSequence r3 = (java.lang.CharSequence) r3
                int r3 = r3.length()
                if (r3 <= 0) goto L_0x0042
                r3 = r4
                goto L_0x0043
            L_0x0042:
                r3 = r5
            L_0x0043:
                if (r3 == 0) goto L_0x006b
            L_0x0045:
                java.lang.String r3 = r8.getEncodedUsername$okhttp()
                r1.append(r3)
                java.lang.String r3 = r8.getEncodedPassword$okhttp()
                java.lang.CharSequence r3 = (java.lang.CharSequence) r3
                int r3 = r3.length()
                if (r3 <= 0) goto L_0x0059
                goto L_0x005a
            L_0x0059:
                r4 = r5
            L_0x005a:
                if (r4 == 0) goto L_0x0066
                r1.append(r6)
                java.lang.String r3 = r8.getEncodedPassword$okhttp()
                r1.append(r3)
            L_0x0066:
                r3 = 64
                r1.append(r3)
            L_0x006b:
                java.lang.String r3 = r8.getHost$okhttp()
                if (r3 == 0) goto L_0x009b
                java.lang.String r3 = r8.getHost$okhttp()
                kotlin.jvm.internal.Intrinsics.checkNotNull(r3)
                java.lang.CharSequence r3 = (java.lang.CharSequence) r3
                r4 = 2
                r7 = 0
                boolean r3 = kotlin.text.StringsKt.contains$default((java.lang.CharSequence) r3, (char) r6, (boolean) r5, (int) r4, (java.lang.Object) r7)
                if (r3 == 0) goto L_0x0094
                r3 = 91
                r1.append(r3)
                java.lang.String r3 = r8.getHost$okhttp()
                r1.append(r3)
                r3 = 93
                r1.append(r3)
                goto L_0x009b
            L_0x0094:
                java.lang.String r3 = r8.getHost$okhttp()
                r1.append(r3)
            L_0x009b:
                int r3 = r8.getPort$okhttp()
                r4 = -1
                if (r3 != r4) goto L_0x00a8
                java.lang.String r3 = r8.getScheme$okhttp()
                if (r3 == 0) goto L_0x00c7
            L_0x00a8:
                int r3 = r8.effectivePort()
                java.lang.String r4 = r8.getScheme$okhttp()
                if (r4 == 0) goto L_0x00c1
                okhttp3.HttpUrl$Companion r4 = okhttp3.HttpUrl.Companion
                java.lang.String r5 = r8.getScheme$okhttp()
                kotlin.jvm.internal.Intrinsics.checkNotNull(r5)
                int r4 = r4.defaultPort(r5)
                if (r3 == r4) goto L_0x00c7
            L_0x00c1:
                r1.append(r6)
                r1.append(r3)
            L_0x00c7:
                okhttp3.HttpUrl$Companion r3 = okhttp3.HttpUrl.Companion
                java.util.List r4 = r8.getEncodedPathSegments$okhttp()
                r3.toPathString$okhttp(r4, r1)
                java.util.List r3 = r8.getEncodedQueryNamesAndValues$okhttp()
                if (r3 == 0) goto L_0x00e7
                r3 = 63
                r1.append(r3)
                okhttp3.HttpUrl$Companion r3 = okhttp3.HttpUrl.Companion
                java.util.List r4 = r8.getEncodedQueryNamesAndValues$okhttp()
                kotlin.jvm.internal.Intrinsics.checkNotNull(r4)
                r3.toQueryString$okhttp(r4, r1)
            L_0x00e7:
                java.lang.String r3 = r8.getEncodedFragment$okhttp()
                if (r3 == 0) goto L_0x00f9
                r3 = 35
                r1.append(r3)
                java.lang.String r3 = r8.getEncodedFragment$okhttp()
                r1.append(r3)
            L_0x00f9:
                java.lang.String r0 = r0.toString()
                java.lang.String r1 = "StringBuilder().apply(builderAction).toString()"
                kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r0, r1)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.HttpUrl.Builder.toString():java.lang.String");
        }

        public final Builder parse$okhttp(HttpUrl base, String input) {
            int limit;
            int pos;
            int c;
            String str;
            String str2;
            int limit2;
            int schemeDelimiterOffset;
            int i;
            int slashCount;
            int limit3;
            String str3;
            String str4 = input;
            Intrinsics.checkNotNullParameter(str4, "input");
            int pos2 = Util.indexOfFirstNonAsciiWhitespace$default(str4, 0, 0, 3, (Object) null);
            int limit4 = Util.indexOfLastNonAsciiWhitespace$default(str4, pos2, 0, 2, (Object) null);
            Companion companion = Companion;
            int schemeDelimiterOffset2 = companion.schemeDelimiterOffset(str4, pos2, limit4);
            String str5 = "this as java.lang.String…ing(startIndex, endIndex)";
            int i2 = 65535;
            int pos3 = 1;
            if (schemeDelimiterOffset2 != -1) {
                if (StringsKt.startsWith(str4, "https:", pos2, true)) {
                    this.scheme = "https";
                    pos2 += "https:".length();
                } else if (StringsKt.startsWith(str4, "http:", pos2, true)) {
                    this.scheme = "http";
                    pos2 += "http:".length();
                } else {
                    StringBuilder append = new StringBuilder().append("Expected URL scheme 'http' or 'https' but was '");
                    String substring = str4.substring(0, schemeDelimiterOffset2);
                    Intrinsics.checkNotNullExpressionValue(substring, str5);
                    throw new IllegalArgumentException(append.append(substring).append('\'').toString());
                }
            } else if (base != null) {
                this.scheme = base.scheme();
            } else {
                int i3 = limit4;
                throw new IllegalArgumentException(Intrinsics.stringPlus("Expected URL scheme 'http' or 'https' but no scheme was found for ", input.length() > 6 ? Intrinsics.stringPlus(StringsKt.take(str4, 6), "...") : str4));
            }
            int slashCount2 = companion.slashCount(str4, pos2, limit4);
            char c2 = '#';
            if (slashCount2 >= 2 || base == null || !Intrinsics.areEqual((Object) base.scheme(), (Object) this.scheme)) {
                int pos4 = pos2 + slashCount2;
                boolean hasUsername = false;
                boolean hasPassword = false;
                while (true) {
                    int componentDelimiterOffset = Util.delimiterOffset(str4, "@/\\?#", pos4, limit4);
                    if (componentDelimiterOffset != limit4) {
                        c = str4.charAt(componentDelimiterOffset);
                    } else {
                        c = i2;
                    }
                    switch (c) {
                        case -1:
                        case 35:
                        case 47:
                        case 63:
                        case 92:
                            int i4 = slashCount2;
                            int i5 = pos3;
                            String str6 = str5;
                            int i6 = schemeDelimiterOffset2;
                            limit = limit4;
                            int limit5 = componentDelimiterOffset;
                            Companion companion2 = Companion;
                            int pos5 = pos4;
                            int portColonOffset = companion2.portColonOffset(str4, pos5, limit5);
                            if (portColonOffset + 1 < limit5) {
                                this.host = HostnamesKt.toCanonicalHost(Companion.percentDecode$okhttp$default(HttpUrl.Companion, input, pos5, portColonOffset, false, 4, (Object) null));
                                int access$parsePort = companion2.parsePort(str4, portColonOffset + 1, limit5);
                                this.port = access$parsePort;
                                if ((access$parsePort != -1 ? i5 : 0) != 0) {
                                    str = str6;
                                } else {
                                    StringBuilder append2 = new StringBuilder().append("Invalid URL port: \"");
                                    String substring2 = str4.substring(portColonOffset + 1, limit5);
                                    Intrinsics.checkNotNullExpressionValue(substring2, str6);
                                    throw new IllegalArgumentException(append2.append(substring2).append(Typography.quote).toString().toString());
                                }
                            } else {
                                str = str6;
                                this.host = HostnamesKt.toCanonicalHost(Companion.percentDecode$okhttp$default(HttpUrl.Companion, input, pos5, portColonOffset, false, 4, (Object) null));
                                Companion companion3 = HttpUrl.Companion;
                                String str7 = this.scheme;
                                Intrinsics.checkNotNull(str7);
                                this.port = companion3.defaultPort(str7);
                            }
                            if ((this.host != null ? i5 : 0) != 0) {
                                pos2 = limit5;
                                break;
                            } else {
                                StringBuilder append3 = new StringBuilder().append("Invalid URL host: \"");
                                String substring3 = str4.substring(pos5, portColonOffset);
                                Intrinsics.checkNotNullExpressionValue(substring3, str);
                                throw new IllegalArgumentException(append3.append(substring3).append(Typography.quote).toString().toString());
                            }
                        case 64:
                            if (!hasPassword) {
                                int passwordColonOffset = Util.delimiterOffset(str4, ':', pos4, componentDelimiterOffset);
                                String str8 = "%40";
                                int componentDelimiterOffset2 = componentDelimiterOffset;
                                int i7 = pos4;
                                slashCount = slashCount2;
                                i = pos3;
                                str2 = str5;
                                schemeDelimiterOffset = schemeDelimiterOffset2;
                                String canonicalUsername = Companion.canonicalize$okhttp$default(HttpUrl.Companion, input, pos4, passwordColonOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, false, false, (Charset) null, 240, (Object) null);
                                if (hasUsername) {
                                    str3 = this.encodedUsername + str8 + canonicalUsername;
                                } else {
                                    str3 = canonicalUsername;
                                }
                                this.encodedUsername = str3;
                                int passwordColonOffset2 = passwordColonOffset;
                                int componentDelimiterOffset3 = componentDelimiterOffset2;
                                if (passwordColonOffset2 != componentDelimiterOffset3) {
                                    hasPassword = true;
                                    int i8 = passwordColonOffset2;
                                    String str9 = canonicalUsername;
                                    this.encodedPassword = Companion.canonicalize$okhttp$default(HttpUrl.Companion, input, passwordColonOffset2 + 1, componentDelimiterOffset3, " \"':;<=>@[]^`{}|/\\?#", true, false, false, false, (Charset) null, 240, (Object) null);
                                } else {
                                    String str10 = canonicalUsername;
                                }
                                hasUsername = true;
                                limit2 = limit4;
                                limit3 = componentDelimiterOffset3;
                            } else {
                                slashCount = slashCount2;
                                i = pos3;
                                str2 = str5;
                                schemeDelimiterOffset = schemeDelimiterOffset2;
                                int componentDelimiterOffset4 = componentDelimiterOffset;
                                limit2 = limit4;
                                limit3 = componentDelimiterOffset4;
                                this.encodedPassword += "%40" + Companion.canonicalize$okhttp$default(HttpUrl.Companion, input, pos4, componentDelimiterOffset4, " \"':;<=>@[]^`{}|/\\?#", true, false, false, false, (Charset) null, 240, (Object) null);
                            }
                            pos4 = limit3 + 1;
                            slashCount2 = slashCount;
                            pos3 = i;
                            schemeDelimiterOffset2 = schemeDelimiterOffset;
                            limit4 = limit2;
                            str5 = str2;
                            c2 = '#';
                            i2 = -1;
                            continue;
                        default:
                            char c3 = c2;
                            int slashCount3 = slashCount2;
                            int i9 = pos3;
                            int i10 = i2;
                            String str11 = str5;
                            int i11 = schemeDelimiterOffset2;
                            int limit6 = limit4;
                            int limit7 = componentDelimiterOffset;
                            int i12 = pos4;
                            limit4 = limit6;
                            slashCount2 = slashCount3;
                            pos3 = i9;
                            continue;
                    }
                }
            } else {
                this.encodedUsername = base.encodedUsername();
                this.encodedPassword = base.encodedPassword();
                this.host = base.host();
                this.port = base.port();
                this.encodedPathSegments.clear();
                this.encodedPathSegments.addAll(base.encodedPathSegments());
                if (pos2 == limit4 || str4.charAt(pos2) == '#') {
                    encodedQuery(base.encodedQuery());
                }
                int i13 = slashCount2;
                int i14 = schemeDelimiterOffset2;
                limit = limit4;
            }
            int limit8 = limit;
            int pathDelimiterOffset = Util.delimiterOffset(str4, "?#", pos2, limit8);
            resolvePath(str4, pos2, pathDelimiterOffset);
            int pos6 = pathDelimiterOffset;
            if (pos6 >= limit8 || str4.charAt(pos6) != '?') {
                pos = pos6;
            } else {
                int queryDelimiterOffset = Util.delimiterOffset(str4, '#', pos6, limit8);
                int i15 = pos6;
                this.encodedQueryNamesAndValues = HttpUrl.Companion.toQueryNamesAndValues$okhttp(Companion.canonicalize$okhttp$default(HttpUrl.Companion, input, pos6 + 1, queryDelimiterOffset, HttpUrl.QUERY_ENCODE_SET, true, false, true, false, (Charset) null, 208, (Object) null));
                pos = queryDelimiterOffset;
            }
            if (pos >= limit8 || str4.charAt(pos) != '#') {
            } else {
                int i16 = pos;
                this.encodedFragment = Companion.canonicalize$okhttp$default(HttpUrl.Companion, input, pos + 1, limit8, HttpUrl.FRAGMENT_ENCODE_SET, true, false, false, true, (Charset) null, 176, (Object) null);
            }
            return this;
        }

        private final void resolvePath(String input, int startPos, int limit) {
            int pos = startPos;
            if (pos != limit) {
                char c = input.charAt(pos);
                if (c == '/' || c == '\\') {
                    this.encodedPathSegments.clear();
                    this.encodedPathSegments.add(HttpUrl.FRAGMENT_ENCODE_SET);
                    pos++;
                } else {
                    List<String> list = this.encodedPathSegments;
                    list.set(list.size() - 1, HttpUrl.FRAGMENT_ENCODE_SET);
                }
                int i = pos;
                while (i < limit) {
                    int pathSegmentDelimiterOffset = Util.delimiterOffset(input, "/\\", i, limit);
                    boolean segmentHasTrailingSlash = pathSegmentDelimiterOffset < limit;
                    push(input, i, pathSegmentDelimiterOffset, segmentHasTrailingSlash, true);
                    i = pathSegmentDelimiterOffset;
                    if (segmentHasTrailingSlash) {
                        i++;
                    }
                }
            }
        }

        private final void push(String input, int pos, int limit, boolean addTrailingSlash, boolean alreadyEncoded) {
            String segment = Companion.canonicalize$okhttp$default(HttpUrl.Companion, input, pos, limit, HttpUrl.PATH_SEGMENT_ENCODE_SET, alreadyEncoded, false, false, false, (Charset) null, 240, (Object) null);
            if (!isDot(segment)) {
                if (isDotDot(segment)) {
                    pop();
                    return;
                }
                List<String> list = this.encodedPathSegments;
                if (list.get(list.size() - 1).length() == 0) {
                    List<String> list2 = this.encodedPathSegments;
                    list2.set(list2.size() - 1, segment);
                } else {
                    this.encodedPathSegments.add(segment);
                }
                if (addTrailingSlash) {
                    this.encodedPathSegments.add(HttpUrl.FRAGMENT_ENCODE_SET);
                }
            }
        }

        private final boolean isDot(String input) {
            return Intrinsics.areEqual((Object) input, (Object) ".") || StringsKt.equals(input, "%2e", true);
        }

        private final boolean isDotDot(String input) {
            if (Intrinsics.areEqual((Object) input, (Object) "..") || StringsKt.equals(input, "%2e.", true) || StringsKt.equals(input, ".%2e", true) || StringsKt.equals(input, "%2e%2e", true)) {
                return true;
            }
            return false;
        }

        private final void pop() {
            List<String> list = this.encodedPathSegments;
            if (!(list.remove(list.size() - 1).length() == 0) || !(!this.encodedPathSegments.isEmpty())) {
                this.encodedPathSegments.add(HttpUrl.FRAGMENT_ENCODE_SET);
                return;
            }
            List<String> list2 = this.encodedPathSegments;
            list2.set(list2.size() - 1, HttpUrl.FRAGMENT_ENCODE_SET);
        }

        @Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J \u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\u0006H\u0002J \u0010\n\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\u0006H\u0002J \u0010\u000b\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\u0006H\u0002J\u001c\u0010\f\u001a\u00020\u0006*\u00020\u00042\u0006\u0010\b\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\u0006H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000¨\u0006\r"}, d2 = {"Lokhttp3/HttpUrl$Builder$Companion;", "", "()V", "INVALID_HOST", "", "parsePort", "", "input", "pos", "limit", "portColonOffset", "schemeDelimiterOffset", "slashCount", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
        /* compiled from: HttpUrl.kt */
        public static final class Companion {
            public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
                this();
            }

            private Companion() {
            }

            /* access modifiers changed from: private */
            public final int schemeDelimiterOffset(String input, int pos, int limit) {
                if (limit - pos < 2) {
                    return -1;
                }
                char c0 = input.charAt(pos);
                if ((Intrinsics.compare((int) c0, 97) < 0 || Intrinsics.compare((int) c0, 122) > 0) && (Intrinsics.compare((int) c0, 65) < 0 || Intrinsics.compare((int) c0, 90) > 0)) {
                    return -1;
                }
                int i = pos + 1;
                while (i < limit) {
                    int i2 = i;
                    i++;
                    char charAt = input.charAt(i2);
                    boolean z = false;
                    if (((((('a' <= charAt && charAt < '{') || ('A' <= charAt && charAt < '[')) || ('0' <= charAt && charAt < ':')) || charAt == '+') || charAt == '-') || charAt == '.') {
                        z = true;
                        continue;
                    }
                    if (!z) {
                        if (charAt == ':') {
                            return i2;
                        }
                        return -1;
                    }
                }
                return -1;
            }

            /* access modifiers changed from: private */
            public final int slashCount(String $this$slashCount, int pos, int limit) {
                int slashCount = 0;
                int i = pos;
                while (i < limit) {
                    int i2 = i;
                    i++;
                    char c = $this$slashCount.charAt(i2);
                    if (c != '\\' && c != '/') {
                        break;
                    }
                    slashCount++;
                }
                return slashCount;
            }

            /* access modifiers changed from: private */
            public final int portColonOffset(String input, int pos, int limit) {
                int i = pos;
                while (i < limit) {
                    char charAt = input.charAt(i);
                    if (charAt == '[') {
                        do {
                            i++;
                            if (i >= limit) {
                                break;
                            }
                        } while (input.charAt(i) == ']');
                    } else if (charAt == ':') {
                        return i;
                    }
                    i++;
                }
                return limit;
            }

            /* access modifiers changed from: private */
            public final int parsePort(String input, int pos, int limit) {
                try {
                    int i = Integer.parseInt(Companion.canonicalize$okhttp$default(HttpUrl.Companion, input, pos, limit, HttpUrl.FRAGMENT_ENCODE_SET, false, false, false, false, (Charset) null, 248, (Object) null));
                    boolean z = false;
                    if (1 <= i && i < 65536) {
                        z = true;
                    }
                    if (z) {
                        return i;
                    }
                    return -1;
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
        }
    }

    @Metadata(d1 = {"\u0000p\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0019\n\u0002\b\t\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0004H\u0007J\u0017\u0010\u0014\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u0016\u001a\u00020\u0017H\u0007¢\u0006\u0002\b\u0018J\u0017\u0010\u0014\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u0019\u001a\u00020\u001aH\u0007¢\u0006\u0002\b\u0018J\u0015\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0019\u001a\u00020\u0004H\u0007¢\u0006\u0002\b\u0018J\u0017\u0010\u001b\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u0019\u001a\u00020\u0004H\u0007¢\u0006\u0002\b\u001cJa\u0010\u001d\u001a\u00020\u0004*\u00020\u00042\b\b\u0002\u0010\u001e\u001a\u00020\u00122\b\b\u0002\u0010\u001f\u001a\u00020\u00122\u0006\u0010 \u001a\u00020\u00042\b\b\u0002\u0010!\u001a\u00020\"2\b\b\u0002\u0010#\u001a\u00020\"2\b\b\u0002\u0010$\u001a\u00020\"2\b\b\u0002\u0010%\u001a\u00020\"2\n\b\u0002\u0010&\u001a\u0004\u0018\u00010'H\u0000¢\u0006\u0002\b(J\u001c\u0010)\u001a\u00020\"*\u00020\u00042\u0006\u0010\u001e\u001a\u00020\u00122\u0006\u0010\u001f\u001a\u00020\u0012H\u0002J/\u0010*\u001a\u00020\u0004*\u00020\u00042\b\b\u0002\u0010\u001e\u001a\u00020\u00122\b\b\u0002\u0010\u001f\u001a\u00020\u00122\b\b\u0002\u0010$\u001a\u00020\"H\u0000¢\u0006\u0002\b+J\u0011\u0010,\u001a\u00020\u0015*\u00020\u0004H\u0007¢\u0006\u0002\b\u0014J\u0013\u0010-\u001a\u0004\u0018\u00010\u0015*\u00020\u0017H\u0007¢\u0006\u0002\b\u0014J\u0013\u0010-\u001a\u0004\u0018\u00010\u0015*\u00020\u001aH\u0007¢\u0006\u0002\b\u0014J\u0013\u0010-\u001a\u0004\u0018\u00010\u0015*\u00020\u0004H\u0007¢\u0006\u0002\b\u001bJ#\u0010.\u001a\u00020/*\b\u0012\u0004\u0012\u00020\u0004002\n\u00101\u001a\u000602j\u0002`3H\u0000¢\u0006\u0002\b4J\u0019\u00105\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000406*\u00020\u0004H\u0000¢\u0006\u0002\b7J%\u00108\u001a\u00020/*\n\u0012\u0006\u0012\u0004\u0018\u00010\u0004002\n\u00101\u001a\u000602j\u0002`3H\u0000¢\u0006\u0002\b9JV\u0010:\u001a\u00020/*\u00020;2\u0006\u0010<\u001a\u00020\u00042\u0006\u0010\u001e\u001a\u00020\u00122\u0006\u0010\u001f\u001a\u00020\u00122\u0006\u0010 \u001a\u00020\u00042\u0006\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020\"2\u0006\u0010$\u001a\u00020\"2\u0006\u0010%\u001a\u00020\"2\b\u0010&\u001a\u0004\u0018\u00010'H\u0002J,\u0010=\u001a\u00020/*\u00020;2\u0006\u0010>\u001a\u00020\u00042\u0006\u0010\u001e\u001a\u00020\u00122\u0006\u0010\u001f\u001a\u00020\u00122\u0006\u0010$\u001a\u00020\"H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000¨\u0006?"}, d2 = {"Lokhttp3/HttpUrl$Companion;", "", "()V", "FORM_ENCODE_SET", "", "FRAGMENT_ENCODE_SET", "FRAGMENT_ENCODE_SET_URI", "HEX_DIGITS", "", "PASSWORD_ENCODE_SET", "PATH_SEGMENT_ENCODE_SET", "PATH_SEGMENT_ENCODE_SET_URI", "QUERY_COMPONENT_ENCODE_SET", "QUERY_COMPONENT_ENCODE_SET_URI", "QUERY_COMPONENT_REENCODE_SET", "QUERY_ENCODE_SET", "USERNAME_ENCODE_SET", "defaultPort", "", "scheme", "get", "Lokhttp3/HttpUrl;", "uri", "Ljava/net/URI;", "-deprecated_get", "url", "Ljava/net/URL;", "parse", "-deprecated_parse", "canonicalize", "pos", "limit", "encodeSet", "alreadyEncoded", "", "strict", "plusIsSpace", "unicodeAllowed", "charset", "Ljava/nio/charset/Charset;", "canonicalize$okhttp", "isPercentEncoded", "percentDecode", "percentDecode$okhttp", "toHttpUrl", "toHttpUrlOrNull", "toPathString", "", "", "out", "Ljava/lang/StringBuilder;", "Lkotlin/text/StringBuilder;", "toPathString$okhttp", "toQueryNamesAndValues", "", "toQueryNamesAndValues$okhttp", "toQueryString", "toQueryString$okhttp", "writeCanonicalized", "Lokio/Buffer;", "input", "writePercentDecoded", "encoded", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: HttpUrl.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @JvmStatic
        public final int defaultPort(String scheme) {
            Intrinsics.checkNotNullParameter(scheme, "scheme");
            if (Intrinsics.areEqual((Object) scheme, (Object) "http")) {
                return 80;
            }
            if (Intrinsics.areEqual((Object) scheme, (Object) "https")) {
                return 443;
            }
            return -1;
        }

        public final void toPathString$okhttp(List<String> $this$toPathString, StringBuilder out) {
            Intrinsics.checkNotNullParameter($this$toPathString, "<this>");
            Intrinsics.checkNotNullParameter(out, "out");
            int size = $this$toPathString.size();
            int i = 0;
            while (i < size) {
                int i2 = i;
                i++;
                out.append('/');
                out.append($this$toPathString.get(i2));
            }
        }

        public final void toQueryString$okhttp(List<String> $this$toQueryString, StringBuilder out) {
            int i;
            Intrinsics.checkNotNullParameter($this$toQueryString, "<this>");
            Intrinsics.checkNotNullParameter(out, "out");
            IntProgression step = RangesKt.step((IntProgression) RangesKt.until(0, $this$toQueryString.size()), 2);
            int first = step.getFirst();
            int last = step.getLast();
            int step2 = step.getStep();
            if ((step2 > 0 && first <= last) || (step2 < 0 && last <= first)) {
                do {
                    i = first;
                    first += step2;
                    String name = $this$toQueryString.get(i);
                    String value = $this$toQueryString.get(i + 1);
                    if (i > 0) {
                        out.append(Typography.amp);
                    }
                    out.append(name);
                    if (value != null) {
                        out.append('=');
                        out.append(value);
                        continue;
                    }
                } while (i != last);
            }
        }

        public final List<String> toQueryNamesAndValues$okhttp(String $this$toQueryNamesAndValues) {
            Intrinsics.checkNotNullParameter($this$toQueryNamesAndValues, "<this>");
            List result = new ArrayList();
            int pos = 0;
            while (pos <= $this$toQueryNamesAndValues.length()) {
                int ampersandOffset = StringsKt.indexOf$default((CharSequence) $this$toQueryNamesAndValues, (char) Typography.amp, pos, false, 4, (Object) null);
                if (ampersandOffset == -1) {
                    ampersandOffset = $this$toQueryNamesAndValues.length();
                }
                int ampersandOffset2 = ampersandOffset;
                int equalsOffset = StringsKt.indexOf$default((CharSequence) $this$toQueryNamesAndValues, '=', pos, false, 4, (Object) null);
                if (equalsOffset == -1 || equalsOffset > ampersandOffset2) {
                    String substring = $this$toQueryNamesAndValues.substring(pos, ampersandOffset2);
                    Intrinsics.checkNotNullExpressionValue(substring, "this as java.lang.String…ing(startIndex, endIndex)");
                    result.add(substring);
                    result.add((Object) null);
                } else {
                    String substring2 = $this$toQueryNamesAndValues.substring(pos, equalsOffset);
                    Intrinsics.checkNotNullExpressionValue(substring2, "this as java.lang.String…ing(startIndex, endIndex)");
                    result.add(substring2);
                    String substring3 = $this$toQueryNamesAndValues.substring(equalsOffset + 1, ampersandOffset2);
                    Intrinsics.checkNotNullExpressionValue(substring3, "this as java.lang.String…ing(startIndex, endIndex)");
                    result.add(substring3);
                }
                pos = ampersandOffset2 + 1;
            }
            return result;
        }

        @JvmStatic
        public final HttpUrl get(String $this$toHttpUrl) {
            Intrinsics.checkNotNullParameter($this$toHttpUrl, "<this>");
            return new Builder().parse$okhttp((HttpUrl) null, $this$toHttpUrl).build();
        }

        @JvmStatic
        public final HttpUrl parse(String $this$toHttpUrlOrNull) {
            Intrinsics.checkNotNullParameter($this$toHttpUrlOrNull, "<this>");
            try {
                return get($this$toHttpUrlOrNull);
            } catch (IllegalArgumentException e) {
                HttpUrl httpUrl = null;
                return null;
            }
        }

        @JvmStatic
        public final HttpUrl get(URL $this$toHttpUrlOrNull) {
            Intrinsics.checkNotNullParameter($this$toHttpUrlOrNull, "<this>");
            String url = $this$toHttpUrlOrNull.toString();
            Intrinsics.checkNotNullExpressionValue(url, "toString()");
            return parse(url);
        }

        @JvmStatic
        public final HttpUrl get(URI $this$toHttpUrlOrNull) {
            Intrinsics.checkNotNullParameter($this$toHttpUrlOrNull, "<this>");
            String uri = $this$toHttpUrlOrNull.toString();
            Intrinsics.checkNotNullExpressionValue(uri, "toString()");
            return parse(uri);
        }

        @Deprecated(level = DeprecationLevel.ERROR, message = "moved to extension function", replaceWith = @ReplaceWith(expression = "url.toHttpUrl()", imports = {"okhttp3.HttpUrl.Companion.toHttpUrl"}))
        /* renamed from: -deprecated_get  reason: not valid java name */
        public final HttpUrl m1869deprecated_get(String url) {
            Intrinsics.checkNotNullParameter(url, "url");
            return get(url);
        }

        @Deprecated(level = DeprecationLevel.ERROR, message = "moved to extension function", replaceWith = @ReplaceWith(expression = "url.toHttpUrlOrNull()", imports = {"okhttp3.HttpUrl.Companion.toHttpUrlOrNull"}))
        /* renamed from: -deprecated_parse  reason: not valid java name */
        public final HttpUrl m1872deprecated_parse(String url) {
            Intrinsics.checkNotNullParameter(url, "url");
            return parse(url);
        }

        @Deprecated(level = DeprecationLevel.ERROR, message = "moved to extension function", replaceWith = @ReplaceWith(expression = "url.toHttpUrlOrNull()", imports = {"okhttp3.HttpUrl.Companion.toHttpUrlOrNull"}))
        /* renamed from: -deprecated_get  reason: not valid java name */
        public final HttpUrl m1871deprecated_get(URL url) {
            Intrinsics.checkNotNullParameter(url, "url");
            return get(url);
        }

        @Deprecated(level = DeprecationLevel.ERROR, message = "moved to extension function", replaceWith = @ReplaceWith(expression = "uri.toHttpUrlOrNull()", imports = {"okhttp3.HttpUrl.Companion.toHttpUrlOrNull"}))
        /* renamed from: -deprecated_get  reason: not valid java name */
        public final HttpUrl m1870deprecated_get(URI uri) {
            Intrinsics.checkNotNullParameter(uri, "uri");
            return get(uri);
        }

        public static /* synthetic */ String percentDecode$okhttp$default(Companion companion, String str, int i, int i2, boolean z, int i3, Object obj) {
            if ((i3 & 1) != 0) {
                i = 0;
            }
            if ((i3 & 2) != 0) {
                i2 = str.length();
            }
            if ((i3 & 4) != 0) {
                z = false;
            }
            return companion.percentDecode$okhttp(str, i, i2, z);
        }

        public final String percentDecode$okhttp(String $this$percentDecode, int pos, int limit, boolean plusIsSpace) {
            Intrinsics.checkNotNullParameter($this$percentDecode, "<this>");
            int i = pos;
            while (i < limit) {
                int i2 = i;
                i++;
                char c = $this$percentDecode.charAt(i2);
                if (c == '%' || (c == '+' && plusIsSpace)) {
                    Buffer out = new Buffer();
                    out.writeUtf8($this$percentDecode, pos, i2);
                    writePercentDecoded(out, $this$percentDecode, i2, limit, plusIsSpace);
                    return out.readUtf8();
                }
            }
            String substring = $this$percentDecode.substring(pos, limit);
            Intrinsics.checkNotNullExpressionValue(substring, "this as java.lang.String…ing(startIndex, endIndex)");
            return substring;
        }

        private final void writePercentDecoded(Buffer $this$writePercentDecoded, String encoded, int pos, int limit, boolean plusIsSpace) {
            int i = pos;
            while (i < limit) {
                int codePoint = encoded.codePointAt(i);
                if (codePoint == 37 && i + 2 < limit) {
                    int d1 = Util.parseHexDigit(encoded.charAt(i + 1));
                    int d2 = Util.parseHexDigit(encoded.charAt(i + 2));
                    if (!(d1 == -1 || d2 == -1)) {
                        $this$writePercentDecoded.writeByte((d1 << 4) + d2);
                        i = i + 2 + Character.charCount(codePoint);
                    }
                } else if (codePoint == 43 && plusIsSpace) {
                    $this$writePercentDecoded.writeByte(32);
                    i++;
                }
                $this$writePercentDecoded.writeUtf8CodePoint(codePoint);
                i += Character.charCount(codePoint);
            }
        }

        private final boolean isPercentEncoded(String $this$isPercentEncoded, int pos, int limit) {
            return pos + 2 < limit && $this$isPercentEncoded.charAt(pos) == '%' && Util.parseHexDigit($this$isPercentEncoded.charAt(pos + 1)) != -1 && Util.parseHexDigit($this$isPercentEncoded.charAt(pos + 2)) != -1;
        }

        public static /* synthetic */ String canonicalize$okhttp$default(Companion companion, String str, int i, int i2, String str2, boolean z, boolean z2, boolean z3, boolean z4, Charset charset, int i3, Object obj) {
            int i4;
            int i5;
            boolean z5;
            boolean z6;
            boolean z7;
            boolean z8;
            int i6 = i3;
            if ((i6 & 1) != 0) {
                i4 = 0;
            } else {
                i4 = i;
            }
            if ((i6 & 2) != 0) {
                i5 = str.length();
            } else {
                i5 = i2;
            }
            if ((i6 & 8) != 0) {
                z5 = false;
            } else {
                z5 = z;
            }
            if ((i6 & 16) != 0) {
                z6 = false;
            } else {
                z6 = z2;
            }
            if ((i6 & 32) != 0) {
                z7 = false;
            } else {
                z7 = z3;
            }
            if ((i6 & 64) != 0) {
                z8 = false;
            } else {
                z8 = z4;
            }
            return companion.canonicalize$okhttp(str, i4, i5, str2, z5, z6, z7, z8, (i6 & 128) != 0 ? null : charset);
        }

        public final String canonicalize$okhttp(String $this$canonicalize, int pos, int limit, String encodeSet, boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean unicodeAllowed, Charset charset) {
            String str = $this$canonicalize;
            int i = limit;
            String str2 = encodeSet;
            Intrinsics.checkNotNullParameter(str, "<this>");
            Intrinsics.checkNotNullParameter(str2, "encodeSet");
            int i2 = pos;
            while (i2 < i) {
                int codePoint = str.codePointAt(i2);
                if (codePoint >= 32) {
                    if (codePoint != 127) {
                        if (codePoint < 128 || unicodeAllowed) {
                            if (!StringsKt.contains$default((CharSequence) str2, (char) codePoint, false, 2, (Object) null)) {
                                if (codePoint == 37) {
                                    if (alreadyEncoded) {
                                        if (strict) {
                                            if (!isPercentEncoded(str, i2, i)) {
                                            }
                                        }
                                    }
                                }
                                if (codePoint != 43 || !plusIsSpace) {
                                    i2 += Character.charCount(codePoint);
                                    int i3 = codePoint;
                                }
                            }
                        }
                    }
                }
                Buffer out = new Buffer();
                out.writeUtf8(str, pos, i2);
                Buffer out2 = out;
                writeCanonicalized(out, $this$canonicalize, i2, limit, encodeSet, alreadyEncoded, strict, plusIsSpace, unicodeAllowed, charset);
                return out2.readUtf8();
            }
            String substring = $this$canonicalize.substring(pos, limit);
            Intrinsics.checkNotNullExpressionValue(substring, "this as java.lang.String…ing(startIndex, endIndex)");
            return substring;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:35:0x0067, code lost:
            if (isPercentEncoded(r15, r6, r2) == false) goto L_0x0078;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private final void writeCanonicalized(okio.Buffer r14, java.lang.String r15, int r16, int r17, java.lang.String r18, boolean r19, boolean r20, boolean r21, boolean r22, java.nio.charset.Charset r23) {
            /*
                r13 = this;
                r0 = r14
                r1 = r15
                r2 = r17
                r3 = r23
                r4 = 0
                r5 = 0
                r6 = r16
            L_0x000a:
                if (r6 >= r2) goto L_0x00c6
                int r5 = r15.codePointAt(r6)
                if (r19 == 0) goto L_0x0028
                r7 = 9
                if (r5 == r7) goto L_0x0025
                r7 = 10
                if (r5 == r7) goto L_0x0025
                r7 = 12
                if (r5 == r7) goto L_0x0022
                r7 = 13
                if (r5 != r7) goto L_0x0028
            L_0x0022:
                r7 = r13
                goto L_0x00bf
            L_0x0025:
                r7 = r13
                goto L_0x00bf
            L_0x0028:
                r7 = 43
                if (r5 != r7) goto L_0x003b
                if (r21 == 0) goto L_0x003b
                if (r19 == 0) goto L_0x0033
                java.lang.String r7 = "+"
                goto L_0x0035
            L_0x0033:
                java.lang.String r7 = "%2B"
            L_0x0035:
                r14.writeUtf8((java.lang.String) r7)
                r7 = r13
                goto L_0x00bf
            L_0x003b:
                r7 = 32
                r8 = 37
                if (r5 < r7) goto L_0x0077
                r7 = 127(0x7f, float:1.78E-43)
                if (r5 == r7) goto L_0x0075
                r7 = 128(0x80, float:1.794E-43)
                if (r5 < r7) goto L_0x004e
                if (r22 == 0) goto L_0x004c
                goto L_0x004e
            L_0x004c:
                r7 = r13
                goto L_0x0078
            L_0x004e:
                r7 = r18
                java.lang.CharSequence r7 = (java.lang.CharSequence) r7
                char r9 = (char) r5
                r10 = 0
                r11 = 2
                r12 = 0
                boolean r7 = kotlin.text.StringsKt.contains$default((java.lang.CharSequence) r7, (char) r9, (boolean) r10, (int) r11, (java.lang.Object) r12)
                if (r7 != 0) goto L_0x0073
                if (r5 != r8) goto L_0x006e
                if (r19 == 0) goto L_0x006c
                if (r20 == 0) goto L_0x006a
                r7 = r13
                boolean r9 = r13.isPercentEncoded(r15, r6, r2)
                if (r9 != 0) goto L_0x006f
                goto L_0x0078
            L_0x006a:
                r7 = r13
                goto L_0x006f
            L_0x006c:
                r7 = r13
                goto L_0x0078
            L_0x006e:
                r7 = r13
            L_0x006f:
                r14.writeUtf8CodePoint((int) r5)
                goto L_0x00bf
            L_0x0073:
                r7 = r13
                goto L_0x0078
            L_0x0075:
                r7 = r13
                goto L_0x0078
            L_0x0077:
                r7 = r13
            L_0x0078:
                if (r4 != 0) goto L_0x0080
                okio.Buffer r9 = new okio.Buffer
                r9.<init>()
                r4 = r9
            L_0x0080:
                if (r3 == 0) goto L_0x0094
                java.nio.charset.Charset r9 = java.nio.charset.StandardCharsets.UTF_8
                boolean r9 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r3, (java.lang.Object) r9)
                if (r9 == 0) goto L_0x008b
                goto L_0x0094
            L_0x008b:
                int r9 = java.lang.Character.charCount(r5)
                int r9 = r9 + r6
                r4.writeString((java.lang.String) r15, (int) r6, (int) r9, (java.nio.charset.Charset) r3)
                goto L_0x0097
            L_0x0094:
                r4.writeUtf8CodePoint((int) r5)
            L_0x0097:
                boolean r9 = r4.exhausted()
                if (r9 != 0) goto L_0x00bf
                byte r9 = r4.readByte()
                r9 = r9 & 255(0xff, float:3.57E-43)
                r14.writeByte((int) r8)
                char[] r10 = okhttp3.HttpUrl.HEX_DIGITS
                int r11 = r9 >> 4
                r11 = r11 & 15
                char r10 = r10[r11]
                r14.writeByte((int) r10)
                char[] r10 = okhttp3.HttpUrl.HEX_DIGITS
                r11 = r9 & 15
                char r10 = r10[r11]
                r14.writeByte((int) r10)
                goto L_0x0097
            L_0x00bf:
                int r8 = java.lang.Character.charCount(r5)
                int r6 = r6 + r8
                goto L_0x000a
            L_0x00c6:
                r7 = r13
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.HttpUrl.Companion.writeCanonicalized(okio.Buffer, java.lang.String, int, int, java.lang.String, boolean, boolean, boolean, boolean, java.nio.charset.Charset):void");
        }
    }
}

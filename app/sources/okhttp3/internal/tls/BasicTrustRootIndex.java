package okhttp3.internal.tls;

import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0010\"\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\u0018\u00002\u00020\u0001B\u0019\u0012\u0012\u0010\u0002\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00040\u0003\"\u00020\u0004¢\u0006\u0002\u0010\u0005J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u0002J\u0012\u0010\u000e\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u000f\u001a\u00020\u0004H\u0016J\b\u0010\u0010\u001a\u00020\u0011H\u0016R \u0010\u0006\u001a\u0014\u0012\u0004\u0012\u00020\b\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\t0\u0007X\u0004¢\u0006\u0002\n\u0000¨\u0006\u0012"}, d2 = {"Lokhttp3/internal/tls/BasicTrustRootIndex;", "Lokhttp3/internal/tls/TrustRootIndex;", "caCerts", "", "Ljava/security/cert/X509Certificate;", "([Ljava/security/cert/X509Certificate;)V", "subjectToCaCerts", "", "Ljavax/security/auth/x500/X500Principal;", "", "equals", "", "other", "", "findByIssuerAndSignature", "cert", "hashCode", "", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: BasicTrustRootIndex.kt */
public final class BasicTrustRootIndex implements TrustRootIndex {
    private final Map<X500Principal, Set<X509Certificate>> subjectToCaCerts;

    public BasicTrustRootIndex(X509Certificate... caCerts) {
        Object answer$iv;
        Intrinsics.checkNotNullParameter(caCerts, "caCerts");
        Map linkedHashMap = new LinkedHashMap();
        int length = caCerts.length;
        int i = 0;
        while (i < length) {
            X509Certificate caCert = caCerts[i];
            i++;
            Object key$iv = caCert.getSubjectX500Principal();
            Intrinsics.checkNotNullExpressionValue(key$iv, "caCert.subjectX500Principal");
            Map $this$getOrPut$iv = linkedHashMap;
            Object value$iv = $this$getOrPut$iv.get(key$iv);
            if (value$iv == null) {
                answer$iv = (Set) new LinkedHashSet();
                $this$getOrPut$iv.put(key$iv, answer$iv);
            } else {
                answer$iv = value$iv;
            }
            ((Set) answer$iv).add(caCert);
        }
        this.subjectToCaCerts = linkedHashMap;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v0, resolved type: java.security.cert.X509Certificate} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: java.security.cert.X509Certificate} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v0, resolved type: java.security.cert.X509Certificate} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v3, resolved type: java.security.cert.X509Certificate} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.security.cert.X509Certificate findByIssuerAndSignature(java.security.cert.X509Certificate r12) {
        /*
            r11 = this;
            java.lang.String r0 = "cert"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r12, r0)
            javax.security.auth.x500.X500Principal r0 = r12.getIssuerX500Principal()
            java.util.Map<javax.security.auth.x500.X500Principal, java.util.Set<java.security.cert.X509Certificate>> r1 = r11.subjectToCaCerts
            java.lang.Object r1 = r1.get(r0)
            java.util.Set r1 = (java.util.Set) r1
            r2 = 0
            if (r1 != 0) goto L_0x0015
            return r2
        L_0x0015:
            r3 = r1
            java.lang.Iterable r3 = (java.lang.Iterable) r3
            r4 = 0
            java.util.Iterator r5 = r3.iterator()
        L_0x001d:
            boolean r6 = r5.hasNext()
            if (r6 == 0) goto L_0x003c
            java.lang.Object r6 = r5.next()
            r7 = r6
            java.security.cert.X509Certificate r7 = (java.security.cert.X509Certificate) r7
            r8 = 0
            java.security.PublicKey r9 = r7.getPublicKey()     // Catch:{ Exception -> 0x0035 }
            r12.verify(r9)     // Catch:{ Exception -> 0x0035 }
            r9 = 1
            goto L_0x0038
        L_0x0035:
            r9 = move-exception
            r10 = 0
            r9 = r10
        L_0x0038:
            if (r9 == 0) goto L_0x001d
            r2 = r6
            goto L_0x003d
        L_0x003c:
        L_0x003d:
            java.security.cert.X509Certificate r2 = (java.security.cert.X509Certificate) r2
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.tls.BasicTrustRootIndex.findByIssuerAndSignature(java.security.cert.X509Certificate):java.security.cert.X509Certificate");
    }

    public boolean equals(Object other) {
        return other == this || ((other instanceof BasicTrustRootIndex) && Intrinsics.areEqual((Object) ((BasicTrustRootIndex) other).subjectToCaCerts, (Object) this.subjectToCaCerts));
    }

    public int hashCode() {
        return this.subjectToCaCerts.hashCode();
    }
}

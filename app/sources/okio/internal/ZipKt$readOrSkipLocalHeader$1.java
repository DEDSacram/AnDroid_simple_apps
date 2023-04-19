package okio.internal;

import java.io.IOException;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Lambda;
import kotlin.jvm.internal.Ref;
import okio.BufferedSource;

@Metadata(d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\n"}, d2 = {"<anonymous>", "", "headerId", "", "dataSize", ""}, k = 3, mv = {1, 5, 1}, xi = 48)
/* compiled from: zip.kt */
final class ZipKt$readOrSkipLocalHeader$1 extends Lambda implements Function2<Integer, Long, Unit> {
    final /* synthetic */ Ref.ObjectRef<Long> $createdAtMillis;
    final /* synthetic */ Ref.ObjectRef<Long> $lastAccessedAtMillis;
    final /* synthetic */ Ref.ObjectRef<Long> $lastModifiedAtMillis;
    final /* synthetic */ BufferedSource $this_readOrSkipLocalHeader;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    ZipKt$readOrSkipLocalHeader$1(BufferedSource bufferedSource, Ref.ObjectRef<Long> objectRef, Ref.ObjectRef<Long> objectRef2, Ref.ObjectRef<Long> objectRef3) {
        super(2);
        this.$this_readOrSkipLocalHeader = bufferedSource;
        this.$lastModifiedAtMillis = objectRef;
        this.$lastAccessedAtMillis = objectRef2;
        this.$createdAtMillis = objectRef3;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
        invoke(((Number) p1).intValue(), ((Number) p2).longValue());
        return Unit.INSTANCE;
    }

    public final void invoke(int headerId, long dataSize) {
        if (headerId != 21589) {
            return;
        }
        if (dataSize >= 1) {
            int flags = this.$this_readOrSkipLocalHeader.readByte() & 255;
            boolean hasCreatedAtMillis = false;
            boolean hasLastModifiedAtMillis = (flags & 1) == 1;
            boolean hasLastAccessedAtMillis = (flags & 2) == 2;
            if ((flags & 4) == 4) {
                hasCreatedAtMillis = true;
            }
            BufferedSource bufferedSource = this.$this_readOrSkipLocalHeader;
            long result = 1;
            if (hasLastModifiedAtMillis) {
                result = 1 + 4;
            }
            if (hasLastAccessedAtMillis) {
                result += 4;
            }
            if (hasCreatedAtMillis) {
                result += 4;
            }
            if (dataSize >= result) {
                if (hasLastModifiedAtMillis) {
                    this.$lastModifiedAtMillis.element = Long.valueOf(((long) this.$this_readOrSkipLocalHeader.readIntLe()) * 1000);
                }
                if (hasLastAccessedAtMillis) {
                    this.$lastAccessedAtMillis.element = Long.valueOf(((long) this.$this_readOrSkipLocalHeader.readIntLe()) * 1000);
                }
                if (hasCreatedAtMillis) {
                    this.$createdAtMillis.element = Long.valueOf(((long) this.$this_readOrSkipLocalHeader.readIntLe()) * 1000);
                    return;
                }
                return;
            }
            throw new IOException("bad zip: extended timestamp extra too short");
        }
        throw new IOException("bad zip: extended timestamp extra too short");
    }
}

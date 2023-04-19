package okhttp3.internal.cache;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import okhttp3.internal.concurrent.Task;

@Metadata(d1 = {"\u0000\u0011\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H\u0016¨\u0006\u0004"}, d2 = {"okhttp3/internal/cache/DiskLruCache$cleanupTask$1", "Lokhttp3/internal/concurrent/Task;", "runOnce", "", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: DiskLruCache.kt */
public final class DiskLruCache$cleanupTask$1 extends Task {
    final /* synthetic */ DiskLruCache this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    DiskLruCache$cleanupTask$1(DiskLruCache $receiver, String $super_call_param$1) {
        super($super_call_param$1, false, 2, (DefaultConstructorMarker) null);
        this.this$0 = $receiver;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0040, code lost:
        return -1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long runOnce() {
        /*
            r6 = this;
            okhttp3.internal.cache.DiskLruCache r0 = r6.this$0
            monitor-enter(r0)
            r1 = 0
            boolean r2 = r0.initialized     // Catch:{ all -> 0x0041 }
            r3 = -1
            if (r2 == 0) goto L_0x003e
            boolean r2 = r0.getClosed$okhttp()     // Catch:{ all -> 0x0041 }
            if (r2 == 0) goto L_0x0013
            goto L_0x003e
        L_0x0013:
            r2 = 1
            r0.trimToSize()     // Catch:{ IOException -> 0x0019 }
            goto L_0x001d
        L_0x0019:
            r5 = move-exception
            r0.mostRecentTrimFailed = r2     // Catch:{ all -> 0x0041 }
        L_0x001d:
            boolean r5 = r0.journalRebuildRequired()     // Catch:{ IOException -> 0x002c }
            if (r5 == 0) goto L_0x003b
            r0.rebuildJournal$okhttp()     // Catch:{ IOException -> 0x002c }
            r5 = 0
            r0.redundantOpCount = r5     // Catch:{ IOException -> 0x002c }
            goto L_0x003b
        L_0x002c:
            r5 = move-exception
            r0.mostRecentRebuildFailed = r2     // Catch:{ all -> 0x0041 }
            okio.Sink r2 = okio.Okio.blackhole()     // Catch:{ all -> 0x0041 }
            okio.BufferedSink r2 = okio.Okio.buffer((okio.Sink) r2)     // Catch:{ all -> 0x0041 }
            r0.journalWriter = r2     // Catch:{ all -> 0x0041 }
        L_0x003b:
            monitor-exit(r0)
            return r3
        L_0x003e:
            monitor-exit(r0)
            return r3
        L_0x0041:
            r1 = move-exception
            monitor-exit(r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache$cleanupTask$1.runOnce():long");
    }
}

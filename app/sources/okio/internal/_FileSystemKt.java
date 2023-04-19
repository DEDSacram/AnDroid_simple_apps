package okio.internal;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.collections.ArrayDeque;
import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import okio.BufferedSink;
import okio.FileMetadata;
import okio.FileSystem;
import okio.Okio;
import okio.Path;
import okio.Source;

@Metadata(d1 = {"\u00004\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\r\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001aI\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u00022\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u00072\u0006\u0010\b\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\nH@ø\u0001\u0000¢\u0006\u0002\u0010\f\u001a\u001c\u0010\r\u001a\u00020\u0001*\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u0003H\u0000\u001a\u001c\u0010\u0010\u001a\u00020\u0001*\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u00032\u0006\u0010\u0012\u001a\u00020\nH\u0000\u001a\u001c\u0010\u0013\u001a\u00020\u0001*\u00020\u00052\u0006\u0010\u0014\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\nH\u0000\u001a\u0014\u0010\u0016\u001a\u00020\n*\u00020\u00052\u0006\u0010\b\u001a\u00020\u0003H\u0000\u001a\"\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00030\u0018*\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\nH\u0000\u001a\u0014\u0010\u0019\u001a\u00020\u001a*\u00020\u00052\u0006\u0010\b\u001a\u00020\u0003H\u0000\u001a\u0016\u0010\u001b\u001a\u0004\u0018\u00010\u0003*\u00020\u00052\u0006\u0010\b\u001a\u00020\u0003H\u0000\u0002\u0004\n\u0002\b\u0019¨\u0006\u001c"}, d2 = {"collectRecursively", "", "Lkotlin/sequences/SequenceScope;", "Lokio/Path;", "fileSystem", "Lokio/FileSystem;", "stack", "Lkotlin/collections/ArrayDeque;", "path", "followSymlinks", "", "postorder", "(Lkotlin/sequences/SequenceScope;Lokio/FileSystem;Lkotlin/collections/ArrayDeque;Lokio/Path;ZZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "commonCopy", "source", "target", "commonCreateDirectories", "dir", "mustCreate", "commonDeleteRecursively", "fileOrDirectory", "mustExist", "commonExists", "commonListRecursively", "Lkotlin/sequences/Sequence;", "commonMetadata", "Lokio/FileMetadata;", "symlinkTarget", "okio"}, k = 2, mv = {1, 5, 1}, xi = 48)
/* compiled from: -FileSystem.kt */
public final class _FileSystemKt {
    public static final FileMetadata commonMetadata(FileSystem $this$commonMetadata, Path path) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonMetadata, "<this>");
        Intrinsics.checkNotNullParameter(path, "path");
        FileMetadata metadataOrNull = $this$commonMetadata.metadataOrNull(path);
        if (metadataOrNull != null) {
            return metadataOrNull;
        }
        throw new FileNotFoundException(Intrinsics.stringPlus("no such file: ", path));
    }

    public static final boolean commonExists(FileSystem $this$commonExists, Path path) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonExists, "<this>");
        Intrinsics.checkNotNullParameter(path, "path");
        return $this$commonExists.metadataOrNull(path) != null;
    }

    public static final void commonCreateDirectories(FileSystem $this$commonCreateDirectories, Path dir, boolean mustCreate) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonCreateDirectories, "<this>");
        Intrinsics.checkNotNullParameter(dir, "dir");
        ArrayDeque directories = new ArrayDeque();
        Path path = dir;
        while (path != null && !$this$commonCreateDirectories.exists(path)) {
            directories.addFirst(path);
            path = path.parent();
        }
        if (!mustCreate || !directories.isEmpty()) {
            Iterator it = directories.iterator();
            while (it.hasNext()) {
                $this$commonCreateDirectories.createDirectory((Path) it.next());
            }
            return;
        }
        throw new IOException(dir + " already exist.");
    }

    public static final void commonCopy(FileSystem $this$commonCopy, Path source, Path target) throws IOException {
        Object result$iv;
        Throwable thrown$iv;
        FileSystem fileSystem = $this$commonCopy;
        Path path = target;
        Intrinsics.checkNotNullParameter(fileSystem, "<this>");
        Intrinsics.checkNotNullParameter(source, "source");
        Intrinsics.checkNotNullParameter(path, TypedValues.AttributesType.S_TARGET);
        Closeable $this$use$iv = $this$commonCopy.source(source);
        Object result$iv2 = null;
        Throwable thrown$iv2 = null;
        try {
            Source bytesIn = (Source) $this$use$iv;
            Closeable $this$use$iv2 = Okio.buffer(fileSystem.sink(path));
            result$iv = null;
            thrown$iv = null;
            try {
                result$iv = Long.valueOf(((BufferedSink) $this$use$iv2).writeAll(bytesIn));
            } catch (Throwable t$iv) {
                thrown$iv = t$iv;
            }
            if ($this$use$iv2 != null) {
                $this$use$iv2.close();
            }
        } catch (Throwable t$iv2) {
            thrown$iv2 = t$iv2;
        }
        if (thrown$iv == null) {
            Intrinsics.checkNotNull(result$iv);
            result$iv2 = Long.valueOf(((Number) result$iv).longValue());
            if ($this$use$iv != null) {
                try {
                    $this$use$iv.close();
                } catch (Throwable th) {
                    Throwable t$iv3 = th;
                    if (thrown$iv2 == null) {
                        thrown$iv2 = t$iv3;
                    } else {
                        ExceptionsKt.addSuppressed(thrown$iv2, t$iv3);
                    }
                }
            }
            if (thrown$iv2 == null) {
                Intrinsics.checkNotNull(result$iv2);
                return;
            }
            throw thrown$iv2;
        }
        throw thrown$iv;
    }

    public static final void commonDeleteRecursively(FileSystem $this$commonDeleteRecursively, Path fileOrDirectory, boolean mustExist) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonDeleteRecursively, "<this>");
        Intrinsics.checkNotNullParameter(fileOrDirectory, "fileOrDirectory");
        Iterator iterator = SequencesKt.sequence(new _FileSystemKt$commonDeleteRecursively$sequence$1($this$commonDeleteRecursively, fileOrDirectory, (Continuation<? super _FileSystemKt$commonDeleteRecursively$sequence$1>) null)).iterator();
        while (iterator.hasNext()) {
            $this$commonDeleteRecursively.delete((Path) iterator.next(), mustExist && !iterator.hasNext());
        }
    }

    public static final Sequence<Path> commonListRecursively(FileSystem $this$commonListRecursively, Path dir, boolean followSymlinks) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonListRecursively, "<this>");
        Intrinsics.checkNotNullParameter(dir, "dir");
        return SequencesKt.sequence(new _FileSystemKt$commonListRecursively$1(dir, $this$commonListRecursively, followSymlinks, (Continuation<? super _FileSystemKt$commonListRecursively$1>) null));
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0092, code lost:
        r10 = r9;
        r14 = r7;
        r7 = r6;
        r6 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x009b, code lost:
        r9 = r8.listOrNull(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x009f, code lost:
        if (r9 != null) goto L_0x00a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00a1, code lost:
        r9 = kotlin.collections.CollectionsKt.emptyList();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00ad, code lost:
        if ((!r9.isEmpty()) == false) goto L_0x0127;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00af, code lost:
        r11 = r7;
        r12 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00b2, code lost:
        if (r5 == false) goto L_0x00c7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00b8, code lost:
        if (r6.contains(r11) != false) goto L_0x00bb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00c6, code lost:
        throw new java.io.IOException(kotlin.jvm.internal.Intrinsics.stringPlus("symlink cycle at ", r7));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00c7, code lost:
        r13 = symlinkTarget(r8, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00cb, code lost:
        if (r13 != null) goto L_0x0122;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00cd, code lost:
        if (r5 != false) goto L_0x00d1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00cf, code lost:
        if (r12 != 0) goto L_0x0127;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00d1, code lost:
        r6.addLast(r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:?, code lost:
        r11 = r9.iterator();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00d9, code lost:
        r9 = r8;
        r8 = r6;
        r6 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00e0, code lost:
        if (r6.hasNext() == false) goto L_0x0118;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00e2, code lost:
        r18 = r6.next();
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00eb, code lost:
        if (r5 == false) goto L_0x00ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00ed, code lost:
        r12 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00ef, code lost:
        r12 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00f0, code lost:
        if (r3 == false) goto L_0x00f3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00f2, code lost:
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00f3, code lost:
        r1.L$0 = r10;
        r1.L$1 = r9;
        r1.L$2 = r8;
        r1.L$3 = r7;
        r1.L$4 = r6;
        r1.Z$0 = r5;
        r1.Z$1 = r3;
        r1.label = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x0113, code lost:
        if (collectRecursively(r10, r9, r8, r18, r12, r11, r1) != r0) goto L_0x00dc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0118, code lost:
        r8.removeLast();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x011c, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x011d, code lost:
        r8 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x011e, code lost:
        r8.removeLast();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x0121, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x0122, code lost:
        r11 = r13;
        r12 = r12 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x0127, code lost:
        if (r3 == false) goto L_0x013e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x0129, code lost:
        r1.L$0 = null;
        r1.L$1 = null;
        r1.L$2 = null;
        r1.L$3 = null;
        r1.L$4 = null;
        r1.label = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x013b, code lost:
        if (r10.yield(r7, r1) != r0) goto L_0x013e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x013d, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x0140, code lost:
        return kotlin.Unit.INSTANCE;
     */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x0030  */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0035  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0055  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x006d  */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0028  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final java.lang.Object collectRecursively(kotlin.sequences.SequenceScope<? super okio.Path> r15, okio.FileSystem r16, kotlin.collections.ArrayDeque<okio.Path> r17, okio.Path r18, boolean r19, boolean r20, kotlin.coroutines.Continuation<? super kotlin.Unit> r21) {
        /*
            r0 = r21
            boolean r1 = r0 instanceof okio.internal._FileSystemKt$collectRecursively$1
            if (r1 == 0) goto L_0x0016
            r1 = r0
            okio.internal._FileSystemKt$collectRecursively$1 r1 = (okio.internal._FileSystemKt$collectRecursively$1) r1
            int r2 = r1.label
            r3 = -2147483648(0xffffffff80000000, float:-0.0)
            r2 = r2 & r3
            if (r2 == 0) goto L_0x0016
            int r0 = r1.label
            int r0 = r0 - r3
            r1.label = r0
            goto L_0x001b
        L_0x0016:
            okio.internal._FileSystemKt$collectRecursively$1 r1 = new okio.internal._FileSystemKt$collectRecursively$1
            r1.<init>(r0)
        L_0x001b:
            r0 = r1
            java.lang.Object r2 = r1.result
            java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r3 = r1.label
            r4 = 1
            switch(r3) {
                case 0: goto L_0x006d;
                case 1: goto L_0x0055;
                case 2: goto L_0x0035;
                case 3: goto L_0x0030;
                default: goto L_0x0028;
            }
        L_0x0028:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "call to 'resume' before 'invoke' with coroutine"
            r0.<init>(r1)
            throw r0
        L_0x0030:
            kotlin.ResultKt.throwOnFailure(r2)
            goto L_0x013e
        L_0x0035:
            boolean r3 = r1.Z$1
            boolean r5 = r1.Z$0
            java.lang.Object r6 = r1.L$4
            java.util.Iterator r6 = (java.util.Iterator) r6
            java.lang.Object r7 = r1.L$3
            okio.Path r7 = (okio.Path) r7
            java.lang.Object r8 = r1.L$2
            kotlin.collections.ArrayDeque r8 = (kotlin.collections.ArrayDeque) r8
            java.lang.Object r9 = r1.L$1
            okio.FileSystem r9 = (okio.FileSystem) r9
            java.lang.Object r10 = r1.L$0
            kotlin.sequences.SequenceScope r10 = (kotlin.sequences.SequenceScope) r10
            kotlin.ResultKt.throwOnFailure(r2)     // Catch:{ all -> 0x0052 }
            goto L_0x0117
        L_0x0052:
            r0 = move-exception
            goto L_0x011e
        L_0x0055:
            boolean r3 = r1.Z$1
            boolean r5 = r1.Z$0
            java.lang.Object r6 = r1.L$3
            okio.Path r6 = (okio.Path) r6
            java.lang.Object r7 = r1.L$2
            kotlin.collections.ArrayDeque r7 = (kotlin.collections.ArrayDeque) r7
            java.lang.Object r8 = r1.L$1
            okio.FileSystem r8 = (okio.FileSystem) r8
            java.lang.Object r9 = r1.L$0
            kotlin.sequences.SequenceScope r9 = (kotlin.sequences.SequenceScope) r9
            kotlin.ResultKt.throwOnFailure(r2)
            goto L_0x0092
        L_0x006d:
            kotlin.ResultKt.throwOnFailure(r2)
            r9 = r15
            r7 = r17
            r5 = r19
            r8 = r16
            r6 = r18
            r3 = r20
            if (r3 != 0) goto L_0x0097
            r1.L$0 = r9
            r1.L$1 = r8
            r1.L$2 = r7
            r1.L$3 = r6
            r1.Z$0 = r5
            r1.Z$1 = r3
            r1.label = r4
            java.lang.Object r10 = r9.yield(r6, r1)
            if (r10 != r0) goto L_0x0092
        L_0x0091:
            return r0
        L_0x0092:
            r10 = r9
            r14 = r7
            r7 = r6
            r6 = r14
            goto L_0x009b
        L_0x0097:
            r10 = r9
            r14 = r7
            r7 = r6
            r6 = r14
        L_0x009b:
            java.util.List r9 = r8.listOrNull(r7)
            if (r9 != 0) goto L_0x00a5
            java.util.List r9 = kotlin.collections.CollectionsKt.emptyList()
        L_0x00a5:
            r11 = r9
            java.util.Collection r11 = (java.util.Collection) r11
            boolean r11 = r11.isEmpty()
            r11 = r11 ^ r4
            if (r11 == 0) goto L_0x0127
            r11 = r7
            r12 = 0
        L_0x00b1:
            if (r5 == 0) goto L_0x00c7
            boolean r13 = r6.contains(r11)
            if (r13 != 0) goto L_0x00bb
            goto L_0x00c7
        L_0x00bb:
            java.io.IOException r0 = new java.io.IOException
            java.lang.String r4 = "symlink cycle at "
            java.lang.String r4 = kotlin.jvm.internal.Intrinsics.stringPlus(r4, r7)
            r0.<init>(r4)
            throw r0
        L_0x00c7:
            okio.Path r13 = symlinkTarget(r8, r11)
            if (r13 != 0) goto L_0x0122
            if (r5 != 0) goto L_0x00d1
            if (r12 != 0) goto L_0x0127
        L_0x00d1:
            r6.addLast(r11)
            java.util.Iterator r11 = r9.iterator()     // Catch:{ all -> 0x011c }
            r9 = r8
            r8 = r6
            r6 = r11
        L_0x00dc:
            boolean r11 = r6.hasNext()     // Catch:{ all -> 0x0052 }
            if (r11 == 0) goto L_0x0118
            java.lang.Object r11 = r6.next()     // Catch:{ all -> 0x0052 }
            okio.Path r11 = (okio.Path) r11     // Catch:{ all -> 0x0052 }
            r18 = r11
            r11 = 0
            if (r5 == 0) goto L_0x00ef
            r12 = r4
            goto L_0x00f0
        L_0x00ef:
            r12 = r11
        L_0x00f0:
            if (r3 == 0) goto L_0x00f3
            r11 = r4
        L_0x00f3:
            r1.L$0 = r10     // Catch:{ all -> 0x0052 }
            r1.L$1 = r9     // Catch:{ all -> 0x0052 }
            r1.L$2 = r8     // Catch:{ all -> 0x0052 }
            r1.L$3 = r7     // Catch:{ all -> 0x0052 }
            r1.L$4 = r6     // Catch:{ all -> 0x0052 }
            r1.Z$0 = r5     // Catch:{ all -> 0x0052 }
            r1.Z$1 = r3     // Catch:{ all -> 0x0052 }
            r13 = 2
            r1.label = r13     // Catch:{ all -> 0x0052 }
            r15 = r10
            r16 = r9
            r17 = r8
            r19 = r12
            r20 = r11
            r21 = r1
            java.lang.Object r11 = collectRecursively(r15, r16, r17, r18, r19, r20, r21)     // Catch:{ all -> 0x0052 }
            if (r11 != r0) goto L_0x0117
            goto L_0x0091
        L_0x0117:
            goto L_0x00dc
        L_0x0118:
            r8.removeLast()
            goto L_0x0127
        L_0x011c:
            r0 = move-exception
            r8 = r6
        L_0x011e:
            r8.removeLast()
            throw r0
        L_0x0122:
            r11 = r13
            int r12 = r12 + 1
            goto L_0x00b1
        L_0x0127:
            if (r3 == 0) goto L_0x0141
            r3 = 0
            r1.L$0 = r3
            r1.L$1 = r3
            r1.L$2 = r3
            r1.L$3 = r3
            r1.L$4 = r3
            r3 = 3
            r1.label = r3
            java.lang.Object r3 = r10.yield(r7, r1)
            if (r3 != r0) goto L_0x013e
            return r0
        L_0x013e:
            kotlin.Unit r0 = kotlin.Unit.INSTANCE
            return r0
        L_0x0141:
            goto L_0x013e
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.internal._FileSystemKt.collectRecursively(kotlin.sequences.SequenceScope, okio.FileSystem, kotlin.collections.ArrayDeque, okio.Path, boolean, boolean, kotlin.coroutines.Continuation):java.lang.Object");
    }

    public static final Path symlinkTarget(FileSystem $this$symlinkTarget, Path path) throws IOException {
        Intrinsics.checkNotNullParameter($this$symlinkTarget, "<this>");
        Intrinsics.checkNotNullParameter(path, "path");
        Path target = $this$symlinkTarget.metadata(path).getSymlinkTarget();
        if (target == null) {
            return null;
        }
        Path parent = path.parent();
        Intrinsics.checkNotNull(parent);
        return parent.resolve(target);
    }
}

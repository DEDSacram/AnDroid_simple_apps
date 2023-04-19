package okhttp3.internal.cache;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Flushable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import kotlin.KotlinNothingValueException;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import kotlin.text.Typography;
import okhttp3.internal.Util;
import okhttp3.internal.concurrent.TaskQueue;
import okhttp3.internal.concurrent.TaskRunner;
import okhttp3.internal.io.FileSystem;
import okhttp3.internal.platform.Platform;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;
import okio.Source;

@Metadata(d1 = {"\u0000y\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010)\n\u0002\b\u0007*\u0001\u0014\u0018\u0000 [2\u00020\u00012\u00020\u0002:\u0004[\\]^B7\b\u0000\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\b\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\r¢\u0006\u0002\u0010\u000eJ\b\u00108\u001a\u000209H\u0002J\b\u0010:\u001a\u000209H\u0016J!\u0010;\u001a\u0002092\n\u0010<\u001a\u00060=R\u00020\u00002\u0006\u0010>\u001a\u00020\u0010H\u0000¢\u0006\u0002\b?J\u0006\u0010@\u001a\u000209J \u0010A\u001a\b\u0018\u00010=R\u00020\u00002\u0006\u0010B\u001a\u00020(2\b\b\u0002\u0010C\u001a\u00020\u000bH\u0007J\u0006\u0010D\u001a\u000209J\b\u0010E\u001a\u000209H\u0016J\u0017\u0010F\u001a\b\u0018\u00010GR\u00020\u00002\u0006\u0010B\u001a\u00020(H\u0002J\u0006\u0010H\u001a\u000209J\u0006\u0010I\u001a\u00020\u0010J\b\u0010J\u001a\u00020\u0010H\u0002J\b\u0010K\u001a\u00020%H\u0002J\b\u0010L\u001a\u000209H\u0002J\b\u0010M\u001a\u000209H\u0002J\u0010\u0010N\u001a\u0002092\u0006\u0010O\u001a\u00020(H\u0002J\r\u0010P\u001a\u000209H\u0000¢\u0006\u0002\bQJ\u000e\u0010R\u001a\u00020\u00102\u0006\u0010B\u001a\u00020(J\u0019\u0010S\u001a\u00020\u00102\n\u0010T\u001a\u00060)R\u00020\u0000H\u0000¢\u0006\u0002\bUJ\b\u0010V\u001a\u00020\u0010H\u0002J\u0006\u00105\u001a\u00020\u000bJ\u0010\u0010W\u001a\f\u0012\b\u0012\u00060GR\u00020\u00000XJ\u0006\u0010Y\u001a\u000209J\u0010\u0010Z\u001a\u0002092\u0006\u0010B\u001a\u00020(H\u0002R\u000e\u0010\u0007\u001a\u00020\bX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u00020\u0014X\u0004¢\u0006\u0004\n\u0002\u0010\u0015R\u001a\u0010\u0016\u001a\u00020\u0010X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001aR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0014\u0010\u0003\u001a\u00020\u0004X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u000e\u0010\u001f\u001a\u00020\u0010X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020\u0010X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\u0006X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020\u0006X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020\u0006X\u0004¢\u0006\u0002\n\u0000R\u0010\u0010$\u001a\u0004\u0018\u00010%X\u000e¢\u0006\u0002\n\u0000R$\u0010&\u001a\u0012\u0012\u0004\u0012\u00020(\u0012\b\u0012\u00060)R\u00020\u00000'X\u0004¢\u0006\b\n\u0000\u001a\u0004\b*\u0010+R&\u0010\n\u001a\u00020\u000b2\u0006\u0010,\u001a\u00020\u000b8F@FX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b-\u0010.\"\u0004\b/\u00100R\u000e\u00101\u001a\u00020\u0010X\u000e¢\u0006\u0002\n\u0000R\u000e\u00102\u001a\u00020\u0010X\u000e¢\u0006\u0002\n\u0000R\u000e\u00103\u001a\u00020\u000bX\u000e¢\u0006\u0002\n\u0000R\u000e\u00104\u001a\u00020\bX\u000e¢\u0006\u0002\n\u0000R\u000e\u00105\u001a\u00020\u000bX\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\u00020\bX\u0004¢\u0006\b\n\u0000\u001a\u0004\b6\u00107¨\u0006_"}, d2 = {"Lokhttp3/internal/cache/DiskLruCache;", "Ljava/io/Closeable;", "Ljava/io/Flushable;", "fileSystem", "Lokhttp3/internal/io/FileSystem;", "directory", "Ljava/io/File;", "appVersion", "", "valueCount", "maxSize", "", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "(Lokhttp3/internal/io/FileSystem;Ljava/io/File;IIJLokhttp3/internal/concurrent/TaskRunner;)V", "civilizedFileSystem", "", "cleanupQueue", "Lokhttp3/internal/concurrent/TaskQueue;", "cleanupTask", "okhttp3/internal/cache/DiskLruCache$cleanupTask$1", "Lokhttp3/internal/cache/DiskLruCache$cleanupTask$1;", "closed", "getClosed$okhttp", "()Z", "setClosed$okhttp", "(Z)V", "getDirectory", "()Ljava/io/File;", "getFileSystem$okhttp", "()Lokhttp3/internal/io/FileSystem;", "hasJournalErrors", "initialized", "journalFile", "journalFileBackup", "journalFileTmp", "journalWriter", "Lokio/BufferedSink;", "lruEntries", "Ljava/util/LinkedHashMap;", "", "Lokhttp3/internal/cache/DiskLruCache$Entry;", "getLruEntries$okhttp", "()Ljava/util/LinkedHashMap;", "value", "getMaxSize", "()J", "setMaxSize", "(J)V", "mostRecentRebuildFailed", "mostRecentTrimFailed", "nextSequenceNumber", "redundantOpCount", "size", "getValueCount$okhttp", "()I", "checkNotClosed", "", "close", "completeEdit", "editor", "Lokhttp3/internal/cache/DiskLruCache$Editor;", "success", "completeEdit$okhttp", "delete", "edit", "key", "expectedSequenceNumber", "evictAll", "flush", "get", "Lokhttp3/internal/cache/DiskLruCache$Snapshot;", "initialize", "isClosed", "journalRebuildRequired", "newJournalWriter", "processJournal", "readJournal", "readJournalLine", "line", "rebuildJournal", "rebuildJournal$okhttp", "remove", "removeEntry", "entry", "removeEntry$okhttp", "removeOldestEntry", "snapshots", "", "trimToSize", "validateKey", "Companion", "Editor", "Entry", "Snapshot", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: DiskLruCache.kt */
public final class DiskLruCache implements Closeable, Flushable {
    public static final long ANY_SEQUENCE_NUMBER = -1;
    public static final String CLEAN = "CLEAN";
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    public static final String DIRTY = "DIRTY";
    public static final String JOURNAL_FILE = "journal";
    public static final String JOURNAL_FILE_BACKUP = "journal.bkp";
    public static final String JOURNAL_FILE_TEMP = "journal.tmp";
    public static final Regex LEGAL_KEY_PATTERN = new Regex("[a-z0-9_-]{1,120}");
    public static final String MAGIC = "libcore.io.DiskLruCache";
    public static final String READ = "READ";
    public static final String REMOVE = "REMOVE";
    public static final String VERSION_1 = "1";
    private final int appVersion;
    /* access modifiers changed from: private */
    public boolean civilizedFileSystem;
    private final TaskQueue cleanupQueue;
    private final DiskLruCache$cleanupTask$1 cleanupTask;
    private boolean closed;
    private final File directory;
    private final FileSystem fileSystem;
    /* access modifiers changed from: private */
    public boolean hasJournalErrors;
    /* access modifiers changed from: private */
    public boolean initialized;
    private final File journalFile;
    private final File journalFileBackup;
    private final File journalFileTmp;
    /* access modifiers changed from: private */
    public BufferedSink journalWriter;
    private final LinkedHashMap<String, Entry> lruEntries = new LinkedHashMap<>(0, 0.75f, true);
    private long maxSize;
    /* access modifiers changed from: private */
    public boolean mostRecentRebuildFailed;
    /* access modifiers changed from: private */
    public boolean mostRecentTrimFailed;
    private long nextSequenceNumber;
    /* access modifiers changed from: private */
    public int redundantOpCount;
    private long size;
    private final int valueCount;

    public final Editor edit(String str) throws IOException {
        Intrinsics.checkNotNullParameter(str, "key");
        return edit$default(this, str, 0, 2, (Object) null);
    }

    public DiskLruCache(FileSystem fileSystem2, File directory2, int appVersion2, int valueCount2, long maxSize2, TaskRunner taskRunner) {
        Intrinsics.checkNotNullParameter(fileSystem2, "fileSystem");
        Intrinsics.checkNotNullParameter(directory2, "directory");
        Intrinsics.checkNotNullParameter(taskRunner, "taskRunner");
        this.fileSystem = fileSystem2;
        this.directory = directory2;
        this.appVersion = appVersion2;
        this.valueCount = valueCount2;
        this.maxSize = maxSize2;
        boolean z = false;
        this.cleanupQueue = taskRunner.newQueue();
        this.cleanupTask = new DiskLruCache$cleanupTask$1(this, Intrinsics.stringPlus(Util.okHttpName, " Cache"));
        if (maxSize2 > 0) {
            if (valueCount2 > 0 ? true : z) {
                this.journalFile = new File(directory2, JOURNAL_FILE);
                this.journalFileTmp = new File(directory2, JOURNAL_FILE_TEMP);
                this.journalFileBackup = new File(directory2, JOURNAL_FILE_BACKUP);
                return;
            }
            throw new IllegalArgumentException("valueCount <= 0".toString());
        }
        throw new IllegalArgumentException("maxSize <= 0".toString());
    }

    public final FileSystem getFileSystem$okhttp() {
        return this.fileSystem;
    }

    public final File getDirectory() {
        return this.directory;
    }

    public final int getValueCount$okhttp() {
        return this.valueCount;
    }

    public final synchronized long getMaxSize() {
        return this.maxSize;
    }

    public final synchronized void setMaxSize(long value) {
        this.maxSize = value;
        if (this.initialized) {
            TaskQueue.schedule$default(this.cleanupQueue, this.cleanupTask, 0, 2, (Object) null);
        }
    }

    public final LinkedHashMap<String, Entry> getLruEntries$okhttp() {
        return this.lruEntries;
    }

    public final boolean getClosed$okhttp() {
        return this.closed;
    }

    public final void setClosed$okhttp(boolean z) {
        this.closed = z;
    }

    public final synchronized void initialize() throws IOException {
        if (Util.assertionsEnabled) {
            if (!Thread.holdsLock(this)) {
                throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + this);
            }
        }
        if (!this.initialized) {
            if (this.fileSystem.exists(this.journalFileBackup)) {
                if (this.fileSystem.exists(this.journalFile)) {
                    this.fileSystem.delete(this.journalFileBackup);
                } else {
                    this.fileSystem.rename(this.journalFileBackup, this.journalFile);
                }
            }
            this.civilizedFileSystem = Util.isCivilized(this.fileSystem, this.journalFileBackup);
            if (this.fileSystem.exists(this.journalFile)) {
                try {
                    readJournal();
                    processJournal();
                    this.initialized = true;
                    return;
                } catch (IOException journalIsCorrupt) {
                    Platform.Companion.get().log("DiskLruCache " + this.directory + " is corrupt: " + journalIsCorrupt.getMessage() + ", removing", 5, journalIsCorrupt);
                    delete();
                    this.closed = false;
                } catch (Throwable th) {
                    this.closed = false;
                    throw th;
                }
            }
            rebuildJournal$okhttp();
            this.initialized = true;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00cd, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00ce, code lost:
        kotlin.io.CloseableKt.closeFinally(r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00d1, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void readJournal() throws java.io.IOException {
        /*
            r12 = this;
            java.lang.String r0 = ", "
            okhttp3.internal.io.FileSystem r1 = r12.fileSystem
            java.io.File r2 = r12.journalFile
            okio.Source r1 = r1.source(r2)
            okio.BufferedSource r1 = okio.Okio.buffer((okio.Source) r1)
            java.io.Closeable r1 = (java.io.Closeable) r1
            r2 = r1
            okio.BufferedSource r2 = (okio.BufferedSource) r2     // Catch:{ all -> 0x00cb }
            r3 = 0
            java.lang.String r4 = r2.readUtf8LineStrict()     // Catch:{ all -> 0x00cb }
            java.lang.String r5 = r2.readUtf8LineStrict()     // Catch:{ all -> 0x00cb }
            java.lang.String r6 = r2.readUtf8LineStrict()     // Catch:{ all -> 0x00cb }
            java.lang.String r7 = r2.readUtf8LineStrict()     // Catch:{ all -> 0x00cb }
            java.lang.String r8 = r2.readUtf8LineStrict()     // Catch:{ all -> 0x00cb }
            java.lang.String r9 = MAGIC     // Catch:{ all -> 0x00cb }
            boolean r9 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r9, (java.lang.Object) r4)     // Catch:{ all -> 0x00cb }
            if (r9 == 0) goto L_0x0093
            java.lang.String r9 = VERSION_1     // Catch:{ all -> 0x00cb }
            boolean r9 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r9, (java.lang.Object) r5)     // Catch:{ all -> 0x00cb }
            if (r9 == 0) goto L_0x0093
            int r9 = r12.appVersion     // Catch:{ all -> 0x00cb }
            java.lang.String r9 = java.lang.String.valueOf(r9)     // Catch:{ all -> 0x00cb }
            boolean r9 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r9, (java.lang.Object) r6)     // Catch:{ all -> 0x00cb }
            if (r9 == 0) goto L_0x0093
            int r9 = r12.getValueCount$okhttp()     // Catch:{ all -> 0x00cb }
            java.lang.String r9 = java.lang.String.valueOf(r9)     // Catch:{ all -> 0x00cb }
            boolean r9 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r9, (java.lang.Object) r7)     // Catch:{ all -> 0x00cb }
            if (r9 == 0) goto L_0x0093
            r9 = r8
            java.lang.CharSequence r9 = (java.lang.CharSequence) r9     // Catch:{ all -> 0x00cb }
            int r9 = r9.length()     // Catch:{ all -> 0x00cb }
            if (r9 <= 0) goto L_0x005d
            r9 = 1
            goto L_0x005e
        L_0x005d:
            r9 = 0
        L_0x005e:
            if (r9 != 0) goto L_0x0093
            r0 = 0
        L_0x0061:
            java.lang.String r9 = r2.readUtf8LineStrict()     // Catch:{ EOFException -> 0x006d }
            r12.readJournalLine(r9)     // Catch:{ EOFException -> 0x006d }
            int r0 = r0 + 1
            goto L_0x0061
        L_0x006d:
            r9 = move-exception
            java.util.LinkedHashMap r9 = r12.getLruEntries$okhttp()     // Catch:{ all -> 0x00cb }
            int r9 = r9.size()     // Catch:{ all -> 0x00cb }
            int r9 = r0 - r9
            r12.redundantOpCount = r9     // Catch:{ all -> 0x00cb }
            boolean r9 = r2.exhausted()     // Catch:{ all -> 0x00cb }
            if (r9 != 0) goto L_0x0085
            r12.rebuildJournal$okhttp()     // Catch:{ all -> 0x00cb }
            goto L_0x008b
        L_0x0085:
            okio.BufferedSink r9 = r12.newJournalWriter()     // Catch:{ all -> 0x00cb }
            r12.journalWriter = r9     // Catch:{ all -> 0x00cb }
        L_0x008b:
            kotlin.Unit r0 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x00cb }
            r0 = 0
            kotlin.io.CloseableKt.closeFinally(r1, r0)
            return
        L_0x0093:
            java.io.IOException r9 = new java.io.IOException     // Catch:{ all -> 0x00cb }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x00cb }
            r10.<init>()     // Catch:{ all -> 0x00cb }
            java.lang.String r11 = "unexpected journal header: ["
            java.lang.StringBuilder r10 = r10.append(r11)     // Catch:{ all -> 0x00cb }
            java.lang.StringBuilder r10 = r10.append(r4)     // Catch:{ all -> 0x00cb }
            java.lang.StringBuilder r10 = r10.append(r0)     // Catch:{ all -> 0x00cb }
            java.lang.StringBuilder r10 = r10.append(r5)     // Catch:{ all -> 0x00cb }
            java.lang.StringBuilder r10 = r10.append(r0)     // Catch:{ all -> 0x00cb }
            java.lang.StringBuilder r10 = r10.append(r7)     // Catch:{ all -> 0x00cb }
            java.lang.StringBuilder r0 = r10.append(r0)     // Catch:{ all -> 0x00cb }
            java.lang.StringBuilder r0 = r0.append(r8)     // Catch:{ all -> 0x00cb }
            r10 = 93
            java.lang.StringBuilder r0 = r0.append(r10)     // Catch:{ all -> 0x00cb }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x00cb }
            r9.<init>(r0)     // Catch:{ all -> 0x00cb }
            throw r9     // Catch:{ all -> 0x00cb }
        L_0x00cb:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x00cd }
        L_0x00cd:
            r2 = move-exception
            kotlin.io.CloseableKt.closeFinally(r1, r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.readJournal():void");
    }

    private final BufferedSink newJournalWriter() throws FileNotFoundException {
        return Okio.buffer((Sink) new FaultHidingSink(this.fileSystem.appendingSink(this.journalFile), new DiskLruCache$newJournalWriter$faultHidingSink$1(this)));
    }

    private final void readJournalLine(String line) throws IOException {
        String key;
        String str = line;
        int firstSpace = StringsKt.indexOf$default((CharSequence) str, ' ', 0, false, 6, (Object) null);
        if (firstSpace != -1) {
            int keyBegin = firstSpace + 1;
            int secondSpace = StringsKt.indexOf$default((CharSequence) str, ' ', keyBegin, false, 4, (Object) null);
            if (secondSpace == -1) {
                String substring = str.substring(keyBegin);
                Intrinsics.checkNotNullExpressionValue(substring, "this as java.lang.String).substring(startIndex)");
                key = substring;
                String str2 = REMOVE;
                if (firstSpace == str2.length() && StringsKt.startsWith$default(str, str2, false, 2, (Object) null)) {
                    this.lruEntries.remove(key);
                    return;
                }
            } else {
                String substring2 = str.substring(keyBegin, secondSpace);
                Intrinsics.checkNotNullExpressionValue(substring2, "this as java.lang.String…ing(startIndex, endIndex)");
                key = substring2;
            }
            Entry entry = this.lruEntries.get(key);
            if (entry == null) {
                entry = new Entry(this, key);
                this.lruEntries.put(key, entry);
            }
            if (secondSpace != -1) {
                String str3 = CLEAN;
                if (firstSpace == str3.length() && StringsKt.startsWith$default(str, str3, false, 2, (Object) null)) {
                    String substring3 = str.substring(secondSpace + 1);
                    Intrinsics.checkNotNullExpressionValue(substring3, "this as java.lang.String).substring(startIndex)");
                    List parts = StringsKt.split$default((CharSequence) substring3, new char[]{' '}, false, 0, 6, (Object) null);
                    entry.setReadable$okhttp(true);
                    entry.setCurrentEditor$okhttp((Editor) null);
                    entry.setLengths$okhttp(parts);
                    return;
                }
            }
            if (secondSpace == -1) {
                String str4 = DIRTY;
                if (firstSpace == str4.length() && StringsKt.startsWith$default(str, str4, false, 2, (Object) null)) {
                    entry.setCurrentEditor$okhttp(new Editor(this, entry));
                    return;
                }
            }
            if (secondSpace == -1) {
                String str5 = READ;
                if (firstSpace == str5.length() && StringsKt.startsWith$default(str, str5, false, 2, (Object) null)) {
                    return;
                }
            }
            throw new IOException(Intrinsics.stringPlus("unexpected journal line: ", str));
        }
        throw new IOException(Intrinsics.stringPlus("unexpected journal line: ", str));
    }

    private final void processJournal() throws IOException {
        this.fileSystem.delete(this.journalFileTmp);
        Iterator i = this.lruEntries.values().iterator();
        while (i.hasNext()) {
            Entry next = i.next();
            Intrinsics.checkNotNullExpressionValue(next, "i.next()");
            Entry entry = next;
            int i2 = 0;
            if (entry.getCurrentEditor$okhttp() == null) {
                int i3 = this.valueCount;
                while (i2 < i3) {
                    int t = i2;
                    i2++;
                    this.size += entry.getLengths$okhttp()[t];
                }
            } else {
                entry.setCurrentEditor$okhttp((Editor) null);
                int i4 = this.valueCount;
                while (i2 < i4) {
                    int t2 = i2;
                    i2++;
                    this.fileSystem.delete(entry.getCleanFiles$okhttp().get(t2));
                    this.fileSystem.delete(entry.getDirtyFiles$okhttp().get(t2));
                }
                i.remove();
            }
        }
    }

    public final synchronized void rebuildJournal$okhttp() throws IOException {
        BufferedSink bufferedSink = this.journalWriter;
        if (bufferedSink != null) {
            bufferedSink.close();
        }
        Closeable buffer = Okio.buffer(this.fileSystem.sink(this.journalFileTmp));
        try {
            BufferedSink sink = (BufferedSink) buffer;
            sink.writeUtf8(MAGIC).writeByte(10);
            sink.writeUtf8(VERSION_1).writeByte(10);
            sink.writeDecimalLong((long) this.appVersion).writeByte(10);
            sink.writeDecimalLong((long) getValueCount$okhttp()).writeByte(10);
            sink.writeByte(10);
            for (Entry entry : getLruEntries$okhttp().values()) {
                try {
                    if (entry.getCurrentEditor$okhttp() != null) {
                        sink.writeUtf8(DIRTY).writeByte(32);
                        sink.writeUtf8(entry.getKey$okhttp());
                        sink.writeByte(10);
                    } else {
                        sink.writeUtf8(CLEAN).writeByte(32);
                        sink.writeUtf8(entry.getKey$okhttp());
                        entry.writeLengths$okhttp(sink);
                        sink.writeByte(10);
                    }
                } catch (Throwable th) {
                    th = th;
                    try {
                        throw th;
                    } catch (Throwable th2) {
                        CloseableKt.closeFinally(buffer, th);
                        throw th2;
                    }
                }
            }
            Unit unit = Unit.INSTANCE;
            CloseableKt.closeFinally(buffer, (Throwable) null);
            if (this.fileSystem.exists(this.journalFile)) {
                this.fileSystem.rename(this.journalFile, this.journalFileBackup);
            }
            this.fileSystem.rename(this.journalFileTmp, this.journalFile);
            this.fileSystem.delete(this.journalFileBackup);
            this.journalWriter = newJournalWriter();
            this.hasJournalErrors = false;
            this.mostRecentRebuildFailed = false;
        } catch (Throwable th3) {
            th = th3;
            throw th;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x005a, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final synchronized okhttp3.internal.cache.DiskLruCache.Snapshot get(java.lang.String r10) throws java.io.IOException {
        /*
            r9 = this;
            monitor-enter(r9)
            java.lang.String r0 = "key"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r10, r0)     // Catch:{ all -> 0x005b }
            r9.initialize()     // Catch:{ all -> 0x005b }
            r9.checkNotClosed()     // Catch:{ all -> 0x005b }
            r9.validateKey(r10)     // Catch:{ all -> 0x005b }
            java.util.LinkedHashMap<java.lang.String, okhttp3.internal.cache.DiskLruCache$Entry> r0 = r9.lruEntries     // Catch:{ all -> 0x005b }
            java.lang.Object r0 = r0.get(r10)     // Catch:{ all -> 0x005b }
            okhttp3.internal.cache.DiskLruCache$Entry r0 = (okhttp3.internal.cache.DiskLruCache.Entry) r0     // Catch:{ all -> 0x005b }
            r1 = 0
            if (r0 != 0) goto L_0x001c
            monitor-exit(r9)
            return r1
        L_0x001c:
            okhttp3.internal.cache.DiskLruCache$Snapshot r2 = r0.snapshot$okhttp()     // Catch:{ all -> 0x005b }
            if (r2 != 0) goto L_0x0024
            monitor-exit(r9)
            return r1
        L_0x0024:
            r1 = r2
            int r2 = r9.redundantOpCount     // Catch:{ all -> 0x005b }
            int r2 = r2 + 1
            r9.redundantOpCount = r2     // Catch:{ all -> 0x005b }
            okio.BufferedSink r2 = r9.journalWriter     // Catch:{ all -> 0x005b }
            kotlin.jvm.internal.Intrinsics.checkNotNull(r2)     // Catch:{ all -> 0x005b }
            java.lang.String r3 = READ     // Catch:{ all -> 0x005b }
            okio.BufferedSink r2 = r2.writeUtf8(r3)     // Catch:{ all -> 0x005b }
            r3 = 32
            okio.BufferedSink r2 = r2.writeByte(r3)     // Catch:{ all -> 0x005b }
            okio.BufferedSink r2 = r2.writeUtf8(r10)     // Catch:{ all -> 0x005b }
            r3 = 10
            r2.writeByte(r3)     // Catch:{ all -> 0x005b }
            boolean r2 = r9.journalRebuildRequired()     // Catch:{ all -> 0x005b }
            if (r2 == 0) goto L_0x0059
            okhttp3.internal.concurrent.TaskQueue r3 = r9.cleanupQueue     // Catch:{ all -> 0x005b }
            okhttp3.internal.cache.DiskLruCache$cleanupTask$1 r2 = r9.cleanupTask     // Catch:{ all -> 0x005b }
            r4 = r2
            okhttp3.internal.concurrent.Task r4 = (okhttp3.internal.concurrent.Task) r4     // Catch:{ all -> 0x005b }
            r5 = 0
            r7 = 2
            r8 = 0
            okhttp3.internal.concurrent.TaskQueue.schedule$default(r3, r4, r5, r7, r8)     // Catch:{ all -> 0x005b }
        L_0x0059:
            monitor-exit(r9)
            return r1
        L_0x005b:
            r10 = move-exception
            monitor-exit(r9)
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.get(java.lang.String):okhttp3.internal.cache.DiskLruCache$Snapshot");
    }

    public static /* synthetic */ Editor edit$default(DiskLruCache diskLruCache, String str, long j, int i, Object obj) throws IOException {
        if ((i & 2) != 0) {
            j = ANY_SEQUENCE_NUMBER;
        }
        return diskLruCache.edit(str, j);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0029, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final synchronized okhttp3.internal.cache.DiskLruCache.Editor edit(java.lang.String r10, long r11) throws java.io.IOException {
        /*
            r9 = this;
            monitor-enter(r9)
            java.lang.String r0 = "key"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r10, r0)     // Catch:{ all -> 0x0095 }
            r9.initialize()     // Catch:{ all -> 0x0095 }
            r9.checkNotClosed()     // Catch:{ all -> 0x0095 }
            r9.validateKey(r10)     // Catch:{ all -> 0x0095 }
            java.util.LinkedHashMap<java.lang.String, okhttp3.internal.cache.DiskLruCache$Entry> r0 = r9.lruEntries     // Catch:{ all -> 0x0095 }
            java.lang.Object r0 = r0.get(r10)     // Catch:{ all -> 0x0095 }
            okhttp3.internal.cache.DiskLruCache$Entry r0 = (okhttp3.internal.cache.DiskLruCache.Entry) r0     // Catch:{ all -> 0x0095 }
            long r1 = ANY_SEQUENCE_NUMBER     // Catch:{ all -> 0x0095 }
            int r1 = (r11 > r1 ? 1 : (r11 == r1 ? 0 : -1))
            r2 = 0
            if (r1 == 0) goto L_0x002a
            if (r0 == 0) goto L_0x0028
            long r3 = r0.getSequenceNumber$okhttp()     // Catch:{ all -> 0x0095 }
            int r1 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r1 == 0) goto L_0x002a
        L_0x0028:
            monitor-exit(r9)
            return r2
        L_0x002a:
            if (r0 != 0) goto L_0x002e
            r1 = r2
            goto L_0x0032
        L_0x002e:
            okhttp3.internal.cache.DiskLruCache$Editor r1 = r0.getCurrentEditor$okhttp()     // Catch:{ all -> 0x0095 }
        L_0x0032:
            if (r1 == 0) goto L_0x0036
            monitor-exit(r9)
            return r2
        L_0x0036:
            if (r0 == 0) goto L_0x0040
            int r1 = r0.getLockingSourceCount$okhttp()     // Catch:{ all -> 0x0095 }
            if (r1 == 0) goto L_0x0040
            monitor-exit(r9)
            return r2
        L_0x0040:
            boolean r1 = r9.mostRecentTrimFailed     // Catch:{ all -> 0x0095 }
            if (r1 != 0) goto L_0x0085
            boolean r1 = r9.mostRecentRebuildFailed     // Catch:{ all -> 0x0095 }
            if (r1 == 0) goto L_0x0049
            goto L_0x0085
        L_0x0049:
            okio.BufferedSink r1 = r9.journalWriter     // Catch:{ all -> 0x0095 }
            kotlin.jvm.internal.Intrinsics.checkNotNull(r1)     // Catch:{ all -> 0x0095 }
            java.lang.String r3 = DIRTY     // Catch:{ all -> 0x0095 }
            okio.BufferedSink r3 = r1.writeUtf8(r3)     // Catch:{ all -> 0x0095 }
            r4 = 32
            okio.BufferedSink r3 = r3.writeByte(r4)     // Catch:{ all -> 0x0095 }
            okio.BufferedSink r3 = r3.writeUtf8(r10)     // Catch:{ all -> 0x0095 }
            r4 = 10
            r3.writeByte(r4)     // Catch:{ all -> 0x0095 }
            r1.flush()     // Catch:{ all -> 0x0095 }
            boolean r3 = r9.hasJournalErrors     // Catch:{ all -> 0x0095 }
            if (r3 == 0) goto L_0x006c
            monitor-exit(r9)
            return r2
        L_0x006c:
            if (r0 != 0) goto L_0x007b
            okhttp3.internal.cache.DiskLruCache$Entry r2 = new okhttp3.internal.cache.DiskLruCache$Entry     // Catch:{ all -> 0x0095 }
            r2.<init>(r9, r10)     // Catch:{ all -> 0x0095 }
            r0 = r2
            java.util.LinkedHashMap<java.lang.String, okhttp3.internal.cache.DiskLruCache$Entry> r2 = r9.lruEntries     // Catch:{ all -> 0x0095 }
            java.util.Map r2 = (java.util.Map) r2     // Catch:{ all -> 0x0095 }
            r2.put(r10, r0)     // Catch:{ all -> 0x0095 }
        L_0x007b:
            okhttp3.internal.cache.DiskLruCache$Editor r2 = new okhttp3.internal.cache.DiskLruCache$Editor     // Catch:{ all -> 0x0095 }
            r2.<init>(r9, r0)     // Catch:{ all -> 0x0095 }
            r0.setCurrentEditor$okhttp(r2)     // Catch:{ all -> 0x0095 }
            monitor-exit(r9)
            return r2
        L_0x0085:
            okhttp3.internal.concurrent.TaskQueue r3 = r9.cleanupQueue     // Catch:{ all -> 0x0095 }
            okhttp3.internal.cache.DiskLruCache$cleanupTask$1 r1 = r9.cleanupTask     // Catch:{ all -> 0x0095 }
            r4 = r1
            okhttp3.internal.concurrent.Task r4 = (okhttp3.internal.concurrent.Task) r4     // Catch:{ all -> 0x0095 }
            r5 = 0
            r7 = 2
            r8 = 0
            okhttp3.internal.concurrent.TaskQueue.schedule$default(r3, r4, r5, r7, r8)     // Catch:{ all -> 0x0095 }
            monitor-exit(r9)
            return r2
        L_0x0095:
            r10 = move-exception
            monitor-exit(r9)
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.edit(java.lang.String, long):okhttp3.internal.cache.DiskLruCache$Editor");
    }

    public final synchronized long size() throws IOException {
        initialize();
        return this.size;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:51:0x0137, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final synchronized void completeEdit$okhttp(okhttp3.internal.cache.DiskLruCache.Editor r13, boolean r14) throws java.io.IOException {
        /*
            r12 = this;
            monitor-enter(r12)
            java.lang.String r0 = "editor"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r13, r0)     // Catch:{ all -> 0x0144 }
            okhttp3.internal.cache.DiskLruCache$Entry r0 = r13.getEntry$okhttp()     // Catch:{ all -> 0x0144 }
            okhttp3.internal.cache.DiskLruCache$Editor r1 = r0.getCurrentEditor$okhttp()     // Catch:{ all -> 0x0144 }
            boolean r1 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r1, (java.lang.Object) r13)     // Catch:{ all -> 0x0144 }
            if (r1 == 0) goto L_0x0138
            r1 = 0
            if (r14 == 0) goto L_0x005a
            boolean r2 = r0.getReadable$okhttp()     // Catch:{ all -> 0x0144 }
            if (r2 != 0) goto L_0x005a
            int r2 = r12.valueCount     // Catch:{ all -> 0x0144 }
            r3 = r1
        L_0x0020:
            if (r3 >= r2) goto L_0x005a
            r4 = r3
            int r3 = r3 + 1
            boolean[] r5 = r13.getWritten$okhttp()     // Catch:{ all -> 0x0144 }
            kotlin.jvm.internal.Intrinsics.checkNotNull(r5)     // Catch:{ all -> 0x0144 }
            boolean r5 = r5[r4]     // Catch:{ all -> 0x0144 }
            if (r5 == 0) goto L_0x0047
            okhttp3.internal.io.FileSystem r5 = r12.fileSystem     // Catch:{ all -> 0x0144 }
            java.util.List r6 = r0.getDirtyFiles$okhttp()     // Catch:{ all -> 0x0144 }
            java.lang.Object r6 = r6.get(r4)     // Catch:{ all -> 0x0144 }
            java.io.File r6 = (java.io.File) r6     // Catch:{ all -> 0x0144 }
            boolean r5 = r5.exists(r6)     // Catch:{ all -> 0x0144 }
            if (r5 != 0) goto L_0x0020
            r13.abort()     // Catch:{ all -> 0x0144 }
            monitor-exit(r12)
            return
        L_0x0047:
            r13.abort()     // Catch:{ all -> 0x0144 }
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException     // Catch:{ all -> 0x0144 }
            java.lang.String r2 = "Newly created entry didn't create value for index "
            java.lang.Integer r3 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x0144 }
            java.lang.String r2 = kotlin.jvm.internal.Intrinsics.stringPlus(r2, r3)     // Catch:{ all -> 0x0144 }
            r1.<init>(r2)     // Catch:{ all -> 0x0144 }
            throw r1     // Catch:{ all -> 0x0144 }
        L_0x005a:
            int r2 = r12.valueCount     // Catch:{ all -> 0x0144 }
        L_0x005c:
            if (r1 >= r2) goto L_0x00aa
            r3 = r1
            int r1 = r1 + 1
            java.util.List r4 = r0.getDirtyFiles$okhttp()     // Catch:{ all -> 0x0144 }
            java.lang.Object r4 = r4.get(r3)     // Catch:{ all -> 0x0144 }
            java.io.File r4 = (java.io.File) r4     // Catch:{ all -> 0x0144 }
            if (r14 == 0) goto L_0x00a4
            boolean r5 = r0.getZombie$okhttp()     // Catch:{ all -> 0x0144 }
            if (r5 != 0) goto L_0x00a4
            okhttp3.internal.io.FileSystem r5 = r12.fileSystem     // Catch:{ all -> 0x0144 }
            boolean r5 = r5.exists(r4)     // Catch:{ all -> 0x0144 }
            if (r5 == 0) goto L_0x005c
            java.util.List r5 = r0.getCleanFiles$okhttp()     // Catch:{ all -> 0x0144 }
            java.lang.Object r5 = r5.get(r3)     // Catch:{ all -> 0x0144 }
            java.io.File r5 = (java.io.File) r5     // Catch:{ all -> 0x0144 }
            okhttp3.internal.io.FileSystem r6 = r12.fileSystem     // Catch:{ all -> 0x0144 }
            r6.rename(r4, r5)     // Catch:{ all -> 0x0144 }
            long[] r6 = r0.getLengths$okhttp()     // Catch:{ all -> 0x0144 }
            r7 = r6[r3]     // Catch:{ all -> 0x0144 }
            r6 = r7
            okhttp3.internal.io.FileSystem r8 = r12.fileSystem     // Catch:{ all -> 0x0144 }
            long r8 = r8.size(r5)     // Catch:{ all -> 0x0144 }
            long[] r10 = r0.getLengths$okhttp()     // Catch:{ all -> 0x0144 }
            r10[r3] = r8     // Catch:{ all -> 0x0144 }
            long r10 = r12.size     // Catch:{ all -> 0x0144 }
            long r10 = r10 - r6
            long r10 = r10 + r8
            r12.size = r10     // Catch:{ all -> 0x0144 }
            goto L_0x005c
        L_0x00a4:
            okhttp3.internal.io.FileSystem r5 = r12.fileSystem     // Catch:{ all -> 0x0144 }
            r5.delete(r4)     // Catch:{ all -> 0x0144 }
            goto L_0x005c
        L_0x00aa:
            r1 = 0
            r0.setCurrentEditor$okhttp(r1)     // Catch:{ all -> 0x0144 }
            boolean r1 = r0.getZombie$okhttp()     // Catch:{ all -> 0x0144 }
            if (r1 == 0) goto L_0x00b9
            r12.removeEntry$okhttp(r0)     // Catch:{ all -> 0x0144 }
            monitor-exit(r12)
            return
        L_0x00b9:
            int r1 = r12.redundantOpCount     // Catch:{ all -> 0x0144 }
            r2 = 1
            int r1 = r1 + r2
            r12.redundantOpCount = r1     // Catch:{ all -> 0x0144 }
            okio.BufferedSink r1 = r12.journalWriter     // Catch:{ all -> 0x0144 }
            kotlin.jvm.internal.Intrinsics.checkNotNull(r1)     // Catch:{ all -> 0x0144 }
            r3 = 0
            boolean r4 = r0.getReadable$okhttp()     // Catch:{ all -> 0x0144 }
            r5 = 10
            r6 = 32
            if (r4 != 0) goto L_0x00f1
            if (r14 == 0) goto L_0x00d2
            goto L_0x00f1
        L_0x00d2:
            java.util.LinkedHashMap r2 = r12.getLruEntries$okhttp()     // Catch:{ all -> 0x0144 }
            java.lang.String r4 = r0.getKey$okhttp()     // Catch:{ all -> 0x0144 }
            r2.remove(r4)     // Catch:{ all -> 0x0144 }
            java.lang.String r2 = REMOVE     // Catch:{ all -> 0x0144 }
            okio.BufferedSink r2 = r1.writeUtf8(r2)     // Catch:{ all -> 0x0144 }
            r2.writeByte(r6)     // Catch:{ all -> 0x0144 }
            java.lang.String r2 = r0.getKey$okhttp()     // Catch:{ all -> 0x0144 }
            r1.writeUtf8(r2)     // Catch:{ all -> 0x0144 }
            r1.writeByte(r5)     // Catch:{ all -> 0x0144 }
            goto L_0x0116
        L_0x00f1:
            r0.setReadable$okhttp(r2)     // Catch:{ all -> 0x0144 }
            java.lang.String r2 = CLEAN     // Catch:{ all -> 0x0144 }
            okio.BufferedSink r2 = r1.writeUtf8(r2)     // Catch:{ all -> 0x0144 }
            r2.writeByte(r6)     // Catch:{ all -> 0x0144 }
            java.lang.String r2 = r0.getKey$okhttp()     // Catch:{ all -> 0x0144 }
            r1.writeUtf8(r2)     // Catch:{ all -> 0x0144 }
            r0.writeLengths$okhttp(r1)     // Catch:{ all -> 0x0144 }
            r1.writeByte(r5)     // Catch:{ all -> 0x0144 }
            if (r14 == 0) goto L_0x0116
            long r4 = r12.nextSequenceNumber     // Catch:{ all -> 0x0144 }
            r6 = 1
            long r6 = r6 + r4
            r12.nextSequenceNumber = r6     // Catch:{ all -> 0x0144 }
            r0.setSequenceNumber$okhttp(r4)     // Catch:{ all -> 0x0144 }
        L_0x0116:
            r1.flush()     // Catch:{ all -> 0x0144 }
            long r1 = r12.size     // Catch:{ all -> 0x0144 }
            long r3 = r12.maxSize     // Catch:{ all -> 0x0144 }
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 > 0) goto L_0x0129
            boolean r1 = r12.journalRebuildRequired()     // Catch:{ all -> 0x0144 }
            if (r1 == 0) goto L_0x0136
        L_0x0129:
            okhttp3.internal.concurrent.TaskQueue r1 = r12.cleanupQueue     // Catch:{ all -> 0x0144 }
            okhttp3.internal.cache.DiskLruCache$cleanupTask$1 r2 = r12.cleanupTask     // Catch:{ all -> 0x0144 }
            okhttp3.internal.concurrent.Task r2 = (okhttp3.internal.concurrent.Task) r2     // Catch:{ all -> 0x0144 }
            r3 = 0
            r5 = 2
            r6 = 0
            okhttp3.internal.concurrent.TaskQueue.schedule$default(r1, r2, r3, r5, r6)     // Catch:{ all -> 0x0144 }
        L_0x0136:
            monitor-exit(r12)
            return
        L_0x0138:
            java.lang.String r1 = "Check failed."
            java.lang.IllegalStateException r2 = new java.lang.IllegalStateException     // Catch:{ all -> 0x0144 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0144 }
            r2.<init>(r1)     // Catch:{ all -> 0x0144 }
            throw r2     // Catch:{ all -> 0x0144 }
        L_0x0144:
            r13 = move-exception
            monitor-exit(r12)
            throw r13
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.completeEdit$okhttp(okhttp3.internal.cache.DiskLruCache$Editor, boolean):void");
    }

    /* access modifiers changed from: private */
    public final boolean journalRebuildRequired() {
        int i = this.redundantOpCount;
        return i >= 2000 && i >= this.lruEntries.size();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002d, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final synchronized boolean remove(java.lang.String r8) throws java.io.IOException {
        /*
            r7 = this;
            monitor-enter(r7)
            java.lang.String r0 = "key"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r8, r0)     // Catch:{ all -> 0x002e }
            r7.initialize()     // Catch:{ all -> 0x002e }
            r7.checkNotClosed()     // Catch:{ all -> 0x002e }
            r7.validateKey(r8)     // Catch:{ all -> 0x002e }
            java.util.LinkedHashMap<java.lang.String, okhttp3.internal.cache.DiskLruCache$Entry> r0 = r7.lruEntries     // Catch:{ all -> 0x002e }
            java.lang.Object r0 = r0.get(r8)     // Catch:{ all -> 0x002e }
            okhttp3.internal.cache.DiskLruCache$Entry r0 = (okhttp3.internal.cache.DiskLruCache.Entry) r0     // Catch:{ all -> 0x002e }
            r1 = 0
            if (r0 != 0) goto L_0x001c
            monitor-exit(r7)
            return r1
        L_0x001c:
            boolean r2 = r7.removeEntry$okhttp(r0)     // Catch:{ all -> 0x002e }
            if (r2 == 0) goto L_0x002c
            long r3 = r7.size     // Catch:{ all -> 0x002e }
            long r5 = r7.maxSize     // Catch:{ all -> 0x002e }
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 > 0) goto L_0x002c
            r7.mostRecentTrimFailed = r1     // Catch:{ all -> 0x002e }
        L_0x002c:
            monitor-exit(r7)
            return r2
        L_0x002e:
            r8 = move-exception
            monitor-exit(r7)
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.remove(java.lang.String):boolean");
    }

    public final boolean removeEntry$okhttp(Entry entry) throws IOException {
        BufferedSink it;
        Intrinsics.checkNotNullParameter(entry, "entry");
        if (!this.civilizedFileSystem) {
            if (entry.getLockingSourceCount$okhttp() > 0 && (it = this.journalWriter) != null) {
                it.writeUtf8(DIRTY);
                it.writeByte(32);
                it.writeUtf8(entry.getKey$okhttp());
                it.writeByte(10);
                it.flush();
            }
            if (entry.getLockingSourceCount$okhttp() > 0 || entry.getCurrentEditor$okhttp() != null) {
                entry.setZombie$okhttp(true);
                return true;
            }
        }
        Editor currentEditor$okhttp = entry.getCurrentEditor$okhttp();
        if (currentEditor$okhttp != null) {
            currentEditor$okhttp.detach$okhttp();
        }
        int i = 0;
        int i2 = this.valueCount;
        while (i < i2) {
            int i3 = i;
            i++;
            this.fileSystem.delete(entry.getCleanFiles$okhttp().get(i3));
            this.size -= entry.getLengths$okhttp()[i3];
            entry.getLengths$okhttp()[i3] = 0;
        }
        this.redundantOpCount++;
        BufferedSink it2 = this.journalWriter;
        if (it2 != null) {
            it2.writeUtf8(REMOVE);
            it2.writeByte(32);
            it2.writeUtf8(entry.getKey$okhttp());
            it2.writeByte(10);
        }
        this.lruEntries.remove(entry.getKey$okhttp());
        if (journalRebuildRequired()) {
            TaskQueue.schedule$default(this.cleanupQueue, this.cleanupTask, 0, 2, (Object) null);
        }
        return true;
    }

    private final synchronized void checkNotClosed() {
        if (!(!this.closed)) {
            throw new IllegalStateException("cache is closed".toString());
        }
    }

    public synchronized void flush() throws IOException {
        if (this.initialized) {
            checkNotClosed();
            trimToSize();
            BufferedSink bufferedSink = this.journalWriter;
            Intrinsics.checkNotNull(bufferedSink);
            bufferedSink.flush();
        }
    }

    public final synchronized boolean isClosed() {
        return this.closed;
    }

    public synchronized void close() throws IOException {
        if (this.initialized) {
            if (!this.closed) {
                Collection thisCollection$iv = this.lruEntries.values();
                Intrinsics.checkNotNullExpressionValue(thisCollection$iv, "lruEntries.values");
                int i = 0;
                Object[] array = thisCollection$iv.toArray(new Entry[0]);
                if (array != null) {
                    Entry[] entryArr = (Entry[]) array;
                    int length = entryArr.length;
                    while (i < length) {
                        Entry entry = entryArr[i];
                        i++;
                        if (entry.getCurrentEditor$okhttp() != null) {
                            Editor currentEditor$okhttp = entry.getCurrentEditor$okhttp();
                            if (currentEditor$okhttp != null) {
                                currentEditor$okhttp.detach$okhttp();
                            }
                        }
                    }
                    trimToSize();
                    BufferedSink bufferedSink = this.journalWriter;
                    Intrinsics.checkNotNull(bufferedSink);
                    bufferedSink.close();
                    this.journalWriter = null;
                    this.closed = true;
                    return;
                }
                throw new NullPointerException("null cannot be cast to non-null type kotlin.Array<T of kotlin.collections.ArraysKt__ArraysJVMKt.toTypedArray>");
            }
        }
        this.closed = true;
    }

    public final void trimToSize() throws IOException {
        while (this.size > this.maxSize) {
            if (!removeOldestEntry()) {
                return;
            }
        }
        this.mostRecentTrimFailed = false;
    }

    private final boolean removeOldestEntry() {
        for (Entry toEvict : this.lruEntries.values()) {
            if (!toEvict.getZombie$okhttp()) {
                Intrinsics.checkNotNullExpressionValue(toEvict, "toEvict");
                removeEntry$okhttp(toEvict);
                return true;
            }
        }
        return false;
    }

    public final void delete() throws IOException {
        close();
        this.fileSystem.deleteContents(this.directory);
    }

    public final synchronized void evictAll() throws IOException {
        initialize();
        Collection thisCollection$iv = this.lruEntries.values();
        Intrinsics.checkNotNullExpressionValue(thisCollection$iv, "lruEntries.values");
        Object[] array = thisCollection$iv.toArray(new Entry[0]);
        if (array != null) {
            Entry[] entryArr = (Entry[]) array;
            int length = entryArr.length;
            int i = 0;
            while (i < length) {
                Entry entry = entryArr[i];
                i++;
                Intrinsics.checkNotNullExpressionValue(entry, "entry");
                removeEntry$okhttp(entry);
            }
            this.mostRecentTrimFailed = false;
        } else {
            throw new NullPointerException("null cannot be cast to non-null type kotlin.Array<T of kotlin.collections.ArraysKt__ArraysJVMKt.toTypedArray>");
        }
    }

    private final void validateKey(String key) {
        if (!LEGAL_KEY_PATTERN.matches(key)) {
            throw new IllegalArgumentException(("keys must match regex [a-z0-9_-]{1,120}: \"" + key + Typography.quote).toString());
        }
    }

    public final synchronized Iterator<Snapshot> snapshots() throws IOException {
        initialize();
        return new DiskLruCache$snapshots$1(this);
    }

    @Metadata(d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0016\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0004\u0018\u00002\u00020\u0001B-\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\u0006\u0010\t\u001a\u00020\n¢\u0006\u0002\u0010\u000bJ\b\u0010\f\u001a\u00020\rH\u0016J\f\u0010\u000e\u001a\b\u0018\u00010\u000fR\u00020\u0010J\u000e\u0010\u0011\u001a\u00020\u00052\u0006\u0010\u0012\u001a\u00020\u0013J\u000e\u0010\u0014\u001a\u00020\b2\u0006\u0010\u0012\u001a\u00020\u0013J\u0006\u0010\u0002\u001a\u00020\u0003R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0004¢\u0006\u0002\n\u0000¨\u0006\u0015"}, d2 = {"Lokhttp3/internal/cache/DiskLruCache$Snapshot;", "Ljava/io/Closeable;", "key", "", "sequenceNumber", "", "sources", "", "Lokio/Source;", "lengths", "", "(Lokhttp3/internal/cache/DiskLruCache;Ljava/lang/String;JLjava/util/List;[J)V", "close", "", "edit", "Lokhttp3/internal/cache/DiskLruCache$Editor;", "Lokhttp3/internal/cache/DiskLruCache;", "getLength", "index", "", "getSource", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: DiskLruCache.kt */
    public final class Snapshot implements Closeable {
        private final String key;
        private final long[] lengths;
        private final long sequenceNumber;
        private final List<Source> sources;
        final /* synthetic */ DiskLruCache this$0;

        public Snapshot(DiskLruCache this$02, String key2, long sequenceNumber2, List<? extends Source> sources2, long[] lengths2) {
            Intrinsics.checkNotNullParameter(this$02, "this$0");
            Intrinsics.checkNotNullParameter(key2, "key");
            Intrinsics.checkNotNullParameter(sources2, "sources");
            Intrinsics.checkNotNullParameter(lengths2, "lengths");
            this.this$0 = this$02;
            this.key = key2;
            this.sequenceNumber = sequenceNumber2;
            this.sources = sources2;
            this.lengths = lengths2;
        }

        public final String key() {
            return this.key;
        }

        public final Editor edit() throws IOException {
            return this.this$0.edit(this.key, this.sequenceNumber);
        }

        public final Source getSource(int index) {
            return this.sources.get(index);
        }

        public final long getLength(int index) {
            return this.lengths[index];
        }

        public void close() {
            for (Source source : this.sources) {
                Util.closeQuietly((Closeable) source);
            }
        }
    }

    @Metadata(d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0018\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0004\u0018\u00002\u00020\u0001B\u0013\b\u0000\u0012\n\u0010\u0002\u001a\u00060\u0003R\u00020\u0004¢\u0006\u0002\u0010\u0005J\u0006\u0010\u000e\u001a\u00020\u000fJ\u0006\u0010\u0010\u001a\u00020\u000fJ\r\u0010\u0011\u001a\u00020\u000fH\u0000¢\u0006\u0002\b\u0012J\u000e\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016J\u0010\u0010\u0017\u001a\u0004\u0018\u00010\u00182\u0006\u0010\u0015\u001a\u00020\u0016R\u000e\u0010\u0006\u001a\u00020\u0007X\u000e¢\u0006\u0002\n\u0000R\u0018\u0010\u0002\u001a\u00060\u0003R\u00020\u0004X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0016\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006\u0019"}, d2 = {"Lokhttp3/internal/cache/DiskLruCache$Editor;", "", "entry", "Lokhttp3/internal/cache/DiskLruCache$Entry;", "Lokhttp3/internal/cache/DiskLruCache;", "(Lokhttp3/internal/cache/DiskLruCache;Lokhttp3/internal/cache/DiskLruCache$Entry;)V", "done", "", "getEntry$okhttp", "()Lokhttp3/internal/cache/DiskLruCache$Entry;", "written", "", "getWritten$okhttp", "()[Z", "abort", "", "commit", "detach", "detach$okhttp", "newSink", "Lokio/Sink;", "index", "", "newSource", "Lokio/Source;", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: DiskLruCache.kt */
    public final class Editor {
        private boolean done;
        private final Entry entry;
        final /* synthetic */ DiskLruCache this$0;
        private final boolean[] written;

        public Editor(DiskLruCache this$02, Entry entry2) {
            Intrinsics.checkNotNullParameter(this$02, "this$0");
            Intrinsics.checkNotNullParameter(entry2, "entry");
            this.this$0 = this$02;
            this.entry = entry2;
            this.written = entry2.getReadable$okhttp() ? null : new boolean[this$02.getValueCount$okhttp()];
        }

        public final Entry getEntry$okhttp() {
            return this.entry;
        }

        public final boolean[] getWritten$okhttp() {
            return this.written;
        }

        public final void detach$okhttp() {
            if (!Intrinsics.areEqual((Object) this.entry.getCurrentEditor$okhttp(), (Object) this)) {
                return;
            }
            if (this.this$0.civilizedFileSystem) {
                this.this$0.completeEdit$okhttp(this, false);
            } else {
                this.entry.setZombie$okhttp(true);
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:21:0x004f, code lost:
            return null;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final okio.Source newSource(int r6) {
            /*
                r5 = this;
                okhttp3.internal.cache.DiskLruCache r0 = r5.this$0
                monitor-enter(r0)
                r1 = 0
                boolean r2 = r5.done     // Catch:{ all -> 0x005c }
                r2 = r2 ^ 1
                if (r2 == 0) goto L_0x0050
                okhttp3.internal.cache.DiskLruCache$Entry r2 = r5.getEntry$okhttp()     // Catch:{ all -> 0x005c }
                boolean r2 = r2.getReadable$okhttp()     // Catch:{ all -> 0x005c }
                r3 = 0
                if (r2 == 0) goto L_0x004d
                okhttp3.internal.cache.DiskLruCache$Entry r2 = r5.getEntry$okhttp()     // Catch:{ all -> 0x005c }
                okhttp3.internal.cache.DiskLruCache$Editor r2 = r2.getCurrentEditor$okhttp()     // Catch:{ all -> 0x005c }
                boolean r2 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r2, (java.lang.Object) r5)     // Catch:{ all -> 0x005c }
                if (r2 == 0) goto L_0x004d
                okhttp3.internal.cache.DiskLruCache$Entry r2 = r5.getEntry$okhttp()     // Catch:{ all -> 0x005c }
                boolean r2 = r2.getZombie$okhttp()     // Catch:{ all -> 0x005c }
                if (r2 == 0) goto L_0x002e
                goto L_0x004d
            L_0x002e:
                okhttp3.internal.io.FileSystem r2 = r0.getFileSystem$okhttp()     // Catch:{ FileNotFoundException -> 0x0046 }
                okhttp3.internal.cache.DiskLruCache$Entry r4 = r5.getEntry$okhttp()     // Catch:{ FileNotFoundException -> 0x0046 }
                java.util.List r4 = r4.getCleanFiles$okhttp()     // Catch:{ FileNotFoundException -> 0x0046 }
                java.lang.Object r4 = r4.get(r6)     // Catch:{ FileNotFoundException -> 0x0046 }
                java.io.File r4 = (java.io.File) r4     // Catch:{ FileNotFoundException -> 0x0046 }
                okio.Source r3 = r2.source(r4)     // Catch:{ FileNotFoundException -> 0x0046 }
                goto L_0x004a
            L_0x0046:
                r2 = move-exception
                r4 = r3
                okio.Source r4 = (okio.Source) r4     // Catch:{ all -> 0x005c }
            L_0x004a:
                monitor-exit(r0)
                return r3
            L_0x004d:
                monitor-exit(r0)
                return r3
            L_0x0050:
                java.lang.String r2 = "Check failed."
                java.lang.IllegalStateException r3 = new java.lang.IllegalStateException     // Catch:{ all -> 0x005c }
                java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x005c }
                r3.<init>(r2)     // Catch:{ all -> 0x005c }
                throw r3     // Catch:{ all -> 0x005c }
            L_0x005c:
                r1 = move-exception
                monitor-exit(r0)
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.Editor.newSource(int):okio.Source");
        }

        public final Sink newSink(int index) {
            DiskLruCache diskLruCache = this.this$0;
            synchronized (diskLruCache) {
                if (!(!this.done)) {
                    throw new IllegalStateException("Check failed.".toString());
                } else if (!Intrinsics.areEqual((Object) getEntry$okhttp().getCurrentEditor$okhttp(), (Object) this)) {
                    Sink blackhole = Okio.blackhole();
                    return blackhole;
                } else {
                    if (!getEntry$okhttp().getReadable$okhttp()) {
                        boolean[] written$okhttp = getWritten$okhttp();
                        Intrinsics.checkNotNull(written$okhttp);
                        written$okhttp[index] = true;
                    }
                    try {
                        Sink faultHidingSink = new FaultHidingSink(diskLruCache.getFileSystem$okhttp().sink(getEntry$okhttp().getDirtyFiles$okhttp().get(index)), new DiskLruCache$Editor$newSink$1$1(diskLruCache, this));
                        return faultHidingSink;
                    } catch (FileNotFoundException e) {
                        return Okio.blackhole();
                    }
                }
            }
        }

        public final void commit() throws IOException {
            DiskLruCache diskLruCache = this.this$0;
            synchronized (diskLruCache) {
                if (!this.done) {
                    if (Intrinsics.areEqual((Object) getEntry$okhttp().getCurrentEditor$okhttp(), (Object) this)) {
                        diskLruCache.completeEdit$okhttp(this, true);
                    }
                    this.done = true;
                    Unit unit = Unit.INSTANCE;
                } else {
                    throw new IllegalStateException("Check failed.".toString());
                }
            }
        }

        public final void abort() throws IOException {
            DiskLruCache diskLruCache = this.this$0;
            synchronized (diskLruCache) {
                if (!this.done) {
                    if (Intrinsics.areEqual((Object) getEntry$okhttp().getCurrentEditor$okhttp(), (Object) this)) {
                        diskLruCache.completeEdit$okhttp(this, false);
                    }
                    this.done = true;
                    Unit unit = Unit.INSTANCE;
                } else {
                    throw new IllegalStateException("Check failed.".toString());
                }
            }
        }
    }

    @Metadata(d1 = {"\u0000v\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0016\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\b\n\u0002\u0010\u0001\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0004\u0018\u00002\u00020\u0001B\u000f\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0016\u0010.\u001a\u00020/2\f\u00100\u001a\b\u0012\u0004\u0012\u00020\u000301H\u0002J\u0010\u00102\u001a\u0002032\u0006\u00104\u001a\u00020\u001aH\u0002J\u001b\u00105\u001a\u0002062\f\u00100\u001a\b\u0012\u0004\u0012\u00020\u000301H\u0000¢\u0006\u0002\b7J\u0013\u00108\u001a\b\u0018\u000109R\u00020\fH\u0000¢\u0006\u0002\b:J\u0015\u0010;\u001a\u0002062\u0006\u0010<\u001a\u00020=H\u0000¢\u0006\u0002\b>R\u001a\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR \u0010\n\u001a\b\u0018\u00010\u000bR\u00020\fX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001a\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\tR\u0014\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0014\u0010\u0015\u001a\u00020\u0016X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u001a\u0010\u0019\u001a\u00020\u001aX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001eR\u001a\u0010\u001f\u001a\u00020 X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b!\u0010\"\"\u0004\b#\u0010$R\u001a\u0010%\u001a\u00020&X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b'\u0010(\"\u0004\b)\u0010*R\u001a\u0010+\u001a\u00020 X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b,\u0010\"\"\u0004\b-\u0010$¨\u0006?"}, d2 = {"Lokhttp3/internal/cache/DiskLruCache$Entry;", "", "key", "", "(Lokhttp3/internal/cache/DiskLruCache;Ljava/lang/String;)V", "cleanFiles", "", "Ljava/io/File;", "getCleanFiles$okhttp", "()Ljava/util/List;", "currentEditor", "Lokhttp3/internal/cache/DiskLruCache$Editor;", "Lokhttp3/internal/cache/DiskLruCache;", "getCurrentEditor$okhttp", "()Lokhttp3/internal/cache/DiskLruCache$Editor;", "setCurrentEditor$okhttp", "(Lokhttp3/internal/cache/DiskLruCache$Editor;)V", "dirtyFiles", "getDirtyFiles$okhttp", "getKey$okhttp", "()Ljava/lang/String;", "lengths", "", "getLengths$okhttp", "()[J", "lockingSourceCount", "", "getLockingSourceCount$okhttp", "()I", "setLockingSourceCount$okhttp", "(I)V", "readable", "", "getReadable$okhttp", "()Z", "setReadable$okhttp", "(Z)V", "sequenceNumber", "", "getSequenceNumber$okhttp", "()J", "setSequenceNumber$okhttp", "(J)V", "zombie", "getZombie$okhttp", "setZombie$okhttp", "invalidLengths", "", "strings", "", "newSource", "Lokio/Source;", "index", "setLengths", "", "setLengths$okhttp", "snapshot", "Lokhttp3/internal/cache/DiskLruCache$Snapshot;", "snapshot$okhttp", "writeLengths", "writer", "Lokio/BufferedSink;", "writeLengths$okhttp", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: DiskLruCache.kt */
    public final class Entry {
        private final List<File> cleanFiles = new ArrayList();
        private Editor currentEditor;
        private final List<File> dirtyFiles = new ArrayList();
        private final String key;
        private final long[] lengths;
        private int lockingSourceCount;
        private boolean readable;
        private long sequenceNumber;
        final /* synthetic */ DiskLruCache this$0;
        private boolean zombie;

        public Entry(DiskLruCache this$02, String key2) {
            Intrinsics.checkNotNullParameter(this$02, "this$0");
            Intrinsics.checkNotNullParameter(key2, "key");
            this.this$0 = this$02;
            this.key = key2;
            this.lengths = new long[this$02.getValueCount$okhttp()];
            StringBuilder fileBuilder = new StringBuilder(key2).append('.');
            int truncateTo = fileBuilder.length();
            int valueCount$okhttp = this$02.getValueCount$okhttp();
            int i = 0;
            while (i < valueCount$okhttp) {
                int i2 = i;
                i++;
                fileBuilder.append(i2);
                this.cleanFiles.add(new File(this.this$0.getDirectory(), fileBuilder.toString()));
                fileBuilder.append(".tmp");
                this.dirtyFiles.add(new File(this.this$0.getDirectory(), fileBuilder.toString()));
                fileBuilder.setLength(truncateTo);
            }
        }

        public final String getKey$okhttp() {
            return this.key;
        }

        public final long[] getLengths$okhttp() {
            return this.lengths;
        }

        public final List<File> getCleanFiles$okhttp() {
            return this.cleanFiles;
        }

        public final List<File> getDirtyFiles$okhttp() {
            return this.dirtyFiles;
        }

        public final boolean getReadable$okhttp() {
            return this.readable;
        }

        public final void setReadable$okhttp(boolean z) {
            this.readable = z;
        }

        public final boolean getZombie$okhttp() {
            return this.zombie;
        }

        public final void setZombie$okhttp(boolean z) {
            this.zombie = z;
        }

        public final Editor getCurrentEditor$okhttp() {
            return this.currentEditor;
        }

        public final void setCurrentEditor$okhttp(Editor editor) {
            this.currentEditor = editor;
        }

        public final int getLockingSourceCount$okhttp() {
            return this.lockingSourceCount;
        }

        public final void setLockingSourceCount$okhttp(int i) {
            this.lockingSourceCount = i;
        }

        public final long getSequenceNumber$okhttp() {
            return this.sequenceNumber;
        }

        public final void setSequenceNumber$okhttp(long j) {
            this.sequenceNumber = j;
        }

        public final void setLengths$okhttp(List<String> strings) throws IOException {
            Intrinsics.checkNotNullParameter(strings, "strings");
            if (strings.size() == this.this$0.getValueCount$okhttp()) {
                int i = 0;
                try {
                    int size = strings.size();
                    while (i < size) {
                        int i2 = i;
                        i++;
                        this.lengths[i2] = Long.parseLong(strings.get(i2));
                    }
                } catch (NumberFormatException e) {
                    invalidLengths(strings);
                    throw new KotlinNothingValueException();
                }
            } else {
                invalidLengths(strings);
                throw new KotlinNothingValueException();
            }
        }

        public final void writeLengths$okhttp(BufferedSink writer) throws IOException {
            Intrinsics.checkNotNullParameter(writer, "writer");
            long[] jArr = this.lengths;
            int length = jArr.length;
            int i = 0;
            while (i < length) {
                long length2 = jArr[i];
                i++;
                writer.writeByte(32).writeDecimalLong(length2);
            }
        }

        private final Void invalidLengths(List<String> strings) throws IOException {
            throw new IOException(Intrinsics.stringPlus("unexpected journal line: ", strings));
        }

        public final Snapshot snapshot$okhttp() {
            Object $this$assertThreadHoldsLock$iv = this.this$0;
            if (Util.assertionsEnabled && !Thread.holdsLock($this$assertThreadHoldsLock$iv)) {
                throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertThreadHoldsLock$iv);
            } else if (!this.readable) {
                return null;
            } else {
                if (!this.this$0.civilizedFileSystem && (this.currentEditor != null || this.zombie)) {
                    return null;
                }
                List<Source> sources = new ArrayList<>();
                long[] lengths2 = (long[]) this.lengths.clone();
                int i = 0;
                try {
                    int valueCount$okhttp = this.this$0.getValueCount$okhttp();
                    while (i < valueCount$okhttp) {
                        int i2 = i;
                        i++;
                        sources.add(newSource(i2));
                    }
                    return new Snapshot(this.this$0, this.key, this.sequenceNumber, sources, lengths2);
                } catch (FileNotFoundException e) {
                    for (Source source : sources) {
                        Util.closeQuietly((Closeable) source);
                    }
                    try {
                        this.this$0.removeEntry$okhttp(this);
                    } catch (IOException e2) {
                    }
                    return null;
                }
            }
        }

        private final Source newSource(int index) {
            Source fileSource = this.this$0.getFileSystem$okhttp().source(this.cleanFiles.get(index));
            if (this.this$0.civilizedFileSystem) {
                return fileSource;
            }
            this.lockingSourceCount++;
            return new DiskLruCache$Entry$newSource$1(fileSource, this.this$0, this);
        }
    }

    @Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u0010\u0010\u0003\u001a\u00020\u00048\u0006XD¢\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u00020\u00068\u0006XD¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u00020\u00068\u0006XD¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u00020\u00068\u0006XD¢\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u00020\u00068\u0006XD¢\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u00020\u00068\u0006XD¢\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u00020\f8\u0006X\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u00020\u00068\u0006XD¢\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u00020\u00068\u0006XD¢\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u00020\u00068\u0006XD¢\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u00020\u00068\u0006XD¢\u0006\u0002\n\u0000¨\u0006\u0011"}, d2 = {"Lokhttp3/internal/cache/DiskLruCache$Companion;", "", "()V", "ANY_SEQUENCE_NUMBER", "", "CLEAN", "", "DIRTY", "JOURNAL_FILE", "JOURNAL_FILE_BACKUP", "JOURNAL_FILE_TEMP", "LEGAL_KEY_PATTERN", "Lkotlin/text/Regex;", "MAGIC", "READ", "REMOVE", "VERSION_1", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: DiskLruCache.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }
}

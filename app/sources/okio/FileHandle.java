package okio;

import java.io.Closeable;
import java.io.IOException;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0012\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\b\b&\u0018\u00002\u00060\u0001j\u0002`\u0002:\u0002()B\r\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0002\u0010\u0005J\u0006\u0010\u000b\u001a\u00020\fJ\u0006\u0010\r\u001a\u00020\u000eJ\u0006\u0010\u000f\u001a\u00020\u000eJ\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\fJ\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u0014J\b\u0010\u0015\u001a\u00020\u000eH$J\b\u0010\u0016\u001a\u00020\u000eH$J(\u0010\u0017\u001a\u00020\b2\u0006\u0010\u0018\u001a\u00020\u00112\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\b2\u0006\u0010\u001c\u001a\u00020\bH$J\u0010\u0010\u001d\u001a\u00020\u000e2\u0006\u0010\u001e\u001a\u00020\u0011H$J\b\u0010\u001f\u001a\u00020\u0011H$J(\u0010 \u001a\u00020\u000e2\u0006\u0010\u0018\u001a\u00020\u00112\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\b2\u0006\u0010\u001c\u001a\u00020\bH$J&\u0010!\u001a\u00020\b2\u0006\u0010\u0018\u001a\u00020\u00112\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\b2\u0006\u0010\u001c\u001a\u00020\bJ\u001e\u0010!\u001a\u00020\u00112\u0006\u0010\u0018\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\"2\u0006\u0010\u001c\u001a\u00020\u0011J \u0010#\u001a\u00020\u00112\u0006\u0010\u0018\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\"2\u0006\u0010\u001c\u001a\u00020\u0011H\u0002J\u0016\u0010$\u001a\u00020\u000e2\u0006\u0010\u0012\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\u0011J\u0016\u0010$\u001a\u00020\u000e2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0010\u001a\u00020\u0011J\u000e\u0010%\u001a\u00020\u000e2\u0006\u0010\u001e\u001a\u00020\u0011J\u0010\u0010\u0012\u001a\u00020\f2\b\b\u0002\u0010\u0018\u001a\u00020\u0011J\u0006\u0010\u001e\u001a\u00020\u0011J\u0010\u0010\u0013\u001a\u00020\u00142\b\b\u0002\u0010\u0018\u001a\u00020\u0011J&\u0010&\u001a\u00020\u000e2\u0006\u0010\u0018\u001a\u00020\u00112\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\b2\u0006\u0010\u001c\u001a\u00020\bJ\u001e\u0010&\u001a\u00020\u000e2\u0006\u0010\u0018\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\"2\u0006\u0010\u001c\u001a\u00020\u0011J \u0010'\u001a\u00020\u000e2\u0006\u0010\u0018\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\"2\u0006\u0010\u001c\u001a\u00020\u0011H\u0002R\u000e\u0010\u0006\u001a\u00020\u0004X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n¨\u0006*"}, d2 = {"Lokio/FileHandle;", "Ljava/io/Closeable;", "Lokio/Closeable;", "readWrite", "", "(Z)V", "closed", "openStreamCount", "", "getReadWrite", "()Z", "appendingSink", "Lokio/Sink;", "close", "", "flush", "position", "", "sink", "source", "Lokio/Source;", "protectedClose", "protectedFlush", "protectedRead", "fileOffset", "array", "", "arrayOffset", "byteCount", "protectedResize", "size", "protectedSize", "protectedWrite", "read", "Lokio/Buffer;", "readNoCloseCheck", "reposition", "resize", "write", "writeNoCloseCheck", "FileHandleSink", "FileHandleSource", "okio"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* compiled from: FileHandle.kt */
public abstract class FileHandle implements Closeable {
    /* access modifiers changed from: private */
    public boolean closed;
    /* access modifiers changed from: private */
    public int openStreamCount;
    private final boolean readWrite;

    /* access modifiers changed from: protected */
    public abstract void protectedClose() throws IOException;

    /* access modifiers changed from: protected */
    public abstract void protectedFlush() throws IOException;

    /* access modifiers changed from: protected */
    public abstract int protectedRead(long j, byte[] bArr, int i, int i2) throws IOException;

    /* access modifiers changed from: protected */
    public abstract void protectedResize(long j) throws IOException;

    /* access modifiers changed from: protected */
    public abstract long protectedSize() throws IOException;

    /* access modifiers changed from: protected */
    public abstract void protectedWrite(long j, byte[] bArr, int i, int i2) throws IOException;

    public FileHandle(boolean readWrite2) {
        this.readWrite = readWrite2;
    }

    public final boolean getReadWrite() {
        return this.readWrite;
    }

    public final int read(long fileOffset, byte[] array, int arrayOffset, int byteCount) throws IOException {
        Intrinsics.checkNotNullParameter(array, "array");
        synchronized (this) {
            if (!this.closed) {
                Unit unit = Unit.INSTANCE;
            } else {
                throw new IllegalStateException("closed".toString());
            }
        }
        return protectedRead(fileOffset, array, arrayOffset, byteCount);
    }

    public final long read(long fileOffset, Buffer sink, long byteCount) throws IOException {
        Intrinsics.checkNotNullParameter(sink, "sink");
        synchronized (this) {
            if (!this.closed) {
                Unit unit = Unit.INSTANCE;
            } else {
                throw new IllegalStateException("closed".toString());
            }
        }
        return readNoCloseCheck(fileOffset, sink, byteCount);
    }

    public final long size() throws IOException {
        synchronized (this) {
            if (!this.closed) {
                Unit unit = Unit.INSTANCE;
            } else {
                throw new IllegalStateException("closed".toString());
            }
        }
        return protectedSize();
    }

    public final void resize(long size) throws IOException {
        if (this.readWrite) {
            synchronized (this) {
                if (!this.closed) {
                    Unit unit = Unit.INSTANCE;
                } else {
                    throw new IllegalStateException("closed".toString());
                }
            }
            protectedResize(size);
            return;
        }
        throw new IllegalStateException("file handle is read-only".toString());
    }

    public final void write(long fileOffset, byte[] array, int arrayOffset, int byteCount) {
        Intrinsics.checkNotNullParameter(array, "array");
        if (this.readWrite) {
            synchronized (this) {
                if (!this.closed) {
                    Unit unit = Unit.INSTANCE;
                } else {
                    throw new IllegalStateException("closed".toString());
                }
            }
            protectedWrite(fileOffset, array, arrayOffset, byteCount);
            return;
        }
        throw new IllegalStateException("file handle is read-only".toString());
    }

    public final void write(long fileOffset, Buffer source, long byteCount) throws IOException {
        Intrinsics.checkNotNullParameter(source, "source");
        if (this.readWrite) {
            synchronized (this) {
                if (!this.closed) {
                    Unit unit = Unit.INSTANCE;
                } else {
                    throw new IllegalStateException("closed".toString());
                }
            }
            writeNoCloseCheck(fileOffset, source, byteCount);
            return;
        }
        throw new IllegalStateException("file handle is read-only".toString());
    }

    public final void flush() throws IOException {
        if (this.readWrite) {
            synchronized (this) {
                if (!this.closed) {
                    Unit unit = Unit.INSTANCE;
                } else {
                    throw new IllegalStateException("closed".toString());
                }
            }
            protectedFlush();
            return;
        }
        throw new IllegalStateException("file handle is read-only".toString());
    }

    public static /* synthetic */ Source source$default(FileHandle fileHandle, long j, int i, Object obj) throws IOException {
        if (obj == null) {
            if ((i & 1) != 0) {
                j = 0;
            }
            return fileHandle.source(j);
        }
        throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: source");
    }

    public final Source source(long fileOffset) throws IOException {
        synchronized (this) {
            if (!this.closed) {
                this.openStreamCount++;
            } else {
                throw new IllegalStateException("closed".toString());
            }
        }
        return new FileHandleSource(this, fileOffset);
    }

    public final long position(Source source) throws IOException {
        Intrinsics.checkNotNullParameter(source, "source");
        Source source2 = source;
        long bufferSize = 0;
        if (source2 instanceof RealBufferedSource) {
            bufferSize = ((RealBufferedSource) source2).bufferField.size();
            source2 = ((RealBufferedSource) source2).source;
        }
        if (!((source2 instanceof FileHandleSource) && ((FileHandleSource) source2).getFileHandle() == this)) {
            throw new IllegalArgumentException("source was not created by this FileHandle".toString());
        } else if (!((FileHandleSource) source2).getClosed()) {
            return ((FileHandleSource) source2).getPosition() - bufferSize;
        } else {
            throw new IllegalStateException("closed".toString());
        }
    }

    public final void reposition(Source source, long position) throws IOException {
        Intrinsics.checkNotNullParameter(source, "source");
        boolean z = false;
        if (source instanceof RealBufferedSource) {
            Source fileHandleSource = ((RealBufferedSource) source).source;
            if (!((fileHandleSource instanceof FileHandleSource) && ((FileHandleSource) fileHandleSource).getFileHandle() == this)) {
                throw new IllegalArgumentException("source was not created by this FileHandle".toString());
            } else if (!((FileHandleSource) fileHandleSource).getClosed()) {
                long bufferSize = ((RealBufferedSource) source).bufferField.size();
                long toSkip = position - (((FileHandleSource) fileHandleSource).getPosition() - bufferSize);
                if (0 <= toSkip && toSkip < bufferSize) {
                    z = true;
                }
                if (z) {
                    ((RealBufferedSource) source).skip(toSkip);
                    return;
                }
                ((RealBufferedSource) source).bufferField.clear();
                ((FileHandleSource) fileHandleSource).setPosition(position);
            } else {
                throw new IllegalStateException("closed".toString());
            }
        } else {
            if ((source instanceof FileHandleSource) && ((FileHandleSource) source).getFileHandle() == this) {
                z = true;
            }
            if (!z) {
                throw new IllegalArgumentException("source was not created by this FileHandle".toString());
            } else if (!((FileHandleSource) source).getClosed()) {
                ((FileHandleSource) source).setPosition(position);
            } else {
                throw new IllegalStateException("closed".toString());
            }
        }
    }

    public static /* synthetic */ Sink sink$default(FileHandle fileHandle, long j, int i, Object obj) throws IOException {
        if (obj == null) {
            if ((i & 1) != 0) {
                j = 0;
            }
            return fileHandle.sink(j);
        }
        throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: sink");
    }

    public final Sink sink(long fileOffset) throws IOException {
        if (this.readWrite) {
            synchronized (this) {
                if (!this.closed) {
                    this.openStreamCount++;
                } else {
                    throw new IllegalStateException("closed".toString());
                }
            }
            return new FileHandleSink(this, fileOffset);
        }
        throw new IllegalStateException("file handle is read-only".toString());
    }

    public final Sink appendingSink() throws IOException {
        return sink(size());
    }

    public final long position(Sink sink) throws IOException {
        Intrinsics.checkNotNullParameter(sink, "sink");
        Sink sink2 = sink;
        long bufferSize = 0;
        if (sink2 instanceof RealBufferedSink) {
            bufferSize = ((RealBufferedSink) sink2).bufferField.size();
            sink2 = ((RealBufferedSink) sink2).sink;
        }
        if (!((sink2 instanceof FileHandleSink) && ((FileHandleSink) sink2).getFileHandle() == this)) {
            throw new IllegalArgumentException("sink was not created by this FileHandle".toString());
        } else if (!((FileHandleSink) sink2).getClosed()) {
            return ((FileHandleSink) sink2).getPosition() + bufferSize;
        } else {
            throw new IllegalStateException("closed".toString());
        }
    }

    public final void reposition(Sink sink, long position) throws IOException {
        Intrinsics.checkNotNullParameter(sink, "sink");
        boolean z = false;
        if (sink instanceof RealBufferedSink) {
            Sink fileHandleSink = ((RealBufferedSink) sink).sink;
            if ((fileHandleSink instanceof FileHandleSink) && ((FileHandleSink) fileHandleSink).getFileHandle() == this) {
                z = true;
            }
            if (!z) {
                throw new IllegalArgumentException("sink was not created by this FileHandle".toString());
            } else if (!((FileHandleSink) fileHandleSink).getClosed()) {
                ((RealBufferedSink) sink).emit();
                ((FileHandleSink) fileHandleSink).setPosition(position);
            } else {
                throw new IllegalStateException("closed".toString());
            }
        } else {
            if ((sink instanceof FileHandleSink) && ((FileHandleSink) sink).getFileHandle() == this) {
                z = true;
            }
            if (!z) {
                throw new IllegalArgumentException("sink was not created by this FileHandle".toString());
            } else if (!((FileHandleSink) sink).getClosed()) {
                ((FileHandleSink) sink).setPosition(position);
            } else {
                throw new IllegalStateException("closed".toString());
            }
        }
    }

    public final void close() throws IOException {
        synchronized (this) {
            if (!this.closed) {
                this.closed = true;
                if (this.openStreamCount == 0) {
                    Unit unit = Unit.INSTANCE;
                    protectedClose();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public final long readNoCloseCheck(long fileOffset, Buffer sink, long byteCount) {
        Buffer buffer = sink;
        if (byteCount >= 0) {
            long currentOffset = fileOffset;
            long targetOffset = fileOffset + byteCount;
            while (true) {
                if (currentOffset >= targetOffset) {
                    break;
                }
                Segment tail = buffer.writableSegment$okio(1);
                int readByteCount = protectedRead(currentOffset, tail.data, tail.limit, (int) Math.min(targetOffset - currentOffset, (long) (8192 - tail.limit)));
                if (readByteCount == -1) {
                    if (tail.pos == tail.limit) {
                        buffer.head = tail.pop();
                        SegmentPool.recycle(tail);
                    }
                    if (fileOffset == currentOffset) {
                        return -1;
                    }
                } else {
                    tail.limit += readByteCount;
                    currentOffset += (long) readByteCount;
                    buffer.setSize$okio(sink.size() + ((long) readByteCount));
                }
            }
            return currentOffset - fileOffset;
        }
        throw new IllegalArgumentException(Intrinsics.stringPlus("byteCount < 0: ", Long.valueOf(byteCount)).toString());
    }

    /* access modifiers changed from: private */
    public final void writeNoCloseCheck(long fileOffset, Buffer source, long byteCount) {
        Buffer buffer = source;
        _UtilKt.checkOffsetAndCount(source.size(), 0, byteCount);
        long currentOffset = fileOffset;
        long targetOffset = fileOffset + byteCount;
        while (currentOffset < targetOffset) {
            Segment segment = buffer.head;
            Intrinsics.checkNotNull(segment);
            Segment head = segment;
            int toCopy = (int) Math.min(targetOffset - currentOffset, (long) (head.limit - head.pos));
            protectedWrite(currentOffset, head.data, head.pos, toCopy);
            head.pos += toCopy;
            currentOffset += (long) toCopy;
            buffer.setSize$okio(source.size() - ((long) toCopy));
            if (head.pos == head.limit) {
                buffer.head = head.pop();
                SegmentPool.recycle(head);
            }
        }
    }

    @Metadata(d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\b\u0010\u0013\u001a\u00020\u0014H\u0016J\b\u0010\u0015\u001a\u00020\u0014H\u0016J\b\u0010\u0016\u001a\u00020\u0017H\u0016J\u0018\u0010\u0018\u001a\u00020\u00142\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u0005H\u0016R\u001a\u0010\u0007\u001a\u00020\bX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u001a\u0010\u0004\u001a\u00020\u0005X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012¨\u0006\u001c"}, d2 = {"Lokio/FileHandle$FileHandleSink;", "Lokio/Sink;", "fileHandle", "Lokio/FileHandle;", "position", "", "(Lokio/FileHandle;J)V", "closed", "", "getClosed", "()Z", "setClosed", "(Z)V", "getFileHandle", "()Lokio/FileHandle;", "getPosition", "()J", "setPosition", "(J)V", "close", "", "flush", "timeout", "Lokio/Timeout;", "write", "source", "Lokio/Buffer;", "byteCount", "okio"}, k = 1, mv = {1, 5, 1}, xi = 48)
    /* compiled from: FileHandle.kt */
    private static final class FileHandleSink implements Sink {
        private boolean closed;
        private final FileHandle fileHandle;
        private long position;

        public FileHandleSink(FileHandle fileHandle2, long position2) {
            Intrinsics.checkNotNullParameter(fileHandle2, "fileHandle");
            this.fileHandle = fileHandle2;
            this.position = position2;
        }

        public final FileHandle getFileHandle() {
            return this.fileHandle;
        }

        public final long getPosition() {
            return this.position;
        }

        public final void setPosition(long j) {
            this.position = j;
        }

        public final boolean getClosed() {
            return this.closed;
        }

        public final void setClosed(boolean z) {
            this.closed = z;
        }

        public void write(Buffer source, long byteCount) {
            Intrinsics.checkNotNullParameter(source, "source");
            if (!this.closed) {
                this.fileHandle.writeNoCloseCheck(this.position, source, byteCount);
                this.position += byteCount;
                return;
            }
            throw new IllegalStateException("closed".toString());
        }

        public void flush() {
            if (!this.closed) {
                this.fileHandle.protectedFlush();
                return;
            }
            throw new IllegalStateException("closed".toString());
        }

        public Timeout timeout() {
            return Timeout.NONE;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:17:0x003b, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void close() {
            /*
                r5 = this;
                boolean r0 = r5.closed
                if (r0 == 0) goto L_0x0005
                return
            L_0x0005:
                r0 = 1
                r5.closed = r0
                okio.FileHandle r0 = r5.fileHandle
                r1 = 0
                monitor-enter(r0)
                r2 = 0
                okio.FileHandle r3 = r5.getFileHandle()     // Catch:{ all -> 0x003c }
                int r4 = r3.openStreamCount     // Catch:{ all -> 0x003c }
                int r4 = r4 + -1
                r3.openStreamCount = r4     // Catch:{ all -> 0x003c }
                okio.FileHandle r3 = r5.getFileHandle()     // Catch:{ all -> 0x003c }
                int r3 = r3.openStreamCount     // Catch:{ all -> 0x003c }
                if (r3 != 0) goto L_0x003a
                okio.FileHandle r3 = r5.getFileHandle()     // Catch:{ all -> 0x003c }
                boolean r3 = r3.closed     // Catch:{ all -> 0x003c }
                if (r3 != 0) goto L_0x002f
                goto L_0x003a
            L_0x002f:
                kotlin.Unit r2 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x003c }
                monitor-exit(r0)
                okio.FileHandle r0 = r5.fileHandle
                r0.protectedClose()
                return
            L_0x003a:
                monitor-exit(r0)
                return
            L_0x003c:
                r2 = move-exception
                monitor-exit(r0)
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: okio.FileHandle.FileHandleSink.close():void");
        }
    }

    @Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\b\u0010\u0013\u001a\u00020\u0014H\u0016J\u0018\u0010\u0015\u001a\u00020\u00052\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0005H\u0016J\b\u0010\u0019\u001a\u00020\u001aH\u0016R\u001a\u0010\u0007\u001a\u00020\bX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u001a\u0010\u0004\u001a\u00020\u0005X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012¨\u0006\u001b"}, d2 = {"Lokio/FileHandle$FileHandleSource;", "Lokio/Source;", "fileHandle", "Lokio/FileHandle;", "position", "", "(Lokio/FileHandle;J)V", "closed", "", "getClosed", "()Z", "setClosed", "(Z)V", "getFileHandle", "()Lokio/FileHandle;", "getPosition", "()J", "setPosition", "(J)V", "close", "", "read", "sink", "Lokio/Buffer;", "byteCount", "timeout", "Lokio/Timeout;", "okio"}, k = 1, mv = {1, 5, 1}, xi = 48)
    /* compiled from: FileHandle.kt */
    private static final class FileHandleSource implements Source {
        private boolean closed;
        private final FileHandle fileHandle;
        private long position;

        public FileHandleSource(FileHandle fileHandle2, long position2) {
            Intrinsics.checkNotNullParameter(fileHandle2, "fileHandle");
            this.fileHandle = fileHandle2;
            this.position = position2;
        }

        public final FileHandle getFileHandle() {
            return this.fileHandle;
        }

        public final long getPosition() {
            return this.position;
        }

        public final void setPosition(long j) {
            this.position = j;
        }

        public final boolean getClosed() {
            return this.closed;
        }

        public final void setClosed(boolean z) {
            this.closed = z;
        }

        public long read(Buffer sink, long byteCount) {
            Intrinsics.checkNotNullParameter(sink, "sink");
            if (!this.closed) {
                long result = this.fileHandle.readNoCloseCheck(this.position, sink, byteCount);
                if (result != -1) {
                    this.position += result;
                }
                return result;
            }
            throw new IllegalStateException("closed".toString());
        }

        public Timeout timeout() {
            return Timeout.NONE;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:17:0x003b, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void close() {
            /*
                r5 = this;
                boolean r0 = r5.closed
                if (r0 == 0) goto L_0x0005
                return
            L_0x0005:
                r0 = 1
                r5.closed = r0
                okio.FileHandle r0 = r5.fileHandle
                r1 = 0
                monitor-enter(r0)
                r2 = 0
                okio.FileHandle r3 = r5.getFileHandle()     // Catch:{ all -> 0x003c }
                int r4 = r3.openStreamCount     // Catch:{ all -> 0x003c }
                int r4 = r4 + -1
                r3.openStreamCount = r4     // Catch:{ all -> 0x003c }
                okio.FileHandle r3 = r5.getFileHandle()     // Catch:{ all -> 0x003c }
                int r3 = r3.openStreamCount     // Catch:{ all -> 0x003c }
                if (r3 != 0) goto L_0x003a
                okio.FileHandle r3 = r5.getFileHandle()     // Catch:{ all -> 0x003c }
                boolean r3 = r3.closed     // Catch:{ all -> 0x003c }
                if (r3 != 0) goto L_0x002f
                goto L_0x003a
            L_0x002f:
                kotlin.Unit r2 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x003c }
                monitor-exit(r0)
                okio.FileHandle r0 = r5.fileHandle
                r0.protectedClose()
                return
            L_0x003a:
                monitor-exit(r0)
                return
            L_0x003c:
                r2 = move-exception
                monitor-exit(r0)
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: okio.FileHandle.FileHandleSource.close():void");
        }
    }
}

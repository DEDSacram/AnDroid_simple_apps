package okio;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okio.internal._PathKt;

@Metadata(d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u000f\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0010\f\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u0000\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 .2\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001.B\u000f\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0011\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u0000H\u0002J\u0016\u0010 \u001a\u00020\u00002\u0006\u0010!\u001a\u00020\rH\u0002¢\u0006\u0002\b\"J\u0016\u0010 \u001a\u00020\u00002\u0006\u0010!\u001a\u00020\u0003H\u0002¢\u0006\u0002\b\"J\u0016\u0010 \u001a\u00020\u00002\u0006\u0010!\u001a\u00020\u0000H\u0002¢\u0006\u0002\b\"J\u0013\u0010#\u001a\u00020\b2\b\u0010\u001f\u001a\u0004\u0018\u00010$H\u0002J\b\u0010%\u001a\u00020\u001eH\u0016J\u0006\u0010&\u001a\u00020\u0000J\u000e\u0010'\u001a\u00020\u00002\u0006\u0010\u001f\u001a\u00020\u0000J\u0018\u0010\"\u001a\u00020\u00002\u0006\u0010!\u001a\u00020\r2\b\b\u0002\u0010(\u001a\u00020\bJ\u0018\u0010\"\u001a\u00020\u00002\u0006\u0010!\u001a\u00020\u00032\b\b\u0002\u0010(\u001a\u00020\bJ\u0018\u0010\"\u001a\u00020\u00002\u0006\u0010!\u001a\u00020\u00002\b\b\u0002\u0010(\u001a\u00020\bJ\u0006\u0010)\u001a\u00020*J\b\u0010+\u001a\u00020,H\u0007J\b\u0010-\u001a\u00020\rH\u0016R\u0014\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u0011\u0010\u0007\u001a\u00020\b8F¢\u0006\u0006\u001a\u0004\b\u0007\u0010\tR\u0011\u0010\n\u001a\u00020\b8F¢\u0006\u0006\u001a\u0004\b\n\u0010\tR\u0011\u0010\u000b\u001a\u00020\b8F¢\u0006\u0006\u001a\u0004\b\u000b\u0010\tR\u0011\u0010\f\u001a\u00020\r8G¢\u0006\u0006\u001a\u0004\b\f\u0010\u000eR\u0011\u0010\u000f\u001a\u00020\u00038G¢\u0006\u0006\u001a\u0004\b\u000f\u0010\u0006R\u0013\u0010\u0010\u001a\u0004\u0018\u00010\u00008G¢\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011R\u0013\u0010\u0012\u001a\u0004\u0018\u00010\u00008F¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0011R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\r0\u00158F¢\u0006\u0006\u001a\u0004\b\u0016\u0010\u0017R\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00030\u00158F¢\u0006\u0006\u001a\u0004\b\u0019\u0010\u0017R\u0013\u0010\u001a\u001a\u0004\u0018\u00010\u001b8G¢\u0006\u0006\u001a\u0004\b\u001a\u0010\u001c¨\u0006/"}, d2 = {"Lokio/Path;", "", "bytes", "Lokio/ByteString;", "(Lokio/ByteString;)V", "getBytes$okio", "()Lokio/ByteString;", "isAbsolute", "", "()Z", "isRelative", "isRoot", "name", "", "()Ljava/lang/String;", "nameBytes", "parent", "()Lokio/Path;", "root", "getRoot", "segments", "", "getSegments", "()Ljava/util/List;", "segmentsBytes", "getSegmentsBytes", "volumeLetter", "", "()Ljava/lang/Character;", "compareTo", "", "other", "div", "child", "resolve", "equals", "", "hashCode", "normalized", "relativeTo", "normalize", "toFile", "Ljava/io/File;", "toNioPath", "Ljava/nio/file/Path;", "toString", "Companion", "okio"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* compiled from: Path.kt */
public final class Path implements Comparable<Path> {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    public static final String DIRECTORY_SEPARATOR;
    private final ByteString bytes;

    @JvmStatic
    public static final Path get(File file) {
        return Companion.get(file);
    }

    @JvmStatic
    public static final Path get(File file, boolean z) {
        return Companion.get(file, z);
    }

    @JvmStatic
    public static final Path get(String str) {
        return Companion.get(str);
    }

    @JvmStatic
    public static final Path get(String str, boolean z) {
        return Companion.get(str, z);
    }

    @JvmStatic
    public static final Path get(java.nio.file.Path path) {
        return Companion.get(path);
    }

    @JvmStatic
    public static final Path get(java.nio.file.Path path, boolean z) {
        return Companion.get(path, z);
    }

    public Path(ByteString bytes2) {
        Intrinsics.checkNotNullParameter(bytes2, "bytes");
        this.bytes = bytes2;
    }

    public final ByteString getBytes$okio() {
        return this.bytes;
    }

    public final Path getRoot() {
        int rootLength$iv = _PathKt.rootLength(this);
        if (rootLength$iv == -1) {
            return null;
        }
        return new Path(getBytes$okio().substring(0, rootLength$iv));
    }

    public final List<String> getSegments() {
        List result$iv$iv = new ArrayList();
        int segmentStart$iv$iv = _PathKt.rootLength(this);
        if (segmentStart$iv$iv == -1) {
            segmentStart$iv$iv = 0;
        } else if (segmentStart$iv$iv < getBytes$okio().size() && getBytes$okio().getByte(segmentStart$iv$iv) == ((byte) 92)) {
            segmentStart$iv$iv++;
        }
        int size = getBytes$okio().size();
        if (segmentStart$iv$iv < size) {
            int segmentStart$iv$iv2 = segmentStart$iv$iv;
            do {
                int i$iv$iv = segmentStart$iv$iv;
                segmentStart$iv$iv++;
                if (getBytes$okio().getByte(i$iv$iv) == ((byte) 47) || getBytes$okio().getByte(i$iv$iv) == ((byte) 92)) {
                    result$iv$iv.add(getBytes$okio().substring(segmentStart$iv$iv2, i$iv$iv));
                    segmentStart$iv$iv2 = i$iv$iv + 1;
                    continue;
                }
            } while (segmentStart$iv$iv < size);
            segmentStart$iv$iv = segmentStart$iv$iv2;
        }
        if (segmentStart$iv$iv < getBytes$okio().size()) {
            result$iv$iv.add(getBytes$okio().substring(segmentStart$iv$iv, getBytes$okio().size()));
        }
        Iterable<ByteString> $this$map$iv$iv = result$iv$iv;
        Collection destination$iv$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv$iv, 10));
        for (ByteString it$iv : $this$map$iv$iv) {
            destination$iv$iv$iv.add(it$iv.utf8());
        }
        return (List) destination$iv$iv$iv;
    }

    public final List<ByteString> getSegmentsBytes() {
        List<ByteString> arrayList = new ArrayList<>();
        int segmentStart$iv = _PathKt.rootLength(this);
        if (segmentStart$iv == -1) {
            segmentStart$iv = 0;
        } else if (segmentStart$iv < getBytes$okio().size() && getBytes$okio().getByte(segmentStart$iv) == ((byte) 92)) {
            segmentStart$iv++;
        }
        int size = getBytes$okio().size();
        if (segmentStart$iv < size) {
            int segmentStart$iv2 = segmentStart$iv;
            do {
                int i$iv = segmentStart$iv;
                segmentStart$iv++;
                if (getBytes$okio().getByte(i$iv) == ((byte) 47) || getBytes$okio().getByte(i$iv) == ((byte) 92)) {
                    arrayList.add(getBytes$okio().substring(segmentStart$iv2, i$iv));
                    segmentStart$iv2 = i$iv + 1;
                    continue;
                }
            } while (segmentStart$iv < size);
            segmentStart$iv = segmentStart$iv2;
        }
        if (segmentStart$iv < getBytes$okio().size()) {
            arrayList.add(getBytes$okio().substring(segmentStart$iv, getBytes$okio().size()));
        }
        return arrayList;
    }

    public final boolean isAbsolute() {
        return _PathKt.rootLength(this) != -1;
    }

    public final boolean isRelative() {
        return _PathKt.rootLength(this) == -1;
    }

    public final Character volumeLetter() {
        boolean z = false;
        if (ByteString.indexOf$default(getBytes$okio(), _PathKt.SLASH, 0, 2, (Object) null) != -1 || getBytes$okio().size() < 2 || getBytes$okio().getByte(1) != ((byte) 58)) {
            return null;
        }
        char c$iv = (char) getBytes$okio().getByte(0);
        if (!('a' <= c$iv && c$iv <= 'z')) {
            if ('A' <= c$iv && c$iv <= 'Z') {
                z = true;
            }
            if (!z) {
                return null;
            }
        }
        return Character.valueOf(c$iv);
    }

    public final ByteString nameBytes() {
        int lastSlash$iv = _PathKt.getIndexOfLastSlash(this);
        if (lastSlash$iv != -1) {
            return ByteString.substring$default(getBytes$okio(), lastSlash$iv + 1, 0, 2, (Object) null);
        }
        if (volumeLetter() == null || getBytes$okio().size() != 2) {
            return getBytes$okio();
        }
        return ByteString.EMPTY;
    }

    public final String name() {
        return nameBytes().utf8();
    }

    public final Path parent() {
        if (Intrinsics.areEqual((Object) getBytes$okio(), (Object) _PathKt.DOT) || Intrinsics.areEqual((Object) getBytes$okio(), (Object) _PathKt.SLASH) || Intrinsics.areEqual((Object) getBytes$okio(), (Object) _PathKt.BACKSLASH) || _PathKt.lastSegmentIsDotDot(this)) {
            return null;
        }
        int lastSlash$iv = _PathKt.getIndexOfLastSlash(this);
        if (lastSlash$iv != 2 || volumeLetter() == null) {
            if (lastSlash$iv == 1 && getBytes$okio().startsWith(_PathKt.BACKSLASH)) {
                return null;
            }
            if (lastSlash$iv != -1 || volumeLetter() == null) {
                if (lastSlash$iv == -1) {
                    return new Path(_PathKt.DOT);
                }
                if (lastSlash$iv == 0) {
                    return new Path(ByteString.substring$default(getBytes$okio(), 0, 1, 1, (Object) null));
                }
                return new Path(ByteString.substring$default(getBytes$okio(), 0, lastSlash$iv, 1, (Object) null));
            } else if (getBytes$okio().size() == 2) {
                return null;
            } else {
                return new Path(ByteString.substring$default(getBytes$okio(), 0, 2, 1, (Object) null));
            }
        } else if (getBytes$okio().size() == 3) {
            return null;
        } else {
            return new Path(ByteString.substring$default(getBytes$okio(), 0, 3, 1, (Object) null));
        }
    }

    public final boolean isRoot() {
        return _PathKt.rootLength(this) == getBytes$okio().size();
    }

    public final Path resolve(String child) {
        Intrinsics.checkNotNullParameter(child, "child");
        return _PathKt.commonResolve(this, _PathKt.toPath(new Buffer().writeUtf8(child), false), false);
    }

    public final Path resolve(ByteString child) {
        Intrinsics.checkNotNullParameter(child, "child");
        return _PathKt.commonResolve(this, _PathKt.toPath(new Buffer().write(child), false), false);
    }

    public final Path resolve(Path child) {
        Intrinsics.checkNotNullParameter(child, "child");
        return _PathKt.commonResolve(this, child, false);
    }

    public static /* synthetic */ Path resolve$default(Path path, String str, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return path.resolve(str, z);
    }

    public final Path resolve(String child, boolean normalize) {
        Intrinsics.checkNotNullParameter(child, "child");
        return _PathKt.commonResolve(this, _PathKt.toPath(new Buffer().writeUtf8(child), false), normalize);
    }

    public static /* synthetic */ Path resolve$default(Path path, ByteString byteString, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return path.resolve(byteString, z);
    }

    public final Path resolve(ByteString child, boolean normalize) {
        Intrinsics.checkNotNullParameter(child, "child");
        return _PathKt.commonResolve(this, _PathKt.toPath(new Buffer().write(child), false), normalize);
    }

    public static /* synthetic */ Path resolve$default(Path path, Path path2, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return path.resolve(path2, z);
    }

    public final Path resolve(Path child, boolean normalize) {
        Intrinsics.checkNotNullParameter(child, "child");
        return _PathKt.commonResolve(this, child, normalize);
    }

    public final Path relativeTo(Path other) {
        Intrinsics.checkNotNullParameter(other, "other");
        if (Intrinsics.areEqual((Object) getRoot(), (Object) other.getRoot())) {
            List thisSegments$iv = getSegmentsBytes();
            List otherSegments$iv = other.getSegmentsBytes();
            int firstNewSegmentIndex$iv = 0;
            int minSegmentsSize$iv = Math.min(thisSegments$iv.size(), otherSegments$iv.size());
            while (firstNewSegmentIndex$iv < minSegmentsSize$iv && Intrinsics.areEqual((Object) thisSegments$iv.get(firstNewSegmentIndex$iv), (Object) otherSegments$iv.get(firstNewSegmentIndex$iv))) {
                firstNewSegmentIndex$iv++;
            }
            if (firstNewSegmentIndex$iv == minSegmentsSize$iv && getBytes$okio().size() == other.getBytes$okio().size()) {
                return Companion.get$default(Companion, ".", false, 1, (Object) null);
            }
            if (otherSegments$iv.subList(firstNewSegmentIndex$iv, otherSegments$iv.size()).indexOf(_PathKt.DOT_DOT) == -1) {
                Buffer buffer$iv = new Buffer();
                ByteString slash$iv = _PathKt.getSlash(other);
                if (slash$iv == null && (slash$iv = _PathKt.getSlash(this)) == null) {
                    slash$iv = _PathKt.toSlash(DIRECTORY_SEPARATOR);
                }
                int size = otherSegments$iv.size();
                if (firstNewSegmentIndex$iv < size) {
                    int i = firstNewSegmentIndex$iv;
                    do {
                        int i2 = i;
                        i++;
                        buffer$iv.write(_PathKt.DOT_DOT);
                        buffer$iv.write(slash$iv);
                    } while (i < size);
                }
                int size2 = thisSegments$iv.size();
                if (firstNewSegmentIndex$iv < size2) {
                    int i3 = firstNewSegmentIndex$iv;
                    do {
                        int i$iv = i3;
                        i3++;
                        buffer$iv.write(thisSegments$iv.get(i$iv));
                        buffer$iv.write(slash$iv);
                    } while (i3 < size2);
                }
                return _PathKt.toPath(buffer$iv, false);
            }
            throw new IllegalArgumentException(("Impossible relative path to resolve: " + this + " and " + other).toString());
        }
        throw new IllegalArgumentException(("Paths of different roots cannot be relative to each other: " + this + " and " + other).toString());
    }

    public final Path normalized() {
        return Companion.get(toString(), true);
    }

    public final File toFile() {
        return new File(toString());
    }

    public final java.nio.file.Path toNioPath() {
        java.nio.file.Path path = Paths.get(toString(), new String[0]);
        Intrinsics.checkNotNullExpressionValue(path, "get(toString())");
        return path;
    }

    public int compareTo(Path other) {
        Intrinsics.checkNotNullParameter(other, "other");
        return getBytes$okio().compareTo(other.getBytes$okio());
    }

    public boolean equals(Object other) {
        return (other instanceof Path) && Intrinsics.areEqual((Object) ((Path) other).getBytes$okio(), (Object) getBytes$okio());
    }

    public int hashCode() {
        return getBytes$okio().hashCode();
    }

    public String toString() {
        return getBytes$okio().utf8();
    }

    @Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u001b\u0010\u0005\u001a\u00020\u0006*\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tH\u0007¢\u0006\u0002\b\nJ\u001b\u0010\u0005\u001a\u00020\u0006*\u00020\u000b2\b\b\u0002\u0010\b\u001a\u00020\tH\u0007¢\u0006\u0002\b\nJ\u001b\u0010\f\u001a\u00020\u0006*\u00020\u00042\b\b\u0002\u0010\b\u001a\u00020\tH\u0007¢\u0006\u0002\b\nR\u0010\u0010\u0003\u001a\u00020\u00048\u0006X\u0004¢\u0006\u0002\n\u0000¨\u0006\r"}, d2 = {"Lokio/Path$Companion;", "", "()V", "DIRECTORY_SEPARATOR", "", "toOkioPath", "Lokio/Path;", "Ljava/io/File;", "normalize", "", "get", "Ljava/nio/file/Path;", "toPath", "okio"}, k = 1, mv = {1, 5, 1}, xi = 48)
    /* compiled from: Path.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        @JvmStatic
        public final Path get(File file) {
            Intrinsics.checkNotNullParameter(file, "<this>");
            return get$default(this, file, false, 1, (Object) null);
        }

        @JvmStatic
        public final Path get(String str) {
            Intrinsics.checkNotNullParameter(str, "<this>");
            return get$default(this, str, false, 1, (Object) null);
        }

        @JvmStatic
        public final Path get(java.nio.file.Path path) {
            Intrinsics.checkNotNullParameter(path, "<this>");
            return get$default(this, path, false, 1, (Object) null);
        }

        private Companion() {
        }

        public static /* synthetic */ Path get$default(Companion companion, String str, boolean z, int i, Object obj) {
            if ((i & 1) != 0) {
                z = false;
            }
            return companion.get(str, z);
        }

        @JvmStatic
        public final Path get(String $this$toPath, boolean normalize) {
            Intrinsics.checkNotNullParameter($this$toPath, "<this>");
            return _PathKt.commonToPath($this$toPath, normalize);
        }

        public static /* synthetic */ Path get$default(Companion companion, File file, boolean z, int i, Object obj) {
            if ((i & 1) != 0) {
                z = false;
            }
            return companion.get(file, z);
        }

        @JvmStatic
        public final Path get(File $this$toOkioPath, boolean normalize) {
            Intrinsics.checkNotNullParameter($this$toOkioPath, "<this>");
            String file = $this$toOkioPath.toString();
            Intrinsics.checkNotNullExpressionValue(file, "toString()");
            return get(file, normalize);
        }

        public static /* synthetic */ Path get$default(Companion companion, java.nio.file.Path path, boolean z, int i, Object obj) {
            if ((i & 1) != 0) {
                z = false;
            }
            return companion.get(path, z);
        }

        @JvmStatic
        public final Path get(java.nio.file.Path $this$toOkioPath, boolean normalize) {
            Intrinsics.checkNotNullParameter($this$toOkioPath, "<this>");
            return get($this$toOkioPath.toString(), normalize);
        }
    }

    static {
        String str = File.separator;
        Intrinsics.checkNotNullExpressionValue(str, "separator");
        DIRECTORY_SEPARATOR = str;
    }
}

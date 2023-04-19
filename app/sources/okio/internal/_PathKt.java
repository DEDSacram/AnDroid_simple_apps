package okio.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import okio.Buffer;
import okio.ByteString;
import okio.Path;

@Metadata(d1 = {"\u0000H\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\u0010\u0000\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0010\f\n\u0002\b\u0006\n\u0002\u0010\u0005\n\u0000\u001a\u0015\u0010\u0014\u001a\u00020\r*\u00020\u000e2\u0006\u0010\u0015\u001a\u00020\u000eH\b\u001a\u0017\u0010\u0016\u001a\u00020\u0017*\u00020\u000e2\b\u0010\u0015\u001a\u0004\u0018\u00010\u0018H\b\u001a\r\u0010\u0019\u001a\u00020\r*\u00020\u000eH\b\u001a\r\u0010\u001a\u001a\u00020\u0017*\u00020\u000eH\b\u001a\r\u0010\u001b\u001a\u00020\u0017*\u00020\u000eH\b\u001a\r\u0010\u001c\u001a\u00020\u0017*\u00020\u000eH\b\u001a\r\u0010\u001d\u001a\u00020\u001e*\u00020\u000eH\b\u001a\r\u0010\u001f\u001a\u00020\u0001*\u00020\u000eH\b\u001a\r\u0010 \u001a\u00020\u000e*\u00020\u000eH\b\u001a\u000f\u0010!\u001a\u0004\u0018\u00010\u000e*\u00020\u000eH\b\u001a\u0015\u0010\"\u001a\u00020\u000e*\u00020\u000e2\u0006\u0010\u0015\u001a\u00020\u000eH\b\u001a\u001d\u0010#\u001a\u00020\u000e*\u00020\u000e2\u0006\u0010$\u001a\u00020\u001e2\u0006\u0010%\u001a\u00020\u0017H\b\u001a\u001d\u0010#\u001a\u00020\u000e*\u00020\u000e2\u0006\u0010$\u001a\u00020&2\u0006\u0010%\u001a\u00020\u0017H\b\u001a\u001d\u0010#\u001a\u00020\u000e*\u00020\u000e2\u0006\u0010$\u001a\u00020\u00012\u0006\u0010%\u001a\u00020\u0017H\b\u001a\u001c\u0010#\u001a\u00020\u000e*\u00020\u000e2\u0006\u0010$\u001a\u00020\u000e2\u0006\u0010%\u001a\u00020\u0017H\u0000\u001a\u000f\u0010'\u001a\u0004\u0018\u00010\u000e*\u00020\u000eH\b\u001a\u0013\u0010(\u001a\b\u0012\u0004\u0012\u00020\u001e0)*\u00020\u000eH\b\u001a\u0013\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00010)*\u00020\u000eH\b\u001a\u0012\u0010+\u001a\u00020\u000e*\u00020\u001e2\u0006\u0010%\u001a\u00020\u0017\u001a\r\u0010,\u001a\u00020\u001e*\u00020\u000eH\b\u001a\u0014\u0010-\u001a\u0004\u0018\u00010.*\u00020\u000eH\b¢\u0006\u0002\u0010/\u001a\f\u00100\u001a\u00020\u0017*\u00020\u000eH\u0002\u001a\f\u00101\u001a\u00020\r*\u00020\u000eH\u0002\u001a\u0014\u00102\u001a\u00020\u0017*\u00020&2\u0006\u0010\u0011\u001a\u00020\u0001H\u0002\u001a\u0014\u00103\u001a\u00020\u000e*\u00020&2\u0006\u0010%\u001a\u00020\u0017H\u0000\u001a\f\u00104\u001a\u00020\u0001*\u000205H\u0002\u001a\f\u00104\u001a\u00020\u0001*\u00020\u001eH\u0002\"\u0016\u0010\u0000\u001a\u00020\u00018\u0002X\u0004¢\u0006\b\n\u0000\u0012\u0004\b\u0002\u0010\u0003\"\u0016\u0010\u0004\u001a\u00020\u00018\u0002X\u0004¢\u0006\b\n\u0000\u0012\u0004\b\u0005\u0010\u0003\"\u0016\u0010\u0006\u001a\u00020\u00018\u0002X\u0004¢\u0006\b\n\u0000\u0012\u0004\b\u0007\u0010\u0003\"\u0016\u0010\b\u001a\u00020\u00018\u0002X\u0004¢\u0006\b\n\u0000\u0012\u0004\b\t\u0010\u0003\"\u0016\u0010\n\u001a\u00020\u00018\u0002X\u0004¢\u0006\b\n\u0000\u0012\u0004\b\u000b\u0010\u0003\"\u0018\u0010\f\u001a\u00020\r*\u00020\u000e8BX\u0004¢\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010\"\u001a\u0010\u0011\u001a\u0004\u0018\u00010\u0001*\u00020\u000e8BX\u0004¢\u0006\u0006\u001a\u0004\b\u0012\u0010\u0013¨\u00066"}, d2 = {"ANY_SLASH", "Lokio/ByteString;", "getANY_SLASH$annotations", "()V", "BACKSLASH", "getBACKSLASH$annotations", "DOT", "getDOT$annotations", "DOT_DOT", "getDOT_DOT$annotations", "SLASH", "getSLASH$annotations", "indexOfLastSlash", "", "Lokio/Path;", "getIndexOfLastSlash", "(Lokio/Path;)I", "slash", "getSlash", "(Lokio/Path;)Lokio/ByteString;", "commonCompareTo", "other", "commonEquals", "", "", "commonHashCode", "commonIsAbsolute", "commonIsRelative", "commonIsRoot", "commonName", "", "commonNameBytes", "commonNormalized", "commonParent", "commonRelativeTo", "commonResolve", "child", "normalize", "Lokio/Buffer;", "commonRoot", "commonSegments", "", "commonSegmentsBytes", "commonToPath", "commonToString", "commonVolumeLetter", "", "(Lokio/Path;)Ljava/lang/Character;", "lastSegmentIsDotDot", "rootLength", "startsWithVolumeLetterAndColon", "toPath", "toSlash", "", "okio"}, k = 2, mv = {1, 5, 1}, xi = 48)
/* compiled from: -Path.kt */
public final class _PathKt {
    private static final ByteString ANY_SLASH = ByteString.Companion.encodeUtf8("/\\");
    /* access modifiers changed from: private */
    public static final ByteString BACKSLASH = ByteString.Companion.encodeUtf8("\\");
    /* access modifiers changed from: private */
    public static final ByteString DOT = ByteString.Companion.encodeUtf8(".");
    /* access modifiers changed from: private */
    public static final ByteString DOT_DOT = ByteString.Companion.encodeUtf8("..");
    /* access modifiers changed from: private */
    public static final ByteString SLASH = ByteString.Companion.encodeUtf8("/");

    private static /* synthetic */ void getANY_SLASH$annotations() {
    }

    private static /* synthetic */ void getBACKSLASH$annotations() {
    }

    private static /* synthetic */ void getDOT$annotations() {
    }

    private static /* synthetic */ void getDOT_DOT$annotations() {
    }

    private static /* synthetic */ void getSLASH$annotations() {
    }

    public static final Path commonRoot(Path $this$commonRoot) {
        Intrinsics.checkNotNullParameter($this$commonRoot, "<this>");
        int rootLength = rootLength($this$commonRoot);
        if (rootLength == -1) {
            return null;
        }
        return new Path($this$commonRoot.getBytes$okio().substring(0, rootLength));
    }

    public static final List<String> commonSegments(Path $this$commonSegments) {
        Intrinsics.checkNotNullParameter($this$commonSegments, "<this>");
        Path $this$commonSegmentsBytes$iv = $this$commonSegments;
        List result$iv = new ArrayList();
        int segmentStart$iv = rootLength($this$commonSegmentsBytes$iv);
        if (segmentStart$iv == -1) {
            segmentStart$iv = 0;
        } else if (segmentStart$iv < $this$commonSegmentsBytes$iv.getBytes$okio().size() && $this$commonSegmentsBytes$iv.getBytes$okio().getByte(segmentStart$iv) == ((byte) 92)) {
            segmentStart$iv++;
        }
        int size = $this$commonSegmentsBytes$iv.getBytes$okio().size();
        if (segmentStart$iv < size) {
            int segmentStart$iv2 = segmentStart$iv;
            do {
                int i$iv = segmentStart$iv;
                segmentStart$iv++;
                if ($this$commonSegmentsBytes$iv.getBytes$okio().getByte(i$iv) == ((byte) 47) || $this$commonSegmentsBytes$iv.getBytes$okio().getByte(i$iv) == ((byte) 92)) {
                    result$iv.add($this$commonSegmentsBytes$iv.getBytes$okio().substring(segmentStart$iv2, i$iv));
                    segmentStart$iv2 = i$iv + 1;
                    continue;
                }
            } while (segmentStart$iv < size);
            segmentStart$iv = segmentStart$iv2;
        }
        if (segmentStart$iv < $this$commonSegmentsBytes$iv.getBytes$okio().size()) {
            result$iv.add($this$commonSegmentsBytes$iv.getBytes$okio().substring(segmentStart$iv, $this$commonSegmentsBytes$iv.getBytes$okio().size()));
        }
        Iterable<ByteString> $this$map$iv = result$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        for (ByteString it : $this$map$iv) {
            destination$iv$iv.add(it.utf8());
        }
        return (List) destination$iv$iv;
    }

    public static final List<ByteString> commonSegmentsBytes(Path $this$commonSegmentsBytes) {
        Intrinsics.checkNotNullParameter($this$commonSegmentsBytes, "<this>");
        List<ByteString> arrayList = new ArrayList<>();
        int segmentStart = rootLength($this$commonSegmentsBytes);
        if (segmentStart == -1) {
            segmentStart = 0;
        } else if (segmentStart < $this$commonSegmentsBytes.getBytes$okio().size() && $this$commonSegmentsBytes.getBytes$okio().getByte(segmentStart) == ((byte) 92)) {
            segmentStart++;
        }
        int size = $this$commonSegmentsBytes.getBytes$okio().size();
        if (segmentStart < size) {
            int segmentStart2 = segmentStart;
            do {
                int i = segmentStart;
                segmentStart++;
                if ($this$commonSegmentsBytes.getBytes$okio().getByte(i) == ((byte) 47) || $this$commonSegmentsBytes.getBytes$okio().getByte(i) == ((byte) 92)) {
                    arrayList.add($this$commonSegmentsBytes.getBytes$okio().substring(segmentStart2, i));
                    segmentStart2 = i + 1;
                    continue;
                }
            } while (segmentStart < size);
            segmentStart = segmentStart2;
        }
        if (segmentStart < $this$commonSegmentsBytes.getBytes$okio().size()) {
            arrayList.add($this$commonSegmentsBytes.getBytes$okio().substring(segmentStart, $this$commonSegmentsBytes.getBytes$okio().size()));
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    public static final int rootLength(Path $this$rootLength) {
        if ($this$rootLength.getBytes$okio().size() == 0) {
            return -1;
        }
        boolean z = false;
        if ($this$rootLength.getBytes$okio().getByte(0) == ((byte) 47)) {
            return 1;
        }
        byte b = (byte) 92;
        if ($this$rootLength.getBytes$okio().getByte(0) == b) {
            if ($this$rootLength.getBytes$okio().size() <= 2 || $this$rootLength.getBytes$okio().getByte(1) != b) {
                return 1;
            }
            int uncRootEnd = $this$rootLength.getBytes$okio().indexOf(BACKSLASH, 2);
            if (uncRootEnd == -1) {
                return $this$rootLength.getBytes$okio().size();
            }
            return uncRootEnd;
        } else if ($this$rootLength.getBytes$okio().size() <= 2 || $this$rootLength.getBytes$okio().getByte(1) != ((byte) 58) || $this$rootLength.getBytes$okio().getByte(2) != b) {
            return -1;
        } else {
            char c = (char) $this$rootLength.getBytes$okio().getByte(0);
            if ('a' <= c && c <= 'z') {
                return 3;
            }
            if ('A' <= c && c <= 'Z') {
                z = true;
            }
            if (!z) {
                return -1;
            }
            return 3;
        }
    }

    public static final boolean commonIsAbsolute(Path $this$commonIsAbsolute) {
        Intrinsics.checkNotNullParameter($this$commonIsAbsolute, "<this>");
        return rootLength($this$commonIsAbsolute) != -1;
    }

    public static final boolean commonIsRelative(Path $this$commonIsRelative) {
        Intrinsics.checkNotNullParameter($this$commonIsRelative, "<this>");
        return rootLength($this$commonIsRelative) == -1;
    }

    public static final Character commonVolumeLetter(Path $this$commonVolumeLetter) {
        Intrinsics.checkNotNullParameter($this$commonVolumeLetter, "<this>");
        boolean z = false;
        if (ByteString.indexOf$default($this$commonVolumeLetter.getBytes$okio(), SLASH, 0, 2, (Object) null) != -1 || $this$commonVolumeLetter.getBytes$okio().size() < 2 || $this$commonVolumeLetter.getBytes$okio().getByte(1) != ((byte) 58)) {
            return null;
        }
        char c = (char) $this$commonVolumeLetter.getBytes$okio().getByte(0);
        if (!('a' <= c && c <= 'z')) {
            if ('A' <= c && c <= 'Z') {
                z = true;
            }
            if (!z) {
                return null;
            }
        }
        return Character.valueOf(c);
    }

    /* access modifiers changed from: private */
    public static final int getIndexOfLastSlash(Path $this$indexOfLastSlash) {
        int lastSlash = ByteString.lastIndexOf$default($this$indexOfLastSlash.getBytes$okio(), SLASH, 0, 2, (Object) null);
        if (lastSlash != -1) {
            return lastSlash;
        }
        return ByteString.lastIndexOf$default($this$indexOfLastSlash.getBytes$okio(), BACKSLASH, 0, 2, (Object) null);
    }

    public static final ByteString commonNameBytes(Path $this$commonNameBytes) {
        Intrinsics.checkNotNullParameter($this$commonNameBytes, "<this>");
        int lastSlash = getIndexOfLastSlash($this$commonNameBytes);
        if (lastSlash != -1) {
            return ByteString.substring$default($this$commonNameBytes.getBytes$okio(), lastSlash + 1, 0, 2, (Object) null);
        }
        if ($this$commonNameBytes.volumeLetter() == null || $this$commonNameBytes.getBytes$okio().size() != 2) {
            return $this$commonNameBytes.getBytes$okio();
        }
        return ByteString.EMPTY;
    }

    public static final String commonName(Path $this$commonName) {
        Intrinsics.checkNotNullParameter($this$commonName, "<this>");
        return $this$commonName.nameBytes().utf8();
    }

    public static final Path commonParent(Path $this$commonParent) {
        Intrinsics.checkNotNullParameter($this$commonParent, "<this>");
        if (Intrinsics.areEqual((Object) $this$commonParent.getBytes$okio(), (Object) DOT) || Intrinsics.areEqual((Object) $this$commonParent.getBytes$okio(), (Object) SLASH) || Intrinsics.areEqual((Object) $this$commonParent.getBytes$okio(), (Object) BACKSLASH) || lastSegmentIsDotDot($this$commonParent)) {
            return null;
        }
        int lastSlash = getIndexOfLastSlash($this$commonParent);
        if (lastSlash != 2 || $this$commonParent.volumeLetter() == null) {
            if (lastSlash == 1 && $this$commonParent.getBytes$okio().startsWith(BACKSLASH)) {
                return null;
            }
            if (lastSlash != -1 || $this$commonParent.volumeLetter() == null) {
                if (lastSlash == -1) {
                    return new Path(DOT);
                }
                if (lastSlash == 0) {
                    return new Path(ByteString.substring$default($this$commonParent.getBytes$okio(), 0, 1, 1, (Object) null));
                }
                return new Path(ByteString.substring$default($this$commonParent.getBytes$okio(), 0, lastSlash, 1, (Object) null));
            } else if ($this$commonParent.getBytes$okio().size() == 2) {
                return null;
            } else {
                return new Path(ByteString.substring$default($this$commonParent.getBytes$okio(), 0, 2, 1, (Object) null));
            }
        } else if ($this$commonParent.getBytes$okio().size() == 3) {
            return null;
        } else {
            return new Path(ByteString.substring$default($this$commonParent.getBytes$okio(), 0, 3, 1, (Object) null));
        }
    }

    /* access modifiers changed from: private */
    public static final boolean lastSegmentIsDotDot(Path $this$lastSegmentIsDotDot) {
        if (!$this$lastSegmentIsDotDot.getBytes$okio().endsWith(DOT_DOT) || ($this$lastSegmentIsDotDot.getBytes$okio().size() != 2 && !$this$lastSegmentIsDotDot.getBytes$okio().rangeEquals($this$lastSegmentIsDotDot.getBytes$okio().size() - 3, SLASH, 0, 1) && !$this$lastSegmentIsDotDot.getBytes$okio().rangeEquals($this$lastSegmentIsDotDot.getBytes$okio().size() - 3, BACKSLASH, 0, 1))) {
            return false;
        }
        return true;
    }

    public static final boolean commonIsRoot(Path $this$commonIsRoot) {
        Intrinsics.checkNotNullParameter($this$commonIsRoot, "<this>");
        return rootLength($this$commonIsRoot) == $this$commonIsRoot.getBytes$okio().size();
    }

    public static final Path commonResolve(Path $this$commonResolve, String child, boolean normalize) {
        Intrinsics.checkNotNullParameter($this$commonResolve, "<this>");
        Intrinsics.checkNotNullParameter(child, "child");
        return commonResolve($this$commonResolve, toPath(new Buffer().writeUtf8(child), false), normalize);
    }

    public static final Path commonResolve(Path $this$commonResolve, ByteString child, boolean normalize) {
        Intrinsics.checkNotNullParameter($this$commonResolve, "<this>");
        Intrinsics.checkNotNullParameter(child, "child");
        return commonResolve($this$commonResolve, toPath(new Buffer().write(child), false), normalize);
    }

    public static final Path commonResolve(Path $this$commonResolve, Buffer child, boolean normalize) {
        Intrinsics.checkNotNullParameter($this$commonResolve, "<this>");
        Intrinsics.checkNotNullParameter(child, "child");
        return commonResolve($this$commonResolve, toPath(child, false), normalize);
    }

    public static final Path commonResolve(Path $this$commonResolve, Path child, boolean normalize) {
        Intrinsics.checkNotNullParameter($this$commonResolve, "<this>");
        Intrinsics.checkNotNullParameter(child, "child");
        if (child.isAbsolute() || child.volumeLetter() != null) {
            return child;
        }
        ByteString slash = getSlash($this$commonResolve);
        if (slash == null && (slash = getSlash(child)) == null) {
            slash = toSlash(Path.DIRECTORY_SEPARATOR);
        }
        Buffer buffer = new Buffer();
        buffer.write($this$commonResolve.getBytes$okio());
        if (buffer.size() > 0) {
            buffer.write(slash);
        }
        buffer.write(child.getBytes$okio());
        return toPath(buffer, normalize);
    }

    public static final Path commonRelativeTo(Path $this$commonRelativeTo, Path other) {
        Intrinsics.checkNotNullParameter($this$commonRelativeTo, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        if (Intrinsics.areEqual((Object) $this$commonRelativeTo.getRoot(), (Object) other.getRoot())) {
            List thisSegments = $this$commonRelativeTo.getSegmentsBytes();
            List otherSegments = other.getSegmentsBytes();
            int firstNewSegmentIndex = 0;
            int minSegmentsSize = Math.min(thisSegments.size(), otherSegments.size());
            while (firstNewSegmentIndex < minSegmentsSize && Intrinsics.areEqual((Object) thisSegments.get(firstNewSegmentIndex), (Object) otherSegments.get(firstNewSegmentIndex))) {
                firstNewSegmentIndex++;
            }
            if (firstNewSegmentIndex == minSegmentsSize && $this$commonRelativeTo.getBytes$okio().size() == other.getBytes$okio().size()) {
                return Path.Companion.get$default(Path.Companion, ".", false, 1, (Object) null);
            }
            if (otherSegments.subList(firstNewSegmentIndex, otherSegments.size()).indexOf(DOT_DOT) == -1) {
                Buffer buffer = new Buffer();
                ByteString slash = getSlash(other);
                if (slash == null && (slash = getSlash($this$commonRelativeTo)) == null) {
                    slash = toSlash(Path.DIRECTORY_SEPARATOR);
                }
                int size = otherSegments.size();
                if (firstNewSegmentIndex < size) {
                    int i = firstNewSegmentIndex;
                    do {
                        int i2 = i;
                        i++;
                        buffer.write(DOT_DOT);
                        buffer.write(slash);
                    } while (i < size);
                }
                int size2 = thisSegments.size();
                if (firstNewSegmentIndex < size2) {
                    int i3 = firstNewSegmentIndex;
                    do {
                        int i4 = i3;
                        i3++;
                        buffer.write(thisSegments.get(i4));
                        buffer.write(slash);
                    } while (i3 < size2);
                }
                return toPath(buffer, false);
            }
            throw new IllegalArgumentException(("Impossible relative path to resolve: " + $this$commonRelativeTo + " and " + other).toString());
        }
        throw new IllegalArgumentException(("Paths of different roots cannot be relative to each other: " + $this$commonRelativeTo + " and " + other).toString());
    }

    public static final Path commonNormalized(Path $this$commonNormalized) {
        Intrinsics.checkNotNullParameter($this$commonNormalized, "<this>");
        return Path.Companion.get($this$commonNormalized.toString(), true);
    }

    /* access modifiers changed from: private */
    public static final ByteString getSlash(Path $this$slash) {
        ByteString bytes$okio = $this$slash.getBytes$okio();
        ByteString byteString = SLASH;
        if (ByteString.indexOf$default(bytes$okio, byteString, 0, 2, (Object) null) != -1) {
            return byteString;
        }
        ByteString bytes$okio2 = $this$slash.getBytes$okio();
        ByteString byteString2 = BACKSLASH;
        if (ByteString.indexOf$default(bytes$okio2, byteString2, 0, 2, (Object) null) != -1) {
            return byteString2;
        }
        return null;
    }

    public static final int commonCompareTo(Path $this$commonCompareTo, Path other) {
        Intrinsics.checkNotNullParameter($this$commonCompareTo, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        return $this$commonCompareTo.getBytes$okio().compareTo(other.getBytes$okio());
    }

    public static final boolean commonEquals(Path $this$commonEquals, Object other) {
        Intrinsics.checkNotNullParameter($this$commonEquals, "<this>");
        return (other instanceof Path) && Intrinsics.areEqual((Object) ((Path) other).getBytes$okio(), (Object) $this$commonEquals.getBytes$okio());
    }

    public static final int commonHashCode(Path $this$commonHashCode) {
        Intrinsics.checkNotNullParameter($this$commonHashCode, "<this>");
        return $this$commonHashCode.getBytes$okio().hashCode();
    }

    public static final String commonToString(Path $this$commonToString) {
        Intrinsics.checkNotNullParameter($this$commonToString, "<this>");
        return $this$commonToString.getBytes$okio().utf8();
    }

    public static final Path commonToPath(String $this$commonToPath, boolean normalize) {
        Intrinsics.checkNotNullParameter($this$commonToPath, "<this>");
        return toPath(new Buffer().writeUtf8($this$commonToPath), normalize);
    }

    public static final Path toPath(Buffer $this$toPath, boolean normalize) {
        ByteString byteString;
        ByteString part;
        ByteString byteString2;
        Buffer buffer = $this$toPath;
        Intrinsics.checkNotNullParameter(buffer, "<this>");
        ByteString slash = null;
        Buffer result = new Buffer();
        int leadingSlashCount = 0;
        while (true) {
            if (!buffer.rangeEquals(0, SLASH)) {
                byteString = BACKSLASH;
                if (!buffer.rangeEquals(0, byteString)) {
                    break;
                }
            }
            slash = slash == null ? toSlash($this$toPath.readByte()) : slash;
            leadingSlashCount++;
        }
        boolean windowsUncPath = leadingSlashCount >= 2 && Intrinsics.areEqual((Object) slash, (Object) byteString);
        long j = -1;
        if (windowsUncPath) {
            Intrinsics.checkNotNull(slash);
            result.write(slash);
            result.write(slash);
        } else if (leadingSlashCount > 0) {
            Intrinsics.checkNotNull(slash);
            result.write(slash);
        } else {
            long limit = buffer.indexOfElement(ANY_SLASH);
            if (slash != null) {
                byteString2 = slash;
            } else if (limit == -1) {
                byteString2 = toSlash(Path.DIRECTORY_SEPARATOR);
            } else {
                byteString2 = toSlash(buffer.getByte(limit));
            }
            slash = byteString2;
            if (startsWithVolumeLetterAndColon(buffer, slash)) {
                if (limit == 2) {
                    result.write(buffer, 3);
                } else {
                    result.write(buffer, 2);
                }
            }
        }
        boolean absolute = result.size() > 0;
        List canonicalParts = new ArrayList();
        while (!$this$toPath.exhausted()) {
            long limit2 = buffer.indexOfElement(ANY_SLASH);
            if (limit2 == j) {
                part = $this$toPath.readByteString();
            } else {
                part = buffer.readByteString(limit2);
                $this$toPath.readByte();
            }
            ByteString byteString3 = DOT_DOT;
            if (Intrinsics.areEqual((Object) part, (Object) byteString3)) {
                if (!absolute || !canonicalParts.isEmpty()) {
                    if (!normalize || (!absolute && (canonicalParts.isEmpty() || Intrinsics.areEqual(CollectionsKt.last(canonicalParts), (Object) byteString3)))) {
                        canonicalParts.add(part);
                        j = -1;
                    } else if (!windowsUncPath || canonicalParts.size() != 1) {
                        CollectionsKt.removeLastOrNull(canonicalParts);
                        j = -1;
                    } else {
                        j = -1;
                    }
                }
            } else if (Intrinsics.areEqual((Object) part, (Object) DOT) || Intrinsics.areEqual((Object) part, (Object) ByteString.EMPTY)) {
                j = -1;
            } else {
                canonicalParts.add(part);
                j = -1;
            }
        }
        int size = canonicalParts.size();
        if (size > 0) {
            int i = 0;
            while (true) {
                int i2 = i;
                int i3 = i + 1;
                if (i2 > 0) {
                    result.write(slash);
                }
                result.write((ByteString) canonicalParts.get(i2));
                if (i3 >= size) {
                    break;
                }
                i = i3;
            }
        }
        if (result.size() == 0) {
            result.write(DOT);
        }
        return new Path(result.readByteString());
    }

    /* access modifiers changed from: private */
    public static final ByteString toSlash(String $this$toSlash) {
        if (Intrinsics.areEqual((Object) $this$toSlash, (Object) "/")) {
            return SLASH;
        }
        if (Intrinsics.areEqual((Object) $this$toSlash, (Object) "\\")) {
            return BACKSLASH;
        }
        throw new IllegalArgumentException(Intrinsics.stringPlus("not a directory separator: ", $this$toSlash));
    }

    private static final ByteString toSlash(byte $this$toSlash) {
        if ($this$toSlash == 47) {
            return SLASH;
        }
        if ($this$toSlash == 92) {
            return BACKSLASH;
        }
        throw new IllegalArgumentException(Intrinsics.stringPlus("not a directory separator: ", Byte.valueOf($this$toSlash)));
    }

    private static final boolean startsWithVolumeLetterAndColon(Buffer $this$startsWithVolumeLetterAndColon, ByteString slash) {
        if (!Intrinsics.areEqual((Object) slash, (Object) BACKSLASH) || $this$startsWithVolumeLetterAndColon.size() < 2 || $this$startsWithVolumeLetterAndColon.getByte(1) != ((byte) 58)) {
            return false;
        }
        char b = (char) $this$startsWithVolumeLetterAndColon.getByte(0);
        if (!('a' <= b && b <= 'z')) {
            if (!('A' <= b && b <= 'Z')) {
                return false;
            }
        }
        return true;
    }
}

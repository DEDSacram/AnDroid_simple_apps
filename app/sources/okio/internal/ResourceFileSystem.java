package okio.internal;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okhttp3.HttpUrl;
import okio.FileHandle;
import okio.FileMetadata;
import okio.FileSystem;
import okio.Path;
import okio.Sink;
import okio.Source;

@Metadata(d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0000\u0018\u0000 *2\u00020\u0001:\u0001*B\u0017\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0018\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\n2\u0006\u0010\u0012\u001a\u00020\u0005H\u0016J\u0018\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\n2\u0006\u0010\u0016\u001a\u00020\nH\u0016J\u0010\u0010\u0017\u001a\u00020\n2\u0006\u0010\u0018\u001a\u00020\nH\u0016J\u0010\u0010\u0019\u001a\u00020\n2\u0006\u0010\u0018\u001a\u00020\nH\u0002J\u0018\u0010\u001a\u001a\u00020\u00142\u0006\u0010\u001b\u001a\u00020\n2\u0006\u0010\u001c\u001a\u00020\u0005H\u0016J\u0018\u0010\u001d\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\n2\u0006\u0010\u0016\u001a\u00020\nH\u0016J\u0018\u0010\u001e\u001a\u00020\u00142\u0006\u0010\u0018\u001a\u00020\n2\u0006\u0010\u0012\u001a\u00020\u0005H\u0016J\u0016\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\n0\b2\u0006\u0010\u001b\u001a\u00020\nH\u0016J\u0018\u0010 \u001a\n\u0012\u0004\u0012\u00020\n\u0018\u00010\b2\u0006\u0010\u001b\u001a\u00020\nH\u0016J\u0012\u0010!\u001a\u0004\u0018\u00010\"2\u0006\u0010\u0018\u001a\u00020\nH\u0016J\u0010\u0010#\u001a\u00020$2\u0006\u0010\u0011\u001a\u00020\nH\u0016J \u0010%\u001a\u00020$2\u0006\u0010\u0011\u001a\u00020\n2\u0006\u0010\u001c\u001a\u00020\u00052\u0006\u0010\u0012\u001a\u00020\u0005H\u0016J\u0018\u0010&\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\n2\u0006\u0010\u001c\u001a\u00020\u0005H\u0016J\u0010\u0010\u0015\u001a\u00020'2\u0006\u0010\u0011\u001a\u00020\nH\u0016J\f\u0010(\u001a\u00020)*\u00020\nH\u0002R-\u0010\u0007\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\n0\t0\b8BX\u0002¢\u0006\f\n\u0004\b\r\u0010\u000e\u001a\u0004\b\u000b\u0010\f¨\u0006+"}, d2 = {"Lokio/internal/ResourceFileSystem;", "Lokio/FileSystem;", "classLoader", "Ljava/lang/ClassLoader;", "indexEagerly", "", "(Ljava/lang/ClassLoader;Z)V", "roots", "", "Lkotlin/Pair;", "Lokio/Path;", "getRoots", "()Ljava/util/List;", "roots$delegate", "Lkotlin/Lazy;", "appendingSink", "Lokio/Sink;", "file", "mustExist", "atomicMove", "", "source", "target", "canonicalize", "path", "canonicalizeInternal", "createDirectory", "dir", "mustCreate", "createSymlink", "delete", "list", "listOrNull", "metadataOrNull", "Lokio/FileMetadata;", "openReadOnly", "Lokio/FileHandle;", "openReadWrite", "sink", "Lokio/Source;", "toRelativePath", "", "Companion", "okio"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* compiled from: ResourceFileSystem.kt */
public final class ResourceFileSystem extends FileSystem {
    /* access modifiers changed from: private */
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    /* access modifiers changed from: private */
    @Deprecated
    public static final Path ROOT = Path.Companion.get$default(Path.Companion, "/", false, 1, (Object) null);
    private final Lazy roots$delegate;

    public ResourceFileSystem(ClassLoader classLoader, boolean indexEagerly) {
        Intrinsics.checkNotNullParameter(classLoader, "classLoader");
        this.roots$delegate = LazyKt.lazy(new ResourceFileSystem$roots$2(classLoader));
        if (indexEagerly) {
            getRoots().size();
        }
    }

    private final List<Pair<FileSystem, Path>> getRoots() {
        return (List) this.roots$delegate.getValue();
    }

    public Path canonicalize(Path path) {
        Intrinsics.checkNotNullParameter(path, "path");
        return canonicalizeInternal(path);
    }

    private final Path canonicalizeInternal(Path path) {
        return ROOT.resolve(path, true);
    }

    public List<Path> list(Path dir) {
        String relativePath;
        Path path = dir;
        Intrinsics.checkNotNullParameter(path, "dir");
        String relativePath2 = toRelativePath(dir);
        Set result = new LinkedHashSet();
        boolean foundAny = false;
        for (Pair next : getRoots()) {
            FileSystem fileSystem = (FileSystem) next.component1();
            Path base = (Path) next.component2();
            try {
                Collection collection = result;
                Collection destination$iv$iv = new ArrayList();
                for (Object element$iv$iv : fileSystem.list(base.resolve(relativePath2))) {
                    relativePath = relativePath2;
                    try {
                        if (Companion.keepPath((Path) element$iv$iv)) {
                            destination$iv$iv.add(element$iv$iv);
                        }
                        relativePath2 = relativePath;
                    } catch (IOException e) {
                        relativePath2 = relativePath;
                    }
                }
                String relativePath3 = relativePath2;
                Iterable<Path> $this$map$iv = (List) destination$iv$iv;
                Collection destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
                for (Path it : $this$map$iv) {
                    destination$iv$iv2.add(Companion.removeBase(it, base));
                    $this$map$iv = $this$map$iv;
                }
                CollectionsKt.addAll(collection, (List) destination$iv$iv2);
                foundAny = true;
                relativePath2 = relativePath3;
            } catch (IOException e2) {
                relativePath = relativePath2;
                relativePath2 = relativePath;
            }
        }
        if (foundAny) {
            return CollectionsKt.toList(result);
        }
        throw new FileNotFoundException(Intrinsics.stringPlus("file not found: ", path));
    }

    public List<Path> listOrNull(Path dir) {
        String relativePath;
        Intrinsics.checkNotNullParameter(dir, "dir");
        String relativePath2 = toRelativePath(dir);
        Set result = new LinkedHashSet();
        boolean foundAny = false;
        Iterator<Pair<FileSystem, Path>> it = getRoots().iterator();
        while (true) {
            Collection collection = null;
            if (!it.hasNext()) {
                break;
            }
            Pair next = it.next();
            Path base = (Path) next.component2();
            List<Path> $this$filter$iv = ((FileSystem) next.component1()).listOrNull(base.resolve(relativePath2));
            if ($this$filter$iv == null) {
                relativePath = relativePath2;
            } else {
                Collection destination$iv$iv = new ArrayList();
                for (Object element$iv$iv : $this$filter$iv) {
                    String relativePath3 = relativePath2;
                    if (Companion.keepPath((Path) element$iv$iv)) {
                        destination$iv$iv.add(element$iv$iv);
                    }
                    relativePath2 = relativePath3;
                }
                relativePath = relativePath2;
                Iterable<Path> $this$map$iv = (List) destination$iv$iv;
                Collection destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
                for (Path it2 : $this$map$iv) {
                    destination$iv$iv2.add(Companion.removeBase(it2, base));
                }
                collection = (List) destination$iv$iv2;
            }
            Collection $this$map$iv2 = collection;
            if ($this$map$iv2 != null) {
                CollectionsKt.addAll(result, $this$map$iv2);
                foundAny = true;
                relativePath2 = relativePath;
            } else {
                relativePath2 = relativePath;
            }
        }
        if (foundAny) {
            return CollectionsKt.toList(result);
        }
        return null;
    }

    public FileHandle openReadOnly(Path file) {
        Intrinsics.checkNotNullParameter(file, "file");
        if (Companion.keepPath(file)) {
            String relativePath = toRelativePath(file);
            for (Pair next : getRoots()) {
                try {
                    return ((FileSystem) next.component1()).openReadOnly(((Path) next.component2()).resolve(relativePath));
                } catch (FileNotFoundException e) {
                }
            }
            throw new FileNotFoundException(Intrinsics.stringPlus("file not found: ", file));
        }
        throw new FileNotFoundException(Intrinsics.stringPlus("file not found: ", file));
    }

    public FileHandle openReadWrite(Path file, boolean mustCreate, boolean mustExist) {
        Intrinsics.checkNotNullParameter(file, "file");
        throw new IOException("resources are not writable");
    }

    public FileMetadata metadataOrNull(Path path) {
        Intrinsics.checkNotNullParameter(path, "path");
        if (!Companion.keepPath(path)) {
            return null;
        }
        String relativePath = toRelativePath(path);
        for (Pair next : getRoots()) {
            FileMetadata metadataOrNull = ((FileSystem) next.component1()).metadataOrNull(((Path) next.component2()).resolve(relativePath));
            if (metadataOrNull != null) {
                return metadataOrNull;
            }
        }
        return null;
    }

    public Source source(Path file) {
        Intrinsics.checkNotNullParameter(file, "file");
        if (Companion.keepPath(file)) {
            String relativePath = toRelativePath(file);
            for (Pair next : getRoots()) {
                try {
                    return ((FileSystem) next.component1()).source(((Path) next.component2()).resolve(relativePath));
                } catch (FileNotFoundException e) {
                }
            }
            throw new FileNotFoundException(Intrinsics.stringPlus("file not found: ", file));
        }
        throw new FileNotFoundException(Intrinsics.stringPlus("file not found: ", file));
    }

    public Sink sink(Path file, boolean mustCreate) {
        Intrinsics.checkNotNullParameter(file, "file");
        throw new IOException(this + " is read-only");
    }

    public Sink appendingSink(Path file, boolean mustExist) {
        Intrinsics.checkNotNullParameter(file, "file");
        throw new IOException(this + " is read-only");
    }

    public void createDirectory(Path dir, boolean mustCreate) {
        Intrinsics.checkNotNullParameter(dir, "dir");
        throw new IOException(this + " is read-only");
    }

    public void atomicMove(Path source, Path target) {
        Intrinsics.checkNotNullParameter(source, "source");
        Intrinsics.checkNotNullParameter(target, TypedValues.AttributesType.S_TARGET);
        throw new IOException(this + " is read-only");
    }

    public void delete(Path path, boolean mustExist) {
        Intrinsics.checkNotNullParameter(path, "path");
        throw new IOException(this + " is read-only");
    }

    public void createSymlink(Path source, Path target) {
        Intrinsics.checkNotNullParameter(source, "source");
        Intrinsics.checkNotNullParameter(target, TypedValues.AttributesType.S_TARGET);
        throw new IOException(this + " is read-only");
    }

    private final String toRelativePath(Path $this$toRelativePath) {
        return canonicalizeInternal($this$toRelativePath).relativeTo(ROOT).toString();
    }

    @Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0004H\u0002J\u0012\u0010\n\u001a\u00020\u0004*\u00020\u00042\u0006\u0010\u000b\u001a\u00020\u0004J\u001c\u0010\f\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00040\u000e0\r*\u00020\u0010J\u0018\u0010\u0011\u001a\u0010\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u000e*\u00020\u0012J\u0018\u0010\u0013\u001a\u0010\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u000e*\u00020\u0012R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0014"}, d2 = {"Lokio/internal/ResourceFileSystem$Companion;", "", "()V", "ROOT", "Lokio/Path;", "getROOT", "()Lokio/Path;", "keepPath", "", "path", "removeBase", "base", "toClasspathRoots", "", "Lkotlin/Pair;", "Lokio/FileSystem;", "Ljava/lang/ClassLoader;", "toFileRoot", "Ljava/net/URL;", "toJarRoot", "okio"}, k = 1, mv = {1, 5, 1}, xi = 48)
    /* compiled from: ResourceFileSystem.kt */
    private static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final Path getROOT() {
            return ResourceFileSystem.ROOT;
        }

        public final Path removeBase(Path $this$removeBase, Path base) {
            Intrinsics.checkNotNullParameter($this$removeBase, "<this>");
            Intrinsics.checkNotNullParameter(base, "base");
            return getROOT().resolve(StringsKt.replace$default(StringsKt.removePrefix($this$removeBase.toString(), (CharSequence) base.toString()), '\\', '/', false, 4, (Object) null));
        }

        public final List<Pair<FileSystem, Path>> toClasspathRoots(ClassLoader $this$toClasspathRoots) {
            ClassLoader classLoader = $this$toClasspathRoots;
            Intrinsics.checkNotNullParameter(classLoader, "<this>");
            Enumeration<URL> resources = classLoader.getResources(HttpUrl.FRAGMENT_ENCODE_SET);
            Intrinsics.checkNotNullExpressionValue(resources, "getResources(\"\")");
            ArrayList<T> list = Collections.list(resources);
            Intrinsics.checkNotNullExpressionValue(list, "java.util.Collections.list(this)");
            Iterable<URL> $this$mapNotNull$iv = list;
            Collection destination$iv$iv = new ArrayList();
            for (URL it : $this$mapNotNull$iv) {
                Iterable $this$mapNotNull$iv2 = $this$mapNotNull$iv;
                Companion access$getCompanion$p = ResourceFileSystem.Companion;
                Intrinsics.checkNotNullExpressionValue(it, "it");
                Object it$iv$iv = access$getCompanion$p.toFileRoot(it);
                if (it$iv$iv != null) {
                    destination$iv$iv.add(it$iv$iv);
                }
                $this$mapNotNull$iv = $this$mapNotNull$iv2;
            }
            Collection collection = (List) destination$iv$iv;
            Enumeration<URL> resources2 = classLoader.getResources("META-INF/MANIFEST.MF");
            Intrinsics.checkNotNullExpressionValue(resources2, "getResources(\"META-INF/MANIFEST.MF\")");
            ArrayList<T> $this$mapNotNull$iv3 = Collections.list(resources2);
            Intrinsics.checkNotNullExpressionValue($this$mapNotNull$iv3, "java.util.Collections.list(this)");
            Collection destination$iv$iv2 = new ArrayList();
            for (T it2 : $this$mapNotNull$iv3) {
                Companion access$getCompanion$p2 = ResourceFileSystem.Companion;
                Intrinsics.checkNotNullExpressionValue(it2, "it");
                Object it$iv$iv2 = access$getCompanion$p2.toJarRoot(it2);
                if (it$iv$iv2 != null) {
                    destination$iv$iv2.add(it$iv$iv2);
                }
                ClassLoader classLoader2 = $this$toClasspathRoots;
            }
            return CollectionsKt.plus(collection, (List) destination$iv$iv2);
        }

        public final Pair<FileSystem, Path> toFileRoot(URL $this$toFileRoot) {
            Intrinsics.checkNotNullParameter($this$toFileRoot, "<this>");
            if (!Intrinsics.areEqual((Object) $this$toFileRoot.getProtocol(), (Object) "file")) {
                return null;
            }
            return TuplesKt.to(FileSystem.SYSTEM, Path.Companion.get$default(Path.Companion, new File($this$toFileRoot.toURI()), false, 1, (Object) null));
        }

        public final Pair<FileSystem, Path> toJarRoot(URL $this$toJarRoot) {
            int suffixStart;
            Intrinsics.checkNotNullParameter($this$toJarRoot, "<this>");
            String urlString = $this$toJarRoot.toString();
            Intrinsics.checkNotNullExpressionValue(urlString, "toString()");
            if (!StringsKt.startsWith$default(urlString, "jar:file:", false, 2, (Object) null) || (suffixStart = StringsKt.lastIndexOf$default((CharSequence) urlString, "!", 0, false, 6, (Object) null)) == -1) {
                return null;
            }
            Path.Companion companion = Path.Companion;
            String substring = urlString.substring("jar:".length(), suffixStart);
            Intrinsics.checkNotNullExpressionValue(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
            return TuplesKt.to(ZipKt.openZip(Path.Companion.get$default(companion, new File(URI.create(substring)), false, 1, (Object) null), FileSystem.SYSTEM, ResourceFileSystem$Companion$toJarRoot$zip$1.INSTANCE), getROOT());
        }

        /* access modifiers changed from: private */
        public final boolean keepPath(Path path) {
            return !StringsKt.endsWith(path.name(), ".class", true);
        }
    }
}

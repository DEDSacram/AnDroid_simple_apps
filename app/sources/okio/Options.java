package okio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import kotlin.Metadata;
import kotlin.UByte;
import kotlin.collections.AbstractList;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\u0015\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\b\u0018\u0000 \u00152\b\u0012\u0004\u0012\u00020\u00020\u00012\u00060\u0003j\u0002`\u0004:\u0001\u0015B\u001f\b\u0002\u0012\u000e\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0002\u0010\tJ\u0011\u0010\u0013\u001a\u00020\u00022\u0006\u0010\u0014\u001a\u00020\u000eH\u0002R\u001e\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00020\u0006X\u0004¢\u0006\n\n\u0002\u0010\f\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\r\u001a\u00020\u000e8VX\u0004¢\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010R\u0014\u0010\u0007\u001a\u00020\bX\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012¨\u0006\u0016"}, d2 = {"Lokio/Options;", "Lkotlin/collections/AbstractList;", "Lokio/ByteString;", "Ljava/util/RandomAccess;", "Lkotlin/collections/RandomAccess;", "byteStrings", "", "trie", "", "([Lokio/ByteString;[I)V", "getByteStrings$okio", "()[Lokio/ByteString;", "[Lokio/ByteString;", "size", "", "getSize", "()I", "getTrie$okio", "()[I", "get", "index", "Companion", "okio"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* compiled from: Options.kt */
public final class Options extends AbstractList<ByteString> implements RandomAccess {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    private final ByteString[] byteStrings;
    private final int[] trie;

    public /* synthetic */ Options(ByteString[] byteStringArr, int[] iArr, DefaultConstructorMarker defaultConstructorMarker) {
        this(byteStringArr, iArr);
    }

    @JvmStatic
    public static final Options of(ByteString... byteStringArr) {
        return Companion.of(byteStringArr);
    }

    public final /* bridge */ boolean contains(Object element) {
        if (!(element instanceof ByteString)) {
            return false;
        }
        return contains((ByteString) element);
    }

    public /* bridge */ boolean contains(ByteString element) {
        return super.contains(element);
    }

    public final /* bridge */ int indexOf(Object element) {
        if (!(element instanceof ByteString)) {
            return -1;
        }
        return indexOf((ByteString) element);
    }

    public /* bridge */ int indexOf(ByteString element) {
        return super.indexOf(element);
    }

    public final /* bridge */ int lastIndexOf(Object element) {
        if (!(element instanceof ByteString)) {
            return -1;
        }
        return lastIndexOf((ByteString) element);
    }

    public /* bridge */ int lastIndexOf(ByteString element) {
        return super.lastIndexOf(element);
    }

    public final ByteString[] getByteStrings$okio() {
        return this.byteStrings;
    }

    public final int[] getTrie$okio() {
        return this.trie;
    }

    private Options(ByteString[] byteStrings2, int[] trie2) {
        this.byteStrings = byteStrings2;
        this.trie = trie2;
    }

    public int getSize() {
        return this.byteStrings.length;
    }

    public ByteString get(int index) {
        return this.byteStrings[index];
    }

    @Metadata(d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010\u0011\n\u0002\b\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002JT\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\u00052\b\b\u0002\u0010\f\u001a\u00020\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\b\b\u0002\u0010\u0011\u001a\u00020\r2\b\b\u0002\u0010\u0012\u001a\u00020\r2\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\r0\u000fH\u0002J!\u0010\u0014\u001a\u00020\u00152\u0012\u0010\u000e\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00100\u0016\"\u00020\u0010H\u0007¢\u0006\u0002\u0010\u0017R\u0018\u0010\u0003\u001a\u00020\u0004*\u00020\u00058BX\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0018"}, d2 = {"Lokio/Options$Companion;", "", "()V", "intCount", "", "Lokio/Buffer;", "getIntCount", "(Lokio/Buffer;)J", "buildTrieRecursive", "", "nodeOffset", "node", "byteStringOffset", "", "byteStrings", "", "Lokio/ByteString;", "fromIndex", "toIndex", "indexes", "of", "Lokio/Options;", "", "([Lokio/ByteString;)Lokio/Options;", "okio"}, k = 1, mv = {1, 5, 1}, xi = 48)
    /* compiled from: Options.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @JvmStatic
        public final Options of(ByteString... byteStrings) {
            ByteString[] byteStringArr = byteStrings;
            Intrinsics.checkNotNullParameter(byteStringArr, "byteStrings");
            if (byteStringArr.length == 0) {
                return new Options(new ByteString[0], new int[]{0, -1}, (DefaultConstructorMarker) null);
            }
            List list = ArraysKt.toMutableList((T[]) byteStrings);
            CollectionsKt.sort(list);
            ByteString[] byteStringArr2 = byteStrings;
            Collection destination$iv$iv = new ArrayList(byteStringArr2.length);
            for (ByteString byteString : byteStringArr2) {
                destination$iv$iv.add(-1);
            }
            Collection thisCollection$iv = (List) destination$iv$iv;
            Collection collection = thisCollection$iv;
            Object[] array = thisCollection$iv.toArray(new Integer[0]);
            if (array != null) {
                Integer[] numArr = (Integer[]) array;
                List indexes = CollectionsKt.mutableListOf(Arrays.copyOf(numArr, numArr.length));
                ByteString[] byteStringArr3 = byteStrings;
                int index$iv = 0;
                int length = byteStringArr3.length;
                int i = 0;
                while (i < length) {
                    indexes.set(CollectionsKt.binarySearch$default(list, (Comparable) byteStringArr3[i], 0, 0, 6, (Object) null), Integer.valueOf(index$iv));
                    i++;
                    index$iv++;
                }
                if (((ByteString) list.get(0)).size() > 0) {
                    int a = 0;
                    while (a < list.size()) {
                        ByteString prefix = (ByteString) list.get(a);
                        int b = a + 1;
                        while (b < list.size()) {
                            ByteString byteString2 = (ByteString) list.get(b);
                            if (!byteString2.startsWith(prefix)) {
                                continue;
                                break;
                            }
                            if (!(byteString2.size() != prefix.size())) {
                                throw new IllegalArgumentException(Intrinsics.stringPlus("duplicate option: ", byteString2).toString());
                            } else if (((Number) indexes.get(b)).intValue() > ((Number) indexes.get(a)).intValue()) {
                                list.remove(b);
                                indexes.remove(b);
                            } else {
                                b++;
                            }
                        }
                        a++;
                    }
                    Buffer trieBytes = new Buffer();
                    int i2 = a;
                    List list2 = indexes;
                    buildTrieRecursive$default(this, 0, trieBytes, 0, list, 0, 0, indexes, 53, (Object) null);
                    int[] trie = new int[((int) getIntCount(trieBytes))];
                    int i3 = 0;
                    while (!trieBytes.exhausted()) {
                        trie[i3] = trieBytes.readInt();
                        i3++;
                    }
                    Object[] copyOf = Arrays.copyOf(byteStringArr, byteStringArr.length);
                    Intrinsics.checkNotNullExpressionValue(copyOf, "java.util.Arrays.copyOf(this, size)");
                    return new Options((ByteString[]) copyOf, trie, (DefaultConstructorMarker) null);
                }
                throw new IllegalArgumentException("the empty byte string is not a supported option".toString());
            }
            throw new NullPointerException("null cannot be cast to non-null type kotlin.Array<T>");
        }

        static /* synthetic */ void buildTrieRecursive$default(Companion companion, long j, Buffer buffer, int i, List list, int i2, int i3, List list2, int i4, Object obj) {
            long j2;
            int i5;
            int i6;
            int i7;
            if ((i4 & 1) != 0) {
                j2 = 0;
            } else {
                j2 = j;
            }
            if ((i4 & 4) != 0) {
                i5 = 0;
            } else {
                i5 = i;
            }
            if ((i4 & 16) != 0) {
                i6 = 0;
            } else {
                i6 = i2;
            }
            if ((i4 & 32) != 0) {
                i7 = list.size();
            } else {
                i7 = i3;
            }
            companion.buildTrieRecursive(j2, buffer, i5, list, i6, i7, list2);
        }

        private final void buildTrieRecursive(long nodeOffset, Buffer node, int byteStringOffset, List<? extends ByteString> byteStrings, int fromIndex, int toIndex, List<Integer> indexes) {
            int fromIndex2;
            ByteString from;
            int prefixIndex;
            ByteString from2;
            ByteString to;
            int scanByteCount;
            int selectChoiceCount;
            int prefixIndex2;
            int selectChoiceCount2;
            int rangeEnd;
            ByteString to2;
            Buffer childNodes;
            ByteString from3;
            int rangeEnd2;
            int fromIndex3;
            int prefixIndex3;
            Buffer buffer = node;
            int i = byteStringOffset;
            List<? extends ByteString> list = byteStrings;
            int i2 = fromIndex;
            int i3 = toIndex;
            List<Integer> list2 = indexes;
            boolean z = false;
            if (i2 < i3) {
                if (i2 < i3) {
                    int i4 = i2;
                    do {
                        int i5 = i4;
                        i4++;
                        if (!(((ByteString) list.get(i5)).size() >= i)) {
                            throw new IllegalArgumentException("Failed requirement.".toString());
                        }
                    } while (i4 < i3);
                }
                int fromIndex4 = fromIndex;
                ByteString from4 = (ByteString) list.get(fromIndex4);
                ByteString to3 = (ByteString) list.get(i3 - 1);
                if (i == from4.size()) {
                    int prefixIndex4 = list2.get(fromIndex4).intValue();
                    int fromIndex5 = fromIndex4 + 1;
                    fromIndex2 = fromIndex5;
                    from = (ByteString) list.get(fromIndex5);
                    prefixIndex = prefixIndex4;
                } else {
                    fromIndex2 = fromIndex4;
                    from = from4;
                    prefixIndex = -1;
                }
                if (from.getByte(i) != to3.getByte(i)) {
                    int selectChoiceCount3 = 1;
                    int i6 = fromIndex2 + 1;
                    if (i6 < i3) {
                        do {
                            int i7 = i6;
                            i6++;
                            if (((ByteString) list.get(i7 - 1)).getByte(i) != ((ByteString) list.get(i7)).getByte(i)) {
                                selectChoiceCount3++;
                                continue;
                            }
                        } while (i6 < i3);
                        selectChoiceCount = selectChoiceCount3;
                    } else {
                        selectChoiceCount = 1;
                    }
                    long childNodesOffset = nodeOffset + getIntCount(buffer) + ((long) 2) + ((long) (selectChoiceCount * 2));
                    buffer.writeInt(selectChoiceCount);
                    buffer.writeInt(prefixIndex);
                    if (fromIndex2 < i3) {
                        int i8 = fromIndex2;
                        do {
                            int i9 = i8;
                            i8++;
                            int rangeByte = ((ByteString) list.get(i9)).getByte(i);
                            if (i9 == fromIndex2 || rangeByte != ((ByteString) list.get(i9 - 1)).getByte(i)) {
                                buffer.writeInt(rangeByte & 255);
                                continue;
                            }
                        } while (i8 < i3);
                    }
                    Buffer childNodes2 = new Buffer();
                    int rangeStart = fromIndex2;
                    while (rangeStart < i3) {
                        byte rangeByte2 = ((ByteString) list.get(rangeStart)).getByte(i);
                        int rangeEnd3 = toIndex;
                        int rangeEnd4 = rangeStart + 1;
                        if (rangeEnd4 < i3) {
                            while (true) {
                                int i10 = rangeEnd4;
                                rangeEnd4++;
                                selectChoiceCount2 = selectChoiceCount;
                                int i11 = i10;
                                prefixIndex2 = prefixIndex;
                                if (rangeByte2 != ((ByteString) list.get(i11)).getByte(i)) {
                                    rangeEnd = i11;
                                    break;
                                } else if (rangeEnd4 >= i3) {
                                    break;
                                } else {
                                    selectChoiceCount = selectChoiceCount2;
                                    prefixIndex = prefixIndex2;
                                }
                            }
                        } else {
                            selectChoiceCount2 = selectChoiceCount;
                            prefixIndex2 = prefixIndex;
                        }
                        rangeEnd = rangeEnd3;
                        if (rangeStart + 1 == rangeEnd && i + 1 == ((ByteString) list.get(rangeStart)).size()) {
                            buffer.writeInt(list2.get(rangeStart).intValue());
                            int i12 = rangeStart;
                            byte b = rangeByte2;
                            childNodes = childNodes2;
                            rangeEnd2 = rangeEnd;
                            fromIndex3 = fromIndex2;
                            to2 = to3;
                            prefixIndex3 = prefixIndex2;
                            from3 = from;
                        } else {
                            buffer.writeInt(((int) (childNodesOffset + getIntCount(childNodes2))) * -1);
                            byte b2 = rangeByte2;
                            childNodes = childNodes2;
                            rangeEnd2 = rangeEnd;
                            prefixIndex3 = prefixIndex2;
                            from3 = from;
                            fromIndex3 = fromIndex2;
                            to2 = to3;
                            buildTrieRecursive(childNodesOffset, childNodes2, i + 1, byteStrings, rangeStart, rangeEnd2, indexes);
                        }
                        rangeStart = rangeEnd2;
                        prefixIndex = prefixIndex3;
                        fromIndex2 = fromIndex3;
                        selectChoiceCount = selectChoiceCount2;
                        from = from3;
                        childNodes2 = childNodes;
                        to3 = to2;
                        int prefixIndex5 = fromIndex;
                        list2 = indexes;
                    }
                    int i13 = rangeStart;
                    int i14 = selectChoiceCount;
                    int i15 = prefixIndex;
                    ByteString byteString = from;
                    buffer.writeAll(childNodes2);
                    int i16 = fromIndex2;
                    ByteString byteString2 = to3;
                    List<Integer> list3 = indexes;
                    return;
                }
                int prefixIndex6 = prefixIndex;
                ByteString from5 = from;
                int fromIndex6 = fromIndex2;
                ByteString to4 = to3;
                int scanByteCount2 = 0;
                int min = Math.min(from5.size(), to4.size());
                if (i < min) {
                    int i17 = i;
                    while (true) {
                        int i18 = i17;
                        i17++;
                        from2 = from5;
                        to = to4;
                        if (from2.getByte(i18) != to.getByte(i18)) {
                            scanByteCount = scanByteCount2;
                            break;
                        }
                        scanByteCount2++;
                        if (i17 >= min) {
                            scanByteCount = scanByteCount2;
                            break;
                        } else {
                            to4 = to;
                            from5 = from2;
                        }
                    }
                } else {
                    from2 = from5;
                    to = to4;
                    scanByteCount = 0;
                }
                long childNodesOffset2 = nodeOffset + getIntCount(buffer) + ((long) 2) + ((long) scanByteCount) + 1;
                buffer.writeInt(-scanByteCount);
                buffer.writeInt(prefixIndex6);
                int i19 = i + scanByteCount;
                if (i < i19) {
                    int i20 = i;
                    do {
                        int i21 = i20;
                        i20++;
                        buffer.writeInt((int) from2.getByte(i21) & UByte.MAX_VALUE);
                    } while (i20 < i19);
                }
                if (fromIndex6 + 1 == i3) {
                    if (i + scanByteCount == ((ByteString) list.get(fromIndex6)).size()) {
                        z = true;
                    }
                    if (z) {
                        int fromIndex7 = fromIndex6;
                        buffer.writeInt(indexes.get(fromIndex7).intValue());
                        int i22 = fromIndex7;
                        ByteString byteString3 = to;
                        ByteString byteString4 = from2;
                        return;
                    }
                    throw new IllegalStateException("Check failed.".toString());
                }
                int fromIndex8 = fromIndex6;
                List<Integer> list4 = indexes;
                Buffer childNodes3 = new Buffer();
                buffer.writeInt(((int) (childNodesOffset2 + getIntCount(childNodes3))) * -1);
                int i23 = scanByteCount;
                ByteString byteString5 = to;
                ByteString byteString6 = from2;
                buildTrieRecursive(childNodesOffset2, childNodes3, i + scanByteCount, byteStrings, fromIndex8, toIndex, indexes);
                buffer.writeAll(childNodes3);
                return;
            }
            throw new IllegalArgumentException("Failed requirement.".toString());
        }

        private final long getIntCount(Buffer $this$intCount) {
            return $this$intCount.size() / ((long) 4);
        }
    }
}

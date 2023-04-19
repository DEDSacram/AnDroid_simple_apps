package com.skydoves.expandablelayout;

import android.content.Context;
import com.skydoves.expandablelayout.ExpandableLayout;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u001e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\u001a)\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0017\u0010\u0003\u001a\u0013\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004¢\u0006\u0002\b\u0007H\bø\u0001\u0000\u0002\u0007\n\u0005\b20\u0001¨\u0006\b"}, d2 = {"expandableLayout", "Lcom/skydoves/expandablelayout/ExpandableLayout;", "Landroid/content/Context;", "block", "Lkotlin/Function1;", "Lcom/skydoves/expandablelayout/ExpandableLayout$Builder;", "", "Lkotlin/ExtensionFunctionType;", "expandablelayout_release"}, k = 2, mv = {1, 4, 2})
/* compiled from: ExpandableLayoutExtension.kt */
public final class ExpandableLayoutExtensionKt {
    @ExpandableLayoutDsl
    public static final /* synthetic */ ExpandableLayout expandableLayout(Context $this$expandableLayout, Function1<? super ExpandableLayout.Builder, Unit> block) {
        Intrinsics.checkNotNullParameter($this$expandableLayout, "$this$expandableLayout");
        Intrinsics.checkNotNullParameter(block, "block");
        ExpandableLayout.Builder builder = new ExpandableLayout.Builder($this$expandableLayout);
        block.invoke(builder);
        return builder.build();
    }
}

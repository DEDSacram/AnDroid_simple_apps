package com.skydoves.expandablelayout;

import kotlin.Metadata;
import kotlin.jvm.functions.Function1;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\n¢\u0006\u0002\b\u0004¨\u0006\u0005"}, d2 = {"<anonymous>", "", "isExpanded", "", "onExpand", "com/skydoves/expandablelayout/ExpandableLayout$Builder$setOnExpandListener$2$1"}, k = 3, mv = {1, 4, 2})
/* compiled from: ExpandableLayout.kt */
final class ExpandableLayout$Builder$setOnExpandListener$$inlined$apply$lambda$1 implements OnExpandListener {
    final /* synthetic */ Function1 $block$inlined;

    ExpandableLayout$Builder$setOnExpandListener$$inlined$apply$lambda$1(Function1 function1) {
        this.$block$inlined = function1;
    }

    public final void onExpand(boolean isExpanded) {
        this.$block$inlined.invoke(Boolean.valueOf(isExpanded));
    }
}

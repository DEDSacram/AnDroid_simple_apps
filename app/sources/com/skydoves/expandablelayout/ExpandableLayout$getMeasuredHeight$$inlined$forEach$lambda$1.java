package com.skydoves.expandablelayout;

import android.view.View;
import kotlin.Metadata;
import kotlin.jvm.internal.Ref;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002¨\u0006\u0003"}, d2 = {"<anonymous>", "", "run", "com/skydoves/expandablelayout/ExpandableLayout$getMeasuredHeight$2$1"}, k = 3, mv = {1, 4, 2})
/* compiled from: ExpandableLayout.kt */
final class ExpandableLayout$getMeasuredHeight$$inlined$forEach$lambda$1 implements Runnable {
    final /* synthetic */ View $child;
    final /* synthetic */ Ref.IntRef $height$inlined;
    final /* synthetic */ ExpandableLayout this$0;

    ExpandableLayout$getMeasuredHeight$$inlined$forEach$lambda$1(View view, ExpandableLayout expandableLayout, Ref.IntRef intRef) {
        this.$child = view;
        this.this$0 = expandableLayout;
        this.$height$inlined = intRef;
    }

    public final void run() {
        this.$height$inlined.element += this.this$0.getMeasuredHeight(this.$child);
    }
}

package com.skydoves.expandablelayout;

import android.view.View;
import android.view.ViewGroup;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002¨\u0006\u0003"}, d2 = {"<anonymous>", "", "run", "com/skydoves/expandablelayout/ExpandableLayout$updateSecondLayout$1$1"}, k = 3, mv = {1, 4, 2})
/* compiled from: ExpandableLayout.kt */
final class ExpandableLayout$updateSecondLayout$$inlined$apply$lambda$1 implements Runnable {
    final /* synthetic */ View $this_apply;
    final /* synthetic */ ExpandableLayout this$0;

    ExpandableLayout$updateSecondLayout$$inlined$apply$lambda$1(View view, ExpandableLayout expandableLayout) {
        this.$this_apply = view;
        this.this$0 = expandableLayout;
    }

    public final void run() {
        ExpandableLayout expandableLayout = this.this$0;
        View view = this.$this_apply;
        Intrinsics.checkNotNullExpressionValue(view, "this");
        expandableLayout.measuredSecondLayoutHeight = expandableLayout.getMeasuredHeight(view);
        View $this$updateLayoutParams$iv = this.$this_apply;
        ViewGroup.LayoutParams params$iv$iv = $this$updateLayoutParams$iv.getLayoutParams();
        if (params$iv$iv != null) {
            params$iv$iv.height = 0;
            $this$updateLayoutParams$iv.setLayoutParams(params$iv$iv);
            this.$this_apply.setY((float) this.this$0.getParentLayout().getMeasuredHeight());
            ViewExtensionKt.visible(this.this$0, true);
            return;
        }
        throw new NullPointerException("null cannot be cast to non-null type android.view.ViewGroup.LayoutParams");
    }
}

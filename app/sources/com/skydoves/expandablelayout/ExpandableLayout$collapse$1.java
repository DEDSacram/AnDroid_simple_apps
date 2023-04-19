package com.skydoves.expandablelayout;

import android.animation.ValueAnimator;
import kotlin.Metadata;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\b\n\u0000\n\u0002\u0010\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, d2 = {"<anonymous>", "", "run"}, k = 3, mv = {1, 4, 2})
/* compiled from: ExpandableLayout.kt */
final class ExpandableLayout$collapse$1 implements Runnable {
    final /* synthetic */ ExpandableLayout this$0;

    ExpandableLayout$collapse$1(ExpandableLayout expandableLayout) {
        this.this$0 = expandableLayout;
    }

    public final void run() {
        if (this.this$0.isExpanded() && !this.this$0.isCollapsing()) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
            ValueAnimator $this$apply = ofFloat;
            this.this$0.setCollapsing(true);
            $this$apply.setDuration(this.this$0.getDuration());
            AnimatorExtensionKt.applyInterpolator($this$apply, this.this$0.getExpandableAnimation());
            $this$apply.addUpdateListener(new ExpandableLayout$collapse$1$$special$$inlined$apply$lambda$1(this));
            ofFloat.start();
        }
    }
}

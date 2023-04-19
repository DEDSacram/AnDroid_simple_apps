package com.skydoves.expandablelayout;

import androidx.appcompat.widget.AppCompatImageView;
import kotlin.Metadata;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002¨\u0006\u0003"}, d2 = {"<anonymous>", "", "run", "com/skydoves/expandablelayout/ExpandableLayout$updateSpinner$1$2"}, k = 3, mv = {1, 4, 2})
/* compiled from: ExpandableLayout.kt */
final class ExpandableLayout$updateSpinner$$inlined$with$lambda$1 implements Runnable {
    final /* synthetic */ AppCompatImageView $this_with;
    final /* synthetic */ ExpandableLayout this$0;

    ExpandableLayout$updateSpinner$$inlined$with$lambda$1(AppCompatImageView appCompatImageView, ExpandableLayout expandableLayout) {
        this.$this_with = appCompatImageView;
        this.this$0 = expandableLayout;
    }

    public final void run() {
        this.$this_with.setY((((float) this.this$0.getParentLayout().getHeight()) / 2.0f) - (this.this$0.getSpinnerSize() / ((float) 2)));
    }
}

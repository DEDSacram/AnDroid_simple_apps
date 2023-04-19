package com.skydoves.expandablelayout;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.AppCompatImageView;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u0003H\n¢\u0006\u0002\b\u0005¨\u0006\u0006"}, d2 = {"<anonymous>", "", "it", "Landroid/animation/ValueAnimator;", "kotlin.jvm.PlatformType", "onAnimationUpdate", "com/skydoves/expandablelayout/ExpandableLayout$expand$1$1$1"}, k = 3, mv = {1, 4, 2})
/* compiled from: ExpandableLayout.kt */
final class ExpandableLayout$expand$1$$special$$inlined$apply$lambda$1 implements ValueAnimator.AnimatorUpdateListener {
    final /* synthetic */ ExpandableLayout$expand$1 this$0;

    ExpandableLayout$expand$1$$special$$inlined$apply$lambda$1(ExpandableLayout$expand$1 expandableLayout$expand$1) {
        this.this$0 = expandableLayout$expand$1;
    }

    public final void onAnimationUpdate(ValueAnimator it) {
        int i;
        Intrinsics.checkNotNullExpressionValue(it, "it");
        Object animatedValue = it.getAnimatedValue();
        if (animatedValue != null) {
            float value = ((Float) animatedValue).floatValue();
            View $this$updateLayoutParams$iv = this.this$0.this$0.getSecondLayout();
            ViewGroup.LayoutParams params$iv$iv = $this$updateLayoutParams$iv.getLayoutParams();
            if (params$iv$iv != null) {
                ViewGroup.LayoutParams $this$updateLayoutParams = params$iv$iv;
                if (this.this$0.$expandableHeight > 0) {
                    i = ((int) (((float) this.this$0.$expandableHeight) * value)) + this.this$0.this$0.getParentLayout().getHeight();
                } else {
                    i = ((int) (((float) this.this$0.this$0.measuredSecondLayoutHeight) * value)) + this.this$0.this$0.getParentLayout().getHeight();
                }
                $this$updateLayoutParams.height = i;
                $this$updateLayoutParams$iv.setLayoutParams(params$iv$iv);
                if (this.this$0.this$0.getSpinnerAnimate()) {
                    AppCompatImageView appCompatImageView = this.this$0.this$0.binding.arrow;
                    Intrinsics.checkNotNullExpressionValue(appCompatImageView, "binding.arrow");
                    appCompatImageView.setRotation(((float) this.this$0.this$0.getSpinnerRotation()) * value);
                }
                if (value >= 1.0f) {
                    OnExpandListener onExpandListener = this.this$0.this$0.getOnExpandListener();
                    if (onExpandListener != null) {
                        onExpandListener.onExpand(this.this$0.this$0.isExpanded());
                    }
                    this.this$0.this$0.setExpanding(false);
                    this.this$0.this$0.setExpanded(true);
                    return;
                }
                return;
            }
            throw new NullPointerException("null cannot be cast to non-null type android.view.ViewGroup.LayoutParams");
        }
        throw new NullPointerException("null cannot be cast to non-null type kotlin.Float");
    }
}

package com.skydoves.expandablelayout;

import android.animation.Animator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0014\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0000¨\u0006\u0005"}, d2 = {"applyInterpolator", "", "Landroid/animation/Animator;", "liftAnimation", "Lcom/skydoves/expandablelayout/ExpandableAnimation;", "expandablelayout_release"}, k = 2, mv = {1, 4, 2})
/* compiled from: AnimatorExtension.kt */
public final class AnimatorExtensionKt {

    @Metadata(bv = {1, 0, 3}, k = 3, mv = {1, 4, 2})
    public final /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[ExpandableAnimation.values().length];
            $EnumSwitchMapping$0 = iArr;
            iArr[ExpandableAnimation.NORMAL.ordinal()] = 1;
            iArr[ExpandableAnimation.ACCELERATE.ordinal()] = 2;
            iArr[ExpandableAnimation.BOUNCE.ordinal()] = 3;
            iArr[ExpandableAnimation.OVERSHOOT.ordinal()] = 4;
        }
    }

    public static final void applyInterpolator(Animator $this$applyInterpolator, ExpandableAnimation liftAnimation) {
        Intrinsics.checkNotNullParameter($this$applyInterpolator, "$this$applyInterpolator");
        Intrinsics.checkNotNullParameter(liftAnimation, "liftAnimation");
        switch (WhenMappings.$EnumSwitchMapping$0[liftAnimation.ordinal()]) {
            case 1:
                $this$applyInterpolator.setInterpolator(new LinearInterpolator());
                return;
            case 2:
                $this$applyInterpolator.setInterpolator(new AccelerateInterpolator());
                return;
            case 3:
                $this$applyInterpolator.setInterpolator(new BounceInterpolator());
                return;
            case 4:
                $this$applyInterpolator.setInterpolator(new OvershootInterpolator());
                return;
            default:
                return;
        }
    }
}

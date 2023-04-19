package com.skydoves.expandablelayout;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import androidx.exifinterface.media.ExifInterface;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u00002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\u001a6\u0010\u0000\u001a\u00020\u0001\"\n\b\u0000\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0017\u0010\u0006\u001a\u0013\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u00020\u00010\u0007¢\u0006\u0002\b\bH\b\u001a\u0014\u0010\t\u001a\u00020\n*\u00020\u00052\u0006\u0010\u000b\u001a\u00020\nH\u0000\u001a\u0014\u0010\t\u001a\u00020\n*\u00020\u00052\u0006\u0010\u000b\u001a\u00020\fH\u0000\u001a)\u0010\r\u001a\u00020\u0001*\u00020\u00052\u0017\u0010\u0006\u001a\u0013\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u0007¢\u0006\u0002\b\bH\bø\u0001\u0000\u001a\u0014\u0010\u000e\u001a\u00020\u0001*\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u0010H\u0000\u0002\u0007\n\u0005\b20\u0001¨\u0006\u0011"}, d2 = {"updateLayoutParam", "", "T", "Landroid/view/ViewGroup$LayoutParams;", "view", "Landroid/view/View;", "block", "Lkotlin/Function1;", "Lkotlin/ExtensionFunctionType;", "dp2Px", "", "dp", "", "updateLayoutParams", "visible", "value", "", "expandablelayout_release"}, k = 2, mv = {1, 4, 2})
/* compiled from: ViewExtension.kt */
public final class ViewExtensionKt {
    public static final void visible(View $this$visible, boolean value) {
        Intrinsics.checkNotNullParameter($this$visible, "$this$visible");
        if (value) {
            $this$visible.setVisibility(0);
        } else {
            $this$visible.setVisibility(4);
        }
    }

    public static final float dp2Px(View $this$dp2Px, int dp) {
        Intrinsics.checkNotNullParameter($this$dp2Px, "$this$dp2Px");
        Resources resources = $this$dp2Px.getResources();
        Intrinsics.checkNotNullExpressionValue(resources, "resources");
        return ((float) dp) * resources.getDisplayMetrics().density;
    }

    public static final float dp2Px(View $this$dp2Px, float dp) {
        Intrinsics.checkNotNullParameter($this$dp2Px, "$this$dp2Px");
        Resources resources = $this$dp2Px.getResources();
        Intrinsics.checkNotNullExpressionValue(resources, "resources");
        return dp * resources.getDisplayMetrics().density;
    }

    public static final void updateLayoutParams(View $this$updateLayoutParams, Function1<? super ViewGroup.LayoutParams, Unit> block) {
        Intrinsics.checkNotNullParameter($this$updateLayoutParams, "$this$updateLayoutParams");
        Intrinsics.checkNotNullParameter(block, "block");
        ViewGroup.LayoutParams params$iv = $this$updateLayoutParams.getLayoutParams();
        if (params$iv != null) {
            block.invoke(params$iv);
            $this$updateLayoutParams.setLayoutParams(params$iv);
            return;
        }
        throw new NullPointerException("null cannot be cast to non-null type android.view.ViewGroup.LayoutParams");
    }

    /* access modifiers changed from: private */
    public static final /* synthetic */ <T extends ViewGroup.LayoutParams> void updateLayoutParam(View view, Function1<? super T, Unit> block) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        Intrinsics.reifiedOperationMarker(1, ExifInterface.GPS_DIRECTION_TRUE);
        ViewGroup.LayoutParams params = layoutParams;
        block.invoke(params);
        view.setLayoutParams(params);
    }
}

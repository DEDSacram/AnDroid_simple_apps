package com.skydoves.expandablelayout;

import kotlin.Function;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FunctionAdapter;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, k = 3, mv = {1, 4, 2})
/* compiled from: ExpandableLayout.kt */
final class ExpandableLayout$sam$com_skydoves_expandablelayout_OnExpandListener$0 implements OnExpandListener, FunctionAdapter {
    private final /* synthetic */ Function1 function;

    ExpandableLayout$sam$com_skydoves_expandablelayout_OnExpandListener$0(Function1 function1) {
        this.function = function1;
    }

    public boolean equals(Object obj) {
        return (obj instanceof OnExpandListener) && (obj instanceof FunctionAdapter) && Intrinsics.areEqual((Object) this.function, (Object) ((FunctionAdapter) obj).getFunctionDelegate());
    }

    public Function getFunctionDelegate() {
        return this.function;
    }

    public int hashCode() {
        return this.function.hashCode();
    }

    public final /* synthetic */ void onExpand(boolean z) {
        Intrinsics.checkNotNullExpressionValue(this.function.invoke(Boolean.valueOf(z)), "invoke(...)");
    }
}

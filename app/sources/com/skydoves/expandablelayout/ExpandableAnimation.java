package com.skydoves.expandablelayout;

import kotlin.Metadata;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\b\n\u0002\b\b\b\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\n¨\u0006\u000b"}, d2 = {"Lcom/skydoves/expandablelayout/ExpandableAnimation;", "", "value", "", "(Ljava/lang/String;II)V", "getValue", "()I", "NORMAL", "ACCELERATE", "BOUNCE", "OVERSHOOT", "expandablelayout_release"}, k = 1, mv = {1, 4, 2})
/* compiled from: ExpandableAnimation.kt */
public enum ExpandableAnimation {
    NORMAL(0),
    ACCELERATE(1),
    BOUNCE(2),
    OVERSHOOT(3);
    
    private final int value;

    private ExpandableAnimation(int value2) {
        this.value = value2;
    }

    public final int getValue() {
        return this.value;
    }
}

package com.skydoves.expandablelayout;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.GravityCompat;
import androidx.core.widget.ImageViewCompat;
import com.skydoves.expandablelayout.databinding.ExpandableLayoutFrameBinding;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.ranges.RangesKt;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000x\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b.\n\u0002\u0010\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u00002\u00020\u0001:\u0001{B%\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ\u0006\u0010d\u001a\u00020eJ\u0012\u0010f\u001a\u00020e2\b\b\u0003\u0010g\u001a\u00020\u0007H\u0007J\u001a\u0010h\u001a\u00020e2\b\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010i\u001a\u00020\u0007H\u0002J\u0010\u0010j\u001a\u00020\u00072\u0006\u0010k\u001a\u000206H\u0002J\u001a\u0010l\u001a\n m*\u0004\u0018\u000106062\b\b\u0001\u0010n\u001a\u00020\u0007H\u0002J\b\u0010o\u001a\u00020eH\u0014J\u001a\u0010p\u001a\u00020e2\u0012\u0010q\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020e0rJ\u000e\u0010p\u001a\u00020e2\u0006\u00102\u001a\u000201J\u0010\u0010s\u001a\u00020e2\u0006\u0010t\u001a\u00020uH\u0002J\u0006\u0010v\u001a\u00020eJ\b\u0010w\u001a\u00020eH\u0002J\b\u0010x\u001a\u00020eH\u0002J\b\u0010y\u001a\u00020eH\u0002J\b\u0010z\u001a\u00020eH\u0002R\u000e\u0010\t\u001a\u00020\nX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\nX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\nX\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\r\u001a\u00020\u00078\u0002@\u0002X\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u000e\u001a\u00020\u00078\u0002@\u0002X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\nX\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0010\u001a\u00020\u00078\u0002@\u0002X\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0015\u001a\u00020\u00168\u0002@\u0002X\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0017\u001a\u00020\u00168\u0002@\u0002X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u001a\u001a\u00020\u001bX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001fR\u001a\u0010 \u001a\u00020!X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010#\"\u0004\b$\u0010%R$\u0010'\u001a\u00020\n2\u0006\u0010&\u001a\u00020\n8F@BX\u000e¢\u0006\f\u001a\u0004\b'\u0010(\"\u0004\b)\u0010*R$\u0010+\u001a\u00020\n2\u0006\u0010&\u001a\u00020\n8F@BX\u000e¢\u0006\f\u001a\u0004\b+\u0010(\"\u0004\b,\u0010*R$\u0010-\u001a\u00020\n2\u0006\u0010&\u001a\u00020\n8F@BX\u000e¢\u0006\f\u001a\u0004\b-\u0010(\"\u0004\b.\u0010*R\u0012\u0010/\u001a\u00020\u00078\u0002@\u0002X\u000e¢\u0006\u0002\n\u0000R\"\u00102\u001a\u0004\u0018\u0001012\b\u00100\u001a\u0004\u0018\u000101@BX\u000e¢\u0006\b\n\u0000\u001a\u0004\b3\u00104R\u001a\u00105\u001a\u000206X.¢\u0006\u000e\n\u0000\u001a\u0004\b7\u00108\"\u0004\b9\u0010:R&\u0010;\u001a\u00020\u00072\b\b\u0001\u0010&\u001a\u00020\u00078G@FX\u000e¢\u0006\f\u001a\u0004\b<\u0010=\"\u0004\b>\u0010?R\u001a\u0010@\u001a\u000206X.¢\u0006\u000e\n\u0000\u001a\u0004\bA\u00108\"\u0004\bB\u0010:R&\u0010C\u001a\u00020\u00072\b\b\u0001\u0010&\u001a\u00020\u00078G@FX\u000e¢\u0006\f\u001a\u0004\bD\u0010=\"\u0004\bE\u0010?R$\u0010F\u001a\u00020\n2\u0006\u0010&\u001a\u00020\n8F@FX\u000e¢\u0006\f\u001a\u0004\bG\u0010(\"\u0004\bH\u0010*R\u001a\u0010I\u001a\u00020\nX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bJ\u0010(\"\u0004\bK\u0010*R&\u0010L\u001a\u00020\u00072\b\b\u0001\u0010&\u001a\u00020\u00078G@FX\u000e¢\u0006\f\u001a\u0004\bM\u0010=\"\u0004\bN\u0010?R(\u0010O\u001a\u0004\u0018\u00010\u00122\b\u0010&\u001a\u0004\u0018\u00010\u00128F@FX\u000e¢\u0006\f\u001a\u0004\bP\u0010Q\"\u0004\bR\u0010SR$\u0010T\u001a\u00020\u00142\u0006\u0010&\u001a\u00020\u00148F@FX\u000e¢\u0006\f\u001a\u0004\bU\u0010V\"\u0004\bW\u0010XR&\u0010Y\u001a\u00020\u00162\b\b\u0001\u0010&\u001a\u00020\u00168G@FX\u000e¢\u0006\f\u001a\u0004\bZ\u0010[\"\u0004\b\\\u0010]R\u001a\u0010^\u001a\u00020\u0007X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b_\u0010=\"\u0004\b`\u0010?R&\u0010a\u001a\u00020\u00162\b\b\u0001\u0010&\u001a\u00020\u00168G@FX\u000e¢\u0006\f\u001a\u0004\bb\u0010[\"\u0004\bc\u0010]¨\u0006|"}, d2 = {"Lcom/skydoves/expandablelayout/ExpandableLayout;", "Landroid/widget/FrameLayout;", "context", "Landroid/content/Context;", "attributeSet", "Landroid/util/AttributeSet;", "defStyle", "", "(Landroid/content/Context;Landroid/util/AttributeSet;I)V", "_isCollapsing", "", "_isExpanded", "_isExpanding", "_parentLayoutResource", "_secondLayoutResource", "_showSpinner", "_spinnerColor", "_spinnerDrawable", "Landroid/graphics/drawable/Drawable;", "_spinnerGravity", "Lcom/skydoves/expandablelayout/SpinnerGravity;", "_spinnerMargin", "", "_spinnerSize", "binding", "Lcom/skydoves/expandablelayout/databinding/ExpandableLayoutFrameBinding;", "duration", "", "getDuration", "()J", "setDuration", "(J)V", "expandableAnimation", "Lcom/skydoves/expandablelayout/ExpandableAnimation;", "getExpandableAnimation", "()Lcom/skydoves/expandablelayout/ExpandableAnimation;", "setExpandableAnimation", "(Lcom/skydoves/expandablelayout/ExpandableAnimation;)V", "value", "isCollapsing", "()Z", "setCollapsing", "(Z)V", "isExpanded", "setExpanded", "isExpanding", "setExpanding", "measuredSecondLayoutHeight", "<set-?>", "Lcom/skydoves/expandablelayout/OnExpandListener;", "onExpandListener", "getOnExpandListener", "()Lcom/skydoves/expandablelayout/OnExpandListener;", "parentLayout", "Landroid/view/View;", "getParentLayout", "()Landroid/view/View;", "setParentLayout", "(Landroid/view/View;)V", "parentLayoutResource", "getParentLayoutResource", "()I", "setParentLayoutResource", "(I)V", "secondLayout", "getSecondLayout", "setSecondLayout", "secondLayoutResource", "getSecondLayoutResource", "setSecondLayoutResource", "showSpinner", "getShowSpinner", "setShowSpinner", "spinnerAnimate", "getSpinnerAnimate", "setSpinnerAnimate", "spinnerColor", "getSpinnerColor", "setSpinnerColor", "spinnerDrawable", "getSpinnerDrawable", "()Landroid/graphics/drawable/Drawable;", "setSpinnerDrawable", "(Landroid/graphics/drawable/Drawable;)V", "spinnerGravity", "getSpinnerGravity", "()Lcom/skydoves/expandablelayout/SpinnerGravity;", "setSpinnerGravity", "(Lcom/skydoves/expandablelayout/SpinnerGravity;)V", "spinnerMargin", "getSpinnerMargin", "()F", "setSpinnerMargin", "(F)V", "spinnerRotation", "getSpinnerRotation", "setSpinnerRotation", "spinnerSize", "getSpinnerSize", "setSpinnerSize", "collapse", "", "expand", "expandableHeight", "getAttrs", "defStyleAttr", "getMeasuredHeight", "view", "inflate", "kotlin.jvm.PlatformType", "resource", "onFinishInflate", "setOnExpandListener", "block", "Lkotlin/Function1;", "setTypeArray", "a", "Landroid/content/res/TypedArray;", "toggleLayout", "updateExpandableLayout", "updateParentLayout", "updateSecondLayout", "updateSpinner", "Builder", "expandablelayout_release"}, k = 1, mv = {1, 4, 2})
/* compiled from: ExpandableLayout.kt */
public final class ExpandableLayout extends FrameLayout {
    private boolean _isCollapsing;
    private boolean _isExpanded;
    private boolean _isExpanding;
    private int _parentLayoutResource;
    private int _secondLayoutResource;
    private boolean _showSpinner;
    private int _spinnerColor;
    private Drawable _spinnerDrawable;
    private SpinnerGravity _spinnerGravity;
    private float _spinnerMargin;
    private float _spinnerSize;
    /* access modifiers changed from: private */
    public final ExpandableLayoutFrameBinding binding;
    private long duration;
    private ExpandableAnimation expandableAnimation;
    /* access modifiers changed from: private */
    public int measuredSecondLayoutHeight;
    /* access modifiers changed from: private */
    public OnExpandListener onExpandListener;
    public View parentLayout;
    public View secondLayout;
    private boolean spinnerAnimate;
    private int spinnerRotation;

    @Metadata(bv = {1, 0, 3}, k = 3, mv = {1, 4, 2})
    public final /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[SpinnerGravity.values().length];
            $EnumSwitchMapping$0 = iArr;
            iArr[SpinnerGravity.START.ordinal()] = 1;
            iArr[SpinnerGravity.END.ordinal()] = 2;
        }
    }

    public ExpandableLayout(Context context) {
        this(context, (AttributeSet) null, 0, 6, (DefaultConstructorMarker) null);
    }

    public ExpandableLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0, 4, (DefaultConstructorMarker) null);
    }

    public final void expand() {
        expand$default(this, 0, 1, (Object) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ExpandableLayout(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        Intrinsics.checkNotNullParameter(context, "context");
        ExpandableLayoutFrameBinding inflate = ExpandableLayoutFrameBinding.inflate(LayoutInflater.from(context), (ViewGroup) null, false);
        Intrinsics.checkNotNullExpressionValue(inflate, "ExpandableLayoutFrameBin…om(context), null, false)");
        this.binding = inflate;
        this._parentLayoutResource = R.layout.expandable_layout_frame;
        this._secondLayoutResource = R.layout.expandable_layout_child;
        this._spinnerMargin = ViewExtensionKt.dp2Px((View) this, 14);
        this._spinnerSize = ViewExtensionKt.dp2Px((View) this, 12);
        this._spinnerColor = -1;
        this._spinnerGravity = SpinnerGravity.END;
        this._showSpinner = true;
        this.duration = 250;
        this.expandableAnimation = ExpandableAnimation.NORMAL;
        this.spinnerRotation = -180;
        this.spinnerAnimate = true;
        if (attributeSet != null) {
            getAttrs(attributeSet, defStyle);
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ ExpandableLayout(android.content.Context r1, android.util.AttributeSet r2, int r3, int r4, kotlin.jvm.internal.DefaultConstructorMarker r5) {
        /*
            r0 = this;
            r5 = r4 & 2
            if (r5 == 0) goto L_0x0008
            r2 = 0
            r5 = r2
            android.util.AttributeSet r5 = (android.util.AttributeSet) r5
        L_0x0008:
            r4 = r4 & 4
            if (r4 == 0) goto L_0x000d
            r3 = 0
        L_0x000d:
            r0.<init>(r1, r2, r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.skydoves.expandablelayout.ExpandableLayout.<init>(android.content.Context, android.util.AttributeSet, int, int, kotlin.jvm.internal.DefaultConstructorMarker):void");
    }

    public final View getParentLayout() {
        View view = this.parentLayout;
        if (view == null) {
            Intrinsics.throwUninitializedPropertyAccessException("parentLayout");
        }
        return view;
    }

    public final void setParentLayout(View view) {
        Intrinsics.checkNotNullParameter(view, "<set-?>");
        this.parentLayout = view;
    }

    public final View getSecondLayout() {
        View view = this.secondLayout;
        if (view == null) {
            Intrinsics.throwUninitializedPropertyAccessException("secondLayout");
        }
        return view;
    }

    public final void setSecondLayout(View view) {
        Intrinsics.checkNotNullParameter(view, "<set-?>");
        this.secondLayout = view;
    }

    public final boolean isExpanded() {
        return this._isExpanded;
    }

    /* access modifiers changed from: private */
    public final void setExpanded(boolean value) {
        this._isExpanded = value;
    }

    public final boolean isExpanding() {
        return this._isExpanding;
    }

    /* access modifiers changed from: private */
    public final void setExpanding(boolean value) {
        this._isExpanding = value;
    }

    public final boolean isCollapsing() {
        return this._isCollapsing;
    }

    /* access modifiers changed from: private */
    public final void setCollapsing(boolean value) {
        this._isCollapsing = value;
    }

    public final int getParentLayoutResource() {
        return this._parentLayoutResource;
    }

    public final void setParentLayoutResource(int value) {
        this._parentLayoutResource = value;
        updateExpandableLayout();
    }

    public final int getSecondLayoutResource() {
        return this._secondLayoutResource;
    }

    public final void setSecondLayoutResource(int value) {
        this._secondLayoutResource = value;
        updateExpandableLayout();
    }

    public final Drawable getSpinnerDrawable() {
        return this._spinnerDrawable;
    }

    public final void setSpinnerDrawable(Drawable value) {
        this._spinnerDrawable = value;
        updateSpinner();
    }

    public final float getSpinnerSize() {
        return this._spinnerSize;
    }

    public final void setSpinnerSize(float value) {
        this._spinnerSize = ViewExtensionKt.dp2Px((View) this, value);
        updateSpinner();
    }

    public final float getSpinnerMargin() {
        return this._spinnerMargin;
    }

    public final void setSpinnerMargin(float value) {
        this._spinnerMargin = ViewExtensionKt.dp2Px((View) this, value);
        updateSpinner();
    }

    public final int getSpinnerColor() {
        return this._spinnerColor;
    }

    public final void setSpinnerColor(int value) {
        this._spinnerColor = value;
        updateSpinner();
    }

    public final SpinnerGravity getSpinnerGravity() {
        return this._spinnerGravity;
    }

    public final void setSpinnerGravity(SpinnerGravity value) {
        Intrinsics.checkNotNullParameter(value, "value");
        this._spinnerGravity = value;
        updateSpinner();
    }

    public final boolean getShowSpinner() {
        return this._showSpinner;
    }

    public final void setShowSpinner(boolean value) {
        this._showSpinner = value;
        updateSpinner();
    }

    public final long getDuration() {
        return this.duration;
    }

    public final void setDuration(long j) {
        this.duration = j;
    }

    public final ExpandableAnimation getExpandableAnimation() {
        return this.expandableAnimation;
    }

    public final void setExpandableAnimation(ExpandableAnimation expandableAnimation2) {
        Intrinsics.checkNotNullParameter(expandableAnimation2, "<set-?>");
        this.expandableAnimation = expandableAnimation2;
    }

    public final int getSpinnerRotation() {
        return this.spinnerRotation;
    }

    public final void setSpinnerRotation(int i) {
        this.spinnerRotation = i;
    }

    public final boolean getSpinnerAnimate() {
        return this.spinnerAnimate;
    }

    public final void setSpinnerAnimate(boolean z) {
        this.spinnerAnimate = z;
    }

    public final OnExpandListener getOnExpandListener() {
        return this.onExpandListener;
    }

    private final void getAttrs(AttributeSet attributeSet, int defStyleAttr) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.ExpandableLayout, defStyleAttr, 0);
        Intrinsics.checkNotNullExpressionValue(typedArray, "context.obtainStyledAttr…fStyleAttr,\n      0\n    )");
        try {
            setTypeArray(typedArray);
        } finally {
            typedArray.recycle();
        }
    }

    private final void setTypeArray(TypedArray a) {
        this._isExpanded = a.getBoolean(R.styleable.ExpandableLayout_expandable_isExpanded, this._isExpanded);
        this._parentLayoutResource = a.getResourceId(R.styleable.ExpandableLayout_expandable_parentLayout, this._parentLayoutResource);
        this._secondLayoutResource = a.getResourceId(R.styleable.ExpandableLayout_expandable_secondLayout, this._secondLayoutResource);
        int it = a.getResourceId(R.styleable.ExpandableLayout_expandable_spinner, -1);
        if (it != -1) {
            this._spinnerDrawable = AppCompatResources.getDrawable(getContext(), it);
        }
        this._showSpinner = a.getBoolean(R.styleable.ExpandableLayout_expandable_showSpinner, this._showSpinner);
        this._spinnerSize = (float) a.getDimensionPixelSize(R.styleable.ExpandableLayout_expandable_spinner_size, (int) this._spinnerSize);
        this._spinnerMargin = (float) a.getDimensionPixelSize(R.styleable.ExpandableLayout_expandable_spinner_margin, (int) this._spinnerMargin);
        this._spinnerColor = a.getColor(R.styleable.ExpandableLayout_expandable_spinner_color, this._spinnerColor);
        int spinnerGravity = a.getInteger(R.styleable.ExpandableLayout_expandable_spinner_gravity, this._spinnerGravity.getValue());
        if (spinnerGravity == SpinnerGravity.START.getValue()) {
            this._spinnerGravity = SpinnerGravity.START;
        } else if (spinnerGravity == SpinnerGravity.END.getValue()) {
            this._spinnerGravity = SpinnerGravity.END;
        }
        this.duration = (long) a.getInteger(R.styleable.ExpandableLayout_expandable_duration, (int) this.duration);
        int animation = a.getInteger(R.styleable.ExpandableLayout_expandable_animation, this.expandableAnimation.getValue());
        if (animation == ExpandableAnimation.NORMAL.getValue()) {
            this.expandableAnimation = ExpandableAnimation.NORMAL;
        } else if (animation == ExpandableAnimation.ACCELERATE.getValue()) {
            this.expandableAnimation = ExpandableAnimation.ACCELERATE;
        } else if (animation == ExpandableAnimation.BOUNCE.getValue()) {
            this.expandableAnimation = ExpandableAnimation.BOUNCE;
        } else if (animation == ExpandableAnimation.OVERSHOOT.getValue()) {
            this.expandableAnimation = ExpandableAnimation.OVERSHOOT;
        }
        this.spinnerAnimate = a.getBoolean(R.styleable.ExpandableLayout_expandable_spinner_animate, this.spinnerAnimate);
        this.spinnerRotation = a.getInt(R.styleable.ExpandableLayout_expandable_spinner_rotation, this.spinnerRotation);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        updateExpandableLayout();
        if (isExpanded()) {
            setExpanded(!isExpanded());
            expand$default(this, 0, 1, (Object) null);
        }
    }

    private final void updateExpandableLayout() {
        ViewExtensionKt.visible(this, false);
        removeAllViews();
        updateParentLayout();
        updateSecondLayout();
        updateSpinner();
    }

    private final void updateParentLayout() {
        View inflate = inflate(getParentLayoutResource());
        View $this$apply = inflate;
        $this$apply.measure(0, 0);
        this.binding.cover.addView($this$apply);
        FrameLayout frameLayout = this.binding.cover;
        Intrinsics.checkNotNullExpressionValue(frameLayout, "binding.cover");
        View $this$updateLayoutParams$iv = frameLayout;
        ViewGroup.LayoutParams params$iv$iv = $this$updateLayoutParams$iv.getLayoutParams();
        if (params$iv$iv != null) {
            params$iv$iv.height = $this$apply.getMeasuredHeight();
            $this$updateLayoutParams$iv.setLayoutParams(params$iv$iv);
            addView(this.binding.getRoot());
            Unit unit = Unit.INSTANCE;
            Intrinsics.checkNotNullExpressionValue(inflate, "inflate(parentLayoutReso…dView(binding.root)\n    }");
            this.parentLayout = inflate;
            return;
        }
        throw new NullPointerException("null cannot be cast to non-null type android.view.ViewGroup.LayoutParams");
    }

    private final void updateSecondLayout() {
        View inflate = inflate(getSecondLayoutResource());
        View $this$apply = inflate;
        addView($this$apply);
        $this$apply.post(new ExpandableLayout$updateSecondLayout$$inlined$apply$lambda$1($this$apply, this));
        Unit unit = Unit.INSTANCE;
        Intrinsics.checkNotNullExpressionValue(inflate, "inflate(secondLayoutReso…sible(true)\n      }\n    }");
        this.secondLayout = inflate;
    }

    private final void updateSpinner() {
        int i;
        AppCompatImageView $this$with = this.binding.arrow;
        ViewExtensionKt.visible($this$with, getShowSpinner());
        Drawable it = getSpinnerDrawable();
        if (it != null) {
            $this$with.setImageDrawable(it);
        }
        ImageViewCompat.setImageTintList($this$with, ColorStateList.valueOf(getSpinnerColor()));
        View view = this.parentLayout;
        if (view == null) {
            Intrinsics.throwUninitializedPropertyAccessException("parentLayout");
        }
        view.post(new ExpandableLayout$updateSpinner$$inlined$with$lambda$1($this$with, this));
        ViewGroup.LayoutParams layoutParams = $this$with.getLayoutParams();
        if (layoutParams != null) {
            FrameLayout.LayoutParams $this$with2 = (FrameLayout.LayoutParams) layoutParams;
            $this$with2.width = (int) getSpinnerSize();
            $this$with2.height = (int) getSpinnerSize();
            $this$with2.leftMargin = (int) getSpinnerMargin();
            $this$with2.rightMargin = (int) getSpinnerMargin();
            switch (WhenMappings.$EnumSwitchMapping$0[getSpinnerGravity().ordinal()]) {
                case 1:
                    i = GravityCompat.START;
                    break;
                case 2:
                    i = GravityCompat.END;
                    break;
                default:
                    throw new NoWhenBranchMatchedException();
            }
            $this$with2.gravity = i;
            return;
        }
        throw new NullPointerException("null cannot be cast to non-null type android.widget.FrameLayout.LayoutParams");
    }

    /* access modifiers changed from: private */
    public final int getMeasuredHeight(View view) {
        Ref.IntRef height = new Ref.IntRef();
        height.element = view.getHeight();
        if (view instanceof ViewGroup) {
            Iterable $this$map$iv = RangesKt.until(0, ((ViewGroup) view).getChildCount());
            Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
            Iterator it = $this$map$iv.iterator();
            while (it.hasNext()) {
                destination$iv$iv.add(((ViewGroup) view).getChildAt(((IntIterator) it).nextInt()));
            }
            for (View child : (List) destination$iv$iv) {
                if (child instanceof ExpandableLayout) {
                    child.post(new ExpandableLayout$getMeasuredHeight$$inlined$forEach$lambda$1(child, this, height));
                }
            }
        }
        return height.element;
    }

    public static /* synthetic */ void expand$default(ExpandableLayout expandableLayout, int i, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            i = 0;
        }
        expandableLayout.expand(i);
    }

    public final void expand(int expandableHeight) {
        post(new ExpandableLayout$expand$1(this, expandableHeight));
    }

    public final void collapse() {
        post(new ExpandableLayout$collapse$1(this));
    }

    public final void toggleLayout() {
        if (isExpanded()) {
            collapse();
        } else {
            expand$default(this, 0, 1, (Object) null);
        }
    }

    public final void setOnExpandListener(OnExpandListener onExpandListener2) {
        Intrinsics.checkNotNullParameter(onExpandListener2, "onExpandListener");
        this.onExpandListener = onExpandListener2;
    }

    public final /* synthetic */ void setOnExpandListener(Function1<? super Boolean, Unit> block) {
        Intrinsics.checkNotNullParameter(block, "block");
        this.onExpandListener = new ExpandableLayout$sam$com_skydoves_expandablelayout_OnExpandListener$0(block);
    }

    private final View inflate(int resource) {
        return LayoutInflater.from(getContext()).inflate(resource, this, false);
    }

    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0006\u0010\u0007\u001a\u00020\u0006J\u000e\u0010\b\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\nJ\u000e\u0010\u000b\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\fJ\u001a\u0010\r\u001a\u00020\u00002\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u000fJ\u000e\u0010\r\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0012J\u0010\u0010\u0013\u001a\u00020\u00002\b\b\u0001\u0010\t\u001a\u00020\u0014J\u0010\u0010\u0015\u001a\u00020\u00002\b\b\u0001\u0010\t\u001a\u00020\u0014J\u000e\u0010\u0016\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0010J\u000e\u0010\u0017\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0010J\u000e\u0010\u0018\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0019J\u0010\u0010\u001a\u001a\u00020\u00002\b\b\u0001\u0010\t\u001a\u00020\u001bJ\u000e\u0010\u001c\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0014J\u0010\u0010\u001d\u001a\u00020\u00002\b\b\u0001\u0010\t\u001a\u00020\u001bR\u000e\u0010\u0005\u001a\u00020\u0006X\u0004¢\u0006\u0002\n\u0000¨\u0006\u001e"}, d2 = {"Lcom/skydoves/expandablelayout/ExpandableLayout$Builder;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "expandableLayout", "Lcom/skydoves/expandablelayout/ExpandableLayout;", "build", "setDuration", "value", "", "setExpandableAnimation", "Lcom/skydoves/expandablelayout/ExpandableAnimation;", "setOnExpandListener", "block", "Lkotlin/Function1;", "", "", "Lcom/skydoves/expandablelayout/OnExpandListener;", "setParentLayoutResource", "", "setSecondLayoutResource", "setShowSpinner", "setSpinnerAnimate", "setSpinnerDrawable", "Landroid/graphics/drawable/Drawable;", "setSpinnerMargin", "", "setSpinnerRotation", "setSpinnerSize", "expandablelayout_release"}, k = 1, mv = {1, 4, 2})
    @ExpandableLayoutDsl
    /* compiled from: ExpandableLayout.kt */
    public static final class Builder {
        private final ExpandableLayout expandableLayout;

        public Builder(Context context) {
            Intrinsics.checkNotNullParameter(context, "context");
            this.expandableLayout = new ExpandableLayout(context, (AttributeSet) null, 0, 6, (DefaultConstructorMarker) null);
        }

        public final Builder setParentLayoutResource(int value) {
            this.expandableLayout.setParentLayoutResource(value);
            return this;
        }

        public final Builder setSecondLayoutResource(int value) {
            this.expandableLayout.setSecondLayoutResource(value);
            return this;
        }

        public final Builder setSpinnerDrawable(Drawable value) {
            Intrinsics.checkNotNullParameter(value, "value");
            this.expandableLayout.setSpinnerDrawable(value);
            return this;
        }

        public final Builder setShowSpinner(boolean value) {
            this.expandableLayout.setShowSpinner(value);
            return this;
        }

        public final Builder setSpinnerRotation(int value) {
            this.expandableLayout.setSpinnerRotation(value);
            return this;
        }

        public final Builder setSpinnerAnimate(boolean value) {
            this.expandableLayout.setSpinnerAnimate(value);
            return this;
        }

        public final Builder setSpinnerSize(float value) {
            this.expandableLayout.setSpinnerSize(value);
            return this;
        }

        public final Builder setSpinnerMargin(float value) {
            this.expandableLayout.setSpinnerMargin(value);
            return this;
        }

        public final Builder setDuration(long value) {
            this.expandableLayout.setDuration(value);
            return this;
        }

        public final Builder setExpandableAnimation(ExpandableAnimation value) {
            Intrinsics.checkNotNullParameter(value, "value");
            this.expandableLayout.setExpandableAnimation(value);
            return this;
        }

        public final Builder setOnExpandListener(OnExpandListener value) {
            Intrinsics.checkNotNullParameter(value, "value");
            this.expandableLayout.onExpandListener = value;
            return this;
        }

        public final /* synthetic */ Builder setOnExpandListener(Function1<? super Boolean, Unit> block) {
            Intrinsics.checkNotNullParameter(block, "block");
            this.expandableLayout.onExpandListener = new ExpandableLayout$Builder$setOnExpandListener$$inlined$apply$lambda$1(block);
            return this;
        }

        public final ExpandableLayout build() {
            return this.expandableLayout;
        }
    }
}

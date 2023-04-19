package com.skydoves.expandablelayout.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.viewbinding.ViewBinding;
import com.skydoves.expandablelayout.R;

public final class ExpandableLayoutChildBinding implements ViewBinding {
    private final LinearLayout rootView;

    private ExpandableLayoutChildBinding(LinearLayout rootView2) {
        this.rootView = rootView2;
    }

    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static ExpandableLayoutChildBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static ExpandableLayoutChildBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.expandable_layout_child, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static ExpandableLayoutChildBinding bind(View rootView2) {
        if (rootView2 != null) {
            return new ExpandableLayoutChildBinding((LinearLayout) rootView2);
        }
        throw new NullPointerException("rootView");
    }
}

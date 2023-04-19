package com.skydoves.expandablelayout.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.viewbinding.ViewBinding;
import com.skydoves.expandablelayout.R;

public final class ExpandableLayoutFrameBinding implements ViewBinding {
    public final AppCompatImageView arrow;
    public final FrameLayout cover;
    public final FrameLayout frameParent;
    private final FrameLayout rootView;

    private ExpandableLayoutFrameBinding(FrameLayout rootView2, AppCompatImageView arrow2, FrameLayout cover2, FrameLayout frameParent2) {
        this.rootView = rootView2;
        this.arrow = arrow2;
        this.cover = cover2;
        this.frameParent = frameParent2;
    }

    public FrameLayout getRoot() {
        return this.rootView;
    }

    public static ExpandableLayoutFrameBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static ExpandableLayoutFrameBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.expandable_layout_frame, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static ExpandableLayoutFrameBinding bind(View rootView2) {
        int id = R.id.arrow;
        AppCompatImageView arrow2 = (AppCompatImageView) rootView2.findViewById(id);
        if (arrow2 != null) {
            id = R.id.cover;
            FrameLayout cover2 = (FrameLayout) rootView2.findViewById(id);
            if (cover2 != null) {
                return new ExpandableLayoutFrameBinding((FrameLayout) rootView2, arrow2, cover2, (FrameLayout) rootView2);
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(rootView2.getResources().getResourceName(id)));
    }
}

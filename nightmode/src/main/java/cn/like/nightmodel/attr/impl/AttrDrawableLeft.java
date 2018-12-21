package cn.like.nightmodel.attr.impl;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import cn.like.nightmodel.attr.AttrType;

public class AttrDrawableLeft extends AttrType {
    public AttrDrawableLeft() {
        super("drawableLeft");
    }

    @Override
    public void apply(View view, String resName) {
        Drawable drawable = getDrawable(view.getContext(), resName);
        if (drawable != null && view instanceof TextView) {
            Drawable[] compoundDrawables = ((TextView) view).getCompoundDrawables();
            ((TextView) view).setCompoundDrawables(drawable, compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
        }
    }

}

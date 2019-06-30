package pokercc.android.nightmodel.attr.impl;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import pokercc.android.nightmodel.attr.AttrType;

public class AttrDrawableLeft extends AttrType {
    public AttrDrawableLeft() {
        super("drawableLeft");
    }

    @Override
    public void apply(View view, String resName) {
        Drawable drawable = getDrawable(view.getContext(), resName);
        if (drawable != null && view instanceof TextView) {
            Drawable[] compoundDrawables = ((TextView) view).getCompoundDrawables();
            ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(drawable, compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
        }
    }

}

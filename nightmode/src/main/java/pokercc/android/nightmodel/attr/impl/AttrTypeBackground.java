package pokercc.android.nightmodel.attr.impl;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import pokercc.android.nightmodel.attr.AttrType;

/**
 * Created by like on 2017/7/25.
 */

public class AttrTypeBackground extends AttrType {

    public AttrTypeBackground() {
        super("background");
    }

    @Override
    public void apply(View view, String resName) {
        if (TextUtils.isEmpty(resName)) return;
        Drawable drawable = getDrawable(view.getContext(), resName);
        if (drawable != null) {
            view.setBackgroundDrawable(drawable);
        } else {
            Resources resources = view.getResources();
            int resId = resources.getIdentifier(resName, DEF_TYPE_COLOR, view.getContext().getPackageName());
            if (0 != resId) {
                ColorStateList colorList = resources.getColorStateList(resId);
                if (colorList != null) {
                    view.setBackgroundColor(colorList.getDefaultColor());
                }
            }
        }


    }

    @Override
    public void applyColor(View view, int color) {
        super.applyColor(view, color);
        view.setBackgroundColor(color);
    }

    @Override
    public String getResourceName(String attrValue, Resources resources) {
        return getIntResourceName(attrValue, resources);
    }
}

package cn.like.nightmodel.attr;

import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by like on 16/7/20.
 */
public class Attr {
    final String resName;
    final AttrType attrType;

    final boolean isAttrRef;

    public Attr(String resName, AttrType attrType, boolean isAttrRef) {
        this.resName = resName;
        this.attrType = attrType;
        this.isAttrRef = isAttrRef;
    }

    public void apply(View view) {

        if (!isAttrRef) {
            attrType.apply(view, resName);
        } else {
            Resources mResources = view.getResources();
            final TypedValue typedValue = new TypedValue();
            view.getContext().getTheme().resolveAttribute(mResources.getIdentifier(resName, "attr", null), typedValue, true);
            if (typedValue.resourceId == 0) {
                Log.i("dayNight", resName + ":是值，不是引用,typedValue:" + typedValue);
                if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                    attrType.applyColor(view, typedValue.data);
                }
            } else {
                attrType.apply(view, mResources.getResourceEntryName(typedValue.resourceId));
            }
        }

    }
}
